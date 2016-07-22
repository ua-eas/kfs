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


public class DocumentTypeAndRelationshipToNoteAuthorPermissionTypeService
		extends DocumentTypePermissionTypeServiceImpl {

	@Override
	protected List<Permission> performPermissionMatches(
			Map<String, String> requestedDetails,
			List<Permission> permissionsList) {

		List<Permission> matchingPermissions = new ArrayList<Permission>();
		if (requestedDetails == null) {
			return matchingPermissions; // empty list
		}

		// loop over the permissions, checking the non-document-related ones
		for (Permission permission : permissionsList) {
            PermissionBo bo = PermissionBo.from(permission);
			if (Boolean.parseBoolean(requestedDetails
					.get(KimConstants.AttributeConstants.CREATED_BY_SELF))) {
				if(Boolean.parseBoolean(bo.getDetails().get(
						KimConstants.AttributeConstants.CREATED_BY_SELF_ONLY))){
					matchingPermissions.add(permission);
				}

			}else{
				if (!Boolean.parseBoolean(bo.getDetails().get(
						KimConstants.AttributeConstants.CREATED_BY_SELF_ONLY))) {
					matchingPermissions.add(permission);
				}
			}

		}

		matchingPermissions = super.performPermissionMatches(requestedDetails, matchingPermissions);
		return matchingPermissions;
	}
}
