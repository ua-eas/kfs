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
package org.kuali.kfs.gl.service.impl.sf;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectLevel;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.gl.businessobject.SufficientFundBalances;
import org.kuali.kfs.gl.businessobject.SufficientFundRebuild;
import org.kuali.kfs.gl.service.impl.MockGeneralLedgerPostingDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.document.GeneralLedgerPostingDocument;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SufficientFundsTestFixtures {
    public static Account getTestAccount(String chartOfAccountsCode, String accountNumber, String accountSufficientFundsCode,boolean pendingAcctSufficientFundsIndicator) {
        Account a = new Account();
        a.setChartOfAccountsCode(chartOfAccountsCode);
        a.setAccountNumber(accountNumber);
        a.setAccountSufficientFundsCode(accountSufficientFundsCode);
        a.setPendingAcctSufficientFundsIndicator(pendingAcctSufficientFundsIndicator);
        return a;
    }

    public static ObjectCode getTestObjectCode() {
        ObjectCode oc = new ObjectCode() {
            @Override
            public void refreshReferenceObject(String referenceObjectName) {
            }
        };
        oc.setFinancialObjectCode("1234");
        oc.setFinancialObjectLevelCode("2345");

        ObjectLevel ol = new ObjectLevel();
        ol.setFinancialConsolidationObjectCode("3456");
        oc.setFinancialObjectLevel(ol);

        return oc;
    }

    public static SystemOptions getTestSystemOptions(int year,boolean budgetCheckingOptionsCode) {
        return getTestSystemOptions(year,budgetCheckingOptionsCode,false);
    }

    public static SystemOptions getTestSystemOptions(int year,boolean budgetCheckingOptionsCode,boolean financialBeginBalanceLoadInd) {
        SystemOptions so = new SystemOptions();
        so.setUniversityFiscalYear(year);
        so.setBudgetCheckingOptionsCode(budgetCheckingOptionsCode);
        so.setBudgetCheckingBalanceTypeCd("CB");
        so.setFinancialBeginBalanceLoadInd(financialBeginBalanceLoadInd);
        return so;
    }

    public static Map<String,Object> getTestSufficientFundRebuildKeys(String chart,String account) {
        Map<String, Object> keys = new HashMap<>();
        keys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chart);
        keys.put(KFSPropertyConstants.ACCOUNT_NUMBER_FINANCIAL_OBJECT_CODE, account);
        return keys;
    }

    public static SufficientFundBalances getTestSufficientFundBalances(String chart, String account) {
        SufficientFundBalances b = new SufficientFundBalances();
        b.setChartOfAccountsCode(chart);
        b.setAccountNumber(account);
        b.setFinancialObjectCode("    ");
        b.setAccountActualExpenditureAmt(KualiDecimal.ZERO);
        b.setAccountEncumbranceAmount(KualiDecimal.ZERO);
        b.setCurrentBudgetBalanceAmount(new KualiDecimal("100.00"));
        b.setAccountSufficientFundsCode(KFSConstants.SF_TYPE_ACCOUNT);
        return b;
    }

    public static Map<String,Object> getTestSufficientFundBalancesKeys(String chart,String account) {
        return getTestSufficientFundBalancesKeys(2017,chart,account,"    ");
    }

    public static Map<String,Object> getTestSufficientFundBalancesKeys(int year,String chart,String account,String objectCode) {
        Map<String, Object> keys = new HashMap<>();
        keys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, year);
        keys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chart);
        keys.put(KFSPropertyConstants.ACCOUNT_NUMBER, account);
        keys.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectCode);
        return keys;
    }

    public static List<String> getTestCurrentYearExpenseObjectTypes(String objectType) {
        List<String> objectTypes = new ArrayList<>();
        objectTypes.add(objectType);
        return objectTypes;
    }

    public static GeneralLedgerPendingEntry getTestGeneralLedgerPendingEntry(String chartOfAccountsCode, String accountNumber, String objectCode, String accountSufficientFundsCode,KualiDecimal amount) {
        return getTestGeneralLedgerPendingEntry(chartOfAccountsCode,accountNumber,objectCode,accountSufficientFundsCode,amount,true,"AC");
    }

    public static GeneralLedgerPendingEntry getTestGeneralLedgerPendingEntry(String chartOfAccountsCode, String accountNumber, String objectCode, String accountSufficientFundsCode,KualiDecimal amount,boolean pendingAcctSufficientFundsIndicator,String balanceTypeCode) {
        GeneralLedgerPendingEntry glpe = new GeneralLedgerPendingEntry() {
            @Override
            public void refreshNonUpdateableReferences() {
            }
        };

        ObjectCode oc = new ObjectCode();
        oc.setChartOfAccountsCode(chartOfAccountsCode);
        oc.setUniversityFiscalYear(2016);
        oc.setFinancialObjectCode(objectCode);
        Chart occ = new Chart();
        occ.setChartOfAccountsCode(chartOfAccountsCode);
        occ.setFinancialCashObjectCode("8000");
        oc.setChartOfAccounts(occ);
        glpe.setFinancialObject(oc);

        glpe.setAccountNumber(accountNumber);
        glpe.setChartOfAccountsCode(chartOfAccountsCode);
        glpe.setAccount(getTestAccount(chartOfAccountsCode,accountNumber,accountSufficientFundsCode,pendingAcctSufficientFundsIndicator));
        glpe.setDocumentNumber("DOC12345");

        ObjectType ot = new ObjectType();
        ot.setCode("EX");
        ot.setFinObjectTypeDebitcreditCd("D");
        glpe.setObjectType(ot);

        BalanceType bt = new BalanceType();
        bt.setCode(balanceTypeCode);
        glpe.setBalanceType(bt);

        glpe.setFinancialObjectCode("9876");
        glpe.setTransactionDebitCreditCode("D");
        glpe.setTransactionLedgerEntryAmount(amount);
        glpe.setFinancialDocumentTypeCode("DI");

        return glpe;
    }

    public static GeneralLedgerPostingDocument getTestGeneralLedgerPostingDocument(List<GeneralLedgerPendingEntry> glpes) {
        MockGeneralLedgerPostingDocument doc = new MockGeneralLedgerPostingDocument();
        doc.setGeneralLedgerPendingEntries(glpes);
        return doc;
    }

    public static Collection<SufficientFundRebuild> getTestSufficientFundRebuildList(String chart, String account, String objectCode) {
        SufficientFundRebuild r = new SufficientFundRebuild();
        r.setChartOfAccountsCode(chart);
        r.setAccountNumberFinancialObjectCode(account);
        r.setAccountNumberFinancialObjectCode(objectCode);

        return Collections.singletonList(r);
    }
}
