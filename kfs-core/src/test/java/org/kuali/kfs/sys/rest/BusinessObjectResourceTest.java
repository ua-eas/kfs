package org.kuali.kfs.sys.rest;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.lang3.ObjectUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.kfs.fp.businessobject.Deposit;
import org.kuali.kfs.fp.businessobject.DepositCashReceiptControl;
import org.kuali.kfs.krad.UserSession;
import org.kuali.kfs.krad.bo.ModuleConfiguration;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.datadictionary.BusinessObjectEntry;
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
import org.kuali.kfs.sec.service.AccessSecurityService;
import org.kuali.kfs.sec.service.impl.AccessSecurityServiceImpl;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.UnitOfMeasure;
import org.kuali.kfs.sys.identity.TestPerson;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.common.template.Template;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.permission.PermissionService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class BusinessObjectResourceTest {
    
    private BusinessObjectResource apiResource;
    private KualiModuleService kualiModuleService;
    private ModuleService moduleService;
    private BusinessObjectService businessObjectService;
    private PersistenceStructureService persistenceStructureService;
    private ConfigurationService configurationService;
    private DataDictionaryService dataDictionaryService;
    private DataDictionary dataDictionary;
    private PermissionService permissionService;
    private AccessSecurityService accessSecurityService;
    private UserSession userSession;
    private Date now = new Date(System.currentTimeMillis());
    private Deposit deposit = getDeposit();
    private UnitOfMeasure uom = getUom();
    
    @Before
    public void setup() {
        apiResource = new BusinessObjectResource();
        kualiModuleService = EasyMock.createMock(KualiModuleService.class);
        moduleService = EasyMock.createMock(ModuleService.class);
        businessObjectService = EasyMock.createMock(BusinessObjectService.class);
        persistenceStructureService = EasyMock.createMock(PersistenceStructureService.class);
        configurationService = EasyMock.createMock(ConfigurationService.class);
        dataDictionaryService = EasyMock.createMock(DataDictionaryService.class);
        dataDictionary = EasyMock.createMock(DataDictionary.class);
        permissionService = EasyMock.createMock(PermissionService.class);
        accessSecurityService = EasyMock.createMock(AccessSecurityService.class);
        userSession = EasyMock.createMock(UserSession.class);
        PowerMock.mockStatic(KRADServiceLocator.class);
        PowerMock.mockStaticPartial(org.kuali.kfs.krad.util.ObjectUtils.class, "materializeSubObjectsToDepth");
        PowerMock.mockStatic(KRADUtils.class);
    }
    
    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class, KRADUtils.class})
    public void testClassNotFound() throws Exception {
        EasyMock.expect(kualiModuleService.getInstalledModuleServices()).andReturn(getInstalledModuleServices());
        EasyMock.expect(moduleService.getModuleConfiguration()).andReturn(getModuleConfiguration());
        EasyMock.expect(moduleService.getModuleConfiguration()).andReturn(getModuleConfiguration());
        EasyMock.expect(dataDictionaryService.containsDictionaryObject("Sillyclasse")).andReturn(false);
        
        EasyMock.replay(kualiModuleService, moduleService, businessObjectService, dataDictionaryService);
        BusinessObjectResource.setKualiModuleService(kualiModuleService);
        BusinessObjectResource.setBusinessObjectService(businessObjectService);
        
        Response response = apiResource.getSingleObject("sys", "sillyclasses", "12345");
        EasyMock.verify(kualiModuleService, moduleService, businessObjectService, dataDictionaryService);
        Assert.assertTrue("Should have returned 404", response.getStatus() == Status.NOT_FOUND.getStatusCode());
    }
    
    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class, KRADUtils.class})
    public void testNotAuthorized() throws Exception {
        ModuleConfiguration moduleConfig = getModuleConfiguration();
        String className = "UnitOfMeasure";
        Class clazz = UnitOfMeasure.class;
        String namespaceCode = "KFS-SYS";
        Person person = getPerson();
        
        EasyMock.expect(kualiModuleService.getInstalledModuleServices()).andReturn(getInstalledModuleServices());
        EasyMock.expect(moduleService.getModuleConfiguration()).andReturn(moduleConfig).anyTimes();
        EasyMock.expect(dataDictionaryService.containsDictionaryObject(className)).andReturn(true);
        EasyMock.expect(dataDictionaryService.getDictionaryObject(className)).andReturn(getDDEntry(clazz));
        EasyMock.expect(KRADUtils.getUserSessionFromRequest(null)).andReturn(userSession);
        EasyMock.expect(KRADUtils.getNamespaceAndComponentSimpleName(clazz)).andReturn(makeMap(namespaceCode, className));
        EasyMock.expect(userSession.getPerson()).andReturn(person);
        EasyMock.expect(permissionService.isAuthorizedByTemplate("testPrincipalId", "KR-NS", KimConstants.PermissionTemplateNames.INQUIRE_INTO_RECORDS, makeMap(namespaceCode, className), Collections.<String, String>emptyMap()))
            .andReturn(false);
        
        
        EasyMock.replay(kualiModuleService, moduleService, businessObjectService, persistenceStructureService, dataDictionaryService, permissionService, accessSecurityService, userSession);
        PowerMock.replay(KRADServiceLocator.class);
        PowerMock.replay(org.kuali.kfs.krad.util.ObjectUtils.class);
        PowerMock.replay(KRADUtils.class);
        BusinessObjectResource.setKualiModuleService(kualiModuleService);
        BusinessObjectResource.setBusinessObjectService(businessObjectService);
        BusinessObjectResource.setPermissionService(permissionService);
        BusinessObjectResource.setAccessSecurityService(accessSecurityService);
        
        Response response = apiResource.getSingleObject("sys", "unit-of-measures", "12345");
        EasyMock.verify(kualiModuleService, moduleService, businessObjectService, persistenceStructureService, dataDictionaryService, permissionService, accessSecurityService, userSession);
        Assert.assertTrue("Should have returned Forbidden", response.getStatus() == Status.FORBIDDEN.getStatusCode());
    }
    
    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class, KRADUtils.class})
    public void testNoAccessSecurity() throws Exception {
        ModuleConfiguration moduleConfig = getModuleConfiguration();
        String className = "UnitOfMeasure";
        Class clazz = UnitOfMeasure.class;
        String namespaceCode = "KFS-SYS";
        Person person = getPerson();
        Collection collection = getUomCollection();
        
        EasyMock.expect(kualiModuleService.getInstalledModuleServices()).andReturn(getInstalledModuleServices());
        EasyMock.expect(moduleService.getModuleConfiguration()).andReturn(moduleConfig).anyTimes();
        EasyMock.expect(dataDictionaryService.containsDictionaryObject(className)).andReturn(true);
        EasyMock.expect(dataDictionaryService.getDictionaryObject(className)).andReturn(getDDEntry(clazz));
        EasyMock.expect(KRADUtils.getUserSessionFromRequest(null)).andReturn(userSession).times(2);
        EasyMock.expect(KRADUtils.getNamespaceAndComponentSimpleName(clazz)).andReturn(makeMap(namespaceCode, className));
        EasyMock.expect(userSession.getPerson()).andReturn(person).times(2);
        EasyMock.expect(permissionService.isAuthorizedByTemplate("testPrincipalId", "KR-NS", KimConstants.PermissionTemplateNames.INQUIRE_INTO_RECORDS, makeMap(namespaceCode, className), Collections.<String, String>emptyMap()))
            .andReturn(true);
        Map<String, String> queryCriteria = new HashMap<String, String>();
        queryCriteria.put(KRADPropertyConstants.OBJECT_ID, "12345");
        EasyMock.expect(businessObjectService.findMatching(clazz, queryCriteria)).andReturn(collection);
        EasyMock.expect(accessSecurityService.getInquiryWithFieldValueTemplate()).andReturn(null);
        EasyMock.expect(KRADUtils.getNamespaceCode(clazz)).andReturn(namespaceCode);
        accessSecurityService.applySecurityRestrictions((List)collection, person, null, Collections.singletonMap(KimConstants.AttributeConstants.NAMESPACE_CODE, namespaceCode));
        EasyMock.expectLastCall().andDelegateTo(new AccessSecurityServiceImpl() {
            @Override
            public void applySecurityRestrictions(List<? extends BusinessObject> results, Person person, Template permissionTemplate, Map<String,String> additionalPermissionDetails) {
                results.clear();
            }
        });
        
        EasyMock.replay(kualiModuleService, moduleService, businessObjectService, persistenceStructureService, dataDictionaryService, permissionService, accessSecurityService, userSession);
        PowerMock.replay(KRADServiceLocator.class);
        PowerMock.replay(org.kuali.kfs.krad.util.ObjectUtils.class);
        PowerMock.replay(KRADUtils.class);
        BusinessObjectResource.setKualiModuleService(kualiModuleService);
        BusinessObjectResource.setBusinessObjectService(businessObjectService);
        BusinessObjectResource.setPermissionService(permissionService);
        BusinessObjectResource.setAccessSecurityService(accessSecurityService);
        
        Response response = apiResource.getSingleObject("sys", "unit-of-measures", "12345");
        EasyMock.verify(kualiModuleService, moduleService, businessObjectService, persistenceStructureService, dataDictionaryService, permissionService, accessSecurityService, userSession);
        Assert.assertTrue("Should have returned Forbidden", response.getStatus() == Status.FORBIDDEN.getStatusCode());
    }
    
    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class, KRADUtils.class})
    public void testSimpleBoReturned() throws Exception {
        commonTestPrep(UnitOfMeasure.class, "KFS-SYS", getUomCollection(), getModuleConfiguration());
        
        EasyMock.replay(kualiModuleService, moduleService, businessObjectService, persistenceStructureService, dataDictionaryService, permissionService, accessSecurityService, userSession);
        PowerMock.replay(KRADServiceLocator.class);
        PowerMock.replay(org.kuali.kfs.krad.util.ObjectUtils.class);
        PowerMock.replay(KRADUtils.class);
        BusinessObjectResource.setKualiModuleService(kualiModuleService);
        BusinessObjectResource.setBusinessObjectService(businessObjectService);
        BusinessObjectResource.setPermissionService(permissionService);
        BusinessObjectResource.setAccessSecurityService(accessSecurityService);
        
        Response response = apiResource.getSingleObject("sys", "unit-of-measures", "12345");
        EasyMock.verify(kualiModuleService, moduleService, businessObjectService, persistenceStructureService, dataDictionaryService, permissionService, accessSecurityService, userSession);
        Assert.assertTrue("Should have returned OK", response.getStatus() == Status.OK.getStatusCode());
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        BeanMap beanMap = new BeanMap(uom);
        Assert.assertTrue("Beans should have matching values " + beanMap.toString() + " and " + entity.toString(), 
                mapsEqualEnough(entity, beanMap, "itemUnitOfMeasureCode", "itemUnitOfMeasureDescription"));      
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class, KRADUtils.class})
    public void testComplexBoReturned() throws Exception {
        commonTestPrep(Deposit.class, "KFS-FP", getDepositCollection(), getFpModuleConfiguration());
        
        EasyMock.expect(dataDictionary.getBusinessObjectEntryForConcreteClass(Bank.class.getName())).andReturn(getDDEntry(Bank.class));
        EasyMock.expect(dataDictionary.getBusinessObjectEntryForConcreteClass(DepositCashReceiptControl.class.getName()))
            .andReturn(getDDEntry(DepositCashReceiptControl.class))
            .times(2);       
        EasyMock.expect(dataDictionaryService.getDataDictionary()).andReturn(dataDictionary).anyTimes();
        EasyMock.expect(kualiModuleService.getResponsibleModuleService(Bank.class)).andReturn(moduleService);
        EasyMock.expect(kualiModuleService.getResponsibleModuleService(DepositCashReceiptControl.class)).andReturn(moduleService);
        EasyMock.expect(kualiModuleService.getResponsibleModuleService(DepositCashReceiptControl.class)).andReturn(moduleService);
        EasyMock.expect(configurationService.getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY)).andReturn("http://myapp").times(3);
        
        EasyMock.replay(kualiModuleService, moduleService, businessObjectService, persistenceStructureService, configurationService, dataDictionaryService, 
                dataDictionary, permissionService, accessSecurityService, userSession);
        PowerMock.replay(KRADServiceLocator.class);
        PowerMock.replay(org.kuali.kfs.krad.util.ObjectUtils.class);
        PowerMock.replay(KRADUtils.class);
        BusinessObjectResource.setKualiModuleService(kualiModuleService);
        BusinessObjectResource.setBusinessObjectService(businessObjectService);
        BusinessObjectResource.setConfigurationService(configurationService);
        BusinessObjectResource.setPermissionService(permissionService);
        BusinessObjectResource.setAccessSecurityService(accessSecurityService);
        
        Response response = apiResource.getSingleObject("fp", "deposits", "12345");
        EasyMock.verify(kualiModuleService, moduleService, businessObjectService, persistenceStructureService, dataDictionaryService, dataDictionary, 
                permissionService, accessSecurityService, userSession);
        Assert.assertTrue("Should have returned OK", response.getStatus() == Status.OK.getStatusCode());
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        BeanMap beanMap = new BeanMap(deposit);
        Assert.assertTrue("Beans should have matching values " + beanMap.toString() + " and " + entity.toString(), 
                mapsEqualEnough(entity, beanMap, "depositBankCode", "depositAmount"));
        Map<String, Object> bankMap = (Map<String, Object>) entity.get("bank");
        String bankLink = (String) bankMap.get("link");
        Assert.assertEquals("Bank link incorrect: " + bankLink, "http://myapp/api/v1/business-object/fp/banks/B123", bankLink);
        List<Map<String, Object>> depositCashReceiptControl = (List<Map<String, Object>>) entity.get("depositCashReceiptControl");
        Assert.assertEquals("Should be 2 depositCashReceiptControl", 2, depositCashReceiptControl.size());
        String dcLink = (String) depositCashReceiptControl.get(0).get("link");
        Assert.assertEquals("depositCashReceiptControl link incorrect", "http://myapp/api/v1/business-object/fp/deposit-cash-receipt-controls/DC001", dcLink);
        Long depositDate = (Long) entity.get("depositDate");
        Assert.assertEquals("depositDate incorrect", now.getTime(), depositDate.longValue());       
    }
    
    private void commonTestPrep(Class clazz, String namespaceCode, Collection collection, ModuleConfiguration moduleConfig) {
        String className = clazz.getSimpleName();
        Person person = getPerson();
        PersistableBusinessObject result = (PersistableBusinessObject) ((List) collection).get(0);
        EasyMock.expect(kualiModuleService.getInstalledModuleServices()).andReturn(getInstalledModuleServices());
        EasyMock.expect(moduleService.getModuleConfiguration()).andReturn(moduleConfig).anyTimes();
        EasyMock.expect(dataDictionaryService.containsDictionaryObject(className)).andReturn(true);
        EasyMock.expect(dataDictionaryService.getDictionaryObject(className)).andReturn(getDDEntry(clazz));
        EasyMock.expect(KRADUtils.getUserSessionFromRequest(null)).andReturn(userSession).times(2);
        EasyMock.expect(KRADUtils.getNamespaceAndComponentSimpleName(clazz)).andReturn(makeMap(namespaceCode, className));
        EasyMock.expect(userSession.getPerson()).andReturn(person).times(2);
        EasyMock.expect(permissionService.isAuthorizedByTemplate("testPrincipalId", "KR-NS", KimConstants.PermissionTemplateNames.INQUIRE_INTO_RECORDS, makeMap(namespaceCode, className), Collections.<String, String>emptyMap()))
            .andReturn(true);
        Map<String, String> queryCriteria = new HashMap<String, String>();
        queryCriteria.put(KRADPropertyConstants.OBJECT_ID, "12345");
        EasyMock.expect(businessObjectService.findMatching(clazz, queryCriteria)).andReturn(collection);
        EasyMock.expect(accessSecurityService.getInquiryWithFieldValueTemplate()).andReturn(null);
        EasyMock.expect(KRADUtils.getNamespaceCode(clazz)).andReturn(namespaceCode);
        accessSecurityService.applySecurityRestrictions((List)collection, person, null, Collections.singletonMap(KimConstants.AttributeConstants.NAMESPACE_CODE, namespaceCode));
        EasyMock.expectLastCall();
        EasyMock.expect(KRADServiceLocator.getPersistenceStructureService()).andReturn(persistenceStructureService);
        org.kuali.kfs.krad.util.ObjectUtils.materializeSubObjectsToDepth(result, 3);
        PowerMock.expectLastCall();
        EasyMock.expect(persistenceStructureService.isPersistable(clazz)).andReturn(true);
        EasyMock.expect(persistenceStructureService.getBusinessObjectAttributeClass(clazz, "extension")).andReturn(null);
    }

    private Collection<UnitOfMeasure> getUomCollection() {
        List<UnitOfMeasure> result = new ArrayList<>();
        result.add(uom);
        return result;
    }

    private UnitOfMeasure getUom() {
        UnitOfMeasure uom = new UnitOfMeasure();
        uom.setItemUnitOfMeasureCode("DEV");
        uom.setItemUnitOfMeasureDescription("Developer");
        return uom;
    }
    
    private Collection<Deposit> getDepositCollection() {
        List<Deposit> result = new ArrayList<>();
        result.add(deposit);
        return result;
    }

    private Deposit getDeposit() {
        Deposit deposit = new Deposit();
        deposit.setDepositBankCode("FellsWargo");
        Bank bank = new Bank();
        bank.setObjectId("B123");
        deposit.setBank(bank);
        deposit.setDepositDate(now);
        deposit.setDepositAmount(new KualiDecimal(100.02));
        
        DepositCashReceiptControl receiptControl1 = new DepositCashReceiptControl();
        receiptControl1.setObjectId("DC001");
        DepositCashReceiptControl receiptControl2 = new DepositCashReceiptControl();
        receiptControl2.setObjectId("DC002");
        deposit.getDepositCashReceiptControl().add(receiptControl1);
        deposit.getDepositCashReceiptControl().add(receiptControl2);
        return deposit;
    }
    
    private Map<String, String> makeMap(String namespaceCode, String className) {
        Map<String, String> result = new HashMap<>();
        result.put(KRADConstants.NAMESPACE_CODE, namespaceCode);
        result.put(KRADConstants.COMPONENT_NAME, className);
        return result;
    }
    
    private boolean mapsEqualEnough(Map<String, Object> map1, Map<String, Object> map2, String... properties) {
        for (String property : properties) {
            if (!ObjectUtils.equals(map1.get(property), map2.get(property))) {
                return false;
            }
        }
        
        return true;
    }

    private ModuleConfiguration getModuleConfiguration() {
        ModuleConfiguration result = new ModuleConfiguration();
        result.setNamespaceCode("KFS-SYS");
        result.setPackagePrefixes(new ArrayList<String>());
        result.getPackagePrefixes().add("org.kuali.kfs.sys");
        result.setDataDictionaryService(dataDictionaryService);
        return result;
    }
    
    private ModuleConfiguration getFpModuleConfiguration() {
        ModuleConfiguration result = new ModuleConfiguration();
        result.setNamespaceCode("KFS-FP");
        result.setPackagePrefixes(new ArrayList<String>());
        result.getPackagePrefixes().add("org.kuali.kfs.fp");
        result.setDataDictionaryService(dataDictionaryService);
        return result;
    }

    private List<ModuleService> getInstalledModuleServices() {
        List<ModuleService> result = new ArrayList<ModuleService>();
        result.add(moduleService);
        return result;
    }
    
    private BusinessObjectEntry getDDEntry(Class clazz) {
        BusinessObjectEntry result = new BusinessObjectEntry();
        result.setBusinessObjectClass(clazz);
        return result;
    }
    
    private Person getPerson() {
        return new TestPerson("testPrincipalId", "testPrincipalName");       
    }
}
