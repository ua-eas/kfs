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

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.service.PurapAccountingService;
import org.kuali.kfs.module.purap.service.impl.PurapGeneralLedgerServiceImpl;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SpringContext.class})
public class GenerateEntriesCreatePaymentRequestTest extends PurapGeneralLedgerTest {
    private PurapGeneralLedgerServiceImpl purapGeneralLedgerService;
    private PurchaseOrderService purchaseOrderService;
    private PurapAccountingService purapAccountingService;
    private BusinessObjectService businessObjectService;
    private AccountService accountService;

    private PaymentRequestDocument paymentRequestDocument;
    private PurchaseOrderDocument purchaseOrderDocument;

    @Before
    public void setUp() {
        purapGeneralLedgerService = new PurapGeneralLedgerServiceImpl();
        purchaseOrderService = EasyMock.createMock(PurchaseOrderService.class);
        businessObjectService = EasyMock.createMock(BusinessObjectService.class);
        purapAccountingService = EasyMock.createMock(PurapAccountingService.class);
        purapGeneralLedgerService.setPurchaseOrderService(purchaseOrderService);
        purapGeneralLedgerService.setBusinessObjectService(businessObjectService);
        purapGeneralLedgerService.setPurapAccountingService(purapAccountingService);

        paymentRequestDocument = EasyMock.createMock(PaymentRequestDocument.class);
        purchaseOrderDocument = EasyMock.createMock(PurchaseOrderDocument.class);

        PowerMock.mockStatic(SpringContext.class);
        accountService = EasyMock.createMock(AccountService.class);
    }

    @Test
    public void testNoItems() {
        EasyMock.expect(paymentRequestDocument.getPurchaseOrderIdentifier()).andReturn(1000);
        EasyMock.expect(purchaseOrderService.getCurrentPurchaseOrder(1000)).andReturn(purchaseOrderDocument);
        EasyMock.expect(paymentRequestDocument.getItems()).andReturn(new ArrayList<>());
        EasyMock.expect(businessObjectService.save(purchaseOrderDocument)).andReturn(purchaseOrderDocument);
        EasyMock.expect(purapAccountingService.generateSummaryAccountsWithNoZeroTotalsNoUseTax(paymentRequestDocument)).andReturn(new ArrayList<>());
        paymentRequestDocument.setGeneralLedgerPendingEntries(new ArrayList<>());
        EasyMock.expect(paymentRequestDocument.getDocumentNumber()).andReturn("1111");
        Map<String,Object> matcher = new HashMap<>();
        matcher.put("financialSystemOriginationCode","01");
        matcher.put("documentNumber","1111");
        EasyMock.expect(businessObjectService.countMatching(GeneralLedgerPendingEntry.class,matcher)).andReturn(0);
        paymentRequestDocument.setDebitCreditCodeForGLEntries("C");
        paymentRequestDocument.setGenerateEncumbranceEntries(true);
        EasyMock.expect(paymentRequestDocument.getGeneralLedgerPendingEntries()).andReturn(new ArrayList<>());
        EasyMock.expect(businessObjectService.save(new ArrayList<>())).andReturn(new ArrayList<>());
        replayAll();

        purapGeneralLedgerService.generateEntriesCreatePaymentRequest(paymentRequestDocument);

        verifyAll();
    }

    @Test
    public void testItemsNoGLPEsNoAccountingLines() {
        EasyMock.expect(paymentRequestDocument.getPurchaseOrderIdentifier()).andReturn(1000);
        EasyMock.expect(purchaseOrderService.getCurrentPurchaseOrder(1000)).andReturn(purchaseOrderDocument);

        List<PurApItem> preqItems = new ArrayList<>();
        preqItems.add(getPaymentRequestItem(paymentRequestDocument,100,null,null,ITEM));
        preqItems.add(getPaymentRequestItem(paymentRequestDocument,1,null,null,ITEM));
        preqItems.add(getPaymentRequestItem(paymentRequestDocument,2,null,null,ITEM));
        preqItems.add(getPaymentRequestItem(paymentRequestDocument,3,KDONE,BigDecimal.ZERO,ITEM));
        preqItems.add(getPaymentRequestItem(paymentRequestDocument,4,KDTWO,BigDecimal.ZERO,ITEM));
        preqItems.add(getPaymentRequestItem(paymentRequestDocument,5,KDONE,BigDecimal.ZERO,ITEM));
        preqItems.add(getPaymentRequestItem(paymentRequestDocument,6,KDONE,BigDecimal.ZERO,ITEM));
        preqItems.add(getPaymentRequestItem(paymentRequestDocument,7,KDONE,new BigDecimal("2.00"),ITEM));
        EasyMock.expect(paymentRequestDocument.getItems()).andReturn(preqItems);

        List<PurApItem> poItems = new ArrayList<>();
        poItems.add(getPurchaseOrderItem(purchaseOrderDocument,1,null,null,null,null,null,null,ITEM));
        poItems.add(getPurchaseOrderItem(purchaseOrderDocument,2,KDONE,BigDecimal.ZERO,null,null,null,null,ITEM));
        poItems.add(getPurchaseOrderItem(purchaseOrderDocument,3,KDONE,BigDecimal.ZERO,null,null,null,null,ITEM));
        poItems.add(getPurchaseOrderItem(purchaseOrderDocument,4,KDONE,BigDecimal.ZERO,KDONE,null,null,null,ITEM));
        poItems.add(getPurchaseOrderItem(purchaseOrderDocument,5,KDTWO,BigDecimal.ZERO,KDONE,null,null,null,ITEM));
        poItems.add(getPurchaseOrderItem(purchaseOrderDocument,6,KDTWO,BigDecimal.ZERO,KDONE,KDONE,null,null,ITEM));
        poItems.add(getPurchaseOrderItem(purchaseOrderDocument,7,KDTWO,BigDecimal.ONE,KDONE,KDONE,new KualiDecimal("0.50"),new KualiDecimal("0.75"),ITEM));

        EasyMock.expect(purchaseOrderDocument.getItems()).andReturn(poItems).times(8);

        EasyMock.expect(paymentRequestDocument.isUseTaxIndicator()).andReturn(false).times(7);
        EasyMock.expect(businessObjectService.save(purchaseOrderDocument)).andReturn(purchaseOrderDocument);
        EasyMock.expect(purapAccountingService.generateSummaryAccountsWithNoZeroTotalsNoUseTax(paymentRequestDocument)).andReturn(new ArrayList<>());
        paymentRequestDocument.setGeneralLedgerPendingEntries(new ArrayList<>());
        EasyMock.expect(paymentRequestDocument.getDocumentNumber()).andReturn("1111");
        Map<String,Object> matcher = new HashMap<>();
        matcher.put("financialSystemOriginationCode","01");
        matcher.put("documentNumber","1111");
        EasyMock.expect(businessObjectService.countMatching(GeneralLedgerPendingEntry.class,matcher)).andReturn(0);
        paymentRequestDocument.setDebitCreditCodeForGLEntries("C");
        paymentRequestDocument.setGenerateEncumbranceEntries(true);
        EasyMock.expect(paymentRequestDocument.getGeneralLedgerPendingEntries()).andReturn(new ArrayList<>());
        EasyMock.expect(businessObjectService.save(new ArrayList<>())).andReturn(new ArrayList<>());
        replayAll();

        purapGeneralLedgerService.generateEntriesCreatePaymentRequest(paymentRequestDocument);

        verifyAll();
    }

    @Test
    public void testItemsNoGLPEsNoAccountingLinesMisc() {
        EasyMock.expect(paymentRequestDocument.getPurchaseOrderIdentifier()).andReturn(1000);
        EasyMock.expect(purchaseOrderService.getCurrentPurchaseOrder(1000)).andReturn(purchaseOrderDocument);

        List<PurApItem> preqItems = new ArrayList<>();
        preqItems.add(getPaymentRequestItem(paymentRequestDocument,100,null,null,MISC));
        preqItems.add(getPaymentRequestItem(paymentRequestDocument,1,null,null,MISC));
        preqItems.add(getPaymentRequestItem(paymentRequestDocument,2,null,null,MISC));
        preqItems.add(getPaymentRequestItem(paymentRequestDocument,3,KDONE,BigDecimal.ZERO,MISC));
        preqItems.add(getPaymentRequestItem(paymentRequestDocument,4,KDTWO,BigDecimal.ZERO,MISC));
        preqItems.add(getPaymentRequestItem(paymentRequestDocument,5,KDONE,BigDecimal.ZERO,MISC));
        preqItems.add(getPaymentRequestItem(paymentRequestDocument,6,KDONE,BigDecimal.ZERO,MISC));
        preqItems.add(getPaymentRequestItem(paymentRequestDocument,7,KDONE,new BigDecimal("2.00"),MISC));
        EasyMock.expect(paymentRequestDocument.getItems()).andReturn(preqItems);

        List<PurApItem> poItems = new ArrayList<>();
        poItems.add(getPurchaseOrderItem(purchaseOrderDocument,1,null,null,null,null,KualiDecimal.ZERO,KualiDecimal.ZERO,ITEM));
        poItems.add(getPurchaseOrderItem(purchaseOrderDocument,2,KDONE,BigDecimal.ZERO,null,null,KualiDecimal.ZERO,KualiDecimal.ZERO,MISC));
        poItems.add(getPurchaseOrderItem(purchaseOrderDocument,3,KDONE,BigDecimal.ZERO,null,null,KualiDecimal.ZERO,KualiDecimal.ZERO,MISC));
        poItems.add(getPurchaseOrderItem(purchaseOrderDocument,4,KDONE,BigDecimal.ZERO,KDONE,null,KualiDecimal.ZERO,KualiDecimal.ZERO,MISC));
        poItems.add(getPurchaseOrderItem(purchaseOrderDocument,5,KDTWO,BigDecimal.ZERO,KDONE,null,KualiDecimal.ZERO,KualiDecimal.ZERO,MISC));
        poItems.add(getPurchaseOrderItem(purchaseOrderDocument,6,KDTWO,BigDecimal.ZERO,KDONE,KDONE,KualiDecimal.ZERO,KualiDecimal.ZERO,MISC));
        poItems.add(getPurchaseOrderItem(purchaseOrderDocument,7,KDTWO,BigDecimal.ONE,KDONE,KDONE,new KualiDecimal("0.50"),new KualiDecimal("0.75"),MISC));

        EasyMock.expect(purchaseOrderDocument.getItems()).andReturn(poItems).times(8);

        EasyMock.expect(paymentRequestDocument.isUseTaxIndicator()).andReturn(false).times(8);
        EasyMock.expect(businessObjectService.save(purchaseOrderDocument)).andReturn(purchaseOrderDocument);
        EasyMock.expect(purapAccountingService.generateSummaryAccountsWithNoZeroTotalsNoUseTax(paymentRequestDocument)).andReturn(new ArrayList<>());
        paymentRequestDocument.setGeneralLedgerPendingEntries(new ArrayList<>());
        EasyMock.expect(paymentRequestDocument.getDocumentNumber()).andReturn("1111");
        Map<String,Object> matcher = new HashMap<>();
        matcher.put("financialSystemOriginationCode","01");
        matcher.put("documentNumber","1111");
        EasyMock.expect(businessObjectService.countMatching(GeneralLedgerPendingEntry.class,matcher)).andReturn(0);
        paymentRequestDocument.setDebitCreditCodeForGLEntries("C");
        paymentRequestDocument.setGenerateEncumbranceEntries(true);
        EasyMock.expect(paymentRequestDocument.getGeneralLedgerPendingEntries()).andReturn(new ArrayList<>());
        EasyMock.expect(businessObjectService.save(new ArrayList<>())).andReturn(new ArrayList<>());
        replayAll();

        purapGeneralLedgerService.generateEntriesCreatePaymentRequest(paymentRequestDocument);

        verifyAll();
    }

    @Test
    public void testItemsNoGLPEsAccountingLines() {
        EasyMock.expect(paymentRequestDocument.getPurchaseOrderIdentifier()).andReturn(1000);
        EasyMock.expect(purchaseOrderService.getCurrentPurchaseOrder(1000)).andReturn(purchaseOrderDocument);

        List<PurApItem> preqItems = new ArrayList<>();
        preqItems.add(getPaymentRequestItem(paymentRequestDocument,1,KDONE,new BigDecimal("2.00"),MISC));
        EasyMock.expect(paymentRequestDocument.getItems()).andReturn(preqItems);

        List<PurApItem> poItems = new ArrayList<>();
        PurchaseOrderItem poItem = (PurchaseOrderItem)getPurchaseOrderItem(purchaseOrderDocument,1,KDTWO,BigDecimal.ONE,KDONE,KDONE,new KualiDecimal("0.50"),new KualiDecimal("0.75"),MISC);
        List<PurApAccountingLine> als = new ArrayList<>();
        als.add(getPurchaseOrderAccount(new KualiDecimal("10.00"),new KualiDecimal("5.00")));
        poItem.setSourceAccountingLines(als);
        poItems.add(poItem);

        EasyMock.expect(SpringContext.getBean(AccountService.class)).andReturn(accountService);
        accountService.populateAccountingLineChartIfNeeded(EasyMock.anyObject());

        EasyMock.expect(purchaseOrderDocument.getItems()).andReturn(poItems);

        EasyMock.expect(paymentRequestDocument.isUseTaxIndicator()).andReturn(false);
        EasyMock.expect(businessObjectService.save(purchaseOrderDocument)).andReturn(purchaseOrderDocument);
        EasyMock.expect(purapAccountingService.generateSummaryAccountsWithNoZeroTotalsNoUseTax(paymentRequestDocument)).andReturn(new ArrayList<>());
        paymentRequestDocument.setGeneralLedgerPendingEntries(new ArrayList<>());
        EasyMock.expect(paymentRequestDocument.getDocumentNumber()).andReturn("1111");
        Map<String,Object> matcher = new HashMap<>();
        matcher.put("financialSystemOriginationCode","01");
        matcher.put("documentNumber","1111");
        EasyMock.expect(businessObjectService.countMatching(GeneralLedgerPendingEntry.class,matcher)).andReturn(0);
        paymentRequestDocument.setDebitCreditCodeForGLEntries("C");
        paymentRequestDocument.setGenerateEncumbranceEntries(true);
        EasyMock.expect(paymentRequestDocument.generateGeneralLedgerPendingEntries(EasyMock.anyObject(),EasyMock.anyObject())).andReturn(true);
        EasyMock.expect(paymentRequestDocument.getGeneralLedgerPendingEntries()).andReturn(new ArrayList<>());
        EasyMock.expect(businessObjectService.save(new ArrayList<>())).andReturn(new ArrayList<>());
        replayAll();

        purapGeneralLedgerService.generateEntriesCreatePaymentRequest(paymentRequestDocument);

        verifyAll();
    }

    private void replayAll() {
        EasyMock.replay(paymentRequestDocument,purchaseOrderService,purchaseOrderDocument,businessObjectService,purapAccountingService,accountService);
        PowerMock.replay(SpringContext.class);
    }

    private void verifyAll() {
        EasyMock.verify(paymentRequestDocument,purchaseOrderService,purchaseOrderDocument,businessObjectService,purapAccountingService,accountService);
        PowerMock.verify(SpringContext.class);
    }

}
