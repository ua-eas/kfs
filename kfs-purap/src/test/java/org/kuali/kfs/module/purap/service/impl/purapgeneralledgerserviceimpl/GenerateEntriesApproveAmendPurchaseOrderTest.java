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
package org.kuali.kfs.module.purap.service.impl.purapgeneralledgerserviceimpl;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderAccount;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.service.PurapAccountingService;
import org.kuali.kfs.module.purap.service.impl.PurapGeneralLedgerServiceImpl;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GenerateEntriesApproveAmendPurchaseOrderTest {
    private PurapGeneralLedgerServiceImpl purapGeneralLedgerService;

    private PurchaseOrderDocument oldPo;
    private PurchaseOrderDocument newPo;
    private PurapAccountingService purapAccountingService;
    private GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    private BusinessObjectService businessObjectService;
    private PurchaseOrderService purchaseOrderService;

    private List<PurApItem> newPoItems;
    private List<PurApItem> newPoActiveItems;
    private List<PurApItem> oldPoItems;
    private List<GeneralLedgerPendingEntry> glpes;
    private List<SourceAccountingLine> newSummaryAccountingLines;
    private List<SourceAccountingLine> oldSummaryAccountingLines;
    private List<SourceAccountingLine> diffSummaryAccountingLines;
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

        oldPo = EasyMock.createMock(PurchaseOrderDocument.class);
        newPo = EasyMock.createMock(PurchaseOrderDocument.class);

        newPoItems = new ArrayList<>();
        newPoActiveItems = new ArrayList<>();
        oldPoItems = new ArrayList<>();
        glpes = new ArrayList<>();
        newSummaryAccountingLines = new ArrayList<>();
        oldSummaryAccountingLines = new ArrayList<>();
        diffSummaryAccountingLines = new ArrayList<>();
        dynamicMocks = new ArrayList<>();
    }

    private void execute() {
        EasyMock.replay(oldPo, newPo, purapAccountingService, generalLedgerPendingEntryService, businessObjectService,
            purchaseOrderService);
        for (Object mock : dynamicMocks) {
            EasyMock.replay(mock);
        }

        purapGeneralLedgerService.generateEntriesApproveAmendPurchaseOrder(newPo);
        EasyMock.verify(oldPo, newPo, purapAccountingService, generalLedgerPendingEntryService, businessObjectService,
            purchaseOrderService);
        for (Object mock : dynamicMocks) {
            EasyMock.verify(mock);
        }
    }

    private void prepareNewPO() {
        EasyMock.expect(newPo.getPurapDocumentIdentifier()).andReturn(101);
        EasyMock.expect(newPo.getItems()).andReturn(newPoItems).anyTimes();
        EasyMock.expect(newPo.getItemsActiveOnly()).andReturn(newPoActiveItems).anyTimes();
        newPo.setGlOnlySourceAccountingLines(eqSummaryAccountingLineList(diffSummaryAccountingLines));
        EasyMock.expectLastCall();
        EasyMock.expect(newPo.getGeneralLedgerPendingEntries()).andReturn(glpes).anyTimes();
    }

    private void prepareOldPO() {
        EasyMock.expect(oldPo.getItemsActiveOnlySetupAlternateAmount()).andReturn(oldPoItems);
    }

    private KualiDecimal doubleToKualiDecimal(Double amount) {
        if (amount == null) {
            return null;
        }
        return new KualiDecimal(amount);
    }

    private void prepareItem(Double quantity, Double invoicedQuantity, Double outstandingQuantity,
                             Double unitPrice, Double invoicedAmount, Double taxAmount, Double outstandingAmount,
                             List<PurApAccountingLine> accountingLines, boolean active) {
        PurchaseOrderItem item = EasyMock.createMock(PurchaseOrderItem.class);

        EasyMock.expect(item.getItemQuantity()).andReturn(doubleToKualiDecimal(quantity)).anyTimes();
        EasyMock.expect(item.getItemInvoicedTotalQuantity()).andReturn(doubleToKualiDecimal(invoicedQuantity)).anyTimes();
        EasyMock.expect(item.getItemInvoicedTotalAmount()).andReturn(doubleToKualiDecimal(invoicedAmount)).anyTimes();
        EasyMock.expect(item.getSourceAccountingLines()).andReturn(accountingLines).anyTimes();
        EasyMock.expect(item.getItemOutstandingEncumberedQuantity()).andReturn(doubleToKualiDecimal(outstandingQuantity)).anyTimes();
        EasyMock.expect(item.getItemOutstandingEncumberedAmount()).andReturn(doubleToKualiDecimal(outstandingAmount)).anyTimes();
        EasyMock.expect(item.getItemUnitPrice()).andReturn(new BigDecimal(unitPrice)).anyTimes();
        EasyMock.expect(item.getItemTaxAmount()).andReturn(doubleToKualiDecimal(taxAmount)).anyTimes();

        item.setItemInvoicedTotalQuantity(doubleToKualiDecimal(invoicedQuantity));
        EasyMock.expectLastCall();
        item.setItemInvoicedTotalAmount(doubleToKualiDecimal(invoicedAmount));
        EasyMock.expectLastCall();
        item.setItemOutstandingEncumberedQuantity(doubleToKualiDecimal(outstandingQuantity));
        EasyMock.expectLastCall();
        item.setItemOutstandingEncumberedAmount(doubleToKualiDecimal(outstandingAmount));
        EasyMock.expectLastCall();

        EasyMock.expect(item.isItemActiveIndicator()).andReturn(active).anyTimes();
        if (active) {
            newPoActiveItems.add(item);
        }

        newPoItems.add(item);
        dynamicMocks.add(item);
    }

    private PurchaseOrderAccount createPoAccountingLine(double percent, double amount, double newOutstandingAmount) {
        PurchaseOrderAccount account = EasyMock.mock(PurchaseOrderAccount.class);
        //EasyMock.expect(account.isEmpty()).andReturn(false).anyTimes();
        EasyMock.expect(account.getAccountLinePercent()).andReturn(new BigDecimal(percent)).anyTimes();
        EasyMock.expect(account.getAmount()).andReturn(doubleToKualiDecimal(amount)).anyTimes();
        EasyMock.expect(account.getItemAccountOutstandingEncumbranceAmount()).andReturn(doubleToKualiDecimal(newOutstandingAmount));
        //EasyMock.expect(account.compareTo(EasyMock.isA(PurchaseOrderAccount.class))).andReturn(0).anyTimes();

        account.setItemAccountOutstandingEncumbranceAmount(doubleToKualiDecimal(newOutstandingAmount));
        EasyMock.expectLastCall();
        account.setAlternateAmountForGLEntryCreation(doubleToKualiDecimal(newOutstandingAmount));
        EasyMock.expectLastCall();

        dynamicMocks.add(account);
        return account;
    }

    private void prepareSummaryAccountingLine(Double newAmount, Double oldAmount, Double diffAmount, String objectCode) {
        SourceAccountingLine account;

        if (newAmount != null) {
            account = new SourceAccountingLine();
            account.setFinancialObjectCode(objectCode);
            account.setAmount(doubleToKualiDecimal(newAmount));
            newSummaryAccountingLines.add(account);
        }

        if (oldAmount != null) {
            account = new SourceAccountingLine();
            account.setFinancialObjectCode(objectCode);
            account.setAmount(doubleToKualiDecimal(oldAmount));
            oldSummaryAccountingLines.add(account);
        }

        account = new SourceAccountingLine();
        account.setFinancialObjectCode(objectCode);
        account.setAmount(doubleToKualiDecimal(diffAmount));
        diffSummaryAccountingLines.add(account);
    }

    private void prepareItemsNoAccountingLines() {
        // Active
        prepareItem(3.0, 1.0, 2.0, 5.0, 5.0, 0.5, 10.5, new ArrayList<>(), true);
        // Not active
        prepareItem(3.0, 1.0, 0.0, 5.0, 5.0, 0.5, 0.0, new ArrayList<>(), false);
        // Null quantity
        prepareItem(null, 0.0, null, 10.0, 3.0, 0.5, 7.0, new ArrayList<>(), true);
    }

    private void prepareItemsWithAccountingLines() {
        List<PurApAccountingLine> accountingLines;

        // One accounting line
        accountingLines = new ArrayList<>();
        accountingLines.add(createPoAccountingLine(100, 15.0, 10.0));
        prepareItem(3.0, 1.0, 2.0, 5.0, 5.0, 0.0, 10.0, accountingLines, true);

        // 50/50
        accountingLines = new ArrayList<>();
        accountingLines.add(createPoAccountingLine(50, 7.5, 5.0));
        accountingLines.add(createPoAccountingLine(50, 7.5, 5.0));
        prepareItem(3.0, 1.0, 2.0, 5.0, 5.0, 0.0, 10.0, accountingLines, true);

        // Rounding error that never gets fixed.  This is incorrect behavior that should be addressed at some time.
        accountingLines = new ArrayList<>();
        accountingLines.add(createPoAccountingLine(66.66, 6666.67, 6700.0));
        accountingLines.add(createPoAccountingLine(33.33, 3333.33, 3300.0));
        prepareItem(1.0, 0.0, 1.0, 10000.0, 0.0, 0.0, 10000.0, accountingLines, true);
    }

    private void prepareSummaryAccountingLines() {
        // Matching old and new lines
        prepareSummaryAccountingLine(10.0, 7.0, 3.0, "Matching");

        // New line without old line
        prepareSummaryAccountingLine(10.0, null, 10.0, "NewOnly");

        // Old line without new line
        prepareSummaryAccountingLine(null, 7.0, -7.0, "OldOnly");
    }

    private void baseExpectations() {
        prepareOldPO();
        prepareNewPO();
        EasyMock.expect(purchaseOrderService.getCurrentPurchaseOrder(101)).andReturn(oldPo);
        EasyMock.expect(purapAccountingService.generateSummaryWithNoZeroTotalsUsingAlternateAmount(newPoActiveItems))
            .andReturn(newSummaryAccountingLines);
        EasyMock.expect(purapAccountingService.generateSummaryWithNoZeroTotalsUsingAlternateAmount(oldPoItems))
            .andReturn(oldSummaryAccountingLines);
        EasyMock.expect(generalLedgerPendingEntryService.generateGeneralLedgerPendingEntries(newPo)).andReturn(true);
        EasyMock.expect(businessObjectService.save(glpes)).andReturn(null);
    }

    @Test
    public void testBaseCase() {
        baseExpectations();
        execute();
    }

    @Test
    public void testPoWithItems() {
        baseExpectations();
        prepareItemsNoAccountingLines();
        execute();
    }

    @Test
    public void testPoWithAccountingLines() {
        baseExpectations();
        prepareItemsWithAccountingLines();
        execute();
    }

    @Test
    public void testPoWithSummaryAccountingLines() {
        baseExpectations();
        prepareSummaryAccountingLines();
        execute();
    }

    public static List<SourceAccountingLine> eqSummaryAccountingLineList(List<SourceAccountingLine> in) {
        EasyMock.reportMatcher(new SummaryAccountingListEquals(in));
        return null;
    }

    /**
     * Argument matcher that verifies the list of summary accounting lines match, without worrying about order
     */
    private static class SummaryAccountingListEquals implements IArgumentMatcher {

        private List<SourceAccountingLine> expected;

        public SummaryAccountingListEquals(List<SourceAccountingLine> expected) {
            this.expected = expected;
        }

        @Override
        public boolean matches(Object argument) {
            if (!(argument instanceof List)) {
                return false;
            }
            List<SourceAccountingLine> actual = (List<SourceAccountingLine>) argument;

            if (actual.size() != expected.size()) {
                return false;
            }

            Collections.sort(expected, (a1, a2) -> a1.getFinancialObjectCode().compareTo(a2.getFinancialObjectCode()));
            Collections.sort(actual, (a1, a2) -> a1.getFinancialObjectCode().compareTo(a2.getFinancialObjectCode()));
            for (int i = 0; i < expected.size(); i++) {
                SourceAccountingLine expectedItem = expected.get(i);
                SourceAccountingLine actualItem = actual.get(i);
                if (!expectedItem.getFinancialObjectCode().equals(actualItem.getFinancialObjectCode())
                    || !expectedItem.getAmount().equals(actualItem.getAmount())) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public void appendTo(StringBuffer buffer) {
            buffer.append(expected);
        }

    }
}
