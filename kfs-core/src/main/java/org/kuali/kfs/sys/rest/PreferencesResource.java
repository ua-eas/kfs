package org.kuali.kfs.sys.rest;

import org.kuali.kfs.krad.util.KRADUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.InstitutionPreferencesService;
import org.kuali.kfs.sys.service.UserPreferencesService;
import org.kuali.rice.kim.api.identity.Person;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/preferences")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PreferencesResource {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PreferencesResource.class);

    protected static volatile InstitutionPreferencesService institutionPreferencesService;
    protected static volatile UserPreferencesService userPreferencesService;

    @Context
    private HttpServletRequest servletRequest;

    @GET
    @Path("/institution_links/{principalName}")
    public Response getInstitutionLinks(@HeaderParam("cache-control")String cacheControlHeader,@PathParam("principalName")String principalName) {
        LOG.debug("getInstitutionLinks() started");

        boolean useCache = true;
        if ( cacheControlHeader != null ) {
            CacheControl cacheControl = CacheControl.valueOf(cacheControlHeader);
            useCache = !cacheControl.isMustRevalidate();
        }

        if ( isAuthorized(principalName) ) {
            Map<String, Object> preferences = getInstitutionPreferencesService().findInstitutionPreferencesLinks(getPerson(),useCache);
            return Response.ok(preferences).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized to retrieve preferences for this user").build();
        }
    }

    @GET
    @Path("/institution")
    public Response getInstitutionNoLinks() {
        LOG.debug("getInstitutionNoLinks() started");

        Map<String, Object> preferences = getInstitutionPreferencesService().findInstitutionPreferencesNoLinks();
        return Response.ok(preferences).build();
    }

    @PUT
    @Path("/institution/{institutionId}")
    public Response saveInstitutionPreferences(@PathParam("institutionId") String institutionId, String linkGroups) {
        LOG.debug("saveInstitutionPreferences started");

        if (!getInstitutionPreferencesService().hasConfigurationPermission(getPrincipalName())) {
            return Response.status(Response.Status.FORBIDDEN).entity("User "+getPrincipalName()+" does not have access to InstitutionConfig").build();
        }

        getInstitutionPreferencesService().saveInstitutionPreferences(institutionId, linkGroups);
        return Response.ok(linkGroups).build();
    }

    @GET
    @Path("/config/groups")
    public Response getGroupLinks() {
        LOG.debug("getGroupLinks started");

        if (!getInstitutionPreferencesService().hasConfigurationPermission(getPrincipalName())) {
            return Response.status(Response.Status.FORBIDDEN).entity("User " + getPrincipalName() + " does not have access to InstitutionConfig").build();
        }

        Map<String, Object> linkGroups = getInstitutionPreferencesService().getAllLinkGroups();
        return Response.ok(linkGroups).build();
    }

    @GET
    @Path("/users/{principalName}")
    public Response getUserPreferences(@PathParam("principalName")String principalName) {
        LOG.debug("getUserPreferences() started");

        if ( isAuthorized(principalName) ) {
            Map<String, Object> preferences = getUserPreferencesService().getUserPreferences(principalName);
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
    public Response saveUserPreferences(@PathParam("principalName")String principalName, String preferences) {
        LOG.debug("saveUserPreferences() started");

        if ( isAuthorized(principalName) ) {
            getUserPreferencesService().saveUserPreferences(principalName, preferences);
            return Response.ok(preferences).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized to save preferences for this user").build();
        }
    }

    @PUT
    @Path("/users/{principalName}/{key}")
    public Response saveUserPreferenceKey(@PathParam("principalName")String principalName,@PathParam("key")String key,String preferences) {
        LOG.debug("saveUserPreferenceKey() started");

        if ( isAuthorized(principalName) ) {
            getUserPreferencesService().saveUserPreferencesKey(principalName, key, preferences);
            return Response.ok(preferences).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized to save preferences for this user").build();
        }
    }

    private boolean isAuthorized(String principalName) {
        String loggedinPrincipalName = getPrincipalName();

        return loggedinPrincipalName.equals(principalName);
    }

    protected String getPrincipalName() {
        return getPerson().getPrincipalName();
    }

    protected Person getPerson() {
        return KRADUtils.getUserSessionFromRequest(servletRequest).getPerson();
    }

    protected UserPreferencesService getUserPreferencesService() {
        if (userPreferencesService == null) {
            userPreferencesService = SpringContext.getBean(UserPreferencesService.class);
        }
        return userPreferencesService;
    }

    protected InstitutionPreferencesService getInstitutionPreferencesService() {
        if (institutionPreferencesService == null) {
            institutionPreferencesService = SpringContext.getBean(InstitutionPreferencesService.class);
        }
        return institutionPreferencesService;
    }
}