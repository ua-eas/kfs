package org.kuali.kfs.sys.rest;

import org.kuali.kfs.krad.util.KRADUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Path("/authorization")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthorizationResource {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AuthorizationResource.class);

    @Context
    private HttpServletRequest servletRequest;

    @GET
    @Path("/reports/{reportCode}")
    public Response userCanViewReport(@PathParam("reportCode") String reportCode) {
        LOG.debug("userCanViewReport() started");

        Person currentUser = KRADUtils.getUserSessionFromRequest(servletRequest).getPerson();
        Map<String, String> details = new HashMap<>();
        details.put(KFSConstants.REPORT_CODE, reportCode);
        boolean isAuthorized = KimApiServiceLocator.getPermissionService().isAuthorizedByTemplate(currentUser.getPrincipalId(),
                KFSConstants.PermissionTemplate.VIEW_REPORT.namespace, KFSConstants.PermissionTemplate.VIEW_REPORT.name,
                details, Collections.<String, String>emptyMap());

        Map<String, Object> responseEntity = new HashMap<>();
        responseEntity.put(KFSConstants.IS_AUTHORIZED, isAuthorized);
        responseEntity.put(KFSPropertyConstants.PERSON_USER_ID, currentUser.getPrincipalName());
        responseEntity.put(KFSConstants.REPORT_CODE, reportCode);
        return Response.ok(responseEntity).build();
    }

}
