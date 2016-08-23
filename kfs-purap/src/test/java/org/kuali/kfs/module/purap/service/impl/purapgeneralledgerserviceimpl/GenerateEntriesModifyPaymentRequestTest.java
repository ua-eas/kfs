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
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.service.PurapAccountRevisionService;
import org.kuali.kfs.module.purap.service.PurapAccountingService;
import org.kuali.kfs.module.purap.service.impl.PurapGeneralLedgerServiceImpl;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenerateEntriesModifyPaymentRequestTest extends PurapGeneralLedgerTest {
    private PurapGeneralLedgerServiceImpl purapGeneralLedgerService;
    private PurapAccountingService purapAccountingService;
    private BusinessObjectService businessObjectService;
    private PurapAccountRevisionService purapAccountRevisionService;
    private PaymentRequestDocument paymentRequestDocument;

    @Before
    public void setUp() {
        purapGeneralLedgerService = new PurapGeneralLedgerServiceImpl();
        businessObjectService = EasyMock.createMock(BusinessObjectService.class);
        purapAccountingService = EasyMock.createMock(PurapAccountingService.class);
        purapAccountRevisionService = EasyMock.createMock(PurapAccountRevisionService.class);
        purapGeneralLedgerService.setBusinessObjectService(businessObjectService);
        purapGeneralLedgerService.setPurapAccountingService(purapAccountingService);
        purapGeneralLedgerService.setPurapAccountRevisionService(purapAccountRevisionService);

        paymentRequestDocument = EasyMock.createMock(PaymentRequestDocument.class);
    }

    @Test
    public void testNoAccountingLines() {
        EasyMock.expect(paymentRequestDocument.getItems()).andReturn(new ArrayList());
        EasyMock.expect(purapAccountingService.generateSummaryWithNoZeroTotalsNoUseTax(new ArrayList())).andReturn(new ArrayList());
        EasyMock.expect(paymentRequestDocument.getPurapDocumentIdentifier()).andReturn(1000);
        EasyMock.expect(purapAccountingService.getAccountsPayableSummaryAccounts(1000, "PREQ")).andReturn(new ArrayList());
        paymentRequestDocument.setGeneralLedgerPendingEntries(new ArrayList());
        EasyMock.expect(paymentRequestDocument.getDocumentNumber()).andReturn("1000");
        Map<String, Object> matcher = new HashMap<>();
        matcher.put("financialSystemOriginationCode", "01");
        matcher.put("documentNumber", "1000");
        EasyMock.expect(businessObjectService.countMatching(GeneralLedgerPendingEntry.class, matcher)).andReturn(0);
        EasyMock.expect(paymentRequestDocument.getGeneralLedgerPendingEntries()).andReturn(new ArrayList<>());
        EasyMock.expect(businessObjectService.save(new ArrayList<>())).andReturn(new ArrayList<>());
        replayAll();

        purapGeneralLedgerService.generateEntriesModifyPaymentRequest(paymentRequestDocument);

        verifyAll();
    }

    @Test
    public void testAccountingLines() {
        List<PurApItem> items = new ArrayList<>();
        PurApItem item1 = getPaymentRequestItem(paymentRequestDocument, 1, KDONE, new BigDecimal("100.00"), ITEM);
        items.add(item1);

        List<PurApAccountingLine> lines = new ArrayList<>();
        lines.add(getPurchaseOrderAccount(KDTWO, KDONE));
        item1.setSourceAccountingLines(lines);

        EasyMock.expect(paymentRequestDocument.getItems()).andReturn(items).times(2);

        List<SourceAccountingLine> salines = new ArrayList<>();
        salines.add(getPurchaseOrderAccount(KDTWO, KDONE));
        EasyMock.expect(purapAccountingService.generateSummaryWithNoZeroTotalsNoUseTax(items)).andReturn(salines);

        EasyMock.expect(paymentRequestDocument.getPurapDocumentIdentifier()).andReturn(1000).times(2);
        EasyMock.expect(purapAccountingService.getAccountsPayableSummaryAccounts(1000, "PREQ")).andReturn(new ArrayList());
        paymentRequestDocument.setGeneralLedgerPendingEntries(new ArrayList());
        EasyMock.expect(paymentRequestDocument.getDocumentNumber()).andReturn("1000");
        Map<String, Object> matcher = new HashMap<>();
        matcher.put("financialSystemOriginationCode", "01");
        matcher.put("documentNumber", "1000");
        EasyMock.expect(businessObjectService.countMatching(GeneralLedgerPendingEntry.class, matcher)).andReturn(0);
        paymentRequestDocument.setGenerateEncumbranceEntries(false);
        paymentRequestDocument.setDebitCreditCodeForGLEntries("D");
        EasyMock.expect(paymentRequestDocument.generateGeneralLedgerPendingEntries(EasyMock.anyObject(), EasyMock.anyObject())).andReturn(true);
        EasyMock.expect(purapAccountingService.generateUseTaxAccount(paymentRequestDocument)).andReturn(new ArrayList<>());
        EasyMock.expect(purapAccountingService.generateSummaryAccountsWithNoZeroTotalsNoUseTax(paymentRequestDocument)).andReturn(new ArrayList<>());
        purapAccountingService.deleteSummaryAccounts(1000, "PREQ");
        EasyMock.expect(businessObjectService.save(new ArrayList<>())).andReturn(new ArrayList<>()).times(2);
        EasyMock.expect(paymentRequestDocument.getPostingYearFromPendingGLEntries()).andReturn(2016);
        EasyMock.expect(paymentRequestDocument.getPostingPeriodCodeFromPendingGLEntries()).andReturn("01");
        purapAccountRevisionService.savePaymentRequestAccountRevisions(EasyMock.anyObject(), EasyMock.anyInt(), EasyMock.anyString());
        EasyMock.expect(paymentRequestDocument.getGeneralLedgerPendingEntries()).andReturn(new ArrayList<>());
        replayAll();

        purapGeneralLedgerService.generateEntriesModifyPaymentRequest(paymentRequestDocument);

        verifyAll();
    }

    private void replayAll() {
        EasyMock.replay(paymentRequestDocument, purapAccountingService, businessObjectService, purapAccountRevisionService);
    }

    private void verifyAll() {
        EasyMock.verify(paymentRequestDocument, purapAccountingService, businessObjectService, purapAccountRevisionService);
    }
}
