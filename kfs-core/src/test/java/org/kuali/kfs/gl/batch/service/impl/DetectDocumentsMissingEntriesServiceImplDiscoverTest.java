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
package org.kuali.kfs.gl.batch.service.impl;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.gl.batch.service.impl.DetectDocumentsMissingEntriesServiceImpl;
import org.kuali.kfs.gl.batch.DetectDocumentsMissingEntriesStep;
import org.kuali.kfs.gl.batch.dataaccess.DetectDocumentsMissingEntriesDao;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.businessobject.DocumentHeaderData;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class DetectDocumentsMissingEntriesServiceImplDiscoverTest {
    private DetectDocumentsMissingEntriesServiceImpl detectDocumentsMissingEntriesService;
    private DetectDocumentsMissingEntriesDao detectDocumentsMissingEntriesDao;
    private ParameterService parameterService;

    @Before
    public void setUp() {
        detectDocumentsMissingEntriesDao = EasyMock.createMock(DetectDocumentsMissingEntriesDao.class);
        parameterService = EasyMock.createMock(ParameterService.class);

        detectDocumentsMissingEntriesService = new DetectDocumentsMissingEntriesServiceImpl();
        detectDocumentsMissingEntriesService.setDetectDocumentsMissingEntriesDao(detectDocumentsMissingEntriesDao);
        detectDocumentsMissingEntriesService.setParameterService(parameterService);
    }

    @Test
    public void testDiscoverNoExceptions() {
        List<DocumentHeaderData> documentHeaderDataList = new ArrayList<>();
        DocumentHeaderData documentHeaderData1 = new DocumentHeaderData("AAAA", "DT1");
        DocumentHeaderData documentHeaderData2 = new DocumentHeaderData("BBBB", "DT2");
        DocumentHeaderData documentHeaderData3 = new DocumentHeaderData("CCCC", "DT3");
        documentHeaderDataList.add(documentHeaderData1);
        documentHeaderDataList.add(documentHeaderData2);
        documentHeaderDataList.add(documentHeaderData3);

        EasyMock.expect(parameterService.getParameterValueAsString(DetectDocumentsMissingEntriesStep.class,
                KFSParameterKeyConstants.DetectDocumentsMissingEntriesConstants.LOOK_BACK_DAYS, "7")).andReturn("7");
        EasyMock.expect(parameterService.getParameterValuesAsString(DetectDocumentsMissingEntriesStep.class,
                KFSParameterKeyConstants.DetectDocumentsMissingEntriesConstants.ENTRY_GENERATING_DOCUMENT_TYPES))
                .andReturn(new ArrayList<>());
        EasyMock.expect(detectDocumentsMissingEntriesDao.discoverLedgerDocumentsWithoutEntries(EasyMock.isA(Date.class),
                EasyMock.eq(new ArrayList<>()))).andReturn(documentHeaderDataList);
        EasyMock.replay(detectDocumentsMissingEntriesDao, parameterService);

        List<DocumentHeaderData> results = detectDocumentsMissingEntriesService
                .discoverGeneralLedgerDocumentsWithoutEntries();

        Assert.assertEquals(3, results.size());
    }

    @Test
    public void testNONCheckCTRLDocument() throws Exception {
        List<DocumentHeaderData> documentHeaderDataList = new ArrayList<>();
        DocumentHeaderData documentHeaderData1 = new DocumentHeaderData("AAAA", "CTRL");
        documentHeaderDataList.add(documentHeaderData1);

        List<String> docTypes = new ArrayList<>();
        docTypes.add("CTRL");
        docTypes.add("DV");
        EasyMock.expect(parameterService.getParameterValueAsString(DetectDocumentsMissingEntriesStep.class,
                KFSParameterKeyConstants.DetectDocumentsMissingEntriesConstants.LOOK_BACK_DAYS, "7")).andReturn("7");
        EasyMock.expect(parameterService.getParameterValuesAsString(DetectDocumentsMissingEntriesStep.class,
                KFSParameterKeyConstants.DetectDocumentsMissingEntriesConstants.ENTRY_GENERATING_DOCUMENT_TYPES))
                .andReturn(docTypes);
        EasyMock.expect(detectDocumentsMissingEntriesDao.discoverLedgerDocumentsWithoutEntries(EasyMock.isA(Date.class),
                EasyMock.eq(docTypes))).andReturn(documentHeaderDataList);
        EasyMock.expect(detectDocumentsMissingEntriesDao.getCustomerPaymentMediumCodeFromCashControlDocument("AAAA")).andReturn(Optional.of("CA"));
        EasyMock.replay(detectDocumentsMissingEntriesDao, parameterService);

        List<DocumentHeaderData> results = detectDocumentsMissingEntriesService
                .discoverGeneralLedgerDocumentsWithoutEntries();

        Assert.assertEquals(0, results.size());
    }

    @Test
    public void testCheckCTRLDocument() throws Exception {
        List<DocumentHeaderData> documentHeaderDataList = new ArrayList<>();
        DocumentHeaderData documentHeaderData1 = new DocumentHeaderData("AAAA", "CTRL");
        documentHeaderDataList.add(documentHeaderData1);

        EasyMock.expect(parameterService.getParameterValueAsString(DetectDocumentsMissingEntriesStep.class,
                KFSParameterKeyConstants.DetectDocumentsMissingEntriesConstants.LOOK_BACK_DAYS, "7")).andReturn("7");
        EasyMock.expect(parameterService.getParameterValuesAsString(DetectDocumentsMissingEntriesStep.class,
                KFSParameterKeyConstants.DetectDocumentsMissingEntriesConstants.ENTRY_GENERATING_DOCUMENT_TYPES))
                .andReturn(new ArrayList<>());
        EasyMock.expect(detectDocumentsMissingEntriesDao.discoverLedgerDocumentsWithoutEntries(EasyMock.isA(Date.class),
                EasyMock.eq(new ArrayList<>()))).andReturn(documentHeaderDataList);
        EasyMock.expect(detectDocumentsMissingEntriesDao.getCustomerPaymentMediumCodeFromCashControlDocument("AAAA")).andReturn(Optional.of("CK"));
        EasyMock.replay(detectDocumentsMissingEntriesDao, parameterService);

        List<DocumentHeaderData> results = detectDocumentsMissingEntriesService
                .discoverGeneralLedgerDocumentsWithoutEntries();

        Assert.assertEquals(1, results.size());
    }

    @Test
    public void testBadCTRLDocument() throws Exception {
        List<DocumentHeaderData> documentHeaderDataList = new ArrayList<>();
        DocumentHeaderData documentHeaderData1 = new DocumentHeaderData("AAAA", "CTRL");
        documentHeaderDataList.add(documentHeaderData1);

        EasyMock.expect(parameterService.getParameterValueAsString(DetectDocumentsMissingEntriesStep.class,
                KFSParameterKeyConstants.DetectDocumentsMissingEntriesConstants.LOOK_BACK_DAYS, "7")).andReturn("7");
        EasyMock.expect(parameterService.getParameterValuesAsString(DetectDocumentsMissingEntriesStep.class,
                KFSParameterKeyConstants.DetectDocumentsMissingEntriesConstants.ENTRY_GENERATING_DOCUMENT_TYPES))
                .andReturn(new ArrayList<>());
        EasyMock.expect(detectDocumentsMissingEntriesDao.discoverLedgerDocumentsWithoutEntries(EasyMock.isA(Date.class),
                EasyMock.eq(new ArrayList<>()))).andReturn(documentHeaderDataList);
        EasyMock.expect(detectDocumentsMissingEntriesDao.getCustomerPaymentMediumCodeFromCashControlDocument("AAAA")).andReturn(Optional.empty());
        EasyMock.replay(detectDocumentsMissingEntriesDao, parameterService);

        try {
            detectDocumentsMissingEntriesService.discoverGeneralLedgerDocumentsWithoutEntries();
            Assert.fail();
        } catch (RuntimeException e) {
            // This is expected
        }
    }
}
