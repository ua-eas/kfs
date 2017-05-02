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
package org.kuali.kfs.sys.rest.resource;

import com.fasterxml.jackson.databind.JsonNode;
import org.kuali.kfs.kns.bo.AuthenticationValidationResponse;
import org.kuali.kfs.kns.service.CfAuthenticationService;
import org.kuali.kfs.kns.service.KNSServiceLocator;
import org.kuali.kfs.krad.UserSession;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.KRADUtils;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.permission.Permission;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/backdoor")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BackdoorResource {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BackdoorResource.class);

    private CfAuthenticationService cfAuthenticationService;

    @POST
    @Path("/login")
    public Response login(@Context HttpServletRequest request, JsonNode body) {
        LOG.debug("login() started");

        if (ConfigContext.getCurrentContextConfig().isProductionEnvironment()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String backdoorId = "";
        if (body.has("backdoorId")) {
            backdoorId = body.get("backdoorId").asText();
        }

        UserSession uSession = KRADUtils.getUserSessionFromRequest(request);

        AuthenticationValidationResponse response = getCfAuthenticationService().validatePrincipalName(backdoorId);
        switch (response) {
            case INVALID_PRINCIPAL_NAME_BLANK:
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"message\":\"BackdoorId was empty\"}").build();
            case INVALID_PRINCIPAL_DOES_NOT_EXIST:
                LOG.debug("login() Principal does not exist");
                return logout(request);
            case INVALID_PRINCIPAL_CANNOT_LOGIN:
                LOG.debug("login() Principal does not have permissions to back door login");
                return logout(request);
        }

        if (!isBackdoorAuthorized(uSession)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"message\":\"User not permitted to use backdoor functionality\"}").build();
        }

        uSession.clearObjectMap();

        try {
            uSession.setBackdoorUser(backdoorId);
        } catch (RiceRuntimeException e) {
            LOG.warn("invalid backdoor id " + backdoorId, e);
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"message\":\"Invalid backdoorId\"}").build();
        }

        return Response.ok("{\"backdoorId\": \"" + uSession.getPrincipalName() + "\"}").build();
    }

    @GET
    @Path("/logout")
    public Response logout(@Context HttpServletRequest request) {
        LOG.debug("logout() started");

        UserSession uSession = KRADUtils.getUserSessionFromRequest(request);

        if (uSession == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"message\":\"Session was empty\"}").build();
        }
        uSession.clearBackdoorUser();
        return Response.ok("{\"message\":\"Successfully logged out\"}").build();
    }

    @GET
    @Path("/id")
    public Response findBackdoorId(@Context HttpServletRequest request) {
        LOG.debug("findBackdoorId() started");

        UserSession uSession = KRADUtils.getUserSessionFromRequest(request);
        String backdoorId = "";
        if (uSession != null && uSession.isBackdoorInUse()) {
            backdoorId = uSession.getPrincipalName();
        }
        return Response.ok("{\"backdoorId\": \"" + backdoorId + "\"}").build();
    }

    public boolean isBackdoorAuthorized(UserSession uSession) {
        boolean isAuthorized = true;

        //we should check to see if a kim permission exists for the requested application first
        Map<String, String> permissionDetails = new HashMap<>();
        String requestAppCode = ConfigContext.getCurrentContextConfig().getProperty("app.code");
        permissionDetails.put(KimConstants.AttributeConstants.APP_CODE, requestAppCode);
        List<Permission> perms = KimApiServiceLocator.getPermissionService().findPermissionsByTemplate(
            KRADConstants.KUALI_RICE_SYSTEM_NAMESPACE, KimConstants.PermissionTemplateNames.BACKDOOR_RESTRICTION);
        for (Permission kpi : perms) {
            if (kpi.getAttributes().values().contains(requestAppCode)) {
                //if a permission exists, is the user granted permission to use backdoor?
                isAuthorized = KimApiServiceLocator.getPermissionService().isAuthorizedByTemplate(
                    uSession.getActualPerson().getPrincipalId(), KRADConstants.KUALI_RICE_SYSTEM_NAMESPACE,
                    KimConstants.PermissionTemplateNames.BACKDOOR_RESTRICTION, permissionDetails,
                    Collections.<String, String>emptyMap());
            }
        }
        if (!isAuthorized) {
            LOG.warn("Attempt to backdoor was made by user: "
                + uSession.getPerson().getPrincipalId()
                + " into application with app code: "
                + requestAppCode
                + " but they do not have appropriate permissions. Backdoor processing aborted.");
        }
        return isAuthorized;
    }

    private CfAuthenticationService getCfAuthenticationService() {
        if (cfAuthenticationService == null) {
            cfAuthenticationService = KNSServiceLocator.getCfAuthenticationService();
        }
        return cfAuthenticationService;
    }
}
