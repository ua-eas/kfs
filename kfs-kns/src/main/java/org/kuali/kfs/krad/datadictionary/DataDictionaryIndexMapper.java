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
package org.kuali.kfs.krad.datadictionary;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.krad.uif.UifConstants;
import org.kuali.kfs.krad.uif.view.View;
import org.springframework.beans.PropertyValues;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A DataDictionaryMapper that simply consults the statically initialized
 * DataDictionaryIndex mappings
 */
public class DataDictionaryIndexMapper implements DataDictionaryMapper {
    private static final Logger LOG = Logger.getLogger(DataDictionaryIndexMapper.class);

    /**
     * @see DataDictionaryMapper#getAllInactivationBlockingMetadatas(DataDictionaryIndex, java.lang.Class)
     */
    public Set<InactivationBlockingMetadata> getAllInactivationBlockingMetadatas(DataDictionaryIndex index, Class<?> blockedClass) {
        return index.getInactivationBlockersForClass().get(blockedClass);
    }

    /**
     * @see DataDictionaryMapper#getBusinessObjectClassNames(DataDictionaryIndex)
     */
    public List<String> getBusinessObjectClassNames(DataDictionaryIndex index) {
        List classNames = new ArrayList();
        classNames.addAll(index.getBusinessObjectEntries().keySet());

        return Collections.unmodifiableList(classNames);
    }

    /**
     * @see DataDictionaryMapper#getBusinessObjectEntries(DataDictionaryIndex)
     */
    public Map<String, BusinessObjectEntry> getBusinessObjectEntries(DataDictionaryIndex index) {
        return index.getBusinessObjectEntries();
    }

    /**
     * @see DataDictionaryMapper#getDataObjectEntryForConcreteClass(DataDictionaryIndex, java.lang.String)
     */
    @Override
    public DataObjectEntry getDataObjectEntryForConcreteClass(DataDictionaryIndex ddIndex, String className) {
        if (StringUtils.isBlank(className)) {
            throw new IllegalArgumentException("invalid (blank) className");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("calling getDataObjectEntry '" + className + "'");
        }

        String trimmedClassName = className;
        int index = className.indexOf("$$");
        if (index >= 0) {
            trimmedClassName = className.substring(0, index);
        }
        return ddIndex.getDataObjectEntries().get(trimmedClassName);
    }

    /**
     * @see DataDictionaryMapper#getBusinessObjectEntryForConcreteClass(java.lang.String)
     */
    public BusinessObjectEntry getBusinessObjectEntryForConcreteClass(DataDictionaryIndex ddIndex, String className) {
        if (StringUtils.isBlank(className)) {
            throw new IllegalArgumentException("invalid (blank) className");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("calling getBusinessObjectEntry '" + className + "'");
        }
        int index = className.indexOf("$$");
        if (index >= 0) {
            className = className.substring(0, index);
        }
        return ddIndex.getBusinessObjectEntries().get(className);
    }

    /**
     * @see DataDictionaryMapper#getDictionaryObjectEntry(DataDictionaryIndex, java.lang.String)
     */
    public DataDictionaryEntry getDictionaryObjectEntry(DataDictionaryIndex ddIndex, String className) {
        if (StringUtils.isBlank(className)) {
            throw new IllegalArgumentException("invalid (blank) className");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("calling getDictionaryObjectEntry '" + className + "'");
        }
        int index = className.indexOf("$$");
        if (index >= 0) {
            className = className.substring(0, index);
        }

        // look in the JSTL key cache
        DataDictionaryEntry entry = ddIndex.getEntriesByJstlKey().get(className);

        // check the Object list
        if (entry == null) {
            entry = ddIndex.getDataObjectEntries().get(className);
        }
        // KULRICE-8005 Breaks when override business object classes
        // check the BO list
        if (entry == null) {
            entry = getBusinessObjectEntry(ddIndex, className);
        }
        // check the document list
        if (entry == null) {
            entry = getDocumentEntry(ddIndex, className);
        }

        return entry;
    }

    /**
     * @see DataDictionaryMapper#getDataObjectEntry(DataDictionaryIndex, java.lang.String)
     */
    @Override
    public DataObjectEntry getDataObjectEntry(DataDictionaryIndex index, String className) {
        DataObjectEntry entry = getDataObjectEntryForConcreteClass(index, className);

        if (entry == null) {
            Class<?> boClass = null;
            try {
                boClass = Class.forName(className);
                ModuleService responsibleModuleService = KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService(boClass);
                if (responsibleModuleService != null && responsibleModuleService.isExternalizable(boClass)) {
                    entry = responsibleModuleService.getExternalizableBusinessObjectDictionaryEntry(boClass);
                }
            } catch (ClassNotFoundException cnfex) {
                // swallow so we can return null
            }
        }

        return entry;
    }

    public BusinessObjectEntry getBusinessObjectEntry(DataDictionaryIndex index, String className) {
        BusinessObjectEntry entry = getBusinessObjectEntryForConcreteClass(index, className);
        if (entry == null) {
            Class boClass = null;
            try {
                boClass = Class.forName(className);
                ModuleService responsibleModuleService = KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService(boClass);
                if (responsibleModuleService != null && responsibleModuleService.isExternalizable(boClass)) {
                    return responsibleModuleService.getExternalizableBusinessObjectDictionaryEntry(boClass);
                }
            } catch (ClassNotFoundException cnfex) {
            }
            return null;
        } else {
            return entry;
        }
    }

    /**
     * @see DataDictionaryMapper#getDocumentEntries(DataDictionaryIndex)
     */
    public Map<String, DocumentEntry> getDocumentEntries(DataDictionaryIndex index) {
        return Collections.unmodifiableMap(index.getDocumentEntries());
    }

    /**
     * @see DataDictionaryMapper#getDocumentEntry(DataDictionaryIndex, java.lang.String)
     */
    public DocumentEntry getDocumentEntry(DataDictionaryIndex index, String documentTypeDDKey) {

        if (StringUtils.isBlank(documentTypeDDKey)) {
            throw new IllegalArgumentException("invalid (blank) documentTypeName");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("calling getDocumentEntry by documentTypeName '" + documentTypeDDKey + "'");
        }

        DocumentEntry de = index.getDocumentEntries().get(documentTypeDDKey);

        if (de == null) {
            try {
                Class<?> clazz = Class.forName(documentTypeDDKey);
                de = index.getDocumentEntriesByBusinessObjectClass().get(clazz);
                if (de == null) {
                    de = index.getDocumentEntriesByMaintainableClass().get(clazz);
                }
            } catch (ClassNotFoundException ex) {
                LOG.warn("Unable to find document entry for key: " + documentTypeDDKey);
            }
        }

        return de;
    }

    /**
     * @see DataDictionaryMapper#getDocumentTypeName(DataDictionaryIndex, java.lang.String)
     */
    public String getDocumentTypeName(DataDictionaryIndex index,
                                      String documentTypeName) {
        // TODO arh14 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * @see DataDictionaryMapper#getMaintenanceDocumentEntryForBusinessObjectClass(DataDictionaryIndex, java.lang.Class)
     */
    public MaintenanceDocumentEntry getMaintenanceDocumentEntryForBusinessObjectClass(DataDictionaryIndex index, Class<?> businessObjectClass) {
        if (businessObjectClass == null) {
            throw new IllegalArgumentException("invalid (null) dataObjectClass");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("calling getDocumentEntry by dataObjectClass '" + businessObjectClass + "'");
        }

        return (MaintenanceDocumentEntry) index.getDocumentEntriesByBusinessObjectClass().get(businessObjectClass);
    }

    /**
     * @see DataDictionaryMapper#getViewById(org.kuali.rice.krad.datadictionary.view.ViewDictionaryIndex,
     * java.lang.String)
     */
    public View getViewById(UifDictionaryIndex index, String viewId) {
        if (StringUtils.isBlank(viewId)) {
            throw new IllegalArgumentException("invalid (blank) view id");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("calling getViewById by id '" + viewId + "'");
        }

        return index.getViewById(viewId);
    }

    /**
     * @see DataDictionaryMapper#getViewByTypeIndex(UifDictionaryIndex,
     * java.lang.String, java.util.Map)
     */
    public View getViewByTypeIndex(UifDictionaryIndex index, UifConstants.ViewType viewTypeName, Map<String, String> indexKey) {
        if (viewTypeName == null) {
            throw new IllegalArgumentException("invalid (blank) view type name");
        }
        if ((indexKey == null) || indexKey.isEmpty()) {
            throw new IllegalArgumentException("index key must have at least one entry");
        }

        return index.getViewByTypeIndex(viewTypeName, indexKey);
    }

    /**
     * @see DataDictionaryIndexMapper#viewByTypeExist(UifDictionaryIndex,
     * java.lang.String, java.util.Map)
     */
    public boolean viewByTypeExist(UifDictionaryIndex index, UifConstants.ViewType viewTypeName,
                                   Map<String, String> indexKey) {
        if (viewTypeName == null) {
            throw new IllegalArgumentException("invalid (blank) view type name");
        }
        if ((indexKey == null) || indexKey.isEmpty()) {
            throw new IllegalArgumentException("index key must have at least one entry");
        }

        return index.viewByTypeExist(viewTypeName, indexKey);
    }

    /**
     * @see DataDictionaryMapper#getViewPropertiesById(org.kuali.rice.krad.datadictionary.view.ViewDictionaryIndex,
     * java.lang.String)
     */
    public PropertyValues getViewPropertiesById(UifDictionaryIndex index, String viewId) {
        if (StringUtils.isBlank(viewId)) {
            throw new IllegalArgumentException("invalid (blank) view id");
        }

        return index.getViewPropertiesById(viewId);
    }

    /**
     * @see DataDictionaryIndexMapper#getViewPropertiesByType(UifDictionaryIndex,
     * java.lang.String, java.util.Map)
     */
    public PropertyValues getViewPropertiesByType(UifDictionaryIndex index, UifConstants.ViewType viewTypeName,
                                                  Map<String, String> indexKey) {
        if (viewTypeName == null) {
            throw new IllegalArgumentException("invalid (blank) view type name");
        }
        if ((indexKey == null) || indexKey.isEmpty()) {
            throw new IllegalArgumentException("index key must have at least one entry");
        }

        return index.getViewPropertiesByType(viewTypeName, indexKey);
    }

    /**
     * @see DataDictionaryMapper#getViewsForType(UifDictionaryIndex,
     * java.lang.String)
     */
    public List<View> getViewsForType(UifDictionaryIndex index, UifConstants.ViewType viewTypeName) {
        if (viewTypeName == null) {
            throw new IllegalArgumentException("invalid (blank) view type name");
        }

        return index.getViewsForType(viewTypeName);
    }

}
