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
package org.kuali.kfs.krad.datadictionary.validation.result;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class AttributeValidationResult implements Serializable {

	private String attributeName;
	private Map<String, ConstraintValidationResult> constraintValidationResultMap;

	public AttributeValidationResult(String attributeName) {
		this.attributeName = attributeName;
		this.constraintValidationResultMap = new LinkedHashMap<String, ConstraintValidationResult>();
	}

	public void addConstraintValidationResult(ConstraintValidationResult constraintValidationResult) {
		constraintValidationResultMap.put(constraintValidationResult.getConstraintName(), constraintValidationResult);
	}

	public Iterator<ConstraintValidationResult> iterator() {
		return constraintValidationResultMap.values().iterator();
	}

	protected ConstraintValidationResult getConstraintValidationResult(String constraintName) {
		ConstraintValidationResult constraintValidationResult = constraintValidationResultMap.get(constraintName);
		if (constraintValidationResult == null) {
			constraintValidationResult = new ConstraintValidationResult(constraintName);
			constraintValidationResultMap.put(constraintName, constraintValidationResult);
		}
		return constraintValidationResult;
	}

	/**
	 * @return the attributeName
	 */
	public String getAttributeName() {
		return this.attributeName;
	}

	/**
	 * @param attributeName the attributeName to set
	 */
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	/*
	private static final long serialVersionUID = 1L;

	protected String element;

	protected ErrorLevel level = ErrorLevel.OK;

	private String entryName;
	private String attributeName;
	private String errorKey;
	private String[] errorParameters;

	public AttributeValidationResult(String attributeName) {
		this.level = ErrorLevel.OK;
		this.attributeName = attributeName;
	}

	public AttributeValidationResult(String entryName, String attributeName) {
		this.level = ErrorLevel.OK;
		this.entryName = entryName;
		this.attributeName = attributeName;
	}

	public ErrorLevel getLevel() {
		return level;
	}

	public void setLevel(ErrorLevel level) {
		this.level = level;
	}

	public String getElement() {
		return element;
	}

	public void setElement(String element) {
		this.element = element;
	}


    public ErrorLevel getErrorLevel() {
        return level;
    }

    public void setError(String errorKey, String... errorParameters) {
    	this.level = ErrorLevel.ERROR;
    	this.errorKey = errorKey;
    	this.errorParameters = errorParameters;
    }

    public boolean isOk() {
        return getErrorLevel() == ErrorLevel.OK;
    }


    public boolean isWarn() {
        return getErrorLevel() == ErrorLevel.WARN;
    }

    public boolean isError() {
        return getErrorLevel() == ErrorLevel.ERROR;
    }

    public String toString(){
    	return "Entry: [" + entryName + "] Attribute: [" + attributeName + "] - " + errorKey + " data=[" + errorParameters + "]";
    }

	public String getEntryName() {
		return this.entryName;
	}

	public void setEntryName(String entryName) {
		this.entryName = entryName;
	}

	public String getAttributeName() {
		return this.attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getErrorKey() {
		return this.errorKey;
	}

	public void setErrorKey(String errorKey) {
		this.errorKey = errorKey;
	}

	public String[] getErrorParameters() {
		return this.errorParameters;
	}
	public void setErrorParameters(String[] errorParameters) {
		this.errorParameters = errorParameters;
	}
	*/

}
