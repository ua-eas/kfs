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



import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
 *
 * 1. empty value check. (failure) {@link #testValueInvalidMonthEmpty()}
 * 2. value with valid month. (success) {@link #testValueValidMonth()}
 * 3. value with valid month. (success) {@link #testValueValidMonth1()}
 * 4. value with valid month. (success) {@link #testValueValidMonth2()}
 * 5. value with invalid month. (failure) {@link #testValueInvalidMonth()}
 * 6. value with invalid month. (failure) {@link #testValueInvalidMonth1()}
 * 7. value with invalid month. (failure) {@link #testValueInvalidMonth2()}
 * 8. value with invalid month. (failure) {@link #testValueInvalidMonth3()}
 *
 *
 */
public class MonthPatternConstraintTest {

	private final String PATTERN_CONSTRAINT = "validationPatternRegex.month";

	private AttributeDefinition monthDefinition;

	private DictionaryValidationResult dictionaryValidationResult;

	private ValidCharactersConstraintProcessor processor;

	private String validMonth;
	private String validMonth1;
	private String validMonth2;
	private String invalidMonthEmpty;
	private String invalidMonth;
	private String invalidMonth1;
	private String invalidMonth2;
	private String invalidMonth3;

	private ConfigurationBasedRegexPatternConstraint monthPatternConstraint;

	@Before
	public void setUp() throws Exception {

		String regex = ApplicationResources.getProperty(PATTERN_CONSTRAINT);

		processor = new ValidCharactersConstraintProcessor();

		dictionaryValidationResult = new DictionaryValidationResult();
		dictionaryValidationResult.setErrorLevel(ErrorLevel.NOCONSTRAINT);

		validMonth = "1";
		validMonth1 = "05";
		validMonth2 = "12";
		invalidMonthEmpty = "";
		invalidMonth = "00";
		invalidMonth1 = "0";
		invalidMonth2 = "13";
		invalidMonth3 = "JAN";

		monthPatternConstraint = new ConfigurationBasedRegexPatternConstraint();
		monthPatternConstraint.setValue(regex);

		monthDefinition = new AttributeDefinition();
		monthDefinition.setName("month");
		monthDefinition.setValidCharactersConstraint(monthPatternConstraint);
	}

	@Test
	public void testValueInvalidMonthEmpty() {
		ConstraintValidationResult result = process(invalidMonthEmpty, "month", monthPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.INAPPLICABLE, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueValidMonth() {
		ConstraintValidationResult result = process(validMonth, "month", monthPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueValidMonth1() {
		ConstraintValidationResult result = process(validMonth1, "Mmonth", monthPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueValidMonth2() {
		ConstraintValidationResult result = process(validMonth2, "month", monthPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueInvalidMonth() {
		ConstraintValidationResult result = process(invalidMonth, "month", monthPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueInvalidMonth1() {
		ConstraintValidationResult result = process(invalidMonth1, "month", monthPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueInvalidMonth2() {
		ConstraintValidationResult result = process(invalidMonth2, "month", monthPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueInvalidMonth3() {
		ConstraintValidationResult result = process(invalidMonth3, "month", monthPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	private ConstraintValidationResult process(Object object, String attributeName, ValidCharactersConstraint constraint) {
		AttributeValueReader attributeValueReader = new SingleAttributeValueReader(object, "org.kuali.rice.kns.datadictionary.validation.MockAddress", attributeName,  monthDefinition);
		Object value = attributeValueReader.getValue();
		return processor.process(dictionaryValidationResult, value, constraint, attributeValueReader).getFirstConstraintValidationResult();
	}
}
