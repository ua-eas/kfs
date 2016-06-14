package org.kuali.kfs.sys.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.sys.context.SpringContext;

import com.google.common.base.CaseFormat;

@Path("/api")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ApiResource {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ApiResource.class);
    
    protected static volatile KualiModuleService kualiModuleService;
    
    @Context
    private HttpServletRequest servletRequest;
    
    @GET
    @Path("/v1/{moduleName}/business-object/{businessObjectName}/{objectId}")
    public Response getSingleObject(@PathParam("moduleName")String moduleName, @PathParam("businessObjectName")String businessObjectName, @PathParam("objectId")String objectId) {
        LOG.debug("processV1Request() started");
        
        Class boClass = determineClass(moduleName, businessObjectName);
        if (boClass == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        
        // TODO: Check authorization, find object, create response
        
        return null;        
    }

    private Class determineClass(String moduleName, String businessObjectName) {
        ModuleService moduleService = determineModuleService(moduleName);
        if (moduleService == null) {
            return null;
        }
        String boClassName = convertUrlBoNameToClassName(businessObjectName);
        // Search for class in module.
        for (String prefix : moduleService.getModuleConfiguration().getPackagePrefixes()) {
            try {
                return Class.forName(prefix + ".businessobject." + boClassName);
            } catch (ClassNotFoundException e) {
                // Keep looking
            }
        }
        // Couldn't find it.
        return null;
    }

    private ModuleService determineModuleService(String moduleName) {
        String namespaceCode = "KFS-" + StringUtils.upperCase(moduleName);
        return getKualiModuleService().getModuleServiceByNamespaceCode(namespaceCode);
    }

    private String convertUrlBoNameToClassName(String businessObjectName) {
        // TODO: create option to map BO names that don't pluralize with a single "s".
        String camelCaseName = CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, businessObjectName);
        // Remove plural "s" from end of name.
        return StringUtils.chop(camelCaseName);
    }
    
    protected KualiModuleService getKualiModuleService() {
        if (kualiModuleService == null) {
            kualiModuleService = SpringContext.getBean(KualiModuleService.class);
        }
        return kualiModuleService;
    }
}
