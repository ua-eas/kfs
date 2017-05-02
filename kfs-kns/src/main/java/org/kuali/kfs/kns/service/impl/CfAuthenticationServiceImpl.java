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
package org.kuali.kfs.kns.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.kns.bo.AuthenticationValidationResponse;
import org.kuali.kfs.kns.service.CfAuthenticationService;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.permission.PermissionService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;

import java.util.Collections;

public class CfAuthenticationServiceImpl implements CfAuthenticationService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CfAuthenticationServiceImpl.class);

    private IdentityService identityService;
    private PermissionService permissionService;

    @Override
    public AuthenticationValidationResponse validatePrincipalName(String principalName) {
        LOG.debug("validatePrincipalName() started");

        if (StringUtils.isBlank(principalName)) {
            return AuthenticationValidationResponse.INVALID_PRINCIPAL_NAME_BLANK;
        }

        Principal principal = getIdentityService().getPrincipalByPrincipalName(principalName);
        if (principal == null) {
            return AuthenticationValidationResponse.INVALID_PRINCIPAL_DOES_NOT_EXIST;
        }

        if (!isAuthorizedToLogin(principal.getPrincipalId())) {
            return AuthenticationValidationResponse.INVALID_PRINCIPAL_CANNOT_LOGIN;
        }

        return AuthenticationValidationResponse.VALID_AUTHENTICATION;
    }

    /**
     * checks if the passed in principalId is authorized to log in.
     */
    protected boolean isAuthorizedToLogin(String principalId) {
        return getPermissionService().isAuthorized(
            principalId,
            KimConstants.KIM_TYPE_DEFAULT_NAMESPACE,
            KimConstants.PermissionNames.LOG_IN,
            Collections.singletonMap("principalId", principalId));
    }

    public IdentityService getIdentityService() {
        if (identityService == null) {
            identityService = KimApiServiceLocator.getIdentityService();
        }
        return identityService;
    }

    public PermissionService getPermissionService() {
        if (permissionService == null) {
            permissionService = KimApiServiceLocator.getPermissionService();
        }
        return permissionService;
    }
}
