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
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Type service for the 'View' KIM type which matches on the id for a UIF view
 */
public class ViewPermissionTypeServiceImpl extends PermissionTypeServiceBase {
    private boolean exactMatchPriority = true;

    @Override
    protected List<String> getRequiredAttributes() {
        List<String> attributes = new ArrayList<String>(super.getRequiredAttributes());
        attributes.add(KimConstants.AttributeConstants.VIEW_ID);

        return Collections.unmodifiableList(attributes);
    }

    /**
     * Filters the given permission list to return those that match the view id qualifier
     * <p>
     * <p>
     * By default, this method will return all exact matches if any exist, and it will only return partial matches
     * if there are no exact matches. i.e. KR-DocumentView will have priority over KR-*. If ExactMatchPriority is
     * false, then this method will return all exact AND partial matching permissions.  By default, ExactMatchPriority
     * will be set to true.
     * </p>
     *
     * @param requestedDetails - map of details requested with permission (used for matching)
     * @param permissionsList  - list of permissions to process for matches
     * @return List<Permission> list of permissions that match the requested details
     */
    @Override
    protected List<Permission> performPermissionMatches(Map<String, String> requestedDetails,
                                                        List<Permission> permissionsList) {
        List<Permission> matchingPermissions = new ArrayList<Permission>();

        String requestedViewId = requestedDetails.get(KimConstants.AttributeConstants.VIEW_ID);

        // add all exact matches to the list
        for (Permission permission : permissionsList) {
            PermissionBo bo = PermissionBo.from(permission);

            String permissionViewId = bo.getDetails().get(KimConstants.AttributeConstants.VIEW_ID);
            if (StringUtils.equals(requestedViewId, permissionViewId)) {
                matchingPermissions.add(permission);
            }
        }

        // add partial matches to the list if there are no exact matches or if exactMatchPriority is false
        if ((exactMatchPriority && matchingPermissions.isEmpty()) || (!(exactMatchPriority))) {
            for (Permission kpi : permissionsList) {
                PermissionBo bo = PermissionBo.from(kpi);

                String permissionViewId = bo.getDetails().get(KimConstants.AttributeConstants.VIEW_ID);
                if (requestedViewId != null && permissionViewId != null && (!(StringUtils.equals(requestedViewId,
                    permissionViewId))) && requestedViewId.matches(permissionViewId.replaceAll("\\*", ".*"))) {
                    matchingPermissions.add(kpi);
                }
            }
        }

        return matchingPermissions;
    }

    /**
     * Indicates whether permissions with details that exactly match the requested details have priority over
     * permissions with details that partially match (based on wildcard match). Default is set to true
     *
     * @return boolean true if exact matches should be given priority, false if not
     */
    public boolean getExactMatchPriority() {
        return this.exactMatchPriority;
    }

    /**
     * Setter for the exact match priority indicator
     *
     * @param exactMatchPriority
     */
    public void setExactMatchPriority(Boolean exactMatchPriority) {
        this.exactMatchPriority = exactMatchPriority;
    }
}
