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
package org.kuali.kfs.kns.document.authorization;

import org.kuali.kfs.kns.web.ui.Field;
import org.kuali.kfs.krad.datadictionary.mask.MaskFormatter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BusinessObjectRestrictionsBase implements
		BusinessObjectRestrictions {
	private Map<String, MaskFormatter> partiallyMaskedFields;
	private Map<String, MaskFormatter> fullyMaskedFields;

	protected Set<String> allRestrictedFields;

	public BusinessObjectRestrictionsBase() {
		clearAllRestrictions();
	}

	public boolean hasAnyFieldRestrictions() {
		return !partiallyMaskedFields.isEmpty() || !fullyMaskedFields.isEmpty();
	}

	public boolean hasRestriction(String fieldName) {
		return isPartiallyMaskedField(fieldName) || isFullyMaskedField(fieldName);
	}

	public void addFullyMaskedField(String fieldName,
			MaskFormatter maskFormatter) {
		fullyMaskedFields.put(fieldName, maskFormatter);
	}

	public void addPartiallyMaskedField(String fieldName,
			MaskFormatter maskFormatter) {
		partiallyMaskedFields.put(fieldName, maskFormatter);
	}

	/**
	 *
	 * This method returns the authorization setting for the given field name.
	 * If the field name is not restricted in any way, a default full-editable
	 * value is returned.
	 *
	 * @param fieldName
	 *            - name of field to get authorization restrictions for.
	 * @return a populated FieldAuthorization class for this field
	 *
	 */
	public FieldRestriction getFieldRestriction(String fieldName) {
		if (hasRestriction(fieldName)) {
			FieldRestriction fieldRestriction = null;
			if (isPartiallyMaskedField(fieldName)) {
				fieldRestriction = new FieldRestriction(fieldName,
						Field.PARTIALLY_MASKED);
				fieldRestriction.setMaskFormatter(partiallyMaskedFields
						.get(normalizeFieldName(fieldName)));
			}
			if (isFullyMaskedField(fieldName)) {
				fieldRestriction = new FieldRestriction(fieldName, Field.MASKED);
				fieldRestriction.setMaskFormatter(fullyMaskedFields
						.get(normalizeFieldName(fieldName)));
			}
			return fieldRestriction;
		} else {
			return new FieldRestriction(fieldName, Field.EDITABLE);
		}
	}

	public void clearAllRestrictions() {
		partiallyMaskedFields = new HashMap<String, MaskFormatter>();
		fullyMaskedFields = new HashMap<String, MaskFormatter>();
		allRestrictedFields = null;
	}


	/**
	 * This method is used to convert field names on forms into a format that's compatible with field names
	 * that are registered with a restriction.  The base implementation of this method just returns the string.
	 *
	 * @param fieldName The field name that would be rendered on a form
	 * @return
	 */
	protected String normalizeFieldName(String fieldName) {
		return fieldName;
	}

	protected boolean isFullyMaskedField(String fieldName) {
		String normalizedFieldName = normalizeFieldName(fieldName);
		return fullyMaskedFields.containsKey(normalizedFieldName);
	}

	protected boolean isPartiallyMaskedField(String fieldName) {
		String normalizedFieldName = normalizeFieldName(fieldName);
		return partiallyMaskedFields.containsKey(normalizedFieldName);
	}
}
