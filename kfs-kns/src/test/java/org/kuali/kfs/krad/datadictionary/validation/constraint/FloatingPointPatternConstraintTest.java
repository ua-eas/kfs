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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kuali.kfs.krad.datadictionary.AttributeDefinition;
import org.kuali.kfs.krad.datadictionary.BusinessObjectEntry;
import org.kuali.kfs.krad.datadictionary.validation.Account;
import org.kuali.kfs.krad.datadictionary.validation.AttributeValueReader;
import org.kuali.kfs.krad.datadictionary.validation.DictionaryObjectAttributeValueReader;
import org.kuali.kfs.krad.datadictionary.validation.ErrorLevel;
import org.kuali.kfs.krad.datadictionary.validation.processor.ValidCharactersConstraintProcessor;
import org.kuali.kfs.krad.datadictionary.validation.result.ConstraintValidationResult;
import org.kuali.kfs.krad.datadictionary.validation.result.DictionaryValidationResult;


/**
 * Things this test should check:
 *
 * 1. empty value check. (failure) {@link #testValueEmpty()}
 * 2. value with valid positive number. (success) {@link #testValueValidPositiveNumber()}
 * 3. value with invalid negative number as allowNegative set to false. (failure) {@link #testValueInvalidNegativeNumber()}
 * 4. value with valid negative number as allowNegative set to true. (success) {@link #testValueValidNegativeNumber()}
 * 5. value with invalid negative number as allowNegative set to true.. (failure) {@link #testValueInvalidNegativeNumber1()}
 * 6. value with invalid positive number. (failure) {@link #testValueInvalidPositiveNumber()}
 * 7. value with valid negative number as allowNegative set to true. (success) {@link #testValueValidNegativeNumber1)}
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class FloatingPointPatternConstraintTest {

	private final String PATTERN_CONSTRAINT = "validationPatternRegex.floatingPoint";

	private AttributeDefinition totalBalanceDefinition;
	private AttributeDefinition debitBalanceDefinition;

	private BusinessObjectEntry addressEntry;
	private DictionaryValidationResult dictionaryValidationResult;

	private ValidCharactersConstraintProcessor processor;

	private Account iciciAccount = new Account("11111111","ICICI","5000.00","15000","-10000");
	private Account citibankAccount = new Account("22222222","Citi Bank","15000.70","14999.70","1.");
	private Account wellsFargoAccount = new Account("33333333","Wells Fargo","",".25","-.25");
	private Account morganAccount = new Account("44444444","J P Morgan","-1000.00","1000.00","(2000.00)");

	private FloatingPointPatternConstraint totalBalanceFloatingPointPatternConstraint;
	private FloatingPointPatternConstraint debitBalanceFloatingPointPatternConstraint;

	@Before
	public void setUp() throws Exception {

		String regex = ApplicationResources.getProperty(PATTERN_CONSTRAINT);

		processor = new ValidCharactersConstraintProcessor();

		dictionaryValidationResult = new DictionaryValidationResult();
		dictionaryValidationResult.setErrorLevel(ErrorLevel.NOCONSTRAINT);

		addressEntry = new BusinessObjectEntry();

		List<AttributeDefinition> attributes = new ArrayList<AttributeDefinition>();

		totalBalanceFloatingPointPatternConstraint = new FloatingPointPatternConstraint();
		totalBalanceFloatingPointPatternConstraint.setValue(regex);

		totalBalanceDefinition = new AttributeDefinition();
		totalBalanceDefinition.setName("totalBalance");
		totalBalanceDefinition.setValidCharactersConstraint(totalBalanceFloatingPointPatternConstraint);
		attributes.add(totalBalanceDefinition);

		debitBalanceFloatingPointPatternConstraint = new FloatingPointPatternConstraint();
		debitBalanceFloatingPointPatternConstraint.setValue("-?"+regex);
		debitBalanceFloatingPointPatternConstraint.setAllowNegative(true);

		debitBalanceDefinition = new AttributeDefinition();
		debitBalanceDefinition.setName("debitBalance");
		debitBalanceDefinition.setValidCharactersConstraint(debitBalanceFloatingPointPatternConstraint);
		attributes.add(debitBalanceDefinition);


		addressEntry.setAttributes(attributes);
	}

	@Test
	public void testValueEmpty() {
		ConstraintValidationResult result = process(wellsFargoAccount, "totalBalance", totalBalanceFloatingPointPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.INAPPLICABLE, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueValidPositiveNumber() {
		ConstraintValidationResult result = process(citibankAccount, "totalBalance", totalBalanceFloatingPointPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

    @Ignore
	@Test
	public void testValueInvalidNegativeNumber() {
		ConstraintValidationResult result = process(morganAccount, "totalBalance", totalBalanceFloatingPointPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueValidNegativeNumber() {
		ConstraintValidationResult result = process(iciciAccount, "debitBalance", debitBalanceFloatingPointPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

    @Ignore
	@Test
	public void testValueInvalidNegativeNumber1() {
		ConstraintValidationResult result = process(morganAccount, "debitBalance", debitBalanceFloatingPointPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

    @Ignore
	@Test
	public void testValueInvalidPositiveNumber() {
		ConstraintValidationResult result = process(citibankAccount, "debitBalance", debitBalanceFloatingPointPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueValidNegativeNumber1() {
		ConstraintValidationResult result = process(wellsFargoAccount, "debitBalance", debitBalanceFloatingPointPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	private ConstraintValidationResult process(Object object, String attributeName, ValidCharactersConstraint constraint) {
		AttributeValueReader attributeValueReader = new DictionaryObjectAttributeValueReader(object, "org.kuali.rice.kns.datadictionary.validation.MockAddress", addressEntry);
		attributeValueReader.setAttributeName(attributeName);

		Object value = attributeValueReader.getValue();
		return processor.process(dictionaryValidationResult, value, constraint, attributeValueReader).getFirstConstraintValidationResult();
	}
}
