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
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.ClassLoaderUtils;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.kfs.krad.datadictionary.AttributeDefinition;
import org.kuali.kfs.krad.datadictionary.AttributeSecurity;
import org.kuali.kfs.krad.datadictionary.BusinessObjectEntry;
import org.kuali.kfs.krad.datadictionary.CollectionDefinition;
import org.kuali.kfs.krad.datadictionary.DataDictionary;
import org.kuali.kfs.krad.datadictionary.DataDictionaryEntryBase;
import org.kuali.kfs.krad.datadictionary.DataObjectEntry;
import org.kuali.kfs.krad.datadictionary.DocumentEntry;
import org.kuali.kfs.krad.datadictionary.InactivationBlockingMetadata;
import org.kuali.kfs.krad.datadictionary.PrimitiveAttributeDefinition;
import org.kuali.kfs.krad.datadictionary.RelationshipDefinition;
import org.kuali.kfs.krad.datadictionary.control.ControlDefinition;
import org.kuali.kfs.krad.datadictionary.exception.UnknownBusinessClassAttributeException;
import org.kuali.kfs.krad.datadictionary.exception.UnknownDocumentTypeException;
import org.kuali.kfs.krad.datadictionary.validation.ValidationPattern;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.keyvalues.KeyValuesFinder;
import org.kuali.kfs.krad.service.DataDictionaryService;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.uif.view.View;
import org.kuali.kfs.krad.uif.UifConstants.ViewType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Service implementation for a DataDictionary. It is a thin wrapper around creating, initializing, and
 * returning a DataDictionary. This is the default, Kuali delivered implementation
 *
 *
 */
public class DataDictionaryServiceImpl implements DataDictionaryService {
    private static final Logger LOG = Logger.getLogger(DataDictionaryServiceImpl.class);

    private DataDictionary dataDictionary;

    private ConfigurationService kualiConfigurationService;
    private KualiModuleService kualiModuleService;
    private volatile DocumentTypeService documentTypeService;

    /**
     * @see DataDictionaryService#setBaselinePackages(java.lang.String)
     */
    public void setBaselinePackages(List baselinePackages) throws IOException {
        this.addDataDictionaryLocations(baselinePackages);
    }

    /**
     * Default constructor.
     */
    public DataDictionaryServiceImpl() {
        this.dataDictionary = new DataDictionary();
    }

    public DataDictionaryServiceImpl(DataDictionary dataDictionary) {
        this.dataDictionary = dataDictionary;
    }

    /**
     * @see DataDictionaryService#getDataDictionary()
     */
    public DataDictionary getDataDictionary() {
        return dataDictionary;
    }

    /**
     * @see DataDictionaryService#getAttributeControlDefinition(java.lang.String)
     */
    public ControlDefinition getAttributeControlDefinition(String entryName, String attributeName) {
        ControlDefinition controlDefinition = null;

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            controlDefinition = attributeDefinition.getControl();
        }

        return controlDefinition;
    }

    /**
     * @see DataDictionaryService#getAttributeSize(java.lang.String)
     */
    public Integer getAttributeSize(String entryName, String attributeName) {
        Integer size = null;

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            ControlDefinition controlDefinition = attributeDefinition.getControl();
            if (controlDefinition.isText() || controlDefinition.isCurrency()) {
                size = controlDefinition.getSize();
            }
        }

        return size;
    }

    /**
     * @see DataDictionaryService#getAttributeMinLength(java.lang.String)
     */
    public Integer getAttributeMinLength(String entryName, String attributeName) {
        Integer minLength = null;

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            minLength = attributeDefinition.getMinLength();
        }

        return minLength;
    }

    /**
     * @see DataDictionaryService#getAttributeMaxLength(java.lang.String)
     */
    public Integer getAttributeMaxLength(String entryName, String attributeName) {
        Integer maxLength = null;

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            maxLength = attributeDefinition.getMaxLength();
        }

        return maxLength;
    }

    /**
     * @see DataDictionaryService#getAttributeExclusiveMin
     */
    public String getAttributeExclusiveMin(String entryName, String attributeName) {
        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        return attributeDefinition == null ? null : attributeDefinition.getExclusiveMin();
    }

    /**
     * @see DataDictionaryService#getAttributeInclusiveMax
     */
    public String getAttributeInclusiveMax(String entryName, String attributeName) {
        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        return attributeDefinition == null ? null : attributeDefinition.getInclusiveMax();
    }

    /**
     * @see DataDictionaryService#getAttributeValidatingExpression(java.lang.String)
     */
    public Pattern getAttributeValidatingExpression(String entryName, String attributeName) {
        Pattern regex = null;

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            if (attributeDefinition.hasValidationPattern()) {
                regex = attributeDefinition.getValidationPattern().getRegexPattern();
            } else {
                // workaround for existing calls which don't bother checking for null return values
                regex = Pattern.compile(".*");
            }
        }

        return regex;
    }

    /**
     * @see DataDictionaryService#getAttributeLabel(java.lang.String)
     */
    public String getAttributeLabel(String entryName, String attributeName) {
        String label = "";

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            // KULRICE-4445 prevent NullPointerException by ensuring a label is set
            label = attributeDefinition.getLabel();
            if (!StringUtils.isEmpty(attributeDefinition.getDisplayLabelAttribute())) {
                attributeDefinition = getAttributeDefinition(entryName, attributeDefinition.getDisplayLabelAttribute());
                if (attributeDefinition != null) {
                    label = attributeDefinition.getLabel();
                }
            }
        }

        return label;
    }

    /**
     * @see DataDictionaryService#getAttributeShortLabel(java.lang.String)
     */
    public String getAttributeShortLabel(String entryName, String attributeName) {
        String shortLabel = "";

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            if (!StringUtils.isEmpty(attributeDefinition.getDisplayLabelAttribute())) {
                attributeDefinition = getAttributeDefinition(entryName, attributeDefinition.getDisplayLabelAttribute());
                if (attributeDefinition != null) {
                    shortLabel = attributeDefinition.getShortLabel();
                }
            } else {
                shortLabel = attributeDefinition.getShortLabel();
            }
        }

        return shortLabel;
    }

    /**
     * @see DataDictionaryService#getAttributeErrorLabel(java.lang.String)
     */
    public String getAttributeErrorLabel(String entryName, String attributeName) {
        String longAttributeLabel = this.getAttributeLabel(entryName, attributeName);
        String shortAttributeLabel = this.getAttributeShortLabel(entryName, attributeName);
        return longAttributeLabel + " (" + shortAttributeLabel + ")";
    }

    /**
     * @see DataDictionaryService#getAttributeFormatter(java.lang.String)
     */
    public Class<? extends Formatter> getAttributeFormatter(String entryName, String attributeName) {
        Class formatterClass = null;

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            if (attributeDefinition.hasFormatterClass()) {
                formatterClass = ClassLoaderUtils.getClass(attributeDefinition.getFormatterClass());
            }
        }

        return formatterClass;
    }

    /**
     * @see DataDictionaryService#getAttributeForceUppercase(java.lang.String)
     */
    public Boolean getAttributeForceUppercase(String entryName,
            String attributeName) throws UnknownBusinessClassAttributeException {
        Boolean forceUppercase = null;

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition == null) {
            throw new UnknownBusinessClassAttributeException(
                    "Could not find a matching data dictionary business class attribute entry for " + entryName + "." +
                            attributeName);
        }
        forceUppercase = attributeDefinition.getForceUppercase();

        return forceUppercase;
    }

    /**
     * @see DataDictionaryService#getAttributeDisplayMask(java.lang.String, java.lang.String)
     */
    public AttributeSecurity getAttributeSecurity(String entryName, String attributeName) {
        AttributeSecurity attributeSecurity = null;

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            attributeSecurity = attributeDefinition.getAttributeSecurity();
        }

        return attributeSecurity;
    }

    /**
     * @see DataDictionaryService#getAttributeSummary(java.lang.String)
     */
    public String getAttributeSummary(String entryName, String attributeName) {
        String summary = null;

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            summary = attributeDefinition.getSummary();
        }

        return summary;
    }

    /**
     * @see DataDictionaryService#getAttributeDescription(java.lang.String)
     */
    public String getAttributeDescription(String entryName, String attributeName) {
        String description = null;

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            description = attributeDefinition.getDescription();
        }

        return description;
    }

    /**
     * @see DataDictionaryService#isAttributeRequired(java.lang.Class, java.lang.String)
     */
    public Boolean isAttributeRequired(String entryName, String attributeName) {
        Boolean required = null;

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            required = attributeDefinition.isRequired();
        }

        return required;
    }

    /**
     * @see DataDictionaryService#isAttributeDefined(java.lang.Class, java.lang.String)
     */
    public Boolean isAttributeDefined(String entryName, String attributeName) {
        boolean isDefined = false;

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            isDefined = true;
        }

        return isDefined;
    }

    /**
     * @see DataDictionaryService#getAttributeValuesScopeId(java.lang.Class,
     *      java.lang.String)
     */
    public Class<? extends KeyValuesFinder> getAttributeValuesFinderClass(String entryName, String attributeName) {
        Class valuesFinderClass = null;

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            String valuesFinderClassName = attributeDefinition.getControl().getValuesFinderClass();
            valuesFinderClass = ClassLoaderUtils.getClass(valuesFinderClassName);
        }

        return valuesFinderClass;
    }

    /**
     * @see DataDictionaryService#getCollectionLabel(java.lang.Class, java.lang.String)
     */
    public String getCollectionLabel(String entryName, String collectionName) {
        String label = "";

        CollectionDefinition collectionDefinition = getCollectionDefinition(entryName, collectionName);
        if (collectionDefinition != null) {
            label = collectionDefinition.getLabel();
        }

        return label;
    }

    /**
     * @see DataDictionaryService#getCollectionShortLabel(java.lang.Class, java.lang.String)
     */
    public String getCollectionShortLabel(String entryName, String collectionName) {
        String shortLabel = "";

        CollectionDefinition collectionDefinition = getCollectionDefinition(entryName, collectionName);
        if (collectionDefinition != null) {
            shortLabel = collectionDefinition.getShortLabel();
        }

        return shortLabel;
    }

    /**
     * @see DataDictionaryService#getCollectionElementLabel(java.lang.Class,
     *      java.lang.String)
     */
    public String getCollectionElementLabel(String entryName, String collectionName, Class dataObjectClass) {
        String elementLabel = "";

        CollectionDefinition collectionDefinition = getCollectionDefinition(entryName, collectionName);
        if (collectionDefinition != null) {
            elementLabel = collectionDefinition.getElementLabel();
            if (StringUtils.isEmpty(elementLabel)) {
                BusinessObjectEntry boe = getDataDictionary().getBusinessObjectEntry(dataObjectClass.getName());
                if (boe != null) {
                    elementLabel = boe.getObjectLabel();
                }
            }
        }

        return elementLabel;
    }

    /**
     * @see DataDictionaryService#getCollectionSummary(java.lang.Class, java.lang.String)
     */
    public String getCollectionSummary(String entryName, String collectionName) {
        String summary = null;

        CollectionDefinition collectionDefinition = getCollectionDefinition(entryName, collectionName);
        if (collectionDefinition != null) {
            summary = collectionDefinition.getSummary();
        }

        return summary;
    }

    /**
     * @see DataDictionaryService#getCollectionDescription(java.lang.Class,
     *      java.lang.String)
     */
    public String getCollectionDescription(String entryName, String collectionName) {
        String description = null;

        CollectionDefinition collectionDefinition = getCollectionDefinition(entryName, collectionName);
        if (collectionDefinition != null) {
            description = collectionDefinition.getDescription();
        }

        return description;
    }

    public Class<? extends BusinessObject> getRelationshipSourceClass(String entryName, String relationshipName) {
        Class sourceClass = null;

        RelationshipDefinition rd = getRelationshipDefinition(entryName, relationshipName);
        if (rd != null) {
            sourceClass = rd.getSourceClass();
        }

        return sourceClass;
    }

    public Class<? extends BusinessObject> getRelationshipTargetClass(String entryName, String relationshipName) {
        Class targetClass = null;

        RelationshipDefinition rd = getRelationshipDefinition(entryName, relationshipName);
        if (rd != null) {
            targetClass = rd.getTargetClass();
        }

        return targetClass;
    }

    public List<String> getRelationshipSourceAttributes(String entryName, String relationshipName) {
        List<String> sourceAttributes = null;

        RelationshipDefinition rd = getRelationshipDefinition(entryName, relationshipName);
        if (rd != null) {
            sourceAttributes = new ArrayList<String>();

            for (PrimitiveAttributeDefinition pad : rd.getPrimitiveAttributes()) {
                sourceAttributes.add(pad.getSourceName());
            }
        }

        return sourceAttributes;
    }

    public List<String> getRelationshipTargetAttributes(String entryName, String relationshipName) {
        List<String> targetAttributes = null;

        RelationshipDefinition rd = getRelationshipDefinition(entryName, relationshipName);
        if (rd != null) {
            targetAttributes = new ArrayList<String>();

            for (PrimitiveAttributeDefinition pad : rd.getPrimitiveAttributes()) {
                targetAttributes.add(pad.getTargetName());
            }
        }

        return targetAttributes;
    }

    public List<String> getRelationshipEntriesForSourceAttribute(String entryName, String sourceAttributeName) {
        List<String> relationships = new ArrayList<String>();

        DataDictionaryEntryBase entry =
                (DataDictionaryEntryBase) getDataDictionary().getDictionaryObjectEntry(entryName);

        for (RelationshipDefinition def : entry.getRelationships()) {
            for (PrimitiveAttributeDefinition pddef : def.getPrimitiveAttributes()) {
                if (StringUtils.equals(sourceAttributeName, pddef.getSourceName())) {
                    relationships.add(def.getObjectAttributeName());
                    break;
                }
            }
        }
        return relationships;
    }

    public List<String> getRelationshipEntriesForTargetAttribute(String entryName, String targetAttributeName) {
        List<String> relationships = new ArrayList<String>();

        DataDictionaryEntryBase entry =
                (DataDictionaryEntryBase) getDataDictionary().getDictionaryObjectEntry(entryName);

        for (RelationshipDefinition def : entry.getRelationships()) {
            for (PrimitiveAttributeDefinition pddef : def.getPrimitiveAttributes()) {
                if (StringUtils.equals(targetAttributeName, pddef.getTargetName())) {
                    relationships.add(def.getObjectAttributeName());
                    break;
                }
            }
        }
        return relationships;
    }

    /**
     * @param objectClass
     * @param attributeName
     * @return AttributeDefinition for the given dataObjectClass and attribute name, or null if there is none
     * @throws IllegalArgumentException if the given Class is null or is not a BusinessObject class
     */
    public AttributeDefinition getAttributeDefinition(String entryName, String attributeName) {
        if (StringUtils.isBlank(attributeName)) {
            throw new IllegalArgumentException("invalid (blank) attributeName");
        }
        AttributeDefinition attributeDefinition = null;

        DataDictionaryEntryBase entry =
                (DataDictionaryEntryBase) getDataDictionary().getDictionaryObjectEntry(entryName);
        if (entry != null) {
            attributeDefinition = entry.getAttributeDefinition(attributeName);
        }

        return attributeDefinition;
    }

    /**
     * @param entryName
     * @param collectionName
     * @return CollectionDefinition for the given entryName and collectionName, or null if there is none
     */
    private CollectionDefinition getCollectionDefinition(String entryName, String collectionName) {
        if (StringUtils.isBlank(collectionName)) {
            throw new IllegalArgumentException("invalid (blank) collectionName");
        }
        CollectionDefinition collectionDefinition = null;

        DataDictionaryEntryBase entry =
                (DataDictionaryEntryBase) getDataDictionary().getDictionaryObjectEntry(entryName);
        if (entry != null) {
            collectionDefinition = entry.getCollectionDefinition(collectionName);
        }

        return collectionDefinition;
    }

    /**
     * @param entryName
     * @param relationshipName
     * @return RelationshipDefinition for the given entryName and relationshipName, or null if there is none
     */
    private RelationshipDefinition getRelationshipDefinition(String entryName, String relationshipName) {
        if (StringUtils.isBlank(relationshipName)) {
            throw new IllegalArgumentException("invalid (blank) relationshipName");
        }

        RelationshipDefinition relationshipDefinition = null;

        DataDictionaryEntryBase entry =
                (DataDictionaryEntryBase) getDataDictionary().getDictionaryObjectEntry(entryName);
        if (entry != null) {
            relationshipDefinition = entry.getRelationshipDefinition(relationshipName);
        }

        return relationshipDefinition;
    }

    /**
     * @see DataDictionaryService#getRelationshipAttributeMap(java.lang.String, java.lang.String)
     */
    public Map<String, String> getRelationshipAttributeMap(String entryName, String relationshipName) {
        Map<String, String> attributeMap = new HashMap<String, String>();
        RelationshipDefinition relationshipDefinition = getRelationshipDefinition(entryName, relationshipName);
        for (Iterator iter = relationshipDefinition.getPrimitiveAttributes().iterator(); iter.hasNext(); ) {
            PrimitiveAttributeDefinition attribute = (PrimitiveAttributeDefinition) iter.next();
            attributeMap.put(attribute.getTargetName(), attribute.getSourceName());
        }
        return attributeMap;
    }

    public boolean hasRelationship(String entryName, String relationshipName) {
        return getRelationshipDefinition(entryName, relationshipName) != null;
    }

    public List<String> getRelationshipNames(String entryName) {
        DataDictionaryEntryBase entry =
                (DataDictionaryEntryBase) getDataDictionary().getDictionaryObjectEntry(entryName);

        List<String> relationshipNames = new ArrayList<String>();
        for (RelationshipDefinition def : entry.getRelationships()) {
            relationshipNames.add(def.getObjectAttributeName());
        }
        return relationshipNames;
    }

    /**
     * @see DataDictionaryService#getAttributeControlDefinition(java.lang.String, java.lang.String)
     */
    public ControlDefinition getAttributeControlDefinition(Class dataObjectClass, String attributeName) {
        return getAttributeControlDefinition(dataObjectClass.getName(), attributeName);
    }

    /**
     * @see DataDictionaryService#getAttributeDescription(java.lang.String, java.lang.String)
     */
    public String getAttributeDescription(Class dataObjectClass, String attributeName) {
        return getAttributeDescription(dataObjectClass.getName(), attributeName);
    }

    /**
     * @see DataDictionaryService#getAttributeForceUppercase(java.lang.String, java.lang.String)
     */
    public Boolean getAttributeForceUppercase(Class dataObjectClass, String attributeName) {
        return getAttributeForceUppercase(dataObjectClass.getName(), attributeName);
    }

    /**
     * @see DataDictionaryService#getAttributeFormatter(java.lang.String, java.lang.String)
     */
    public Class<? extends Formatter> getAttributeFormatter(Class dataObjectClass, String attributeName) {
        return getAttributeFormatter(dataObjectClass.getName(), attributeName);
    }

    /**
     * @see DataDictionaryService#getAttributeLabel(java.lang.String, java.lang.String)
     */
    public String getAttributeLabel(Class dataObjectClass, String attributeName) {
        return getAttributeLabel(dataObjectClass.getName(), attributeName);
    }

    /**
     * @see DataDictionaryService#getAttributeMaxLength(java.lang.String, java.lang.String)
     */
    public Integer getAttributeMaxLength(Class dataObjectClass, String attributeName) {
        return getAttributeMaxLength(dataObjectClass.getName(), attributeName);
    }

    /**
     * @see DataDictionaryService#getAttributeShortLabel(java.lang.String, java.lang.String)
     */
    public String getAttributeShortLabel(Class dataObjectClass, String attributeName) {
        return getAttributeShortLabel(dataObjectClass.getName(), attributeName);
    }

    /**
     * @see DataDictionaryService#getAttributeErrorLabel(java.lang.String, java.lang.String)
     */
    public String getAttributeErrorLabel(Class dataObjectClass, String attributeName) {
        return getAttributeErrorLabel(dataObjectClass.getName(), attributeName);
    }

    /**
     * @see DataDictionaryService#getAttributeSize(java.lang.String, java.lang.String)
     */
    public Integer getAttributeSize(Class dataObjectClass, String attributeName) {
        return getAttributeSize(dataObjectClass.getName(), attributeName);
    }

    /**
     * @see DataDictionaryService#getAttributeSummary(java.lang.String, java.lang.String)
     */
    public String getAttributeSummary(Class dataObjectClass, String attributeName) {
        return getAttributeSummary(dataObjectClass.getName(), attributeName);
    }

    /**
     * @see DataDictionaryService#getAttributeValidatingExpression(java.lang.String, java.lang.String)
     */
    public Pattern getAttributeValidatingExpression(Class dataObjectClass, String attributeName) {
        return getAttributeValidatingExpression(dataObjectClass.getName(), attributeName);
    }

    /**
     * @see DataDictionaryService#getAttributeValuesFinderClass(java.lang.String, java.lang.String)
     */
    public Class getAttributeValuesFinderClass(Class dataObjectClass, String attributeName) {
        return getAttributeValuesFinderClass(dataObjectClass.getName(), attributeName);
    }

    /**
     * @see DataDictionaryService#getAttributeValidatingErrorMessageKey(java.lang.String, java.lang.String)
     */
    public String getAttributeValidatingErrorMessageKey(String entryName, String attributeName) {
        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            if (attributeDefinition.hasValidationPattern()) {
                ValidationPattern validationPattern = attributeDefinition.getValidationPattern();
                return validationPattern.getValidationErrorMessageKey();
            }
        }
        return null;
    }

    /**
     * @see DataDictionaryService#getAttributeValidatingErrorMessageParameters(java.lang.String, java.lang.String)
     */
    public String[] getAttributeValidatingErrorMessageParameters(String entryName, String attributeName) {
        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            if (attributeDefinition.hasValidationPattern()) {
                ValidationPattern validationPattern = attributeDefinition.getValidationPattern();
                String attributeLabel = getAttributeErrorLabel(entryName, attributeName);
                return validationPattern.getValidationErrorMessageParameters(attributeLabel);
            }
        }
        return null;
    }

    /**
     * @see DataDictionaryService#getCollectionDescription(java.lang.String, java.lang.String)
     */
    public String getCollectionDescription(Class dataObjectClass, String collectionName) {
        return getCollectionDescription(dataObjectClass.getName(), collectionName);
    }

    /**
     * @see DataDictionaryService#getCollectionLabel(java.lang.String, java.lang.String)
     */
    public String getCollectionLabel(Class dataObjectClass, String collectionName) {
        return getCollectionLabel(dataObjectClass.getName(), collectionName);
    }

    /**
     * @see DataDictionaryService#getCollectionShortLabel(java.lang.String, java.lang.String)
     */
    public String getCollectionShortLabel(Class dataObjectClass, String collectionName) {
        return getCollectionShortLabel(dataObjectClass.getName(), collectionName);
    }

    /**
     * @see DataDictionaryService#getCollectionSummary(java.lang.String, java.lang.String)
     */
    public String getCollectionSummary(Class dataObjectClass, String collectionName) {
        return getCollectionSummary(dataObjectClass.getName(), collectionName);
    }

    /**
     * @see DataDictionaryService#isAttributeDefined(java.lang.String, java.lang.String)
     */
    public Boolean isAttributeDefined(Class dataObjectClass, String attributeName) {
        return isAttributeDefined(dataObjectClass.getName(), attributeName);
    }

    /**
     * @see DataDictionaryService#isAttributeRequired(java.lang.String, java.lang.String)
     */
    public Boolean isAttributeRequired(Class dataObjectClass, String attributeName) {
        return isAttributeRequired(dataObjectClass.getName(), attributeName);
    }

    /**
     * @see DataDictionaryService#getDocumentLabelByClass(java.lang.Class)
     */
    public String getDocumentLabelByClass(Class documentOrBusinessObjectClass) {
        return getDocumentLabelByTypeName(getDocumentTypeNameByClass(documentOrBusinessObjectClass));
    }

    /**
     * @see DataDictionaryService#getDocumentLabelByTypeName(java.lang.String)
     */
    public String getDocumentLabelByTypeName(String documentTypeName) {
        String label = null;
        if (StringUtils.isNotBlank(documentTypeName)) {
            DocumentType documentType = getDocumentTypeService().getDocumentTypeByName(documentTypeName);
            if (documentType != null) {
                label = documentType.getLabel();
            }
        }
        return label;
    }

    /**
     * @see DataDictionaryService#getDocumentTypeNameByClass(java.lang.Class)
     */
    public String getDocumentTypeNameByClass(Class documentClass) {
        if (documentClass == null) {
            throw new IllegalArgumentException("invalid (null) documentClass");
        }
        if (!Document.class.isAssignableFrom(documentClass)) {
            throw new IllegalArgumentException("invalid (non-Document) documentClass");
        }

        String documentTypeName = null;

        DocumentEntry documentEntry = getDataDictionary().getDocumentEntry(documentClass.getName());
        if (documentEntry != null) {
            documentTypeName = documentEntry.getDocumentTypeName();
        }

        return documentTypeName;
    }

    /**
     * @see DataDictionaryService#getValidDocumentTypeNameByClass(java.lang.Class)
     */
    public String getValidDocumentTypeNameByClass(Class documentClass) {
        String documentTypeName = getDocumentTypeNameByClass(documentClass);
        if (StringUtils.isBlank(documentTypeName)) {
            throw new UnknownDocumentTypeException(
                    "unable to get documentTypeName for unknown documentClass '" + documentClass.getName() + "'");
        }
        return documentTypeName;
    }

    /**
     * @see DataDictionaryService#getDocumentClassByTypeName(java.lang.String)
     */
    public Class<? extends Document> getDocumentClassByTypeName(String documentTypeName) {
        Class clazz = null;

        DocumentEntry documentEntry = getDataDictionary().getDocumentEntry(documentTypeName);
        if (documentEntry != null) {
            clazz = documentEntry.getDocumentClass();
        }

        return clazz;
    }

    /**
     * @see DataDictionaryService#getValidDocumentClassByTypeName(java.lang.String)
     */
    public Class<? extends Document> getValidDocumentClassByTypeName(String documentTypeName) {
        Class clazz = getDocumentClassByTypeName(documentTypeName);
        if (clazz == null) {
            throw new UnknownDocumentTypeException(
                    "unable to get class for unknown documentTypeName '" + documentTypeName + "'");
        }
        return clazz;
    }

    /**
     * @see DataDictionaryService#getViewById(java.lang.String)
     */
    public View getViewById(String viewId) {
        return dataDictionary.getViewById(viewId);
    }

    /**
     * @see DataDictionaryService#getDictionaryObject(java.lang.String)
     */
    public Object getDictionaryObject(String id) {
        return dataDictionary.getDictionaryObject(id);
    }

    /**
     * @see DataDictionaryService#containsDictionaryObject(java.lang.String)
     */
    public boolean containsDictionaryObject(String id) {
        return dataDictionary.containsDictionaryObject(id);
    }

    /**
     * @see DataDictionaryService#getViewByTypeIndex(java.lang.String,
     *      java.util.Map)
     */
    public View getViewByTypeIndex(ViewType viewTypeName, Map<String, String> indexKey) {
        return dataDictionary.getViewByTypeIndex(viewTypeName, indexKey);
    }

    public void addDataDictionaryLocation(String location) throws IOException {
        dataDictionary.addConfigFileLocation(location);
    }

    public void addDataDictionaryLocations(List<String> locations) throws IOException {
        for (String location : locations) {
            addDataDictionaryLocation(location);
        }
    }

    /**
     * @see DataDictionaryService#getGroupByAttributesForEffectiveDating(java.lang.Class)
     */
    public List<String> getGroupByAttributesForEffectiveDating(Class dataObjectClass) {
        List<String> groupByList = null;

        DataObjectEntry objectEntry = getDataDictionary().getDataObjectEntry(dataObjectClass.getName());
        if (objectEntry != null) {
            groupByList = objectEntry.getGroupByAttributesForEffectiveDating();
        }

        return groupByList;
    }

    /**
     * Returns all of the inactivation blocks registered for a particular business object
     *
     * @see DataDictionaryService#getAllInactivationBlockingDefinitions(java.lang.Class)
     */
    public Set<InactivationBlockingMetadata> getAllInactivationBlockingDefinitions(
            Class inactivationBlockedBusinessObjectClass) {
        Set<InactivationBlockingMetadata> blockingClasses =
                dataDictionary.getAllInactivationBlockingMetadatas(inactivationBlockedBusinessObjectClass);
        if (blockingClasses == null) {
            return Collections.emptySet();
        }
        return blockingClasses;
    }

    public DocumentTypeService getDocumentTypeService() {
        if (documentTypeService == null) {
            documentTypeService = KewApiServiceLocator.getDocumentTypeService();
        }
        return documentTypeService;
    }

    public void setKualiConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public ConfigurationService getKualiConfigurationService() {
        return kualiConfigurationService;
    }

    public KualiModuleService getKualiModuleService() {
        return kualiModuleService;
    }

    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }
}
