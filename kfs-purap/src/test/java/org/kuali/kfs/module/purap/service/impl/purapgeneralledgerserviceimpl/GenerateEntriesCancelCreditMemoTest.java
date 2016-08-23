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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.CreditMemoItem;
import org.kuali.kfs.module.purap.businessobject.ItemType;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderAccount;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.service.PurapAccountRevisionService;
import org.kuali.kfs.module.purap.service.PurapAccountingService;
import org.kuali.kfs.module.purap.service.impl.PurapGeneralLedgerServiceImpl;
import org.kuali.kfs.module.purap.util.SummaryAccount;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class GenerateEntriesCancelCreditMemoTest {
    private PurapGeneralLedgerServiceImpl purapGeneralLedgerService;

    private VendorCreditMemoDocument cm;
    private PurchaseOrderDocument po;
    private PaymentRequestDocument preq;
    private PurapAccountingService purapAccountingService;
    private GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    private BusinessObjectService businessObjectService;
    private PurchaseOrderService purchaseOrderService;
    private PurapAccountRevisionService purapAccountRevisionService;

    private List<CreditMemoItem> cmItems;
    private List<PurchaseOrderItem> poItems;
    private List<Object> dynamicMocks;

    @Before
    public void setUp() {
        purapGeneralLedgerService = new PurapGeneralLedgerServiceImpl();

        purapAccountingService = EasyMock.createMock(PurapAccountingService.class);
        purapGeneralLedgerService.setPurapAccountingService(purapAccountingService);
        generalLedgerPendingEntryService = EasyMock.createMock(GeneralLedgerPendingEntryService.class);
        purapGeneralLedgerService.setGeneralLedgerPendingEntryService(generalLedgerPendingEntryService);
        businessObjectService = EasyMock.createMock(BusinessObjectService.class);
        purapGeneralLedgerService.setBusinessObjectService(businessObjectService);
        purchaseOrderService = EasyMock.createMock(PurchaseOrderService.class);
        purapGeneralLedgerService.setPurchaseOrderService(purchaseOrderService);
        purapAccountRevisionService = EasyMock.createMock(PurapAccountRevisionService.class);
        purapGeneralLedgerService.setPurapAccountRevisionService(purapAccountRevisionService);

        cm = EasyMock.createMock(VendorCreditMemoDocument.class);
        po = EasyMock.createMock(PurchaseOrderDocument.class);
        preq = EasyMock.createMock(PaymentRequestDocument.class);

        cmItems = new ArrayList<>();
        poItems = new ArrayList<>();
        dynamicMocks = new ArrayList<>();
    }

    private void execute() {
        EasyMock.replay(cm, po, preq, purapAccountingService, purchaseOrderService,
                generalLedgerPendingEntryService, businessObjectService, purapAccountRevisionService);
        for (Object mock : dynamicMocks) {
            EasyMock.replay(mock);
        }

        purapGeneralLedgerService.generateEntriesCancelAccountsPayableDocument(cm);
        EasyMock.verify(cm, po, preq, purapAccountingService, purchaseOrderService,
                generalLedgerPendingEntryService, businessObjectService, purapAccountRevisionService);
        for (Object mock : dynamicMocks) {
            EasyMock.verify(mock);
        }
    }

    private void prepareCreditMemo(boolean poSource, boolean preqSource) {
        cm.setGeneralLedgerPendingEntries(new ArrayList<>());
        EasyMock.expectLastCall();
        EasyMock.expect(cm.getDocumentNumber()).andReturn("1").anyTimes();
        EasyMock.expect(cm.isSourceVendor()).andReturn(!(poSource || preqSource)).anyTimes();
        EasyMock.expect(cm.getGeneralLedgerPendingEntries()).andReturn(new ArrayList<>()).anyTimes();
        EasyMock.expect(cm.getItems()).andReturn(cmItems).anyTimes();
        EasyMock.expect(cm.isSourceDocumentPurchaseOrder()).andReturn(poSource).anyTimes();
        EasyMock.expect(cm.isSourceDocumentPaymentRequest()).andReturn(preqSource).anyTimes();
        EasyMock.expect(cm.getPostingYearFromPendingGLEntries()).andReturn(2015).anyTimes();
        EasyMock.expect(cm.getPostingPeriodCodeFromPendingGLEntries()).andReturn("01").anyTimes();
    }

    private void prepareBusinessObjectService() {
        Map<String, String> fieldValues = new HashMap<>();
        fieldValues.put("financialSystemOriginationCode", PurapConstants.PURAP_ORIGIN_CODE);
        fieldValues.put("documentNumber", "1");
        EasyMock.expect(businessObjectService.countMatching(GeneralLedgerPendingEntry.class, fieldValues)).andReturn(2);
        EasyMock.expect(businessObjectService.save(new ArrayList<>())).andReturn(null);
    }

    private void baseExpectations(boolean poSource, boolean preqSource, List<SummaryAccount> summaryAccounts) {
        prepareCreditMemo(poSource, preqSource);
        prepareBusinessObjectService();
        EasyMock.expect(purapAccountingService.generateSummaryAccountsWithNoZeroTotalsNoUseTax(cm)).andReturn(summaryAccounts);
    }

    private void preparePO(String status) {
        EasyMock.expect(purchaseOrderService.getCurrentPurchaseOrder(1234)).andReturn(po);
        EasyMock.expect(businessObjectService.save(po)).andReturn(po);
        EasyMock.expect(po.getApplicationDocumentStatus()).andReturn(status);
        EasyMock.expect(po.getItems()).andReturn(poItems).anyTimes();
    }

    private CreditMemoItem createCmItem(int lineNumber, double totalAmount, double quantity, ItemType type) {
        CreditMemoItem item = EasyMock.createMock(CreditMemoItem.class);
        EasyMock.expect(item.getItemLineNumber()).andReturn(lineNumber).anyTimes();
        EasyMock.expect(item.getItemType()).andReturn(type).anyTimes();
        EasyMock.expect(item.getItemTypeCode()).andReturn(type.getItemTypeCode()).anyTimes();
        EasyMock.expect(item.getTotalAmount()).andReturn(new KualiDecimal(totalAmount)).anyTimes();
        EasyMock.expect(item.getItemQuantity()).andReturn(new KualiDecimal(quantity)).anyTimes();
        dynamicMocks.add(item);
        return item;
    }

    private PurchaseOrderItem createPoItem(int lineNumber, double quantity, double amount, double invoicedQuantity, double newInvoicedQuantity, double invoicedAmount, double newInvoicedAmount,
            double outstandingQuantity, double newOutstandingQuantity, double outstandingAmount, double newOutstandingAmount,
            double unitPrice, double taxAmount, ItemType type, List<PurApAccountingLine> sourceAccountingLines) {
        List<PurApAccountingLine> sourceAccountingLinesCopy = new ArrayList<>();
        sourceAccountingLinesCopy.addAll(sourceAccountingLines);
        PurchaseOrderItem item = EasyMock.createMock(PurchaseOrderItem.class);
        EasyMock.expect(item.getItemLineNumber()).andReturn(lineNumber).anyTimes();
        EasyMock.expect(item.getItemType()).andReturn(type).anyTimes();
        EasyMock.expect(item.getItemTypeCode()).andReturn(type.getItemTypeCode()).anyTimes();
        EasyMock.expect(item.getItemInvoicedTotalQuantity()).andReturn(new KualiDecimal(invoicedQuantity)).anyTimes();
        EasyMock.expect(item.getItemOutstandingEncumberedQuantity()).andReturn(new KualiDecimal(outstandingQuantity)).anyTimes();
        EasyMock.expect(item.getItemUnitPrice()).andReturn(new BigDecimal(unitPrice)).anyTimes();
        EasyMock.expect(item.getItemTaxAmount()).andReturn(new KualiDecimal(taxAmount)).anyTimes();
        EasyMock.expect(item.getItemQuantity()).andReturn(new KualiDecimal(quantity)).anyTimes();
        EasyMock.expect(item.getItemOutstandingEncumberedAmount()).andReturn(new KualiDecimal(outstandingAmount)).anyTimes();
        EasyMock.expect(item.getItemInvoicedTotalAmount()).andReturn(new KualiDecimal(invoicedAmount)).anyTimes();
        EasyMock.expect(item.getSourceAccountingLines()).andReturn(sourceAccountingLinesCopy).anyTimes();
        EasyMock.expect(item.getTotalAmount()).andReturn(new KualiDecimal(amount)).anyTimes();
        if (type.isQuantityBasedGeneralLedgerIndicator()) {
            item.setItemInvoicedTotalQuantity(new KualiDecimal(newInvoicedQuantity));
            EasyMock.expectLastCall();
            item.setItemOutstandingEncumberedQuantity(new KualiDecimal(newOutstandingQuantity));
            EasyMock.expectLastCall();
        }
        item.setItemOutstandingEncumberedAmount(new KualiDecimal(newOutstandingAmount));
        EasyMock.expectLastCall();
        item.setItemInvoicedTotalAmount(new KualiDecimal(newInvoicedAmount));
        EasyMock.expectLastCall();

        dynamicMocks.add(item);
        return item;
    }

    private PurchaseOrderAccount createPoAccountingLine(double percent, double outstandingAmount, double newOutstandingAmount, Double altAmount,
            String objectCode) {
        SourceAccountingLine acctString = new SourceAccountingLine();
        acctString.setFinancialObjectCode(objectCode);

        PurchaseOrderAccount account = EasyMock.mock(PurchaseOrderAccount.class);
        EasyMock.expect(account.isEmpty()).andReturn(false).anyTimes();
        EasyMock.expect(account.getAccountLinePercent()).andReturn(new BigDecimal(percent)).anyTimes();
        EasyMock.expect(account.getItemAccountOutstandingEncumbranceAmount()).andReturn(new KualiDecimal(outstandingAmount));
        EasyMock.expect(account.generateSourceAccountingLine()).andReturn(acctString).anyTimes();
        EasyMock.expect(account.compareTo(EasyMock.isA(PurchaseOrderAccount.class))).andReturn(0).anyTimes();

        account.setItemAccountOutstandingEncumbranceAmount(new KualiDecimal(newOutstandingAmount));
        EasyMock.expectLastCall();

        if (altAmount != null) {
            EasyMock.expect(account.getItemAccountOutstandingEncumbranceAmount()).andReturn(new KualiDecimal(newOutstandingAmount));
            account.setItemAccountOutstandingEncumbranceAmount(new KualiDecimal(altAmount));
            EasyMock.expectLastCall();
        }

        dynamicMocks.add(account);
        return account;
    }

    private ItemType createItemType(String code, boolean lineItem, boolean quantityBased) {
        ItemType itemType = new ItemType();
        itemType.setItemTypeCode(code);
        itemType.setQuantityBasedGeneralLedgerIndicator(quantityBased);
        itemType.setAdditionalChargeIndicator(!lineItem);
        return itemType;
    }

    private void prepareItem(int lineNumber, double cmAmount, double cmQuantity, double poAmount, double poQuantity,
            double invoicedQuantity, double newInvoicedQuantity, double invoicedAmount, double newInvoicedAmount,
            double outstandingQuantity, double newOutstandingQuantity, double outstandingAmount, double newOutstandingAmount,
            double unitPrice, double taxAmount, ItemType type, List<PurApAccountingLine> sourceAccountingLines) {
        cmItems.add(createCmItem(lineNumber, cmAmount, cmQuantity, type));
        poItems.add(createPoItem(lineNumber, poQuantity, poAmount, invoicedQuantity, newInvoicedQuantity, invoicedAmount, newInvoicedAmount,
                outstandingQuantity, newOutstandingQuantity, outstandingAmount, newOutstandingAmount, unitPrice, taxAmount, type, sourceAccountingLines));
    }

    private void prepareItemsNoAccountingLines() {
        ItemType lineItemQty = createItemType("LINEQ", true, true);
        ItemType lineItemAmt = createItemType("LINEA", true, false);
        ItemType addlItem = createItemType("ADDL", false, false);

        // "Normal" quantity based item, no tax
        // PO: 4 @ $2.5 = $10.00
        // Previously invoiced: 1 @ $2.5 = $2.5
        // CM: 1 @ $2.5 = $2.5
        // Result: 2 @ $2.5 = $5.00 outstanding
        prepareItem(1, 2.5, 1, 10.0, 4, 1, 2, 2.5, 5.0, 3, 2, 7.5, 5.0, 2.5, 0, lineItemQty, new ArrayList<>());

        // "Normal" amount based item, no tax
        // PO: $10.00
        // Previously invoiced: $4.00
        // CM: $2.00
        // Result: $4.00 outstanding
        prepareItem(2, 2.0, 0, 10.0, 0, 0, 0, 4.0, 6.0, 0, 0, 6.0, 4.0, 0, 0, lineItemAmt, new ArrayList<>());

        // Below-the-line item, no tax
        // PO: $10
        // Previously invoiced: $3.00
        // CM: $1.00
        // Result: $6.00 outstanding
        prepareItem(3, 1.0, 0, 10.0, 0, 0, 0, 3.0, 4.0, 0, 0, 7.0, 6.0, 0, 0, addlItem, new ArrayList<>());

        // Amount-based item, tax = $5.00 (tax is ignored in amount-based calculation)
        // PO: $10.00
        // Previously invoiced: $4.00
        // CM: $2.00
        // Result: $4.00 outstanding
        prepareItem(4, 2.0, 0, 10.0, 0, 0, 0, 4.0, 6.0, 0, 0, 6.0, 4.0, 0, 5.0, lineItemAmt, new ArrayList<>());

        // Quantity based item, $0.40 tax (Tax should be prorated by quantity)
        // PO: 4 @ $2.5 = $10.00
        // Previously invoiced: 1 @ $2.5 = $2.5 (and $0.10 tax)
        // CM: 1 @ $2.5 = $2.5 (and $0.10 tax)
        // Result: 2 @ $2.5 + tax = $5.20 outstanding
        prepareItem(5, 2.5, 1, 10.0, 4, 1, 2, 2.5, 5.0, 3, 2, 7.80, 5.2, 2.5, 0.4, lineItemQty, new ArrayList<>());
    }

    private void prepareItemsAccountingLines(boolean encumbranceOpen) {
        // All items in this set of scenarios have the following in common:
        // Amount-based item, no tax
        // PO: $10000.00
        // Previously invoiced: $3000.00
        // CM: $1000.00
        // Result: $6000.00 outstanding
        ItemType lineItemAmt = createItemType("LINEA", true, false);
        List<PurApAccountingLine> sourceAccountingLines;

        // All one accounting line
        sourceAccountingLines = new ArrayList<>();
        sourceAccountingLines.add(createPoAccountingLine(100, 7000.0, 6000.0, 6000.0, "A"));
        prepareItem(1, 1000.0, 0, 10000.0, 0, 0, 0, 3000.0, 4000.0, 0, 0, 7000.0, 6000.0, 0, 0, lineItemAmt, sourceAccountingLines);

        // 50/50
        sourceAccountingLines = new ArrayList<>();
        sourceAccountingLines.add(createPoAccountingLine(50, 3500.0, 3000.0, null, "B"));
        sourceAccountingLines.add(createPoAccountingLine(50, 3500.0, 3000.0, 3000.0, "C"));
        prepareItem(2, 1000.0, 0, 10000.0, 0, 0, 0, 3000.0, 4000.0, 0, 0, 7000.0, 6000.0, 0, 0, lineItemAmt, sourceAccountingLines);

        // Rounding error to fix
        sourceAccountingLines = new ArrayList<>();
        sourceAccountingLines.add(createPoAccountingLine(66.66, 4666.2, 3999.6, null, "C"));
        sourceAccountingLines.add(createPoAccountingLine(33.33, 2333.8, 2000.5, 2000.4, "D"));
        prepareItem(3, 1000.0, 0, 10000.0, 0, 0, 0, 3000.0, 4000.0, 0, 0, 7000.0, 6000.0, 0, 0, lineItemAmt, sourceAccountingLines);

        // Check that amounts combine correctly
        if (encumbranceOpen) {
            SourceAccountingLine acctString1 = new SourceAccountingLine();
            acctString1.setFinancialObjectCode("A");
            acctString1.setAmount(new KualiDecimal(1000));

            SourceAccountingLine acctString2 = new SourceAccountingLine();
            acctString2.setFinancialObjectCode("B");
            acctString2.setAmount(new KualiDecimal(500));

            SourceAccountingLine acctString3 = new SourceAccountingLine();
            acctString3.setFinancialObjectCode("C");
            acctString3.setAmount(new KualiDecimal(1166.6));

            SourceAccountingLine acctString4 = new SourceAccountingLine();
            acctString4.setFinancialObjectCode("D");
            acctString4.setAmount(new KualiDecimal(333.4));

            EasyMock.expect(cm.generateGeneralLedgerPendingEntries(EasyMock.eq(acctString1), EasyMock.anyObject())).andReturn(true);
            EasyMock.expect(cm.generateGeneralLedgerPendingEntries(EasyMock.eq(acctString2), EasyMock.anyObject())).andReturn(true);
            EasyMock.expect(cm.generateGeneralLedgerPendingEntries(EasyMock.eq(acctString3), EasyMock.anyObject())).andReturn(true);
            EasyMock.expect(cm.generateGeneralLedgerPendingEntries(EasyMock.eq(acctString4), EasyMock.anyObject())).andReturn(true);
        }
    }

    @Test
    public void testBaseCase() {
        baseExpectations(false, false, null);
        execute();
    }

    @Test
    public void testClosedPONoItems() {
        baseExpectations(true, false, null);
        preparePO(PurapConstants.PurchaseOrderStatuses.APPDOC_CLOSED);
        EasyMock.expect(cm.getPurchaseOrderIdentifier()).andReturn(1234);
        execute();
    }

    @Test
    public void testClosedPOFromPreqNoItems() {
        baseExpectations(false, true, null);
        preparePO(PurapConstants.PurchaseOrderStatuses.APPDOC_CLOSED);
        EasyMock.expect(cm.getPaymentRequestDocument()).andReturn(preq);
        EasyMock.expect(preq.getPurchaseOrderIdentifier()).andReturn(1234);
        execute();
    }

    @Test
    public void testClosedPOWithItemsNoAccountingLines() {
        prepareItemsNoAccountingLines();
        baseExpectations(true, false, null);
        preparePO(PurapConstants.PurchaseOrderStatuses.APPDOC_CLOSED);
        EasyMock.expect(cm.getPurchaseOrderIdentifier()).andReturn(1234);
        execute();
    }

    @Test
    public void testOpenPOWithItemsNoAccountingLines() {
        prepareItemsNoAccountingLines();
        baseExpectations(true, false, null);
        preparePO(PurapConstants.PurchaseOrderStatuses.APPDOC_OPEN);
        EasyMock.expect(cm.getPurchaseOrderIdentifier()).andReturn(1234);
        cm.setGenerateEncumbranceEntries(true);
        EasyMock.expectLastCall();
        cm.setDebitCreditCodeForGLEntries(KFSConstants.GL_DEBIT_CODE);
        EasyMock.expectLastCall();
        execute();
    }

    @Test
    public void testClosedPOAccountingLines() {
        prepareItemsAccountingLines(false);
        baseExpectations(true, false, null);
        preparePO(PurapConstants.PurchaseOrderStatuses.APPDOC_CLOSED);
        EasyMock.expect(cm.getPurchaseOrderIdentifier()).andReturn(1234);
        execute();
    }

    @Test
    public void testOpenPOAccountingLines() {
        prepareItemsAccountingLines(true);
        baseExpectations(true, false, null);
        preparePO(PurapConstants.PurchaseOrderStatuses.APPDOC_OPEN);
        EasyMock.expect(cm.getPurchaseOrderIdentifier()).andReturn(1234);
        cm.setGenerateEncumbranceEntries(true);
        EasyMock.expectLastCall();
        cm.setDebitCreditCodeForGLEntries(KFSConstants.GL_DEBIT_CODE);
        EasyMock.expectLastCall();
        execute();
    }

    @Test
    public void testSummaryAccountHandling() {
        List<SummaryAccount> summaryAccounts = new ArrayList<>();
        SummaryAccount summaryAccount = EasyMock.createMock(SummaryAccount.class);
        SourceAccountingLine account = new SourceAccountingLine();
        EasyMock.expect(summaryAccount.getAccount()).andReturn(account);
        summaryAccounts.add(summaryAccount);
        dynamicMocks.add(summaryAccount);
        baseExpectations(false, false, summaryAccounts);
        cm.setGenerateEncumbranceEntries(false);
        EasyMock.expectLastCall();
        cm.setDebitCreditCodeForGLEntries(KFSConstants.GL_DEBIT_CODE);
        EasyMock.expectLastCall();
        EasyMock.expect(cm.generateGeneralLedgerPendingEntries(EasyMock.eq(account), EasyMock.anyObject())).andReturn(true);
        EasyMock.expect(purapAccountingService.generateUseTaxAccount(cm)).andReturn(new ArrayList<>());
        purapAccountRevisionService.cancelCreditMemoAccountRevisions(new ArrayList<>(), 2015, "01");
        EasyMock.expectLastCall();
        execute();
    }
}
