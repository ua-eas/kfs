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

import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.krad.util.KRADPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.BusinessObject;

import com.google.common.base.CaseFormat;

@Path("/api")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ApiResource {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ApiResource.class);
    
    protected static volatile KualiModuleService kualiModuleService;

    protected static volatile BusinessObjectService businessObjectService;
    
    @Context
    private HttpServletRequest servletRequest;
    
    @GET
    @Path("/v1/{moduleName}/business-object/{businessObjectName}/{objectId}")
    public Response getSingleObject(@PathParam("moduleName")String moduleName, @PathParam("businessObjectName")String businessObjectName, @PathParam("objectId")String objectId) {
        LOG.debug("processV1Request() started");
        
        Class<BusinessObject> boClass = determineClass(moduleName, businessObjectName);
        if (boClass == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        
        BusinessObject businessObject = findBusinessObject(boClass, objectId);
        if (businessObject == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        
        try {
            JSONObject json = new JSONObject();
            for (PropertyDescriptor propertyDescriptor : PropertyUtils.getPropertyDescriptors(businessObject)) {
                Object jsonValue = getJsonValue(businessObject, propertyDescriptor);
                if (jsonValue != null) {                    
                    json.put(propertyDescriptor.getName(), jsonValue);
                }
            }
        } catch (Exception e) {
            LOG.error("Could not serialize BO", e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        // TODO: Check authorization, create response
        
        return null;        
    }

    private Object getJsonValue(BusinessObject businessObject, PropertyDescriptor propertyDescriptor) throws Exception {
        Object value = PropertyUtils.getSimpleProperty(businessObject, propertyDescriptor.getName());
        if (value == null) {
            return null;
        }
        Class<?> propertyClass = propertyDescriptor.getPropertyType();
        
        if (BusinessObject.class.isAssignableFrom(propertyClass)) {
            // TODO: Level 1 serialization
            return null;
        }
        
        if (Collection.class.isAssignableFrom(propertyClass)) {
            Collection<?> collection = (Collection<?>) value;
            Iterator<?> it = collection.iterator();
            JSONArray newList = new JSONArray();
            while (it.hasNext()) {
                Object item = it.next();
                if (item instanceof BusinessObject) {
                    // TODO: Level 1 serialization
                }
                else {
                    newList.put(item);
                }
                return newList;
            }
        }
        return value;
    }

    protected <T extends BusinessObject> T findBusinessObject(Class<T> boClass, String objectId) {
        Map<String, String> queryCriteria = new HashMap<String, String>();
        queryCriteria.put(KRADPropertyConstants.OBJECT_ID, objectId);
        Collection<T> queryResults = getBusinessObjectService().findMatching(boClass, queryCriteria);
        if (queryResults.size() != 1) {
            return null;
        }
        return queryResults.iterator().next();
    }

    @SuppressWarnings("unchecked")
    protected Class<BusinessObject> determineClass(String moduleName, String businessObjectName) {
        ModuleService moduleService = determineModuleService(moduleName);
        if (moduleService == null) {
            return null;
        }
        String boClassName = convertUrlBoNameToClassName(businessObjectName);
        // Search for class in module.
        for (String prefix : moduleService.getModuleConfiguration().getPackagePrefixes()) {
            String fullClassName = prefix + ".businessobject." + boClassName;
            try {
                return (Class<BusinessObject>) Class.forName(fullClassName).asSubclass(BusinessObject.class);
            } catch (ClassNotFoundException | ClassCastException e) {
                // Keep looking
            }
        }
        // Couldn't find it.
        return null;
    }

    protected ModuleService determineModuleService(String moduleName) {
        for (ModuleService moduleService : getKualiModuleService().getInstalledModuleServices()) {
            String namespaceCode = moduleService.getModuleConfiguration().getNamespaceCode().toUpperCase();
            if (namespaceCode.contains(StringUtils.upperCase(moduleName))) {
                return moduleService;
            }
        }
        return null;
    }

    protected String convertUrlBoNameToClassName(String businessObjectName) {
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

    protected BusinessObjectService getBusinessObjectService() {
        if (businessObjectService == null) {
            businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        }
        return businessObjectService;
    }
    
    public static void setKualiModuleService(KualiModuleService kualiModuleService) {
        ApiResource.kualiModuleService = kualiModuleService;
    }

    public static void setBusinessObjectService(BusinessObjectService businessObjectService) {
        ApiResource.businessObjectService = businessObjectService;
    }   
}
