/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-${YEAR} The Kuali Foundation
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

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.fixture.AccountFixture;
import org.kuali.rice.core.api.config.property.ConfigurationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PosterServiceImplTest {

    private PosterServiceImpl posterService;

    @Before
    public void setUp() {
        posterService = new PosterServiceImpl();
        posterService.setAccountingCycleCachingService(new MockAccountingCycleCachingServiceImpl());
        posterService.setConfigurationService(new MockConfigurationServiceImpl());
    }

    @Test
    public void getAccountWithPotentialContinuationAccountOpen() throws Exception {
        Account originalAccount = AccountFixture.ACTIVE_ACCOUNT2.createAccount();
        Account expectedAccount = originalAccount;

        validateGetAccountWithPotentialContinuation(originalAccount, expectedAccount, null);
    }

    @Test
    public void getAccountWithPotentialContinuationAccountClosedNoContinuation() throws Exception {
        Account originalAccount = AccountFixture.CLOSED_ACCOUNT_NO_CONTINUATION.createAccount();
        Account expectedAccount = originalAccount;
        String expectedMessage = originalAccount.getChartOfAccountsCode() + "-" + originalAccount.getAccountNumber() + " is closed and a search of 10 levels of continuation accounts did not result in an open account to use for Indirect Cost Recovery.";

        validateGetAccountWithPotentialContinuation(originalAccount, expectedAccount, expectedMessage);
    }

    @Test
    public void getAccountWithPotentialContinuationAccountClosedContinuationOpen() throws Exception {
        Account originalAccount = AccountFixture.CLOSED_ACCOUNT_OPEN_CONTINUATION.createAccount();
        Account expectedAccount = AccountFixture.ACTIVE_CONTINUATION_ACCOUNT.createAccount();
        String expectedMessage = originalAccount.getChartOfAccountsCode() + "-" + originalAccount.getAccountNumber() + " is closed, so the Indirect Cost Recovery will use continuation account " + expectedAccount.getChartOfAccountsCode() + "-" + expectedAccount.getAccountNumber() + " instead.";

        validateGetAccountWithPotentialContinuation(originalAccount, expectedAccount, expectedMessage);
    }

    @Test
    public void getAccountWithPotentialContinuationAccountClosedContinuationClosedContinuationOpen() throws Exception {
        Account originalAccount = AccountFixture.CLOSED_ACCOUNT_CLOSED_CONTINUATION_OPEN_CONTINUATION.createAccount();
        Account expectedAccount = AccountFixture.ACTIVE_CONTINUATION_ACCOUNT.createAccount();
        String expectedMessage = originalAccount.getChartOfAccountsCode() + "-" + originalAccount.getAccountNumber() + " is closed, so the Indirect Cost Recovery will use continuation account " + expectedAccount.getChartOfAccountsCode() + "-" + expectedAccount.getAccountNumber() + " instead.";

        validateGetAccountWithPotentialContinuation(originalAccount, expectedAccount, expectedMessage);
    }

    @Test
    public void getAccountWithPotentialContinuationAccountClosedContinuationClosed() throws Exception {
        Account originalAccount = AccountFixture.CLOSED_ACCOUNT_CLOSED_CONTINUATION.createAccount();
        Account expectedAccount = originalAccount;
        String expectedMessage = originalAccount.getChartOfAccountsCode() + "-" + originalAccount.getAccountNumber() + " is closed and a search of 10 levels of continuation accounts did not result in an open account to use for Indirect Cost Recovery.";

        validateGetAccountWithPotentialContinuation(originalAccount, expectedAccount, expectedMessage);
    }

    private void validateGetAccountWithPotentialContinuation(Account originalAccount, Account expectedAccount, String expectedErrorMessage) {
        OriginEntryFull transaction = buildTransaction(originalAccount);
        List<Message> errors = new ArrayList<>();

        Account returnedAccount = posterService.getAccountWithPotentialContinuation(transaction, errors);

        validateReturnedAccount(expectedAccount, returnedAccount);
        validateErrors(expectedErrorMessage, errors);
    }

    private OriginEntryFull buildTransaction(Account account) {
        OriginEntryFull tran = new OriginEntryFull();
        tran.setChartOfAccountsCode(account.getChartOfAccountsCode());
        tran.setAccountNumber(account.getAccountNumber());
        tran.setAccount(account);
        return tran;
    }

    private void validateReturnedAccount(Account expectedAccount, Account returnedAccount) {
        Assert.assertEquals(expectedAccount.getChartOfAccountsCode(), returnedAccount.getChartOfAccountsCode());
        Assert.assertEquals(expectedAccount.getAccountNumber(), returnedAccount.getAccountNumber());
    }

    private void validateErrors(String expectedErrorMessage, List<Message> errors) {
        if (StringUtils.isBlank(expectedErrorMessage)) {
            Assert.assertTrue(errors.isEmpty());
        } else {
            Assert.assertFalse(errors.isEmpty());
            Assert.assertEquals(expectedErrorMessage, errors.get(0).getMessage());
        }
    }

    private class MockAccountingCycleCachingServiceImpl extends AccountingCycleCachingServiceImpl {

        private Map<String, Account> accounts = new HashMap<>();

        MockAccountingCycleCachingServiceImpl() {
            Account closedAccountClosedContinuationClosed = AccountFixture.CLOSED_ACCOUNT_CLOSED_CONTINUATION.createAccount();
            accounts.put(closedAccountClosedContinuationClosed.getAccountNumber(), closedAccountClosedContinuationClosed);
            Account closedAccountNoContinuation = AccountFixture.CLOSED_ACCOUNT_NO_CONTINUATION.createAccount();
            accounts.put(closedAccountNoContinuation.getAccountNumber(), closedAccountNoContinuation);
            Account closedAccountOpenContinuation = AccountFixture.CLOSED_ACCOUNT_OPEN_CONTINUATION.createAccount();
            accounts.put(closedAccountOpenContinuation.getAccountNumber(), closedAccountOpenContinuation);
            Account closedContinuationAccount = AccountFixture.CLOSED_CONTINUATION_ACCOUNT.createAccount();
            accounts.put(closedContinuationAccount.getAccountNumber(), closedContinuationAccount);
            Account closedAccountCloseContinuationOpenContinuationAccount = AccountFixture.CLOSED_ACCOUNT_CLOSED_CONTINUATION_OPEN_CONTINUATION.createAccount();
            accounts.put(closedAccountCloseContinuationOpenContinuationAccount.getAccountNumber(), closedAccountCloseContinuationOpenContinuationAccount);
            Account closedContinuationAccountOpenContinuation = AccountFixture.CLOSED_CONTINUATION_ACCOUNT_OPEN_CONTINUATION.createAccount();
            accounts.put(closedContinuationAccountOpenContinuation.getAccountNumber(), closedContinuationAccountOpenContinuation);
            Account activeContinuationAccount = AccountFixture.ACTIVE_CONTINUATION_ACCOUNT.createAccount();
            accounts.put(activeContinuationAccount.getAccountNumber(), activeContinuationAccount);
        }

        public Account getAccount(String chartCode, String accountNumber) {
            if (StringUtils.isBlank(chartCode) || StringUtils.isBlank(accountNumber)) {
                return null;
            }

            Account account = accounts.get(accountNumber);
            if (ObjectUtils.isNotNull(account)) {
                return account;
            }

            account = new Account();
            account.setChartOfAccountsCode(chartCode);
            account.setAccountNumber(accountNumber);

            return account;
        }

    }

    private class MockConfigurationServiceImpl implements ConfigurationService {

        public String getPropertyValueAsString(String key) {
            if (StringUtils.equals(key, KFSKeyConstants.WARNING_ICRACCOUNT_CONTINUATION_ACCOUNT_USED)) {
                return "{0}-{1} is closed, so the Indirect Cost Recovery will use continuation account {2}-{3} instead.";
            } else if (StringUtils.equals(key, KFSKeyConstants.ERROR_ICRACCOUNT_CONTINUATION_ACCOUNT_CLOSED)) {
                return "{0}-{1} is closed and a search of {2} levels of continuation accounts did not result in an open account to use for Indirect Cost Recovery.";
            }

            return null;
        }

        @Override
        public boolean getPropertyValueAsBoolean(String s) {
            return false;
        }

        @Override
        public boolean getPropertyValueAsBoolean(String s, boolean b) {
            return false;
        }

        @Override
        public Map<String, String> getAllProperties() {
            return null;
        }

    }
}