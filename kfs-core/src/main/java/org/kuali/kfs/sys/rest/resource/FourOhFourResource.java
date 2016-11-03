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

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/{ignoreme:.*}")
public class FourOhFourResource {

    @GET
    public Response get404() {
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    public Response post404() {
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    public Response put404() {
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    public Response delete404() {
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @HEAD
    public Response head404() {
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
