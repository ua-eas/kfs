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
import org.kuali.kfs.krad.datadictionary.validation.constraint.Constraint;
import org.kuali.kfs.krad.datadictionary.validation.constraint.SimpleConstraint;
import org.kuali.kfs.krad.datadictionary.validation.result.ConstraintValidationResult;
import org.kuali.kfs.krad.datadictionary.validation.result.DictionaryValidationResult;
import org.kuali.kfs.krad.datadictionary.validation.result.ProcessorResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Processor for simple constraint which takes out each constraining value it contains and calls the appropriate
 * processor
 */
public class SimpleConstraintProcessor extends MandatoryElementConstraintProcessor<SimpleConstraint> {

    private static final String CONSTRAINT_NAME = "simple constraint";

    RangeConstraintProcessor rangeConstraintProcessor = new RangeConstraintProcessor();
    LengthConstraintProcessor lengthConstraintProcessor = new LengthConstraintProcessor();
    ExistenceConstraintProcessor existenceConstraintProcessor = new ExistenceConstraintProcessor();
    DataTypeConstraintProcessor dataTypeConstraintProcessor = new DataTypeConstraintProcessor();

    /**
     * Processes the SimpleConstraint by calling process on the other smaller constraints it represents and
     * putting the results together in ProcessorResult
     *
     * @return
     * @throws AttributeValidationException
     * @see MandatoryElementConstraintProcessor#process(DictionaryValidationResult,
     * Object, Constraint,
     * AttributeValueReader)
     */
    @Override
    public ProcessorResult process(DictionaryValidationResult result, Object value, final SimpleConstraint constraint,
                                   AttributeValueReader attributeValueReader) throws AttributeValidationException {

        ProcessorResult dataTypePR = dataTypeConstraintProcessor.process(result, value, constraint,
            attributeValueReader);
        ProcessorResult existencePR = existenceConstraintProcessor.process(result, value, constraint,
            attributeValueReader);
        ProcessorResult rangePR = rangeConstraintProcessor.process(result, value, constraint, attributeValueReader);
        ProcessorResult lengthPR = lengthConstraintProcessor.process(result, value, constraint, attributeValueReader);
        List<ConstraintValidationResult> cvrList = new ArrayList<ConstraintValidationResult>();
        cvrList.addAll(existencePR.getConstraintValidationResults());
        cvrList.addAll(rangePR.getConstraintValidationResults());
        cvrList.addAll(lengthPR.getConstraintValidationResults());
        cvrList.addAll(dataTypePR.getConstraintValidationResults());
        return new ProcessorResult(cvrList);
    }

    @Override
    public String getName() {
        return CONSTRAINT_NAME;
    }

    @Override
    public Class<? extends Constraint> getConstraintType() {
        return SimpleConstraint.class;
    }
}
