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

import org.kuali.kfs.krad.document.Document;

/**
 * Specifies an implementation used during document workflow XML serialization that
 * will be able to determine whether a specific property is serializable
 */
public interface PropertySerializabilityEvaluator {

    /**
     * Initializes the evaluator so that calls to {@link #isPropertySerializable(DocumentSerializationState, Object, String, Object)} and
     * {@link #determinePropertyType(Object)} will function properly
     *
     * @param document the document instance
     */
    public void initializeEvaluatorForDocument(Document document);

    public void initializeEvaluatorForDataObject(Object businessObject);

    /**
     * Determines whether a child property of an object is serializable.
     *
     * @param state              Information about the properties that have been serialized so far
     * @param containingObject   The object containing the reference to childPropertyValue
     * @param childPropertyName  The name property to determine whether to serialize, relative to containingObject (i.e. not a nested attribute)
     * @param childPropertyValue If serializable, this property would be serialized by the serializer service.
     * @return
     */
    public boolean isPropertySerializable(SerializationState state, Object containingObject, String childPropertyName, Object childPropertyValue);

    /**
     * Determines the type of a object
     *
     * @param object
     * @return
     */
    public PropertyType determinePropertyType(Object object);
}
