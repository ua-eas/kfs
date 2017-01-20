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
package org.kuali.kfs.module.cam.batch.service;

import org.kuali.kfs.kns.bo.Step;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.batch.ExtractProcessLog;
import org.kuali.kfs.module.cam.batch.ExtractStep;
import org.kuali.kfs.module.cam.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cam.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.cam.businessobject.PurchasingAccountsPayableItemAsset;
import org.kuali.kfs.module.cam.businessobject.PurchasingAccountsPayableLineAssetAccount;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.ProxyUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.core.api.datetime.DateTimeService;

import java.io.File;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class tests the extract step involved in CAB Batch job
 */
public class ExtractStepTest extends BatchTestBase {

    /**
     * This class prevents creating PDF files for every test run. Also it helps in asserting the extract log content
     */
    private static class MockBatchExtractReportService implements BatchExtractReportService {
        public File generateStatusReportPDF(ExtractProcessLog extractProcessLog) {
            assertNotNull(extractProcessLog);
            assertTrue(extractProcessLog.isSuccess());
            assertEquals(Integer.valueOf(13), extractProcessLog.getTotalGlCount());
            assertEquals(Integer.valueOf(2), extractProcessLog.getNonPurApGlCount());
            assertEquals(Integer.valueOf(11), extractProcessLog.getPurApGlCount());
            assertNotNull(extractProcessLog.getStartTime());
            assertNotNull(extractProcessLog.getFinishTime());
            assertNotNull(extractProcessLog.getLastExtractTime());
            assertTrue(extractProcessLog.getIgnoredGLEntries() == null || extractProcessLog.getIgnoredGLEntries().isEmpty());
            assertTrue(extractProcessLog.getMismatchedGLEntries() == null || extractProcessLog.getMismatchedGLEntries().isEmpty());
            assertTrue(extractProcessLog.getDuplicateGLEntries() == null || extractProcessLog.getDuplicateGLEntries().isEmpty());
            return null;
        }

        public File generateMismatchReportPDF(ExtractProcessLog extractProcessLog) {
            return null;
        }
    }

    private Timestamp beforeRun = null;

    private DateTimeService dateTimeService;
    private BusinessObjectService boService;
    private ExtractStep extractStep;

    @ConfigureContext(session = UserNameFixture.khuntley, shouldCommitTransactions = false)
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dateTimeService = SpringContext.getBean(DateTimeService.class);
        extractStep = (ExtractStep) ProxyUtils.getTargetIfProxied(SpringContext.getBean(Step.class, "cabExtractStep"));
        beforeRun = dateTimeService.getCurrentTimestamp();
        extractStep.setBatchExtractReportService(new MockBatchExtractReportService());
        boService = SpringContext.getBean(BusinessObjectService.class);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        extractStep.setBatchExtractReportService(SpringContext.getBean(BatchExtractReportService.class));
    }

    //NO RUN SETUP
    public void testNothing() {
    }

    public void NORUN_testExecute() throws Exception {
//        java.sql.Date currentSqlDate = SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
        Date currentSqlDate = dateTimeService.getCurrentSqlDate();
        extractStep.execute("CabBatchExtractJob", dateTimeService.getCurrentDate());
        // Count of GL lines

        Map<String, Object> m = new HashMap<String, Object>();
        m.put("transactionDate", dateTimeService.getCurrentSqlDate());
        Collection<GeneralLedgerEntry> gls = boService.findMatching(GeneralLedgerEntry.class, m);
        assertEquals(13, gls.size());

        // Count of purap docs
        Map<String, String> m2 = new HashMap<String, String>();
        m2.put("activityStatusCode", CamsConstants.ActivityStatusCode.NEW);
        Collection<PurchasingAccountsPayableDocument> allCabDocs = boService.findMatching(PurchasingAccountsPayableDocument.class, m2);
        assertEquals(7, allCabDocs.size());

        // Count of purap items ---- drill through doc header to get itemAssets and test against qty = 1....
        //Map<String, Object> keys = new HashMap<String, Object>();
        //keys.put(CabPropertyConstants.PurchasingAccountsPayableItemAsset.DOCUMENT_NUMBER, cabPurapDoc.getDocumentNumber());
        //keys.put(CabPropertyConstants.PurchasingAccountsPayableItemAsset.ACCOUNTS_PAYABLE_LINE_ITEM_IDENTIFIER, apItem.getItemIdentifier());
        //Collection<PurchasingAccountsPayableItemAsset> matchingItems = businessObjectService.findMatching(PurchasingAccountsPayableItemAsset.class, keys);

        Map<String, Object> m3 = new HashMap<String, Object>();
        m3.put("activityStatusCode", CamsConstants.ActivityStatusCode.NEW);
        Collection<PurchasingAccountsPayableItemAsset> allCabItems = boService.findMatching(PurchasingAccountsPayableItemAsset.class, m3);
        for (PurchasingAccountsPayableItemAsset aci : allCabItems) {

            System.out.println(aci.isActive() + " - " + aci.getActivityStatusCode());
        }
        //assertEquals(14, allCabItems.size());

        // Count of purap account lines
        Collection<PurchasingAccountsPayableLineAssetAccount> allCabAccts = boService.findAll(PurchasingAccountsPayableLineAssetAccount.class);
        //for(PurchasingAccountsPayableLineAssetAccount aca:allCabAccts){

        //     System.out.println(aca.isActive()+" - "+aca.getGeneralLedgerEntry().getTransactionDate());
        // }
        //assertEquals(17, allCabAccts.size());

        // assert the extract date value
        SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");
        assertEquals(fmt.format(currentSqlDate), findCabExtractTimeParam().getValue().substring(0, 10));
    }
    // END NO RUN SETUP
}
