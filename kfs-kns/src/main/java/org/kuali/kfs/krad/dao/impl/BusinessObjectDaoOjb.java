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
package org.kuali.kfs.krad.dao.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.dao.BusinessObjectDao;
import org.kuali.kfs.krad.service.KRADServiceLocatorInternal;
import org.kuali.kfs.krad.service.PersistenceStructureService;
import org.kuali.kfs.krad.util.KRADPropertyConstants;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.krad.util.OjbCollectionAware;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.krad.bo.BusinessObject;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.ObjectRetrievalFailureException;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class is the OJB implementation of the BusinessObjectDao interface and should be used for generic business object unit
 * tests.
 */
public class BusinessObjectDaoOjb extends PlatformAwareDaoBaseOjb implements BusinessObjectDao, OjbCollectionAware {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BusinessObjectDaoOjb.class);

    private PersistenceStructureService persistenceStructureService;

    /**
     * This constructs a {@link BusinessObjectDaoOjb}
     */
    public BusinessObjectDaoOjb(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

    /**
     * @see BusinessObjectDao#findBySinglePrimaryKey(java.lang.Class, java.lang.Object)
     */
    public <T extends BusinessObject> T findBySinglePrimaryKey(Class<T> clazz, Object primaryKey) {
        if (primaryKey.getClass().getName().startsWith("java.lang.")
            || primaryKey.getClass().getName().startsWith("java.sql.")
            || primaryKey.getClass().getName().startsWith("java.math.")
            || primaryKey.getClass().getName().startsWith("java.util.")) {
            try {
                return (T) getPersistenceBrokerTemplate().getObjectById(clazz, primaryKey);
            } catch (ObjectRetrievalFailureException ex) {
                // it doesn't exist, just return null
                return null;
            }
        } else {
            Criteria criteria = buildCriteria(clazz, primaryKey);

            return (T) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(clazz, criteria));
        }
    }

    /**
     * @see BusinessObjectDao#findByPrimaryKey(java.lang.Class, java.util.Map)
     */
    public <T extends BusinessObject> T findByPrimaryKey(Class<T> clazz, Map<String, ?> primaryKeys) {
        Criteria criteria = buildCriteria(primaryKeys);

        return (T) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(clazz, criteria));
    }

    /**
     * Retrieves all of the records for a given class name.
     *
     * @param clazz - the name of the object being used, either KualiCodeBase or a subclass
     * @return Collection
     * @see BusinessObjectDao#findAll(java.lang.Class)
     */
    public <T extends BusinessObject> Collection<T> findAll(Class<T> clazz) {
        return (Collection<T>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(clazz, (Criteria) null));
    }

    /**
     * @see BusinessObjectDao#findAllOrderBy(java.lang.Class, java.lang.String, boolean)
     */
    public <T extends BusinessObject> Collection<T> findAllOrderBy(Class<T> clazz, String sortField, boolean sortAscending) {
        QueryByCriteria queryByCriteria = new QueryByCriteria(clazz, (Criteria) null);

        if (sortAscending) {
            queryByCriteria.addOrderByAscending(sortField);
        } else {
            queryByCriteria.addOrderByDescending(sortField);
        }

        return (Collection<T>) getPersistenceBrokerTemplate().getCollectionByQuery(queryByCriteria);
    }

    /**
     * This is the default impl that comes with Kuali - uses OJB.
     *
     * @see BusinessObjectDao#findMatching(java.lang.Class, java.util.Map)
     */
    public <T extends BusinessObject> Collection<T> findMatching(Class<T> clazz, Map<String, ?> fieldValues) {
        Criteria criteria = buildCriteria(fieldValues);

        return (Collection<T>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(clazz, criteria));
    }

    /**
     * This is the default impl that comes with Kuali - uses OJB.
     *
     * @see BusinessObjectDao#findMatching(java.lang.Class, java.util.Map, int, int, Instant, Instant, String[])
     */
    public <T extends BusinessObject> Collection<T> findMatching(Class<T> clazz, Map<String, ?> fieldValues, int skip, int limit,
                                                                 Instant updatedBefore, Instant updatedAfter, String[] orderBy) {
        Criteria criteria = buildCriteria(fieldValues);

        if (updatedBefore != null) {
            criteria.addLessOrEqualThan("lastUpdatedTimestamp", Timestamp.from(updatedBefore));
        }

        if (updatedAfter != null) {
            criteria.addGreaterOrEqualThan("lastUpdatedTimestamp", Timestamp.from(updatedAfter));
        }

        QueryByCriteria query = QueryFactory.newQuery(clazz, criteria);
        query.setStartAtIndex(skip + 1);
        query.setEndAtIndex(skip + limit);

        for (String orderByItem : orderBy) {
            if (orderByItem.startsWith("-")) {
                query.addOrderByDescending(orderByItem.substring(1));
            } else {
                query.addOrderByAscending(orderByItem);
            }
        }

        return (Collection<T>) getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }


    /**
     * Throws an UnsupportedOperationException
     * @see BusinessObjectDao#findMatching(org.kuali.rice.core.framework.persistence.jpa.criteria.Criteria)
     */
    //public <T extends BusinessObject> Collection<T> findMatching(org.kuali.rice.core.jpa.criteria.Criteria criteria) {
    //	throw new UnsupportedOperationException("OJB does not support finding matching business objects using JPA criteria");
    //}

    /**
     * @see BusinessObjectDao#findAllActive(java.lang.Class)
     */
    public <T extends BusinessObject> Collection<T> findAllActive(Class<T> clazz) {
        return (Collection<T>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(clazz, buildActiveCriteria()));
    }

    /**
     * @see BusinessObjectDao#findAllActive(java.lang.Class)
     */
    public <T extends BusinessObject> Collection<T> findAllInactive(Class<T> clazz) {
        return (Collection<T>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(clazz, buildInactiveCriteria()));
    }

    /**
     * @see BusinessObjectDao#findAllActiveOrderBy(java.lang.Class, java.lang.String, boolean)
     */
    public <T extends BusinessObject> Collection<T> findAllActiveOrderBy(Class<T> clazz, String sortField, boolean sortAscending) {
        QueryByCriteria queryByCriteria = new QueryByCriteria(clazz, buildActiveCriteria());

        if (sortAscending) {
            queryByCriteria.addOrderByAscending(sortField);
        } else {
            queryByCriteria.addOrderByDescending(sortField);
        }

        return (Collection<T>) getPersistenceBrokerTemplate().getCollectionByQuery(queryByCriteria);
    }

    /**
     * @see BusinessObjectDao#findMatchingActive(java.lang.Class, java.util.Map)
     */
    public <T extends BusinessObject> Collection<T> findMatchingActive(Class<T> clazz, Map<String, ?> fieldValues) {
        Criteria criteria = buildCriteria(fieldValues);
        criteria.addAndCriteria(buildActiveCriteria());

        return (Collection<T>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(clazz, criteria));
    }

    /**
     * This is the default impl that comes with Kuali - uses OJB.
     *
     * @see BusinessObjectDao#countMatching(java.lang.Class, java.util.Map, java.time.Instant, java.time.Instant)
     */
    public int countMatching(Class clazz, Map<String, ?> fieldValues, Instant updatedBefore, Instant updatedAfter) {
        Criteria criteria = buildCriteria(fieldValues);

        if (updatedBefore != null) {
            criteria.addLessOrEqualThan("lastUpdatedTimestamp", Timestamp.from(updatedBefore));
        }

        if (updatedAfter != null) {
            criteria.addGreaterOrEqualThan("lastUpdatedTimestamp", Timestamp.from(updatedAfter));
        }


        return getPersistenceBrokerTemplate().getCount(QueryFactory.newQuery(clazz, criteria));
    }

    /**
     * This is the default impl that comes with Kuali - uses OJB.
     *
     * @see BusinessObjectDao#countMatching(java.lang.Class, java.util.Map, java.util.Map)
     */
    public int countMatching(Class clazz, Map<String, ?> positiveFieldValues, Map<String, ?> negativeFieldValues) {
        Criteria criteria = buildCriteria(positiveFieldValues);
        Criteria negativeCriteria = buildNegativeCriteria(negativeFieldValues);
        criteria.addAndCriteria(negativeCriteria);
        return getPersistenceBrokerTemplate().getCount(QueryFactory.newQuery(clazz, criteria));
    }


    /**
     * This is the default impl that comes with Kuali - uses OJB.
     *
     * @see BusinessObjectDao#findMatching(java.lang.Class, java.util.Map)
     */
    public <T extends BusinessObject> Collection<T> findMatchingOrderBy(Class<T> clazz, Map<String, ?> fieldValues, String sortField, boolean sortAscending) {
        Criteria criteria = buildCriteria(fieldValues);
        QueryByCriteria queryByCriteria = new QueryByCriteria(clazz, criteria);

        if (sortAscending) {
            queryByCriteria.addOrderByAscending(sortField);
        } else {
            queryByCriteria.addOrderByDescending(sortField);
        }

        return (Collection<T>) getPersistenceBrokerTemplate().getCollectionByQuery(queryByCriteria);
    }

    /**
     * Saves a business object.
     *
     * @see BusinessObjectDao#save(PersistableBusinessObject)
     */
    public PersistableBusinessObject save(PersistableBusinessObject bo) throws DataAccessException {
        // if collections exist on the BO, create a copy and use to process the
        // collections to ensure
        // that removed elements are deleted from the database
        Set<String> boCollections = getPersistenceStructureService().listCollectionObjectTypes(bo.getClass()).keySet();
        PersistableBusinessObject savedBo = null;
        if (!boCollections.isEmpty()) {
            // refresh bo to get db copy of collections
            savedBo = (PersistableBusinessObject) ObjectUtils.deepCopy(bo);
            for (String boCollection : boCollections) {
                if (getPersistenceStructureService().isCollectionUpdatable(savedBo.getClass(), boCollection)) {
                    savedBo.refreshReferenceObject(boCollection);
                }
            }
            KRADServiceLocatorInternal.getOjbCollectionHelper().processCollections(this, bo, savedBo);
        }

        getPersistenceBrokerTemplate().store(bo);
        return bo;
    }

    /**
     * Saves a business object.
     *
     * @see BusinessObjectDao#save(PersistableBusinessObject)
     */
    public List<? extends PersistableBusinessObject> save(List businessObjects) throws DataAccessException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("About to persist the following BOs:");
            for (Object bo : businessObjects) {
                LOG.debug("   --->" + bo);
            }
        }
        for (Iterator i = businessObjects.iterator(); i.hasNext(); ) {
            Object bo = i.next();
            getPersistenceBrokerTemplate().store(bo);
        }
        return businessObjects;
    }


    /**
     * Deletes the business object passed in.
     *
     * @param bo
     * @throws DataAccessException
     * @see BusinessObjectDao#delete(PersistableBusinessObject)
     */
    public void delete(PersistableBusinessObject bo) {
        getPersistenceBrokerTemplate().delete(bo);
    }

    /**
     * @see BusinessObjectDao#delete(java.util.List)
     */
    public void delete(List<? extends PersistableBusinessObject> boList) {
        for (PersistableBusinessObject bo : boList) {
            getPersistenceBrokerTemplate().delete(bo);
        }
    }


    /**
     * @see BusinessObjectDao#deleteMatching(java.lang.Class, java.util.Map)
     */
    public void deleteMatching(Class clazz, Map<String, ?> fieldValues) {
        Criteria criteria = buildCriteria(fieldValues);

        getPersistenceBrokerTemplate().deleteByQuery(QueryFactory.newQuery(clazz, criteria));

        // An ojb delete by query doesn't update the cache so we need to clear the cache for everything to work property.
        // don't believe me? Read the source code to OJB
        getPersistenceBrokerTemplate().clearCache();
    }

    /**
     * @see BusinessObjectDao#retrieve(PersistableBusinessObject)
     */
    public PersistableBusinessObject retrieve(PersistableBusinessObject object) {
        return (PersistableBusinessObject) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQueryByIdentity(object));
    }

    /**
     * OJB does not support this method
     *
     * @see BusinessObjectDao#findByPrimaryKeyUsingKeyObject(java.lang.Class, java.lang.Object)
     */
    public <T extends BusinessObject> T findByPrimaryKeyUsingKeyObject(Class<T> clazz, Object pkObject) {
        throw new UnsupportedOperationException("OJB does not support this option");
    }

    /**
     * No need to do anything - avoid saving and OJB will "manage read only"
     *
     * @see BusinessObjectDao#manageReadOnly(PersistableBusinessObject)
     */
    public PersistableBusinessObject manageReadOnly(PersistableBusinessObject bo) {
        return bo;
    }

    /**
     * This method will build out criteria in the key-value paradigm (attribute-value).
     *
     * @param fieldValues
     * @return
     */
    private Criteria buildCriteria(Map<String, ?> fieldValues) {
        Criteria criteria = new Criteria();
        for (Iterator i = fieldValues.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry<String, Object> e = (Map.Entry<String, Object>) i.next();

            String key = e.getKey();
            Object value = e.getValue();
            if (value instanceof Collection) {
                criteria.addIn(key, (Collection) value);
            } else if (value instanceof String && ((String) value).contains("*")) {
                value = ((String) value).replace("*", "%");
                criteria.addLike(key, value);
            } else {
                criteria.addEqualTo(key, value);
            }
        }

        return criteria;
    }

    private <T extends BusinessObject> Criteria buildCriteria(Class<T> clazz, Object primaryKey) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        List<String> fieldNames = getPersistenceStructureService().getPrimaryKeys(clazz);

        //create map of values
        for (String fieldName : fieldNames) {
            Object fieldValue;

            try {
                fieldValue = primaryKey.getClass().getMethod("get" + StringUtils.capitalize(fieldName)).invoke(primaryKey);
                fieldValues.put(fieldName, fieldValue);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return this.buildCriteria(fieldValues);
    }

    /**
     * Builds a Criteria object for active field set to true
     *
     * @return Criteria
     */
    private Criteria buildActiveCriteria() {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KRADPropertyConstants.ACTIVE, true);

        return criteria;
    }

    /**
     * Builds a Criteria object for active field set to true
     *
     * @return Criteria
     */
    private Criteria buildInactiveCriteria() {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KRADPropertyConstants.ACTIVE, false);

        return criteria;
    }

    /**
     * This method will build out criteria in the key-value paradigm (attribute-value).
     *
     * @param negativeFieldValues
     * @return
     */
    private Criteria buildNegativeCriteria(Map<String, ?> negativeFieldValues) {
        Criteria criteria = new Criteria();
        for (Iterator i = negativeFieldValues.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry<String, Object> e = (Map.Entry<String, Object>) i.next();

            String key = e.getKey();
            Object value = e.getValue();
            if (value instanceof Collection) {
                criteria.addNotIn(key, (Collection) value);
            } else {
                criteria.addNotEqualTo(key, value);
            }
        }

        return criteria;
    }

    /**
     * Gets the persistenceStructureService attribute.
     *
     * @return Returns the persistenceStructureService.
     */
    protected PersistenceStructureService getPersistenceStructureService() {
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

}
