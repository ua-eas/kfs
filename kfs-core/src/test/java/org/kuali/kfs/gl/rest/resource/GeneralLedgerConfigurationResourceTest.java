package org.kuali.kfs.gl.rest.resource;

import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.ScrubberStep;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RunWith(EasyMockRunner.class)
public class GeneralLedgerConfigurationResourceTest {
    @TestSubject
    GeneralLedgerConfigurationResource resource = new GeneralLedgerConfigurationResource();

    @Mock
    AccountService accountService;

    @Mock
    ParameterService parameterService;

    @Test
    public void testGetGeneralLedgerConfiguration_AcccountsCanCrossChartsOn() {
        EasyMock.expect(accountService.accountsCanCrossCharts()).andReturn(true);
        EasyMock.expect(parameterService.getParameterValuesAsString(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.CONTINUATION_ACCOUNT_BYPASS_ORIGINATION_CODES)).andReturn(new ArrayList<>());
        EasyMock.expect(parameterService.getParameterValuesAsString(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.CONTINUATION_ACCOUNT_BYPASS_DOCUMENT_TYPE_CODES)).andReturn(new ArrayList<>());
        EasyMock.expect(parameterService.getParameterValuesAsString(Account.class, KFSConstants.ChartApcParms.ACCOUNT_CG_DENOTING_VALUE)).andReturn(new ArrayList<>());
        EasyMock.expect(parameterService.getParameterValueAsBoolean(Account.class, KFSConstants.ChartApcParms.ACCOUNT_FUND_GROUP_DENOTES_CG)).andReturn(false);
        EasyMock.expect(parameterService.getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, KFSConstants.SystemGroupParameterNames.GL_ANNUAL_CLOSING_DOC_TYPE)).andReturn("");
        EasyMock.expect(parameterService.getParameterValueAsString(ScrubberStep.class, KFSConstants.SystemGroupParameterNames.GL_SCRUBBER_VALIDATION_DAYS_OFFSET)).andReturn("");
        EasyMock.replay(accountService, parameterService);

        resource.setAccountService(accountService);
        resource.setParameterService(parameterService);

        final Response response = resource.getGeneralLedgerConfiguration();

        EasyMock.verify(accountService, parameterService);
        assertValidResponse(response);
        final Map<String, Object> configuration = (Map<String, Object>)response.getEntity();
        Assert.assertTrue("We should have a key for accountsCanCrossCharts", configuration.containsKey("accountsCanCrossCharts"));
        Assert.assertTrue("The value of accountsCanCrossCharts should be true", (boolean)configuration.get("accountsCanCrossCharts"));
    }

    @Test
    public void testGetGeneralLedgerConfiguration_AcccountsCanCrossChartsOff() {
        EasyMock.expect(accountService.accountsCanCrossCharts()).andReturn(false);
        EasyMock.expect(parameterService.getParameterValuesAsString(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.CONTINUATION_ACCOUNT_BYPASS_ORIGINATION_CODES)).andReturn(new ArrayList<>());
        EasyMock.expect(parameterService.getParameterValuesAsString(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.CONTINUATION_ACCOUNT_BYPASS_DOCUMENT_TYPE_CODES)).andReturn(new ArrayList<>());
        EasyMock.expect(parameterService.getParameterValuesAsString(Account.class, KFSConstants.ChartApcParms.ACCOUNT_CG_DENOTING_VALUE)).andReturn(new ArrayList<>());
        EasyMock.expect(parameterService.getParameterValueAsBoolean(Account.class, KFSConstants.ChartApcParms.ACCOUNT_FUND_GROUP_DENOTES_CG)).andReturn(false);
        EasyMock.expect(parameterService.getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, KFSConstants.SystemGroupParameterNames.GL_ANNUAL_CLOSING_DOC_TYPE)).andReturn("");
        EasyMock.expect(parameterService.getParameterValueAsString(ScrubberStep.class, KFSConstants.SystemGroupParameterNames.GL_SCRUBBER_VALIDATION_DAYS_OFFSET)).andReturn("");
        EasyMock.replay(accountService, parameterService);

        resource.setAccountService(accountService);
        resource.setParameterService(parameterService);

        final Response response = resource.getGeneralLedgerConfiguration();

        EasyMock.verify(accountService, parameterService);
        assertValidResponse(response);
        final Map<String, Object> configuration = (Map<String, Object>)response.getEntity();
        Assert.assertTrue("We should have a key for accountsCanCrossCharts", configuration.containsKey("accountsCanCrossCharts"));
        Assert.assertFalse("The value of accountsCanCrossCharts should be true", (boolean)configuration.get("accountsCanCrossCharts"));
    }

    @Test
    public void testContinuationAccountValues() {
        EasyMock.expect(accountService.accountsCanCrossCharts()).andReturn(false);
        EasyMock.expect(parameterService.getParameterValuesAsString(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.CONTINUATION_ACCOUNT_BYPASS_ORIGINATION_CODES)).andReturn(Arrays.asList("02","03"));
        EasyMock.expect(parameterService.getParameterValuesAsString(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.CONTINUATION_ACCOUNT_BYPASS_DOCUMENT_TYPE_CODES)).andReturn(Arrays.asList("IB")); // no more IB
        EasyMock.expect(parameterService.getParameterValuesAsString(Account.class, KFSConstants.ChartApcParms.ACCOUNT_CG_DENOTING_VALUE)).andReturn(new ArrayList<>());
        EasyMock.expect(parameterService.getParameterValueAsBoolean(Account.class, KFSConstants.ChartApcParms.ACCOUNT_FUND_GROUP_DENOTES_CG)).andReturn(false);
        EasyMock.expect(parameterService.getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, KFSConstants.SystemGroupParameterNames.GL_ANNUAL_CLOSING_DOC_TYPE)).andReturn("");
        EasyMock.expect(parameterService.getParameterValueAsString(ScrubberStep.class, KFSConstants.SystemGroupParameterNames.GL_SCRUBBER_VALIDATION_DAYS_OFFSET)).andReturn("");
        EasyMock.replay(accountService, parameterService);

        resource.setAccountService(accountService);
        resource.setParameterService(parameterService);

        final Response response = resource.getGeneralLedgerConfiguration();

        EasyMock.verify(accountService, parameterService);
        assertValidResponse(response);
        final Map<String, Object> configuration = (Map<String, Object>)response.getEntity();
        Assert.assertTrue("Configuration should have key for \"continuationAccountBypassBalanceTypeCodes\"",configuration.containsKey("continuationAccountBypassBalanceTypeCodes"));
        Assert.assertEquals("There should be 3 continuationAccountBypassBalanceTypeCodes", 3, ((List<String>)configuration.get("continuationAccountBypassBalanceTypeCodes")).size());
        Assert.assertEquals("The first continuationAccountBypassBalanceTypeCodes should be \"EX\"", "EX", ((List<String>)configuration.get("continuationAccountBypassBalanceTypeCodes")).get(0));
        Assert.assertTrue("Configuration should have key for \"continuationAccountBypassOriginationCodes\"",configuration.containsKey("continuationAccountBypassOriginationCodes"));
        Assert.assertEquals("There should be 2 continuationAccountBypassOriginationCodes", 2, ((List<String>)configuration.get("continuationAccountBypassOriginationCodes")).size());
        Assert.assertEquals("The first continuationAccountBypassOriginationCodes should be \"02\"", "02", ((List<String>)configuration.get("continuationAccountBypassOriginationCodes")).get(0));
        Assert.assertTrue("Configuration should have key for \"continuationAccountBypassDocumentTypeCodes\"",configuration.containsKey("continuationAccountBypassDocumentTypeCodes"));
        Assert.assertEquals("There should be 1 continuationAccountBypassDocumentTypeCodes", 1, ((List<String>)configuration.get("continuationAccountBypassDocumentTypeCodes")).size());
        Assert.assertEquals("The single continuationAccountBypassDocumentTypeCodes should be \"IB\"", "IB", ((List<String>)configuration.get("continuationAccountBypassDocumentTypeCodes")).get(0));
    }

    @Test
    public void testAccountContractsAndGrantsValues() {
        EasyMock.expect(accountService.accountsCanCrossCharts()).andReturn(false);
        EasyMock.expect(parameterService.getParameterValuesAsString(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.CONTINUATION_ACCOUNT_BYPASS_ORIGINATION_CODES)).andReturn(Arrays.asList("02","03"));
        EasyMock.expect(parameterService.getParameterValuesAsString(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.CONTINUATION_ACCOUNT_BYPASS_DOCUMENT_TYPE_CODES)).andReturn(Arrays.asList("IB")); // no more IB
        EasyMock.expect(parameterService.getParameterValuesAsString(Account.class, KFSConstants.ChartApcParms.ACCOUNT_CG_DENOTING_VALUE)).andReturn(Arrays.asList("AA"));
        EasyMock.expect(parameterService.getParameterValueAsBoolean(Account.class, KFSConstants.ChartApcParms.ACCOUNT_FUND_GROUP_DENOTES_CG)).andReturn(true);
        EasyMock.expect(parameterService.getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, KFSConstants.SystemGroupParameterNames.GL_ANNUAL_CLOSING_DOC_TYPE)).andReturn("");
        EasyMock.expect(parameterService.getParameterValueAsString(ScrubberStep.class, KFSConstants.SystemGroupParameterNames.GL_SCRUBBER_VALIDATION_DAYS_OFFSET)).andReturn("");
        EasyMock.replay(accountService, parameterService);

        resource.setAccountService(accountService);
        resource.setParameterService(parameterService);

        final Response response = resource.getGeneralLedgerConfiguration();

        EasyMock.verify(accountService, parameterService);
        assertValidResponse(response);
        final Map<String, Object> configuration = (Map<String, Object>)response.getEntity();
        Assert.assertTrue("Map should have key \"fundGroupDenotesContractsandGrants\"", configuration.containsKey("fundGroupDenotesContractsandGrants"));
        Assert.assertTrue("fundGroupDenotesContractsAndGrants should be true", (Boolean)configuration.get("fundGroupDenotesContractsandGrants"));
        Assert.assertTrue("Map should have key \"contractsAndGrantsDenotingValues\"", configuration.containsKey("contractsAndGrantsDenotingValues"));
        Assert.assertEquals("contractsAndGrantsDenotingValues should have a size of 1", 1, ((List<String>)configuration.get("contractsAndGrantsDenotingValues")).size());
        Assert.assertEquals("The single value of contractsAndGrantsDenotingValues should be \"AA\"", "AA", ((List<String>)configuration.get("contractsAndGrantsDenotingValues")).get(0));
    }

    @Test
    public void testAccountingClosingDocumentType() {
        EasyMock.expect(accountService.accountsCanCrossCharts()).andReturn(false);
        EasyMock.expect(parameterService.getParameterValuesAsString(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.CONTINUATION_ACCOUNT_BYPASS_ORIGINATION_CODES)).andReturn(Arrays.asList("02","03"));
        EasyMock.expect(parameterService.getParameterValuesAsString(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.CONTINUATION_ACCOUNT_BYPASS_DOCUMENT_TYPE_CODES)).andReturn(Arrays.asList("IB")); // no more IB
        EasyMock.expect(parameterService.getParameterValuesAsString(Account.class, KFSConstants.ChartApcParms.ACCOUNT_CG_DENOTING_VALUE)).andReturn(new ArrayList<>());
        EasyMock.expect(parameterService.getParameterValueAsBoolean(Account.class, KFSConstants.ChartApcParms.ACCOUNT_FUND_GROUP_DENOTES_CG)).andReturn(false);
        EasyMock.expect(parameterService.getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, KFSConstants.SystemGroupParameterNames.GL_ANNUAL_CLOSING_DOC_TYPE)).andReturn("ACLO");
        EasyMock.expect(parameterService.getParameterValueAsString(ScrubberStep.class, KFSConstants.SystemGroupParameterNames.GL_SCRUBBER_VALIDATION_DAYS_OFFSET)).andReturn("");
        EasyMock.replay(accountService, parameterService);

        resource.setAccountService(accountService);
        resource.setParameterService(parameterService);

        final Response response = resource.getGeneralLedgerConfiguration();

        EasyMock.verify(accountService, parameterService);
        assertValidResponse(response);
        final Map<String, Object> configuration = (Map<String, Object>)response.getEntity();
        Assert.assertTrue("Map should have key \"annualClosingDocumentType\"", configuration.containsKey("annualClosingDocumentType"));
        Assert.assertEquals("The value of \"annualClosingDocumentType\" should be \"ACLO\"","ACLO",configuration.get("annualClosingDocumentType"));
    }

    @Test
    public void testScrubberValidationDaysOffset() {
        EasyMock.expect(accountService.accountsCanCrossCharts()).andReturn(false);
        EasyMock.expect(parameterService.getParameterValuesAsString(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.CONTINUATION_ACCOUNT_BYPASS_ORIGINATION_CODES)).andReturn(Arrays.asList("02","03"));
        EasyMock.expect(parameterService.getParameterValuesAsString(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.CONTINUATION_ACCOUNT_BYPASS_DOCUMENT_TYPE_CODES)).andReturn(Arrays.asList("IB")); // no more IB
        EasyMock.expect(parameterService.getParameterValuesAsString(Account.class, KFSConstants.ChartApcParms.ACCOUNT_CG_DENOTING_VALUE)).andReturn(new ArrayList<>());
        EasyMock.expect(parameterService.getParameterValueAsBoolean(Account.class, KFSConstants.ChartApcParms.ACCOUNT_FUND_GROUP_DENOTES_CG)).andReturn(false);
        EasyMock.expect(parameterService.getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, KFSConstants.SystemGroupParameterNames.GL_ANNUAL_CLOSING_DOC_TYPE)).andReturn("");
        EasyMock.expect(parameterService.getParameterValueAsString(ScrubberStep.class, KFSConstants.SystemGroupParameterNames.GL_SCRUBBER_VALIDATION_DAYS_OFFSET)).andReturn("30");
        EasyMock.replay(accountService, parameterService);

        resource.setAccountService(accountService);
        resource.setParameterService(parameterService);

        final Response response = resource.getGeneralLedgerConfiguration();

        EasyMock.verify(accountService, parameterService);
        assertValidResponse(response);
        final Map<String, Object> configuration = (Map<String, Object>)response.getEntity();
        Assert.assertTrue("Map should have key \"scrubberValidationDaysOffset\"", configuration.containsKey("scrubberValidationDaysOffset"));
        Assert.assertEquals("The value of \"scrubberValidationDaysOffset\" should be \"30\"","30",configuration.get("scrubberValidationDaysOffset"));
    }

    private void assertValidResponse(Response response) {
        Assert.assertNotNull("We should have a response returned", response);
        Assert.assertEquals("The status should be 200", 200, response.getStatus());
        Assert.assertNotNull("The response should have an entity", response.getEntity());
        Assert.assertTrue("The entity should be a Map", Map.class.isAssignableFrom(response.getEntity().getClass()));
    }
}