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
package org.kuali.kfs.sys.rest.service;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.kns.datadictionary.MaintainableCollectionDefinition;
import org.kuali.kfs.kns.datadictionary.MaintainableFieldDefinition;
import org.kuali.kfs.kns.datadictionary.MaintainableItemDefinition;
import org.kuali.kfs.kns.datadictionary.MaintainableSectionDefinition;
import org.kuali.kfs.kns.datadictionary.MaintenanceDocumentEntry;
import org.kuali.kfs.sys.businessobject.TaxRegionRate;
import org.kuali.kfs.sys.rest.BusinessObjectApiResourceTestHelper;
import org.kuali.kfs.sys.rest.helper.CollectionSerializationHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class SerializationServiceTest {

    private MaintenanceDocumentEntry maintenanceDocumentEntry;

    @Before
    public void setup() {
        maintenanceDocumentEntry = EasyMock.createMock(MaintenanceDocumentEntry.class);
    }

    @Test
    public void testBusinessObjectFieldsToMap() {
        List<String> fields = Arrays.asList(
            "accountName",
            "organization.responsibilityCenterCode",
            "organization.responsibilityCenterCode2",
            "organization.responsibilityCenter.responsibilityCenterName",
            "organization.responsibilityCenter.responsibilityCenterName2"
        );

        Map<String, Object> results = SerializationService.businessObjectFieldsToMap(fields);
        Assert.assertEquals(2, results.size());
        Assert.assertEquals(1, ((List<String>)results.get(SerializationService.FIELDS_KEY)).size());
        Assert.assertEquals("accountName", ((List<String>)results.get(SerializationService.FIELDS_KEY)).get(0));
        Map<String, Object> organization = (Map<String, Object>)results.get("organization");
        Assert.assertEquals(2, organization.size());
        Assert.assertEquals(2, ((List<String>)organization.get(SerializationService.FIELDS_KEY)).size());
        Assert.assertEquals("responsibilityCenterCode", ((List<String>)organization.get(SerializationService.FIELDS_KEY)).get(0));
        Map<String, Object> responsibilityCenter = (Map<String, Object>)organization.get("responsibilityCenter");
        Assert.assertEquals(1, responsibilityCenter.size());
        Assert.assertEquals(2, ((List<String>)responsibilityCenter.get(SerializationService.FIELDS_KEY)).size());
        Assert.assertEquals("responsibilityCenterName", ((List<String>)responsibilityCenter.get(SerializationService.FIELDS_KEY)).get(0));
    }

    @Test
    public void testFindBusinessObjectFields() {
        addTaxRegionMaintainbleSections();
        EasyMock.replay(maintenanceDocumentEntry);
        Map<String, Object> fields = SerializationService.findBusinessObjectFields(maintenanceDocumentEntry);
        Assert.assertEquals(2, fields.size());
        Assert.assertEquals(8, ((List<String>)fields.get(SerializationService.FIELDS_KEY)).size());
        Assert.assertEquals("taxRegionCode", ((List<String>)fields.get(SerializationService.FIELDS_KEY)).get(0));
        List<CollectionSerializationHelper> serializationHelpers = (List< CollectionSerializationHelper>)fields.get(SerializationService.COLLECTIONS_KEY);
        Assert.assertEquals(1, serializationHelpers.size());
        CollectionSerializationHelper serializationHelper = serializationHelpers.get(0);
        Assert.assertEquals("taxRegionRates", serializationHelper.getCollectionName());
        Assert.assertEquals(3, serializationHelper.getFields().size());
        Assert.assertEquals(2, serializationHelper.getTranslatedFields().size());
        List<String> collectionTopLevelFields = (List<String>)serializationHelper.getTranslatedFields().get(SerializationService.FIELDS_KEY);
        Assert.assertEquals(2, collectionTopLevelFields.size());
        Assert.assertEquals("effectiveDate", collectionTopLevelFields.get(0));
        Map<String, Object> taxRate = (Map<String, Object>)serializationHelper.getTranslatedFields().get("taxRate");
        Assert.assertEquals(1, taxRate.size());
        List<String> taxRateTopLevelFields = (List<String>)taxRate.get(SerializationService.FIELDS_KEY);
        Assert.assertEquals(1, taxRateTopLevelFields.size());
        Assert.assertEquals("name", taxRateTopLevelFields.get(0));
        EasyMock.verify(maintenanceDocumentEntry);
    }

    private void addTaxRegionMaintainbleSections() {
        List<MaintainableSectionDefinition> maintainableSections = new ArrayList<>();
        MaintainableSectionDefinition maintainableSectionDefinition = new MaintainableSectionDefinition();
        maintainableSections.add(maintainableSectionDefinition);
        List<MaintainableItemDefinition> maintainableItemDefinitions = BusinessObjectApiResourceTestHelper.createItemDefinitions("taxRegionCode",
            "taxRegionName","taxRegionTypeCode","chartOfAccountsCode", "accountNumber","financialObjectCode",
            "taxRegionUseTaxIndicator","active");

        MaintainableCollectionDefinition maintainableCollectionDefinition = new MaintainableCollectionDefinition();
        maintainableCollectionDefinition.setName("taxRegionRates");
        maintainableCollectionDefinition.setBusinessObjectClass(TaxRegionRate.class);
        List<MaintainableFieldDefinition> taxRegionRatesFieldDefinitions = BusinessObjectApiResourceTestHelper.createFieldDefinitions("effectiveDate","taxRateCode","taxRate.name");

        maintainableCollectionDefinition.setMaintainableFields(taxRegionRatesFieldDefinitions);
        maintainableItemDefinitions.add(maintainableCollectionDefinition);
        maintainableSectionDefinition.setMaintainableItems(maintainableItemDefinitions);
        EasyMock.expect(maintenanceDocumentEntry.getMaintainableSections()).andReturn(maintainableSections);
    }
}
