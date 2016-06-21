package org.kuali.kfs.sys.rest;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
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
import org.kuali.kfs.krad.bo.ModuleConfiguration;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.KRADServiceLocator;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.krad.service.PersistenceStructureService;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.KRADPropertyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.UnitOfMeasure;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
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
        PowerMock.mockStatic(KRADServiceLocator.class);
        PowerMock.mockStatic(org.kuali.kfs.krad.util.ObjectUtils.class);
    }
    
    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class})
    public void testClassNotFound() throws Exception {
        EasyMock.expect(kualiModuleService.getInstalledModuleServices()).andReturn(getInstalledModuleServices());
        EasyMock.expect(moduleService.getModuleConfiguration()).andReturn(getModuleConfiguration());
        EasyMock.expect(moduleService.getModuleConfiguration()).andReturn(getModuleConfiguration());
        
        EasyMock.replay(kualiModuleService, moduleService, businessObjectService);
        BusinessObjectResource.setKualiModuleService(kualiModuleService);
        BusinessObjectResource.setBusinessObjectService(businessObjectService);
        
        Response response = apiResource.getSingleObject("sys", "sillyclasses", "12345");
        EasyMock.verify(kualiModuleService, moduleService, businessObjectService);
        Assert.assertTrue("Should have returned 404", response.getStatus() == Status.NOT_FOUND.getStatusCode());
    }
    
    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class})
    public void testSimpleBoReturned() throws Exception {
        EasyMock.expect(kualiModuleService.getInstalledModuleServices()).andReturn(getInstalledModuleServices());
        EasyMock.expect(moduleService.getModuleConfiguration()).andReturn(getModuleConfiguration());
        EasyMock.expect(moduleService.getModuleConfiguration()).andReturn(getModuleConfiguration());
        Map<String, String> queryCriteria = new HashMap<String, String>();
        queryCriteria.put(KRADPropertyConstants.OBJECT_ID, "12345");
        EasyMock.expect(businessObjectService.findMatching(UnitOfMeasure.class, queryCriteria)).andReturn(getUomCollection());
        EasyMock.expect(KRADServiceLocator.getPersistenceStructureService()).andReturn(persistenceStructureService);
        org.kuali.kfs.krad.util.ObjectUtils.materializeSubObjectsToDepth(uom, 3);
        PowerMock.expectLastCall();
        EasyMock.expect(persistenceStructureService.isPersistable(UnitOfMeasure.class)).andReturn(true);
        EasyMock.expect(persistenceStructureService.getBusinessObjectAttributeClass(UnitOfMeasure.class, "extension")).andReturn(null);
        
        EasyMock.replay(kualiModuleService, moduleService, businessObjectService, persistenceStructureService);
        PowerMock.replay(KRADServiceLocator.class);
        PowerMock.replay(org.kuali.kfs.krad.util.ObjectUtils.class);
        BusinessObjectResource.setKualiModuleService(kualiModuleService);
        BusinessObjectResource.setBusinessObjectService(businessObjectService);
        
        Response response = apiResource.getSingleObject("sys", "unit-of-measures", "12345");
        EasyMock.verify(kualiModuleService, moduleService, businessObjectService, persistenceStructureService);
        Assert.assertTrue("Should have returned OK", response.getStatus() == Status.OK.getStatusCode());
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        BeanMap beanMap = new BeanMap(uom);
        Assert.assertTrue("Beans should have matching values " + beanMap.toString() + " and " + entity.toString(), 
                mapsEqualEnough(entity, beanMap, "itemUnitOfMeasureCode", "itemUnitOfMeasureDescription"));
        
    }
    
    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class})
    public void testComplexBoReturned() throws Exception {
        EasyMock.expect(kualiModuleService.getInstalledModuleServices()).andReturn(getInstalledModuleServices());
        EasyMock.expect(moduleService.getModuleConfiguration()).andReturn(getFpModuleConfiguration()).times(5);
        Map<String, String> queryCriteria = new HashMap<String, String>();
        queryCriteria.put(KRADPropertyConstants.OBJECT_ID, "12345");
        EasyMock.expect(businessObjectService.findMatching(Deposit.class, queryCriteria)).andReturn(getDepositCollection());
        EasyMock.expect(KRADServiceLocator.getPersistenceStructureService()).andReturn(persistenceStructureService);
        org.kuali.kfs.krad.util.ObjectUtils.materializeSubObjectsToDepth(deposit, 3);
        PowerMock.expectLastCall();
        EasyMock.expect(persistenceStructureService.isPersistable(Deposit.class)).andReturn(true);
        EasyMock.expect(persistenceStructureService.getBusinessObjectAttributeClass(Deposit.class, "extension")).andReturn(null);
        EasyMock.expect(kualiModuleService.getResponsibleModuleService(Bank.class)).andReturn(moduleService);
        EasyMock.expect(kualiModuleService.getResponsibleModuleService(DepositCashReceiptControl.class)).andReturn(moduleService);
        EasyMock.expect(kualiModuleService.getResponsibleModuleService(DepositCashReceiptControl.class)).andReturn(moduleService);
        EasyMock.expect(configurationService.getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY)).andReturn("http://myapp").times(3);
        
        EasyMock.replay(kualiModuleService, moduleService, businessObjectService, persistenceStructureService, configurationService);
        PowerMock.replay(KRADServiceLocator.class);
        PowerMock.replay(org.kuali.kfs.krad.util.ObjectUtils.class);
        BusinessObjectResource.setKualiModuleService(kualiModuleService);
        BusinessObjectResource.setBusinessObjectService(businessObjectService);
        BusinessObjectResource.setConfigurationService(configurationService);
        
        Response response = apiResource.getSingleObject("fp", "deposits", "12345");
        EasyMock.verify(kualiModuleService, moduleService, businessObjectService, persistenceStructureService);
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
        return result;
    }
    
    private ModuleConfiguration getFpModuleConfiguration() {
        ModuleConfiguration result = new ModuleConfiguration();
        result.setNamespaceCode("KFS-FP");
        result.setPackagePrefixes(new ArrayList<String>());
        result.getPackagePrefixes().add("org.kuali.kfs.fp");
        return result;
    }

    private List<ModuleService> getInstalledModuleServices() {
        List<ModuleService> result = new ArrayList<ModuleService>();
        result.add(moduleService);
        return result;
    }
}
