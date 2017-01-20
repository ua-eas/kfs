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
package org.kuali.kfs.coa.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.easymock.IMockBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryAccount;
import org.kuali.kfs.krad.service.impl.BusinessObjectServiceImpl;
import org.kuali.kfs.krad.service.impl.DataDictionaryServiceImpl;
import org.kuali.kfs.krad.util.ErrorMessage;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.krad.bo.BusinessObject;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IndirectCostRecoveryAccountsRuleTest {

    private IndirectCostRecoveryAccountsRule accountRule;

    @Before
    public void setUp() {
        ArrayList<String> methodNames = new ArrayList<>();
        for (Method method : AccountRule.class.getMethods()) {
            if (!Modifier.isFinal(method.getModifiers()) && !method.getName().startsWith("set") && !method.getName().startsWith("get")) {
                methodNames.add(method.getName());
            }
        }
        IMockBuilder<AccountRule> builder = EasyMock.createMockBuilder(AccountRule.class).addMockedMethods(methodNames.toArray(new String[0]));
        accountRule = builder.createNiceMock();

        accountRule.setBoService(new MockBusinessObjectService());
        accountRule.setDdService(new MockDataDictionaryService());
    }

    @After
    public void tearDown() {
        GlobalVariables.getMessageMap().clearErrorMessages();
    }

    @Test
    public void checkIndirectCostRecoveryAccountValidAccount() {
        Assert.assertTrue("validation should pass with valid account", accountRule.checkIndirectCostRecoveryAccount("BL", "1031400", new BigDecimal(100), true));
    }

    @Test
    public void checkIndirectCostRecoveryAccountNullChart() {
        validateCheckIndirectCostRecoveryAccount("validation should fail with null chart of accounts code", null, "1031400", 1, KFSKeyConstants.ERROR_REQUIRED, "Indirect Cost Recovery Chart Of Accounts Code", "indirectCostRecoveryFinCoaCode");
    }

    @Test
    public void checkIndirectCostRecoveryAccountEmptyChart() {
        validateCheckIndirectCostRecoveryAccount("validation should fail with empty chart of accounts code", "", "1031400", 1, KFSKeyConstants.ERROR_REQUIRED, "Indirect Cost Recovery Chart Of Accounts Code", "indirectCostRecoveryFinCoaCode");
    }

    @Test
    public void checkIndirectCostRecoveryAccountNullAccount() {
        validateCheckIndirectCostRecoveryAccount("validation should fail with null account number", "BL", null, 1, KFSKeyConstants.ERROR_REQUIRED, "Indirect Cost Recovery Account Number", "indirectCostRecoveryAccountNumber");
    }

    @Test
    public void checkIndirectCostRecoveryAccountEmptyAccount() {
        validateCheckIndirectCostRecoveryAccount("validation should fail with empty account number", "BL", "", 1, "error.required", "Indirect Cost Recovery Account Number", "indirectCostRecoveryAccountNumber");
    }

    @Test
    public void checkIndirectCostRecoveryAccountInvalidChart() {
        validateCheckIndirectCostRecoveryAccount("validation should fail with invalid chart of accounts code", "XX", "1031400", 2, KFSKeyConstants.ERROR_EXISTENCE, "XX", "indirectCostRecoveryFinCoaCode");
        ErrorMessage expectedErrorMessage = new ErrorMessage(KFSKeyConstants.ERROR_EXISTENCE, "XX-1031400");
        Assert.assertEquals("error message didn't match what we were expecting", expectedErrorMessage, GlobalVariables.getMessageMap().getErrorMessages().get("indirectCostRecoveryAccountNumber").get(0));
    }

    @Test
    public void checkIndirectCostRecoveryAccountInvalidAccount() {
        validateCheckIndirectCostRecoveryAccount("validation should fail with invalid account number", "BL", "XXXXXXX", 1, KFSKeyConstants.ERROR_EXISTENCE, "BL-XXXXXXX", "indirectCostRecoveryAccountNumber");
    }

    @Test
    public void checkIndirectCostRecoveryAccountClosedAccount() {
        validateCheckIndirectCostRecoveryAccount("validation should fail with closed account", "BL", "2131401", 1, KFSKeyConstants.ERROR_INACTIVE, "BL-2131401", "indirectCostRecoveryAccountNumber");
    }

    @Test
    public void checkIndirectCostRecoveryAccountPercentTooLow() {
        validateCheckIndirectCostRecoveryAccount("validation should fail with percent < 0", "BL", "1031400", -10, 1, KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ICR_ACCOUNT_INVALID_LINE_PERCENT, StringUtils.EMPTY, KFSPropertyConstants.ICR_ACCOUNT_LINE_PERCENT);
    }

    @Test
    public void checkIndirectCostRecoveryAccountPercentTooHigh() {
        validateCheckIndirectCostRecoveryAccount("validation should fail with percent > 100", "BL", "1031400", 110, 1, KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ICR_ACCOUNT_INVALID_LINE_PERCENT, StringUtils.EMPTY, KFSPropertyConstants.ICR_ACCOUNT_LINE_PERCENT);
    }

    private void validateCheckIndirectCostRecoveryAccount(String message, String chartOfAccountsCode, String accountNumber, int expectedErrorCount, String expectedErrorKey, String errorMessageParameters, String actualErrorKey) {
        validateCheckIndirectCostRecoveryAccount(message, chartOfAccountsCode, accountNumber, 100, expectedErrorCount, expectedErrorKey, errorMessageParameters, actualErrorKey);
    }

    private void validateCheckIndirectCostRecoveryAccount(String message, String chartOfAccountsCode, String accountNumber, int icraAccountLinePercentage, int expectedErrorCount, String expectedErrorKey, String errorMessageParameters, String actualErrorKey) {
        Assert.assertFalse(message, accountRule.checkIndirectCostRecoveryAccount(chartOfAccountsCode, accountNumber, new BigDecimal(icraAccountLinePercentage), true));
        Assert.assertEquals("did not get the expected number of errors", expectedErrorCount, GlobalVariables.getMessageMap().getErrorCount());
        ErrorMessage expectedErrorMessage = buildErrorMessage(expectedErrorKey, errorMessageParameters);
        Assert.assertEquals("error message didn't match what we were expecting", expectedErrorMessage, GlobalVariables.getMessageMap().getErrorMessages().get(actualErrorKey).get(0));
    }

    private ErrorMessage buildErrorMessage(String expectedErrorKey, String errorMessageParameters) {
        ErrorMessage expectedErrorMessage;
        if (StringUtils.isNotBlank(errorMessageParameters)) {
            expectedErrorMessage = new ErrorMessage(expectedErrorKey, errorMessageParameters);
        } else {
            expectedErrorMessage = new ErrorMessage(expectedErrorKey);
        }
        return expectedErrorMessage;
    }

    @Test
    public void checkIndirectCostRecoveryAccountDistributionsNoAccountsValid() {
        Assert.assertTrue("validation should pass with no ICR accounts", accountRule.checkIndirectCostRecoveryAccountDistributions());
    }

    @Test
    public void checkIndirectCostRecoveryAccountDistributionsOneInactiveOneActiveAccountValid() {
        setupAccountLists("2131401", new BigDecimal(100), "1031400");

        Assert.assertTrue("validation should pass with one inactive invalid and active valid account", accountRule.checkIndirectCostRecoveryAccountDistributions());
        Assert.assertEquals("expecting no errors", 0, GlobalVariables.getMessageMap().getErrorCount());
    }

    @Test
    public void checkIndirectCostRecoveryAccountDistributionsOneInactiveOneClosedAccount() {
        setupAccountLists("1031400", new BigDecimal(100), "2131401");

        Assert.assertTrue("validation shouldn't fail even with closed account", accountRule.checkIndirectCostRecoveryAccountDistributions());
        verifyCheckIndirectCostRecoveryAccountDistributionsErrorMessages(KFSKeyConstants.ERROR_INACTIVE, "BL-2131401", "document.newMaintainableObject.null[1].indirectCostRecoveryAccountNumber");
    }

    @Test
    public void checkIndirectCostRecoveryAccountDistributionsBadPercent() {
        setupAccountLists("1031400", new BigDecimal(50), "1031400");

        Assert.assertFalse("validation should fail with invalid percent", accountRule.checkIndirectCostRecoveryAccountDistributions());
        verifyCheckIndirectCostRecoveryAccountDistributionsErrorMessages(KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ICR_ACCOUNT_TOTAL_NOT_100_PERCENT, StringUtils.EMPTY, "document.newMaintainableObject.null");
    }

    private void setupAccountLists(String indirectCostRecoveryAccountNumber, BigDecimal accountLinePercent, String indirectCostRecoveryAccountNumber2) {
        List<IndirectCostRecoveryAccount> activeIndirectCostRecoveryAccountList = new ArrayList<>();
        List<IndirectCostRecoveryAccount> indirectCostRecoveryAccountList = new ArrayList<>();

        indirectCostRecoveryAccountList.add(setupIcrAccount(indirectCostRecoveryAccountNumber, accountLinePercent, false));

        IndirectCostRecoveryAccount icrAccount = setupIcrAccount(indirectCostRecoveryAccountNumber2, accountLinePercent, true);
        activeIndirectCostRecoveryAccountList.add(icrAccount);
        indirectCostRecoveryAccountList.add(icrAccount);

        accountRule.setActiveIndirectCostRecoveryAccountList(activeIndirectCostRecoveryAccountList);
        accountRule.setIndirectCostRecoveryAccountList(indirectCostRecoveryAccountList);
    }

    private IndirectCostRecoveryAccount setupIcrAccount(String indirectCostRecoveryAccountNumber, BigDecimal accountLinePercent, boolean active) {
        IndirectCostRecoveryAccount icrAccount = new IndirectCostRecoveryAccount();
        icrAccount.setIndirectCostRecoveryFinCoaCode("BL");
        icrAccount.setIndirectCostRecoveryAccountNumber(indirectCostRecoveryAccountNumber);
        icrAccount.setAccountLinePercent(accountLinePercent);
        icrAccount.setActive(active);
        return icrAccount;
    }

    private void verifyCheckIndirectCostRecoveryAccountDistributionsErrorMessages(String expectedErrorKey, String errorMessageParameters, String errorKey) {
        Assert.assertEquals("expecting one error", 1, GlobalVariables.getMessageMap().getErrorCount());
        ErrorMessage expectedErrorMessage = buildErrorMessage(expectedErrorKey, errorMessageParameters);
        List<ErrorMessage> errorMessages = GlobalVariables.getMessageMap().getErrorMessages().get(errorKey);
        Assert.assertTrue("errorMessages shouldn't be null", ObjectUtils.isNotNull(errorMessages));
        Assert.assertEquals("errorMessages should contain one message", 1, errorMessages.size());
        Assert.assertEquals("error message didn't match what we were expecting", expectedErrorMessage, errorMessages.get(0));
    }

    private class MockBusinessObjectService extends BusinessObjectServiceImpl {

        @Override
        public <T extends BusinessObject> T findByPrimaryKey(Class<T> clazz, Map<String, ?> primaryKeys) {
            Account account = new Account();
            String chartOfAccountsCode = (String)primaryKeys.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
            String accountNumber = (String)primaryKeys.get(KFSPropertyConstants.ACCOUNT_NUMBER);
            account.setChartOfAccountsCode(chartOfAccountsCode);
            account.setAccountNumber(accountNumber);
            if (StringUtils.equals("2131401", accountNumber)) {
                account.setClosed(true);
            }

            return (T)account;
        }

        @Override
        public int countMatching(Class clazz, Map<String, ?> fieldValues) {
            String chartOfAccountsCode = (String)fieldValues.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
            String accountNumber = (String)fieldValues.get(KFSPropertyConstants.ACCOUNT_NUMBER);

            if (StringUtils.isNotBlank(chartOfAccountsCode)) {
                if (StringUtils.isBlank(accountNumber)) {
                    if (StringUtils.equals("XX", chartOfAccountsCode)) {
                        return 0;
                    } else {
                        return 1;
                    }
                } else {
                    if (StringUtils.equals("XX", chartOfAccountsCode) || StringUtils.equals("XXXXXXX", accountNumber)) {
                        return 0;
                    } else {
                        return 1;
                    }
                }
            }

            return 0;
        }

    }

    private class MockDataDictionaryService extends DataDictionaryServiceImpl {

        @Override
        public String getAttributeLabel(Class dataObjectClass, String attributeName) {
            if (StringUtils.equals("indirectCostRecoveryFinCoaCode", attributeName)) {
                return "Indirect Cost Recovery Chart Of Accounts Code";
            }
            if (StringUtils.equals("indirectCostRecoveryAccountNumber", attributeName)) {
                return "Indirect Cost Recovery Account Number";
            }

            return null;
        }

    }

}
