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

import org.kuali.kfs.kns.datadictionary.BusinessObjectEntry;
import org.kuali.kfs.krad.bo.ModuleConfiguration;
import org.kuali.kfs.krad.datadictionary.AttributeSecurity;
import org.kuali.kfs.krad.datadictionary.HelpDefinition;
import org.kuali.kfs.krad.datadictionary.PrimitiveAttributeDefinition;
import org.kuali.kfs.krad.datadictionary.QuinoaMaskFormatterAdapter;
import org.kuali.kfs.krad.datadictionary.RelationshipDefinition;
import org.kuali.kfs.krad.datadictionary.SupportAttributeDefinition;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class QuinoaModuleServiceAdapter implements ModuleService {
    private org.kuali.rice.krad.service.ModuleService quinoaModuleService;
    private Map<Class<?>, BusinessObjectEntry> CACHED_BUSINESS_OBJECT_ENTRIES;

    public QuinoaModuleServiceAdapter(org.kuali.rice.krad.service.ModuleService quinoaModuleService) {
        this.quinoaModuleService = quinoaModuleService;
        this.CACHED_BUSINESS_OBJECT_ENTRIES = new ConcurrentHashMap<>();
    }

    @Override
    public ModuleConfiguration getModuleConfiguration() {
        org.kuali.rice.krad.bo.ModuleConfiguration quinoaModuleConfiguration = quinoaModuleService.getModuleConfiguration();
        org.kuali.kfs.krad.bo.ModuleConfiguration allBranTranslatedModuleConfiguration = new org.kuali.kfs.krad.bo.ModuleConfiguration();
        allBranTranslatedModuleConfiguration.setNamespaceCode(quinoaModuleConfiguration.getNamespaceCode());
        allBranTranslatedModuleConfiguration.setPackagePrefixes(quinoaModuleConfiguration.getPackagePrefixes());
        allBranTranslatedModuleConfiguration.setDatabaseRepositoryFilePaths(quinoaModuleConfiguration.getDatabaseRepositoryFilePaths());
        allBranTranslatedModuleConfiguration.setDataDictionaryPackages(quinoaModuleConfiguration.getDataDictionaryPackages());
        allBranTranslatedModuleConfiguration.setScriptConfigurationFilePaths(quinoaModuleConfiguration.getScriptConfigurationFilePaths());
        /*allBranTranslatedModuleConfiguration.setJobNames(quinoaModuleConfiguration.getJobNames());
        allBranTranslatedModuleConfiguration.setTriggerNames(quinoaModuleConfiguration.getTriggerNames());*/
        allBranTranslatedModuleConfiguration.setDataSourceName(quinoaModuleConfiguration.getDataSourceName());
        allBranTranslatedModuleConfiguration.setExternalizableBusinessObjectImplementations(quinoaModuleConfiguration.getExternalizableBusinessObjectImplementations());
        return allBranTranslatedModuleConfiguration;
    }

    @Override
    public boolean isResponsibleFor(Class businessObjectClass) {
        return this.quinoaModuleService.isResponsibleFor(businessObjectClass);
    }

    @Override
    public boolean isResponsibleForJob(String jobName) {
        //return this.quinoaModuleService.isResponsibleForJob(jobName);
        return false;
    }

    @Override
    public List listPrimaryKeyFieldNames(Class businessObjectInterfaceClass) {
        return this.quinoaModuleService.listPrimaryKeyFieldNames(businessObjectInterfaceClass);
    }

    @Override
    public List<List<String>> listAlternatePrimaryKeyFieldNames(Class businessObjectInterfaceClass) {
        return this.quinoaModuleService.listAlternatePrimaryKeyFieldNames(businessObjectInterfaceClass);
    }

    @Override
    public BusinessObjectEntry getExternalizableBusinessObjectDictionaryEntry(Class businessObjectInterfaceClass) {
        if (CACHED_BUSINESS_OBJECT_ENTRIES.containsKey(businessObjectInterfaceClass)) {
            return CACHED_BUSINESS_OBJECT_ENTRIES.get(businessObjectInterfaceClass);
        }
        org.kuali.rice.krad.datadictionary.BusinessObjectEntry qboe = quinoaModuleService.getExternalizableBusinessObjectDictionaryEntry(businessObjectInterfaceClass);
        BusinessObjectEntry boe = translateBusinessObjectEntry(qboe);
        CACHED_BUSINESS_OBJECT_ENTRIES.put(businessObjectInterfaceClass, boe);

        return boe;
    }

    protected BusinessObjectEntry translateBusinessObjectEntry(org.kuali.rice.krad.datadictionary.BusinessObjectEntry qboe) {
        try {
            // this translation currently does not adapt/translate the following properties: collections, complexAttributes, exporterClass, inactivationBlockingDefinitions, mustOccurConstraints
            BusinessObjectEntry boe = new BusinessObjectEntry();
            boe.setObjectLabel(qboe.getObjectLabel());
            boe.setBusinessObjectClass(qboe.getBusinessObjectClass());
            boe.setBaseBusinessObjectClass(qboe.getBaseBusinessObjectClass());
            boe.setBoNotesEnabled(qboe.isBoNotesEnabled());
            boe.setDataObjectClass(qboe.getDataObjectClass());
            boe.setGroupByAttributesForEffectiveDating(qboe.getGroupByAttributesForEffectiveDating());
            boe.setHelpDefinition(translateHelpDefinition(qboe.getHelpDefinition()));
            boe.setName(qboe.getName());
            boe.setObjectDescription(qboe.getObjectDescription());
            boe.setObjectLabel(qboe.getObjectLabel());
            boe.setPrimaryKeys(qboe.getPrimaryKeys());
            if (!ObjectUtils.isNull(qboe.getRelationships()) && !qboe.getRelationships().isEmpty()) {
                boe.setRelationships(qboe.getRelationships().stream().map((org.kuali.rice.krad.datadictionary.RelationshipDefinition relationshipDefinition) -> translateRelationshipDefinition(relationshipDefinition)).collect(Collectors.toList()));
            }
            boe.setTitleAttribute(qboe.getTitleAttribute());
            if (!ObjectUtils.isNull(qboe.getAttributes()) && !qboe.getAttributes().isEmpty()) {
                boe.setAttributes(qboe.getAttributes().stream().map((org.kuali.rice.krad.datadictionary.AttributeDefinition attributeDefinition) -> translateAttributeDefinition(attributeDefinition)).collect(Collectors.toList()));
            }
            return boe;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected HelpDefinition translateHelpDefinition(org.kuali.rice.krad.datadictionary.HelpDefinition helpDefinition) {
        if (helpDefinition == null) {
            return null;
        }
        HelpDefinition translatedHelpDefinition = new HelpDefinition();
        translatedHelpDefinition.setParameterDetailType(helpDefinition.getParameterDetailType());
        translatedHelpDefinition.setParameterName(helpDefinition.getParameterName());
        translatedHelpDefinition.setParameterNamespace(helpDefinition.getParameterNamespace());
        return translatedHelpDefinition;
    }

    protected RelationshipDefinition translateRelationshipDefinition(org.kuali.rice.krad.datadictionary.RelationshipDefinition relationshipDefinition) {
        RelationshipDefinition translatedRelationshipDefinition = new RelationshipDefinition();
        translatedRelationshipDefinition.setObjectAttributeName(relationshipDefinition.getObjectAttributeName());
        if (!ObjectUtils.isNull(relationshipDefinition.getPrimitiveAttributes()) && !relationshipDefinition.getPrimitiveAttributes().isEmpty()) {
            translatedRelationshipDefinition.setPrimitiveAttributes(relationshipDefinition.getPrimitiveAttributes().stream().map((org.kuali.rice.krad.datadictionary.PrimitiveAttributeDefinition primitiveAttribute) -> translatePrimitiveAttribute(primitiveAttribute)).collect(Collectors.toList()));
        }
        translatedRelationshipDefinition.setSourceClass(relationshipDefinition.getSourceClass());
        if (!ObjectUtils.isNull(relationshipDefinition.getSupportAttributes()) && !relationshipDefinition.getSupportAttributes().isEmpty()) {
            translatedRelationshipDefinition.setSupportAttributes(relationshipDefinition.getSupportAttributes().stream().map((org.kuali.rice.krad.datadictionary.SupportAttributeDefinition supportAttribute) -> translateSupportAttribute(supportAttribute)).collect(Collectors.toList()));
        }
        translatedRelationshipDefinition.setTargetClass(relationshipDefinition.getTargetClass());
        return translatedRelationshipDefinition;
    }

    private SupportAttributeDefinition translateSupportAttribute(org.kuali.rice.krad.datadictionary.SupportAttributeDefinition supportAttribute) {
        SupportAttributeDefinition translatedSupportAttribute = new SupportAttributeDefinition();
        translatedSupportAttribute.setIdentifier(supportAttribute.isIdentifier());
        translatedSupportAttribute.setId(supportAttribute.getId());
        translatedSupportAttribute.setSourceName(supportAttribute.getSourceName());
        translatedSupportAttribute.setTargetName(supportAttribute.getTargetName());
        return translatedSupportAttribute;
    }

    private PrimitiveAttributeDefinition translatePrimitiveAttribute(org.kuali.rice.krad.datadictionary.PrimitiveAttributeDefinition primitiveAttribute) {
        PrimitiveAttributeDefinition translatedPrimitiveAttributeDefinition = new PrimitiveAttributeDefinition();
        translatedPrimitiveAttributeDefinition.setId(primitiveAttribute.getId());
        translatedPrimitiveAttributeDefinition.setSourceName(primitiveAttribute.getSourceName());
        translatedPrimitiveAttributeDefinition.setTargetName(primitiveAttribute.getTargetName());
        return translatedPrimitiveAttributeDefinition;
    }

    protected org.kuali.kfs.krad.datadictionary.AttributeDefinition translateAttributeDefinition(AttributeDefinition attributeDefinition) {
        org.kuali.kfs.krad.datadictionary.AttributeDefinition attrDefn = new org.kuali.kfs.krad.datadictionary.AttributeDefinition();
        // this does not adapt/translate the following properties: caseConstraint, control, dataType, mustOccurConstraints, optionsFinder, optionsFinderClass, preqrequisitesConstraints, validationPattern, validCharactersConstraint
        attrDefn.setAdditionalDisplayAttributeName(attributeDefinition.getAdditionalDisplayAttributeName());
        attrDefn.setAlternateDisplayAttributeName(attributeDefinition.getAdditionalDisplayAttributeName());
        attrDefn.setAttributeSecurity(translateAttributeSecurity(attributeDefinition.getAttributeSecurity()));
        attrDefn.setChildEntryName(attributeDefinition.getChildEntryName());
        attrDefn.setCustomValidatorClass(attributeDefinition.getCustomValidatorClass());
        attrDefn.setExclusiveMin(attributeDefinition.getExclusiveMin());
        attrDefn.setForceUppercase(attributeDefinition.getForceUppercase());
        attrDefn.setInclusiveMax(attributeDefinition.getInclusiveMax());
        if (!ObjectUtils.isNull(attributeDefinition.getFormatterClass())) {
            attrDefn.setFormatterClass(attributeDefinition.getFormatterClass());
        }
        //attrDefn.setLookupContextPath(attributeDefinition.getLookupContextPath());
        attrDefn.setMaxLength(attributeDefinition.getMaxLength());
        attrDefn.setMinLength(attributeDefinition.getMinLength());
        attrDefn.setUnique(attributeDefinition.getUnique());
        attrDefn.setConstraintText(attributeDefinition.getConstraintText());
        attrDefn.setDescription(attributeDefinition.getDescription());
        attrDefn.setDisplayLabelAttribute(attributeDefinition.getDisplayLabelAttribute());
        attrDefn.setId(attributeDefinition.getId());
        attrDefn.setLabel(attributeDefinition.getLabel());
        attrDefn.setName(attributeDefinition.getName());
        attrDefn.setRequired(attributeDefinition.isRequired());
        attrDefn.setShortLabel(attributeDefinition.getShortLabel());
        attrDefn.setSummary(attributeDefinition.getSummary());
        return attrDefn;
    }

    private AttributeSecurity translateAttributeSecurity(org.kuali.rice.krad.datadictionary.AttributeSecurity attributeSecurity) {
        if (attributeSecurity == null) {
            return null;
        }
        AttributeSecurity translatedAttributeSecurity = new AttributeSecurity();
        translatedAttributeSecurity.setHide(attributeSecurity.isHide());
        translatedAttributeSecurity.setMask(attributeSecurity.isMask());
        translatedAttributeSecurity.setMaskFormatter(new QuinoaMaskFormatterAdapter(attributeSecurity.getMaskFormatter()));
        translatedAttributeSecurity.setPartialMask(attributeSecurity.isPartialMask());
        translatedAttributeSecurity.setPartialMaskFormatter(new QuinoaMaskFormatterAdapter(attributeSecurity.getPartialMaskFormatter()));
        translatedAttributeSecurity.setReadOnly(attributeSecurity.isReadOnly());
        //translatedAttributeSecurity.setId(attributeSecurity.getId());
        return translatedAttributeSecurity;
    }

    @Override
    public <T extends ExternalizableBusinessObject> T getExternalizableBusinessObject(Class<T> businessObjectClass, Map<String, Object> fieldValues) {
        return this.quinoaModuleService.getExternalizableBusinessObject(businessObjectClass, fieldValues);
    }

    @Override
    public <T extends ExternalizableBusinessObject> List<T> getExternalizableBusinessObjectsList(Class<T> businessObjectClass, Map<String, Object> fieldValues) {
        return this.quinoaModuleService.getExternalizableBusinessObjectsList(businessObjectClass, fieldValues);
    }

    @Override
    public <T extends ExternalizableBusinessObject> List<T> getExternalizableBusinessObjectsListForLookup(Class<T> businessObjectClass, Map<String, Object> fieldValues, boolean unbounded) {
        return this.quinoaModuleService.getExternalizableBusinessObjectsListForLookup(businessObjectClass, fieldValues, unbounded);
    }

    @Override
    public String getExternalizableDataObjectInquiryUrl(Class<?> inquiryDataObjectClass, Properties parameters) {
        return this.quinoaModuleService.getExternalizableDataObjectInquiryUrl(inquiryDataObjectClass, parameters);
    }

    @Override
    public String getExternalizableDataObjectLookupUrl(Class<?> inquiryDataObjectClass, Properties parameters) {
        return this.quinoaModuleService.getExternalizableDataObjectLookupUrl(inquiryDataObjectClass, parameters);
    }

    @Override
    public String getExternalizableBusinessObjectInquiryUrl(Class inquiryBusinessObjectClass, Map<String, String[]> parameters) {
        return this.quinoaModuleService.getExternalizableBusinessObjectInquiryUrl(inquiryBusinessObjectClass, parameters);
    }

    @Override
    public String getExternalizableBusinessObjectLookupUrl(Class inquiryBusinessObjectClass, Map<String, String> parameters) {
        return this.quinoaModuleService.getExternalizableBusinessObjectLookupUrl(inquiryBusinessObjectClass, parameters);
    }

    @Override
    public <T extends ExternalizableBusinessObject> T retrieveExternalizableBusinessObjectIfNecessary(BusinessObject businessObject, T currentInstanceExternalizableBO, String externalizableRelationshipName) {
        return this.quinoaModuleService.retrieveExternalizableBusinessObjectIfNecessary(businessObject, currentInstanceExternalizableBO, externalizableRelationshipName);
    }

    @Override
    public <T extends ExternalizableBusinessObject> List<T> retrieveExternalizableBusinessObjectsList(BusinessObject businessObject, String externalizableRelationshipName, Class<T> externalizableClazz) {
        return this.quinoaModuleService.retrieveExternalizableBusinessObjectsList(businessObject, externalizableRelationshipName, externalizableClazz);
    }

    @Override
    public boolean isExternalizable(Class boClass) {
        return this.quinoaModuleService.isExternalizable(boClass);
    }

    @Override
    public boolean isExternalizableBusinessObjectLookupable(Class boClass) {
        return this.quinoaModuleService.isExternalizableBusinessObjectLookupable(boClass);
    }

    @Override
    public boolean isExternalizableBusinessObjectInquirable(Class boClass) {
        return this.quinoaModuleService.isExternalizableBusinessObjectInquirable(boClass);
    }

    @Override
    public <T extends ExternalizableBusinessObject> T createNewObjectFromExternalizableClass(Class<T> boClass) {
        return this.quinoaModuleService.createNewObjectFromExternalizableClass(boClass);
    }

    @Override
    public <E extends ExternalizableBusinessObject> Class<E> getExternalizableBusinessObjectImplementation(Class<E> externalizableBusinessObjectInterface) {
        return this.quinoaModuleService.getExternalizableBusinessObjectImplementation(externalizableBusinessObjectInterface);
    }

    @Override
    public boolean isLocked() {
        return this.quinoaModuleService.isLocked();
    }

    @Override
    public boolean goToCentralRiceForInquiry() {
        return this.quinoaModuleService.goToCentralRiceForInquiry();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    @Override
    public boolean isExternal(Class boClass) {
        return true;
    }
}
