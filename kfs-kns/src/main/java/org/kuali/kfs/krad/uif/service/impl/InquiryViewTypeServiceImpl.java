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

import org.kuali.kfs.krad.uif.UifConstants;
import org.kuali.kfs.krad.uif.UifConstants.ViewType;
import org.kuali.kfs.krad.uif.UifParameters;
import org.kuali.kfs.krad.uif.service.ViewTypeService;
import org.kuali.kfs.krad.uif.util.ViewModelUtils;
import org.springframework.beans.PropertyValues;

import java.util.HashMap;
import java.util.Map;

/**
 * Type service implementation for Inquiry views
 */
public class InquiryViewTypeServiceImpl implements ViewTypeService {

    /**
     * @see ViewTypeService#getViewTypeName()
     */
    public ViewType getViewTypeName() {
        return ViewType.INQUIRY;
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
     * @see ViewTypeService#getParametersFromRequest(java.util.Map)
     */
    public Map<String, String> getParametersFromRequest(Map<String, String> requestParameters) {
        Map<String, String> parameters = new HashMap<String, String>();

        if (requestParameters.containsKey(UifParameters.VIEW_NAME)) {
            parameters.put(UifParameters.VIEW_NAME, requestParameters.get(UifParameters.VIEW_NAME));
        } else {
            parameters.put(UifParameters.VIEW_NAME, UifConstants.DEFAULT_VIEW_NAME);
        }

        if (requestParameters.containsKey(UifParameters.DATA_OBJECT_CLASS_NAME)) {
            parameters.put(UifParameters.DATA_OBJECT_CLASS_NAME,
                requestParameters.get(UifParameters.DATA_OBJECT_CLASS_NAME));
        } else {
            throw new RuntimeException("Parameter '" + UifParameters.DATA_OBJECT_CLASS_NAME
                + "' must be given to find views of type: " + getViewTypeName());
        }

        return parameters;
    }

}
