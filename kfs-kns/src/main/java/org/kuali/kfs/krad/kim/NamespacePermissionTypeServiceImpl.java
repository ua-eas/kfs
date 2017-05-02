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
package org.kuali.kfs.krad.kim;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.kns.kim.permission.PermissionTypeServiceBase;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.permission.Permission;
import org.kuali.rice.kim.impl.permission.PermissionBo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class NamespacePermissionTypeServiceImpl extends PermissionTypeServiceBase {

    private Boolean exactMatchPriority = true;

    {
//		requiredAttributes.add(KimAttributes.NAMESPACE_CODE);
    }

    /**
     * Check for entries that match the namespace.
     * <p>
     * By default, this method will return all exact matches if any exist, and it will only return partial matches if there are no exact matches.
     * i.e. KR-NS will have priority over KR-*
     * <p>
     * If ExactMatchPriority is false, then this method will return all exact AND partial matching permissions.  By default, ExactMatchPriority will be set to true.
     */
    @Override
    protected List<Permission> performPermissionMatches(Map<String, String> requestedDetails, List<Permission> permissionsList) {
        List<Permission> matchingPermissions = new ArrayList<Permission>();

        String requestedNamespaceCode = requestedDetails.get(KimConstants.AttributeConstants.NAMESPACE_CODE);

        // Add all exact matches to the list
        for (Permission permission : permissionsList) {
            PermissionBo bo = PermissionBo.from(permission);
            String permissionNamespaceCode = bo.getDetails().get(KimConstants.AttributeConstants.NAMESPACE_CODE);
            if (StringUtils.equals(requestedNamespaceCode, permissionNamespaceCode)) {
                matchingPermissions.add(permission);
            }
        }

        // Add partial matches to the list if there are no exact matches or if exactMatchPriority is false
        if ((exactMatchPriority && matchingPermissions.isEmpty()) || (!(exactMatchPriority))) {
            for (Permission kpi : permissionsList) {
                PermissionBo bo = PermissionBo.from(kpi);
                String permissionNamespaceCode = bo.getDetails().get(KimConstants.AttributeConstants.NAMESPACE_CODE);
                if (requestedNamespaceCode != null
                    && permissionNamespaceCode != null
                    && (!(StringUtils.equals(requestedNamespaceCode, permissionNamespaceCode)))
                    && requestedNamespaceCode.matches(permissionNamespaceCode.replaceAll("\\*", ".*"))) {
                    matchingPermissions.add(kpi);
                }
            }
        }
        return matchingPermissions;
    }

    public Boolean getExactMatchPriority() {
        return this.exactMatchPriority;
    }

    public void setExactMatchPriority(Boolean exactMatchPriority) {
        this.exactMatchPriority = exactMatchPriority;
    }
}
