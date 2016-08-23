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

import org.apache.log4j.Logger;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.krad.service.PersistenceService;
import org.kuali.kfs.krad.util.ExternalizableBusinessObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class is the service implementation for the Persistence structure.
 * OjbRepositoryExplorer provides functions for extracting information from the
 * OJB repository at runtime. This is the default implementation, that is
 * delivered with Kuali.
 */
@Transactional
public class PersistenceServiceImpl extends PersistenceServiceImplBase implements PersistenceService {

	private static Logger LOG = Logger.getLogger(PersistenceServiceImpl.class);

    private KualiModuleService kualiModuleService;

	private PersistenceService persistenceServiceOjb;

	public void setPersistenceServiceOjb(PersistenceService persistenceServiceOjb) {
		this.persistenceServiceOjb = persistenceServiceOjb;
	}

	private PersistenceService getService(Class clazz) {
    	return persistenceServiceOjb;
	}

	// This method is for OJB specfic features. It is now being called directly where needed.
	public void clearCache() {
		throw new UnsupportedOperationException("This should be called directly from the OJB Impl if needed.");
	}

	// This method is for OJB specfic features. It is now being called directly where needed.
	public void loadRepositoryDescriptor(String ojbRepositoryFilePath) {
		throw new UnsupportedOperationException("This should be called directly from the OJB Impl if needed.");
	}

	public Object resolveProxy(Object o) {
		return getService(o.getClass()).resolveProxy(o);
	}

	/**
	 * @see PersistenceService#retrieveNonKeyFields(java.lang.Object)
	 */
	public void retrieveNonKeyFields(Object persistableObject) {
        if (persistableObject != null &&
                ExternalizableBusinessObjectUtils.isExternalizableBusinessObject(persistableObject.getClass())) {
            //
            // special handling for EBOs
            //
            Map<String, ?> criteria = KRADServiceLocatorWeb.getDataObjectMetaDataService().getPrimaryKeyFieldValues(persistableObject);
            if (!CollectionUtils.isEmpty(criteria)) {
                ModuleService moduleService = getKualiModuleService().getResponsibleModuleService(persistableObject.getClass());
                if (moduleService != null) {
                    Class<? extends ExternalizableBusinessObject> clazz =
                            ExternalizableBusinessObjectUtils.determineExternalizableBusinessObjectSubInterface(persistableObject.getClass());
                    ExternalizableBusinessObject freshEbo = moduleService.getExternalizableBusinessObject(clazz, (Map<String, Object>)criteria);
                    if (freshEbo != null) {
                        BeanUtils.copyProperties(freshEbo, persistableObject);
                    }
                }
            }
        } else {
            getService(persistableObject.getClass()).retrieveNonKeyFields(persistableObject);
        }
	}

	/**
	 * @see PersistenceService#retrieveReferenceObject(java.lang.Object,
	 *      String referenceObjectName)
	 */
	public void retrieveReferenceObject(Object persistableObject, String referenceObjectName) {
		getService(persistableObject.getClass()).retrieveReferenceObject(persistableObject, referenceObjectName);
	}

	/**
	 * @see PersistenceService#retrieveReferenceObject(java.lang.Object,
	 *      String referenceObjectName)
	 */
	public void retrieveReferenceObjects(Object persistableObject, List referenceObjectNames) {
		getService(persistableObject.getClass()).retrieveReferenceObjects(persistableObject, referenceObjectNames);
	}

	/**
	 * @see PersistenceService#retrieveReferenceObject(java.lang.Object,
	 *      String referenceObjectName)
	 */
	public void retrieveReferenceObjects(List persistableObjects, List referenceObjectNames) {
		if (persistableObjects == null) {
			throw new IllegalArgumentException("invalid (null) persistableObjects");
		}
		if (persistableObjects.isEmpty()) {
			throw new IllegalArgumentException("invalid (empty) persistableObjects");
		}
		if (referenceObjectNames == null) {
			throw new IllegalArgumentException("invalid (null) referenceObjectNames");
		}
		if (referenceObjectNames.isEmpty()) {
			throw new IllegalArgumentException("invalid (empty) referenceObjectNames");
		}

		for (Iterator i = persistableObjects.iterator(); i.hasNext();) {
			Object persistableObject = i.next();
			retrieveReferenceObjects(persistableObject, referenceObjectNames);
		}
	}

	/**
	 * @see PersistenceService#getFlattenedPrimaryKeyFieldValues(java.lang.Object)
	 */
	public String getFlattenedPrimaryKeyFieldValues(Object persistableObject) {
		return getService(persistableObject.getClass()).getFlattenedPrimaryKeyFieldValues(persistableObject);
	}

	/**
	 * For each reference object to the parent persistableObject, sets the key
	 * values for that object. First, if the reference object already has a
	 * value for the key, the value is left unchanged. Otherwise, for
	 * non-anonymous keys, the value is taken from the parent object. For
	 * anonymous keys, all other persistableObjects are checked until a value
	 * for the key is found.
	 *
	 * @see PersistenceService#getReferencedObject(java.lang.Object,
	 *      org.apache.ojb.broker.metadata.ObjectReferenceDescriptor)
	 */
	public void linkObjects(Object persistableObject) {
		getService(persistableObject.getClass()).linkObjects(persistableObject);
	}

	/**
	 *
	 * @see PersistenceService#allForeignKeyValuesPopulatedForReference(org.kuali.rice.krad.bo.BusinessObject,
	 *      java.lang.String)
	 */
	public boolean allForeignKeyValuesPopulatedForReference(PersistableBusinessObject bo, String referenceName) {
		return getService(bo.getClass()).allForeignKeyValuesPopulatedForReference(bo, referenceName);
	}

	/**
	 *
	 * @see PersistenceService#refreshAllNonUpdatingReferences(org.kuali.rice.krad.bo.BusinessObject)
	 */
	public void refreshAllNonUpdatingReferences(PersistableBusinessObject bo) {
		getService(bo.getClass()).refreshAllNonUpdatingReferences(bo);
	}

	/**
	 * Defers to the service for the given class
	 *
	 * @see PersistenceService#isProxied(java.lang.Object)
	 */
	public boolean isProxied(Object bo) {
		return getService(bo.getClass()).isProxied(bo);
	}

	@Override
	public boolean isJpaEnabledForKradClass(Class clazz) {
		return false;
	}

	public KualiModuleService getKualiModuleService() {
        return kualiModuleService;
    }

    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }
}
