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
package org.kuali.kfs.krad.service;

import org.kuali.kfs.krad.exception.ClassNotPersistableException;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;

import java.util.List;
import java.util.Map;

public interface PersistenceService {
//    public void initialize();

	public void loadRepositoryDescriptor(String ojbRepositoryFilePath);

    public void clearCache();

    public Object resolveProxy(Object o);

    /**
     * @param persistableObject object whose primary key field name,value pairs you want
     * @return a Map containing the names and values of fields specified the given class which are designated as key fields in the
     *         OJB repository file
     * @throws IllegalArgumentException if the given Object is null
     * @throws ClassNotPersistableException if the given object is of a type not described in the OJB repository
     */
    public Map getPrimaryKeyFieldValues(Object persistableObject);

    /**
     * @param persistableObject object whose primary key field name,value pairs you want
     * @param sortFieldNames if true, the returned Map will iterate through its entries sorted by fieldName
     * @return a Map containing the names and values of fields specified the given class which are designated as key fields in the
     *         OJB repository file
     * @throws IllegalArgumentException if the given Object is null
     * @throws ClassNotPersistableException if the given object is of a type not described in the OJB repository
     */
    public Map getPrimaryKeyFieldValues(Object persistableObject, boolean sortFieldNames);

    /**
     * @param persistableObject object whose objects need to be filled in based on primary keys
     * @return the object whose key fields have just been retrieved
     * @throws IllegalArgumentException if the given Object is null
     * @throws ClassNotPersistableException if the given object is of a type not described in the OJB repository
     */
    public void retrieveNonKeyFields(Object persistableObject);

    /**
     * @param persistableObject object whose specified reference object needs to be filled in based on primary keys
     * @param referenceObjectName the name of the reference object that will be filled in based on primary key values
     * @throws IllegalArgumentException if the given Object is null
     * @throws ClassNotPersistableException if the given object is of a type not described in the OJB repository
     */
    public void retrieveReferenceObject(Object persistableObject, String referenceObjectName);


    /**
     * @param persistableObject object whose specified reference objects need to be filled in based on primary keys
     * @param referenceObjectNames the names of the reference objects that will be filled in based on primary key values
     * @throws IllegalArgumentException if either of the given lists is null or empty, or if any of the referenceObjectNames is
     *         blank
     * @throws ClassNotPersistableException if the given object is of a type not described in the OJB repository
     */
    public void retrieveReferenceObjects(Object persistableObject, List referenceObjectNames);

    /**
     * @param persistableObjects objects whose specified reference objects need to be filled in based on primary keys
     * @param referenceObjectNames the names of the reference objects that will be filled in based on primary key values
     * @throws IllegalArgumentException if either of the given lists is null or empty, or if any of the referenceObjectNames is
     *         blank
     * @throws ClassNotPersistableException if the given object is of a type not described in the OJB repository
     */
    public void retrieveReferenceObjects(List persistableObjects, List referenceObjectNames);


    /**
     * @param persistableObject object whose objects need to have keys filled
     * @return the object whose key fields have just been filled
     * @throws IllegalArgumentException if the given Object is null
     * @throws ClassNotPersistableException if the given object is of a type not described in the OJB repository
     */
    public void linkObjects(Object persistableObject);


    /**
     * Gets the value for the given field name from the object, works for anonymous fields as well as simple fields
     *
     * @param persistableObject object to get value from
     * @param fieldName name of the field to get from the object
     * @return Object value of field in object, or null
     */
    // This method never called
    //public Object getFieldValue(Object persistableObject, String fieldName);

    /**
     * @param persistableObject object whose primary key field name,value pairs you want
     * @param bounded - whether to restrict the number of rows returned
     * @return a String representation of the primary key fields and values for the given persistableObject
     * @throws IllegalArgumentException if the given Object is null
     * @throws ClassNotPersistableException if the given object is of a type not described in the OJB repository
     */
    public String getFlattenedPrimaryKeyFieldValues(Object persistableObject);

    /**
     *
     * This method examines whether all the foreign key fields for the specified reference contain values.
     *
     * @param bo
     * @param referenceName
     * @return true if they all are accessible and have values, false otherwise
     *
     */
    public boolean allForeignKeyValuesPopulatedForReference(PersistableBusinessObject bo, String referenceName);

    /**
     *
     * This method refreshes all reference objects to this main object that are 'non-updateable'. In general, this means that if a
     * reference object is configured to not be updated when the parent document is saved, then they are non-updated.
     *
     * This will not refresh updateable objects, which can cause problems when you're creating new objects.
     *
     * See PersistenceServiceImpl.isUpdateableReference() for the full logic.
     *
     * @param bo - the businessObject to be refreshed
     *
     */
    public void refreshAllNonUpdatingReferences(PersistableBusinessObject bo);


    /**
     * Determines if the given object is proxied by the ORM or not
     *
     * @param object the object to determine if it is a proxy
     * @return true if the object is an ORM proxy; false otherwise
     */
    public abstract boolean isProxied(Object object);

    /**
	 * Determines if JPA is enabled for the KNS and for the given class
	 *
	 * @param clazz the class to check for JPA enabling of
	 * @return true if JPA is enabled for the class, false otherwise
	 */
	public abstract boolean isJpaEnabledForKradClass(Class clazz);
}
