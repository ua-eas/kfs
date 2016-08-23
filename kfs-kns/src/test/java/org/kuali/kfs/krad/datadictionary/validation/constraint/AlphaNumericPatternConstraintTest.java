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
package org.kuali.kfs.krad.datadictionary.validation.constraint;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kuali.kfs.krad.datadictionary.AttributeDefinition;
import org.kuali.kfs.krad.datadictionary.BusinessObjectEntry;
import org.kuali.kfs.krad.datadictionary.validation.Address;
import org.kuali.kfs.krad.datadictionary.validation.AttributeValueReader;
import org.kuali.kfs.krad.datadictionary.validation.DictionaryObjectAttributeValueReader;
import org.kuali.kfs.krad.datadictionary.validation.ErrorLevel;
import org.kuali.kfs.krad.datadictionary.validation.processor.ValidCharactersConstraintProcessor;
import org.kuali.kfs.krad.datadictionary.validation.result.ConstraintValidationResult;
import org.kuali.kfs.krad.datadictionary.validation.result.DictionaryValidationResult;

import java.util.ArrayList;
import java.util.List;


/**
 * Things this test should check:
 * <p>
 * 1. value with all valid characters. (success) {@link #testValueAllValidChars()}
 * 2. value with invalid characters. (failure) {@link #testValueNotValidChars()}
 * 3. value with all valid characters. Allowing white space.(success) {@link #testValueAllValidCharsAllowWhitespace()}
 * 4. value with invalid characters. Allowing white space. (failure) {@link #testValueNotValidCharsAllowWhitespace()}
 * 5. value with all valid characters. Allowing white space, period, underscore and parenthesis. (success) {@link #testValueAllValidCharsAllowWhitespaceAndPeriodAndUnderscoreAndParenthesis()}
 * 6. value with invalid characters. Allowing white space, period, underscore and parenthesis. (failure) {@link #testValueNotValidCharsAllowWhitespaceAndPeriodAndUnderscoreAndParenthesis()}
 */
public class AlphaNumericPatternConstraintTest {

    private AttributeDefinition street1Definition;
    private AttributeDefinition street2Definition;
    private AttributeDefinition postalCodeDefinition;

    private BusinessObjectEntry addressEntry;
    private DictionaryValidationResult dictionaryValidationResult;

    private ValidCharactersConstraintProcessor processor;

    private Address washingtonDCAddress = new Address("893 Presidential Ave", "(A_123) Suite 800.", "Washington", "DC", "NHW123A", "USA", null);
    private Address newYorkNYAddress = new Address("Presidential Street", "(A-123) Suite 800", "New York", "NY", "ZH 3456", "USA", null);
    private Address sydneyAUSAddress = new Address("Presidential Street-Ave.", "Suite_800.", "Sydney", "ZZ", "ZH-5656", "USA", null);

    private AlphaNumericPatternConstraint street1AlphaNumericPatternConstraint;
    private AlphaNumericPatternConstraint street2AlphaNumericPatternConstraint;
    private AlphaNumericPatternConstraint postalCodeAlphaNumericPatternConstraint;

    @Before
    public void setUp() throws Exception {

        processor = new ValidCharactersConstraintProcessor();

        dictionaryValidationResult = new DictionaryValidationResult();
        dictionaryValidationResult.setErrorLevel(ErrorLevel.NOCONSTRAINT);

        addressEntry = new BusinessObjectEntry();

        List<AttributeDefinition> attributes = new ArrayList<AttributeDefinition>();

        street1AlphaNumericPatternConstraint = new AlphaNumericPatternConstraint();
        street1AlphaNumericPatternConstraint.setAllowWhitespace(true);

        street1Definition = new AttributeDefinition();
        street1Definition.setName("street1");
        street1Definition.setValidCharactersConstraint(street1AlphaNumericPatternConstraint);
        attributes.add(street1Definition);


        street2AlphaNumericPatternConstraint = new AlphaNumericPatternConstraint();
        street2AlphaNumericPatternConstraint.setAllowWhitespace(true);
        street2AlphaNumericPatternConstraint.setAllowParenthesis(true);
        street2AlphaNumericPatternConstraint.setAllowPeriod(true);
        street2AlphaNumericPatternConstraint.setAllowUnderscore(true);

        street2Definition = new AttributeDefinition();
        street2Definition.setName("street2");
        street2Definition.setValidCharactersConstraint(street2AlphaNumericPatternConstraint);
        attributes.add(street2Definition);

        postalCodeAlphaNumericPatternConstraint = new AlphaNumericPatternConstraint();

        postalCodeDefinition = new AttributeDefinition();
        postalCodeDefinition.setName("postalCode");
        postalCodeDefinition.setValidCharactersConstraint(postalCodeAlphaNumericPatternConstraint);
        attributes.add(postalCodeDefinition);

        addressEntry.setAttributes(attributes);
    }

    @Test
    public void testValueAllValidChars() {
        ConstraintValidationResult result = process(washingtonDCAddress, "postalCode", postalCodeAlphaNumericPatternConstraint);
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
        Assert.assertEquals(ErrorLevel.OK, result.getStatus());
        Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
    }

    @Ignore
    @Test
    public void testValueNotValidChars() {
        ConstraintValidationResult result = process(newYorkNYAddress, "postalCode", postalCodeAlphaNumericPatternConstraint);
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
        Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
        Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
        Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
    }

    @Test
    public void testValueAllValidCharsAllowWhitespace() {
        ConstraintValidationResult result = process(newYorkNYAddress, "street1", street1AlphaNumericPatternConstraint);
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
        Assert.assertEquals(ErrorLevel.OK, result.getStatus());
        Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
    }

    @Ignore
    @Test
    public void testValueNotValidCharsAllowWhitespace() {
        ConstraintValidationResult result = process(sydneyAUSAddress, "street1", street1AlphaNumericPatternConstraint);
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
        Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
        Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
        Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
    }

    @Test
    public void testValueAllValidCharsAllowWhitespaceAndPeriodAndUnderscoreAndParenthesis() {
        ConstraintValidationResult result = process(washingtonDCAddress, "street2", street2AlphaNumericPatternConstraint);
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
        Assert.assertEquals(ErrorLevel.OK, result.getStatus());
        Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
    }

    @Ignore
    @Test
    public void testValueNotValidCharsAllowWhitespaceAndPeriodAndUnderscoreAndParenthesis() {
        ConstraintValidationResult result = process(newYorkNYAddress, "street2", street2AlphaNumericPatternConstraint);
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
        Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
        Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
        Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
    }

    private ConstraintValidationResult process(Object object, String attributeName, ValidCharactersConstraint constraint) {
        AttributeValueReader attributeValueReader = new DictionaryObjectAttributeValueReader(object, "org.kuali.rice.kns.datadictionary.validation.MockAddress", addressEntry);
        attributeValueReader.setAttributeName(attributeName);

        Object value = attributeValueReader.getValue();
        return processor.process(dictionaryValidationResult, value, constraint, attributeValueReader).getFirstConstraintValidationResult();
    }
}
