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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.ClassNotPersistenceCapableException;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.kns.service.BusinessObjectMetaDataService;
import org.kuali.kfs.kns.service.DataDictionaryService;
import org.kuali.kfs.krad.bo.DataObjectRelationship;
import org.kuali.kfs.krad.datadictionary.AttributeDefinition;
import org.kuali.kfs.krad.datadictionary.BusinessObjectEntry;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.service.LookupService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.BusinessObjectComponent;
import org.kuali.kfs.sys.businessobject.BusinessObjectProperty;
import org.kuali.kfs.sys.dataaccess.BusinessObjectMetaDataDao;
import org.kuali.kfs.sys.service.KfsBusinessObjectMetaDataService;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.krad.bo.BusinessObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@NonTransactional
public class KfsBusinessObjectMetaDataServiceImpl implements KfsBusinessObjectMetaDataService {
    private Logger LOG = Logger.getLogger(KfsBusinessObjectMetaDataServiceImpl.class);
    private DataDictionaryService dataDictionaryService;
    private ParameterService parameterService;
    private BusinessObjectService businessObjectService;
    private BusinessObjectMetaDataService businessObjectMetaDataService;
    private BusinessObjectMetaDataDao businessObjectMetaDataDao;
    private LookupService lookupService;

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    protected BusinessObjectComponent getBusinessObjectComponent(Class<?> componentClass) {
        return new BusinessObjectComponent(KRADServiceLocatorWeb.getKualiModuleService().getNamespaceCode(componentClass), (org.kuali.kfs.kns.datadictionary.BusinessObjectEntry) dataDictionaryService.getDataDictionary().getBusinessObjectEntry(componentClass.getName()));
    }

    @Override
    public BusinessObjectProperty getBusinessObjectProperty(String componentClass, String propertyName) {
        try {
            return new BusinessObjectProperty(getBusinessObjectComponent(Class.forName(componentClass)), dataDictionaryService.getDataDictionary().getBusinessObjectEntry(componentClass).getAttributeDefinition(propertyName));
        } catch (ClassNotFoundException ex) {
            LOG.error("Unable to resolve component class name: " + componentClass);
        }
        return null;
    }

    @Override
    public List<BusinessObjectComponent> findBusinessObjectComponents(String namespaceCode, String componentLabel) {
        Map<Class, BusinessObjectComponent> matchingBusinessObjectComponents = new HashMap<Class, BusinessObjectComponent>();
        Pattern componentLabelRegex = null;
        if (StringUtils.isNotBlank(componentLabel)) {
            String patternStr = componentLabel.replace("*", ".*").toUpperCase();
            try {
                componentLabelRegex = Pattern.compile(patternStr);
            } catch (PatternSyntaxException ex) {
                LOG.error("KfsBusinessObjectMetaDataServiceImpl unable to parse componentLabel pattern, ignoring.", ex);
            }
        }
        for (BusinessObjectEntry businessObjectEntry : dataDictionaryService.getDataDictionary().getBusinessObjectEntries().values()) {
            if ((StringUtils.isBlank(namespaceCode) || namespaceCode.equals(KRADServiceLocatorWeb.getKualiModuleService().getNamespaceCode(businessObjectEntry.getBusinessObjectClass())))
                && ((componentLabelRegex == null) || (StringUtils.isNotBlank(businessObjectEntry.getObjectLabel()) && componentLabelRegex.matcher(businessObjectEntry.getObjectLabel().toUpperCase()).matches()))) {
                matchingBusinessObjectComponents.put(businessObjectEntry.getBusinessObjectClass(), new BusinessObjectComponent(KRADServiceLocatorWeb.getKualiModuleService().getNamespaceCode(businessObjectEntry.getBusinessObjectClass()), (org.kuali.kfs.kns.datadictionary.BusinessObjectEntry) businessObjectEntry));
            }
        }
        return new ArrayList<BusinessObjectComponent>(matchingBusinessObjectComponents.values());
    }

    @Override
    public List<BusinessObjectProperty> findBusinessObjectProperties(String namespaceCode, String componentLabel, String propertyLabel) {
        List<BusinessObjectComponent> businessObjectComponents = findBusinessObjectComponents(namespaceCode, componentLabel);

        Pattern propertyLabelRegex = null;
        if (StringUtils.isNotBlank(propertyLabel)) {
            String patternStr = propertyLabel.replace("*", ".*").toUpperCase();
            try {
                propertyLabelRegex = Pattern.compile(patternStr);
            } catch (PatternSyntaxException ex) {
                LOG.error("KfsBusinessObjectMetaDataServiceImpl unable to parse propertyLabel pattern, ignoring.", ex);
            }
        }

        List<BusinessObjectProperty> matchingBusinessObjectProperties = new ArrayList<BusinessObjectProperty>();
        for (BusinessObjectComponent businessObjectComponent : businessObjectComponents) {
            for (AttributeDefinition attributeDefinition : dataDictionaryService.getDataDictionary().getBusinessObjectEntry(businessObjectComponent.getComponentClass().toString()).getAttributes()) {
                if (!attributeDefinition.getName().endsWith(KFSPropertyConstants.VERSION_NUMBER) && !attributeDefinition.getName().endsWith(KFSPropertyConstants.OBJECT_ID) && ((propertyLabelRegex == null) || propertyLabelRegex.matcher(attributeDefinition.getLabel().toUpperCase()).matches())) {
                    matchingBusinessObjectProperties.add(new BusinessObjectProperty(businessObjectComponent, attributeDefinition));
                }
            }
        }

        return matchingBusinessObjectProperties;
    }


    @Override
    public boolean isMatch(String componentClass, String propertyName, String tableNameSearchCriterion, String fieldNameSearchCriterion) {
        ClassDescriptor classDescriptor = null;
        try {
            classDescriptor = org.apache.ojb.broker.metadata.MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(componentClass);
            Pattern tableNameRegex = null;
            if (StringUtils.isNotBlank(tableNameSearchCriterion)) {
                String patternStr = tableNameSearchCriterion.replace("*", ".*").toUpperCase();
                try {
                    tableNameRegex = Pattern.compile(patternStr);
                } catch (PatternSyntaxException ex) {
                    LOG.error("DataMappingFieldDefinitionLookupableHelperServiceImpl unable to parse tableName pattern, ignoring.", ex);
                }
            }
            Pattern fieldNameRegex = null;
            if (StringUtils.isNotBlank(fieldNameSearchCriterion)) {
                String patternStr = fieldNameSearchCriterion.replace("*", ".*").toUpperCase();
                try {
                    fieldNameRegex = Pattern.compile(patternStr);
                } catch (PatternSyntaxException ex) {
                    LOG.error("DataMappingFieldDefinitionLookupableHelperServiceImpl unable to parse fieldName pattern, ignoring.", ex);
                }
            }
            return ((tableNameRegex == null) || tableNameRegex.matcher(classDescriptor.getFullTableName().toUpperCase()).matches()) && ((fieldNameRegex == null) || ((classDescriptor.getFieldDescriptorByName(propertyName) != null) && fieldNameRegex.matcher(classDescriptor.getFieldDescriptorByName(propertyName).getColumnName().toUpperCase()).matches()));
        } catch (ClassNotPersistenceCapableException e) {
            return StringUtils.isBlank(tableNameSearchCriterion) && StringUtils.isBlank(fieldNameSearchCriterion);
        }
    }

    @Override
    public String getReferenceComponentLabel(Class componentClass, String propertyName) {
        DataObjectRelationship relationship = null;
        try {
            relationship = businessObjectMetaDataService.getBusinessObjectRelationship((BusinessObject) componentClass.newInstance(), propertyName);
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("KfsBusinessObjectMetadataServiceImpl unable to instantiate componentClass: " + componentClass, e);
            }
        }
        if (relationship != null) {
            return dataDictionaryService.getDataDictionary().getBusinessObjectEntry(relationship.getRelatedClass().getName()).getObjectLabel();
        }
        return "";
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setBusinessObjectMetaDataService(BusinessObjectMetaDataService businessObjectMetaDataService) {
        this.businessObjectMetaDataService = businessObjectMetaDataService;
    }

    public void setBusinessObjectMetaDataDao(BusinessObjectMetaDataDao businessObjectMetaDataDao) {
        this.businessObjectMetaDataDao = businessObjectMetaDataDao;
    }

    public void setLookupService(LookupService lookupService) {
        this.lookupService = lookupService;
    }
}
