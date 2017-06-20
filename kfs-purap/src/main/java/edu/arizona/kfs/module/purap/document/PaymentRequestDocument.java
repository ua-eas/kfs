package edu.arizona.kfs.module.purap.document;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.document.ProcurementCardDocument;
import org.kuali.kfs.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.kfs.module.purap.PurapParameterConstants.NRATaxParameters;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingCapitalAssetItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.AccountsPayableService;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.util.ExpiredOrClosedAccountEntry;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.businessobject.TaxRegion;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorHeader;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.framework.postprocessor.ActionTakenEvent;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.krad.document.DocumentAuthorizer;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

import edu.arizona.kfs.fp.businessobject.PaymentMethod;
import edu.arizona.kfs.fp.service.PaymentMethodGeneralLedgerPendingEntryService;
import edu.arizona.kfs.module.purap.PurapConstants;
import edu.arizona.kfs.module.purap.PurapParameterConstants;
import edu.arizona.kfs.module.purap.businessobject.PaymentRequestIncomeType;
import edu.arizona.kfs.module.purap.businessobject.PaymentRequestItem;
import edu.arizona.kfs.module.purap.document.service.PurapIncomeTypeHandler;
import edu.arizona.kfs.module.purap.service.PurapGeneralLedgerService;
import edu.arizona.kfs.module.purap.service.PurapUseTaxEntryArchiveService;
import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSPropertyConstants;
import edu.arizona.kfs.sys.document.IncomeTypeContainer;
import edu.arizona.kfs.vnd.businessobject.VendorDetailExtension;

@SuppressWarnings({ "unchecked", "deprecation" })
public class PaymentRequestDocument extends org.kuali.kfs.module.purap.document.PaymentRequestDocument implements IncomeTypeContainer<PaymentRequestIncomeType, Integer> {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentRequestDocument.class);
    private static final long serialVersionUID = -4229712908255426814L;

    public static final String DOCUMENT_TYPE_NON_CHECK = "PRNC";
    public static final String BANK = "bank";

    private transient PurapIncomeTypeHandler<PaymentRequestIncomeType, Integer> incomeTypeHandler;
    private List<PaymentRequestIncomeType> incomeTypes;
    private String paymentPaidYear;
    private boolean payment1099Indicator;
    private boolean taxWithholdingEntriesGenerated;


    // default this value to "A" to preserve baseline behavior
    protected String paymentMethodCode = "A"; // check
    protected transient PaymentMethod paymentMethod;
    protected static PaymentMethodGeneralLedgerPendingEntryService paymentMethodGeneralLedgerPendingEntryService;

    public String getPaymentPaidYear() {
        return paymentPaidYear;
    }

    public void setPaymentPaidYear(String paymentPaidYear) {
        this.paymentPaidYear = paymentPaidYear;
    }

    public boolean isPayment1099Indicator() {
        return payment1099Indicator;
    }

    public void setPayment1099Indicator(boolean payment1099Indicator) {
        this.payment1099Indicator = payment1099Indicator;
    }

    public String getPaymentMethodCode() {
        return paymentMethodCode;
    }

    public void setPaymentMethodCode(String paymentMethodCode) {
        this.paymentMethodCode = paymentMethodCode;
        paymentMethod = null;
    }

    public PaymentMethod getPaymentMethod() {
        if (paymentMethod == null) {
            paymentMethod = SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(PaymentMethod.class, paymentMethodCode);
        }
        return paymentMethod;
    }
    
    public boolean isTaxWithholdingEntriesGenerated() {
        return taxWithholdingEntriesGenerated;
    }

    public void setTaxWithholdingEntriesGenerated(boolean taxWithholdingEntriesGenerated) {
        this.taxWithholdingEntriesGenerated = taxWithholdingEntriesGenerated;
    }

    @Override
    public void processAfterRetrieve() {
        synchronizeBankCodeWithPaymentMethod();
        super.processAfterRetrieve();
    }

    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        LOG.debug("doRouteStatusChange() in PaymentRequestDocument.java started");
        if (getDocumentHeader().getWorkflowDocument().isProcessed() && !paymentMethodCode.equalsIgnoreCase("A")) {
            Date processDate = dateTimeService.getCurrentSqlDate();
            setExtractedTimestamp(new Timestamp(processDate.getTime()));
            setPaymentPaidTimestamp(new Timestamp(processDate.getTime()));
            SpringContext.getBean(PurapService.class).saveDocumentNoValidation(this);
        }

        // Once a doc is final, there's no need to retain archived use tax entries, because they can't be
        // reversed anymore
        WorkflowDocument workflowDocument = getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.isProcessed() || workflowDocument.isCanceled() || workflowDocument.isDisapproved()) {
            SpringContext.getBean(PurapUseTaxEntryArchiveService.class).deletePaymentRequestUseTaxArchivedEntries(this);
        }

        super.doRouteStatusChange(statusChangeEvent);
    }

    @Override
    public void prepareForSave() {
        super.prepareForSave();

        DocumentAuthorizer docAuth = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(this);

        // First, only do this if the document is in initiated status - after that, we don't want to
        // accidentally reset the bank code
        if (KewApiConstants.ROUTE_HEADER_INITIATED_CD.equals(getDocumentHeader().getWorkflowDocument().getStatus().getCode()) || KewApiConstants.ROUTE_HEADER_SAVED_CD.equals(getDocumentHeader().getWorkflowDocument().getStatus().getCode())) {
            // need to check whether the user has the permission to edit the bank code
            // if so, don't synchronize since we can't tell whether the value coming in
            // was entered by the user or not.

            if (!docAuth.isAuthorizedByTemplate(this, KFSConstants.CoreModuleNamespaces.KFS, KFSConstants.PermissionTemplate.EDIT_BANK_CODE.name, GlobalVariables.getUserSession().getPrincipalId())) {
                synchronizeBankCodeWithPaymentMethod();
            } else {
                // ensure that the name is updated properly
                refreshReferenceObject(BANK);
            }
        }

        getIncomeTypeHandler().removeZeroValuedIncomeTypes();

        // Only update paid year if the document is in final status
        if (KewApiConstants.ROUTE_HEADER_FINAL_CD.equals(getDocumentHeader().getWorkflowDocument().getStatus().getCode())) {
            if (paymentPaidTimestamp != null) {
                setPaymentPaidYear(getPaymentPaidTimestamp().toString().substring(0, 4));
            } else {
                setPaymentPaidYear(null);
            }
        }

        setPayment1099Indicator(false);
        for (PaymentRequestIncomeType incomeType : getIncomeTypes()) {
            boolean isCodeExist = StringUtils.isNotBlank(incomeType.getIncomeTypeCode());
            boolean isReportable = !incomeType.getIncomeTypeCode().equals(KFSConstants.IncomeTypeConstants.INCOME_TYPE_NON_REPORTABLE_CODE);
            if (isCodeExist && isReportable) {
                setPayment1099Indicator(true);
                break;
            }
        }

    }

    public void synchronizeBankCodeWithPaymentMethod() {
        Bank bank = getPaymentMethodGeneralLedgerPendingEntryService().getBankForPaymentMethod(getPaymentMethodCode());
        if (bank != null) {
            setBankCode(bank.getBankCode());
            setBank(bank);
        } else {
            // no bank code, no bank needed
            setBankCode(null);
            setBank(null);
        }
    }

    protected PaymentMethodGeneralLedgerPendingEntryService getPaymentMethodGeneralLedgerPendingEntryService() {
        if (paymentMethodGeneralLedgerPendingEntryService == null) {
            paymentMethodGeneralLedgerPendingEntryService = SpringContext.getBean(PaymentMethodGeneralLedgerPendingEntryService.class);
        }
        return paymentMethodGeneralLedgerPendingEntryService;
    }

    @Override
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {
        super.customizeExplicitGeneralLedgerPendingEntry(postable, explicitEntry);
        // if the document is not processed using PDP, then the cash entries
        // need to be created instead of liability
        // so, switch the document type so the offset generation uses a cash
        // offset object code
        if (!getPaymentMethodGeneralLedgerPendingEntryService().isPaymentMethodProcessedUsingPdp(getPaymentMethodCode())) {
            explicitEntry.setFinancialDocumentTypeCode(DOCUMENT_TYPE_NON_CHECK);
        }
    }

    /**
     * Update to baseline method to additionally set the payment method when a vendor is selected.
     */
    @Override
    public void populatePaymentRequestFromPurchaseOrder(PurchaseOrderDocument po, HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList) {
        this.setPurchaseOrderIdentifier(po.getPurapDocumentIdentifier());
        this.getDocumentHeader().setOrganizationDocumentNumber(po.getDocumentHeader().getOrganizationDocumentNumber());
        this.setPostingYear(po.getPostingYear());
        this.setReceivingDocumentRequiredIndicator(po.isReceivingDocumentRequiredIndicator());
        this.setUseTaxIndicator(po.isUseTaxIndicator());
        this.setPaymentRequestPositiveApprovalIndicator(po.isPaymentRequestPositiveApprovalIndicator());
        this.setVendorCustomerNumber(po.getVendorCustomerNumber());
        this.setAccountDistributionMethod(po.getAccountDistributionMethod());

        if (po.getPurchaseOrderCostSource() != null) {
            this.setPaymentRequestCostSource(po.getPurchaseOrderCostSource());
            this.setPaymentRequestCostSourceCode(po.getPurchaseOrderCostSourceCode());
        }

        if (po.getVendorShippingPaymentTerms() != null) {
            this.setVendorShippingPaymentTerms(po.getVendorShippingPaymentTerms());
            this.setVendorShippingPaymentTermsCode(po.getVendorShippingPaymentTermsCode());
        }

        if (po.getVendorPaymentTerms() != null) {
            this.setVendorPaymentTermsCode(po.getVendorPaymentTermsCode());
            this.setVendorPaymentTerms(po.getVendorPaymentTerms());
        }

        if (po.getRecurringPaymentType() != null) {
            this.setRecurringPaymentType(po.getRecurringPaymentType());
            this.setRecurringPaymentTypeCode(po.getRecurringPaymentTypeCode());
        }

        this.setVendorHeaderGeneratedIdentifier(po.getVendorHeaderGeneratedIdentifier());
        this.setVendorDetailAssignedIdentifier(po.getVendorDetailAssignedIdentifier());
        this.setVendorCustomerNumber(po.getVendorCustomerNumber());
        this.setVendorName(po.getVendorName());

        // set original vendor
        this.setOriginalVendorHeaderGeneratedIdentifier(po.getVendorHeaderGeneratedIdentifier());
        this.setOriginalVendorDetailAssignedIdentifier(po.getVendorDetailAssignedIdentifier());

        // set alternate vendor info as well
        this.setAlternateVendorHeaderGeneratedIdentifier(po.getAlternateVendorHeaderGeneratedIdentifier());
        this.setAlternateVendorDetailAssignedIdentifier(po.getAlternateVendorDetailAssignedIdentifier());

        // populate preq vendor address with the default remit address type for the vendor if found
        String userCampus = GlobalVariables.getUserSession().getPerson().getCampusCode();
        VendorAddress vendorAddress = SpringContext.getBean(VendorService.class).getVendorDefaultAddress(po.getVendorHeaderGeneratedIdentifier(), po.getVendorDetailAssignedIdentifier(), VendorConstants.AddressTypes.REMIT, userCampus);
        if (vendorAddress != null) {
            this.templateVendorAddress(vendorAddress);
            this.setVendorAddressGeneratedIdentifier(vendorAddress.getVendorAddressGeneratedIdentifier());
            setVendorAttentionName(StringUtils.defaultString(vendorAddress.getVendorAttentionName()));
        }
        else {
            // set address from PO
            this.setVendorAddressGeneratedIdentifier(po.getVendorAddressGeneratedIdentifier());
            this.setVendorLine1Address(po.getVendorLine1Address());
            this.setVendorLine2Address(po.getVendorLine2Address());
            this.setVendorCityName(po.getVendorCityName());
            this.setVendorAddressInternationalProvinceName(po.getVendorAddressInternationalProvinceName());
            this.setVendorStateCode(po.getVendorStateCode());
            this.setVendorPostalCode(po.getVendorPostalCode());
            this.setVendorCountryCode(po.getVendorCountryCode());

            boolean blankAttentionLine = StringUtils.equalsIgnoreCase(KFSConstants.ParameterValues.YES, SpringContext.getBean(ParameterService.class).getParameterValueAsString(PurapConstants.PURAP_NAMESPACE, KfsParameterConstants.DOCUMENT_COMPONENT, PurapParameterConstants.BLANK_ATTENTION_LINE_FOR_PO_TYPE_ADDRESS));

            if (blankAttentionLine) {
                setVendorAttentionName(StringUtils.EMPTY);
            }
            else {
                setVendorAttentionName(StringUtils.defaultString(po.getVendorAttentionName()));
            }
        }

        this.setPaymentRequestPayDate(SpringContext.getBean(PaymentRequestService.class).calculatePayDate(this.getInvoiceDate(), this.getVendorPaymentTerms()));

        AccountsPayableService accountsPayableService = SpringContext.getBean(AccountsPayableService.class);

        if(SpringContext.getBean(PaymentRequestService.class).encumberedItemExistsForInvoicing(po))
        {
            for (PurchaseOrderItem poi : (List<PurchaseOrderItem>) po.getItems()) {
                // check to make sure it's eligible for payment (i.e. active and has encumbrance available
                if (getDocumentSpecificService().poItemEligibleForAp(this, poi)) {
                    PaymentRequestItem paymentRequestItem = new PaymentRequestItem(poi, this, expiredOrClosedAccountList);
                    this.getItems().add(paymentRequestItem);
                    PurchasingCapitalAssetItem purchasingCAMSItem = po.getPurchasingCapitalAssetItemByItemIdentifier(poi.getItemIdentifier());
                    if (purchasingCAMSItem != null) {
                        paymentRequestItem.setCapitalAssetTransactionTypeCode(purchasingCAMSItem.getCapitalAssetTransactionTypeCode());
                    }
                }
            }
        }

        // add missing below the line
        SpringContext.getBean(PurapService.class).addBelowLineItems(this);
        this.setAccountsPayablePurchasingDocumentLinkIdentifier(po.getAccountsPayablePurchasingDocumentLinkIdentifier());

        //fix up below the line items
        SpringContext.getBean(PaymentRequestService.class).removeIneligibleAdditionalCharges(this);

        this.fixItemReferences();
        this.refreshNonUpdateableReferences();
        if (ObjectUtils.isNotNull(po.getVendorDetail()) && ObjectUtils.isNotNull(po.getVendorDetail().getExtension())) {
            if (po.getVendorDetail().getExtension() instanceof VendorDetailExtension && StringUtils.isNotBlank(((VendorDetailExtension) po.getVendorDetail().getExtension()).getDefaultB2BPaymentMethodCode())) {
                setPaymentMethodCode(((VendorDetailExtension) po.getVendorDetail().getExtension()).getDefaultB2BPaymentMethodCode());
                synchronizeBankCodeWithPaymentMethod();
            }
        }
    }

    @Override
    protected String getCustomDocumentTitle() {

        // set the workflow document title
        String poNumber = getPurchaseOrderIdentifier().toString();
        String vendorName = StringUtils.trimToEmpty(getVendorName());
        // Changing to Total Dollar Amount as this will reflect pre-tax amount for use tax transactions
        String preqAmount = getTotalDollarAmount().toString();

        String documentTitle = "";
        Set<String> nodeNames = this.getFinancialSystemDocumentHeader().getWorkflowDocument().getCurrentNodeNames();

        // if this doc is final or will be final
        if (CollectionUtils.isEmpty(nodeNames) || this.getFinancialSystemDocumentHeader().getWorkflowDocument().isFinal()) {
            documentTitle = (new StringBuilder("PO: ")).append(poNumber).append(" Vendor: ").append(vendorName).append(" Amount: ").append(preqAmount).toString();
        } else {
            PurApAccountingLine theAccount = getFirstAccount();
            String accountNumber = (theAccount != null ? StringUtils.trimToEmpty(theAccount.getAccountNumber()) : "n/a");
            String subAccountNumber = (theAccount != null ? StringUtils.trimToEmpty(theAccount.getSubAccountNumber()) : "");
            String accountChart = (theAccount != null ? theAccount.getChartOfAccountsCode() : "");
            String payDate = getDateTimeService().toDateString(getPaymentRequestPayDate());
            String indicator = getTitleIndicator();
            // set title to: PO# - VendorName - Chart/Account - total amt - Pay Date - Indicator (ie Hold, Request Cancel)
            documentTitle = (new StringBuilder("PO: ")).append(poNumber).append(" Vendor: ").append(vendorName).append(" Account: ").append(accountChart).append(" ").append(accountNumber).append(" ").append(subAccountNumber).append(" Amount: ").append(preqAmount).append(" Pay Date: ").append(payDate).append(" ").append(indicator).toString();
        }
        return documentTitle;
    }

    @Override
    public KualiDecimal getTotalDollarAmount() {
        if (this.isUseTaxIndicator()) {
            KualiDecimal totalPreTaxDollarAmount = this.getTotalPreTaxDollarAmount();
            getFinancialSystemDocumentHeader().setFinancialDocumentTotalAmount(totalPreTaxDollarAmount);
            return totalPreTaxDollarAmount;
        }
        return super.getTotalDollarAmount();
    }

    @Override
    public KualiDecimal getGrandTotal() {
        return getTotalDollarAmountAllItems(null);
    }

    @Override
    public List<PaymentRequestIncomeType> getIncomeTypes() {
        if (incomeTypes == null) {
            incomeTypes = new ArrayList<PaymentRequestIncomeType>();
        }
        return incomeTypes;
    }

    public void setIncomeTypes(List<PaymentRequestIncomeType> incomeTypes) {
        this.incomeTypes = incomeTypes;
    }

    @Override
    public Integer getDocumentIdentifier() {
        return getPurapDocumentIdentifier();
    }

    @Override
    public String getPaidYear() {
        return getIncomeTypeHandler().getYearFromTimestamp(getPaymentPaidTimestamp());
    }

    @Override
    public PurapIncomeTypeHandler<PaymentRequestIncomeType, Integer> getIncomeTypeHandler() {
        if (incomeTypeHandler == null) {
            incomeTypeHandler = new PurapIncomeTypeHandler<PaymentRequestIncomeType, Integer>(this, PaymentRequestIncomeType.class) {
            };
        }
        return incomeTypeHandler;
    }

    @Override
    public VendorHeader getVendorHeader() {
        VendorHeader retval = getVendorDetail().getVendorHeader();
        if (retval == null) {
            retval = getIncomeTypeHandler().getVendorHeaderFromVendorNumber(getVendorNumber());
        }
        return retval;
    }

    @Override
    public boolean getReportable1099TransactionsFlag() {
        boolean retval = false;
        if (getItems() != null) {
            Iterator<PurApItem> it = getItems().iterator();
            while (!retval && it.hasNext()) {
                List<PurApAccountingLine> acctlines = it.next().getBaselineSourceAccountingLines();
                if (acctlines != null) {
                    for (PurApAccountingLine acctline : acctlines) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("financial object code: " + acctline.getFinancialObjectCode() + ", is reportable" + getIncomeTypeHandler().is1099Reportable(acctline.getFinancialObjectCode()));
                        }
                        if (getIncomeTypeHandler().is1099Reportable(acctline.getFinancialObjectCode())) {
                            retval = true;
                            break;
                        }
                    }
                }
            }
        }
        return retval;
    }

    @Override
    public String getRouteStatus() {
        String retval = KFSConstants.EMPTY_STRING;
        try {
            retval = getDocumentHeader().getWorkflowDocument().getStatus().getCode();
        } catch (Exception ex) {
            LOG.warn(ex);
        }
        return retval;
    }

    @Override
    public void populateDocumentForRouting() {
        super.populateDocumentForRouting();
        getIncomeTypeHandler().removeZeroValuedIncomeTypes();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List buildListOfDeletionAwareLists() {
        List managedLists = super.buildListOfDeletionAwareLists();
        if (incomeTypes != null) {
            managedLists.add(incomeTypes);
        }
        return managedLists;
    }

    public boolean getPayment1099IndicatorForSearching() {
        return payment1099Indicator;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class getItemClass() {
        return PaymentRequestItem.class;
    }
    
    @Override
    public void doActionTaken(ActionTakenEvent event) {
        super.doActionTaken(event);
        WorkflowDocument workflowDocument = getDocumentHeader().getWorkflowDocument();
        String currentNode = null;
        Set<String> currentNodes = workflowDocument.getCurrentNodeNames();
        if (CollectionUtils.isNotEmpty(currentNodes)) {
            Object[] names = currentNodes.toArray();
            if (names.length > 0) {
                currentNode = (String)names[0];
            }
        }

        // everything in the below list requires correcting entries to be written to the GL
        if (PaymentRequestStatuses.getNodesRequiringCorrectingGeneralLedgerEntries().contains(currentNode)) {
            SpringContext.getBean(PurapGeneralLedgerService.class).generateEntriesModifyPaymentRequest(this, currentNode);
        }
    }
    
    @Override
    public boolean processNodeChange(String newNodeName, String oldNodeName) {
        if (PaymentRequestStatuses.APPDOC_AUTO_APPROVED.equals(getApplicationDocumentStatus())) {
            // do nothing for an auto approval
            return false;
        }
        if (PaymentRequestStatuses.NODE_ADHOC_REVIEW.equals(oldNodeName)) {
            SpringContext.getBean(AccountsPayableService.class).performLogicForFullEntryCompleted(this);
        } else if (PurapConstants.PaymentRequestStatuses.NODE_VENDOR_TAX_REVIEW.equals(oldNodeName)) {
            this.setTaxWithholdingEntriesGenerated(true);
        }
        return true;
    }
    
	@Override
	public boolean generateGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
		LOG.debug("processGenerateGeneralLedgerPendingEntries(AccountingDocument, AccountingLine, GeneralLedgerPendingEntrySequenceHelper) - start");

		GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();
		processExplicitGeneralLedgerPendingEntry(sequenceHelper, glpeSourceDetail, explicitEntry);

		sequenceHelper.increment();

		GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(explicitEntry);
		boolean success = processOffsetGeneralLedgerPendingEntry(sequenceHelper, glpeSourceDetail, explicitEntry, offsetEntry);

		processUseTaxOffsetGeneralLedgerPendingEntries(sequenceHelper, glpeSourceDetail, explicitEntry, offsetEntry);

		processTaxWithholdingGeneralLedgerPendingEntriesPREQ(sequenceHelper, glpeSourceDetail, explicitEntry, offsetEntry);

		LOG.debug("processGenerateGeneralLedgerPendingEntries(AccountingDocument, AccountingLine, GeneralLedgerPendingEntrySequenceHelper) - end");
		return success;
	}

	public List<PurApItem> getItemsSetupAlternateAmount() {
		List<PurApItem> returnList = new ArrayList<PurApItem>();
		for (Iterator<PaymentRequestItem> iter = getItems().iterator(); iter.hasNext();) {
			PaymentRequestItem item = iter.next();
			KualiDecimal remitAmount = item.getTotalRemitAmount();
			if (!PurapConstants.ItemTypeCodes.ITEM_TYPE_FEDERAL_TAX_CODE.equals(item.getItemTypeCode()) && !PurapConstants.ItemTypeCodes.ITEM_TYPE_STATE_TAX_CODE.equals(item.getItemTypeCode())) {
				if ((StringUtils.isNotEmpty(getTaxClassificationCode()) && !StringUtils.equalsIgnoreCase(getTaxClassificationCode(), "N")) && ((getTaxFederalPercent().compareTo(new BigDecimal(0)) != 0) || (getTaxStatePercent().compareTo(new BigDecimal(0)) != 0))) {
					KualiDecimal taxPercentWhole = getTaxFederalPercentShort().add(getTaxStatePercentShort());
					KualiDecimal taxPercent = taxPercentWhole.divide(new KualiDecimal(100));
					KualiDecimal currentItemWithholdingAmount = remitAmount.multiply(taxPercent);
					remitAmount = remitAmount.subtract(currentItemWithholdingAmount);
				}
			}

			if ((remitAmount != null) && KualiDecimal.ZERO.compareTo(remitAmount) != 0) {
				KualiDecimal accountTotal = KualiDecimal.ZERO;
				PurApAccountingLine lastAccount = null;
				for (PurApAccountingLine account : item.getSourceAccountingLines()) {
					if (ObjectUtils.isNotNull(account.getAccountLinePercent())) {
						BigDecimal pct = new BigDecimal(account.getAccountLinePercent().toString()).divide(new BigDecimal(100));
						account.setAlternateAmountForGLEntryCreation(new KualiDecimal(pct.multiply(new BigDecimal(remitAmount.toString())).setScale(KualiDecimal.SCALE, KualiDecimal.ROUND_BEHAVIOR)));
					} else {
						account.setAlternateAmountForGLEntryCreation(KualiDecimal.ZERO);
					}
					accountTotal = accountTotal.add(account.getAlternateAmountForGLEntryCreation());
					lastAccount = account;
				}

				if (lastAccount != null) {
					KualiDecimal difference = remitAmount.subtract(accountTotal);
					lastAccount.setAlternateAmountForGLEntryCreation(lastAccount.getAlternateAmountForGLEntryCreation().add(difference));
				}
			} else {
				for (PurApAccountingLine account : item.getSourceAccountingLines()) {
					account.setAlternateAmountForGLEntryCreation(KualiDecimal.ZERO);
				}
			}
			returnList.add(item);
		}
		return returnList;
	}

	@Override
	public boolean customizeOffsetGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail accountingLine, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
		boolean value = super.customizeOffsetGeneralLedgerPendingEntry(accountingLine, explicitEntry, offsetEntry);
		ParameterService parameterService = SpringContext.getBean(ParameterService.class);
		String taxAccount = parameterService.getParameterValueAsString(PaymentRequestDocument.class, NRATaxParameters.FEDERAL_TAX_PARM_PREFIX + NRATaxParameters.TAX_PARM_ACCOUNT_SUFFIX);
		if(offsetEntry != null && this.offsetUseTax != null) {
            offsetEntry.setFinancialObjectTypeCode(offsetEntry.getFinancialObject().getFinancialObjectTypeCode());
        }
		if (offsetEntry.getAccountNumber().equals(taxAccount)) {
			String glpeOffsetObjectCode = parameterService.getParameterValueAsString(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.GENERAL_LEDGER_PENDING_ENTRY_OFFSET_OBJECT_CODE);
			SystemOptions options = SpringContext.getBean(OptionsService.class).getOptions(explicitEntry.getUniversityFiscalYear());
			offsetEntry.setFinancialObjectCode(glpeOffsetObjectCode);
			offsetEntry.refreshReferenceObject(KFSPropertyConstants.FINANCIAL_OBJECT);
			offsetEntry.setFinancialObjectTypeCode(options.getFinancialObjectTypeAssetsCd());
			offsetEntry.refreshReferenceObject(KFSPropertyConstants.OBJECT_TYPE);
		} else {
			value = false;
		}
		return value;
	}

	private void processTaxWithholdingGeneralLedgerPendingEntriesPREQ(GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
		String incomeClassCode = ((PaymentRequestDocument) this).getTaxClassificationCode();
		if ((StringUtils.isNotEmpty(incomeClassCode) && !StringUtils.equalsIgnoreCase(incomeClassCode, "N")) && ((((PaymentRequestDocument) this).getTaxFederalPercent().compareTo(new BigDecimal(0)) != 0) || (((PaymentRequestDocument) this).getTaxStatePercent().compareTo(new BigDecimal(0)) != 0))) {
			ParameterService parameterService = SpringContext.getBean(ParameterService.class);
			String taxAccount = parameterService.getParameterValueAsString(PaymentRequestDocument.class, NRATaxParameters.FEDERAL_TAX_PARM_PREFIX + NRATaxParameters.TAX_PARM_ACCOUNT_SUFFIX);
			if (!offsetEntry.getAccountNumber().equals(taxAccount)) {
				processTaxWithholdingGeneralLedgerPendingEntries(sequenceHelper, glpeSourceDetail, explicitEntry, offsetEntry, parameterService);
			}
		}
	}

	private void processTaxWithholdingGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry, ParameterService parameterService) {
		sequenceHelper.increment();

		GeneralLedgerPendingEntry taxWithholdingExplicit = new GeneralLedgerPendingEntry(explicitEntry);
		taxWithholdingExplicit.setTransactionLedgerEntrySequenceNumber(sequenceHelper.getSequenceCounter());
		taxWithholdingExplicit.setTransactionDebitCreditCode(offsetEntry.getTransactionDebitCreditCode());
		addPendingEntry(taxWithholdingExplicit);

		String glpOffsetObjectCode = parameterService.getParameterValueAsString(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.GENERAL_LEDGER_PENDING_ENTRY_OFFSET_OBJECT_CODE);
		SystemOptions options = SpringContext.getBean(OptionsService.class).getOptions(explicitEntry.getUniversityFiscalYear());

		sequenceHelper.increment();

		GeneralLedgerPendingEntry taxWithholdingOffset = new GeneralLedgerPendingEntry(offsetEntry);
		taxWithholdingOffset.setTransactionLedgerEntrySequenceNumber(sequenceHelper.getSequenceCounter());
		taxWithholdingOffset.setFinancialObjectCode(glpOffsetObjectCode);
		taxWithholdingOffset.setFinancialObjectTypeCode(options.getFinancialObjectTypeAssetsCd());
		taxWithholdingOffset.setTransactionDebitCreditCode(explicitEntry.getTransactionDebitCreditCode());

		addPendingEntry(taxWithholdingOffset);
	}

	private void processUseTaxOffsetGeneralLedgerPendingEntries( GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
		ParameterService parameterService = SpringContext.getBean(ParameterService.class);

		String glpeOffsetObjectCode = parameterService.getParameterValueAsString(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.GENERAL_LEDGER_PENDING_ENTRY_OFFSET_OBJECT_CODE);

		Map<String, String> pkMap = new HashMap<String, String>();
		pkMap.put(KFSPropertyConstants.TAX_REGION_CODE, parameterService.getParameterValueAsString(ProcurementCardDocument.class, PurapParameterConstants.GL_USETAX_TAX_REGION));
		TaxRegion taxRegion = (TaxRegion) getBusinessObjectService().findByPrimaryKey(TaxRegion.class, pkMap);

		if (offsetEntry.getAccountNumber().equals(taxRegion.getAccountNumber())) {
			offsetEntry.setSubAccountNumber(null);
			offsetEntry.setSubAccount(null);
			offsetEntry.setFinancialSubObjectCode(null);
			offsetEntry.setFinancialSubObject(null);
			offsetEntry.setProjectCode(null);

			SystemOptions options = SpringContext.getBean(OptionsService.class).getOptions(explicitEntry.getUniversityFiscalYear());

			sequenceHelper.increment();

			GeneralLedgerPendingEntry useTaxExplicit = new GeneralLedgerPendingEntry(explicitEntry);
			useTaxExplicit.setTransactionLedgerEntrySequenceNumber(sequenceHelper.getSequenceCounter());
			useTaxExplicit.setTransactionLedgerEntryDescription(PurapConstants.GLPE_USE_TAX_GENERATED_OFFSET_DESCRIPTION);
			useTaxExplicit.setFinancialObjectCode(glpeOffsetObjectCode);
			useTaxExplicit.setFinancialObjectTypeCode(options.getFinancialObjectTypeAssetsCd());
			useTaxExplicit.setTransactionDebitCreditCode(offsetEntry.getTransactionDebitCreditCode());

			addPendingEntry(useTaxExplicit);

			sequenceHelper.increment();

			GeneralLedgerPendingEntry useTaxOffset = new GeneralLedgerPendingEntry(offsetEntry);
			useTaxOffset.setTransactionLedgerEntrySequenceNumber(sequenceHelper.getSequenceCounter());
			useTaxOffset.setTransactionLedgerEntryDescription(PurapConstants.GLPE_USE_TAX_GENERATED_OFFSET_DESCRIPTION);
			useTaxOffset.setFinancialObjectCode(glpeOffsetObjectCode);
			useTaxOffset.setFinancialObjectTypeCode(options.getFinancialObjectTypeAssetsCd());
			useTaxOffset.setTransactionDebitCreditCode(explicitEntry.getTransactionDebitCreditCode());

			addPendingEntry(useTaxOffset);
		}
	}

}
