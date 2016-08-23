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
package org.kuali.kfs.krad.datadictionary.validation.processor;

import org.kuali.kfs.krad.datadictionary.exception.AttributeValidationException;
import org.kuali.kfs.krad.datadictionary.validation.ValidationUtils;
import org.kuali.kfs.krad.datadictionary.validation.capability.RangeConstrainable;
import org.kuali.kfs.krad.datadictionary.validation.constraint.Constraint;
import org.kuali.kfs.krad.datadictionary.validation.result.ConstraintValidationResult;
import org.kuali.kfs.krad.datadictionary.validation.result.DictionaryValidationResult;
import org.kuali.kfs.krad.datadictionary.validation.result.ProcessorResult;
import org.kuali.rice.core.api.data.DataType;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.kfs.krad.datadictionary.validation.AttributeValueReader;
import org.kuali.kfs.krad.datadictionary.validation.constraint.RangeConstraint;

import java.math.BigDecimal;
import java.util.Date;

/**
 * This class enforces range constraints - that is, constraints that keep a number or a date within a specific range. An attribute
 * that is {@link RangeConstrainable} will expose a minimum and maximum value, and these will be validated against the passed
 * value in the code below.
 *
 *
 */
public class RangeConstraintProcessor extends MandatoryElementConstraintProcessor<RangeConstraint> {

	private static final String CONSTRAINT_NAME = "range constraint";
    private static final String MIN_EXCLUSIVE_KEY = "validation.minExclusive";
    private static final String MAX_INCLUSIVE_KEY = "validation.maxInclusive";
    private static final String RANGE_KEY = "validation.range";

	/**
	 * @see ConstraintProcessor#process(DictionaryValidationResult, Object, org.kuali.rice.krad.datadictionary.validation.capability.Validatable, AttributeValueReader)
	 */
	@Override
	public ProcessorResult process(DictionaryValidationResult result, Object value, RangeConstraint constraint, AttributeValueReader attributeValueReader) throws AttributeValidationException {

		// Since any given definition that is range constrained only expressed a single min and max, it means that there is only a single constraint to impose
		return new ProcessorResult(processSingleRangeConstraint(result, value, constraint, attributeValueReader));
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
		return RangeConstraint.class;
	}

	protected ConstraintValidationResult processSingleRangeConstraint(DictionaryValidationResult result, Object value, RangeConstraint constraint, AttributeValueReader attributeValueReader) throws AttributeValidationException {
		// Can't process any range constraints on null values
		if (ValidationUtils.isNullOrEmpty(value) ||
                (constraint.getExclusiveMin() == null && constraint.getInclusiveMax() ==  null)){
			return result.addSkipped(attributeValueReader, CONSTRAINT_NAME);
        }


		// This is necessary because sometimes we'll be getting a string, for example, that represents a date.
		DataType dataType = constraint.getDataType();
		Object typedValue = value;

		if (dataType != null) {
			typedValue = ValidationUtils.convertToDataType(value, dataType, dateTimeService);
		}
        else if(value instanceof String){
            //assume string is a number of type double
            try{
                Double d = Double.parseDouble((String)value);
                typedValue = d;
            }
            catch(NumberFormatException n){
                //do nothing, typedValue is never reset
            }
        }

		// TODO: decide if there is any reason why the following would be insufficient - i.e. if something numeric could still be cast to String at this point
		if (typedValue instanceof Date)
			return validateRange(result, (Date)typedValue, constraint, attributeValueReader);
		else if (typedValue instanceof Number)
			return validateRange(result, (Number)typedValue, constraint, attributeValueReader);

		return result.addSkipped(attributeValueReader, CONSTRAINT_NAME);
	}

	protected ConstraintValidationResult validateRange(DictionaryValidationResult result, Date value, RangeConstraint constraint, AttributeValueReader attributeValueReader) throws IllegalArgumentException {

		Date date = value != null ? ValidationUtils.getDate(value, dateTimeService) : null;

        String inclusiveMaxText = constraint.getInclusiveMax();
        String exclusiveMinText = constraint.getExclusiveMin();

        Date inclusiveMax = inclusiveMaxText != null ? ValidationUtils.getDate(inclusiveMaxText, dateTimeService) : null;
        Date exclusiveMin = exclusiveMinText != null ? ValidationUtils.getDate(exclusiveMinText, dateTimeService) : null;

		return isInRange(result, date, inclusiveMax, inclusiveMaxText, exclusiveMin, exclusiveMinText, attributeValueReader);
	}

	protected ConstraintValidationResult validateRange(DictionaryValidationResult result, Number value, RangeConstraint constraint, AttributeValueReader attributeValueReader) throws IllegalArgumentException {

		// TODO: JLR - need a code review of the conversions below to make sure this is the best way to ensure accuracy across all numerics
        // This will throw NumberFormatException if the value is 'NaN' or infinity... probably shouldn't be a NFE but something more intelligible at a higher level
        BigDecimal number = value != null ? new BigDecimal(value.toString()) : null;

        String inclusiveMaxText = constraint.getInclusiveMax();
        String exclusiveMinText = constraint.getExclusiveMin();

        BigDecimal inclusiveMax = inclusiveMaxText != null ? new BigDecimal(inclusiveMaxText) : null;
        BigDecimal exclusiveMin = exclusiveMinText != null ? new BigDecimal(exclusiveMinText) : null;

		return isInRange(result, number, inclusiveMax, inclusiveMaxText, exclusiveMin, exclusiveMinText, attributeValueReader);
	}

	private <T> ConstraintValidationResult isInRange(DictionaryValidationResult result, T value, Comparable<T> inclusiveMax, String inclusiveMaxText, Comparable<T> exclusiveMin, String exclusiveMinText, AttributeValueReader attributeValueReader) {
        // What we want to know is that the maximum value is greater than or equal to the number passed (the number can be equal to the max, i.e. it's 'inclusive')
        ValidationUtils.Result lessThanMax = ValidationUtils.isLessThanOrEqual(value, inclusiveMax);
        // On the other hand, since the minimum is exclusive, we just want to make sure it's less than the number (the number can't be equal to the min, i.e. it's 'exclusive')
        ValidationUtils.Result greaterThanMin = ValidationUtils.isGreaterThan(value, exclusiveMin);

        // It's okay for one end of the range to be undefined - that's not an error. It's only an error if one of them is actually invalid.
        if (lessThanMax != ValidationUtils.Result.INVALID && greaterThanMin != ValidationUtils.Result.INVALID) {
        	// Of course, if they're both undefined then we didn't actually have a real constraint
        	if (lessThanMax == ValidationUtils.Result.UNDEFINED && greaterThanMin == ValidationUtils.Result.UNDEFINED)
        		return result.addNoConstraint(attributeValueReader, CONSTRAINT_NAME);

        	// In this case, we've succeeded
        	return result.addSuccess(attributeValueReader, CONSTRAINT_NAME);
        }

        // If both comparisons happened then if either comparison failed we can show the end user the expected range on both sides.
        if (lessThanMax != ValidationUtils.Result.UNDEFINED && greaterThanMin != ValidationUtils.Result.UNDEFINED)
        	return result.addError(RANGE_KEY, attributeValueReader, CONSTRAINT_NAME, RiceKeyConstants.ERROR_OUT_OF_RANGE, exclusiveMinText, inclusiveMaxText);
        // If it's the max comparison that fails, then just tell the end user what the max can be
        else if (lessThanMax == ValidationUtils.Result.INVALID)
        	return result.addError(MAX_INCLUSIVE_KEY, attributeValueReader, CONSTRAINT_NAME, RiceKeyConstants.ERROR_INCLUSIVE_MAX, inclusiveMaxText);
        // Otherwise, just tell them what the min can be
        else
        	return result.addError(MIN_EXCLUSIVE_KEY, attributeValueReader, CONSTRAINT_NAME, RiceKeyConstants.ERROR_EXCLUSIVE_MIN, exclusiveMinText);
	}

}
