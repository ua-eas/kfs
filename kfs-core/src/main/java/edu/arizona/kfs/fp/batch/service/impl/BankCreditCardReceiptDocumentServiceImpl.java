package edu.arizona.kfs.fp.batch.service.impl;

import edu.arizona.kfs.fp.businessobject.BankTransaction;
import edu.arizona.kfs.fp.businessobject.ChartBankObjectCode;
import edu.arizona.kfs.fp.document.CreditCardReceiptDocument;
import edu.arizona.kfs.sys.KFSConstants;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.CreditCardDetail;
import org.kuali.kfs.fp.businessobject.CreditCardVendor;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.ObjectUtils;

import java.text.SimpleDateFormat;


/**
 * Created by nataliac on 5/8/17.
 */
public class BankCreditCardReceiptDocumentServiceImpl extends AbstractBankDocumentService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BankCreditCardReceiptDocumentServiceImpl.class);


    @Override
    protected KFSConstants.BankTransactionDocumentType getDocumentType() {
        return KFSConstants.BankTransactionDocumentType.CCR;
    }


    /**
     * @param bankTransaction
     * @return
     * @see AbstractBankDocumentService
     */
    @Override
    public Document getDocument(BankTransaction bankTransaction) {
        LOG.debug("BankAdvanceDepositDocumentServiceImpl: getCreditCardReceiptDocument for " + bankTransaction.toString());
        CreditCardReceiptDocument creditCardRecieptDocument = null;
        try {
            creditCardRecieptDocument = (CreditCardReceiptDocument) getDocumentService().getNewDocument(getDocumentType().name());
        } catch (WorkflowException e) {
            LOG.error("Exception at creating CreditCardReceiptDocument: ", e);
            throw new RuntimeException("Exception at creating CreditCardReceiptDocument: ", e);
        }

        Bank bank = findBank(bankTransaction);

        creditCardRecieptDocument.setAccountingPeriod(getAccountingPeriodService().getByDate(bankTransaction.getValueDate()));
        creditCardRecieptDocument.setCreditCardReceiptBankCode(bank.getBankCode());
        creditCardRecieptDocument.setPostingDate(bankTransaction.getValueDate());

        // Customer Reference Number in bankTransaction is the Vendor Number in Credit Card Table
        CreditCardVendor ccv = getTransactionPostingDao().getCreditCardVendorObject(bankTransaction.getCustRefNo());
        if (ObjectUtils.isNull(ccv)) {
            LOG.error("Could not find CreditCardVendor for " + bankTransaction.getCustRefNo() + " ...Aborting");
            throw new RuntimeException("Could not find CreditCardVendor for " + bankTransaction.getCustRefNo() + " ...Aborting!");
        }

        // Create Credit card Detail Object
        CreditCardDetail creditCardDetail = populateCreditCardDetail(bankTransaction, ccv);
        creditCardRecieptDocument.addCreditCardReceipt(creditCardDetail);

        // Create Accounting Lines Object
        SourceAccountingLine accountingLine = populateSourceAccountingLine(bankTransaction, ccv);

        creditCardRecieptDocument.addSourceAccountingLine(accountingLine);
        buildHeader(creditCardRecieptDocument, bankTransaction);

        createDocumentNote(creditCardRecieptDocument, bankTransaction);

        LOG.debug("BankAdvanceDepositDocumentServiceImpl: CreditCardReceiptDocument = " + creditCardRecieptDocument.toString());
        return creditCardRecieptDocument;
    }


    private CreditCardDetail populateCreditCardDetail(BankTransaction bankTransaction, CreditCardVendor ccv) {
        CreditCardDetail creditCardDetail = new CreditCardDetail();
        creditCardDetail.setCreditCardDepositDate(bankTransaction.getValueDate());
        creditCardDetail.setCreditCardAdvanceDepositAmount(bankTransaction.getAmount());

        creditCardDetail.setFinancialDocumentCreditCardTypeCode(ccv.getFinancialDocumentCreditCardTypeCode());
        creditCardDetail.setFinancialDocumentCreditCardVendorNumber(ccv.getFinancialDocumentCreditCardVendorNumber());

        //BankReferenceNumber truncated to 10 most right characters
        String bankReferenceNumber = StringUtils.right(bankTransaction.getBankReference(), KFSConstants.BankTransactionConstants.MAX_BANK_REF_NBR);
        if (StringUtils.isBlank(bankReferenceNumber)) {
            creditCardDetail.setCreditCardDepositReferenceNumber(bankTransaction.getValueDate().toString());
        } else {
            creditCardDetail.setCreditCardDepositReferenceNumber(bankReferenceNumber);
        }
        LOG.debug("Exit populateCreditCardDetail=" + creditCardDetail.toString());
        return creditCardDetail;
    }


    private SourceAccountingLine populateSourceAccountingLine(BankTransaction bankTransaction, CreditCardVendor ccv) {
        SourceAccountingLine sourceAccountingLine = new SourceAccountingLine();

        ChartBankObjectCode cboc = getBankParametersAccessService().getChartBankObjectCodeForDescriptionSearch(bankTransaction.getDescription());
        if (ObjectUtils.isNotNull(cboc)) {
            sourceAccountingLine.setChartOfAccountsCode(cboc.getChartCode());
            sourceAccountingLine.setAccountNumber(cboc.getAccountNumber());
            sourceAccountingLine.setFinancialObjectCode(cboc.getObjectCode());
        } else {
            //ChartBankObjectCode - is empty, populate from ccv
            if (bankTransaction.getAmount().isPositive()) {
                //Amount is positive - populate with income info
                sourceAccountingLine.setChartOfAccountsCode(ccv.getIncomeFinancialChartOfAccountsCode());
                sourceAccountingLine.setAccountNumber(ccv.getIncomeAccountNumber());
                sourceAccountingLine.setFinancialObjectCode(ccv.getIncomeFinancialObjectCode());
                sourceAccountingLine.setFinancialSubObjectCode(ccv.getIncomeFinancialSubObjectCode());
                sourceAccountingLine.setSubAccountNumber(ccv.getIncomeSubAccountNumber());
            } else {
                //Amount is negative - populate with expense info
                sourceAccountingLine.setChartOfAccountsCode(ccv.getExpenseFinancialChartOfAccountsCode());
                sourceAccountingLine.setAccountNumber(ccv.getExpenseAccountNumber());
                sourceAccountingLine.setFinancialObjectCode(ccv.getExpenseFinancialObjectCode());
                sourceAccountingLine.setFinancialSubObjectCode(ccv.getExpenseFinancialSubObjectCode());
                sourceAccountingLine.setSubAccountNumber(ccv.getExpenseSubAccountNumber());
            }
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat(KFSConstants.BankTransactionConstants.ACCT_LINE_DATE_FORMAT);
        String valuePostedDate = dateFormat.format(bankTransaction.getValueDate());
        sourceAccountingLine.setReferenceNumber(valuePostedDate);


        sourceAccountingLine.setReferenceOriginCode(KFSConstants.BankTransactionConstants.BANK_TRANSACTION_ORIGIN_CODE);
        sourceAccountingLine.setFinancialDocumentLineDescription("Vendor = " + ccv.getFinancialDocumentCreditCardVendorName());
        sourceAccountingLine.setReferenceTypeCode(getDocumentType().name());

        KualiDecimal amount = bankTransaction.getAmount();
        if (amount.isPositive()) {
            // Error invoking method getFinancialObjectTypeCode
            if (getOptionsService().getCurrentYearOptions().getFinancialObjectTypeAssetsCd().equals(ccv.getIncomeFinancialObject().getFinancialObjectTypeCode())
                    || getOptionsService().getCurrentYearOptions().getFinObjTypeExpenditureexpCd().equals(ccv.getIncomeFinancialObject().getFinancialObjectTypeCode())) {
                amount = amount.multiply(new KualiDecimal(-1));
            }
        } else {
            if (getOptionsService().getCurrentYearOptions().getFinancialObjectTypeAssetsCd().equals(ccv.getExpenseFinancialObject().getFinancialObjectTypeCode())
                    || getOptionsService().getCurrentYearOptions().getFinObjTypeExpenditureexpCd().equals(ccv.getExpenseFinancialObject().getFinancialObjectTypeCode())) {
                amount = amount.multiply(new KualiDecimal(-1));
            }
        }
        sourceAccountingLine.setAmount(amount);

        LOG.debug("Exit populateSourceAccountingLine=" + sourceAccountingLine.toString());
        return sourceAccountingLine;
    }

}
