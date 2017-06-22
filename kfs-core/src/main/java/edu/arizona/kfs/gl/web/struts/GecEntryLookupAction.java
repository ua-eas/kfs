package edu.arizona.kfs.gl.web.struts;

import static org.kuali.kfs.sys.KFSPropertyConstants.DOCUMENT_NUMBER;
import static org.kuali.kfs.sys.KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.fp.document.GeneralErrorCorrectionDocument;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.DebitDeterminerService;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.web.struts.action.KualiMultipleValueLookupAction;
import org.kuali.rice.kns.web.struts.form.MultipleValueLookupForm;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.LookupService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

import edu.arizona.kfs.fp.document.web.struts.GeneralErrorCorrectionAction;
import edu.arizona.kfs.gl.GeneralLedgerConstants;
import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSPropertyConstants;


public class GecEntryLookupAction extends KualiMultipleValueLookupAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GecEntryLookupAction.class);

    private ObjectCodeService objectCodeService;
    private ParameterEvaluatorService parameterEvaluatorService;
    private LookupService lookupService;
    private SystemOptions systemOptions;
    private BusinessObjectService businessObjectService;
    private OptionsService optionsService;
    protected DebitDeterminerService debitDeterminerService;


    @Override
    public ActionForward prepareToReturnSelectedResults(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        MultipleValueLookupForm multipleValueLookupForm = (GecMultipleValueLookupForm) form;
        GecMultipleValueLookupForm gecForm = (GecMultipleValueLookupForm) form;
        if (StringUtils.isBlank(gecForm.getLookupResultsSequenceNumber())) {
            // no search was executed
            return prepareToReturnNone(mapping, gecForm, request, response);
        }

        prepareToReturnSelectedResultBOs(multipleValueLookupForm);

        // build the parameters for the refresh url
        Properties parameters = new Properties();
        parameters.put(KRADConstants.LOOKUP_RESULTS_BO_CLASS_NAME, multipleValueLookupForm.getBusinessObjectClassName());
        parameters.put(KRADConstants.LOOKUP_RESULTS_SEQUENCE_NUMBER, multipleValueLookupForm.getLookupResultsSequenceNumber());
        parameters.put(KRADConstants.DOC_FORM_KEY, multipleValueLookupForm.getFormKey());
        parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.RETURN_METHOD_TO_CALL);
        parameters.put(KRADConstants.REFRESH_CALLER, KRADConstants.MULTIPLE_VALUE);
        parameters.put(KRADConstants.ANCHOR, multipleValueLookupForm.getLookupAnchor());
        if (multipleValueLookupForm.getDocNum() != null) {
            parameters.put(KRADConstants.DOC_NUM, multipleValueLookupForm.getDocNum());
        }

        String backUrl = UrlFactory.parameterizeUrl(multipleValueLookupForm.getBackLocation(), parameters);
        return new ActionForward(backUrl, true);
    }


    @SuppressWarnings({"deprecation", "unchecked"}) //ResultRow, super.performMultipleValueLookup()
    @Override
    protected Collection<Entry> performMultipleValueLookup(MultipleValueLookupForm multipleValueLookupForm, List<ResultRow> resultTable, int maxRowsPerPage, boolean bounded) {
        GecMultipleValueLookupForm gecForm = (GecMultipleValueLookupForm) multipleValueLookupForm;

        LOG.info("Starting multi-value lookup...");
        super.performMultipleValueLookup(gecForm, resultTable, maxRowsPerPage, true);

        Collection<Entry> entries = filterResults(gecForm, resultTable, maxRowsPerPage);
        LOG.info(String.format("Multi-value lookup complete, found %d results.", entries.size()));


        return entries;
    }

    @Override
    public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!shouldPerformSearch(request)) {
            // Test if we're searching straight off the doc, and only kick off a real search when
            // the user has supplied the two mandetory fields, otherwise just land on the search
            // page w/out actually searching (avoids wasted operations and validation STE)
            String forwardUrl = createSearchLandingUrl(mapping, request);
            return new ActionForward(forwardUrl, true);
        } else {
            // This is not off of the GECD, so let it go through
            return super.search(mapping, form, request, response);
        }
    }


    private boolean shouldPerformSearch(HttpServletRequest request) {
        Properties props = convertRequestParamMap(request);

        String testParameter = props.getProperty(GeneralErrorCorrectionAction.INITIATED_BY_GEC_SENTINAL);
        if (StringUtils.isNotBlank(testParameter)) {
            props.remove(GeneralErrorCorrectionAction.INITIATED_BY_GEC_SENTINAL);
            // If we're here, then we came straight from GeneralErrorCorrectionAction, but
            // should only run actual search if we have both:
            String year = props.getProperty(UNIVERSITY_FISCAL_YEAR);
            String docNum = props.getProperty(DOCUMENT_NUMBER);
            return StringUtils.isNotBlank(year) && StringUtils.isNotBlank(docNum);
        }

        // We did not come from the GeneralErrorCorrectionAction, run as usual
        return true;
    }


    private String createSearchLandingUrl(ActionMapping mapping, HttpServletRequest request) {
        // Replace methodToCall's "search" to "start", this averts the actual search
        Properties props = convertRequestParamMap(request);
        props.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);

        // Forward back to the ourselves, since the search is now no-op'ed
        String basePath = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY);
        String forwardUrl = UrlFactory.parameterizeUrl(basePath + mapping.getPath() + KFSConstants.ACTION_EXTENSION_DOT_DO, props);

        return forwardUrl;
    }


    @SuppressWarnings("unchecked")//request.getParameterMap()
    private Properties convertRequestParamMap(HttpServletRequest request) {
        Map<String, String[]> requestParamMap = request.getParameterMap();
        Properties props = new Properties();

        for (Map.Entry<String, String[]> mapEntry : requestParamMap.entrySet()) {
            String key = mapEntry.getKey();
            String[] array = mapEntry.getValue();
            if (array != null && array.length > 0) {
                props.put(key, array[0]);
            }
        }

        return props;
    }


    @SuppressWarnings("deprecation") //ResultRow
    @Override
    protected List<ResultRow> selectAll(MultipleValueLookupForm multipleValueLookupForm, int maxRowsPerPage) {
        GecMultipleValueLookupForm gecForm = (GecMultipleValueLookupForm) multipleValueLookupForm;
        List<ResultRow> resultTable = super.selectAll(gecForm, maxRowsPerPage);
        filterResults(gecForm, resultTable, maxRowsPerPage);

        return resultTable;
    }


    @SuppressWarnings("deprecation") //ResultRow
    @Override
    protected List<ResultRow> unselectAll(MultipleValueLookupForm multipleValueLookupForm, int maxRowsPerPage) {
        GecMultipleValueLookupForm gecForm = (GecMultipleValueLookupForm) multipleValueLookupForm;
        List<ResultRow> resultTable = super.unselectAll(gecForm, maxRowsPerPage);
        filterResults(gecForm, resultTable, maxRowsPerPage);

        return resultTable;
    }


    @SuppressWarnings("deprecation") //ResultRow
    @Override
    protected List<ResultRow> switchToPage(MultipleValueLookupForm multipleValueLookupForm, int maxRowsPerPage) {
        GecMultipleValueLookupForm gecForm = (GecMultipleValueLookupForm) multipleValueLookupForm;
        List<ResultRow> resultTable = super.switchToPage(gecForm, maxRowsPerPage);
        filterResults(gecForm, resultTable, maxRowsPerPage);

        return resultTable;
    }


    @SuppressWarnings("deprecation") //ResultRow
    @Override
    protected List<ResultRow> sort(MultipleValueLookupForm multipleValueLookupForm, int maxRowsPerPage) {
        GecMultipleValueLookupForm gecForm = (GecMultipleValueLookupForm) multipleValueLookupForm;
        List<ResultRow> resultTable = super.sort(gecForm, maxRowsPerPage);
        filterResults(gecForm, resultTable, maxRowsPerPage);
        return resultTable;
    }


    @SuppressWarnings("deprecation") //ResultRow
    private Collection<Entry> filterResults(GecMultipleValueLookupForm gecForm, List<ResultRow> resultTable, int maxRowsPerPage) {
        int startSize = resultTable.size();
        LOG.info(String.format("Starting to filter %d raw entries...", startSize));

        Collection<Entry> entries = new ArrayList<>();
        for (ResultRow row : resultTable) {
            /*
             * Something non-obvious here:
             * row.getBusinessObject() only works due to DD config setting it up with:
             * <property name="exporterClass" value="org.kuali.rice.kew.export.DataExporter"/>
             * in {GecEntry|YeGecEntry}.xml. Otherwise rice does _not_ add the BO to the
             * RowResult.
             */
            entries.add((Entry) row.getBusinessObject());
        }

        // Separate entries to those that should be removed or disaabled
        List<Entry> entriesToRemove = new ArrayList<Entry>();
        List<String> entryIdsToDisable = new ArrayList<String>();
        for(Entry e : entries){
            if (shouldRemoveEntry(e)) {
                entriesToRemove.add(e);
            } else if (shoulDisableEntry(e)) {
                entryIdsToDisable.add(e.getObjectId());
            }
        }

        // Do actual removals
        LOG.debug("Removing " + entriesToRemove.size() + "entries.");
        for (Entry entry : entriesToRemove) {
            removeRecord(entry, resultTable, entries);
            gecForm.getCompositeObjectIdMap().remove(entry.getObjectId());
        }

        // Do actual disables
        LOG.debug("Disabling " + entryIdsToDisable.size() + "entries.");
        for (ResultRow resultRow : resultTable) {
            String entryObjectId = ((Entry) resultRow.getBusinessObject()).getObjectId();
            if (entryIdsToDisable.contains(entryObjectId)) {
                resultRow.setReturnUrl(StringUtils.EMPTY);
                resultRow.setRowReturnable(false);
            }
        }

        // Set back to jump user was on, with the column sort they had last(if any)
        gecForm.jumpToPage(gecForm.getViewedPageNumber(), resultTable.size(), maxRowsPerPage);
        if (gecForm.getPreviouslySortedColumnIndex() != null) {
            gecForm.setColumnToSortIndex(Integer.parseInt(gecForm.getPreviouslySortedColumnIndex()));
        }

        int removedSize = startSize - entries.size();
        LOG.info(String.format("Done filtering, returning %d entries, having removed %d.", entries.size(), removedSize));

        return entries;
    }


    // Short circuit on first disqualification to save time
    private boolean shouldRemoveEntry(Entry entry) {
        LOG.debug(String.format("Determining if Entry(entryId: %d) should be removed.", entry.getEntryId()));

        if (!areLookupFieldsValid(entry)) {
            // Valid Entry lookup criteria
            LOG.debug(String.format("Entry(entryId: %d) not valid by the lookup field values specifications.", entry.getEntryId()));
            return true;
        } else if (!areParametersValid(entry)) {
            // Parameter Based Validation
            LOG.debug(String.format("Entry(entryId: %d) not valid by the parameters", entry.getEntryId()));
            return true;
        } else if (isOffset(entry)) {
            // Exclude offset entries.
            LOG.debug(String.format("Entry(entryId: %d) not valid because it is an offset.", entry.getEntryId()));
            return true;
        } else if (!isTransactionAmountValid(entry)) {
            return true;
        }

        return false;
    }


    // Short circuit on first disqualification to save time
    private boolean areLookupFieldsValid(Entry entry) {
        if (!isFiscalYearValid(entry)) {
            LOG.debug(String.format("Entry(entryId: %d)'s Fiscal Year not valid per specifications.", entry.getEntryId()));
            return false;
        } else if (entry.getTransactionLedgerEntryAmount().isZero()) {
            LOG.debug(String.format("Entry(entryId: %d)'s Entry Amount not valid per specifications.", entry.getEntryId()));
            return false;
        }

        return true;
    }


    // Short circuit on first disqualification to save time
    private boolean areParametersValid(Entry entry) {
        if (!isObjectSubTypeValid(entry)) {
            LOG.debug(String.format("Entry(entryId: %d)'s Object Sub-Type is invalid", entry.getEntryId()));
            return false;
        }

        return true;
    }


    protected boolean isFiscalYearValid(Entry entry) {
        return getSystemOptions().getUniversityFiscalYear().toString().equals(entry.getUniversityFiscalYear().toString());
    }


    private boolean isObjectSubTypeValid(Entry entry) {
        @SuppressWarnings("deprecation")//ObjectCode
        ObjectCode code = getObjectCodeService().getByPrimaryId(entry.getUniversityFiscalYear(), entry.getChartOfAccountsCode(), entry.getFinancialObjectCode());
        return getParameterEvaluatorService().getParameterEvaluator(GeneralErrorCorrectionDocument.class, GeneralLedgerConstants.GeneralErrorCorrectionGroupParameters.RESTRICTED_OBJECT_SUB_TYPE_CODES, code.getFinancialObjectSubTypeCode()).evaluationSucceeds();
    }


    private boolean isOffset(Entry entry) {
        if (isOffsetDefinition(entry)) {
            LOG.debug(String.format("Entry(entryId: %d) is an Offset Definition", entry.getEntryId()));
            return true;
        }

        return false;
    }


    private boolean isOffsetDefinition(Entry entry) {
        Map<String, String> primaryKeys = new HashMap<String, String>();
        primaryKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, entry.getUniversityFiscalYear().toString());
        primaryKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, entry.getChartOfAccountsCode());
        primaryKeys.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, entry.getFinancialDocumentTypeCode());
        primaryKeys.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, entry.getFinancialBalanceTypeCode());

        OffsetDefinition offsetDefinition = getBusinessObjectService().findByPrimaryKey(OffsetDefinition.class, primaryKeys);
        if (offsetDefinition == null || offsetDefinition.getFinancialObjectCode() == null || entry.getFinancialObjectCode() == null) {
            return false;
        } else if (offsetDefinition.getFinancialObjectCode().equals(entry.getFinancialObjectCode())) {
            return true;
        }

        return false;
    }


    /*
     * Currently, the sole reason to disable, is if the entry is already associated
     * with another active GEC doc. When a GEC doc goes through route status change,
     * GeneralErrorCorrectionDocument.doRouteStatusChange() handles setting all associated
     * entry.gecDocumentNumber accordingly. So here, if we see a GEC number, it means
     * the association is still active, and we should disable selection for this GEC.
     */
    private boolean shoulDisableEntry(Entry entry) {
        LOG.debug("Determining if entry should be disabled: " + entry.toString());

        String gecDocumentNumber = entry.getGecDocumentNumber();
        if (StringUtils.isNotBlank(gecDocumentNumber)) {
            // This entry is associtated with a GEC document, disable it
            LOG.debug("Disabling Entry selection, already associated with active GEC: (entryId, gecDocNumber): (" + entry.getEntryId().toString() + ", " + entry.getGecDocumentNumber() + ")");
            return true;
        }

        LOG.debug("Entry not associated with GEC, not be disabling.");
        return false;
    }


    @SuppressWarnings("deprecation") //ResultRow
    private void removeRecord(Entry entry, List<ResultRow> resultTable, Collection c) {
        if (c != null) {
            c.remove(entry);
        }
        Iterator<ResultRow> iter = resultTable.iterator();
        while (iter.hasNext()) {
            String objectId = iter.next().getObjectId();
            if (objectId != null && objectId.equals(entry.getObjectId())) {
                iter.remove();
            }
        }
    }


    // Valid if the transaction is positive (negative indicator is not stored in the DB)
    private boolean isTransactionAmountValid(Entry entry) {
        KualiDecimal amount = entry.getTransactionLedgerEntryAmount();
        String objectTypeCode = entry.getFinancialObjectTypeCode();
        String debitCreditCode = entry.getTransactionDebitCreditCode();

        if (StringUtils.isBlank(objectTypeCode) || StringUtils.isBlank(debitCreditCode) || amount == null) {
            // Defaulting false will filter these out; we don't have enough info to do otherwise
            LOG.warn(String.format("Could not determine if amount is valid (entryId, objectTypeCode, amount): (%s, %s, %s)", entry.getEntryId(), objectTypeCode, amount));
            return false;
        }

        String testValue = getDebitDeterminerService().getConvertedAmount(objectTypeCode, debitCreditCode, amount.toString());
        KualiDecimal negPosAmount = new KualiDecimal(testValue);

        return negPosAmount.isPositive();
    }


    private ParameterEvaluatorService getParameterEvaluatorService() {
        if (parameterEvaluatorService == null) {
            parameterEvaluatorService = SpringContext.getBean(ParameterEvaluatorService.class);
        }
        return parameterEvaluatorService;
    }


    private ObjectCodeService getObjectCodeService() {
        if (objectCodeService == null) {
            objectCodeService = SpringContext.getBean(ObjectCodeService.class);
        }
        return objectCodeService;
    }


    private LookupService getLookupService() {
        if (lookupService == null) {
            lookupService = SpringContext.getBean(LookupService.class);
        }
        return lookupService;
    }


    protected SystemOptions getSystemOptions() {
        if (systemOptions == null) {
            systemOptions = getOptionsService().getCurrentYearOptions();
        }
        return systemOptions;
    }


    private OptionsService getOptionsService() {
        if (optionsService == null) {
            optionsService = SpringContext.getBean(OptionsService.class);
        }
        return optionsService;
    }


    private BusinessObjectService getBusinessObjectService() {
        if (businessObjectService == null) {
            businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        }
        return businessObjectService;
    }

    public DebitDeterminerService getDebitDeterminerService() {
        if (debitDeterminerService == null) {
            debitDeterminerService = SpringContext.getBean(DebitDeterminerService.class);
        }
        return debitDeterminerService;
    }
}
