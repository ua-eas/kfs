package org.kuali.kfs.sys.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.PreferencesService;
import org.kuali.kfs.sys.web.WebUtilities;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.UserSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Path("/preferences")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PreferencesResource {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PreferencesResource.class);

    protected static volatile PreferencesService preferencesService;

    @Context
    private HttpServletRequest servletRequest;

    @GET
    @Path("/institution")
    public Response getGroupedJobs() {
        LOG.debug("getGroupedJobs() started");

        Map<String, Object> preferences = getPreferencesService().findInstitutionPreferences();
        return Response.ok(preferences).build();
    }

    @GET
    @Path("/users/{principalName}")
    public Response getUserPreferences(@PathParam("principalName")String principalName) {
        LOG.debug("getUserPreferences() started");

        String loggedinPrincipalName = getPrincipalName();

        if ( loggedinPrincipalName.equals(principalName) ) {
            Map<String, Object> preferences = getPreferencesService().getUserPreferences(principalName);
            if (preferences == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("User Preference Not Found").build();
            }
            return Response.ok(preferences).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized to retrieve preferences for this user").build();
        }
    }

    @PUT
    @Path("/users/{principalName}")
    public Response saveUserPreferences(@PathParam("principalName")String principalName,String preferences) {
        LOG.debug("saveUserPreferences() started");

        String loggedinPrincipalName = getPrincipalName();

        if ( loggedinPrincipalName.equals(principalName) ) {
            getPreferencesService().saveUserPreferences(loggedinPrincipalName,preferences);
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized to save preferences for this user").build();
        }
    }

    @PUT
    @Path("/users/{principalName}/{key}")
    public Response saveUserPreferenceKey(@PathParam("principalName")String principalName,@PathParam("key")String key,String preferences) {
        LOG.debug("saveUserPreferenceKey() started");

        String loggedinPrincipalName = getPrincipalName();

        LOG.fatal("Prefs: " + preferences);

        if ( loggedinPrincipalName.equals(principalName) ) {
            getPreferencesService().saveUserPreferencesKey(loggedinPrincipalName, key, preferences);
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized to save preferences for this user").build();
        }
    }

    private String getPrincipalName() {
        return WebUtilities.retrieveUserSession(servletRequest).getPrincipalName();
    }

    protected PreferencesService getPreferencesService() {
        if (preferencesService == null) {
            preferencesService = SpringContext.getBean(PreferencesService.class);
        }
        return preferencesService;
    }

}
