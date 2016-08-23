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
import org.kuali.kfs.krad.datadictionary.validation.AttributeValueReader;
import org.kuali.kfs.krad.datadictionary.validation.ErrorLevel;
import org.kuali.kfs.krad.datadictionary.validation.SingleAttributeValueReader;
import org.kuali.kfs.krad.datadictionary.validation.processor.ValidCharactersConstraintProcessor;
import org.kuali.kfs.krad.datadictionary.validation.result.ConstraintValidationResult;
import org.kuali.kfs.krad.datadictionary.validation.result.DictionaryValidationResult;


/**
 * Things this test should check:
 * <p>
 * 1. empty value check. (failure) {@link #testValueInvalidTimestampEmpty()}
 * 2. value with valid timestamp. (success) {@link #testValueValidTimestamp()}
 * 3. value with valid timestamp. (success) {@link #testValueValidTimestamp1()}
 * 4. value with invalid timestamp. (failure) {@link #testValueInvalidTimestamp()}
 * 5. value with invalid timestamp. (failure) {@link #testValueInvalidTimestamp1()}
 * 6. value with invalid timestamp. (failure) {@link #testValueInvalidTimestamp2()}
 * 7. value with invalid timestamp. (failure) {@link #testValueInvalidTimestamp3()}
 */
public class TimestampPatternConstraintTest {

    private final String PATTERN_CONSTRAINT = "validationPatternRegex.timestamp";

    private AttributeDefinition timestampDefinition;

    private DictionaryValidationResult dictionaryValidationResult;

    private ValidCharactersConstraintProcessor processor;

    private String validTimestamp;
    private String validTimestamp1;
    private String invalidTimestampEmpty;
    private String invalidTimestamp;
    private String invalidTimestamp1;
    private String invalidTimestamp2;
    private String invalidTimestamp3;

    private ConfigurationBasedRegexPatternConstraint timestampPatternConstraint;

    @Before
    public void setUp() throws Exception {

        String regex = ApplicationResources.getProperty(PATTERN_CONSTRAINT);

        processor = new ValidCharactersConstraintProcessor();

        dictionaryValidationResult = new DictionaryValidationResult();
        dictionaryValidationResult.setErrorLevel(ErrorLevel.NOCONSTRAINT);

        validTimestamp = "2011-07-28 15:10:36.300";
        validTimestamp1 = "1936-07-28 15:10:36.9999999";
        invalidTimestampEmpty = "";
        invalidTimestamp = "2011/07/28 15:10:36.300";
        invalidTimestamp1 = "2011-07-28 15:10:36.300 IST";
        invalidTimestamp2 = "2011-07-28";
        invalidTimestamp3 = "28-07-2011 15:10:36.300";

        timestampPatternConstraint = new ConfigurationBasedRegexPatternConstraint();
        timestampPatternConstraint.setValue(regex);

        timestampDefinition = new AttributeDefinition();
        timestampDefinition.setName("timestamp");
        timestampDefinition.setValidCharactersConstraint(timestampPatternConstraint);
    }

    @Test
    public void testValueInvalidTimestampEmpty() {
        ConstraintValidationResult result = process(invalidTimestampEmpty, "timestamp", timestampPatternConstraint);
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
        Assert.assertEquals(ErrorLevel.INAPPLICABLE, result.getStatus());
        Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
    }

    @Test
    public void testValueValidTimestamp() {
        ConstraintValidationResult result = process(validTimestamp, "timestamp", timestampPatternConstraint);
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
        Assert.assertEquals(ErrorLevel.OK, result.getStatus());
        Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
    }

    @Test
    public void testValueValidTimestamp1() {
        ConstraintValidationResult result = process(validTimestamp1, "Mtimestamp", timestampPatternConstraint);
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
        Assert.assertEquals(ErrorLevel.OK, result.getStatus());
        Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
    }

    @Test
    public void testValueInvalidTimestamp() {
        ConstraintValidationResult result = process(invalidTimestamp, "timestamp", timestampPatternConstraint);
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
        Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
        Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
        Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
    }

    @Test
    public void testValueInvalidTimestamp1() {
        ConstraintValidationResult result = process(invalidTimestamp1, "timestamp", timestampPatternConstraint);
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
        Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
        Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
        Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
    }

    @Test
    public void testValueInvalidTimestamp2() {
        ConstraintValidationResult result = process(invalidTimestamp2, "timestamp", timestampPatternConstraint);
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
        Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
        Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
        Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
    }

    @Test
    public void testValueInvalidTimestamp3() {
        ConstraintValidationResult result = process(invalidTimestamp3, "timestamp", timestampPatternConstraint);
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
        Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
        Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
        Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
    }

    private ConstraintValidationResult process(Object object, String attributeName, ValidCharactersConstraint constraint) {
        AttributeValueReader attributeValueReader = new SingleAttributeValueReader(object, "org.kuali.rice.kns.datadictionary.validation.MockAddress", attributeName, timestampDefinition);
        Object value = attributeValueReader.getValue();
        return processor.process(dictionaryValidationResult, value, constraint, attributeValueReader).getFirstConstraintValidationResult();
    }
}
