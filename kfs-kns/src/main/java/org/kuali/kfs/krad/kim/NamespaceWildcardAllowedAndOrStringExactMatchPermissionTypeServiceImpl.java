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
import org.kuali.kfs.coreservice.api.CoreServiceApiServiceLocator;
import org.kuali.kfs.coreservice.api.namespace.Namespace;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.permission.Permission;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.impl.permission.PermissionBo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class NamespaceWildcardAllowedAndOrStringExactMatchPermissionTypeServiceImpl
		extends NamespacePermissionTypeServiceImpl {
	protected static final String NAMESPACE_CODE = KimConstants.UniqueKeyConstants.NAMESPACE_CODE;
	
	protected String exactMatchStringAttributeName;
	protected boolean namespaceRequiredOnStoredMap;
    private List<String> requiredAttributes = new ArrayList<String>();

    @Override
    protected List<String> getRequiredAttributes() {
        return Collections.unmodifiableList(requiredAttributes);
    }

	@Override
	protected List<Permission> performPermissionMatches(Map<String, String> requestedDetails, List<Permission> permissionsList) {
	    List<Permission> matchingPermissions = new ArrayList<Permission>();
        List<Permission> matchingBlankPermissions = new ArrayList<Permission>();
	    String requestedAttributeValue = requestedDetails.get(exactMatchStringAttributeName);
	    for ( Permission kpi : permissionsList ) {
            PermissionBo bo = PermissionBo.from(kpi);
	        String permissionAttributeValue = bo.getDetails().get(exactMatchStringAttributeName);
	        if ( StringUtils.equals(requestedAttributeValue, permissionAttributeValue) ) {
	            matchingPermissions.add(kpi);
	        } else if ( StringUtils.isBlank(permissionAttributeValue) ) {
	            matchingBlankPermissions.add(kpi);
	        }
	    }
	    // if the exact match worked, use those when checking the namespace
	    // otherwise, use those with a blank additional property value
	    if ( !matchingPermissions.isEmpty() ) {
            List<Permission> matchingWithNamespace = super.performPermissionMatches(requestedDetails, matchingPermissions);
	        if ( !namespaceRequiredOnStoredMap ) {
	            // if the namespace is not required and the namespace match would have excluded
	            // the results, return the original set of matches
	            if ( matchingWithNamespace.isEmpty() ) {
	                return matchingPermissions;
	            }
	        }
            return matchingWithNamespace;
	    } else if ( !matchingBlankPermissions.isEmpty() ) {
            List<Permission> matchingWithNamespace = super.performPermissionMatches(requestedDetails, matchingBlankPermissions);
            if ( !namespaceRequiredOnStoredMap ) {
                // if the namespace is not required and the namespace match would have excluded
                // the results, return the original set of matches
                if ( matchingWithNamespace.isEmpty() ) {
                    return matchingBlankPermissions;
                }
            }
            return matchingWithNamespace;
	    }
	    return matchingPermissions; // will be empty if drops to here
	}
	
	public void setExactMatchStringAttributeName(
			String exactMatchStringAttributeName) {
		this.exactMatchStringAttributeName = exactMatchStringAttributeName;
		requiredAttributes.add(exactMatchStringAttributeName);
	}

	public void setNamespaceRequiredOnStoredMap(
			boolean namespaceRequiredOnStoredMap) {
		this.namespaceRequiredOnStoredMap = namespaceRequiredOnStoredMap;
	}

	/**
	 * Overrides the superclass's version of this method in order to account for "namespaceCode" permission detail values containing wildcards.
	 */
	@Override
	protected List<RemotableAttributeError> validateReferencesExistAndActive(KimType kimType, Map<String, String> attributes, List<RemotableAttributeError> previousValidationErrors) {
		List<RemotableAttributeError> errors = new ArrayList<RemotableAttributeError>();
		Map<String, String> nonNamespaceCodeAttributes = new HashMap<String, String>(attributes);
		// Check if "namespaceCode" is one of the permission detail values.
		if (attributes.containsKey(NAMESPACE_CODE)) {
			nonNamespaceCodeAttributes.remove(NAMESPACE_CODE);

            final Namespace namespace =
                    StringUtils.isBlank(attributes.get(NAMESPACE_CODE)) ?
                            null : CoreServiceApiServiceLocator.getNamespaceService().getNamespace(attributes.get(NAMESPACE_CODE));

            if (namespace != null) {
			    errors.addAll(super.validateReferencesExistAndActive(kimType, Collections.singletonMap(NAMESPACE_CODE,
                        namespace.getCode()), previousValidationErrors));
			} else {
				// If no namespaces were found, let the superclass generate an appropriate error.
				errors.addAll(super.validateReferencesExistAndActive(kimType, Collections.singletonMap(NAMESPACE_CODE,
                        attributes.get(NAMESPACE_CODE)), previousValidationErrors));
			}
		}
		// Validate all non-namespaceCode attributes.
		errors.addAll(super.validateReferencesExistAndActive(kimType, nonNamespaceCodeAttributes, previousValidationErrors));
		return errors;
	}
}
