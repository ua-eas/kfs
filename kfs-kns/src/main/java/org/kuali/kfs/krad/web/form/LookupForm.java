/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.kfs.krad.web.form;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.lookup.LookupUtils;
import org.kuali.kfs.krad.lookup.Lookupable;
import org.kuali.kfs.krad.uif.UifConstants.ViewType;
import org.kuali.kfs.krad.uif.view.LookupView;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.KRADUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Form class for <code>LookupView</code> screens
 *
 *
 */
public class LookupForm extends UifFormBase {
    private static final long serialVersionUID = -7323484966538685327L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LookupForm.class);

    private String dataObjectClassName;
    private String docNum;
    private String referencesToRefresh;

    private boolean multipleValuesSelect;
    private String lookupCollectionName;

    private Map<String, String> criteriaFields;
    private Map<String, String> fieldConversions;

    private boolean atLeastOneRowReturnable;
    private boolean atLeastOneRowHasActions;

    private Collection<?> searchResults;

    private boolean redirectedLookup;

    public LookupForm() {
        super();

        setViewTypeName(ViewType.LOOKUP);
        atLeastOneRowReturnable = false;
        atLeastOneRowHasActions = false;
        multipleValuesSelect = false;
        redirectedLookup = false;

        criteriaFields = new HashMap<String, String>();
        fieldConversions = new HashMap<String, String>();
    }

    /**
     * Picks out business object name from the request to get retrieve a
     * lookupable and set properties
     */
    @Override
    public void postBind(HttpServletRequest request) {
        super.postBind(request);

        try {
            Lookupable lookupable = getLookupable();
            if (lookupable == null) {
                // assume lookupable will be set by controller or a redirect will happen
                return;
            }

            if (StringUtils.isBlank(getDataObjectClassName())) {
                setDataObjectClassName(((LookupView) getView()).getDataObjectClassName().getName());
            }

            // init lookupable with data object class
            Class<?> dataObjectClass = Class.forName(getDataObjectClassName());
            lookupable.setDataObjectClass(dataObjectClass);

            // if showMaintenanceLinks is not already true, only show maintenance links
            // if the lookup was called from the home application view
            if (!((LookupView) getView()).isShowMaintenanceLinks()) {
                // TODO replace with check to history
                if (StringUtils.contains(getReturnLocation(), "/" + KRADConstants.PORTAL_ACTION) ||
                        StringUtils.contains(getReturnLocation(), "/index.html")) {
                    ((LookupView) getView()).setShowMaintenanceLinks(true);
                }
            }

            // populate lookup read only fields list on lookupable
            lookupable.setReadOnlyFieldsList(getReadOnlyFieldsList());

            // populate field conversions list
            if (request.getParameter(KRADConstants.CONVERSION_FIELDS_PARAMETER) != null) {
                String conversionFields = request.getParameter(KRADConstants.CONVERSION_FIELDS_PARAMETER);
                setFieldConversions(KRADUtils.convertStringParameterToMap(conversionFields));
                lookupable.setFieldConversions(getFieldConversions());
            }

            // perform upper casing of lookup parameters
            Map<String, String> fieldValues = new HashMap<String, String>();
            Map<String, String> formFields = getCriteriaFields();

            if (formFields != null) {
                for (Map.Entry<String, String> entry : formFields.entrySet()) {
                    // check here to see if this field is a criteria element on the form
                    fieldValues.put(entry.getKey(),
                            LookupUtils.forceUppercase(dataObjectClass, entry.getKey(), entry.getValue()));
                }
            }

            // fieldValues.put(UifParameters.RETURN_FORM_KEY, getReturnFormKey());
            // fieldValues.put(UifParameters.RETURN_LOCATION, getReturnLocation());
            if (StringUtils.isNotBlank(getDocNum())) {
                fieldValues.put(KRADConstants.DOC_NUM, getDocNum());
            }

            this.setCriteriaFields(fieldValues);
        } catch (ClassNotFoundException e) {
            LOG.error("Object class " + getDataObjectClassName() + " not found");
            throw new RuntimeException("Object class " + getDataObjectClassName() + " not found", e);
        }
    }

    public Lookupable getLookupable() {
        if ((getView() != null) && Lookupable.class.isAssignableFrom(getView().getViewHelperService().getClass())) {
            return (Lookupable) getView().getViewHelperService();
        }

        return null;
    }

    public String getDataObjectClassName() {
        return this.dataObjectClassName;
    }

    public void setDataObjectClassName(String dataObjectClassName) {
        this.dataObjectClassName = dataObjectClassName;
    }

    public String getDocNum() {
        return this.docNum;
    }

    public void setDocNum(String docNum) {
        this.docNum = docNum;
    }

    public String getReferencesToRefresh() {
        return referencesToRefresh;
    }

    public void setReferencesToRefresh(String referencesToRefresh) {
        this.referencesToRefresh = referencesToRefresh;
    }

    /**
     * Indicates whether multiple values select should be enabled for the lookup
     *
     * <p>
     * When set to true, the select field is enabled for the lookup results group that allows the user
     * to select one or more rows for returning
     * </p>
     *
     * @return boolean true if multiple values should be enabled, false otherwise
     */
    public boolean isMultipleValuesSelect() {
        return multipleValuesSelect;
    }

    /**
     * Setter for the multiple values select indicator
     *
     * @param multipleValuesSelect
     */
    public void setMultipleValuesSelect(boolean multipleValuesSelect) {
        this.multipleValuesSelect = multipleValuesSelect;
    }

    /**
     * For the case of multi-value lookup, indicates the collection that should be populated with
     * the return results
     *
     * @return String collection name (must be full binding path)
     */
    public String getLookupCollectionName() {
        return lookupCollectionName;
    }

    /**
     * Setter for the name of the collection that should be populated with lookup results
     *
     * @param lookupCollectionName
     */
    public void setLookupCollectionName(String lookupCollectionName) {
        this.lookupCollectionName = lookupCollectionName;
    }

    public Map<String, String> getCriteriaFields() {
        return this.criteriaFields;
    }

    public void setCriteriaFields(Map<String, String> criteriaFields) {
        this.criteriaFields = criteriaFields;
    }

    public Map<String, String> getFieldConversions() {
        return this.fieldConversions;
    }

    public void setFieldConversions(Map<String, String> fieldConversions) {
        this.fieldConversions = fieldConversions;
    }

    public Collection<?> getSearchResults() {
        return this.searchResults;
    }

    public void setSearchResults(Collection<?> searchResults) {
        this.searchResults = searchResults;
    }

    public boolean isAtLeastOneRowReturnable() {
        return atLeastOneRowReturnable;
    }

    public void setAtLeastOneRowReturnable(boolean atLeastOneRowReturnable) {
        this.atLeastOneRowReturnable = atLeastOneRowReturnable;
    }

    public boolean isAtLeastOneRowHasActions() {
        return atLeastOneRowHasActions;
    }

    public void setAtLeastOneRowHasActions(boolean atLeastOneRowHasActions) {
        this.atLeastOneRowHasActions = atLeastOneRowHasActions;
    }

    /**
     * Indicates whether the requested was redirected from the lookup framework due to an external object
     * request. This prevents the framework from performing another redirect check
     *
     * @return boolean true if request was a redirect, false if not
     */
    public boolean isRedirectedLookup() {
        return redirectedLookup;
    }

    /**
     * Setter for the redirected request indicator
     *
     * @param redirectedLookup
     */
    public void setRedirectedLookup(boolean redirectedLookup) {
        this.redirectedLookup = redirectedLookup;
    }
}
