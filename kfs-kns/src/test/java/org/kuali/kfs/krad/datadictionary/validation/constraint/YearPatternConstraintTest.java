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
 * 1. empty value check. (failure) {@link #testValueInvalidYearEmpty()}
 * 2. value with valid year. (success) {@link #testValueValidYear()}
 * 3. value with valid year. (success) {@link #testValueValidYear1()}
 * 4. value with valid year. (success) {@link #testValueValidYear2()}
 * 5. value with invalid year. (failure) {@link #testValueInvalidYear()}
 * 6. value with invalid year. (failure) {@link #testValueInvalidYear1()}
 * 7. value with invalid year. (failure) {@link #testValueInvalidYear2()}
 * 8. value with invalid year. (failure) {@link #testValueInvalidYear3()}
 *
 * 
 */
public class YearPatternConstraintTest {

	private final String PATTERN_CONSTRAINT = "validationPatternRegex.year";

	private AttributeDefinition yearDefinition;

	private DictionaryValidationResult dictionaryValidationResult;

	private ValidCharactersConstraintProcessor processor;

	private String validYear;
	private String validYear1;
	private String validYear2;
	private String invalidYearEmpty;
	private String invalidYear;
	private String invalidYear1;
	private String invalidYear2;
	private String invalidYear3;

	private ConfigurationBasedRegexPatternConstraint yearPatternConstraint;

	@Before
	public void setUp() throws Exception {

		String regex = ApplicationResources.getProperty(PATTERN_CONSTRAINT);

		processor = new ValidCharactersConstraintProcessor();

		dictionaryValidationResult = new DictionaryValidationResult();
		dictionaryValidationResult.setErrorLevel(ErrorLevel.NOCONSTRAINT);

		validYear = "1901";
		validYear1 = "2050";
		validYear2 = "1837";
		invalidYearEmpty = "";
		invalidYear = "00";
		invalidYear1 = "337";
		invalidYear2 = "2300";
		invalidYear3 = "99999";

		yearPatternConstraint = new ConfigurationBasedRegexPatternConstraint();
		yearPatternConstraint.setValue(regex);

		yearDefinition = new AttributeDefinition();
		yearDefinition.setName("year");
		yearDefinition.setValidCharactersConstraint(yearPatternConstraint);
	}

	@Test
	public void testValueInvalidYearEmpty() {
		ConstraintValidationResult result = process(invalidYearEmpty, "year", yearPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.INAPPLICABLE, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueValidYear() {
		ConstraintValidationResult result = process(validYear, "year", yearPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueValidYear1() {
		ConstraintValidationResult result = process(validYear1, "Myear", yearPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueValidYear2() {
		ConstraintValidationResult result = process(validYear2, "year", yearPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueInvalidYear() {
		ConstraintValidationResult result = process(invalidYear, "year", yearPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueInvalidYear1() {
		ConstraintValidationResult result = process(invalidYear1, "year", yearPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueInvalidYear2() {
		ConstraintValidationResult result = process(invalidYear2, "year", yearPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueInvalidYear3() {
		ConstraintValidationResult result = process(invalidYear3, "year", yearPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	private ConstraintValidationResult process(Object object, String attributeName, ValidCharactersConstraint constraint) {
		AttributeValueReader attributeValueReader = new SingleAttributeValueReader(object, "org.kuali.rice.kns.datadictionary.validation.MockAddress", attributeName,  yearDefinition);
		Object value = attributeValueReader.getValue();
		return processor.process(dictionaryValidationResult, value, constraint, attributeValueReader).getFirstConstraintValidationResult();
	}
}
