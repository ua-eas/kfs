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
import org.kuali.kfs.krad.datadictionary.validation.constraint.Constraint;
import org.kuali.kfs.krad.datadictionary.validation.constraint.MustOccurConstraint;
import org.kuali.kfs.krad.datadictionary.validation.constraint.PrerequisiteConstraint;
import org.kuali.kfs.krad.datadictionary.validation.result.ConstraintValidationResult;
import org.kuali.kfs.krad.datadictionary.validation.result.DictionaryValidationResult;
import org.kuali.kfs.krad.datadictionary.validation.result.ProcessorResult;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.kfs.krad.datadictionary.validation.AttributeValueReader;
import org.kuali.kfs.krad.datadictionary.validation.ErrorLevel;

import java.util.List;

/**
 * 
 *  
 */
public class MustOccurConstraintProcessor extends BasePrerequisiteConstraintProcessor<MustOccurConstraint> {

	private static final String CONSTRAINT_NAME = "must occur constraint";
	
	/**
	 * @see ConstraintProcessor#process(DictionaryValidationResult, Object, Constraint, AttributeValueReader)
     */
	@Override
	public ProcessorResult process(DictionaryValidationResult result,
			Object value, MustOccurConstraint constraint, AttributeValueReader attributeValueReader)
			throws AttributeValidationException {

		if (ValidationUtils.isNullOrEmpty(value))
			return new ProcessorResult(result.addSkipped(attributeValueReader, CONSTRAINT_NAME));
		

		ConstraintValidationResult constraintValidationResult = new ConstraintValidationResult(CONSTRAINT_NAME);
        constraintValidationResult.setConstraintLabelKey(constraint.getLabelKey());
        constraintValidationResult.setErrorParameters(constraint.getValidationMessageParamsArray());

        // If the processing of this constraint is not successful then it's an error
		if (!processMustOccurConstraint(constraintValidationResult, constraint, attributeValueReader)) {
			// if attributeName is null, use the entry name since we are processing a must occur constraint that may be referencing multiple attributes
		    if (attributeValueReader.getAttributeName() == null){
		        constraintValidationResult.setAttributeName(attributeValueReader.getEntryName());
		    } else{
		        constraintValidationResult.setAttributeName(attributeValueReader.getAttributeName());
		        constraintValidationResult.setAttributePath(attributeValueReader.getPath());
		    }
			constraintValidationResult.setError(RiceKeyConstants.ERROR_OCCURS);
		} 

		// Store the label key (if one exists) for this constraint on the constraint validation result so it can be shown later

		// Add it to the DictionaryValidationResult object
		result.addConstraintValidationResult(attributeValueReader, constraintValidationResult);

		return new ProcessorResult(constraintValidationResult);

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
		return MustOccurConstraint.class;
	}
	
    protected boolean processMustOccurConstraint(ConstraintValidationResult topLevelResult, MustOccurConstraint constraint, AttributeValueReader attributeValueReader) throws AttributeValidationException {

        boolean isSuccessful = false;
        int trueCount = 0;
        
        List<PrerequisiteConstraint> prerequisiteConstraints = constraint.getPrerequisiteConstraints();
        if (prerequisiteConstraints != null) {
	        for (PrerequisiteConstraint prerequisiteConstraint : prerequisiteConstraints) {
	        	ConstraintValidationResult constraintValidationResult = processPrerequisiteConstraint(prerequisiteConstraint, attributeValueReader);
                constraintValidationResult.setConstraintLabelKey(prerequisiteConstraint.getLabelKey());
                constraintValidationResult.setErrorParameters(prerequisiteConstraint.getValidationMessageParamsArray());
	        	// Add the result of each prerequisite constraint validation to the top level result object as a child
	        	topLevelResult.addChild(constraintValidationResult);
	            trueCount += (constraintValidationResult.getStatus().getLevel() <= ErrorLevel.WARN.getLevel()) ? 1 : 0;
	        }
        }

        List<MustOccurConstraint> mustOccurConstraints = constraint.getMustOccurConstraints();
        if (mustOccurConstraints != null) {
	        for (MustOccurConstraint mustOccurConstraint : mustOccurConstraints) {
	        	// Create a new constraint validation result for this must occur constraint and make it child of the top-level constraint, 
	        	// then pass it in to the recursive call so that prerequisite constraints can be placed under it
	        	ConstraintValidationResult constraintValidationResult = new ConstraintValidationResult(CONSTRAINT_NAME);
                constraintValidationResult.setConstraintLabelKey(mustOccurConstraint.getLabelKey());
                constraintValidationResult.setErrorParameters(mustOccurConstraint.getValidationMessageParamsArray());
	        	topLevelResult.addChild(constraintValidationResult);
	            trueCount += (processMustOccurConstraint(constraintValidationResult, mustOccurConstraint, attributeValueReader)) ? 1 : 0;
	        }
        }

        int minimum = constraint.getMin() != null ? constraint.getMin().intValue() : 0;
        int maximum = constraint.getMax() != null ? constraint.getMax().intValue() : 0;
        
        isSuccessful = (trueCount >= minimum && trueCount <= maximum) ? true : false;

        return isSuccessful;
    }

}
