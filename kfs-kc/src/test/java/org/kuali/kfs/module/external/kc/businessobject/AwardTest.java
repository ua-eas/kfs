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
package org.kuali.kfs.module.external.kc.businessobject;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.persistence.jpa.jpql.Assert;
import org.junit.Test;
import org.kuali.kfs.integration.ar.ArIntegrationConstants;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;

public class AwardTest {

    private AwardAccount awardAccount1 = createAwardAccount("Test-00001");
    private AwardAccount awardAccount2 = createAwardAccount("Test-00002");
    private AwardAccount awardAccount3 = createAwardAccount("Test-00003");

    @Test
    public void testGetActiveAccountsByAccount() {
        Award award = createAward("Test-00002", ArIntegrationConstants.AwardInvoicingOptions.INV_ACCOUNT);
        List<ContractsAndGrantsBillingAwardAccount> activeAwardAccounts = award.getActiveAwardAccounts();

        List<AwardAccount> expectedResults = Arrays.asList(awardAccount2);

        Assert.isTrue(activeAwardAccounts.equals(expectedResults),
                "Expected " + toString(expectedResults) + "; got " + toString(activeAwardAccounts));
    }

    @Test
    public void testGetActiveAccountsByAwardHierarchy1() {
        Award award = createAward("Test-00001", ArIntegrationConstants.AwardInvoicingOptions.INV_AWARD);
        List<ContractsAndGrantsBillingAwardAccount> activeAwardAccounts = award.getActiveAwardAccounts();

        List<AwardAccount> expectedResults = Arrays.asList(awardAccount1, awardAccount2, awardAccount3);

        Assert.isTrue(activeAwardAccounts.equals(expectedResults),
                "Expected " + toString(expectedResults) + "; got " + toString(activeAwardAccounts));
    }

    @Test
    public void testGetActiveAccountsByAwardHierarchy2() {
        Award award = createAward("Test-00002", ArIntegrationConstants.AwardInvoicingOptions.INV_AWARD);
        List<ContractsAndGrantsBillingAwardAccount> activeAwardAccounts = award.getActiveAwardAccounts();

        Assert.isTrue(activeAwardAccounts.isEmpty(),
                "No award accounts should have been returned, instead got " + toString(activeAwardAccounts));
    }

    @Test
    public void testGetActiveAccountsByCCA1() {
        Award award = createAward("Test-00001",
                ArIntegrationConstants.AwardInvoicingOptions.INV_CONTRACT_CONTROL_ACCOUNT);
        List<ContractsAndGrantsBillingAwardAccount> activeAwardAccounts = award.getActiveAwardAccounts();

        List<AwardAccount> expectedResults = Arrays.asList(awardAccount1, awardAccount2, awardAccount3);

        Assert.isTrue(activeAwardAccounts.equals(expectedResults),
                "Expected " + toString(expectedResults) + "; got " + toString(activeAwardAccounts));
    }

    @Test
    public void testGetActiveAccountsByCCA2() {
        Award award = createAward("Test-00002",
                ArIntegrationConstants.AwardInvoicingOptions.INV_CONTRACT_CONTROL_ACCOUNT);
        List<ContractsAndGrantsBillingAwardAccount> activeAwardAccounts = award.getActiveAwardAccounts();

        Assert.isTrue(activeAwardAccounts.isEmpty(),
                "No award accounts should have been returned, instead got " + toString(activeAwardAccounts));
    }

    private Award createAward(String awardNumber, String invoicingOptionCode) {
        Award award = new Award();

        award.setAwardNumber(awardNumber);
        award.setInvoicingOptionCode(invoicingOptionCode);

        award.getAwardAccounts().add(awardAccount1);
        award.getAwardAccounts().add(awardAccount2);
        award.getAwardAccounts().add(awardAccount3);

        return award;
    }

    private AwardAccount createAwardAccount(String awardNumber) {
        AwardAccount awardAccount = new AwardAccount();
        Award award = new Award();
        award.setAwardNumber(awardNumber);
        awardAccount.setAward(award);
        return awardAccount;
    }

    private String toString(List<? extends ContractsAndGrantsBillingAwardAccount> awardAccounts) {
        return awardAccounts.stream().map(a -> ((AwardAccount) a).getAward().getAwardNumber())
                .collect(Collectors.joining(","));
    }
}
