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
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.kfs.krad.datadictionary.validation.AttributeValueReader;
import org.kuali.kfs.krad.datadictionary.validation.ValidationUtils;
import org.kuali.kfs.krad.datadictionary.validation.ValidationUtils.Result;
import org.kuali.kfs.krad.datadictionary.validation.constraint.CollectionSizeConstraint;
import org.kuali.kfs.krad.datadictionary.validation.constraint.Constraint;
import org.kuali.kfs.krad.datadictionary.validation.result.ProcessorResult;

import java.util.Collection;

/**
 * This class validates attributes that are collection size constrained - ones that can only have between x and y number 
 * 
 *  
 */
public class CollectionSizeConstraintProcessor implements CollectionConstraintProcessor<Collection<?>, CollectionSizeConstraint> {

	private static final String CONSTRAINT_NAME = "collection size constraint";
	
	/**
	 * @see ConstraintProcessor#process(DictionaryValidationResult, Object, org.kuali.rice.krad.datadictionary.validation.capability.Validatable, AttributeValueReader)
	 */
	@Override
	public ProcessorResult process(DictionaryValidationResult result, Collection<?> collection, CollectionSizeConstraint constraint, AttributeValueReader attributeValueReader) throws AttributeValidationException {
		
		// To accommodate the needs of other processors, the ConstraintProcessor.process() method returns a list of ConstraintValidationResult objects
		// but since a definition that is collection size constrained only provides a single max and minimum, there is effectively a single constraint
		// being imposed.
        return new ProcessorResult(processSingleCollectionSizeConstraint(result, collection, constraint, attributeValueReader));
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
		return CollectionSizeConstraint.class;
	}

	/**
	 * @see ConstraintProcessor#isOptional()
	 */
	@Override
	public boolean isOptional() {
		return false;
	}

	protected ConstraintValidationResult processSingleCollectionSizeConstraint(DictionaryValidationResult result, Collection<?> collection, CollectionSizeConstraint constraint, AttributeValueReader attributeValueReader) throws AttributeValidationException {
	    Integer sizeOfCollection = new Integer(0);
	    if (collection != null){
	        sizeOfCollection = Integer.valueOf(collection.size());
	    }
		
		Integer maxOccurances = constraint.getMaximumNumberOfElements();
		Integer minOccurances = constraint.getMinimumNumberOfElements();
		
		Result lessThanMax = ValidationUtils.isLessThanOrEqual(sizeOfCollection, maxOccurances);
		Result greaterThanMin = ValidationUtils.isGreaterThanOrEqual(sizeOfCollection, minOccurances);

		// It's okay for one end of the range to be undefined - that's not an error. It's only an error if one of them is invalid 
        if (lessThanMax != Result.INVALID && greaterThanMin != Result.INVALID) { 
        	// Of course, if they're both undefined then we didn't actually have a real constraint
        	if (lessThanMax == Result.UNDEFINED && greaterThanMin == Result.UNDEFINED)
        		return result.addNoConstraint(attributeValueReader, CONSTRAINT_NAME);
        	
        	// In this case, we've succeeded
        	return result.addSuccess(attributeValueReader, CONSTRAINT_NAME);
        }
        
		String maxErrorParameter = maxOccurances != null ? maxOccurances.toString() : null;
		String minErrorParameter = minOccurances != null ? minOccurances.toString() : null;
        
        // If both comparisons happened then if either comparison failed we can show the end user the expected range on both sides.
        if (lessThanMax != Result.UNDEFINED && greaterThanMin != Result.UNDEFINED) 
        	return result.addError(attributeValueReader, CONSTRAINT_NAME, RiceKeyConstants.ERROR_QUANTITY_RANGE, minErrorParameter, maxErrorParameter);
        // If it's the max comparison that fails, then just tell the end user what the max can be
        else if (lessThanMax == Result.INVALID)
        	return result.addError(attributeValueReader, CONSTRAINT_NAME, RiceKeyConstants.ERROR_MAX_OCCURS, maxErrorParameter);
        // Otherwise, just tell them what the min can be
        else 
        	return result.addError(attributeValueReader, CONSTRAINT_NAME, RiceKeyConstants.ERROR_MIN_OCCURS, minErrorParameter);
        
        // Obviously the last else above is unnecessary, since anything after it is dead code, but keeping it seems clearer than dropping it
	}
	
}
