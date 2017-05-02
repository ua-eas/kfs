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
import org.kuali.kfs.krad.datadictionary.validation.capability.RangeConstrainable;
import org.kuali.kfs.krad.datadictionary.validation.constraint.ValidCharactersConstraint;
import org.kuali.kfs.krad.datadictionary.validation.result.ConstraintValidationResult;
import org.kuali.kfs.krad.datadictionary.validation.result.DictionaryValidationResult;
import org.kuali.rice.core.api.data.DataType;

import java.util.ArrayList;
import java.util.List;


/**
 *
 *
 */
public class RangeConstraintProcessorTest {

    private AttributeDefinition street1Definition;
    private AttributeDefinition street2Definition;
    private AttributeDefinition stateDefinition;
    private AttributeDefinition postalCodeDefinition;
    private BusinessObjectEntry addressEntry;
    private DictionaryValidationResult dictionaryValidationResult;

    private RangeConstraintProcessor processor;

    private Address washingtonDCAddress = new Address("893 Presidential Ave", "Suite 800", "Washington", "DC", "20500", "USA", null);
    private Address newYorkNYAddress = new Address("5 Presidential Street", "Suite 800", "New York", "NY", "10012", "USA", null);
    private Address timbucktooAddress = new Address("5 Presidential Street", "Suite 800", "Timbucktoo", "ZZ", "100000", "USA", null);
    private Address sydneyAUSAddress = new Address("5 Presidential Street", "Suite 800", "Sydney", "ZZ", "999", "USA", null);
    private Address londonUKAddress = new Address("5 Presidential Street", "Suite 800", "Timbucktoo", "ZZ", "99999", "USA", null);
    private Address torontoONAddress = new Address("5 Presidential Street", "Suite 800", "Sydney", "ZZ", "1000", "USA", null);

    @Before
    public void setUp() throws Exception {

        processor = new RangeConstraintProcessor();

        dictionaryValidationResult = new DictionaryValidationResult();
        dictionaryValidationResult.setErrorLevel(ErrorLevel.NOCONSTRAINT);

        addressEntry = new BusinessObjectEntry();

        List<AttributeDefinition> attributes = new ArrayList<AttributeDefinition>();

        ValidCharactersConstraint street1ValidCharactersConstraint = new ValidCharactersConstraint();
        street1ValidCharactersConstraint.setValue("regex:\\d{3}\\s+\\w+\\s+Ave");

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

        ValidCharactersConstraint stateValidCharactersConstraint = new ValidCharactersConstraint();
        stateValidCharactersConstraint.setValue("ABCD");

        stateDefinition = new AttributeDefinition();
        stateDefinition.setName("state");
        stateDefinition.setValidCharactersConstraint(stateValidCharactersConstraint);
        attributes.add(stateDefinition);

        postalCodeDefinition = new AttributeDefinition();
        postalCodeDefinition.setName("postalCode");
        postalCodeDefinition.setExclusiveMin("1000");
        postalCodeDefinition.setInclusiveMax("99999");
        postalCodeDefinition.setDataType(DataType.LONG);
        attributes.add(postalCodeDefinition);

        AttributeDefinition countryDefinition = new AttributeDefinition();
        countryDefinition.setName("country");
        attributes.add(countryDefinition);

        addressEntry.setAttributes(attributes);
    }

    @Test
    public void testNumberWithinRange1() {
        ConstraintValidationResult result = process(washingtonDCAddress, "postalCode", postalCodeDefinition);
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
        Assert.assertEquals(ErrorLevel.OK, result.getStatus());
        Assert.assertEquals(new RangeConstraintProcessor().getName(), result.getConstraintName());
    }

    @Test
    public void testNumberWithinRange2() {
        ConstraintValidationResult result = process(newYorkNYAddress, "postalCode", postalCodeDefinition);
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
        Assert.assertEquals(ErrorLevel.OK, result.getStatus());
        Assert.assertEquals(new RangeConstraintProcessor().getName(), result.getConstraintName());
    }

    @Test
    public void testNumberAboveRange() {
        ConstraintValidationResult result = process(timbucktooAddress, "postalCode", postalCodeDefinition);
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
        Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
        Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
        Assert.assertEquals(new RangeConstraintProcessor().getName(), result.getConstraintName());
    }

    @Test
    public void testNumberBelowRange() {
        ConstraintValidationResult result = process(sydneyAUSAddress, "postalCode", postalCodeDefinition);
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
        Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
        Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
        Assert.assertEquals(new RangeConstraintProcessor().getName(), result.getConstraintName());
    }

    @Test
    public void testNumberAtTopOfRange() {
        ConstraintValidationResult result = process(londonUKAddress, "postalCode", postalCodeDefinition);
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
        Assert.assertEquals(ErrorLevel.OK, result.getStatus());
        Assert.assertEquals(new RangeConstraintProcessor().getName(), result.getConstraintName());
    }

    @Test
    public void testNumberAtBottomOfRange() {
        ConstraintValidationResult result = process(torontoONAddress, "postalCode", postalCodeDefinition);
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
        Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
        Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
        Assert.assertEquals(new RangeConstraintProcessor().getName(), result.getConstraintName());
    }


    private ConstraintValidationResult process(Object object, String attributeName, RangeConstrainable definition) {
        AttributeValueReader attributeValueReader = new DictionaryObjectAttributeValueReader(object, "org.kuali.rice.kns.datadictionary.validation.MockAddress", addressEntry);
        attributeValueReader.setAttributeName(attributeName);

        Object value = attributeValueReader.getValue();
        return processor.process(dictionaryValidationResult, value, definition, attributeValueReader).getFirstConstraintValidationResult();
    }

}
