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

package org.kuali.kfs.sys.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.kuali.kfs.kns.datadictionary.MaintainableCollectionDefinition;
import org.kuali.kfs.kns.datadictionary.MaintainableFieldDefinition;
import org.kuali.kfs.kns.datadictionary.MaintainableItemDefinition;
import org.kuali.kfs.kns.datadictionary.MaintenanceDocumentEntry;
import org.kuali.kfs.kns.service.DataDictionaryService;
import org.kuali.kfs.krad.bo.GlobalBusinessObject;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.datadictionary.AttributeDefinition;
import org.kuali.kfs.krad.datadictionary.BusinessObjectEntry;
import org.kuali.kfs.krad.datadictionary.DocumentEntry;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.PersistenceStructureService;
import org.kuali.kfs.sys.batch.DataDictionaryFilteredEntity;
import org.kuali.kfs.sys.batch.DataDictionaryFilteredField;
import org.kuali.kfs.sys.batch.DataDictionaryFilteredTable;
import org.kuali.kfs.sys.businessobject.dto.ConcernDTO;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DataDictionaryMigrationServiceImpl implements DataDictionaryMigrationService {

    protected DataDictionaryService dataDictionaryService;
    protected KualiModuleService kualiModuleService;
    protected PersistenceStructureService persistenceStructureService;
    protected ConfigurationService configurationService;

    private List<DataDictionaryFilteredEntity> filteredEntities = new ArrayList<>();
    private List<DataDictionaryFilteredTable> filteredTables = new ArrayList<>();
    private List<DataDictionaryFilteredField> filteredFields = new ArrayList<>();
    private List<String> concerns = new ArrayList<>();

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DataDictionaryMigrationServiceImpl.class);

    @Override
    public void migrate() {
        final List<MaintenanceDocumentEntry> entities = retrieveAllMaintenanceDocumentEntries();
        Set<Class<? extends PersistableBusinessObject>> collectionClasses = new HashSet<>();
        Map<Class<? extends PersistableBusinessObject>, String> businessObjectsOwnedByEntities = listAllTakenBusinessObjects(entities);
        Map<Class<? extends PersistableBusinessObject>, String> nestedBusinessObjectsByEntity = listNestedBusinessObjects(entities, businessObjectsOwnedByEntities, collectionClasses);
        businessObjectsOwnedByEntities.putAll(nestedBusinessObjectsByEntity);

        Iterator iterator = businessObjectsOwnedByEntities.entrySet().iterator();
        Map<String, List<Class<? extends PersistableBusinessObject>>> entityBusinessObjectClasses = new HashMap<>();
        while (iterator.hasNext()) {
            Map.Entry<Class<? extends PersistableBusinessObject>, String> entry = (Map.Entry<Class<? extends PersistableBusinessObject>, String>) iterator.next();
            if (entityBusinessObjectClasses.containsKey(entry.getValue())) {
                entityBusinessObjectClasses.get(entry.getValue()).add(entry.getKey());
            } else {
                List<Class<? extends PersistableBusinessObject>> businessObjectClasses = new ArrayList<>();
                businessObjectClasses.add(entry.getKey());
                entityBusinessObjectClasses.put(entry.getValue(), businessObjectClasses);
            }
        }

        Set<String> migratedEntities = new HashSet<>();
        List<EntityDTO> maintenanceEntities = entities.stream()
            .map(maintenanceDocumentEntry -> convertMaintenanceDocumentToEntityDTO(maintenanceDocumentEntry, entityBusinessObjectClasses, migratedEntities, collectionClasses))
            .filter(entityDTO -> entityDTO != null)
            .distinct()
            .collect(Collectors.toList());

        Map<String, ConcernDTO> takenTables = new HashMap<>();
        List<EntityDTO> transactionalEntities = new ArrayList<>();
        EntityDTO tf = gatherTransactionalEntities("TF", "Transfer of Funds", "KFS-FP", businessObjectsOwnedByEntities, takenTables);
        transactionalEntities.add(tf);
        EntityDTO ib = gatherTransactionalEntities("IB", "Internal Billing", "KFS-FP", businessObjectsOwnedByEntities, takenTables);
        transactionalEntities.add(ib);


        List<EntityDTO> allEntities = new ArrayList<>();
        allEntities.addAll(maintenanceEntities);
        allEntities.addAll(transactionalEntities);


        String finConfigUrl = configurationService.getPropertyValueAsString("config.url");
        String finConfigToken = configurationService.getPropertyValueAsString("config.authToken");
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPut request = new HttpPut(finConfigUrl + "/api/v1/metadata/entities");
        request.addHeader(HttpHeaders.AUTHORIZATION,"Bearer " + finConfigToken);
        request.addHeader(HttpHeaders.CONTENT_TYPE,"application/json");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> finalMap = new HashMap<>();
            finalMap.put("entities", allEntities);
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

    protected EntityDTO gatherTransactionalEntities(String documentTypeCode, String documentName, String moduleCode, Map<Class<? extends PersistableBusinessObject>, String> businessObjectsOwnedByEntities, Map<String, ConcernDTO> takenTables) {
        DocumentEntry transactionalDoc = dataDictionaryService.getDataDictionary().getDocumentEntry(documentTypeCode);
        Set<TableDTO> refTableDTOs = new HashSet<>();
        Set<ConcernDTO> concernDTOs = new HashSet<>();

        Map<String, Class> refObjectFields = persistenceStructureService.listReferenceObjectFields(transactionalDoc.getDocumentClass());
        Iterator<Map.Entry<String, Class>> refObjIter = refObjectFields.entrySet().iterator();
        while (refObjIter.hasNext()) {
            Map.Entry refObj = refObjIter.next();
            Class<? extends PersistableBusinessObject> refObjClass = (Class<? extends PersistableBusinessObject>)refObj.getValue();
            if (!businessObjectsOwnedByEntities.containsKey(refObjClass)) {
                String tableName = persistenceStructureService.getTableName(refObjClass);
                if (concerns.contains(persistenceStructureService.getTableName(refObjClass))) {
                    ConcernDTO concern;
                    if (!takenTables.containsKey(tableName)) {
                        concern = buildConcernDTO(refObjClass, false);
                        takenTables.put(tableName, concern);
                    } else {
                        concern = takenTables.get(tableName);
                    }
                    concernDTOs.add(concern);
                } else {
                    refTableDTOs.add(buildTableDTO(refObjClass, false));
                }
            }
        }

        Map<String, Class> collectionObjectTypes = persistenceStructureService.listCollectionObjectTypes(transactionalDoc.getDocumentClass());
        Iterator<Map.Entry<String, Class>> collObjIter = collectionObjectTypes.entrySet().iterator();
        while (collObjIter.hasNext()) {
            Map.Entry collObj = collObjIter.next();
            Class<? extends PersistableBusinessObject> collObjClass = (Class<? extends PersistableBusinessObject>)collObj.getValue();
            if (!businessObjectsOwnedByEntities.containsKey(collObjClass)) {
                String tableName = persistenceStructureService.getTableName(collObjClass);
                if (concerns.contains(persistenceStructureService.getTableName(collObjClass))) {
                    ConcernDTO concern;
                    if (!takenTables.containsKey(tableName)) {
                        concern = buildConcernDTO(collObjClass, true);
                        takenTables.put(tableName, concern);
                    } else {
                        concern = takenTables.get(tableName);
                    }
                    concernDTOs.add(concern);
                } else {
                    refTableDTOs.add(buildTableDTO(collObjClass, true));
                }
            }
        }

        EntityDTO entityDTO = new EntityDTO();
        entityDTO.setTables(refTableDTOs);
        entityDTO.setConcerns(concernDTOs);
        entityDTO.setName(documentName);
        entityDTO.setCode(documentTypeCode);
        entityDTO.setModuleCode(moduleCode);

        return entityDTO;
    }

    protected ConcernDTO buildConcernDTO(Class<? extends PersistableBusinessObject> tableClass, boolean collection) {
        ConcernDTO concernDTO = new ConcernDTO();
        if (persistenceStructureService.isPersistable(tableClass)) {
            TableDTO tableDTO = buildTableDTO(tableClass, collection);
            concernDTO.setTable(tableDTO);
            concernDTO.setCode(tableDTO.getCode());
            concernDTO.setDescription(tableDTO.getDescription());
            concernDTO.setName(tableDTO.getName());
        }
        return concernDTO;
    }


    protected Map<Class<? extends PersistableBusinessObject>,String> listNestedBusinessObjects(List<MaintenanceDocumentEntry> entities, Map<Class<? extends PersistableBusinessObject>, String> businessObjectsOwnedByEntities, Set<Class<? extends PersistableBusinessObject>> collectionClasses) {
        Map<Class<? extends PersistableBusinessObject>,String> nestedBusinessObjects = new HashMap<>();

        entities.stream().forEach(entity -> {
            final String documentTypeName = entity.getDocumentTypeName();
            final Class<? extends BusinessObject> businessObjectClass = entity.getBusinessObjectClass();
            entity.getMaintainableSections().stream().forEach(maintainableSection -> {
                List<MaintainableItemDefinition> maintainableItems = maintainableSection.getMaintainableItems();
                assignUnclaimedAttributeClasses(businessObjectsOwnedByEntities, nestedBusinessObjects, documentTypeName, businessObjectClass, maintainableItems);
                maintainableItems.stream()
                    .filter(maintainbleItem -> maintainbleItem instanceof MaintainableCollectionDefinition)
                    .map(maintainableItem -> (MaintainableCollectionDefinition)maintainableItem)
                    .forEach(maintainableCollectionDefinition -> {
                        collectionClasses.add((Class<? extends PersistableBusinessObject>)maintainableCollectionDefinition.getBusinessObjectClass());
                        assignUnclaimedAttributeClasses(businessObjectsOwnedByEntities, nestedBusinessObjects, documentTypeName, maintainableCollectionDefinition.getBusinessObjectClass(), maintainableCollectionDefinition.getMaintainableFields());
                        if (maintainableCollectionDefinition.getMaintainableCollections() != null && !maintainableCollectionDefinition.getMaintainableCollections().isEmpty()) {
                            maintainableCollectionDefinition.getMaintainableCollections().forEach(maintainableCollection -> {
                                collectionClasses.add((Class<? extends PersistableBusinessObject>)maintainableCollection.getBusinessObjectClass());
                                assignUnclaimedAttributeClasses(businessObjectsOwnedByEntities, nestedBusinessObjects, documentTypeName, maintainableCollection.getBusinessObjectClass(), maintainableCollection.getMaintainableFields());
                            });
                        }
                    });
            });
        });

        return nestedBusinessObjects;
    }

    protected void assignUnclaimedAttributeClasses(Map<Class<? extends PersistableBusinessObject>, String> businessObjectsOwnedByEntities, Map<Class<? extends PersistableBusinessObject>, String> nestedBusinessObjects, String documentTypeName, Class<? extends BusinessObject> businessObjectClass, List<? extends MaintainableItemDefinition> maintainableItems) {
        maintainableItems.stream()
            .filter(maintainableItem -> maintainableItem instanceof MaintainableFieldDefinition)
            .filter(maintainableField -> maintainableField.getName().contains("."))
            .filter(maintainableField -> !((MaintainableFieldDefinition)maintainableField).isUnconditionallyReadOnly())
            .map(maintainableField -> {
                String attributeKey = maintainableField.getName().substring(0, maintainableField.getName().indexOf("."));
                return thieveAttributeClassFromBusinessObjectClass(businessObjectClass, attributeKey);
            })
            .filter(attributeClass -> attributeClass != null && PersistableBusinessObject.class.isAssignableFrom(attributeClass) && !businessObjectsOwnedByEntities.containsKey((Class<? extends PersistableBusinessObject>)attributeClass) )
            .map(attributeClass -> (Class<? extends PersistableBusinessObject>)attributeClass)
            .forEach(attributeClass -> {
                if (nestedBusinessObjects.containsKey(attributeClass)) {
                    LOG.error("Attribute Class already taken: " + attributeClass.getSimpleName() + "(" + documentTypeName + ")");
                } else {
                    nestedBusinessObjects.put(attributeClass, documentTypeName);
                }
            });
    }

    protected List<MaintenanceDocumentEntry> retrieveAllMaintenanceDocumentEntries() {

         final Map<Boolean, List<MaintenanceDocumentEntry>> partitionedEntries = dataDictionaryService.getDataDictionary().getDocumentEntries().values().stream()
                .filter(entry -> entry instanceof MaintenanceDocumentEntry)
                .map(entry -> (MaintenanceDocumentEntry)entry)
                .filter(entry -> !GlobalBusinessObject.class.isAssignableFrom(entry.getDataObjectClass()))
                .collect(Collectors.partitioningBy((MaintenanceDocumentEntry entry) -> filteredEntities.stream()
                        .noneMatch(filteredField -> filteredField.matches(entry.getDocumentTypeName()))));

         partitionedEntries.get(false).stream().forEach(entry -> {
             LOG.warn("Filtered out Entity for " + entry.getDocumentTypeName() + ": " + retrieveObjectLabel((Class<? extends PersistableBusinessObject>)entry.getBusinessObjectClass()) );
         });

         return partitionedEntries.get(true);

    }

    protected Map<Class<? extends PersistableBusinessObject>, String> listAllTakenBusinessObjects(List<MaintenanceDocumentEntry> maintenanceDocumentEntries) {
        Map<Class<? extends PersistableBusinessObject>, String> takenBusinessObjects = new HashMap<>();
        maintenanceDocumentEntries.forEach(entry -> {
            if (PersistableBusinessObject.class.isAssignableFrom(entry.getDataObjectClass())) {
                String documentTypeName = entry.getDocumentTypeName();
                takenBusinessObjects.put((Class<? extends PersistableBusinessObject>)entry.getDataObjectClass(), documentTypeName);

                entry.getMaintainableSections().stream().forEach(maintainableSection ->
                    maintainableSection.getMaintainableItems().stream()
                        .filter(maintainableItem -> maintainableItem instanceof MaintainableCollectionDefinition)
                        .map(maintainableItem -> (MaintainableCollectionDefinition) maintainableItem)
                        .forEach(maintainableCollectionDefinition -> {
                            Class<? extends PersistableBusinessObject> businessObjectClass = (Class<? extends PersistableBusinessObject>) maintainableCollectionDefinition.getBusinessObjectClass();
                            if (takenBusinessObjects.containsKey(businessObjectClass)) {
                                LOG.error("Collection Class already taken: " + businessObjectClass.getSimpleName() + "(" + documentTypeName + ")");
                            } else {
                                takenBusinessObjects.put(((Class<? extends PersistableBusinessObject>) maintainableCollectionDefinition.getBusinessObjectClass()), documentTypeName);
                            }
                            if (maintainableCollectionDefinition.getMaintainableCollections() != null && !maintainableCollectionDefinition.getMaintainableCollections().isEmpty()) {
                                maintainableCollectionDefinition.getMaintainableCollections().forEach(maintainableSubCollection -> {
                                    Class<? extends PersistableBusinessObject> subCollectionBusinessObjectClass = (Class<? extends PersistableBusinessObject>) maintainableSubCollection.getBusinessObjectClass();
                                    if (takenBusinessObjects.containsKey(subCollectionBusinessObjectClass)) {
                                        LOG.error("Collection Class already taken: " + subCollectionBusinessObjectClass.getSimpleName() + "(" + documentTypeName + ")");
                                    } else {
                                        takenBusinessObjects.put(subCollectionBusinessObjectClass, documentTypeName);
                                    }
                                });
                            }
                        })
                );

            }

        });
        return takenBusinessObjects;
    }

    protected EntityDTO convertMaintenanceDocumentToEntityDTO(MaintenanceDocumentEntry maintenanceDocumentEntry, Map<String, List<Class<? extends PersistableBusinessObject>>> businessObjectsOwnedByEntities, Set<String> migratedEntities, Set<Class<? extends PersistableBusinessObject>> collectionClasses) {
        if (!migratedEntities.contains(maintenanceDocumentEntry.getDocumentTypeName())) {
            EntityDTO entityDTO = new EntityDTO();
            entityDTO.setModuleCode(kualiModuleService.getResponsibleModuleService(maintenanceDocumentEntry.getDataObjectClass()).getModuleConfiguration().getNamespaceCode());
            entityDTO.setCode(maintenanceDocumentEntry.getDocumentTypeName());
            entityDTO.setName(retrieveObjectLabel((Class<? extends PersistableBusinessObject>) maintenanceDocumentEntry.getDataObjectClass()));
            Set<TableDTO> tables = buildTableDTOs(businessObjectsOwnedByEntities.get(maintenanceDocumentEntry.getDocumentTypeName()), collectionClasses);
            entityDTO.setTables(tables);
            return entityDTO;
        } else {
            return null;
        }
    }

    protected Set<TableDTO> buildTableDTOs(List<Class<? extends PersistableBusinessObject>> businessObjectClassesForEntity, Set<Class<? extends PersistableBusinessObject>> collectionClasses) {
        Map<Boolean, List<Class<? extends PersistableBusinessObject>>> partitionedTables = businessObjectClassesForEntity.stream()
                .collect(Collectors.partitioningBy(businessObjectClass -> filteredTables.stream()
                        .noneMatch(filteredTable -> filteredTable.matches(businessObjectClass.getSimpleName()))));


        partitionedTables.get(false).stream().forEach(businessObjectClass -> {
            LOG.warn("Filtered out Table for " + businessObjectClass.getSimpleName());
        });

        return partitionedTables.get(true).stream().map(businessObjectClass -> buildTableDTO(businessObjectClass, collectionClasses.contains(businessObjectClass)))
                .filter(tableDTO -> tableDTO != null).distinct().collect(Collectors.toSet());

    }

    protected TableDTO buildTableDTO(Class<? extends PersistableBusinessObject> tableClass, boolean collection) {
        TableDTO tableDTO = new TableDTO();
        if (persistenceStructureService.isPersistable(tableClass)) {
            tableDTO.setCode(persistenceStructureService.getTableName(tableClass));
            tableDTO.setName(retrieveObjectLabel(tableClass));
            tableDTO.setFields(buildFieldDTOs(tableClass));
            tableDTO.setCollection(collection);
            return tableDTO;
        }
        return null;
    }

    protected List<FieldDTO> buildFieldDTOs(final Class<? extends PersistableBusinessObject> tableClass) {

        Map<Boolean, List<String>> partitionedFields = (Map<Boolean, List<String>>)persistenceStructureService
                                            .listFieldNames(tableClass)
                                            .stream()
                                            .collect(Collectors.partitioningBy(fieldName -> filteredFields.stream()
                                                                    .noneMatch(filteredField -> filteredField.matches(tableClass.getSimpleName(), (String)fieldName))));

        partitionedFields.get(false).stream().forEach(fieldName -> {
            if (isAnyClassField(fieldName)) {
                LOG.debug("Filtered out Field for " + tableClass.getSimpleName() + "." + fieldName);
            } else {
                LOG.warn("Filtered out Field for " + tableClass.getSimpleName() + "." + fieldName);
            }
        });

        return partitionedFields.get(true).stream().map(fieldName -> buildFieldDTO(tableClass, fieldName))
                                            .filter(fieldMap -> fieldMap != null)
                                            .distinct()
                                            .collect(Collectors.toList());
    }

    protected boolean isAnyClassField(String fieldName) {
        return filteredFields.stream().anyMatch(filteredField -> filteredField.matchesAnyClass() && StringUtils.equals(filteredField.getPropertyName(), fieldName));
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

    public List<DataDictionaryFilteredEntity> getFilteredEntities() {
        return filteredEntities;
    }

    public void setFilteredEntities(List<DataDictionaryFilteredEntity> filteredEntities) {
        this.filteredEntities = filteredEntities;
    }

    public List<DataDictionaryFilteredTable> getFilteredTables() {
        return filteredTables;
    }

    public void setFilteredTables(List<DataDictionaryFilteredTable> filteredTables) {
        this.filteredTables = filteredTables;
    }

    public List<DataDictionaryFilteredField> getFilteredFields() {
        return filteredFields;
    }

    public void setFilteredFields(List<DataDictionaryFilteredField> filteredFields) {
        this.filteredFields = filteredFields;
    }

    public List<String> getConcerns() {
        return concerns;
    }

    public void setConcerns(List<String> concerns) {
        this.concerns = concerns;
    }
}
