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
package org.kuali.kfs.krad.util.documentserializer;

import java.util.ArrayList;
import java.util.List;

/**
 * This object keeps track of most of the open tags while a document is serialized.  Note that instances of this class
 * may not necessarily hold all open tags of a document while it is being serialized.  For example, tags enclosing list elements
 * and map entries are not contained within here.  See {@link DocumentSerializerServiceImpl} to determine when this object's state
 * is modified.
 * <p>
 * This class's manipulators behave much like a stack, but it has random access characteristics like an array.
 */
public class DocumentSerializationState {
    protected class SerializationPropertyElement {
        private String elementName;
        private PropertyType propertyType;

        public SerializationPropertyElement(String elementName, PropertyType propertyType) {
            this.elementName = elementName;
            this.propertyType = propertyType;
        }

        public String getElementName() {
            return this.elementName;
        }

        public PropertyType getPropertyType() {
            return this.propertyType;
        }
    }

    private List<SerializationPropertyElement> pathElements;

    public DocumentSerializationState() {
        pathElements = new ArrayList<SerializationPropertyElement>();
    }

    /**
     * The number of property elements in this state object.
     *
     * @return
     */
    public int numPropertyElements() {
        return pathElements.size();
    }

    /**
     * Adds an additional state element into this object.
     *
     * @param elementName
     * @param propertyType the type of the property when it was serialized
     */
    public void addSerializedProperty(String elementName, PropertyType propertyType) {
        SerializationPropertyElement serializationPropertyElement = new SerializationPropertyElement(elementName, propertyType);
        pathElements.add(serializationPropertyElement);
    }

    /**
     * Removes the last added serialized property
     */
    public void removeSerializedProperty() {
        pathElements.remove(pathElements.size() - 1);
    }

    /**
     * Retrieves the element name of the state element.  A parameter value of 0 represents the first element that was added
     * by calling {@link #addSerializedProperty(String, PropertyType)} that hasn't been removed, and a value of
     * {@link #numPropertyElements()} - 1 represents the element last added that hasn't been removed.
     *
     * @param propertyIndex most be between 0 and the value returned by {@link #numPropertyElements()} - 1
     * @return
     */
    public String getElementName(int propertyIndex) {
        return pathElements.get(propertyIndex).getElementName();
    }

    /**
     * Retrieves the property type of the state element.  A parameter value of 0 represents the first element that was added
     * by calling {@link #addSerializedProperty(String, PropertyType)} that hasn't been removed, and a value of
     * {@link #numPropertyElements()} - 1 represents the element last added that hasn't been removed.
     *
     * @param propertyIndex most be between 0 and the value returned by {@link #numPropertyElements()} - 1
     * @return
     */
    public PropertyType getPropertyType(int propertyIndex) {
        return pathElements.get(propertyIndex).getPropertyType();
    }
}
