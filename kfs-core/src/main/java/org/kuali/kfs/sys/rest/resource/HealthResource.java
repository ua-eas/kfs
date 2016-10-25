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
package org.kuali.kfs.sys.rest.resource;

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

