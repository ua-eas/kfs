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

import org.kuali.kfs.krad.bo.DataObjectRelationship;
import org.kuali.kfs.krad.datadictionary.RelationshipDefinition;

import java.util.List;
import java.util.Map;

/**
 * Provides metadata such as relationships and key fields for data objects
 *
 * <p>
 * Service provides a facade to the various services for retrieving metadata
 * within the framework, such as the <code>DataDictionaryService</code> and
 * the <code>PersistenceService</code>
 * </p>
 *
 *
 */
public interface DataObjectMetaDataService {

    /**
     * Checks the DataDictionary and OJB Repository File to determine the primary
     * fields names for a given class.
     *
     * @param clazz - the Class to check for primary keys
     * @return a list of the primary key field names or an empty list if none are found
     */
    public List<String> listPrimaryKeyFieldNames(Class<?> clazz);

    /**
     * Determines the primary keys for the class of the given object, then for each
     * key field retrieves the value from the object instance and populates the return
     * map with the primary key name as the map key and the object value as the map value
     *
     * @param dataObject - object whose primary key field name,value pairs you want
     * @return a Map containing the names and values of fields for the given class which
     *         are designated as key fields in the OJB repository file or DataDictionary
     * @throws IllegalArgumentException if the given Object is null
     */
    public Map<String, ?> getPrimaryKeyFieldValues(Object dataObject);

    /**
     * Determines the primary keys for the class of the given object, then for each
     * key field retrieves the value from the object instance and populates the return
     * map with the primary key name as the map key and the object value as the map value
     *
     * @param dataObject - object whose primary key field name,value pairs you want
     * @param sortFieldNames - if true, the returned Map will iterate through its entries sorted by fieldName
     * @return a Map containing the names and values of fields for the given class which
     *         are designated as key fields in the OJB repository file or DataDictionary
     * @throws IllegalArgumentException if the given Object is null
     */
    public Map<String, ?> getPrimaryKeyFieldValues(Object dataObject, boolean sortFieldNames);

    /**
     * Compares two dataObject instances for equality of type and key values using toString()
     * of each value for comparison purposes.
     *
     * @param do1
     * @param do2
     * @return boolean indicating whether the two objects are equal.
     */
    public boolean equalsByPrimaryKeys(Object do1, Object do2);

    /**
     * Attempts to find a relationship for the given attribute within the given
     * data object
     *
     * <p>
     * First the data dictionary is queried to find any relationship definitions
     * setup that include the attribute, if found the
     * <code>BusinessObjectRetationship</code> is build from that. If not and
     * the data object class is persistent, relationships are retrieved from the
     * persistence service. Nested attributes are handled in addition to
     * external business objects. If multiple relationships are found, the one
     * that contains the least amount of joining keys is returned
     * </p>
     *
     * @param dataObject - data object instance that contains the attribute
     * @param dataObjectClass - class for the data object that contains the attribute
     * @param attributeName - property name for the attribute
     * @param attributePrefix - property prefix for the attribute
     * @param keysOnly - indicates whether only primary key fields should be returned
     * in the relationship
     * @param supportsLookup - indicates whether the relationship should support lookup
     * @param supportsInquiry - indicates whether the relationship should support inquiry
     * @return BusinessObjectRelationship for the attribute, or null if not
     *         found
     */
    public DataObjectRelationship getDataObjectRelationship(Object dataObject, Class<?> dataObjectClass,
            String attributeName, String attributePrefix, boolean keysOnly, boolean supportsLookup,
            boolean supportsInquiry);

    /**
     * Attempts to find relationships for the given data object class
     *
     * <p>
     * First the data dictionary is queried to find any relationship definitions
     * <code>BusinessObjectRetationship</code> is build from that. If not and
     * the data object class is persistent, relationships are retrieved from the
     * persistence service. Nested attributes are handled in addition to
     * external business objects. If multiple relationships are found, the one
     * that contains the least amount of joining keys is returned
     * </p>
     *
     * @param dataObjectClass - class for the data object that contains the attribute
     * @return List of DataObjectRelationship for the class
     */
    public List<DataObjectRelationship> getDataObjectRelationships(Class<?> dataObjectClass);

    /**
     * Fetches the RelationshipDefinition for the attribute with the given name within
     * the given class
     *
     * @param dataObjectClass - data object class that contains the attribute
     * @param attributeName - property name for the attribute
     * @return RelationshipDefinition for the attribute, or null if not found
     */
    public RelationshipDefinition getDictionaryRelationship(Class<?> dataObjectClass, String attributeName);

    /**
     * Returns the attribute to be associated with for object level markings.  This would
     * be the field chosen for inquiry links etc.
     *
     * @param dataObjectClass - data object class to obtain title attribute of
     * @return property name of title attribute or null if data object entry not found
     * @throws IllegalArgumentException if the given Class is null
     */
    public String getTitleAttribute(Class<?> dataObjectClass);

    /**
     * Indicates whether notes are supported by the given data object class, currently this
     * can only be true for business objects
     *
     * @param dataObjectClass - class for data object to check
     * @return boolean true if notes are supported for data object, false if notes are not supported
     */
    public boolean areNotesSupported(Class<?> dataObjectClass);

    /**
     * Builds a string that uniquely identifiers the data object instance
     *
     * <p>
     * Based on the metadata available for the class of the data object, the values for fields that uniquely
     * identify an instance are concatenated together into one string. For general data objects these fields
     * will be the primary key fields defined in the data dictionary. For the case of objects with type
     * <code>PersistableBusinessObject</code>, the object id field will be used.
     * </p>
     *
     * @param dataObject - data object instance to build identifier string for
     * @return String identifier string for data object
     */
    public String getDataObjectIdentifierString(Object dataObject);
}
