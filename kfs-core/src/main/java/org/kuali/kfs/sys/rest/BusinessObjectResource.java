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

import com.google.common.base.CaseFormat;
import javassist.Modifier;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.kuali.kfs.kns.service.BusinessObjectAuthorizationService;
import org.kuali.kfs.kns.lookup.LookupUtils;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.datadictionary.AttributeDefinition;
import org.kuali.kfs.krad.datadictionary.BusinessObjectEntry;
import org.kuali.kfs.krad.datadictionary.DataDictionary;
import org.kuali.kfs.krad.datadictionary.mask.MaskFormatter;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.DataDictionaryService;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.KRADPropertyConstants;
import org.kuali.kfs.krad.util.KRADUtils;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.sec.SecConstants;
import org.kuali.kfs.sec.service.AccessSecurityService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.permission.PermissionService;
import org.kuali.rice.krad.bo.BusinessObject;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("{moduleName}/{businessObjectName}")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BusinessObjectResource {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BusinessObjectResource.class);

    protected static volatile KualiModuleService kualiModuleService;
    protected static volatile BusinessObjectService businessObjectService;
    protected static volatile BusinessObjectAuthorizationService businessObjectAuthorizationService;
    protected static volatile ConfigurationService configurationService;
    protected static volatile PermissionService permissionService;
    protected static volatile AccessSecurityService accessSecurityService;
    protected static volatile DataDictionaryService dataDictionaryService;

    @Context
    protected HttpServletRequest servletRequest;

    @GET
    public Response searchObjects(@PathParam("moduleName") String moduleName,
                                  @PathParam("businessObjectName") String businessObjectName,
                                  @QueryParam("skip") Integer skip,
                                  @QueryParam("limit") Integer limit,
                                  @QueryParam("sort") String sort,
                                  @Context UriInfo uriInfo) {
        LOG.debug("searchObjects() started");

        Class<PersistableBusinessObject> boClass = determineClass(moduleName, businessObjectName);
        if (boClass == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        if (!isAuthorized(KimConstants.PermissionTemplateNames.INQUIRE_INTO_RECORDS, boClass)) {
            return Response.status(Status.FORBIDDEN).build();
        }

        Map<String, Object> results;
        try {
            results = searchBusinessObjects(boClass, uriInfo);
        } catch (ReflectiveOperationException e) {
            LOG.error("Could not serialize BO", e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.ok(results).build();
    }

    @GET
    @Path("/{objectId}")
    public Response getSingleObject(@PathParam("moduleName") String moduleName, @PathParam("businessObjectName") String businessObjectName, @PathParam("objectId") String objectId) {
        LOG.debug("getSingleObject() started");

        Class<PersistableBusinessObject> boClass = determineClass(moduleName, businessObjectName);
        if (boClass == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        if (!isAuthorized(KimConstants.PermissionTemplateNames.INQUIRE_INTO_RECORDS, boClass)) {
            return Response.status(Status.FORBIDDEN).build();
        }

        PersistableBusinessObject businessObject = findBusinessObject(boClass, objectId);
        if (businessObject == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        if (!isAuthorizedByAccessSecurity(businessObject)) {
            return Response.status(Status.FORBIDDEN).build();
        }

        Map<String, Object> jsonObject;
        try {
            jsonObject = businessObjectToJson(boClass, businessObject);
        } catch (ReflectiveOperationException e) {
            LOG.error("Could not serialize BO", e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        // TODO: Check authorization

        return Response.ok(jsonObject).build();
    }

    protected MaskFormatter buildMaskFormatter(AttributeDefinition attributeDefinition) {
        return (attributeDefinition.getAttributeSecurity().isMask())
                ? attributeDefinition.getAttributeSecurity().getMaskFormatter()
                : attributeDefinition.getAttributeSecurity().getPartialMaskFormatter();
    }

    protected Object maskJsonValueIfNecessary(String businessObjectName, String attributeName, Object jsonValue) {
        final AttributeDefinition attributeDefinition = getDataDictionaryService().getAttributeDefinition(businessObjectName, attributeName);
        if (attributeDefinition == null || attributeDefinition.getAttributeSecurity() == null || (!attributeDefinition.getAttributeSecurity().isMask() && !attributeDefinition.getAttributeSecurity().isPartialMask())) {
            return jsonValue;
        }
        final MaskFormatter maskFormatter = buildMaskFormatter(attributeDefinition);
        if (getBusinessObjectAuthorizationService().isNonProductionEnvAndUnmaskingTurnedOff()) {
            return maskFormatter.maskValue(jsonValue); // it's non-production and unmasking is turned off, so let's always mask
        } else {
            final Class<? extends BusinessObject> businessObjectClass = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(businessObjectName).getBusinessObjectClass();
            return maskJsonValue(jsonValue, businessObjectClass, attributeDefinition, maskFormatter);
        }
    }

    protected boolean shouldMask(Class<? extends BusinessObject> businessObjectClass, AttributeDefinition attributeDefinition) {
        return (attributeDefinition.getAttributeSecurity().isMask()
                    && !getBusinessObjectAuthorizationService().canFullyUnmaskField(getPerson(), businessObjectClass, attributeDefinition.getName(), null))
                ||
                (attributeDefinition.getAttributeSecurity().isPartialMask()
                        && !getBusinessObjectAuthorizationService().canPartiallyUnmaskField(getPerson(), businessObjectClass, attributeDefinition.getName(), null));
    }

    protected Object maskJsonValue(Object jsonValue, Class<? extends BusinessObject> businessObjectClass, AttributeDefinition attributeDefinition, MaskFormatter maskFormatter) {
        return (shouldMask(businessObjectClass, attributeDefinition))
            ? maskFormatter.maskValue(jsonValue)
            : jsonValue;
    }

    protected Object getJsonValue(PersistableBusinessObject businessObject, PropertyDescriptor propertyDescriptor) throws ReflectiveOperationException {
        Object value = PropertyUtils.getSimpleProperty(businessObject, propertyDescriptor.getName());
        if (ObjectUtils.isNull(value)) {
            return null;
        }
        Class<?> propertyClass = propertyDescriptor.getPropertyType();

        if (BusinessObject.class.isAssignableFrom(propertyClass)) {
            return convertBoToUrl((BusinessObject) value);
        }

        if (Collection.class.isAssignableFrom(propertyClass)) {
            Collection<?> collection = (Collection<?>) value;
            Iterator<?> it = collection.iterator();
            List<Object> newList = new ArrayList<Object>();
            while (it.hasNext()) {
                Object item = it.next();
                if (item instanceof BusinessObject) {
                    Map<String, Object> url = convertBoToUrl((BusinessObject) item);
                    if (url != null) {
                        newList.add(url);
                    }
                } else {
                    newList.add(item);
                }
            }
            return newList;
        }

        if (Date.class.isAssignableFrom(propertyClass)) {
            return ((Date) value).getTime();
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

    protected <T extends PersistableBusinessObject> Map<String, Object> searchBusinessObjects(Class<T> boClass, UriInfo uriInfo) throws ReflectiveOperationException {
        MultivaluedMap<String, String> params = uriInfo.getQueryParameters();
        Map<String, String> queryCriteria = getSearchQueryCriteria(params);

        int skip = getIntQueryParameter(KFSConstants.Search.SKIP, params);
        int limit = getIntQueryParameter(KFSConstants.Search.LIMIT, params);
        if (limit == 0) {
            limit = LookupUtils.getSearchResultsLimit(boClass);
        }

        String orderByString = params.getFirst(KFSConstants.Search.SORT);
        String[] orderBy = new String[]{"objectId"};
        if (orderByString != null) {
            orderBy = orderByString.split(",");
        }

        Map<String, Object> results = new HashMap<>();
        results.put(KFSConstants.Search.SORT, orderBy);
        results.put(KFSConstants.Search.SKIP, skip);
        results.put(KFSConstants.Search.LIMIT, limit);
        results.put(KFSConstants.Search.QUERY, queryCriteria);
        results.put(KFSConstants.Search.TOTAL_COUNT, getBusinessObjectService().countMatching(boClass, queryCriteria));

        Collection<T> queryResults = getBusinessObjectService().findMatching(boClass, queryCriteria, skip, limit, orderBy);
        if (queryResults.size() < 1) {
            results.put(KFSConstants.Search.RESULTS, new ArrayList<>());
            return results;
        }

        List<Map<String, Object>> jsonResults = new ArrayList<>();
        for (PersistableBusinessObject bo : queryResults) {
            ObjectUtils.materializeSubObjectsToDepth(bo, 1);
            // ^^^^^^^ Taking Super Long ^^^^^^^^^
            Map<String, Object> jsonObject = businessObjectToJson(boClass, bo);
            jsonResults.add(jsonObject);
            // TODO: Check authorization
        }

        results.put(KFSConstants.Search.RESULTS, jsonResults);
        return results;
    }

    private int getIntQueryParameter(String name, MultivaluedMap<String, String> params) {
        String paramString = params.getFirst(name);
        int value = 0;
        try {
            value = Integer.parseInt(paramString);
        } catch (NumberFormatException nfe) {
            LOG.debug(name + " parameter is invalid format", nfe);
        }
        return value;
    }

    private Map<String, String> getSearchQueryCriteria(MultivaluedMap<String, String> params) {
        List<String> whiteList = new ArrayList<>(Arrays.asList(KFSConstants.Search.SORT, KFSConstants.Search.LIMIT, KFSConstants.Search.SKIP));
        return params.entrySet().stream()
            .filter(map -> !whiteList.contains(map.getKey().toLowerCase()))
            .collect(Collectors.toMap(p -> p.getKey(), p -> params.getFirst(p.getKey())));
    }

    private <T extends PersistableBusinessObject> Map<String, Object> businessObjectToJson(Class<T> boClass, PersistableBusinessObject bo) throws ReflectiveOperationException {
        ObjectUtils.materializeSubObjectsToDepth(bo, 3);

        Map<String, Object> jsonObject = new LinkedHashMap<String, Object>();
        for (PropertyDescriptor propertyDescriptor : PropertyUtils.getPropertyDescriptors(bo)) {
            Method readMethod = propertyDescriptor.getReadMethod();
            if (readMethod != null && readMethod.getParameterCount() == 0 && Modifier.isPublic(readMethod.getModifiers())) {
                Object jsonValue = getJsonValue(bo, propertyDescriptor);

                if (jsonValue != null) {
                    final Object possiblyMaskedJsonValue = maskJsonValueIfNecessary(boClass.getSimpleName(), propertyDescriptor.getName(), jsonValue);
                    jsonObject.put(propertyDescriptor.getName(), possiblyMaskedJsonValue);
                }
            }
        }

        return jsonObject;
    }

    @SuppressWarnings("unchecked")
    protected Class<PersistableBusinessObject> determineClass(String moduleName, String businessObjectName) {
        ModuleService moduleService = determineModuleService(moduleName);
        if (moduleService == null) {
            return null;
        }
        String boClassName = convertUrlBoNameToClassName(businessObjectName);
        // Search for class in module.
        if (!getDataDictionaryService().containsDictionaryObject(boClassName)) {
            return null;
        }
        Object ddObject = getDataDictionaryService().getDictionaryObject(boClassName);
        if (!(ddObject instanceof BusinessObjectEntry)) {
            return null;
        }

        BusinessObjectEntry boEntry = (BusinessObjectEntry) ddObject;
        Class<? extends BusinessObject> boClass = boEntry.getBusinessObjectClass();
        if (!(PersistableBusinessObject.class.isAssignableFrom(boClass))) {
            return null;
        }
        return (Class<PersistableBusinessObject>) boClass;
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

    protected String convertClassToUrlBoName(Class clazz, ModuleService moduleService) {
        DataDictionary dd = getDataDictionaryService().getDataDictionary();
        BusinessObjectEntry boEntry = dd.getBusinessObjectEntryForConcreteClass(clazz.getName());
        if (boEntry == null) {
            return null;
        }
        String beanName = boEntry.getJstlKey();
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, beanName) + "s";
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

        ModuleService moduleService = kualiModuleService.getResponsibleModuleService(businessObject.getClass());
        if (moduleService == null) {
            return null;
        }

        String moduleName = getModuleName(moduleService);
        if (moduleName == null) {
            return null;
        }

        String urlBoName = convertClassToUrlBoName(persistableBo.getClass(), moduleService);
        if (urlBoName == null) {
            return null;
        }

        Map<String, Object> result = new LinkedHashMap<>();
        String url = getBaseUrl()
            + "/api/v1/"
            + "business-object/"
            + moduleName
            + "/"
            + urlBoName
            + "/"
            + persistableBo.getObjectId();
        result.put(KFSPropertyConstants.LINK, url);

        return result;
    }

    protected String getBaseUrl() {
        return getConfigurationService().getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY);
    }

    protected String getModuleName(ModuleService moduleService) {
        String moduleServiceName = moduleService.getModuleConfiguration().getNamespaceCode().toLowerCase();
        if (moduleServiceName.contains("-")) {
            moduleServiceName = StringUtils.substringAfter(moduleServiceName, "-");
        }
        return moduleServiceName;
    }

    protected boolean isAuthorized(String inquireIntoRecords, Class<PersistableBusinessObject> boClass) {
        return getPermissionService().isAuthorizedByTemplate(getPrincipalId(), KRADConstants.KNS_NAMESPACE,
            KimConstants.PermissionTemplateNames.INQUIRE_INTO_RECORDS,
            KRADUtils.getNamespaceAndComponentSimpleName(boClass),
            Collections.<String, String>emptyMap());
    }

    protected boolean isAuthorizedByAccessSecurity(PersistableBusinessObject businessObject) {
        List<PersistableBusinessObject> list = new ArrayList<>();
        list.add(businessObject);
        applySecurityRestrictionsForInquiry(businessObject.getClass(), list);
        return (!list.isEmpty());
    }

    protected void applySecurityRestrictionsForInquiry(Class<? extends PersistableBusinessObject> boClass, List<PersistableBusinessObject> results) {
        final AccessSecurityService accessSecurityService = getAccessSecurityService();
        if (accessSecurityService != null) {
            accessSecurityService.applySecurityRestrictions(results,
                getPerson(),
                accessSecurityService.getInquiryWithFieldValueTemplate(),
                Collections.singletonMap(KimConstants.AttributeConstants.NAMESPACE_CODE, KRADUtils.getNamespaceCode(boClass)));
        }
    }

    protected String getPrincipalId() {
        return getPerson().getPrincipalId();
    }

    protected Person getPerson() {
        return KRADUtils.getUserSessionFromRequest(servletRequest).getPerson();
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

    public static BusinessObjectAuthorizationService getBusinessObjectAuthorizationService() {
        if (businessObjectAuthorizationService == null) {
            businessObjectAuthorizationService = SpringContext.getBean(BusinessObjectAuthorizationService.class);
        }
        return businessObjectAuthorizationService;
    }

    protected ConfigurationService getConfigurationService() {
        if (configurationService == null) {
            configurationService = SpringContext.getBean(ConfigurationService.class);
        }
        return configurationService;
    }

    protected PermissionService getPermissionService() {
        if (permissionService == null) {
            permissionService = SpringContext.getBean(PermissionService.class);
        }
        return permissionService;
    }

    protected AccessSecurityService getAccessSecurityService() {
        if (!getConfigurationService().getPropertyValueAsBoolean(SecConstants.ACCESS_SECURITY_MODULE_ENABLED_PROPERTY_NAME)) {
            return null;
        }
        if (accessSecurityService == null) {
            accessSecurityService = SpringContext.getBean(AccessSecurityService.class);
        }
        return accessSecurityService;
    }

    public static DataDictionaryService getDataDictionaryService() {
        if (dataDictionaryService == null) {
            dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        }
        return dataDictionaryService;
    }

    public static void setKualiModuleService(KualiModuleService kualiModuleService) {
        BusinessObjectResource.kualiModuleService = kualiModuleService;
    }

    public static void setBusinessObjectService(BusinessObjectService businessObjectService) {
        BusinessObjectResource.businessObjectService = businessObjectService;
    }

    public static void setBusinessObjectAuthorizationService(BusinessObjectAuthorizationService businessObjectAuthorizationService) {
        BusinessObjectResource.businessObjectAuthorizationService = businessObjectAuthorizationService;
    }

    public static void setConfigurationService(ConfigurationService configurationService) {
        BusinessObjectResource.configurationService = configurationService;
    }

    public static void setPermissionService(PermissionService permissionService) {
        BusinessObjectResource.permissionService = permissionService;
    }

    public static void setAccessSecurityService(AccessSecurityService accessSecurityService) {
        BusinessObjectResource.accessSecurityService = accessSecurityService;
    }

    public static void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        BusinessObjectResource.dataDictionaryService = dataDictionaryService;
    }
}
