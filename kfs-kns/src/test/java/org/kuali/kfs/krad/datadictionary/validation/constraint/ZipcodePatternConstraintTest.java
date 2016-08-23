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
 * 1. value with valid zipcode. (success) {@link #testValueValidZipcode()}
 * 2. value with valid zipcode. (success) {@link #testValueValidZipcode1()}
 * 3. value with invalid zipcode. (failure) {@link #testValueInvalidZipcode()}
 * 4. value with invalid zipcode. (failure) {@link #testValueInvalidZipcode1()}
 * 5. value with invalid zipcode. (failure) {@link #testValueInvalidZipcode2()}
 */
public class ZipcodePatternConstraintTest {

    private final String PATTERN_CONSTRAINT = "validationPatternRegex.zipcode";

    private AttributeDefinition postalCodeDefinition;

    private BusinessObjectEntry addressEntry;
    private DictionaryValidationResult dictionaryValidationResult;

    private ValidCharactersConstraintProcessor processor;

    private Address washingtonDCAddress = new Address("893 Presidential Ave", "(A_123) Suite 800.", "Washington", "DC", "12345", "USA", null);
    private Address newYorkNYAddress = new Address("Presidential Street", "(A-123) Suite 800", "New York", "NY", "12345-1234", "USA", null);
    private Address sydneySDAddress = new Address("Presidential Street-Ave.", "Suite_800.", "Sydney", "SD", "ZH-123456", "Australia", null);
    private Address holandHDAddress = new Address("Presidential Street-Ave.", "Suite_800.", "Holand", "HD", "12345-123", "Holand", null);
    private Address bombayMHAddress = new Address("Presidential Street-Ave.", "Suite_800.", "Bombay", "MH", "380002", "India", null);

    private ConfigurationBasedRegexPatternConstraint postalCodePatternConstraint;

    @Before
    public void setUp() throws Exception {

        String regex = ApplicationResources.getProperty(PATTERN_CONSTRAINT);

        processor = new ValidCharactersConstraintProcessor();

        dictionaryValidationResult = new DictionaryValidationResult();
        dictionaryValidationResult.setErrorLevel(ErrorLevel.NOCONSTRAINT);

        addressEntry = new BusinessObjectEntry();

        List<AttributeDefinition> attributes = new ArrayList<AttributeDefinition>();

        postalCodePatternConstraint = new ConfigurationBasedRegexPatternConstraint();
        postalCodePatternConstraint.setValue(regex);

        postalCodeDefinition = new AttributeDefinition();
        postalCodeDefinition.setName("postalCode");
        postalCodeDefinition.setValidCharactersConstraint(postalCodePatternConstraint);
        attributes.add(postalCodeDefinition);

        addressEntry.setAttributes(attributes);
    }

    @Test
    public void testValueValidZipcode() {
        ConstraintValidationResult result = process(washingtonDCAddress, "postalCode", postalCodePatternConstraint);
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
        Assert.assertEquals(ErrorLevel.OK, result.getStatus());
        Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
    }

    @Test
    public void testValueValidZipcode1() {
        ConstraintValidationResult result = process(newYorkNYAddress, "postalCode", postalCodePatternConstraint);
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
        Assert.assertEquals(ErrorLevel.OK, result.getStatus());
        Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
    }

    @Test
    public void testValueInvalidZipcode() {
        ConstraintValidationResult result = process(sydneySDAddress, "postalCode", postalCodePatternConstraint);
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
        Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
        Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
        Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
    }

    @Test
    public void testValueInvalidZipcode1() {
        ConstraintValidationResult result = process(holandHDAddress, "postalCode", postalCodePatternConstraint);
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
        Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
        Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
        Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
    }

    @Test
    public void testValueInvalidZipcode2() {
        ConstraintValidationResult result = process(bombayMHAddress, "postalCode", postalCodePatternConstraint);
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
