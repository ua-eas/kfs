package org.kuali.kfs.sys.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.krad.bo.ModuleConfiguration;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.krad.service.PersistenceStructureService;
import org.kuali.kfs.krad.util.KRADPropertyConstants;
import org.kuali.kfs.sys.businessobject.UnitOfMeasure;

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
    
    /*
     * This test currently fails, because getExtension() on PersistableBusinessObjectBase has a static
     * method call that fails when there is no Spring context.
     * Choices:
     *  - Refactor PersistableBusinessObjectBase to have a public setPersistenceService() method.  Undesirable to
     *    expose that for every business object everywhere.
     *  - Mock out every business object used in this test.  This will make the test very brittle, especially if
     *    we want to include more complicated classes.
     *  - Adopt PowerMock, which lets you capture static method calls. This is my vote!
    @Test
    public void testSimpleBoReturned() throws Exception {
        EasyMock.expect(kualiModuleService.getInstalledModuleServices()).andReturn(getInstalledModuleServices());
        EasyMock.expect(moduleService.getModuleConfiguration()).andReturn(getModuleConfiguration());
        EasyMock.expect(moduleService.getModuleConfiguration()).andReturn(getModuleConfiguration());
        Map<String, String> queryCriteria = new HashMap<String, String>();
        queryCriteria.put(KRADPropertyConstants.OBJECT_ID, "12345");
        EasyMock.expect(businessObjectService.findMatching(UnitOfMeasure.class, queryCriteria)).andReturn(getUom());
        
        EasyMock.replay(kualiModuleService);
        EasyMock.replay(moduleService);
        EasyMock.replay(businessObjectService);
        ApiResource.setKualiModuleService(kualiModuleService);
        ApiResource.setBusinessObjectService(businessObjectService);
        
        Response response = apiResource.getSingleObject("sys", "unit-of-measures", "12345");
        EasyMock.verify(kualiModuleService);
        EasyMock.verify(moduleService);
        EasyMock.verify(businessObjectService);
        Assert.assertTrue("Should have returned OK", response.getStatus() == Status.OK.getStatusCode());
    }
    
    */

    private Collection<UnitOfMeasure> getUom() {
        List<UnitOfMeasure> result = new ArrayList<UnitOfMeasure>();
        UnitOfMeasure uom = new UnitOfMeasure();
        uom.setItemUnitOfMeasureCode("DEV");
        uom.setItemUnitOfMeasureDescription("Developer");
        result.add(uom);
        return result;
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
