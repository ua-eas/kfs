package edu.arizona.kfs.module.purap.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.batch.ElectronicInvoiceStep;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceRejectReason;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceRejectReasonType;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.service.impl.ElectronicInvoiceItemHolder;
import org.kuali.kfs.module.purap.service.impl.ElectronicInvoiceOrderHolder;
import org.kuali.kfs.module.purap.util.PurApItemUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.vnd.businessobject.PurchaseOrderCostSource;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

import edu.arizona.kfs.module.purap.PurapConstants;
import edu.arizona.kfs.module.purap.PurapKeyConstants;
import edu.arizona.kfs.sys.service.TaxHelperService;

public class ElectronicInvoiceMatchingServiceImpl extends org.kuali.kfs.module.purap.service.impl.ElectronicInvoiceMatchingServiceImpl {

    TaxHelperService taxHelperService;

    public void setTaxHelperService(TaxHelperService taxHelperService) {
        this.taxHelperService = taxHelperService;
    }

    @Override
    public void doMatchingProcess(ElectronicInvoiceOrderHolder orderHolder) {

        if (LOG.isInfoEnabled()) {
            LOG.info("Matching process started");
        }

        upperVariancePercentString = SpringContext.getBean(ParameterService.class).getParameterValueAsString(ElectronicInvoiceStep.class, PurapParameterConstants.ElectronicInvoiceParameters.SALES_TAX_UPPER_VARIANCE_PERCENT);
        lowerVariancePercentString = SpringContext.getBean(ParameterService.class).getParameterValueAsString(ElectronicInvoiceStep.class, PurapParameterConstants.ElectronicInvoiceParameters.SALES_TAX_LOWER_VARIANCE_PERCENT);

        try {
            if (orderHolder.isValidateHeaderInformation()) {

                validateHeaderInformation(orderHolder);

                if (orderHolder.isInvoiceRejected()) {
                    if (LOG.isInfoEnabled()) {
                        LOG.info("Matching process failed at header validation");
                    }
                }
            }

            validateInvoiceDetails(orderHolder);

            if (orderHolder.isInvoiceRejected()) {
                if (LOG.isInfoEnabled()) {
                    LOG.info("Matching process failed at order detail validation");
                }
                return;
            }

        } catch (NumberFormatException e) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Matching process matching failed due to number format exception " + e.getMessage());
            }
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.INVALID_NUMBER_FORMAT, e.getMessage(), orderHolder.getFileName());
            orderHolder.addInvoiceHeaderRejectReason(rejectReason);
        }

        if (LOG.isInfoEnabled()) {
            LOG.info("Matching process ended successfully");
        }
    }

    @Override
    protected void validateHeaderInformation(ElectronicInvoiceOrderHolder orderHolder) {

        String dunsField = PurapConstants.ElectronicInvoice.RejectDocumentFields.VENDOR_DUNS_NUMBER;
        String applnResourceKeyName = PurapKeyConstants.ERROR_REJECT_INVALID_DUNS;

        if (StringUtils.isEmpty(orderHolder.getDunsNumber())) {
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.DUNS_NOT_FOUND, null, orderHolder.getFileName());
            orderHolder.addInvoiceHeaderRejectReason(rejectReason, dunsField, applnResourceKeyName);
        }

        if (orderHolder.isRejectDocumentHolder()) {
            VendorDetail vendorDetail = SpringContext.getBean(VendorService.class).getVendorByDunsNumber(orderHolder.getDunsNumber());
            if (vendorDetail == null) {
                ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.DUNS_INVALID, null, orderHolder.getFileName());
                orderHolder.addInvoiceHeaderRejectReason(rejectReason, dunsField, applnResourceKeyName);
            }
        } else {
            if (orderHolder.getVendorHeaderId() == null && orderHolder.getVendorDetailId() == null) {
                ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.DUNS_INVALID, null, orderHolder.getFileName());
                orderHolder.addInvoiceHeaderRejectReason(rejectReason, dunsField, applnResourceKeyName);
            }
        }

        String invoiceNumberField = PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_FILE_NUMBER;
        if (!orderHolder.isInvoiceNumberAcceptIndicatorEnabled()) {
            if (StringUtils.isEmpty(orderHolder.getInvoiceNumber())) {
                ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.INVOICE_ID_EMPTY, null, orderHolder.getFileName());
                orderHolder.addInvoiceHeaderRejectReason(rejectReason, invoiceNumberField, PurapKeyConstants.ERROR_REJECT_INVOICE_NUMBER_EMPTY);
            }
        }

        String invoiceDateField = PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_FILE_DATE;

        if (StringUtils.isEmpty(orderHolder.getInvoiceDateString()) || orderHolder.getInvoiceDate() == null) {
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.INVOICE_DATE_INVALID, null, orderHolder.getFileName());
            orderHolder.addInvoiceHeaderRejectReason(rejectReason, invoiceDateField, PurapKeyConstants.ERROR_REJECT_INVOICE_DATE_INVALID);
        } else if (orderHolder.getInvoiceDate().after(dateTimeService.getCurrentDate())) {
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.INVOICE_DATE_GREATER, null, orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason, invoiceDateField, PurapKeyConstants.ERROR_REJECT_INVOICE_DATE_GREATER);
        }

        if (orderHolder.isInformationOnly()) {
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.INFORMATION_ONLY, null, orderHolder.getFileName());
            orderHolder.addInvoiceHeaderRejectReason(rejectReason);
        }

        validateSummaryAmounts(orderHolder);

        validateItemTypes(orderHolder);

    }

    @Override
    protected void validateInvoiceDetails(ElectronicInvoiceOrderHolder orderHolder) {

        validatePurchaseOrderMatch(orderHolder);

        validateInvoiceItems(orderHolder);

        if (LOG.isInfoEnabled()) {
            if (!orderHolder.isInvoiceRejected()) {
                LOG.info("Purchase order document match done successfully");
            }
        }
    }

    @Override
    protected void validatePurchaseOrderMatch(ElectronicInvoiceOrderHolder orderHolder) {

        String poIDFieldName = PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_PO_ID;
        String poID = orderHolder.getInvoicePurchaseOrderID();

        if (StringUtils.isEmpty(poID)) {
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.PO_ID_EMPTY, null, orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason, poIDFieldName, PurapKeyConstants.ERROR_REJECT_INVOICE_POID_EMPTY);
            return;
        }

        String extraDesc = "Invoice Order ID:" + poID;

        if (!NumberUtils.isDigits(poID)) {
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.PO_ID_INVALID_FORMAT, extraDesc, orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason, poIDFieldName, PurapKeyConstants.ERROR_REJECT_INVOICE_POID_INVALID);
        }

        PurchaseOrderDocument poDoc = orderHolder.getPurchaseOrderDocument();

        if (poDoc == null) {
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.PO_NOT_EXISTS, extraDesc, orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason, poIDFieldName, PurapKeyConstants.ERROR_REJECT_INVOICE__PO_NOT_EXISTS);
            return;
        }

        if (!poDoc.getApplicationDocumentStatus().equals(PurchaseOrderStatuses.APPDOC_OPEN)) {
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.PO_NOT_OPEN, null, orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason, poIDFieldName, PurapKeyConstants.ERROR_REJECT_INVOICE_PO_CLOSED);
        }

        if (poDoc.getVendorHeaderGeneratedIdentifier() == null || poDoc.getVendorDetailAssignedIdentifier() == null || !(poDoc.getVendorHeaderGeneratedIdentifier().equals(orderHolder.getVendorHeaderId()) && poDoc.getVendorDetailAssignedIdentifier().equals(orderHolder.getVendorDetailId()))) {
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.PO_VENDOR_NOT_MATCHES_WITH_INVOICE_VENDOR, null, orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason);
        }

    }

    @Override
    protected void validateInvoiceItem(ElectronicInvoiceItemHolder itemHolder, Set poLineNumbers) {

        PurchaseOrderItem poItem = itemHolder.getPurchaseOrderItem();
        ElectronicInvoiceOrderHolder orderHolder = itemHolder.getInvoiceOrderHolder();

        if (poItem == null) {
            String extraDescription = "Invoice Item Line Number:" + itemHolder.getInvoiceItemLineNumber();
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.NO_MATCHING_PO_ITEM, extraDescription, orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason, PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_ITEM_LINE_NUMBER, PurapKeyConstants.ERROR_REJECT_INVOICE__ITEM_NOMATCH);
            return;
        }

        if (poLineNumbers.contains(itemHolder.getInvoiceItemLineNumber())) {
            String extraDescription = "Invoice Item Line Number:" + itemHolder.getInvoiceItemLineNumber();
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.DUPLIATE_INVOICE_LINE_ITEM, extraDescription, orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason, PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_ITEM_LINE_NUMBER, PurapKeyConstants.ERROR_REJECT_PO_ITEM_DUPLICATE);
        } else {
            poLineNumbers.add(itemHolder.getInvoiceItemLineNumber());
        }

        if (!poItem.isItemActiveIndicator()) {
            String extraDescription = "PO Item Line Number:" + poItem.getItemLineNumber();
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.INACTIVE_LINE_ITEM, extraDescription, orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason, PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_ITEM_LINE_NUMBER, PurapKeyConstants.ERROR_REJECT_PO_ITEM_INACTIVE);
        }

        if (!itemHolder.isCatalogNumberAcceptIndicatorEnabled()) {
            validateCatalogNumber(itemHolder);
        }

        if (!itemHolder.isUnitOfMeasureAcceptIndicatorEnabled()) {
            if (!StringUtils.equals(poItem.getItemUnitOfMeasureCode(), itemHolder.getInvoiceItemUnitOfMeasureCode())) {
                String extraDescription = "Invoice UOM:" + itemHolder.getInvoiceItemUnitOfMeasureCode() + ", PO UOM:" + poItem.getItemUnitOfMeasureCode();
                ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.UNIT_OF_MEASURE_MISMATCH, extraDescription, orderHolder.getFileName());
                orderHolder.addInvoiceOrderRejectReason(rejectReason, PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_ITEM_UOM, PurapKeyConstants.ERROR_REJECT_UOM_MISMATCH);
            }
        }

        if (!itemHolder.isUnitPriceAcceptIndicatorEnabled()) {
            validateUnitPrice(itemHolder);
        }

        validateSalesTax(itemHolder);

        if (poItem.getItemQuantity() != null) {
            validateQtyBasedItem(itemHolder);
        } else {
            validateNonQtyBasedItem(itemHolder);
        }

    }

    @Override
    protected void validateQtyBasedItem(ElectronicInvoiceItemHolder itemHolder) {

        PurchaseOrderItem poItem = itemHolder.getPurchaseOrderItem();

        String fileName = itemHolder.getInvoiceOrderHolder().getFileName();
        ElectronicInvoiceOrderHolder orderHolder = itemHolder.getInvoiceOrderHolder();

        if (KualiDecimal.ZERO.compareTo(poItem.getItemOutstandingEncumberedQuantity()) >= 0) {
            // we have no quantity left encumbered on the po item
            String extraDescription = "Invoice Item Line Number:" + itemHolder.getInvoiceItemLineNumber();
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.OUTSTANDING_ENCUMBERED_QTY_AVAILABLE, extraDescription, orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason, PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_ITEM_QUANTITY, PurapKeyConstants.ERROR_REJECT_POITEM_OUTSTANDING_QTY);
        }

        if (itemHolder.getInvoiceItemQuantity() == null || itemHolder.getInvoiceItemQuantity().equals(BigDecimal.ZERO)) {
            // we have quantity entered on the PO Item but the Invoice has no quantity
            String extraDescription = "Invoice Item Line Number:" + itemHolder.getInvoiceItemLineNumber();
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.INVOICE_QTY_EMPTY, extraDescription, orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason, PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_ITEM_QUANTITY, PurapKeyConstants.ERROR_REJECT_POITEM_INVOICE_QTY_EMPTY);
        } else {

            if (!itemHolder.getInvoiceOrderHolder().getPurchaseOrderDocument().isReceivingDocumentRequiredIndicator()) {

                if ((itemHolder.getInvoiceItemQuantity().compareTo(poItem.getItemOutstandingEncumberedQuantity().bigDecimalValue())) > 0) {
                    // we have more quantity on the e-invoice than left outstanding encumbered on the PO item
                    String extraDescription = "Invoice Item Line Number:" + itemHolder.getInvoiceItemLineNumber();
                    ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.PO_ITEM_QTY_LESSTHAN_INVOICE_ITEM_QTY, extraDescription, orderHolder.getFileName());
                    orderHolder.addInvoiceOrderRejectReason(rejectReason, PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_ITEM_QUANTITY, PurapKeyConstants.ERROR_REJECT_POITEM_LESS_OUTSTANDING_QTY);
                }
            }
        }

    }

    @Override
    protected void validateNonQtyBasedItem(ElectronicInvoiceItemHolder itemHolder) {

        PurchaseOrderItem poItem = itemHolder.getPurchaseOrderItem();

        String fileName = itemHolder.getInvoiceOrderHolder().getFileName();
        ElectronicInvoiceOrderHolder orderHolder = itemHolder.getInvoiceOrderHolder();

        if ((KualiDecimal.ZERO.compareTo(poItem.getItemOutstandingEncumberedAmount())) >= 0) {
            // we have no dollars left encumbered on the po item
            String extraDescription = "Invoice Item Line Number:" + itemHolder.getInvoiceItemLineNumber();
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.OUTSTANDING_ENCUMBERED_AMT_AVAILABLE, extraDescription, orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason, PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_ITEM_LINE_NUMBER, PurapKeyConstants.ERROR_REJECT_POITEM_OUTSTANDING_EMCUMBERED_AMOUNT);
        } else {
            // we have encumbered dollars left on PO
            if (((itemHolder.getInvoiceItemSubTotalAmount().setScale(KualiDecimal.SCALE, KualiDecimal.ROUND_BEHAVIOR)).compareTo(poItem.getItemOutstandingEncumberedAmount().bigDecimalValue())) > 0) {
                String extraDescription = "Invoice Item Line Number:" + itemHolder.getInvoiceItemLineNumber();
                ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.PO_ITEM_AMT_LESSTHAN_INVOICE_ITEM_AMT, extraDescription, orderHolder.getFileName());
                orderHolder.addInvoiceOrderRejectReason(rejectReason, PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_ITEM_LINE_NUMBER, PurapKeyConstants.ERROR_REJECT_POITEM_LESS_OUTSTANDING_EMCUMBERED_AMOUNT);
            }

        }
    }

    @Override
    protected void validateUnitPrice(ElectronicInvoiceItemHolder itemHolder) {

        PurchaseOrderCostSource costSource = itemHolder.getInvoiceOrderHolder().getPurchaseOrderDocument().getPurchaseOrderCostSource();
        PurchaseOrderItem poItem = itemHolder.getPurchaseOrderItem();
        ElectronicInvoiceOrderHolder orderHolder = itemHolder.getInvoiceOrderHolder();

        String extraDescription = "Invoice Item Line Number:" + itemHolder.getInvoiceItemLineNumber();

        if (itemHolder.getInvoiceItemUnitPrice() == null || itemHolder.getInvoiceItemUnitPrice().equals(BigDecimal.ZERO)) {
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.INVOICE_AMT_EMPTY, extraDescription, orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason, PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_ITEM_UNIT_PRICE, PurapKeyConstants.ERROR_REJECT_UNITPRICE_EMPTY);
            return; // validations beyond this point require a valid unit price
        }

        BigDecimal actualVariance = itemHolder.getInvoiceItemUnitPrice().subtract(poItem.getItemUnitPrice());

        BigDecimal lowerPercentage = null;
        if (costSource.getItemUnitPriceLowerVariancePercent() != null) {
            // Checking for lower variance
            lowerPercentage = costSource.getItemUnitPriceLowerVariancePercent();
        } else {
            // If the cost source itemUnitPriceLowerVariancePercent is null then
            // we'll use the exact match (100%).
            lowerPercentage = new BigDecimal(100);
        }

        BigDecimal lowerAcceptableVariance = (lowerPercentage.divide(new BigDecimal(100))).multiply(poItem.getItemUnitPrice()).negate();

        if (lowerAcceptableVariance.compareTo(actualVariance) > 0) {
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.INVOICE_AMT_LESSER_THAN_LOWER_VARIANCE, extraDescription, orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason, PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_ITEM_UNIT_PRICE, PurapKeyConstants.ERROR_REJECT_UNITPRICE_LOWERVARIANCE);
        }

        BigDecimal upperPercentage = null;

        if (costSource.getItemUnitPriceUpperVariancePercent() != null) {
            // Checking for upper variance
            upperPercentage = costSource.getItemUnitPriceUpperVariancePercent();
        } else {
            // If the cost source itemUnitPriceLowerVariancePercent is null then
            // we'll use the exact match (100%).
            upperPercentage = new BigDecimal(100);
        }
        BigDecimal upperAcceptableVariance = (upperPercentage.divide(new BigDecimal(100))).multiply(poItem.getItemUnitPrice());

        if (upperAcceptableVariance.compareTo(actualVariance) < 0) {
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.INVOICE_AMT_GREATER_THAN_UPPER_VARIANCE, extraDescription, orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason, PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_ITEM_UNIT_PRICE, PurapKeyConstants.ERROR_REJECT_UNITPRICE_UPPERVARIANCE);
        }

    }

    @Override
    protected void validateSalesTax(ElectronicInvoiceItemHolder itemHolder) {

        if (LOG.isInfoEnabled()) {
            LOG.info("Validating sales tax");
        }

        ElectronicInvoiceOrderHolder orderHolder = itemHolder.getInvoiceOrderHolder();
        PurchaseOrderItem poItem = itemHolder.getPurchaseOrderItem();
        KualiDecimal invoiceSalesTaxAmount = new KualiDecimal(itemHolder.getTaxAmount());

        boolean enableSalesTaxInd = SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.ENABLE_SALES_TAX_IND);

        boolean salesTaxUsed = false;
        PurchaseOrderDocument poDoc = orderHolder.getPurchaseOrderDocument();
        List<PurApItem> items = PurApItemUtils.getAboveTheLineOnly(poDoc.getItems());
        for (PurApItem item : items) {
            if (item.getItemType().isTaxableIndicator()) {
                salesTaxUsed = true;
                break;
            }
        }
        boolean useTaxUsed = poDoc.isUseTaxIndicator();
        enableSalesTaxInd &= (poItem.getItemType().isTaxableIndicator() && salesTaxUsed && !useTaxUsed);

        if (LOG.isInfoEnabled()) {
            LOG.info("Sales Tax Enable Indicator - " + enableSalesTaxInd);
            LOG.info("Invoice item tax amount - " + invoiceSalesTaxAmount);
        }
        if (!enableSalesTaxInd) {
            // if the line item is not taxable, or if it's use-tax, then the eInvoice should not send tax amounts
            if (invoiceSalesTaxAmount.isNonZero()) {
                String extraDescription = "Item Tax Amount:" + invoiceSalesTaxAmount;
                ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.TAX_SUMMARY_AMT_EXISTS, extraDescription, orderHolder.getFileName());
                orderHolder.addInvoiceHeaderRejectReason(rejectReason);
            }
            return; // stop processing taxes if taxes are not enabled
        }

        // For reject doc, trans date should be the einvoice processed date.
        java.sql.Date transTaxDate = itemHolder.getInvoiceOrderHolder().getInvoiceProcessedDate();
        String deliveryPostalCode = poItem.getPurchaseOrder().getDeliveryPostalCode();
        KualiDecimal extendedPrice = new KualiDecimal(getExtendedPrice(itemHolder).setScale(KualiDecimal.SCALE, KualiDecimal.ROUND_BEHAVIOR));

        KualiDecimal taxAmountCalculated = KualiDecimal.ZERO;
        if (useTaxUsed) {
            taxAmountCalculated = taxHelperService.getTotalUseTaxAmount(transTaxDate, deliveryPostalCode, extendedPrice);
        } else {
            taxAmountCalculated = taxService.getTotalSalesTaxAmount(transTaxDate, deliveryPostalCode, extendedPrice);
        }
        KualiDecimal actualVariance = invoiceSalesTaxAmount.subtract(taxAmountCalculated);

        if (LOG.isInfoEnabled()) {
            LOG.info("Sales Tax Upper Variance param - " + upperVariancePercentString);
            LOG.info("Sales Tax Lower Variance param - " + lowerVariancePercentString);
            LOG.info("Trans date (from invoice/rejectdoc) - " + transTaxDate);
            LOG.info("Delivery Postal Code - " + deliveryPostalCode);
            LOG.info("Extended price - " + extendedPrice);
            LOG.info("Invoice Item Tax Amount - " + itemHolder.getTaxAmount().toString());
            if (useTaxUsed) {
                LOG.info("Use Tax amount (from tax helper service) - " + taxAmountCalculated);
            } else {
                LOG.info("Sales Tax amount (from sales tax service) - " + taxAmountCalculated);
            }
        }

        final String extraDescription = "Invoice Item Line Number:" + itemHolder.getInvoiceItemLineNumber() + "; sales tax amt s/b $" + taxAmountCalculated.toString();

        if (StringUtils.isNotEmpty(upperVariancePercentString) && actualVariance.isPositive()) {

            double upperVariancePercent = Double.valueOf(upperVariancePercentString) * .01d;
            double upperAcceptableVariance = taxAmountCalculated.doubleValue() * upperVariancePercent;

            if (actualVariance.isGreaterThan(new KualiDecimal(upperAcceptableVariance))) {
                ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.SALES_TAX_AMT_GREATER_THAN_UPPER_VARIANCE, extraDescription, orderHolder.getFileName());
                orderHolder.addInvoiceOrderRejectReason(rejectReason, PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_ITEM_TAX_AMT, PurapKeyConstants.ERROR_REJECT_TAXAMOUNT_UPPERVARIANCE);
                return;
            }

        }

        if (StringUtils.isNotEmpty(lowerVariancePercentString) && actualVariance.isNegative()) {

            double lowerVariancePercent = Double.valueOf(lowerVariancePercentString) * .01d;
            double lowerAcceptableVariance = taxAmountCalculated.doubleValue() * lowerVariancePercent;

            if (actualVariance.abs().isGreaterThan(new KualiDecimal(lowerAcceptableVariance))) {
                ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.SALES_TAX_AMT_LESSER_THAN_LOWER_VARIANCE, extraDescription, orderHolder.getFileName());
                orderHolder.addInvoiceOrderRejectReason(rejectReason, PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_ITEM_TAX_AMT, PurapKeyConstants.ERROR_REJECT_TAXAMOUNT_LOWERVARIANCE);
            }
        }

    }

    @Override
    public ElectronicInvoiceRejectReason createRejectReason(String rejectReasonTypeCode, String extraDescription, String fileName) {

        ElectronicInvoiceRejectReasonType rejectReasonType = getElectronicInvoiceRejectReasonType(rejectReasonTypeCode);
        ElectronicInvoiceRejectReason eInvoiceRejectReason = new ElectronicInvoiceRejectReason();

        if (rejectReasonType == null) {
            throw new NullPointerException("Reject reason type for " + rejectReasonTypeCode + " not available in DB");
        }
        eInvoiceRejectReason.setInvoiceFileName(fileName);
        eInvoiceRejectReason.setInvoiceRejectReasonTypeCode(rejectReasonTypeCode);

        if (StringUtils.isNotEmpty(extraDescription)) {
            String rejectReasonDesc = rejectReasonType.getInvoiceRejectReasonTypeDescription() + " (" + extraDescription + ")";
            if (rejectReasonDesc.length() > 400) {
                eInvoiceRejectReason.setInvoiceRejectReasonDescription(rejectReasonDesc.substring(0, 400));
            } else {
                eInvoiceRejectReason.setInvoiceRejectReasonDescription(rejectReasonDesc);
            }
        } else {
            eInvoiceRejectReason.setInvoiceRejectReasonDescription(rejectReasonType.getInvoiceRejectReasonTypeDescription());
        }

        return eInvoiceRejectReason;

    }

}
