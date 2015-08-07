package org.kuali.kfs.sys.rest;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.web.WebUtilities;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.permission.Permission;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.util.KRADConstants;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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

    @POST
    @Path("/login")
    public Response login(@Context HttpServletRequest request, JsonNode body) {
        String backdoorId = "";
        if (body.has("backdoorId")) {
            backdoorId = body.get("backdoorId").asText();
        }
        if (StringUtils.isBlank(backdoorId)) {
            return Response.status(HttpStatus.BAD_REQUEST.value()).entity("{\"message\":\"BackdoorId was empty\"}").build();
        }
        UserSession uSession = WebUtilities.retrieveUserSession(request);

        if (uSession == null) {
            return Response.status(HttpStatus.BAD_REQUEST.value()).entity("{\"message\":\"Session was empty\"}").build();
        }

        uSession.clearObjectMap();

        if (!isBackdoorAuthorized(uSession)) {
            return Response.status(HttpStatus.UNAUTHORIZED.value()).entity("{\"message\":\"User not permitted to use backdoor functionality\"}").build();
        }

        try {
            uSession.setBackdoorUser(backdoorId);
        } catch (RiceRuntimeException e) {
            LOG.warn("invalid backdoor id " + backdoorId, e);
            return Response.status(HttpStatus.BAD_REQUEST.value()).entity("{\"message\":\"Invalid backdoorId\"}").build();
        }

        return Response.ok("{\"backdoorId\": \"" + uSession.getPrincipalName() + "\"}").build();
    }

    @GET
    @Path("/logout")
    public Response logout(@Context HttpServletRequest request) {
        UserSession uSession = WebUtilities.retrieveUserSession(request);

        if (uSession == null) {
            return Response.status(HttpStatus.BAD_REQUEST.value()).entity("{\"message\":\"Session was empty\"}").build();
        }
        uSession.clearBackdoorUser();
        return Response.ok("{\"message\":\"Successfully logged out\"}").build();
    }

    @GET
    @Path("/id")
    public Response findBackdoorId(@Context HttpServletRequest request) {
        UserSession uSession = WebUtilities.retrieveUserSession(request);
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
}
