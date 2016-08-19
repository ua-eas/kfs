/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.kfs.module.purap.service.impl.purapgeneralledgerserviceimpl;

import org.kuali.kfs.module.purap.businessobject.ItemType;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderAccount;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.math.BigDecimal;

public class PurapGeneralLedgerTest {
    public static final String ITEM = "ITEM";
    public static final String MISC = "MISC";

    public static final KualiDecimal KDONE = new KualiDecimal("1.00");
    public static final KualiDecimal KDTWO = new KualiDecimal("2.00");

    protected PurchaseOrderAccount getPurchaseOrderAccount(KualiDecimal amount, KualiDecimal itemAccountOutstandingEncumbranceAmount) {
        PurchaseOrderAccount al = new PurchaseOrderAccount();
        al.setAmount(amount);
        al.setItemAccountOutstandingEncumbranceAmount(itemAccountOutstandingEncumbranceAmount);
        return al;
    }

    protected PurApItem getPurchaseOrderItem(PurchaseOrderDocument document, int lineNumber, KualiDecimal quantity, BigDecimal itemUnitPrice, KualiDecimal encumberedQuantity, KualiDecimal itemInvoicedTotalQuantity, KualiDecimal encumberedAmount, KualiDecimal itemInvoicedTotalAmount, String itemTypeCode) {
        PurchaseOrderItem item = new PurchaseOrderItem() {
            @Override
            public void refreshReferenceObject(String referenceObjectName) {
            }
        };
        item.setItemInvoicedTotalQuantity(itemInvoicedTotalQuantity);
        item.setItemInvoicedTotalAmount(itemInvoicedTotalAmount);
        item.setItemQuantity(quantity);
        item.setItemUnitPrice(itemUnitPrice);
        item.setItemOutstandingEncumberedQuantity(encumberedQuantity);
        item.setItemOutstandingEncumberedAmount(encumberedAmount);
        item.setItemLineNumber(lineNumber);
        item.setPurapDocument(document);
        ItemType it = getItemType(itemTypeCode);
        item.setItemTypeCode(it.getItemTypeCode());
        //noinspection deprecation
        item.setItemType(it);
        return item;
    }

    protected PurApItem getPaymentRequestItem(PaymentRequestDocument document, int lineNumber, KualiDecimal quantity, BigDecimal price, String itemTypeCode) {
        PurApItem item = new PaymentRequestItem() {
            @Override
            public void refreshReferenceObject(String referenceObjectName) {
            }
        };
        item.setItemQuantity(quantity);
        item.setItemUnitPrice(price);
        item.setItemLineNumber(lineNumber);
        item.setPurapDocument(document);
        ItemType it = getItemType(itemTypeCode);
        item.setItemTypeCode(it.getItemTypeCode());
        //noinspection deprecation
        item.setItemType(it);
        return item;
    }

    protected ItemType getItemType(String code) {
        ItemType it = new ItemType();
        if ( ITEM.equals(code) ) {
            it.setItemTypeCode(ITEM);
            it.setAdditionalChargeIndicator(false);
            it.setQuantityBasedGeneralLedgerIndicator(true);
            it.setTaxableIndicator(true);
            it.setActive(true);
        } else {
            it.setItemTypeCode(MISC);
            it.setAdditionalChargeIndicator(true);
            it.setQuantityBasedGeneralLedgerIndicator(false);
            it.setTaxableIndicator(true);
            it.setActive(true);
        }
        return it;
    }
}
