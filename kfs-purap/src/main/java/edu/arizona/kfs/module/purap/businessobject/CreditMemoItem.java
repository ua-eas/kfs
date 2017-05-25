package edu.arizona.kfs.module.purap.businessobject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;

import org.kuali.kfs.module.purap.businessobject.CreditMemoAccount;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestAccount;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.module.purap.document.service.AccountsPayableService;
import org.kuali.kfs.module.purap.util.ExpiredOrClosedAccountEntry;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;

public class CreditMemoItem extends org.kuali.kfs.module.purap.businessobject.CreditMemoItem {
    
    public CreditMemoItem() {
    }
    
    public CreditMemoItem(VendorCreditMemoDocument cmDocument, PaymentRequestItem preqItem, PurchaseOrderItem poItem, HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList) {
        super();

        setPurapDocumentIdentifier(cmDocument.getPurapDocumentIdentifier());
        setItemLineNumber(preqItem.getItemLineNumber());
        this.setPurapDocument(cmDocument);

        // take invoiced quantities from the lower of the preq and po if different
        if (poItem != null && preqItem != null) {
            if (poItem.getItemInvoicedTotalQuantity() != null && preqItem.getItemQuantity() != null) {
                if (poItem.getItemInvoicedTotalQuantity().isLessThan(preqItem.getItemQuantity())) {
                    setPreqInvoicedTotalQuantity(poItem.getItemInvoicedTotalQuantity());
                    setPreqTotalAmount(poItem.getItemInvoicedTotalAmount());
                }
            }
        } else {
            setPreqInvoicedTotalQuantity(preqItem.getItemQuantity());
            setPreqTotalAmount(preqItem.getTotalAmount());
        }

        setPreqUnitPrice(preqItem.getItemUnitPrice());
        setItemTypeCode(preqItem.getItemTypeCode());

        if ((ObjectUtils.isNotNull(this.getItemType()) && this.getItemType().isAmountBasedGeneralLedgerIndicator())) {
            // setting unit price to be null to be more consistent with other below the line
            this.setItemUnitPrice(null);
        } else {
            setItemUnitPrice(preqItem.getItemUnitPrice());
        }

        setItemCatalogNumber(preqItem.getItemCatalogNumber());
        setItemDescription(preqItem.getItemDescription());

        setCapitalAssetTransactionTypeCode(preqItem.getCapitalAssetTransactionTypeCode());

        if (getPreqInvoicedTotalQuantity() == null) {
            setPreqInvoicedTotalQuantity(KualiDecimal.ZERO);
        }
        if (getPreqUnitPrice() == null) {
            setPreqUnitPrice(BigDecimal.ZERO);
        }
        if (getPreqTotalAmount() == null) {
            setPreqTotalAmount(KualiDecimal.ZERO);
        }

        for (Iterator iter = preqItem.getSourceAccountingLines().iterator(); iter.hasNext();) {
            PaymentRequestAccount account = (PaymentRequestAccount) iter.next();

            // check if this account is expired/closed and replace as needed
            SpringContext.getBean(AccountsPayableService.class).processExpiredOrClosedAccount(account, expiredOrClosedAccountList);

            getSourceAccountingLines().add(new CreditMemoAccount(account));
        }
    }
}
