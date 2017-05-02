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
package org.kuali.kfs.module.ld.document.web.struts;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.businessobject.CorrectionChange;
import org.kuali.kfs.gl.businessobject.CorrectionChangeGroup;
import org.kuali.kfs.gl.businessobject.CorrectionCriteria;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.document.CorrectionDocumentUtils;
import org.kuali.kfs.gl.document.authorization.CorrectionDocumentAuthorizer;
import org.kuali.kfs.gl.document.service.CorrectionDocumentService;
import org.kuali.kfs.gl.document.web.struts.GeneralLedgerCorrectionProcessAction;
import org.kuali.kfs.gl.document.web.struts.GeneralLedgerCorrectionProcessForm;
import org.kuali.kfs.gl.service.GlCorrectionProcessOriginEntryService;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.gl.service.OriginEntryService;
import org.kuali.kfs.kns.util.WebUtils;
import org.kuali.kfs.kns.web.struts.form.KualiTableRenderFormMetadata;
import org.kuali.kfs.kns.web.ui.Column;
import org.kuali.kfs.krad.service.SequenceAccessorService;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.module.ld.businessobject.options.CorrectionLaborGroupEntriesFinder;
import org.kuali.kfs.module.ld.businessobject.options.LaborOriginEntryFieldFinder;
import org.kuali.kfs.module.ld.document.LedgerCorrectionDocument;
import org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService;
import org.kuali.kfs.module.ld.service.LaborOriginEntryGroupService;
import org.kuali.kfs.module.ld.service.LaborOriginEntryService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.WorkflowDocument;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Struts Action Class for the Labor Ledger Correction Process.
 */
public class LedgerCorrectionAction extends GeneralLedgerCorrectionProcessAction {

    LaborOriginEntryService laborOriginEntryService = SpringContext.getBean(LaborOriginEntryService.class);

    /**
     * This needs to be done just in case they decide to execute.
     *
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#excute(org.apache.struts.action.ActionMapping,
     * org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     * <p>
     * KRAD Conversion: Lookupable performs customized sort on search results.
     * <p>
     * Uses data dictionary to get the metadata to render the columns.
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("execute() started");

        GeneralLedgerCorrectionProcessForm generalLedgerCorrectionProcessForm = (GeneralLedgerCorrectionProcessForm) form;

        // Init our services once
        if (originEntryGroupService == null) {
            GeneralLedgerCorrectionProcessAction.originEntryGroupService = (OriginEntryGroupService) SpringContext.getBean(LaborOriginEntryGroupService.class);
            ;
            GeneralLedgerCorrectionProcessAction.originEntryService = SpringContext.getBean(OriginEntryService.class);
            GeneralLedgerCorrectionProcessAction.dateTimeService = SpringContext.getBean(DateTimeService.class);
            GeneralLedgerCorrectionProcessAction.kualiConfigurationService = SpringContext.getBean(ConfigurationService.class);
        }

        LedgerCorrectionForm rForm = (LedgerCorrectionForm) form;
        if (LOG.isDebugEnabled()) {
            LOG.debug("execute() methodToCall: " + rForm.getMethodToCall());
        }
        Collection<OriginEntryFull> persistedOriginEntries = null;

        // If we are called from the docHandler or reload, ignore the persisted origin entries because we are either creating a new
        // document
        // or loading an old one
        if (!(KFSConstants.DOC_HANDLER_METHOD.equals(rForm.getMethodToCall()) || KFSConstants.RELOAD_METHOD_TO_CALL.equals(rForm.getMethodToCall()))) {
            restoreSystemAndEditMethod(rForm);
            restoreInputGroupSelectionForDatabaseEdits(rForm);
            if (!rForm.isRestrictedFunctionalityMode()) {
                if (StringUtils.isNotBlank(rForm.getGlcpSearchResultsSequenceNumber())) {
                    rForm.setAllEntries(SpringContext.getBean(GlCorrectionProcessOriginEntryService.class).retrieveAllEntries(rForm.getGlcpSearchResultsSequenceNumber()));
                    if (rForm.getAllEntries() == null) {
                        rForm.setDisplayEntries(null);
                    } else {
                        rForm.setDisplayEntries(new ArrayList<OriginEntryFull>(rForm.getAllEntries()));
                    }

                    if ((!"showOutputGroup".equals(rForm.getMethodToCall())) && rForm.getShowOutputFlag()) {
                        // reapply the any criteria to pare down the list if the match criteria only flag is checked
                        LedgerCorrectionDocument document = rForm.getLaborCorrectionDocument();
                        List<CorrectionChangeGroup> groups = document.getCorrectionChangeGroup();
                        //TODO:- need to change for LLCP
                        updateEntriesFromCriteria(rForm, rForm.isRestrictedFunctionalityMode());
                    }

                    if (!KFSConstants.TableRenderConstants.SORT_METHOD.equals(rForm.getMethodToCall())) {
                        // if sorting, we'll let the action take care of the sorting
                        KualiTableRenderFormMetadata originEntrySearchResultTableMetadata = rForm.getOriginEntrySearchResultTableMetadata();
                        if (originEntrySearchResultTableMetadata.getPreviouslySortedColumnIndex() != -1) {
                            List<Column> columns = SpringContext.getBean(LaborCorrectionDocumentService.class).getTableRenderColumnMetadata(rForm.getDocument().getDocumentNumber());

                            String propertyToSortName = columns.get(originEntrySearchResultTableMetadata.getPreviouslySortedColumnIndex()).getPropertyName();
                            Comparator valueComparator = columns.get(originEntrySearchResultTableMetadata.getPreviouslySortedColumnIndex()).getValueComparator();
                            sortList(rForm.getDisplayEntries(), propertyToSortName, valueComparator, originEntrySearchResultTableMetadata.isSortDescending());
                        }
                        if (rForm.getAllEntries() != null) {
                            int maxRowsPerPage = CorrectionDocumentUtils.getRecordsPerPage();
                            originEntrySearchResultTableMetadata.jumpToPage(originEntrySearchResultTableMetadata.getViewedPageNumber(), rForm.getDisplayEntries().size(), maxRowsPerPage);
                            originEntrySearchResultTableMetadata.setColumnToSortIndex(originEntrySearchResultTableMetadata.getPreviouslySortedColumnIndex());
                        }
                    }
                }
            }
        }

        ActionForward af = super.superExecute(mapping, form, request, response);
        return af;
    }

    /**
     * This needs to be done just in case they decide to save.
     *
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#save(org.apache.struts.action.ActionMapping,
     * org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("save() started");

        LedgerCorrectionForm ledgerCorrectionForm = (LedgerCorrectionForm) form;
        LedgerCorrectionDocument document = ledgerCorrectionForm.getLaborCorrectionDocument();

        // Did they pick the edit method and system?
        if (!checkMainDropdown(ledgerCorrectionForm)) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        if (!checkRestrictedFunctionalityModeForManualEdit(ledgerCorrectionForm)) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        if (!validGroupsItemsForDocumentSave(ledgerCorrectionForm)) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        if (!validChangeGroups(ledgerCorrectionForm)) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        if (!checkInputGroupPersistedForDocumentSave(ledgerCorrectionForm)) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // Populate document
        document.setCorrectionTypeCode(ledgerCorrectionForm.getEditMethod());
        document.setCorrectionSelection(ledgerCorrectionForm.getMatchCriteriaOnly());
        document.setCorrectionFileDelete(!ledgerCorrectionForm.getProcessInBatch());
        document.setCorrectionInputFileName(ledgerCorrectionForm.getInputGroupId());
        document.setCorrectionOutputFileName(null);
        if (ledgerCorrectionForm.getDataLoadedFlag() || ledgerCorrectionForm.isRestrictedFunctionalityMode()) {
            document.setCorrectionInputFileName(ledgerCorrectionForm.getInputGroupId());
        } else {
            document.setCorrectionInputFileName(null);
        }
        document.setCorrectionOutputFileName(null);

        SpringContext.getBean(LaborCorrectionDocumentService.class).persistOriginEntryGroupsForDocumentSave(document, ledgerCorrectionForm);
        if (LOG.isDebugEnabled()) {
            LOG.debug("save() doc type name: " + ledgerCorrectionForm.getDocTypeName());
        }
        ActionForward af = super.superSave(mapping, form, request, response);
        return af;
    }


    /**
     * Called when the document is loaded from action list or doc search or a new document is created.
     *
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#docHandler(org.apache.struts.action.ActionMapping,
     * org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("docHandler() started");

        LedgerCorrectionForm ledgerCorrectionForm = (LedgerCorrectionForm) form;
        String command = ledgerCorrectionForm.getCommand();

        if (KewApiConstants.INITIATE_COMMAND.equals(command)) {
            ledgerCorrectionForm.clearForm();
            createDocument(ledgerCorrectionForm);
        } else {
            loadDocument(ledgerCorrectionForm);

            LedgerCorrectionDocument laborDocument = ledgerCorrectionForm.getLaborCorrectionDocument();
            ledgerCorrectionForm.setInputGroupIdFromLastDocumentLoad(laborDocument.getCorrectionInputFileName());
            populateAuthorizationFields(ledgerCorrectionForm);
            Map<String, String> documentActions = ledgerCorrectionForm.getDocumentActions();
            if (documentActions.containsKey(KRADConstants.KUALI_ACTION_CAN_EDIT)) {
                // They have saved the document and they are retreiving it to be completed
                ledgerCorrectionForm.setProcessInBatch(!laborDocument.getCorrectionFileDelete());
                ledgerCorrectionForm.setMatchCriteriaOnly(laborDocument.getCorrectionSelection());
                ledgerCorrectionForm.setEditMethod(laborDocument.getCorrectionTypeCode());

                if (laborDocument.getCorrectionInputFileName() != null) {
                    if (CorrectionDocumentService.CORRECTION_TYPE_CRITERIA.equals(laborDocument.getCorrectionTypeCode())) {
                        loadPersistedInputGroup(ledgerCorrectionForm);
                        ledgerCorrectionForm.setDeleteFileFlag(false);
                    } else if (CorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(laborDocument.getCorrectionTypeCode())) {
                        // for the "true" param below, when the origin entries are persisted in the CorrectionDocumentService, they
                        // // are likely
                        // // not to have origin entry IDs assigned to them. So, we create pseudo entry IDs that are
                        // // unique within the allEntries list, but not necessarily within the DB. The persistence layer
                        // // is responsible for auto-incrementing entry IDs in the DB.
                        loadPersistedOutputGroup(ledgerCorrectionForm, true);

                        ledgerCorrectionForm.setManualEditFlag(true);
                        ledgerCorrectionForm.setEditableFlag(false);
                        ledgerCorrectionForm.setDeleteFileFlag(false);
                    } else if (CorrectionDocumentService.CORRECTION_TYPE_REMOVE_GROUP_FROM_PROCESSING.equals(laborDocument.getCorrectionTypeCode())) {
                        loadPersistedInputGroup(ledgerCorrectionForm);
                        ledgerCorrectionForm.setDeleteFileFlag(true);
                    } else {
                        throw new RuntimeException("Unknown edit method " + laborDocument.getCorrectionTypeCode());
                    }
                    ledgerCorrectionForm.setDataLoadedFlag(true);
                } else {
                    ledgerCorrectionForm.setDataLoadedFlag(false);
                }
                ledgerCorrectionForm.setShowOutputFlag(documentActions.containsKey(KRADConstants.KUALI_ACTION_CAN_APPROVE));
                if (ledgerCorrectionForm.getShowOutputFlag() && !ledgerCorrectionForm.isRestrictedFunctionalityMode()) {
                    updateEntriesFromCriteria(ledgerCorrectionForm, false);
                }
                ledgerCorrectionForm.setInputFileName(laborDocument.getCorrectionInputFileName());
                if (laborDocument.getCorrectionInputFileName() != null) {
                    ledgerCorrectionForm.setChooseSystem(CorrectionDocumentService.SYSTEM_UPLOAD);
                } else {
                    ledgerCorrectionForm.setChooseSystem(CorrectionDocumentService.SYSTEM_DATABASE);
                }

                ledgerCorrectionForm.setPreviousChooseSystem(ledgerCorrectionForm.getChooseSystem());
                ledgerCorrectionForm.setPreviousEditMethod(ledgerCorrectionForm.getEditMethod());
                ledgerCorrectionForm.setPreviousInputGroupId(ledgerCorrectionForm.getInputGroupId());
            } else {
                // They are calling this from their action list to look at it or approve it
                ledgerCorrectionForm.setProcessInBatch(!laborDocument.getCorrectionFileDelete());
                ledgerCorrectionForm.setMatchCriteriaOnly(laborDocument.getCorrectionSelection());

                // we don't care about setting entry IDs for the records below, so the param is false below
                loadPersistedOutputGroup(ledgerCorrectionForm, false);
                ledgerCorrectionForm.setShowOutputFlag(true);
            }
            ledgerCorrectionForm.setInputGroupIdFromLastDocumentLoadIsMissing(!originEntryGroupService.getGroupExists(laborDocument.getCorrectionInputFileName()));
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * This handles the action for uploading a file
     *
     * @see GeneralLedgerCorrectionProcessAction#uploadFile(org.apache.struts.action.ActionMapping,
     * org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward uploadFile(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException, IOException, Exception {
        LOG.debug("uploadFile() started");

        LedgerCorrectionForm ledgerCorrectionForm = (LedgerCorrectionForm) form;
        LedgerCorrectionDocument document = ledgerCorrectionForm.getLaborCorrectionDocument();

        Date now = GeneralLedgerCorrectionProcessAction.dateTimeService.getCurrentDate();
        //creat file after all enries loaded well
        //OriginEntryGroup newOriginEntryGroup = GeneralLedgerCorrectionProcessAction.originEntryGroupService.createGroup(today, OriginEntrySource.LABOR_CORRECTION_PROCESS_EDOC, false, false, false);

        FormFile sourceFile = ledgerCorrectionForm.getSourceFile();
        String llcpDirectory = SpringContext.getBean(LaborCorrectionDocumentService.class).getLlcpDirectoryName();
        String fullFileName = llcpDirectory + File.separator + sourceFile.getFileName() + "-" + GeneralLedgerCorrectionProcessAction.dateTimeService.toDateTimeStringForFilename(now);
        BufferedReader br = new BufferedReader(new InputStreamReader(sourceFile.getInputStream()));

        //create a file
        File uploadedFile = new File(fullFileName);
        PrintStream uploadedFilePrintStream;
        try {
            uploadedFilePrintStream = new PrintStream(uploadedFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //write entries to file
        int loadedCount = 0;
        String stringLine;
        while ((stringLine = br.readLine()) != null) {
            try {
                uploadedFilePrintStream.printf("%s\n", stringLine);
                loadedCount++;
            } catch (Exception e) {
                throw new IOException(e.toString());
            }
        }
        uploadedFilePrintStream.close();


        int recordCountFunctionalityLimit = CorrectionDocumentUtils.getRecordCountFunctionalityLimit();
        if (CorrectionDocumentUtils.isRestrictedFunctionalityMode(loadedCount, recordCountFunctionalityLimit)) {
            ledgerCorrectionForm.setRestrictedFunctionalityMode(true);
            ledgerCorrectionForm.setDataLoadedFlag(false);
            document.setCorrectionInputFileName(fullFileName);
            ledgerCorrectionForm.setInputFileName(fullFileName);

            if (CorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(ledgerCorrectionForm.getEditMethod())) {
                // the group size is not suitable for manual editing because it is too large
                if (recordCountFunctionalityLimit == CorrectionDocumentUtils.RECORD_COUNT_FUNCTIONALITY_LIMIT_IS_NONE) {
                    GlobalVariables.getMessageMap().putError(SYSTEM_AND_EDIT_METHOD_ERROR_KEY, KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_UNABLE_TO_MANUAL_EDIT_ANY_GROUP);
                } else {
                    GlobalVariables.getMessageMap().putError(SYSTEM_AND_EDIT_METHOD_ERROR_KEY, KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_UNABLE_TO_MANUAL_EDIT_LARGE_GROUP, String.valueOf(recordCountFunctionalityLimit));
                }
            }
        } else {
            ledgerCorrectionForm.setRestrictedFunctionalityMode(false);
            if (loadedCount > 0) {
                //now we can load all data from file
                List<LaborOriginEntry> originEntryList = new ArrayList();
                Map loadMessageMap = laborOriginEntryService.getEntriesByGroupIdWithPath(uploadedFile.getAbsolutePath(), originEntryList);
                //put errors on GlobalVariables
                if (loadMessageMap.size() > 0) {
                    Iterator iter = loadMessageMap.keySet().iterator();
                    while (iter.hasNext()) {
                        Integer lineNumber = (Integer) iter.next();
                        List<Message> messageList = (List<Message>) loadMessageMap.get(lineNumber);
                        if (messageList.size() > 0) {
                            for (Message errorMmessage : messageList) {
                                GlobalVariables.getMessageMap().putError("fileUpload", KFSKeyConstants.ERROR_INVALID_FORMAT_ORIGIN_ENTRY_FROM_TEXT_FILE, new String[]{lineNumber.toString(), errorMmessage.toString()});
                            }
                        }
                    }
                    return mapping.findForward(KFSConstants.MAPPING_BASIC);
                }
                // Set all the data that we know
                ledgerCorrectionForm.setDataLoadedFlag(true);
                ledgerCorrectionForm.setInputFileName(fullFileName);
                document.setCorrectionInputFileName(fullFileName);
                List<OriginEntryFull> originEntryFullList = new ArrayList();
                originEntryFullList.addAll(originEntryList);
                loadAllEntries(originEntryFullList, ledgerCorrectionForm);

                if (CorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(ledgerCorrectionForm.getEditMethod())) {
                    ledgerCorrectionForm.setEditableFlag(false);
                    ledgerCorrectionForm.setManualEditFlag(true);
                }
            } else {
                GlobalVariables.getMessageMap().putError("fileUpload", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_NO_RECORDS);
            }
        }

        if (document.getCorrectionChangeGroup().isEmpty()) {
            document.addCorrectionChangeGroup(new CorrectionChangeGroup());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * Show all entries for Manual edit with groupId and persist these entries to the DB The restricted functionality mode flag MUST
     * BE SET PRIOR TO CALLING this method.
     *
     * @param groupId        group ID
     * @param correctionForm correction form
     * @throws Exception
     */
//    protected void loadAllEntries(Integer groupId, GeneralLedgerCorrectionProcessForm correctionForm) throws Exception {
//        LOG.debug("loadAllEntries() started");
//
//        if (!correctionForm.isRestrictedFunctionalityMode()) {
//            GeneralLedgerCorrectionProcessDocument document = correctionForm.getCorrectionDocument();
//            List<LaborOriginEntry> laborSearchResults = laborOriginEntryService.getEntriesByGroupId(groupId);
//            List<OriginEntryFull> searchResults = new ArrayList();
//            searchResults.addAll(laborSearchResults);
//
//            correctionForm.setAllEntries(searchResults);
//            correctionForm.setDisplayEntries(new ArrayList<OriginEntryFull>(searchResults));
//
//            updateDocumentSummary(document, correctionForm.getAllEntries(), correctionForm.isRestrictedFunctionalityMode());
//
//            // if not in restricted functionality mode, then we can store these results temporarily in the GLCP origin entry service
//            SequenceAccessorService sequenceAccessorService = SpringContext.getBean(SequenceAccessorService.class);
//            String glcpSearchResultsSequenceNumber = String.valueOf(sequenceAccessorService.getNextAvailableSequenceNumber(KRADConstants.LOOKUP_RESULTS_SEQUENCE));
//
//            SpringContext.getBean(GlCorrectionProcessOriginEntryService.class).persistAllEntries(glcpSearchResultsSequenceNumber, searchResults);
//            correctionForm.setGlcpSearchResultsSequenceNumber(glcpSearchResultsSequenceNumber);
//
//            int maxRowsPerPage = CorrectionDocumentUtils.getRecordsPerPage();
//            KualiTableRenderFormMetadata originEntrySearchResultTableMetadata = correctionForm.getOriginEntrySearchResultTableMetadata();
//            originEntrySearchResultTableMetadata.jumpToFirstPage(correctionForm.getDisplayEntries().size(), maxRowsPerPage);
//            originEntrySearchResultTableMetadata.setColumnToSortIndex(-1);
//        }
//    }
    protected void loadAllEntries(String fileNameWithPath, LedgerCorrectionForm ledgerCorrectionForm) {
        LOG.debug("loadAllEntries() started");
        LedgerCorrectionDocument document = ledgerCorrectionForm.getLaborCorrectionDocument();

        if (!ledgerCorrectionForm.isRestrictedFunctionalityMode()) {
            List<LaborOriginEntry> laborSearchResults = new ArrayList();
            Map loadMessageMap = laborOriginEntryService.getEntriesByGroupIdWithPath(fileNameWithPath, laborSearchResults);
            List<OriginEntryFull> searchResults = new ArrayList();
            searchResults.addAll(laborSearchResults);

            //put errors on GlobalVariables
            if (loadMessageMap.size() > 0) {
                Iterator iter = loadMessageMap.keySet().iterator();
                while (iter.hasNext()) {
                    Integer lineNumber = (Integer) iter.next();
                    List<Message> messageList = (List<Message>) loadMessageMap.get(lineNumber);
                    for (Message errorMmessage : messageList) {
                        GlobalVariables.getMessageMap().putError("fileUpload", KFSKeyConstants.ERROR_INVALID_FORMAT_ORIGIN_ENTRY_FROM_TEXT_FILE, new String[]{lineNumber.toString(), errorMmessage.toString()});

                    }
                }
            } else {
                try {
                    loadAllEntries(searchResults, ledgerCorrectionForm);

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    /**
     * Save a changed row in the group
     *
     * @see GeneralLedgerCorrectionProcessAction#saveManualEntry(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward saveManualEntry(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("saveManualEdit() started");

        LedgerCorrectionForm ledgerCorrectionForm = (LedgerCorrectionForm) form;
        LedgerCorrectionDocument document = ledgerCorrectionForm.getLaborCorrectionDocument();

        if (validLaborOriginEntry(ledgerCorrectionForm)) {
            int entryId = ledgerCorrectionForm.getLaborEntryForManualEdit().getEntryId();

            // Find it and replace it with the one from the edit spot
            for (Iterator<OriginEntryFull> iter = ledgerCorrectionForm.getAllEntries().iterator(); iter.hasNext(); ) {
                OriginEntryFull element = iter.next();
                if (element.getEntryId() == entryId) {
                    iter.remove();
                }
            }

            ledgerCorrectionForm.updateLaborEntryForManualEdit();
            ledgerCorrectionForm.getAllEntries().add(ledgerCorrectionForm.getLaborEntryForManualEdit());

            // we've modified the list of all entries, so repersist it
            SpringContext.getBean(GlCorrectionProcessOriginEntryService.class).persistAllEntries(ledgerCorrectionForm.getGlcpSearchResultsSequenceNumber(), ledgerCorrectionForm.getAllEntries());
            // ledgerCorrectionForm.setDisplayEntries(null);
            ledgerCorrectionForm.setDisplayEntries(ledgerCorrectionForm.getAllEntries());

            if (ledgerCorrectionForm.getShowOutputFlag()) {
                removeNonMatchingEntries(ledgerCorrectionForm.getDisplayEntries(), document.getCorrectionChangeGroup());
            }

            // Clear out the additional row
            ledgerCorrectionForm.clearLaborEntryForManualEdit();
        }

        // Calculate the debit/credit/row count
        updateDocumentSummary(document, ledgerCorrectionForm.getAllEntries(), ledgerCorrectionForm.isRestrictedFunctionalityMode());

        // list has changed, we'll need to repage and resort
        applyPagingAndSortingFromPreviousPageView(ledgerCorrectionForm);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * Add a new row to the group
     *
     * @see GeneralLedgerCorrectionProcessAction#addManualEntry(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward addManualEntry(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("addManualEdit() started");

        LedgerCorrectionForm ledgerCorrectionForm = (LedgerCorrectionForm) form;
        LedgerCorrectionDocument document = ledgerCorrectionForm.getLaborCorrectionDocument();

        if (validLaborOriginEntry(ledgerCorrectionForm)) {
            ledgerCorrectionForm.updateLaborEntryForManualEdit();

            // new entryId is always 0, so give it a unique Id, SequenceAccessorService is used.
            //Long newEntryId = SpringContext.getBean(SequenceAccessorService.class).getNextAvailableSequenceNumber("GL_ORIGIN_ENTRY_T_SEQ");
            int newEntryId = getMaxEntryId(ledgerCorrectionForm.getAllEntries()) + 1;
            ledgerCorrectionForm.getEntryForManualEdit().setEntryId(new Integer(newEntryId));

            ledgerCorrectionForm.getAllEntries().add(ledgerCorrectionForm.getLaborEntryForManualEdit());

            // Clear out the additional row
            ledgerCorrectionForm.clearLaborEntryForManualEdit();
        }


        // Calculate the debit/credit/row count
        updateDocumentSummary(document, ledgerCorrectionForm.getAllEntries(), ledgerCorrectionForm.isRestrictedFunctionalityMode());

        ledgerCorrectionForm.setShowSummaryOutputFlag(true);

        // we've modified the list of all entries, so repersist it
        SpringContext.getBean(GlCorrectionProcessOriginEntryService.class).persistAllEntries(ledgerCorrectionForm.getGlcpSearchResultsSequenceNumber(), ledgerCorrectionForm.getAllEntries());
        ledgerCorrectionForm.setDisplayEntries(new ArrayList<OriginEntryFull>(ledgerCorrectionForm.getAllEntries()));
        if (ledgerCorrectionForm.getShowOutputFlag()) {
            removeNonMatchingEntries(ledgerCorrectionForm.getDisplayEntries(), document.getCorrectionChangeGroup());
        }

        // list has changed, we'll need to repage and resort
        applyPagingAndSortingFromPreviousPageView(ledgerCorrectionForm);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Handles manual edit of labor correction form
     *
     * @see GeneralLedgerCorrectionProcessAction#manualEdit(org.apache.struts.action.ActionMapping,
     * org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward manualEdit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

        LedgerCorrectionForm ledgerCorrectionForm = (LedgerCorrectionForm) form;
        LedgerCorrectionDocument document = ledgerCorrectionForm.getLaborCorrectionDocument();
        ledgerCorrectionForm.clearLaborEntryForManualEdit();

        ledgerCorrectionForm.clearEntryForManualEdit();
        ledgerCorrectionForm.setEditableFlag(true);
        ledgerCorrectionForm.setManualEditFlag(false);

        if (document.getCorrectionChangeGroup().isEmpty()) {
            document.addCorrectionChangeGroup(new CorrectionChangeGroup());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Handles edit of manual entry
     *
     * @see GeneralLedgerCorrectionProcessAction#editManualEntry(org.apache.struts.action.ActionMapping,
     * org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward editManualEntry(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("editManualEdit() started");

        LedgerCorrectionForm ledgerCorrectionForm = (LedgerCorrectionForm) form;
        LedgerCorrectionDocument document = ledgerCorrectionForm.getLaborCorrectionDocument();

        int entryId = Integer.parseInt(getImageContext(request, "entryId"));

        // Find it and put it in the editing spot
        for (Iterator iter = ledgerCorrectionForm.getAllEntries().iterator(); iter.hasNext(); ) {
            LaborOriginEntry element = (LaborOriginEntry) iter.next();
            if (element.getEntryId() == entryId) {
                ledgerCorrectionForm.setLaborEntryForManualEdit(element);
                ledgerCorrectionForm.setLaborEntryFinancialDocumentReversalDate(CorrectionDocumentUtils.convertToString(element.getFinancialDocumentReversalDate(), "Date"));
                ledgerCorrectionForm.setLaborEntryTransactionDate(CorrectionDocumentUtils.convertToString(element.getTransactionDate(), "Date"));
                ledgerCorrectionForm.setLaborEntryTransactionLedgerEntryAmount(CorrectionDocumentUtils.convertToString(element.getTransactionLedgerEntryAmount(), "KualiDecimal"));
                ledgerCorrectionForm.setLaborEntryTransactionLedgerEntrySequenceNumber(CorrectionDocumentUtils.convertToString(element.getTransactionLedgerEntrySequenceNumber(), "Integer"));
                ledgerCorrectionForm.setLaborEntryUniversityFiscalYear(CorrectionDocumentUtils.convertToString(element.getUniversityFiscalYear(), "Integer"));

                ledgerCorrectionForm.setLaborEntryTransactionPostingDate(CorrectionDocumentUtils.convertToString(element.getTransactionPostingDate(), "Date"));
                ledgerCorrectionForm.setLaborEntryPayPeriodEndDate(CorrectionDocumentUtils.convertToString(element.getPayPeriodEndDate(), "Date"));
                ledgerCorrectionForm.setLaborEntryTransactionTotalHours(CorrectionDocumentUtils.convertToString(element.getTransactionTotalHours(), "BigDecimal"));
                ledgerCorrectionForm.setLaborEntryPayrollEndDateFiscalYear(CorrectionDocumentUtils.convertToString(element.getPayrollEndDateFiscalYear(), "Integer"));
                ledgerCorrectionForm.setLaborEntryEmployeeRecord(CorrectionDocumentUtils.convertToString(element.getEmployeeRecord(), "Integer"));

                break;
            }
        }

        ledgerCorrectionForm.setShowSummaryOutputFlag(true);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method is for validation of Labor Origin Entry
     *
     * @param ledgerCorrectionForm
     * @return boolean
     */
    protected boolean validLaborOriginEntry(LedgerCorrectionForm ledgerCorrectionForm) {
        LOG.debug("validOriginEntry() started");

        LaborOriginEntry oe = ledgerCorrectionForm.getLaborEntryForManualEdit();

        boolean valid = true;
        LaborOriginEntryFieldFinder loeff = new LaborOriginEntryFieldFinder();
        List fields = loeff.getKeyValues();
        for (Iterator iter = fields.iterator(); iter.hasNext(); ) {
            KeyValue lkp = (KeyValue) iter.next();

            // Get field name, type, length & value on the form
            String fieldName = (String) lkp.getKey();
            String fieldDisplayName = lkp.getValue();
            String fieldType = loeff.getFieldType(fieldName);
            int fieldLength = loeff.getFieldLength(fieldName);
            String fieldValue = null;
            if ("String".equals(fieldType)) {
                fieldValue = (String) oe.getFieldValue(fieldName);
            } else if (KFSPropertyConstants.FINANCIAL_DOCUMENT_REVERSAL_DATE.equals(fieldName)) {
                fieldValue = ledgerCorrectionForm.getLaborEntryFinancialDocumentReversalDate();
            } else if (KFSPropertyConstants.TRANSACTION_DATE.equals(fieldName)) {
                fieldValue = ledgerCorrectionForm.getLaborEntryTransactionDate();
            } else if (KFSPropertyConstants.TRN_ENTRY_LEDGER_SEQUENCE_NUMBER.equals(fieldName)) {
                fieldValue = ledgerCorrectionForm.getLaborEntryTransactionLedgerEntrySequenceNumber();
            } else if (KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT.equals(fieldName)) {
                fieldValue = ledgerCorrectionForm.getLaborEntryTransactionLedgerEntryAmount();
            } else if (KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR.equals(fieldName)) {
                fieldValue = ledgerCorrectionForm.getLaborEntryUniversityFiscalYear();
            }

            // for Labor Specified fields
            else if (KFSPropertyConstants.TRANSACTION_POSTING_DATE.equals(fieldName)) {
                fieldValue = ledgerCorrectionForm.getLaborEntryTransactionPostingDate();
            } else if (KFSPropertyConstants.PAY_PERIOD_END_DATE.equals(fieldName)) {
                fieldValue = ledgerCorrectionForm.getLaborEntryPayPeriodEndDate();
            } else if (KFSPropertyConstants.TRANSACTION_TOTAL_HOURS.equals(fieldName)) {
                fieldValue = ledgerCorrectionForm.getLaborEntryTransactionTotalHours();
            } else if (KFSPropertyConstants.PAYROLL_END_DATE_FISCAL_YEAR.equals(fieldName)) {
                fieldValue = ledgerCorrectionForm.getLaborEntryPayrollEndDateFiscalYear();
            } else if (KFSPropertyConstants.EMPLOYEE_RECORD.equals(fieldName)) {
                fieldValue = ledgerCorrectionForm.getLaborEntryEmployeeRecord();
            }


            // Now check that the data is valid
            if (!StringUtils.isEmpty(fieldValue)) {
                if (!loeff.isValidValue(fieldName, fieldValue)) {
                    GlobalVariables.getMessageMap().putError("searchResults", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_INVALID_VALUE, new String[]{fieldDisplayName, fieldValue});
                    valid = false;
                }
            } else if (!loeff.allowNull(fieldName)) {
                GlobalVariables.getMessageMap().putError("searchResults", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_INVALID_VALUE, new String[]{fieldDisplayName, fieldValue});
                valid = false;
            }
        }

        return valid;
    }

    /**
     * @see GeneralLedgerCorrectionProcessAction#removeNonMatchingEntries(java.util.Collection,
     * java.util.Collection)
     */
    protected void removeNonMatchingEntries(Collection<OriginEntryFull> entries, Collection<CorrectionChangeGroup> groups) {
        Iterator<OriginEntryFull> loei = entries.iterator();
        while (loei.hasNext()) {
            OriginEntryFull oe = loei.next();
            if (!org.kuali.kfs.module.ld.util.CorrectionDocumentUtils.doesLaborEntryMatchAnyCriteriaGroups(oe, groups)) {
                loei.remove();
            }
        }
    }

    /**
     * Validate all the correction groups
     *
     * @param doc
     * @return if valid, return true, false if not
     */
    @Override
    protected boolean validChangeGroups(GeneralLedgerCorrectionProcessForm generalLedgerCorrectionProcessForm) {
        LOG.debug("validChangeGroups() started");

        LedgerCorrectionForm form = (LedgerCorrectionForm) generalLedgerCorrectionProcessForm;
        LedgerCorrectionDocument doc = form.getLaborCorrectionDocument();
        String tab = "";
        if (CorrectionDocumentService.CORRECTION_TYPE_CRITERIA.equals(form.getEditMethod())) {
            tab = "editCriteria";
        } else {
            tab = "manualEditCriteria";
        }

        boolean allValid = true;

        LaborOriginEntryFieldFinder loeff = new LaborOriginEntryFieldFinder();
        List fields = loeff.getKeyValues();

        List l = doc.getCorrectionChangeGroup();
        for (Iterator iter = l.iterator(); iter.hasNext(); ) {
            CorrectionChangeGroup ccg = (CorrectionChangeGroup) iter.next();
            for (Iterator iterator = ccg.getCorrectionCriteria().iterator(); iterator.hasNext(); ) {
                CorrectionCriteria cc = (CorrectionCriteria) iterator.next();
                if (!loeff.isValidValue(cc.getCorrectionFieldName(), cc.getCorrectionFieldValue())) {
                    GlobalVariables.getMessageMap().putError(tab, KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_INVALID_VALUE, new String[]{loeff.getFieldDisplayName(cc.getCorrectionFieldName()), cc.getCorrectionFieldValue()});
                    allValid = false;
                }
            }
            for (Iterator iterator = ccg.getCorrectionChange().iterator(); iterator.hasNext(); ) {
                CorrectionChange cc = (CorrectionChange) iterator.next();
                if (!loeff.isValidValue(cc.getCorrectionFieldName(), cc.getCorrectionFieldValue())) {
                    GlobalVariables.getMessageMap().putError(tab, KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_INVALID_VALUE, new String[]{loeff.getFieldDisplayName(cc.getCorrectionFieldName()), cc.getCorrectionFieldValue()});
                    allValid = false;
                }
            }
        }
        return allValid;
    }

    /**
     * This method is for loading loadPersistedInputGroup
     *
     * @param generalLedgerCorrectionProcessForm
     * @throws Exception
     */
    protected void loadPersistedInputGroup(GeneralLedgerCorrectionProcessForm generalLedgerCorrectionProcessForm) throws Exception {

        LedgerCorrectionForm ledgerCorrectionForm = (LedgerCorrectionForm) generalLedgerCorrectionProcessForm;
        LedgerCorrectionDocument document = ledgerCorrectionForm.getLaborCorrectionDocument();

        int recordCountFunctionalityLimit = CorrectionDocumentUtils.getRecordCountFunctionalityLimit();
        LaborCorrectionDocumentService laborCorrectionDocumentService = SpringContext.getBean(LaborCorrectionDocumentService.class);

        if (!laborCorrectionDocumentService.areInputOriginEntriesPersisted(document)) {
            // the input origin entry group has been purged from the system
            ledgerCorrectionForm.setPersistedOriginEntriesMissing(true);
            ledgerCorrectionForm.setRestrictedFunctionalityMode(true);
            return;
        }

        ledgerCorrectionForm.setPersistedOriginEntriesMissing(false);
        List<LaborOriginEntry> laborSearchResults = laborCorrectionDocumentService.retrievePersistedInputOriginEntries(document, recordCountFunctionalityLimit);
        if (laborSearchResults == null) {
            // null when the origin entry list is too large (i.e. in restricted functionality mode)
            ledgerCorrectionForm.setRestrictedFunctionalityMode(true);
            updateDocumentSummary(document, null, true);
        } else {
            List<OriginEntryFull> searchResults = new ArrayList();
            searchResults.addAll(laborSearchResults);
            ledgerCorrectionForm.setAllEntries(searchResults);
            ledgerCorrectionForm.setDisplayEntries(new ArrayList<OriginEntryFull>(searchResults));

            updateDocumentSummary(document, ledgerCorrectionForm.getAllEntries(), false);

            // if not in restricted functionality mode, then we can store these results temporarily in the GLCP origin entry service
            SequenceAccessorService sequenceAccessorService = SpringContext.getBean(SequenceAccessorService.class);
            String glcpSearchResultsSequenceNumber = String.valueOf(sequenceAccessorService.getNextAvailableSequenceNumber(KRADConstants.LOOKUP_RESULTS_SEQUENCE));

            SpringContext.getBean(GlCorrectionProcessOriginEntryService.class).persistAllEntries(glcpSearchResultsSequenceNumber, searchResults);
            ledgerCorrectionForm.setGlcpSearchResultsSequenceNumber(glcpSearchResultsSequenceNumber);

            int maxRowsPerPage = CorrectionDocumentUtils.getRecordsPerPage();
            KualiTableRenderFormMetadata originEntrySearchResultTableMetadata = ledgerCorrectionForm.getOriginEntrySearchResultTableMetadata();
            originEntrySearchResultTableMetadata.jumpToFirstPage(ledgerCorrectionForm.getDisplayEntries().size(), maxRowsPerPage);
            originEntrySearchResultTableMetadata.setColumnToSortIndex(-1);
        }
    }

    /**
     * Loads persisted output group
     *
     * @see GeneralLedgerCorrectionProcessAction#loadPersistedOutputGroup(GeneralLedgerCorrectionProcessForm,
     * boolean)
     */
    protected void loadPersistedOutputGroup(GeneralLedgerCorrectionProcessForm generalLedgerCorrectionProcessForm, boolean setSequentialIds) throws Exception {
        LedgerCorrectionForm ledgerCorrectionForm = (LedgerCorrectionForm) generalLedgerCorrectionProcessForm;
        LedgerCorrectionDocument document = ledgerCorrectionForm.getLaborCorrectionDocument();

        LaborCorrectionDocumentService laborCorrectionDocumentService = SpringContext.getBean(LaborCorrectionDocumentService.class);
        if (!laborCorrectionDocumentService.areOutputOriginEntriesPersisted(document)) {
            // the input origin entry group has been purged from the system
            ledgerCorrectionForm.setPersistedOriginEntriesMissing(true);
            ledgerCorrectionForm.setRestrictedFunctionalityMode(true);
            return;
        }

        ledgerCorrectionForm.setPersistedOriginEntriesMissing(false);

        int recordCountFunctionalityLimit;
        if (CorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(ledgerCorrectionForm.getEditMethod())) {
            // with manual edits, rows may have been added so that the list goes would go into restricted func mode
            // so for manual edits, we ignore this limit
            recordCountFunctionalityLimit = CorrectionDocumentUtils.RECORD_COUNT_FUNCTIONALITY_LIMIT_IS_UNLIMITED;
        } else {
            recordCountFunctionalityLimit = CorrectionDocumentUtils.getRecordCountFunctionalityLimit();
        }

        List<LaborOriginEntry> laborSearchResults = laborCorrectionDocumentService.retrievePersistedOutputOriginEntries(document, recordCountFunctionalityLimit);

        if (laborSearchResults == null) {
            // null when the origin entry list is too large (i.e. in restricted functionality mode)
            ledgerCorrectionForm.setRestrictedFunctionalityMode(true);

            WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

            CorrectionDocumentAuthorizer cda = new CorrectionDocumentAuthorizer();
            // TODO fix for KIM
//            Map editingMode = cda.getEditMode(document, GlobalVariables.getUserSession().getPerson());
//            if (editingMode.containsKey(KfsAuthorizationConstants.TransactionalEditMode.FULL_ENTRY) || workflowDocument.isCanceled()) {
//                // doc in read/write mode or is cancelled, so the doc summary fields of the doc are unreliable, so clear them out
//                updateDocumentSummary(document, null, true);
//            }
            // else we defer to the values already in the doc, and just don't touch the values
        } else {
            List<OriginEntryFull> searchResults = new ArrayList();
            searchResults.addAll(laborSearchResults);
            ledgerCorrectionForm.setAllEntries(searchResults);
            ledgerCorrectionForm.setDisplayEntries(new ArrayList<OriginEntryFull>(searchResults));

            if (setSequentialIds) {
                CorrectionDocumentUtils.setSequentialEntryIds(ledgerCorrectionForm.getAllEntries());
            }

            // if we can display the entries (i.e. not restricted functionality mode), then recompute the summary
            updateDocumentSummary(document, ledgerCorrectionForm.getAllEntries(), false);

            // if not in restricted functionality mode, then we can store these results temporarily in the GLCP origin entry service
            SequenceAccessorService sequenceAccessorService = SpringContext.getBean(SequenceAccessorService.class);
            String glcpSearchResultsSequenceNumber = String.valueOf(sequenceAccessorService.getNextAvailableSequenceNumber(KRADConstants.LOOKUP_RESULTS_SEQUENCE));

            SpringContext.getBean(GlCorrectionProcessOriginEntryService.class).persistAllEntries(glcpSearchResultsSequenceNumber, searchResults);
            ledgerCorrectionForm.setGlcpSearchResultsSequenceNumber(glcpSearchResultsSequenceNumber);

            int maxRowsPerPage = CorrectionDocumentUtils.getRecordsPerPage();
            KualiTableRenderFormMetadata originEntrySearchResultTableMetadata = ledgerCorrectionForm.getOriginEntrySearchResultTableMetadata();
            originEntrySearchResultTableMetadata.jumpToFirstPage(ledgerCorrectionForm.getDisplayEntries().size(), maxRowsPerPage);
            originEntrySearchResultTableMetadata.setColumnToSortIndex(-1);
        }
    }

    /**
     * Prepare labor correction document for routing
     *
     * @see GeneralLedgerCorrectionProcessAction#prepareForRoute(GeneralLedgerCorrectionProcessForm)
     */
    protected boolean prepareForRoute(GeneralLedgerCorrectionProcessForm generalLedgerCorrectionProcessForm) throws Exception {

        LedgerCorrectionForm ledgerCorrectionForm = (LedgerCorrectionForm) generalLedgerCorrectionProcessForm;
        LedgerCorrectionDocument document = ledgerCorrectionForm.getLaborCorrectionDocument();

        // Is there a description?
        if (StringUtils.isEmpty(document.getDocumentHeader().getDocumentDescription())) {
            GlobalVariables.getMessageMap().putError("document.documentHeader.documentDescription", KFSKeyConstants.ERROR_DOCUMENT_NO_DESCRIPTION);
            return false;
        }

        if (ledgerCorrectionForm.isPersistedOriginEntriesMissing()) {
            GlobalVariables.getMessageMap().putError("searchResults", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_PERSISTED_ORIGIN_ENTRIES_MISSING);
            return false;
        }

        // Did they pick the edit method and system?
        if (!checkMainDropdown(ledgerCorrectionForm)) {
            return false;
        }

        if (ledgerCorrectionForm.getDataLoadedFlag() || ledgerCorrectionForm.isRestrictedFunctionalityMode()) {
            document.setCorrectionInputFileName(generalLedgerCorrectionProcessForm.getInputGroupId());
        } else {
            document.setCorrectionInputFileName(null);
        }
        if (!checkOriginEntryGroupSelectionBeforeRouting(document)) {
            return false;
        }

        // were the system and edit methods inappropriately changed?
        if (GlobalVariables.getMessageMap().containsMessageKey(KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_INVALID_SYSTEM_OR_EDIT_METHOD_CHANGE)) {
            return false;
        }

        // was the input group inappropriately changed?
        if (GlobalVariables.getMessageMap().containsMessageKey(KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_INVALID_INPUT_GROUP_CHANGE)) {
            return false;
        }

        if (!validGroupsItemsForDocumentSave(ledgerCorrectionForm)) {
            return false;
        }

        // If it is criteria, are all the criteria valid?
        if (CorrectionDocumentService.CORRECTION_TYPE_CRITERIA.equals(ledgerCorrectionForm.getEditMethod())) {
            if (!validChangeGroups(ledgerCorrectionForm)) {
                return false;
            }
        }

        if (!checkRestrictedFunctionalityModeForManualEdit(ledgerCorrectionForm)) {
            return false;
        }

        if (!checkInputGroupPersistedForDocumentSave(ledgerCorrectionForm)) {
            return false;
        }
        // Get the output group if necessary
        if (CorrectionDocumentService.CORRECTION_TYPE_CRITERIA.equals(ledgerCorrectionForm.getEditMethod())) {
            if (!ledgerCorrectionForm.isRestrictedFunctionalityMode() && ledgerCorrectionForm.getDataLoadedFlag() && !ledgerCorrectionForm.getShowOutputFlag()) {
                // we're going to force the user to view the output group upon routing, so apply the criteria
                // if the user wasn't in show output mode.
                updateEntriesFromCriteria(ledgerCorrectionForm, false);
            }
            ledgerCorrectionForm.setShowOutputFlag(true);
        } else {
            // If it is manual edit, we don't need to save any correction groups
            document.getCorrectionChangeGroup().clear();
        }

        // Populate document
        document.setCorrectionTypeCode(ledgerCorrectionForm.getEditMethod());
        document.setCorrectionSelection(ledgerCorrectionForm.getMatchCriteriaOnly());
        document.setCorrectionFileDelete(!ledgerCorrectionForm.getProcessInBatch());
        document.setCorrectionInputFileName(ledgerCorrectionForm.getInputGroupId());
        document.setCorrectionOutputFileName(null); // this field is never used

        // we'll populate the output group id when the doc has a route level change
        document.setCorrectionOutputFileName(null);

        SpringContext.getBean(LaborCorrectionDocumentService.class).persistOriginEntryGroupsForDocumentSave(document, ledgerCorrectionForm);

        return true;
    }

    /**
     * Save labor correction form as a text document (.txt)
     *
     * @see GeneralLedgerCorrectionProcessAction#saveToDesktop(org.apache.struts.action.ActionMapping,
     * org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward saveToDesktop(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOG.debug("saveToDesktop() started");

        LedgerCorrectionForm ledgerCorrectionForm = (LedgerCorrectionForm) form;

        if (checkOriginEntryGroupSelection(ledgerCorrectionForm)) {
            if (ledgerCorrectionForm.isInputGroupIdFromLastDocumentLoadIsMissing() && ledgerCorrectionForm.getInputGroupIdFromLastDocumentLoad() != null && ledgerCorrectionForm.getInputGroupIdFromLastDocumentLoad().equals(ledgerCorrectionForm.getInputGroupId())) {
                if (ledgerCorrectionForm.isPersistedOriginEntriesMissing()) {
                    GlobalVariables.getMessageMap().putError("documentsInSystem", LaborKeyConstants.ERROR_LABOR_ERROR_CORRECTION_PERSISTED_ORIGIN_ENTRIES_MISSING);
                    return mapping.findForward(KFSConstants.MAPPING_BASIC);
                } else {
                    String fileName = "llcp_archived_group_" + ledgerCorrectionForm.getInputGroupIdFromLastDocumentLoad().toString() + ".txt";
                    // set response
                    response.setContentType("application/txt");
                    response.setHeader("Content-disposition", "attachment; filename=" + fileName);
                    response.setHeader("Expires", "0");
                    response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
                    response.setHeader("Pragma", "public");

                    BufferedOutputStream bw = new BufferedOutputStream(response.getOutputStream());

                    SpringContext.getBean(CorrectionDocumentService.class).writePersistedInputOriginEntriesToStream((LedgerCorrectionDocument) ledgerCorrectionForm.getDocument(), bw);

                    bw.flush();
                    bw.close();

                    return null;
                }
            } else {
                String batchDirectory = SpringContext.getBean(LaborCorrectionDocumentService.class).getBatchFileDirectoryName();
                String fileNameWithPath;
                if (!ledgerCorrectionForm.getInputGroupId().contains(batchDirectory)) {
                    fileNameWithPath = batchDirectory + File.separator + ledgerCorrectionForm.getInputGroupId();
                } else {
                    fileNameWithPath = ledgerCorrectionForm.getInputGroupId();
                }

                FileReader fileReader = new FileReader(fileNameWithPath);
                BufferedReader br = new BufferedReader(fileReader);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // write to output
                buildTextOutputfile(baos, br);
                WebUtils.saveMimeOutputStreamAsFile(response, "application/txt", baos, ledgerCorrectionForm.getInputGroupId());

                br.close();

                return null;
            }
        } else {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
    }

    /**
     * Sort labor correction document by selected column
     *
     * @see org.kuali.rice.kns.web.struts.action.KualiTableRenderAction#sort(org.apache.struts.action.ActionMapping,
     * org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     * <p>
     * KRAD Conversion: Performs sorting of the results based on column to sort.
     * <p>
     * Uses data dictionary for originEntrySearchResultTableMetadata
     */
    public ActionForward sort(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LedgerCorrectionForm correctionForm = (LedgerCorrectionForm) form;

        // when we return from the lookup, our next request's method to call is going to be refresh
        correctionForm.registerEditableProperty(KRADConstants.DISPATCH_REQUEST_PARAMETER);

        int maxRowsPerPage = CorrectionDocumentUtils.getRecordsPerPage();

        KualiTableRenderFormMetadata originEntrySearchResultTableMetadata = correctionForm.getOriginEntrySearchResultTableMetadata();

        List<Column> columns = SpringContext.getBean(LaborCorrectionDocumentService.class).getTableRenderColumnMetadata(correctionForm.getDocument().getDocumentNumber());

        String propertyToSortName = columns.get(originEntrySearchResultTableMetadata.getColumnToSortIndex()).getPropertyName();
        Comparator valueComparator = columns.get(originEntrySearchResultTableMetadata.getColumnToSortIndex()).getValueComparator();

        boolean sortDescending = false;
        if (originEntrySearchResultTableMetadata.getPreviouslySortedColumnIndex() == originEntrySearchResultTableMetadata.getColumnToSortIndex()) {
            // clicked sort on the same column that was previously sorted, so we will reverse the sort order
            sortDescending = !originEntrySearchResultTableMetadata.isSortDescending();
            originEntrySearchResultTableMetadata.setSortDescending(sortDescending);
        }

        originEntrySearchResultTableMetadata.setSortDescending(sortDescending);
        // sort the list now so that it will be rendered correctly
        sortList(correctionForm.getDisplayEntries(), propertyToSortName, valueComparator, sortDescending);

        // sorting, so go back to the first page
        originEntrySearchResultTableMetadata.jumpToFirstPage(correctionForm.getDisplayEntries().size(), maxRowsPerPage);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Apply paging and sorting from previous page view
     *
     * @see GeneralLedgerCorrectionProcessAction#applyPagingAndSortingFromPreviousPageView(GeneralLedgerCorrectionProcessForm)
     * <p>
     * KRAD Conversion: Performs sorting of the results based on column to sort.
     * <p>
     * Uses data dictionary for originEntrySearchResultTableMetadata
     */
    protected void applyPagingAndSortingFromPreviousPageView(LedgerCorrectionForm ledgerCorrectionForm) {
        KualiTableRenderFormMetadata originEntrySearchResultTableMetadata = ledgerCorrectionForm.getOriginEntrySearchResultTableMetadata();
        if (originEntrySearchResultTableMetadata.getPreviouslySortedColumnIndex() != -1) {

            List<Column> columns = SpringContext.getBean(LaborCorrectionDocumentService.class).getTableRenderColumnMetadata(ledgerCorrectionForm.getDocument().getDocumentNumber());

            String propertyToSortName = columns.get(originEntrySearchResultTableMetadata.getPreviouslySortedColumnIndex()).getPropertyName();
            Comparator valueComparator = columns.get(originEntrySearchResultTableMetadata.getPreviouslySortedColumnIndex()).getValueComparator();
            sortList(ledgerCorrectionForm.getDisplayEntries(), propertyToSortName, valueComparator, originEntrySearchResultTableMetadata.isSortDescending());
        }

        int maxRowsPerPage = CorrectionDocumentUtils.getRecordsPerPage();
        originEntrySearchResultTableMetadata.jumpToPage(originEntrySearchResultTableMetadata.getViewedPageNumber(), ledgerCorrectionForm.getDisplayEntries().size(), maxRowsPerPage);
    }

    /**
     * Returns true if input group exists from labor correction document
     *
     * @see GeneralLedgerCorrectionProcessAction#checkInputGroupPersistedForDocumentSave(GeneralLedgerCorrectionProcessForm)
     */
    protected boolean checkInputGroupPersistedForDocumentSave(GeneralLedgerCorrectionProcessForm generalLedgerCorrectionProcessForm) {
        boolean present;
        LedgerCorrectionForm ledgerCorrectionForm = (LedgerCorrectionForm) generalLedgerCorrectionProcessForm;
        WorkflowDocument workflowDocument = ledgerCorrectionForm.getDocument().getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.isInitiated() || (workflowDocument.isSaved() && (ledgerCorrectionForm.getInputGroupIdFromLastDocumentLoad() == null || !ledgerCorrectionForm.getInputGroupIdFromLastDocumentLoad().equals(ledgerCorrectionForm.getInputGroupId())))) {
            present = originEntryGroupService.getGroupExists(((LedgerCorrectionDocument) ledgerCorrectionForm.getDocument()).getCorrectionInputFileName());
        } else {
            present = SpringContext.getBean(LaborCorrectionDocumentService.class).areInputOriginEntriesPersisted((LedgerCorrectionDocument) ledgerCorrectionForm.getDocument());
        }
        if (!present) {
            GlobalVariables.getMessageMap().putError(SYSTEM_AND_EDIT_METHOD_ERROR_KEY, KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_PERSISTED_ORIGIN_ENTRIES_MISSING);
        }
        return present;
    }

    /**
     * Called when selecting the system and method. If this button is pressed, the document should be reset as if it is the first
     * time it was pressed.
     *
     * @see GeneralLedgerCorrectionProcessAction#selectSystemEditMethod(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward selectSystemEditMethod(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("selectSystemEditMethod() started");

        LedgerCorrectionForm ledgerCorrectionForm = (LedgerCorrectionForm) form;
        LedgerCorrectionDocument document = ledgerCorrectionForm.getLaborCorrectionDocument();

        if (checkMainDropdown(ledgerCorrectionForm)) {
            // Clear out any entries that were already loaded
            document.setCorrectionInputFileName(null);
            document.setCorrectionOutputFileName(null);
            document.setCorrectionCreditTotalAmount(null);
            document.setCorrectionDebitTotalAmount(null);
            document.setCorrectionBudgetTotalAmount(null);
            document.setCorrectionRowCount(null);
            document.getCorrectionChangeGroup().clear();

            ledgerCorrectionForm.setDataLoadedFlag(false);
            ledgerCorrectionForm.setDeleteFileFlag(false);
            ledgerCorrectionForm.setEditableFlag(false);
            ledgerCorrectionForm.setManualEditFlag(false);
            ledgerCorrectionForm.setShowOutputFlag(false);
            ledgerCorrectionForm.setAllEntries(new ArrayList<OriginEntryFull>());
            ledgerCorrectionForm.setRestrictedFunctionalityMode(false);
            ledgerCorrectionForm.setProcessInBatch(true);

            if (CorrectionDocumentService.SYSTEM_DATABASE.equals(ledgerCorrectionForm.getChooseSystem())) {
                // if users choose database, then get the list of origin entry groups and set the default

                // I shouldn't have to do this query twice, but with the current architecture, I can't find anyway not to do it.
                CorrectionLaborGroupEntriesFinder f = new CorrectionLaborGroupEntriesFinder();
                List values = f.getKeyValues();
                if (values.size() > 0) {
                    //TODO:- need to change using file
                    //OriginEntryGroup g = GeneralLedgerCorrectionProcessAction.originEntryGroupService.getNewestScrubberErrorGroup();
                    String newestScrubberErrorFileName = GeneralLedgerCorrectionProcessAction.originEntryGroupService.getNewestScrubberErrorFileName();
                    //if (g != null) {
                    if (newestScrubberErrorFileName != null) {
                        document.setCorrectionInputFileName(newestScrubberErrorFileName);
                    } else {
                        KeyValue klp = (KeyValue) values.get(0);
                        //document.setCorrectionInputGroupId(Integer.parseInt((String) klp.getKey()));
                        document.setCorrectionInputFileName((String) klp.getKey());
                    }
                } else {
                    GlobalVariables.getMessageMap().putError(SYSTEM_AND_EDIT_METHOD_ERROR_KEY, KFSKeyConstants.ERROR_NO_ORIGIN_ENTRY_GROUPS);
                    ledgerCorrectionForm.setChooseSystem("");
                }
            }
        } else {
            ledgerCorrectionForm.setEditMethod("");
            ledgerCorrectionForm.setChooseSystem("");
        }
        ledgerCorrectionForm.setPreviousChooseSystem(ledgerCorrectionForm.getChooseSystem());
        ledgerCorrectionForm.setPreviousEditMethod(ledgerCorrectionForm.getEditMethod());
        ledgerCorrectionForm.setPreviousInputGroupId(null);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Called when Load Group button is pressed
     */
    public ActionForward loadGroup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("loadGroup() started");

        LedgerCorrectionForm ledgerCorrectionForm = (LedgerCorrectionForm) form;
        String batchDirectory = SpringContext.getBean(LaborCorrectionDocumentService.class).getBatchFileDirectoryName();

        if (checkOriginEntryGroupSelection(ledgerCorrectionForm)) {
            LedgerCorrectionDocument doc = ledgerCorrectionForm.getLaborCorrectionDocument();
            doc.setCorrectionInputFileName(batchDirectory + File.separator + ledgerCorrectionForm.getInputGroupId());

            // TODO:- need to change using file - just size info will be enough
            // TODO:- int will be enough? should I change it long??
            int inputGroupSize = laborOriginEntryService.getGroupCount(ledgerCorrectionForm.getInputGroupId());
            int recordCountFunctionalityLimit = CorrectionDocumentUtils.getRecordCountFunctionalityLimit();
            ledgerCorrectionForm.setPersistedOriginEntriesMissing(false);

            if (CorrectionDocumentUtils.isRestrictedFunctionalityMode(inputGroupSize, recordCountFunctionalityLimit)) {
                ledgerCorrectionForm.setRestrictedFunctionalityMode(true);
                ledgerCorrectionForm.setDataLoadedFlag(false);
                updateDocumentSummary(ledgerCorrectionForm.getCorrectionDocument(), null, true);

                if (CorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(ledgerCorrectionForm.getEditMethod())) {
                    // the group size is not suitable for manual editing because it is too large
                    if (recordCountFunctionalityLimit == CorrectionDocumentUtils.RECORD_COUNT_FUNCTIONALITY_LIMIT_IS_NONE) {
                        GlobalVariables.getMessageMap().putError(SYSTEM_AND_EDIT_METHOD_ERROR_KEY, KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_UNABLE_TO_MANUAL_EDIT_ANY_GROUP);
                    } else {
                        GlobalVariables.getMessageMap().putError(SYSTEM_AND_EDIT_METHOD_ERROR_KEY, KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_UNABLE_TO_MANUAL_EDIT_LARGE_GROUP, String.valueOf(recordCountFunctionalityLimit));
                    }
                }
            } else {
                ledgerCorrectionForm.setRestrictedFunctionalityMode(false);

                //TODO:- need to change using file
                loadAllEntries(ledgerCorrectionForm.getInputGroupId(), ledgerCorrectionForm);

                if (ledgerCorrectionForm.getAllEntries().size() > 0) {
                    if (CorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(ledgerCorrectionForm.getEditMethod())) {
                        ledgerCorrectionForm.setManualEditFlag(true);
                        ledgerCorrectionForm.setEditableFlag(false);
                        ledgerCorrectionForm.setDeleteFileFlag(false);
                    }
                    ledgerCorrectionForm.setDataLoadedFlag(true);
                } else {
                    GlobalVariables.getMessageMap().putError("documentsInSystem", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_NO_RECORDS);
                }
            }

            LedgerCorrectionDocument document = ledgerCorrectionForm.getLaborCorrectionDocument();
            if (document.getCorrectionChangeGroup().isEmpty()) {
                document.addCorrectionChangeGroup(new CorrectionChangeGroup());
            }

            ledgerCorrectionForm.setPreviousInputGroupId(ledgerCorrectionForm.getInputGroupId());
        }

        ledgerCorrectionForm.setShowOutputFlag(false);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    @Override
    public ActionForward confirmDeleteDocument(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("confirmDeleteDocument() started");

        LedgerCorrectionForm correctionForm = (LedgerCorrectionForm) form;

        if (checkOriginEntryGroupSelection(correctionForm)) {
            String batchDirectory = SpringContext.getBean(LaborCorrectionDocumentService.class).getBatchFileDirectoryName();
            String doneFileName = batchDirectory + File.separator + correctionForm.getInputGroupId();
            String dataFileName = doneFileName.replace(GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION, GeneralLedgerConstants.BatchFileSystem.EXTENSION);

            int groupCount = laborOriginEntryService.getGroupCount(dataFileName);
            int recordCountFunctionalityLimit = CorrectionDocumentUtils.getRecordCountFunctionalityLimit();

            if (!CorrectionDocumentUtils.isRestrictedFunctionalityMode(groupCount, recordCountFunctionalityLimit)) {
                loadAllEntries(dataFileName, correctionForm);
                correctionForm.setDeleteFileFlag(true);
                correctionForm.setDataLoadedFlag(true);
                correctionForm.setRestrictedFunctionalityMode(false);
            } else {
                correctionForm.setRestrictedFunctionalityMode(true);
            }

            LedgerCorrectionDocument document = (LedgerCorrectionDocument) correctionForm.getDocument();
            document.setCorrectionInputFileName(dataFileName);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);

    }

}

