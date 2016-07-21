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
package org.kuali.kfs.krad.service.impl;

import org.kuali.rice.kim.api.identity.Person;
import org.kuali.kfs.krad.datadictionary.AttributeSecurity;
import org.kuali.kfs.krad.document.DocumentAuthorizer;
import org.kuali.kfs.krad.document.DocumentPresentationController;
import org.kuali.kfs.krad.maintenance.MaintenanceDocumentAuthorizer;
import org.kuali.kfs.krad.maintenance.MaintenanceDocumentPresentationController;
import org.kuali.kfs.krad.service.DataDictionaryService;
import org.kuali.kfs.krad.service.DataObjectAuthorizationService;
import org.kuali.kfs.krad.service.DocumentDictionaryService;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;

/**
 * Implementation of <code>DataObjectAuthorizationService</code> that uses the
 * configured <code>AttributeSecurity</code> for a field to determine authorization
 * checks that need to be performed
 *
 * 
 */
public class DataObjectAuthorizationServiceImpl implements DataObjectAuthorizationService {

    private DataDictionaryService dataDictionaryService;
    private DocumentDictionaryService documentDictionaryService;

    /**
     * @see DataObjectAuthorizationServiceImpl#attributeValueNeedsToBeEncryptedOnFormsAndLinks
     */
    @Override
    public boolean attributeValueNeedsToBeEncryptedOnFormsAndLinks(Class<?> dataObjectClass, String attributeName) {
        AttributeSecurity attributeSecurity =
                getDataDictionaryService().getAttributeSecurity(dataObjectClass.getName(), attributeName);

        return attributeSecurity != null && attributeSecurity.hasRestrictionThatRemovesValueFromUI();
    }

    /**
     * @see DataObjectAuthorizationServiceImpl#canCreate
     */
    @Override
    public boolean canCreate(Class<?> dataObjectClass, Person user, String docTypeName) {
        DocumentPresentationController documentPresentationController =
                getDocumentDictionaryService().getDocumentPresentationController(docTypeName);
        boolean canCreate =
                ((MaintenanceDocumentPresentationController) documentPresentationController).canCreate(dataObjectClass);
        if (canCreate) {
            DocumentAuthorizer documentAuthorizer = getDocumentDictionaryService().getDocumentAuthorizer(docTypeName);
            canCreate = ((MaintenanceDocumentAuthorizer) documentAuthorizer).canCreate(dataObjectClass, user);
        }
        return canCreate;
    }

    /**
     * @see DataObjectAuthorizationServiceImpl#canMaintain
     */
    @Override
    public boolean canMaintain(Object dataObject, Person user, String docTypeName) {
        return ((MaintenanceDocumentAuthorizer) getDocumentDictionaryService().getDocumentAuthorizer(docTypeName))
                .canMaintain(dataObject, user);
    }

    protected DataDictionaryService getDataDictionaryService() {
        if (dataDictionaryService == null) {
            this.dataDictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();
        }
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    protected DocumentDictionaryService getDocumentDictionaryService() {
        if (documentDictionaryService == null) {
            documentDictionaryService = KRADServiceLocatorWeb.getDocumentDictionaryService();
        }
        return documentDictionaryService;
    }

    public void setDocumentDictionaryService(DocumentDictionaryService documentDictionaryService) {
        this.documentDictionaryService = documentDictionaryService;
    }
}
