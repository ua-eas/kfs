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
package org.kuali.kfs.krad.datadictionary.validation.processor;

import org.kuali.kfs.krad.datadictionary.exception.AttributeValidationException;
import org.kuali.kfs.krad.datadictionary.validation.ValidationUtils;
import org.kuali.kfs.krad.datadictionary.validation.capability.Constrainable;
import org.kuali.kfs.krad.datadictionary.validation.constraint.Constraint;
import org.kuali.kfs.krad.datadictionary.validation.constraint.DataTypeConstraint;
import org.kuali.kfs.krad.datadictionary.validation.result.ConstraintValidationResult;
import org.kuali.kfs.krad.datadictionary.validation.result.DictionaryValidationResult;
import org.kuali.kfs.krad.datadictionary.validation.result.ProcessorResult;
import org.kuali.rice.core.api.data.DataType;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.kfs.krad.datadictionary.validation.AttributeValueReader;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org) 
 */
public class DataTypeConstraintProcessor extends MandatoryElementConstraintProcessor<DataTypeConstraint> {

	private static final String CONSTRAINT_NAME = "data type constraint";
	
	/**
	 * @see ConstraintProcessor#process(DictionaryValidationResult, Object, Constrainable, AttributeValueReader)
	 */
	@Override
	public ProcessorResult process(DictionaryValidationResult result, Object value, DataTypeConstraint constraint, AttributeValueReader attributeValueReader)
			throws AttributeValidationException {

		DataType dataType = constraint.getDataType();
		
		return new ProcessorResult(processDataTypeConstraint(result, dataType, value, attributeValueReader));
	}
	
	@Override 
	public String getName() {
		return CONSTRAINT_NAME;
	}

	/**
	 * @see ConstraintProcessor#getConstraintType()
	 */
	@Override
	public Class<? extends Constraint> getConstraintType() {
		return DataTypeConstraint.class;
	}
	
	protected ConstraintValidationResult processDataTypeConstraint(DictionaryValidationResult result, DataType dataType, Object value, AttributeValueReader attributeValueReader) {
		if (dataType == null)
			return result.addNoConstraint(attributeValueReader, CONSTRAINT_NAME);
		
		if (ValidationUtils.isNullOrEmpty(value))
			return result.addSkipped(attributeValueReader, CONSTRAINT_NAME);
		
		try {
			ValidationUtils.convertToDataType(value, dataType, dateTimeService);
		} catch (Exception e) {		
			switch (dataType) {
			case BOOLEAN:
				return result.addError(attributeValueReader, CONSTRAINT_NAME, RiceKeyConstants.ERROR_BOOLEAN);
			case INTEGER:
				return result.addError(attributeValueReader, CONSTRAINT_NAME, RiceKeyConstants.ERROR_INTEGER);
			case LONG:
				return result.addError(attributeValueReader, CONSTRAINT_NAME, RiceKeyConstants.ERROR_LONG);
			case DOUBLE:
				return result.addError(attributeValueReader, CONSTRAINT_NAME, RiceKeyConstants.ERROR_BIG_DECIMAL);
			case FLOAT:
				return result.addError(attributeValueReader, CONSTRAINT_NAME, RiceKeyConstants.ERROR_BIG_DECIMAL);
			case TRUNCATED_DATE:
			case DATE:
				return result.addError(attributeValueReader, CONSTRAINT_NAME, RiceKeyConstants.ERROR_BIG_DECIMAL);
			case STRING:
			}
		}
		
		// If we get here then it was a success!
		return result.addSuccess(attributeValueReader, CONSTRAINT_NAME);
	}
	
}
