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
package org.kuali.kfs.gl.batch.service.impl;

import java.math.BigDecimal;

import org.apache.log4j.BasicConfigurator;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryAccount;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;

public class IndirectCostRecoveryAccountDistributionMetadataTest {


    private static final String CONT1_ACCT_NUMBER = "cont_acct_number";
    private static final String CONT1_COA_CODE = "cont_coa_code";
    private static final String CONT2_ACCT_NUMBER = "cont2_acct_number";
    private static final String CONT2_COA_CODE = "cont2_coa_code";
    private static final BigDecimal IDCR_ACCOUNT_LINE_PERCENT = new BigDecimal(50);
    private static final String IDCR_ACCOUNT_NUMBER = "accountNumber";
    private static final String IDCR_FIN_COA_CODE = "finCoaCode";
    private static final String CONTINUATION_CLOSED_ACCOUNT_ERROR_STRING = "{0}-{1} is closed and a search of {2} levels of continuation accounts did not result in an open account to use for Indirect Cost Recovery.";

    @Before
    public void setUp() throws Exception {
        BasicConfigurator.configure();
    }

    @Test
    public void testIndirectCostRecoveryAccountDistributionMetadataNoICR() {
        IndirectCostRecoveryAccount account = buildIndirectCostRecoveryAccountBase();
        IndirectCostRecoveryAccountDistributionMetadata meta = new IndirectCostRecoveryAccountDistributionMetadata(account);
        assertIndirectCostValuesCorrrect(meta, IDCR_FIN_COA_CODE, IDCR_ACCOUNT_NUMBER);
    }

    private void assertIndirectCostValuesCorrrect(IndirectCostRecoveryAccountDistributionMetadata indirectCostRecoveryAccountDistributionMetadata,
            String expectedFinCoaCode, String expectedAccountNumber) {
        Assert.assertEquals(expectedFinCoaCode, indirectCostRecoveryAccountDistributionMetadata.getIndirectCostRecoveryFinCoaCode());
        Assert.assertEquals(expectedAccountNumber, indirectCostRecoveryAccountDistributionMetadata.getIndirectCostRecoveryAccountNumber());
        Assert.assertEquals(IDCR_ACCOUNT_LINE_PERCENT, indirectCostRecoveryAccountDistributionMetadata.getAccountLinePercent());
    }

    @Test
    public void testIndirectCostRecoveryAccountDistributionMetadataOpenICRAccount() {
        IndirectCostRecoveryAccount account = buildIndirectCostRecoveryAccountOpen();
        IndirectCostRecoveryAccountDistributionMetadata meta = new IndirectCostRecoveryAccountDistributionMetadata(account);
        assertIndirectCostValuesCorrrect(meta, IDCR_FIN_COA_CODE, IDCR_ACCOUNT_NUMBER);
    }

    @Test
    public void testIndirectCostRecoveryAccountDistributionMetadataClosedICRAccount() {
        IndirectCostRecoveryAccount account = buildIndirectCostRecoveryAccountClosed();
        IndirectCostRecoveryAccountDistributionMetadata meta = new MockedIndirectCostRecoveryAccountDistributionMetadata(account);
        assertIndirectCostValuesCorrrect(meta, IDCR_FIN_COA_CODE, IDCR_ACCOUNT_NUMBER);
    }

    @Test
    public void testIndirectCostRecoveryAccountDistributionMetadataICRAccountWithContinuation() {
        IndirectCostRecoveryAccount account = buildIndirectCostRecoveryAccountWithContinuation();
        IndirectCostRecoveryAccountDistributionMetadata meta = new MockedIndirectCostRecoveryAccountDistributionMetadata(account);
        assertIndirectCostValuesCorrrect(meta, CONT1_COA_CODE, CONT1_ACCT_NUMBER);
    }

    @Test
    public void testIndirectCostRecoveryAccountDistributionMetadataICRAccountWithContinuation2() {
        IndirectCostRecoveryAccount account = buildIndirectCostRecoveryAccountWithContinuation2();
        IndirectCostRecoveryAccountDistributionMetadata meta = new MockedIndirectCostRecoveryAccountDistributionMetadata(account);
        assertIndirectCostValuesCorrrect(meta, CONT2_COA_CODE, CONT2_ACCT_NUMBER);
    }

    @Test
    public void testIndirectCostRecoveryAccountDistributionMetadataICRAccountWithContinuationLastClosed() {
        IndirectCostRecoveryAccount account = buildIndirectCostRecoveryAccountWithContinuation2();
        account.getIndirectCostRecoveryAccount().getContinuationAccount().getContinuationAccount().setClosed(true);
        IndirectCostRecoveryAccountDistributionMetadata meta = new MockedIndirectCostRecoveryAccountDistributionMetadata(account);
        assertIndirectCostValuesCorrrect(meta, IDCR_FIN_COA_CODE, IDCR_ACCOUNT_NUMBER);
    }

    @Test
    public void testIndirectCostRecoveryAccountDistributionMetadataICRAccountWithContinuation10LastClosed() {
        IndirectCostRecoveryAccount account = buildIndirectCostRecoveryAccountWithContinuation10Closed();

        try {
            IndirectCostRecoveryAccountDistributionMetadata meta = new MockedIndirectCostRecoveryAccountDistributionMetadata(account);
        } catch (UnsupportedOperationException uoe) {
            Assert.assertTrue("We got the error we expected to", true);
            return;
        }
        Assert.assertTrue("We should have gotten an UnsupportedOperationException exception", false);
    }

    private IndirectCostRecoveryAccount buildIndirectCostRecoveryAccountBase() {
        IndirectCostRecoveryAccount account = new IndirectCostRecoveryAccount();
        account.setIndirectCostRecoveryFinCoaCode(IDCR_FIN_COA_CODE);
        account.setIndirectCostRecoveryAccountNumber(IDCR_ACCOUNT_NUMBER);
        account.setAccountLinePercent(IDCR_ACCOUNT_LINE_PERCENT);

        return account;
    }

    private IndirectCostRecoveryAccount buildIndirectCostRecoveryAccountOpen() {
        IndirectCostRecoveryAccount account = buildIndirectCostRecoveryAccountBase();
        Account indirectCostRecoveryAccount = new Account();
        indirectCostRecoveryAccount.setClosed(false);
        account.setIndirectCostRecoveryAccount(indirectCostRecoveryAccount);
        return account;
    }

    private IndirectCostRecoveryAccount buildIndirectCostRecoveryAccountClosed() {
        IndirectCostRecoveryAccount account = buildIndirectCostRecoveryAccountOpen();
        account.getIndirectCostRecoveryAccount().setClosed(true);
        return account;
    }

    private IndirectCostRecoveryAccount buildIndirectCostRecoveryAccountWithContinuation() {
        IndirectCostRecoveryAccount account = buildIndirectCostRecoveryAccountClosed();
        Account continuationAccount = builtContinuationAccount(CONT1_COA_CODE, CONT1_ACCT_NUMBER, false);
        account.getIndirectCostRecoveryAccount().setContinuationAccount(continuationAccount);
        return account;
    }

    private Account builtContinuationAccount(String coaCode, String accountNumber, boolean isClosed) {
        Account continuationAccount = new Account();
        continuationAccount.setClosed(false);;
        continuationAccount.setChartOfAccountsCode(coaCode);
        continuationAccount.setAccountNumber(accountNumber);
        continuationAccount.setClosed(isClosed);
        return continuationAccount;
    }

    private IndirectCostRecoveryAccount buildIndirectCostRecoveryAccountWithContinuation2() {
        IndirectCostRecoveryAccount account = buildIndirectCostRecoveryAccountWithContinuation();
        account.getIndirectCostRecoveryAccount().getContinuationAccount().setClosed(true);
        Account continuationAccount = builtContinuationAccount(CONT2_COA_CODE, CONT2_ACCT_NUMBER, false);
        account.getIndirectCostRecoveryAccount().getContinuationAccount().setContinuationAccount(continuationAccount);
        return account;
    }

    private IndirectCostRecoveryAccount buildIndirectCostRecoveryAccountWithContinuation10Closed() {
        IndirectCostRecoveryAccount account = buildIndirectCostRecoveryAccountWithContinuation();
        account.getIndirectCostRecoveryAccount().getContinuationAccount().setClosed(true);

        int i = 0;
        Account continuationAccount = account.getIndirectCostRecoveryAccount().getContinuationAccount();

        while (i < 10) {
            Account newContinuationAccount = builtContinuationAccount(CONT2_COA_CODE+i, CONT2_ACCT_NUMBER+i, true);
            continuationAccount.setContinuationAccount(newContinuationAccount);
            continuationAccount = newContinuationAccount;
            i++;
        }
        return account;
    }

    private class MockedIndirectCostRecoveryAccountDistributionMetadata extends IndirectCostRecoveryAccountDistributionMetadata {

        public MockedIndirectCostRecoveryAccountDistributionMetadata(IndirectCostRecoveryAccount icrAccount) {
            super(icrAccount);
        }

        @Override
        public ConfigurationService getConfigurationService() {
            ConfigurationService configurationService = EasyMock.createMock(ConfigurationService.class);
            EasyMock.expect(configurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_ICRACCOUNT_CONTINUATION_ACCOUNT_CLOSED)).andReturn(CONTINUATION_CLOSED_ACCOUNT_ERROR_STRING);
            EasyMock.replay(configurationService);
            return configurationService;
        }

    }

}
