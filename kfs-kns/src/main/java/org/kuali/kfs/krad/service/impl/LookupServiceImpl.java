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
import org.kuali.kfs.kns.service.KNSServiceLocator;
import org.kuali.kfs.krad.dao.LookupDao;
import org.kuali.kfs.krad.service.LookupService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.search.SearchOperator;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Service implementation for the Lookup structure. It Provides a generic search
 * mechanism against Business Objects. This is the default implementation, that
 * is delivered with Kuali.
 */
@Transactional
public class LookupServiceImpl implements LookupService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LookupServiceImpl.class);

    private LookupDao lookupDao;
    private ConfigurationService kualiConfigurationService;

    public <T extends Object> Collection<T> findCollectionBySearchUnbounded(Class<T> example,
                                                                            Map<String, String> formProps) {
        return findCollectionBySearchHelper(example, formProps, true);
    }

    /**
     * Returns a collection of objects based on the given search parameters.
     *
     * @return Collection returned from the search
     */
    public <T extends Object> Collection<T> findCollectionBySearch(Class<T> example, Map<String, String> formProps) {
        return findCollectionBySearchHelper(example, formProps, false);
    }

    public <T extends Object> Collection<T> findCollectionBySearchHelper(Class<T> example,
                                                                         Map<String, String> formProps, boolean unbounded) {
        return lookupDao.findCollectionBySearchHelper(example, formProps, unbounded,
            allPrimaryKeyValuesPresentAndNotWildcard(example, formProps));
    }

    /**
     * Retrieves a Object based on the search criteria, which should uniquely
     * identify a record.
     *
     * @return Object returned from the search
     */
    public <T extends Object> T findObjectBySearch(Class<T> example, Map<String, String> formProps) {
        if (example == null || formProps == null) {
            throw new IllegalArgumentException("Object and Map must not be null");
        }

        T obj = null;
        try {
            obj = example.newInstance();
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot get new instance of " + example.getName(), e);
        } catch (InstantiationException e) {
            throw new RuntimeException("Cannot instantiate " + example.getName(), e);
        }

        return lookupDao.findObjectByMap(obj, formProps);
    }

    public boolean allPrimaryKeyValuesPresentAndNotWildcard(Class<?> boClass, Map<String, String> formProps) {
        List<String> pkFields = KNSServiceLocator.getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(
            boClass);
        Iterator<String> pkIter = pkFields.iterator();
        boolean returnVal = true;
        while (returnVal && pkIter.hasNext()) {
            String pkName = pkIter.next();
            String pkValue = formProps.get(pkName);

            if (StringUtils.isBlank(pkValue)) {
                returnVal = false;
            } else {
                for (SearchOperator op : SearchOperator.QUERY_CHARACTERS) {
                    if (pkValue.contains(op.op())) {
                        returnVal = false;
                        break;
                    }
                }
            }
        }
        return returnVal;
    }

    /**
     * @return Returns the lookupDao.
     */
    public LookupDao getLookupDao() {
        return lookupDao;
    }

    /**
     * @param lookupDao The lookupDao to set.
     */
    public void setLookupDao(LookupDao lookupDao) {
        this.lookupDao = lookupDao;
    }

    public ConfigurationService getKualiConfigurationService() {
        return kualiConfigurationService;
    }

    public void setKualiConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
}
