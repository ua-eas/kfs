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
package org.kuali.kfs.gl.rest.resource;

import org.kuali.kfs.gl.service.CollectorApiService;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.permission.PermissionService;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Produces(MediaType.APPLICATION_JSON)
@Path("collector")
public class CollectorResource {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CollectorResource.class);

    private CollectorApiService collectorApiService;
    private PermissionService permissionService;

    @POST
    @Consumes({MediaType.TEXT_PLAIN,MediaType.TEXT_XML})
    public Response postCollectorFile(@HeaderParam("content-type") String contentType, InputStream inputStream) {
        LOG.debug("postCollectorFile() started");

        if ( MediaType.TEXT_PLAIN.equals(contentType) ) {
            if ( ! isAuthorizedForFlatFileUpload() ) {
                return returnError(Response.Status.UNAUTHORIZED,Collections.singletonList("Unauthorized"));
            }
        } else if ( MediaType.TEXT_XML.equals(contentType) ) {
            if ( ! isAuthorizedForXmlFileUpload() ) {
                return returnError(Response.Status.UNAUTHORIZED,Collections.singletonList("Unauthorized"));
            }
        } else {
            return returnError(Response.Status.BAD_REQUEST,Collections.singletonList("Invalid content type"));
        }

        List<String> errors = getCollectorApiService().collectorApiLoad(inputStream,contentType);
        if ( errors.isEmpty() ) {
            return Response.ok().build();
        } else {
            return returnError(Response.Status.BAD_REQUEST,errors);
        }
    }

    private boolean isAuthorizedForFlatFileUpload() {
        return isAuthorized("collectorFlatFileInputFileType");
    }

    private boolean isAuthorizedForXmlFileUpload() {
        return isAuthorized("collectorXmlInputFileType");
    }

    private boolean isAuthorized(String beanName) {
        Map<String,String> params = new HashMap<>();
        params.put("beanName",beanName);
        params.put("namespaceCode",KFSConstants.CoreModuleNamespaces.GL);
        return getPermissionService().hasPermissionByTemplate(getPrincipalId(), KFSConstants.CoreModuleNamespaces.KNS, "Upload Batch Input File(s)",params);
    }

    private Response returnError(Response.Status errorStatus, List<String> errors) {
        Map<String,List<String>> errorResponse = new HashMap<>();
        errorResponse.put("errors",errors);
        return Response.status(errorStatus).entity(errorResponse).build();
    }

    protected String getPrincipalId() {
        return GlobalVariables.getUserSession().getPerson().getPrincipalId();
    }

    protected PermissionService getPermissionService() {
        if ( permissionService == null ) {
            permissionService = SpringContext.getBean(PermissionService.class);
        }
        return permissionService;
    }

    protected CollectorApiService getCollectorApiService() {
        if ( collectorApiService == null ) {
            collectorApiService = SpringContext.getBean(CollectorApiService.class);
        }
        return collectorApiService;
    }
}
