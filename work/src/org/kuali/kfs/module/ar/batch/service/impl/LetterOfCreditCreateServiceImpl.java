/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.batch.service.impl;

import java.io.IOException;
import java.io.PrintStream;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.ojb.broker.query.Criteria;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.batch.service.LetterOfCreditCreateService;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.dataaccess.CashControlDetailDao;
import org.kuali.kfs.module.ar.document.dataaccess.CashControlDocumentDao;
import org.kuali.kfs.module.ar.document.service.CashControlDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria;
import org.kuali.rice.kew.api.document.search.DocumentSearchResult;
import org.kuali.rice.kew.api.document.search.DocumentSearchResults;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Defines a service class for creating Cash Control documents from the LOC Review Document.
 */
public class LetterOfCreditCreateServiceImpl implements LetterOfCreditCreateService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LetterOfCreditCreateServiceImpl.class);
    public static final String WORKFLOW_SEARCH_RESULT_KEY = "routeHeaderId";
    protected CashControlDetailDao cashControlDetailDao;
    protected CashControlDocumentDao cashControlDocumentDao;
    protected CashControlDocumentService cashControlDocumentService;
    protected ConfigurationService configService;
    protected DocumentService documentService;
    protected FinancialSystemDocumentService financialSystemDocumentService;
    protected ParameterService parameterService;
    protected WorkflowDocumentService workflowDocumentService;

    @NonTransactional
    public ConfigurationService getConfigService() {
        return configService;
    }

    @NonTransactional
    public void setConfigService(ConfigurationService configService) {
        this.configService = configService;
    }

    @NonTransactional
    public ParameterService getParameterService() {
        return parameterService;
    }

    @NonTransactional
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
    /**
     * This method created cashcontrol documents and payment application based on the loc creation type and loc value passed.
     *
     * @param customerNumber
     * @param locCreationType
     * @param locValue
     * @param totalAmount
     * @param outputFileStream
     * @return
     */
    @Override
    @Transactional
    public String createCashControlDocuments(String customerNumber, String locCreationType, String locValue, KualiDecimal totalAmount, PrintStream outputFileStream) {
        String documentNumber = null;

        try {
            CashControlDocument cashControlDoc = (CashControlDocument) documentService.getNewDocument(CashControlDocument.class);
            cashControlDoc.getDocumentHeader().setDocumentDescription(configService.getPropertyValueAsString(ArKeyConstants.CASH_CTRL_DOC_CREATED_BY_BATCH));
            AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = new AccountsReceivableDocumentHeader();
            accountsReceivableDocumentHeader.setDocumentNumber(cashControlDoc.getDocumentNumber());
            // To get default processing chart code and org code from Paramters
            String defaultProcessingChartCode = parameterService.getParameterValueAsString(CashControlDocument.class, ArConstants.DEFAULT_PROCESSING_CHART);
            String defaultProcessingOrgCode = parameterService.getParameterValueAsString(CashControlDocument.class, ArConstants.DEFAULT_PROCESSING_ORG);
            accountsReceivableDocumentHeader.setProcessingChartOfAccountCode(defaultProcessingChartCode);
            accountsReceivableDocumentHeader.setProcessingOrganizationCode(defaultProcessingOrgCode);
            if (ObjectUtils.isNotNull(locCreationType) && ObjectUtils.isNotNull(locValue)) {
                cashControlDoc.setLetterOfCreditCreationType(locCreationType);
                if (cashControlDoc.getLetterOfCreditCreationType().equalsIgnoreCase(ArConstants.LOC_BY_LOC_FUND)) {
                    cashControlDoc.setLetterOfCreditFundCode(locValue);
                }
                else if (cashControlDoc.getLetterOfCreditCreationType().equalsIgnoreCase(ArConstants.LOC_BY_LOC_FUND_GRP)) {
                    cashControlDoc.setLetterOfCreditFundGroupCode(locValue);
                }
            }
            cashControlDoc.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);

            cashControlDoc.setCustomerPaymentMediumCode(ArConstants.PaymentMediumCode.LOC_WIRE);
            // To set invoice document type to CG Invoice as we would be dealing only with CG Invoices.
            cashControlDoc.setInvoiceDocumentType(ArConstants.CGIN_DOCUMENT_TYPE);

            // To create cash-control detail for the cash control document
            CashControlDetail cashControlDetail = new CashControlDetail();
            cashControlDetail.setCustomerNumber(customerNumber);// Assuming that the retrieved awards/invoices would have the same
                                                                // customer number.
            cashControlDetail.setFinancialDocumentLineAmount(totalAmount);
            // To set the date in cashcontrol detail to today.
            Timestamp ts = new Timestamp(new java.util.Date().getTime());
            java.sql.Date today = new java.sql.Date(ts.getTime());
            cashControlDetail.setCustomerPaymentDate(today);
            cashControlDetail.setReferenceFinancialDocumentNumber(cashControlDoc.getDocumentNumber());

            cashControlDocumentService.addNewCashControlDetail(configService.getPropertyValueAsString(ArKeyConstants.CREATED_BY_CASH_CTRL_DOC), cashControlDoc, cashControlDetail);

            documentService.saveDocument(cashControlDoc);

            documentNumber = cashControlDoc.getDocumentNumber();
        }
        catch (WorkflowException ex) {
            String error = "Error Creating Cash Control/Payment Application Documents for Customer Number#" + customerNumber;

            try {

                writeErrorEntry(error, outputFileStream);
            }
            catch (IOException ioe) {
                LOG.error("LetterOfCreditCreateServiceImpl.createCashControlDocuments Stopped: " + ioe.getMessage());
                throw new RuntimeException("LetterOfCreditCreateServiceImpl.createCashControlDocuments Stopped: " + ioe.getMessage(), ioe);
            }

        }

        return documentNumber;
    }


    /**
     * The method validates if there are any existing cash control documents for the same locValue and customer number combination.
     *
     * @param customerNumber
     * @param locCreationType
     * @param locValue
     * @param outputFileStream
     * @return
     */
    @Override
    @Transactional
    public boolean validatecashControlDocument(String customerNumber, String locCreationType, String locValue, PrintStream outputFileStream) {
        boolean isExists = false;
        Criteria criteria = new Criteria();
        criteria.addEqualTo(ArConstants.LETTER_OF_CREDIT_CREATION_TYPE, locCreationType);
        if (locCreationType.equalsIgnoreCase(ArConstants.LOC_BY_AWARD)) {
            criteria.addEqualTo(ArConstants.PROPOSAL_NUMBER, (new Long(locValue)));
        }
        else if (locCreationType.equalsIgnoreCase(ArConstants.LOC_BY_LOC_FUND)) {
            criteria.addEqualTo(ArConstants.LETTER_OF_CREDIT_FUND_CODE, locValue);
        }
        else if (locCreationType.equalsIgnoreCase(ArConstants.LOC_BY_LOC_FUND_GRP)) {
            criteria.addEqualTo(ArConstants.LETTER_OF_CREDIT_FUND_GROUP_CODE, locValue);
        }

        criteria.addNotEqualTo(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.APPROVED);
        criteria.addNotEqualTo(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.CANCELLED);
        criteria.addNotEqualTo(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.DISAPPROVED);

        CashControlDocument cashControlDocument = cashControlDocumentDao.getCashControlDocumentByCriteria(criteria);
        if (ObjectUtils.isNotNull(cashControlDocument)) {
            // Now to check if there is a cash control detail with the same customer number. - just double checking.
            List<CashControlDetail> cashControlDetails = new ArrayList<CashControlDetail>();
            List<CashControlDetail> cashCtrlDetails = cashControlDocument.getCashControlDetails();

            for (CashControlDetail cashControlDetail : cashCtrlDetails) {
                if (ObjectUtils.isNotNull(cashControlDetail.getCustomerNumber()) && cashControlDetail.getCustomerNumber().equals(customerNumber)) {
                    cashControlDetails.add(cashControlDetail);
                }
            }

            if (CollectionUtils.isNotEmpty(cashControlDetails)) {
                isExists = true;
                String error = ArConstants.BatchFileSystem.LOC_CREATION_ERROR__CSH_CTRL_IN_PROGRESS + " (" + cashControlDocument.getDocumentNumber() + ")" + " for Customer Number #" + customerNumber + " and LOC Value of " + locValue;
                try {

                    writeErrorEntry(error, outputFileStream);
                }
                catch (IOException ioe) {
                    LOG.error("LetterOfCreditCreateServiceImpl.validatecashControlDocument Stopped: " + ioe.getMessage());
                    throw new RuntimeException("LetterOfCreditCreateServiceImpl.validatecashControlDocument Stopped: " + ioe.getMessage(), ioe);
                }
            }

        }
        return isExists;
    }



    /**
     * This method retrieves all the cash control and payment application docs with a status of 'I' and routes them to the next step in the
     * routing path.
     *
     * @return True if the routing was performed successfully. A runtime exception will be thrown if any errors occur while routing.
     *
     */
    @Override
    @NonTransactional
    public boolean routeLOCDocuments() {
        List<String> cashControlDocumentIdList = null;
        List<String> payAppDocumentIdList = null;
        try {
            cashControlDocumentIdList = retrieveCashControlDocumentsToRoute(KewApiConstants.ROUTE_HEADER_SAVED_CD);
            payAppDocumentIdList = retrievePayAppDocumentsToRoute(KewApiConstants.ROUTE_HEADER_SAVED_CD);
        }
        catch (WorkflowException e1) {
            LOG.error("Error retrieving LOC documents for routing: " + e1.getMessage(), e1);
            throw new RuntimeException(e1.getMessage(), e1);
        }
        catch (RemoteException re) {
            LOG.error("Error retrieving LOC documents for routing: " + re.getMessage(), re);
            throw new RuntimeException(re.getMessage(), re);
        }

        if (LOG.isInfoEnabled()) {
            LOG.info("Cash control Documents to Route: " + cashControlDocumentIdList);
            LOG.info("Payment Application Documents to Route: " + payAppDocumentIdList);
        }
        //1. Cash control documents
        for (String cashControlDocId : cashControlDocumentIdList) {
            try {
                CashControlDocument cashControlDoc = (CashControlDocument) documentService.getByDocumentHeaderId(cashControlDocId);
                //To route documents only if the user in the session is same as the initiator.
                if(cashControlDoc.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId().equals(GlobalVariables.getUserSession().getPerson().getPrincipalId())){

                if (LOG.isInfoEnabled()) {
                    LOG.info("Routing Cash control document # " + cashControlDocId + ".");
                }
                documentService.prepareWorkflowDocument(cashControlDoc);

                // calling workflow service to bypass business rule checks
                workflowDocumentService.route(cashControlDoc.getDocumentHeader().getWorkflowDocument(), "", null);
            }
            }
            catch (WorkflowException e) {
                LOG.error("Error routing document # " + cashControlDocId + " " + e.getMessage());
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        //1. PAyment Applciation documents
        for (String payAppDocId : payAppDocumentIdList) {
            try {
                PaymentApplicationDocument payAppDoc = (PaymentApplicationDocument) documentService.getByDocumentHeaderId(payAppDocId);
                //To route documents only if the user in the session is same as the initiator.
                if(payAppDoc.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId().equals(GlobalVariables.getUserSession().getPerson().getPrincipalId())){

                if (LOG.isInfoEnabled()) {
                    LOG.info("Routing PAyment Application document # " + payAppDocId + ".");
                }
                documentService.prepareWorkflowDocument(payAppDoc);

                // calling workflow service to bypass business rule checks
                workflowDocumentService.route(payAppDoc.getDocumentHeader().getWorkflowDocument(), "", null);
            }
            }
            catch (WorkflowException e) {
                LOG.error("Error routing document # " + payAppDocId + " " + e.getMessage());
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        return true;
    }


    /**
     * Returns a list of all initiated but not yet routed documents, using the KualiWorkflowInfo service.
     *
     * @return a list of documents to route
     */
    @SuppressWarnings("deprecation")
    protected List<String> retrieveCashControlDocumentsToRoute(String statusCode) throws WorkflowException, RemoteException {
        List<String> documentIds = new ArrayList<String>();

        DocumentSearchCriteria.Builder criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(KFSConstants.FinancialDocumentTypeCodes.CASH_CONTROL);
        criteria.setDocumentStatuses(Collections.singletonList(DocumentStatus.fromCode(statusCode)));

        DocumentSearchCriteria crit = criteria.build();

        int maxResults = financialSystemDocumentService.getMaxResultCap(crit);
        int iterations = financialSystemDocumentService.getFetchMoreIterationLimit();

        for (int i = 0; i < iterations; i++) {
            LOG.debug("Fetch Iteration: "+ i);
            criteria.setStartAtIndex(maxResults * i);
            crit = criteria.build();
            LOG.debug("Max Results: "+criteria.getStartAtIndex());
        DocumentSearchResults results = KewApiServiceLocator.getWorkflowDocumentService().documentSearch(
                    GlobalVariables.getUserSession().getPrincipalId(), crit);
            if (results.getSearchResults().isEmpty()) {
                break;
            }
        for (DocumentSearchResult resultRow: results.getSearchResults()) {
            documentIds.add(resultRow.getDocument().getDocumentId());
                LOG.debug(resultRow.getDocument().getDocumentId());
        }
        }
        return documentIds;
    }


    /**
     * Returns a list of all initiated but not yet routed documents, using the KualiWorkflowInfo service.
     *
     * @return a list of documents to route
     */
    @SuppressWarnings("deprecation")
    protected List<String> retrievePayAppDocumentsToRoute(String statusCode) throws WorkflowException, RemoteException {
        List<String> documentIds = new ArrayList<String>();

        DocumentSearchCriteria.Builder criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(KFSConstants.FinancialDocumentTypeCodes.PAYMENT_APPLICATION);
        criteria.setDocumentStatuses(Collections.singletonList(DocumentStatus.fromCode(statusCode)));

        DocumentSearchCriteria crit = criteria.build();

        int maxResults = financialSystemDocumentService.getMaxResultCap(crit);
        int iterations = financialSystemDocumentService.getFetchMoreIterationLimit();

        for (int i = 0; i < iterations; i++) {
            LOG.debug("Fetch Iteration: "+ i);
            criteria.setStartAtIndex(maxResults * i);
            crit = criteria.build();
            LOG.debug("Max Results: "+criteria.getStartAtIndex());
        DocumentSearchResults results = KewApiServiceLocator.getWorkflowDocumentService().documentSearch(
                    GlobalVariables.getUserSession().getPrincipalId(), crit);
            if (results.getSearchResults().isEmpty()) {
                break;
            }
        for (DocumentSearchResult resultRow: results.getSearchResults()) {
            documentIds.add(resultRow.getDocument().getDocumentId());
                LOG.debug(resultRow.getDocument().getDocumentId());
        }
        }

        return documentIds;
    }

    /**
     * Retrieves the document id out of the route document header
     *
     * @param routeDocHeader the String representing an HTML link to the document
     * @return the document id
     */
    protected String parseDocumentIdFromRouteDocHeader(String routeDocHeader) {
        int rightBound = routeDocHeader.indexOf('>') + 1;
        int leftBound = routeDocHeader.indexOf('<', rightBound);
        return routeDocHeader.substring(rightBound, leftBound);
    }


    /**
     * This method writes issues to the error file.
     *
     * @param line
     * @param printStream
     * @throws IOException
     */
    protected void writeErrorEntry(String line, PrintStream printStream) throws IOException {
        try {
            printStream.printf("%s\n", line);
        }
        catch (Exception e) {
            throw new IOException(e.toString());
        }
    }


    /**
     * Gets the documentService attribute.
     *
     * @return Returns the documentService.
     */
    @NonTransactional
    public DocumentService getDocumentService() {
        return documentService;
    }


    /**
     * Sets the documentService attribute value.
     *
     * @param documentService The documentService to set.
     */
    @NonTransactional
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }


    /**
     * Gets the cashControlDocumentService attribute.
     *
     * @return Returns the cashControlDocumentService.
     */
    @NonTransactional
    public CashControlDocumentService getCashControlDocumentService() {
        return cashControlDocumentService;
    }


    /**
     * Sets the cashControlDocumentService attribute value.
     *
     * @param cashControlDocumentService The cashControlDocumentService to set.
     */
    @NonTransactional
    public void setCashControlDocumentService(CashControlDocumentService cashControlDocumentService) {
        this.cashControlDocumentService = cashControlDocumentService;
    }


    /**
     * Gets the cashControlDocumentDao attribute.
     *
     * @return Returns the cashControlDocumentDao.
     */
    @NonTransactional
    public CashControlDocumentDao getCashControlDocumentDao() {
        return cashControlDocumentDao;
    }


    /**
     * Sets the cashControlDocumentDao attribute value.
     *
     * @param cashControlDocumentDao The cashControlDocumentDao to set.
     */
    @NonTransactional
    public void setCashControlDocumentDao(CashControlDocumentDao cashControlDocumentDao) {
        this.cashControlDocumentDao = cashControlDocumentDao;
    }


    /**
     * Gets the cashControlDetailDao attribute.
     *
     * @return Returns the cashControlDetailDao.
     */
    @NonTransactional
    public CashControlDetailDao getCashControlDetailDao() {
        return cashControlDetailDao;
    }


    /**
     * Sets the cashControlDetailDao attribute value.
     *
     * @param cashControlDetailDao The cashControlDetailDao to set.
     */
    @NonTransactional
    public void setCashControlDetailDao(CashControlDetailDao cashControlDetailDao) {
        this.cashControlDetailDao = cashControlDetailDao;
    }

    @NonTransactional
    public WorkflowDocumentService getWorkflowDocumentService() {
        return workflowDocumentService;
    }

    @NonTransactional
    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }

    public FinancialSystemDocumentService getFinancialSystemDocumentService() {
        return financialSystemDocumentService;
    }

    public void setFinancialSystemDocumentService(FinancialSystemDocumentService financialSystemDocumentService) {
        this.financialSystemDocumentService = financialSystemDocumentService;
    }

}