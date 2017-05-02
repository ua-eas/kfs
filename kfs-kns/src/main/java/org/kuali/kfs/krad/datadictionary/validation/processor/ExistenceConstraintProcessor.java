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
import org.kuali.kfs.krad.datadictionary.validation.DictionaryObjectAttributeValueReader;
import org.kuali.kfs.krad.datadictionary.validation.ValidationUtils;
import org.kuali.kfs.krad.datadictionary.validation.constraint.Constraint;
import org.kuali.kfs.krad.datadictionary.validation.constraint.ExistenceConstraint;
import org.kuali.kfs.krad.datadictionary.validation.result.ConstraintValidationResult;
import org.kuali.kfs.krad.datadictionary.validation.result.DictionaryValidationResult;
import org.kuali.kfs.krad.datadictionary.validation.result.ProcessorResult;
import org.kuali.rice.core.api.util.RiceKeyConstants;

/**
 *
 *
 */
public class ExistenceConstraintProcessor extends OptionalElementConstraintProcessor<ExistenceConstraint> {

    private static final String CONSTRAINT_NAME = "existence constraint";

    /**
     * @see ConstraintProcessor#process(DictionaryValidationResult, Object, Constraint, AttributeValueReader) \
     */
    @Override
    public ProcessorResult process(DictionaryValidationResult result, Object value, ExistenceConstraint constraint, AttributeValueReader attributeValueReader) throws AttributeValidationException {

        // To accommodate the needs of other processors, the ConstraintProcessor.process() method returns a list of ConstraintValidationResult objects
        // but since a definition that is existence constrained only provides a single isRequired field, there is effectively a single constraint
        // being imposed.
        return new ProcessorResult(processSingleExistenceConstraint(result, value, constraint, attributeValueReader));
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
        return ExistenceConstraint.class;
    }

    protected ConstraintValidationResult processSingleExistenceConstraint(DictionaryValidationResult result, Object value, ExistenceConstraint constraint, AttributeValueReader attributeValueReader) throws AttributeValidationException {
        // If it's not set, then there's no constraint
        if (constraint.isRequired() == null)
            return result.addNoConstraint(attributeValueReader, CONSTRAINT_NAME);

        if (constraint.isRequired().booleanValue() && !skipConstraint(attributeValueReader)) {
            // If this attribute is required and the value is null then
            if (ValidationUtils.isNullOrEmpty(value))
                return result.addError(attributeValueReader, CONSTRAINT_NAME, RiceKeyConstants.ERROR_REQUIRED, attributeValueReader.getLabel(attributeValueReader.getAttributeName()));
            return result.addSuccess(attributeValueReader, CONSTRAINT_NAME);
        }

        return result.addSkipped(attributeValueReader, CONSTRAINT_NAME);
    }

    /**
     * Checks to see if existence constraint should be skipped.  Required constraint should be skipped if it is an attribute of a complex
     * attribute and the complex attribute is not required.
     *
     * @param attributeValueReader
     * @return
     */
    private boolean skipConstraint(AttributeValueReader attributeValueReader) {
        boolean skipConstraint = false;
        if (attributeValueReader instanceof DictionaryObjectAttributeValueReader) {
            DictionaryObjectAttributeValueReader dictionaryValueReader = (DictionaryObjectAttributeValueReader) attributeValueReader;
            skipConstraint = dictionaryValueReader.isNestedAttribute() && dictionaryValueReader.isParentAttributeNull();
        }
        return skipConstraint;
    }


}
