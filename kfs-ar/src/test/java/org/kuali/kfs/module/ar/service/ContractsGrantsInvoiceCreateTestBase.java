/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.kfs.module.ar.service;

import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.krad.bo.ModuleConfiguration;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.DocumentService;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.module.ar.batch.service.VerifyBillingFrequencyService;
import org.kuali.kfs.module.ar.businessobject.Bill;
import org.kuali.kfs.module.ar.businessobject.BillingPeriod;
import org.kuali.kfs.module.ar.businessobject.InvoiceBill;
import org.kuali.kfs.module.ar.businessobject.PredeterminedBillingSchedule;
import org.kuali.kfs.module.ar.dataaccess.AwardAccountObjectCodeTotalBilledDao;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsBillingAwardVerificationService;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerService;
import org.kuali.kfs.module.ar.fixture.ARAwardAccountFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardFundManagerFixture;
import org.kuali.kfs.module.ar.fixture.InvoiceBillFixture;
import org.kuali.kfs.module.ar.service.impl.ContractsGrantsInvoiceCreateDocumentServiceImpl;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.AwardAccount;
import org.kuali.kfs.module.cg.businessobject.AwardFundManager;
import org.kuali.kfs.sys.FinancialSystemModuleConfiguration;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;

import java.io.File;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Shared methods for Contracts & Grants Invoice creation services testing
 */
public abstract class ContractsGrantsInvoiceCreateTestBase extends KualiTestBase {
    protected BusinessObjectService businessObjectService;
    protected ConfigurationService configurationService;
    protected DocumentService documentService;
    protected KualiModuleService kualiModuleService;
    protected AccountingPeriodService accountingPeriodService;
    protected ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    protected UniversityDateService originalUniversityDateService;
    protected VerifyBillingFrequencyService verifyBillingFrequencyService;
    protected String errorOutputFile;

    protected ContractsGrantsInvoiceCreateDocumentServiceImpl contractsGrantsInvoiceCreateDocumentService;
    public static final Integer LAST_FISCAL_YEAR = 2015;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        documentService = SpringContext.getBean(DocumentService.class);
        configurationService = SpringContext.getBean(ConfigurationService.class);
        kualiModuleService = SpringContext.getBean(KualiModuleService.class);
        verifyBillingFrequencyService = SpringContext.getBean(VerifyBillingFrequencyService.class);
        originalUniversityDateService = SpringContext.getBean(UniversityDateService.class);
        contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
        accountingPeriodService = buildMockAccountingPeriodService();

        ModuleConfiguration systemConfiguration = kualiModuleService.getModuleServiceByNamespaceCode(KFSConstants.OptionalModuleNamespaces.ACCOUNTS_RECEIVABLE).getModuleConfiguration();
        String destinationFolderPath = ((FinancialSystemModuleConfiguration) systemConfiguration).getBatchFileDirectories().get(0);
        errorOutputFile = destinationFolderPath + File.separator + "JUNIT TEST.log";

        contractsGrantsInvoiceCreateDocumentService = new ContractsGrantsInvoiceCreateDocumentServiceImpl();

        contractsGrantsInvoiceCreateDocumentService.setAccountService(SpringContext.getBean(AccountService.class));
        contractsGrantsInvoiceCreateDocumentService.setAccountingPeriodService(accountingPeriodService);
        contractsGrantsInvoiceCreateDocumentService.setAccountsReceivableDocumentHeaderService(SpringContext.getBean(AccountsReceivableDocumentHeaderService.class));
        contractsGrantsInvoiceCreateDocumentService.setAwardAccountObjectCodeTotalBilledDao(SpringContext.getBean(AwardAccountObjectCodeTotalBilledDao.class));
        contractsGrantsInvoiceCreateDocumentService.setBusinessObjectService(businessObjectService);
        contractsGrantsInvoiceCreateDocumentService.setConfigurationService(configurationService);
        contractsGrantsInvoiceCreateDocumentService.setContractsGrantsBillingAwardVerificationService(SpringContext.getBean(ContractsGrantsBillingAwardVerificationService.class));
        contractsGrantsInvoiceCreateDocumentService.setContractsGrantsBillingUtilityService(SpringContext.getBean(ContractsGrantsBillingUtilityService.class));
        contractsGrantsInvoiceCreateDocumentService.setContractsGrantsInvoiceDocumentService(contractsGrantsInvoiceDocumentService);
        contractsGrantsInvoiceCreateDocumentService.setCostCategoryService(SpringContext.getBean(CostCategoryService.class));
        contractsGrantsInvoiceCreateDocumentService.setCustomerService(SpringContext.getBean(CustomerService.class));
        contractsGrantsInvoiceCreateDocumentService.setDateTimeService(SpringContext.getBean(DateTimeService.class));
        contractsGrantsInvoiceCreateDocumentService.setDocumentService(documentService);
        contractsGrantsInvoiceCreateDocumentService.setFinancialSystemDocumentService(SpringContext.getBean(FinancialSystemDocumentService.class));
        contractsGrantsInvoiceCreateDocumentService.setKualiModuleService(kualiModuleService);
        contractsGrantsInvoiceCreateDocumentService.setVerifyBillingFrequencyService(verifyBillingFrequencyService);
        contractsGrantsInvoiceCreateDocumentService.setUniversityDateService(originalUniversityDateService);
        contractsGrantsInvoiceCreateDocumentService.setOptionsService(SpringContext.getBean(OptionsService.class));
    }

    private AccountingPeriodService buildMockAccountingPeriodService() {
        AccountingPeriod accountingPeriod = new AccountingPeriod();
        accountingPeriod.setUniversityFiscalYear(LAST_FISCAL_YEAR);
        accountingPeriod.setUniversityFiscalPeriodCode("02");

        AccountingPeriodService accountingPeriodService = EasyMock.createMock(AccountingPeriodService.class);
        EasyMock.expect(accountingPeriodService.getByDate(EasyMock.anyObject())).andReturn(accountingPeriod).anyTimes();
        EasyMock.replay(accountingPeriodService);

        return accountingPeriodService;
    }

    @Override
    public void tearDown() throws Exception {
        File errors = new File(errorOutputFile);

        if (errors.exists()) {
            errors.delete();
        }

        super.tearDown();
    }

    protected List<ContractsAndGrantsBillingAward> setupAwards() {
        List<ContractsAndGrantsBillingAward> awards = new ArrayList<ContractsAndGrantsBillingAward>();

        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_VALID.createAward();
        award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_VALID.setAgencyFromFixture((Award) award);
        award.getActiveAwardAccounts().clear();
        AwardAccount awardAccount_1 = ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1.createAwardAccount();
        awardAccount_1.refreshReferenceObject("account");
        List<AwardAccount> awardAccounts = new ArrayList<AwardAccount>();
        awardAccounts.add(awardAccount_1);
        ((Award)award).setAwardAccounts(awardAccounts);
        award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_VALID.setAwardOrganizationFromFixture((Award) award);
        AwardFundManager awardFundManager = ARAwardFundManagerFixture.AWD_FND_MGR1.createAwardFundManager();
        ((Award)award).getAwardFundManagers().add(awardFundManager);
        ((Award)award).setAwardPrimaryFundManager(ARAwardFundManagerFixture.AWD_FND_MGR1.createAwardFundManager());
        awards.add(award);

        return awards;
    }

    protected List<ContractsAndGrantsBillingAward> setupBillableAwards() {
        List<ContractsAndGrantsBillingAward> awards = new ArrayList<ContractsAndGrantsBillingAward>();

        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_VALID.createAward();
        award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_VALID.setAgencyFromFixture((Award) award);
        award.getActiveAwardAccounts().clear();
        AwardAccount awardAccount_1 = ARAwardAccountFixture.AWD_ACCT_WITH_CCA_4.createAwardAccount();
        awardAccount_1.refreshReferenceObject("account");
        List<AwardAccount> awardAccounts = new ArrayList<AwardAccount>();
        awardAccounts.add(awardAccount_1);
        ((Award)award).setAwardAccounts(awardAccounts);
        award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_VALID.setAwardOrganizationFromFixture((Award) award);
        AwardFundManager awardFundManager = ARAwardFundManagerFixture.AWD_FND_MGR1.createAwardFundManager();
        ((Award)award).getAwardFundManagers().add(awardFundManager);
        ((Award)award).setAwardPrimaryFundManager(ARAwardFundManagerFixture.AWD_FND_MGR1.createAwardFundManager());
        awards.add(award);

        return awards;
    }

    protected void setupBills(ContractsGrantsInvoiceDocument document) {
        List<InvoiceBill> invoiceBills = new ArrayList<InvoiceBill>();
        InvoiceBill invBill_1 = InvoiceBillFixture.INV_BILL_2.createInvoiceBill();
        invBill_1.setDocumentNumber(document.getDocumentNumber());

        Timestamp ts = new Timestamp(new java.util.Date().getTime());
        java.sql.Date today = new java.sql.Date(ts.getTime());
        AccountingPeriod currPeriod = accountingPeriodService.getByDate(today);
        BillingPeriod billingPeriod = verifyBillingFrequencyService.getStartDateAndEndDateOfPreviousBillingPeriod(document.getInvoiceGeneralDetail().getAward(), currPeriod);
        Date invoiceDate = billingPeriod.getEndDate();
        Date billDate = new Date(new DateTime(invoiceDate.getTime()).minusDays(1).toDate().getTime());

        invBill_1.setBillDate(billDate);

        businessObjectService.save(invBill_1);
        invoiceBills.add(invBill_1);
        document.setInvoiceBills(invoiceBills);

        Bill bill = new Bill();
        bill.setProposalNumber(document.getInvoiceGeneralDetail().getProposalNumber());
        bill.setBillNumber(invBill_1.getBillNumber());
        bill.setBillDescription(invBill_1.getBillDescription());
        bill.setBillIdentifier(invBill_1.getBillIdentifier());
        bill.setBillDate(invBill_1.getBillDate());
        bill.setEstimatedAmount(invBill_1.getEstimatedAmount());
        bill.setBilled(false);
        bill.setAward(document.getInvoiceGeneralDetail().getAward());
        bill.setActive(true);

        PredeterminedBillingSchedule predeterminedBillingSchedule = new PredeterminedBillingSchedule();
        predeterminedBillingSchedule.setProposalNumber(document.getInvoiceGeneralDetail().getProposalNumber());
        List<Bill> bills = new ArrayList<Bill>();
        bills.add(bill);
        predeterminedBillingSchedule.setBills(bills);
        businessObjectService.save(predeterminedBillingSchedule);
        businessObjectService.save(bill);
    }

    protected UniversityDateService buildMockUniversityDateService() throws Exception {
        UniversityDateService universityDateService = EasyMock.createMock(UniversityDateService.class);
        EasyMock.expect(universityDateService.getCurrentFiscalYear()).andReturn(LAST_FISCAL_YEAR).anyTimes();
        EasyMock.replay(universityDateService);

        return universityDateService;
    }

}
