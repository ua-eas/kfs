package org.kuali.kfs.module.external.kc.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.easymock.EasyMock;
import org.eclipse.persistence.jpa.jpql.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.integration.ar.AccountsReceivableBillingFrequency;
import org.kuali.kfs.integration.ar.businessobject.BillingFrequency;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.module.external.kc.dto.BillingFrequencyDTO;

public class BillingFrequencyServiceImplTest {
    
    private BillingFrequencyServiceImpl billingFrequencyService;
    private ModuleService responsibleModuleService;

    @Before
    public void setup() {
        billingFrequencyService = new BillingFrequencyServiceImpl();
        responsibleModuleService = EasyMock.createMock(ModuleService.class);
    }
    
    @Test
    public void testGetAll() {
        Map<String, Object> criteria = new HashMap<String, Object>();
        EasyMock.expect(responsibleModuleService.getExternalizableBusinessObjectsListForLookup(AccountsReceivableBillingFrequency.class, criteria, true))
            .andReturn(getSampleBillingFrequencyList());
        EasyMock.replay(responsibleModuleService);
        
        billingFrequencyService.setResponsibleModuleService(responsibleModuleService);
        List<BillingFrequencyDTO> results = billingFrequencyService.getAll();
        
        EasyMock.verify(responsibleModuleService);
        if (results.size() != 1) {
            Assert.fail("billingFrequencyService.getAll() should have returned 1 result; instead it returned " + results.size());
            return;
        }
        
        BillingFrequencyDTO result = results.get(0);
        
        BillingFrequency expected = createSampleBillingFrequency();
        Assert.isTrue(compareBillingFrequencies(result, expected),
                "Expected result was " + ToStringBuilder.reflectionToString(expected) + 
                " but got " + ToStringBuilder.reflectionToString(result));        
    }
    
    @Test
    public void testGetActive() {
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put("active", "Y");
        EasyMock.expect(responsibleModuleService.getExternalizableBusinessObjectsListForLookup(AccountsReceivableBillingFrequency.class, criteria, true))
            .andReturn(getSampleBillingFrequencyList());
        EasyMock.replay(responsibleModuleService);
        
        billingFrequencyService.setResponsibleModuleService(responsibleModuleService);
        List<BillingFrequencyDTO> results = billingFrequencyService.getActive();
        
        EasyMock.verify(responsibleModuleService);
        if (results.size() != 1) {
            Assert.fail("billingFrequencyService.getActive() should have returned 1 result; instead it returned " + results.size());
            return;
        }
        
        BillingFrequencyDTO result = results.get(0);
        
        BillingFrequency expected = createSampleBillingFrequency();
        Assert.isTrue(compareBillingFrequencies(result, expected),
                "Expected result was " + ToStringBuilder.reflectionToString(expected) + 
                " but got " + ToStringBuilder.reflectionToString(result));       
    }  
    
    @Test
    public void testGetBillingFrequency() {
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put("frequency", "Sample");
        EasyMock.expect(responsibleModuleService.getExternalizableBusinessObject(AccountsReceivableBillingFrequency.class, criteria))
            .andReturn(createSampleBillingFrequency());
        EasyMock.replay(responsibleModuleService);
        
        billingFrequencyService.setResponsibleModuleService(responsibleModuleService);
        BillingFrequencyDTO result = billingFrequencyService.getBillingFrequency("Sample");
        
        BillingFrequency expected = createSampleBillingFrequency();
        Assert.isTrue(compareBillingFrequencies(result, expected),
                "Expected result was " + ToStringBuilder.reflectionToString(expected) + 
                " but got " + ToStringBuilder.reflectionToString(result));  
    }

    private List<AccountsReceivableBillingFrequency> getSampleBillingFrequencyList() {
        List<AccountsReceivableBillingFrequency> results = new ArrayList<AccountsReceivableBillingFrequency>();
        
        BillingFrequency billingFrequency = createSampleBillingFrequency();
        results.add(billingFrequency);     
        
        return results;
    }

    private BillingFrequency createSampleBillingFrequency() {
        BillingFrequency billingFrequency = new BillingFrequency();
        billingFrequency.setActive(true);
        billingFrequency.setFrequency("Sample");
        billingFrequency.setFrequencyDescription("Sample Billing Frequency");
        billingFrequency.setGracePeriodDays(3);
        return billingFrequency;
    }
    
    private boolean compareBillingFrequencies(BillingFrequencyDTO result, BillingFrequency expected) {
        return expected.isActive() == result.isActive() &&
                expected.getFrequency().equals(result.getFrequency()) &&
                expected.getFrequencyDescription().equals(result.getFrequencyDescription()) &&
                expected.getGracePeriodDays().equals(result.getGracePeriodDays());
    }
}
