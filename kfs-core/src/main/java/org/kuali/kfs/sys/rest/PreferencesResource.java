package org.kuali.kfs.sys.rest;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.PreferencesService;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/preferences")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PreferencesResource {

    protected static volatile PreferencesService preferencesService;

    @GET
    @Path("/institution")
    public Response getGroupedJobs() {
        Map<String, Object> preferences = getPreferencesService().findInstitutionPreferences();
        return Response.ok(preferences).build();
    }

    protected PreferencesService getPreferencesService() {
        if (preferencesService == null) {
            preferencesService = SpringContext.getBean(PreferencesService.class);
        }
        return preferencesService;
    }

}
