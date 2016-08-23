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

import java.util.HashSet;
import java.util.Set;

import org.kuali.kfs.kns.web.ui.Field;

public class MaintenanceDocumentRestrictionsBase extends InquiryOrMaintenanceDocumentRestrictionsBase implements MaintenanceDocumentRestrictions {
	private Set<String> readOnlyFields;
	private Set<String> readOnlySectionIds;

	public MaintenanceDocumentRestrictionsBase() {
	}

	public void addReadOnlyField(String fieldName) {
		readOnlyFields.add(fieldName);
	}

	public void addReadOnlySectionId(String sectionId) {
		readOnlySectionIds.add(sectionId);
	}

	public Set<String> getReadOnlySectionIds() {
		return readOnlySectionIds;
	}

	@Override
	public FieldRestriction getFieldRestriction(String fieldName) {
		FieldRestriction fieldRestriction = super
				.getFieldRestriction(fieldName);
		if (fieldRestriction == null && isReadOnlyField(fieldName)) {
			fieldRestriction = new FieldRestriction(fieldName, Field.READONLY);
		}
		// TODO: next block could probably be removed since the superclass would return null for a read-only field
		if (Field.EDITABLE
				.equals(fieldRestriction.getKualiFieldDisplayFlag())
				&& isReadOnlyField(fieldName)) {
			fieldRestriction = new FieldRestriction(fieldName,
					Field.READONLY);
		}
		return fieldRestriction;
	}

	@Override
	public void clearAllRestrictions() {
		super.clearAllRestrictions();
		readOnlyFields = new HashSet<String>();
		readOnlySectionIds = new HashSet<String>();
	}

	protected boolean isReadOnlyField(String fieldName) {
		String normalizedFieldName = normalizeFieldName(fieldName);
		return readOnlyFields.contains(normalizedFieldName);
	}

	/**
	 * @see org.kuali.rice.krad.authorization.InquiryOrMaintenanceDocumentRestrictionsBase#hasAnyFieldRestrictions()
	 */
	@Override
	public boolean hasAnyFieldRestrictions() {
		return super.hasAnyFieldRestrictions() || !readOnlyFields.isEmpty();
	}

	/**
	 * @see org.kuali.rice.krad.authorization.InquiryOrMaintenanceDocumentRestrictionsBase#hasRestriction(java.lang.String)
	 */
	@Override
	public boolean hasRestriction(String fieldName) {
		return super.hasRestriction(fieldName) || isReadOnlyField(fieldName);
	}

	/**
	 * @see MaintenanceDocumentRestrictions#isReadOnlySectionId(java.lang.String)
	 */
	public boolean isReadOnlySectionId(String sectionId) {
		return readOnlySectionIds.contains(sectionId);
	}
}
