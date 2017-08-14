package edu.arizona.kfs.fp.batch.service.impl;

import edu.arizona.kfs.fp.businessobject.BankTransaction;
import edu.arizona.kfs.fp.businessobject.ChartBankObjectCode;
import edu.arizona.kfs.fp.document.DistributionOfIncomeAndExpenseDocument;
import edu.arizona.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.kfs.krad.document.Document;
import org.kuali.rice.krad.util.ObjectUtils;

import java.text.SimpleDateFormat;

/**
 * Created by nataliac on 5/8/17.
 */
public class BankDistIncomeDocumentServiceImpl extends AbstractBankDocumentService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BankDistIncomeDocumentServiceImpl.class);

    @Override
    protected KFSConstants.BankTransactionDocumentType getDocumentType() {
        return KFSConstants.BankTransactionDocumentType.DI;
    }

    @Override
    public Document getDocument(BankTransaction bankTransaction) {
        LOG.debug("BankAdvanceDepositDocumentServiceImpl: getDistributionOfIncomeAndExpenseDocument for " + bankTransaction.toString());
        DistributionOfIncomeAndExpenseDocument diDocument = null;
        try {
            diDocument = (DistributionOfIncomeAndExpenseDocument) getDocumentService().getNewDocument(getDocumentType().name());
        } catch (WorkflowException e) {
            LOG.error("Exception at creating DistributionOfIncomeAndExpenseDocument: ", e);
            throw new RuntimeException("Exception at creating DistributionOfIncomeAndExpenseDocument: ", e);
        }

        diDocument.setPostingDate(bankTransaction.getValueDate());
        diDocument.setAccountingPeriod(getAccountingPeriodService().getByDate(bankTransaction.getValueDate()));

        // Populate Accounting Lines
        Bank bank = findBank(bankTransaction);
        ChartBankObjectCode cboc = findChartBankObjectCode(bankTransaction);

        SourceAccountingLine sourceAccountingLine = populateSourceAccountingLine(bankTransaction, bank, cboc);
        diDocument.addSourceAccountingLine(sourceAccountingLine);

        TargetAccountingLine targetAccountingLine = populateTargetAccountingLine(bankTransaction, bank, cboc);
        diDocument.addTargetAccountingLine(targetAccountingLine);

        buildHeader(diDocument, bankTransaction);

        createDocumentNote(diDocument, bankTransaction);

        LOG.debug("DistributionOfIncomeAndExpenseDocument: DistributionOfIncomeAndExpenseDocument = " + diDocument.toString());
        return diDocument;
    }


    private SourceAccountingLine populateSourceAccountingLine(BankTransaction bankTransaction, Bank bank, ChartBankObjectCode cboc) {
        SourceAccountingLine sourceAccountingLine = new SourceAccountingLine();
        KualiDecimal amount = bankTransaction.getAmount();
        if (amount.isPositive()) {
            sourceAccountingLine.setChartOfAccountsCode(cboc.getChartCode());
            sourceAccountingLine.setAccountNumber(cboc.getAccountNumber());
            sourceAccountingLine.setFinancialObjectCode(cboc.getObjectCode());

        } else {
            amount = bankTransaction.getAmount().multiply(new KualiDecimal(-1));

            sourceAccountingLine.setChartOfAccountsCode(bank.getCashOffsetFinancialChartOfAccountCode());
            sourceAccountingLine.setAccountNumber(bank.getCashOffsetAccountNumber());
            sourceAccountingLine.setFinancialObjectCode(bank.getCashOffsetObjectCode());
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(KFSConstants.BankTransactionConstants.ACCT_LINE_DATE_FORMAT);
        String refNumber = dateFormat.format(bankTransaction.getValueDate());

        sourceAccountingLine.setFinancialDocumentLineDescription(getDocumentDescription(bankTransaction));
        sourceAccountingLine.setReferenceNumber(refNumber);
        sourceAccountingLine.setReferenceOriginCode(KFSConstants.BankTransactionConstants.BANK_TRANSACTION_ORIGIN_CODE);
        sourceAccountingLine.setReferenceTypeCode(getDocumentType().name());
        sourceAccountingLine.setAmount(amount);

        LOG.debug("DistributionOfIncomeAndExpenseDocument: sourceAccountingLine = " + sourceAccountingLine.toString());
        return sourceAccountingLine;

    }


    private TargetAccountingLine populateTargetAccountingLine(BankTransaction bankTransaction, Bank bank, ChartBankObjectCode cboc) {
        TargetAccountingLine targetAccountingLine = new TargetAccountingLine();
        KualiDecimal amount = bankTransaction.getAmount();
        if (amount.isPositive()) {
            targetAccountingLine.setChartOfAccountsCode(bank.getCashOffsetFinancialChartOfAccountCode());
            targetAccountingLine.setAccountNumber(bank.getCashOffsetAccountNumber());
            targetAccountingLine.setFinancialObjectCode(bank.getCashOffsetObjectCode());

        } else {
            amount = amount.multiply(new KualiDecimal(-1));
            targetAccountingLine.setChartOfAccountsCode(cboc.getChartCode());
            targetAccountingLine.setAccountNumber(cboc.getAccountNumber());
            targetAccountingLine.setFinancialObjectCode(cboc.getObjectCode());
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat(KFSConstants.BankTransactionConstants.ACCT_LINE_DATE_FORMAT);
        String refNumber = dateFormat.format(bankTransaction.getValueDate());

        targetAccountingLine.setFinancialDocumentLineDescription(getDocumentDescription(bankTransaction));
        targetAccountingLine.setReferenceNumber(refNumber);
        targetAccountingLine.setReferenceOriginCode(KFSConstants.BankTransactionConstants.BANK_TRANSACTION_ORIGIN_CODE);
        targetAccountingLine.setReferenceTypeCode(getDocumentType().name());

        targetAccountingLine.setAmount(amount);

        LOG.debug("DistributionOfIncomeAndExpenseDocument: targetAccountingLine = " + targetAccountingLine.toString());
        return targetAccountingLine;
    }


    /*
    * Returns the appropriate Chart of Accounts, Bank and Object Code for the given bankTransaction
    */
    private ChartBankObjectCode findChartBankObjectCode(BankTransaction bankTransaction) {
        ChartBankObjectCode cboc = getBankParametersAccessService().getChartBankObjectCodeForDescriptionSearch(bankTransaction.getDescription());
        if (ObjectUtils.isNull(cboc)) {
            //use values from DEFAULT_BANK param
            Bank defaultBank = getTransactionPostingDao().getBankObjectByBankCode(getBankParametersAccessService().getDefaultBank());
            if (ObjectUtils.isNull(defaultBank)) {
                LOG.error("Invalid System Configuration - Default bank parameter is not configured or has an invalid value =" + getBankParametersAccessService().getDefaultBank());
                throw new RuntimeException("Invalid System Configuration - Default bank parameter is not configured or has an invalid value =" + getBankParametersAccessService().getDefaultBank());
            }
            cboc = new ChartBankObjectCode();
            cboc.setChartCode(defaultBank.getCashOffsetFinancialChartOfAccountCode());
            cboc.setAccountNumber(defaultBank.getCashOffsetAccountNumber());
            cboc.setObjectCode(defaultBank.getCashOffsetObjectCode());
        }

        LOG.debug("BankAdvanceDepositDocumentServiceImpl: findChartBankObjectCode  for " + bankTransaction + " = " + cboc);
        return cboc;
    }


}