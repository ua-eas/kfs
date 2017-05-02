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

/**
 * This class represents metadata about the serializibility of a property during the document serialization proces..
 */
public interface PropertySerializabilityMetadata {
    /**
     * See docs for the elements of this enum
     */
    public enum PropertySerializability {
        /**
         * Indicates that the property represented by this metadata object should be serialized (i.e. have an open
         * and close XML tag rendered) as well as all of the property's primitives.  It does not mean that all child
         * non-primitive properties should be serialized.  Child non-primitives are only serialized if a call to
         * {@link PropertySerializabilityMetadata#getSerializableSubProperty(String)} returns a non-null result when
         * the child property name is passed in.
         */
        SERIALIZE_OBJECT_AND_ALL_PRIMITIVES,

        /**
         * Indicates that the property represented by this metadata object should be serialized (i.e. have an open
         * and close XML tag rendered).  Child properties (primitive or otherwise) are only serialized if a call to
         * {@link PropertySerializabilityMetadata#getSerializableSubProperty(String)} returns a non-null result when
         * the child property name is passed in.
         */
        SERIALIZE_OBJECT
    }

    /**
     * Returns the serializability of this property.  See {@link PropertySerializability}.
     *
     * @return
     */
    public PropertySerializability getPropertySerializability();

    /**
     * Returns the full path string of the property corresponding to this metadata.
     *
     * @return
     */
    public String getPathString();

    /**
     * Returns metadata bout a child property, if it exists
     *
     * @param childPropertyName the name of a child property, relative to this property (i.e. no .'s in the name)
     * @return null if there is no child property with the specified name, otherwise, metadata about the child
     */
    public PropertySerializabilityMetadata getSerializableChildProperty(String childPropertyName);
}
