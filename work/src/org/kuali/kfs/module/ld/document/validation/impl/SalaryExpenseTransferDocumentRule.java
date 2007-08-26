/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.labor.rules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.RiceConstants;
import org.kuali.core.document.Document;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.bo.ExpenseTransferAccountingLine;
import org.kuali.module.labor.bo.ExpenseTransferSourceAccountingLine;
import org.kuali.module.labor.bo.LaborObject;
import org.kuali.module.labor.document.LaborExpenseTransferDocumentBase;
import org.kuali.module.labor.document.LaborLedgerPostingDocument;
import org.kuali.module.labor.document.SalaryExpenseTransferDocument;
import org.kuali.module.labor.rule.GenerateLaborLedgerBenefitClearingPendingEntriesRule;
import org.kuali.module.labor.service.LaborLedgerPendingEntryService;


/**
 * Business rule(s) applicable to Salary Expense Transfer documents.
 */
public class SalaryExpenseTransferDocumentRule extends LaborExpenseTransferDocumentRules implements GenerateLaborLedgerBenefitClearingPendingEntriesRule<LaborLedgerPostingDocument> {

    /**
     * Constructor
     */
    public SalaryExpenseTransferDocumentRule() {
        super();
    }

    /**
     * @see org.kuali.core.rules.SaveDocumentRule#processCustomSaveDocumentBusinessRules(Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        // Validate that an employee ID is enterred.
        SalaryExpenseTransferDocument salaryExpenseTransferDocument = (SalaryExpenseTransferDocument) document;
        String emplid = salaryExpenseTransferDocument.getEmplid();
        if ((emplid == null) || (emplid.trim().length() == 0)) {
            reportError(KFSConstants.EMPLOYEE_LOOKUP_ERRORS, KFSKeyConstants.Labor.MISSING_EMPLOYEE_ID, emplid);
            return false;
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.rules.AccountingDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.kfs.document.AccountingDocument,
     *      org.kuali.kfs.bo.AccountingLine)
     */
    @Override
    protected boolean processCustomAddAccountingLineBusinessRules(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        boolean isValid = super.processCustomAddAccountingLineBusinessRules(accountingDocument, accountingLine);

        // only salary labor object codes are allowed on the salary expense transfer document
        if (!isSalaryObjectCode(accountingLine)) {
            reportError(KFSPropertyConstants.ACCOUNT, KFSKeyConstants.Labor.INVALID_SALARY_OBJECT_CODE_ERROR, accountingLine.getAccountNumber());
            return false;
        }

        // ensure the employee ids in the source accounting lines are same
        if (!hasSameEmployee(accountingDocument)) {
            reportError(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES, KFSKeyConstants.Labor.ERROR_EMPLOYEE_ID_NOT_SAME);
            return false;
        }

        return isValid;
    }

    /**
     * @see org.kuali.module.labor.rules.LaborExpenseTransferDocumentRules#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);

        SalaryExpenseTransferDocument expenseTransferDocument = (SalaryExpenseTransferDocument) document;

        // must not have any pending labor ledger entries with same emplId, periodCode, accountNumber, objectCode
        if (isValid) {
            isValid = !hasPendingLedgerEntry(expenseTransferDocument);
        }

        return isValid;
    }

    /**
     * Determine whether the object code of given accounting line is a salary labor object code
     * 
     * @param accountingLine the given accounting line
     * @return true if the object code of given accounting line is a salary; otherwise, false
     */
    private boolean isSalaryObjectCode(AccountingLine accountingLine) {
        ExpenseTransferAccountingLine expenseTransferAccountingLine = (ExpenseTransferAccountingLine) accountingLine;

        LaborObject laborObject = expenseTransferAccountingLine.getLaborObject();
        if (laborObject == null) {
            return false;
        }

        String fringeOrSalaryCode = laborObject.getFinancialObjectFringeOrSalaryCode();
        return StringUtils.equals(LaborConstants.SalaryExpenseTransfer.LABOR_LEDGER_SALARY_CODE, fringeOrSalaryCode);
    }

    /**
     * determine whether the employees in the source accouting lines are same
     * 
     * @param accountingDocument the given accouting document
     * @return true if the employees in the source accouting lines are same; otherwise, false
     */
    private boolean hasSameEmployee(AccountingDocument accountingDocument) {
        LOG.debug("started hasSameEmployee");

        LaborExpenseTransferDocumentBase expenseTransferDocument = (LaborExpenseTransferDocumentBase) accountingDocument;
        List<ExpenseTransferSourceAccountingLine> sourceAccountingLines = expenseTransferDocument.getSourceAccountingLines();

        String cachedEmplid = null;
        for (ExpenseTransferSourceAccountingLine sourceAccountingLine : sourceAccountingLines) {
            String emplid = sourceAccountingLine.getEmplid();
            if (emplid == null) {
                return false;
            }

            cachedEmplid = cachedEmplid == null ? emplid : cachedEmplid;
            if (!emplid.equals(cachedEmplid)) {
                return false;
            }
        }
        return true;
    }

    /**
     * determine if there is any pending entry for the source accounting lines of the given document
     * 
     * @param accountingDocument the given accounting document
     * @return true if there is a pending entry for the source accounting lines of the given document; otherwise, false
     */
    public boolean hasPendingLedgerEntry(AccountingDocument accountingDocument) {
        LOG.info("started hasPendingLedgerEntry(accountingDocument)");
        
        LaborExpenseTransferDocumentBase expenseTransferDocument = (LaborExpenseTransferDocumentBase) accountingDocument;
        List<ExpenseTransferAccountingLine> sourceAccountingLines = expenseTransferDocument.getSourceAccountingLines();

        Map<String, String> fieldValues = new HashMap<String, String>();
        for (ExpenseTransferAccountingLine sourceAccountingLine : sourceAccountingLines) {
            String payPeriodCode = sourceAccountingLine.getPayrollEndDateFiscalPeriodCode();
            String accountNumber = sourceAccountingLine.getAccountNumber();
            String objectCode = sourceAccountingLine.getFinancialObjectCode();
            String emplid = sourceAccountingLine.getEmplid();
            String documentNumber = accountingDocument.getDocumentNumber();

            fieldValues.put(KFSPropertyConstants.PAYROLL_END_DATE_FISCAL_PERIOD_CODE, payPeriodCode);
            fieldValues.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);
            fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectCode);
            fieldValues.put(KFSPropertyConstants.EMPLID, emplid);
            fieldValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, RiceConstants.NOT_LOGICAL_OPERATOR + documentNumber);

            if (SpringContext.getBean(LaborLedgerPendingEntryService.class).hasPendingLaborLedgerEntry(fieldValues)) {
                reportError(KFSConstants.EMPLOYEE_LOOKUP_ERRORS, KFSKeyConstants.Labor.PENDING_SALARY_TRANSFER_ERROR, emplid, payPeriodCode, accountNumber, objectCode);
                return true;
            }
        }
        return false;
    }
}
