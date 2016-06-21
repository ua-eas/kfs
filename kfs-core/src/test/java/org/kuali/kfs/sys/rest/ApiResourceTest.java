package org.kuali.kfs.sys.rest;

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
import org.kuali.kfs.krad.bo.ModuleConfiguration;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.KRADServiceLocator;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.krad.service.PersistenceStructureService;
import org.kuali.kfs.krad.util.KRADPropertyConstants;
import org.kuali.kfs.sys.businessobject.UnitOfMeasure;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@PrepareForTest(KRADServiceLocator.class)
@RunWith(PowerMockRunner.class)
public class ApiResourceTest {
    
    private ApiResource apiResource;
    private KualiModuleService kualiModuleService;
    private ModuleService moduleService;
    private BusinessObjectService businessObjectService;
    private PersistenceStructureService persistenceStructureService;
    
    @Before
    public void setup() {
        apiResource = new ApiResource();
        kualiModuleService = EasyMock.createMock(KualiModuleService.class);
        moduleService = EasyMock.createMock(ModuleService.class);
        businessObjectService = EasyMock.createMock(BusinessObjectService.class);
        persistenceStructureService = EasyMock.createMock(PersistenceStructureService.class);
        PowerMock.mockStatic(KRADServiceLocator.class);
    }
    
    @Test
    public void testClassNotFound() throws Exception {
        EasyMock.expect(kualiModuleService.getInstalledModuleServices()).andReturn(getInstalledModuleServices());
        EasyMock.expect(moduleService.getModuleConfiguration()).andReturn(getModuleConfiguration());
        EasyMock.expect(moduleService.getModuleConfiguration()).andReturn(getModuleConfiguration());
        
        EasyMock.replay(kualiModuleService);
        EasyMock.replay(moduleService);
        EasyMock.replay(businessObjectService);
        ApiResource.setKualiModuleService(kualiModuleService);
        ApiResource.setBusinessObjectService(businessObjectService);
        
        Response response = apiResource.getSingleObject("sys", "sillyclasses", "12345");
        EasyMock.verify(kualiModuleService);
        EasyMock.verify(moduleService);
        EasyMock.verify(businessObjectService);
        Assert.assertTrue("Should have returned 404", response.getStatus() == Status.NOT_FOUND.getStatusCode());
    }
    
    @Test
    public void testSimpleBoReturned() throws Exception {
        EasyMock.expect(kualiModuleService.getInstalledModuleServices()).andReturn(getInstalledModuleServices());
        EasyMock.expect(moduleService.getModuleConfiguration()).andReturn(getModuleConfiguration());
        EasyMock.expect(moduleService.getModuleConfiguration()).andReturn(getModuleConfiguration());
        Map<String, String> queryCriteria = new HashMap<String, String>();
        queryCriteria.put(KRADPropertyConstants.OBJECT_ID, "12345");
        EasyMock.expect(businessObjectService.findMatching(UnitOfMeasure.class, queryCriteria)).andReturn(getUomCollection());
        EasyMock.expect(KRADServiceLocator.getPersistenceStructureService()).andReturn(persistenceStructureService);
        EasyMock.expect(persistenceStructureService.isPersistable(UnitOfMeasure.class)).andReturn(true);
        EasyMock.expect(persistenceStructureService.getBusinessObjectAttributeClass(UnitOfMeasure.class, "extension")).andReturn(null);
        
        EasyMock.replay(kualiModuleService);
        EasyMock.replay(moduleService);
        EasyMock.replay(businessObjectService);
        PowerMock.replay(KRADServiceLocator.class);
        EasyMock.replay(persistenceStructureService);
        ApiResource.setKualiModuleService(kualiModuleService);
        ApiResource.setBusinessObjectService(businessObjectService);
        
        Response response = apiResource.getSingleObject("sys", "unit-of-measures", "12345");
        EasyMock.verify(kualiModuleService);
        EasyMock.verify(moduleService);
        EasyMock.verify(businessObjectService);
        EasyMock.verify(persistenceStructureService);
        Assert.assertTrue("Should have returned OK", response.getStatus() == Status.OK.getStatusCode());
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        BeanMap beanMap = new BeanMap(getUom());
        Assert.assertTrue("Beans should have matching values " + beanMap.toString() + " and " + entity.toString(), 
                mapsEqualEnough(entity, beanMap, "itemUnitOfMeasureCode", "itemUnitOfMeasureDescription"));
        
    }

    private Collection<UnitOfMeasure> getUomCollection() {
        List<UnitOfMeasure> result = new ArrayList<UnitOfMeasure>();
        result.add(getUom());
        return result;
    }

    private UnitOfMeasure getUom() {
        UnitOfMeasure uom = new UnitOfMeasure();
        uom.setItemUnitOfMeasureCode("DEV");
        uom.setItemUnitOfMeasureDescription("Developer");
        return uom;
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

    private List<ModuleService> getInstalledModuleServices() {
        List<ModuleService> result = new ArrayList<ModuleService>();
        result.add(moduleService);
        return result;
    }
}
