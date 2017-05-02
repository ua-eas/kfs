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
package org.kuali.kfs.module.ar.document.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.service.impl.AccountingPeriodServiceImpl;
import org.kuali.kfs.coa.service.impl.MockAccountingPeriodService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.batch.service.impl.VerifyBillingFrequencyServiceImpl;
import org.kuali.kfs.module.ar.businessobject.BillingFrequency;
import org.kuali.kfs.module.ar.businessobject.BillingPeriod;
import org.kuali.kfs.module.ar.fixture.ARAwardFixture;
import org.kuali.kfs.sys.service.impl.ConfigurableDateTimeServiceImpl;

import java.sql.Date;

public class VerifyBillingFrequencyServiceTest {
    private VerifyBillingFrequencyServiceImpl verifyBillingFrequencyService;

    @Before
    public void setUp() throws Exception {
        verifyBillingFrequencyService = new VerifyBillingFrequencyServiceImpl();
        verifyBillingFrequencyService.setAccountingPeriodService(getMockAccountingPeriodService());
    }

    @Test
    public void testMonthlyNullLastBilledDate() {
        runBillingTest("2011-10-31", "2011-01-01", "2011-09-30", ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_NULL, true);
    }

    @Test
    public void testMilestoneNullLastBilledDate() {
        runBillingTest("2011-10-31", "2011-01-01", "2011-09-30", ARAwardFixture.CG_AWARD_MILESTONE_BILLED_DATE_NULL, true);
    }

    @Test
    public void testPredeterminedBillingNullLastBilledDate() {
        runBillingTest("2011-10-31", "2011-01-01", "2011-09-30", ARAwardFixture.CG_AWARD_PREDETERMINED_BILLED_DATE_NULL, true);
    }

    @Test
    public void testQuarterlyNullLastBilledDate() {
        runBillingTest("2011-10-31", "2011-01-01", "2011-09-30", ARAwardFixture.CG_AWARD_QUAR_BILLED_DATE_NULL, true);
    }

    @Test
    public void testSemiAnnualNullLastBilledDate() {
        runBillingTest("2011-10-31", "2011-01-01", "2011-06-30", ARAwardFixture.CG_AWARD_SEMI_ANN_BILLED_DATE_NULL, true);
    }

    @Test
    public void testAnnualNullLastBilledDate() {
        runBillingTest("2011-10-31", "2011-01-01", "2011-06-30", ARAwardFixture.CG_AWARD_ANNUAL_BILLED_DATE_NULL, true);
    }

    @Test
    public void testMonthValidLastBilledDate() {
        runBillingTest("2011-11-01", "2011-09-01", "2011-10-31", ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_VALID, true);
    }

    @Test
    public void testMilestoneValidLastBilledDate() {
        runBillingTest("2011-11-01", "2011-09-01", "2011-10-31", ARAwardFixture.CG_AWARD_MILESTONE_BILLED_DATE_VALID, true);
    }

    @Test
    public void testPredeterminedValidLastBilledDate() {
        runBillingTest("2011-11-01", "2011-09-01", "2011-10-31", ARAwardFixture.CG_AWARD_PREDETERMINED_BILLED_DATE_VALID, true);
    }

    @Test
    public void testQuarterlyValidLastBilledDate() {
        runBillingTest("2011-11-01", "2011-04-01", "2011-09-30", ARAwardFixture.CG_AWARD_QUAR_BILLED_DATE_VALID, true);
    }

    @Test
    public void testSemiAnnualBillingValidLastBilledDate() {
        runBillingTest("2012-01-01", "2011-01-01", "2011-12-31", ARAwardFixture.CG_AWARD_SEMI_ANN_BILLED_DATE_VALID, true);
    }

    @Test
    public void testAnnualBillingValidLastBilledDate() {
        runBillingTest("2011-07-01", "2009-07-01", "2011-06-30", ARAwardFixture.CG_AWARD_ANNUAL_BILLED_DATE_VALID, true);
    }

    @Test
    public void testLOCBillingNullLastBilledDate() {
        runLOCBillingTest("2011-10-31", "2011-01-01", ARAwardFixture.CG_AWARD_LOCB_BILLED_DATE_NULL);
    }

    @Test
    public void testLOCBillingValidLastBilledDate() {
        runLOCBillingTest("2011-12-15", "2010-12-13", ARAwardFixture.CG_AWARD_LOCB_BILLED_DATE_VALID);
    }

    protected void runLOCBillingTest(String currentDate, String startDate, ARAwardFixture awardFixture) {
        AccountingPeriod currPeriod = getMockAccountingPeriodService().getByDate(Date.valueOf(currentDate));
        ContractsAndGrantsBillingAward award = awardFixture.createAwardWithLastBillingDate();

        ConfigurableDateTimeServiceImpl dateTimeService = new ConfigurableDateTimeServiceImpl();
        dateTimeService.setCurrentDate(Date.valueOf(currentDate));
        verifyBillingFrequencyService.setDateTimeService(dateTimeService);
        BillingPeriod billingPeriod = verifyBillingFrequencyService.getStartDateAndEndDateOfPreviousBillingPeriod(award, currPeriod);
        Assert.assertEquals(Date.valueOf(startDate), billingPeriod.getStartDate());
    }

    protected void runBillingTest(String currentDate, String beginningDate, String endDate, ARAwardFixture awardFixture, boolean expectedWithinGracePeriod) {
        Date date = Date.valueOf(currentDate);
        AccountingPeriod currPeriod = getMockAccountingPeriodService().getByDate(date);
        ContractsAndGrantsBillingAward award = awardFixture.createAwardWithLastBillingDate();

        ConfigurableDateTimeServiceImpl dateTimeService = new ConfigurableDateTimeServiceImpl();
        dateTimeService.setCurrentDate(Date.valueOf(currentDate));
        verifyBillingFrequencyService.setDateTimeService(dateTimeService);
        BillingPeriod billingPeriod = verifyBillingFrequencyService.getStartDateAndEndDateOfPreviousBillingPeriod(award, currPeriod);
        Assert.assertEquals(Date.valueOf(beginningDate), billingPeriod.getStartDate());
        Assert.assertEquals(Date.valueOf(endDate), billingPeriod.getEndDate());

        boolean withinGracePeriod = verifyBillingFrequencyService.calculateIfWithinGracePeriod(date, billingPeriod, award.getLastBilledDate(), (BillingFrequency) award.getBillingFrequency());
        Assert.assertEquals(expectedWithinGracePeriod, withinGracePeriod);
    }

    protected AccountingPeriodServiceImpl getMockAccountingPeriodService() {
        return new MockAccountingPeriodService();
    }
}
