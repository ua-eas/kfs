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

import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.permission.Permission;
import org.kuali.rice.kim.impl.permission.PermissionBo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public class DocumentTypeAndEditModePermissionTypeServiceImpl extends DocumentTypePermissionTypeServiceImpl {

    @Override
    protected List<String> getRequiredAttributes() {
        List<String> attrs = new ArrayList<String>(super.getRequiredAttributes());
        attrs.add(KimConstants.AttributeConstants.EDIT_MODE);
        return attrs;
    }
	
	@Override
	protected List<Permission> performPermissionMatches(
			Map<String, String> requestedDetails,
			List<Permission> permissionsList) {

        List<Permission> matchingPermissions = new ArrayList<Permission>();
		for (Permission permission : permissionsList) {
            PermissionBo bo = PermissionBo.from(permission);
			if (requestedDetails.get(KimConstants.AttributeConstants.EDIT_MODE).equals(bo.getDetails().get(KimConstants.AttributeConstants.EDIT_MODE))) {
				matchingPermissions.add(permission);
			}
		}
		return super.performPermissionMatches(requestedDetails, matchingPermissions);
	}
}
