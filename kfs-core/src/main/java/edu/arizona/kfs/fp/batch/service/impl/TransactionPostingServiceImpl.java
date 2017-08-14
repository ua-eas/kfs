package edu.arizona.kfs.fp.batch.service.impl;

import edu.arizona.kfs.fp.batch.dataaccess.TransactionPostingDao;
import edu.arizona.kfs.fp.batch.dataaccess.impl.CheckReconciliationFileType;
import edu.arizona.kfs.fp.batch.service.BankDocumentService;
import edu.arizona.kfs.fp.batch.service.BankParametersAccessService;
import edu.arizona.kfs.fp.batch.service.TransactionPostingService;
import edu.arizona.kfs.fp.businessobject.BankTransaction;
import edu.arizona.kfs.sys.KFSConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.fp.businessobject.CreditCardVendor;
import org.kuali.kfs.krad.document.Document;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This service is used to load and process the consolidated Bank Transaction batch file (so-called tfile) for the Document Creation batch job
 * and record/create appropriate documents in the system for each transaction in the file
 */
@Transactional
public class TransactionPostingServiceImpl implements TransactionPostingService {
    private static final Logger LOG = Logger.getLogger(TransactionPostingServiceImpl.class);

    BankParametersAccessService bankParametersAccessService;
    TransactionPostingDao transactionPostingDao;
    CheckReconciliationFileType checkReconciliationFileType;
    private Map<String, BankDocumentService> bankDocServiceMap;


    /**
     * Reads each transactions in the consolidated bank transaction file and create the appropriate document for it:
     * Check recon file, DI, AD and CCR
     * <p>
     * If there are any errors, batch processing is stopped and everything is rolled back.
     *
     * @return True if transaction was posted with no errors, false otherwise.
     */
    @Transactional
    public List<String> postTransaction(BankTransaction bankTransaction) {
        LOG.info("Posting Transaction " + bankTransaction.toString());
        List<String> errorList = new ArrayList<String>();

        int baiTypeCode = bankTransaction.getBaiType().intValue();
        if (getBankParametersAccessService().isCheckReconBai(baiTypeCode)) {
            LOG.debug("postTransaction- isCheckReconBai TRUE");
            errorList = getCheckReconciliationFileType().writeRecord(bankTransaction);
            return errorList;
        }

        if (getBankParametersAccessService().isExcludedBai(baiTypeCode)) {
            // is excluded BAI type - just skip it.
            LOG.info("BAI is Excluded - Ignoring Transaction " + bankTransaction.toString());
            return errorList;
        }

        if (getBankParametersAccessService().isExcludedCustomerRefNbr(bankTransaction.getCustRefNo())) {
            // is excluded Customer Reference Number - skip it.
            LOG.info("Customer Reference Number is Excluded - Ignoring Transaction " + bankTransaction.toString());
            return errorList;
        }

        if (getBankParametersAccessService().isExcludedByBaiAndAccount(baiTypeCode, bankTransaction.getAccountNumber().toString())) {
            // is excluded by BAI Type and Account Number - skip it.
            LOG.info("Transaction excluded by BAI Code and Account Number - Ignoring Transaction " + bankTransaction.toString());
            return errorList;
        }

        //find what Type of BankDocument we need to create from BAI Type Code
        String documentType = getBankParametersAccessService().getDocumentTypeByBai(baiTypeCode);
        if (documentType.equals(KFSConstants.BankTransactionConstants.DOCUMENT_TYPE_CCR)) {
            // for a Credit Card Record, the Customer Reference Number holds the vendor number
            String vendorNumber = bankTransaction.getCustRefNo();
            CreditCardVendor ccv = getTransactionPostingDao().getCreditCardVendorObject(vendorNumber);

            if (ObjectUtils.isNull(ccv)) {
                // Wait we need to create a different type of doc: AD
                documentType = KFSConstants.BankTransactionConstants.DOCUMENT_TYPE_AD;
            } else {
                if (bankTransaction.getAmount().isNegative()) {
                    if (StringUtils.isBlank(ccv.getExpenseFinancialChartOfAccountsCode()) || StringUtils.isBlank(ccv.getExpenseAccountNumber()) || StringUtils.isBlank(ccv.getExpenseFinancialObjectCode())) {
                        documentType = KFSConstants.BankTransactionConstants.DOCUMENT_TYPE_AD;
                    }
                }
            }
        }
        LOG.debug("Post transaction - document type is " + documentType);

        //create appropriate Document for Bank Transaction
        BankDocumentService service = getBankDocServiceMap().get(documentType);
        Document doc = service.getDocument(bankTransaction);

        //save and blanket approve created document
        service.blanketApproveBankDocument(doc);

        LOG.debug("Completed postTransaction. ErrorList= " + (errorList == null ? "NULL" : errorList.size() + ":" + errorList.toString()));
        return errorList;
    }

    /**
     * If there is an output check recon file, it creates a .done for it and resets the timestamp for the filename.
     */
    public void finalizeCheckRecon(){
        //create a .done file in CheckReconFileType
        getCheckReconciliationFileType().createCheckReconDoneFile();
        //make sure a new instance of the CheckReconFileType is created before starting to post transactions to avoid file name collisions
        getCheckReconciliationFileType().resetFileNameTimestamp();
    }


    public boolean isDuplicateBatch(String bankTransactionsFileName, Timestamp timestampBatchDate, String bankTransactionBatchName) {
        return getTransactionPostingDao().isDuplicateBatch(bankTransactionsFileName, timestampBatchDate, bankTransactionBatchName);
    }


    public CheckReconciliationFileType getCheckReconciliationFileType() {
        return checkReconciliationFileType;
    }

    public void setCheckReconciliationFileType(CheckReconciliationFileType checkReconciliationFileType) {
        this.checkReconciliationFileType = checkReconciliationFileType;
    }

    public BankParametersAccessService getBankParametersAccessService() {
        return bankParametersAccessService;
    }

    public void setBankParametersAccessService(BankParametersAccessService bankParametersAccessService) {
        this.bankParametersAccessService = bankParametersAccessService;
    }


    public TransactionPostingDao getTransactionPostingDao() {
        return transactionPostingDao;
    }

    public void setTransactionPostingDao(TransactionPostingDao transactionPostingDao) {
        this.transactionPostingDao = transactionPostingDao;
    }

    public void setBankDocServiceMap(Map<String, BankDocumentService> serviceMap) {
        this.bankDocServiceMap = serviceMap;
    }

    public Map<String, BankDocumentService> getBankDocServiceMap() {
        return bankDocServiceMap;
    }
}
