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
package org.kuali.kfs.kns.document.authorization;

import org.kuali.kfs.kns.web.ui.Field;
import org.kuali.kfs.kns.inquiry.InquiryRestrictions;

import java.util.HashSet;
import java.util.Set;

public class InquiryOrMaintenanceDocumentRestrictionsBase extends
		BusinessObjectRestrictionsBase implements InquiryOrMaintenanceDocumentRestrictions, InquiryRestrictions {
	private Set<String> hiddenFields;
	private Set<String> hiddenSectionIds;

	public void addHiddenField(String fieldName) {
		hiddenFields.add(fieldName);
	}

	public void addHiddenSectionId(String sectionId) {
		hiddenSectionIds.add(sectionId);
	}

	@Override
	public FieldRestriction getFieldRestriction(String fieldName) {
		FieldRestriction fieldRestriction = super
				.getFieldRestriction(fieldName);
		if (isHiddenField(fieldName)) {
			fieldRestriction = new FieldRestriction(fieldName, Field.HIDDEN);
		}
		return fieldRestriction;			
	}

	/**
	 * @see org.kuali.rice.krad.authorization.BusinessObjectRestrictionsBase#hasRestriction(java.lang.String)
	 */
	@Override
	public boolean hasRestriction(String fieldName) {
		return super.hasRestriction(fieldName) || isHiddenField(fieldName);
	}
	
	/**
	 * @see org.kuali.rice.krad.authorization.BusinessObjectRestrictionsBase#hasAnyFieldRestrictions()
	 */
	@Override
	public boolean hasAnyFieldRestrictions() {
		return super.hasAnyFieldRestrictions() || !hiddenFields.isEmpty();
	}

	@Override
	public void clearAllRestrictions() {
		super.clearAllRestrictions();
		hiddenFields = new HashSet<String>();
		hiddenSectionIds = new HashSet<String>();
	}

	/**
	 * @see org.kuali.rice.krad.authorization.InquiryOrMaintenanceDocumentRestrictions#isHiddenSectionId(java.lang.String)
	 */
	public boolean isHiddenSectionId(String sectionId) {
		return hiddenSectionIds.contains(sectionId);
	}

	protected boolean isHiddenField(String fieldName) {
		String normalizedFieldName = normalizeFieldName(fieldName);
		return hiddenFields.contains(normalizedFieldName);
	}
}
