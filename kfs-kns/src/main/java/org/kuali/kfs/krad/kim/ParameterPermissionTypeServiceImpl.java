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
package org.kuali.kfs.krad.kim;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.permission.Permission;
import org.kuali.rice.kim.impl.permission.PermissionBo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ParameterPermissionTypeServiceImpl extends NamespaceWildcardAllowedAndOrStringExactMatchPermissionTypeServiceImpl {

    @Override
    protected List<String> getRequiredAttributes() {
        final List<String> attrs = new ArrayList<String>(super.getRequiredAttributes());
        attrs.add(KimConstants.AttributeConstants.PARAMETER_NAME);
        attrs.add(KimConstants.AttributeConstants.COMPONENT_NAME);
        return Collections.unmodifiableList(attrs);
    }

    @Override
    protected boolean isCheckRequiredAttributes() {
        return true;
    }
    
    @Override
    protected List<Permission> performPermissionMatches(Map<String, String> requestedDetails, List<Permission> permissionsList) {
        String requestedParameterName = requestedDetails.get(KimConstants.AttributeConstants.PARAMETER_NAME);
        String requestedComponentName = requestedDetails.get(KimConstants.AttributeConstants.COMPONENT_NAME);
        List<Permission> matchingPermissions = new ArrayList<Permission>();
        for (Permission kpi : permissionsList ) {
            PermissionBo bo = PermissionBo.from(kpi);
            String parameterName = bo.getDetails().get(KimConstants.AttributeConstants.PARAMETER_NAME);
            String componentName = bo.getDetails().get(KimConstants.AttributeConstants.COMPONENT_NAME);
            if ( (StringUtils.isBlank(parameterName)
                    || StringUtils.equals(requestedParameterName, parameterName)) 
                &&(StringUtils.isBlank(componentName)
                        || StringUtils.equals(requestedComponentName, componentName))) {
                matchingPermissions.add(kpi);
            }
        }
        return super.performPermissionMatches(requestedDetails, matchingPermissions);
    }    
}
