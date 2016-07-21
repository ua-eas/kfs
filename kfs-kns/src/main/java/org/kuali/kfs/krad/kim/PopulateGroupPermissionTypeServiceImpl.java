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
import java.util.List;
import java.util.Map;

/**
 * 
 *
 * The order of precedence for this permission type service is as follows:
 *
 * 1 - Group Name
 * 2 - Kim Type Name
 * 3 - Group Namespace
 *
 * If there is a permission that is an exact match for any of these, less granular permissions will not be considered.
 *
 * For example, if there is a populate group permission for KFS-VND groups, a populate group permission for KFS* groups
 * will not be considered.   Likewise, if there is a populate group permission for the group ContractManagers (which has
 * group namespace of KFS-VND), both the populate group permisson for KFS-VND and KFS* will NOT be considered.
 *
 *  ALSO NOTE - At a minimum, a group namespace attribute must be specifed on any populate group permission, even if
 *              it is only a partial namespace.
 */
public class PopulateGroupPermissionTypeServiceImpl extends NamespaceWildcardAllowedAndOrStringExactMatchPermissionTypeServiceImpl {

    @Override
    protected List<Permission> performPermissionMatches(Map<String, String> requestedDetails, List<Permission> permissionsList) {
        String requestedGroupName = requestedDetails.get(KimConstants.AttributeConstants.GROUP_NAME);
        String requestedKimTypeName = requestedDetails.get(KimConstants.AttributeConstants.KIM_TYPE_NAME);
        String requestedNamespaceCode = requestedDetails.get(KimConstants.AttributeConstants.NAMESPACE_CODE);

        List<Permission> exactMatchingPermissions = new ArrayList<Permission>();
        List<Permission> nonKimTypeMatchingPermissions = new ArrayList<Permission>();

        for (Permission kpi : permissionsList ) {
            PermissionBo bo = PermissionBo.from(kpi);
            String groupName = bo.getDetails().get(KimConstants.AttributeConstants.GROUP_NAME);
            if (StringUtils.equals(requestedGroupName, groupName)) {
                exactMatchingPermissions.add(kpi);
            }
        }

        if  (exactMatchingPermissions.isEmpty()) {
            for (Permission kpi : permissionsList ) {
                PermissionBo bo = PermissionBo.from(kpi);
                String kimTypeName = bo.getDetails().get(KimConstants.AttributeConstants.KIM_TYPE_NAME);
                String namespaceCode = bo.getDetails().get(KimConstants.AttributeConstants.NAMESPACE_CODE);
                if (StringUtils.equals(requestedKimTypeName, kimTypeName) &&
                    requestedNamespaceCode.matches(namespaceCode.replaceAll("\\*", ".*"))) {
                        exactMatchingPermissions.add(kpi);
                } else if (StringUtils.isEmpty(kimTypeName)) {
                    nonKimTypeMatchingPermissions.add(kpi);
                }
            }
        }

        if  (exactMatchingPermissions.isEmpty()) {
            for (Permission kpi : permissionsList ) {
                PermissionBo bo = PermissionBo.from(kpi);
                String namespaceCode = bo.getDetails().get(KimConstants.AttributeConstants.NAMESPACE_CODE);
                if (StringUtils.equals(requestedNamespaceCode, namespaceCode)) {
                    exactMatchingPermissions.add(kpi);
                }
            }
        }

        if  (!exactMatchingPermissions.isEmpty()) {
            return super.performPermissionMatches(requestedDetails, exactMatchingPermissions);
        } else {
            return super.performPermissionMatches(requestedDetails, nonKimTypeMatchingPermissions);
        }
    }
}
