package edu.arizona.kfs.fp.batch.service.impl;

import edu.arizona.kfs.fp.batch.dataaccess.TransactionPostingDao;
import edu.arizona.kfs.fp.batch.service.BankDocumentService;
import edu.arizona.kfs.fp.batch.service.BankParametersAccessService;
import edu.arizona.kfs.fp.businessobject.BankTransaction;
import edu.arizona.kfs.sys.KFSConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.rules.rule.event.SaveOnlyDocumentEvent;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;


import java.util.Calendar;

/**
 * Created by nataliac on 5/3/17.
 */
public abstract class AbstractBankDocumentService implements BankDocumentService {
    private static final Logger LOG = Logger.getLogger(AbstractBankDocumentService.class);

    protected DocumentService documentService;
    protected WorkflowDocumentService workflowDocumentService;
    protected TransactionPostingDao transactionPostingDao;
    protected BankParametersAccessService bankParametersAccessService;
    protected BusinessObjectService boService;
    protected AccountingPeriodService accountingPeriodService;
    protected OptionsService optionsService;


    protected abstract KFSConstants.BankTransactionDocumentType getDocumentType();


    /**
     * This method builds the Header of the Document
     */
    protected void buildHeader(Document document, BankTransaction bankTransaction) {
        DocumentHeader documentHeader = document.getDocumentHeader();
        //set description
        String documentDescription = StringUtils.isBlank(bankTransaction.getDescriptiveTxt6())?bankTransaction.getBankReference():bankTransaction.getDescription();
        documentHeader.setDocumentDescription(StringUtils.left(documentDescription, KFSConstants.BankTransactionConstants.MAX_DESCRIPTION_LEN));

        //build explanation string
        String accountNumber = bankTransaction.getAccountNumber().toString();
        String baiTypeStr = bankTransaction.getBaiType().toString();
        String custRefNo = bankTransaction.getCustRefNo();
        accountNumber = (StringUtils.isBlank(accountNumber)) ? "EMPTY" : accountNumber;
        accountNumber = StringUtils.left(accountNumber, KFSConstants.BankTransactionConstants.MAX_NUMBER_LEN);
        custRefNo = (StringUtils.isBlank(custRefNo)) ? "EMPTY" : custRefNo;
        custRefNo = StringUtils.left(custRefNo, KFSConstants.BankTransactionConstants.MAX_NUMBER_LEN);
        String explanation = accountNumber + " - " + baiTypeStr + " - " + custRefNo;

        documentHeader.setExplanation(explanation);
        LOG.debug("buildHeader finished. Header = "+documentHeader.toString());
    }

    /*//TODO Seems deprecated since all this stuff should be set when the doc is saved and blanketApproved.
     *  Method implementing a hack to set the CreatedDate, ApprovedDate, FinalizedDate andLastModifiedDate to the given date...
     */
    protected void setRoutingDate(Document document, java.sql.Date date) {
        WorkflowDocument wfd = document.getDocumentHeader().getWorkflowDocument();
        wfd.getDocument();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
//TODO: Not sure what exactly the old code was trying to accomplish here and how to translate this to the new implementation...
//        try {
//            wfd.getActionsTaken()
//                    /*
//                     * if(!wfd.stateIsSaved()){ }
//                     */
//            // post processor caught exception while handling route status change: unable to load document 324562
//            // wfd.saveDocument("Saved");
//            wfd.getRouteHeader().setDateCreated(cal);
//            wfd.getRouteHeader().setDateApproved(cal);
//            wfd.getRouteHeader().setDateFinalized(cal);
//            wfd.getRouteHeader().setDateLastModified(cal);
//            wfd.saveRoutingData();
//        }
//        catch (WorkflowException ex) {
//            ex.printStackTrace();
//            throw new RuntimeException(ex);
//        }
    }

    protected void createDocumentNote(Document bankDocument, BankTransaction bankTransaction) {
        LOG.debug(" createDocumentNote() for " + bankTransaction.toString());
        try {
            String noteText = "Description: = " + bankTransaction.getDescriptiveTxt6() + " - " + bankTransaction.getDescriptiveTxt7();
            Note note = getDocumentService().createNoteFromDocument(bankDocument, noteText);
            bankDocument.addNote(note);
        } catch (Exception e) {
            LOG.error("Error when creating note for bankTransaction: " + bankTransaction.toString(), e);
            throw new RuntimeException("Error when creating note for bankTransaction: " + bankTransaction.toString(), e);
        }
    }

    /**
     * Returns trimmed down description from the bankTransaction to set in the document as following:
     *  1. If bankTransaction.DescriptiveTxt6 is empty, it will return the bankTransaction.BankReference
     *  2. Otherwise it will return bankTransaction.DescriptiveTxt6 concatenated with bankTransaction.DescriptiveTxt7
     *  Result above is trimmed down KFSConstants.BankTransactionConstants.MAX_DESCRIPTION_LEN leftmost charaters
     *
     * @param bankTransaction
     * @return
     */
    protected String getDocumentDescription(BankTransaction bankTransaction){
        String documentDescription = bankTransaction.getDescription();
        if ( StringUtils.isBlank(bankTransaction.getDescriptiveTxt6()) ){
            documentDescription = bankTransaction.getBankReference();
        }
        return StringUtils.left(documentDescription, KFSConstants.BankTransactionConstants.MAX_DESCRIPTION_LEN);
    }

    public void blanketApproveBankDocument(Document bankDocument) {
        LOG.info(" Saving and blanketApproving [" + bankDocument.getClass().toString() + "] " + bankDocument.getDocumentNumber());
        try {

            LOG.info(" Saving [" + bankDocument.getClass().toString() + "] " + bankDocument.getDocumentNumber());
            getDocumentService().saveDocument(bankDocument, SaveOnlyDocumentEvent.class);
            getDocumentService().saveDocumentNotes(bankDocument);
            LOG.info(" BlanketApproving [" + bankDocument.getClass().toString() + "] " + bankDocument.getDocumentNumber());
            getDocumentService().prepareWorkflowDocument(bankDocument);
            // calling workflow service to bypass business rule checks
            getWorkflowDocumentService().blanketApprove(bankDocument.getDocumentHeader().getWorkflowDocument(), "", null);
        } catch (Exception e) {
            LOG.error("Error when creating and blanketApproving document " + bankDocument.getDocumentNumber(), e);
            throw new RuntimeException("Could not create and blanketApproving document " + bankDocument.getDocumentNumber(), e);
        }
    }

    /**
     * Finds the appropriate Bank from bankTransaction AccountNumber. If none is configured for that particular AccountNumber,
     * it will return the default bank configured for the documentType.
     * If it can't find any default banks for the docType or AccountNumber, it will throw a Runtime exception.
     * @return Bank - non empty bank object.
     */
    protected Bank findBank(BankTransaction bankTransaction) {
        Bank bank = getTransactionPostingDao().getBankObjectByAccountNumber(bankTransaction.getAccountNumber().toString());
        if (ObjectUtils.isNull(bank)) {
            String bankCode = getBankParametersAccessService().getDefaultBankByDocType(getDocumentType());
            bank = new Bank();
            bank.setBankCode(bankCode);
        }

        if (bank == null) {
            LOG.error("Invalid System Configuration for  Account [" +bankTransaction.getAccountNumber()+ "] DocType ["+getDocumentType().name()+"]:  No bank or default bank configured.");
            throw new RuntimeException("Invalid System Configuration for Account [" +bankTransaction.getAccountNumber()+ "] DocType ["+getDocumentType().name()+"]:  No bank or default bank configured.");
        }

        LOG.debug("AbstractBankDocumentServiceImpl: findBank for " + bankTransaction + " = " + bank.getBankCode());
        return bank;
    }


    public DocumentService getDocumentService() {
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public WorkflowDocumentService getWorkflowDocumentService() {
        return workflowDocumentService;
    }

    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }

    public TransactionPostingDao getTransactionPostingDao() {
        return transactionPostingDao;
    }

    public void setTransactionPostingDao(TransactionPostingDao transactionPostingDao) {
        this.transactionPostingDao = transactionPostingDao;
    }

    public BankParametersAccessService getBankParametersAccessService() {
        return bankParametersAccessService;
    }

    public void setBankParametersAccessService(BankParametersAccessService bankParametersAccessService) {
        this.bankParametersAccessService = bankParametersAccessService;
    }

    public BusinessObjectService getBoService() {
        return boService;
    }

    public void setBoService(BusinessObjectService boService) {
        this.boService = boService;
    }

    public OptionsService getOptionsService() {
        return optionsService;
    }

    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }

    public AccountingPeriodService getAccountingPeriodService() {
        return accountingPeriodService;
    }

    public void setAccountingPeriodService(AccountingPeriodService accountingPeriodService) {
        this.accountingPeriodService = accountingPeriodService;
    }
}
