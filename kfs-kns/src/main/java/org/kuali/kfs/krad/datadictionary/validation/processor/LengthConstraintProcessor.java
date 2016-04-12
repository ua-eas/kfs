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
import org.kuali.kfs.krad.datadictionary.validation.result.ConstraintValidationResult;
import org.kuali.kfs.krad.datadictionary.validation.result.DictionaryValidationResult;
import org.kuali.rice.core.api.data.DataType;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.kfs.krad.datadictionary.validation.AttributeValueReader;
import org.kuali.kfs.krad.datadictionary.validation.ValidationUtils;
import org.kuali.kfs.krad.datadictionary.validation.ValidationUtils.Result;
import org.kuali.kfs.krad.datadictionary.validation.constraint.Constraint;
import org.kuali.kfs.krad.datadictionary.validation.constraint.LengthConstraint;
import org.kuali.kfs.krad.datadictionary.validation.result.ProcessorResult;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org) 
 */
public class LengthConstraintProcessor extends MandatoryElementConstraintProcessor<LengthConstraint> {

    private static final String MIN_LENGTH_KEY = "validation.minLengthConditional";
    private static final String MAX_LENGTH_KEY = "validation.maxLengthConditional";
    private static final String RANGE_KEY = "validation.lengthRange";

	private static final String CONSTRAINT_NAME = "length constraint";
	
	/**
	 * @see ConstraintProcessor#process(DictionaryValidationResult, Object, org.kuali.rice.krad.datadictionary.validation.capability.Validatable, AttributeValueReader)
	 */
	@Override
	public ProcessorResult process(DictionaryValidationResult result, Object value, LengthConstraint constraint, AttributeValueReader attributeValueReader) throws AttributeValidationException {

		// To accommodate the needs of other processors, the ConstraintProcessor.process() method returns a list of ConstraintValidationResult objects
		// but since a definition that is length constrained only constrains a single field, there is effectively always a single constraint
		// being imposed
		return new ProcessorResult(processSingleLengthConstraint(result, value, constraint, attributeValueReader));
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
		return LengthConstraint.class;
	}
	
	protected ConstraintValidationResult processSingleLengthConstraint(DictionaryValidationResult result, Object value, LengthConstraint constraint, AttributeValueReader attributeValueReader) throws AttributeValidationException {
		// Can't process any range constraints on null values
		if (ValidationUtils.isNullOrEmpty(value))
			return result.addSkipped(attributeValueReader, CONSTRAINT_NAME);

		DataType dataType = constraint.getDataType();
		Object typedValue = value;

		if (dataType != null) {
			typedValue = ValidationUtils.convertToDataType(value, dataType, dateTimeService);
		}	

		// The only thing that can have a length constraint currently is a string. 
		if (typedValue instanceof String) {
			return validateLength(result, (String)typedValue, constraint, attributeValueReader);
		} 
		
		return result.addSkipped(attributeValueReader, CONSTRAINT_NAME);
	}
	
	
	protected ConstraintValidationResult validateLength(DictionaryValidationResult result, String value, LengthConstraint constraint, AttributeValueReader attributeValueReader) throws IllegalArgumentException {
		Integer valueLength = Integer.valueOf(value.length());
		
		Integer maxLength = constraint.getMaxLength();
		Integer minLength = constraint.getMinLength();
		
		Result lessThanMax = ValidationUtils.isLessThanOrEqual(valueLength, maxLength);
		Result greaterThanMin = ValidationUtils.isGreaterThanOrEqual(valueLength, minLength);
		
        // It's okay for one end of the range to be undefined - that's not an error. It's only an error if one of them is invalid 
        if (lessThanMax != Result.INVALID && greaterThanMin != Result.INVALID) { 
        	// Of course, if they're both undefined then we didn't actually have a real constraint
        	if (lessThanMax == Result.UNDEFINED && greaterThanMin == Result.UNDEFINED)
        		return result.addNoConstraint(attributeValueReader, CONSTRAINT_NAME);
        	
        	// In this case, we've succeeded
        	return result.addSuccess(attributeValueReader, CONSTRAINT_NAME);
        }
        
		String maxErrorParameter = maxLength != null ? maxLength.toString() : null;
		String minErrorParameter = minLength != null ? minLength.toString() : null;
        
        // If both comparisons happened then if either comparison failed we can show the end user the expected range on both sides.
        if (lessThanMax != Result.UNDEFINED && greaterThanMin != Result.UNDEFINED) 
        	return result.addError(RANGE_KEY, attributeValueReader, CONSTRAINT_NAME, RiceKeyConstants.ERROR_OUT_OF_RANGE, minErrorParameter, maxErrorParameter);
        // If it's the max comparison that fails, then just tell the end user what the max can be
        else if (lessThanMax == Result.INVALID)
        	return result.addError(MAX_LENGTH_KEY, attributeValueReader, CONSTRAINT_NAME, RiceKeyConstants.ERROR_INCLUSIVE_MAX, maxErrorParameter);
        // Otherwise, just tell them what the min can be
        else 
        	return result.addError(MIN_LENGTH_KEY, attributeValueReader, CONSTRAINT_NAME, RiceKeyConstants.ERROR_EXCLUSIVE_MIN, minErrorParameter);
        
	}

}
