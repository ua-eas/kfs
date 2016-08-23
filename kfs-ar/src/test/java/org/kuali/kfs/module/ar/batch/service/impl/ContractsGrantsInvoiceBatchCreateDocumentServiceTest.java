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
package org.kuali.kfs.module.ar.batch.service.impl;

import org.apache.commons.io.FileUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.krad.util.ErrorMessage;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceDocumentErrorLog;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.fixture.ARAwardAccountFixture;
import org.kuali.kfs.module.ar.service.ContractsGrantsInvoiceCreateTestBase;
import org.kuali.kfs.module.ar.service.impl.ContractsGrantsInvoiceCreateDocumentServiceImpl;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.AwardAccount;
import org.kuali.kfs.module.cg.businessobject.AwardOrganization;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.rice.kew.api.exception.WorkflowException;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.kuali.kfs.sys.fixture.UserNameFixture.kfs;

/**
 * Tests for the ContractsGrantsInvoiceBatchCreateDocumentService
 */
@ConfigureContext(session = kfs)
public class ContractsGrantsInvoiceBatchCreateDocumentServiceTest extends ContractsGrantsInvoiceCreateTestBase {
    protected ContractsGrantsInvoiceBatchCreateDocumentServiceImpl contractsGrantsInvoiceBatchCreateDocumentService;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        contractsGrantsInvoiceBatchCreateDocumentService = new ContractsGrantsInvoiceBatchCreateDocumentServiceImpl();
        contractsGrantsInvoiceBatchCreateDocumentService.setConfigurationService(configurationService);
        contractsGrantsInvoiceBatchCreateDocumentService.setContractsGrantsInvoiceCreateDocumentService(contractsGrantsInvoiceCreateDocumentService);
    }


    public void testBatchCreateCGInvoiceDocumentsByAwardsOneValid() throws Exception {
        contractsGrantsInvoiceCreateDocumentService.setUniversityDateService(buildMockUniversityDateService());

        List<ContractsAndGrantsBillingAward> awards = setupBillableAwards();

        contractsGrantsInvoiceBatchCreateDocumentService.createCGInvoiceDocumentsByAwards(awards, errorOutputFile);

        File errors = new File(errorOutputFile);
        assertFalse("errors should not be written", errors.exists());

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertEquals("no errors should be persisted", 0, persistedErrors.size());

        contractsGrantsInvoiceCreateDocumentService.setUniversityDateService(originalUniversityDateService);
    }

    protected ContractsGrantsInvoiceBatchCreateDocumentServiceImpl setInvoiceCreateServiceOnInvoiceBatchCreateService(ContractsGrantsInvoiceCreateDocumentServiceImpl contractsGrantsInvoiceCreateDocumentService) throws Exception {
        ContractsGrantsInvoiceBatchCreateDocumentServiceImpl contractsGrantsInvoiceBatchCreateDocumentService = (ContractsGrantsInvoiceBatchCreateDocumentServiceImpl) TestUtils.getUnproxiedService("contractsGrantsInvoiceBatchCreateDocumentService");
        contractsGrantsInvoiceBatchCreateDocumentService.setContractsGrantsInvoiceCreateDocumentService(contractsGrantsInvoiceCreateDocumentService);
        return contractsGrantsInvoiceBatchCreateDocumentService;
    }

    public void testBatchCreateCGInvoiceDocumentsByAwardsEmptyAwardsList() throws IOException {
        List<ContractsAndGrantsBillingAward> awards = new ArrayList<ContractsAndGrantsBillingAward>();

        contractsGrantsInvoiceBatchCreateDocumentService.createCGInvoiceDocumentsByAwards(awards, errorOutputFile);

        String errorMessage = configurationService.getPropertyValueAsString(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.NO_AWARD);

        File errors = new File(errorOutputFile);
        assertTrue("errors should be written", errors.exists());
        assertTrue("errorOutputFile should not be empty", errors.length() > 0);
        assertTrue("error file should contain expected error", FileUtils.readFileToString(errors).contains(errorMessage));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("one error should be persisted", persistedErrors.size() == 1);
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError : persistedErrors) {
            assertTrue("process type should be batch", persistedError.getCreationProcessTypeCode().equals(ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.BATCH.getCode()));
            assertTrue("error message text should match", persistedError.getErrorMessages().get(0).getErrorMessageText().equals(errorMessage));
        }
    }

    public void testBatchCreateCGInvoiceDocumentsByAwardsNullAwardsList() throws IOException {
        List<ContractsAndGrantsBillingAward> awards = null;

        contractsGrantsInvoiceBatchCreateDocumentService.createCGInvoiceDocumentsByAwards(awards, errorOutputFile);

        String errorMessage = configurationService.getPropertyValueAsString(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.NO_AWARD);

        File errors = new File(errorOutputFile);
        assertTrue("errors should be written", errors.exists());
        assertTrue("errorOutputFile should not be empty", errors.length() > 0);
        assertTrue("error file should contain expected error", FileUtils.readFileToString(errors).contains(errorMessage));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("one error should be persisted", persistedErrors.size() == 1);
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError : persistedErrors) {
            assertTrue("process type should be batch", persistedError.getCreationProcessTypeCode().equals(ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.BATCH.getCode()));
            assertTrue("error message text should match", persistedError.getErrorMessages().get(0).getErrorMessageText().equals(errorMessage));
        }
    }

    public void testBatchCreateCGInvoiceDocumentsByAwardsNoOrg() throws IOException {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        Award award = (Award) awards.get(0);
        award.setAwardOrganizations(new ArrayList<AwardOrganization>());

        contractsGrantsInvoiceBatchCreateDocumentService.createCGInvoiceDocumentsByAwards(awards, errorOutputFile);

        String errorMessage = configurationService.getPropertyValueAsString(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.NO_ORGANIZATION_ON_AWARD);
        errorMessage = MessageFormat.format(errorMessage, award.getProposalNumber().toString());

        File errors = new File(errorOutputFile);
        assertTrue("errors should be written", errors.exists());
        assertTrue("errorOutputFile should not be empty", errors.length() > 0);
        assertTrue("error file should contain expected error", FileUtils.readFileToString(errors).contains(errorMessage));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("one error should be persisted", persistedErrors.size() == 1);
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError : persistedErrors) {
            assertTrue("process type should be batch", persistedError.getCreationProcessTypeCode().equals(ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.BATCH.getCode()));
            assertTrue("error message text should match", persistedError.getErrorMessages().get(0).getErrorMessageText().equals(errorMessage));
        }
    }

    public void testBatchCreateCGInvoiceDocumentsByAccountNonBillable() throws WorkflowException, IOException {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        Award award = ((Award) awards.get(0));
        award.setBillingFrequencyCode(ArConstants.BillingFrequencyValues.PREDETERMINED_BILLING.getCode());
        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();
        ContractsGrantsInvoiceDocument cgInvoice = contractsGrantsInvoiceCreateDocumentService.createCGInvoiceDocumentByAwardInfo(award, award.getActiveAwardAccounts(), "BL", "PSY", errorMessages, null, null);
        documentService.saveDocument(cgInvoice);
        setupBills(cgInvoice);
        documentService.saveDocument(cgInvoice);

        List<ContractsAndGrantsBillingAward> awards2 = setupAwards();
        Award award2 = (Award) awards2.get(0);
        award2.setInvoicingOptionCode(ArConstants.INV_ACCOUNT);

        contractsGrantsInvoiceBatchCreateDocumentService.createCGInvoiceDocumentsByAwards(awards2, errorOutputFile);

        String errorMessage = configurationService.getPropertyValueAsString(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.NON_BILLABLE);
        errorMessage = MessageFormat.format(errorMessage, award2.getActiveAwardAccounts().get(0).getAccountNumber(), award.getProposalNumber().toString());

        File errors = new File(errorOutputFile);
        assertTrue("errors should be written", errors.exists());
        assertTrue("errorOutputFile should not be empty", errors.length() > 0);
        assertTrue("error file should contain expected error", FileUtils.readFileToString(errors).contains(errorMessage));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("one error should be persisted", persistedErrors.size() == 1);
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError : persistedErrors) {
            assertTrue("process type should be batch", persistedError.getCreationProcessTypeCode().equals(ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.BATCH.getCode()));
            assertTrue("error message text should match", persistedError.getErrorMessages().get(0).getErrorMessageText().equals(errorMessage));
        }
    }

    public void testBatchCreateCGInvoiceDocumentsByAccountOneBillableOneNonBillable() throws Exception {
        contractsGrantsInvoiceCreateDocumentService.setUniversityDateService(buildMockUniversityDateService());

        List<ContractsAndGrantsBillingAward> awards = setupBillableAwards();
        Award award = ((Award) awards.get(0));
        award.setInvoicingOptionCode(ArConstants.INV_ACCOUNT);
        AwardAccount awardAccount_2 = ARAwardAccountFixture.AWD_ACCT_WITH_CCA_2.createAwardAccount();
        awardAccount_2.setCurrentLastBilledDate(new java.sql.Date(System.currentTimeMillis()));
        awardAccount_2.refreshReferenceObject("account");
        award.getAwardAccounts().add(awardAccount_2);

        contractsGrantsInvoiceBatchCreateDocumentService.createCGInvoiceDocumentsByAwards(awards, errorOutputFile);

        String errorMessage = configurationService.getPropertyValueAsString(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.NON_BILLABLE);
        errorMessage = MessageFormat.format(errorMessage, awardAccount_2.getAccountNumber(), award.getProposalNumber().toString());

        File errors = new File(errorOutputFile);
        assertTrue("errors should be written", errors.exists());
        assertTrue("errorOutputFile should not be empty", errors.length() > 0);
        assertTrue("error file should contain expected error", FileUtils.readFileToString(errors).contains(errorMessage));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertEquals("one error should be persisted", 1, persistedErrors.size());
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError : persistedErrors) {
            assertTrue("process type should be batch", persistedError.getCreationProcessTypeCode().equals(ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.BATCH.getCode()));
            assertTrue("error message text should match", persistedError.getErrorMessages().get(0).getErrorMessageText().equals(errorMessage));
        }

        contractsGrantsInvoiceCreateDocumentService.setUniversityDateService(originalUniversityDateService);
    }

    public void testBatchCreateCGInvoiceDocumentsByCCAContractAccountNotBillable() throws WorkflowException, IOException {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        Award award = ((Award) awards.get(0));
        award.setBillingFrequencyCode(ArConstants.BillingFrequencyValues.PREDETERMINED_BILLING.getCode());
        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();
        ContractsGrantsInvoiceDocument cgInvoice = contractsGrantsInvoiceCreateDocumentService.createCGInvoiceDocumentByAwardInfo(award, award.getActiveAwardAccounts(), "BL", "PSY", errorMessages, null, null);
        documentService.saveDocument(cgInvoice);
        setupBills(cgInvoice);
        documentService.saveDocument(cgInvoice);

        List<ContractsAndGrantsBillingAward> awards2 = setupAwards();
        Award award2 = (Award) awards2.get(0);
        award2.setInvoicingOptionCode(ArConstants.INV_CONTRACT_CONTROL_ACCOUNT);

        contractsGrantsInvoiceBatchCreateDocumentService.createCGInvoiceDocumentsByAwards(awards2, errorOutputFile);

        String errorMessage = configurationService.getPropertyValueAsString(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.CONTROL_ACCOUNT_NON_BILLABLE);
        errorMessage = MessageFormat.format(errorMessage, award2.getActiveAwardAccounts().get(0).getAccount().getContractControlAccount().getAccountNumber(), award.getProposalNumber().toString());

        File errors = new File(errorOutputFile);
        assertTrue("errors should be written", errors.exists());
        assertTrue("errorOutputFile should not be empty", errors.length() > 0);
        assertTrue("error file should contain expected error", FileUtils.readFileToString(errors).contains(errorMessage));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("one error should be persisted", persistedErrors.size() == 1);
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError : persistedErrors) {
            assertTrue("process type should be batch", persistedError.getCreationProcessTypeCode().equals(ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.BATCH.getCode()));
            assertTrue("error message text should match", persistedError.getErrorMessages().get(0).getErrorMessageText().equals(errorMessage));
        }
    }

    public void testBatchCreateCGInvoiceDocumentsByAwardNotAllBillableAccounts() throws WorkflowException, IOException {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        Award award = ((Award) awards.get(0));
        award.setBillingFrequencyCode(ArConstants.BillingFrequencyValues.PREDETERMINED_BILLING.getCode());
        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();
        ContractsGrantsInvoiceDocument cgInvoice = contractsGrantsInvoiceCreateDocumentService.createCGInvoiceDocumentByAwardInfo(award, award.getActiveAwardAccounts(), "BL", "PSY", errorMessages, null, null);
        documentService.saveDocument(cgInvoice);
        setupBills(cgInvoice);
        documentService.saveDocument(cgInvoice);

        List<ContractsAndGrantsBillingAward> awards2 = setupAwards();
        Award award2 = (Award) awards2.get(0);
        award2.setInvoicingOptionCode(ArConstants.INV_AWARD);

        contractsGrantsInvoiceBatchCreateDocumentService.createCGInvoiceDocumentsByAwards(awards2, errorOutputFile);

        String errorMessage = configurationService.getPropertyValueAsString(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.NOT_ALL_BILLABLE_ACCOUNTS);
        errorMessage = MessageFormat.format(errorMessage, award2.getProposalNumber().toString());

        File errors = new File(errorOutputFile);
        assertTrue("errors should be written", errors.exists());
        assertTrue("errorOutputFile should not be empty", errors.length() > 0);
        assertTrue("error file should contain expected error", FileUtils.readFileToString(errors).contains(errorMessage));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("one error should be persisted", persistedErrors.size() == 1);
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError : persistedErrors) {
            assertTrue("process type should be batch", persistedError.getCreationProcessTypeCode().equals(ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.BATCH.getCode()));
            assertTrue("error message text should match", persistedError.getErrorMessages().get(0).getErrorMessageText().equals(errorMessage));
        }
    }
}
