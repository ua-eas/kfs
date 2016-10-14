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
package org.kuali.kfs.module.ar.service.impl.ContractsGrantsInvoiceCreateDocumentService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.batch.service.VerifyBillingFrequencyService;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceDocumentErrorLog;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsBillingAwardVerificationService;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.fixture.ARAwardAccountFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardFundManagerFixture;
import org.kuali.kfs.module.ar.service.impl.ContractsGrantsInvoiceCreateDocumentServiceImpl;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.AwardAccount;
import org.kuali.kfs.module.cg.businessobject.AwardFundManager;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class ValidateAwardsTest {

    private ContractsGrantsInvoiceCreateDocumentServiceImpl contractsGrantsInvoiceCreateDocumentService;
    private ContractsGrantsBillingAwardVerificationService contractsGrantsBillingAwardVerificationService;
    private ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    private VerifyBillingFrequencyService verifyBillingFrequencyService;
    private FinancialSystemDocumentService financialSystemDocumentService;
    private BusinessObjectService businessObjectService;
    private ConfigurationService configurationService;
    private OptionsService optionsService;
    private DateTimeService dateTimeService;

    private Set<String> pendingDocumentStatuses = new HashSet<>(Arrays.asList("I", "S", "R", "E"));

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
    }

    @Test
    public void testValidateManualAwardsOneValidAward() {
        List<ContractsAndGrantsBillingAward> awards = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1);
        prepareOtherMocks();
        replayMocks();
        Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();

        Collection<ContractsAndGrantsBillingAward> validAwards = contractsGrantsInvoiceCreateDocumentService
                .validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null,
                        ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode());

        verifyMocks();

        Assert.assertTrue("contractsGrantsInvoiceDocumentErrorLogs should be empty.",
                contractsGrantsInvoiceDocumentErrorLogs.size() == 0);
        Assert.assertTrue("validAwards should contain one award.", validAwards.size() == 1);
        Assert.assertTrue("validAwards should contain our initial award.", validAwards.contains(awards.get(0)));
    }

    @Test
    public void testValidateManualAwardsOneInvalidAward() {
        List<ContractsAndGrantsBillingAward> awards = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1);
        expectError(ArKeyConstants.CGINVOICE_CREATION_AWARD_EXCLUDED_FROM_INVOICING);
        prepareOtherMocks();
        replayMocks();
        ((Award) awards.get(0)).setExcludedFromInvoicing(true);
        List<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();

        Collection<ContractsAndGrantsBillingAward> validAwards = contractsGrantsInvoiceCreateDocumentService
                .validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null,
                        ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode());

        verifyMocks();
        Assert.assertTrue("contractsGrantsInvoiceDocumentErrorLogs should contain one award.",
                contractsGrantsInvoiceDocumentErrorLogs.size() == 1);
        Assert.assertTrue("validAwards should be empty.", validAwards.size() == 0);
        Assert.assertTrue("contractsGrantsInvoiceDocumentErrorLogs should contain our initial award.",
                errorLogContainsAward(contractsGrantsInvoiceDocumentErrorLogs, awards.get(0)));
    }

    @Test
    public void testValidateManualAwardsTwoValidAwards() {
        List<ContractsAndGrantsBillingAward> awards = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1);
        List<ContractsAndGrantsBillingAward> awards2 = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_2);
        awards.addAll(awards2);
        prepareOtherMocks();
        replayMocks();
        Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();

        Collection<ContractsAndGrantsBillingAward> validAwards = contractsGrantsInvoiceCreateDocumentService
                .validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null,
                        ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode());

        verifyMocks();
        Assert.assertTrue("contractsGrantsInvoiceDocumentErrorLogs should be empty.",
                contractsGrantsInvoiceDocumentErrorLogs.size() == 0);
        Assert.assertTrue("validAwards should contain one award.", validAwards.size() == 2);
        Assert.assertTrue("validAwards should contain our first award.", validAwards.contains(awards.get(0)));
        Assert.assertTrue("validAwards should contain our second award.", validAwards.contains(awards.get(1)));
    }

    @Test
    public void testValidateManualAwardsTwoInvalidAwards() {
        List<ContractsAndGrantsBillingAward> awards = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1);
        ((Award) awards.get(0)).setExcludedFromInvoicing(true);
        List<ContractsAndGrantsBillingAward> awards2 = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_2);
        ((Award) awards2.get(0)).setExcludedFromInvoicing(true);
        awards.addAll(awards2);
        expectError(ArKeyConstants.CGINVOICE_CREATION_AWARD_EXCLUDED_FROM_INVOICING);
        expectError(ArKeyConstants.CGINVOICE_CREATION_AWARD_EXCLUDED_FROM_INVOICING);
        prepareOtherMocks();
        replayMocks();
        List<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();

        Collection<ContractsAndGrantsBillingAward> validAwards = contractsGrantsInvoiceCreateDocumentService
                .validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null,
                        ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode());

        verifyMocks();
        Assert.assertTrue("contractsGrantsInvoiceDocumentErrorLogs should contain two awards.",
                contractsGrantsInvoiceDocumentErrorLogs.size() == 2);
        Assert.assertTrue("validAwards should be empty.", validAwards.size() == 0);
        Assert.assertTrue("contractsGrantsInvoiceDocumentErrorLogs should contain our first award.",
                errorLogContainsAward(contractsGrantsInvoiceDocumentErrorLogs, awards.get(0)));
        Assert.assertTrue("contractsGrantsInvoiceDocumentErrorLogs should contain our second award.",
                errorLogContainsAward(contractsGrantsInvoiceDocumentErrorLogs, awards.get(1)));
    }

    @Test
    public void testValidateManualAwardsOneValidOneInvalidAwards() {
        List<ContractsAndGrantsBillingAward> awards = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1);
        List<ContractsAndGrantsBillingAward> awards2 = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_2);
        ((Award) awards2.get(0)).setExcludedFromInvoicing(true);
        awards.addAll(awards2);
        expectError(ArKeyConstants.CGINVOICE_CREATION_AWARD_EXCLUDED_FROM_INVOICING);
        prepareOtherMocks();
        replayMocks();
        List<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();

        Collection<ContractsAndGrantsBillingAward> validAwards = contractsGrantsInvoiceCreateDocumentService
                .validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null,
                        ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode());

        verifyMocks();
        Assert.assertTrue("contractsGrantsInvoiceDocumentErrorLogs should contain one award.",
                contractsGrantsInvoiceDocumentErrorLogs.size() == 1);
        Assert.assertTrue("validAwards should contain one award.", validAwards.size() == 1);
        Assert.assertTrue("validAwards should contain our first award.", validAwards.contains(awards.get(0)));
        Assert.assertTrue("contractsGrantsInvoiceDocumentErrorLogs should contain our second award.",
                errorLogContainsAward(contractsGrantsInvoiceDocumentErrorLogs, awards.get(1)));
    }

    @Test
    public void testValidateBatchAwardsOneValidAward() {
        List<ContractsAndGrantsBillingAward> awards = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1);
        prepareOtherMocks();
        replayMocks();

        Collection<ContractsAndGrantsBillingAward> validAwards = contractsGrantsInvoiceCreateDocumentService
                .validateAwards(awards, null, null,
                        ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.BATCH.getCode());

        verifyMocks();
        Assert.assertTrue("validAwards should contain one award.", validAwards.size() == 1);
        Assert.assertTrue("validAwards should contain our initial award.", validAwards.contains(awards.get(0)));
    }

    @Test
    public void testValidateBatchAwardsOneInvalidAward() {
        List<ContractsAndGrantsBillingAward> awards = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1);
        ((Award) awards.get(0)).setExcludedFromInvoicing(true);
        expectError(ArKeyConstants.CGINVOICE_CREATION_AWARD_EXCLUDED_FROM_INVOICING);
        prepareOtherMocks();
        replayMocks();

        Collection<ContractsAndGrantsBillingAward> validAwards = contractsGrantsInvoiceCreateDocumentService
                .validateAwards(awards, null, null,
                        ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.BATCH.getCode());

        verifyMocks();
        Assert.assertTrue("validAwards should be empty.", validAwards.size() == 0);
    }

    @Test
    public void testValidateBatchAwardsTwoValidAwards() {
        List<ContractsAndGrantsBillingAward> awards = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1);
        List<ContractsAndGrantsBillingAward> awards2 = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_2);
        awards.addAll(awards2);
        prepareOtherMocks();
        replayMocks();

        Collection<ContractsAndGrantsBillingAward> validAwards = contractsGrantsInvoiceCreateDocumentService
                .validateAwards(awards, null, null,
                        ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.BATCH.getCode());

        verifyMocks();
        Assert.assertTrue("validAwards should contain one award.", validAwards.size() == 2);
        Assert.assertTrue("validAwards should contain our first award.", validAwards.contains(awards.get(0)));
        Assert.assertTrue("validAwards should contain our second award.", validAwards.contains(awards.get(1)));
    }

    @Test
    public void testValidateBatchAwardsTwoInvalidAwards() {
        List<ContractsAndGrantsBillingAward> awards = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1);
        ((Award) awards.get(0)).setExcludedFromInvoicing(true);
        List<ContractsAndGrantsBillingAward> awards2 = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_2);
        ((Award) awards2.get(0)).setExcludedFromInvoicing(true);
        awards.addAll(awards2);
        expectError(ArKeyConstants.CGINVOICE_CREATION_AWARD_EXCLUDED_FROM_INVOICING);
        expectError(ArKeyConstants.CGINVOICE_CREATION_AWARD_EXCLUDED_FROM_INVOICING);
        prepareOtherMocks();
        replayMocks();

        Collection<ContractsAndGrantsBillingAward> validAwards = contractsGrantsInvoiceCreateDocumentService
                .validateAwards(awards, null, null,
                        ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.BATCH.getCode());

        verifyMocks();
        Assert.assertTrue("validAwards should be empty.", validAwards.size() == 0);
    }

    @Test
    public void testValidateBatchAwardsOneValidOneInvalidAwards() {
        List<ContractsAndGrantsBillingAward> awards = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1);
        List<ContractsAndGrantsBillingAward> awards2 = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_2);
        ((Award) awards2.get(0)).setExcludedFromInvoicing(true);
        awards.addAll(awards2);
        expectError(ArKeyConstants.CGINVOICE_CREATION_AWARD_EXCLUDED_FROM_INVOICING);
        prepareOtherMocks();
        replayMocks();

        Collection<ContractsAndGrantsBillingAward> validAwards = contractsGrantsInvoiceCreateDocumentService
                .validateAwards(awards, null, null,
                        ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.BATCH.getCode());

        verifyMocks();
        Assert.assertTrue("validAwards should contain one award.", validAwards.size() == 1);
        Assert.assertTrue("validAwards should contain our first award.", validAwards.contains(awards.get(0)));
    }

    @Test
    public void testValidateDuplicateAccount() {
        List<ContractsAndGrantsBillingAward> awards = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1);
        List<ContractsAndGrantsBillingAward> awards2 = setupAward(ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1);
        awards.addAll(awards2);
        expectError(ArKeyConstants.CGINVOICE_CREATION_ACCOUNT_ON_MULTIPLE_AWARDS);
        expectError(ArKeyConstants.CGINVOICE_CREATION_ACCOUNT_ON_MULTIPLE_AWARDS);
        prepareOtherMocks();
        replayMocks();

        Collection<ContractsAndGrantsBillingAward> validAwards = contractsGrantsInvoiceCreateDocumentService
                .validateAwards(awards, null, null,
                        ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.BATCH.getCode());

        verifyMocks();
        Assert.assertTrue("validAwards should be empty.", validAwards.size() == 0);
    }

    private List<ContractsAndGrantsBillingAward> setupAward(ARAwardAccountFixture awardAccountFixture) {
        List<ContractsAndGrantsBillingAward> awards = new ArrayList<ContractsAndGrantsBillingAward>();

        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_VALID.createAward();
        award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_VALID.setAgencyFromFixture((Award) award);
        award.getActiveAwardAccounts().clear();
        AwardAccount awardAccount_1 = awardAccountFixture.createAwardAccount();
        List<AwardAccount> awardAccounts = new ArrayList<AwardAccount>();
        awardAccounts.add(awardAccount_1);
        ((Award) award).setAwardAccounts(awardAccounts);
        award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_VALID.setAwardOrganizationFromFixture((Award) award);
        AwardFundManager awardFundManager = ARAwardFundManagerFixture.AWD_FND_MGR1.createAwardFundManager();
        ((Award) award).getAwardFundManagers().add(awardFundManager);
        ((Award) award).setAwardPrimaryFundManager(ARAwardFundManagerFixture.AWD_FND_MGR1.createAwardFundManager());
        prepareMocks(award);
        awards.add(award);

        return awards;
    }

    private void prepareMocks(ContractsAndGrantsBillingAward award) {
        EasyMock.expect(contractsGrantsBillingAwardVerificationService.isValueOfBillingFrequencyValid(award))
                .andReturn(true);
        EasyMock.expect(contractsGrantsBillingAwardVerificationService.isBillingFrequencySetCorrectly(award))
                .andReturn(true);
        EasyMock.expect(verifyBillingFrequencyService.validateBillingFrequency(award)).andReturn(true);
        EasyMock.expect(contractsGrantsInvoiceDocumentService.getExpiredAccountsOfAward(award)).andReturn(null);
        EasyMock.expect(contractsGrantsBillingAwardVerificationService.isAwardFinalInvoiceAlreadyBuilt(award))
                .andReturn(false);
        EasyMock.expect(contractsGrantsBillingAwardVerificationService.hasMilestonesToInvoice(award)).andReturn(true);
        EasyMock.expect(contractsGrantsBillingAwardVerificationService.hasBillsToInvoice(award)).andReturn(true);
        EasyMock.expect(contractsGrantsBillingAwardVerificationService.owningAgencyHasCustomerRecord(award))
                .andReturn(true);
        EasyMock.expect(contractsGrantsInvoiceDocumentService.checkAwardContractControlAccounts(award)).andReturn(null);
        EasyMock.expect(contractsGrantsBillingAwardVerificationService.isChartAndOrgSetupForInvoicing(award))
                .andReturn(true);

        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(ArPropertyConstants.ContractsGrantsInvoiceDocumentFields.PROPOSAL_NUMBER,
                award.getProposalNumber());
        fieldValues.put(KFSPropertyConstants.DOCUMENT_HEADER + "." + KFSPropertyConstants.WORKFLOW_DOCUMENT_STATUS_CODE,
                pendingDocumentStatuses);

        EasyMock.expect(businessObjectService.findMatching(ContractsGrantsInvoiceDocument.class, fieldValues))
                .andReturn(new ArrayList<>());

        for (ContractsAndGrantsBillingAwardAccount awardAccount : award.getActiveAwardAccounts()) {
            EasyMock.expect(verifyBillingFrequencyService.validateBillingFrequency(award, awardAccount))
                    .andReturn(true);
        }
    }

    private void expectError(String errorKey) {
        EasyMock.expect(configurationService.getPropertyValueAsString(errorKey)).andReturn("Error");
        EasyMock.expect(contractsGrantsInvoiceDocumentService.getBudgetAndActualsForAwardAccount(EasyMock.anyObject(),
                EasyMock.anyString(), EasyMock.anyObject())).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(dateTimeService.getCurrentTimestamp()).andReturn(null);
        EasyMock.expect(businessObjectService.save(EasyMock.isA(ContractsGrantsInvoiceDocumentErrorLog.class)))
                .andReturn(null);
    }

    private void prepareOtherMocks() {
        EasyMock.expect(optionsService.getCurrentYearOptions()).andReturn(new SystemOptions()).anyTimes();
        EasyMock.expect(financialSystemDocumentService.getPendingDocumentStatuses()).andReturn(pendingDocumentStatuses)
                .anyTimes();
    }

    private void replayMocks() {
        EasyMock.replay(contractsGrantsBillingAwardVerificationService, contractsGrantsInvoiceDocumentService,
                verifyBillingFrequencyService, financialSystemDocumentService, businessObjectService,
                configurationService, optionsService, dateTimeService);
    }

    private void verifyMocks() {
        EasyMock.verify(contractsGrantsBillingAwardVerificationService, contractsGrantsInvoiceDocumentService,
                verifyBillingFrequencyService, financialSystemDocumentService, businessObjectService,
                configurationService, optionsService, dateTimeService);
    }

    private boolean errorLogContainsAward(
            Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs,
            ContractsAndGrantsBillingAward award) {
        for (ContractsGrantsInvoiceDocumentErrorLog contractsGrantsInvoiceDocumentErrorLog : contractsGrantsInvoiceDocumentErrorLogs) {
            if (contractsGrantsInvoiceDocumentErrorLog.getProposalNumber().equals(award.getProposalNumber())) {
                return true;
            }
        }
        return false;
    }
}
