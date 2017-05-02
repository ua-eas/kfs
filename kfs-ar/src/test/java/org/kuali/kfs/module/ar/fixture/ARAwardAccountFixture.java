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
package org.kuali.kfs.module.ar.fixture;

import org.kuali.kfs.module.cg.businessobject.AwardAccount;

import java.sql.Date;

/**
 * Fixture class for CG AwardAccount
 */
public enum ARAwardAccountFixture {

    AWD_ACCT_1("11", "BL", "2336320", false, null, null),
    AWD_ACCT_2("11", "IN", "1292016", false, null, null),
    AWD_ACCT_3("11", "BL", "1024697", false, null, null),
    AWD_ACCT_4("11", "BA", "6044901", false, null, null),
    AWD_ACCT_WITH_CCA_1("11", "BL", "1020087", false, null, null),
    AWD_ACCT_WITH_CCA_2("11", "BL", "1021887", false, null, null),
    AWD_ACCT_WITH_CCA_3("11", "BL", "2424704", false, null, null),
    AWD_ACCT_WITH_CCA_4("11", "BL", "0211707", false, null, null);

    private String proposalNumber;
    private String chartOfAccountsCode;
    private String accountNumber;
    private boolean active = true;
    private boolean finalBilledIndicator;
    private Date currentLastBilledDate;
    private Date previousLastBilledDate;


    private ARAwardAccountFixture(String proposalNumber, String chartOfAccountsCode, String accountNumber, boolean finalBilledIndicator, Date currentLastBilledDate, Date previousLastBilledDate) {

        this.proposalNumber = proposalNumber;
        this.chartOfAccountsCode = chartOfAccountsCode;
        this.accountNumber = accountNumber;
        this.finalBilledIndicator = finalBilledIndicator;
        this.currentLastBilledDate = currentLastBilledDate;
        this.previousLastBilledDate = previousLastBilledDate;


    }

    public AwardAccount createAwardAccount() {
        AwardAccount awardAccount = new AwardAccount();

        awardAccount.setProposalNumber(this.proposalNumber);
        awardAccount.setChartOfAccountsCode(this.chartOfAccountsCode);
        awardAccount.setAccountNumber(this.accountNumber);
        awardAccount.setFinalBilledIndicator(this.finalBilledIndicator);
        awardAccount.setCurrentLastBilledDate(this.currentLastBilledDate);
        awardAccount.setPreviousLastBilledDate(this.previousLastBilledDate);
        return awardAccount;
    }
}
