/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2015 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.krad.uif.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.service.DocumentDictionaryService;
import org.kuali.kfs.krad.service.DocumentService;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.uif.UifConstants;
import org.kuali.kfs.krad.uif.UifConstants.ViewType;
import org.kuali.kfs.krad.uif.UifParameters;
import org.kuali.kfs.krad.uif.util.ViewModelUtils;
import org.kuali.kfs.krad.uif.service.ViewTypeService;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.KRADPropertyConstants;
import org.springframework.beans.PropertyValues;

/**
 * Type service implementation for maintenance views
 * 
 * <p>
 * Indexes views on object class and name. Can retrieve views by object class,
 * object class and name, or document id
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MaintenanceViewTypeServiceImpl implements ViewTypeService {
	private DocumentService documentService;
    private DocumentDictionaryService documentDictionaryService;

	/**
	 * @see ViewTypeService#getViewTypeName()
	 */
	public ViewType getViewTypeName() {
		return ViewType.MAINTENANCE;
	}

    /**
     * @see ViewTypeService#getParametersFromViewConfiguration(org.springframework.beans.PropertyValues)
     */
    public Map<String, String> getParametersFromViewConfiguration(PropertyValues propertyValues) {
        Map<String, String> parameters = new HashMap<String, String>();

        String viewName = ViewModelUtils.getStringValFromPVs(propertyValues, UifParameters.VIEW_NAME);
        String dataObjectClassName = ViewModelUtils.getStringValFromPVs(propertyValues,
                UifParameters.DATA_OBJECT_CLASS_NAME);

        parameters.put(UifParameters.VIEW_NAME, viewName);
        parameters.put(UifParameters.DATA_OBJECT_CLASS_NAME, dataObjectClassName);

        return parameters;
    }

	/**
	 * Check for document id in request parameters, if given retrieve document
	 * instance to get the object class and set the name parameter
	 * 
	 * @see ViewTypeService#getParametersFromRequest(java.util.Map)
	 */
	@Override
	public Map<String, String> getParametersFromRequest(Map<String, String> requestParameters) {
		Map<String, String> parameters = new HashMap<String, String>();

		if (requestParameters.containsKey(UifParameters.VIEW_NAME)) {
			parameters.put(UifParameters.VIEW_NAME, requestParameters.get(UifParameters.VIEW_NAME));
		}
		else {
			parameters.put(UifParameters.VIEW_NAME, UifConstants.DEFAULT_VIEW_NAME);
		}

		if (requestParameters.containsKey(UifParameters.DATA_OBJECT_CLASS_NAME)) {
			parameters.put(UifParameters.DATA_OBJECT_CLASS_NAME,
					requestParameters.get(UifParameters.DATA_OBJECT_CLASS_NAME));
		}
		else if (requestParameters.containsKey(KRADPropertyConstants.DOC_ID)) {
			String documentNumber = requestParameters.get(KRADPropertyConstants.DOC_ID);

			boolean objectClassFound = false;
			try {
				// determine object class based on the document type
				Document document = documentService.getByDocumentHeaderId(documentNumber);
                if (!documentService.documentExists(documentNumber)) {
                    parameters = new HashMap<String,String>();
                    parameters.put(UifParameters.VIEW_ID, KRADConstants.KRAD_INITIATED_DOCUMENT_VIEW_NAME);
                    return parameters;
                }
				if (document != null) {
					String docTypeName = document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName();
					Class<?> objectClassName = getDocumentDictionaryService().getMaintenanceDataObjectClass(docTypeName);
					if (objectClassName != null) {
						objectClassFound = true;
						parameters.put(UifParameters.DATA_OBJECT_CLASS_NAME, objectClassName.getName());
					}
				}

				if (!objectClassFound) {
					throw new RuntimeException("Could not determine object class for maintenance document with id: "
							+ documentNumber);
				}
			}
			catch (WorkflowException e) {
				throw new RuntimeException("Encountered workflow exception while retrieving document with id: "
						+ documentNumber, e);
			}
		}

		return parameters;
	}

	protected DocumentService getDocumentService() {
        if (documentService == null) {
            this.documentService = KRADServiceLocatorWeb.getDocumentService();
        }
		return this.documentService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

    public DocumentDictionaryService getDocumentDictionaryService() {
        if (documentDictionaryService == null) {
            this.documentDictionaryService = KRADServiceLocatorWeb.getDocumentDictionaryService();
        }
        return documentDictionaryService;
    }

    public void setDocumentDictionaryService(DocumentDictionaryService documentDictionaryService) {
        this.documentDictionaryService = documentDictionaryService;
    }
}
