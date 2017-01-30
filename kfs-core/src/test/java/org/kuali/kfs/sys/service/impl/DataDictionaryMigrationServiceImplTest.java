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

package org.kuali.kfs.sys.service.impl;

import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountGlobal;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.OrganizationType;
import org.kuali.kfs.kns.datadictionary.MaintainableCollectionDefinition;
import org.kuali.kfs.kns.datadictionary.MaintainableFieldDefinition;
import org.kuali.kfs.kns.datadictionary.MaintainableItemDefinition;
import org.kuali.kfs.kns.datadictionary.MaintainableSectionDefinition;
import org.kuali.kfs.kns.datadictionary.MaintenanceDocumentEntry;
import org.kuali.kfs.kns.service.DataDictionaryService;
import org.kuali.kfs.krad.bo.ModuleConfiguration;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.datadictionary.AttributeDefinition;
import org.kuali.kfs.krad.datadictionary.BusinessObjectEntry;
import org.kuali.kfs.krad.datadictionary.DataDictionary;
import org.kuali.kfs.krad.datadictionary.DocumentEntry;
import org.kuali.kfs.krad.datadictionary.TransactionalDocumentEntry;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.krad.service.PersistenceStructureService;
import org.kuali.kfs.sys.batch.DataDictionaryMigrationField;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.dto.EntityDTO;
import org.kuali.kfs.sys.businessobject.dto.FieldDTO;
import org.kuali.kfs.sys.businessobject.dto.TableDTO;
import org.kuali.kfs.vnd.businessobject.AddressType;
import org.kuali.kfs.vnd.businessobject.CampusParameter;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorContact;
import org.kuali.kfs.vnd.businessobject.VendorDefaultAddress;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.businessobject.VendorHeader;
import org.kuali.kfs.vnd.businessobject.VendorPhoneNumber;
import org.kuali.rice.krad.bo.BusinessObject;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(EasyMockRunner.class)
public class DataDictionaryMigrationServiceImplTest {
    @TestSubject
    DataDictionaryMigrationServiceImpl dataDictionaryMigrationService = new DataDictionaryMigrationServiceImpl();

    @Mock
    DataDictionaryService dataDictionaryService;

    @Mock
    DataDictionary dataDictionary;

    @Mock
    BusinessObjectEntry businessObjectEntry;

    @Mock
    PersistenceStructureService persistenceStructureService;

    @Mock
    KualiModuleService kualiModuleService;

    @Mock
    ModuleService moduleService;

    @Test
    public void testRetrieveAllMaintenanceDocumentEntries() throws Exception {
        EasyMock.expect(dataDictionaryService.getDataDictionary()).andReturn(dataDictionary).anyTimes();
        Map<String, DocumentEntry> documentEntries = new HashMap<>();
        documentEntries.put("FR", buildAssetFabricationMaintenanceDocumentEntryFixture());
        documentEntries.put("ACCT", buildAccountMaintenanceDocumentEntryFixture());
        documentEntries.put("ORR", buildOrganizationReviewRoleMaintenanceDocumentEntryFixture());
        documentEntries.put("FollowYourArrow", buildFakeTransactionalDocumentEntryFixture());
        documentEntries.put("GACC", buildAccountGlobalDocumentEntryFixture());
        EasyMock.expect(dataDictionary.getDocumentEntries()).andReturn(documentEntries);
        BusinessObjectEntry orrEntry = new BusinessObjectEntry();
        orrEntry.setBusinessObjectClass(org.kuali.kfs.coa.businessobject.OrganizationType.class);
        orrEntry.setObjectLabel("Organization Type");
        EasyMock.expect(dataDictionary.getBusinessObjectEntry("org.kuali.kfs.coa.businessobject.OrganizationType")).andReturn(orrEntry).anyTimes();
        dataDictionaryMigrationService.setFilteredEntities(Arrays.asList("FR","ORR"));

        EasyMock.replay(dataDictionaryService, dataDictionary);

        final List<MaintenanceDocumentEntry> maintenanceDocumentEntries = dataDictionaryMigrationService.retrieveAllMaintenanceDocumentEntries();

        EasyMock.verify(dataDictionaryService, dataDictionary);
        Assert.assertNotNull("We should have gotten back a list of maintenance document entries", maintenanceDocumentEntries);
        Assert.assertEquals("The size of the maintenance document entries list should be 1", 1, maintenanceDocumentEntries.size());
        Assert.assertEquals("The only maintenance document to be returned should be \"ACCT\"", "ACCT", maintenanceDocumentEntries.get(0).getDocumentTypeName());
    }

    @Test
    public void testConvertMaintenanceDocumentToEntityDTO() throws Exception {
        MaintenanceDocumentEntry maintenanceDocumentEntry = buildAccountMaintenanceDocumentEntryFixture();
        ModuleConfiguration moduleConfiguration = new ModuleConfiguration();
        moduleConfiguration.setNamespaceCode("LUSH-LIT UP");
        EasyMock.expect(kualiModuleService.getResponsibleModuleService(Account.class)).andReturn(moduleService);
        EasyMock.expect(moduleService.getModuleConfiguration()).andReturn(moduleConfiguration);
        EasyMock.expect(dataDictionaryService.getDataDictionary()).andReturn(dataDictionary);
        EasyMock.expect(dataDictionary.getBusinessObjectEntry("org.kuali.kfs.coa.businessobject.Account")).andReturn(businessObjectEntry);
        EasyMock.expect(businessObjectEntry.getObjectLabel()).andReturn("Account"); // this gets called twice, so we'll simply expect it again
        initializeExpectationsForAccountTable();
        setupFilteredFields();

        EasyMock.replay(dataDictionaryService, dataDictionary, businessObjectEntry, persistenceStructureService, kualiModuleService, moduleService);

        Map<String, List<Class<? extends PersistableBusinessObject>>> businessObjectsOwnedByEntities = new HashMap<>();
        businessObjectsOwnedByEntities.put("ACCT", Arrays.asList(Account.class));
        final EntityDTO entityDTO = dataDictionaryMigrationService.convertMaintenanceDocumentToEntityDTO(maintenanceDocumentEntry, businessObjectsOwnedByEntities);

        EasyMock.verify(dataDictionaryService, dataDictionary, businessObjectEntry, persistenceStructureService, kualiModuleService, moduleService);

        Assert.assertNotNull("We should have gotten back an EntityDTO", entityDTO);
        Assert.assertEquals("The module namespace should be \"LUSH-LIT UP\" as that's what I was listening to at the time", "LUSH-LIT UP", entityDTO.getModuleCode());
        Assert.assertEquals("The code for the Entity should be \"ACCT\"", "ACCT", entityDTO.getCode());
        Assert.assertEquals("The name for the Entity should be \"Account\"", "Account", entityDTO.getName());
        Assert.assertNull("The description for the Entity should be null", entityDTO.getDescription());
        assertAgainstTableDTOs(entityDTO.getTables());
    }

    @Test
    public void testBuildTableDTOs_NotPersistableClass() {
        EasyMock.expect(persistenceStructureService.isPersistable(EasyMock.anyObject())).andReturn(false);
        EasyMock.replay(persistenceStructureService);

        final List<TableDTO> tableDTOs = dataDictionaryMigrationService.buildTableDTOs(Arrays.asList((Class<? extends PersistableBusinessObject>)Account.class));

        EasyMock.verify(persistenceStructureService);
        Assert.assertEquals(0, tableDTOs.size());
    }

    @Test
    public void testBuildTableDTOs() {
        MaintenanceDocumentEntry documentEntry = buildAccountMaintenanceDocumentEntryFixture();
        initializeExpectationsForAccountTable();
        setupFilteredFields();

        EasyMock.replay(dataDictionaryService, dataDictionary, businessObjectEntry, persistenceStructureService);

        final List<TableDTO> tableDTOs = dataDictionaryMigrationService.buildTableDTOs(Arrays.asList((Class<? extends PersistableBusinessObject>)documentEntry.getDataObjectClass()));

        EasyMock.verify(dataDictionaryService, dataDictionary, businessObjectEntry, persistenceStructureService);
        assertAgainstTableDTOs(tableDTOs);
    }

    protected void setupFilteredFields() {
        dataDictionaryMigrationService.setFilteredFields(Arrays.asList(new DataDictionaryMigrationField("*.objectId"),
                new DataDictionaryMigrationField("*.versionNumber"),
                new DataDictionaryMigrationField("*.lastUpdatedTimestamp")));
    }

    @Test
    public void testBuildTableDTOs_Filtered() {
        MaintenanceDocumentEntry documentEntry = buildAccountMaintenanceDocumentEntryFixture();
        dataDictionaryMigrationService.setFilteredTables(Arrays.asList("Account"));

        final List<TableDTO> tableDTOs = dataDictionaryMigrationService.buildTableDTOs(Arrays.asList((Class<? extends PersistableBusinessObject>)documentEntry.getDataObjectClass()));

        Assert.assertNotNull("We should have gotten back a List of TableDTOs", tableDTOs);
        Assert.assertTrue("That list of TableDTOs should be empty", tableDTOs.isEmpty());
    }

    @Test
    public void testBuildTableDTO() {
        initializeExpectationsForAccountTable();
        setupFilteredFields();

        EasyMock.replay(dataDictionaryService, dataDictionary, businessObjectEntry, persistenceStructureService);

        final TableDTO tableDTO = dataDictionaryMigrationService.buildTableDTO(Account.class);

        EasyMock.verify(dataDictionaryService, dataDictionary, businessObjectEntry, persistenceStructureService);
        assertAgainstAccountTableDTO(tableDTO);
    }

    @Test
    public void testBuildTableDTO_NonPersistable() {
        EasyMock.expect(persistenceStructureService.isPersistable(Bank.class)).andReturn(false);
        EasyMock.replay(persistenceStructureService);

        final TableDTO tableDTO = dataDictionaryMigrationService.buildTableDTO(Bank.class);

        EasyMock.verify(persistenceStructureService);
        Assert.assertNull("The TableDTO should be null", tableDTO);
    }

    @Test
    public void testBuildFieldDTOs() {
        initializedExpectationsForAccount();
        setupFilteredFields();

        EasyMock.replay(dataDictionaryService, dataDictionary, businessObjectEntry, persistenceStructureService);

        final List<FieldDTO> fieldDTOs = dataDictionaryMigrationService.buildFieldDTOs(Account.class);

        EasyMock.verify(dataDictionaryService, dataDictionary, businessObjectEntry, persistenceStructureService);
        assertAgainstFieldDTOs(fieldDTOs);
    }

    @Test
    public void testBuildFieldDTO() {
        final AttributeDefinition chartOfAccountsAttributeDefinition = buildChartOfAccountsCodeAttributeDefintion();
        EasyMock.expect(dataDictionaryService.getDataDictionary()).andReturn(dataDictionary);
        EasyMock.expect(dataDictionary.getBusinessObjectEntry("org.kuali.kfs.coa.businessobject.Chart")).andReturn(businessObjectEntry);
        EasyMock.expect(businessObjectEntry.getAttributeDefinition("chartOfAccountsCode")).andReturn(chartOfAccountsAttributeDefinition);
        EasyMock.expect(persistenceStructureService.getColumnNameForFieldName(Chart.class, "chartOfAccountsCode")).andReturn("FIN_COA_CD");

        EasyMock.replay(dataDictionaryService, dataDictionary, businessObjectEntry, persistenceStructureService);

        final FieldDTO fieldDTO = dataDictionaryMigrationService.buildFieldDTO(Chart.class, "chartOfAccountsCode");

        EasyMock.verify(dataDictionaryService, dataDictionary, businessObjectEntry, persistenceStructureService);
        Assert.assertNotNull("FieldDTO should not be null", fieldDTO);
        Assert.assertEquals("FieldDTO code should be \"FIN_COA_CD\"", "FIN_COA_CD", fieldDTO.getCode());
        Assert.assertEquals("FieldDTO name should be \"Chart\"", "Chart", fieldDTO.getName());
        Assert.assertEquals("FieldDTO type should be \"text\"", "text", fieldDTO.getFieldType());
        Assert.assertEquals("FieldDTO short name should be \"Crt\"", "Crt", fieldDTO.getShortName());
        Assert.assertEquals("FieldDTO length should be 2", 2, fieldDTO.getLength());
        Assert.assertTrue("FieldDTO should be required", fieldDTO.isRequired());
    }

    @Test
    public void testBuildFieldDTO_NullAttribute() {
        EasyMock.expect(dataDictionaryService.getDataDictionary()).andReturn(dataDictionary);
        EasyMock.expect(dataDictionary.getBusinessObjectEntry("org.kuali.kfs.coa.businessobject.Chart")).andReturn(businessObjectEntry);
        EasyMock.expect(businessObjectEntry.getAttributeDefinition("fourSticks")).andReturn(null);

        EasyMock.replay(dataDictionaryService, dataDictionary, businessObjectEntry);

        final FieldDTO fieldDTO = dataDictionaryMigrationService.buildFieldDTO(Chart.class, "fourSticks");
        Assert.assertNull("FieldDTO should be null", fieldDTO);
    }

    @Test
    public void testDetermineFieldType_Currency() {
        final String type = dataDictionaryMigrationService.determineFieldType(GeneralLedgerPendingEntry.class, "transactionLedgerEntryAmount");
        Assert.assertNotNull("The returned type isn't null", type);
        Assert.assertEquals("The returned type is \"currency\"", "currency", type);
    }

    @Test
    public void testDetermineFieldType_Number() {
        final String type = dataDictionaryMigrationService.determineFieldType(GeneralLedgerPendingEntry.class, "transactionLedgerEntrySequenceNumber");
        Assert.assertNotNull("The returned type isn't null", type);
        Assert.assertEquals("The returned type is \"number\"", "number", type);
    }

    @Test
    public void testDetermineFieldType_Date() {
        final String type = dataDictionaryMigrationService.determineFieldType(GeneralLedgerPendingEntry.class, "transactionDate");
        Assert.assertNotNull("The returned type isn't null", type);
        Assert.assertEquals("The returned type is \"date\"", "date", type);
    }

    @Test
    public void testDetermineFieldType_Datetime() {
        final String type = dataDictionaryMigrationService.determineFieldType(GeneralLedgerPendingEntry.class, "transactionEntryProcessedTs");
        Assert.assertNotNull("The returned type isn't null", type);
        Assert.assertEquals("The returned type is \"datetime\"", "datetime", type);
    }

    @Test
    public void testDetermineFieldType_Indicator() {
        final String type = dataDictionaryMigrationService.determineFieldType(GeneralLedgerPendingEntry.class, "transactionEntryOffsetIndicator");
        Assert.assertNotNull("The returned type isn't null", type);
        Assert.assertEquals("The returned type is \"indicator\"", "indicator", type);
    }

    @Test
    public void testDetermineFieldType_PhoneNumber() {
        final String type = dataDictionaryMigrationService.determineFieldType(VendorPhoneNumber.class, "vendorPhoneNumber");
        Assert.assertNotNull("The returned type isn't null", type);
        Assert.assertEquals("The returned type is \"phone\"", "phone", type);
    }

    @Test
    public void testDetermineFieldType_Email() {
        final String type = dataDictionaryMigrationService.determineFieldType(VendorContact.class, "vendorContactEmailAddress");
        Assert.assertNotNull("The returned type isn't null", type);
        Assert.assertEquals("The returned type is \"email\"", "email", type);
    }

    @Test
    public void testDetermineFieldType_Text() {
        final String type = dataDictionaryMigrationService.determineFieldType(GeneralLedgerPendingEntry.class, "chartOfAccountsCode");
        Assert.assertNotNull("The returned type isn't null", type);
        Assert.assertEquals("The returned type is \"text\"", "text", type);
    }

    @Test
    public void testInputStreamToString() throws Exception {
        final String message = "Narana, pum, pum, Popocatepetl";
        final ByteArrayInputStream messageStream = new ByteArrayInputStream(message.getBytes());
        final String retrievedMessage = dataDictionaryMigrationService.inputStreamToString(messageStream);
        Assert.assertNotNull("Retrieved Message should not be null", retrievedMessage);
        Assert.assertEquals("Retrieved Message should be equal to original message", message, retrievedMessage);
    }

    @Test
    public void testRetrieveObjectLabel() {
        EasyMock.expect(dataDictionaryService.getDataDictionary()).andReturn(dataDictionary);
        EasyMock.expect(dataDictionary.getBusinessObjectEntry("org.kuali.kfs.coa.businessobject.Chart")).andReturn(businessObjectEntry);
        EasyMock.expect(businessObjectEntry.getObjectLabel()).andReturn("Chart");

        EasyMock.replay(dataDictionaryService, dataDictionary, businessObjectEntry);

        final String label = dataDictionaryMigrationService.retrieveObjectLabel(Chart.class);

        EasyMock.verify(dataDictionaryService, dataDictionary, businessObjectEntry);
        Assert.assertNotNull("We should have gotten back a label", label);
        Assert.assertEquals("Label should be \"Chart\"", "Chart", label);
    }

    @Test
    public void testThieveAttributeClassFromBusinessObjectClass() {
        final Class<?> theftedAttributeClass = dataDictionaryMigrationService.thieveAttributeClassFromBusinessObjectClass(Chart.class, "chartOfAccountsCode");
        Assert.assertNotNull("We should have gotten an attribute class back", theftedAttributeClass);
        Assert.assertEquals("And that thefted attribute class should be a String", String.class, theftedAttributeClass);
    }

    @Test
    public void testRetrieveAttribute() {
        AttributeDefinition chartOfAccountsAttributeDefinition = new AttributeDefinition();
        chartOfAccountsAttributeDefinition.setName("chartOfAccountsCode");
        EasyMock.expect(dataDictionaryService.getDataDictionary()).andReturn(dataDictionary);
        EasyMock.expect(dataDictionary.getBusinessObjectEntry("org.kuali.kfs.coa.businessobject.Chart")).andReturn(businessObjectEntry);
        EasyMock.expect(businessObjectEntry.getAttributeDefinition("chartOfAccountsCode")).andReturn(chartOfAccountsAttributeDefinition);

        EasyMock.replay(dataDictionaryService, dataDictionary, businessObjectEntry);

        final AttributeDefinition returnedAttributeDefinition = dataDictionaryMigrationService.retrieveAttribute(Chart.class, "chartOfAccountsCode");
        EasyMock.verify(dataDictionaryService, dataDictionary, businessObjectEntry);
        Assert.assertNotNull("We should have gotten back an attribute definition", returnedAttributeDefinition);
        Assert.assertEquals("The attribute definition we got back should be the same as the one we mocked out", chartOfAccountsAttributeDefinition, returnedAttributeDefinition);
        Assert.assertEquals("The attribute definition should have a name of \"chartOfAccountsCode\"", "chartOfAccountsCode", returnedAttributeDefinition.getName());
    }

    @Test
    public void testRetrieveAttribute_Null() {
        AttributeDefinition chartOfAccountsAttributeDefinition = new AttributeDefinition();
        chartOfAccountsAttributeDefinition.setName("chartOfAccountsCode");
        EasyMock.expect(dataDictionaryService.getDataDictionary()).andReturn(dataDictionary);
        EasyMock.expect(dataDictionary.getBusinessObjectEntry("org.kuali.kfs.coa.businessobject.Chart")).andReturn(businessObjectEntry);
        EasyMock.expect(businessObjectEntry.getAttributeDefinition("scarlet")).andReturn(null);

        EasyMock.replay(dataDictionaryService, dataDictionary, businessObjectEntry);

        final AttributeDefinition returnedAttributeDefinition = dataDictionaryMigrationService.retrieveAttribute(Chart.class, "scarlet");
        EasyMock.verify(dataDictionaryService, dataDictionary, businessObjectEntry);
        Assert.assertNull("We should have not gotten back an attribute definition", returnedAttributeDefinition);
    }

    @Test
    public void testRetrieveAttribute_NullTable() {
        AttributeDefinition chartOfAccountsAttributeDefinition = new AttributeDefinition();
        chartOfAccountsAttributeDefinition.setName("chartOfAccountsCode");
        EasyMock.expect(dataDictionaryService.getDataDictionary()).andReturn(dataDictionary);
        EasyMock.expect(dataDictionary.getBusinessObjectEntry("org.kuali.kfs.coa.businessobject.Chart")).andReturn(null);

        EasyMock.replay(dataDictionaryService, dataDictionary, businessObjectEntry);

        final AttributeDefinition returnedAttributeDefinition = dataDictionaryMigrationService.retrieveAttribute(Chart.class, "scarlet");
        EasyMock.verify(dataDictionaryService, dataDictionary, businessObjectEntry);
        Assert.assertNull("We should have not gotten back an attribute definition", returnedAttributeDefinition);
    }

    @Test
    public void testListAllTakenBusinessObjects_Collection() {
        MaintenanceDocumentEntry documentEntry = buildVendorMaintenanceDocumentEntryFixture();
        initializeExpectationsForVendorTable();

        List<MaintenanceDocumentEntry> entities = Arrays.asList(documentEntry);

        Map<Class<? extends PersistableBusinessObject>,String> nestedBusinessObjects = dataDictionaryMigrationService.listAllTakenBusinessObjects(entities);

        Assert.assertEquals(3, nestedBusinessObjects.size());
        Assert.assertEquals("PVEN", nestedBusinessObjects.get(VendorAddress.class));
        Assert.assertEquals("PVEN", nestedBusinessObjects.get(VendorDetail.class));
        Assert.assertEquals("PVEN", nestedBusinessObjects.get(VendorDefaultAddress.class));
    }

    @Test
    public void testListAllTakenBusinessObjects_ClassAlreadyTaken() {
        MaintenanceDocumentEntry documentEntry = buildVendorWithAccountsMaintenanceDocumentEntryFixture();
        MaintenanceDocumentEntry accountEntry = buildAccountMaintenanceDocumentEntryFixture();
        initializeExpectationsForVendorTable();

        List<MaintenanceDocumentEntry> entities = Arrays.asList(accountEntry, documentEntry);

        Map<Class<? extends PersistableBusinessObject>,String> nestedBusinessObjects = dataDictionaryMigrationService.listAllTakenBusinessObjects(entities);

        Assert.assertEquals(4, nestedBusinessObjects.size());
        Assert.assertEquals("PVEN", nestedBusinessObjects.get(VendorAddress.class));
        Assert.assertEquals("PVEN", nestedBusinessObjects.get(VendorDetail.class));
        Assert.assertEquals("PVEN", nestedBusinessObjects.get(VendorDefaultAddress.class));
        Assert.assertNotEquals("PVEN", nestedBusinessObjects.get(Account.class));
    }

    @Test
    public void testAssignUnclaimedAttributeClasses() {
        Map<Class<? extends PersistableBusinessObject>, String> businessObjectsOwnedByEntities = new HashMap<>();
        Map<Class<? extends PersistableBusinessObject>, String> nestedBusinessObjects = new HashMap<>();
        String documentTypeName = "PVEN";
        Class<? extends BusinessObject > businessObjectClass = VendorDetail.class;
        MaintenanceDocumentEntry vendorEntry = buildVendorMaintenanceDocumentEntryFixture();
        List<? extends MaintainableItemDefinition> maintainableItems = vendorEntry.getMaintainableSections().get(1).getMaintainableItems();

        dataDictionaryMigrationService.assignUnclaimedAttributeClasses(businessObjectsOwnedByEntities, nestedBusinessObjects, documentTypeName, businessObjectClass, maintainableItems);

        Assert.assertEquals(1, nestedBusinessObjects.size());
        Assert.assertEquals("PVEN", nestedBusinessObjects.get(VendorHeader.class));
    }

    @Test
    public void testAssignUnclaimedAttributeClasses_AlreadyClaimed() {
        Map<Class<? extends PersistableBusinessObject>, String> businessObjectsOwnedByEntities = new HashMap<>();
        Map<Class<? extends PersistableBusinessObject>, String> nestedBusinessObjects = new HashMap<>();
        nestedBusinessObjects.put(VendorHeader.class, "BOB");
        String documentTypeName = "PVEN";
        Class<? extends BusinessObject > businessObjectClass = VendorDetail.class;
        MaintenanceDocumentEntry vendorEntry = buildVendorMaintenanceDocumentEntryFixture();
        List<? extends MaintainableItemDefinition> maintainableItems = vendorEntry.getMaintainableSections().get(1).getMaintainableItems();

        dataDictionaryMigrationService.assignUnclaimedAttributeClasses(businessObjectsOwnedByEntities, nestedBusinessObjects, documentTypeName, businessObjectClass, maintainableItems);

        Assert.assertEquals(1, nestedBusinessObjects.size());
        Assert.assertEquals("BOB", nestedBusinessObjects.get(VendorHeader.class));
    }

    @Test
    public void testAssignUnclaimedAttributeClasses_UnconditionallyReadonly() {
        Map<Class<? extends PersistableBusinessObject>, String> businessObjectsOwnedByEntities = new HashMap<>();
        Map<Class<? extends PersistableBusinessObject>, String> nestedBusinessObjects = new HashMap<>();
        String documentTypeName = "PVEN";
        Class<? extends BusinessObject > businessObjectClass = VendorDetail.class;


        List<MaintainableItemDefinition> fields = new ArrayList<>();
        MaintainableFieldDefinition vendorTaxTypeCode = new MaintainableFieldDefinition();
        vendorTaxTypeCode.setName("vendorHeader.vendorTaxTypeCode");
        vendorTaxTypeCode.setUnconditionallyReadOnly(true);
        fields.add(vendorTaxTypeCode);

        dataDictionaryMigrationService.assignUnclaimedAttributeClasses(businessObjectsOwnedByEntities, nestedBusinessObjects, documentTypeName, businessObjectClass, fields);

        Assert.assertEquals(0, nestedBusinessObjects.size());
    }

    @Test
    public void testAssignUnclaimedAttributeClasses_UnknownAttributeKey() {
        Map<Class<? extends PersistableBusinessObject>, String> businessObjectsOwnedByEntities = new HashMap<>();
        Map<Class<? extends PersistableBusinessObject>, String> nestedBusinessObjects = new HashMap<>();
        String documentTypeName = "PVEN";
        Class<? extends BusinessObject > businessObjectClass = VendorDetail.class;


        List<MaintainableItemDefinition> fields = new ArrayList<>();
        MaintainableFieldDefinition vendorTaxTypeCode = new MaintainableFieldDefinition();
        vendorTaxTypeCode.setName("garbage.vendorTaxTypeCode");
        fields.add(vendorTaxTypeCode);

        dataDictionaryMigrationService.assignUnclaimedAttributeClasses(businessObjectsOwnedByEntities, nestedBusinessObjects, documentTypeName, businessObjectClass, fields);

        Assert.assertEquals(0, nestedBusinessObjects.size());
    }

    @Test
    public void testListNestedBusinessObjects() {
        MaintenanceDocumentEntry documentEntry = buildVendorWithAccountsMaintenanceDocumentEntryFixture();

        List<MaintenanceDocumentEntry> entities = Arrays.asList(documentEntry);
        Map<Class<? extends PersistableBusinessObject>, String> businessObjectsOwnedByEntities = new HashMap<>();

        Map<Class<? extends PersistableBusinessObject>,String> nestedBusinessObjects = dataDictionaryMigrationService.listNestedBusinessObjects(entities, businessObjectsOwnedByEntities);

        Assert.assertEquals(1, nestedBusinessObjects.size());
        Assert.assertEquals("PVEN", nestedBusinessObjects.get(VendorHeader.class));
    }

    @Test
    public void testListNestedBusinessObjects_NestedFieldsInCollection() {
        MaintenanceDocumentEntry documentEntry = buildVendorWithAccountsMaintenanceDocumentEntryFixture();
        MaintainableSectionDefinition collectionSection = documentEntry.getMaintainableSections().get(0);
        MaintainableCollectionDefinition collection = (MaintainableCollectionDefinition)collectionSection.getMaintainableItems().get(0);
        List<MaintainableFieldDefinition> collectionFields = collection.getMaintainableFields();
        MaintainableFieldDefinition nestedField = new MaintainableFieldDefinition();
        nestedField.setName("vendorAddressType.itreallydoesnotmatter");
        collectionFields.add(nestedField);


        List<MaintenanceDocumentEntry> entities = Arrays.asList(documentEntry);
        Map<Class<? extends PersistableBusinessObject>, String> businessObjectsOwnedByEntities = new HashMap<>();

        Map<Class<? extends PersistableBusinessObject>,String> nestedBusinessObjects = dataDictionaryMigrationService.listNestedBusinessObjects(entities, businessObjectsOwnedByEntities);

        Assert.assertEquals("PVEN", nestedBusinessObjects.get(AddressType.class));
    }

    @Test
    public void testListNestedBusinessObjects_NestedFieldsInSubCollection() {
        MaintenanceDocumentEntry documentEntry = buildVendorWithAccountsMaintenanceDocumentEntryFixture();
        MaintainableSectionDefinition collectionSection = documentEntry.getMaintainableSections().get(0);
        MaintainableCollectionDefinition collection = (MaintainableCollectionDefinition)collectionSection.getMaintainableItems().get(0);
        MaintainableCollectionDefinition subCollection = collection.getMaintainableCollections().get(0);
        List<MaintainableFieldDefinition> subCollectionFields = subCollection.getMaintainableFields();

        MaintainableFieldDefinition nestedField = new MaintainableFieldDefinition();
        nestedField.setName("vendorCampus.itreallydoesnotmatter");
        subCollectionFields.add(nestedField);

        List<MaintenanceDocumentEntry> entities = Arrays.asList(documentEntry);
        Map<Class<? extends PersistableBusinessObject>, String> businessObjectsOwnedByEntities = new HashMap<>();

        Map<Class<? extends PersistableBusinessObject>,String> nestedBusinessObjects = dataDictionaryMigrationService.listNestedBusinessObjects(entities, businessObjectsOwnedByEntities);

        Assert.assertEquals("PVEN", nestedBusinessObjects.get(CampusParameter.class));
    }

    private AttributeDefinition buildChartOfAccountsCodeAttributeDefintion() {
        AttributeDefinition chartOfAccountsAttributeDefinition = new AttributeDefinition();
        chartOfAccountsAttributeDefinition.setName("chartOfAccountsCode");
        chartOfAccountsAttributeDefinition.setLabel("Chart");
        chartOfAccountsAttributeDefinition.setShortLabel("Crt");
        chartOfAccountsAttributeDefinition.setMaxLength(2);
        chartOfAccountsAttributeDefinition.setRequired(true);
        return chartOfAccountsAttributeDefinition;
    }

    private AttributeDefinition buildAccountNumberAttributeDefintion() {
        AttributeDefinition accountNumberAccountDefinition = new AttributeDefinition();
        accountNumberAccountDefinition.setName("accountNumber");
        accountNumberAccountDefinition.setLabel("Account Number");
        accountNumberAccountDefinition.setShortLabel("Acct");
        accountNumberAccountDefinition.setMaxLength(7);
        accountNumberAccountDefinition.setRequired(true);
        return accountNumberAccountDefinition;
    }

    private AttributeDefinition buildVendorNumberAttributeDefintion() {
        AttributeDefinition vendorNumberAttributeDefinition = new AttributeDefinition();
        vendorNumberAttributeDefinition.setName("vendorNumber");
        vendorNumberAttributeDefinition.setLabel("Vendor Number");
        vendorNumberAttributeDefinition.setShortLabel("Vnd Num");
        vendorNumberAttributeDefinition.setMaxLength(2);
        vendorNumberAttributeDefinition.setRequired(true);
        return vendorNumberAttributeDefinition;
    }

    private AttributeDefinition buildVendorNameAttributeDefintion() {
        AttributeDefinition vendorNamAttributeDefinition = new AttributeDefinition();
        vendorNamAttributeDefinition.setName("vendorName");
        vendorNamAttributeDefinition.setLabel("Vendor Name");
        vendorNamAttributeDefinition.setShortLabel("Name");
        vendorNamAttributeDefinition.setMaxLength(20);
        vendorNamAttributeDefinition.setRequired(true);
        return vendorNamAttributeDefinition;
    }

    private void initializedExpectationsForAccount() {
        final AttributeDefinition chartOfAccountsAttributeDefinition = buildChartOfAccountsCodeAttributeDefintion();
        final AttributeDefinition accountNumberAttributeDefinition = buildAccountNumberAttributeDefintion();
        EasyMock.expect(persistenceStructureService.listFieldNames(Account.class)).andReturn(Arrays.asList("chartOfAccountsCode", "accountNumber", "objectId", "versionNumber", "lastUpdatedTimestamp", "goodToBeOnTheRoadBackHome"));
        EasyMock.expect(dataDictionaryService.getDataDictionary()).andReturn(dataDictionary).times(3);
        EasyMock.expect(dataDictionary.getBusinessObjectEntry("org.kuali.kfs.coa.businessobject.Account")).andReturn(businessObjectEntry).times(3);
        EasyMock.expect(businessObjectEntry.getAttributeDefinition("chartOfAccountsCode")).andReturn(chartOfAccountsAttributeDefinition);
        EasyMock.expect(persistenceStructureService.getColumnNameForFieldName(Account.class, "chartOfAccountsCode")).andReturn("FIN_COA_CD");
        EasyMock.expect(businessObjectEntry.getAttributeDefinition("accountNumber")).andReturn(accountNumberAttributeDefinition);
        EasyMock.expect(persistenceStructureService.getColumnNameForFieldName(Account.class, "accountNumber")).andReturn("ACCOUNT_NBR");
        EasyMock.expect(businessObjectEntry.getAttributeDefinition("goodToBeOnTheRoadBackHome")).andReturn(null);
    }

    private void initializedExpectationsForVendor() {
        final AttributeDefinition vendorNumberAttributeDefinition = buildVendorNumberAttributeDefintion();
        final AttributeDefinition vendorNameAttributeDefinition = buildVendorNameAttributeDefintion();
        EasyMock.expect(persistenceStructureService.listFieldNames(VendorDetail.class)).andReturn(Arrays.asList("vendorNumber", "vendorName"));
        EasyMock.expect(dataDictionaryService.getDataDictionary()).andReturn(dataDictionary).times(3);
        EasyMock.expect(dataDictionary.getBusinessObjectEntry("org.kuali.kfs.vnd.businessobject.VendorDetail")).andReturn(businessObjectEntry).times(3);
        EasyMock.expect(businessObjectEntry.getAttributeDefinition("vendorNumber")).andReturn(vendorNumberAttributeDefinition);
        EasyMock.expect(persistenceStructureService.getColumnNameForFieldName(VendorDetail.class, "vendorNumber")).andReturn("VNDR_NBR");
        EasyMock.expect(businessObjectEntry.getAttributeDefinition("vendorName")).andReturn(vendorNameAttributeDefinition);
        EasyMock.expect(persistenceStructureService.getColumnNameForFieldName(VendorDetail.class, "vendorName")).andReturn("VNDR_NM");
    }

    private void assertAgainstFieldDTOs(List<FieldDTO> fieldDTOs) {
        Assert.assertNotNull("The fieldDTOs list should not be null", fieldDTOs);
        Assert.assertEquals("The size of the fieldDTOs list should be 2", 2, fieldDTOs.size());
        Assert.assertEquals("The first fieldDTO should have a code of \"FIN_COA_CD\"", "FIN_COA_CD", fieldDTOs.get(0).getCode());
        Assert.assertEquals("The first fieldDTO should have a code of \"ACCOUNT_NBR\"", "ACCOUNT_NBR", fieldDTOs.get(1).getCode());
    }

    private void initializeExpectationsForAccountTable() {
        EasyMock.expect(persistenceStructureService.isPersistable(Account.class)).andReturn(true);
        EasyMock.expect(dataDictionaryService.getDataDictionary()).andReturn(dataDictionary);
        EasyMock.expect(dataDictionary.getBusinessObjectEntry("org.kuali.kfs.coa.businessobject.Account")).andReturn(businessObjectEntry);
        EasyMock.expect(businessObjectEntry.getObjectLabel()).andReturn("Account");
        EasyMock.expect(persistenceStructureService.getTableName(Account.class)).andReturn("CA_ACCOUNT_T");
        initializedExpectationsForAccount();
    }

    private void initializeExpectationsForVendorTable() {
        EasyMock.expect(persistenceStructureService.isPersistable(VendorDetail.class)).andReturn(true);
        EasyMock.expect(dataDictionaryService.getDataDictionary()).andReturn(dataDictionary);
        EasyMock.expect(dataDictionary.getBusinessObjectEntry("org.kuali.kfs.vnd.businessobject.VendorDetail")).andReturn(businessObjectEntry);
        EasyMock.expect(businessObjectEntry.getObjectLabel()).andReturn("Vendor");
        EasyMock.expect(persistenceStructureService.getTableName(Account.class)).andReturn("PUR_VNDR_DTL_T");
        initializedExpectationsForVendor();
    }

    private void assertAgainstAccountTableDTO(TableDTO tableDTO) {
        Assert.assertNotNull("The TableDTO should not be null", tableDTO);
        Assert.assertEquals("The TableDTO code should be \"CA_ACCOUNT_T\"", "CA_ACCOUNT_T", tableDTO.getCode());
        Assert.assertEquals("The TableDTO name should be \"Account\"", "Account", tableDTO.getName());
        Assert.assertNull("The TableDTO description should be null", tableDTO.getDescription());
        assertAgainstFieldDTOs(tableDTO.getFields());
    }

    private MaintenanceDocumentEntry buildAccountMaintenanceDocumentEntryFixture() {
        MaintenanceDocumentEntry documentEntry = new MaintenanceDocumentEntry();
        documentEntry.setDataObjectClass(Account.class);
        documentEntry.setDocumentTypeName("ACCT");
        return documentEntry;
    }

    private MaintenanceDocumentEntry buildVendorMaintenanceDocumentEntryFixture() {
        MaintenanceDocumentEntry documentEntry = new MaintenanceDocumentEntry();
        documentEntry.setDataObjectClass(VendorDetail.class);
        documentEntry.setDocumentTypeName("PVEN");

        MaintainableSectionDefinition addressSection = buildVendorAddressSection();
        MaintainableSectionDefinition fieldsSection = buildVendorFieldsSection();
        List<MaintainableSectionDefinition> maintainableSectionDefinitions = Arrays.asList(addressSection, fieldsSection);
        documentEntry.setMaintainableSections(maintainableSectionDefinitions);

        return documentEntry;
    }

    private MaintainableSectionDefinition buildVendorFieldsSection() {
        MaintainableSectionDefinition fieldsSection = new MaintainableSectionDefinition();

        List<MaintainableItemDefinition> fields = new ArrayList<>();
        MaintainableItemDefinition vendorTaxTypeCode = new MaintainableFieldDefinition();
        vendorTaxTypeCode.setName("vendorHeader.vendorTaxTypeCode");
        fields.add(vendorTaxTypeCode);

        MaintainableItemDefinition vendorOwnershipCode = new MaintainableFieldDefinition();
        vendorOwnershipCode.setName("vendorHeader.vendorOwnershipCode");
        fields.add(vendorOwnershipCode);

        fieldsSection.setMaintainableItems(fields);
        fieldsSection.setTitle("VendorFields");

        return fieldsSection;
    }

    private MaintenanceDocumentEntry buildVendorWithAccountsMaintenanceDocumentEntryFixture() {
        MaintenanceDocumentEntry documentEntry = buildVendorMaintenanceDocumentEntryFixture();
        List<MaintainableItemDefinition> items = documentEntry.getMaintainableSections().get(0).getMaintainableItems();

        MaintainableCollectionDefinition accountItem = new MaintainableCollectionDefinition();
        accountItem.setBusinessObjectClass(Account.class);
        accountItem.setName("AccountCollection");
        items.add(accountItem);

        MaintainableCollectionDefinition vendorAddress = (MaintainableCollectionDefinition)items.get(0);
        List<MaintainableCollectionDefinition> vendorAddressCollections = vendorAddress.getMaintainableCollections();
        List<MaintainableCollectionDefinition> newCollections = new ArrayList<>();
        newCollections.addAll(vendorAddressCollections);
        newCollections.add(accountItem);
        vendorAddress.setMaintainableCollections(newCollections);

        return documentEntry;
    }

    private MaintainableSectionDefinition buildVendorAddressSection() {
        MaintainableSectionDefinition vendorAddressSection = new MaintainableSectionDefinition();
        vendorAddressSection.setTitle("VendorAddressSection");

        MaintainableCollectionDefinition vendorAddressCollection = new MaintainableCollectionDefinition();
        vendorAddressCollection.setBusinessObjectClass(VendorAddress.class);
        List<MaintainableFieldDefinition> vendorAddressFields = new ArrayList<>();
        MaintainableFieldDefinition vendorCityName = new MaintainableFieldDefinition();
        vendorCityName.setName("vendorCityName");
        vendorAddressFields.add(vendorCityName);
        MaintainableFieldDefinition vendorStateCode = new MaintainableFieldDefinition();
        vendorStateCode.setName("vendorStateCode");
        vendorAddressFields.add(vendorStateCode);
        MaintainableFieldDefinition vendorZipCode = new MaintainableFieldDefinition();
        vendorZipCode.setName("vendorZipCode");
        vendorAddressFields.add(vendorZipCode);
        vendorAddressCollection.setMaintainableFields(vendorAddressFields);
        vendorAddressCollection.setName("VendorAddressCollection");

        vendorAddressCollection.setMaintainableCollections(Arrays.asList(buildVendorDefaultAddressMaintainableCollectionDefintion()));

        List<MaintainableItemDefinition> itemDefinitions = new ArrayList<>();
        itemDefinitions.add(vendorAddressCollection);

        vendorAddressSection.setMaintainableItems(itemDefinitions);

        return vendorAddressSection;
    }

    private MaintainableCollectionDefinition buildVendorDefaultAddressMaintainableCollectionDefintion() {
        MaintainableCollectionDefinition defaultAddressCollection = new MaintainableCollectionDefinition();

        defaultAddressCollection.setBusinessObjectClass(VendorDefaultAddress.class);
        List<MaintainableFieldDefinition> vendorAddressFields = new ArrayList<>();
        MaintainableFieldDefinition vendorCampusCode = new MaintainableFieldDefinition();
        vendorCampusCode.setName("vendorCampusCode");
        vendorAddressFields.add(vendorCampusCode);
        MaintainableFieldDefinition vendorAddressGeneratedIdentifier = new MaintainableFieldDefinition();
        vendorAddressGeneratedIdentifier.setName("vendorAddressGeneratedIdentifier");
        vendorAddressFields.add(vendorAddressGeneratedIdentifier);
        defaultAddressCollection.setMaintainableFields(vendorAddressFields);

        return defaultAddressCollection;
    }

    private MaintenanceDocumentEntry buildOrganizationReviewRoleMaintenanceDocumentEntryFixture() {
        MaintenanceDocumentEntry documentEntry = new MaintenanceDocumentEntry();
        documentEntry.setDataObjectClass(OrganizationType.class);
        documentEntry.setDocumentTypeName("ORR");
        return documentEntry;
    }

    private MaintenanceDocumentEntry buildAssetFabricationMaintenanceDocumentEntryFixture() {
        MaintenanceDocumentEntry documentEntry = new MaintenanceDocumentEntry();
        documentEntry.setDataObjectClass(OrganizationType.class); // fake, but we need something in there so the test works
        documentEntry.setDocumentTypeName("FR");
        return documentEntry;
    }

    private TransactionalDocumentEntry buildFakeTransactionalDocumentEntryFixture() {
        TransactionalDocumentEntry transactionalDocumentEntry = new TransactionalDocumentEntry();
        transactionalDocumentEntry.setDocumentTypeName("FollowYourArrow");
        return transactionalDocumentEntry;
    }

    private MaintenanceDocumentEntry buildAccountGlobalDocumentEntryFixture() {
        MaintenanceDocumentEntry documentEntry = new MaintenanceDocumentEntry();
        documentEntry.setDataObjectClass(AccountGlobal.class);
        documentEntry.setDocumentTypeName("GACC");
        return documentEntry;
    }

    private void assertAgainstTableDTOs(List<TableDTO> tableDTOs) {
        Assert.assertNotNull("We should have gotten back a list of TableDTOs", tableDTOs);
        Assert.assertEquals("The size of the list should be 1", 1, tableDTOs.size());
        assertAgainstAccountTableDTO(tableDTOs.get(0));
    }
}