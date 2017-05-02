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
package org.kuali.kfs.krad.service;

import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.bo.BusinessObject;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * This interface defines methods that a BusinessObjectService must provide.
 */
public interface BusinessObjectService {

    /**
     * Saves the passed in object via the persistence layer.
     * <p>
     * This will throw an IllegalArgumentException (runtime exception) if the object passed in is not a descendent of
     * BusinessObject.
     *
     * @param bo A BusinessObject instance or descendent that you wish to be stored.
     */
    public <T extends PersistableBusinessObject> T save(T bo);

    /**
     * Saves the businessObjects on the list via the persistence layer.
     * <p>
     * This will throw an IllegalArgumentException (runtime exception) if any of the objects passed in is not a descendent of
     * BusinessObject.
     *
     * @param businessObjects A List<PersistableBusinessObject> of objects to persist.
     */
    public List<? extends PersistableBusinessObject> save(List<? extends PersistableBusinessObject> businessObjects);

    /**
     * Links up any contained objects, and then Saves the passed in object via the persistence layer.
     * <p>
     * This will throw an IllegalArgumentException (runtime exception) if the object passed in is not a descendent of
     * BusinessObject.
     *
     * @param bo A BusinessObject instance or descendent that you wish to be stored.
     */
    public PersistableBusinessObject linkAndSave(PersistableBusinessObject bo);

    /**
     * Links up any contained objects, and Saves the businessObjects on the list via the persistence layer.
     * <p>
     * This will throw an IllegalArgumentException (runtime exception) if any of the objects passed in is not a descendent of
     * BusinessObject.
     *
     * @param businessObjects A List<BusinessObject> of objects to persist.
     */
    public List<? extends PersistableBusinessObject> linkAndSave(List<? extends PersistableBusinessObject> businessObjects);

    /**
     * Retrieves an object instance identified by its primary key. For composite keys, use {@link #findByPrimaryKey(Class, Map)}
     *
     * @param clazz
     * @param primaryKey
     * @return
     */
    public <T extends BusinessObject> T findBySinglePrimaryKey(Class<T> clazz, Object primaryKey);

    /**
     * Retrieves an object instance identified by its primary keys and values. This can be done by constructing a map where the key
     * to the map entry is the primary key attribute and the value of the entry being the primary key value. For composite keys,
     * pass in each primaryKey attribute and its value as a map entry.
     *
     * @param clazz
     * @param primaryKeys
     * @return
     */
    public <T extends BusinessObject> T findByPrimaryKey(Class<T> clazz, Map<String, ?> primaryKeys);

    /**
     * Retrieves an object instance identified by the class of the given object and the object's primary key values.
     *
     * @param object
     * @return
     */
    public PersistableBusinessObject retrieve(PersistableBusinessObject object);

    /**
     * Retrieves a collection of business objects populated with data, such that each record in the database populates a new object
     * instance. This will only retrieve business objects by class type.
     *
     * @param clazz
     * @return
     */
    public <T extends BusinessObject> Collection<T> findAll(Class<T> clazz);

    /**
     * Retrieves a collection of business objects populated with data, such that each record in the database populates a new object
     * instance. This will only retrieve business objects by class type.
     *
     * @param clazz
     * @return
     */
    public <T extends BusinessObject> Collection<T> findAllOrderBy(Class<T> clazz, String sortField, boolean sortAscending);

    /**
     * This method retrieves a collection of business objects populated with data, such that each record in the database populates a
     * new object instance. This will retrieve business objects by class type and also by criteria passed in as key-value pairs,
     * specifically attribute name and its expected value.
     *
     * @param clazz
     * @param fieldValues
     * @return
     */
    public <T extends BusinessObject> Collection<T> findMatching(Class<T> clazz, Map<String, ?> fieldValues);

    /**
     * This method retrieves a collection of business objects populated with data, such that each record in the database populates a
     * new object instance. This will retrieve business objects by class type and also by criteria passed in as key-value pairs,
     * specifically attribute name and its expected value.
     *
     * @param clazz
     * @param fieldValues
     * @param skip sql query offset
     * @param limit sql query limit
     * @param updatedBefore filter results to only those with lastUpdatedTimestamp before this Instant (optional)
     * @param updatedAfter filter results to only those with lastUpdatedTimestamp after this Instant (optional)
     * @return
     */
    <T extends BusinessObject> Collection<T> findMatching(Class<T> clazz, Map<String, ?> fieldValues, int skip, int limit,
                                                          Instant updatedBefore, Instant updatedAfter, String[] orderBy);

    /**
     * Finds all entities matching the passed in Rice JPA criteria
     *
     * @param <T> the type of the entity that will be returned
     * @param criteria the criteria to form the query with
     * @return a Collection (most likely a List) of all matching entities
     */
    //public abstract <T extends BusinessObject> Collection<T> findMatching(Criteria criteria);

    /**
     * This method retrieves a count of the business objects populated with data which match the criteria in the given Map.
     *
     * @param clazz
     * @param fieldValues
     * @return number of businessObjects of the given class whose fields match the values in the given expected-value Map
     */
    public int countMatching(Class clazz, Map<String, ?> fieldValues);

    /**
     * This method retrieves a count of the business objects populated with data which match the criteria in the given Map.
     *
     * @param clazz
     * @param fieldValues
     * @param updatedBefore filter results to only those with lastUpdatedTimestamp before this Instant (optional)
     * @param updatedAfter filter results to only those with lastUpdatedTimestamp after this Instant (optional)
     * @return number of businessObjects of the given class whose fields match the values in the given expected-value Map
     */
    public int countMatching(Class clazz, Map<String, ?> fieldValues, Instant updatedBefore, Instant updatedAfter);

    /**
     * This method retrieves a count of the business objects populated with data which match both the positive criteria
     * and the negative criteria in the given Map.
     *
     * @param clazz
     * @param positiveFieldValues
     * @param negativeFieldValues
     * @return number of businessObjects of the given class whose fields match the values in the given expected-value Maps
     */
    public int countMatching(Class clazz, Map<String, ?> positiveFieldValues, Map<String, ?> negativeFieldValues);

    /**
     * This method retrieves a collection of business objects populated with data, such that each record in the database populates a
     * new object instance. This will retrieve business objects by class type and also by criteria passed in as key-value pairs,
     * specifically attribute name and its expected value. Performs an order by on sort field.
     *
     * @param clazz
     * @param fieldValues
     * @return
     */
    public <T extends BusinessObject> Collection<T> findMatchingOrderBy(Class<T> clazz, Map<String, ?> fieldValues, String sortField, boolean sortAscending);

    /**
     * Deletes a business object from the database.
     *
     * @param bo
     */
    public void delete(PersistableBusinessObject bo);

    /**
     * Deletes each business object in the given List.
     *
     * @param boList
     */
    public void delete(List<? extends PersistableBusinessObject> boList);

    /**
     * Deletes the object(s) matching the given field values
     *
     * @param clazz
     * @param fieldValues
     */
    public void deleteMatching(Class clazz, Map<String, ?> fieldValues);

    /**
     * This method attempts to retrieve the reference from a BO if it exists.
     *
     * @param bo            - populated BusinessObject instance that includes the referenceName property
     * @param referenceName - name of the member/property to load
     * @return A populated object from the DB, if it exists
     */
    public BusinessObject getReferenceIfExists(BusinessObject bo, String referenceName);

    /**
     * Updates all KualiUser or Person objects contained within this BO, based on the UserID as the authoritative key. The
     * appropriate foreign-key field in the BO itself is also updated.
     * <p>
     * This allows UserIDs to be entered on forms, and the back-end will link up correctly based on this non-key field.
     *
     * @param bo The populated BO (or descendent) instance to be linked & updated
     */
    public void linkUserFields(PersistableBusinessObject bo);

    /**
     * Updates all KualiUser or Person objects contained within this BO, based on the UserID as the authoritative key. The
     * appropriate foreign-key field in the BO itself is also updated.
     * <p>
     * This allows UserIDs to be entered on forms, and the back-end will link up correctly based on this non-key field.
     *
     * @param bos A List of populated BusinessObject (or descendent) instances to be linked & updated.
     */
    public void linkUserFields(List<PersistableBusinessObject> bos);

    /**
     * Merges the given business object, but tells the ORM that the object is to be treated as Read Only,
     * and even if it has changes, it will not be persisted to the database
     *
     * @param bo the business object to managed
     * @return the managed copied of the business object
     */
    public PersistableBusinessObject manageReadOnly(PersistableBusinessObject bo);

}

