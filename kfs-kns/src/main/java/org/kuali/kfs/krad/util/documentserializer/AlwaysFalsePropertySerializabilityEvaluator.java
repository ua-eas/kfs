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
package org.kuali.kfs.krad.util.documentserializer;

import org.kuali.kfs.krad.document.Document;

/**
 * This implementation of {@link PropertySerializabilityEvaluator} specifies that all properties of a document are serializable.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class AlwaysFalsePropertySerializabilityEvaluator extends PropertySerializabilityEvaluatorBase {

	@Override
	public void initializeEvaluatorForDocument(Document document) {
		// do nothing, because we know that every property is not serializable
	}

	@Override
	public boolean isPropertySerializable(SerializationState state,
			Object containingObject, String childPropertyName,
			Object childPropertyValue) {
		return false;
	}

}
