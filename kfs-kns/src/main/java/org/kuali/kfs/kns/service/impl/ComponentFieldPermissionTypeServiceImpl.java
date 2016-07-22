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
package org.kuali.kfs.kns.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.permission.Permission;
import org.kuali.rice.kim.impl.permission.PermissionBo;
import org.kuali.kfs.kns.kim.permission.PermissionTypeServiceBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ComponentFieldPermissionTypeServiceImpl extends PermissionTypeServiceBase {

	/**
	 * Compare the component and property names between the request and matching permissions.
	 * Make entries with a matching property name take precedence over those with blank property
	 * names on the stored permissions.  Only match entries with blank property names if
	 * no entries match on the exact property name.
	 */
	@Override
	protected List<Permission> performPermissionMatches(Map<String, String> requestedDetails,
			List<Permission> permissionsList) {

        List<Permission> propertyMatches = new ArrayList<Permission>();
		List<Permission> prefixPropertyMatches = new ArrayList<Permission>();
		List<Permission> blankPropertyMatches = new ArrayList<Permission>();
		String propertyName = requestedDetails.get(KimConstants.AttributeConstants.PROPERTY_NAME);
		String componentName = requestedDetails.get(KimConstants.AttributeConstants.COMPONENT_NAME);
		for ( Permission kpi : permissionsList ) {
            PermissionBo bo = PermissionBo.from(kpi);
			if ( StringUtils.equals( componentName, bo.getDetails().get( KimConstants.AttributeConstants.COMPONENT_NAME ) ) ) {
				String permPropertyName = bo.getDetails().get(KimConstants.AttributeConstants.PROPERTY_NAME);
				if ( StringUtils.isBlank( permPropertyName ) ) {
					blankPropertyMatches.add( kpi );
				} else if ( StringUtils.equals( propertyName, permPropertyName ) ) {
					propertyMatches.add( kpi );
				} else if ( doesPropertyNameMatch(propertyName, permPropertyName) ) {
					prefixPropertyMatches.add( kpi );
				}
			}
		}
		if ( !propertyMatches.isEmpty() ) {
			return propertyMatches;
		} else if ( !prefixPropertyMatches.isEmpty() ) {
			return prefixPropertyMatches;
		} else {
			return blankPropertyMatches;
		}
	}

}
