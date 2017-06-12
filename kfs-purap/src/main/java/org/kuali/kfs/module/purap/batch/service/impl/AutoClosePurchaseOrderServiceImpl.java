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
package org.kuali.kfs.module.purap.batch.service.impl;


import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.batch.service.AutoClosePurchaseOrderService;
import org.kuali.kfs.module.purap.businessobject.AutoClosePurchaseOrderView;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.springframework.transaction.annotation.Transactional;


@NonTransactional
public class AutoClosePurchaseOrderServiceImpl implements AutoClosePurchaseOrderService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AutoClosePurchaseOrderServiceImpl.class);
    private static final String AUTO_CLOSE_NOTE = "This PO was automatically closed in batch.";

    protected PurchaseOrderService purchaseOrderService;


    /**
     * @see AutoClosePurchaseOrderService#autoCloseFullyDisencumberedOrders()
     */
    @Override
    @NonTransactional
    public boolean autoCloseFullyDisencumberedOrders() {
        LOG.debug("autoCloseFullyDisencumberedOrders() started");

        LOG.info("autoCloseFullyDisencumberedOrders(): Querying PO view for all auto-close eligible records...");
        List<AutoClosePurchaseOrderView> autoCloseList = getPurchaseOrderService().getAllOpenPurchaseOrdersForAutoClose();
        LOG.info("autoCloseFullyDisencumberedOrders(): Query returned with " + autoCloseList.size() + " records.");

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
            String documentType = PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_CLOSE_DOCUMENT;
            PurchaseOrderDocument document = getPurchaseOrderService().getPurchaseOrderByDocumentNumber(poAutoClose.getDocumentNumber());

            getPurchaseOrderService().createNoteForAutoCloseOrders(document, AUTO_CLOSE_NOTE);
            getPurchaseOrderService().createAndRoutePotentialChangeDocument(poAutoClose.getDocumentNumber(), documentType, AUTO_CLOSE_NOTE, null, newStatus);
        }
    }

    public PurchaseOrderService getPurchaseOrderService() {
        return purchaseOrderService;
    }

    public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }
}
