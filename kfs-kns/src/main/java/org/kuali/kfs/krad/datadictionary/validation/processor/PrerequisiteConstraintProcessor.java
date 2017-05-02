/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
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
import org.kuali.kfs.krad.datadictionary.validation.AttributeValueReader;
import org.kuali.kfs.krad.datadictionary.validation.ValidationUtils;
import org.kuali.kfs.krad.datadictionary.validation.constraint.Constraint;
import org.kuali.kfs.krad.datadictionary.validation.constraint.PrerequisiteConstraint;
import org.kuali.kfs.krad.datadictionary.validation.result.ConstraintValidationResult;
import org.kuali.kfs.krad.datadictionary.validation.result.DictionaryValidationResult;
import org.kuali.kfs.krad.datadictionary.validation.result.ProcessorResult;

/**
 *
 *
 */
public class PrerequisiteConstraintProcessor extends BasePrerequisiteConstraintProcessor<PrerequisiteConstraint> {

    private static final String CONSTRAINT_NAME = "prerequisite constraint";


    /**
     * @see ConstraintProcessor#process(DictionaryValidationResult, Object, org.kuali.rice.krad.datadictionary.validation.capability.Validatable, AttributeValueReader)
     */
    @Override
    public ProcessorResult process(DictionaryValidationResult result, Object value, PrerequisiteConstraint constraint, AttributeValueReader attributeValueReader) throws AttributeValidationException {

        if (ValidationUtils.isNullOrEmpty(value))
            return new ProcessorResult(result.addSkipped(attributeValueReader, CONSTRAINT_NAME));


        ConstraintValidationResult constraintValidationResult = processPrerequisiteConstraint(constraint, attributeValueReader);

        if (constraint != null) {
            constraintValidationResult.setConstraintLabelKey(constraint.getLabelKey());
            constraintValidationResult.setErrorParameters(constraint.getValidationMessageParamsArray());
        }

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
        return PrerequisiteConstraint.class;
    }

}
