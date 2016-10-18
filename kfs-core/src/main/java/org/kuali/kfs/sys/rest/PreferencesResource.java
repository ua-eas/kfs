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
package org.kuali.kfs.sys.rest;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import org.kuali.kfs.krad.util.KRADUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.InstitutionPreferencesService;
import org.kuali.kfs.sys.service.UserPreferencesService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kim.api.identity.Person;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Path("/preferences")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PreferencesResource {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PreferencesResource.class);

    protected static volatile InstitutionPreferencesService institutionPreferencesService;
    protected static volatile UserPreferencesService userPreferencesService;

    @Context
    private HttpServletRequest servletRequest;

    @Context
    private ServletContext servletContext;

    @GET
    @Path("/institution-links/{principalName}")
    public Response getInstitutionLinks(@HeaderParam("cache-control") String cacheControlHeader, @PathParam("principalName") String principalName) {
        LOG.debug("getInstitutionLinks() started");

        boolean useCache = true;
        if (cacheControlHeader != null) {
            CacheControl cacheControl = CacheControl.valueOf(cacheControlHeader);
            useCache = !cacheControl.isMustRevalidate();
        }

        if (isAuthorized(principalName)) {
            Map<String, Object> preferences = getInstitutionPreferencesService().findInstitutionPreferencesLinks(getPerson(), useCache);
            return Response.ok(preferences).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized to retrieve preferences for this user").build();
        }
    }

    @GET
    @Path("/institution")
    public Response getInstitutionNoLinks() {
        LOG.debug("getInstitutionNoLinks() started");

        Map<String, Object> preferences = getInstitutionPreferencesService().findInstitutionPreferencesNoLinks(getRiceVersion());
        return Response.ok(preferences).build();
    }

    protected Optional<String> getRiceVersion() {
        return Optional.of(SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString("rice.version"));
    }

    @PUT
    @Path("/institution/{institutionId}")
    public Response saveInstitutionPreferences(@PathParam("institutionId") String institutionId, String linkGroups) {
        LOG.debug("saveInstitutionPreferences started");

        if (!getInstitutionPreferencesService().hasConfigurationPermission(getPrincipalName())) {
            return Response.status(Response.Status.FORBIDDEN).entity("User " + getPrincipalName() + " does not have access to InstitutionConfig").build();
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
    @Path("/config/menu")
    public Response getMenu() {
        LOG.debug("getMenu started");

        if (!getInstitutionPreferencesService().hasConfigurationPermission(getPrincipalName())) {
            return Response.status(Response.Status.FORBIDDEN).entity("User " + getPrincipalName() + " does not have access to InstitutionConfig").build();
        }

        List<Map<String, String>> menu = getInstitutionPreferencesService().getMenu();
        return Response.ok(menu).build();
    }

    @PUT
    @Path("/config/menu")
    public Response saveMenu(String menu) {
        LOG.debug("saveMenu started");

        if (!getInstitutionPreferencesService().hasConfigurationPermission(getPrincipalName())) {
            return Response.status(Response.Status.FORBIDDEN).entity("User " + getPrincipalName() + " does not have access to InstitutionConfig").build();
        }

        List<Map<String, String>> menuResponse = getInstitutionPreferencesService().saveMenu(menu);
        return Response.ok(menuResponse).build();
    }

    @GET
    @Path("/config/logo")
    public Response getLogo() {
        LOG.debug("getLogo started");

        if (!getInstitutionPreferencesService().hasConfigurationPermission(getPrincipalName())) {
            return Response.status(Response.Status.FORBIDDEN).entity("User " + getPrincipalName() + " does not have access to InstitutionConfig").build();
        }

        Map<String, String> logo = getInstitutionPreferencesService().getLogo();
        return Response.ok(logo).build();
    }

    @POST
    @Path("/config/logo")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadLogo(@FormDataParam("logo") InputStream fileInputStream,
                               @FormDataParam("logo") FormDataContentDisposition contentDispositionHeader) {
        LOG.debug("uploadLogo started");

        if (!getInstitutionPreferencesService().hasConfigurationPermission(getPrincipalName())) {
            return Response.status(Response.Status.FORBIDDEN).entity("User " + getPrincipalName() + " does not have access to InstitutionConfig").build();
        }

        Map<String, String> logoResponse;
        try {
            String filename = contentDispositionHeader.getFileName();
            logoResponse = getInstitutionPreferencesService().uploadLogo(fileInputStream, filename);
        } catch (RuntimeException re) {
            return Response.status(Response.Status.BAD_REQUEST).entity(re.getMessage()).build();
        }
        return Response.ok(logoResponse).build();
    }

    @PUT
    @Path("/config/logo")
    public Response saveLogo(String logo) {
        LOG.debug("saveLogo started");

        if (!getInstitutionPreferencesService().hasConfigurationPermission(getPrincipalName())) {
            return Response.status(Response.Status.FORBIDDEN).entity("User " + getPrincipalName() + " does not have access to InstitutionConfig").build();
        }

        Map<String, String> logoResponse = getInstitutionPreferencesService().saveLogo(logo);
        return Response.ok(logoResponse).build();
    }

    @GET
    @Path("/users/{principalName}")
    public Response getUserPreferences(@PathParam("principalName") String principalName) {
        LOG.debug("getUserPreferences() started");

        if (isAuthorized(principalName)) {
            Map<String, Object> preferences = getUserPreferencesService().getUserPreferences(principalName);

            if (preferences == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("User Preference Not Found").build();
            }
            preferences.put("defaultFinancialsChartOfAccountsCode","");
            preferences.put("defaultOrganizationCode","");
            preferences.put("defaultAccounts",new ArrayList<ChartAccount>());

            return Response.ok(preferences).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized to retrieve preferences for this user").build();
        }
    }

    @PUT
    @Path("/users/{principalName}")
    public Response saveUserPreferences(@PathParam("principalName") String principalName, String preferences) {
        LOG.debug("saveUserPreferences() started");

        if (isAuthorized(principalName)) {
            getUserPreferencesService().saveUserPreferences(principalName, preferences);
            return Response.ok(preferences).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized to save preferences for this user").build();
        }
    }

    @PUT
    @Path("/users/{principalName}/{key}")
    public Response saveUserPreferenceKey(@PathParam("principalName") String principalName, @PathParam("key") String key, String preferences) {
        LOG.debug("saveUserPreferenceKey() started");

        if (isAuthorized(principalName)) {
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

    class ChartAccount {
        public String financialChartOfAccountsCode;
        public String accountNumber;
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
