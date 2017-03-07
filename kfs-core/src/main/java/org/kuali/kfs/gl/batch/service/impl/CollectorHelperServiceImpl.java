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
package org.kuali.kfs.gl.batch.service.impl;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.CollectorBatch;
import org.kuali.kfs.gl.batch.CollectorStep;
import org.kuali.kfs.gl.batch.service.CollectorHelperService;
import org.kuali.kfs.gl.batch.service.CollectorReportService;
import org.kuali.kfs.gl.batch.service.CollectorScrubberService;
import org.kuali.kfs.gl.businessobject.CollectorDetail;
import org.kuali.kfs.gl.businessobject.CollectorHeader;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.businessobject.OriginEntryInformation;
import org.kuali.kfs.gl.report.CollectorReportData;
import org.kuali.kfs.gl.report.PreScrubberReportData;
import org.kuali.kfs.gl.service.PreScrubberService;
import org.kuali.kfs.gl.service.SufficientFundsService;
import org.kuali.kfs.gl.service.impl.CollectorScrubberStatus;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.util.ErrorMessage;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.MessageMap;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.SystemGroupParameterNames;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.batch.service.WrappingBatchService;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.SufficientFundsItem;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.springframework.util.AutoPopulatingList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CollectorHelperServiceImpl implements CollectorHelperService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CollectorHelperServiceImpl.class);

    public static final String ALL = "All";
    public static final String COLLECTOR_CHECK_SUFFICIENT_FUNDS_IND = "COLLECTOR_CHECK_SUFFICIENT_FUNDS_IND";

    private ParameterService parameterService;
    private BatchInputFileService batchInputFileService;
    private CollectorScrubberService collectorScrubberService;
    private AccountService accountService;
    private PreScrubberService preScrubberService;
    private String batchFileDirectoryName;
    private SufficientFundsService sufficientFundsService;
    private BusinessObjectService businessObjectService;
    private CollectorReportService collectorReportService;
    private ReportWriterService collectorReportWriterService;

    @Override
    public List<ErrorMessage> loadCollectorApiData(InputStream inputStream, BatchInputFileType collectorInputFileType) {
        LOG.debug("loadCollectorApiData() started");

        initializeCollectorReportWriterService();

        List<ErrorMessage> errorMessages = new ArrayList<>();
        boolean isValid = true;

        CollectorReportData collectorReportData = new CollectorReportData();
        MessageMap fileMessageMap = collectorReportData.getMessageMapForFileName("api");

        List<CollectorBatch> batches = doCollectorFileParse(inputStream,"api", fileMessageMap, collectorInputFileType, collectorReportData);

        if ( batches != null ) {
            for (int i = 0; i < batches.size(); i++) {
                CollectorBatch batch = batches.get(i);

                batch.setBatchName("api Batch " + String.valueOf(i + 1));
                collectorReportData.addBatch(batch);

                MessageMap messageMap = batch.getMessageMap();

                // terminate if there were parse errors
                if (messageMap.hasErrors()) {
                    isValid = false;
                }

                if (isValid) {
                    collectorReportData.setNumInputDetails(batch);
                    // check totals
                    isValid = checkTrailerTotals(batch, collectorReportData, messageMap);
                }

                // do validation, base collector files rules and total checks
                if (isValid) {
                    isValid = performValidation(batch, messageMap);
                }

                // Load the GL entries if valid
                if (isValid) {
                    loadGlEntriesIntoGlPendingTable(batch);
                }
                collectorReportData.markValidationStatus(batch, isValid);

                Map<String, AutoPopulatingList<ErrorMessage>> messages = messageMap.getErrorMessages();
                messages.keySet().forEach(key -> messages.get(key).forEach(message -> errorMessages.add(message)));
            }
        }

        collectorReportService.generateCollectorRunReports(collectorReportData);

        Map<String,AutoPopulatingList<ErrorMessage>> messages = fileMessageMap.getErrorMessages();
        messages.keySet().forEach(key -> messages.get(key).forEach(message -> errorMessages.add(message)));

        return errorMessages;
    }

    @Override
    public boolean loadCollectorFile(String fileName, CollectorReportData collectorReportData, List<CollectorScrubberStatus> collectorScrubberStatuses, BatchInputFileType collectorInputFileType, PrintStream originEntryOutputPs) {
        LOG.debug("loadCollectorFile() started");

        boolean isValid = true;

        MessageMap fileMessageMap = collectorReportData.getMessageMapForFileName(fileName);

        List<CollectorBatch> batches = doCollectorFileParse(fileName, fileMessageMap, collectorInputFileType, collectorReportData);
        for (int i = 0; i < batches.size(); i++) {
            CollectorBatch collectorBatch = batches.get(i);

            collectorBatch.setBatchName(fileName + " Batch " + String.valueOf(i + 1));
            collectorReportData.addBatch(collectorBatch);

            isValid &= loadCollectorBatch(collectorBatch, fileName, i + 1, collectorReportData, collectorScrubberStatuses, collectorInputFileType, originEntryOutputPs);
        }
        return isValid;
    }

    protected void initializeCollectorReportWriterService() {
        ((WrappingBatchService)collectorReportWriterService).initialize();
    }

    protected void loadGlEntriesIntoGlPendingTable(CollectorBatch batch) {
        batch.getOriginEntries().stream().forEach(entry -> {
            GeneralLedgerPendingEntry pendingEntry = new GeneralLedgerPendingEntry(entry);
            pendingEntry.setAcctSufficientFundsFinObjCd(KFSConstants.NOT_AVAILABLE_STRING);
            pendingEntry.setFinancialDocumentApprovedCode(KFSConstants.DocumentStatusCodes.APPROVED);
            businessObjectService.save(pendingEntry);
        });
    }

    protected boolean loadCollectorBatch(CollectorBatch batch, String fileName, int batchIndex, CollectorReportData collectorReportData, List<CollectorScrubberStatus> collectorScrubberStatuses, BatchInputFileType collectorInputFileType, PrintStream originEntryOutputPs) {
        boolean isValid = true;

        MessageMap messageMap = batch.getMessageMap();
        // terminate if there were parse errors
        if (messageMap.hasErrors()) {
            isValid = false;
        }

        if (isValid) {
            collectorReportData.setNumInputDetails(batch);
            // check totals
            isValid = checkTrailerTotals(batch, collectorReportData, messageMap);
        }

        // do validation, base collector files rules and total checks
        if (isValid) {
            isValid = performValidation(batch, messageMap);
        }

        if (isValid) {
            // mark batch as valid
            collectorReportData.markValidationStatus(batch, true);

            prescrubParsedCollectorBatch(batch, collectorReportData);

            String collectorFileDirectoryName = collectorInputFileType.getDirectoryPath();
            // create a input file for scrubber
            String collectorInputFileNameForScrubber = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.COLLECTOR_BACKUP_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
            PrintStream inputFilePs = null;
            try {
                inputFilePs = new PrintStream(collectorInputFileNameForScrubber);

                for (OriginEntryFull entry : batch.getOriginEntries()) {
                    inputFilePs.printf("%s\n", entry.getLine());
                }
            } catch (IOException e) {
                throw new RuntimeException("loadCollectorFile Stopped: " + e.getMessage(), e);
            } finally {
                IOUtils.closeQuietly(inputFilePs);
            }

            CollectorScrubberStatus collectorScrubberStatus = collectorScrubberService.scrub(batch, collectorReportData, collectorFileDirectoryName);
            collectorScrubberStatuses.add(collectorScrubberStatus);
            processInterDepartmentalBillingAmounts(batch);

            // store origin group, entries, and collector details
            String collectorDemergerOutputFileName = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.COLLECTOR_DEMERGER_VAILD_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
            batch.setDefaultsAndStore(collectorReportData, collectorDemergerOutputFileName, originEntryOutputPs);
            collectorReportData.incrementNumPersistedBatches();
        } else {
            collectorReportData.incrementNumNonPersistedBatches();
            collectorReportData.incrementNumNotPersistedOriginEntryRecords(batch.getOriginEntries().size());
            collectorReportData.incrementNumNotPersistedCollectorDetailRecords(batch.getCollectorDetails().size());
            // mark batch as invalid
            collectorReportData.markValidationStatus(batch, false);
        }

        return isValid;
    }

    /**
     * Calls batch input service to parse the xml contents into an object. Any errors will be contained in GlobalVariables.MessageMap
     */
    protected List<CollectorBatch> doCollectorFileParse(String fileName, MessageMap messageMap, BatchInputFileType collectorInputFileType, CollectorReportData collectorReportData) {

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            LOG.error("doCollectorFileParse() file to parse not found " + fileName, e);
            collectorReportData.markUnparsableFileNames(fileName);
            throw new RuntimeException("Cannot find the file requested to be parsed " + fileName + " " + e.getMessage(), e);
        } catch (RuntimeException e) {
            collectorReportData.markUnparsableFileNames(fileName);
            throw e;
        }

        return doCollectorFileParse(inputStream,fileName,messageMap,collectorInputFileType,collectorReportData);
    }

    /**
     * Calls batch input service to parse the xml contents into an object. Any errors will be contained in GlobalVariables.MessageMap
     */
    protected List<CollectorBatch> doCollectorFileParse(InputStream inputStream, String fileName, MessageMap messageMap, BatchInputFileType collectorInputFileType, CollectorReportData collectorReportData) {

        List<CollectorBatch> parsedObject = null;
        try {
            byte[] fileByteContent = IOUtils.toByteArray(inputStream);
            parsedObject = (List<CollectorBatch>) batchInputFileService.parse(collectorInputFileType, fileByteContent);
        } catch (IOException e) {
            LOG.error("doCollectorFileParse() error while getting file bytes:  " + e.getMessage(), e);
            collectorReportData.markUnparsableFileNames(fileName);
            throw new RuntimeException("Error encountered while attempting to get file bytes: " + e.getMessage(), e);
        } catch (ParseException e1) {
            LOG.error("doCollectorFileParse() errors parsing file " + e1.getMessage(), e1);
            collectorReportData.markUnparsableFileNames(fileName);
            messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_PARSING_XML, new String[]{e1.getMessage()});
        } catch (RuntimeException e) {
            collectorReportData.markUnparsableFileNames(fileName);
            throw e;
        }

        return parsedObject;
    }

    protected void prescrubParsedCollectorBatch(CollectorBatch collectorBatch, CollectorReportData collectorReportData) {
        if (preScrubberService.deriveChartOfAccountsCodeIfSpaces()) {
            PreScrubberReportData preScrubberReportData = collectorReportData.getPreScrubberReportData();

            int inputRecords = collectorBatch.getOriginEntries().size();
            Set<String> noChartCodesCache = new HashSet<String>();
            Set<String> multipleChartCodesCache = new HashSet<String>();
            Map<String, String> accountNumberToChartCodeCache = new HashMap<String, String>();

            Iterator<?> originEntryAndDetailIterator = IteratorUtils.chainedIterator(collectorBatch.getOriginEntries().iterator(), collectorBatch.getCollectorDetails().iterator());
            while (originEntryAndDetailIterator.hasNext()) {
                Object originEntryOrDetail = originEntryAndDetailIterator.next();
                if (StringUtils.isBlank(extractChartOfAccountsCode(originEntryOrDetail))) {
                    String accountNumber = extractAccountNumber(originEntryOrDetail);

                    boolean nonExistent = false;
                    boolean multipleFound = false;
                    String chartOfAccountsCode = null;

                    if (noChartCodesCache.contains(accountNumber)) {
                        nonExistent = true;
                    } else if (multipleChartCodesCache.contains(accountNumber)) {
                        multipleFound = true;
                    } else if (accountNumberToChartCodeCache.containsKey(accountNumber)) {
                        chartOfAccountsCode = accountNumberToChartCodeCache.get(accountNumber);
                    } else {
                        Collection<Account> accounts = accountService.getAccountsForAccountNumber(accountNumber);
                        if (accounts.size() == 1) {
                            chartOfAccountsCode = accounts.iterator().next().getChartOfAccountsCode();
                            accountNumberToChartCodeCache.put(accountNumber, chartOfAccountsCode);
                        } else if (accounts.size() == 0) {
                            noChartCodesCache.add(accountNumber);
                            nonExistent = true;
                        } else {
                            multipleChartCodesCache.add(accountNumber);
                            multipleFound = true;
                        }
                    }

                    if (!nonExistent && !multipleFound) {
                        setChartOfAccountsCode(originEntryOrDetail, chartOfAccountsCode);
                    }
                }
            }

            preScrubberReportData.getAccountsWithMultipleCharts().addAll(multipleChartCodesCache);
            preScrubberReportData.getAccountsWithNoCharts().addAll(noChartCodesCache);
            preScrubberReportData.setInputRecords(preScrubberReportData.getInputRecords() + inputRecords);
            preScrubberReportData.setOutputRecords(preScrubberReportData.getOutputRecords() + inputRecords);
        }
    }

    protected String extractChartOfAccountsCode(Object originEntryOrDetail) {
        if (originEntryOrDetail instanceof OriginEntryInformation) {
            return ((OriginEntryInformation) originEntryOrDetail).getChartOfAccountsCode();
        }
        return ((CollectorDetail) originEntryOrDetail).getChartOfAccountsCode();
    }

    protected String extractAccountNumber(Object originEntryOrDetail) {
        if (originEntryOrDetail instanceof OriginEntryInformation) {
            return ((OriginEntryInformation) originEntryOrDetail).getAccountNumber();
        }
        return ((CollectorDetail) originEntryOrDetail).getAccountNumber();
    }

    protected void setChartOfAccountsCode(Object originEntryOrDetail, String chartOfAccountsCode) {
        if (originEntryOrDetail instanceof OriginEntryInformation) {
            ((OriginEntryInformation) originEntryOrDetail).setChartOfAccountsCode(chartOfAccountsCode);
        } else {
            ((CollectorDetail) originEntryOrDetail).setChartOfAccountsCode(chartOfAccountsCode);
        }
    }

    /**
     * Validates the contents of a parsed file.
     *
     * @param batch - batch to validate
     * @return boolean - true if validation was OK, false if there were errors
     */
    public boolean performValidation(CollectorBatch batch) {
        return performValidation(batch, GlobalVariables.getMessageMap());
    }

    /**
     * Performs the following checks on the collector batch: Any errors will be contained in GlobalVariables.MessageMap
     */
    protected boolean performValidation(CollectorBatch batch, MessageMap messageMap) {
        boolean valid = performCollectorHeaderValidation(batch, messageMap);

        performUppercasing(batch);

        boolean performDuplicateHeaderCheck = parameterService.getParameterValueAsBoolean(CollectorStep.class, SystemGroupParameterNames.COLLECTOR_PERFORM_DUPLICATE_HEADER_CHECK);
        if (valid && performDuplicateHeaderCheck) {
            valid = duplicateHeaderCheck(batch, messageMap);
        }
        if (valid) {
            valid = checkForMixedDocumentTypes(batch, messageMap);
        }

        if (valid) {
            valid = checkForMixedBalanceTypes(batch, messageMap);
        }

        if (valid) {
            valid = checkDetailKeys(batch, messageMap);
        }

        boolean performSufficientFundsCheck = parameterService.getParameterValueAsBoolean(KFSConstants.CoreModuleNamespaces.GL, ALL, COLLECTOR_CHECK_SUFFICIENT_FUNDS_IND);
        if (valid && performSufficientFundsCheck) {
            valid = checkSufficientFunds(batch, messageMap);
        }
        return valid;
    }

    /**
     * Uppercases sub-account, sub-object, and project fields
     *
     * @param batch CollectorBatch with data to uppercase
     */
    protected void performUppercasing(CollectorBatch batch) {
        for (OriginEntryFull originEntry : batch.getOriginEntries()) {
            if (StringUtils.isNotBlank(originEntry.getSubAccountNumber())) {
                originEntry.setSubAccountNumber(originEntry.getSubAccountNumber().toUpperCase());
            }

            if (StringUtils.isNotBlank(originEntry.getFinancialSubObjectCode())) {
                originEntry.setFinancialSubObjectCode(originEntry.getFinancialSubObjectCode().toUpperCase());
            }

            if (StringUtils.isNotBlank(originEntry.getProjectCode())) {
                originEntry.setProjectCode(originEntry.getProjectCode().toUpperCase());
            }
        }

        for (CollectorDetail detail : batch.getCollectorDetails()) {
            if (StringUtils.isNotBlank(detail.getSubAccountNumber())) {
                detail.setSubAccountNumber(detail.getSubAccountNumber().toUpperCase());
            }

            if (StringUtils.isNotBlank(detail.getFinancialSubObjectCode())) {
                detail.setFinancialSubObjectCode(detail.getFinancialSubObjectCode().toUpperCase());
            }
        }
    }

    protected boolean performCollectorHeaderValidation(CollectorBatch batch, MessageMap messageMap) {
        if (batch.isHeaderlessBatch()) {
            // if it's a headerless batch, don't validate the header, but it's still an error
            return false;
        }
        boolean valid = true;
        if (StringUtils.isBlank(batch.getChartOfAccountsCode())) {
            valid = false;
            messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.HEADER_CHART_CODE_REQUIRED);
        }
        if (StringUtils.isBlank(batch.getOrganizationCode())) {
            valid = false;
            messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.HEADER_ORGANIZATION_CODE_REQUIRED);
        }
        if (StringUtils.isBlank(batch.getCampusCode())) {
            valid = false;
            messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.HEADER_CAMPUS_CODE_REQUIRED);
        }
        if (StringUtils.isBlank(batch.getPhoneNumber())) {
            valid = false;
            messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.HEADER_PHONE_NUMBER_REQUIRED);
        }
        if (StringUtils.isBlank(batch.getMailingAddress())) {
            valid = false;
            messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.HEADER_MAILING_ADDRESS_REQUIRED);
        }
        if (StringUtils.isBlank(batch.getDepartmentName())) {
            valid = false;
            messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.HEADER_DEPARTMENT_NAME_REQUIRED);
        }
        return valid;
    }

    /**
     * Modifies the amounts in the ID Billing Detail rows, depending on specific business rules.
     *
     * @param batch a CollectorBatch to process
     */
    protected void processInterDepartmentalBillingAmounts(CollectorBatch batch) {
        for (CollectorDetail collectorDetail : batch.getCollectorDetails()) {
            String balanceTypeCode = getBalanceTypeCode(collectorDetail, batch);

            BalanceType balanceTyp = new BalanceType();
            balanceTyp.setFinancialBalanceTypeCode(balanceTypeCode);
            balanceTyp = (BalanceType) businessObjectService.retrieve(balanceTyp);
            if (balanceTyp == null) {
                LOG.info("processInterDepartmentalBillingAmounts() No balance type code found for ID billing record. " + collectorDetail);
                continue;
            }

            collectorDetail.refreshReferenceObject(KFSPropertyConstants.FINANCIAL_OBJECT);
            if (collectorDetail.getFinancialObject() == null) {
                LOG.info("processInterDepartmentalBillingAmounts() No object code found for ID billing record. " + collectorDetail);
                continue;
            }
        }
    }

    /**
     * Returns the balance type code for the interDepartmentalBilling record. This default implementation will look into the system
     * parameters to determine the balance type
     */
    protected String getBalanceTypeCode(CollectorDetail collectorDetail, CollectorBatch batch) {
        return collectorDetail.getFinancialBalanceTypeCode();
    }

    /**
     * Checks header against previously loaded batch headers for a duplicate submission.
     *
     * @param batch - batch to check
     * @return true if header if OK, false if header was used previously
     */
    protected boolean duplicateHeaderCheck(CollectorBatch batch, MessageMap messageMap) {
        boolean validHeader = true;

        CollectorHeader foundHeader = batch.retrieveDuplicateHeader();

        if (foundHeader != null) {
            LOG.error("duplicateHeaderCheck() batch header was matched to a previously loaded batch");
            messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.DUPLICATE_BATCH_HEADER);

            validHeader = false;
        }

        return validHeader;
    }

    /**
     * Iterates through the origin entries and builds a map on the document types. Then checks there was only one document type
     * found.
     *
     * @param batch - batch to check document types
     * @return true if there is only one document type, false if multiple document types were found.
     */
    protected boolean checkForMixedDocumentTypes(CollectorBatch batch, MessageMap messageMap) {
        boolean docTypesNotMixed = true;

        Set<String> batchDocumentTypes = new HashSet<>();
        for (OriginEntryFull entry : batch.getOriginEntries()) {
            batchDocumentTypes.add(entry.getFinancialDocumentTypeCode());
        }

        if (batchDocumentTypes.size() > 1) {
            LOG.error("checkForMixedDocumentTypes() mixed document types found in batch");
            messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.MIXED_DOCUMENT_TYPES);

            docTypesNotMixed = false;
        }

        return docTypesNotMixed;
    }

    /**
     * Iterates through the origin entries and builds a map on the balance types. Then checks there was only one balance type found.
     *
     * @param batch - batch to check balance types
     * @return true if there is only one balance type, false if multiple balance types were found
     */
    protected boolean checkForMixedBalanceTypes(CollectorBatch batch, MessageMap messageMap) {
        boolean balanceTypesNotMixed = true;

        Set<String> balanceTypes = new HashSet<>();
        for (OriginEntryFull entry : batch.getOriginEntries()) {
            balanceTypes.add(entry.getFinancialBalanceTypeCode());
        }

        if (balanceTypes.size() > 1) {
            LOG.error("checkForMixedBalanceTypes() mixed balance types found in batch");
            messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.MIXED_BALANCE_TYPES);

            balanceTypesNotMixed = false;
        }

        return balanceTypesNotMixed;
    }

    /**
     * Verifies each detail (id billing) record key has an corresponding gl entry in the same batch. The key is built by joining the
     * values of chart of accounts code, account number, sub account number, object code, and sub object code.
     *
     * @param batch - batch to validate
     * @return true if all detail records had matching keys, false otherwise
     */
    protected boolean checkDetailKeys(CollectorBatch batch, MessageMap messageMap) {
        boolean detailKeysFound = true;

        // build a Set of keys from the gl entries to compare with
        Set<String> glEntryKeys = new HashSet<>();
        for (OriginEntryFull entry : batch.getOriginEntries()) {
            glEntryKeys.add(generateOriginEntryMatchingKey(entry, ", "));
        }

        for (CollectorDetail collectorDetail : batch.getCollectorDetails()) {
            String collectorDetailKey = generateCollectorDetailMatchingKey(collectorDetail, ", ");
            if (!glEntryKeys.contains(collectorDetailKey)) {
                LOG.error("checkDetailKeys() found detail key without a matching gl entry key " + collectorDetailKey);
                messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.NONMATCHING_DETAIL_KEY, collectorDetailKey);

                detailKeysFound = false;
            }
        }

        return detailKeysFound;
    }

    /**
     * Checks for sufficient funds on each entry
     *
     * @param batch - batch to validate
     * @return true if all entries had sufficient funds, false otherwise
     */
    protected boolean checkSufficientFunds(CollectorBatch batch, MessageMap messageMap) {
        List<SufficientFundsItem> sufficientFundsItems = sufficientFundsService.checkSufficientFunds(batch.getOriginEntries());

        if ( sufficientFundsItems.size() > 0 ) {
            sufficientFundsItems.stream().forEach(item -> reportSufficientFundsError(item,messageMap));
            return false;
        }
        return true;
    }

    /**
     * Add error message for sufficient funds problem
     */
    protected void reportSufficientFundsError(SufficientFundsItem item,MessageMap messageMap) {
        StringBuilder key = new StringBuilder();
        key.append(item.getAccount().getChartOfAccountsCode());
        key.append("-");
        key.append(item.getAccount().getAccountNumber());
        key.append("-");
        key.append(item.getFinancialObject().getFinancialObjectCode());
        key.append("-");
        key.append(item.getDocumentTypeCode());
        key.append("-");
        key.append(item.getBalanceTyp().getFinancialBalanceTypeCode());
        key.append("-");
        key.append(item.getFinancialObjectType().getCode());
        key.append("-");
        key.append(item.getAmount());
        messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.INSUFFICIENT_FUNDS, key.toString());
    }

    /**
     * Generates a String representation of the OriginEntryFull's primary key
     *
     * @param entry     origin entry to get key from
     * @param delimiter the String delimiter to separate parts of the key
     * @return the key as a String
     */
    protected String generateOriginEntryMatchingKey(OriginEntryFull entry, String delimiter) {
        return StringUtils.join(new String[]{ObjectUtils.isNull(entry.getUniversityFiscalYear()) ? "" : entry.getUniversityFiscalYear().toString(), entry.getUniversityFiscalPeriodCode(), entry.getChartOfAccountsCode(), entry.getAccountNumber(), entry.getSubAccountNumber(), entry.getFinancialObjectCode(), entry.getFinancialSubObjectCode(), entry.getFinancialObjectTypeCode(), entry.getDocumentNumber(), entry.getFinancialDocumentTypeCode(), entry.getFinancialSystemOriginationCode()}, delimiter);
    }

    /**
     * Generates a String representation of the CollectorDetail's primary key
     *
     * @param collectorDetail collector detail to get key from
     * @param delimiter       the String delimiter to separate parts of the key
     * @return the key as a String
     */
    protected String generateCollectorDetailMatchingKey(CollectorDetail collectorDetail, String delimiter) {
        return StringUtils.join(new String[]{ObjectUtils.isNull(collectorDetail.getUniversityFiscalYear()) ? "" : collectorDetail.getUniversityFiscalYear().toString(), collectorDetail.getUniversityFiscalPeriodCode(), collectorDetail.getChartOfAccountsCode(), collectorDetail.getAccountNumber(), collectorDetail.getSubAccountNumber(), collectorDetail.getFinancialObjectCode(), collectorDetail.getFinancialSubObjectCode(), collectorDetail.getFinancialObjectTypeCode(), collectorDetail.getDocumentNumber(), collectorDetail.getFinancialDocumentTypeCode(), collectorDetail.getFinancialSystemOriginationCode()}, delimiter);
    }

    /**
     * Checks the batch total line count and amounts against the trailer. Any errors will be contained in GlobalVariables.MessageMap
     *
     * @param batch               batch to check totals for
     * @param collectorReportData collector report data (optional)
     */
    public boolean checkTrailerTotals(CollectorBatch batch, CollectorReportData collectorReportData) {
        return checkTrailerTotals(batch, collectorReportData, GlobalVariables.getMessageMap());
    }

    /**
     * Checks the batch total line count and amounts against the trailer. Any errors will be contained in GlobalVariables.MessageMap
     *
     * @param batch - batch to check totals for
     * @return boolean - true if validation was successful, false it not
     */
    protected boolean checkTrailerTotals(CollectorBatch batch, CollectorReportData collectorReportData, MessageMap messageMap) {
        boolean trailerTotalsMatch = true;

        int actualRecordCount = batch.getOriginEntries().size() + batch.getCollectorDetails().size();
        if (actualRecordCount != batch.getTotalRecords()) {
            LOG.error("checkTrailerTotals() trailer check on total count did not pass, expected count: " + String.valueOf(batch.getTotalRecords()) + ", actual count: " + String.valueOf(actualRecordCount));
            messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.TRAILER_ERROR_COUNTNOMATCH, String.valueOf(batch.getTotalRecords()), String.valueOf(actualRecordCount));
            trailerTotalsMatch = false;
        }

        OriginEntryTotals totals = batch.getOriginEntryTotals();

        if (batch.getOriginEntries().size() == 0) {
            if (!KualiDecimal.ZERO.equals(batch.getTotalAmount())) {
                LOG.error("checkTrailerTotals() trailer total should be zero when there are no origin entries");
                messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.TRAILER_ERROR_AMOUNT_SHOULD_BE_ZERO);
            }
            return false;
        }

        // retrieve document types that balance by equal debits and credits
        Collection<String> documentTypes = new ArrayList<>(parameterService.getParameterValuesAsString(CollectorStep.class, KFSConstants.SystemGroupParameterNames.COLLECTOR_EQUAL_DC_TOTAL_DOCUMENT_TYPES));

        boolean equalDebitCreditTotal = false;
        for (String documentType : documentTypes) {
            documentType = StringUtils.remove(documentType, "*").toUpperCase();
            if (batch.getOriginEntries().get(0).getFinancialDocumentTypeCode().startsWith(documentType)
                && KFSConstants.BALANCE_TYPE_ACTUAL.equals(batch.getOriginEntries().get(0).getFinancialBalanceTypeCode())) {
                equalDebitCreditTotal = true;
            }
        }

        if (equalDebitCreditTotal) {
            // credits must equal debits must equal total trailer amount
            if (!totals.getCreditAmount().equals(totals.getDebitAmount()) || !totals.getCreditAmount().equals(batch.getTotalAmount())) {
                LOG.error("checkTrailerTotals() trailer check on total amount did not pass, debit should equal credit, should equal trailer total");
                messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.TRAILER_ERROR_AMOUNTNOMATCH1, totals.getCreditAmount().toString(), totals.getDebitAmount().toString(), batch.getTotalAmount().toString());
                trailerTotalsMatch = false;
            }
        } else {
            // credits plus debits plus other amount must equal trailer
            KualiDecimal totalGlEntries = totals.getCreditAmount().add(totals.getDebitAmount()).add(totals.getOtherAmount());
            if (!totalGlEntries.equals(batch.getTotalAmount())) {
                LOG.error("checkTrailerTotals() trailer check on total amount did not pass, sum of gl entry amounts should equal trailer total");
                messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Collector.TRAILER_ERROR_AMOUNTNOMATCH2, totalGlEntries.toString(), batch.getTotalAmount().toString());
                trailerTotalsMatch = false;
            }
        }

        return trailerTotalsMatch;
    }

    public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
        this.batchInputFileService = batchInputFileService;
    }

    public void setCollectorScrubberService(CollectorScrubberService collectorScrubberService) {
        this.collectorScrubberService = collectorScrubberService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public void setPreScrubberService(PreScrubberService preScrubberService) {
        this.preScrubberService = preScrubberService;
    }

    public void setSufficientFundsService(SufficientFundsService sufficientFundsService) {
        this.sufficientFundsService = sufficientFundsService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setCollectorReportService(CollectorReportService collectorReportService) {
        this.collectorReportService = collectorReportService;
    }

    public void setCollectorReportWriterService(ReportWriterService collectorReportWriterService) {
        this.collectorReportWriterService = collectorReportWriterService;
    }
}
