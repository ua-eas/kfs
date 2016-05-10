package org.kuali.kfs.module.ar;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.businessobject.InvoiceGeneralDetail;

public class BillingFrequencyEnumTest {
    @Test
    public void testMilestoneAward() {
        ContractsAndGrantsBillingAward award = EasyMock.createMock(ContractsAndGrantsBillingAward.class);
        EasyMock.expect(award.getBillingFrequencyCode()).andReturn(ArConstants.BillingFrequencyValues.MILESTONE.getCode()).anyTimes();
        EasyMock.replay(award);

        Assert.assertTrue("ArConstants.BillingFrequency.isMilestone should be true of awards with milestone", ArConstants.BillingFrequencyValues.isMilestone(award));
        Assert.assertFalse("ArConstants.BillingFrequency.isPredeterminedBilling should not be true of awards with milestones", ArConstants.BillingFrequencyValues.isPredeterminedBilling(award));
        Assert.assertFalse("ArConstants.BillingFrequency.isLetterOfCredit should not be true of awards with milestones", ArConstants.BillingFrequencyValues.isLetterOfCredit(award));
    }

    @Test
    public void testPredeterminedBillingAward() {
        ContractsAndGrantsBillingAward award = EasyMock.createMock(ContractsAndGrantsBillingAward.class);
        EasyMock.expect(award.getBillingFrequencyCode()).andReturn(ArConstants.BillingFrequencyValues.PREDETERMINED_BILLING.getCode()).anyTimes();
        EasyMock.replay(award);

        Assert.assertTrue("ArConstants.BillingFrequency.isPredeterminedBilling should be true of awards with predetermined billing", ArConstants.BillingFrequencyValues.isPredeterminedBilling(award));
        Assert.assertFalse("ArConstants.BillingFrequency.isMileston should not be true of awards with predetermined billing", ArConstants.BillingFrequencyValues.isMilestone(award));
        Assert.assertFalse("ArConstants.BillingFrequency.isLetterOfCredit should not be true of awards with predetermined billing", ArConstants.BillingFrequencyValues.isLetterOfCredit(award));
    }

    @Test
    public void testLetterOfCreditAward() {
        ContractsAndGrantsBillingAward award = EasyMock.createMock(ContractsAndGrantsBillingAward.class);
        EasyMock.expect(award.getBillingFrequencyCode()).andReturn(ArConstants.BillingFrequencyValues.LETTER_OF_CREDIT.getCode()).anyTimes();
        EasyMock.replay(award);

        Assert.assertTrue("ArConstants.BillingFrequency.isLetterOfCredit should be true of awards with letter of credit", ArConstants.BillingFrequencyValues.isLetterOfCredit(award));
        Assert.assertFalse("ArConstants.BillingFrequency.isMileston should not be true of awards with letter of credit", ArConstants.BillingFrequencyValues.isMilestone(award));
        Assert.assertFalse("ArConstants.BillingFrequency.isPredeterminedBilling should not be true of awards with letter of credit", ArConstants.BillingFrequencyValues.isPredeterminedBilling(award));
    }

    @Test
    public void testMilestoneInvoiceGeneralDetail() {
        InvoiceGeneralDetail invoiceDetail = EasyMock.createMock(InvoiceGeneralDetail.class);
        EasyMock.expect(invoiceDetail.getBillingFrequencyCode()).andReturn(ArConstants.BillingFrequencyValues.MILESTONE.getCode()).anyTimes();
        EasyMock.replay(invoiceDetail);

        Assert.assertTrue("ArConstants.BillingFrequency.isMilestone should be true of invoice general details with milestone", ArConstants.BillingFrequencyValues.isMilestone(invoiceDetail));
        Assert.assertFalse("ArConstants.BillingFrequency.isPredeterminedBilling should not be true of invoice general details with milestones", ArConstants.BillingFrequencyValues.isPredeterminedBilling(invoiceDetail));
        Assert.assertFalse("ArConstants.BillingFrequency.isLetterOfCredit should not be true of invoice general details with milestones", ArConstants.BillingFrequencyValues.isLetterOfCredit(invoiceDetail));
        Assert.assertFalse("ArConstants.BillingFrequency.isTimeBased should not be true of invoice general details with milestones", ArConstants.BillingFrequencyValues.isTimeBased(invoiceDetail));
    }

    @Test
    public void testPredeterminedBillingInvoiceGeneralDetail() {
        InvoiceGeneralDetail invoiceDetail = EasyMock.createMock(InvoiceGeneralDetail.class);
        EasyMock.expect(invoiceDetail.getBillingFrequencyCode()).andReturn(ArConstants.BillingFrequencyValues.PREDETERMINED_BILLING.getCode()).anyTimes();
        EasyMock.replay(invoiceDetail);

        Assert.assertTrue("ArConstants.BillingFrequency.isPredeterminedBilling should be true of invoice general details with predetermined billing", ArConstants.BillingFrequencyValues.isPredeterminedBilling(invoiceDetail));
        Assert.assertFalse("ArConstants.BillingFrequency.isMilestone should not be true of invoice general details with predetermined billing", ArConstants.BillingFrequencyValues.isMilestone(invoiceDetail));
        Assert.assertFalse("ArConstants.BillingFrequency.isLetterOfCredit should not be true of invoice general details with predetermined billing", ArConstants.BillingFrequencyValues.isLetterOfCredit(invoiceDetail));
        Assert.assertFalse("ArConstants.BillingFrequency.isTimeBased should not be true of invoice general details with predetermined billing", ArConstants.BillingFrequencyValues.isTimeBased(invoiceDetail));
    }

    @Test
    public void testLetterOfCreditInvoiceGeneralDetail() {
        InvoiceGeneralDetail invoiceDetail = EasyMock.createMock(InvoiceGeneralDetail.class);
        EasyMock.expect(invoiceDetail.getBillingFrequencyCode()).andReturn(ArConstants.BillingFrequencyValues.LETTER_OF_CREDIT.getCode()).anyTimes();
        EasyMock.replay(invoiceDetail);

        Assert.assertFalse("ArConstants.BillingFrequency.isPredeterminedBilling should not be true of invoice general details with letter of credit billing", ArConstants.BillingFrequencyValues.isPredeterminedBilling(invoiceDetail));
        Assert.assertFalse("ArConstants.BillingFrequency.isMilestone should not be true of invoice general details with letter of credit billing", ArConstants.BillingFrequencyValues.isMilestone(invoiceDetail));
        Assert.assertTrue("ArConstants.BillingFrequency.isLetterOfCredit should be true of invoice general details with letter of credit billing", ArConstants.BillingFrequencyValues.isLetterOfCredit(invoiceDetail));
        Assert.assertFalse("ArConstants.BillingFrequency.isTimeBased should not be true of invoice general details with letter of credit billing", ArConstants.BillingFrequencyValues.isTimeBased(invoiceDetail));
    }

    @Test
    public void testAnnuallyInvoiceGeneralDetail() {
        InvoiceGeneralDetail invoiceDetail = EasyMock.createMock(InvoiceGeneralDetail.class);
        EasyMock.expect(invoiceDetail.getBillingFrequencyCode()).andReturn(ArConstants.BillingFrequencyValues.ANNUALLY.getCode()).anyTimes();
        EasyMock.replay(invoiceDetail);

        Assert.assertFalse("ArConstants.BillingFrequency.isPredeterminedBilling should be not true of invoice general details with annual billing", ArConstants.BillingFrequencyValues.isPredeterminedBilling(invoiceDetail));
        Assert.assertFalse("ArConstants.BillingFrequency.isMilestone should not be true of invoice general details with annual billing", ArConstants.BillingFrequencyValues.isMilestone(invoiceDetail));
        Assert.assertFalse("ArConstants.BillingFrequency.isLetterOfCredit should not be true of invoice general details with annual billing", ArConstants.BillingFrequencyValues.isLetterOfCredit(invoiceDetail));
        Assert.assertTrue("ArConstants.BillingFrequency.isTimeBased should be true of invoice general details with annual billing", ArConstants.BillingFrequencyValues.isTimeBased(invoiceDetail));
    }

    @Test
    public void testSemiAnnuallyInvoiceGeneralDetail() {
        InvoiceGeneralDetail invoiceDetail = EasyMock.createMock(InvoiceGeneralDetail.class);
        EasyMock.expect(invoiceDetail.getBillingFrequencyCode()).andReturn(ArConstants.BillingFrequencyValues.SEMI_ANNUALLY.getCode()).anyTimes();
        EasyMock.replay(invoiceDetail);

        Assert.assertFalse("ArConstants.BillingFrequency.isPredeterminedBilling should not be true of invoice general details with semi-annual billing", ArConstants.BillingFrequencyValues.isPredeterminedBilling(invoiceDetail));
        Assert.assertFalse("ArConstants.BillingFrequency.isMilestone should not be true of invoice general details with semi-annual billing", ArConstants.BillingFrequencyValues.isMilestone(invoiceDetail));
        Assert.assertFalse("ArConstants.BillingFrequency.isLetterOfCredit should not be true of invoice general details with semi-annual billing", ArConstants.BillingFrequencyValues.isLetterOfCredit(invoiceDetail));
        Assert.assertTrue("ArConstants.BillingFrequency.isTimeBased should be true of invoice general details with semi-annual billing", ArConstants.BillingFrequencyValues.isTimeBased(invoiceDetail));
    }

    @Test
    public void testQuarterlyInvoiceGeneralDetail() {
        InvoiceGeneralDetail invoiceDetail = EasyMock.createMock(InvoiceGeneralDetail.class);
        EasyMock.expect(invoiceDetail.getBillingFrequencyCode()).andReturn(ArConstants.BillingFrequencyValues.QUARTERLY.getCode()).anyTimes();
        EasyMock.replay(invoiceDetail);

        Assert.assertFalse("ArConstants.BillingFrequency.isPredeterminedBilling should not be true of invoice general details with quarterly billing", ArConstants.BillingFrequencyValues.isPredeterminedBilling(invoiceDetail));
        Assert.assertFalse("ArConstants.BillingFrequency.isMilestone should not be true of invoice general details with quarterly billing", ArConstants.BillingFrequencyValues.isMilestone(invoiceDetail));
        Assert.assertFalse("ArConstants.BillingFrequency.isLetterOfCredit should not be true of invoice general details with quarterly billing", ArConstants.BillingFrequencyValues.isLetterOfCredit(invoiceDetail));
        Assert.assertTrue("ArConstants.BillingFrequency.isTimeBased should be true of invoice general details with quarterly billing", ArConstants.BillingFrequencyValues.isTimeBased(invoiceDetail));
    }

    @Test
    public void testMonthlyInvoiceGeneralDetail() {
        InvoiceGeneralDetail invoiceDetail = EasyMock.createMock(InvoiceGeneralDetail.class);
        EasyMock.expect(invoiceDetail.getBillingFrequencyCode()).andReturn(ArConstants.BillingFrequencyValues.MONTHLY.getCode()).anyTimes();
        EasyMock.replay(invoiceDetail);

        Assert.assertFalse("ArConstants.BillingFrequency.isPredeterminedBilling should not be true of invoice general details with monthly billing", ArConstants.BillingFrequencyValues.isPredeterminedBilling(invoiceDetail));
        Assert.assertFalse("ArConstants.BillingFrequency.isMilestone should not be true of invoice general details with monthly billing", ArConstants.BillingFrequencyValues.isMilestone(invoiceDetail));
        Assert.assertFalse("ArConstants.BillingFrequency.isLetterOfCredit should not be true of invoice general details with monthly billing", ArConstants.BillingFrequencyValues.isLetterOfCredit(invoiceDetail));
        Assert.assertTrue("ArConstants.BillingFrequency.isTimeBased should be true of invoice general details with monthly billing", ArConstants.BillingFrequencyValues.isTimeBased(invoiceDetail));
    }
}
