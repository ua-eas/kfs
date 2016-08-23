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

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.krad.datadictionary.AttributeDefinition;
import org.kuali.kfs.krad.datadictionary.BusinessObjectEntry;
import org.kuali.kfs.krad.datadictionary.validation.AttributeValueReader;
import org.kuali.kfs.krad.datadictionary.validation.DictionaryObjectAttributeValueReader;
import org.kuali.kfs.krad.datadictionary.validation.Employee;
import org.kuali.kfs.krad.datadictionary.validation.ErrorLevel;
import org.kuali.kfs.krad.datadictionary.validation.processor.ValidCharactersConstraintProcessor;
import org.kuali.kfs.krad.datadictionary.validation.result.ConstraintValidationResult;
import org.kuali.kfs.krad.datadictionary.validation.result.DictionaryValidationResult;


/**
 * Things this test should check:
 *
 * 1. empty value check. (failure) {@link #testValueInvalidPhoneNumberEmpty()}
 * 2. value with valid phone number. (success) {@link #testValueValidPhoneNumber()}
 * 3. value with invalid phone number. (failure) {@link #testValueInvalidPhoneNumber()}
 * 4. value with invalid phone number. (failure) {@link #testValueInvalidPhoneNumber1()}
 * 5. value with invalid phone number. (failure) {@link #testValueInvalidPhoneNumber2()}
 * 6. value with invalid phone number. (failure) {@link #testValueInvalidPhoneNumber3()}
 * 7. value with invalid phone number. (failure) {@link #testValueInvalidPhoneNumber4()}
 * 8. value with invalid phone number. (failure) {@link #testValueInvalidPhoneNumber5()}
 * 9. value with invalid phone number. (failure) {@link #testValueInvalidPhoneNumber6()}
 *
 *
 *
 */
public class PhoneNumberPatternConstraintTest {

	private final String PATTERN_CONSTRAINT = "validationPatternRegex.phoneNumber";

	private AttributeDefinition contactPhoneDefinition;

	private BusinessObjectEntry addressEntry;
	private DictionaryValidationResult dictionaryValidationResult;

	private ValidCharactersConstraintProcessor processor;

	private Employee validPhoneEmployee = new Employee();
	private Employee invalidPhoneEmployeeEmpty = new Employee();
	private Employee invalidPhoneEmployee = new Employee();
	private Employee invalidPhoneEmployee1 = new Employee();
	private Employee invalidPhoneEmployee2 = new Employee();
	private Employee invalidPhoneEmployee3 = new Employee();
	private Employee invalidPhoneEmployee4 = new Employee();
	private Employee invalidPhoneEmployee5 = new Employee();
	private Employee invalidPhoneEmployee6 = new Employee();

	private ConfigurationBasedRegexPatternConstraint contactPhoneNumberPatternConstraint;

	@Before
	public void setUp() throws Exception {

		String regex = ApplicationResources.getProperty(PATTERN_CONSTRAINT);

		processor = new ValidCharactersConstraintProcessor();

		dictionaryValidationResult = new DictionaryValidationResult();
		dictionaryValidationResult.setErrorLevel(ErrorLevel.NOCONSTRAINT);

		addressEntry = new BusinessObjectEntry();

		List<AttributeDefinition> attributes = new ArrayList<AttributeDefinition>();

		validPhoneEmployee.setContactPhone("056-012-1200");
		invalidPhoneEmployeeEmpty.setContactPhone("");
		invalidPhoneEmployee.setContactPhone("09712248474");
		invalidPhoneEmployee1.setContactPhone("+19712248474");
		invalidPhoneEmployee2.setContactPhone("+1-972-232-3333");
		invalidPhoneEmployee3.setContactPhone("+1-(972)-23334444");
		invalidPhoneEmployee4.setContactPhone("+1-(972)-1223444 xtn.222");
		invalidPhoneEmployee5.setContactPhone("+1 056 012 1200");
		invalidPhoneEmployee6.setContactPhone("056\\-012\\-1200");

		contactPhoneNumberPatternConstraint = new ConfigurationBasedRegexPatternConstraint();
		contactPhoneNumberPatternConstraint.setValue(regex);

		contactPhoneDefinition = new AttributeDefinition();
		contactPhoneDefinition.setName("contactPhone");
		contactPhoneDefinition.setValidCharactersConstraint(contactPhoneNumberPatternConstraint);
		attributes.add(contactPhoneDefinition);

		addressEntry.setAttributes(attributes);
	}

	@Test
	public void testValueInvalidPhoneNumberEmpty() {
		ConstraintValidationResult result = process(invalidPhoneEmployeeEmpty, "contactPhone", contactPhoneNumberPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.INAPPLICABLE, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueValidPhoneNumber() {
		ConstraintValidationResult result = process(validPhoneEmployee, "contactPhone", contactPhoneNumberPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}


	@Test
	public void testValueInvalidPhoneNumber() {
		ConstraintValidationResult result = process(invalidPhoneEmployee, "contactPhone", contactPhoneNumberPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueInvalidPhoneNumber1() {
		ConstraintValidationResult result = process(invalidPhoneEmployee1, "contactPhone", contactPhoneNumberPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueInvalidPhoneNumber2() {
		ConstraintValidationResult result = process(invalidPhoneEmployee2, "contactPhone", contactPhoneNumberPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueInvalidPhoneNumber3() {
		ConstraintValidationResult result = process(invalidPhoneEmployee3, "contactPhone", contactPhoneNumberPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueInvalidPhoneNumber4() {
		ConstraintValidationResult result = process(invalidPhoneEmployee4, "contactPhone", contactPhoneNumberPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueInvalidPhoneNumber5() {
		ConstraintValidationResult result = process(invalidPhoneEmployee5, "contactPhone", contactPhoneNumberPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueInvalidPhoneNumber6() {
		ConstraintValidationResult result = process(invalidPhoneEmployee6, "contactPhone", contactPhoneNumberPatternConstraint);
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
