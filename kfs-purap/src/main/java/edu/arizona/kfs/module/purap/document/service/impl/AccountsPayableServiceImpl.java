package edu.arizona.kfs.module.purap.document.service.impl;

import java.util.List;

import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.AccountsPayableDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.module.purap.document.service.AccountsPayableService;
import org.kuali.rice.krad.util.ObjectUtils;

import edu.arizona.kfs.module.purap.businessobject.CreditMemoItem;
import edu.arizona.kfs.module.purap.businessobject.PaymentRequestItem;
import edu.arizona.kfs.module.purap.document.PaymentRequestDocument;

public class AccountsPayableServiceImpl extends org.kuali.kfs.module.purap.document.service.impl.AccountsPayableServiceImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountsPayableService.class);

    @Override
    public void updateItemList(AccountsPayableDocument apDocument) {
        // don't run the following if past full entry
        if (purapService.isFullDocumentEntryCompleted(apDocument)) {
            return;
        }

        if (apDocument instanceof VendorCreditMemoDocument) {
            VendorCreditMemoDocument cm = (VendorCreditMemoDocument) apDocument;
            updateVendorCreditMemoDocumentItemList(cm);
            return;
        }

        if (apDocument instanceof PaymentRequestDocument) {
            PaymentRequestDocument preq = (PaymentRequestDocument) apDocument;
            updatePaymentRequestDocumentItemList(preq);
            return;
        }
    }

    private void updateVendorCreditMemoDocumentItemList(VendorCreditMemoDocument cm) {
        if (cm.isSourceDocumentPaymentRequest()) {
            updateVendorCreditMemoDocumentItemListFromPREQ(cm);
        }
        if (cm.isSourceDocumentPurchaseOrder()) {
            updateVendorCreditMemoDocumentItemListFromPO(cm);
        }
    }

    private void updateVendorCreditMemoDocumentItemListFromPREQ(VendorCreditMemoDocument cm) {

        @SuppressWarnings("unchecked")
        List<PaymentRequestItem> items = cm.getPaymentRequestDocument().getItems();
        for (PaymentRequestItem preqItem : items) {
            // skip inactive and below the line
            if (preqItem.getItemType().isAdditionalChargeIndicator()) {
                LOG.info("Item type is below the line.");
                continue;
            }

            PurchaseOrderItem poItem = preqItem.getPurchaseOrderItem();
            if (poItem == null) {
                LOG.info("Purchase Order Item is null, cannot process further on line item " + preqItem.getItemLineNumber() + ":" + preqItem.toString());
                continue;
            }

            CreditMemoItem cmItem = (CreditMemoItem) cm.getAPItemFromPOItem(poItem);
            if (cmItem == null) {
                LOG.info("Credit Memo Item is null, cannot process further on line item " + poItem.getItemLineNumber() + ":" + poItem.toString());
                continue;
            }
            updateEncumberances(preqItem, poItem, cmItem);
        }
    }

    private void updateVendorCreditMemoDocumentItemListFromPO(VendorCreditMemoDocument cm) {
        PurchaseOrderDocument po = purchaseOrderService.getCurrentPurchaseOrder(cm.getPurchaseOrderIdentifier());
        @SuppressWarnings("unchecked")
        List<PurchaseOrderItem> poItems = po.getItems();
        @SuppressWarnings("unchecked")
        List<CreditMemoItem> cmItems = cm.getItems();
        // iterate through the above the line poItems to find matching
        for (PurchaseOrderItem purchaseOrderItem : poItems) {
            // skip inactive and below the line
            if (purchaseOrderItem.getItemType().isAdditionalChargeIndicator()) {
                continue;
            }

            CreditMemoItem cmItem = (CreditMemoItem) cm.getAPItemFromPOItem(purchaseOrderItem);
            // check if any action needs to be taken on the items (i.e. add for new eligible items or remove for ineligible)
            if (cm.getDocumentSpecificService().poItemEligibleForAp(cm, purchaseOrderItem)) {
                // if eligible and not there - add
                if (ObjectUtils.isNull(cmItem)) {
                    CreditMemoItem cmi = new CreditMemoItem(cm, purchaseOrderItem);
                    cmi.setPurapDocument(cm);
                    cmItems.add(cmi);
                } else {
                    // is eligible and on doc, update encumberances
                    // (this is only qty and amount for now NOTE we should also update other key fields, like description
                    // etc in case ammendment modified a line
                    updateEncumberance(purchaseOrderItem, cmItem);
                }
            } else { // if not eligible and there - remove
                if (ObjectUtils.isNotNull(cmItem)) {
                    cmItems.remove(cmItem);
                    // don't update encumberance
                    continue;
                }
            }

        }

    }

    private void updatePaymentRequestDocumentItemList(PaymentRequestDocument preq) {
        // get a fresh purchase order
        PurchaseOrderDocument po = purchaseOrderService.getCurrentPurchaseOrder(preq.getPurchaseOrderIdentifier());

        @SuppressWarnings("unchecked")
        List<PurchaseOrderItem> poItems = po.getItems();
        @SuppressWarnings("unchecked")
        List<PaymentRequestItem> preqItems = preq.getItems();
        // iterate through the above the line poItems to find matching
        for (PurchaseOrderItem purchaseOrderItem : poItems) {
            // skip below the line
            if (purchaseOrderItem.getItemType().isAdditionalChargeIndicator()) {
                continue;
            }
            PaymentRequestItem preqItem = (PaymentRequestItem) preq.getAPItemFromPOItem(purchaseOrderItem);
            // check if any action needs to be taken on the items (i.e. add for new eligible items or remove for ineligible)
            if (preq.getDocumentSpecificService().poItemEligibleForAp(preq, purchaseOrderItem)) {
                // if eligible and not there - add
                if (ObjectUtils.isNull(preqItem)) {
                    PaymentRequestItem pri = new PaymentRequestItem(purchaseOrderItem, preq);
                    pri.setPurapDocument(preq);
                    preqItems.add(pri);
                } else {
                    updatePossibleAmmendedFields(purchaseOrderItem, preqItem);
                }
            } else { // if not eligible and there - remove
                if (ObjectUtils.isNotNull(preqItem)) {
                    preqItems.remove(preqItem);
                }
            }
        }
    }

}
