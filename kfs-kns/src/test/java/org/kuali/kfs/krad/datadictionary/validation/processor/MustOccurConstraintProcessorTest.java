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
package org.kuali.kfs.krad.datadictionary.validation.processor;

import org.junit.Assert;
import org.junit.Test;
import org.kuali.kfs.krad.datadictionary.validation.Address;
import org.kuali.kfs.krad.datadictionary.validation.ErrorLevel;
import org.kuali.kfs.krad.datadictionary.validation.result.ConstraintValidationResult;

/**
 * Things this test should check:
 *
 * 1. city and state entered, but no postal code (success) {@link #testCityStateNoPostalSuccess()}
 * 2. city entered, no state or postal code (failure) {@link #testCityNoStateNoPostalFailure()}
 * 3. postal code entered but no city or state (success) {@link #testPostalNoCityStateSuccess()}
 *
 *
 */
public class MustOccurConstraintProcessorTest extends BaseConstraintProcessorTest<MustOccurConstraintProcessor> {

	private Address noPostalCodeAddress = new Address("893 Presidential Ave", "Suite 800", "Washington", "DC", "", "USA", null);
	private Address noStateOrPostalCodeAddress = new Address("893 Presidential Ave", "Suite 800", "Washington", "", "", "USA", null);
	private Address noCityStateAddress = new Address("893 Presidential Ave", "Suite 800", "", "", "12340", "USA", null);

	@Test
	public void testCityStateNoPostalSuccess() {
		ConstraintValidationResult result = process(noPostalCodeAddress, null, topLevelConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, result.getStatus());
		Assert.assertEquals(new MustOccurConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testCityNoStateNoPostalFailure() {
		ConstraintValidationResult result = process(noStateOrPostalCodeAddress, null, topLevelConstraint);
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new MustOccurConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testPostalNoCityStateSuccess() {
		ConstraintValidationResult result = process(noCityStateAddress, null, topLevelConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, result.getStatus());
		Assert.assertEquals(new MustOccurConstraintProcessor().getName(), result.getConstraintName());
	}

	/**
	 * @see org.kuali.rice.kns.datadictionary.validation.processor.BaseConstraintProcessorTest#newProcessor()
	 */
	@Override
	protected MustOccurConstraintProcessor newProcessor() {
		return new MustOccurConstraintProcessor();
	}

}
