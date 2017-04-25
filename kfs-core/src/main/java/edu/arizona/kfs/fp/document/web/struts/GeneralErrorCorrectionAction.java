package edu.arizona.kfs.fp.document.web.struts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.SegmentedLookupResultsService;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

import edu.arizona.kfs.fp.businessobject.GECSourceAccountingLine;
import edu.arizona.kfs.fp.document.GeneralErrorCorrectionDocument;
import edu.arizona.kfs.gl.businessobject.GecEntryRelationship;
import edu.arizona.kfs.sys.KFSConstants;


public class GeneralErrorCorrectionAction extends org.kuali.kfs.fp.document.web.struts.GeneralErrorCorrectionAction {

    private SegmentedLookupResultsService segmentedLookupResultsService;


    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.refresh(mapping, form, request, response);

        GeneralErrorCorrectionForm gecForm = (GeneralErrorCorrectionForm) form;
        GeneralErrorCorrectionDocument gecDoc = (GeneralErrorCorrectionDocument) gecForm.getDocument();

        // If multiple asset lookup was used to select the assets, then....
        if (StringUtils.equals(KFSConstants.MULTIPLE_VALUE, gecForm.getRefreshCaller())) {
            String lookupResultsSequenceNumber = gecForm.getLookupResultsSequenceNumber();

            if (StringUtils.isNotBlank(lookupResultsSequenceNumber)) {
                // actually returning from a multiple value lookup
                Set<String> selectedObjectIds = getSegmentedLookupResultsService().retrieveSetOfSelectedObjectIds(lookupResultsSequenceNumber, GlobalVariables.getUserSession().getPerson().getPrincipalId());

                // Retrieving selected data from table.
                Collection<Entry> rawValues = retrieveSelectedResultBOs(selectedObjectIds);

                if (rawValues == null || rawValues.isEmpty()) {
                    return mapping.findForward(KFSConstants.MAPPING_BASIC);
                }

                for (Entry entry : rawValues) {
                    GECSourceAccountingLine line = convertEntryToSourceAcctLine(entry);
                    line.setDocumentNumber(gecDoc.getDocumentNumber());
                    line.setObjectId(UUID.randomUUID().toString());

                    super.insertAccountingLine(true, (KualiAccountingDocumentFormBase) form, line);

                    GecEntryRelationship gecEntryRelationship = new GecEntryRelationship(entry.getEntryId(), gecDoc.getDocumentNumber(), line.getSequenceNumber(), line.getFinancialDocumentLineTypeCode(), gecDoc.getDocumentHeader().getWorkflowDocument().getStatus().getCode(), line.getObjectId(), entry);
                    gecDoc.getGecEntryRelationships().add(gecEntryRelationship);
                    entry.setGecDocumentNumber(gecDoc.getDocumentNumber());
                }

                processAccountingLineOverrides(gecDoc, gecDoc.getSourceAccountingLines());
                processAccountingLineOverrides(gecDoc, gecDoc.getTargetAccountingLines());

                // next refresh should not attempt to retrieve these objects.
                gecForm.setLookupResultsSequenceNumber(KFSConstants.EMPTY_STRING);

            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    @Override
    public ActionForward performLookup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // parse out the important strings from our methodToCall parameter
        String fullParameter = (String) request.getAttribute(KFSConstants.METHOD_TO_CALL_ATTRIBUTE);

        // determine what the action path is
        String actionPath = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_PARM4_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM4_RIGHT_DEL);
        if (StringUtils.isBlank(actionPath)) {
            return super.performLookup(mapping, form, request, response);
        }

        GeneralErrorCorrectionForm financialDocumentForm = (GeneralErrorCorrectionForm) form;

        // when we return from the lookup, our next request's method to call is going to be refresh
        financialDocumentForm.registerEditableProperty(KRADConstants.DISPATCH_REQUEST_PARAMETER);

        String basePath = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY);

        // parse out business object class name for lookup
        String boClassName = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_BOPARM_LEFT_DEL, KFSConstants.METHOD_TO_CALL_BOPARM_RIGHT_DEL);
        if (StringUtils.isBlank(boClassName)) {
            throw new RuntimeException("Illegal call to perform lookup, no business object class name specified.");
        }

        // build the parameters for the lookup url
        Properties parameters = new Properties();
        String conversionFields = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_PARM1_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM1_RIGHT_DEL);
        if (StringUtils.isNotBlank(conversionFields)) {
            parameters.put(KFSConstants.CONVERSION_FIELDS_PARAMETER, conversionFields);
        }

        // pass values from form that should be pre-populated on lookup search
        String parameterFields = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_PARM2_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM2_RIGHT_DEL);
        if (StringUtils.isNotBlank(parameterFields)) {
            String[] lookupParams = parameterFields.split(KFSConstants.FIELD_CONVERSIONS_SEPERATOR);

            for (int i = 0; i < lookupParams.length; i++) {
                String[] keyValue = lookupParams[i].split(KFSConstants.FIELD_CONVERSION_PAIR_SEPERATOR);

                // hard-coded passed value
                if (StringUtils.contains(keyValue[0], KRADConstants.SINGLE_QUOTE)) {
                    parameters.put(keyValue[1], StringUtils.replace(keyValue[0], KRADConstants.SINGLE_QUOTE, KRADConstants.EMPTY_STRING));
                }
                // passed value should come from property
                else if (StringUtils.isNotBlank(request.getParameter(keyValue[0]))) {
                    parameters.put(keyValue[1], request.getParameter(keyValue[0]));
                }
            }
        }

        // grab whether or not the "return value" link should be hidden or not
        String hideReturnLink = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_PARM3_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM3_RIGHT_DEL);
        if (StringUtils.isNotBlank(hideReturnLink)) {
            parameters.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, hideReturnLink);
        }

        // anchor, if it exists
        if (StringUtils.isNotEmpty(((KualiForm) form).getAnchor())) {
            parameters.put(KFSConstants.LOOKUP_ANCHOR, ((KualiForm) form).getAnchor());
        }

        // now add required parameters
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);
        parameters.put(KFSConstants.DOC_FORM_KEY, GlobalVariables.getUserSession().addObjectWithGeneratedKey(form));
        parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, boClassName);
        parameters.put(KFSConstants.RETURN_LOCATION_PARAMETER, basePath + mapping.getPath() + KFSConstants.ACTION_EXTENSION_DOT_DO);

        String lookupUrl = UrlFactory.parameterizeUrl(basePath + KFSConstants.PATH_SEPERATOR + actionPath, parameters);

        return new ActionForward(lookupUrl, true);
    }


    @SuppressWarnings("deprecation")// SourceAccountingLine#set{Chart|Account|ObjectCode|RefOrigin|BalType}()
    private GECSourceAccountingLine convertEntryToSourceAcctLine(Entry entry) {
        GECSourceAccountingLine newSourceLine = new GECSourceAccountingLine();
        newSourceLine.setFinancialDocumentLineTypeCode(KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE);
        newSourceLine.setChartOfAccountsCode(entry.getChartOfAccountsCode());
        newSourceLine.setAccountNumber(entry.getAccountNumber());
        if (!entry.getSubAccountNumber().equals(KFSConstants.BLANK_SUBACCOUNT)) {
            newSourceLine.setSubAccountNumber(entry.getSubAccountNumber());
        }
        newSourceLine.setFinancialObjectCode(entry.getFinancialObjectCode());
        if (!entry.getFinancialSubObjectCode().equals(KFSConstants.BLANK_SUBOBJECT)) {
            newSourceLine.setFinancialSubObjectCode(entry.getFinancialSubObjectCode());
        }
        if (!entry.getProjectCode().equals(KFSConstants.BLANK_PROJECT_CODE)) {
            newSourceLine.setProjectCode(entry.getProjectCode());
        }
        newSourceLine.setOrganizationReferenceId(entry.getOrganizationReferenceId());
        newSourceLine.setAmount(entry.getTransactionLedgerEntryAmount());
        newSourceLine.setReferenceOriginCode(entry.getFinancialSystemOriginationCode());
        newSourceLine.setReferenceNumber(entry.getDocumentNumber());
        newSourceLine.setFinancialDocumentLineDescription(entry.getTransactionLedgerEntryDescription());
        String debitCreditCode = reverseDebitCreditCode(entry.getTransactionDebitCreditCode());
        newSourceLine.setDebitCreditCode(debitCreditCode);
        newSourceLine.setBalanceTypeCode(entry.getFinancialBalanceTypeCode());

        // copy helper objects
        newSourceLine.setChart(entry.getChart());
        newSourceLine.setAccount(entry.getAccount());
        newSourceLine.setObjectCode(entry.getFinancialObject());
        newSourceLine.setReferenceOrigin(entry.getReferenceOriginationCode());
        newSourceLine.setBalanceTyp(entry.getBalanceType());

        return newSourceLine;
    }


    private String reverseDebitCreditCode(String transactionDebitCreditCode) {
        if (transactionDebitCreditCode.equalsIgnoreCase(KFSConstants.GL_DEBIT_CODE)) {
            return KFSConstants.GL_CREDIT_CODE;
        } else if (transactionDebitCreditCode.equalsIgnoreCase(KFSConstants.GL_CREDIT_CODE)) {
            return KFSConstants.GL_DEBIT_CODE;
        } else {
            throw new RuntimeException("Unknown credit/debit code" + transactionDebitCreditCode);
        }
    }


    protected Collection<Entry> retrieveSelectedResultBOs(Set<String> setOfSelectedEntryIds) throws Exception {
        if (setOfSelectedEntryIds == null || setOfSelectedEntryIds.isEmpty()) {
            // OJB throws exception if querying on empty set
            return new ArrayList<Entry>();
        }

        Map<String, Collection<String>> queryCriteria = new HashMap<String, Collection<String>>();
        queryCriteria.put(KFSConstants.GEC_ENTRY_OBJ_ID, setOfSelectedEntryIds);

        return getBusinessObjectService().findMatching(Entry.class, queryCriteria);
    }


    protected void reverseAccountingLine(AccountingLine source, AccountingLine target) {
        target.copyFrom(source);
        target.setFinancialDocumentLineTypeCode(KFSConstants.TARGET_ACCT_LINE_TYPE_CODE);
        String debitCreditCode = reverseDebitCreditCode(source.getDebitCreditCode());
        target.setDebitCreditCode(debitCreditCode);
    }


    // Action button
    @SuppressWarnings("unused") // invoked via reflection, so IDE thinks it's unused
    public ActionForward copyAllAccountingLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        GeneralErrorCorrectionForm gecForm = (GeneralErrorCorrectionForm) form;
        GeneralErrorCorrectionDocument gecDoc = (GeneralErrorCorrectionDocument) gecForm.getDocument();

        for (Object line : gecForm.getFinancialDocument().getSourceAccountingLines()) {
            AccountingLine sourceLine = (AccountingLine) line;
            AccountingLine targetLine = (AccountingLine) gecForm.getFinancialDocument().getTargetAccountingLineClass().newInstance();
            reverseAccountingLine(sourceLine, targetLine);
            insertAccountingLine(false, gecForm, targetLine);
        }

        processAccountingLineOverrides(gecDoc, gecDoc.getSourceAccountingLines());
        processAccountingLineOverrides(gecDoc, gecDoc.getTargetAccountingLines());

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /*
     * This is the "delete all" button in the jsp for source lines. We will clear all source
     * lines, but only mark the gecDocNum on the entry as 'null', which is a sentinal value
     * indicating any source Entry has been dissociated. The az.GECD will have it's extended
     * postProcessSave() method clear out entries marked as such -- this is necessary for
     * change detection for line operations occuring between route changes.
     */
    @SuppressWarnings("unused")// invoked via reflection, so IDE thinks it's unused
    public ActionForward deleteAllSourceAccountingLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        GeneralErrorCorrectionForm gecForm = (GeneralErrorCorrectionForm) form;
        GeneralErrorCorrectionDocument gecDoc = (GeneralErrorCorrectionDocument) gecForm.getDocument();

        @SuppressWarnings("unchecked") // super is not parameterized
        List<GECSourceAccountingLine> gecSourceLines = gecDoc.getSourceAccountingLines();
        for (GECSourceAccountingLine line : gecSourceLines) {
            dissociateEntryByLine(gecDoc, line);
        }

        gecSourceLines.clear();
        gecDoc.setNextSourceLineNumber(KFSConstants.ONE.intValue());

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    // Action button
    @SuppressWarnings("unused")// invoked via reflection, so IDE thinks it's unused
    public ActionForward deleteAllTargetAccountingLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        GeneralErrorCorrectionForm gecForm = (GeneralErrorCorrectionForm) form;
        GeneralErrorCorrectionDocument gecDoc = (GeneralErrorCorrectionDocument) gecForm.getDocument();

        gecForm.getFinancialDocument().getTargetAccountingLines().clear();
        gecDoc.setNextSourceLineNumber(KFSConstants.ONE.intValue());

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /*
     * Overridden to dissociate relationships as they happen
     */
    @Override
    public ActionForward deleteSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        GeneralErrorCorrectionForm gecForm = (GeneralErrorCorrectionForm) form;
        GeneralErrorCorrectionDocument gecDoc = (GeneralErrorCorrectionDocument) gecForm.getDocument();

        // Get rid of GecEntryRelationship before it's removed from doc
        int lineIndex = getLineToDelete(request);
        GECSourceAccountingLine gecSourceLine = (GECSourceAccountingLine) gecDoc.getSourceAccountingLine(lineIndex);
        dissociateEntryByLine(gecDoc, gecSourceLine);

        super.deleteAccountingLine(true, gecForm, lineIndex);

        return mapping.findForward(org.kuali.kfs.sys.KFSConstants.MAPPING_BASIC);
    }


    /*
     * Overridden in order to add doc number to imported target lines
     */
    @Override
    protected void uploadAccountingLines(boolean isSource, ActionForm form) throws IOException {
        super.uploadAccountingLines(isSource, form);

        GeneralErrorCorrectionForm gecForm = (GeneralErrorCorrectionForm) form;
        GeneralErrorCorrectionDocument gecDoc = (GeneralErrorCorrectionDocument) gecForm.getDocument();

        @SuppressWarnings("unchecked")// super is not parameterized
        List<AccountingLine> targetLines = gecDoc.getTargetAccountingLines();
        for (AccountingLine line : targetLines) {
            line.setDocumentNumber(gecDoc.getDocumentNumber());
        }

    }


    private void dissociateEntryByLine(GeneralErrorCorrectionDocument doc, GECSourceAccountingLine gecSourceLine) {

        // Try to find any Entry associated to this line, then set null for GECD#, this will
        // persist in the doc's postSave() hook
        Entry entry = doc.getEntryByAccountingLine(gecSourceLine);
        if (entry == null) {
            // Not associated, so don't do anything
            return;
        } else {
            // This entry's null GEC# will be noticed in the GEC doc during saves/route changes
            // via its overridden postProcessSave()
            entry.setGecDocumentNumber(null);
        }

        // Find the GecEntyRelationship to remove
        List<GecEntryRelationship> relsToDelete = new ArrayList<GecEntryRelationship>();
        for (GecEntryRelationship rel : doc.getGecEntryRelationships()) {
            if (rel.getEntryId().equals(entry.getEntryId())) {
                relsToDelete.add(rel);
                break; // doc.getGecEntryRelationships() is a set based on entryId, thus we exit loop
            }
        }

        // Effect the changes
        if (relsToDelete.size() > 0) {
            doc.getGecEntryRelationships().removeAll(relsToDelete);
            getBusinessObjectService().delete(relsToDelete);
            doc.updateEntryGecDocNums(Collections.singletonList(entry));
        }

    }


    // Action Button
    @SuppressWarnings("unused")// invoked via reflection, so IDE thinks it's unused
    public ActionForward copyAccountingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        GeneralErrorCorrectionForm gecForm = (GeneralErrorCorrectionForm) form;
        GeneralErrorCorrectionDocument gecDocument = (GeneralErrorCorrectionDocument) gecForm.getDocument();

        int index = getSelectedLine(request);
        AccountingLine sourceLine = gecDocument.getSourceAccountingLine(index);
        AccountingLine targetLine = (AccountingLine) gecForm.getFinancialDocument().getTargetAccountingLineClass().newInstance();

        reverseAccountingLine(sourceLine, targetLine);
        insertAccountingLine(false, gecForm, targetLine);

        processAccountingLineOverrides(sourceLine);
        processAccountingLineOverrides(targetLine);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    @Override
    public ActionForward insertTargetLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        GeneralErrorCorrectionForm gecForm = (GeneralErrorCorrectionForm) form;
        GeneralErrorCorrectionDocument gecDocument = (GeneralErrorCorrectionDocument) gecForm.getDocument();

        int index = getSelectedLine(request);
        AccountingLine originalLine = gecDocument.getTargetAccountingLine(index);
        AccountingLine targetLine = (AccountingLine) gecForm.getFinancialDocument().getTargetAccountingLineClass().newInstance();

        targetLine.copyFrom(originalLine);
        targetLine.setAmount(KualiDecimal.ZERO);
        insertAccountingLine(false, gecForm, targetLine);

        processAccountingLineOverrides(originalLine);
        processAccountingLineOverrides(targetLine);

        gecDocument.resequenceLinesAndRelationships();

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /*
     * This is the "import" button in the "reversing" section, so we know this will always be
     * a target line -- the "reversing" section does not have import, since the GLE always
     * comes off of a GLE search. Still popped it into an if/else just in case someone else down
     * the line uses this.
     *
     * Note: this also does the reversing of the flags
     */
    @Override
    protected void insertAccountingLine(boolean isSource, KualiAccountingDocumentFormBase financialDocumentForm, AccountingLine line) {

        // Only set d/c flag for target import, just in case this gets called in future code w/ a source line
        if (!isSource) {
            // Collect all the source lines' d/c codes
            @SuppressWarnings("unchecked")// super is not parameterized
            List<GECSourceAccountingLine> sourceLines = financialDocumentForm.getFinancialDocument().getSourceAccountingLines();
            Set<String> debitCreditSet = new HashSet<>();
            for (GECSourceAccountingLine sourceLine : sourceLines) {
                debitCreditSet.add(sourceLine.getDebitCreditCode());
            }

            // Default to natural balance state, already reversed
            String debitCreditCode = KFSConstants.GL_DEBIT_CODE;

            // Deal with unnatural balance state by going the opposite of our source lines (which come from GLEs)
            if (debitCreditSet.size() == 1) {
                debitCreditCode = debitCreditSet.iterator().next();
                debitCreditCode = reverseDebitCreditCode(debitCreditCode);
            } else if (debitCreditSet.size() > 1) {
                throw new RuntimeException("Source lines had multiple debit/credit codes!: " + debitCreditSet);
            }

            // Now set it on this target line
            line.setDebitCreditCode(debitCreditCode);
        }

        super.insertAccountingLine(isSource, financialDocumentForm, line);

        GeneralErrorCorrectionDocument gecDoc = (GeneralErrorCorrectionDocument) financialDocumentForm.getDocument();
        gecDoc.resequenceLinesAndRelationships();
    }


    private SegmentedLookupResultsService getSegmentedLookupResultsService() {
        if (segmentedLookupResultsService == null) {
            segmentedLookupResultsService = SpringContext.getBean(SegmentedLookupResultsService.class);
        }
        return segmentedLookupResultsService;
    }

}
