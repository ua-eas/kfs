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
import org.kuali.kfs.krad.datadictionary.DataDictionary;
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
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.BusinessObject;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SerializationService {
    public static final String FIELDS_KEY = "!topLevelFields!";
    public static final String COLLECTIONS_KEY = "!collectionsFields!";

    public static Map<String, Object> findBusinessObjectFields(MaintenanceDocumentEntry maintenanceDocumentEntry) {
        List<String> fields = new ArrayList<>();
        List<CollectionSerializationHelper> collectionSerializationHelpers = new ArrayList<>();
        List<MaintainableSectionDefinition> maintainableSections = maintenanceDocumentEntry.getMaintainableSections();
        for (MaintainableSectionDefinition section : maintainableSections) {
            List<MaintainableItemDefinition> itemDefinitions = section.getMaintainableItems();
            for(MaintainableItemDefinition itemDefinition : itemDefinitions) {
                if (itemDefinition instanceof MaintainableFieldDefinition) {
                    fields.add(itemDefinition.getName());
                } else if (itemDefinition instanceof MaintainableCollectionDefinition) {
                    collectionSerializationHelpers.add(buildCollectionSerializationHelper((MaintainableCollectionDefinition)itemDefinition));
                }
            }
        }

        Map<String, Object> businessObjectFieldsMap = businessObjectFieldsToMap(fields);
        if (collectionSerializationHelpers.size() > 0) {
            businessObjectFieldsMap.put(COLLECTIONS_KEY, collectionSerializationHelpers);
        }
        return businessObjectFieldsMap;
    }

    protected static CollectionSerializationHelper buildCollectionSerializationHelper(MaintainableCollectionDefinition maintainableCollectionDefinition) {
        CollectionSerializationHelper helper = new CollectionSerializationHelper(maintainableCollectionDefinition.getName(), maintainableCollectionDefinition.getBusinessObjectClass());
        for (MaintainableFieldDefinition fieldDefinition : maintainableCollectionDefinition.getMaintainableFields()) {
            helper.addField(fieldDefinition.getName());
        }
        return helper;
    }

    public static Map<String, Object> businessObjectFieldsToMap(List<String> fields) {
        Map<String, Object> fieldsMap = createBusinessObjectFieldsMap();
        for (String field: fields) {
            populateFieldsMapWithField(fieldsMap, field);
        }
        return fieldsMap;
    }

    protected static void populateFieldsMapWithField(Map<String, Object> fieldsMap, String field) {
        if (field.indexOf(".") < 0) {
            ((List<String>)fieldsMap.get(FIELDS_KEY)).add(field);
        } else {
            final String head = field.substring(0, field.indexOf('.'));
            final String tail = field.substring(field.indexOf('.') + 1);
            Map<String, Object> childFieldsMap = fieldsMap.containsKey(head)
                ? (Map<String, Object>)fieldsMap.get(head)
                : createBusinessObjectFieldsMap();
            fieldsMap.put(head, childFieldsMap);
            populateFieldsMapWithField(childFieldsMap, tail);
        }
    }

    protected static Map<String, Object> createBusinessObjectFieldsMap() {
        Map<String, Object> fieldsMap = new HashMap<>();
        fieldsMap.put(FIELDS_KEY, new ArrayList<String>());
        return fieldsMap;
    }

    public static <T extends PersistableBusinessObject> Map<String, Object> businessObjectToJson(Class<T> boClass, PersistableBusinessObject bo,
                                                                                                 Map<String, Object> fields,
                                                                                                 Person person,
                                                                                                 PersistenceStructureService persistenceStructureService,
                                                                                                 DataDictionaryService dataDictionaryService,
                                                                                                 BusinessObjectAuthorizationService businessObjectAuthorizationService,
                                                                                                 KualiModuleService kualiModuleService,
                                                                                                 ConfigurationService configurationService) {
        Map<String, Object> jsonObject = new LinkedHashMap<>();
        for (String key : fields.keySet()) {
            if (key.equals(FIELDS_KEY)) {
                for (String field : (List<String>)fields.get(FIELDS_KEY)) {
                    try {
                        Object value = PropertyUtils.getProperty(bo, field);
                        if (value != null) {
                            final Object possiblyMaskedJsonValue = maskJsonValueIfNecessary(boClass.getSimpleName(), field, value, person,
                                                                    dataDictionaryService, businessObjectAuthorizationService);
                            jsonObject.put(field, possiblyMaskedJsonValue);
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
                                        childBusinessObject, collectionSerializationHelper.getTranslatedFields(), person, persistenceStructureService,
                                        dataDictionaryService, businessObjectAuthorizationService, kualiModuleService, configurationService);
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
                            Map<String, Object> childSerialized = businessObjectToJson(childBusinessObject.getClass(),
                                childBusinessObject, (Map<String, Object>) fields.get(key), person, persistenceStructureService,
                                dataDictionaryService, businessObjectAuthorizationService, kualiModuleService, configurationService);
                            jsonObject.put(key, childSerialized);
                        }
                    }
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException("Could not retrieve child business object "+key, e);
                }
            }
        }

        jsonObject.put(KFSPropertyConstants.OBJECT_ID, bo.getObjectId());

        populateRelatedBusinessObjectFields(bo, jsonObject, kualiModuleService, configurationService, dataDictionaryService.getDataDictionary());
        return jsonObject;
    }

    protected static MaskFormatter buildMaskFormatter(AttributeDefinition attributeDefinition) {
        return (attributeDefinition.getAttributeSecurity().isMask())
            ? attributeDefinition.getAttributeSecurity().getMaskFormatter()
            : attributeDefinition.getAttributeSecurity().getPartialMaskFormatter();
    }

    protected static Object maskJsonValueIfNecessary(String businessObjectName, String attributeName, Object jsonValue,
                                                     Person person, DataDictionaryService dataDictionaryService,
                                                     BusinessObjectAuthorizationService businessObjectAuthorizationService) {
        final AttributeDefinition attributeDefinition = dataDictionaryService.getAttributeDefinition(businessObjectName, attributeName);
        if (attributeDefinition == null || attributeDefinition.getAttributeSecurity() == null || (!attributeDefinition.getAttributeSecurity().isMask() && !attributeDefinition.getAttributeSecurity().isPartialMask())) {
            return jsonValue;
        }
        final MaskFormatter maskFormatter = buildMaskFormatter(attributeDefinition);
        if (businessObjectAuthorizationService.isNonProductionEnvAndUnmaskingTurnedOff()) {
            return maskFormatter.maskValue(jsonValue); // it's non-production and unmasking is turned off, so let's always mask
        } else {
            final Class<? extends BusinessObject> businessObjectClass = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(businessObjectName).getBusinessObjectClass();
            return maskJsonValue(jsonValue, businessObjectClass, attributeDefinition, maskFormatter, person, businessObjectAuthorizationService);
        }
    }

    protected static boolean shouldMask(Class<? extends BusinessObject> businessObjectClass, AttributeDefinition attributeDefinition,
                                        Person person, BusinessObjectAuthorizationService businessObjectAuthorizationService) {
        return (attributeDefinition.getAttributeSecurity().isMask()
            && !businessObjectAuthorizationService.canFullyUnmaskField(person, businessObjectClass, attributeDefinition.getName(), null))
            ||
            (attributeDefinition.getAttributeSecurity().isPartialMask()
                && !businessObjectAuthorizationService.canPartiallyUnmaskField(person, businessObjectClass, attributeDefinition.getName(), null));
    }

    protected static Object maskJsonValue(Object jsonValue, Class<? extends BusinessObject> businessObjectClass,
                                          AttributeDefinition attributeDefinition, MaskFormatter maskFormatter,
                                          Person person, BusinessObjectAuthorizationService businessObjectAuthorizationService) {
        return (shouldMask(businessObjectClass, attributeDefinition, person, businessObjectAuthorizationService))
            ? maskFormatter.maskValue(jsonValue)
            : jsonValue;
    }

    protected static void populateRelatedBusinessObjectFields(PersistableBusinessObject businessObject, Map<String, Object> serializedBusinessObject, KualiModuleService kualiModuleService,
                                                              ConfigurationService configurationService, DataDictionary dataDictionary) {
        try {
            for (PropertyDescriptor propertyDescriptor : PropertyUtils.getPropertyDescriptors(businessObject)) {
                Method readMethod = propertyDescriptor.getReadMethod();
                if (readMethod != null && readMethod.getParameterCount() == 0 && Modifier.isPublic(readMethod.getModifiers())) {
                    final Class<?> propertyClass = propertyDescriptor.getPropertyType();

                    if (PersistableBusinessObject.class.isAssignableFrom(propertyClass)) {
                        Map<String, Object> linkObject = convertBoToUrl((PersistableBusinessObject)PropertyUtils.getProperty(businessObject, propertyDescriptor.getName()), kualiModuleService, configurationService, dataDictionary);
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
                                final Map<String, Object> memberMap = convertBoToUrl((PersistableBusinessObject)member, kualiModuleService, configurationService, dataDictionary);
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

    protected static Map<String, Object> convertBoToUrl(PersistableBusinessObject businessObject, KualiModuleService kualiModuleService, ConfigurationService configurationService, DataDictionary dataDictionary) {
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

        String urlBoName = lookupMaintenanceDocumentTypeForClass(ObjectUtils.materializeClassForProxiedObject(businessObject), dataDictionary);
        if (urlBoName == null) {
            return null;
        }

        Map<String, Object> result = new LinkedHashMap<>();
        String url = getBaseUrl(configurationService)
                + "/"
                + moduleName
                + "/api/v1/reference/"
                + urlBoName
                + "/"
                + businessObject.getObjectId();
        result.put(KFSPropertyConstants.LINK, url);

        return result;
    }

    protected static String getBaseUrl(ConfigurationService configurationService) {
        return configurationService.getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY);
    }

    protected static String getModuleName(ModuleService moduleService) {
        String moduleServiceName = moduleService.getModuleConfiguration().getNamespaceCode().toLowerCase();
        if (moduleServiceName.contains("-")) {
            moduleServiceName = StringUtils.substringAfter(moduleServiceName, "-");
        }
        return moduleServiceName;
    }

    protected static String lookupMaintenanceDocumentTypeForClass(Class clazz, DataDictionary dataDictionary) {
        MaintenanceDocumentEntry maintenanceDocumentEntry = (MaintenanceDocumentEntry) dataDictionary.getMaintenanceDocumentEntryForBusinessObjectClass(clazz);
        if (maintenanceDocumentEntry == null) {
            return null;
        }

        return maintenanceDocumentEntry.getDocumentTypeName().toLowerCase();
    }
}
