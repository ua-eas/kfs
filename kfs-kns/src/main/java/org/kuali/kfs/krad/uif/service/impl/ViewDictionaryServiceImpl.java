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
package org.kuali.kfs.krad.uif.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.datadictionary.DataDictionary;
import org.kuali.kfs.krad.inquiry.Inquirable;
import org.kuali.kfs.krad.service.DataDictionaryService;
import org.kuali.kfs.krad.uif.UifConstants;
import org.kuali.kfs.krad.uif.UifConstants.ViewType;
import org.kuali.kfs.krad.uif.UifParameters;
import org.kuali.kfs.krad.uif.service.ViewDictionaryService;
import org.kuali.kfs.krad.uif.util.ViewModelUtils;
import org.kuali.kfs.krad.uif.view.LookupView;
import org.kuali.kfs.krad.uif.view.View;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.springframework.beans.PropertyValues;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of <code>ViewDictionaryService</code>
 * <p>
 * <p>
 * Pulls view entries from the data dictionary to implement the various query
 * methods
 * </p>
 */
public class ViewDictionaryServiceImpl implements ViewDictionaryService {

    private DataDictionaryService dataDictionaryService;

    /**
     * @see ViewDictionaryService#getInquirable(java.lang.Class,
     * java.lang.String)
     */
    public Inquirable getInquirable(Class<?> dataObjectClass, String viewName) {
        Inquirable inquirable = null;

        if (StringUtils.isBlank(viewName)) {
            viewName = UifConstants.DEFAULT_VIEW_NAME;
        }

        Map<String, String> indexKey = new HashMap<String, String>();
        indexKey.put(UifParameters.VIEW_NAME, viewName);
        indexKey.put(UifParameters.DATA_OBJECT_CLASS_NAME, dataObjectClass.getName());

        // get view properties
        PropertyValues propertyValues = getDataDictionary().getViewPropertiesByType(ViewType.INQUIRY, indexKey);

        String viewHelperServiceClassName = ViewModelUtils.getStringValFromPVs(propertyValues,
            "viewHelperServiceClassName");
        if (StringUtils.isNotBlank(viewHelperServiceClassName)) {
            try {
                inquirable = (Inquirable) ObjectUtils.newInstance(Class.forName(viewHelperServiceClassName));
            } catch (ClassNotFoundException e) {
                throw new RiceRuntimeException(
                    "Unable to find class for inquirable classname: " + viewHelperServiceClassName, e);
            }
        }

        return inquirable;
    }

    /**
     * @see ViewDictionaryService#isInquirable(java.lang.Class)
     */
    public boolean isInquirable(Class<?> dataObjectClass) {
        Map<String, String> indexKey = new HashMap<String, String>();
        indexKey.put(UifParameters.VIEW_NAME, UifConstants.DEFAULT_VIEW_NAME);
        indexKey.put(UifParameters.DATA_OBJECT_CLASS_NAME, dataObjectClass.getName());

        boolean isInquirable = getDataDictionary().viewByTypeExist(ViewType.INQUIRY, indexKey);

        return isInquirable;
    }

    /**
     * @see ViewDictionaryService#isLookupable(java.lang.Class)
     */
    public boolean isLookupable(Class<?> dataObjectClass) {
        Map<String, String> indexKey = new HashMap<String, String>();
        indexKey.put(UifParameters.VIEW_NAME, UifConstants.DEFAULT_VIEW_NAME);
        indexKey.put(UifParameters.DATA_OBJECT_CLASS_NAME, dataObjectClass.getName());

        boolean isLookupable = getDataDictionary().viewByTypeExist(ViewType.LOOKUP, indexKey);

        return isLookupable;
    }

    /**
     * @see ViewDictionaryService#isMaintainable(java.lang.Class)
     */
    public boolean isMaintainable(Class<?> dataObjectClass) {
        Map<String, String> indexKey = new HashMap<String, String>();
        indexKey.put(UifParameters.VIEW_NAME, UifConstants.DEFAULT_VIEW_NAME);
        indexKey.put(UifParameters.DATA_OBJECT_CLASS_NAME, dataObjectClass.getName());

        boolean isMaintainable = getDataDictionary().viewByTypeExist(ViewType.MAINTENANCE, indexKey);

        return isMaintainable;
    }

    /**
     * @see org.kuali.rice.krad.uif.service.impl.ViewDictionaryService#getResultSetLimitForLookup(java.lang.Class)
     */
    @Override
    public Integer getResultSetLimitForLookup(Class<?> dataObjectClass) {
        LookupView lookupView = null;

        List<View> lookupViews = getDataDictionary().getViewsForType(UifConstants.ViewType.LOOKUP);
        for (View view : lookupViews) {
            LookupView lView = (LookupView) view;

            if (StringUtils.equals(lView.getDataObjectClassName().getName(), dataObjectClass.getName())) {
                // if we already found a lookup view, only override if this is the default
                if (lookupView != null) {
                    if (StringUtils.equals(lView.getViewName(), UifConstants.DEFAULT_VIEW_NAME)) {
                        lookupView = lView;
                    }
                } else {
                    lookupView = lView;
                }
            }
        }

        if (lookupView != null) {
            return lookupView.getResultSetLimit();
        }

        return null;
    }

    protected DataDictionary getDataDictionary() {
        return getDataDictionaryService().getDataDictionary();
    }

    protected DataDictionaryService getDataDictionaryService() {
        return this.dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }
}
