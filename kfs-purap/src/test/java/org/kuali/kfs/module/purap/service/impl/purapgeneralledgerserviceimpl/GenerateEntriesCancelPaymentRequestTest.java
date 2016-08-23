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
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.service.PurapAccountRevisionService;
import org.kuali.kfs.module.purap.service.PurapAccountingService;
import org.kuali.kfs.module.purap.service.impl.PurapGeneralLedgerServiceImpl;
import org.kuali.kfs.module.purap.util.SummaryAccount;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SpringContext.class})
public class GenerateEntriesCancelPaymentRequestTest extends PurapGeneralLedgerTest {
    private PurapGeneralLedgerServiceImpl purapGeneralLedgerService;
    private PurchaseOrderService purchaseOrderService;
    private PurapAccountingService purapAccountingService;
    private BusinessObjectService businessObjectService;
    private PurapAccountRevisionService purapAccountRevisionService;
    private AccountService accountService;
    private PaymentRequestDocument paymentRequestDocument;
    private PurchaseOrderDocument purchaseOrderDocument;
    private DateTimeService dateTimeService;

    @Before
    public void setUp() {
        purapGeneralLedgerService = new PurapGeneralLedgerServiceImpl();
        purchaseOrderService = EasyMock.createMock(PurchaseOrderService.class);
        businessObjectService = EasyMock.createMock(BusinessObjectService.class);
        purapAccountingService = EasyMock.createMock(PurapAccountingService.class);
        purapAccountRevisionService = EasyMock.createMock(PurapAccountRevisionService.class);
        purapGeneralLedgerService.setPurchaseOrderService(purchaseOrderService);
        purapGeneralLedgerService.setBusinessObjectService(businessObjectService);
        purapGeneralLedgerService.setPurapAccountingService(purapAccountingService);
        purapGeneralLedgerService.setPurapAccountRevisionService(purapAccountRevisionService);

        paymentRequestDocument = EasyMock.createMock(PaymentRequestDocument.class);
        purchaseOrderDocument = EasyMock.createMock(PurchaseOrderDocument.class);

        PowerMock.mockStatic(SpringContext.class);
        accountService = EasyMock.createMock(AccountService.class);
        dateTimeService = EasyMock.createMock(DateTimeService.class);
    }

    @Test
    public void testNoAccountingLines() {
        EasyMock.expect(paymentRequestDocument.getItems()).andReturn(new ArrayList());
        EasyMock.expect(paymentRequestDocument.getPurchaseOrderIdentifier()).andReturn(1000);
        EasyMock.expect(purchaseOrderService.getCurrentPurchaseOrder(1000)).andReturn(purchaseOrderDocument);
        EasyMock.expect(businessObjectService.save(purchaseOrderDocument)).andReturn(purchaseOrderDocument);
        EasyMock.expect(purapAccountingService.generateSummaryAccountsWithNoZeroTotalsNoUseTax(paymentRequestDocument)).andReturn(new ArrayList());
        paymentRequestDocument.setGeneralLedgerPendingEntries(new ArrayList());
        EasyMock.expect(paymentRequestDocument.getDocumentNumber()).andReturn("1000");
        Map<String, Object> matcher = new HashMap<>();
        matcher.put("financialSystemOriginationCode", "01");
        matcher.put("documentNumber", "1000");
        EasyMock.expect(businessObjectService.countMatching(GeneralLedgerPendingEntry.class, matcher)).andReturn(0);
        EasyMock.expect(paymentRequestDocument.getPurchaseOrderDocument()).andReturn(purchaseOrderDocument);
        EasyMock.expect(purchaseOrderDocument.getApplicationDocumentStatus()).andReturn(PurapConstants.PurchaseOrderStatuses.APPDOC_CANCELLED);
        paymentRequestDocument.setDebitCreditCodeForGLEntries("D");
        paymentRequestDocument.setGenerateEncumbranceEntries(true);
        EasyMock.expect(paymentRequestDocument.getGeneralLedgerPendingEntries()).andReturn(new ArrayList<>());
        EasyMock.expect(businessObjectService.save(new ArrayList<>())).andReturn(new ArrayList<>());
        replayAll();

        purapGeneralLedgerService.generateEntriesCancelAccountsPayableDocument(paymentRequestDocument);

        verifyAll();
    }

    @Test
    public void testItems() {
        List<PurApItem> items = new ArrayList<>();
        PurApItem item1 = getPaymentRequestItem(paymentRequestDocument, 1, KDONE, new BigDecimal("100.00"), ITEM);
        items.add(item1);

        List<PurApAccountingLine> lines = new ArrayList<>();
        lines.add(getPurchaseOrderAccount(KDTWO, KDONE));
        item1.setSourceAccountingLines(lines);

        EasyMock.expect(paymentRequestDocument.getItems()).andReturn(items).times(1);
        EasyMock.expect(paymentRequestDocument.getPurchaseOrderIdentifier()).andReturn(1000);
        EasyMock.expect(purchaseOrderService.getCurrentPurchaseOrder(1000)).andReturn(purchaseOrderDocument);
        EasyMock.expect(purchaseOrderDocument.getItems()).andReturn(new ArrayList());
        EasyMock.expect(paymentRequestDocument.isUseTaxIndicator()).andReturn(false).times(2);
        EasyMock.expect(businessObjectService.save(purchaseOrderDocument)).andReturn(purchaseOrderDocument);
        EasyMock.expect(purapAccountingService.generateSummaryAccountsWithNoZeroTotalsNoUseTax(paymentRequestDocument)).andReturn(new ArrayList());
        paymentRequestDocument.setGeneralLedgerPendingEntries(new ArrayList());
        EasyMock.expect(paymentRequestDocument.getDocumentNumber()).andReturn("1000");
        Map<String, Object> matcher = new HashMap<>();
        matcher.put("financialSystemOriginationCode", "01");
        matcher.put("documentNumber", "1000");
        EasyMock.expect(businessObjectService.countMatching(GeneralLedgerPendingEntry.class, matcher)).andReturn(0);
        EasyMock.expect(paymentRequestDocument.getPurchaseOrderDocument()).andReturn(purchaseOrderDocument);
        EasyMock.expect(purchaseOrderDocument.getApplicationDocumentStatus()).andReturn(PurapConstants.PurchaseOrderStatuses.APPDOC_CLOSED);
        EasyMock.expect(paymentRequestDocument.getGeneralLedgerPendingEntries()).andReturn(new ArrayList<>());
        EasyMock.expect(businessObjectService.save(new ArrayList<>())).andReturn(new ArrayList<>());
        replayAll();

        purapGeneralLedgerService.generateEntriesCancelAccountsPayableDocument(paymentRequestDocument);

        verifyAll();
    }

    @Test
    public void testItemsAccountingLines() {
        List<PurApItem> items = new ArrayList<>();
        PurApItem item1 = getPaymentRequestItem(paymentRequestDocument, 1, KDONE, new BigDecimal("100.00"), ITEM);
        items.add(item1);

        List<PurApAccountingLine> lines = new ArrayList<>();
        lines.add(getPurchaseOrderAccount(KDTWO, KDONE));
        item1.setSourceAccountingLines(lines);
        EasyMock.expect(paymentRequestDocument.getItems()).andReturn(items).times(2);

        EasyMock.expect(paymentRequestDocument.getPurchaseOrderIdentifier()).andReturn(1000);
        EasyMock.expect(purchaseOrderService.getCurrentPurchaseOrder(1000)).andReturn(purchaseOrderDocument);
        EasyMock.expect(purchaseOrderDocument.getItems()).andReturn(new ArrayList());
        EasyMock.expect(paymentRequestDocument.isUseTaxIndicator()).andReturn(false).times(2);
        EasyMock.expect(businessObjectService.save(purchaseOrderDocument)).andReturn(purchaseOrderDocument);

        List<SummaryAccount> sas = new ArrayList<>();
        SummaryAccount sa = new SummaryAccount();
        sa.setAccount(new SourceAccountingLine());
        sas.add(sa);
        EasyMock.expect(purapAccountingService.generateSummaryAccountsWithNoZeroTotalsNoUseTax(paymentRequestDocument)).andReturn(sas);
        paymentRequestDocument.setGeneralLedgerPendingEntries(new ArrayList());
        EasyMock.expect(paymentRequestDocument.getDocumentNumber()).andReturn("1000");
        paymentRequestDocument.setGenerateEncumbranceEntries(false);
        paymentRequestDocument.setDebitCreditCodeForGLEntries("C");
        EasyMock.expect(paymentRequestDocument.generateGeneralLedgerPendingEntries(EasyMock.anyObject(), EasyMock.anyObject())).andReturn(true);

        Map<String, Object> matcher = new HashMap<>();
        matcher.put("financialSystemOriginationCode", "01");
        matcher.put("documentNumber", "1000");
        EasyMock.expect(businessObjectService.countMatching(GeneralLedgerPendingEntry.class, matcher)).andReturn(1);

        EasyMock.expect(paymentRequestDocument.getPurchaseOrderDocument()).andReturn(purchaseOrderDocument);
        EasyMock.expect(purchaseOrderDocument.getApplicationDocumentStatus()).andReturn(PurapConstants.PurchaseOrderStatuses.APPDOC_CLOSED);
        EasyMock.expect(purapAccountingService.generateUseTaxAccount(paymentRequestDocument)).andReturn(new ArrayList<>());
        EasyMock.expect(paymentRequestDocument.getPurapDocumentIdentifier()).andReturn(1000);
        purapAccountingService.deleteSummaryAccounts(1000, "PREQ");

        EasyMock.expect(SpringContext.getBean(AccountService.class)).andReturn(accountService);
        accountService.populateAccountingLineChartIfNeeded(EasyMock.anyObject());

        EasyMock.expect(SpringContext.getBean(DateTimeService.class)).andReturn(dateTimeService);
        EasyMock.expect(dateTimeService.getCurrentTimestamp()).andReturn(getTestTimestamp());

        EasyMock.expect(paymentRequestDocument.getPostingYearFromPendingGLEntries()).andReturn(2016);
        EasyMock.expect(paymentRequestDocument.getPostingPeriodCodeFromPendingGLEntries()).andReturn("01");

        EasyMock.expect(businessObjectService.save((List) EasyMock.anyObject())).andReturn(null).times(2);
        purapAccountRevisionService.cancelPaymentRequestAccountRevisions(EasyMock.anyObject(), EasyMock.anyInt(), EasyMock.anyString());
        EasyMock.expect(paymentRequestDocument.getGeneralLedgerPendingEntries()).andReturn(new ArrayList<>());
        replayAll();

        purapGeneralLedgerService.generateEntriesCancelAccountsPayableDocument(paymentRequestDocument);

        verifyAll();
    }

    private Timestamp getTestTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        try {
            return new Timestamp(sdf.parse("20160701130000000").getTime());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Bad date string");
        }
    }

    private void replayAll() {
        EasyMock.replay(paymentRequestDocument, purapAccountingService, businessObjectService, purapAccountRevisionService, purchaseOrderService, purchaseOrderDocument, accountService, dateTimeService);
        PowerMock.replay(SpringContext.class);
    }

    private void verifyAll() {
        EasyMock.verify(paymentRequestDocument, purapAccountingService, businessObjectService, purapAccountRevisionService, purchaseOrderService, purchaseOrderDocument, accountService, dateTimeService);
        PowerMock.verify(SpringContext.class);
    }
}
