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
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.config.property.Config;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.framework.config.property.SimpleConfig;
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
 * 1. empty value check. (failure) {@link #testValueInvalidDateEmpty()}
 * 2. value with valid date. (success) {@link #testValueValidDate()}
 * 3. value with valid date. (success) {@link #testValueValidDate1()}
 * 4. value with invalid date. (failure) {@link testValueInvalidDate()}
 * 5. value with invalid date. (failure) {@link testValueInvalidDate1()}
 * 6. value with invalid date. (failure) {@link testValueInvalidDate2()}
 * 7. value with invalid date. (failure) {@link testValueInvalidDate3()}  
 * 
 *  
 */
public class DatePatternConstraintTest {
	
	private AttributeDefinition dateDefinition;
	
	private DictionaryValidationResult dictionaryValidationResult;
	
	private ValidCharactersConstraintProcessor processor;
	
	private String validDate;
	private String validDate1;
	private String invalidDateEmpty;
	private String invalidDate;
	private String invalidDate1;
	private String invalidDate2;
	private String invalidDate3;
		
	private DatePatternConstraint datePatternConstraint;
	
	@Before
	public void setUp() throws Exception {
		
		String formats = "MM/dd/yy;MM/dd/yyyy;MM-dd-yy;MM-dd-yyyy"; 
		
		Properties props = new Properties();
		props.put(CoreConstants.STRING_TO_DATE_FORMATS, formats);
		Config config = new SimpleConfig(props);
		
		ConfigContext.init(config);
		ConfigContext.getCurrentContextConfig().putProperty(CoreConstants.STRING_TO_DATE_FORMATS, formats);
		
		processor = new ValidCharactersConstraintProcessor();
				
		dictionaryValidationResult = new DictionaryValidationResult();
		dictionaryValidationResult.setErrorLevel(ErrorLevel.NOCONSTRAINT);
		
		validDate = "07-28-2011";
		validDate1 = "12-31-83";				
		invalidDateEmpty = "";
		invalidDate = "31-12-2010";
		invalidDate1 = "31-12-2010 12:30:45.456";
		invalidDate2 = "2011-07-28 IST";
		invalidDate3 = "12/31/2011";					        
					
		List<String> allowedFormats = new ArrayList<String>();
		allowedFormats.add("MM-dd-yy");
		allowedFormats.add("MM-dd-yyyy");
		
		datePatternConstraint = new DatePatternConstraint();
		datePatternConstraint.setAllowedFormats(allowedFormats);

		dateDefinition = new AttributeDefinition();
		dateDefinition.setName("date");
		dateDefinition.setValidCharactersConstraint(datePatternConstraint);												
	}
	
	@Test
	public void testValueInvalidDateEmpty() {
		ConstraintValidationResult result = process(invalidDateEmpty, "date", datePatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.INAPPLICABLE, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}
	
	@Test
	public void testValueValidDate() {
		ConstraintValidationResult result = process(validDate, "date", datePatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}
	
	@Test
	public void testValueValidDate1() {
		ConstraintValidationResult result = process(validDate1, "Mdate", datePatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}				
	
	@Test
	public void testValueInvalidDate() {
		ConstraintValidationResult result = process(invalidDate, "date", datePatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}
	
	@Test
	public void testValueInvalidDate1() {
		ConstraintValidationResult result = process(invalidDate1, "date", datePatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}
	
	@Test
	public void testValueInvalidDate2() {
		ConstraintValidationResult result = process(invalidDate2, "date", datePatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}
	
	@Test
	public void testValueInvalidDate3() {
		ConstraintValidationResult result = process(invalidDate3, "date", datePatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}		
	
	private ConstraintValidationResult process(Object object, String attributeName, ValidCharactersConstraint constraint) {
		AttributeValueReader attributeValueReader = new SingleAttributeValueReader(object, "org.kuali.rice.kns.datadictionary.validation.MockAddress", attributeName,  dateDefinition);		
		Object value = attributeValueReader.getValue();
		return processor.process(dictionaryValidationResult, value, constraint, attributeValueReader).getFirstConstraintValidationResult();
	}
}
