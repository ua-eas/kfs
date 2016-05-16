package org.kuali.kfs.module.ar.businessobject;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.coa.service.impl.MockAccountingPeriodService;
import org.kuali.kfs.module.ar.ArConstants;

import java.sql.Date;

public class BillingPeriodTest {
    protected void verifyBillingPeriodPriorTo(String awardStartDate, String currentDate, String lastBilledDate, String expectedBillingPeriodStart, String expectedBillingPeriodEnd, boolean expectedBillable, ArConstants.BillingFrequencyValues billingFrequency) {
        Date lastBilledDateAsDate = nullSafeDateFromString(lastBilledDate);
        AccountingPeriodService accountingPeriodService = getMockAccountingPeriodService();

        BillingPeriod priorBillingPeriod = BillingPeriod.determineBillingPeriodPriorTo(Date.valueOf(awardStartDate), Date.valueOf(currentDate), lastBilledDateAsDate, billingFrequency, accountingPeriodService);

        Date expectedStartDate = nullSafeDateFromString(expectedBillingPeriodStart);
        Assert.assertEquals("Billing period start wasn't what we thought it was going to be", expectedStartDate, priorBillingPeriod.getStartDate());
        Date expectedEndDate = nullSafeDateFromString(expectedBillingPeriodEnd);
        Assert.assertEquals("Billing period end wasn't what we thought it was going to be", expectedEndDate, priorBillingPeriod.getEndDate());
        Assert.assertEquals("Billing period billable wasn't what we thought it was going to be", expectedBillable, priorBillingPeriod.isBillable());
    }

    protected AccountingPeriodService getMockAccountingPeriodService() {
        return new MockAccountingPeriodService();
    }

    protected Date nullSafeDateFromString(String date) {
        return (StringUtils.isBlank(date)) ? null : Date.valueOf(date);
    }
}
