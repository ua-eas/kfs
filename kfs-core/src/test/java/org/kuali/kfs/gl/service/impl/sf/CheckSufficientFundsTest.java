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
package org.kuali.kfs.gl.service.impl.sf;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.coa.service.ObjectTypeService;
import org.kuali.kfs.gl.businessobject.SufficientFundBalances;
import org.kuali.kfs.gl.businessobject.SufficientFundRebuild;
import org.kuali.kfs.gl.service.impl.SufficientFundsServiceImpl;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.SufficientFundsItem;
import org.kuali.kfs.sys.document.GeneralLedgerPostingDocument;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CheckSufficientFundsTest {
    private SufficientFundsServiceImpl sufficientFundsService;
    private OptionsService optionsService;
    private ObjectTypeService objectTypeService;
    private BusinessObjectService businessObjectService;
    private GeneralLedgerPendingEntryService generalLedgerPendingEntryService;

    @Before
    public void setUp() {
        sufficientFundsService = new SufficientFundsServiceImpl();

        optionsService = EasyMock.mock(OptionsService.class);
        objectTypeService = EasyMock.mock(ObjectTypeService.class);
        businessObjectService = EasyMock.mock(BusinessObjectService.class);
        generalLedgerPendingEntryService = EasyMock.mock(GeneralLedgerPendingEntryService.class);

        sufficientFundsService.setOptionsService(optionsService);
        sufficientFundsService.setObjectTypeService(objectTypeService);
        sufficientFundsService.setBusinessObjectService(businessObjectService);
        sufficientFundsService.setGeneralLedgerPendingEntryService(generalLedgerPendingEntryService);
    }

    private void replayAll() {
        EasyMock.replay(optionsService);
        EasyMock.replay(objectTypeService);
        EasyMock.replay(businessObjectService);
        EasyMock.replay(generalLedgerPendingEntryService);
    }

    @Test
    public void testNoTransactions() {
        GeneralLedgerPostingDocument doc = SufficientFundsTestFixtures.getTestGeneralLedgerPostingDocument(new ArrayList<>());

        EasyMock.expect(optionsService.getCurrentYearOptions()).andReturn(SufficientFundsTestFixtures.getTestSystemOptions(2017, true));
        replayAll();

        List<SufficientFundsItem> transactions = sufficientFundsService.checkSufficientFunds(doc);
        Assert.assertEquals(0, transactions.size());
    }

    @Test
    public void testInvalidAccount() {
        List<GeneralLedgerPendingEntry> glpes = new ArrayList<>();
        GeneralLedgerPendingEntry glpe = SufficientFundsTestFixtures.getTestGeneralLedgerPendingEntry("BL", "XXXXXXX", "4166", KFSConstants.SF_TYPE_NO_CHECKING, new KualiDecimal("1.00"));
        glpe.setAccount(null);
        glpes.add(glpe);

        GeneralLedgerPostingDocument doc = SufficientFundsTestFixtures.getTestGeneralLedgerPostingDocument(glpes);

        EasyMock.expect(optionsService.getCurrentYearOptions()).andReturn(SufficientFundsTestFixtures.getTestSystemOptions(2017, true));
        replayAll();

        try {
            sufficientFundsService.checkSufficientFunds(doc);
            Assert.fail("Should have thrown exception");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    @Test
    public void testNoChecking() {
        List<GeneralLedgerPendingEntry> glpes = new ArrayList<>();
        glpes.add(SufficientFundsTestFixtures.getTestGeneralLedgerPendingEntry("BL", "1234567", "4166", KFSConstants.SF_TYPE_NO_CHECKING, new KualiDecimal("1.00")));

        GeneralLedgerPostingDocument doc = SufficientFundsTestFixtures.getTestGeneralLedgerPostingDocument(glpes);

        EasyMock.expect(optionsService.getCurrentYearOptions()).andReturn(SufficientFundsTestFixtures.getTestSystemOptions(2017, true));
        replayAll();

        List<SufficientFundsItem> transactions = sufficientFundsService.checkSufficientFunds(doc);
        Assert.assertEquals(0, transactions.size());
    }

    @Test
    public void testAccountChecking() {
        List<GeneralLedgerPendingEntry> glpes = new ArrayList<>();
        glpes.add(SufficientFundsTestFixtures.getTestGeneralLedgerPendingEntry("BL", "1234567", "4166", KFSConstants.SF_TYPE_ACCOUNT, new KualiDecimal("1.00")));

        GeneralLedgerPostingDocument doc = SufficientFundsTestFixtures.getTestGeneralLedgerPostingDocument(glpes);

        List<String> doctypes = Collections.singletonList("DOC12345");
        EasyMock.expect(optionsService.getCurrentYearOptions()).andReturn(SufficientFundsTestFixtures.getTestSystemOptions(2017, true));
        EasyMock.expect(objectTypeService.getCurrentYearExpenseObjectTypes()).andReturn(SufficientFundsTestFixtures.getTestCurrentYearExpenseObjectTypes("EX"));
        EasyMock.expect(businessObjectService.findByPrimaryKey(SufficientFundBalances.class, SufficientFundsTestFixtures.getTestSufficientFundBalancesKeys("BL", "1234567"))).andReturn(null);
        EasyMock.expect(businessObjectService.findMatching(SufficientFundRebuild.class, SufficientFundsTestFixtures.getTestSufficientFundRebuildKeys("BL", "1234567"))).andReturn(new ArrayList());
        EasyMock.expect(generalLedgerPendingEntryService.getExpenseSummary(2017, "BL", "1234567", "    ", true, false, doctypes)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getExpenseSummary(2017, "BL", "1234567", "    ", false, false, doctypes)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getBudgetSummary(2017, "BL", "1234567", "    ", false)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getEncumbranceSummary(2017, "BL", "1234567", "    ", true, false, doctypes)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getEncumbranceSummary(2017, "BL", "1234567", "    ", false, false, doctypes)).andReturn(KualiDecimal.ZERO);

        replayAll();

        List<SufficientFundsItem> transactions = sufficientFundsService.checkSufficientFunds(doc);
        Assert.assertEquals(1, transactions.size());

        SufficientFundsItem item = transactions.get(0);
        Assert.assertEquals(2017, item.getYear().getUniversityFiscalYear().intValue());
        Assert.assertEquals("BL", item.getAccount().getChartOfAccountsCode());
        Assert.assertEquals("1234567", item.getAccount().getAccountNumber());
        Assert.assertEquals(new KualiDecimal("1.00"), item.getAmount());
    }

    @Test
    public void testAccountCheckingZeroDollars() {
        List<GeneralLedgerPendingEntry> glpes = new ArrayList<>();
        glpes.add(SufficientFundsTestFixtures.getTestGeneralLedgerPendingEntry("BL", "1234567", "4166", KFSConstants.SF_TYPE_ACCOUNT, new KualiDecimal("0.00")));

        GeneralLedgerPostingDocument doc = SufficientFundsTestFixtures.getTestGeneralLedgerPostingDocument(glpes);

        List<String> doctypes = Collections.singletonList("DOC12345");
        EasyMock.expect(optionsService.getCurrentYearOptions()).andReturn(SufficientFundsTestFixtures.getTestSystemOptions(2017, true));
        EasyMock.expect(objectTypeService.getCurrentYearExpenseObjectTypes()).andReturn(SufficientFundsTestFixtures.getTestCurrentYearExpenseObjectTypes("EX"));
        EasyMock.expect(businessObjectService.findByPrimaryKey(SufficientFundBalances.class, SufficientFundsTestFixtures.getTestSufficientFundBalancesKeys("BL", "1234567"))).andReturn(null);
        EasyMock.expect(businessObjectService.findMatching(SufficientFundRebuild.class, SufficientFundsTestFixtures.getTestSufficientFundRebuildKeys("BL", "1234567"))).andReturn(new ArrayList());
        EasyMock.expect(generalLedgerPendingEntryService.getExpenseSummary(2017, "BL", "1234567", "    ", true, false, doctypes)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getExpenseSummary(2017, "BL", "1234567", "    ", false, false, doctypes)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getBudgetSummary(2017, "BL", "1234567", "    ", false)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getEncumbranceSummary(2017, "BL", "1234567", "    ", true, false, doctypes)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getEncumbranceSummary(2017, "BL", "1234567", "    ", false, false, doctypes)).andReturn(KualiDecimal.ZERO);

        replayAll();

        List<SufficientFundsItem> transactions = sufficientFundsService.checkSufficientFunds(doc);
        Assert.assertEquals(0, transactions.size());
    }

    @Test
    public void testAccountCheckingBudgetCheckingOptionsCode() {
        List<GeneralLedgerPendingEntry> glpes = new ArrayList<>();
        glpes.add(SufficientFundsTestFixtures.getTestGeneralLedgerPendingEntry("BL", "1234567", "4166", KFSConstants.SF_TYPE_ACCOUNT, new KualiDecimal("1.00")));

        GeneralLedgerPostingDocument doc = SufficientFundsTestFixtures.getTestGeneralLedgerPostingDocument(glpes);

        List<String> doctypes = Collections.singletonList("DOC12345");
        EasyMock.expect(optionsService.getCurrentYearOptions()).andReturn(SufficientFundsTestFixtures.getTestSystemOptions(2017, false));
        EasyMock.expect(objectTypeService.getCurrentYearExpenseObjectTypes()).andReturn(SufficientFundsTestFixtures.getTestCurrentYearExpenseObjectTypes("EX"));
        EasyMock.expect(businessObjectService.findByPrimaryKey(SufficientFundBalances.class, SufficientFundsTestFixtures.getTestSufficientFundBalancesKeys("BL", "1234567"))).andReturn(null);
        EasyMock.expect(businessObjectService.findMatching(SufficientFundRebuild.class, SufficientFundsTestFixtures.getTestSufficientFundRebuildKeys("BL", "1234567"))).andReturn(new ArrayList());
        EasyMock.expect(generalLedgerPendingEntryService.getExpenseSummary(2017, "BL", "1234567", "    ", true, false, doctypes)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getExpenseSummary(2017, "BL", "1234567", "    ", false, false, doctypes)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getBudgetSummary(2017, "BL", "1234567", "    ", false)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getEncumbranceSummary(2017, "BL", "1234567", "    ", true, false, doctypes)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getEncumbranceSummary(2017, "BL", "1234567", "    ", false, false, doctypes)).andReturn(KualiDecimal.ZERO);

        replayAll();

        List<SufficientFundsItem> transactions = sufficientFundsService.checkSufficientFunds(doc);
        Assert.assertEquals(0, transactions.size());
    }

    @Test
    public void testAccountCheckingPendingAcctSufficientFundsIndicator() {
        List<GeneralLedgerPendingEntry> glpes = new ArrayList<>();
        glpes.add(SufficientFundsTestFixtures.getTestGeneralLedgerPendingEntry("BL", "1234567", "4166", KFSConstants.SF_TYPE_ACCOUNT, new KualiDecimal("1.00"), false, "AC"));

        GeneralLedgerPostingDocument doc = SufficientFundsTestFixtures.getTestGeneralLedgerPostingDocument(glpes);

        List<String> doctypes = Collections.singletonList("DOC12345");
        EasyMock.expect(optionsService.getCurrentYearOptions()).andReturn(SufficientFundsTestFixtures.getTestSystemOptions(2017, true));
        EasyMock.expect(objectTypeService.getCurrentYearExpenseObjectTypes()).andReturn(SufficientFundsTestFixtures.getTestCurrentYearExpenseObjectTypes("EX"));
        EasyMock.expect(businessObjectService.findByPrimaryKey(SufficientFundBalances.class, SufficientFundsTestFixtures.getTestSufficientFundBalancesKeys("BL", "1234567"))).andReturn(null);
        EasyMock.expect(businessObjectService.findMatching(SufficientFundRebuild.class, SufficientFundsTestFixtures.getTestSufficientFundRebuildKeys("BL", "1234567"))).andReturn(new ArrayList());
        EasyMock.expect(generalLedgerPendingEntryService.getExpenseSummary(2017, "BL", "1234567", "    ", true, false, doctypes)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getExpenseSummary(2017, "BL", "1234567", "    ", false, false, doctypes)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getBudgetSummary(2017, "BL", "1234567", "    ", false)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getEncumbranceSummary(2017, "BL", "1234567", "    ", true, false, doctypes)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getEncumbranceSummary(2017, "BL", "1234567", "    ", false, false, doctypes)).andReturn(KualiDecimal.ZERO);

        replayAll();

        List<SufficientFundsItem> transactions = sufficientFundsService.checkSufficientFunds(doc);
        Assert.assertEquals(0, transactions.size());
    }

    @Test
    public void testAccountCheckingTwoSimilarLines() {
        List<GeneralLedgerPendingEntry> glpes = new ArrayList<>();
        glpes.add(SufficientFundsTestFixtures.getTestGeneralLedgerPendingEntry("BL", "1234567", "4166", KFSConstants.SF_TYPE_ACCOUNT, new KualiDecimal("1.00")));
        glpes.add(SufficientFundsTestFixtures.getTestGeneralLedgerPendingEntry("BL", "1234567", "4166", KFSConstants.SF_TYPE_ACCOUNT, new KualiDecimal("2.00")));

        GeneralLedgerPostingDocument doc = SufficientFundsTestFixtures.getTestGeneralLedgerPostingDocument(glpes);

        List<String> doctypes = Collections.singletonList("DOC12345");
        EasyMock.expect(optionsService.getCurrentYearOptions()).andReturn(SufficientFundsTestFixtures.getTestSystemOptions(2017, true));
        EasyMock.expect(objectTypeService.getCurrentYearExpenseObjectTypes()).andReturn(SufficientFundsTestFixtures.getTestCurrentYearExpenseObjectTypes("EX"));
        EasyMock.expect(businessObjectService.findByPrimaryKey(SufficientFundBalances.class, SufficientFundsTestFixtures.getTestSufficientFundBalancesKeys("BL", "1234567"))).andReturn(null);
        EasyMock.expect(businessObjectService.findMatching(SufficientFundRebuild.class, SufficientFundsTestFixtures.getTestSufficientFundRebuildKeys("BL", "1234567"))).andReturn(new ArrayList());
        EasyMock.expect(generalLedgerPendingEntryService.getExpenseSummary(2017, "BL", "1234567", "    ", true, false, doctypes)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getExpenseSummary(2017, "BL", "1234567", "    ", false, false, doctypes)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getBudgetSummary(2017, "BL", "1234567", "    ", false)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getEncumbranceSummary(2017, "BL", "1234567", "    ", true, false, doctypes)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getEncumbranceSummary(2017, "BL", "1234567", "    ", false, false, doctypes)).andReturn(KualiDecimal.ZERO);

        replayAll();

        List<SufficientFundsItem> transactions = sufficientFundsService.checkSufficientFunds(doc);
        Assert.assertEquals(1, transactions.size());

        SufficientFundsItem item = transactions.get(0);
        Assert.assertEquals(2017, item.getYear().getUniversityFiscalYear().intValue());
        Assert.assertEquals("BL", item.getAccount().getChartOfAccountsCode());
        Assert.assertEquals("1234567", item.getAccount().getAccountNumber());
        Assert.assertEquals(new KualiDecimal("3.00"), item.getAmount());
    }

    @Test
    public void testAccountCheckingTwoDifferentLines() {
        List<GeneralLedgerPendingEntry> glpes = new ArrayList<>();
        glpes.add(SufficientFundsTestFixtures.getTestGeneralLedgerPendingEntry("BL", "1234567", "4166", KFSConstants.SF_TYPE_ACCOUNT, new KualiDecimal("1.00")));
        glpes.add(SufficientFundsTestFixtures.getTestGeneralLedgerPendingEntry("IN", "7654321", "4166", KFSConstants.SF_TYPE_ACCOUNT, new KualiDecimal("2.00")));

        GeneralLedgerPostingDocument doc = SufficientFundsTestFixtures.getTestGeneralLedgerPostingDocument(glpes);

        List<String> doctypes = Collections.singletonList("DOC12345");
        EasyMock.expect(optionsService.getCurrentYearOptions()).andReturn(SufficientFundsTestFixtures.getTestSystemOptions(2017, true));
        EasyMock.expect(objectTypeService.getCurrentYearExpenseObjectTypes()).andReturn(SufficientFundsTestFixtures.getTestCurrentYearExpenseObjectTypes("EX")).times(2);
        EasyMock.expect(businessObjectService.findByPrimaryKey(SufficientFundBalances.class, SufficientFundsTestFixtures.getTestSufficientFundBalancesKeys("BL", "1234567"))).andReturn(null);
        EasyMock.expect(businessObjectService.findByPrimaryKey(SufficientFundBalances.class, SufficientFundsTestFixtures.getTestSufficientFundBalancesKeys("IN", "7654321"))).andReturn(null);
        EasyMock.expect(businessObjectService.findMatching(SufficientFundRebuild.class, SufficientFundsTestFixtures.getTestSufficientFundRebuildKeys("BL", "1234567"))).andReturn(new ArrayList());
        EasyMock.expect(businessObjectService.findMatching(SufficientFundRebuild.class, SufficientFundsTestFixtures.getTestSufficientFundRebuildKeys("IN", "7654321"))).andReturn(new ArrayList());

        EasyMock.expect(generalLedgerPendingEntryService.getExpenseSummary(2017, "BL", "1234567", "    ", true, false, doctypes)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getExpenseSummary(2017, "BL", "1234567", "    ", false, false, doctypes)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getBudgetSummary(2017, "BL", "1234567", "    ", false)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getEncumbranceSummary(2017, "BL", "1234567", "    ", true, false, doctypes)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getEncumbranceSummary(2017, "BL", "1234567", "    ", false, false, doctypes)).andReturn(KualiDecimal.ZERO);

        EasyMock.expect(generalLedgerPendingEntryService.getExpenseSummary(2017, "IN", "7654321", "    ", true, false, doctypes)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getExpenseSummary(2017, "IN", "7654321", "    ", false, false, doctypes)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getBudgetSummary(2017, "IN", "7654321", "    ", false)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getEncumbranceSummary(2017, "IN", "7654321", "    ", true, false, doctypes)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getEncumbranceSummary(2017, "IN", "7654321", "    ", false, false, doctypes)).andReturn(KualiDecimal.ZERO);

        replayAll();

        List<SufficientFundsItem> transactions = sufficientFundsService.checkSufficientFunds(doc);
        Assert.assertEquals(2, transactions.size());

        Optional<SufficientFundsItem> oBlItem = transactions.stream().filter(i -> "BL".equals(i.getAccount().getChartOfAccountsCode())).findFirst();
        Assert.assertTrue(oBlItem.isPresent());
        SufficientFundsItem blItem = oBlItem.get();

        Optional<SufficientFundsItem> oInItem = transactions.stream().filter(i -> "IN".equals(i.getAccount().getChartOfAccountsCode())).findFirst();
        Assert.assertTrue(oInItem.isPresent());
        SufficientFundsItem inItem = oInItem.get();

        Assert.assertEquals(2017, blItem.getYear().getUniversityFiscalYear().intValue());
        Assert.assertEquals("BL", blItem.getAccount().getChartOfAccountsCode());
        Assert.assertEquals("1234567", blItem.getAccount().getAccountNumber());
        Assert.assertEquals(new KualiDecimal("1.00"), blItem.getAmount());

        Assert.assertEquals(2017, inItem.getYear().getUniversityFiscalYear().intValue());
        Assert.assertEquals("IN", inItem.getAccount().getChartOfAccountsCode());
        Assert.assertEquals("7654321", inItem.getAccount().getAccountNumber());
        Assert.assertEquals(new KualiDecimal("2.00"), inItem.getAmount());
    }

    @Test
    public void testCashAtAccountNonCashChecking() {
        List<GeneralLedgerPendingEntry> glpes = new ArrayList<>();
        glpes.add(SufficientFundsTestFixtures.getTestGeneralLedgerPendingEntry("BL", "1234567", "4166", KFSConstants.SF_TYPE_CASH_AT_ACCOUNT, new KualiDecimal("1.00")));

        GeneralLedgerPostingDocument doc = SufficientFundsTestFixtures.getTestGeneralLedgerPostingDocument(glpes);

        List<String> doctypes = Collections.singletonList("DOC12345");
        EasyMock.expect(optionsService.getCurrentYearOptions()).andReturn(SufficientFundsTestFixtures.getTestSystemOptions(2017, true));
        EasyMock.expect(objectTypeService.getCurrentYearExpenseObjectTypes()).andReturn(SufficientFundsTestFixtures.getTestCurrentYearExpenseObjectTypes("EX"));
        EasyMock.expect(businessObjectService.findByPrimaryKey(SufficientFundBalances.class, SufficientFundsTestFixtures.getTestSufficientFundBalancesKeys("BL", "1234567"))).andReturn(null);
        EasyMock.expect(businessObjectService.findMatching(SufficientFundRebuild.class, SufficientFundsTestFixtures.getTestSufficientFundRebuildKeys("BL", "1234567"))).andReturn(new ArrayList());
        EasyMock.expect(generalLedgerPendingEntryService.getExpenseSummary(2017, "BL", "1234567", "    ", true, false, doctypes)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getExpenseSummary(2017, "BL", "1234567", "    ", false, false, doctypes)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getBudgetSummary(2017, "BL", "1234567", "    ", false)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getEncumbranceSummary(2017, "BL", "1234567", "    ", true, false, doctypes)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getEncumbranceSummary(2017, "BL", "1234567", "    ", false, false, doctypes)).andReturn(KualiDecimal.ZERO);

        replayAll();

        List<SufficientFundsItem> transactions = sufficientFundsService.checkSufficientFunds(doc);
        Assert.assertEquals(0, transactions.size());
    }

    @Test
    public void testBudgetChecking() {
        List<GeneralLedgerPendingEntry> glpes = new ArrayList<>();
        glpes.add(SufficientFundsTestFixtures.getTestGeneralLedgerPendingEntry("BL", "1234567", "8000", KFSConstants.SF_TYPE_LEVEL, new KualiDecimal("1.00")));

        GeneralLedgerPostingDocument doc = SufficientFundsTestFixtures.getTestGeneralLedgerPostingDocument(glpes);

        List<String> doctypes = Collections.singletonList("DOC12345");
        EasyMock.expect(optionsService.getCurrentYearOptions()).andReturn(SufficientFundsTestFixtures.getTestSystemOptions(2017, true));
        EasyMock.expect(objectTypeService.getCurrentYearExpenseObjectTypes()).andReturn(SufficientFundsTestFixtures.getTestCurrentYearExpenseObjectTypes("XX"));
        EasyMock.expect(businessObjectService.findByPrimaryKey(SufficientFundBalances.class, SufficientFundsTestFixtures.getTestSufficientFundBalancesKeys("BL", "1234567"))).andReturn(null);
        EasyMock.expect(businessObjectService.findMatching(SufficientFundRebuild.class, SufficientFundsTestFixtures.getTestSufficientFundRebuildKeys("BL", "1234567"))).andReturn(new ArrayList());
        EasyMock.expect(generalLedgerPendingEntryService.getExpenseSummary(2017, "BL", "1234567", "    ", true, false, doctypes)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getExpenseSummary(2017, "BL", "1234567", "    ", false, false, doctypes)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getBudgetSummary(2017, "BL", "1234567", "    ", false)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getEncumbranceSummary(2017, "BL", "1234567", "    ", true, false, doctypes)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getEncumbranceSummary(2017, "BL", "1234567", "    ", false, false, doctypes)).andReturn(KualiDecimal.ZERO);

        replayAll();

        List<SufficientFundsItem> transactions = sufficientFundsService.checkSufficientFunds(doc);
        Assert.assertEquals(0, transactions.size());
    }

    @Test
    public void testRebuild() {
        List<GeneralLedgerPendingEntry> glpes = new ArrayList<>();
        glpes.add(SufficientFundsTestFixtures.getTestGeneralLedgerPendingEntry("BL", "1234567", "4166", KFSConstants.SF_TYPE_ACCOUNT, new KualiDecimal("1.00")));

        GeneralLedgerPostingDocument doc = SufficientFundsTestFixtures.getTestGeneralLedgerPostingDocument(glpes);

        List<String> doctypes = Collections.singletonList("DOC12345");
        EasyMock.expect(optionsService.getCurrentYearOptions()).andReturn(SufficientFundsTestFixtures.getTestSystemOptions(2017, true));
        EasyMock.expect(objectTypeService.getCurrentYearExpenseObjectTypes()).andReturn(SufficientFundsTestFixtures.getTestCurrentYearExpenseObjectTypes("EX"));
        EasyMock.expect(businessObjectService.findByPrimaryKey(SufficientFundBalances.class, SufficientFundsTestFixtures.getTestSufficientFundBalancesKeys("BL", "1234567"))).andReturn(null);
        EasyMock.expect(businessObjectService.findMatching(SufficientFundRebuild.class, SufficientFundsTestFixtures.getTestSufficientFundRebuildKeys("BL", "1234567"))).andReturn(SufficientFundsTestFixtures.getTestSufficientFundRebuildList("BL", "1234567", "    "));
        EasyMock.expect(generalLedgerPendingEntryService.getExpenseSummary(2017, "BL", "1234567", "    ", true, false, doctypes)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getExpenseSummary(2017, "BL", "1234567", "    ", false, false, doctypes)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getBudgetSummary(2017, "BL", "1234567", "    ", false)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getEncumbranceSummary(2017, "BL", "1234567", "    ", true, false, doctypes)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getEncumbranceSummary(2017, "BL", "1234567", "    ", false, false, doctypes)).andReturn(KualiDecimal.ZERO);

        replayAll();

        List<SufficientFundsItem> transactions = sufficientFundsService.checkSufficientFunds(doc);
        Assert.assertEquals(1, transactions.size());

        SufficientFundsItem item = transactions.get(0);
        Assert.assertEquals(2017, item.getYear().getUniversityFiscalYear().intValue());
        Assert.assertEquals("BL", item.getAccount().getChartOfAccountsCode());
        Assert.assertEquals("1234567", item.getAccount().getAccountNumber());
        Assert.assertEquals(new KualiDecimal("1.00"), item.getAmount());
    }

    @Test
    public void testBalanceNegative() {
        List<GeneralLedgerPendingEntry> glpes = new ArrayList<>();
        glpes.add(SufficientFundsTestFixtures.getTestGeneralLedgerPendingEntry("BL", "1234567", "8000", KFSConstants.SF_TYPE_CASH_AT_ACCOUNT, new KualiDecimal("1.00"), true, "CB"));

        GeneralLedgerPostingDocument doc = SufficientFundsTestFixtures.getTestGeneralLedgerPostingDocument(glpes);

        List<String> doctypes = Collections.singletonList("DOC12345");
        EasyMock.expect(optionsService.getCurrentYearOptions()).andReturn(SufficientFundsTestFixtures.getTestSystemOptions(2017, true));
        EasyMock.expect(objectTypeService.getCurrentYearExpenseObjectTypes()).andReturn(SufficientFundsTestFixtures.getTestCurrentYearExpenseObjectTypes("EX"));
        EasyMock.expect(businessObjectService.findByPrimaryKey(SufficientFundBalances.class, SufficientFundsTestFixtures.getTestSufficientFundBalancesKeys("BL", "1234567"))).andReturn(null);
        EasyMock.expect(businessObjectService.findMatching(SufficientFundRebuild.class, SufficientFundsTestFixtures.getTestSufficientFundRebuildKeys("BL", "1234567"))).andReturn(new ArrayList<>());
        EasyMock.expect(generalLedgerPendingEntryService.getExpenseSummary(2017, "BL", "1234567", "    ", false, false, doctypes)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getBudgetSummary(2017, "BL", "1234567", "    ", false)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getEncumbranceSummary(2017, "BL", "1234567", "    ", true, false, doctypes)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getEncumbranceSummary(2017, "BL", "1234567", "    ", false, false, doctypes)).andReturn(KualiDecimal.ZERO);

        replayAll();

        List<SufficientFundsItem> transactions = sufficientFundsService.checkSufficientFunds(doc);
        Assert.assertEquals(0, transactions.size());
    }

    @Test
    public void testBalancePriorYear() {
        List<GeneralLedgerPendingEntry> glpes = new ArrayList<>();
        glpes.add(SufficientFundsTestFixtures.getTestGeneralLedgerPendingEntry("BL", "1234567", "8000", KFSConstants.SF_TYPE_CASH_AT_ACCOUNT, new KualiDecimal("-1.00"), true, "BB"));

        GeneralLedgerPostingDocument doc = SufficientFundsTestFixtures.getTestGeneralLedgerPostingDocument(glpes);

        List<String> doctypes = Collections.singletonList("DOC12345");
        EasyMock.expect(optionsService.getCurrentYearOptions()).andReturn(SufficientFundsTestFixtures.getTestSystemOptions(2017, true));
        EasyMock.expect(objectTypeService.getCurrentYearExpenseObjectTypes()).andReturn(SufficientFundsTestFixtures.getTestCurrentYearExpenseObjectTypes("EX"));
        EasyMock.expect(businessObjectService.findByPrimaryKey(SufficientFundBalances.class, SufficientFundsTestFixtures.getTestSufficientFundBalancesKeys("BL", "1234567"))).andReturn(null);
        EasyMock.expect(businessObjectService.findMatching(SufficientFundRebuild.class, SufficientFundsTestFixtures.getTestSufficientFundRebuildKeys("BL", "1234567"))).andReturn(new ArrayList<>());
        EasyMock.expect(businessObjectService.findByPrimaryKey(SufficientFundBalances.class, SufficientFundsTestFixtures.getTestSufficientFundBalancesKeys(2016, "BL", "1234567", "    "))).andReturn(null);
        EasyMock.expect(generalLedgerPendingEntryService.getCashSummary(Arrays.asList(new Integer[]{2017, 2016}), "BL", "1234567", true)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getCashSummary(Arrays.asList(new Integer[]{2017, 2016}), "BL", "1234567", false)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getActualSummary(Arrays.asList(new Integer[]{2017, 2016}), "BL", "1234567", true)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getActualSummary(Arrays.asList(new Integer[]{2017, 2016}), "BL", "1234567", false)).andReturn(KualiDecimal.ZERO);

        replayAll();

        List<SufficientFundsItem> transactions = sufficientFundsService.checkSufficientFunds(doc);
        Assert.assertEquals(1, transactions.size());

        SufficientFundsItem item = transactions.get(0);
        Assert.assertEquals(2017, item.getYear().getUniversityFiscalYear().intValue());
        Assert.assertEquals("BL", item.getAccount().getChartOfAccountsCode());
        Assert.assertEquals("1234567", item.getAccount().getAccountNumber());
        Assert.assertEquals(new KualiDecimal("-1.00"), item.getAmount());
    }

    @Test
    public void testBalancePriorYearBBLoaded() {
        List<GeneralLedgerPendingEntry> glpes = new ArrayList<>();
        glpes.add(SufficientFundsTestFixtures.getTestGeneralLedgerPendingEntry("BL", "1234567", "8000", KFSConstants.SF_TYPE_CASH_AT_ACCOUNT, new KualiDecimal("-1.00"), true, "BB"));

        GeneralLedgerPostingDocument doc = SufficientFundsTestFixtures.getTestGeneralLedgerPostingDocument(glpes);

        List<String> doctypes = Collections.singletonList("DOC12345");
        EasyMock.expect(optionsService.getCurrentYearOptions()).andReturn(SufficientFundsTestFixtures.getTestSystemOptions(2017, true, true));
        EasyMock.expect(objectTypeService.getCurrentYearExpenseObjectTypes()).andReturn(SufficientFundsTestFixtures.getTestCurrentYearExpenseObjectTypes("EX"));
        EasyMock.expect(businessObjectService.findByPrimaryKey(SufficientFundBalances.class, SufficientFundsTestFixtures.getTestSufficientFundBalancesKeys("BL", "1234567"))).andReturn(null);
        EasyMock.expect(businessObjectService.findMatching(SufficientFundRebuild.class, SufficientFundsTestFixtures.getTestSufficientFundRebuildKeys("BL", "1234567"))).andReturn(new ArrayList<>());
        EasyMock.expect(businessObjectService.findByPrimaryKey(SufficientFundBalances.class, SufficientFundsTestFixtures.getTestSufficientFundBalancesKeys(2016, "BL", "1234567", "    "))).andReturn(null);

        EasyMock.expect(generalLedgerPendingEntryService.getCashSummary(Arrays.asList(new Integer[]{2017}), "BL", "1234567", true)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getCashSummary(Arrays.asList(new Integer[]{2017}), "BL", "1234567", false)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getActualSummary(Arrays.asList(new Integer[]{2017}), "BL", "1234567", true)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getActualSummary(Arrays.asList(new Integer[]{2017}), "BL", "1234567", false)).andReturn(KualiDecimal.ZERO);

        replayAll();

        List<SufficientFundsItem> transactions = sufficientFundsService.checkSufficientFunds(doc);
        Assert.assertEquals(1, transactions.size());

        SufficientFundsItem item = transactions.get(0);
        Assert.assertEquals(2017, item.getYear().getUniversityFiscalYear().intValue());
        Assert.assertEquals("BL", item.getAccount().getChartOfAccountsCode());
        Assert.assertEquals("1234567", item.getAccount().getAccountNumber());
        Assert.assertEquals(new KualiDecimal("-1.00"), item.getAmount());
    }

    @Test
    public void testHasSufficentFunds() {
        List<GeneralLedgerPendingEntry> glpes = new ArrayList<>();
        glpes.add(SufficientFundsTestFixtures.getTestGeneralLedgerPendingEntry("BL", "1234567", "8000", KFSConstants.SF_TYPE_ACCOUNT, new KualiDecimal("1.00"), true, "AC"));

        GeneralLedgerPostingDocument doc = SufficientFundsTestFixtures.getTestGeneralLedgerPostingDocument(glpes);

        List<String> doctypes = Collections.singletonList("DOC12345");
        EasyMock.expect(optionsService.getCurrentYearOptions()).andReturn(SufficientFundsTestFixtures.getTestSystemOptions(2017, true, false));
        EasyMock.expect(objectTypeService.getCurrentYearExpenseObjectTypes()).andReturn(SufficientFundsTestFixtures.getTestCurrentYearExpenseObjectTypes("EX"));
        EasyMock.expect(businessObjectService.findByPrimaryKey(SufficientFundBalances.class, SufficientFundsTestFixtures.getTestSufficientFundBalancesKeys("BL", "1234567"))).andReturn(SufficientFundsTestFixtures.getTestSufficientFundBalances("BL", "1234567"));
        EasyMock.expect(generalLedgerPendingEntryService.getExpenseSummary(2017, "BL", "1234567", "    ", true, false, doctypes)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getExpenseSummary(2017, "BL", "1234567", "    ", false, false, doctypes)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getBudgetSummary(2017, "BL", "1234567", "    ", false)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getEncumbranceSummary(2017, "BL", "1234567", "    ", true, false, doctypes)).andReturn(KualiDecimal.ZERO);
        EasyMock.expect(generalLedgerPendingEntryService.getEncumbranceSummary(2017, "BL", "1234567", "    ", false, false, doctypes)).andReturn(KualiDecimal.ZERO);

        replayAll();

        List<SufficientFundsItem> transactions = sufficientFundsService.checkSufficientFunds(doc);
        Assert.assertEquals(0, transactions.size());
    }
}
