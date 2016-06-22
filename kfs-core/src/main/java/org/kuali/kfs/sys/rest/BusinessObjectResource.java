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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
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
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.KRADPropertyConstants;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.bo.BusinessObject;

import com.google.common.base.CaseFormat;

import javassist.Modifier;

@Path("{moduleName}/{businessObjectName}")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BusinessObjectResource {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BusinessObjectResource.class);
    
    protected static volatile KualiModuleService kualiModuleService;
    protected static volatile BusinessObjectService businessObjectService;
    protected static volatile ConfigurationService configurationService;
    
    @Context
    private HttpServletRequest servletRequest;
    
    @GET
    @Path("/{objectId}")
    public Response getSingleObject(@PathParam("moduleName")String moduleName, @PathParam("businessObjectName")String businessObjectName, @PathParam("objectId")String objectId) {
        LOG.debug("processV1Request() started");
        
        Class<PersistableBusinessObject> boClass = determineClass(moduleName, businessObjectName);
        if (boClass == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        
        PersistableBusinessObject businessObject = findBusinessObject(boClass, objectId);
        if (businessObject == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        
        ObjectUtils.materializeSubObjectsToDepth(businessObject, 3);
        
        Map<String, Object> jsonObject = new LinkedHashMap<String, Object>();
        try {           
            for (PropertyDescriptor propertyDescriptor : PropertyUtils.getPropertyDescriptors(businessObject)) {
                Method readMethod = propertyDescriptor.getReadMethod();
                if (readMethod != null && readMethod.getParameterCount() == 0 && Modifier.isPublic(readMethod.getModifiers())) {
                    Object jsonValue = getJsonValue(businessObject, propertyDescriptor);
                   
                    if (jsonValue != null) {                    
                        jsonObject.put(propertyDescriptor.getName(), jsonValue);
                    }
                }
            }
        } catch (ReflectiveOperationException e) {
            LOG.error("Could not serialize BO", e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        // TODO: Check authorization, create response
        
        return Response.ok(jsonObject).build();        
    }

    private Object getJsonValue(PersistableBusinessObject businessObject, PropertyDescriptor propertyDescriptor) throws ReflectiveOperationException  {
        Object value = PropertyUtils.getSimpleProperty(businessObject, propertyDescriptor.getName());
        if (value == null) {
            return null;
        }
        Class<?> propertyClass = propertyDescriptor.getPropertyType();
        
        if (BusinessObject.class.isAssignableFrom(propertyClass)) {
            return convertBoToUrl((BusinessObject)value);
        }
        
        if (Collection.class.isAssignableFrom(propertyClass)) {
            Collection<?> collection = (Collection<?>) value;
            Iterator<?> it = collection.iterator();
            List<Object> newList = new ArrayList<Object>();
            while (it.hasNext()) {
                Object item = it.next();
                if (item instanceof BusinessObject) {
                    Map<String, Object> url = convertBoToUrl((BusinessObject)item);
                    if (url != null) {
                        newList.add(url);
                    }
                }
                else {
                    newList.add(item);
                }                
            }
            return newList;
        }
        
        if (Date.class.isAssignableFrom(propertyClass)) {
            return ((Date)value).getTime();
        }
        
        return value;
    }

    protected <T extends PersistableBusinessObject> T findBusinessObject(Class<T> boClass, String objectId) {
        Map<String, String> queryCriteria = new HashMap<String, String>();
        queryCriteria.put(KRADPropertyConstants.OBJECT_ID, objectId);
        Collection<T> queryResults = getBusinessObjectService().findMatching(boClass, queryCriteria);
        if (queryResults.size() != 1) {
            return null;
        }
        return queryResults.iterator().next();
    }

    @SuppressWarnings("unchecked")
    protected Class<PersistableBusinessObject> determineClass(String moduleName, String businessObjectName) {
        ModuleService moduleService = determineModuleService(moduleName);
        if (moduleService == null) {
            return null;
        }
        String boClassName = convertUrlBoNameToClassName(businessObjectName);
        // Search for class in module.
        for (String prefix : moduleService.getModuleConfiguration().getPackagePrefixes()) {
            String fullClassName = prefix + ".businessobject." + boClassName;
            try {
                return (Class<PersistableBusinessObject>) Class.forName(fullClassName).asSubclass(PersistableBusinessObject.class);
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
        String camelCaseName = CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, businessObjectName);
        // Remove plural "s" from end of name.
        return StringUtils.chop(camelCaseName);
    }
    
    protected String convertClassNameToUrlBoName(String className) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, className) + "s";
    }
    
    protected Map<String, Object> convertBoToUrl(BusinessObject businessObject) {
        if (!(businessObject instanceof PersistableBusinessObject)) {
            return null;
        }
        
        PersistableBusinessObject persistableBo = (PersistableBusinessObject) businessObject;
        String objectID = persistableBo.getObjectId();
        if (objectID == null) {
            return null;
        }
        String moduleName = getModuleName(persistableBo);
        if (moduleName == null) {
            return null;
        }
        
        Map<String, Object> result = new LinkedHashMap<>();        
        
        String url = getBaseUrl() 
                + "/api/v1/" 
                + "business-object/"
                + moduleName 
                + "/"
                + convertClassNameToUrlBoName(persistableBo.getClass().getSimpleName())
                + "/"
                + persistableBo.getObjectId();
        result.put(KFSPropertyConstants.LINK, url);
        
        return result;
    } 

    private String getBaseUrl() {
        return getConfigurationService().getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY);
    }
    
    private String getModuleName(PersistableBusinessObject businessObject) {
        ModuleService moduleService = kualiModuleService.getResponsibleModuleService(businessObject.getClass());
        if (moduleService == null) {
            return null;
        }
        String moduleServiceName = moduleService.getModuleConfiguration().getNamespaceCode().toLowerCase();
        if (moduleServiceName.contains("-")) {
            moduleServiceName = StringUtils.substringAfter(moduleServiceName, "-");
        }
        return moduleServiceName;
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
    
    protected ConfigurationService getConfigurationService() {
        if (configurationService == null) {
            configurationService = SpringContext.getBean(ConfigurationService.class);
        }
        return configurationService;
    }
    
    public static void setKualiModuleService(KualiModuleService kualiModuleService) {
        BusinessObjectResource.kualiModuleService = kualiModuleService;
    }

    public static void setBusinessObjectService(BusinessObjectService businessObjectService) {
        BusinessObjectResource.businessObjectService = businessObjectService;
    }
    
    public static void setConfigurationService(ConfigurationService configurationService) {
        BusinessObjectResource.configurationService = configurationService;
    }   
}
