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
package org.kuali.kfs.krad.datadictionary.validation.processor;

import org.junit.Assert;
import org.junit.Test;
import org.kuali.kfs.krad.datadictionary.validation.Address;
import org.kuali.kfs.krad.datadictionary.validation.ErrorLevel;
import org.kuali.kfs.krad.datadictionary.validation.constraint.Constraint;
import org.kuali.kfs.krad.datadictionary.validation.constraint.PrerequisiteConstraint;
import org.kuali.kfs.krad.datadictionary.validation.result.ConstraintValidationResult;
import org.kuali.kfs.krad.datadictionary.validation.result.ProcessorResult;

import java.util.List;



/**
 * 
 * 
 */
public class CaseConstraintProcessorTest extends BaseConstraintProcessorTest<CaseConstraintProcessor> {

	private Address londonAddress = new Address("812 Maiden Lane", "", "London", "", "", "UK", null);
	private Address noStateAddress = new Address("893 Presidential Ave", "Suite 800", "Washington", "", "92342", "USA", null);
	
	
	@Test
	public void testCaseConstraintNotInvoked() {
		ProcessorResult processorResult = processRaw(londonAddress, "country", countryIsUSACaseConstraint);
		ConstraintValidationResult result = processorResult.getFirstConstraintValidationResult();
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.INAPPLICABLE, result.getStatus());
		Assert.assertEquals(new CaseConstraintProcessor().getName(), result.getConstraintName());
		
		List<Constraint> constraints = processorResult.getConstraints();
		
		Assert.assertNotNull(constraints);
		Assert.assertEquals(0, constraints.size());
	}
	
	@Test
	public void testCaseConstraintInvoked() {
		ProcessorResult processorResult = processRaw(noStateAddress, "country", countryIsUSACaseConstraint);
		
		List<Constraint> constraints = processorResult.getConstraints();
		
		Assert.assertNotNull(constraints);
		Assert.assertEquals(1, constraints.size());
		
		Constraint constraint = constraints.get(0);
		
		Assert.assertTrue(constraint instanceof PrerequisiteConstraint);
		
		PrerequisiteConstraint prerequisiteConstraint = (PrerequisiteConstraint)constraint;
		
		Assert.assertEquals("state", prerequisiteConstraint.getPropertyName());
		
		ConstraintValidationResult result = processorResult.getFirstConstraintValidationResult();
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, result.getStatus());
		Assert.assertEquals(new CaseConstraintProcessor().getName(), result.getConstraintName());
	}
	
	/**
	 * @see BaseConstraintProcessorTest#newProcessor()
	 */
	@Override
	protected CaseConstraintProcessor newProcessor() {
		return new CaseConstraintProcessor();
	}

}
