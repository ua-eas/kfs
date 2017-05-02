/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
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
package org.kuali.kfs.kns.web.struts.form;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.kns.service.KNSServiceLocator;
import org.kuali.kfs.krad.document.TransactionalDocument;
import org.kuali.kfs.krad.service.DataDictionaryService;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.kim.api.KimConstants;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


/**
 * This class is the base action form for all transactional documents.
 */
public class KualiTransactionalDocumentFormBase extends KualiDocumentFormBase {
    private static final Logger LOG = Logger.getLogger(KualiTransactionalDocumentFormBase.class);

    private static final long serialVersionUID = 6463383454050206811L;
    @SuppressWarnings("unchecked")
    protected Map forcedReadOnlyFields;

    /**
     * This constructor sets up empty instances for the dependent objects...
     */
    @SuppressWarnings("unchecked")
    public KualiTransactionalDocumentFormBase() {
        super();

        // create a blank DocumentActionFlags instance, since form-recreation needs it
        forcedReadOnlyFields = new HashMap();
    }

    /**
     * @see KualiDocumentFormBase#populate(javax.servlet.http.HttpServletRequest)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);
        populationSpecialEmptyFields(request);
    }

    /**
     * This method retrieves an instance of the form.
     *
     * @return
     */
    public TransactionalDocument getTransactionalDocument() {
        return (TransactionalDocument) getDocument();
    }


    /**
     * Locates the <code>DictionaryService</code> to discover the type name of the document.
     *
     * @return
     */
    protected String discoverDocumentTypeName() {
        return ((DataDictionaryService) KRADServiceLocatorWeb.getDataDictionaryService()).getDataDictionary().getDocumentEntry(getDocument().getClass().getName()).getDocumentTypeName();
    }

    /**
     * This method formats the given java.sql.Date as MMM d, yyyy.
     *
     * @param reversalDate
     * @return String
     */
    protected static String formatReversalDate(java.sql.Date reversalDate) {
        if (reversalDate == null) {
            return "";
        }
        // new for thread safety
        return CoreApiServiceLocator.getDateTimeService().toString(reversalDate, "MMM d, yyyy");
    }

    /**
     * Gets the forcedReadOnlyFields attribute.
     *
     * @return Returns the forcedReadOnlyFields.
     */
    @SuppressWarnings("unchecked")
    public Map getForcedReadOnlyFields() {
        return forcedReadOnlyFields;
    }

    /**
     * Sets the forcedReadOnlyFields attribute value.
     *
     * @param forcedReadOnlyFields The forcedReadOnlyFields to set.
     */
    @SuppressWarnings("unchecked")
    public void setForcedReadOnlyFields(Map forcedReadOnlyFields) {
        this.forcedReadOnlyFields = forcedReadOnlyFields;
    }

    /**
     * Uses the "checkboxToReset" parameter to find checkboxes which had not been
     * populated in the request and attempts to populate them
     *
     * @param request the request to populate
     */
    protected void populateFalseCheckboxes(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap.get("checkboxToReset") != null) {
            final String[] checkboxesToReset = request.getParameterValues("checkboxToReset");
            if (checkboxesToReset != null && checkboxesToReset.length > 0) {
                for (int i = 0; i < checkboxesToReset.length; i++) {
                    String propertyName = (String) checkboxesToReset[i];
                    if (!StringUtils.isBlank(propertyName) && parameterMap.get(propertyName) == null) {
                        populateForProperty(propertyName, KimConstants.KIM_ATTRIBUTE_BOOLEAN_FALSE_STR_VALUE_DISPLAY, parameterMap);
                    } else if (!StringUtils.isBlank(propertyName) && parameterMap.get(propertyName) != null && parameterMap.get(propertyName).length >= 1 && parameterMap.get(propertyName)[0].equalsIgnoreCase("on")) {
                        populateForProperty(propertyName, KimConstants.KIM_ATTRIBUTE_BOOLEAN_TRUE_STR_VALUE_DISPLAY, parameterMap);
                    }
                }
            }
        }
    }

    /**
     * Uses the "checkboxToReset" parameter to find checkboxes which had not been
     * populated in the request and attempts to populate them
     *
     * @param request the request to populate
     */
    protected void populateEmptyMultiSelect(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap.get("multiSelectToReset") != null) {
            final String[] multiSelectToReset = request.getParameterValues("multiSelectToReset");
            if (multiSelectToReset != null && multiSelectToReset.length > 0) {
                for (int i = 0; i < multiSelectToReset.length; i++) {
                    String propertyName = (String) multiSelectToReset[i];
                    if (!StringUtils.isBlank(propertyName) && parameterMap.get(propertyName) == null) {
                        populateForProperty(propertyName, "", parameterMap);
                    } else if (!StringUtils.isBlank(propertyName) && parameterMap.get(propertyName) != null && parameterMap.get(propertyName).length >= 1 && parameterMap.get(propertyName)[0].equalsIgnoreCase("on")) {
                        populateForProperty(propertyName, request.getParameter(propertyName), parameterMap);
                    }
                }
            }
        }
    }

    protected void populationSpecialEmptyFields(HttpServletRequest request) {
        populateFalseCheckboxes(request);
        populateEmptyMultiSelect(request);
    }

    @SuppressWarnings("unchecked")
    protected TransactionalDocument instantiateTransactionalDocumentByDocumentTypeName(String documentTypeName) {
        Class<TransactionalDocument> transDocClass = KNSServiceLocator.getTransactionalDocumentDictionaryService().getDocumentClassByName(documentTypeName);
        if (transDocClass != null) {
            try {
                return transDocClass.newInstance();
            } catch (Exception ex) {
                LOG.error("Unable to instantiate transDocClass: " + transDocClass, ex);
            }
        } else {
            LOG.error("Unable to retrieve transactional document class for type: " + documentTypeName);
        }
        return null;
    }

    /**
     * This overridden method ...
     *
     * @see KualiForm#shouldMethodToCallParameterBeUsed(java.lang.String, java.lang.String, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public boolean shouldMethodToCallParameterBeUsed(
        String methodToCallParameterName,
        String methodToCallParameterValue, HttpServletRequest request) {
        if (methodToCallParameterName.startsWith(KRADConstants.DISPATCH_REQUEST_PARAMETER + "." + KRADConstants.POST_TEXT_AREA_TO_PARENT)) {
            return true;
        }
        return super.shouldMethodToCallParameterBeUsed(methodToCallParameterName, methodToCallParameterValue, request);
    }
}
