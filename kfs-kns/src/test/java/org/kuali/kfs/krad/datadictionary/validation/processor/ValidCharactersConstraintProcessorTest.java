/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
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
import org.kuali.kfs.krad.datadictionary.validation.Address;
import org.kuali.kfs.krad.datadictionary.validation.AttributeValueReader;
import org.kuali.kfs.krad.datadictionary.validation.DictionaryObjectAttributeValueReader;
import org.kuali.kfs.krad.datadictionary.validation.ErrorLevel;
import org.kuali.kfs.krad.datadictionary.validation.constraint.ValidCharactersConstraint;
import org.kuali.kfs.krad.datadictionary.validation.result.ConstraintValidationResult;
import org.kuali.kfs.krad.datadictionary.validation.result.DictionaryValidationResult;

import java.util.ArrayList;
import java.util.List;


/**
 * Things this test should check:
 * <p>
 * 1. value with restricted valid chars (success)
 * 2. value without restricted valid chars (failure)
 * 3. value matching regular expression (success)
 * 4. value not matching regular expression (failure)
 * 5. value without constraint (no constraint)
 */
public class ValidCharactersConstraintProcessorTest {

    private AttributeDefinition street1Definition;
    private AttributeDefinition street2Definition;
    private AttributeDefinition stateDefinition;
    private BusinessObjectEntry addressEntry;
    private DictionaryValidationResult dictionaryValidationResult;

    private ValidCharactersConstraintProcessor processor;

    private Address washingtonDCAddress = new Address("893 Presidential Ave", "Suite 800", "Washington", "DC", "", "USA", null);
    private Address newYorkNYAddress = new Address("5 Presidential Street", "Suite 800", "New York", "NY", "", "USA", null);

    private ValidCharactersConstraint street1ValidCharactersConstraint;
    private ValidCharactersConstraint stateValidCharactersConstraint;


    @Before
    public void setUp() throws Exception {

        processor = new ValidCharactersConstraintProcessor();

        dictionaryValidationResult = new DictionaryValidationResult();
        dictionaryValidationResult.setErrorLevel(ErrorLevel.NOCONSTRAINT);

        addressEntry = new BusinessObjectEntry();

        List<AttributeDefinition> attributes = new ArrayList<AttributeDefinition>();

        street1ValidCharactersConstraint = new ValidCharactersConstraint();
        street1ValidCharactersConstraint.setValue("\\d{3}\\s+\\w+\\s+Ave");

        street1Definition = new AttributeDefinition();
        street1Definition.setName("street1");
        street1Definition.setValidCharactersConstraint(street1ValidCharactersConstraint);
        attributes.add(street1Definition);

        street2Definition = new AttributeDefinition();
        street2Definition.setName("street2");
        attributes.add(street2Definition);

        AttributeDefinition cityDefinition = new AttributeDefinition();
        cityDefinition.setName("city");
        attributes.add(cityDefinition);

        stateValidCharactersConstraint = new ValidCharactersConstraint();
        stateValidCharactersConstraint.setValue("[ABCD]{2}");

        stateDefinition = new AttributeDefinition();
        stateDefinition.setName("state");
        stateDefinition.setValidCharactersConstraint(stateValidCharactersConstraint);
        attributes.add(stateDefinition);

        AttributeDefinition postalCodeDefinition = new AttributeDefinition();
        postalCodeDefinition.setName("postalCode");
        attributes.add(postalCodeDefinition);

        AttributeDefinition countryDefinition = new AttributeDefinition();
        countryDefinition.setName("country");
        attributes.add(countryDefinition);

        addressEntry.setAttributes(attributes);
    }

    @Test
    public void testValueAllValidChars() {
        ConstraintValidationResult result = process(washingtonDCAddress, "state", stateValidCharactersConstraint);
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
        Assert.assertEquals(ErrorLevel.OK, result.getStatus());
        Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
    }

    @Test
    public void testValueNotValidChars() {
        ConstraintValidationResult result = process(newYorkNYAddress, "state", stateValidCharactersConstraint);
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
        Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
        Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
        Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
    }

    @Test
    public void testValueMatchingRegex() {
        ConstraintValidationResult result = process(washingtonDCAddress, "street1", street1ValidCharactersConstraint);
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
        Assert.assertEquals(ErrorLevel.OK, result.getStatus());
        Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
    }

    @Test
    public void testValueNotMatchingRegex() {
        ConstraintValidationResult result = process(newYorkNYAddress, "street1", street1ValidCharactersConstraint);
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
        Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
        Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
        Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
    }

    @Test
    public void testValueNotConstrained() {
        ConstraintValidationResult result = process(washingtonDCAddress, "street2", null);
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
        Assert.assertEquals(ErrorLevel.NOCONSTRAINT, result.getStatus());
        Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
    }

    private ConstraintValidationResult process(Object object, String attributeName, ValidCharactersConstraint constraint) {
        AttributeValueReader attributeValueReader = new DictionaryObjectAttributeValueReader(object, "org.kuali.rice.kns.datadictionary.validation.MockAddress", addressEntry);
        attributeValueReader.setAttributeName(attributeName);

        Object value = attributeValueReader.getValue();
        return processor.process(dictionaryValidationResult, value, constraint, attributeValueReader).getFirstConstraintValidationResult();
    }

}
