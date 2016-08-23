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
package org.kuali.kfs.module.purap.batch.service.impl;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.batch.service.AutoClosePurchaseOrderService;
import org.kuali.kfs.module.purap.businessobject.AutoClosePurchaseOrderView;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@NonTransactional
public class AutoClosePurchaseOrderServiceImpl implements AutoClosePurchaseOrderService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AutoClosePurchaseOrderServiceImpl.class);
    protected PurchaseOrderService purchaseOrderService;


    /**
     * @see AutoClosePurchaseOrderService#autoCloseFullyDisencumberedOrders()
     */
    @Override
    @NonTransactional
    public boolean autoCloseFullyDisencumberedOrders() {
        LOG.debug("autoCloseFullyDisencumberedOrders() started");

        List<AutoClosePurchaseOrderView> autoCloseList = getPurchaseOrderService().getAllOpenPurchaseOrdersForAutoClose();

        for (AutoClosePurchaseOrderView poAutoClose : autoCloseList) {
            autoClosePurchaseOrder(poAutoClose);
        }

        LOG.debug("autoCloseFullyDisencumberedOrders() ended");

        return true;
    }

    @Transactional
    public void autoClosePurchaseOrder(AutoClosePurchaseOrderView poAutoClose) {
        if ((poAutoClose.getTotalAmount() != null) && ((KualiDecimal.ZERO.compareTo(poAutoClose.getTotalAmount())) != 0)) {
            LOG.info("autoCloseFullyDisencumberedOrders() PO ID " + poAutoClose.getPurapDocumentIdentifier() + " with total " + poAutoClose.getTotalAmount().doubleValue() + " will be closed");
            String newStatus = PurapConstants.PurchaseOrderStatuses.APPDOC_PENDING_CLOSE;
            String annotation = "This PO was automatically closed in batch.";
            String documentType = PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_CLOSE_DOCUMENT;
            PurchaseOrderDocument document = getPurchaseOrderService().getPurchaseOrderByDocumentNumber(poAutoClose.getDocumentNumber());
            getPurchaseOrderService().createNoteForAutoCloseOrders(document, annotation);
            getPurchaseOrderService().createAndRoutePotentialChangeDocument(poAutoClose.getDocumentNumber(), documentType, annotation, null, newStatus);

        }
    }

    public PurchaseOrderService getPurchaseOrderService() {
        return purchaseOrderService;
    }

    public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }
}
