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
package org.kuali.kfs.sys.batch.service.impl;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.batch.dataaccess.DetectDocumentsMissingPendingEntriesDao;
import org.kuali.kfs.sys.businessobject.DocumentHeaderData;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class DetectDocumentsMissingPendingEntriesServiceImplDiscoverTest {
    private DetectDocumentsMissingPendingEntriesServiceImpl detectDocumentsMissingPendingEntriesService;
    private DetectDocumentsMissingPendingEntriesDao detectDocumentsMissingPendingEntriesDao;
    private ParameterService parameterService;

    @Before
    public void setUp() {
        detectDocumentsMissingPendingEntriesDao = EasyMock.createMock(DetectDocumentsMissingPendingEntriesDao.class);
        parameterService = EasyMock.createMock(ParameterService.class);

        detectDocumentsMissingPendingEntriesService = new DetectDocumentsMissingPendingEntriesServiceImpl();
        detectDocumentsMissingPendingEntriesService.setDetectDocumentsMissingPendingEntriesDao(detectDocumentsMissingPendingEntriesDao);
        detectDocumentsMissingPendingEntriesService.setParameterService(parameterService);
    }

    @Test
    public void testDiscoverNoExceptions() {
        Date testDate = new Date();

        List<DocumentHeaderData> documentHeaderDataList = new ArrayList<>();
        DocumentHeaderData documentHeaderData1 = new DocumentHeaderData("AAAA","DT1");
        DocumentHeaderData documentHeaderData2 = new DocumentHeaderData("BBBB","DT2");
        DocumentHeaderData documentHeaderData3 = new DocumentHeaderData("CCCC","DT3");
        documentHeaderDataList.add(documentHeaderData1);
        documentHeaderDataList.add(documentHeaderData2);
        documentHeaderDataList.add(documentHeaderData3);

        EasyMock.expect(parameterService.getParameterValuesAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, KFSParameterKeyConstants.DetectDocumentsMissingPendingEntriesConstants.LEDGER_ENTRY_GENERATING_DOCUMENT_TYPES)).andReturn(new ArrayList<>());
        EasyMock.expect(detectDocumentsMissingPendingEntriesDao.discoverLedgerDocumentsWithoutPendingEntries(testDate,new ArrayList<>())).andReturn(documentHeaderDataList);
        EasyMock.replay(detectDocumentsMissingPendingEntriesDao, parameterService);

        List<DocumentHeaderData> results = detectDocumentsMissingPendingEntriesService.discoverGeneralLedgerDocumentsWithoutPendingEntries(testDate);

        Assert.assertEquals(3,results.size());
    }

    @Test
    public void testNONCheckCTRLDocument() throws Exception {
        Date testDate = new Date();

        List<DocumentHeaderData> documentHeaderDataList = new ArrayList<>();
        DocumentHeaderData documentHeaderData1 = new DocumentHeaderData("AAAA","CTRL");
        documentHeaderDataList.add(documentHeaderData1);

        List<String> docTypes = new ArrayList<>();
        docTypes.add("CTRL");
        docTypes.add("DV");
        EasyMock.expect(parameterService.getParameterValuesAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, KFSParameterKeyConstants.DetectDocumentsMissingPendingEntriesConstants.LEDGER_ENTRY_GENERATING_DOCUMENT_TYPES)).andReturn(docTypes);
        EasyMock.expect(detectDocumentsMissingPendingEntriesDao.discoverLedgerDocumentsWithoutPendingEntries(testDate,docTypes)).andReturn(documentHeaderDataList);
        EasyMock.expect(detectDocumentsMissingPendingEntriesDao.getCustomerPaymentMediumCodeFromCashControlDocument("AAAA")).andReturn(Optional.of("CA"));
        EasyMock.replay(detectDocumentsMissingPendingEntriesDao, parameterService);

        List<DocumentHeaderData> results = detectDocumentsMissingPendingEntriesService.discoverGeneralLedgerDocumentsWithoutPendingEntries(testDate);

        Assert.assertEquals(0,results.size());
    }

    @Test
    public void testCheckCTRLDocument() throws Exception {
        Date testDate = new Date();

        List<DocumentHeaderData> documentHeaderDataList = new ArrayList<>();
        DocumentHeaderData documentHeaderData1 = new DocumentHeaderData("AAAA","CTRL");
        documentHeaderDataList.add(documentHeaderData1);

        EasyMock.expect(parameterService.getParameterValuesAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, KFSParameterKeyConstants.DetectDocumentsMissingPendingEntriesConstants.LEDGER_ENTRY_GENERATING_DOCUMENT_TYPES)).andReturn(new ArrayList<>());
        EasyMock.expect(detectDocumentsMissingPendingEntriesDao.discoverLedgerDocumentsWithoutPendingEntries(testDate,new ArrayList<>())).andReturn(documentHeaderDataList);
        EasyMock.expect(detectDocumentsMissingPendingEntriesDao.getCustomerPaymentMediumCodeFromCashControlDocument("AAAA")).andReturn(Optional.of("CK"));
        EasyMock.replay(detectDocumentsMissingPendingEntriesDao, parameterService);

        List<DocumentHeaderData> results = detectDocumentsMissingPendingEntriesService.discoverGeneralLedgerDocumentsWithoutPendingEntries(testDate);

        Assert.assertEquals(1,results.size());
    }

    @Test
    public void testBadCTRLDocument() throws Exception {
        Date testDate = new Date();

        List<DocumentHeaderData> documentHeaderDataList = new ArrayList<>();
        DocumentHeaderData documentHeaderData1 = new DocumentHeaderData("AAAA","CTRL");
        documentHeaderDataList.add(documentHeaderData1);

        EasyMock.expect(parameterService.getParameterValuesAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, KFSParameterKeyConstants.DetectDocumentsMissingPendingEntriesConstants.LEDGER_ENTRY_GENERATING_DOCUMENT_TYPES)).andReturn(new ArrayList<>());
        EasyMock.expect(detectDocumentsMissingPendingEntriesDao.discoverLedgerDocumentsWithoutPendingEntries(testDate,new ArrayList<>())).andReturn(documentHeaderDataList);
        EasyMock.expect(detectDocumentsMissingPendingEntriesDao.getCustomerPaymentMediumCodeFromCashControlDocument("AAAA")).andReturn(Optional.empty());
        EasyMock.replay(detectDocumentsMissingPendingEntriesDao, parameterService);

        try {
            detectDocumentsMissingPendingEntriesService.discoverGeneralLedgerDocumentsWithoutPendingEntries(testDate);
            Assert.fail();
        } catch (RuntimeException e) {
            // This is expected
        }
    }
}
