/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 The Kuali Foundation
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

package org.kuali.kfs.sys.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.kuali.kfs.krad.bo.GlobalBusinessObject;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.datadictionary.AttributeDefinition;
import org.kuali.kfs.krad.datadictionary.BusinessObjectEntry;
import org.kuali.kfs.krad.datadictionary.MaintenanceDocumentEntry;
import org.kuali.kfs.krad.service.DataDictionaryService;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.PersistenceStructureService;
import org.kuali.kfs.sys.businessobject.dto.EntityDTO;
import org.kuali.kfs.sys.businessobject.dto.FieldDTO;
import org.kuali.kfs.sys.businessobject.dto.TableDTO;
import org.kuali.kfs.sys.service.DataDictionaryMigrationService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiPercent;
import org.kuali.rice.krad.bo.BusinessObject;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataDictionaryMigrationServiceImpl implements DataDictionaryMigrationService {
    protected DataDictionaryService dataDictionaryService;
    protected KualiModuleService kualiModuleService;
    protected PersistenceStructureService persistenceStructureService;
    protected ConfigurationService configurationService;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DataDictionaryMigrationServiceImpl.class);

    @Override
    public void migrate() {
        final List<MaintenanceDocumentEntry> entities = retrieveAllMaintenanceDocumentEntries();
        Map<Class<? extends PersistableBusinessObject>, String> businessObjectsOwnedByEntities = listAllTakenBusinessObjects(entities);
        List<EntityDTO> entitiesMap = entities.stream()
            .map(maintenanceDocumentEntry -> convertMaintenanceDocumentToEntityDTO(maintenanceDocumentEntry))
            .distinct()
            .collect(Collectors.toList());
        String finConfigUrl = configurationService.getPropertyValueAsString("config.url");
        String finConfigToken = configurationService.getPropertyValueAsString("config.authToken");
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPut request = new HttpPut(finConfigUrl + "/api/v1/metadata/entities");
        request.addHeader(HttpHeaders.AUTHORIZATION,"Bearer " + finConfigToken);
        request.addHeader(HttpHeaders.CONTENT_TYPE,"application/json");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> finalMap = new HashMap<>();
            finalMap.put("entities", entitiesMap);
            String entitiesMapString = objectMapper.writeValueAsString(finalMap);
            LOG.warn(entitiesMapString);
            StringEntity stringEntity = new StringEntity(entitiesMapString, "UTF-8");
            request.setEntity(stringEntity);
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() != 200) {
                LOG.error("Failed to push data dictionary to fin-config: " + response.getStatusLine().getStatusCode() + " - " + inputStreamToString(response.getEntity().getContent()));
            }
        } catch (JsonProcessingException e) {
            LOG.error("Failed to convert entities to JSON", e);
            throw new RuntimeException(e);
        } catch (IOException ioException) {
            LOG.error("Failed to execute request", ioException);
            throw new RuntimeException(ioException);
        }
    }

    protected List<MaintenanceDocumentEntry> retrieveAllMaintenanceDocumentEntries() {
         return dataDictionaryService.getDataDictionary().getDocumentEntries().values().stream()
                .filter(entry -> entry instanceof MaintenanceDocumentEntry)
                .map(entry -> (MaintenanceDocumentEntry)entry)
                .filter(entry -> !GlobalBusinessObject.class.isAssignableFrom(entry.getDataObjectClass()))
                // FR uses Asset so we don't want to allow it through
                .filter(entry -> !StringUtils.equals(entry.getDocumentTypeName(), "FR") &&
                    !StringUtils.equals(entry.getDocumentTypeName(), "ORR"))
                .collect(Collectors.toList());

    }

    protected Map<Class<? extends PersistableBusinessObject>, String> listAllTakenBusinessObjects(List<MaintenanceDocumentEntry> maintenanceDocumentEntries) {
        Map<Class<? extends PersistableBusinessObject>, String> takenBusinessObjects = new HashMap<>();
        maintenanceDocumentEntries.forEach(entry -> {
            if (PersistableBusinessObject.class.isAssignableFrom(entry.getDataObjectClass())) {
                takenBusinessObjects.put((Class<? extends PersistableBusinessObject>)entry.getDataObjectClass(), entry.getDocumentTypeName());
            }

        });
        return takenBusinessObjects;
    }

    protected EntityDTO convertMaintenanceDocumentToEntityDTO(MaintenanceDocumentEntry maintenanceDocumentEntry) {
        EntityDTO entityDTO = new EntityDTO();
        entityDTO.setModuleCode(kualiModuleService.getResponsibleModuleService(maintenanceDocumentEntry.getDataObjectClass()).getModuleConfiguration().getNamespaceCode());
        entityDTO.setCode(maintenanceDocumentEntry.getDocumentTypeName());
        entityDTO.setName(retrieveObjectLabel((Class<? extends PersistableBusinessObject>)maintenanceDocumentEntry.getDataObjectClass()));
        entityDTO.setTables(buildTableDTOs(maintenanceDocumentEntry));
        return entityDTO;
    }

    protected List<TableDTO> buildTableDTOs(MaintenanceDocumentEntry maintenanceDocumentEntry) {
        List<TableDTO> tableMetadata = new ArrayList<>();
        TableDTO tableMetadatum = buildTableDTO((Class<? extends PersistableBusinessObject>)maintenanceDocumentEntry.getDataObjectClass());
        if (tableMetadatum != null) {
            tableMetadata.add(tableMetadatum);
        }
        return tableMetadata;
    }

    protected TableDTO buildTableDTO(Class<? extends PersistableBusinessObject> tableClass) {
        TableDTO tableDTO = new TableDTO();
        if (persistenceStructureService.isPersistable(tableClass)) {
            tableDTO.setCode(persistenceStructureService.getTableName(tableClass));
            tableDTO.setName(retrieveObjectLabel(tableClass));
            tableDTO.setFields(buildFieldDTOs(tableClass));
            return tableDTO;
        }
        return null;
    }

    protected List<FieldDTO> buildFieldDTOs(final Class<? extends PersistableBusinessObject> tableClass) {
        return (List<FieldDTO>)persistenceStructureService
                                            .listFieldNames(tableClass)
                                            .stream()
                                            .filter(fieldName -> !StringUtils.equals((String)fieldName, "objectId") &&
                                                !StringUtils.equals((String)fieldName, "versionNumber") &&
                                                !StringUtils.equals((String)fieldName, "lastUpdatedTimestamp"))
                                            .map(fieldName -> buildFieldDTO(tableClass, (String)fieldName))
                                            .filter(fieldMap -> fieldMap != null)
                                            .collect(Collectors.toList());
    }

    protected FieldDTO buildFieldDTO(Class<? extends PersistableBusinessObject> tableClass, String fieldName) {
        FieldDTO fieldDTO = new FieldDTO();
        final AttributeDefinition attributeDefinition = retrieveAttribute(tableClass, fieldName);
        if (attributeDefinition == null) {
            return null;
        }
        fieldDTO.setCode(persistenceStructureService.getColumnNameForFieldName(tableClass, fieldName));
        fieldDTO.setName(attributeDefinition.getLabel());
        fieldDTO.setShortName(attributeDefinition.getShortLabel());
        if (attributeDefinition.getMaxLength() != null) {
            fieldDTO.setLength(attributeDefinition.getMaxLength());
        }
        fieldDTO.setRequired(attributeDefinition.isRequired());
        fieldDTO.setFieldType(determineFieldType(tableClass, fieldName));
        return fieldDTO;
    }

    protected String determineFieldType(Class<? extends PersistableBusinessObject> tableClass, String fieldName) {
        Class fieldClass = thieveAttributeClassFromBusinessObjectClass(tableClass, fieldName);
        if (fieldClass == KualiDecimal.class) {
            return "currency";
        } else if (fieldClass == Boolean.TYPE || fieldClass == Boolean.class) {
            return "indicator";
        } else if (java.util.Date.class.isAssignableFrom(fieldClass)) {
            if (fieldClass == java.sql.Time.class) {
                return "time";
            } else if (fieldClass == java.sql.Timestamp.class) {
                return "datetime";
            } else {
                return "date";
            }
        } else if (fieldClass == KualiPercent.class) {
            return "percent";
        } else if (Number.class.isAssignableFrom(fieldClass)) {
            return "number";
        } else if (fieldName.toLowerCase().endsWith("phonenumber") || fieldName.toLowerCase().endsWith("faxnumber")) {
            return "phone";
        } else if (fieldName.toLowerCase().endsWith("email") || fieldName.toLowerCase().endsWith("emailaddress")) {
            return "email";
        }
        return "text";
    }

    /**
     * Given a BusinessObject class and an attribute name, determines the class of that attribute on the BusinessObject class
     *
     * @param boClass      a class extending BusinessObject
     * @param attributeKey the name of a field on that class
     * @return the Class of the given attribute
     */
    protected Class thieveAttributeClassFromBusinessObjectClass(Class<? extends BusinessObject> boClass, String attributeKey) {
        for (PropertyDescriptor prop : PropertyUtils.getPropertyDescriptors(boClass)) {
            if (prop.getName().equals(attributeKey)) {
                return prop.getPropertyType();
            }
        }
        return null;
    }

    protected AttributeDefinition retrieveAttribute(Class<? extends PersistableBusinessObject> tableClass, String fieldName) {
        final BusinessObjectEntry businessObjectEntry = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(tableClass.getName());
        if (businessObjectEntry == null) {
            LOG.error("Could not find businessObjectEntry for " + tableClass.getName() + " " + fieldName);
            return null;
        }
        return businessObjectEntry.getAttributeDefinition(fieldName);
    }

    protected String inputStreamToString(InputStream stream) {
        try {
            return IOUtils.toString(stream,"UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected String retrieveObjectLabel(Class<? extends PersistableBusinessObject> dataObjectClass) {
        return dataDictionaryService.getDataDictionary().getBusinessObjectEntry(dataObjectClass.getName()).getObjectLabel();
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }

    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
