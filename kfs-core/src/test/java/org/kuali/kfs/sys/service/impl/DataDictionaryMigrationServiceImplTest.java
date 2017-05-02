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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountGlobal;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.OrganizationType;
import org.kuali.kfs.fp.document.TransferOfFundsDocument;
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
import org.kuali.kfs.sys.batch.DataDictionaryFilteredEntity;
import org.kuali.kfs.sys.batch.DataDictionaryFilteredField;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.businessobject.dto.ConcernDTO;
import org.kuali.kfs.sys.businessobject.dto.EntityDTO;
import org.kuali.kfs.sys.businessobject.dto.FieldDTO;
import org.kuali.kfs.sys.businessobject.dto.TableDTO;
import org.kuali.kfs.vnd.businessobject.AddressType;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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

    @Before
    public void setup() {
        EasyMock.expect(dataDictionaryService.getDataDictionary()).andReturn(dataDictionary).anyTimes();
    }

    @Test
    public void testRetrieveAllMaintenanceDocumentEntries() throws Exception {
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
        dataDictionaryMigrationService.setFilteredEntities(Arrays.asList(new DataDictionaryFilteredEntity("FR"), new DataDictionaryFilteredEntity("ORR")));

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
        EasyMock.expect(dataDictionary.getBusinessObjectEntry("org.kuali.kfs.coa.businessobject.Account")).andReturn(businessObjectEntry);
        initializeExpectationsForAccountWithFields();
        setupFilteredFields();

        EasyMock.replay(dataDictionaryService, dataDictionary, businessObjectEntry, persistenceStructureService, kualiModuleService, moduleService);

        Set<Class<? extends PersistableBusinessObject>> businessObjectsOwnedByEntities = new HashSet<>();
        businessObjectsOwnedByEntities.add(Account.class);
        final EntityDTO entityDTO = dataDictionaryMigrationService.convertMaintenanceDocumentToEntityDTO(maintenanceDocumentEntry, new HashSet<>(), businessObjectsOwnedByEntities);

        EasyMock.verify(dataDictionaryService, dataDictionary, businessObjectEntry, persistenceStructureService, kualiModuleService, moduleService);

        Assert.assertNotNull("We should have gotten back an EntityDTO", entityDTO);
        Assert.assertEquals("The module namespace should be \"LUSH-LIT UP\" as that's what I was listening to at the time", "LUSH-LIT UP", entityDTO.getModuleCode());
        Assert.assertEquals("The code for the Entity should be \"ACCT\"", "ACCT", entityDTO.getCode());
        Assert.assertEquals("The name for the Entity should be \"Account\"", "Account", entityDTO.getName());
        Assert.assertNull("The description for the Entity should be null", entityDTO.getDescription());
        assertAgainstRootTableDTO(entityDTO.getRootTable());
    }

    @Test
    public void testConvertMaintenanceDocumentToEntityDTO_Duplicate() throws Exception {
        MaintenanceDocumentEntry maintenanceDocumentEntry = buildAccountMaintenanceDocumentEntryFixture();

        EasyMock.replay(dataDictionaryService, dataDictionary, businessObjectEntry, persistenceStructureService, kualiModuleService, moduleService);

        Set<Class<? extends PersistableBusinessObject>> businessObjectsOwnedByEntities = new HashSet<>();
        businessObjectsOwnedByEntities.add(Account.class);

        Set accountDup = new HashSet<>();
        accountDup.add("ACCT");

        final EntityDTO entityDTO = dataDictionaryMigrationService.convertMaintenanceDocumentToEntityDTO(maintenanceDocumentEntry, accountDup, businessObjectsOwnedByEntities);

        EasyMock.verify(dataDictionaryService, dataDictionary, businessObjectEntry, persistenceStructureService, kualiModuleService, moduleService);

        Assert.assertNull("We should not have gotten back an EntityDTO", entityDTO);
    }

    protected void setupFilteredFields() {
        dataDictionaryMigrationService.setFilteredFields(Arrays.asList(new DataDictionaryFilteredField("*.objectId"),
                new DataDictionaryFilteredField("*.versionNumber"),
                new DataDictionaryFilteredField("*.lastUpdatedTimestamp"),
                new DataDictionaryFilteredField("Account.accountName")));
    }

    @Test
    public void testBuildTableDTO() {
        initializeExpectationsForAccountWithFields();
        setupFilteredFields();

        EasyMock.replay(dataDictionaryService, dataDictionary, businessObjectEntry, persistenceStructureService);

        final TableDTO tableDTO = dataDictionaryMigrationService.buildTableDTO(Account.class, false);

        EasyMock.verify(dataDictionaryService, dataDictionary, businessObjectEntry, persistenceStructureService);
        assertAgainstAccountTableDTO(tableDTO);
    }

    @Test
    public void testBuildTableDTO_NotCollection() {
        initializeExpectationsForAccountWithFields();
        setupFilteredFields();

        EasyMock.replay(dataDictionaryService, dataDictionary, businessObjectEntry, persistenceStructureService);

        final TableDTO tableDTO = dataDictionaryMigrationService.buildTableDTO(Account.class, false);

        EasyMock.verify(dataDictionaryService, dataDictionary, businessObjectEntry, persistenceStructureService);
        assertAgainstAccountTableDTO(tableDTO);
        Assert.assertFalse("The TableDTO should not be a collection", tableDTO.isCollection());
    }
    @Test
    public void testBuildTableDTO_collection() {
        initializeExpectationsForAccountWithFields();
        setupFilteredFields();

        EasyMock.replay(dataDictionaryService, dataDictionary, businessObjectEntry, persistenceStructureService);

        final TableDTO tableDTO = dataDictionaryMigrationService.buildTableDTO(Account.class, true);

        EasyMock.verify(dataDictionaryService, dataDictionary, businessObjectEntry, persistenceStructureService);
        assertAgainstAccountTableDTO(tableDTO);
        Assert.assertTrue("The TableDTO is a collection", tableDTO.isCollection());
    }

    @Test
    public void testBuildTableDTO_NonPersistable() {
        EasyMock.expect(persistenceStructureService.isPersistable(Bank.class)).andReturn(false);
        EasyMock.replay(persistenceStructureService);

        final TableDTO tableDTO = dataDictionaryMigrationService.buildTableDTO(Bank.class, false);

        EasyMock.verify(persistenceStructureService);
        Assert.assertNull("The TableDTO should be null", tableDTO);
    }

    @Test
    public void testBuildFieldDTOs() {
        initializeExpectationsForAccountWithFields();
        setupFilteredFields();

        EasyMock.replay(dataDictionaryService, dataDictionary, businessObjectEntry, persistenceStructureService);

        final List<FieldDTO> fieldDTOs = dataDictionaryMigrationService.buildFieldDTOs(Account.class);

        EasyMock.verify(dataDictionaryService, dataDictionary, businessObjectEntry, persistenceStructureService);
        assertAgainstFieldDTOs(fieldDTOs);
    }

    @Test
    public void testBuildFieldDTO() {
        final AttributeDefinition chartOfAccountsAttributeDefinition = buildChartOfAccountsCodeAttributeDefintion();
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
        EasyMock.expect(dataDictionary.getBusinessObjectEntry("org.kuali.kfs.coa.businessobject.Chart")).andReturn(null);

        EasyMock.replay(dataDictionaryService, dataDictionary, businessObjectEntry);

        final AttributeDefinition returnedAttributeDefinition = dataDictionaryMigrationService.retrieveAttribute(Chart.class, "scarlet");
        EasyMock.verify(dataDictionaryService, dataDictionary, businessObjectEntry);
        Assert.assertNull("We should have not gotten back an attribute definition", returnedAttributeDefinition);
    }

    @Test
    public void testPopulateEntityDTO_Collection() {
        MaintenanceDocumentEntry documentEntry = buildVendorMaintenanceDocumentEntryFixture();
        initializeExpectationsForVendor();

        EasyMock.replay(persistenceStructureService, dataDictionary, dataDictionaryService, businessObjectEntry);

        Set<Class<? extends PersistableBusinessObject>> businessObjectsOwnedByEntities = new HashSet<>();

        EntityDTO entityDTO = new EntityDTO();
        TableDTO rootTable = dataDictionaryMigrationService.buildTableDTO(VendorDetail.class, false);
        entityDTO.setRootTable(rootTable);
        dataDictionaryMigrationService.populateEntityDTO(documentEntry, entityDTO, businessObjectsOwnedByEntities);

        EasyMock.verify(persistenceStructureService, dataDictionary, dataDictionaryService, businessObjectEntry);

        TableDTO resultRootTable = entityDTO.getRootTable();
        List<TableDTO> subTables = resultRootTable.getTables();
        Assert.assertNotNull(subTables);
        Assert.assertEquals(1, subTables.size());
        assertIncludedTable(subTables, "PUR_VNDR_ADDR_T");
        List<TableDTO> subSubTables = subTables.get(0).getTables();
        assertIncludedTable(subSubTables, "PUR_VNDR_DFLT_ADDR_T");
    }

    @Test
    public void testPopulateEntityDTO_ClassAlreadyTaken() {
        MaintenanceDocumentEntry documentEntry = buildVendorWithAccountsMaintenanceDocumentEntryFixture();
        MaintenanceDocumentEntry accountEntry = buildAccountMaintenanceDocumentEntryFixture();
        initializeExpectationsForVendor();
        initializeExpectationsFor(Account.class, "CA_ACCOUNT_T");

        EasyMock.replay(persistenceStructureService, dataDictionary, dataDictionaryService, businessObjectEntry);

        Set<Class<? extends PersistableBusinessObject>> businessObjectsOwnedByEntities = new HashSet<>();

        EntityDTO accountEntityDTO = new EntityDTO();
        TableDTO acctRootTable = dataDictionaryMigrationService.buildTableDTO(Account.class, false);
        accountEntityDTO.setRootTable(acctRootTable);
        dataDictionaryMigrationService.populateEntityDTO(accountEntry, accountEntityDTO, businessObjectsOwnedByEntities);

        EntityDTO entityDTO = new EntityDTO();
        TableDTO rootTable = dataDictionaryMigrationService.buildTableDTO(VendorDetail.class, false);
        entityDTO.setRootTable(rootTable);
        dataDictionaryMigrationService.populateEntityDTO(documentEntry, entityDTO, businessObjectsOwnedByEntities);

        EasyMock.verify(persistenceStructureService, dataDictionary, dataDictionaryService, businessObjectEntry);

        TableDTO resultRootTable = entityDTO.getRootTable();
        List<TableDTO> subTables = resultRootTable.getTables();
        Assert.assertNotNull(subTables);
        Assert.assertEquals(1, subTables.size());
        assertIncludedTable(subTables, "PUR_VNDR_ADDR_T");
        List<TableDTO> subSubTables = subTables.get(0).getTables();
        assertIncludedTable(subSubTables, "PUR_VNDR_DFLT_ADDR_T");

        Assert.assertEquals(4, businessObjectsOwnedByEntities.size());
    }

    protected void assertIncludedTable(List<TableDTO> tables, String tableCode) {
        Assert.assertTrue(tables.stream().anyMatch(tableDTO -> StringUtils.equals(tableDTO.getCode(), tableCode)));
    }

    protected void assertIncludedConcern(Set<ConcernDTO> concerns, String tableCode, boolean isCollection) {
        Assert.assertTrue(concerns.stream().anyMatch(concernDTO -> StringUtils.equals(concernDTO.getCode(), tableCode) && concernDTO.getTable().isCollection() == isCollection));
    }

    @Test
    public void testAssignUnclaimedAttributeClasses() {
        Set<Class<? extends PersistableBusinessObject>> businessObjectsOwnedByEntities = new HashSet<>();
        String documentTypeName = "PVEN";
        Class<? extends BusinessObject> businessObjectClass = VendorDetail.class;
        MaintenanceDocumentEntry vendorEntry = buildVendorMaintenanceDocumentEntryFixture();
        List<? extends MaintainableItemDefinition> maintainableItems = vendorEntry.getMaintainableSections().get(1).getMaintainableItems();

        initializeExpectationsFor(VendorHeader.class, "PUR_VNDR_HDR_T");

        EasyMock.replay(persistenceStructureService, dataDictionaryService, dataDictionary);

        TableDTO tableDTO = new TableDTO();
        dataDictionaryMigrationService.assignUnclaimedAttributeClasses(tableDTO, businessObjectsOwnedByEntities, documentTypeName, businessObjectClass, maintainableItems);

        EasyMock.verify(persistenceStructureService, dataDictionaryService, dataDictionary);

        List<TableDTO> subTables = tableDTO.getTables();
        Assert.assertEquals(1, subTables.size());
        Assert.assertEquals("PUR_VNDR_HDR_T", subTables.get(0).getCode());
        Assert.assertTrue(businessObjectsOwnedByEntities.contains(VendorHeader.class));
    }

    @Test
    public void testAssignUnclaimedAttributeClasses_AlreadyClaimed() {
        Set<Class<? extends PersistableBusinessObject>> businessObjectsOwnedByEntities = new HashSet<>();
        businessObjectsOwnedByEntities.add(VendorHeader.class);
        String documentTypeName = "PVEN";
        Class<? extends BusinessObject> businessObjectClass = VendorDetail.class;
        MaintenanceDocumentEntry vendorEntry = buildVendorMaintenanceDocumentEntryFixture();
        List<? extends MaintainableItemDefinition> maintainableItems = vendorEntry.getMaintainableSections().get(1).getMaintainableItems();

        TableDTO tableDTO = new TableDTO();
        dataDictionaryMigrationService.assignUnclaimedAttributeClasses(tableDTO, businessObjectsOwnedByEntities, documentTypeName, businessObjectClass, maintainableItems);

        Assert.assertNull(tableDTO.getTables());
    }

    @Test
    public void testAssignUnclaimedAttributeClasses_UnconditionallyReadonly() {
        Set<Class<? extends PersistableBusinessObject>> businessObjectsOwnedByEntities = new HashSet<>();
        String documentTypeName = "PVEN";
        Class<? extends BusinessObject> businessObjectClass = VendorDetail.class;
        List<MaintainableItemDefinition> fields = new ArrayList<>();
        MaintainableFieldDefinition vendorTaxTypeCode = new MaintainableFieldDefinition();
        vendorTaxTypeCode.setName("vendorHeader.vendorTaxTypeCode");
        vendorTaxTypeCode.setUnconditionallyReadOnly(true);
        fields.add(vendorTaxTypeCode);

        TableDTO tableDTO = new TableDTO();
        dataDictionaryMigrationService.assignUnclaimedAttributeClasses(tableDTO, businessObjectsOwnedByEntities, documentTypeName, businessObjectClass, fields);

        Assert.assertNull(tableDTO.getTables());
        Assert.assertTrue(CollectionUtils.isEmpty(businessObjectsOwnedByEntities));
    }

    @Test
    public void testAssignUnclaimedAttributeClasses_UnknownAttributeKey() {
        Set<Class<? extends PersistableBusinessObject>> businessObjectsOwnedByEntities = new HashSet<>();
        String documentTypeName = "PVEN";
        Class<? extends BusinessObject> businessObjectClass = VendorDetail.class;
        List<MaintainableItemDefinition> fields = new ArrayList<>();
        MaintainableFieldDefinition vendorTaxTypeCode = new MaintainableFieldDefinition();
        vendorTaxTypeCode.setName("garbage.vendorTaxTypeCode");
        fields.add(vendorTaxTypeCode);

        TableDTO tableDTO = new TableDTO();
        dataDictionaryMigrationService.assignUnclaimedAttributeClasses(tableDTO, businessObjectsOwnedByEntities, documentTypeName, businessObjectClass, fields);

        Assert.assertNull(tableDTO.getTables());
        Assert.assertTrue(CollectionUtils.isEmpty(businessObjectsOwnedByEntities));
    }

    @Test
    public void testPopulateEntityDTOWithNestedBOs() {
        MaintenanceDocumentEntry documentEntry = buildVendorWithAccountsMaintenanceDocumentEntryFixture();
        Set<Class<? extends PersistableBusinessObject>> businessObjectsOwnedByEntities = new HashSet<>();

        initializeExpectationsForVendor();
        initializeExpectationsFor(VendorHeader.class, "PUR_VNDR_HDR_T");
        initializeExpectationsFor(Account.class, "CA_ACCOUNT_T");

        EasyMock.replay(persistenceStructureService, dataDictionary, dataDictionaryService, businessObjectEntry);

        EntityDTO entityDTO = new EntityDTO();
        TableDTO rootTable = dataDictionaryMigrationService.buildTableDTO(VendorDetail.class, false);
        entityDTO.setRootTable(rootTable);
        dataDictionaryMigrationService.populateEntityDTO(documentEntry, entityDTO, businessObjectsOwnedByEntities);
        dataDictionaryMigrationService.populateEntityDTOWithNestedBOs(documentEntry, entityDTO, businessObjectsOwnedByEntities);

        EasyMock.verify(persistenceStructureService, dataDictionary, dataDictionaryService, businessObjectEntry);

        TableDTO resultRootTable = entityDTO.getRootTable();
        List<TableDTO> subTables = resultRootTable.getTables();
        Assert.assertNotNull(subTables);
        Assert.assertEquals(2, subTables.size());
        assertIncludedTable(subTables, "PUR_VNDR_ADDR_T");
        assertIncludedTable(subTables, "PUR_VNDR_HDR_T");
        List<TableDTO> subSubTables = subTables.get(0).getTables();
        assertIncludedTable(subSubTables, "PUR_VNDR_DFLT_ADDR_T");

        Assert.assertEquals(5, businessObjectsOwnedByEntities.size());
        assertTableCollectionness(subTables, "PUR_VNDR_ADDR_T", true);
        assertTableCollectionness(retrieveTableByCode(subTables, "PUR_VNDR_ADDR_T").get().getTables(), "PUR_VNDR_DFLT_ADDR_T", true);
        assertTableCollectionness(subTables, "PUR_VNDR_HDR_T", false);
    }

    @Test
    public void testPopulateEntityDTOWithNestedBOs_NestedFieldsInCollection() {
        Set<Class<? extends PersistableBusinessObject>> businessObjectsOwnedByEntities = new HashSet<>();

        MaintenanceDocumentEntry documentEntry = buildVendorWithAccountsMaintenanceDocumentEntryFixture();
        MaintainableSectionDefinition collectionSection = documentEntry.getMaintainableSections().get(0);
        MaintainableCollectionDefinition collection = (MaintainableCollectionDefinition)collectionSection.getMaintainableItems().get(0);
        List<MaintainableFieldDefinition> collectionFields = collection.getMaintainableFields();
        MaintainableFieldDefinition nestedField = new MaintainableFieldDefinition();
        nestedField.setName("vendorAddressType.itreallydoesnotmatter");
        collectionFields.add(nestedField);

        initializeExpectationsForVendor();
        initializeExpectationsFor(VendorHeader.class, "PUR_VNDR_HDR_T");
        initializeExpectationsFor(Account.class, "CA_ACCOUNT_T");
        initializeExpectationsFor(AddressType.class, "PUR_ADDR_TYP_T");

        EasyMock.replay(persistenceStructureService, dataDictionary, dataDictionaryService, businessObjectEntry);

        EntityDTO entityDTO = new EntityDTO();
        TableDTO rootTable = dataDictionaryMigrationService.buildTableDTO(VendorDetail.class, false);
        entityDTO.setRootTable(rootTable);
        dataDictionaryMigrationService.populateEntityDTO(documentEntry, entityDTO, businessObjectsOwnedByEntities);
        dataDictionaryMigrationService.populateEntityDTOWithNestedBOs(documentEntry, entityDTO, businessObjectsOwnedByEntities);

        EasyMock.verify(persistenceStructureService, dataDictionary, dataDictionaryService, businessObjectEntry);

        TableDTO resultRootTable = entityDTO.getRootTable();
        List<TableDTO> subTables = resultRootTable.getTables();
        Assert.assertNotNull(subTables);
        List<TableDTO> subSubTables = subTables.get(0).getTables();
        Assert.assertEquals(3, subSubTables.size());
        assertIncludedTable(subSubTables, "PUR_VNDR_DFLT_ADDR_T");
        assertIncludedTable(subSubTables, "PUR_ADDR_TYP_T");
        assertIncludedTable(subSubTables, "CA_ACCOUNT_T");

        Assert.assertEquals(6, businessObjectsOwnedByEntities.size());
        Assert.assertTrue(businessObjectsOwnedByEntities.contains(AddressType.class));
    }

    @Test
    public void testGatherTransacationalEntities() {
        TransactionalDocumentEntry documentEntry = buildTransferOfFundsDocumentEntryFixture();
        EasyMock.expect(persistenceStructureService.getTableName(TransferOfFundsDocument.class)).andReturn("FP_FND_TRNFR_DOC_T");
        EasyMock.expect(dataDictionary.getDocumentEntry("TF")).andReturn(documentEntry);
        EasyMock.expect(dataDictionary.getBusinessObjectEntry(AccountingPeriod.class.getName())).andReturn(buildBusinessObjectEntryFixture(AccountingPeriod.class));
        EasyMock.expect(dataDictionary.getBusinessObjectEntry(FinancialSystemDocumentHeader.class.getName())).andReturn(buildBusinessObjectEntryFixture(FinancialSystemDocumentHeader.class));
        EasyMock.expect(dataDictionary.getBusinessObjectEntry(TargetAccountingLine.class.getName())).andReturn(buildBusinessObjectEntryFixture(TargetAccountingLine.class));
        Map<String, Class> referenceObjectFields = new HashMap<>();
        referenceObjectFields.put("documentHeader", FinancialSystemDocumentHeader.class);
        referenceObjectFields.put("accoutingPeriod", AccountingPeriod.class);
        EasyMock.expect(persistenceStructureService.listReferenceObjectFields(documentEntry.getDocumentClass())).andReturn(referenceObjectFields);
        Map<String, Class> collectionObjectFields = new HashMap<>();
        collectionObjectFields.put("targetAccountingLines", TargetAccountingLine.class);
        EasyMock.expect(persistenceStructureService.listCollectionObjectTypes(documentEntry.getDocumentClass())).andReturn(collectionObjectFields);

        EasyMock.expect(persistenceStructureService.listFieldNames(EasyMock.anyObject())).andReturn(new ArrayList()).anyTimes();
        EasyMock.expect(persistenceStructureService.getTableName(AccountingPeriod.class)).andReturn("SH_ACCT_PERIOD_T").anyTimes();
        EasyMock.expect(persistenceStructureService.getTableName(FinancialSystemDocumentHeader.class)).andReturn("FS_DOC_HEADER_T").anyTimes();
        EasyMock.expect(persistenceStructureService.getTableName(TargetAccountingLine.class)).andReturn("FP_ACCT_LINES_T").anyTimes();

        EasyMock.expect(persistenceStructureService.isPersistable(EasyMock.anyObject())).andReturn(true).anyTimes();

        dataDictionaryMigrationService.setConcerns(Arrays.asList("FP_ACCT_LINES_T", "GL_PENDING_ENTRY_T", "FS_DOC_HEADER_T"));

        Map<String, ConcernDTO> takenTables = new HashMap();
        Set<Class<? extends PersistableBusinessObject>> businessObjectsOwnedByEntities = new HashSet<>();

        EasyMock.replay(dataDictionary, dataDictionaryService, persistenceStructureService);

        EntityDTO entity = dataDictionaryMigrationService.gatherTransactionalEntities("TF", "Transfer of Funds", "KFS-FP", businessObjectsOwnedByEntities, takenTables);

        EasyMock.verify(dataDictionary, dataDictionaryService, persistenceStructureService);

        Assert.assertEquals("TF", entity.getCode());
        Assert.assertEquals("Transfer of Funds", entity.getName());
        Assert.assertEquals("KFS-FP", entity.getModuleCode());
        TableDTO rootTable = entity.getRootTable();
        Assert.assertEquals("FP_FND_TRNFR_DOC_T", rootTable.getCode());

        List<TableDTO> subTables = rootTable.getTables();
        Assert.assertEquals(1, subTables.size());
        assertIncludedTable(subTables, "SH_ACCT_PERIOD_T");
        Assert.assertFalse(subTables.get(0).isCollection());

        Set<ConcernDTO> concerns = entity.getConcerns();
        Assert.assertEquals(2, concerns.size());
        assertIncludedConcern(concerns, "FS_DOC_HEADER_T", false);
        assertIncludedConcern(concerns, "FP_ACCT_LINES_T", true);
        Assert.assertEquals(2, takenTables.size());
        Assert.assertTrue(takenTables.containsKey("FP_ACCT_LINES_T"));
        Assert.assertTrue(takenTables.containsKey("FS_DOC_HEADER_T"));
    }

    @Test
    public void testGatherTransacationalEntities_ConcernAlreadyTaken() {
        TransactionalDocumentEntry documentEntry = buildTransferOfFundsDocumentEntryFixture();
        EasyMock.expect(persistenceStructureService.getTableName(TransferOfFundsDocument.class)).andReturn("FP_FND_TRNFR_DOC_T");
        EasyMock.expect(dataDictionary.getDocumentEntry("TF")).andReturn(documentEntry);
        Map<String, Class> referenceObjectFields = new HashMap<>();
        referenceObjectFields.put("documentHeader", FinancialSystemDocumentHeader.class);
        EasyMock.expect(persistenceStructureService.listReferenceObjectFields(documentEntry.getDocumentClass())).andReturn(referenceObjectFields);
        Map<String, Class> collectionObjectFields = new HashMap<>();
        EasyMock.expect(persistenceStructureService.listCollectionObjectTypes(documentEntry.getDocumentClass())).andReturn(collectionObjectFields);

        EasyMock.expect(persistenceStructureService.listFieldNames(EasyMock.anyObject())).andReturn(new ArrayList()).anyTimes();
        EasyMock.expect(persistenceStructureService.getTableName(FinancialSystemDocumentHeader.class)).andReturn("FS_DOC_HEADER_T").anyTimes();

        EasyMock.expect(persistenceStructureService.isPersistable(EasyMock.anyObject())).andReturn(true).anyTimes();

        dataDictionaryMigrationService.setConcerns(Arrays.asList("FS_DOC_HEADER_T"));

        Map<String, ConcernDTO> takenTables = new HashMap();
        ConcernDTO concernDTO = new ConcernDTO();
        concernDTO.setCode("TAKE_TABLE_CONCERN");
        TableDTO concernTable = new TableDTO();
        concernTable.setCollection(false);
        concernDTO.setTable(concernTable);
        takenTables.put("FS_DOC_HEADER_T", concernDTO);
        Set<Class<? extends PersistableBusinessObject>> businessObjectsOwnedByEntities = new HashSet<>();

        EasyMock.replay(dataDictionary, dataDictionaryService, persistenceStructureService);

        EntityDTO entity = dataDictionaryMigrationService.gatherTransactionalEntities("TF", "Transfer of Funds", "KFS-FP", businessObjectsOwnedByEntities, takenTables);

        EasyMock.verify(dataDictionary, dataDictionaryService, persistenceStructureService);

        Set<ConcernDTO> concerns = entity.getConcerns();
        Assert.assertEquals(1, concerns.size());
        assertIncludedConcern(concerns, "TAKE_TABLE_CONCERN", false);
        Assert.assertEquals(1, takenTables.size());
        Assert.assertTrue(takenTables.containsKey("FS_DOC_HEADER_T"));
    }

    @Test
    public void testGatherTransacationalEntities_CollectionConcernAlreadyTaken() {
        TransactionalDocumentEntry documentEntry = buildTransferOfFundsDocumentEntryFixture();
        EasyMock.expect(persistenceStructureService.getTableName(TransferOfFundsDocument.class)).andReturn("FP_FND_TRNFR_DOC_T");
        EasyMock.expect(dataDictionary.getDocumentEntry("TF")).andReturn(documentEntry);
        Map<String, Class> referenceObjectFields = new HashMap<>();
        EasyMock.expect(persistenceStructureService.listReferenceObjectFields(documentEntry.getDocumentClass())).andReturn(referenceObjectFields);
        Map<String, Class> collectionObjectFields = new HashMap<>();
        collectionObjectFields.put("documentHeader", FinancialSystemDocumentHeader.class);
        EasyMock.expect(persistenceStructureService.listCollectionObjectTypes(documentEntry.getDocumentClass())).andReturn(collectionObjectFields);

        EasyMock.expect(persistenceStructureService.listFieldNames(EasyMock.anyObject())).andReturn(new ArrayList()).anyTimes();
        EasyMock.expect(persistenceStructureService.getTableName(FinancialSystemDocumentHeader.class)).andReturn("FS_DOC_HEADER_T").anyTimes();

        EasyMock.expect(persistenceStructureService.isPersistable(EasyMock.anyObject())).andReturn(true).anyTimes();

        dataDictionaryMigrationService.setConcerns(Arrays.asList("FS_DOC_HEADER_T"));

        Map<String, ConcernDTO> takenTables = new HashMap();
        ConcernDTO concernDTO = new ConcernDTO();
        concernDTO.setCode("TAKE_TABLE_CONCERN");
        TableDTO concernTable = new TableDTO();
        concernTable.setCollection(true);
        concernDTO.setTable(concernTable);
        takenTables.put("FS_DOC_HEADER_T", concernDTO);
        Set<Class<? extends PersistableBusinessObject>> businessObjectsOwnedByEntities = new HashSet<>();

        EasyMock.replay(dataDictionary, dataDictionaryService, persistenceStructureService);

        EntityDTO entity = dataDictionaryMigrationService.gatherTransactionalEntities("TF", "Transfer of Funds", "KFS-FP", businessObjectsOwnedByEntities, takenTables);

        EasyMock.verify(dataDictionary, dataDictionaryService, persistenceStructureService);

        Set<ConcernDTO> concerns = entity.getConcerns();
        Assert.assertEquals(1, concerns.size());
        assertIncludedConcern(concerns, "TAKE_TABLE_CONCERN", true);
        Assert.assertEquals(1, takenTables.size());
        Assert.assertTrue(takenTables.containsKey("FS_DOC_HEADER_T"));
    }

    @Test
    public void testGatherTransacationalEntities_RootSubTablesCollection() {
        TransactionalDocumentEntry documentEntry = buildTransferOfFundsDocumentEntryFixture();
        EasyMock.expect(persistenceStructureService.getTableName(TransferOfFundsDocument.class)).andReturn("FP_FND_TRNFR_DOC_T");
        EasyMock.expect(dataDictionary.getDocumentEntry("TF")).andReturn(documentEntry);
        EasyMock.expect(dataDictionary.getBusinessObjectEntry(AccountingPeriod.class.getName())).andReturn(buildBusinessObjectEntryFixture(AccountingPeriod.class));
        Map<String, Class> referenceObjectFields = new HashMap<>();
        EasyMock.expect(persistenceStructureService.listReferenceObjectFields(documentEntry.getDocumentClass())).andReturn(referenceObjectFields);
        Map<String, Class> collectionObjectFields = new HashMap<>();
        collectionObjectFields.put("accoutingPeriod", AccountingPeriod.class);
        EasyMock.expect(persistenceStructureService.listCollectionObjectTypes(documentEntry.getDocumentClass())).andReturn(collectionObjectFields);

        EasyMock.expect(persistenceStructureService.listFieldNames(EasyMock.anyObject())).andReturn(new ArrayList()).anyTimes();
        EasyMock.expect(persistenceStructureService.getTableName(AccountingPeriod.class)).andReturn("SH_ACCT_PERIOD_T").anyTimes();

        EasyMock.expect(persistenceStructureService.isPersistable(EasyMock.anyObject())).andReturn(true).anyTimes();

        dataDictionaryMigrationService.setConcerns(Arrays.asList("FP_ACCT_LINES_T", "GL_PENDING_ENTRY_T", "FS_DOC_HEADER_T"));

        Map<String, ConcernDTO> takenTables = new HashMap();
        Set<Class<? extends PersistableBusinessObject>> businessObjectsOwnedByEntities = new HashSet<>();

        EasyMock.replay(dataDictionary, dataDictionaryService, persistenceStructureService);

        EntityDTO entity = dataDictionaryMigrationService.gatherTransactionalEntities("TF", "Transfer of Funds", "KFS-FP", businessObjectsOwnedByEntities, takenTables);

        EasyMock.verify(dataDictionary, dataDictionaryService, persistenceStructureService);

        Assert.assertEquals("TF", entity.getCode());
        Assert.assertEquals("Transfer of Funds", entity.getName());
        Assert.assertEquals("KFS-FP", entity.getModuleCode());
        TableDTO rootTable = entity.getRootTable();
        Assert.assertEquals("FP_FND_TRNFR_DOC_T", rootTable.getCode());

        List<TableDTO> subTables = rootTable.getTables();
        Assert.assertEquals(1, subTables.size());
        assertIncludedTable(subTables, "SH_ACCT_PERIOD_T");
        Assert.assertTrue(subTables.get(0).isCollection());

        Set<ConcernDTO> concerns = entity.getConcerns();
        Assert.assertEquals(0, concerns.size());
    }

    @Test
    public void testBuildConcernDTO() {
        MaintenanceDocumentEntry documentEntry = buildAccountMaintenanceDocumentEntryFixture();
        EasyMock.expect(persistenceStructureService.isPersistable(Account.class)).andReturn(true).anyTimes();
        EasyMock.expect(dataDictionary.getBusinessObjectEntry("org.kuali.kfs.coa.businessobject.Account")).andReturn(businessObjectEntry);
        EasyMock.expect(businessObjectEntry.getObjectLabel()).andReturn("Account");
        EasyMock.expect(persistenceStructureService.getTableName(Account.class)).andReturn("CA_ACCOUNT_T");
        final AttributeDefinition chartOfAccountsAttributeDefinition = buildChartOfAccountsCodeAttributeDefintion();
        final AttributeDefinition accountNumberAttributeDefinition = buildAccountNumberAttributeDefintion();
        EasyMock.expect(persistenceStructureService.listFieldNames(Account.class)).andReturn(Arrays.asList("chartOfAccountsCode", "accountNumber", "goodToBeOnTheRoadBackHome"));
        EasyMock.expect(dataDictionary.getBusinessObjectEntry("org.kuali.kfs.coa.businessobject.Account")).andReturn(businessObjectEntry).anyTimes();
        EasyMock.expect(businessObjectEntry.getAttributeDefinition("chartOfAccountsCode")).andReturn(chartOfAccountsAttributeDefinition);
        EasyMock.expect(persistenceStructureService.getColumnNameForFieldName(Account.class, "chartOfAccountsCode")).andReturn("FIN_COA_CD");
        EasyMock.expect(businessObjectEntry.getAttributeDefinition("accountNumber")).andReturn(accountNumberAttributeDefinition);
        EasyMock.expect(persistenceStructureService.getColumnNameForFieldName(Account.class, "accountNumber")).andReturn("ACCOUNT_NBR");
        EasyMock.expect(businessObjectEntry.getAttributeDefinition("goodToBeOnTheRoadBackHome")).andReturn(null);

        EasyMock.replay(dataDictionaryService, dataDictionary, businessObjectEntry, persistenceStructureService);

        final ConcernDTO concernDTO = dataDictionaryMigrationService.buildConcernDTO((Class<? extends PersistableBusinessObject>)documentEntry.getDataObjectClass(), false);

        EasyMock.verify(dataDictionaryService, dataDictionary, businessObjectEntry, persistenceStructureService);
        Assert.assertEquals("Account", concernDTO.getName());
        Assert.assertEquals("CA_ACCOUNT_T", concernDTO.getCode());
        Assert.assertNull(concernDTO.getDescription());
        Assert.assertNotNull(concernDTO.getTable());
        assertAgainstAccountTableDTO(concernDTO.getTable());
    }

    private Optional<TableDTO> retrieveTableByCode(List<TableDTO> subTables, String code) {
        return subTables.stream()
            .filter(tableDTO -> StringUtils.equals(tableDTO.getCode(), code))
            .findFirst();
    }

    private void assertTableCollectionness(List<TableDTO> subTables, String code, boolean isCollection) {
        Optional<TableDTO> tableDTO = retrieveTableByCode(subTables, code);
        Assert.assertTrue(tableDTO.isPresent());
        Assert.assertEquals(isCollection, tableDTO.get().isCollection());
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

    private AttributeDefinition buildAccountNameAttributeDefinition() {
        AttributeDefinition accountNumberAccountDefinition = new AttributeDefinition();
        accountNumberAccountDefinition.setName("accountName");
        accountNumberAccountDefinition.setLabel("Account Name");
        accountNumberAccountDefinition.setShortLabel("Acct Nm");
        accountNumberAccountDefinition.setMaxLength(100);
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



    private void initializeExpectationsForAccountWithFields() {
        final AttributeDefinition chartOfAccountsAttributeDefinition = buildChartOfAccountsCodeAttributeDefintion();
        final AttributeDefinition accountNumberAttributeDefinition = buildAccountNumberAttributeDefintion();
        EasyMock.expect(persistenceStructureService.isPersistable(Account.class)).andReturn(true).anyTimes();
        EasyMock.expect(persistenceStructureService.getTableName(Account.class)).andReturn("CA_ACCOUNT_T").anyTimes();
        EasyMock.expect(persistenceStructureService.listFieldNames(Account.class)).andReturn(Arrays.asList("chartOfAccountsCode", "accountNumber", "accountName", "objectId", "versionNumber", "lastUpdatedTimestamp", "goodToBeOnTheRoadBackHome")).anyTimes();
        EasyMock.expect(dataDictionary.getBusinessObjectEntry("org.kuali.kfs.coa.businessobject.Account")).andReturn(businessObjectEntry).anyTimes();
        EasyMock.expect(businessObjectEntry.getAttributeDefinition("chartOfAccountsCode")).andReturn(chartOfAccountsAttributeDefinition);
        EasyMock.expect(persistenceStructureService.getColumnNameForFieldName(Account.class, "chartOfAccountsCode")).andReturn("FIN_COA_CD");
        EasyMock.expect(businessObjectEntry.getAttributeDefinition("accountNumber")).andReturn(accountNumberAttributeDefinition);
        EasyMock.expect(persistenceStructureService.getColumnNameForFieldName(Account.class, "accountNumber")).andReturn("ACCOUNT_NBR");
        EasyMock.expect(businessObjectEntry.getAttributeDefinition("goodToBeOnTheRoadBackHome")).andReturn(null);
        EasyMock.expect(businessObjectEntry.getObjectLabel()).andReturn("Account").anyTimes();
    }

    private void initializeExpectationsForVendorWithFields() {
        final AttributeDefinition vendorNumberAttributeDefinition = buildVendorNumberAttributeDefintion();
        final AttributeDefinition vendorNameAttributeDefinition = buildVendorNameAttributeDefintion();
        EasyMock.expect(persistenceStructureService.isPersistable(VendorAddress.class)).andReturn(true);
        EasyMock.expect(persistenceStructureService.isPersistable(VendorDefaultAddress.class)).andReturn(true);
        EasyMock.expect(persistenceStructureService.getTableName(VendorAddress.class)).andReturn("PUR_VNDR_ADDR_T").anyTimes();
        EasyMock.expect(persistenceStructureService.getTableName(VendorDefaultAddress.class)).andReturn("PUR_VNDR_DFLT_ADDR_T").anyTimes();
        EasyMock.expect(dataDictionary.getBusinessObjectEntry("org.kuali.kfs.vnd.businessobject.VendorAddress")).andReturn(buildBusinessObjectEntryFixture(VendorAddress.class)).anyTimes();
        EasyMock.expect(dataDictionary.getBusinessObjectEntry("org.kuali.kfs.vnd.businessobject.VendorDefaultAddress")).andReturn(buildBusinessObjectEntryFixture(VendorDefaultAddress.class)).anyTimes();
        EasyMock.expect(persistenceStructureService.listFieldNames(VendorDetail.class)).andReturn(Arrays.asList("vendorNumber", "vendorName"));
        EasyMock.expect(persistenceStructureService.listFieldNames(VendorAddress.class)).andReturn(new ArrayList());
        EasyMock.expect(persistenceStructureService.listFieldNames(VendorDefaultAddress.class)).andReturn(new ArrayList());
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

    private void initializeExpectationsFor(Class boClass, String tableName) {
        EasyMock.expect(persistenceStructureService.isPersistable(boClass)).andReturn(true);
        EasyMock.expect(dataDictionary.getBusinessObjectEntry(boClass.getName())).andReturn(businessObjectEntry);
        EasyMock.expect(businessObjectEntry.getObjectLabel()).andReturn(boClass.getSimpleName());
        EasyMock.expect(persistenceStructureService.getTableName(boClass)).andReturn(tableName).anyTimes();
        EasyMock.expect(persistenceStructureService.listFieldNames(boClass)).andReturn(new ArrayList());
    }

    private void initializeExpectationsForVendor() {
        EasyMock.expect(persistenceStructureService.isPersistable(VendorDetail.class)).andReturn(true);
        EasyMock.expect(dataDictionary.getBusinessObjectEntry(VendorDetail.class.getName())).andReturn(businessObjectEntry).anyTimes();
        EasyMock.expect(businessObjectEntry.getObjectLabel()).andReturn("Vendor");
        EasyMock.expect(persistenceStructureService.getTableName(VendorDetail.class)).andReturn("PUR_VNDR_DTL_T");
        initializeExpectationsForVendorWithFields();
    }


    private void assertAgainstAccountTableDTO(TableDTO tableDTO) {
        Assert.assertNotNull("The TableDTO should not be null", tableDTO);
        Assert.assertEquals("The TableDTO code should be \"CA_ACCOUNT_T\"", "CA_ACCOUNT_T", tableDTO.getCode());
        Assert.assertEquals("The TableDTO name should be \"Account\"", "Account", tableDTO.getName());
        Assert.assertNull("The TableDTO description should be null", tableDTO.getDescription());
        assertAgainstFieldDTOs(tableDTO.getFields());
    }

    private TransactionalDocumentEntry buildTransferOfFundsDocumentEntryFixture() {
        TransactionalDocumentEntry documentEntry = new TransactionalDocumentEntry();
        documentEntry.setDocumentClass(TransferOfFundsDocument.class);
        documentEntry.setDocumentTypeName("TF");
        return documentEntry;
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

    private MaintenanceDocumentEntry buildAccountGlobalDocumentEntryFixture() {
        MaintenanceDocumentEntry documentEntry = new MaintenanceDocumentEntry();
        documentEntry.setDataObjectClass(AccountGlobal.class);
        documentEntry.setDocumentTypeName("GACC");
        return documentEntry;
    }

    private BusinessObjectEntry buildBusinessObjectEntryFixture(Class clazz) {
        BusinessObjectEntry bo = new BusinessObjectEntry();
        bo.setBaseBusinessObjectClass(clazz);
        return bo;
    }

    private TransactionalDocumentEntry buildFakeTransactionalDocumentEntryFixture() {
        TransactionalDocumentEntry transactionalDocumentEntry = new TransactionalDocumentEntry();
        transactionalDocumentEntry.setDocumentTypeName("FollowYourArrow");
        return transactionalDocumentEntry;
    }

    private void assertAgainstRootTableDTO(TableDTO tableDTO) {
        Assert.assertNotNull("We should have gotten back a root TableDTO", tableDTO);
        assertAgainstAccountTableDTO(tableDTO);
    }
}
