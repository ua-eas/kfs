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

import java.util.ArrayList;
import java.util.List;

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
 * 2. value with valid positive number within specified precision and scale. (success) {@link #testValueValidPositiveNumber()}
 * 3. value with invalid positive number as per given precision and scale. (failure) {@link testValueInvalidPositiveNumber()}
 * 4. value with invalid negative number as allowNegative set to false. (failure) {@link testValueInvalidNegativeNumber()}
 * 5. value with valid negative number within specified precision and scale, with allowNegative set to true. (success) {@link testValueValidNegativeNumber()}
 * 6. value with invalid negative number as per given precision and scale. (failure) {@link testValueInvalidNegativeNumber1()}
 * 7. value with invalid positive number as per given precision and scale. (failure) {@link testValueInvalidPositiveNumber1()}
 * 8. value with valid negative number within specified precision and scale, with allowNegative set to true. (success) {@link testValueValidNegativeNumber1()}
 * 9. zero precision and scale test. (error) {@link testZeroPrecisionAndScale()}
 * 10. precision less than scale test. (error) {@link testPrecisionLessThanScale()}
 * 11. negative precision and scale test. (error) {@link testNegativePrecisionAndScale()}
 * 
 * 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org) 
 */
public class FixedPointPatternConstraintTest {

	private AttributeDefinition totalBalanceDefinition;	
	private AttributeDefinition debitBalanceDefinition;	
	private AttributeDefinition creditBalanceDefinition;
	
	private BusinessObjectEntry addressEntry;
	private DictionaryValidationResult dictionaryValidationResult;
	
	private ValidCharactersConstraintProcessor processor;
		
	private Account iciciAccount = new Account("11111111","ICICI","5000.00","15000","-10000");
	private Account citibankAccount = new Account("22222222","Citi Bank","15000.70","14999.70","1.");
	private Account wellsFargoAccount = new Account("33333333","Wells Fargo","",".25","-.25");
	private Account morganAccount = new Account("44444444","J P Morgan","-1000.00","1000.00","-2000.00");
	
	private FixedPointPatternConstraint totalBalanceFixedPointPatternConstraint;
	private FixedPointPatternConstraint debitBalanceFixedPointPatternConstraint;
	private FixedPointPatternConstraint creditBalanceFixedPointPatternConstraint;
	private FixedPointPatternConstraint creditBalanceFixedPointPatternConstraint1;
	private FixedPointPatternConstraint creditBalanceFixedPointPatternConstraint2;
	
	@Before
	public void setUp() throws Exception {
		
		processor = new ValidCharactersConstraintProcessor();
		
		dictionaryValidationResult = new DictionaryValidationResult();
		dictionaryValidationResult.setErrorLevel(ErrorLevel.NOCONSTRAINT);
		
		addressEntry = new BusinessObjectEntry();
		
		List<AttributeDefinition> attributes = new ArrayList<AttributeDefinition>();						
		
		totalBalanceFixedPointPatternConstraint = new FixedPointPatternConstraint();
		totalBalanceFixedPointPatternConstraint.setPrecision(6);
		totalBalanceFixedPointPatternConstraint.setScale(2);
		
		totalBalanceDefinition = new AttributeDefinition();
		totalBalanceDefinition.setName("totalBalance");
		totalBalanceDefinition.setValidCharactersConstraint(totalBalanceFixedPointPatternConstraint);
		attributes.add(totalBalanceDefinition);
		
		debitBalanceFixedPointPatternConstraint = new FixedPointPatternConstraint();
		debitBalanceFixedPointPatternConstraint.setPrecision(6);
		debitBalanceFixedPointPatternConstraint.setScale(2);
		debitBalanceFixedPointPatternConstraint.setAllowNegative(true);
		
		debitBalanceDefinition = new AttributeDefinition();
		debitBalanceDefinition.setName("debitBalance");
		debitBalanceDefinition.setValidCharactersConstraint(debitBalanceFixedPointPatternConstraint);
		attributes.add(debitBalanceDefinition);
		
		creditBalanceFixedPointPatternConstraint = new FixedPointPatternConstraint();
		creditBalanceFixedPointPatternConstraint.setPrecision(0);
		creditBalanceFixedPointPatternConstraint.setScale(0);
		
		creditBalanceDefinition = new AttributeDefinition();
		creditBalanceDefinition.setName("creditBalance");
		creditBalanceDefinition.setValidCharactersConstraint(creditBalanceFixedPointPatternConstraint);
		attributes.add(creditBalanceDefinition);
		
		creditBalanceFixedPointPatternConstraint1 = new FixedPointPatternConstraint();
		creditBalanceFixedPointPatternConstraint1.setPrecision(2);
		creditBalanceFixedPointPatternConstraint1.setScale(3);
				
		creditBalanceFixedPointPatternConstraint2 = new FixedPointPatternConstraint();
		creditBalanceFixedPointPatternConstraint2.setPrecision(-2);
		creditBalanceFixedPointPatternConstraint2.setScale(-3);		
		
		addressEntry.setAttributes(attributes);	
	}
	
	@Test
	public void testValueEmpty() {
		ConstraintValidationResult result = process(wellsFargoAccount, "totalBalance", totalBalanceFixedPointPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.INAPPLICABLE, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}	
	
	@Test
	public void testValueValidPositiveNumber() {
		ConstraintValidationResult result = process(iciciAccount, "totalBalance", totalBalanceFixedPointPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

    @Ignore
	@Test
	public void testValueInvalidPositiveNumber() {
		ConstraintValidationResult result = process(citibankAccount, "totalBalance", totalBalanceFixedPointPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}	

    @Ignore
	@Test
	public void testValueInvalidNegativeNumber() {
		ConstraintValidationResult result = process(morganAccount, "totalBalance", totalBalanceFixedPointPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}	
	
	@Test
	public void testValueValidNegativeNumber() {
		ConstraintValidationResult result = process(morganAccount, "debitBalance", debitBalanceFixedPointPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

    @Ignore
	@Test
	public void testValueInvalidNegativeNumber1() {
		ConstraintValidationResult result = process(iciciAccount, "debitBalance", debitBalanceFixedPointPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

    @Ignore
	@Test
	public void testValueInvalidPositiveNumber1() {
		ConstraintValidationResult result = process(citibankAccount, "debitBalance", debitBalanceFixedPointPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}
	
	@Test
	public void testValueValidNegativeNumber1() {
		ConstraintValidationResult result = process(wellsFargoAccount, "debitBalance", debitBalanceFixedPointPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}	
	
	@Test(expected=java.util.regex.PatternSyntaxException.class)
	public void testZeroPrecisionAndScale() {
		ConstraintValidationResult result = process(wellsFargoAccount, "creditBalance", creditBalanceFixedPointPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}
	
	@Test(expected=java.util.regex.PatternSyntaxException.class)
	public void testPrecisionLessThanScale() {
		ConstraintValidationResult result = process(wellsFargoAccount, "creditBalance", creditBalanceFixedPointPatternConstraint1);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}
	
	@Test(expected=java.util.regex.PatternSyntaxException.class)
	public void testNegativePrecisionAndScale() {
		ConstraintValidationResult result = process(wellsFargoAccount, "creditBalance", creditBalanceFixedPointPatternConstraint2);
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
