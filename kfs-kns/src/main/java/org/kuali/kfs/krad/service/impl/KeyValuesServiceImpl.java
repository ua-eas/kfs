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

import org.kuali.kfs.krad.dao.BusinessObjectDao;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.service.KeyValuesService;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.krad.service.PersistenceStructureService;
import org.kuali.kfs.krad.util.KRADPropertyConstants;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * This class provides collection retrievals to populate key value pairs of business objects.
 */
@Transactional
public class KeyValuesServiceImpl implements KeyValuesService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KeyValuesServiceImpl.class);

    private BusinessObjectDao businessObjectDao;
    private PersistenceStructureService persistenceStructureService;

    /**
     * @see KeyValuesService#findAll(java.lang.Class)
     */
    @Override
    public <T extends BusinessObject> Collection<T> findAll(Class<T> clazz) {
        ModuleService responsibleModuleService = KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService(clazz);
        if (responsibleModuleService != null && responsibleModuleService.isExternalizable(clazz)) {
            return (Collection<T>) responsibleModuleService.getExternalizableBusinessObjectsList((Class<ExternalizableBusinessObject>) clazz, Collections.<String, Object>emptyMap());
        }
        if (containsActiveIndicator(clazz)) {
            return businessObjectDao.findAllActive(clazz);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Active indicator not found for class " + clazz.getName());
        }
        return businessObjectDao.findAll(clazz);
    }

    public static <E> Collection<E> createUnmodifiableUpcastList(Collection<? extends E> list, Class<E> type) {
        return new ArrayList<E>(list);
    }

    /**
     * @see KeyValuesService#findAllOrderBy(java.lang.Class, java.lang.String, boolean)
     */
    @Override
    public <T extends BusinessObject> Collection<T> findAllOrderBy(Class<T> clazz, String sortField, boolean sortAscending) {
        if (containsActiveIndicator(clazz)) {
            return businessObjectDao.findAllActiveOrderBy(clazz, sortField, sortAscending);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Active indicator not found for class " + clazz.getName());
        }
        return businessObjectDao.findAllOrderBy(clazz, sortField, sortAscending);
    }

    /**
     * @see BusinessObjectService#findMatching(java.lang.Class, java.util.Map)
     */
    @Override
    public <T extends BusinessObject> Collection<T> findMatching(Class<T> clazz, Map<String, Object> fieldValues) {
        if (containsActiveIndicator(clazz)) {
            return businessObjectDao.findMatchingActive(clazz, fieldValues);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Active indicator not found for class " + clazz.getName());
        }
        return businessObjectDao.findMatching(clazz, fieldValues);
    }


    /**
     * @return Returns the businessObjectDao.
     */
    public BusinessObjectDao getBusinessObjectDao() {
        return businessObjectDao;
    }

    /**
     * @param businessObjectDao The businessObjectDao to set.
     */
    public void setBusinessObjectDao(BusinessObjectDao businessObjectDao) {
        this.businessObjectDao = businessObjectDao;
    }

    /**
     * Gets the persistenceStructureService attribute.
     *
     * @return Returns the persistenceStructureService.
     */
    public PersistenceStructureService getPersistenceStructureService() {
        return persistenceStructureService;
    }

    /**
     * Sets the persistenceStructureService attribute value.
     *
     * @param persistenceStructureService The persistenceStructureService to set.
     */
    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

    /**
     * Uses persistence service to determine if the active column is mapped up in ojb.
     *
     * @param clazz
     * @return boolean if active column is mapped for Class
     */
    private <T extends BusinessObject> boolean containsActiveIndicator(Class<T> clazz) {
        boolean containsActive = false;

        if (persistenceStructureService.listFieldNames(clazz).contains(KRADPropertyConstants.ACTIVE)) {
            containsActive = true;
        }

        return containsActive;
    }

    /**
     * @see KeyValuesService#findAll(java.lang.Class)
     */
    @Override
    public <T extends BusinessObject> Collection<T> findAllInactive(Class<T> clazz) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Active indicator not found for class " + clazz.getName());
        }
        return businessObjectDao.findAllInactive(clazz);
    }

}
