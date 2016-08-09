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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.module.purap.businessobject.ItemType;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderAccount;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderCloseDocument;
import org.kuali.kfs.module.purap.service.PurapAccountingService;
import org.kuali.kfs.module.purap.service.impl.PurapGeneralLedgerServiceImpl;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class GenerateEntriesClosePurchaseOrderTest {
    private PurapGeneralLedgerServiceImpl purapGeneralLedgerService;
    private PurapAccountingService purapAccountingService;
    private GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    private BusinessObjectService businessObjectService;
    private PurchaseOrderCloseDocument poClose;

    @Before
    public void setUp() {
        purapGeneralLedgerService = new PurapGeneralLedgerServiceImpl();

        purapAccountingService = EasyMock.createMock(PurapAccountingService.class);
        purapGeneralLedgerService.setPurapAccountingService(purapAccountingService);
        generalLedgerPendingEntryService = EasyMock.createMock(GeneralLedgerPendingEntryService.class);
        purapGeneralLedgerService.setGeneralLedgerPendingEntryService(generalLedgerPendingEntryService);
        businessObjectService = EasyMock.createMock(BusinessObjectService.class);
        purapGeneralLedgerService.setBusinessObjectService(businessObjectService);

        poClose = EasyMock.createMock(PurchaseOrderCloseDocument.class);
    }

    private void setUpBaseExpectations(List<PurchaseOrderItem> purchaseOrderItems, List<PurApItem> activePurchaseOrderItems, List<SourceAccountingLine> accountingLines, List<GeneralLedgerPendingEntry> pendingEntries) {
        EasyMock.expect(poClose.getItems()).andReturn(purchaseOrderItems).times(2);
        EasyMock.expect(poClose.getItemsActiveOnly()).andReturn(activePurchaseOrderItems);
        EasyMock.expect(purapAccountingService.generateSummaryWithNoZeroTotalsUsingAlternateAmount(activePurchaseOrderItems)).andReturn(accountingLines);
        poClose.setGlOnlySourceAccountingLines(accountingLines);
        EasyMock.expectLastCall();
        EasyMock.expect(poClose.getGlOnlySourceAccountingLines()).andReturn(accountingLines);
        EasyMock.expect(generalLedgerPendingEntryService.generateGeneralLedgerPendingEntries(poClose)).andReturn(Boolean.TRUE);
        EasyMock.expect(businessObjectService.save(pendingEntries)).andReturn(null);
    }

    private void verifyAll(List<PurchaseOrderItem> purchaseOrderItems) {
        EasyMock.verify(purapAccountingService, poClose);
        for (PurchaseOrderItem purchaseOrderItem : purchaseOrderItems) {
            EasyMock.verify(purchaseOrderItem);
        }
    }

    private void replayAll(List<PurchaseOrderItem> purchaseOrderItems) {
        EasyMock.replay(purapAccountingService, poClose);
        for (PurchaseOrderItem purchaseOrderItem : purchaseOrderItems) {
            EasyMock.replay(purchaseOrderItem);
        }
    }

    protected PurchaseOrderItem buildBaseMockedPurchaseOrderItem() {
        PurchaseOrderItem purchaseOrderItem = EasyMock.partialMockBuilder(PurchaseOrderItem.class).addMockedMethod("getItemType").createMock();
        purchaseOrderItem.setItemLineNumber(1);
        purchaseOrderItem.setItemDescription("Stuff");
        purchaseOrderItem.setItemActiveIndicator(true);
        purchaseOrderItem.setDocumentNumber("1");
        purchaseOrderItem.setItemIdentifier(1);
        purchaseOrderItem.setSourceAccountingLines(new ArrayList<>());
        return purchaseOrderItem;
    }

    protected PurchaseOrderItem buildQuantityMockedPurchaseOrderItem(KualiDecimal itemQuantity, BigDecimal itemUnitPrice) {
        PurchaseOrderItem purchaseOrderItem = buildBaseMockedPurchaseOrderItem();
        purchaseOrderItem.setItemQuantity(itemQuantity);
        purchaseOrderItem.setItemUnitPrice(itemUnitPrice);
        ItemType itemType = new ItemType();
        itemType.setQuantityBasedGeneralLedgerIndicator(true);
        EasyMock.expect(purchaseOrderItem.getItemType()).andReturn(itemType);
        return purchaseOrderItem;
    }

    protected PurchaseOrderItem buildNonQuantityMockedPurchaseOrderItem(KualiDecimal invoicedAmount) {
        PurchaseOrderItem purchaseOrderItem = buildBaseMockedPurchaseOrderItem();

        purchaseOrderItem.setItemInvoicedTotalAmount(invoicedAmount);

        ItemType itemType = new ItemType();
        itemType.setQuantityBasedGeneralLedgerIndicator(false);
        EasyMock.expect(purchaseOrderItem.getItemType()).andReturn(itemType);
        return purchaseOrderItem;
    }

    private PurchaseOrderAccount buildMockAccountingLine(BigDecimal accountingLinePercent, KualiDecimal amount) {
        PurchaseOrderAccount accountingLine = new PurchaseOrderAccount();
        accountingLine.setAccountLinePercent(accountingLinePercent);
        accountingLine.setAmount(amount);

        return accountingLine;
    }

    @Test
    public void testPurchaseOrderCloseWithNoItems() {
        List<PurchaseOrderItem> purchaseOrderItems = new ArrayList<>();
        List<PurApItem> activePurchaseOrderItems = new ArrayList<>();
        List<SourceAccountingLine> accountingLines = new ArrayList<>();
        List<GeneralLedgerPendingEntry> pendingEntries = new ArrayList<>();

        setUpBaseExpectations(purchaseOrderItems, activePurchaseOrderItems, accountingLines, pendingEntries);

        replayAll(purchaseOrderItems);

        purapGeneralLedgerService.generateEntriesClosePurchaseOrder(poClose);

        verifyAll(purchaseOrderItems);

        Assert.assertEquals("Purchase Order Close should have no general ledger pending entries", 0, pendingEntries.size());
    }

    @Test
    public void testPurchaseOrderCloseWithOneQuantityItemOneAccountingLine() {
        PurchaseOrderItem purchaseOrderItem = buildQuantityMockedPurchaseOrderItem(new KualiDecimal(20), new BigDecimal(5.0));

        PurchaseOrderAccount accountingLine = buildMockAccountingLine(new BigDecimal(100.0), new KualiDecimal(100.0));

        List<PurchaseOrderItem> purchaseOrderItems = new ArrayList<>();
        List<PurApItem> activePurchaseOrderItems = new ArrayList<>();
        List<SourceAccountingLine> accountingLines = new ArrayList<>();
        List<GeneralLedgerPendingEntry> pendingEntries = new ArrayList<>();

        purchaseOrderItem.setItemOutstandingEncumberedQuantity(new KualiDecimal(1.0));
        purchaseOrderItem.setItemOutstandingEncumberedAmount(new KualiDecimal(100.0));

        purchaseOrderItem.getSourceAccountingLines().add(accountingLine);
        purchaseOrderItems.add(purchaseOrderItem);
        activePurchaseOrderItems.add(purchaseOrderItem);
        accountingLines.add(accountingLine);

        setUpBaseExpectations(purchaseOrderItems, activePurchaseOrderItems, accountingLines, pendingEntries);

        EasyMock.expect(poClose.getGeneralLedgerPendingEntries()).andReturn(pendingEntries);

        replayAll(purchaseOrderItems);
        EasyMock.replay(generalLedgerPendingEntryService);
        EasyMock.replay(businessObjectService);

        purapGeneralLedgerService.generateEntriesClosePurchaseOrder(poClose);

        verifyAll(purchaseOrderItems);

        Assert.assertEquals("Purchase Order Item should have outstanding encumbered quantity set to zero", KualiDecimal.ZERO, purchaseOrderItem.getItemOutstandingEncumberedQuantity());
        Assert.assertEquals("Purchase Order Item should have outstanding encumbered amount set to zero", KualiDecimal.ZERO, purchaseOrderItem.getItemOutstandingEncumberedAmount());
        Assert.assertEquals("GL Amount on accounting line should be 100.00", new KualiDecimal(100.0), accountingLine.getAlternateAmountForGLEntryCreation());
        Assert.assertEquals("Outstanding encumbrance amount on accounting line should be zero'd out", KualiDecimal.ZERO, accountingLine.getItemAccountOutstandingEncumbranceAmount());
    }

    @Test
    public void testPurchaseOrderCloseWithOneQuantityItemTwoAccountingLines() {
        PurchaseOrderItem purchaseOrderItem = buildQuantityMockedPurchaseOrderItem(new KualiDecimal(20), new BigDecimal(5));

        PurchaseOrderAccount accountingLine1 = buildMockAccountingLine(new BigDecimal(50.0), new KualiDecimal(50.0));
        PurchaseOrderAccount accountingLine2 = buildMockAccountingLine(new BigDecimal(50.0), new KualiDecimal(50.0));

        List<PurchaseOrderItem> purchaseOrderItems = new ArrayList<>();
        List<PurApItem> activePurchaseOrderItems = new ArrayList<>();
        List<SourceAccountingLine> accountingLines = new ArrayList<>();
        List<GeneralLedgerPendingEntry> pendingEntries = new ArrayList<>();

        purchaseOrderItem.setItemOutstandingEncumberedQuantity(new KualiDecimal(1.0));
        purchaseOrderItem.setItemOutstandingEncumberedAmount(new KualiDecimal(100.0));

        purchaseOrderItem.getSourceAccountingLines().add(accountingLine1);
        purchaseOrderItem.getSourceAccountingLines().add(accountingLine2);
        purchaseOrderItems.add(purchaseOrderItem);
        activePurchaseOrderItems.add(purchaseOrderItem);
        accountingLines.add(accountingLine1);
        accountingLines.add(accountingLine2);

        setUpBaseExpectations(purchaseOrderItems, activePurchaseOrderItems, accountingLines, pendingEntries);

        EasyMock.expect(poClose.getGeneralLedgerPendingEntries()).andReturn(pendingEntries);

        replayAll(purchaseOrderItems);
        EasyMock.replay(generalLedgerPendingEntryService);
        EasyMock.replay(businessObjectService);

        purapGeneralLedgerService.generateEntriesClosePurchaseOrder(poClose);

        verifyAll(purchaseOrderItems);

        Assert.assertEquals("Purchase Order Item should have outstanding encumbered quantity set to zero", KualiDecimal.ZERO, purchaseOrderItem.getItemOutstandingEncumberedQuantity());
        Assert.assertEquals("Purchase Order Item should have outstanding encumbered amount set to zero", KualiDecimal.ZERO, purchaseOrderItem.getItemOutstandingEncumberedAmount());
        Assert.assertEquals("GL Amount on accounting line should be 50", new KualiDecimal(50), accountingLine1.getAlternateAmountForGLEntryCreation());
        Assert.assertEquals("Outstanding encumbrance amount on accounting line should be zero'd out", KualiDecimal.ZERO, accountingLine1.getItemAccountOutstandingEncumbranceAmount());
        Assert.assertEquals("GL Amount on accounting line should be 50", new KualiDecimal(50), accountingLine2.getAlternateAmountForGLEntryCreation());
        Assert.assertEquals("Outstanding encumbrance amount on accounting line should be zero'd out", KualiDecimal.ZERO, accountingLine2.getItemAccountOutstandingEncumbranceAmount());
    }

    @Test
    public void testPurchaseOrderCloseWithTwoItems() {
        PurchaseOrderItem purchaseOrderItem1 = buildQuantityMockedPurchaseOrderItem(new KualiDecimal(20), new BigDecimal(5.0));

        PurchaseOrderAccount accountingLine1 = buildMockAccountingLine(new BigDecimal(100.0), new KualiDecimal(100.0));

        List<PurchaseOrderItem> purchaseOrderItems = new ArrayList<>();
        List<PurApItem> activePurchaseOrderItems = new ArrayList<>();
        List<SourceAccountingLine> accountingLines = new ArrayList<>();
        List<GeneralLedgerPendingEntry> pendingEntries = new ArrayList<>();

        purchaseOrderItem1.setItemOutstandingEncumberedQuantity(new KualiDecimal(1.0));
        purchaseOrderItem1.setItemOutstandingEncumberedAmount(new KualiDecimal(100.0));

        purchaseOrderItem1.getSourceAccountingLines().add(accountingLine1);
        purchaseOrderItems.add(purchaseOrderItem1);
        activePurchaseOrderItems.add(purchaseOrderItem1);
        accountingLines.add(accountingLine1);

        PurchaseOrderItem purchaseOrderItem2 = buildQuantityMockedPurchaseOrderItem(new KualiDecimal(5), new BigDecimal(5));
        PurchaseOrderAccount accountingLine2 = buildMockAccountingLine(new BigDecimal(100), new KualiDecimal(25));

        purchaseOrderItem2.setItemOutstandingEncumberedQuantity(new KualiDecimal(1.0));
        purchaseOrderItem2.setItemOutstandingEncumberedAmount(new KualiDecimal(100.0));

        purchaseOrderItem2.getSourceAccountingLines().add(accountingLine2);
        purchaseOrderItems.add(purchaseOrderItem2);
        activePurchaseOrderItems.add(purchaseOrderItem2);
        accountingLines.add(accountingLine2);

        setUpBaseExpectations(purchaseOrderItems, activePurchaseOrderItems, accountingLines, pendingEntries);

        EasyMock.expect(poClose.getGeneralLedgerPendingEntries()).andReturn(pendingEntries);

        replayAll(purchaseOrderItems);
        EasyMock.replay(generalLedgerPendingEntryService);
        EasyMock.replay(businessObjectService);

        purapGeneralLedgerService.generateEntriesClosePurchaseOrder(poClose);

        verifyAll(purchaseOrderItems);

        Assert.assertEquals("Purchase Order Item 1 should have outstanding encumbered quantity set to zero", KualiDecimal.ZERO, purchaseOrderItem1.getItemOutstandingEncumberedQuantity());
        Assert.assertEquals("Purchase Order Item 1 should have outstanding encumbered amount set to zero", KualiDecimal.ZERO, purchaseOrderItem1.getItemOutstandingEncumberedAmount());
        Assert.assertEquals("GL Amount on accounting line 1 should be 100.00", new KualiDecimal(100.0), accountingLine1.getAlternateAmountForGLEntryCreation());
        Assert.assertEquals("Outstanding encumbrance amount on accounting line 1 should be zero'd out", KualiDecimal.ZERO, accountingLine1.getItemAccountOutstandingEncumbranceAmount());

        Assert.assertEquals("Purchase Order Item 2 should have outstanding encumbered quantity set to zero", KualiDecimal.ZERO, purchaseOrderItem2.getItemOutstandingEncumberedQuantity());
        Assert.assertEquals("Purchase Order Item 2 should have outstanding encumbered amount set to zero", KualiDecimal.ZERO, purchaseOrderItem2.getItemOutstandingEncumberedAmount());
        Assert.assertEquals("GL Amount on accounting line 2 should be 25.00", new KualiDecimal(100.0), accountingLine2.getAlternateAmountForGLEntryCreation());
        Assert.assertEquals("Outstanding encumbrance amount on accounting line 2 should be zero'd out", KualiDecimal.ZERO, accountingLine2.getItemAccountOutstandingEncumbranceAmount());
    }

    @Test
    public void testPurchaseOrderCloseWithOneActiveOneInactiveItem() {
        PurchaseOrderItem purchaseOrderItem1 = buildQuantityMockedPurchaseOrderItem(new KualiDecimal(20), new BigDecimal(5.0));

        PurchaseOrderAccount accountingLine1 = buildMockAccountingLine(new BigDecimal(100.0), new KualiDecimal(100.0));

        List<PurchaseOrderItem> purchaseOrderItems = new ArrayList<>();
        List<PurApItem> activePurchaseOrderItems = new ArrayList<>();
        List<SourceAccountingLine> accountingLines = new ArrayList<>();
        List<GeneralLedgerPendingEntry> pendingEntries = new ArrayList<>();

        purchaseOrderItem1.setItemOutstandingEncumberedQuantity(new KualiDecimal(1.0));
        purchaseOrderItem1.setItemOutstandingEncumberedAmount(new KualiDecimal(100.0));

        purchaseOrderItem1.getSourceAccountingLines().add(accountingLine1);
        purchaseOrderItems.add(purchaseOrderItem1);
        activePurchaseOrderItems.add(purchaseOrderItem1);
        accountingLines.add(accountingLine1);

        PurchaseOrderItem purchaseOrderItem2 = buildQuantityMockedPurchaseOrderItem(new KualiDecimal(5), new BigDecimal(5));
        PurchaseOrderAccount accountingLine2 = buildMockAccountingLine(new BigDecimal(100), new KualiDecimal(25));

        purchaseOrderItem2.setItemOutstandingEncumberedQuantity(new KualiDecimal(1.0));
        purchaseOrderItem2.setItemOutstandingEncumberedAmount(new KualiDecimal(100.0));

        purchaseOrderItem2.getSourceAccountingLines().add(accountingLine2);
        purchaseOrderItems.add(purchaseOrderItem2);
        activePurchaseOrderItems.add(purchaseOrderItem2);
        accountingLines.add(accountingLine2);

        purchaseOrderItem2.setItemActiveIndicator(false);

        setUpBaseExpectations(purchaseOrderItems, activePurchaseOrderItems, accountingLines, pendingEntries);

        EasyMock.expect(poClose.getGeneralLedgerPendingEntries()).andReturn(pendingEntries);

        replayAll(purchaseOrderItems);
        EasyMock.replay(generalLedgerPendingEntryService);
        EasyMock.replay(businessObjectService);

        purapGeneralLedgerService.generateEntriesClosePurchaseOrder(poClose);

        verifyAll(purchaseOrderItems);

        Assert.assertEquals("Purchase Order Item 1 should have outstanding encumbered quantity set to zero", KualiDecimal.ZERO, purchaseOrderItem1.getItemOutstandingEncumberedQuantity());
        Assert.assertEquals("Purchase Order Item 1 should have outstanding encumbered amount set to zero", KualiDecimal.ZERO, purchaseOrderItem1.getItemOutstandingEncumberedAmount());
        Assert.assertEquals("GL Amount on accounting line 1 should be 100.00", new KualiDecimal(100.0), accountingLine1.getAlternateAmountForGLEntryCreation());
        Assert.assertEquals("Outstanding encumbrance amount on accounting line 1 should be zero'd out", KualiDecimal.ZERO, accountingLine1.getItemAccountOutstandingEncumbranceAmount());

        Assert.assertEquals("Purchase Order Item 2 should have outstanding encumbered quantity set to zero", KualiDecimal.ZERO, purchaseOrderItem2.getItemOutstandingEncumberedQuantity());
        Assert.assertEquals("Purchase Order Item 2 should have outstanding encumbered amount set to zero", KualiDecimal.ZERO, purchaseOrderItem2.getItemOutstandingEncumberedAmount());
        Assert.assertEquals("GL Amount on accounting line 2 should be zero", KualiDecimal.ZERO, accountingLine2.getAlternateAmountForGLEntryCreation());
        Assert.assertEquals("Outstanding encumbrance amount on accounting line 2 should be zero'd out", KualiDecimal.ZERO, accountingLine2.getItemAccountOutstandingEncumbranceAmount());
    }

    @Test
    public void testPurchaseOrderCloseWithOneNonQuantityItemOneAccountingLine() {
        PurchaseOrderItem purchaseOrderItem = buildNonQuantityMockedPurchaseOrderItem(new KualiDecimal(105.00));

        PurchaseOrderAccount accountingLine = buildMockAccountingLine(new BigDecimal(100.0), new KualiDecimal(105.0));

        List<PurchaseOrderItem> purchaseOrderItems = new ArrayList<>();
        List<PurApItem> activePurchaseOrderItems = new ArrayList<>();
        List<SourceAccountingLine> accountingLines = new ArrayList<>();
        List<GeneralLedgerPendingEntry> pendingEntries = new ArrayList<>();

        purchaseOrderItem.setItemOutstandingEncumberedAmount(new KualiDecimal(105.0));

        purchaseOrderItem.getSourceAccountingLines().add(accountingLine);
        purchaseOrderItems.add(purchaseOrderItem);
        activePurchaseOrderItems.add(purchaseOrderItem);
        accountingLines.add(accountingLine);

        setUpBaseExpectations(purchaseOrderItems, activePurchaseOrderItems, accountingLines, pendingEntries);

        EasyMock.expect(poClose.getGeneralLedgerPendingEntries()).andReturn(pendingEntries);

        replayAll(purchaseOrderItems);
        EasyMock.replay(generalLedgerPendingEntryService);
        EasyMock.replay(businessObjectService);

        purapGeneralLedgerService.generateEntriesClosePurchaseOrder(poClose);

        verifyAll(purchaseOrderItems);

        Assert.assertNull("Purchase Order Item should have outstanding encumbered quantity set to null", purchaseOrderItem.getItemOutstandingEncumberedQuantity());
        Assert.assertEquals("Purchase Order Item should have outstanding encumbered amount set to zero", KualiDecimal.ZERO, purchaseOrderItem.getItemOutstandingEncumberedAmount());
        Assert.assertEquals("GL Amount on accounting line should be 105.00", new KualiDecimal(105.0), accountingLine.getAlternateAmountForGLEntryCreation());
        Assert.assertEquals("Outstanding encumbrance amount on accounting line should be zero'd out", KualiDecimal.ZERO, accountingLine.getItemAccountOutstandingEncumbranceAmount());
    }

    @Test
    public void testPurchaseOrderCloseWithOneNonQuantityItemTwoAccountingLines() {
        PurchaseOrderItem purchaseOrderItem = buildNonQuantityMockedPurchaseOrderItem(new KualiDecimal(200.07));

        PurchaseOrderAccount accountingLine1 = buildMockAccountingLine(new BigDecimal(50.0), new KualiDecimal(100.03));
        PurchaseOrderAccount accountingLine2 = buildMockAccountingLine(new BigDecimal(50.0), new KualiDecimal(100.03));

        List<PurchaseOrderItem> purchaseOrderItems = new ArrayList<>();
        List<PurApItem> activePurchaseOrderItems = new ArrayList<>();
        List<SourceAccountingLine> accountingLines = new ArrayList<>();
        List<GeneralLedgerPendingEntry> pendingEntries = new ArrayList<>();

        purchaseOrderItem.setItemOutstandingEncumberedAmount(new KualiDecimal(200.07));

        purchaseOrderItem.getSourceAccountingLines().add(accountingLine1);
        purchaseOrderItem.getSourceAccountingLines().add(accountingLine2);
        purchaseOrderItems.add(purchaseOrderItem);
        activePurchaseOrderItems.add(purchaseOrderItem);
        accountingLines.add(accountingLine1);
        accountingLines.add(accountingLine2);

        setUpBaseExpectations(purchaseOrderItems, activePurchaseOrderItems, accountingLines, pendingEntries);

        EasyMock.expect(poClose.getGeneralLedgerPendingEntries()).andReturn(pendingEntries);

        replayAll(purchaseOrderItems);
        EasyMock.replay(generalLedgerPendingEntryService);
        EasyMock.replay(businessObjectService);

        purapGeneralLedgerService.generateEntriesClosePurchaseOrder(poClose);

        verifyAll(purchaseOrderItems);

        Assert.assertNull("Purchase Order Item should have outstanding encumbered quantity set to null", purchaseOrderItem.getItemOutstandingEncumberedQuantity());
        Assert.assertEquals("Purchase Order Item should have outstanding encumbered amount set to zero", KualiDecimal.ZERO, purchaseOrderItem.getItemOutstandingEncumberedAmount());
        Assert.assertEquals("GL Amount on accounting line should be 100.03", new KualiDecimal(100.04), accountingLine1.getAlternateAmountForGLEntryCreation());
        Assert.assertEquals("Outstanding encumbrance amount on accounting line should be zero'd out", KualiDecimal.ZERO, accountingLine1.getItemAccountOutstandingEncumbranceAmount());
        Assert.assertEquals("GL Amount on accounting line should be 100.04", new KualiDecimal(100.03), accountingLine2.getAlternateAmountForGLEntryCreation());
        Assert.assertEquals("Outstanding encumbrance amount on accounting line should be zero'd out", KualiDecimal.ZERO, accountingLine2.getItemAccountOutstandingEncumbranceAmount());
    }
}
