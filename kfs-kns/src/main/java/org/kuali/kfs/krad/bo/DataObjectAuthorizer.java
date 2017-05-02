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
package org.kuali.kfs.krad.bo;

import java.util.Map;

/**
 * Invoked to authorize actions requested on data objects (such as edit or view)
 */
public interface DataObjectAuthorizer {

    /**
     * Determines whether the user identified by the given principal ID has the given permission in the context
     * of the data object
     *
     * @param dataObject
     * @param namespaceCode
     * @param permissionName
     * @param principalId
     * @return boolean true if the user is authorized, false if not
     */
    public boolean isAuthorized(Object dataObject, String namespaceCode, String permissionName, String principalId);

    /**
     * Determines whether the user identified by the given principal ID has been granted a permission of the given
     * template in the context of the data object
     *
     * @param dataObject
     * @param namespaceCode
     * @param permissionTemplateName
     * @param principalId
     * @return boolean true if the user is authorized, false if not
     */
    public boolean isAuthorizedByTemplate(Object dataObject, String namespaceCode, String permissionTemplateName,
                                          String principalId);

    /**
     * Determines whether the user identified by the given principal ID has the given permission in the context
     * of the data object, the additional permission details and role qualifiers are used for the check
     *
     * @param dataObject
     * @param namespaceCode
     * @param permissionName
     * @param principalId
     * @param additionalPermissionDetails
     * @param additionalRoleQualifiers
     * @return boolean true if the user is authorized, false if not
     */
    public boolean isAuthorized(Object dataObject, String namespaceCode, String permissionName, String principalId,
                                Map<String, String> additionalPermissionDetails, Map<String, String> additionalRoleQualifiers);

    /**
     * Determines whether the user identified by the given principal ID has been granted a permission of the given
     * template in the context of the data object, the additional permission details and role qualifiers are used for
     * the check
     *
     * @param dataObject
     * @param namespaceCode
     * @param permissionTemplateName
     * @param principalId
     * @param additionalPermissionDetails
     * @param additionalRoleQualifiers
     * @return boolean true if the user is authorized, false if not
     */
    public boolean isAuthorizedByTemplate(Object dataObject, String namespaceCode, String permissionTemplateName,
                                          String principalId, Map<String, String> additionalPermissionDetails,
                                          Map<String, String> additionalRoleQualifiers);

}
