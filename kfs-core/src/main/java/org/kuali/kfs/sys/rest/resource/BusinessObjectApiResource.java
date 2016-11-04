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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kuali.kfs.kns.datadictionary.MaintenanceDocumentEntry;
import org.kuali.kfs.kns.lookup.LookupUtils;
import org.kuali.kfs.kns.service.BusinessObjectAuthorizationService;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.datadictionary.DataDictionary;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.DataDictionaryService;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.krad.service.PersistenceStructureService;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.KRADPropertyConstants;
import org.kuali.kfs.krad.util.KRADUtils;
import org.kuali.kfs.sec.SecConstants;
import org.kuali.kfs.sec.service.AccessSecurityService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.rest.ErrorMessage;
import org.kuali.kfs.sys.rest.exception.ApiRequestException;
import org.kuali.kfs.sys.rest.service.SerializationService;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.kuali.kfs.sys.rest.service.SerializationService.findBusinessObjectFields;

@Path("reference/{documentTypeName}")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BusinessObjectApiResource {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BusinessObjectApiResource.class);

    private String moduleName;

    private static volatile KualiModuleService kualiModuleService;
    private static volatile BusinessObjectService businessObjectService;
    private static volatile BusinessObjectAuthorizationService businessObjectAuthorizationService;
    private static volatile ConfigurationService configurationService;
    private static volatile PermissionService permissionService;
    private static volatile AccessSecurityService accessSecurityService;
    private static volatile DataDictionaryService dataDictionaryService;
    private static volatile PersistenceStructureService persistenceStructureService;

    @Context
    protected HttpServletRequest servletRequest;

    public BusinessObjectApiResource(String moduleName) {
        this.moduleName = moduleName;
    }

    @GET
    public Response findMultipleBusinessObjects(@PathParam("documentTypeName") String documentTypeName, @Context UriInfo uriInfo) {

        LOG.debug("searchObjects() started");

        MaintenanceDocumentEntry maintenanceDocumentEntry = getMaintenanceDocumentEntry(documentTypeName);
        if (maintenanceDocumentEntry == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Class<PersistableBusinessObject> boClass = determineClass(moduleName, maintenanceDocumentEntry);
        if (boClass == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (!isAuthorized(boClass)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        Map<String, Object> results = searchBusinessObjects(boClass, uriInfo, maintenanceDocumentEntry);

        return Response.ok(results).build();
    }

    @GET
    @Path("{objectId}")
    public Response findSingleBusinessObject(@PathParam("documentTypeName") String documentTypeName,
                                             @PathParam("objectId") String objectId) {
        LOG.debug("getSingleObject() started");

        MaintenanceDocumentEntry maintenanceDocumentEntry = getMaintenanceDocumentEntry(documentTypeName);
        if (maintenanceDocumentEntry == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Class<PersistableBusinessObject> boClass = determineClass(moduleName, maintenanceDocumentEntry);
        if (boClass == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (!isAuthorized(boClass)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        PersistableBusinessObject businessObject = findBusinessObject(boClass, objectId);
        if (businessObject == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (!isAuthorizedByAccessSecurity(businessObject)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        Map<String, Object> fields = findBusinessObjectFields(maintenanceDocumentEntry);

        Map<String, Object> jsonObject = SerializationService.businessObjectToJson(boClass, businessObject, fields, getPerson(),
                                            getPersistenceStructureService(), getDataDictionaryService(), getBusinessObjectAuthorizationService());
        // TODO: Check authorization

        return Response.ok(jsonObject).build();
    }

    protected <T extends PersistableBusinessObject> T findBusinessObject(Class<T> boClass, String objectId) {
        Map<String, String> queryCriteria = new HashMap<>();
        queryCriteria.put(KRADPropertyConstants.OBJECT_ID, objectId);
        Collection<T> queryResults = getBusinessObjectService().findMatching(boClass, queryCriteria);
        if (queryResults.size() != 1) {
            return null;
        }
        return queryResults.iterator().next();
    }

    protected <T extends PersistableBusinessObject> Map<String, Object> searchBusinessObjects(Class<T> boClass, UriInfo uriInfo,
                                                                                              MaintenanceDocumentEntry maintenanceDocumentEntry) {
        MultivaluedMap<String, String> params = uriInfo.getQueryParameters();
        Map<String, String> queryCriteria = getSearchQueryCriteria(boClass, params);

        int skip = getIntQueryParameter(KFSConstants.Search.SKIP, params);
        int limit = getLimit(boClass, params);

        String[] orderBy = getSortCriteria(boClass, params);

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
        Map<String, Object> fields = SerializationService.findBusinessObjectFields(maintenanceDocumentEntry);
        for (PersistableBusinessObject bo : queryResults) {
            Map<String, Object> jsonObject = SerializationService.businessObjectToJson(boClass, bo, fields, getPerson(),
                                                getPersistenceStructureService(), getDataDictionaryService(), getBusinessObjectAuthorizationService());
            jsonResults.add(jsonObject);
            // TODO: Check authorization
        }

        results.put(KFSConstants.Search.RESULTS, jsonResults);
        return results;
    }

    protected <T extends PersistableBusinessObject> int getLimit(Class<T> boClass, MultivaluedMap<String, String> params) {
        int limit = getIntQueryParameter(KFSConstants.Search.LIMIT, params);
        if (limit <= 0) {
            limit = LookupUtils.getSearchResultsLimit(boClass);
            if (limit <= 0) {
                limit = LookupUtils.getApplicationSearchResultsLimit();
            }
        }
        return limit;
    }

    protected <T extends PersistableBusinessObject> String[] getSortCriteria(Class<T> boClass, MultivaluedMap<String, String> params) {
        final List<String> ojbFields = getPersistenceStructureService().listFieldNames(boClass);

        String orderByString = params.getFirst(KFSConstants.Search.SORT);
        if (orderByString != null) {
            List<ErrorMessage> errorMessages = new ArrayList<>();
            List<String> validSortFields = new ArrayList<>();
            String[] orderBy = orderByString.split(",");
            for (String sort : orderBy) {
                String cleanSort = sort.replaceFirst("^-", "");
                if (ojbFields.contains(cleanSort)) {
                    validSortFields.add(sort);
                } else {
                    LOG.debug("invalid sort field: " + sort);
                    errorMessages.add(new ErrorMessage("invalid sort field", sort));
                }
            }

            if (errorMessages.size() > 0) {
                throw new ApiRequestException("Invalid Search Criteria", errorMessages);
            }

            return validSortFields.toArray(new String[]{});
        } else {
            final List<String> ojbPrimaryKeys = getPersistenceStructureService().listPrimaryKeyFieldNames(boClass);
            if (!CollectionUtils.isEmpty(ojbPrimaryKeys)) {
                return ojbPrimaryKeys.toArray(new String[] {});
            } else if (ojbFields.contains("objectId")){
                return new String[]{"objectId"};
            }
        }

        return new String[] { ojbFields.get(0) }; // no other fields to check from...let's just sort on the first ojb column
    }

    protected int getIntQueryParameter(String name, MultivaluedMap<String, String> params) {
        String paramString = params.getFirst(name);
        if (StringUtils.isNotBlank(paramString)) {
            try {
                return Integer.parseInt(paramString);
            } catch (NumberFormatException nfe) {
                LOG.debug(name + " parameter is not a number", nfe);
                throw new ApiRequestException("Invalid Search Criteria", new ErrorMessage("parameter is not a number", name));
            }
        }
        return 0;
    }

    protected <T extends PersistableBusinessObject> Map<String, String> getSearchQueryCriteria(Class<T> boClass, MultivaluedMap<String, String> params) {
        List<String> reservedParams = Arrays.asList(KFSConstants.Search.SORT, KFSConstants.Search.LIMIT, KFSConstants.Search.SKIP);
        List<String> ojbFields = getPersistenceStructureService().listFieldNames(boClass);
        List<ErrorMessage> errorMessages = new ArrayList<>();
        Map<String, String> validParams = params.entrySet().stream()
            .filter(entry -> !reservedParams.contains(entry.getKey().toLowerCase()))
            .filter(entry -> {
                if(ojbFields.contains(entry.getKey())) {
                    return true;
                }

                LOG.debug("invalid query parameter name: " + entry.getKey());
                errorMessages.add(new ErrorMessage("invalid query parameter name", entry.getKey()));
                return false;
            })
            .collect(Collectors.toMap(entry -> entry.getKey(), entry -> params.getFirst(entry.getKey())));

        if (errorMessages.size() > 0) {
            throw new ApiRequestException("Invalid Search Criteria", errorMessages);
        }

        return validParams;
    }

    protected Class<PersistableBusinessObject> determineClass(String moduleName, MaintenanceDocumentEntry maintenanceDocumentEntry) {
        ModuleService moduleService = determineModuleService(moduleName);
        if (moduleService == null) {
            return null;
        }

        Class<? extends BusinessObject> boClass = (Class<? extends BusinessObject>)maintenanceDocumentEntry.getDataObjectClass();
        // Fail if this class is not part of the requested module
        final boolean validModule = moduleService.getModuleConfiguration().getPackagePrefixes().stream()
            .anyMatch(packagePrefix -> boClass.getName().startsWith(packagePrefix));

        if (!validModule) {
            return null;
        }

        if (!(PersistableBusinessObject.class.isAssignableFrom(boClass))) {
            return null;
        }
        return (Class<PersistableBusinessObject>) boClass;
    }

    private MaintenanceDocumentEntry getMaintenanceDocumentEntry(String documentTypeName) {
        // Search for class in module.
        DataDictionary dataDictionary = getDataDictionaryService().getDataDictionary();
        MaintenanceDocumentEntry entry = (MaintenanceDocumentEntry)dataDictionary.getDocumentEntry(documentTypeName.toUpperCase());
        if (entry == null) {
            return null;
        }
        return entry;
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

    protected String convertClassToUrlBoName(Class clazz) {
        DataDictionary dd = getDataDictionaryService().getDataDictionary();
        MaintenanceDocumentEntry maintenanceDocumentEntry = (MaintenanceDocumentEntry) dd.getMaintenanceDocumentEntryForBusinessObjectClass(clazz);
        if (maintenanceDocumentEntry == null) {
            return null;
        }

        return maintenanceDocumentEntry.getDocumentTypeName().toLowerCase();
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

        String urlBoName = convertClassToUrlBoName(persistableBo.getClass());
        if (urlBoName == null) {
            return null;
        }

        Map<String, Object> result = new LinkedHashMap<>();
        String url = getBaseUrl()
            + "/"
            + moduleName
            + "/api/v1/reference/"
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

    protected boolean isAuthorized(Class<PersistableBusinessObject> boClass) {
        return getPermissionService().isAuthorizedByTemplate(getPrincipalId(), KRADConstants.KNS_NAMESPACE,
            KimConstants.PermissionTemplateNames.INQUIRE_INTO_RECORDS,
            KRADUtils.getNamespaceAndComponentSimpleName(boClass),
            Collections.emptyMap());
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

    protected static BusinessObjectAuthorizationService getBusinessObjectAuthorizationService() {
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

    protected static DataDictionaryService getDataDictionaryService() {
        if (dataDictionaryService == null) {
            dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        }
        return dataDictionaryService;
    }

    protected PersistenceStructureService getPersistenceStructureService() {
        if (persistenceStructureService == null) {
            persistenceStructureService = SpringContext.getBean(PersistenceStructureService.class);
        }
        return persistenceStructureService;
    }

    public static void setKualiModuleService(KualiModuleService kualiModuleService) {
        BusinessObjectApiResource.kualiModuleService = kualiModuleService;
    }

    public static void setBusinessObjectService(BusinessObjectService businessObjectService) {
        BusinessObjectApiResource.businessObjectService = businessObjectService;
    }

    public static void setBusinessObjectAuthorizationService(BusinessObjectAuthorizationService businessObjectAuthorizationService) {
        BusinessObjectApiResource.businessObjectAuthorizationService = businessObjectAuthorizationService;
    }

    public static void setConfigurationService(ConfigurationService configurationService) {
        BusinessObjectApiResource.configurationService = configurationService;
    }

    public static void setPermissionService(PermissionService permissionService) {
        BusinessObjectApiResource.permissionService = permissionService;
    }

    public static void setAccessSecurityService(AccessSecurityService accessSecurityService) {
        BusinessObjectApiResource.accessSecurityService = accessSecurityService;
    }

    public static void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        BusinessObjectApiResource.dataDictionaryService = dataDictionaryService;
    }

    public static void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        BusinessObjectApiResource.persistenceStructureService = persistenceStructureService;
    }
}
