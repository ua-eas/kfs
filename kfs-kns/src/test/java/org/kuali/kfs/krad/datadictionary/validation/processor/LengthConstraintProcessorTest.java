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
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.krad.datadictionary.AttributeDefinition;
import org.kuali.kfs.krad.datadictionary.BusinessObjectEntry;
import org.kuali.kfs.krad.datadictionary.validation.AttributeValueReader;
import org.kuali.kfs.krad.datadictionary.validation.Company;
import org.kuali.kfs.krad.datadictionary.validation.DictionaryObjectAttributeValueReader;
import org.kuali.kfs.krad.datadictionary.validation.ErrorLevel;
import org.kuali.kfs.krad.datadictionary.validation.result.ConstraintValidationResult;
import org.kuali.kfs.krad.datadictionary.validation.result.DictionaryValidationResult;
import org.kuali.rice.core.api.data.DataType;

import java.util.Collections;


/**
 * Things this test should check:
 *
 * 1. string length within range (success) {@link #testNameWithinRangeSuccess()}
 * 2. string length at top of range (success) {@link #testNameAtTopOfRangeFailure()}
 * 3. string length at bottom of range (success) {@link #testNameAtBottomOfRangeSuccess()}
 * 4. string length below range (failure) {@link #testNameLengthBelowRangeFailure()}
 * 5. string length above range (failure) {@link #testNameLengthAboveRangeFailure()}
 * 6. no length constraints defined (success) {@link #testNameLengthUnconstrainedSuccess()}
 *
 *
 */
public class LengthConstraintProcessorTest {
	private LengthConstraintProcessor processor;

	private AttributeDefinition constrained0to2;
	private AttributeDefinition constrained0to3;
	private AttributeDefinition constrained2to4;
	private AttributeDefinition constrained3to6;
	private AttributeDefinition constrained5to12;
	private AttributeDefinition unconstrained;

	private Company companyWith3LetterName;

	@Before
	public void setUp() throws Exception {

		processor = new LengthConstraintProcessor();

		companyWith3LetterName = new Company("ABC");

		constrained0to2 = new AttributeDefinition() {

			@Override
			public DataType getDataType() {
				return DataType.STRING;
			}

			@Override
			public String getLabel() {
				return "Company Name";
			}

			@Override
			public String getName() {
				return "name";
			}

			@Override
			public Integer getMaxLength() {
				return Integer.valueOf(2);
			}

			@Override
			public Integer getMinLength() {
				return Integer.valueOf(0);
			}

		};

		constrained0to3 = new AttributeDefinition() {

			@Override
			public DataType getDataType() {
				return DataType.STRING;
			}

			@Override
			public String getLabel() {
				return "Company Name";
			}

			@Override
			public String getName() {
				return "name";
			}

			@Override
			public Integer getMaxLength() {
				return Integer.valueOf(3);
			}

			@Override
			public Integer getMinLength() {
				return Integer.valueOf(0);
			}
		};

		constrained2to4 = new AttributeDefinition() {

			@Override
			public DataType getDataType() {
				return DataType.STRING;
			}

			@Override
			public String getLabel() {
				return "Company Name";
			}

			@Override
			public String getName() {
				return "name";
			}

			@Override
			public Integer getMaxLength() {
				return Integer.valueOf(4);
			}

			@Override
			public Integer getMinLength() {
				return Integer.valueOf(2);
			}
		};

		constrained3to6 = new AttributeDefinition() {

			@Override
			public DataType getDataType() {
				return DataType.STRING;
			}

			@Override
			public String getLabel() {
				return "Company Name";
			}

			@Override
			public String getName() {
				return "name";
			}

			@Override
			public Integer getMaxLength() {
				return Integer.valueOf(6);
			}

			@Override
			public Integer getMinLength() {
				return Integer.valueOf(3);
			}
		};

		constrained5to12 = new AttributeDefinition() {

			@Override
			public DataType getDataType() {
				return DataType.STRING;
			}

			@Override
			public String getLabel() {
				return "Company Name";
			}

			@Override
			public String getName() {
				return "name";
			}

			@Override
			public Integer getMaxLength() {
				return Integer.valueOf(5);
			}

			@Override
			public Integer getMinLength() {
				return Integer.valueOf(12);
			}
		};

		unconstrained = new AttributeDefinition() {

			@Override
			public DataType getDataType() {
				return DataType.STRING;
			}

			@Override
			public String getLabel() {
				return "Company Name";
			}

			@Override
			public String getName() {
				return "name";
			}

			@Override
			public Integer getMaxLength() {
				return null;
			}

			@Override
			public Integer getMinLength() {
				return null;
			}
		};
	}


	@Test
	public void testNameWithinRangeSuccess() {
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		dictionaryValidationResult.setErrorLevel(ErrorLevel.NOCONSTRAINT);
		ConstraintValidationResult result = process(dictionaryValidationResult, companyWith3LetterName, constrained2to4, "name");
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, result.getStatus());
		Assert.assertEquals(new LengthConstraintProcessor().getName(), result.getConstraintName());
	}

	/**
	 * Since the top of the range is
	 */
	@Test
	public void testNameAtTopOfRangeFailure() {
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		dictionaryValidationResult.setErrorLevel(ErrorLevel.NOCONSTRAINT);
		ConstraintValidationResult result = process(dictionaryValidationResult, companyWith3LetterName, constrained0to3, "name");
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, result.getStatus());
		Assert.assertEquals(new LengthConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testNameAtBottomOfRangeSuccess() {
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		dictionaryValidationResult.setErrorLevel(ErrorLevel.NOCONSTRAINT);
		ConstraintValidationResult result = process(dictionaryValidationResult, companyWith3LetterName, constrained3to6, "name");
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, result.getStatus());
		Assert.assertEquals(new LengthConstraintProcessor().getName(), result.getConstraintName());
	}

	/*
	 * Verifies that a company object with a collection attribute 'contactEmails' that has 3 elements returns a validation error when the collection
	 * size is constrained to be between 5 and 12 elements
	 */
	@Test
	public void testNameLengthBelowRangeFailure() {
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		dictionaryValidationResult.setErrorLevel(ErrorLevel.NOCONSTRAINT);
		ConstraintValidationResult result = process(dictionaryValidationResult, companyWith3LetterName, constrained5to12, "name");
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new LengthConstraintProcessor().getName(), result.getConstraintName());
	}

	/*
	 * Verifies that a company object with a collection attribute 'contactEmails' that has 3 elements returns a validation error when the collection
	 * size is constrained to be between 0 and 2 elements
	 */
	@Test
	public void testNameLengthAboveRangeFailure() {
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		dictionaryValidationResult.setErrorLevel(ErrorLevel.NOCONSTRAINT);
		ConstraintValidationResult result = process(dictionaryValidationResult, companyWith3LetterName, constrained0to2, "name");
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new LengthConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testNameLengthUnconstrainedSuccess() {
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		dictionaryValidationResult.setErrorLevel(ErrorLevel.NOCONSTRAINT);
		ConstraintValidationResult result = process(dictionaryValidationResult, companyWith3LetterName, unconstrained, "name");
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.NOCONSTRAINT, result.getStatus());
		Assert.assertEquals(new LengthConstraintProcessor().getName(), result.getConstraintName());
	}

	private ConstraintValidationResult process(DictionaryValidationResult dictionaryValidationResult, Object object, AttributeDefinition definition, String attributeName) {
		BusinessObjectEntry entry = new BusinessObjectEntry();
		entry.setAttributes(Collections.singletonList(definition));

		AttributeValueReader attributeValueReader = new DictionaryObjectAttributeValueReader(object, "org.kuali.rice.kns.datadictionary.validation.MockCompany", entry);
		attributeValueReader.setAttributeName(attributeName);

		Object value = attributeValueReader.getValue();

		return processor.process(dictionaryValidationResult, value, definition, attributeValueReader).getFirstConstraintValidationResult();
	}
}
