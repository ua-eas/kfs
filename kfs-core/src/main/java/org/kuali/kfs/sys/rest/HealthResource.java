package org.kuali.kfs.sys.rest;

import org.kuali.kfs.sys.businessobject.HealthReport;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/check")
public class HealthResource {


    @GET
    public Response health(@DefaultValue("false") @QueryParam("detail") boolean hasDetail) {
        HealthReport hr = new HealthReport().checkHealth();
        if (hr.getStatus().equals("OK") && hasDetail) {
            return Response.ok(hr).build();
        } else if (hr.getStatus().equals("OK")) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }

        return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(hr).build();
    }
}

