/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2015 The Kuali Foundation
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
package org.kuali.kfs.krad.bo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a relationship to another class that exists within a given parent class
 *
 * <p>
 * In terms of relational db, this can be thought of as a foreign key relationship. That is one of the
 * properties (fields) of the parent class (parent table) has a relationship to another class (table)
 * </p>
 *
 * 
 */
public class DataObjectRelationship implements Serializable {
    private Class<?> relatedClass;
    private Class<?> parentClass;
    private String parentAttributeName;
    private String userVisibleIdentifierKey = null;

    private Map<String, String> parentToChildReferences = new HashMap<String, String>(4);

    public DataObjectRelationship() {
    }

    public DataObjectRelationship(Class<?> parentClass, String parentAttributeName, Class<?> relatedClass) {
        super();

        this.relatedClass = relatedClass;
        this.parentClass = parentClass;
        this.parentAttributeName = parentAttributeName;
    }

    /**
     * Returns the Class that contains the relationship (the parent)
     *
     * @return Class<?> parent class
     */
    public Class<?> getParentClass() {
        return parentClass;
    }

    /**
     * Returns the class the attribute within the parent class has a relationship to
     *
     * @return Class<?> related class
     */
    public Class<?> getRelatedClass() {
        return this.relatedClass;
    }

    /**
     * Returns the name of the attribute within the parent class that holds the related class object
     *
     * <p>
     * Note this attribute should be of type given by #getRelatedClass
     * </p>
     *
     * @return String attribute name within parent class
     */
    public String getParentAttributeName() {
        return parentAttributeName;
    }

    /**
     * Provides a Map of attribute pairs that make up the relationship, where the map key
     * is the attribute name on the parent class and the map value is the attribute name on
     * the related class
     *
     * @return Map<String, String> related attribute pairs
     */
    public Map<String, String> getParentToChildReferences() {
        return parentToChildReferences;
    }

    /**
     * Setter for the Map of attributes that participate in the relationship
     *
     * @param referenceAttributes
     */
    public void setParentToChildReferences(Map<String, String> referenceAttributes) {
        this.parentToChildReferences = referenceAttributes;
    }

    /**
     * Retrieves the attribute within the parent class that is related to the given attribute of
     * the related class by the relationship represented by this object
     *
     * @param childAttributeName - name of attribute within the related class to find parent attribute for
     * @return String attribute name within parent class
     */
    public String getParentAttributeForChildAttribute(String childAttributeName) {
        for (Map.Entry<String, String> entry : parentToChildReferences.entrySet()) {
            if (entry.getValue().equals(childAttributeName)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Retrieves the attribute within the related class that is related to the given attribute of the
     * parent class by the relationship represented by this object
     *
     * @param parentAttributeName - name of attribute within the parent class to find related (child) attribute for
     * @return String attribute name within the related class
     */
    public String getChildAttributeForParentAttribute(String parentAttributeName) {
        return parentToChildReferences.get(parentAttributeName);
    }

    public String getUserVisibleIdentifierKey() {
        return userVisibleIdentifierKey;
    }

    public void setUserVisibleIdentifierKey(String userVisibleIdentifierKey) {
        this.userVisibleIdentifierKey = userVisibleIdentifierKey;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Relationship: ").append(parentClass.getName()).append(" -> ").append(relatedClass.getName());
        for (Map.Entry<String, String> refs : parentToChildReferences.entrySet()) {
            sb.append("\n   ").append(refs.getKey()).append(" -> ").append(refs.getValue());
        }
        return sb.toString();
    }
}
