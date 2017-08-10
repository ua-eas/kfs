package edu.arizona.kfs.module.purap.service.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.kns.util.KNSGlobalVariables;
import org.kuali.kfs.krad.document.DocumentBase;
import org.kuali.kfs.krad.exception.ValidationException;
import org.kuali.kfs.krad.service.DocumentService;
import org.kuali.kfs.krad.service.KualiRuleService;
import org.kuali.kfs.krad.util.ErrorMessage;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.workflow.service.WorkflowDocumentService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.service.SubAccountService;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.batch.ElectronicInvoiceStep;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoice;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceLoad;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceLoadSummary;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceOrder;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceRejectReason;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.ElectronicInvoiceRejectDocument;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.service.AccountsPayableService;
import org.kuali.kfs.module.purap.document.service.RequisitionService;
import org.kuali.kfs.module.purap.document.validation.event.AttributedCalculateAccountsPayableEvent;
import org.kuali.kfs.module.purap.document.validation.event.AttributedPaymentRequestForEInvoiceEvent;
import org.kuali.kfs.module.purap.exception.PurError;
import org.kuali.kfs.module.purap.service.impl.ElectronicInvoiceOrderHolder;
import org.kuali.kfs.module.purap.util.ExpiredOrClosedAccountEntry;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.krad.document.DocumentBase;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.rules.rule.event.SaveDocumentEvent;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;
import org.springframework.util.AutoPopulatingList;

import edu.arizona.kfs.gl.GeneralLedgerConstants;
import edu.arizona.kfs.module.purap.PurapKeyConstants;
import edu.arizona.kfs.module.purap.service.PurapAccountingService;
import edu.arizona.kfs.sys.KFSConstants;

/**
 * This is a helper service to parse electronic invoice file, match it with a PO and create PREQs based on the eInvoice. Also, it
 * provides helper methods to the reject document to match it with a PO and create PREQ.
 * Deprecation: KNSGlobalVariables marked as deprecated.
 */

@SuppressWarnings("deprecation")
public class ElectronicInvoiceHelperServiceImpl extends org.kuali.kfs.module.purap.service.impl.ElectronicInvoiceHelperServiceImpl {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicInvoiceHelperServiceImpl.class);

    protected DocumentService documentService;
    protected WorkflowDocumentService workflowDocumentService;
    protected KualiRuleService kualiRuleService;
    protected SubAccountService subAccountService;
    protected PurapAccountingService purapAccountingService;

    public void setSubAccountService(SubAccountService subAccountService) {
        this.subAccountService = subAccountService;
    }

    public void setPurapAccountingService(PurapAccountingService purapAccountingService) {
        this.purapAccountingService = purapAccountingService;
    }

    @Override
    @NonTransactional
    public ElectronicInvoiceLoad loadElectronicInvoices() {

        // add a step to check for directory paths
        prepareDirectories(getRequiredDirectoryNames());

        String rejectDirName = getRejectDirName();
        String acceptDirName = getAcceptDirName();
        emailTextErrorList = new StringBuffer();

        boolean moveFiles = SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(ElectronicInvoiceStep.class, PurapParameterConstants.ElectronicInvoiceParameters.FILE_MOVE_AFTER_LOAD_IND);

        int failedCnt = 0;

        if (LOG.isInfoEnabled()) {
            LOG.info("Invoice Base Directory - " + electronicInvoiceInputFileType.getDirectoryPath());
            LOG.info("Invoice Accept Directory - " + acceptDirName);
            LOG.info("Invoice Reject Directory - " + rejectDirName);
            LOG.info("Is moving files allowed - " + moveFiles);
        }

        if (StringUtils.isBlank(rejectDirName)) {
            throw new RuntimeException("Reject directory name should not be empty");
        }

        if (StringUtils.isBlank(acceptDirName)) {
            throw new RuntimeException("Accept directory name should not be empty");
        }

        File[] filesToBeProcessed = getFilesToBeProcessed();
        ElectronicInvoiceLoad eInvoiceLoad = new ElectronicInvoiceLoad();

        if (filesToBeProcessed == null || filesToBeProcessed.length == 0) {

            StringBuffer mailText = new StringBuffer();

            mailText.append("\n\n");
            mailText.append(PurapConstants.ElectronicInvoice.NO_FILES_PROCESSED_EMAIL_MESSAGE);
            mailText.append("\n\n");

            sendSummary(mailText);
            return eInvoiceLoad;
        }

        try {
            /**
             * Create, if not there
             */
            FileUtils.forceMkdir(new File(acceptDirName));
            FileUtils.forceMkdir(new File(rejectDirName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (LOG.isInfoEnabled()) {
            LOG.info(filesToBeProcessed.length + " file(s) available for processing");
        }

        StringBuilder emailMsg = new StringBuilder();

        for (File xmlFile : filesToBeProcessed) {
            deleteDoneFile(xmlFile);

            LOG.info("Processing " + xmlFile.getName() + "....");

            byte[] modifiedXML = null;
            // process only if file exists and not empty
            if (xmlFile.length() != 0L) {
                modifiedXML = addNamespaceDefinition(eInvoiceLoad, xmlFile);
            }

            boolean isRejected = false;

            if (modifiedXML == null) {// Not able to parse the xml
                isRejected = true;
            } else {
                try {
                    isRejected = processElectronicInvoice(eInvoiceLoad, xmlFile, modifiedXML);
                } catch (Exception e) {
                    String msg = xmlFile.getName() + "\n";
                    LOG.error(msg);

                    // since getMessage() is empty we'll compose the stack trace and nicely format it.
                    StackTraceElement[] elements = e.getStackTrace();
                    StringBuffer trace = new StringBuffer();
                    trace.append(e.getClass().getName());
                    if (e.getMessage() != null) {
                        trace.append(": ");
                        trace.append(e.getMessage());
                    }
                    trace.append("\n");
                    for (int j = 0; j < elements.length; ++j) {
                        StackTraceElement element = elements[j];

                        trace.append("    at ");
                        trace.append(describeStackTraceElement(element));
                        trace.append("\n");
                    }

                    LOG.error(trace);
                    emailMsg.append(msg);
                    msg += "\n--------------------------------------------------------------------------------------\n" + trace;
                    logProcessElectronicInvoiceError(msg);
                    failedCnt++;

                    /**
                     * Clear the error map, so that subsequent EIRT routing isn't prevented since rice
                     * is throwing a ValidationException if the error map is not empty before routing the doc.
                     */
                    GlobalVariables.getMessageMap().clearErrorMessages();

                    // Do not execute rest of code below
                    continue;
                }
            }

            /**
             * If there is a single order has rejects and the remainings are accepted in a invoice file,
             * then the entire file has been moved to the reject dir.
             */
            if (isRejected) {
                if (LOG.isInfoEnabled()) {
                    LOG.info(xmlFile.getName() + " has been rejected");
                }
                if (moveFiles) {
                    if (LOG.isInfoEnabled()) {
                        LOG.info(xmlFile.getName() + " has been marked to move to " + rejectDirName);
                    }
                    eInvoiceLoad.addRejectFileToMove(xmlFile, rejectDirName);
                }
            } else {
                if (LOG.isInfoEnabled()) {
                    LOG.info(xmlFile.getName() + " has been accepted");
                }
                if (moveFiles) {
                    if (!moveFile(xmlFile, acceptDirName)) {
                        String msg = xmlFile.getName() + " unable to move";
                        LOG.error(msg);
                        throw new PurError(msg);
                    }
                }
            }

            if (!moveFiles) {
                String fullPath = FilenameUtils.getFullPath(xmlFile.getAbsolutePath());
                String fileName = FilenameUtils.getBaseName(xmlFile.getAbsolutePath());
                File processedFile = new File(fullPath + File.separator + fileName + ".processed");
                try {
                    FileUtils.touch(processedFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }

        emailTextErrorList.append("\nFAILED FILES\n");
        emailTextErrorList.append("-----------------------------------------------------------\n\n");
        emailTextErrorList.append(emailMsg);
        emailTextErrorList.append("\nTOTAL COUNT\n");
        emailTextErrorList.append("===========================\n");
        emailTextErrorList.append("      " + failedCnt + " FAILED\n");
        emailTextErrorList.append("===========================\n");

        StringBuffer summaryText = saveLoadSummary(eInvoiceLoad);

        StringBuffer finalText = new StringBuffer();
        finalText.append(summaryText);
        finalText.append("\n");
        finalText.append(emailTextErrorList);
        sendSummary(finalText);

        LOG.info("Processing completed");

        return eInvoiceLoad;

    }

    @Override
    protected File[] getFilesToBeProcessed() {
        List<File> fileList = new ArrayList<File>();
        List<File> doneFileList = new ArrayList<File>();
        String baseDirName = getBaseDirName();
        File baseDir = new File(baseDirName);
        if (!baseDir.exists()) {
            throw new RuntimeException("Base dir [" + baseDirName + "] doesn't exists in the system");
        }
        doneFileList = Arrays.asList(baseDir.listFiles(new DoneFilenameFilter()));
        for (File donefile : doneFileList) {
            String fileName = donefile.getAbsolutePath();
            fileName = FilenameUtils.removeExtension(fileName) + KFSConstants.XML_EXTENSION;
            fileList.add(new File(fileName));
        }
        File[] filesToBeProcessed = fileList.toArray(new File[0]);

        return filesToBeProcessed;
    }

    @Override
    protected void deleteDoneFile(File invoiceFile) {
        String strippedOfName = FilenameUtils.removeExtension(invoiceFile.getAbsolutePath());
        String doneFile = strippedOfName + GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION;

        try {
            FileUtils.deleteQuietly(new File(doneFile));
        } catch (Exception e) {
            LOG.error("Error in ElectronicInvoiceHelperService in method deleteDoneFile");
            throw new RuntimeException(e);
        }
    }

    @Override
    protected StringBuffer saveLoadSummary(ElectronicInvoiceLoad eInvoiceLoad) {

        Map savedLoadSummariesMap = new HashMap();
        StringBuffer summaryMessage = new StringBuffer();
        int totalPREQCount = 0;
        int totalEIRTCount = 0;
        summaryMessage.append("PREQ/EIRT DOCUMENT SUMMARY by VENDOR\n");
        summaryMessage.append("-----------------------------------------------------------\n");

        for (Iterator iter = eInvoiceLoad.getInvoiceLoadSummaries().keySet().iterator(); iter.hasNext();) {

            String dunsNumber = (String) iter.next();
            ElectronicInvoiceLoadSummary eInvoiceLoadSummary = (ElectronicInvoiceLoadSummary) eInvoiceLoad.getInvoiceLoadSummaries().get(dunsNumber);

            if (!eInvoiceLoadSummary.isEmpty().booleanValue()) {
                LOG.info("Saving Load Summary for DUNS '" + dunsNumber + "'");

                ElectronicInvoiceLoadSummary currentLoadSummary = saveElectronicInvoiceLoadSummary(eInvoiceLoadSummary);

                summaryMessage.append("DUNS Number - " + eInvoiceLoadSummary.getVendorDescriptor() + ":\n");
                summaryMessage.append("     " + eInvoiceLoadSummary.getInvoiceLoadSuccessCount() + " PREQ successfully processed invoices for a total of $ " + eInvoiceLoadSummary.getInvoiceLoadSuccessAmount().doubleValue() + "\n");
                summaryMessage.append("     " + eInvoiceLoadSummary.getInvoiceLoadFailCount() + " EIRT rejected invoices for an approximate total of $ " + eInvoiceLoadSummary.getInvoiceLoadFailAmount().doubleValue() + "\n");
                totalPREQCount += eInvoiceLoadSummary.getInvoiceLoadSuccessCount();
                totalEIRTCount += eInvoiceLoadSummary.getInvoiceLoadFailCount();
                summaryMessage.append("\n\n");

                savedLoadSummariesMap.put(currentLoadSummary.getVendorDunsNumber(), eInvoiceLoadSummary);

            } else {
                LOG.info("Not saving Load Summary for DUNS '" + dunsNumber + "' because empty indicator is '" + eInvoiceLoadSummary.isEmpty().booleanValue() + "'");
            }
        }

        summaryMessage.append("\nREJECTED INVOICE DETAILS (EIRT)\n");
        summaryMessage.append("-----------------------------------------------------------\n");
        summaryMessage.append("TOTAL COUNTS\n");
        summaryMessage.append("===========================\n");
        summaryMessage.append("      " + totalPREQCount + " PREQ\n");
        summaryMessage.append("      " + totalEIRTCount + " EIRT\n");
        summaryMessage.append("===========================\n");

        for (Iterator rejectIter = eInvoiceLoad.getRejectDocuments().iterator(); rejectIter.hasNext();) {
            ElectronicInvoiceRejectDocument rejectDoc = (ElectronicInvoiceRejectDocument) rejectIter.next();
            routeRejectDocument(rejectDoc, savedLoadSummariesMap);
        }

        /**
         * Even if there is an exception in the reject doc routing, all the files marked as reject will
         * be moved to the reject dir
         */
        moveFileList(eInvoiceLoad.getRejectFilesToMove());

        return summaryMessage;
    }

    @Override
    protected PaymentRequestDocument createPaymentRequest(ElectronicInvoiceOrderHolder orderHolder) {

        if (LOG.isInfoEnabled()) {
            LOG.info("Creating Payment Request document");
        }

        KNSGlobalVariables.getMessageList().clear();

        validateInvoiceOrderValidForPREQCreation(orderHolder);

        if (LOG.isInfoEnabled()) {
            if (orderHolder.isInvoiceRejected()) {
                LOG.info("Not possible to convert einvoice details into payment request");
            } else {
                LOG.info("Payment request document creation validation succeeded");
            }
        }

        if (orderHolder.isInvoiceRejected()) {
            return null;
        }

        PaymentRequestDocument preqDoc = null;
        try {
            preqDoc = (PaymentRequestDocument) SpringContext.getBean(DocumentService.class).getNewDocument("PREQ");
        } catch (WorkflowException e) {
            String extraDescription = "Error=" + e.getMessage();
            ElectronicInvoiceRejectReason rejectReason = matchingService.createRejectReason(PurapConstants.ElectronicInvoice.PREQ_WORKLOW_EXCEPTION, extraDescription, orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason);
            LOG.error("Error creating Payment request document - " + e.getMessage());
            return null;
        }

        PurchaseOrderDocument poDoc = orderHolder.getPurchaseOrderDocument();
        if (poDoc == null) {
            throw new RuntimeException("Purchase Order document (POId=" + poDoc.getPurapDocumentIdentifier() + ") does not exist in the system");
        }

        preqDoc.getDocumentHeader().setDocumentDescription(generatePREQDocumentDescription(poDoc));
        try {
            preqDoc.updateAndSaveAppDocStatus(PurapConstants.PaymentRequestStatuses.APPDOC_IN_PROCESS);
        } catch (WorkflowException we) {
            throw new RuntimeException("Unable to save route status data for document: " + preqDoc.getDocumentNumber(), we);
        }

        preqDoc.setInvoiceDate(orderHolder.getInvoiceDate());
        preqDoc.setInvoiceNumber(orderHolder.getInvoiceNumber());
        preqDoc.setVendorInvoiceAmount(new KualiDecimal(orderHolder.getInvoiceNetAmount()));
        preqDoc.setAccountsPayableProcessorIdentifier("E-Invoice");
        preqDoc.setVendorCustomerNumber(orderHolder.getCustomerNumber());
        preqDoc.setPaymentRequestElectronicInvoiceIndicator(true);

        if (orderHolder.getAccountsPayablePurchasingDocumentLinkIdentifier() != null) {
            preqDoc.setAccountsPayablePurchasingDocumentLinkIdentifier(orderHolder.getAccountsPayablePurchasingDocumentLinkIdentifier());
        }

        // Copied from PaymentRequestServiceImpl.populatePaymentRequest()
        // set bank code to default bank code in the system parameter
        Bank defaultBank = SpringContext.getBean(BankService.class).getDefaultBankByDocType(preqDoc.getClass());
        if (defaultBank != null) {
            preqDoc.setBankCode(defaultBank.getBankCode());
            preqDoc.setBank(defaultBank);
        }

        RequisitionDocument reqDoc = SpringContext.getBean(RequisitionService.class).getRequisitionById(poDoc.getRequisitionIdentifier());
        String reqDocInitiator = reqDoc.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
        try {
            Person user = KimApiServiceLocator.getPersonService().getPerson(reqDocInitiator);

            setProcessingCampus(preqDoc, user.getCampusCode());

        } catch (Exception e) {
            String extraDescription = "Error setting processing campus code - " + e.getMessage() + "(" + generateMessageFromMessageMap() + ")";
            ElectronicInvoiceRejectReason rejectReason = matchingService.createRejectReason(PurapConstants.ElectronicInvoice.PREQ_ROUTING_VALIDATION_ERROR, extraDescription, orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason);
            return null;
        }

        HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList = SpringContext.getBean(AccountsPayableService.class).expiredOrClosedAccountsList(poDoc);
        if (expiredOrClosedAccountList == null) {
            expiredOrClosedAccountList = new HashMap<String, ExpiredOrClosedAccountEntry>();
        }

        if (LOG.isInfoEnabled()) {
            LOG.info(expiredOrClosedAccountList.size() + " accounts has been found as Expired or Closed");
        }

        preqDoc.populatePaymentRequestFromPurchaseOrder(orderHolder.getPurchaseOrderDocument(), expiredOrClosedAccountList);

        populateItemDetails(preqDoc, orderHolder);

        /**
         * Validate totals,paydate
         */
        kualiRuleService.applyRules(new AttributedCalculateAccountsPayableEvent(preqDoc));

        paymentRequestService.calculatePaymentRequest(preqDoc, true);

        processItemsForDiscount(preqDoc, orderHolder);

        if (orderHolder.isInvoiceRejected()) {
            return null;
        }

        paymentRequestService.calculatePaymentRequest(preqDoc, false);
        /**
         * PaymentRequestReview
         */
        kualiRuleService.applyRules(new AttributedPaymentRequestForEInvoiceEvent(preqDoc));

        if (GlobalVariables.getMessageMap().hasErrors()) {
            if (LOG.isInfoEnabled()) {
                LOG.info("***************Error in rules processing - " + GlobalVariables.getMessageMap());
            }

            String errors = generateMessageFromMessageMap();
            ElectronicInvoiceRejectReason rejectReason = matchingService.createRejectReason(PurapConstants.ElectronicInvoice.PREQ_ROUTING_VALIDATION_ERROR, errors, orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason);
            return null;
        }

        if (KNSGlobalVariables.getMessageList().size() > 0) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Payment request contains " + KNSGlobalVariables.getMessageList().size() + " warning message(s)");
                for (int i = 0; i < KNSGlobalVariables.getMessageList().size(); i++) {
                    LOG.info("Warning " + i + "  - " + KNSGlobalVariables.getMessageList().get(i));
                }
            }
        }

        addShipToNotes(preqDoc, orderHolder);

        String routingAnnotation = null;
        if (!orderHolder.isRejectDocumentHolder()) {
            routingAnnotation = "Routed by electronic invoice batch job";
        }

        try {
            LOG.info("Save and Route Document #" + preqDoc.getDocumentNumber());
            documentService.saveDocument(preqDoc);
            documentService.prepareWorkflowDocument(preqDoc);
            DocumentBase docBase = (DocumentBase) preqDoc;
            workflowDocumentService.route(docBase.getDocumentHeader().getWorkflowDocument(), routingAnnotation, null);
        } catch (WorkflowException e) {
            e.printStackTrace();
            ElectronicInvoiceRejectReason rejectReason = matchingService.createRejectReason(PurapConstants.ElectronicInvoice.PREQ_ROUTING_FAILURE, e.getMessage(), orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason);
            return null;
        } catch (ValidationException e) {
            String extraDescription = generateMessageFromMessageMap();
            ElectronicInvoiceRejectReason rejectReason = matchingService.createRejectReason(PurapConstants.ElectronicInvoice.PREQ_ROUTING_VALIDATION_ERROR, extraDescription, orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason);
            return null;
        }

        return preqDoc;
    }

    private String generateMessageFromMessageMap() {

        Map<String, AutoPopulatingList<ErrorMessage>> errorMessageMap = GlobalVariables.getMessageMap().getErrorMessages();

        List<ErrorMessage> errorMessageList = new ArrayList<ErrorMessage>();
        for (String index : errorMessageMap.keySet()) {
            for (ErrorMessage errorMessage : errorMessageMap.get(index)) {
                errorMessageList.add(errorMessage);
            }
        }

        StringBuilder extraDescription = new StringBuilder();
        for (ErrorMessage errorMessage : errorMessageList) {
            String errorMessageKey = errorMessage.getErrorKey();
            String[] errorMessageParameters = errorMessage.getMessageParameters();

            String message = kualiConfigurationService.getPropertyValueAsString(errorMessageKey);
            String formattedMessage = MessageFormat.format(message, (Object[]) errorMessageParameters);
            extraDescription.append(formattedMessage);
            extraDescription.append(", ");
        }

        if (extraDescription.length() >= 2) {
            extraDescription.setLength(extraDescription.length() - 2);
        }
        return extraDescription.toString();
    }

    @Override
    @NonTransactional
    public void validateInvoiceOrderValidForPREQCreation(ElectronicInvoiceOrderHolder orderHolder) {

        if (LOG.isInfoEnabled()) {
            LOG.info("Validiting ElectronicInvoice Order to make sure that it can be turned into a Payment Request document");
        }

        PurchaseOrderDocument poDoc = orderHolder.getPurchaseOrderDocument();

        if (poDoc == null) {
            throw new RuntimeException("PurchaseOrder not available");
        }

        if (!orderHolder.isInvoiceNumberAcceptIndicatorEnabled()) {
            List<?> preqs = paymentRequestService.getPaymentRequestsByVendorNumberInvoiceNumber(poDoc.getVendorHeaderGeneratedIdentifier(), poDoc.getVendorDetailAssignedIdentifier(), orderHolder.getInvoiceNumber());

            if (preqs != null && preqs.size() > 0) {
                ElectronicInvoiceRejectReason rejectReason = matchingService.createRejectReason(PurapConstants.ElectronicInvoice.INVOICE_ORDER_DUPLICATE, null, orderHolder.getFileName());
                orderHolder.addInvoiceOrderRejectReason(rejectReason, PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_FILE_NUMBER, PurapKeyConstants.ERROR_REJECT_INVOICE_DUPLICATE);
                return;
            }
        }

        if (orderHolder.getInvoiceDate() == null) {
            ElectronicInvoiceRejectReason rejectReason = matchingService.createRejectReason(PurapConstants.ElectronicInvoice.INVOICE_DATE_INVALID, null, orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason, PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_FILE_DATE, PurapKeyConstants.ERROR_REJECT_INVOICE_DATE_INVALID);
            return;
        } else if (orderHolder.getInvoiceDate().after(dateTimeService.getCurrentDate())) {
            ElectronicInvoiceRejectReason rejectReason = matchingService.createRejectReason(PurapConstants.ElectronicInvoice.INVOICE_DATE_GREATER, null, orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason, PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_FILE_DATE, PurapKeyConstants.ERROR_REJECT_INVOICE_DATE_GREATER);
            return;
        }

        addInvoiceOrderRejectionForInactiveSubAccountsIfRequired(orderHolder);
    }

    protected void populateItemDetails(PaymentRequestDocument preqDocument, ElectronicInvoiceOrderHolder orderHolder) {
        if (LOG.isInfoEnabled()) {
            LOG.info("Populating invoice order items into the payment request document");
        }

        @SuppressWarnings("unchecked")
        List<PurApItem> preqItems = preqDocument.getItems();

        // process all preq items and apply amounts from order holder
        for (int i = 0; i < preqItems.size(); i++) {
            PaymentRequestItem preqItem = (PaymentRequestItem) preqItems.get(i);
            processInvoiceItem(preqItem, orderHolder);
        }

        // as part of a clean up, remove any preq items that have zero or null unit/extended price
        removeEmptyItems(preqItems);

        if (LOG.isInfoEnabled()) {
            LOG.info("Successfully populated the invoice order items");
        }
    }

    @Override
    protected boolean isItemValidForUpdation(String itemTypeCode, String invoiceItemTypeCode, ElectronicInvoiceOrderHolder orderHolder) {
        boolean isItemTypeAvailableInItemMapping = orderHolder.isItemTypeAvailableInItemMapping(invoiceItemTypeCode);
        String itemTypeCodeFromMappings = orderHolder.getKualiItemTypeCodeFromMappings(itemTypeCode);
        return isItemTypeAvailableInItemMapping && StringUtils.equals(itemTypeCodeFromMappings, invoiceItemTypeCode);
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }

    public void setKualiRuleService(KualiRuleService kualiRuleService) {
        this.kualiRuleService = kualiRuleService;
    }

    @Override
    @NonTransactional
    public ElectronicInvoiceRejectDocument createRejectDocument(ElectronicInvoice eInvoice, ElectronicInvoiceOrder electronicInvoiceOrder, ElectronicInvoiceLoad eInvoiceLoad) {

        if (LOG.isInfoEnabled()) {
            LOG.info("Creating reject document [DUNS=" + eInvoice.getDunsNumber() + ",POID=" + electronicInvoiceOrder.getInvoicePurchaseOrderID() + "]");
        }

        ElectronicInvoiceRejectDocument eInvoiceRejectDocument;

        try {

            eInvoiceRejectDocument = (ElectronicInvoiceRejectDocument) SpringContext.getBean(DocumentService.class).getNewDocument("EIRT");

            eInvoiceRejectDocument.setInvoiceProcessTimestamp(SpringContext.getBean(DateTimeService.class).getCurrentTimestamp());
            String rejectdocDesc = generateRejectDocumentDescription(eInvoice, electronicInvoiceOrder);
            eInvoiceRejectDocument.getDocumentHeader().setDocumentDescription(rejectdocDesc);
            eInvoiceRejectDocument.setDocumentCreationInProgress(true);

            eInvoiceRejectDocument.setFileLevelData(eInvoice);
            eInvoiceRejectDocument.setInvoiceOrderLevelData(eInvoice, electronicInvoiceOrder);

            // MSU fix
            SpringContext.getBean(DocumentService.class).saveDocument(eInvoiceRejectDocument);

            String noteText = "Invoice file";
            attachInvoiceXMLWithRejectDoc(eInvoiceRejectDocument, getInvoiceFile(eInvoice.getFileName()), noteText);

            eInvoiceLoad.addInvoiceReject(eInvoiceRejectDocument);

        } catch (WorkflowException e) {
            throw new RuntimeException(e);
        }

        try {
            eInvoiceRejectDocument.validateBusinessRules(new SaveDocumentEvent(eInvoiceRejectDocument));
        } catch (RuntimeException e) {
            eInvoiceLoad.removeInvoiceReject(eInvoiceRejectDocument);
            throw e;
        }

        if (LOG.isInfoEnabled()) {
            LOG.info("Reject document has been created (DocNo=" + eInvoiceRejectDocument.getDocumentNumber() + ")");
        }

        emailTextErrorList.append("DUNS Number - " + eInvoice.getDunsNumber() + " " + eInvoice.getVendorName() + ":\n");
        emailTextErrorList.append("An Invoice from file '" + eInvoice.getFileName() + "' has been rejected due to the following error(s):\n");

        int index = 1;
        for (ElectronicInvoiceRejectReason reason : eInvoiceRejectDocument.getInvoiceRejectReasons()) {
            emailTextErrorList.append("    - " + reason.getInvoiceRejectReasonDescription() + "\n");
            addRejectReasonsToNote("Reject Reason " + index + ". " + reason.getInvoiceRejectReasonDescription(), eInvoiceRejectDocument);
            index++;
        }

        emailTextErrorList.append("\n");

        return eInvoiceRejectDocument;
    }

    /**
     * This method is responsible for adding an invoice reject reason for inactive sub accounts if required
     *
     * @param orderHolder
     *            - invoice order holder
     */
    private void addInvoiceOrderRejectionForInactiveSubAccountsIfRequired(ElectronicInvoiceOrderHolder orderHolder) {
        PurchaseOrderDocument poDoc = orderHolder.getPurchaseOrderDocument();

        if (poDoc != null) {
            List<SubAccount> inactiveSubAccounts = getInactiveSubAccountsFromPurchaseOrder(poDoc);

            if (!inactiveSubAccounts.isEmpty()) {
                StringBuilder inactiveSubAccountNumbers = new StringBuilder();

                // if we have multiple inactive sub accounts concatenate info
                for (SubAccount subAccount : inactiveSubAccounts) {
                    LOG.debug(subAccount.toString());

                    inactiveSubAccountNumbers.append(subAccount.getChartOfAccountsCode());
                    inactiveSubAccountNumbers.append("---");
                    inactiveSubAccountNumbers.append(subAccount.getAccountNumber());
                    inactiveSubAccountNumbers.append("---");
                    inactiveSubAccountNumbers.append(subAccount.getSubAccountNumber());
                    inactiveSubAccountNumbers.append(VendorConstants.NAME_DELIM);
                }
                inactiveSubAccountNumbers.setLength(inactiveSubAccountNumbers.length() - 2);

                String errors = generateMessageFromMessageMap();
                ElectronicInvoiceRejectReason rejectReason = matchingService.createRejectReason(PurapConstants.ElectronicInvoice.PREQ_ROUTING_VALIDATION_ERROR, errors, orderHolder.getFileName());

                // because the rejected invoice does not show sub accounts (these are found on the requisition),
                // lets customize the reject description to indicate that fact

                String rejectMessage = kualiConfigurationService.getPropertyValueAsString(PurapKeyConstants.ERROR_REJECT_INVOICE_INACTIVE_SUB_ACCOUNT);
                String formattedMessage = MessageFormat.format(rejectMessage, new Object[] { poDoc.getDocumentNumber(), inactiveSubAccountNumbers.toString() });
                rejectReason.setInvoiceRejectReasonDescription(formattedMessage);
                orderHolder.addInvoiceOrderRejectReason(rejectReason);
            }
        }
    }

    /**
     * This method is responsible for checking a purchase order for inactive sub accounts and returning a list of those sub accounts
     *
     * @param po
     *            - input purchase order to check for inactive sub accounts
     * @return list of inactive SubAccount objects
     */
    private List<SubAccount> getInactiveSubAccountsFromPurchaseOrder(PurchaseOrderDocument po) {
        List<SubAccount> retval = new ArrayList<SubAccount>();

        if (po != null) {
            // get list of active accounts
            List<SourceAccountingLine> accountList = purapAccountingService.generateSummary(po.getItemsActiveOnly());

            // loop through accounts
            for (SourceAccountingLine poAccountingLine : accountList) {
                if (poAccountingLine.getSubAccount() != null) {
                    SubAccount subAccount = subAccountService.getByPrimaryId(poAccountingLine.getChartOfAccountsCode(), poAccountingLine.getAccountNumber(), poAccountingLine.getSubAccountNumber());

                    if ((subAccount != null) && !subAccount.isActive()) {
                        retval.add(subAccount);
                    }
                }
            }
        }

        return retval;
    }

    protected class DoneFilenameFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".done");
        }
    }
}
