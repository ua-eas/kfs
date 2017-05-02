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
package org.kuali.kfs.sys.rest.service;

import javassist.Modifier;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kuali.kfs.kns.datadictionary.MaintainableCollectionDefinition;
import org.kuali.kfs.kns.datadictionary.MaintainableFieldDefinition;
import org.kuali.kfs.kns.datadictionary.MaintainableItemDefinition;
import org.kuali.kfs.kns.datadictionary.MaintainableSectionDefinition;
import org.kuali.kfs.kns.datadictionary.MaintenanceDocumentEntry;
import org.kuali.kfs.kns.service.BusinessObjectAuthorizationService;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.datadictionary.AttributeDefinition;
import org.kuali.kfs.krad.datadictionary.mask.MaskFormatter;
import org.kuali.kfs.krad.service.DataDictionaryService;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.krad.service.PersistenceStructureService;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.rest.helper.CollectionSerializationHelper;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.BusinessObject;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SerializationService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SerializationService.class);

    public static final String FIELDS_KEY = "!topLevelFields!";
    public static final String COLLECTIONS_KEY = "!collectionsFields!";

    public static final List<String> UNSERIALIZABLE_FIELDS = Arrays.asList("newCollectionRecord");

    private PersistenceStructureService persistenceStructureService;
    private DataDictionaryService dataDictionaryService;
    private BusinessObjectAuthorizationService businessObjectAuthorizationService;
    private KualiModuleService kualiModuleService;
    private ConfigurationService configurationService;

    public List<String> getBusinessObjectFieldList(MaintenanceDocumentEntry maintenanceDocumentEntry) {
        LOG.debug("getBusinessObjectFieldList() started");

        List<String> fields = new ArrayList<>();
        List<MaintainableSectionDefinition> maintainableSections = maintenanceDocumentEntry.getMaintainableSections();
        for (MaintainableSectionDefinition section : maintainableSections) {
            List<MaintainableItemDefinition> itemDefinitions = section.getMaintainableItems();
            for(MaintainableItemDefinition itemDefinition : itemDefinitions) {
                if (itemDefinition instanceof MaintainableFieldDefinition) {
                    fields.add(itemDefinition.getName());
                }
            }
        }
        fields.add(KFSPropertyConstants.LAST_UPDATED_TIMESTAMP);
        return fields;
    }

    private List<CollectionSerializationHelper> getBusinessObjectCollections(MaintenanceDocumentEntry maintenanceDocumentEntry) {
        List<CollectionSerializationHelper> collectionSerializationHelpers = new ArrayList<>();
        List<MaintainableSectionDefinition> maintainableSections = maintenanceDocumentEntry.getMaintainableSections();
        for (MaintainableSectionDefinition section : maintainableSections) {
            List<MaintainableItemDefinition> itemDefinitions = section.getMaintainableItems();
            for(MaintainableItemDefinition itemDefinition : itemDefinitions) {
                if (itemDefinition instanceof MaintainableCollectionDefinition) {
                    collectionSerializationHelpers.add(buildCollectionSerializationHelper((MaintainableCollectionDefinition)itemDefinition));
                }
            }
        }
        return collectionSerializationHelpers;
    }

    public Map<String, Object> findBusinessObjectFields(MaintenanceDocumentEntry maintenanceDocumentEntry) {
        LOG.debug("findBusinessObjectFields() started");

        List<String> fields = getBusinessObjectFieldList(maintenanceDocumentEntry);
        List<CollectionSerializationHelper> collectionSerializationHelpers = getBusinessObjectCollections(maintenanceDocumentEntry);

        Map<String, Object> businessObjectFieldsMap = businessObjectFieldsToMap(fields);
        if (collectionSerializationHelpers.size() > 0) {
            collectionsToMap(businessObjectFieldsMap, collectionSerializationHelpers);
        }

        return businessObjectFieldsMap;
    }

    protected CollectionSerializationHelper buildCollectionSerializationHelper(MaintainableCollectionDefinition maintainableCollectionDefinition) {
        CollectionSerializationHelper helper = new CollectionSerializationHelper(maintainableCollectionDefinition.getName(),
            maintainableCollectionDefinition.getBusinessObjectClass(), this);
        for (MaintainableFieldDefinition fieldDefinition : maintainableCollectionDefinition.getMaintainableFields()) {
            if (!UNSERIALIZABLE_FIELDS.contains(fieldDefinition.getName())) {
                helper.addField(fieldDefinition.getName());
            }
        }
        for (MaintainableCollectionDefinition collectionDefinition : maintainableCollectionDefinition
            .getMaintainableCollections()) {
            helper.addCollectionSerializationHelper(buildCollectionSerializationHelper(collectionDefinition));
        }
        return helper;
    }

    public Map<String, Object> businessObjectFieldsToMap(List<String> fields) {
        LOG.debug("businessObjectFieldsToMap() started");

        Map<String, Object> fieldsMap = createBusinessObjectFieldsMap();
        for (String field: fields) {
            populateFieldsMapWithField(fieldsMap, field);
        }
        return fieldsMap;
    }

    protected void populateFieldsMapWithField(Map<String, Object> fieldsMap, String field) {
        if (!UNSERIALIZABLE_FIELDS.contains(field)) {
            if (field.indexOf(".") < 0) {
                ((List<String>) fieldsMap.get(FIELDS_KEY)).add(field);
            } else {
                final String head = field.substring(0, field.indexOf('.'));
                final String tail = field.substring(field.indexOf('.') + 1);
                Map<String, Object> childFieldsMap = fieldsMap.containsKey(head)
                    ? (Map<String, Object>) fieldsMap.get(head)
                    : createBusinessObjectFieldsMap();
                fieldsMap.put(head, childFieldsMap);
                populateFieldsMapWithField(childFieldsMap, tail);
            }
        }
    }

    protected Map<String, Object> createBusinessObjectFieldsMap() {
        Map<String, Object> fieldsMap = new HashMap<>();
        fieldsMap.put(FIELDS_KEY, new ArrayList<String>());
        return fieldsMap;
    }

    public Map<String, Object> collectionsToMap(Map<String, Object> fieldsMap, List<CollectionSerializationHelper> serializationHelpers) {
        for (CollectionSerializationHelper helper: serializationHelpers) {
            populateFieldsMapWithCollection(fieldsMap, helper);
        }
        return fieldsMap;
    }

    protected void populateFieldsMapWithCollection(Map<String, Object> fieldsMap, CollectionSerializationHelper collection) {
        String collectionName = collection.getCollectionName();
        if (collectionName.indexOf(".") < 0) {
            if (!fieldsMap.containsKey(COLLECTIONS_KEY)) {
                fieldsMap.put(COLLECTIONS_KEY, new ArrayList<CollectionSerializationHelper>());
            }
            ((List<CollectionSerializationHelper>) fieldsMap.get(COLLECTIONS_KEY)).add(collection);
        } else {
            final String head = collectionName.substring(0, collectionName.indexOf('.'));
            final String tail = collectionName.substring(collectionName.indexOf('.') + 1);
            Map<String, Object> childCollectionsMap = fieldsMap.containsKey(head)
                ? (Map<String, Object>)fieldsMap.get(head)
                : createBusinessObjectCollectionsMap();
            fieldsMap.put(head, childCollectionsMap);
            collection.setCollectionName(tail);
            populateFieldsMapWithCollection(childCollectionsMap, collection);
        }
    }

    protected Map<String, Object> createBusinessObjectCollectionsMap() {
        Map<String, Object> collectionsMap = new HashMap<>();
        collectionsMap.put(COLLECTIONS_KEY, new ArrayList<CollectionSerializationHelper>());
        return collectionsMap;
    }

    public Map<String, Object> businessObjectToJson(Class<? extends PersistableBusinessObject> boClass,
        PersistableBusinessObject bo, Map<String, Object> fields, Person person) {
        return businessObjectToJson(boClass, boClass, bo, fields, person, "");
    }

    public Map<String, Object> businessObjectToJson(Class<? extends PersistableBusinessObject> parentBoClass,
        Class<? extends PersistableBusinessObject> boClass, PersistableBusinessObject bo, Map<String, Object> fields,
        Person person, String parentField) {
        if (StringUtils.isNotBlank(parentField)) {
            parentField += ".";
        }
        Map<String, Object> jsonObject = new LinkedHashMap<>();
        for (String key : fields.keySet()) {
            if (key.equals(FIELDS_KEY)) {
                for (String field : (List<String>)fields.get(FIELDS_KEY)) {
                    try {
                        Object value = PropertyUtils.getProperty(bo, field);
                        if (value != null) {
                            final Object possiblyMaskedJsonValue = maskJsonValueIfNecessary(
                                parentBoClass.getSimpleName(), parentField + field, value, person);
                            jsonObject.put(field, possiblyMaskedJsonValue);
                        } else {
                            jsonObject.put(field, null);
                        }
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        throw new RuntimeException("Failed to get " + field + " from business object", e);
                    }
                }
            } else if (key.equals(COLLECTIONS_KEY)) {
                List<CollectionSerializationHelper> collectionSerializationHelpers = (List<CollectionSerializationHelper>)fields.get(COLLECTIONS_KEY);
                for (CollectionSerializationHelper collectionSerializationHelper : collectionSerializationHelpers) {
                    String name = collectionSerializationHelper.getCollectionName();
                    try {
                        if (persistenceStructureService.hasReference(boClass, name)) {
                            bo.refreshReferenceObject(name);
                        }
                        PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(bo, name);
                        if (Collection.class.isAssignableFrom(descriptor.getPropertyType())) {
                            Collection<PersistableBusinessObject> childrenBusinessObjects = (Collection<PersistableBusinessObject>) PropertyUtils.getProperty(bo, name);
                            if (ObjectUtils.isNotNull(childrenBusinessObjects) && childrenBusinessObjects.size() > 0) {
                                List<Map<String, Object>> collectionJsonObjects = new ArrayList<>();
                                for (PersistableBusinessObject childBusinessObject : childrenBusinessObjects) {
                                    Map<String, Object> childSerialized = businessObjectToJson(childBusinessObject.getClass(),
                                        childBusinessObject, collectionSerializationHelper.getTranslatedFields(), person);
                                    collectionJsonObjects.add(childSerialized);
                                }
                                jsonObject.put(name, collectionJsonObjects);
                            }
                        }
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        throw new RuntimeException("Could not retrieve child business object "+key, e);
                    }
                }
            } else {
                try {
                    if (persistenceStructureService.hasReference(boClass, key)) {
                        bo.refreshReferenceObject(key);
                    }
                    PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(bo, key);
                    if (PersistableBusinessObject.class.isAssignableFrom(descriptor.getPropertyType())) {
                        PersistableBusinessObject childBusinessObject = (PersistableBusinessObject) PropertyUtils.getProperty(bo, key);
                        if (!ObjectUtils.isNull(childBusinessObject)) {
                            Map<String, Object> childSerialized = businessObjectToJson(parentBoClass, childBusinessObject.getClass(),
                                childBusinessObject, (Map<String, Object>) fields.get(key), person, parentField + key);
                            jsonObject.put(key, childSerialized);
                        }
                    }
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException("Could not retrieve child business object "+key, e);
                }
            }
        }

        jsonObject.put(KFSPropertyConstants.OBJECT_ID, bo.getObjectId());

        populateRelatedBusinessObjectFields(bo, jsonObject);
        return jsonObject;
    }

    protected MaskFormatter buildMaskFormatter(AttributeDefinition attributeDefinition) {
        return (attributeDefinition.getAttributeSecurity().isMask())
            ? attributeDefinition.getAttributeSecurity().getMaskFormatter()
            : attributeDefinition.getAttributeSecurity().getPartialMaskFormatter();
    }

    protected Object maskJsonValueIfNecessary(String businessObjectName, String attributeName, Object jsonValue, Person person) {
        final AttributeDefinition attributeDefinition = dataDictionaryService.getAttributeDefinition(businessObjectName, attributeName);
        if (attributeDefinition == null || attributeDefinition.getAttributeSecurity() == null
                || (!attributeDefinition.getAttributeSecurity().isMask() && !attributeDefinition.getAttributeSecurity().isPartialMask())) {
            return jsonValue;
        }
        final MaskFormatter maskFormatter = buildMaskFormatter(attributeDefinition);
        if (businessObjectAuthorizationService.isNonProductionEnvAndUnmaskingTurnedOff()) {
            return maskFormatter.maskValue(jsonValue); // it's non-production and unmasking is turned off, so let's always mask
        } else {
            final Class<? extends BusinessObject> businessObjectClass = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(businessObjectName).getBusinessObjectClass();
            return maskJsonValue(jsonValue, businessObjectClass, attributeDefinition, maskFormatter, person);
        }
    }

    protected boolean shouldMask(Class<? extends BusinessObject> businessObjectClass, AttributeDefinition attributeDefinition, Person person) {
        return (attributeDefinition.getAttributeSecurity().isMask()
            && !businessObjectAuthorizationService.canFullyUnmaskField(person, businessObjectClass, attributeDefinition.getName(), null))
            ||
            (attributeDefinition.getAttributeSecurity().isPartialMask()
                && !businessObjectAuthorizationService.canPartiallyUnmaskField(person, businessObjectClass, attributeDefinition.getName(), null));
    }

    protected Object maskJsonValue(Object jsonValue, Class<? extends BusinessObject> businessObjectClass, AttributeDefinition attributeDefinition, MaskFormatter maskFormatter, Person person) {
        return (shouldMask(businessObjectClass, attributeDefinition, person))
            ? maskFormatter.maskValue(jsonValue)
            : jsonValue;
    }

    protected void populateRelatedBusinessObjectFields(PersistableBusinessObject businessObject, Map<String, Object> serializedBusinessObject) {
        try {
            for (PropertyDescriptor propertyDescriptor : PropertyUtils.getPropertyDescriptors(businessObject)) {
                Method readMethod = propertyDescriptor.getReadMethod();
                if (readMethod != null && readMethod.getParameterCount() == 0 && Modifier.isPublic(readMethod.getModifiers())) {
                    final Class<?> propertyClass = propertyDescriptor.getPropertyType();

                    if (PersistableBusinessObject.class.isAssignableFrom(propertyClass)) {
                        Map<String, Object> linkObject = convertBoToUrl((PersistableBusinessObject)PropertyUtils.getProperty(businessObject, propertyDescriptor.getName()));
                        if (linkObject != null) {
                            if (serializedBusinessObject.containsKey(propertyDescriptor.getName())) {
                                ((Map<String, Object>)serializedBusinessObject.get(propertyDescriptor.getName())).putAll(linkObject);
                            } else {
                                serializedBusinessObject.put(propertyDescriptor.getName(), linkObject);
                            }
                        }
                    } else if (List.class.isAssignableFrom(propertyClass)) {
                        final List<?> collection = (List<?>)PropertyUtils.getProperty(businessObject, propertyDescriptor.getName());
                        if (!CollectionUtils.isEmpty(collection) && collection.get(0) instanceof BusinessObject) {
                            List<Map<String, Object>> serializedCollection = serializedBusinessObject.containsKey(propertyDescriptor.getName())
                                                                             ? (List<Map<String, Object>>) serializedBusinessObject.get(propertyDescriptor.getName())
                                                                             : new ArrayList<>();
                            for (int i = 0; i < collection.size(); i++) {
                                final Object member = collection.get(i);
                                final Map<String, Object> memberMap = convertBoToUrl((PersistableBusinessObject)member);
                                if (memberMap != null) {
                                    if (i < serializedCollection.size() && serializedCollection.get(i) != null) {
                                        serializedCollection.get(i).putAll(memberMap);
                                    } else {
                                        serializedCollection.add(memberMap);
                                    }
                                }
                            }
                            serializedBusinessObject.put(propertyDescriptor.getName(), serializedCollection);
                        }
                    }
                }
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    protected Map<String, Object> convertBoToUrl(PersistableBusinessObject businessObject) {
        if (ObjectUtils.isNull(businessObject)) {
            return null;
        }

        String objectID = businessObject.getObjectId();
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

        String urlBoName = lookupMaintenanceDocumentTypeForClass(ObjectUtils.materializeClassForProxiedObject(businessObject));
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
                + businessObject.getObjectId();
        result.put(KFSPropertyConstants.LINK, url);

        return result;
    }

    protected String getBaseUrl() {
        return configurationService.getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY);
    }

    protected static String getModuleName(ModuleService moduleService) {
        String moduleServiceName = moduleService.getModuleConfiguration().getNamespaceCode().toLowerCase();
        if (moduleServiceName.contains("-")) {
            moduleServiceName = StringUtils.substringAfter(moduleServiceName, "-");
        }
        return moduleServiceName;
    }

    protected String lookupMaintenanceDocumentTypeForClass(Class clazz) {
        MaintenanceDocumentEntry maintenanceDocumentEntry = (MaintenanceDocumentEntry) getDataDictionaryService().getDataDictionary().getMaintenanceDocumentEntryForBusinessObjectClass(clazz);
        if (maintenanceDocumentEntry == null) {
            return null;
        }

        return maintenanceDocumentEntry.getDocumentTypeName().toLowerCase();
    }

    public PersistenceStructureService getPersistenceStructureService() {
        return persistenceStructureService;
    }

    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    public BusinessObjectAuthorizationService getBusinessObjectAuthorizationService() {
        return businessObjectAuthorizationService;
    }

    public void setBusinessObjectAuthorizationService(BusinessObjectAuthorizationService businessObjectAuthorizationService) {
        this.businessObjectAuthorizationService = businessObjectAuthorizationService;
    }

    public KualiModuleService getKualiModuleService() {
        return kualiModuleService;
    }

    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
