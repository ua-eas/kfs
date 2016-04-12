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


/**
 * Things this test should check:
 * 
 * 1. value with all valid characters. (success) {@link #testValueAllValidChars()}
 * 2. value with invalid characters. (failure) {@link #testValueNotValidChars()}
 * 3. value with all valid characters. Allowing white space.(success) {@link #testValueAllValidCharsAllowWhitespace()}
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org) 
 */
public class AnyCharacterPatternConstraintTest {

	private AttributeDefinition street1Definition;
	private AttributeDefinition street2Definition;	
	
	private BusinessObjectEntry addressEntry;
	private DictionaryValidationResult dictionaryValidationResult;
	
	private ValidCharactersConstraintProcessor processor;
	
	private Address washingtonDCAddress = new Address("893	Presidential Ave", "(A_123) Suite 800.", "Washington", "DC", "NHW123A", "USA", null);
	private Address newYorkNYAddress = new Address("Presidential Street", "(A-123) Suite 800", "New York", "NY", "ZH 3456", "USA", null);
	private Address sydneyAUSAddress = new Address("Presidential Street-Ave.", "Suite_800.", "Sydney", "ZZ", "ZH-5656", "USA", null);
	
	private AnyCharacterPatternConstraint street1AnyCharacterPatternConstraint;
	private AnyCharacterPatternConstraint street2AnyCharacterPatternConstraint;	
	
	@Before
	public void setUp() throws Exception {
		
		processor = new ValidCharactersConstraintProcessor();
		
		dictionaryValidationResult = new DictionaryValidationResult();
		dictionaryValidationResult.setErrorLevel(ErrorLevel.NOCONSTRAINT);
		
		addressEntry = new BusinessObjectEntry();
		
		List<AttributeDefinition> attributes = new ArrayList<AttributeDefinition>();						
		
		street1AnyCharacterPatternConstraint = new AnyCharacterPatternConstraint();
		street1AnyCharacterPatternConstraint.setAllowWhitespace(true);
		
		street1Definition = new AttributeDefinition();
		street1Definition.setName("street1");
		street1Definition.setValidCharactersConstraint(street1AnyCharacterPatternConstraint);
		attributes.add(street1Definition);	
		
		
		street2AnyCharacterPatternConstraint = new AnyCharacterPatternConstraint();	
		
		street2Definition = new AttributeDefinition();
		street2Definition.setName("street2");
		street2Definition.setValidCharactersConstraint(street2AnyCharacterPatternConstraint);
		attributes.add(street2Definition);
		
		addressEntry.setAttributes(attributes);	
	}
	
	@Test
	public void testValueAllValidChars() {
		ConstraintValidationResult result = process(sydneyAUSAddress, "street2", street2AnyCharacterPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}
	
	@Test
	public void testValueNotValidChars() {
		ConstraintValidationResult result = process(newYorkNYAddress, "street2", street2AnyCharacterPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}
	
	@Test
	public void testValueAllValidCharsAllowWhitespace() {
		ConstraintValidationResult result = process(washingtonDCAddress, "street1", street1AnyCharacterPatternConstraint);
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
