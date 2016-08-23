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
package org.kuali.kfs.kns.kim.permission;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.kns.kim.type.DataDictionaryTypeServiceBase;
import org.kuali.rice.kim.api.permission.Permission;
import org.kuali.rice.kim.framework.permission.PermissionTypeService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @deprecated A krad integrated type service base class will be provided in the future.
 */
@Deprecated
public class PermissionTypeServiceBase extends DataDictionaryTypeServiceBase implements PermissionTypeService {

	@Override
	public final List<Permission> getMatchingPermissions(Map<String, String> requestedDetails, List<Permission> permissionsList) {
		requestedDetails = translateInputAttributes(requestedDetails);
		validateRequiredAttributesAgainstReceived(requestedDetails);
		return Collections.unmodifiableList(performPermissionMatches(requestedDetails, permissionsList));
	}

	/**
	 * Internal method for matching permissions.  Override this method to customize the matching behavior.
	 *
	 * This base implementation uses the {@link #performMatch(Map, Map)} method
	 * to perform an exact match on the permission details and return all that are equal.
	 */
	protected List<Permission> performPermissionMatches(Map<String, String> requestedDetails, List<Permission> permissionsList) {
		List<Permission> matchingPermissions = new ArrayList<Permission>();
		for (Permission permission : permissionsList) {
			if ( performMatch(requestedDetails, permission.getAttributes()) ) {
				matchingPermissions.add( permission );
			}
		}
		return matchingPermissions;
	}

	/**
	 *
	 * Internal method for checking if property name matches
	 *
	 * @param requestedDetailsPropertyName name of requested details property
	 * @param permissionDetailsPropertyName name of permission details property
	 * @return boolean
	 */
	protected boolean doesPropertyNameMatch(
			String requestedDetailsPropertyName,
			String permissionDetailsPropertyName) {
		if (StringUtils.isBlank(permissionDetailsPropertyName)) {
			return true;
		}
		if ( requestedDetailsPropertyName == null ) {
		    requestedDetailsPropertyName = ""; // prevent NPE
		}
		return StringUtils.equals(requestedDetailsPropertyName, permissionDetailsPropertyName)
				|| (requestedDetailsPropertyName.startsWith(permissionDetailsPropertyName+"."));
	}
}
