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
package org.kuali.kfs.krad.datadictionary.validation;

import org.kuali.kfs.krad.datadictionary.exception.AttributeValidationException;
import org.kuali.rice.core.framework.persistence.jdbc.sql.SQLUtils;
import org.kuali.kfs.krad.util.DataTypeUtil;

import java.util.List;

/**
 * A class that implements the required accessors and legacy processing for an attribute value reader. This provides a convenient base class
 * from which other attribute value readers can be derived.
 *
 *
 */
public abstract class BaseAttributeValueReader implements AttributeValueReader {

	protected String entryName;
	protected String attributeName;

	@Override
	public List<String> getCleanSearchableValues(String attributeKey) throws AttributeValidationException {
		Class<?> attributeType = getType(attributeKey);
		Object rawValue = getValue(attributeKey);

		String attributeInValue = rawValue != null ? rawValue.toString() : "";
		String attributeDataType = DataTypeUtil.determineDataType(attributeType);
		return SQLUtils.getCleanedSearchableValues(attributeInValue, attributeDataType);
	}

	/**
	 * @return the currentName
	 */
	@Override
	public String getAttributeName() {
		return this.attributeName;
	}

	/**
	 * @param currentName the currentName to set
	 */
	@Override
	public void setAttributeName(String currentName) {
		this.attributeName = currentName;
	}

	/**
	 * @return the entryName
	 */
	@Override
	public String getEntryName() {
		return this.entryName;
	}

    @Override
    public abstract AttributeValueReader clone();

}
