package edu.arizona.kfs.fp.batch.service.impl;

import edu.arizona.kfs.fp.businessobject.BankTransaction;
import edu.arizona.kfs.fp.businessobject.ChartBankObjectCode;
import edu.arizona.kfs.fp.document.AdvanceDepositDocument;
import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSPropertyConstants;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.fp.businessobject.AdvanceDepositDetail;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nataliac on 5/8/17.
 */
public class BankAdvanceDepositDocumentServiceImpl extends AbstractBankDocumentService {
    protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BankAdvanceDepositDocumentServiceImpl.class);

    @Override
    protected KFSConstants.BankTransactionDocumentType getDocumentType() {
        return KFSConstants.BankTransactionDocumentType.AD;
    }

    @Override
    public Document getDocument(BankTransaction bankTransaction) {
        LOG.debug("BankAdvanceDepositDocumentServiceImpl: getAdvanceDepositDocument for " + bankTransaction.toString());

        Bank bank = findBank(bankTransaction);
        AdvanceDepositDocument advanceDepositDocument = null;
        try {
            advanceDepositDocument = (AdvanceDepositDocument) getDocumentService().getNewDocument(getDocumentType().name());
        } catch (WorkflowException e) {
            LOG.error("Exception at creating AdvanceDepositDocument: ", e);
            throw new RuntimeException("Exception at creating AdvanceDepositDocument: ", e);
        }
        advanceDepositDocument.setPostingDate(bankTransaction.getValueDate());
        advanceDepositDocument.setAccountingPeriod(getAccountingPeriodService().getByDate(bankTransaction.getValueDate()));
        advanceDepositDocument.setDepositDate(bankTransaction.getValueDate());

        AdvanceDepositDetail advanceDepositDetail = populateAdvanceDepositDetail(bankTransaction);
        advanceDepositDetail.setFinancialDocumentBankCode(bank.getBankCode());
        advanceDepositDocument.addAdvanceDeposit(advanceDepositDetail);

        // Populate Accounting Line
        SourceAccountingLine accountingLineObj = populateSourceAccountingLine(bankTransaction);
        accountingLineObj.setFinancialDocumentLineDescription(advanceDepositDetail.getFinancialDocumentAdvanceDepositDescription());
        advanceDepositDocument.addSourceAccountingLine(accountingLineObj);

        buildHeader(advanceDepositDocument, bankTransaction);

        createDocumentNote(advanceDepositDocument, bankTransaction);

        LOG.debug("BankAdvanceDepositDocumentServiceImpl: AdvancedDepositDocument = " + advanceDepositDocument.toString());
        return advanceDepositDocument;
    }

    private AdvanceDepositDetail populateAdvanceDepositDetail(BankTransaction bankTransaction) {
        AdvanceDepositDetail advanceDepositDetail = new AdvanceDepositDetail();
        advanceDepositDetail.setFinancialDocumentAdvanceDepositDate(bankTransaction.getValueDate());

        //set the Reference Number
        String bankReferenceNumber = bankTransaction.getBankReference();
        if (StringUtils.isBlank(bankReferenceNumber)) {
            //if bankReference is empty, set the ?ValueDate? as the reference number
            advanceDepositDetail.setFinancialDocumentAdvanceDepositReferenceNumber(bankTransaction.getValueDate().toString());
        } else {
            //get 10 most right characters for the bankReference number
            bankReferenceNumber = StringUtils.right(bankReferenceNumber, KFSConstants.BankTransactionConstants.MAX_BANK_REF_NBR);
            advanceDepositDetail.setFinancialDocumentAdvanceDepositReferenceNumber(bankReferenceNumber);
        }

        //set description
        advanceDepositDetail.setFinancialDocumentAdvanceDepositDescription(getDocumentDescription(bankTransaction));
        advanceDepositDetail.setFinancialDocumentAdvanceDepositDate(bankTransaction.getValueDate());
        advanceDepositDetail.setFinancialDocumentAdvanceDepositAmount(bankTransaction.getAmount());
        LOG.debug("Exit populateAdvanceDepositDetail=" + advanceDepositDetail.toString());
        return advanceDepositDetail;
    }


    private SourceAccountingLine populateSourceAccountingLine(BankTransaction bankTransaction) {
        SourceAccountingLine sourceAccountingLine = new SourceAccountingLine();
        ChartBankObjectCode cboc = findChartBankObjectCode(bankTransaction);

        //set carrier attributes for sourceAccountingLine
        sourceAccountingLine.setChartOfAccountsCode(cboc.getChartCode());
        sourceAccountingLine.setAccountNumber(cboc.getAccountNumber());
        sourceAccountingLine.setFinancialObjectCode(cboc.getObjectCode());
        sourceAccountingLine.setReferenceOriginCode(KFSConstants.BankTransactionConstants.BANK_TRANSACTION_ORIGIN_CODE);

        SimpleDateFormat dateFormat = new SimpleDateFormat(KFSConstants.BankTransactionConstants.ACCT_LINE_DATE_FORMAT);
        String valuePostedDate = dateFormat.format(bankTransaction.getValueDate());
        sourceAccountingLine.setReferenceNumber(valuePostedDate);

        sourceAccountingLine.setReferenceTypeCode(getDocumentType().name());

        //see if we need to reverse amount value depending on ObjectCode
        KualiDecimal amount = bankTransaction.getAmount();
        ObjectCode objectCode = findObjectCode(cboc);
        if (getOptionsService().getCurrentYearOptions().getFinancialObjectTypeAssetsCd().equals(objectCode.getFinancialObjectTypeCode()) ||
                getOptionsService().getCurrentYearOptions().getFinObjTypeExpenditureexpCd().equals(objectCode.getFinancialObjectTypeCode())) {
            amount = amount.multiply(new KualiDecimal(-1));
        }

        sourceAccountingLine.setAmount(amount);
        LOG.debug("Exit populateSourceAccountingLine=" + sourceAccountingLine.toString());
        return sourceAccountingLine;
    }


    /*
     * Returns the appropriate Chart of Accounts, Bank and Object Code for the given bankTransaction
     */
    private ChartBankObjectCode findChartBankObjectCode(BankTransaction bankTransaction) {
        ChartBankObjectCode cboc = getBankParametersAccessService().getChartBankObjectCodeForDescriptionSearch(bankTransaction.getDescription());
        if (ObjectUtils.isNull(cboc)) {
            //try to see if it's a Special BAI Type
            cboc = getBankParametersAccessService().getChartBankObjectCodeForSpecialBai(bankTransaction.getBaiType(), bankTransaction.getAccountNumber());
            if (ObjectUtils.isNull(cboc)) {
                //use default values from ELECTRONIC_FUNDS_ACCOUNTS param
                cboc = new ChartBankObjectCode();
                cboc.setChartCode(getBankParametersAccessService().getKeyChartOfElectronicFunds());
                cboc.setAccountNumber(getBankParametersAccessService().getKeyAccountOfElectronicFunds());
                cboc.setObjectCode(getBankParametersAccessService().getObjectCodeForDefaultAD());
            }
        }

        LOG.debug("BankAdvanceDepositDocumentServiceImpl: findChartBankObjectCode  for " + bankTransaction + " = " + cboc);
        return cboc;
    }


    /*
     * Returns the appropriate Object Code from given ChartBankObjectCode
     */
    private ObjectCode findObjectCode(ChartBankObjectCode cboc) {
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, cboc.getChartCode());
        criteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, cboc.getObjectCode());

        ObjectCode result = (ObjectCode) boService.findByPrimaryKey(ObjectCode.class, criteria);
        if (result == null) {
            LOG.error("The ObjectCode specified [" + cboc + "] does not exist.");
            throw new RuntimeException("The ObjectCode specified [" + cboc + "] does not exist.");
        }
        LOG.debug("BankAdvanceDepositDocumentServiceImpl: findObjectCode for " + cboc + " = " + result.getCode());
        return result;
    }


}
