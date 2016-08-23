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
package org.kuali.kfs.krad.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.kns.service.BusinessObjectMetaDataService;
import org.kuali.kfs.krad.bo.DataObjectRelationship;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.datadictionary.BusinessObjectEntry;
import org.kuali.kfs.krad.datadictionary.DataDictionaryEntry;
import org.kuali.kfs.krad.datadictionary.DataObjectEntry;
import org.kuali.kfs.krad.datadictionary.PrimitiveAttributeDefinition;
import org.kuali.kfs.krad.datadictionary.RelationshipDefinition;
import org.kuali.kfs.krad.datadictionary.SupportAttributeDefinition;
import org.kuali.kfs.krad.service.DataDictionaryService;
import org.kuali.kfs.krad.service.DataObjectMetaDataService;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.krad.service.PersistenceStructureService;
import org.kuali.kfs.krad.uif.UifPropertyPaths;
import org.kuali.kfs.krad.uif.service.ViewDictionaryService;
import org.kuali.kfs.krad.uif.util.ObjectPropertyUtils;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.rice.krad.bo.BusinessObject;
import org.springframework.beans.BeanWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;


public class DataObjectMetaDataServiceImpl implements DataObjectMetaDataService {

    private DataDictionaryService dataDictionaryService;
    private KualiModuleService kualiModuleService;
    private PersistenceStructureService persistenceStructureService;
    private ViewDictionaryService viewDictionaryService;

    /**
     * @see DataObjectMetaDataService#listPrimaryKeyFieldNames(java.lang.Class)
     */
    @Override
    public List<String> listPrimaryKeyFieldNames(Class<?> clazz) {
        if (persistenceStructureService.isPersistable(clazz)) {
            return persistenceStructureService.listPrimaryKeyFieldNames(clazz);
        }

        ModuleService responsibleModuleService = getKualiModuleService().getResponsibleModuleService(clazz);
        if (responsibleModuleService != null && responsibleModuleService.isExternalizable(clazz)) {
            return responsibleModuleService.listPrimaryKeyFieldNames(clazz);
        }

        // check the Data Dictionary for PK's of non PBO/EBO
        List<String> pks = dataDictionaryService.getDataDictionary().getDataObjectEntry(clazz.getName())
            .getPrimaryKeys();
        if (pks != null && !pks.isEmpty()) {
            return pks;
        }

        return new ArrayList<String>();
    }

    /**
     * @see DataObjectMetaDataService#getPrimaryKeyFieldValues(java.lang.Object)
     */
    @Override
    public Map<String, ?> getPrimaryKeyFieldValues(Object dataObject) {
        return getPrimaryKeyFieldValues(dataObject, false);
    }

    /**
     * @see DataObjectMetaDataService#getPrimaryKeyFieldValues(java.lang.Object,
     * boolean)
     */
    @Override
    public Map<String, ?> getPrimaryKeyFieldValues(Object dataObject, boolean sortFieldNames) {
        Map<String, Object> keyValueMap;

        if (sortFieldNames) {
            keyValueMap = new TreeMap<String, Object>();
        } else {
            keyValueMap = new HashMap<String, Object>();
        }

        BeanWrapper wrapper = ObjectPropertyUtils.wrapObject(dataObject);

        List<String> fields = listPrimaryKeyFieldNames(dataObject.getClass());
        for (String fieldName : fields) {
            keyValueMap.put(fieldName, wrapper.getPropertyValue(fieldName));
        }

        return keyValueMap;
    }

    /**
     * @see DataObjectMetaDataService#equalsByPrimaryKeys(java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public boolean equalsByPrimaryKeys(Object do1, Object do2) {
        boolean equal = true;

        if (do1 == null && do2 == null) {
            equal = true;
        } else if (do1 == null || do2 == null) {
            equal = false;
        } else if (!do1.getClass().getName().equals(do2.getClass().getName())) {
            equal = false;
        } else {
            Map<String, ?> do1Keys = getPrimaryKeyFieldValues(do1);
            Map<String, ?> do2Keys = getPrimaryKeyFieldValues(do2);
            for (Iterator<String> iter = do1Keys.keySet().iterator(); iter.hasNext(); ) {
                String keyName = iter.next();
                if (do1Keys.get(keyName) != null && do2Keys.get(keyName) != null) {
                    if (!do1Keys.get(keyName).toString().equals(do2Keys.get(keyName).toString())) {
                        equal = false;
                    }
                } else {
                    equal = false;
                }
            }
        }

        return equal;
    }

    /**
     * @see BusinessObjectMetaDataService#getDataObjectRelationship(java.lang.Object,
     * java.lang.Class, java.lang.String, java.lang.String, boolean,
     * boolean, boolean)
     */
    public DataObjectRelationship getDataObjectRelationship(Object dataObject, Class<?> dataObjectClass,
                                                            String attributeName, String attributePrefix, boolean keysOnly, boolean supportsLookup,
                                                            boolean supportsInquiry) {
        RelationshipDefinition ddReference = getDictionaryRelationship(dataObjectClass, attributeName);

        return getDataObjectRelationship(ddReference, dataObject, dataObjectClass, attributeName, attributePrefix,
            keysOnly, supportsLookup, supportsInquiry);
    }

    protected DataObjectRelationship getDataObjectRelationship(RelationshipDefinition ddReference, Object dataObject,
                                                               Class<?> dataObjectClass, String attributeName, String attributePrefix, boolean keysOnly,
                                                               boolean supportsLookup, boolean supportsInquiry) {
        DataObjectRelationship relationship = null;

        // if it is nested then replace the data object and attributeName with the
        // sub-refs
        if (ObjectUtils.isNestedAttribute(attributeName)) {
            if (ddReference != null) {
                relationship = populateRelationshipFromDictionaryReference(dataObjectClass, ddReference,
                    attributePrefix, keysOnly);

                return relationship;
            }

            // recurse down to the next object to find the relationship
            String localPrefix = StringUtils.substringBefore(attributeName, ".");
            String localAttributeName = StringUtils.substringAfter(attributeName, ".");
            if (dataObject == null) {
                dataObject = ObjectUtils.createNewObjectFromClass(dataObjectClass);
            }

            Object nestedObject = ObjectPropertyUtils.getPropertyValue(dataObject, localPrefix);
            Class<?> nestedClass = null;
            if (nestedObject == null) {
                nestedClass = ObjectPropertyUtils.getPropertyType(dataObject, localPrefix);
            } else {
                nestedClass = nestedObject.getClass();
            }

            String fullPrefix = localPrefix;
            if (StringUtils.isNotBlank(attributePrefix)) {
                fullPrefix = attributePrefix + "." + localPrefix;
            }

            relationship = getDataObjectRelationship(nestedObject, nestedClass, localAttributeName, fullPrefix,
                keysOnly, supportsLookup, supportsInquiry);

            return relationship;
        }

        // non-nested reference, get persistence relationships first
        int maxSize = Integer.MAX_VALUE;

        // try persistable reference first
        if (getPersistenceStructureService().isPersistable(dataObjectClass)) {
            Map<String, DataObjectRelationship> rels = getPersistenceStructureService().getRelationshipMetadata(
                dataObjectClass, attributeName, attributePrefix);
            if (rels.size() > 0) {
                for (DataObjectRelationship rel : rels.values()) {
                    if (rel.getParentToChildReferences().size() < maxSize) {
                        maxSize = rel.getParentToChildReferences().size();
                        relationship = rel;
                    }
                }
            }
        } else {
            ModuleService moduleService = getKualiModuleService().getResponsibleModuleService(dataObjectClass);
            if (moduleService != null && moduleService.isExternalizable(dataObjectClass)) {
                relationship = getRelationshipMetadata(dataObjectClass, attributeName, attributePrefix);
                return relationship;
            }
        }

        if (ddReference != null && ddReference.getPrimitiveAttributes().size() < maxSize) {
            relationship = populateRelationshipFromDictionaryReference(dataObjectClass, ddReference, null,
                keysOnly);
        }

        return relationship;
    }

    public RelationshipDefinition getDictionaryRelationship(Class<?> c, String attributeName) {
        DataDictionaryEntry entryBase = getDataDictionaryService().getDataDictionary().getDictionaryObjectEntry(
            c.getName());
        if (entryBase == null) {
            return null;
        }

        RelationshipDefinition relationship = null;

        List<RelationshipDefinition> ddRelationships = entryBase.getRelationships();

        int minKeys = Integer.MAX_VALUE;
        for (RelationshipDefinition def : ddRelationships) {
            // favor key sizes of 1 first
            if (def.getPrimitiveAttributes().size() == 1) {
                for (PrimitiveAttributeDefinition primitive : def.getPrimitiveAttributes()) {
                    if (primitive.getSourceName().equals(attributeName) || def.getObjectAttributeName().equals(
                        attributeName)) {
                        relationship = def;
                        minKeys = 1;
                        break;
                    }
                }
            } else if (def.getPrimitiveAttributes().size() < minKeys) {
                for (PrimitiveAttributeDefinition primitive : def.getPrimitiveAttributes()) {
                    if (primitive.getSourceName().equals(attributeName) || def.getObjectAttributeName().equals(
                        attributeName)) {
                        relationship = def;
                        minKeys = def.getPrimitiveAttributes().size();
                        break;
                    }
                }
            }
        }

        // check the support attributes
        if (relationship == null) {
            for (RelationshipDefinition def : ddRelationships) {
                if (def.hasIdentifier()) {
                    if (def.getIdentifier().getSourceName().equals(attributeName)) {
                        relationship = def;
                    }
                }
            }
        }

        return relationship;
    }

    protected DataObjectRelationship populateRelationshipFromDictionaryReference(Class<?> dataObjectClass,
                                                                                 RelationshipDefinition ddReference, String attributePrefix, boolean keysOnly) {
        DataObjectRelationship relationship = new DataObjectRelationship(dataObjectClass,
            ddReference.getObjectAttributeName(), ddReference.getTargetClass());

        for (PrimitiveAttributeDefinition def : ddReference.getPrimitiveAttributes()) {
            if (StringUtils.isNotBlank(attributePrefix)) {
                relationship.getParentToChildReferences().put(attributePrefix + "." + def.getSourceName(),
                    def.getTargetName());
            } else {
                relationship.getParentToChildReferences().put(def.getSourceName(), def.getTargetName());
            }
        }

        if (!keysOnly) {
            for (SupportAttributeDefinition def : ddReference.getSupportAttributes()) {
                if (StringUtils.isNotBlank(attributePrefix)) {
                    relationship.getParentToChildReferences().put(attributePrefix + "." + def.getSourceName(),
                        def.getTargetName());
                    if (def.isIdentifier()) {
                        relationship.setUserVisibleIdentifierKey(attributePrefix + "." + def.getSourceName());
                    }
                } else {
                    relationship.getParentToChildReferences().put(def.getSourceName(), def.getTargetName());
                    if (def.isIdentifier()) {
                        relationship.setUserVisibleIdentifierKey(def.getSourceName());
                    }
                }
            }
        }

        return relationship;
    }

    protected DataObjectRelationship getRelationshipMetadata(Class<?> dataObjectClass, String attributeName,
                                                             String attributePrefix) {

        RelationshipDefinition relationshipDefinition = getDictionaryRelationship(dataObjectClass, attributeName);
        if (relationshipDefinition == null) {
            return null;
        }

        DataObjectRelationship dataObjectRelationship = new DataObjectRelationship(
            relationshipDefinition.getSourceClass(), relationshipDefinition.getObjectAttributeName(),
            relationshipDefinition.getTargetClass());

        if (!StringUtils.isEmpty(attributePrefix)) {
            attributePrefix += ".";
        }

        List<PrimitiveAttributeDefinition> primitives = relationshipDefinition.getPrimitiveAttributes();
        for (PrimitiveAttributeDefinition primitiveAttributeDefinition : primitives) {
            dataObjectRelationship.getParentToChildReferences().put(
                attributePrefix + primitiveAttributeDefinition.getSourceName(),
                primitiveAttributeDefinition.getTargetName());
        }

        return dataObjectRelationship;
    }

    /**
     * @see DataObjectMetaDataService#getTitleAttribute(java.lang.Class)
     */
    public String getTitleAttribute(Class<?> dataObjectClass) {
        String titleAttribute = null;

        DataObjectEntry entry = getDataObjectEntry(dataObjectClass);
        if (entry != null) {
            titleAttribute = entry.getTitleAttribute();
        }

        return titleAttribute;
    }

    /**
     * @see DataObjectMetaDataService#areNotesSupported(java.lang.Class)
     */
    @Override
    public boolean areNotesSupported(Class<?> dataObjectClass) {
        boolean hasNotesSupport = false;

        DataObjectEntry entry = getDataObjectEntry(dataObjectClass);
        if (entry != null) {
            hasNotesSupport = entry.isBoNotesEnabled();
        }

        return hasNotesSupport;
    }

    /**
     * @see DataObjectMetaDataService#getDataObjectIdentifierString
     */
    public String getDataObjectIdentifierString(Object dataObject) {
        String identifierString = "";

        if (dataObject == null) {
            identifierString = "Null";
            return identifierString;
        }

        Class<?> dataObjectClass = dataObject.getClass();

        // if PBO, use object id property
        if (PersistableBusinessObject.class.isAssignableFrom(dataObjectClass)) {
            String objectId = ObjectPropertyUtils.getPropertyValue(dataObject, UifPropertyPaths.OBJECT_ID);
            if (StringUtils.isBlank(objectId)) {
                objectId = UUID.randomUUID().toString();
                ObjectPropertyUtils.setPropertyValue(dataObject, UifPropertyPaths.OBJECT_ID, objectId);
            }

            identifierString = objectId;
        } else {
            // build identifier string from primary key values
            Map<String, ?> primaryKeyFieldValues = getPrimaryKeyFieldValues(dataObject, true);
            for (Map.Entry<String, ?> primaryKeyValue : primaryKeyFieldValues.entrySet()) {
                if (primaryKeyValue.getValue() == null) {
                    identifierString += "Null";
                } else {
                    identifierString += primaryKeyValue.getValue();
                }
                identifierString += ":";
            }
            identifierString = StringUtils.removeEnd(identifierString, ":");
        }

        return identifierString;
    }

    /**
     * @param dataObjectClass
     * @return DataObjectEntry for the given dataObjectClass, or null if
     * there is none
     * @throws IllegalArgumentException if the given Class is null
     */
    protected DataObjectEntry getDataObjectEntry(Class<?> dataObjectClass) {
        if (dataObjectClass == null) {
            throw new IllegalArgumentException("invalid (null) dataObjectClass");
        }

        DataObjectEntry entry = getDataDictionaryService().getDataDictionary().getDataObjectEntry(
            dataObjectClass.getName());

        return entry;
    }

    public List<DataObjectRelationship> getDataObjectRelationships(Class<?> dataObjectClass) {
        if (dataObjectClass == null) {
            return null;
        }

        Map<String, Class> referenceClasses = null;
        if (PersistableBusinessObject.class.isAssignableFrom(dataObjectClass)
            && getPersistenceStructureService().isPersistable(dataObjectClass)) {
            referenceClasses = getPersistenceStructureService().listReferenceObjectFields(dataObjectClass);
        }
        DataDictionaryEntry ddEntry = getDataDictionaryService().getDataDictionary().getDictionaryObjectEntry(
            dataObjectClass.getName());
        List<RelationshipDefinition> ddRelationships = (ddEntry == null ? new ArrayList<RelationshipDefinition>()
            : ddEntry.getRelationships());
        List<DataObjectRelationship> relationships = new ArrayList<DataObjectRelationship>();

        // loop over all relationships
        if (referenceClasses != null) {
            for (Map.Entry<String, Class> entry : referenceClasses.entrySet()) {
                Map<String, String> fkToPkRefs = getPersistenceStructureService().getForeignKeysForReference(dataObjectClass,
                    entry.getKey());
                DataObjectRelationship rel = new DataObjectRelationship(dataObjectClass, entry.getKey(),
                    entry.getValue());
                for (Map.Entry<String, String> ref : fkToPkRefs.entrySet()) {
                    rel.getParentToChildReferences().put(ref.getKey(), ref.getValue());
                }
                relationships.add(rel);
            }
        }

        for (RelationshipDefinition rd : ddRelationships) {
            DataObjectRelationship rel = new DataObjectRelationship(dataObjectClass, rd.getObjectAttributeName(),
                rd.getTargetClass());
            for (PrimitiveAttributeDefinition def : rd.getPrimitiveAttributes()) {
                rel.getParentToChildReferences().put(def.getSourceName(), def.getTargetName());
            }
            relationships.add(rel);
        }

        return relationships;
    }

    /**
     * @param businessObjectClass - class of business object to return entry for
     * @return BusinessObjectEntry for the given dataObjectClass, or null if
     * there is none
     */
    protected BusinessObjectEntry getBusinessObjectEntry(Class businessObjectClass) {
        validateBusinessObjectClass(businessObjectClass);

        BusinessObjectEntry entry = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(
            businessObjectClass.getName());
        return entry;
    }

    /**
     * @param businessObjectClass
     * @throws IllegalArgumentException if the given Class is null or is not a BusinessObject class
     */
    protected void validateBusinessObjectClass(Class businessObjectClass) {
        if (businessObjectClass == null) {
            throw new IllegalArgumentException("invalid (null) dataObjectClass");
        }
        if (!BusinessObject.class.isAssignableFrom(businessObjectClass)) {
            throw new IllegalArgumentException(
                "class '" + businessObjectClass.getName() + "' is not a descendant of BusinessObject");
        }
    }

    /**
     * Protected method to allow subclasses to access the dataDictionaryService.
     *
     * @return Returns the dataDictionaryService.
     */
    protected DataDictionaryService getDataDictionaryService() {
        return this.dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * Protected method to allow subclasses to access the kualiModuleService.
     *
     * @return Returns the persistenceStructureService.
     */
    protected KualiModuleService getKualiModuleService() {
        return this.kualiModuleService;
    }

    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }

    /**
     * Protected method to allow subclasses to access the
     * persistenceStructureService.
     *
     * @return Returns the persistenceStructureService.
     */
    protected PersistenceStructureService getPersistenceStructureService() {
        return this.persistenceStructureService;
    }

    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

    protected ViewDictionaryService getViewDictionaryService() {
        if (this.viewDictionaryService == null) {
            this.viewDictionaryService = KRADServiceLocatorWeb.getViewDictionaryService();
        }
        return this.viewDictionaryService;
    }

    public void setViewDictionaryService(ViewDictionaryService viewDictionaryService) {
        this.viewDictionaryService = viewDictionaryService;
    }

}
