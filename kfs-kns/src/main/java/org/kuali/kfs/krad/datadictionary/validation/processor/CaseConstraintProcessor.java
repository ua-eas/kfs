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

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.krad.datadictionary.DataDictionaryEntry;
import org.kuali.kfs.krad.datadictionary.exception.AttributeValidationException;
import org.kuali.kfs.krad.datadictionary.validation.AttributeValueReader;
import org.kuali.kfs.krad.datadictionary.validation.DictionaryObjectAttributeValueReader;
import org.kuali.kfs.krad.datadictionary.validation.ValidationUtils;
import org.kuali.kfs.krad.datadictionary.validation.capability.Constrainable;
import org.kuali.kfs.krad.datadictionary.validation.capability.HierarchicallyConstrainable;
import org.kuali.kfs.krad.datadictionary.validation.constraint.CaseConstraint;
import org.kuali.kfs.krad.datadictionary.validation.constraint.Constraint;
import org.kuali.kfs.krad.datadictionary.validation.constraint.DataTypeConstraint;
import org.kuali.kfs.krad.datadictionary.validation.constraint.WhenConstraint;
import org.kuali.kfs.krad.datadictionary.validation.result.DictionaryValidationResult;
import org.kuali.kfs.krad.datadictionary.validation.result.ProcessorResult;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.core.api.data.DataType;

/**
 * This object processes 'case constraints', which are constraints that are imposed only in specific cases, for
 * example,
 * when a value is
 * equal to some constant, or greater than some limit.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CaseConstraintProcessor extends MandatoryElementConstraintProcessor<CaseConstraint> {

    private static final String CONSTRAINT_NAME = "case constraint";

    /**
     * @see ConstraintProcessor#process(DictionaryValidationResult,
     *      Object, Constrainable,
     *      AttributeValueReader)
     */
    @Override
    public ProcessorResult process(DictionaryValidationResult result, Object value, CaseConstraint caseConstraint,
            AttributeValueReader attributeValueReader) throws AttributeValidationException {

        // Don't process this constraint if it's null
        if (null == caseConstraint) {
            return new ProcessorResult(result.addNoConstraint(attributeValueReader, CONSTRAINT_NAME));
        }
        AttributeValueReader constraintAttributeReader = attributeValueReader.clone();

        String operator = (ValidationUtils.hasText(caseConstraint.getOperator())) ? caseConstraint.getOperator() :
                "EQUALS";
        AttributeValueReader fieldPathReader = (ValidationUtils.hasText(caseConstraint.getPropertyName())) ?
                getChildAttributeValueReader(caseConstraint.getPropertyName(), attributeValueReader) :
                attributeValueReader;

        Constrainable caseField = (null != fieldPathReader) ? fieldPathReader.getDefinition(
                fieldPathReader.getAttributeName()) : null;
        Object fieldValue = (null != fieldPathReader) ? fieldPathReader.getValue(fieldPathReader.getAttributeName()) :
                value;
        DataType fieldDataType = (null != caseField && caseField instanceof DataTypeConstraint) ?
                ((DataTypeConstraint) caseField).getDataType() : null;

        // Default to a string comparison
        if (fieldDataType == null) {
            fieldDataType = DataType.STRING;
        }

        // If fieldValue is null then skip Case check
        if (null == fieldValue) {
            // FIXME: not sure if the definition and attribute value reader should change under this case
            return new ProcessorResult(result.addSkipped(attributeValueReader, CONSTRAINT_NAME), caseField,
                    fieldPathReader);
        }

        List<Constraint> constraints = new ArrayList<Constraint>();
        // Extract value for field Key
        for (WhenConstraint wc : caseConstraint.getWhenConstraint()) {
            evaluateWhenConstraint(fieldValue, fieldDataType, operator, caseConstraint, wc, attributeValueReader,
                    constraints);
        }
        if (!constraints.isEmpty()) {
            return new ProcessorResult(result.addSuccess(attributeValueReader, CONSTRAINT_NAME), null,
                    constraintAttributeReader, constraints);
        }

        // Assuming that not finding any case constraints is equivalent to 'skipping' the constraint
        return new ProcessorResult(result.addSkipped(attributeValueReader, CONSTRAINT_NAME));
    }

    private void evaluateWhenConstraint(Object fieldValue, DataType fieldDataType, String operator,
            CaseConstraint caseConstraint, WhenConstraint wc, AttributeValueReader attributeValueReader,
            List<Constraint> constraints) {
        if (ValidationUtils.hasText(wc.getValuePath())) {
            Object whenValue = null;

            AttributeValueReader whenValueReader = getChildAttributeValueReader(wc.getValuePath(),
                    attributeValueReader);
            whenValue = whenValueReader.getValue(whenValueReader.getAttributeName());

            if (ValidationUtils.compareValues(fieldValue, whenValue, fieldDataType, operator,
                    caseConstraint.isCaseSensitive(), dateTimeService) && null != wc.getConstraint()) {
                constraints.add(wc.getConstraint());
            }
        } else {
            List<Object> whenValueList = wc.getValues();

            for (Object whenValue : whenValueList) {
                if (ValidationUtils.compareValues(fieldValue, whenValue, fieldDataType, operator,
                        caseConstraint.isCaseSensitive(), dateTimeService) && null != wc.getConstraint()) {
                    constraints.add(wc.getConstraint());
                    break;
                }
            }
        }
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
        return CaseConstraint.class;
    }

    private AttributeValueReader getChildAttributeValueReader(String key,
            AttributeValueReader attributeValueReader) throws AttributeValidationException {
        String[] lookupPathTokens = ValidationUtils.getPathTokens(key);

        AttributeValueReader localAttributeValueReader = attributeValueReader;
        for (int i = 0; i < lookupPathTokens.length; i++) {
            for (Constrainable definition : localAttributeValueReader.getDefinitions()) {
                String attributeName = definition.getName();
                if (attributeName.equals(lookupPathTokens[i])) {
                    if (i == lookupPathTokens.length - 1) {
                        localAttributeValueReader.setAttributeName(attributeName);
                        return localAttributeValueReader;
                    }
                    if (definition instanceof HierarchicallyConstrainable) {
                        String childEntryName = ((HierarchicallyConstrainable) definition).getChildEntryName();
                        DataDictionaryEntry entry = KRADServiceLocatorWeb.getDataDictionaryService().getDataDictionary()
                                .getDictionaryObjectEntry(childEntryName);
                        Object value = attributeValueReader.getValue(attributeName);
                        attributeValueReader.setAttributeName(attributeName);
                        String attributePath = attributeValueReader.getPath();
                        localAttributeValueReader = new DictionaryObjectAttributeValueReader(value, childEntryName,
                                entry, attributePath);
                    }
                    break;
                }
            }
        }
        return null;
    }

}
