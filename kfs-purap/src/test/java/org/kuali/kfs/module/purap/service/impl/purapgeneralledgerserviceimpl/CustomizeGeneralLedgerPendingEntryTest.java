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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderAccount;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.module.purap.service.impl.PurapGeneralLedgerServiceImpl;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class CustomizeGeneralLedgerPendingEntryTest {
    private PurapGeneralLedgerServiceImpl purapGeneralLedgerService;

    private PurchasingAccountsPayableDocument purapDocument;
    private UniversityDateService universityDateService;
    private ObjectCodeService objectCodeService;
    private PaymentRequestService paymentRequestService;

    @Before
    public void setUp() {
        purapGeneralLedgerService = new PurapGeneralLedgerServiceImpl();

        universityDateService = EasyMock.createMock(UniversityDateService.class);
        purapGeneralLedgerService.setUniversityDateService(universityDateService);
        objectCodeService = EasyMock.createMock(ObjectCodeService.class);
        purapGeneralLedgerService.setObjectCodeService(objectCodeService);
        paymentRequestService = EasyMock.createMock(PaymentRequestService.class);
        purapGeneralLedgerService.setPaymentRequestService(paymentRequestService);

        purapDocument = EasyMock.createMock(PurchasingAccountsPayableDocument.class);
    }

    @Test
    public void testPODocumentSameYearNonEncumbrancePositive() {
        GeneralLedgerPendingEntry explicitEntry = getGeneralLedgerPendingEntry();

        EasyMock.expect(purapDocument.getDocumentNumber()).andReturn("DOC1234");
        EasyMock.expect(purapDocument.getVendorName()).andReturn("ACME Corporation");
        EasyMock.expect(universityDateService.getCurrentUniversityDate()).andReturn(getUniversityDate());
        EasyMock.expect(purapDocument.getPostingYear()).andReturn(2016);
        EasyMock.expect(objectCodeService.getByPrimaryId(2016, "BL", "4166")).andReturn(getObjectCode());

        replayAll();

        purapGeneralLedgerService.customizeGeneralLedgerPendingEntry(purapDocument, getPurchaseOrderAccountingLine(new KualiDecimal("100")), explicitEntry, null, "D", PurapConstants.PurapDocTypeCodes.PO_DOCUMENT, false);

        verifyAll();

        Assert.assertEquals("DOC1234", explicitEntry.getDocumentNumber());
        Assert.assertEquals("ACME Corporation", explicitEntry.getTransactionLedgerEntryDescription());
        Assert.assertEquals(PurapConstants.PURAP_ORIGIN_CODE, explicitEntry.getFinancialSystemOriginationCode());
        Assert.assertEquals(null, explicitEntry.getReferenceFinancialDocumentNumber());
        Assert.assertEquals(null, explicitEntry.getReferenceFinancialDocumentTypeCode());
        Assert.assertEquals(null, explicitEntry.getReferenceFinancialSystemOriginationCode());
        Assert.assertEquals(2016, explicitEntry.getUniversityFiscalYear().intValue());
        Assert.assertEquals("03", explicitEntry.getUniversityFiscalPeriodCode());
        Assert.assertEquals("EX", explicitEntry.getFinancialObjectTypeCode());
        Assert.assertEquals("D", explicitEntry.getTransactionDebitCreditCode());
        Assert.assertEquals("AC", explicitEntry.getFinancialBalanceTypeCode());
    }

    @Test
    public void testPODocumentNextYearNonEncumbrancePositive() {
        GeneralLedgerPendingEntry explicitEntry = getGeneralLedgerPendingEntry();

        EasyMock.expect(purapDocument.getDocumentNumber()).andReturn("DOC1234");
        EasyMock.expect(purapDocument.getVendorName()).andReturn("ACME Corporation");
        EasyMock.expect(universityDateService.getCurrentUniversityDate()).andReturn(getUniversityDate());
        EasyMock.expect(purapDocument.getPostingYear()).andReturn(2017).times(2);
        EasyMock.expect(objectCodeService.getByPrimaryId(2017, "BL", "4166")).andReturn(getObjectCode());

        replayAll();

        purapGeneralLedgerService.customizeGeneralLedgerPendingEntry(purapDocument, getPurchaseOrderAccountingLine(new KualiDecimal("100")), explicitEntry, null, "D", PurapConstants.PurapDocTypeCodes.PO_DOCUMENT, false);

        verifyAll();

        Assert.assertEquals("DOC1234", explicitEntry.getDocumentNumber());
        Assert.assertEquals("ACME Corporation", explicitEntry.getTransactionLedgerEntryDescription());
        Assert.assertEquals(PurapConstants.PURAP_ORIGIN_CODE, explicitEntry.getFinancialSystemOriginationCode());
        Assert.assertEquals(null, explicitEntry.getReferenceFinancialDocumentNumber());
        Assert.assertEquals(null, explicitEntry.getReferenceFinancialDocumentTypeCode());
        Assert.assertEquals(null, explicitEntry.getReferenceFinancialSystemOriginationCode());
        Assert.assertEquals(2017, explicitEntry.getUniversityFiscalYear().intValue());
        Assert.assertEquals("01", explicitEntry.getUniversityFiscalPeriodCode());
        Assert.assertEquals("EX", explicitEntry.getFinancialObjectTypeCode());
        Assert.assertEquals("D", explicitEntry.getTransactionDebitCreditCode());
        Assert.assertEquals("AC", explicitEntry.getFinancialBalanceTypeCode());
    }

    @Test
    public void testPODocumentSameYearNonEncumbranceReferenceDocPositive() {
        GeneralLedgerPendingEntry explicitEntry = getGeneralLedgerPendingEntry();

        EasyMock.expect(purapDocument.getDocumentNumber()).andReturn("DOC1234");
        EasyMock.expect(purapDocument.getVendorName()).andReturn("ACME Corporation");
        EasyMock.expect(universityDateService.getCurrentUniversityDate()).andReturn(getUniversityDate());
        EasyMock.expect(purapDocument.getPostingYear()).andReturn(2016);
        EasyMock.expect(objectCodeService.getByPrimaryId(2016, "BL", "4166")).andReturn(getObjectCode());

        replayAll();

        purapGeneralLedgerService.customizeGeneralLedgerPendingEntry(purapDocument, getPurchaseOrderAccountingLine(new KualiDecimal("100")), explicitEntry, 100, "D", PurapConstants.PurapDocTypeCodes.PO_DOCUMENT, false);

        verifyAll();

        Assert.assertEquals("DOC1234", explicitEntry.getDocumentNumber());
        Assert.assertEquals("ACME Corporation", explicitEntry.getTransactionLedgerEntryDescription());
        Assert.assertEquals(PurapConstants.PURAP_ORIGIN_CODE, explicitEntry.getFinancialSystemOriginationCode());
        Assert.assertEquals("100", explicitEntry.getReferenceFinancialDocumentNumber());
        Assert.assertEquals(PurapConstants.PurapDocTypeCodes.PO_DOCUMENT, explicitEntry.getReferenceFinancialDocumentTypeCode());
        Assert.assertEquals(PurapConstants.PURAP_ORIGIN_CODE, explicitEntry.getReferenceFinancialSystemOriginationCode());
        Assert.assertEquals(2016, explicitEntry.getUniversityFiscalYear().intValue());
        Assert.assertEquals("03", explicitEntry.getUniversityFiscalPeriodCode());
        Assert.assertEquals("EX", explicitEntry.getFinancialObjectTypeCode());
        Assert.assertEquals("D", explicitEntry.getTransactionDebitCreditCode());
        Assert.assertEquals("AC", explicitEntry.getFinancialBalanceTypeCode());
    }

    @Test
    public void testPODocumentSameYearEncumbrancePositive() {
        GeneralLedgerPendingEntry explicitEntry = getGeneralLedgerPendingEntry();

        EasyMock.expect(purapDocument.getDocumentNumber()).andReturn("DOC1234");
        EasyMock.expect(purapDocument.getVendorName()).andReturn("ACME Corporation");
        EasyMock.expect(universityDateService.getCurrentUniversityDate()).andReturn(getUniversityDate());
        EasyMock.expect(purapDocument.getPostingYear()).andReturn(2016);
        EasyMock.expect(objectCodeService.getByPrimaryId(2016, "BL", "4166")).andReturn(getObjectCode());

        replayAll();

        purapGeneralLedgerService.customizeGeneralLedgerPendingEntry(purapDocument, getPurchaseOrderAccountingLine(new KualiDecimal("100")), explicitEntry, null, "D", PurapConstants.PurapDocTypeCodes.PO_DOCUMENT, true);

        verifyAll();

        Assert.assertEquals("DOC1234", explicitEntry.getDocumentNumber());
        Assert.assertEquals("ACME Corporation", explicitEntry.getTransactionLedgerEntryDescription());
        Assert.assertEquals(PurapConstants.PURAP_ORIGIN_CODE, explicitEntry.getFinancialSystemOriginationCode());
        Assert.assertEquals(null, explicitEntry.getReferenceFinancialDocumentNumber());
        Assert.assertEquals(null, explicitEntry.getReferenceFinancialDocumentTypeCode());
        Assert.assertEquals(null, explicitEntry.getReferenceFinancialSystemOriginationCode());
        Assert.assertEquals(2016, explicitEntry.getUniversityFiscalYear().intValue());
        Assert.assertEquals("03", explicitEntry.getUniversityFiscalPeriodCode());
        Assert.assertEquals("EX", explicitEntry.getFinancialObjectTypeCode());
        Assert.assertEquals("D", explicitEntry.getTransactionDebitCreditCode());
        Assert.assertEquals("EX", explicitEntry.getFinancialBalanceTypeCode());
    }

    @Test
    public void testPODocumentSameYearNonEncumbranceNegativeDebit() {
        GeneralLedgerPendingEntry explicitEntry = getGeneralLedgerPendingEntry();

        EasyMock.expect(purapDocument.getDocumentNumber()).andReturn("DOC1234");
        EasyMock.expect(purapDocument.getVendorName()).andReturn("ACME Corporation");
        EasyMock.expect(universityDateService.getCurrentUniversityDate()).andReturn(getUniversityDate());
        EasyMock.expect(purapDocument.getPostingYear()).andReturn(2016);
        EasyMock.expect(objectCodeService.getByPrimaryId(2016, "BL", "4166")).andReturn(getObjectCode());

        replayAll();

        purapGeneralLedgerService.customizeGeneralLedgerPendingEntry(purapDocument, getPurchaseOrderAccountingLine(new KualiDecimal("-100")), explicitEntry, null, "D", PurapConstants.PurapDocTypeCodes.PO_DOCUMENT, false);

        verifyAll();

        Assert.assertEquals("DOC1234", explicitEntry.getDocumentNumber());
        Assert.assertEquals("ACME Corporation", explicitEntry.getTransactionLedgerEntryDescription());
        Assert.assertEquals(PurapConstants.PURAP_ORIGIN_CODE, explicitEntry.getFinancialSystemOriginationCode());
        Assert.assertEquals(null, explicitEntry.getReferenceFinancialDocumentNumber());
        Assert.assertEquals(null, explicitEntry.getReferenceFinancialDocumentTypeCode());
        Assert.assertEquals(null, explicitEntry.getReferenceFinancialSystemOriginationCode());
        Assert.assertEquals(2016, explicitEntry.getUniversityFiscalYear().intValue());
        Assert.assertEquals("03", explicitEntry.getUniversityFiscalPeriodCode());
        Assert.assertEquals("EX", explicitEntry.getFinancialObjectTypeCode());
        Assert.assertEquals("C", explicitEntry.getTransactionDebitCreditCode());
        Assert.assertEquals("AC", explicitEntry.getFinancialBalanceTypeCode());
    }

    @Test
    public void testPODocumentSameYearNonEncumbranceNegativeCredit() {
        GeneralLedgerPendingEntry explicitEntry = getGeneralLedgerPendingEntry();

        EasyMock.expect(purapDocument.getDocumentNumber()).andReturn("DOC1234");
        EasyMock.expect(purapDocument.getVendorName()).andReturn("ACME Corporation");
        EasyMock.expect(universityDateService.getCurrentUniversityDate()).andReturn(getUniversityDate());
        EasyMock.expect(purapDocument.getPostingYear()).andReturn(2016);
        EasyMock.expect(objectCodeService.getByPrimaryId(2016, "BL", "4166")).andReturn(getObjectCode());

        replayAll();

        purapGeneralLedgerService.customizeGeneralLedgerPendingEntry(purapDocument, getPurchaseOrderAccountingLine(new KualiDecimal("-100")), explicitEntry, null, "C", PurapConstants.PurapDocTypeCodes.PO_DOCUMENT, false);

        verifyAll();

        Assert.assertEquals("DOC1234", explicitEntry.getDocumentNumber());
        Assert.assertEquals("ACME Corporation", explicitEntry.getTransactionLedgerEntryDescription());
        Assert.assertEquals(PurapConstants.PURAP_ORIGIN_CODE, explicitEntry.getFinancialSystemOriginationCode());
        Assert.assertEquals(null, explicitEntry.getReferenceFinancialDocumentNumber());
        Assert.assertEquals(null, explicitEntry.getReferenceFinancialDocumentTypeCode());
        Assert.assertEquals(null, explicitEntry.getReferenceFinancialSystemOriginationCode());
        Assert.assertEquals(2016, explicitEntry.getUniversityFiscalYear().intValue());
        Assert.assertEquals("03", explicitEntry.getUniversityFiscalPeriodCode());
        Assert.assertEquals("EX", explicitEntry.getFinancialObjectTypeCode());
        Assert.assertEquals("D", explicitEntry.getTransactionDebitCreditCode());
        Assert.assertEquals("AC", explicitEntry.getFinancialBalanceTypeCode());
    }

    @Test
    public void testInvalidDocType() {
        GeneralLedgerPendingEntry explicitEntry = getGeneralLedgerPendingEntry();

        EasyMock.expect(purapDocument.getDocumentNumber()).andReturn("DOC1234").times(2);
        EasyMock.expect(purapDocument.getVendorName()).andReturn("ACME Corporation");
        EasyMock.expect(universityDateService.getCurrentUniversityDate()).andReturn(getUniversityDate());

        replayAll();

        try {
            purapGeneralLedgerService.customizeGeneralLedgerPendingEntry(purapDocument, getPurchaseOrderAccountingLine(new KualiDecimal("100")), explicitEntry, null, "D", "BAD", true);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // Expected exception
        }
    }

    @Test
    public void testPREQDocumentSameYearNonEncumbrancePositiveNoBackpost() {
        purapDocument = EasyMock.createMock(PaymentRequestDocument.class);

        GeneralLedgerPendingEntry explicitEntry = getGeneralLedgerPendingEntry();

        EasyMock.expect(purapDocument.getDocumentNumber()).andReturn("DOC1234");
        EasyMock.expect(purapDocument.getVendorName()).andReturn("ACME Corporation");
        EasyMock.expect(universityDateService.getCurrentUniversityDate()).andReturn(getUniversityDate());
        EasyMock.expect(objectCodeService.getByPrimaryId(2016, "BL", "4166")).andReturn(getObjectCode());
        EasyMock.expect(paymentRequestService.allowBackpost(EasyMock.anyObject())).andReturn(false);
        EasyMock.expect(((PaymentRequestDocument) purapDocument).getAlternateVendorHeaderGeneratedIdentifier()).andReturn(null);

        replayAll();

        purapGeneralLedgerService.customizeGeneralLedgerPendingEntry(purapDocument, getPurchaseOrderAccountingLine(new KualiDecimal("100")), explicitEntry, null, "D", PurapConstants.PurapDocTypeCodes.PAYMENT_REQUEST_DOCUMENT, false);

        verifyAll();

        Assert.assertEquals("DOC1234", explicitEntry.getDocumentNumber());
        Assert.assertEquals("ACME Corporation", explicitEntry.getTransactionLedgerEntryDescription());
        Assert.assertEquals(PurapConstants.PURAP_ORIGIN_CODE, explicitEntry.getFinancialSystemOriginationCode());
        Assert.assertEquals(null, explicitEntry.getReferenceFinancialDocumentNumber());
        Assert.assertEquals(null, explicitEntry.getReferenceFinancialDocumentTypeCode());
        Assert.assertEquals(null, explicitEntry.getReferenceFinancialSystemOriginationCode());
        Assert.assertEquals(2016, explicitEntry.getUniversityFiscalYear().intValue());
        Assert.assertEquals("03", explicitEntry.getUniversityFiscalPeriodCode());
        Assert.assertEquals("EX", explicitEntry.getFinancialObjectTypeCode());
        Assert.assertEquals("D", explicitEntry.getTransactionDebitCreditCode());
        Assert.assertEquals("AC", explicitEntry.getFinancialBalanceTypeCode());
    }

    @Test
    public void testPREQDocumentSameYearNonEncumbrancePositiveBackpost() {
        purapDocument = EasyMock.createMock(PaymentRequestDocument.class);

        GeneralLedgerPendingEntry explicitEntry = getGeneralLedgerPendingEntry();

        EasyMock.expect(purapDocument.getDocumentNumber()).andReturn("DOC1234");
        EasyMock.expect(purapDocument.getVendorName()).andReturn("ACME Corporation");
        EasyMock.expect(universityDateService.getCurrentUniversityDate()).andReturn(getUniversityDate());
        EasyMock.expect(objectCodeService.getByPrimaryId(2015, "BL", "4166")).andReturn(getObjectCode());
        EasyMock.expect(paymentRequestService.allowBackpost(EasyMock.anyObject())).andReturn(true);
        EasyMock.expect(((PaymentRequestDocument) purapDocument).getAlternateVendorHeaderGeneratedIdentifier()).andReturn(null);

        replayAll();

        purapGeneralLedgerService.customizeGeneralLedgerPendingEntry(purapDocument, getPurchaseOrderAccountingLine(new KualiDecimal("100")), explicitEntry, null, "D", PurapConstants.PurapDocTypeCodes.PAYMENT_REQUEST_DOCUMENT, false);

        verifyAll();

        Assert.assertEquals("DOC1234", explicitEntry.getDocumentNumber());
        Assert.assertEquals("ACME Corporation", explicitEntry.getTransactionLedgerEntryDescription());
        Assert.assertEquals(PurapConstants.PURAP_ORIGIN_CODE, explicitEntry.getFinancialSystemOriginationCode());
        Assert.assertEquals(null, explicitEntry.getReferenceFinancialDocumentNumber());
        Assert.assertEquals(null, explicitEntry.getReferenceFinancialDocumentTypeCode());
        Assert.assertEquals(null, explicitEntry.getReferenceFinancialSystemOriginationCode());
        Assert.assertEquals(2015, explicitEntry.getUniversityFiscalYear().intValue());
        Assert.assertEquals("12", explicitEntry.getUniversityFiscalPeriodCode());
        Assert.assertEquals("EX", explicitEntry.getFinancialObjectTypeCode());
        Assert.assertEquals("D", explicitEntry.getTransactionDebitCreditCode());
        Assert.assertEquals("AC", explicitEntry.getFinancialBalanceTypeCode());
    }

    @Test
    public void testPREQDocumentSameYearNonEncumbrancePositiveAlternativeVendor() {
        PurchaseOrderDocument purchaseOrderDocument = EasyMock.createMock(PurchaseOrderDocument.class);
        purapDocument = EasyMock.createMock(PaymentRequestDocument.class);

        GeneralLedgerPendingEntry explicitEntry = getGeneralLedgerPendingEntry();

        EasyMock.expect(purapDocument.getDocumentNumber()).andReturn("DOC1234");
        EasyMock.expect(purapDocument.getVendorName()).andReturn("ACME Corporation");
        EasyMock.expect(universityDateService.getCurrentUniversityDate()).andReturn(getUniversityDate());
        EasyMock.expect(objectCodeService.getByPrimaryId(2015, "BL", "4166")).andReturn(getObjectCode());
        EasyMock.expect(paymentRequestService.allowBackpost(EasyMock.anyObject())).andReturn(true);
        EasyMock.expect(((PaymentRequestDocument) purapDocument).getAlternateVendorHeaderGeneratedIdentifier()).andReturn(1).times(2);
        EasyMock.expect(((PaymentRequestDocument) purapDocument).getAlternateVendorDetailAssignedIdentifier()).andReturn(1).times(2);
        EasyMock.expect(purapDocument.getVendorHeaderGeneratedIdentifier()).andReturn(1);
        EasyMock.expect(purapDocument.getVendorDetailAssignedIdentifier()).andReturn(1);
        EasyMock.expect(((PaymentRequestDocument) purapDocument).getPurchaseOrderDocument()).andReturn(purchaseOrderDocument);
        EasyMock.expect(purchaseOrderDocument.getAlternateVendorName()).andReturn("HP Corporation");

        replayAll();
        EasyMock.replay(purchaseOrderDocument);

        purapGeneralLedgerService.customizeGeneralLedgerPendingEntry(purapDocument, getPurchaseOrderAccountingLine(new KualiDecimal("100")), explicitEntry, null, "D", PurapConstants.PurapDocTypeCodes.PAYMENT_REQUEST_DOCUMENT, false);

        verifyAll();
        EasyMock.verify(purchaseOrderDocument);

        Assert.assertEquals("DOC1234", explicitEntry.getDocumentNumber());
        Assert.assertEquals("HP Corporation", explicitEntry.getTransactionLedgerEntryDescription());
        Assert.assertEquals(PurapConstants.PURAP_ORIGIN_CODE, explicitEntry.getFinancialSystemOriginationCode());
        Assert.assertEquals(null, explicitEntry.getReferenceFinancialDocumentNumber());
        Assert.assertEquals(null, explicitEntry.getReferenceFinancialDocumentTypeCode());
        Assert.assertEquals(null, explicitEntry.getReferenceFinancialSystemOriginationCode());
        Assert.assertEquals(2015, explicitEntry.getUniversityFiscalYear().intValue());
        Assert.assertEquals("12", explicitEntry.getUniversityFiscalPeriodCode());
        Assert.assertEquals("EX", explicitEntry.getFinancialObjectTypeCode());
        Assert.assertEquals("D", explicitEntry.getTransactionDebitCreditCode());
        Assert.assertEquals("AC", explicitEntry.getFinancialBalanceTypeCode());
    }

    @Test
    public void testCMDocumentSameYearNonEncumbrancePositiveNotAlternativeVendor() {
        PurchaseOrderDocument purchaseOrderDocument = EasyMock.createMock(PurchaseOrderDocument.class);
        purapDocument = EasyMock.createMock(VendorCreditMemoDocument.class);

        GeneralLedgerPendingEntry explicitEntry = getGeneralLedgerPendingEntry();

        EasyMock.expect(purapDocument.getDocumentNumber()).andReturn("DOC1234");
        EasyMock.expect(purapDocument.getVendorName()).andReturn("ACME Corporation");
        EasyMock.expect(universityDateService.getCurrentUniversityDate()).andReturn(getUniversityDate());
        EasyMock.expect(objectCodeService.getByPrimaryId(2016, "BL", "4166")).andReturn(getObjectCode());
        EasyMock.expect(((VendorCreditMemoDocument) purapDocument).isSourceDocumentPaymentRequest()).andReturn(false);

        replayAll();
        EasyMock.replay(purchaseOrderDocument);

        purapGeneralLedgerService.customizeGeneralLedgerPendingEntry(purapDocument, getPurchaseOrderAccountingLine(new KualiDecimal("100")), explicitEntry, null, "D", PurapConstants.PurapDocTypeCodes.CREDIT_MEMO_DOCUMENT, false);

        verifyAll();
        EasyMock.verify(purchaseOrderDocument);

        Assert.assertEquals("DOC1234", explicitEntry.getDocumentNumber());
        Assert.assertEquals("ACME Corporation", explicitEntry.getTransactionLedgerEntryDescription());
        Assert.assertEquals(PurapConstants.PURAP_ORIGIN_CODE, explicitEntry.getFinancialSystemOriginationCode());
        Assert.assertEquals(null, explicitEntry.getReferenceFinancialDocumentNumber());
        Assert.assertEquals(null, explicitEntry.getReferenceFinancialDocumentTypeCode());
        Assert.assertEquals(null, explicitEntry.getReferenceFinancialSystemOriginationCode());
        Assert.assertEquals(2016, explicitEntry.getUniversityFiscalYear().intValue());
        Assert.assertEquals("03", explicitEntry.getUniversityFiscalPeriodCode());
        Assert.assertEquals("EX", explicitEntry.getFinancialObjectTypeCode());
        Assert.assertEquals("D", explicitEntry.getTransactionDebitCreditCode());
        Assert.assertEquals("AC", explicitEntry.getFinancialBalanceTypeCode());
    }

    @Test
    public void testCMDocumentSameYearNonEncumbrancePositiveAlternativeVendor() {
        PurchaseOrderDocument purchaseOrderDocument = EasyMock.createMock(PurchaseOrderDocument.class);
        PaymentRequestDocument paymentRequestDocument = EasyMock.createMock(PaymentRequestDocument.class);
        purapDocument = EasyMock.createMock(VendorCreditMemoDocument.class);

        GeneralLedgerPendingEntry explicitEntry = getGeneralLedgerPendingEntry();

        EasyMock.expect(purapDocument.getDocumentNumber()).andReturn("DOC1234");
        EasyMock.expect(purapDocument.getVendorName()).andReturn("ACME Corporation");
        EasyMock.expect(universityDateService.getCurrentUniversityDate()).andReturn(getUniversityDate());
        EasyMock.expect(objectCodeService.getByPrimaryId(2016, "BL", "4166")).andReturn(getObjectCode());
        EasyMock.expect(((VendorCreditMemoDocument) purapDocument).isSourceDocumentPaymentRequest()).andReturn(true);
        EasyMock.expect(((VendorCreditMemoDocument) purapDocument).getPaymentRequestDocument()).andReturn(paymentRequestDocument);
        EasyMock.expect(paymentRequestDocument.getAlternateVendorHeaderGeneratedIdentifier()).andReturn(111).times(2);
        EasyMock.expect(paymentRequestDocument.getAlternateVendorDetailAssignedIdentifier()).andReturn(222).times(2);
        EasyMock.expect(paymentRequestDocument.getVendorHeaderGeneratedIdentifier()).andReturn(111);
        EasyMock.expect(paymentRequestDocument.getVendorDetailAssignedIdentifier()).andReturn(222);
        EasyMock.expect(((VendorCreditMemoDocument) purapDocument).getPurchaseOrderDocument()).andReturn(purchaseOrderDocument);
        EasyMock.expect(purchaseOrderDocument.getAlternateVendorName()).andReturn("HP Corporation");

        replayAll();
        EasyMock.replay(purchaseOrderDocument);
        EasyMock.replay(paymentRequestDocument);

        purapGeneralLedgerService.customizeGeneralLedgerPendingEntry(purapDocument, getPurchaseOrderAccountingLine(new KualiDecimal("100")), explicitEntry, null, "D", PurapConstants.PurapDocTypeCodes.CREDIT_MEMO_DOCUMENT, false);

        verifyAll();
        EasyMock.verify(purchaseOrderDocument);
        EasyMock.verify(paymentRequestDocument);

        Assert.assertEquals("DOC1234", explicitEntry.getDocumentNumber());
        Assert.assertEquals("HP Corporation", explicitEntry.getTransactionLedgerEntryDescription());
        Assert.assertEquals(PurapConstants.PURAP_ORIGIN_CODE, explicitEntry.getFinancialSystemOriginationCode());
        Assert.assertEquals(null, explicitEntry.getReferenceFinancialDocumentNumber());
        Assert.assertEquals(null, explicitEntry.getReferenceFinancialDocumentTypeCode());
        Assert.assertEquals(null, explicitEntry.getReferenceFinancialSystemOriginationCode());
        Assert.assertEquals(2016, explicitEntry.getUniversityFiscalYear().intValue());
        Assert.assertEquals("03", explicitEntry.getUniversityFiscalPeriodCode());
        Assert.assertEquals("EX", explicitEntry.getFinancialObjectTypeCode());
        Assert.assertEquals("D", explicitEntry.getTransactionDebitCreditCode());
        Assert.assertEquals("AC", explicitEntry.getFinancialBalanceTypeCode());
    }

    private void replayAll() {
        EasyMock.replay(universityDateService, purapDocument, objectCodeService, paymentRequestService);
    }

    private void verifyAll() {
        EasyMock.verify(universityDateService, purapDocument, objectCodeService, paymentRequestService);
    }

    private GeneralLedgerPendingEntry getGeneralLedgerPendingEntry() {
        GeneralLedgerPendingEntry g = new GeneralLedgerPendingEntry();
        g.setUniversityFiscalYear(2016);
        g.setChartOfAccountsCode("BL");
        g.setAccountNumber("7654321");
        g.setFinancialObjectCode("4166");
        g.setFinancialSubObjectCode("321");
        g.setFinancialBalanceTypeCode("AC");
        return g;
    }

    private UniversityDate getUniversityDate() {
        UniversityDate d = new UniversityDate();
        d.setUniversityFiscalYear(2016);
        d.setUniversityFiscalAccountingPeriod("03");
        return d;
    }

    private ObjectCode getObjectCode() {
        ObjectCode o = new ObjectCode();
        o.setUniversityFiscalYear(2016);
        o.setChartOfAccountsCode("BL");
        o.setFinancialObjectCode("4166");
        o.setFinancialObjectTypeCode("EX");
        return o;
    }

    private AccountingLine getPurchaseOrderAccountingLine(KualiDecimal amount) {
        PurchaseOrderAccount p = new PurchaseOrderAccount();
        p.setAmount(amount);
        return p;
    }
}
