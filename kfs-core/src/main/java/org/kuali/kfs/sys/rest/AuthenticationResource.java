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

import org.kuali.kfs.krad.util.KRADUtils;
import org.kuali.rice.kim.api.identity.Person;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/authentication")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthenticationResource {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AuthenticationResource.class);

    @Context
    private HttpServletRequest servletRequest;

    /**
     * This returns the principal name of the user that is active.
     * It will return the logged in user or the back door user if back door is active.
     *
     * @return
     */
    @GET
    @Path("/id")
    public Response getActivePrincipalName() {
        LOG.debug("getActivePrincipalName() started");

        Person activePerson = KRADUtils.getUserSessionFromRequest(servletRequest).getPerson();
        return Response.ok("{ \"principalName\": \"" + activePerson.getPrincipalName() + "\" }").build();
    }

    @GET
    @Path("/logged-in-user")
    public Response getLoggedInUser() {
        LOG.debug("getLoggedInUser() started");

        Person loggedinPerson = KRADUtils.getUserSessionFromRequest(servletRequest).getActualPerson();
        return Response.ok(new PartialPerson(loggedinPerson)).build();
    }

    /**
     * This is used to limit the fields that get returned to the client.
     * Some other information in the Person object may be sensitive.
     */
    class PartialPerson {
        private Person p;

        public PartialPerson(Person p) {
            this.p = p;
        }

        public String getPrincipalId() {
            return p.getPrincipalId();
        }

        public String getPrincipalName() {
            return p.getPrincipalName();
        }

        public String getFirstName() {
            return p.getFirstName();
        }

        public String getMiddleName() {
            return p.getMiddleName();
        }

        public String getLastName() {
            return p.getLastName();
        }

        public String getName() {
            return p.getName();
        }

        public String getEmailAddress() {
            return p.getEmailAddress();
        }
    }
}
