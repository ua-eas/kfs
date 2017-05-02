/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
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

import org.kuali.kfs.krad.util.KRADUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
