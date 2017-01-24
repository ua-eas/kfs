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
import org.kuali.kfs.krad.bo.ModuleConfiguration;
import org.kuali.kfs.krad.datadictionary.AttributeDefinition;
import org.kuali.kfs.krad.datadictionary.BusinessObjectEntry;
import org.kuali.kfs.krad.datadictionary.DataDictionary;
import org.kuali.kfs.krad.datadictionary.DocumentEntry;
import org.kuali.kfs.krad.datadictionary.MaintenanceDocumentEntry;
import org.kuali.kfs.krad.datadictionary.TransactionalDocumentEntry;
import org.kuali.kfs.krad.service.DataDictionaryService;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.krad.service.PersistenceStructureService;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.dto.EntityDTO;
import org.kuali.kfs.sys.businessobject.dto.FieldDTO;
import org.kuali.kfs.sys.businessobject.dto.TableDTO;
import org.kuali.kfs.vnd.businessobject.VendorContact;
import org.kuali.kfs.vnd.businessobject.VendorPhoneNumber;

import java.io.ByteArrayInputStream;
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
        EasyMock.expect(dataDictionaryService.getDataDictionary()).andReturn(dataDictionary);
        Map<String, DocumentEntry> documentEntries = new HashMap<>();
        documentEntries.put("FR", buildAssetFabricationMaintenanceDocumentEntryFixture());
        documentEntries.put("ACCT", buildAccountMaintenanceDocumentEntryFixture());
        documentEntries.put("ORR", buildOrganizationReviewRoleMaintenanceDocumentEntryFixture());
        documentEntries.put("FollowYourArrow", buildFakeTransactionalDocumentEntryFixture());
        documentEntries.put("GACC", buildAccountGlobalDocumentEntryFixture());
        EasyMock.expect(dataDictionary.getDocumentEntries()).andReturn(documentEntries);

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

        EasyMock.replay(dataDictionaryService, dataDictionary, businessObjectEntry, persistenceStructureService, kualiModuleService, moduleService);

        final EntityDTO entityDTO = dataDictionaryMigrationService.convertMaintenanceDocumentToEntityDTO(maintenanceDocumentEntry);

        EasyMock.verify(dataDictionaryService, dataDictionary, businessObjectEntry, persistenceStructureService, kualiModuleService, moduleService);

        Assert.assertNotNull("We should have gotten back an EntityDTO", entityDTO);
        Assert.assertEquals("The module namespace should be \"LUSH-LIT UP\" as that's what I was listening to at the time", "LUSH-LIT UP", entityDTO.getModuleCode());
        Assert.assertEquals("The code for the Entity should be \"ACCT\"", "ACCT", entityDTO.getCode());
        Assert.assertEquals("The name for the Entity should be \"Account\"", "Account", entityDTO.getName());
        Assert.assertNull("The description for the Entity should be null", entityDTO.getDescription());
        assertAgainstTableDTOs(entityDTO.getTables());
    }

    @Test
    public void testBuildTableDTOs() {
        MaintenanceDocumentEntry documentEntry = buildAccountMaintenanceDocumentEntryFixture();
        initializeExpectationsForAccountTable();

        EasyMock.replay(dataDictionaryService, dataDictionary, businessObjectEntry, persistenceStructureService);

        final List<TableDTO> tableDTOs = dataDictionaryMigrationService.buildTableDTOs(documentEntry);

        EasyMock.verify(dataDictionaryService, dataDictionary, businessObjectEntry, persistenceStructureService);
        assertAgainstTableDTOs(tableDTOs);
    }

    @Test
    public void testBuildTableDTO() {
        initializeExpectationsForAccountTable();

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