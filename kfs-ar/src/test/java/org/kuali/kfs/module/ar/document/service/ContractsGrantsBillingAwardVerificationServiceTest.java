package org.kuali.kfs.module.ar.document.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.OrganizationAccountingDefault;
import org.kuali.kfs.module.ar.businessobject.SystemInformation;
import org.kuali.kfs.module.ar.document.service.impl.ContractsGrantsBillingAwardVerificationServiceImpl;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.AwardOrganization;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.service.UniversityDateService;

public class ContractsGrantsBillingAwardVerificationServiceTest {
    
    private ContractsGrantsBillingAwardVerificationServiceImpl contractsGrantsBillingAwardVerificationService;
    private BusinessObjectService businessObjectService;
    private ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    private UniversityDateService universityDateService;

    @Before
    public void setup() {
        contractsGrantsBillingAwardVerificationService = new ContractsGrantsBillingAwardVerificationServiceImpl();
        businessObjectService = EasyMock.createMock(BusinessObjectService.class);
        contractsGrantsBillingAwardVerificationService.setBusinessObjectService(businessObjectService);
        contractsGrantsInvoiceDocumentService = EasyMock.createMock(ContractsGrantsInvoiceDocumentService.class);
        contractsGrantsBillingAwardVerificationService.setContractsGrantsInvoiceDocumentService(contractsGrantsInvoiceDocumentService);
        universityDateService = EasyMock.createMock(UniversityDateService.class);
        contractsGrantsBillingAwardVerificationService.setUniversityDateService(universityDateService);
    }
    
    private void replayAll() {
        EasyMock.replay(businessObjectService, contractsGrantsInvoiceDocumentService, universityDateService);
    }
    
    private void executeIsChartAndOrgSetupForInvoicingTest(boolean hasPrimaryAwardOrganization, boolean hasProcessing, 
            boolean hasOrganizationAccountingDefault, boolean hasSystemInformation, boolean expectedResult) {
        Award award = new Award();
        
        if (hasPrimaryAwardOrganization) {
            AwardOrganization primaryAwardOrganization = new AwardOrganization();
            primaryAwardOrganization.setChartOfAccountsCode("C1");
            primaryAwardOrganization.setOrganizationCode("11111");
            award.setPrimaryAwardOrganization(primaryAwardOrganization );
        }
        
        EasyMock.expect(universityDateService.getCurrentFiscalYear()).andReturn(2015);
        
        if (hasProcessing) {
            EasyMock.expect(contractsGrantsInvoiceDocumentService.getProcessingFromBillingCodes("C1", "11111"))
                .andReturn(Arrays.asList("C2", "22222"));
        }
        else {
            EasyMock.expect(contractsGrantsInvoiceDocumentService.getProcessingFromBillingCodes("C1", "11111"))
                .andReturn(new ArrayList<>());
        }
        
        
        Map<String, Object> criteria = new HashMap<>();
        criteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, 2015);
        criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, "C1");
        criteria.put(KFSPropertyConstants.ORGANIZATION_CODE, "11111");
        if (hasOrganizationAccountingDefault) {
            EasyMock.expect(businessObjectService.findByPrimaryKey(OrganizationAccountingDefault.class, criteria))
                .andReturn(new OrganizationAccountingDefault());
        }
        else {
            EasyMock.expect(businessObjectService.findByPrimaryKey(OrganizationAccountingDefault.class, criteria))
                .andReturn(null);
        }
        
        Map<String, Object> sysCriteria = new HashMap<>();
        sysCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, 2015);        
        sysCriteria.put(ArPropertyConstants.OrganizationAccountingDefaultFields.PROCESSING_CHART_OF_ACCOUNTS_CODE, "C2");
        sysCriteria.put(ArPropertyConstants.OrganizationAccountingDefaultFields.PROCESSING_ORGANIZATION_CODE, "22222");
        if (hasSystemInformation) {
            EasyMock.expect(businessObjectService.findByPrimaryKey(SystemInformation.class, sysCriteria))
                .andReturn(new SystemInformation());
        }
        else {
            EasyMock.expect(businessObjectService.findByPrimaryKey(SystemInformation.class, sysCriteria))
                .andReturn(null);            
        }

        replayAll();
        boolean result = contractsGrantsBillingAwardVerificationService.isChartAndOrgSetupForInvoicing(award);
        Assert.assertTrue("Method should return " + expectedResult, result == expectedResult);
    }
    
    @Test 
    public void testChartAndOrgValid() {
        executeIsChartAndOrgSetupForInvoicingTest(true, true, true, true, true);
    }
    
    @Test
    public void testNoPrimaryAwardOrganization() {
        executeIsChartAndOrgSetupForInvoicingTest(false, true, true, true, false);
    }
    
    @Test
    public void testNoProcessingFromBillingCodes() {
        executeIsChartAndOrgSetupForInvoicingTest(true, false, true, true, false);
    }
    
    @Test
    public void testNoOrganizationAccountingDefault() {
        executeIsChartAndOrgSetupForInvoicingTest(true, true, false, true, false);
    }
    
    @Test
    public void testNoSystemInformation() {
        executeIsChartAndOrgSetupForInvoicingTest(true, true, true, false, false);
    }
}
