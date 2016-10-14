/*
The Kuali Financial System, a comprehensive financial management system for higher education.

Copyright 2005-2016 The Kuali Foundation

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ar.service.impl.ContractsGrantsInvoiceCreateDocumentService;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.easymock.EasyMock;
import org.joda.time.DateTimeUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.batch.service.VerifyBillingFrequencyService;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceDocumentErrorLog;
import org.kuali.kfs.module.ar.businessobject.InvoiceAccountDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsBillingAwardVerificationService;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.fixture.ARAwardAccountFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardFundManagerFixture;
import org.kuali.kfs.module.ar.fixture.BillingFrequencyFixture;
import org.kuali.kfs.module.ar.service.impl.ContractsGrantsInvoiceCreateDocumentServiceImpl;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.AwardAccount;
import org.kuali.kfs.module.cg.businessobject.AwardFundManager;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * This class tests all the validations performed by the PerformAwardValidation
 * method. But since that method is protected, we do it through validateAwards.
 */
public class PerformValidationTest {

    private ContractsGrantsInvoiceCreateDocumentServiceImpl contractsGrantsInvoiceCreateDocumentService;
    private ContractsGrantsBillingAwardVerificationService contractsGrantsBillingAwardVerificationService;
    private ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    private VerifyBillingFrequencyService verifyBillingFrequencyService;
    private FinancialSystemDocumentService financialSystemDocumentService;
    private BusinessObjectService businessObjectService;
    private ConfigurationService configurationService;
    private OptionsService optionsService;
    private DateTimeService dateTimeService;
    private AccountService accountService;

    private Set<String> pendingDocumentStatuses = new HashSet<>(Arrays.asList("I", "S", "R", "E"));
    private Set<ContractsAndGrantsBillingAward> awardsInError;

    private boolean validBillingFrequency;
    private boolean correctBillingFrequency;
    private boolean alreadyBuilt;
    private boolean hasMilestones;
    private boolean validBills;
    private boolean customerRecord;
    private boolean chartOrgSetUp;
    private boolean invoiceInProgress;

    private List<String> ccaErrors;
    private List<ContractsGrantsInvoiceDocument> pendingDocuments;
    private Collection<Account> expiredAccounts;

    @Before
    public void setup() {
        contractsGrantsInvoiceCreateDocumentService = new ContractsGrantsInvoiceCreateDocumentServiceImpl();

        contractsGrantsBillingAwardVerificationService = EasyMock
                .createMock(ContractsGrantsBillingAwardVerificationService.class);
        contractsGrantsInvoiceDocumentService = EasyMock.createMock(ContractsGrantsInvoiceDocumentService.class);
        verifyBillingFrequencyService = EasyMock.createMock(VerifyBillingFrequencyService.class);
        financialSystemDocumentService = EasyMock.createMock(FinancialSystemDocumentService.class);
        businessObjectService = EasyMock.createMock(BusinessObjectService.class);
        configurationService = EasyMock.createMock(ConfigurationService.class);
        optionsService = EasyMock.createMock(OptionsService.class);
        dateTimeService = EasyMock.createMock(DateTimeService.class);
        accountService = EasyMock.createMock(AccountService.class);

        contractsGrantsInvoiceCreateDocumentService
                .setContractsGrantsBillingAwardVerificationService(contractsGrantsBillingAwardVerificationService);
        contractsGrantsInvoiceCreateDocumentService
                .setContractsGrantsInvoiceDocumentService(contractsGrantsInvoiceDocumentService);
        contractsGrantsInvoiceCreateDocumentService.setVerifyBillingFrequencyService(verifyBillingFrequencyService);
        contractsGrantsInvoiceCreateDocumentService.setFinancialSystemDocumentService(financialSystemDocumentService);
        contractsGrantsInvoiceCreateDocumentService.setBusinessObjectService(businessObjectService);
        contractsGrantsInvoiceCreateDocumentService.setConfigurationService(configurationService);
        contractsGrantsInvoiceCreateDocumentService.setOptionsService(optionsService);
        contractsGrantsInvoiceCreateDocumentService.setDateTimeService(dateTimeService);
        contractsGrantsInvoiceCreateDocumentService.setAccountService(accountService);

        validBillingFrequency = true;
        correctBillingFrequency = true;
        alreadyBuilt = false;
        hasMilestones = true;
        expiredAccounts = null;
        validBills = true;
        customerRecord = true;
        chartOrgSetUp = true;
        invoiceInProgress = false;

        ccaErrors = new ArrayList<>();
        pendingDocuments = new ArrayList<>();
        awardsInError = new HashSet<>();
    }

    @Test
    public void testPerformAwardValidationStartDateMissing() {
        List<ContractsAndGrantsBillingAward> awards = new ArrayList<ContractsAndGrantsBillingAward>();
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD1.createAward();
        ((Award) award).setAwardBeginningDate(null);
        awards.add(award);
        prepareOtherMocks();
        expectError(award, ArKeyConstants.CGINVOICE_CREATION_AWARD_START_DATE_MISSING_ERROR);
        replayMocks();
        Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();

        Collection<ContractsAndGrantsBillingAward> qualifiedAwards = contractsGrantsInvoiceCreateDocumentService
                .validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null,
                        ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode());

        verifyMocks();
        Assert.assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
    }

    @Test
    public void testPerformAwardValidationInvalidBillingFrequency() {
        List<ContractsAndGrantsBillingAward> awards = new ArrayList<ContractsAndGrantsBillingAward>();
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD1.createAward();
        awards.add(award);
        prepareOtherMocks();
        expectError(award, ArKeyConstants.CGINVOICE_CREATION_BILLING_FREQUENCY_MISSING_ERROR);
        replayMocks();
        Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();

        Collection<ContractsAndGrantsBillingAward> qualifiedAwards = contractsGrantsInvoiceCreateDocumentService
                .validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null,
                        ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode());

        verifyMocks();
        Assert.assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
    }

    @Test
    public void testPerformAwardValidationInvalidBillingPeriod() {
        List<ContractsAndGrantsBillingAward> awards = new ArrayList<ContractsAndGrantsBillingAward>();
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD_MONTHLY_INVALID_DATES.createAward();
        ((Award) award).setAwardBeginningDate(new Date(DateTimeUtils.currentTimeMillis()));
        awards.add(award);
        validBillingFrequency = false;
        prepareMocks(award);
        prepareOtherMocks();
        expectError(award, ArKeyConstants.CGINVOICE_CREATION_AWARD_INVALID_BILLING_PERIOD);
        replayMocks();
        Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();

        Collection<ContractsAndGrantsBillingAward> qualifiedAwards = contractsGrantsInvoiceCreateDocumentService
                .validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null,
                        ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode());

        verifyMocks();
        Assert.assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
    }

    @Test
    public void testPerformAwardValidationValidAward() {
        List<ContractsAndGrantsBillingAward> awards = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1);
        prepareMocks(awards);
        prepareOtherMocks();
        replayMocks();
        Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();

        Collection<ContractsAndGrantsBillingAward> qualifiedAwards = contractsGrantsInvoiceCreateDocumentService
                .validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null,
                        ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode());

        verifyMocks();
        Assert.assertTrue("qualifiedAwards should  contain one award.", qualifiedAwards.size() == 1);
        Assert.assertTrue("qualifiedAwards should contain our initial award.",
                ((List) qualifiedAwards).get(0).equals(awards.get(0)));
    }

    @Test
    public void testPerformAwardValidationAwardInvoicingSuspended() {
        List<ContractsAndGrantsBillingAward> awards = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1);
        ((Award) awards.get(0)).setExcludedFromInvoicing(true);
        prepareMocks(awards);
        prepareOtherMocks();
        expectError(awards.get(0), ArKeyConstants.CGINVOICE_CREATION_AWARD_EXCLUDED_FROM_INVOICING);
        replayMocks();
        Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();

        Collection<ContractsAndGrantsBillingAward> qualifiedAwards = contractsGrantsInvoiceCreateDocumentService
                .validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null,
                        ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode());

        verifyMocks();
        Assert.assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
    }

    @Test
    public void testPerformAwardValidationInactiveAward() {
        List<ContractsAndGrantsBillingAward> awards = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1);
        ((Award) awards.get(0)).setActive(false);
        prepareMocks(awards);
        prepareOtherMocks();
        expectError(awards.get(0), ArKeyConstants.CGINVOICE_CREATION_AWARD_INACTIVE_ERROR);
        replayMocks();
        Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();

        Collection<ContractsAndGrantsBillingAward> qualifiedAwards = contractsGrantsInvoiceCreateDocumentService
                .validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null,
                        ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode());

        verifyMocks();
        Assert.assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
    }

    @Test
    public void testPerformAwardValidationSuspendedAndInactiveAward() {
        List<ContractsAndGrantsBillingAward> awards = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1);
        ((Award) awards.get(0)).setActive(false);
        ((Award) awards.get(0)).setExcludedFromInvoicing(true);
        prepareMocks(awards);
        prepareOtherMocks();
        expectError(awards.get(0), ArKeyConstants.CGINVOICE_CREATION_AWARD_EXCLUDED_FROM_INVOICING);
        expectError(awards.get(0), ArKeyConstants.CGINVOICE_CREATION_AWARD_INACTIVE_ERROR);
        replayMocks();
        Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();

        Collection<ContractsAndGrantsBillingAward> qualifiedAwards = contractsGrantsInvoiceCreateDocumentService
                .validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null,
                        ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode());

        verifyMocks();
        Assert.assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
    }

    @Test
    public void testPerformAwardValidationTwoInvalidAwards() {
        List<ContractsAndGrantsBillingAward> awards = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1);
        List<ContractsAndGrantsBillingAward> awards2 = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_2);
        ((Award) awards.get(0)).setActive(false);
        ((Award) awards2.get(0)).setExcludedFromInvoicing(true);
        awards.addAll(awards2);
        prepareMocks(awards);
        prepareOtherMocks();
        expectError(awards.get(0), ArKeyConstants.CGINVOICE_CREATION_AWARD_EXCLUDED_FROM_INVOICING);
        expectError(awards.get(1), ArKeyConstants.CGINVOICE_CREATION_AWARD_INACTIVE_ERROR);
        replayMocks();
        Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();

        Collection<ContractsAndGrantsBillingAward> qualifiedAwards = contractsGrantsInvoiceCreateDocumentService
                .validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null,
                        ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode());

        verifyMocks();
        Assert.assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
    }

    @Test
    public void testPerformAwardValidationOneValidOneInvalidAward() {
        List<ContractsAndGrantsBillingAward> awards = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1);
        List<ContractsAndGrantsBillingAward> awards2 = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_2);
        ((Award) awards2.get(0)).setExcludedFromInvoicing(true);
        awards.addAll(awards2);
        prepareMocks(awards);
        prepareOtherMocks();
        expectError(awards.get(1), ArKeyConstants.CGINVOICE_CREATION_AWARD_EXCLUDED_FROM_INVOICING);
        replayMocks();
        Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();

        Collection<ContractsAndGrantsBillingAward> qualifiedAwards = contractsGrantsInvoiceCreateDocumentService
                .validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null,
                        ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode());

        verifyMocks();
        Assert.assertEquals("qualifiedAwards should contain one award.", 1, qualifiedAwards.size());
        Assert.assertTrue("qualifiedAwards should contain our initial award.",
                ((List) qualifiedAwards).get(0).equals(awards.get(0)));
    }

    @Test
    public void testPerformAwardValidationMissingAwardInvoicingOption() {
        List<ContractsAndGrantsBillingAward> awards = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1);
        ((Award) awards.get(0)).setInvoicingOptionCode(null);
        prepareMocks(awards);
        prepareOtherMocks();
        expectError(awards.get(0), ArKeyConstants.CGINVOICE_CREATION_INVOICING_OPTION_MISSING_ERROR);
        replayMocks();
        Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();

        Collection<ContractsAndGrantsBillingAward> qualifiedAwards = contractsGrantsInvoiceCreateDocumentService
                .validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null,
                        ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode());

        verifyMocks();
        Assert.assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
    }

    @Test
    public void testPerformAwardValidationInvalidPredeterminedBillingFrequency() {
        List<ContractsAndGrantsBillingAward> awards = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1);
        Award award = ((Award) awards.get(0));
        award.setBillingFrequencyCode(ArConstants.BillingFrequencyValues.PREDETERMINED_BILLING.getCode());
        award.setBillingFrequency(BillingFrequencyFixture.BILL_FREQ_PDBS.createBillingFrequency());
        AwardAccount awardAccount_2 = ARAwardAccountFixture.AWD_ACCT_WITH_CCA_2.createAwardAccount();
        award.getAwardAccounts().add(awardAccount_2);
        correctBillingFrequency = false;
        prepareMocks(awards);
        prepareOtherMocks();
        expectError(award, ArKeyConstants.CGINVOICE_CREATION_SINGLE_ACCOUNT_ERROR);
        replayMocks();
        Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();

        Collection<ContractsAndGrantsBillingAward> qualifiedAwards = contractsGrantsInvoiceCreateDocumentService
                .validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null,
                        ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode());

        verifyMocks();
        Assert.assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
    }

    @Test
    public void testPerformAwardValidationInvalidMilestoneBillingFrequency() {
        List<ContractsAndGrantsBillingAward> awards = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1);
        Award award = ((Award) awards.get(0));
        award.setBillingFrequencyCode(ArConstants.BillingFrequencyValues.MILESTONE.getCode());
        award.setBillingFrequency(BillingFrequencyFixture.BILL_FREQ_PDBS.createBillingFrequency());
        AwardAccount awardAccount_2 = ARAwardAccountFixture.AWD_ACCT_WITH_CCA_2.createAwardAccount();
        award.getAwardAccounts().add(awardAccount_2);
        correctBillingFrequency = false;
        prepareMocks(awards);
        prepareOtherMocks();
        expectError(award, ArKeyConstants.CGINVOICE_CREATION_SINGLE_ACCOUNT_ERROR);
        replayMocks();
        Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();

        Collection<ContractsAndGrantsBillingAward> qualifiedAwards = contractsGrantsInvoiceCreateDocumentService
                .validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null,
                        ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode());

        verifyMocks();
        Assert.assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
    }

    @Test
    public void testPerformAwardValidationNoActiveAccounts() {
        List<ContractsAndGrantsBillingAward> awards = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1);
        ((Award) awards.get(0)).getAwardAccounts().clear();
        prepareMocks(awards);
        prepareOtherMocks();
        expectError(awards.get(0), ArKeyConstants.CGINVOICE_CREATION_NO_ACTIVE_ACCOUNTS_ASSIGNED_ERROR);
        replayMocks();
        Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();

        Collection<ContractsAndGrantsBillingAward> qualifiedAwards = contractsGrantsInvoiceCreateDocumentService
                .validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null,
                        ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode());

        verifyMocks();
        Assert.assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
    }

    @Test
    public void testPerformAwardValidationExpiredAccount() {
        List<ContractsAndGrantsBillingAward> awards = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1);
        expiredAccounts = new ArrayList<>();
        expiredAccounts.add(new Account());
        prepareMocks(awards);
        prepareOtherMocks();
        expectError(awards.get(0), ArKeyConstants.CGINVOICE_CREATION_CONAINS_EXPIRED_ACCOUNTS_ERROR);
        replayMocks();
        Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();

        Collection<ContractsAndGrantsBillingAward> qualifiedAwards = contractsGrantsInvoiceCreateDocumentService
                .validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null,
                        ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode());

        verifyMocks();
        Assert.assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
    }

    @Test
    public void testPerformAwardValidationAwardFinalInvoiceAlreadyBilled() {
        List<ContractsAndGrantsBillingAward> awards = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1);
        alreadyBuilt = true;

        prepareMocks(awards);
        prepareOtherMocks();
        expectError(awards.get(0), ArKeyConstants.CGINVOICE_CREATION_AWARD_FINAL_BILLED_ERROR);
        replayMocks();
        Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();

        Collection<ContractsAndGrantsBillingAward> qualifiedAwards = contractsGrantsInvoiceCreateDocumentService
                .validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null,
                        ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode());

        verifyMocks();
        Assert.assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
    }

    @Test
    public void testPerformAwardValidationNoValidMilestonesToInvoice() {
        List<ContractsAndGrantsBillingAward> awards = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1);
        ((Award) awards.get(0)).setBillingFrequencyCode(ArConstants.BillingFrequencyValues.MILESTONE.getCode());
        hasMilestones = false;
        prepareMocks(awards);
        prepareOtherMocks();
        expectError(awards.get(0), ArKeyConstants.CGINVOICE_CREATION_AWARD_NO_VALID_MILESTONES);
        replayMocks();
        Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();

        Collection<ContractsAndGrantsBillingAward> qualifiedAwards = contractsGrantsInvoiceCreateDocumentService
                .validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null,
                        ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode());

        verifyMocks();
        Assert.assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
    }

    @Test
    public void testPerformAwardValidationNoValidBillsToInvoice() {
        List<ContractsAndGrantsBillingAward> awards = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1);
        ((Award) awards.get(0))
                .setBillingFrequencyCode(ArConstants.BillingFrequencyValues.PREDETERMINED_BILLING.getCode());
        ((Award) awards.get(0)).setBillingFrequency(BillingFrequencyFixture.BILL_FREQ_PDBS.createBillingFrequency());
        validBills = false;
        prepareMocks(awards);
        prepareOtherMocks();
        expectError(awards.get(0), ArKeyConstants.CGINVOICE_CREATION_AWARD_NO_VALID_BILLS);
        replayMocks();
        Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();

        Collection<ContractsAndGrantsBillingAward> qualifiedAwards = contractsGrantsInvoiceCreateDocumentService
                .validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null,
                        ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode());

        verifyMocks();
        Assert.assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
    }

    @Test
    public void testPerformAwardValidationOneValidOneInvalidAwardAccounts() {
        List<ContractsAndGrantsBillingAward> awards = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1);
        AwardAccount awardAccount_2 = ARAwardAccountFixture.AWD_ACCT_WITH_CCA_2.createAwardAccount();
        awardAccount_2.setCurrentLastBilledDate(new Date(System.currentTimeMillis()));
        ((Award) awards.get(0)).getAwardAccounts().add(awardAccount_2);

        prepareMocks(awards);
        prepareOtherMocks();
        replayMocks();
        Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();

        Collection<ContractsAndGrantsBillingAward> qualifiedAwards = contractsGrantsInvoiceCreateDocumentService
                .validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null,
                        ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode());

        verifyMocks();
        Assert.assertTrue("qualifiedAwards should contain one award.", qualifiedAwards.size() == 1);
    }

    @Test
    public void testPerformAwardValidationAgencyHasNoMatchingCustomer() {
        List<ContractsAndGrantsBillingAward> awards = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1);
        customerRecord = false;
        prepareMocks(awards);
        prepareOtherMocks();
        expectError(awards.get(0), ArKeyConstants.CGINVOICE_CREATION_AWARD_AGENCY_NO_CUSTOMER_RECORD);
        replayMocks();
        Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();

        Collection<ContractsAndGrantsBillingAward> qualifiedAwards = contractsGrantsInvoiceCreateDocumentService
                .validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null,
                        ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode());

        verifyMocks();
        Assert.assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
    }
    
    @Test
    public void testPerformAwardValidationNoValidAwardAccounts() {
        List<ContractsAndGrantsBillingAward> awards = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1);
        ContractsGrantsInvoiceDocument document = EasyMock.createMock(ContractsGrantsInvoiceDocument.class);
        InvoiceAccountDetail detail = new InvoiceAccountDetail();
        ContractsAndGrantsBillingAwardAccount awardAccount = awards.get(0).getActiveAwardAccounts().get(0);
        detail.setAccountNumber(awardAccount.getAccountNumber());
        detail.setChartOfAccountsCode(awardAccount.getChartOfAccountsCode());
        EasyMock.expect(document.getAccountDetails()).andReturn(Arrays.asList(detail));
        EasyMock.expect(
                accountService.getByPrimaryId(awardAccount.getChartOfAccountsCode(), awardAccount.getAccountNumber()))
                .andReturn(awardAccount.getAccount());
        EasyMock.replay(document);
        pendingDocuments.add(document);
        prepareMocks(awards);
        prepareOtherMocks();
        expectError(awards.get(0), ArKeyConstants.CGINVOICE_CREATION_AWARD_NO_VALID_ACCOUNTS);
        replayMocks();
        Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();

        Collection<ContractsAndGrantsBillingAward> qualifiedAwards = contractsGrantsInvoiceCreateDocumentService
                .validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null,
                        ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode());

        verifyMocks();
        Assert.assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
    }

    @Test
    public void testPerformAwardValidationNoContractControlAccountsByCCA() {
        List<ContractsAndGrantsBillingAward> awards = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1);
        ccaErrors.add(ArKeyConstants.AwardConstants.ERROR_NO_CTRL_ACCT);
        ccaErrors.add("");
        prepareMocks(awards);
        prepareOtherMocks();
        expectError(awards.get(0), ArKeyConstants.AwardConstants.ERROR_NO_CTRL_ACCT);
        replayMocks();
        Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();

        Collection<ContractsAndGrantsBillingAward> qualifiedAwards = contractsGrantsInvoiceCreateDocumentService
                .validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null,
                        ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode());

        verifyMocks();
        Assert.assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
    }

    @Test
    public void testPerformAwardValidationNoSysInfo() {
        List<ContractsAndGrantsBillingAward> awards = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1);
        chartOrgSetUp = false;
        prepareMocks(awards);
        prepareOtherMocks();
        expectError(awards.get(0), ArKeyConstants.CGINVOICE_CREATION_SYS_INFO_OADF_NOT_SETUP);
        replayMocks();
        Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();

        Collection<ContractsAndGrantsBillingAward> qualifiedAwards = contractsGrantsInvoiceCreateDocumentService
                .validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null,
                        ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode());

        verifyMocks();
        Assert.assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
    }

    @Test
    public void testPerformAwardValidationInvoicesInProgressMilestone() {
        List<ContractsAndGrantsBillingAward> awards = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1);
        Award award = ((Award) awards.get(0));
        award.setBillingFrequencyCode(ArConstants.BillingFrequencyValues.MILESTONE.getCode());
        invoiceInProgress = true;
        prepareMocks(awards);
        prepareOtherMocks();
        expectError(awards.get(0), ArKeyConstants.CGINVOICE_CREATION_AWARD_NO_VALID_ACCOUNTS);
        replayMocks();
        Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();

        Collection<ContractsAndGrantsBillingAward> qualifiedAwards = contractsGrantsInvoiceCreateDocumentService
                .validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null,
                        ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode());

        verifyMocks();
        Assert.assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
    }

    @Test
    public void testPerformAwardValidationInvoicesInProgressPredeterminedBilling() {
        List<ContractsAndGrantsBillingAward> awards = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1);
        Award award = ((Award) awards.get(0));
        award.setBillingFrequencyCode(ArConstants.BillingFrequencyValues.PREDETERMINED_BILLING.getCode());
        award.setBillingFrequency(BillingFrequencyFixture.BILL_FREQ_PDBS.createBillingFrequency());
        invoiceInProgress = true;
        prepareMocks(awards);
        prepareOtherMocks();
        expectError(awards.get(0), ArKeyConstants.CGINVOICE_CREATION_AWARD_NO_VALID_ACCOUNTS);
        replayMocks();
        Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();

        Collection<ContractsAndGrantsBillingAward> qualifiedAwards = contractsGrantsInvoiceCreateDocumentService
                .validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null,
                        ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode());

        verifyMocks();
        Assert.assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
    }

    private void expectError(ContractsAndGrantsBillingAward award, String errorKey) {
        EasyMock.expect(configurationService.getPropertyValueAsString(errorKey)).andReturn("Error");

        if (!awardsInError.contains(award)) {
            awardsInError.add(award);
            for (ContractsAndGrantsBillingAwardAccount awardAccount : award.getActiveAwardAccounts()) {
                EasyMock.expect(contractsGrantsInvoiceDocumentService.getBudgetAndActualsForAwardAccount(awardAccount,
                        null, award.getAwardBeginningDate())).andReturn(KualiDecimal.ZERO);
            }
            EasyMock.expect(dateTimeService.getCurrentTimestamp()).andReturn(null);
            EasyMock.expect(businessObjectService.save(EasyMock.isA(ContractsGrantsInvoiceDocumentErrorLog.class)))
                    .andReturn(null);
        }
    }

    private List<ContractsAndGrantsBillingAward> setupAward(ARAwardAccountFixture awardAccountFixture) {
        List<ContractsAndGrantsBillingAward> awards = new ArrayList<ContractsAndGrantsBillingAward>();

        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_VALID.createAward();
        award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_VALID.setAgencyFromFixture((Award) award);
        award.getActiveAwardAccounts().clear();
        AwardAccount awardAccount_1 = awardAccountFixture.createAwardAccount();
        Account account = new Account();
        awardAccount_1.setAccount(account);
        List<AwardAccount> awardAccounts = new ArrayList<AwardAccount>();
        awardAccounts.add(awardAccount_1);
        ((Award) award).setAwardAccounts(awardAccounts);
        award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_VALID.setAwardOrganizationFromFixture((Award) award);
        AwardFundManager awardFundManager = ARAwardFundManagerFixture.AWD_FND_MGR1.createAwardFundManager();
        ((Award) award).getAwardFundManagers().add(awardFundManager);
        ((Award) award).setAwardPrimaryFundManager(ARAwardFundManagerFixture.AWD_FND_MGR1.createAwardFundManager());
        awards.add(award);

        return awards;
    }

    private void prepareMocks(List<ContractsAndGrantsBillingAward> awards) {
        for (ContractsAndGrantsBillingAward award : awards) {
            prepareMocks(award);
        }
    }

    private void prepareMocks(ContractsAndGrantsBillingAward award) {
        EasyMock.expect(contractsGrantsBillingAwardVerificationService.isValueOfBillingFrequencyValid(award))
                .andReturn(true);
        EasyMock.expect(verifyBillingFrequencyService.validateBillingFrequency(award)).andReturn(validBillingFrequency);
        if (!validBillingFrequency) {
            return;
        }
        EasyMock.expect(contractsGrantsBillingAwardVerificationService.isBillingFrequencySetCorrectly(award))
                .andReturn(correctBillingFrequency);
        EasyMock.expect(contractsGrantsInvoiceDocumentService.getExpiredAccountsOfAward(award))
                .andReturn(expiredAccounts);
        EasyMock.expect(contractsGrantsBillingAwardVerificationService.isAwardFinalInvoiceAlreadyBuilt(award))
                .andReturn(alreadyBuilt);
        EasyMock.expect(contractsGrantsBillingAwardVerificationService.hasMilestonesToInvoice(award))
                .andReturn(hasMilestones);
        EasyMock.expect(contractsGrantsBillingAwardVerificationService.hasBillsToInvoice(award)).andReturn(validBills);
        EasyMock.expect(contractsGrantsBillingAwardVerificationService.owningAgencyHasCustomerRecord(award))
                .andReturn(customerRecord);
        EasyMock.expect(contractsGrantsInvoiceDocumentService.checkAwardContractControlAccounts(award))
                .andReturn(ccaErrors);
        EasyMock.expect(contractsGrantsBillingAwardVerificationService.isChartAndOrgSetupForInvoicing(award))
                .andReturn(chartOrgSetUp);

        if (ArConstants.BillingFrequencyValues.isMilestone(award) || ArConstants.BillingFrequencyValues.isPredeterminedBilling(award)) {
            EasyMock.expect(contractsGrantsBillingAwardVerificationService.isInvoiceInProgress(award))
                    .andReturn(invoiceInProgress);
        } else if (award.getActiveAwardAccounts().size() > 0) {
            Map<String, Object> fieldValues = new HashMap<String, Object>();
            fieldValues.put(ArPropertyConstants.ContractsGrantsInvoiceDocumentFields.PROPOSAL_NUMBER,
                    award.getProposalNumber());
            fieldValues.put(
                    KFSPropertyConstants.DOCUMENT_HEADER + "." + KFSPropertyConstants.WORKFLOW_DOCUMENT_STATUS_CODE,
                    pendingDocumentStatuses);

            EasyMock.expect(businessObjectService.findMatching(ContractsGrantsInvoiceDocument.class, fieldValues))
                    .andReturn(pendingDocuments);

            if (pendingDocuments.isEmpty()) {
                for (ContractsAndGrantsBillingAwardAccount awardAccount : award.getActiveAwardAccounts()) {
                    EasyMock.expect(verifyBillingFrequencyService.validateBillingFrequency(award, awardAccount))
                            .andReturn(true);
                }
            }
        }
    }

    private void prepareOtherMocks() {
        EasyMock.expect(optionsService.getCurrentYearOptions()).andReturn(new SystemOptions()).anyTimes();
        EasyMock.expect(financialSystemDocumentService.getPendingDocumentStatuses()).andReturn(pendingDocumentStatuses)
                .anyTimes();
        EasyMock.expect(
                configurationService.getPropertyValueAsBoolean(KFSConstants.MODULE_EXTERNAL_KUALI_COEUS_ENABLED))
                .andReturn(false).anyTimes();
    }

    private void replayMocks() {
        EasyMock.replay(contractsGrantsBillingAwardVerificationService, contractsGrantsInvoiceDocumentService,
                verifyBillingFrequencyService, financialSystemDocumentService, businessObjectService,
                configurationService, optionsService, dateTimeService, accountService);
    }

    private void verifyMocks() {
        EasyMock.verify(contractsGrantsBillingAwardVerificationService, contractsGrantsInvoiceDocumentService,
                verifyBillingFrequencyService, financialSystemDocumentService, businessObjectService,
                configurationService, optionsService, dateTimeService, accountService);
    }
}
