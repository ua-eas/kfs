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
package org.kuali.kfs.sys.rest.resource;

import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.lang3.ObjectUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryExclusionType;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryType;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.businessobject.OrganizationExtension;
import org.kuali.kfs.kns.datadictionary.MaintainableSectionDefinition;
import org.kuali.kfs.kns.datadictionary.MaintenanceDocumentEntry;
import org.kuali.kfs.kns.service.BusinessObjectAuthorizationService;
import org.kuali.kfs.krad.UserSession;
import org.kuali.kfs.krad.bo.ModuleConfiguration;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.datadictionary.DataDictionary;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.DataDictionaryService;
import org.kuali.kfs.krad.service.KRADServiceLocator;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.krad.service.PersistenceStructureService;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.KRADPropertyConstants;
import org.kuali.kfs.krad.util.KRADUtils;
import org.kuali.kfs.sec.SecConstants;
import org.kuali.kfs.sec.service.AccessSecurityService;
import org.kuali.kfs.sec.service.impl.AccessSecurityServiceImpl;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.UnitOfMeasure;
import org.kuali.kfs.sys.identity.TestPerson;
import org.kuali.kfs.sys.rest.BusinessObjectApiResourceTestHelper;
import org.kuali.kfs.sys.rest.MockDataDictionaryService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.common.template.Template;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.permission.PermissionService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;


@RunWith(PowerMockRunner.class)
public class BusinessObjectApiSingleObjectResourceTest {

    private BusinessObjectApiResource apiResource;
    private KualiModuleService kualiModuleService;
    private ModuleService moduleService;
    private BusinessObjectService businessObjectService;
    private BusinessObjectAuthorizationService businessObjectAuthorizationService;
    private PersistenceStructureService persistenceStructureService;
    private ConfigurationService configurationService;
    private DataDictionaryService dataDictionaryService;
    private DataDictionary dataDictionary;
    private MaintenanceDocumentEntry maintenanceDocumentEntry;
    private PermissionService permissionService;
    private AccessSecurityService accessSecurityService;
    private UserSession userSession;
    private UnitOfMeasure uom = BusinessObjectApiResourceTestHelper.getUom();
    private Organization org = BusinessObjectApiResourceTestHelper.getOrganization();
    private Bank bank = BusinessObjectApiResourceTestHelper.getBank();
    private IndirectCostRecoveryType indirectCostRecoveryType = BusinessObjectApiResourceTestHelper.getIndirectCostRecoveryType();
    private Person testPerson = new TestPerson("testPrincipalId", "testPrincipalName");

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        apiResource = new BusinessObjectApiResource("sys");
        kualiModuleService = EasyMock.createMock(KualiModuleService.class);
        moduleService = EasyMock.createMock(ModuleService.class);
        businessObjectService = EasyMock.createMock(BusinessObjectService.class);
        persistenceStructureService = EasyMock.createMock(PersistenceStructureService.class);
        configurationService = EasyMock.createMock(ConfigurationService.class);
        dataDictionaryService = EasyMock.partialMockBuilder(MockDataDictionaryService.class).addMockedMethods("containsDictionaryObject", "getDictionaryObject", "getDataDictionary").createMock();
        dataDictionary = EasyMock.createMock(DataDictionary.class);
        maintenanceDocumentEntry = EasyMock.createMock(MaintenanceDocumentEntry.class);
        permissionService = EasyMock.createMock(PermissionService.class);
        accessSecurityService = EasyMock.createMock(AccessSecurityService.class);
        userSession = EasyMock.createMock(UserSession.class);
        businessObjectAuthorizationService = EasyMock.createMock(BusinessObjectAuthorizationService.class);
        PowerMock.mockStatic(KRADServiceLocator.class);
        PowerMock.mockStatic(KRADUtils.class);
    }

    @Test
    @PrepareForTest({KRADUtils.class})
    public void testMaintenanceDocumentEntryNotFound() throws Exception {
        EasyMock.expect(dataDictionary.getDocumentEntry("GRRR")).andReturn(null);
        EasyMock.expect(dataDictionaryService.getDataDictionary()).andReturn(dataDictionary);

        EasyMock.replay(dataDictionaryService, dataDictionary);
        BusinessObjectApiResource.setDataDictionaryService(dataDictionaryService);

        Response response = apiResource.findSingleBusinessObject("grrr", "12345");
        EasyMock.verify(dataDictionaryService, dataDictionary);
        Assert.assertTrue("Should have returned 404", response.getStatus() == Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @PrepareForTest({KRADUtils.class})
    public void testNotAuthorized() throws Exception {
        ModuleConfiguration moduleConfig = BusinessObjectApiResourceTestHelper.getSysModuleConfiguration(dataDictionaryService);
        String documentTypeName = "PMUM";
        Class clazz = UnitOfMeasure.class;
        String namespaceCode = "KFS-SYS";

        EasyMock.expect(kualiModuleService.getInstalledModuleServices()).andReturn(getInstalledModuleServices());
        EasyMock.expect(moduleService.getModuleConfiguration()).andReturn(moduleConfig).anyTimes();
        EasyMock.expect(maintenanceDocumentEntry.getDataObjectClass()).andReturn(clazz);
        EasyMock.expect(dataDictionary.getDocumentEntry(documentTypeName)).andReturn(maintenanceDocumentEntry);
        EasyMock.expect(dataDictionaryService.getDataDictionary()).andReturn(dataDictionary);
        EasyMock.expect(KRADUtils.getUserSessionFromRequest(null)).andReturn(userSession);
        EasyMock.expect(KRADUtils.getNamespaceAndComponentSimpleName(clazz)).andReturn(makeMap(namespaceCode, documentTypeName));
        EasyMock.expect(userSession.getPerson()).andReturn(testPerson);
        EasyMock.expect(permissionService.isAuthorizedByTemplate("testPrincipalId", "KR-NS", KimConstants.PermissionTemplateNames.INQUIRE_INTO_RECORDS, makeMap(namespaceCode, documentTypeName), Collections.emptyMap()))
            .andReturn(false);


        EasyMock.replay(dataDictionaryService, maintenanceDocumentEntry, dataDictionary, kualiModuleService,
                                moduleService, permissionService, userSession);
        PowerMock.replay(KRADUtils.class);
        BusinessObjectApiResource.setKualiModuleService(kualiModuleService);
        BusinessObjectApiResource.setPermissionService(permissionService);
        BusinessObjectApiResource.setDataDictionaryService(dataDictionaryService);

        Response response = apiResource.findSingleBusinessObject(documentTypeName.toLowerCase(), "12345");
        EasyMock.verify(dataDictionaryService, maintenanceDocumentEntry, dataDictionary, kualiModuleService,
                            moduleService, permissionService, userSession);
        Assert.assertTrue("Should have returned Forbidden", response.getStatus() == Status.FORBIDDEN.getStatusCode());
    }

    @Test
    @PrepareForTest({KRADUtils.class})
    public void testNoAccessSecurity() throws Exception {
        ModuleConfiguration moduleConfig = BusinessObjectApiResourceTestHelper.getSysModuleConfiguration(dataDictionaryService);
        String documentTypeName = "PMUM";
        Class clazz = UnitOfMeasure.class;
        String namespaceCode = "KFS-SYS";
        Collection collection = Arrays.asList(BusinessObjectApiResourceTestHelper.getUom());

        EasyMock.expect(kualiModuleService.getInstalledModuleServices()).andReturn(getInstalledModuleServices());
        EasyMock.expect(moduleService.getModuleConfiguration()).andReturn(moduleConfig).anyTimes();
        EasyMock.expect(maintenanceDocumentEntry.getDataObjectClass()).andReturn(clazz);
        EasyMock.expect(dataDictionary.getDocumentEntry(documentTypeName)).andReturn(maintenanceDocumentEntry);
        EasyMock.expect(dataDictionaryService.getDataDictionary()).andReturn(dataDictionary);
        EasyMock.expect(KRADUtils.getUserSessionFromRequest(null)).andReturn(userSession).times(2);
        EasyMock.expect(KRADUtils.getNamespaceAndComponentSimpleName(clazz)).andReturn(makeMap(namespaceCode, documentTypeName));
        EasyMock.expect(userSession.getPerson()).andReturn(testPerson).times(2);
        EasyMock.expect(permissionService.isAuthorizedByTemplate("testPrincipalId", "KR-NS", KimConstants.PermissionTemplateNames.INQUIRE_INTO_RECORDS, makeMap(namespaceCode, documentTypeName), Collections.emptyMap()))
            .andReturn(true);
        Map<String, String> queryCriteria = new HashMap<>();
        queryCriteria.put(KRADPropertyConstants.OBJECT_ID, "12345");
        EasyMock.expect(businessObjectService.findMatching(clazz, queryCriteria)).andReturn(collection);
        EasyMock.expect(configurationService.getPropertyValueAsBoolean(SecConstants.ACCESS_SECURITY_MODULE_ENABLED_PROPERTY_NAME)).andReturn(true).anyTimes();
        EasyMock.expect(accessSecurityService.getInquiryWithFieldValueTemplate()).andReturn(null);
        EasyMock.expect(KRADUtils.getNamespaceCode(clazz)).andReturn(namespaceCode);
        accessSecurityService.applySecurityRestrictions((List) collection, testPerson, null, Collections.singletonMap(KimConstants.AttributeConstants.NAMESPACE_CODE, namespaceCode));
        EasyMock.expectLastCall().andDelegateTo(new AccessSecurityServiceImpl() {
            @Override
            public void applySecurityRestrictions(List<? extends BusinessObject> results, Person person, Template permissionTemplate, Map<String, String> additionalPermissionDetails) {
                results.clear();
            }
        });

        EasyMock.replay(kualiModuleService, moduleService, dataDictionaryService, businessObjectService,
                            permissionService, userSession, maintenanceDocumentEntry, dataDictionary,
                            configurationService, accessSecurityService);
        PowerMock.replay(KRADUtils.class);
        BusinessObjectApiResource.setKualiModuleService(kualiModuleService);
        BusinessObjectApiResource.setBusinessObjectService(businessObjectService);
        BusinessObjectApiResource.setPermissionService(permissionService);
        BusinessObjectApiResource.setAccessSecurityService(accessSecurityService);
        BusinessObjectApiResource.setDataDictionaryService(dataDictionaryService);
        BusinessObjectApiResource.setConfigurationService(configurationService);

        Response response = apiResource.findSingleBusinessObject(documentTypeName.toLowerCase(), "12345");
        EasyMock.verify(kualiModuleService, moduleService, dataDictionaryService, businessObjectService,
                            permissionService, userSession, maintenanceDocumentEntry, dataDictionary,
                            configurationService, accessSecurityService);
        Assert.assertTrue("Should have returned Forbidden", response.getStatus() == Status.FORBIDDEN.getStatusCode());
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class, KRADUtils.class})
    public void testSimpleBoReturned() throws Exception {
        String documentTypeName = "PMUM";
        commonSingleBusinessObjectTestPrep(UnitOfMeasure.class, documentTypeName, "KFS-SYS", () -> BusinessObjectApiResourceTestHelper.getUom(), BusinessObjectApiResourceTestHelper.getSysModuleConfiguration(dataDictionaryService));

        List<MaintainableSectionDefinition> maintainableSections = BusinessObjectApiResourceTestHelper.createUnitOfMeasureMaintainbleSections();
        EasyMock.expect(maintenanceDocumentEntry.getMaintainableSections()).andReturn(maintainableSections);
        EasyMock.expect(persistenceStructureService.isPersistable(UnitOfMeasure.class)).andReturn(false);

        EasyMock.replay(kualiModuleService, moduleService, businessObjectService, dataDictionaryService,
                        permissionService, accessSecurityService, userSession, configurationService, maintenanceDocumentEntry, dataDictionary,
                        persistenceStructureService, businessObjectAuthorizationService);
        PowerMock.replay(KRADServiceLocator.class);
        PowerMock.replay(KRADUtils.class);
        BusinessObjectApiResource.setKualiModuleService(kualiModuleService);
        BusinessObjectApiResource.setBusinessObjectService(businessObjectService);
        BusinessObjectApiResource.setPermissionService(permissionService);
        BusinessObjectApiResource.setAccessSecurityService(accessSecurityService);
        BusinessObjectApiResource.setDataDictionaryService(dataDictionaryService);
        BusinessObjectApiResource.setConfigurationService(configurationService);
        BusinessObjectApiResource.setPersistenceStructureService(persistenceStructureService);
        BusinessObjectApiResource.setBusinessObjectAuthorizationService(businessObjectAuthorizationService);

        Response response = apiResource.findSingleBusinessObject(documentTypeName.toLowerCase(), "12345");
        EasyMock.verify(kualiModuleService, moduleService, businessObjectService, dataDictionaryService,
                        permissionService, accessSecurityService, userSession, configurationService, maintenanceDocumentEntry, dataDictionary,
                        persistenceStructureService, businessObjectAuthorizationService);
        Assert.assertTrue("Should have returned OK", response.getStatus() == Status.OK.getStatusCode());
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        BeanMap beanMap = new BeanMap(uom);
        Assert.assertTrue("Beans should have matching values " + beanMap.toString() + " and " + entity.toString(),
            mapsEqualEnough(entity, beanMap, "itemUnitOfMeasureCode", "itemUnitOfMeasureDescription", "active", "objectId"));
        Assert.assertFalse("Entity should not contain field", entity.containsKey("I should not be here!"));
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class, KRADUtils.class})
    public void testNestedBoReturned() throws Exception {
        apiResource = new BusinessObjectApiResource("coa");

        String documentTypeName = "ORGN";
        commonSingleBusinessObjectTestPrep(Organization.class, documentTypeName, "KFS-COA", () -> BusinessObjectApiResourceTestHelper.getOrganization(), BusinessObjectApiResourceTestHelper.getCoaModuleConfiguration(dataDictionaryService));

        List<MaintainableSectionDefinition> maintainableSections = BusinessObjectApiResourceTestHelper.createOrganizationMaintainbleSections();
        EasyMock.expect(maintenanceDocumentEntry.getMaintainableSections()).andReturn(maintainableSections);

        EasyMock.expect(persistenceStructureService.hasReference(Organization.class, "organizationExtension")).andReturn(false).anyTimes();
        EasyMock.expect(persistenceStructureService.isPersistable(OrganizationExtension.class)).andReturn(false);
        EasyMock.expect(persistenceStructureService.isPersistable(Organization.class)).andReturn(false);
        EasyMock.expect(kualiModuleService.getResponsibleModuleService(OrganizationExtension.class)).andReturn(moduleService);
        EasyMock.expect(dataDictionary.getMaintenanceDocumentEntryForBusinessObjectClass(OrganizationExtension.class)).andReturn(null);
        EasyMock.expect(kualiModuleService.getResponsibleModuleService(Account.class)).andReturn(moduleService);
        MaintenanceDocumentEntry acctMaintenanceDocumentEntry = new MaintenanceDocumentEntry();
        acctMaintenanceDocumentEntry.setDocumentTypeName("ACCT");
        EasyMock.expect(dataDictionary.getMaintenanceDocumentEntryForBusinessObjectClass(Account.class)).andReturn(acctMaintenanceDocumentEntry);
        EasyMock.expect(kualiModuleService.getResponsibleModuleService(Chart.class)).andReturn(moduleService);
        MaintenanceDocumentEntry chartMaintenanceDocumentEntry = new MaintenanceDocumentEntry();
        chartMaintenanceDocumentEntry.setDocumentTypeName("COAT");
        EasyMock.expect(dataDictionary.getMaintenanceDocumentEntryForBusinessObjectClass(Chart.class)).andReturn(chartMaintenanceDocumentEntry);
        EasyMock.expect(configurationService.getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY)).andReturn("https://kuali.co/fin").times(2);

        EasyMock.replay(kualiModuleService, moduleService, businessObjectService, persistenceStructureService, dataDictionaryService,
                        permissionService, accessSecurityService, userSession, configurationService, maintenanceDocumentEntry,
                        dataDictionary, businessObjectAuthorizationService);
        PowerMock.replay(KRADServiceLocator.class);
        PowerMock.replay(KRADUtils.class);
        BusinessObjectApiResource.setKualiModuleService(kualiModuleService);
        BusinessObjectApiResource.setBusinessObjectService(businessObjectService);
        BusinessObjectApiResource.setPermissionService(permissionService);
        BusinessObjectApiResource.setAccessSecurityService(accessSecurityService);
        BusinessObjectApiResource.setDataDictionaryService(dataDictionaryService);
        BusinessObjectApiResource.setConfigurationService(configurationService);
        BusinessObjectApiResource.setPersistenceStructureService(persistenceStructureService);
        BusinessObjectApiResource.setBusinessObjectAuthorizationService(businessObjectAuthorizationService);

        Response response = apiResource.findSingleBusinessObject(documentTypeName.toLowerCase(), "12345");
        EasyMock.verify(kualiModuleService, moduleService, businessObjectService, persistenceStructureService, dataDictionaryService,
                        permissionService, accessSecurityService, userSession, configurationService, maintenanceDocumentEntry,
                        dataDictionary, businessObjectAuthorizationService);
        Assert.assertTrue("Should have returned OK", response.getStatus() == Status.OK.getStatusCode());
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        BeanMap beanMap = new BeanMap(org);
        Assert.assertTrue("Beans should have matching values " + beanMap.toString() + " and " + entity.toString(),
            mapsDeeplyEqualEnough(entity, beanMap, "chartOfAccountsCode",
                "organizationCode","responsibilityCenterCode","organizationName",
                "organizationExtension.chartOfAccountsCode","organizationExtension.organizationCode",
                "organizationExtension.hrmsCompany","organizationExtension.hrmsIuPositionAllowedFlag",
                "organizationExtension.hrmsIuTenureAllowedFlag"));
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class, KRADUtils.class})
    public void testBoWithCollectionReturned() throws Exception {
        apiResource = new BusinessObjectApiResource("coa");
        String documentTypeName = "ITYP";
        commonSingleBusinessObjectTestPrep(IndirectCostRecoveryType.class, documentTypeName, "KFS-COA", () -> BusinessObjectApiResourceTestHelper.getIndirectCostRecoveryType(), BusinessObjectApiResourceTestHelper.getCoaModuleConfiguration(dataDictionaryService));
        List<MaintainableSectionDefinition> maintainableSections = BusinessObjectApiResourceTestHelper.createIndirectCostRecoveryTypeMaintainbleSections();
        EasyMock.expect(maintenanceDocumentEntry.getMaintainableSections()).andReturn(maintainableSections);
        EasyMock.expect(persistenceStructureService.hasReference(IndirectCostRecoveryType.class, "indirectCostRecoveryExclusionTypeDetails")).andReturn(false).anyTimes();
        EasyMock.expect(persistenceStructureService.isPersistable(IndirectCostRecoveryExclusionType.class)).andReturn(false);
        EasyMock.expect(persistenceStructureService.isPersistable(IndirectCostRecoveryType.class)).andReturn(false);
        EasyMock.replay(kualiModuleService, moduleService, businessObjectService, persistenceStructureService, dataDictionaryService,
                        permissionService, accessSecurityService, userSession, configurationService, maintenanceDocumentEntry,
                        dataDictionary, businessObjectAuthorizationService);
        PowerMock.replay(KRADServiceLocator.class);
        PowerMock.replay(KRADUtils.class);
        BusinessObjectApiResource.setKualiModuleService(kualiModuleService);
        BusinessObjectApiResource.setBusinessObjectService(businessObjectService);
        BusinessObjectApiResource.setPermissionService(permissionService);
        BusinessObjectApiResource.setAccessSecurityService(accessSecurityService);
        BusinessObjectApiResource.setDataDictionaryService(dataDictionaryService);
        BusinessObjectApiResource.setConfigurationService(configurationService);
        BusinessObjectApiResource.setPersistenceStructureService(persistenceStructureService);
        BusinessObjectApiResource.setBusinessObjectAuthorizationService(businessObjectAuthorizationService);

        Response response = apiResource.findSingleBusinessObject(documentTypeName.toLowerCase(), "12345");
        EasyMock.verify(kualiModuleService, moduleService, businessObjectService, persistenceStructureService, dataDictionaryService,
                        permissionService, accessSecurityService, userSession, configurationService, maintenanceDocumentEntry,
                        dataDictionary, businessObjectAuthorizationService);
        Assert.assertTrue("Should have returned OK", response.getStatus() == Status.OK.getStatusCode());
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        BeanMap beanMap = new BeanMap(indirectCostRecoveryType);
        Assert.assertTrue("Beans should have matching values " + beanMap.toString() + " and " + entity.toString(),
            mapsEqualEnough(entity, beanMap, "code", "name", "active"));

        List<Object> beanMapCollectionObjects = (List<Object>)beanMap.get("indirectCostRecoveryExclusionTypeDetails");
        List<Map<String, Object>> entityCollections = (List<Map<String, Object>>)entity.get("indirectCostRecoveryExclusionTypeDetails");
        Assert.assertEquals("Serialized indirectCostRecoveryExclusionTypeDetails collections should be the same size", beanMapCollectionObjects.size(), entityCollections.size());
        for (int i = 0; i < beanMapCollectionObjects.size(); i++) {
            BeanMap beanMapCollectionObject = new BeanMap(beanMapCollectionObjects.get(i));
            Map<String, Object> entityCollectionObject = entityCollections.get(i);
            Assert.assertTrue("Beans should have matching values " + beanMapCollectionObject.toString() + " and " + entityCollectionObject.toString(),
                mapsEqualEnough(entityCollectionObject, beanMapCollectionObject, "chartOfAccountsCode", "financialObjectCode", "active"));
        }
    }

//    @Test
//    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class, KRADUtils.class})
//    public void testComplexBoReturned() throws Exception {
//        apiResource = new BusinessObjectApiResource("fp");
//        String documentTypeName = "DPST";
//        commonSingleBusinessObjectTestPrep(Deposit.class, documentTypeName, "KFS-FP", () -> getDeposit(), getFpModuleConfiguration());
//
//        EasyMock.expect(maintenanceDocumentEntry.getDocumentTypeName()).andReturn("BANK");
//        EasyMock.expect(dataDictionary.getMaintenanceDocumentEntryForBusinessObjectClass(Bank.class)).andReturn((MaintenanceDocumentEntry)maintenanceDocumentEntry);
//        MaintenanceDocumentEntry dcrMaintDocEntry = EasyMock.createMock(MaintenanceDocumentEntry.class);
//        EasyMock.expect(dcrMaintDocEntry.getDocumentTypeName()).andReturn("DCRC").times(2);
//        EasyMock.expect(dataDictionary.getMaintenanceDocumentEntryForBusinessObjectClass(DepositCashReceiptControl.class)).andReturn((MaintenanceDocumentEntry)dcrMaintDocEntry).times(2);
//        EasyMock.expect(dataDictionaryService.getDataDictionary()).andReturn(dataDictionary).anyTimes();
//        EasyMock.expect(kualiModuleService.getResponsibleModuleService(Bank.class)).andReturn(moduleService);
//        EasyMock.expect(kualiModuleService.getResponsibleModuleService(DepositCashReceiptControl.class)).andReturn(moduleService).times(2);
//        EasyMock.expect(configurationService.getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY)).andReturn("http://myapp").times(3);
//
//        EasyMock.replay(kualiModuleService, moduleService, businessObjectService, persistenceStructureService, configurationService, dataDictionaryService,
//            dataDictionary, permissionService, accessSecurityService, userSession, maintenanceDocumentEntry, dcrMaintDocEntry);
//        PowerMock.replay(KRADServiceLocator.class);
//        PowerMock.replay(org.kuali.kfs.krad.util.ObjectUtils.class);
//        PowerMock.replay(KRADUtils.class);
//        BusinessObjectApiResource.setKualiModuleService(kualiModuleService);
//        BusinessObjectApiResource.setBusinessObjectService(businessObjectService);
//        BusinessObjectApiResource.setConfigurationService(configurationService);
//        BusinessObjectApiResource.setPermissionService(permissionService);
//        BusinessObjectApiResource.setAccessSecurityService(accessSecurityService);
//        BusinessObjectApiResource.setDataDictionaryService(dataDictionaryService);
//
//        Response response = apiResource.findSingleBusinessObject(documentTypeName.toLowerCase(), "12345");
//        EasyMock.verify(kualiModuleService, moduleService, businessObjectService, persistenceStructureService, dataDictionaryService, dataDictionary,
//             permissionService, accessSecurityService, userSession, configurationService, maintenanceDocumentEntry, dcrMaintDocEntry);
//        Assert.assertTrue("Should have returned OK", response.getStatus() == Status.OK.getStatusCode());
//        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
//        BeanMap beanMap = new BeanMap(deposit);
//        Assert.assertTrue("Beans should have matching values " + beanMap.toString() + " and " + entity.toString(),
//            mapsEqualEnough(entity, beanMap, "depositBankCode", "depositAmount"));
//        Map<String, Object> bankMap = (Map<String, Object>) entity.get("bank");
//        String bankLink = (String) bankMap.get("link");
//        Assert.assertEquals("Bank link incorrect: " + bankLink, "http://myapp/fp/api/v1/reference/bank/B123", bankLink);
//        List<Map<String, Object>> depositCashReceiptControl = (List<Map<String, Object>>) entity.get("depositCashReceiptControl");
//        Assert.assertEquals("Should be 2 depositCashReceiptControl", 2, depositCashReceiptControl.size());
//        String dcLink = (String) depositCashReceiptControl.get(0).get("link");
//        Assert.assertEquals("depositCashReceiptControl link incorrect", "http://myapp/fp/api/v1/reference/dcrc/DC001", dcLink);
//        Long depositDate = (Long) entity.get("depositDate");
//        Assert.assertEquals("depositDate incorrect", now.getTime(), depositDate.longValue());
//    }

    @Test
    @PrepareForTest({KRADServiceLocator.class, KRADUtils.class})
    public void testSimpleMaskedBoReturned() throws Exception {
        String documentTypeName = "BANK";
        commonSingleBusinessObjectTestPrep(Bank.class, documentTypeName, "KFS-SYS", () -> BusinessObjectApiResourceTestHelper.getBank(), BusinessObjectApiResourceTestHelper.getSysModuleConfiguration(dataDictionaryService));

        List<MaintainableSectionDefinition> maintainableSections = BusinessObjectApiResourceTestHelper.createBankMaintainbleSections();
        EasyMock.expect(maintenanceDocumentEntry.getMaintainableSections()).andReturn(maintainableSections);

        EasyMock.expect(businessObjectAuthorizationService.isNonProductionEnvAndUnmaskingTurnedOff()).andReturn(false).anyTimes();
        EasyMock.expect(dataDictionary.getBusinessObjectEntry("Bank")).andReturn(BusinessObjectApiResourceTestHelper.getDDEntry(Bank.class)).anyTimes();
        EasyMock.expect(businessObjectAuthorizationService.canFullyUnmaskField(testPerson, Bank.class, "bankAccountNumber", null)).andReturn(false);
        EasyMock.expect(businessObjectAuthorizationService.canPartiallyUnmaskField(testPerson, Bank.class, "bankRoutingNumber", null)).andReturn(false);
        EasyMock.expect(persistenceStructureService.isPersistable(Bank.class)).andReturn(false);

        EasyMock.replay(kualiModuleService, moduleService, businessObjectService, businessObjectAuthorizationService,
                        persistenceStructureService, dataDictionaryService, dataDictionary, permissionService,
                        accessSecurityService, userSession, configurationService, maintenanceDocumentEntry);
        PowerMock.replay(KRADServiceLocator.class);
        PowerMock.replay(KRADUtils.class);
        BusinessObjectApiResource.setKualiModuleService(kualiModuleService);
        BusinessObjectApiResource.setBusinessObjectService(businessObjectService);
        BusinessObjectApiResource.setPermissionService(permissionService);
        BusinessObjectApiResource.setAccessSecurityService(accessSecurityService);
        BusinessObjectApiResource.setDataDictionaryService(dataDictionaryService);
        BusinessObjectApiResource.setConfigurationService(configurationService);
        BusinessObjectApiResource.setBusinessObjectAuthorizationService(businessObjectAuthorizationService);
        BusinessObjectApiResource.setPersistenceStructureService(persistenceStructureService);

        Response response = apiResource.findSingleBusinessObject(documentTypeName.toLowerCase(), "12345");
        EasyMock.verify(kualiModuleService, moduleService, businessObjectService, businessObjectAuthorizationService, persistenceStructureService,
                        dataDictionaryService, dataDictionary, permissionService, accessSecurityService, userSession,
                        configurationService, maintenanceDocumentEntry);
        Assert.assertTrue("Should have returned OK", response.getStatus() == Status.OK.getStatusCode());
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        BeanMap beanMap = new BeanMap(bank);
        Assert.assertTrue("Banks should have matching values " + beanMap.toString() + " and " + entity.toString(),
            mapsEqualEnough(entity, beanMap, "bankCode", "bankName"));
        Assert.assertNotEquals("Banks should not match routing numbers", beanMap.get(KFSPropertyConstants.BANK_ROUTING_NUMBER), entity.get(KFSPropertyConstants.BANK_ROUTING_NUMBER));
        Assert.assertNotEquals("Banks should not match account numbers", beanMap.get(KFSPropertyConstants.BANK_ACCOUNT_NUMBER), entity.get(KFSPropertyConstants.BANK_ACCOUNT_NUMBER));
        Assert.assertEquals("Bank routing number should be partially masked", "************4444", entity.get(KFSPropertyConstants.BANK_ROUTING_NUMBER));
        Assert.assertEquals("Bank account number should be fully masked", "XXXXZZZZ", entity.get(KFSPropertyConstants.BANK_ACCOUNT_NUMBER));
    }

    private void commonSingleBusinessObjectTestPrep(Class clazz, String documentTypeName, String namespaceCode, Supplier<? extends PersistableBusinessObject> boSupplier, ModuleConfiguration moduleConfig) {
        String className = clazz.getSimpleName();
        PersistableBusinessObject result = boSupplier.get();
        List<? extends PersistableBusinessObject> collection = Arrays.asList(result);
        EasyMock.expect(kualiModuleService.getInstalledModuleServices()).andReturn(getInstalledModuleServices());
        EasyMock.expect(moduleService.getModuleConfiguration()).andReturn(moduleConfig).anyTimes();
        EasyMock.expect(maintenanceDocumentEntry.getDataObjectClass()).andReturn(clazz).anyTimes();
        EasyMock.expect(dataDictionary.getDocumentEntry(documentTypeName)).andReturn(maintenanceDocumentEntry);
        EasyMock.expect(dataDictionaryService.getDataDictionary()).andReturn(dataDictionary).anyTimes();
        EasyMock.expect(KRADUtils.getUserSessionFromRequest(null)).andReturn(userSession).anyTimes();
        EasyMock.expect(KRADUtils.getNamespaceAndComponentSimpleName(clazz)).andReturn(makeMap(namespaceCode, className));
        EasyMock.expect(userSession.getPerson()).andReturn(testPerson).anyTimes();
        EasyMock.expect(permissionService.isAuthorizedByTemplate("testPrincipalId", "KR-NS", KimConstants.PermissionTemplateNames.INQUIRE_INTO_RECORDS, makeMap(namespaceCode, className), Collections.<String, String>emptyMap()))
            .andReturn(true);
        Map<String, String> queryCriteria = new HashMap<String, String>();
        queryCriteria.put(KRADPropertyConstants.OBJECT_ID, "12345");
        EasyMock.expect(businessObjectService.findMatching(clazz, queryCriteria)).andReturn(collection);
        EasyMock.expect(configurationService.getPropertyValueAsBoolean(SecConstants.ACCESS_SECURITY_MODULE_ENABLED_PROPERTY_NAME)).andReturn(true).anyTimes();
        EasyMock.expect(accessSecurityService.getInquiryWithFieldValueTemplate()).andReturn(null);
        EasyMock.expect(KRADUtils.getNamespaceCode(clazz)).andReturn(namespaceCode);
        accessSecurityService.applySecurityRestrictions(collection, testPerson, null, Collections.singletonMap(KimConstants.AttributeConstants.NAMESPACE_CODE, namespaceCode));
        EasyMock.expectLastCall();
        EasyMock.expect(KRADServiceLocator.getPersistenceStructureService()).andReturn(persistenceStructureService);
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class, KRADUtils.class})
    public void testNestedBoReturned_CheckLinks() throws Exception {
        apiResource = new BusinessObjectApiResource("coa");

        String documentTypeName = "ORGN";
        commonSingleBusinessObjectTestPrep(Organization.class, documentTypeName, "KFS-COA", () -> BusinessObjectApiResourceTestHelper.getOrganization(), BusinessObjectApiResourceTestHelper.getCoaModuleConfiguration(dataDictionaryService));

        List<MaintainableSectionDefinition> maintainableSections = BusinessObjectApiResourceTestHelper.createOrganizationMaintainbleSections();
        EasyMock.expect(maintenanceDocumentEntry.getMaintainableSections()).andReturn(maintainableSections);

        EasyMock.expect(persistenceStructureService.hasReference(Organization.class, "organizationExtension")).andReturn(false).anyTimes();
        EasyMock.expect(persistenceStructureService.isPersistable(OrganizationExtension.class)).andReturn(false);
        EasyMock.expect(persistenceStructureService.isPersistable(Organization.class)).andReturn(false);
        EasyMock.expect(kualiModuleService.getResponsibleModuleService(OrganizationExtension.class)).andReturn(moduleService);
        EasyMock.expect(dataDictionary.getMaintenanceDocumentEntryForBusinessObjectClass(OrganizationExtension.class)).andReturn(null);
        EasyMock.expect(kualiModuleService.getResponsibleModuleService(Account.class)).andReturn(moduleService);
        MaintenanceDocumentEntry acctMaintenanceDocumentEntry = new MaintenanceDocumentEntry();
        acctMaintenanceDocumentEntry.setDocumentTypeName("ACCT");
        EasyMock.expect(dataDictionary.getMaintenanceDocumentEntryForBusinessObjectClass(Account.class)).andReturn(acctMaintenanceDocumentEntry);
        EasyMock.expect(kualiModuleService.getResponsibleModuleService(Chart.class)).andReturn(moduleService);
        MaintenanceDocumentEntry chartMaintenanceDocumentEntry = new MaintenanceDocumentEntry();
        chartMaintenanceDocumentEntry.setDocumentTypeName("COAT");
        EasyMock.expect(dataDictionary.getMaintenanceDocumentEntryForBusinessObjectClass(Chart.class)).andReturn(chartMaintenanceDocumentEntry);
        EasyMock.expect(configurationService.getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY)).andReturn("https://kuali.co/fin").times(2);

        EasyMock.replay(kualiModuleService, moduleService, businessObjectService, persistenceStructureService, dataDictionaryService,
                permissionService, accessSecurityService, userSession, configurationService, maintenanceDocumentEntry,
                dataDictionary, businessObjectAuthorizationService);
        PowerMock.replay(KRADServiceLocator.class);
        PowerMock.replay(KRADUtils.class);
        BusinessObjectApiResource.setKualiModuleService(kualiModuleService);
        BusinessObjectApiResource.setBusinessObjectService(businessObjectService);
        BusinessObjectApiResource.setPermissionService(permissionService);
        BusinessObjectApiResource.setAccessSecurityService(accessSecurityService);
        BusinessObjectApiResource.setDataDictionaryService(dataDictionaryService);
        BusinessObjectApiResource.setConfigurationService(configurationService);
        BusinessObjectApiResource.setPersistenceStructureService(persistenceStructureService);
        BusinessObjectApiResource.setBusinessObjectAuthorizationService(businessObjectAuthorizationService);

        Response response = apiResource.findSingleBusinessObject(documentTypeName.toLowerCase(), "12345");
        EasyMock.verify(kualiModuleService, moduleService, businessObjectService, persistenceStructureService, dataDictionaryService,
                permissionService, accessSecurityService, userSession, configurationService, maintenanceDocumentEntry,
                dataDictionary, businessObjectAuthorizationService);
        Assert.assertTrue("Should have returned OK", response.getStatus() == Status.OK.getStatusCode());
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        Assert.assertTrue("Serialized entity should have organization plant chart", entity.containsKey(KFSPropertyConstants.ORGANIZATION_PLANT_CHART));
        Assert.assertEquals("Serialized organization plant chart should have one entry", 1, ((Map<String, Object>)entity.get(KFSPropertyConstants.ORGANIZATION_PLANT_CHART)).size());
        Assert.assertTrue("Serialized organization plant chart should have the key link", ((Map<String, Object>)entity.get(KFSPropertyConstants.ORGANIZATION_PLANT_CHART)).containsKey(KFSPropertyConstants.LINK));
        Assert.assertEquals("Serialized organization plant chart should be as expected", "https://kuali.co/fin/coa/api/v1/reference/coat/CHART777444", ((Map<String, Object>)entity.get(KFSPropertyConstants.ORGANIZATION_PLANT_CHART)).get(KFSPropertyConstants.LINK));
        Assert.assertTrue("Serialized entity should have organization plant account", entity.containsKey(KFSPropertyConstants.ORGANIZATION_PLANT_ACCOUNT));
        Assert.assertEquals("Serialized organization plant account should have one entry", 1, ((Map<String, Object>)entity.get(KFSPropertyConstants.ORGANIZATION_PLANT_ACCOUNT)).size());
        Assert.assertTrue("Serialized organization plant account should have the key link", ((Map<String, Object>)entity.get(KFSPropertyConstants.ORGANIZATION_PLANT_ACCOUNT)).containsKey(KFSPropertyConstants.LINK));
        Assert.assertEquals("Serialized organization plant account should be as expected", "https://kuali.co/fin/coa/api/v1/reference/acct/ACCT777444", ((Map<String, Object>)entity.get(KFSPropertyConstants.ORGANIZATION_PLANT_ACCOUNT)).get(KFSPropertyConstants.LINK));
    }

    private Map<String, String> makeMap(String namespaceCode, String className) {
        Map<String, String> result = new HashMap<>();
        result.put(KRADConstants.NAMESPACE_CODE, namespaceCode);
        result.put(KRADConstants.COMPONENT_NAME, className);
        return result;
    }

    private boolean mapsEqualEnough(Map<String, Object> map1, Map<Object, Object> map2, String... properties) {
        return Arrays.stream(properties).allMatch(property -> propertyEquals(map1, map2, property));
    }

    private boolean mapsDeeplyEqualEnough(Map<String, Object> map1, Map<Object, Object> map2, String... properties) {
        for (String property : properties) {
            if (property.indexOf(".") < 0) {
                if (!propertyEquals(map1, map2, property)) {
                    return false;
                }
            } else {
                final String head = property.substring(0, property.indexOf('.'));
                final String tail = property.substring(property.indexOf('.') + 1);
                Object head1 = map1.get(head);
                Object head2 = map2.get(head);
                if (!(head1 instanceof Map)) {
                    head1 = new BeanMap(head1);
                }
                if (!(head2 instanceof Map)) {
                    head2 = new BeanMap(head2);
                }
                return (mapsDeeplyEqualEnough((Map<String, Object>)head1, (Map<Object, Object>)head2, tail));
            }
        }

        return true;
    }

    private boolean propertyEquals(Map<String, Object> map1, Map<Object, Object> map2, String property) {
        if (map1.get(property) == null && map2.get(property) == null) {
            return false;
        }
        return ObjectUtils.equals(map1.get(property), map2.get(property));
    }

    private List<ModuleService> getInstalledModuleServices() {
        List<ModuleService> result = new ArrayList<ModuleService>();
        result.add(moduleService);
        return result;
    }
}
