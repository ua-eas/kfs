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

import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.kfs.krad.datadictionary.exception.AttributeValidationException;
import org.kuali.kfs.krad.datadictionary.validation.AttributeValueReader;
import org.kuali.kfs.krad.datadictionary.validation.ErrorLevel;
import org.kuali.kfs.krad.datadictionary.validation.ValidationUtils;
import org.kuali.kfs.krad.datadictionary.validation.constraint.Constraint;
import org.kuali.kfs.krad.datadictionary.validation.constraint.PrerequisiteConstraint;
import org.kuali.kfs.krad.datadictionary.validation.result.ConstraintValidationResult;

import java.util.Collection;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org) 
 */
public abstract class BasePrerequisiteConstraintProcessor<C extends Constraint> extends MandatoryElementConstraintProcessor<C> {
	
	protected ConstraintValidationResult processPrerequisiteConstraint(PrerequisiteConstraint constraint, AttributeValueReader attributeValueReader) throws AttributeValidationException {

		ConstraintValidationResult constraintValidationResult = new ConstraintValidationResult(getName());
		
		if (constraint == null) {
			constraintValidationResult.setStatus(ErrorLevel.NOCONSTRAINT);
			return constraintValidationResult;
		}
			
		
    	// TODO: Does this code need to be able to look at more than just the other immediate members of the object? 
        String attributeName = constraint.getPropertyName();
        
        if (ValidationUtils.isNullOrEmpty(attributeName)) {
        	throw new AttributeValidationException("Prerequisite constraints must include the name of the attribute that is required");
        }
        
        Object value = attributeValueReader.getValue(attributeName);

        boolean isSuccessful = true;

        if (value instanceof java.lang.String) {
        	isSuccessful = ValidationUtils.hasText((String) value);
        } else if (value instanceof Collection) {
        	isSuccessful = (((Collection<?>) value).size() > 0);
        } else {
        	isSuccessful = (null != value) ? true : false;
        }

        
        
        if (!isSuccessful) {        	
        	String label = attributeValueReader.getLabel(attributeName); 
        	if (label != null)
        		attributeName = label;
        	
        	constraintValidationResult.setError(RiceKeyConstants.ERROR_REQUIRES_FIELD, attributeName);
            constraintValidationResult.setConstraintLabelKey(constraint.getLabelKey());
            constraintValidationResult.setErrorParameters(constraint.getValidationMessageParamsArray());
        } 
        
        return constraintValidationResult;
    }

}
