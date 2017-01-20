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
        initializeConfigurationValues(new MockConfigurationValues().setAccountsCanCrossCharts(true));

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
        initializeConfigurationValues(new MockConfigurationValues().setAccountsCanCrossCharts(false));

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
        initializeConfigurationValues(new MockConfigurationValues().setContinuationAccountByPassOriginationCodes(Arrays.asList("02","03")).setContinuationAccountBypassDocumentTypeCodes(Arrays.asList("IB")));

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
        initializeConfigurationValues(new MockConfigurationValues().setAccountContractsAndGrantsDenotingValue(Arrays.asList("AA")).setAccountFundGroupDenotesContractsAndGrants(true));

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
        initializeConfigurationValues(new MockConfigurationValues().setAnnualClosingDocumentType("ACLO"));

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
        initializeConfigurationValues(new MockConfigurationValues().setScrubberValidationDaysOffset("30"));

        resource.setAccountService(accountService);
        resource.setParameterService(parameterService);

        final Response response = resource.getGeneralLedgerConfiguration();

        EasyMock.verify(accountService, parameterService);
        assertValidResponse(response);
        final Map<String, Object> configuration = (Map<String, Object>)response.getEntity();
        Assert.assertTrue("Map should have key \"scrubberValidationDaysOffset\"", configuration.containsKey("scrubberValidationDaysOffset"));
        Assert.assertEquals("The value of \"scrubberValidationDaysOffset\" should be \"30\"","30",configuration.get("scrubberValidationDaysOffset"));
    }

    @Test
    public void testObjectTypesBypassOriginationCodes() {
        initializeConfigurationValues(new MockConfigurationValues().setObjectTypeBypassOriginations(Arrays.asList("02", "KL")));

        resource.setAccountService(accountService);
        resource.setParameterService(parameterService);

        final Response response = resource.getGeneralLedgerConfiguration();

        EasyMock.verify(accountService, parameterService);
        assertValidResponse(response);
        final Map<String, Object> configuration = (Map<String, Object>)response.getEntity();
        Assert.assertTrue("Map should have key \"objectTypeBypassOriginations\"", configuration.containsKey("objectTypeBypassOriginations"));
        Assert.assertEquals("The value of \"objectTypeBypassOriginations\" should have size of 2",2, ((List<String>)configuration.get("objectTypeBypassOriginations")).size());
        Assert.assertEquals("The first value of \"objectTypeBypassOriginations\" should be \"02\"", "02", ((List<String>)configuration.get("objectTypeBypassOriginations")).get(0));
    }

    private void assertValidResponse(Response response) {
        Assert.assertNotNull("We should have a response returned", response);
        Assert.assertEquals("The status should be 200", 200, response.getStatus());
        Assert.assertNotNull("The response should have an entity", response.getEntity());
        Assert.assertTrue("The entity should be a Map", Map.class.isAssignableFrom(response.getEntity().getClass()));
    }

    private void initializeConfigurationValues(MockConfigurationValues mockConfigurationValues) {
        EasyMock.expect(accountService.accountsCanCrossCharts()).andReturn(mockConfigurationValues.isAccountsCanCrossCharts());
        EasyMock.expect(parameterService.getParameterValuesAsString(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.CONTINUATION_ACCOUNT_BYPASS_ORIGINATION_CODES)).andReturn(mockConfigurationValues.getContinuationAccountByPassOriginationCodes());
        EasyMock.expect(parameterService.getParameterValuesAsString(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.CONTINUATION_ACCOUNT_BYPASS_DOCUMENT_TYPE_CODES)).andReturn(mockConfigurationValues.getContinuationAccountBypassDocumentTypeCodes());
        EasyMock.expect(parameterService.getParameterValuesAsString(Account.class, KFSConstants.ChartApcParms.ACCOUNT_CG_DENOTING_VALUE)).andReturn(mockConfigurationValues.getAccountContractsAndGrantsDenotingValue());
        EasyMock.expect(parameterService.getParameterValueAsBoolean(Account.class, KFSConstants.ChartApcParms.ACCOUNT_FUND_GROUP_DENOTES_CG)).andReturn(mockConfigurationValues.isAccountFundGroupDenotesContractsAndGrants());
        EasyMock.expect(parameterService.getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, KFSConstants.SystemGroupParameterNames.GL_ANNUAL_CLOSING_DOC_TYPE)).andReturn(mockConfigurationValues.getAnnualClosingDocumentType());
        EasyMock.expect(parameterService.getParameterValueAsString(ScrubberStep.class, KFSConstants.SystemGroupParameterNames.GL_SCRUBBER_VALIDATION_DAYS_OFFSET)).andReturn(mockConfigurationValues.getScrubberValidationDaysOffset());
        EasyMock.expect(parameterService.getParameterValuesAsString(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.OBJECT_TYPE_BYPASS_ORIGINATIONS)).andReturn(mockConfigurationValues.getObjectTypeBypassOriginations());
        EasyMock.replay(accountService, parameterService);
    }

    private static class MockConfigurationValues {
        private boolean accountsCanCrossCharts;
        private List<String> continuationAccountByPassOriginationCodes;
        private List<String> continuationAccountBypassDocumentTypeCodes;
        private List<String> accountContractsAndGrantsDenotingValue;
        private boolean accountFundGroupDenotesContractsAndGrants;
        private String annualClosingDocumentType;
        private String scrubberValidationDaysOffset;
        private List<String> objectTypeBypassOriginations;

        private MockConfigurationValues() {
            accountsCanCrossCharts = true;
            continuationAccountByPassOriginationCodes = new ArrayList<>();
            continuationAccountBypassDocumentTypeCodes = new ArrayList<>();
            accountContractsAndGrantsDenotingValue = new ArrayList<>();
            accountFundGroupDenotesContractsAndGrants = false;
            annualClosingDocumentType = "";
            scrubberValidationDaysOffset = "";
            objectTypeBypassOriginations = new ArrayList<>();
        }

        public boolean isAccountsCanCrossCharts() {
            return accountsCanCrossCharts;
        }

        public List<String> getContinuationAccountByPassOriginationCodes() {
            return continuationAccountByPassOriginationCodes;
        }

        public List<String> getContinuationAccountBypassDocumentTypeCodes() {
            return continuationAccountBypassDocumentTypeCodes;
        }

        public List<String> getAccountContractsAndGrantsDenotingValue() {
            return accountContractsAndGrantsDenotingValue;
        }

        public boolean isAccountFundGroupDenotesContractsAndGrants() {
            return accountFundGroupDenotesContractsAndGrants;
        }

        public String getAnnualClosingDocumentType() {
            return annualClosingDocumentType;
        }

        public String getScrubberValidationDaysOffset() {
            return scrubberValidationDaysOffset;
        }

        public MockConfigurationValues setAccountsCanCrossCharts(boolean accountsCanCrossCharts) {
            this.accountsCanCrossCharts = accountsCanCrossCharts;
            return this;
        }

        public MockConfigurationValues setContinuationAccountByPassOriginationCodes(List<String> continuationAccountByPassOriginationCodes) {
            this.continuationAccountByPassOriginationCodes = continuationAccountByPassOriginationCodes;
            return this;
        }

        public MockConfigurationValues setContinuationAccountBypassDocumentTypeCodes(List<String> continuationAccountBypassDocumentTypeCodes) {
            this.continuationAccountBypassDocumentTypeCodes = continuationAccountBypassDocumentTypeCodes;
            return this;
        }

        public MockConfigurationValues setAccountContractsAndGrantsDenotingValue(List<String> accountContractsAndGrantsDenotingValue) {
            this.accountContractsAndGrantsDenotingValue = accountContractsAndGrantsDenotingValue;
            return this;
        }

        public MockConfigurationValues setAccountFundGroupDenotesContractsAndGrants(boolean accountFundGroupDenotesContractsAndGrants) {
            this.accountFundGroupDenotesContractsAndGrants = accountFundGroupDenotesContractsAndGrants;
            return this;
        }

        public MockConfigurationValues setAnnualClosingDocumentType(String annualClosingDocumentType) {
            this.annualClosingDocumentType = annualClosingDocumentType;
            return this;
        }

        public MockConfigurationValues setScrubberValidationDaysOffset(String scrubberValidationDaysOffset) {
            this.scrubberValidationDaysOffset = scrubberValidationDaysOffset;
            return this;
        }

        public List<String> getObjectTypeBypassOriginations() {
            return objectTypeBypassOriginations;
        }

        public MockConfigurationValues setObjectTypeBypassOriginations(List<String> objectTypeBypassOriginations) {
            this.objectTypeBypassOriginations = objectTypeBypassOriginations;
            return this;
        }
    }
}
