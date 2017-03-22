package org.kuali.kfs.gl.batch.service.impl;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.gl.batch.CollectorBatch;
import org.kuali.kfs.gl.batch.CollectorFlatFileInputType;
import org.kuali.kfs.gl.batch.CollectorStep;
import org.kuali.kfs.gl.batch.service.CollectorReportService;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.report.CollectorReportData;
import org.kuali.kfs.krad.util.ErrorMessage;
import org.kuali.kfs.krad.util.MessageMap;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.batch.service.impl.BatchInputFileServiceImpl;
import org.kuali.kfs.sys.businessobject.SufficientFundsItem;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollectorHelperServiceImplTest {
    private CollectorHelperServiceImpl collectorHelperService;
    private ParameterService parameterService;
    private CollectorFlatFileInputType collectorFlatFileType;
    private DateTimeService dateTimeService;
    private UniversityDateService universityDateService;
    private BatchInputFileService batchInputFileService;
    private CollectorReportService collectorReportService;

    @Before
    public void setUp() {
        collectorHelperService = new CollectorHelperServiceImpl() {
            @Override
            protected boolean checkForMixedDocumentTypes(CollectorBatch batch, MessageMap messageMap) {
                return true;
            }

            @Override
            protected boolean checkForMixedBalanceTypes(CollectorBatch batch, MessageMap messageMap) {
                return true;
            }

            @Override
            protected boolean checkDetailKeys(CollectorBatch batch, MessageMap messageMap) {
                return true;
            }

            @Override
            protected void initializeCollectorReportWriterService() {
            }

            @Override
            protected List<CollectorBatch> doCollectorFileParse(InputStream inputStream, String fileName, MessageMap messageMap, BatchInputFileType collectorInputFileType, CollectorReportData collectorReportData) {
                List<CollectorBatch> batches = new ArrayList<>();
                CollectorBatch batch = getBatch(true);
                batches.add(batch);
                return batches;
            }

            @Override
            protected void loadGlEntriesIntoGlPendingTable(CollectorBatch batch) {
            }
        };

        parameterService = EasyMock.createMock(ParameterService.class);
        dateTimeService = EasyMock.createMock(DateTimeService.class);
        universityDateService = EasyMock.createMock(UniversityDateService.class);
        collectorReportService = EasyMock.createMock(CollectorReportService.class);

        batchInputFileService = new BatchInputFileServiceImpl();

        collectorHelperService.setParameterService(parameterService);
        collectorHelperService.setBatchInputFileService(batchInputFileService);
        collectorHelperService.setCollectorReportService(collectorReportService);

        collectorFlatFileType = new CollectorFlatFileInputType();
        collectorFlatFileType.setDirectoryPath(System.getProperty("java.io.tmpdir"));
        collectorFlatFileType.setFileExtension("data");
        collectorFlatFileType.setDateTimeService(dateTimeService);
        collectorFlatFileType.setUniversityDateService(universityDateService);
        collectorFlatFileType.setCollectorHelperService(collectorHelperService);
    }

    @Test
    public void testLoadCollectorApiDataGood() {
        Collection<String> docTypes = new ArrayList<>();
        docTypes.add("ID*");
        docTypes.add("EB*");
        EasyMock.expect(parameterService.getParameterValuesAsString(CollectorStep.class,"EQUAL_DEBIT_CREDIT_TOTAL_DOCUMENT_TYPES")).andReturn(docTypes);
        collectorReportService.generateCollectorRunReports(EasyMock.anyObject());
        EasyMock.expect(parameterService.getParameterValueAsBoolean(CollectorStep.class,"PERFORM_DUPLICATE_HEADER_CHECK_IND")).andReturn(false);

        EasyMock.replay(parameterService,dateTimeService,universityDateService,collectorReportService);

        List<ErrorMessage> errors = collectorHelperService.loadCollectorApiData(getUnusedInputStream(),collectorFlatFileType);

        EasyMock.verify(parameterService,dateTimeService,universityDateService,collectorReportService);
        Assert.assertEquals(0,errors.size());
    }

    @Test
    public void testLoadCollectorApiDataBad() {
        collectorHelperService = new CollectorHelperServiceImpl() {
            @Override
            protected boolean checkForMixedDocumentTypes(CollectorBatch batch, MessageMap messageMap) {
                return true;
            }

            @Override
            protected boolean checkForMixedBalanceTypes(CollectorBatch batch, MessageMap messageMap) {
                return true;
            }

            @Override
            protected boolean checkDetailKeys(CollectorBatch batch, MessageMap messageMap) {
                return true;
            }

            @Override
            protected void initializeCollectorReportWriterService() {
            }

            @Override
            protected List<CollectorBatch> doCollectorFileParse(InputStream inputStream, String fileName, MessageMap messageMap, BatchInputFileType collectorInputFileType, CollectorReportData collectorReportData) {
                List<CollectorBatch> batches = new ArrayList<>();
                CollectorBatch batch = getBatch(false);
                batches.add(batch);
                return batches;
            }

            @Override
            protected void loadGlEntriesIntoGlPendingTable(CollectorBatch batch) {
            }
        };
        collectorHelperService.setParameterService(parameterService);
        collectorHelperService.setBatchInputFileService(batchInputFileService);
        collectorHelperService.setCollectorReportService(collectorReportService);

        Collection<String> docTypes = new ArrayList<>();
        docTypes.add("ID*");
        docTypes.add("EB*");
        EasyMock.expect(parameterService.getParameterValuesAsString(CollectorStep.class,"EQUAL_DEBIT_CREDIT_TOTAL_DOCUMENT_TYPES")).andReturn(docTypes);
        collectorReportService.generateCollectorRunReports(EasyMock.anyObject());

        EasyMock.replay(parameterService,dateTimeService,universityDateService,collectorReportService);

        List<ErrorMessage> errors = collectorHelperService.loadCollectorApiData(getUnusedInputStream(),collectorFlatFileType);

        EasyMock.verify(parameterService,dateTimeService,universityDateService,collectorReportService);
        Assert.assertEquals(1,errors.size());
        Assert.assertEquals("error.collector.countNoMatch",errors.get(0).getErrorKey());
    }
    @Test

    public void testLoadCollectorApiDataParseError() {
        collectorHelperService = new CollectorHelperServiceImpl() {
            @Override
            protected boolean checkForMixedDocumentTypes(CollectorBatch batch, MessageMap messageMap) {
                return true;
            }

            @Override
            protected boolean checkForMixedBalanceTypes(CollectorBatch batch, MessageMap messageMap) {
                return true;
            }

            @Override
            protected boolean checkDetailKeys(CollectorBatch batch, MessageMap messageMap) {
                return true;
            }

            @Override
            protected void initializeCollectorReportWriterService() {
            }

            @Override
            protected List<CollectorBatch> doCollectorFileParse(InputStream inputStream, String fileName, MessageMap messageMap, BatchInputFileType collectorInputFileType, CollectorReportData collectorReportData) {
                List<CollectorBatch> batches = new ArrayList<>();
                CollectorBatch batch = getBatch(false);
                batch.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS,"parse.error");
                batches.add(batch);
                return batches;
            }

            @Override
            protected void loadGlEntriesIntoGlPendingTable(CollectorBatch batch) {
            }
        };
        collectorHelperService.setParameterService(parameterService);
        collectorHelperService.setBatchInputFileService(batchInputFileService);
        collectorHelperService.setCollectorReportService(collectorReportService);
        collectorReportService.generateCollectorRunReports(EasyMock.anyObject());

        EasyMock.replay(parameterService,dateTimeService,universityDateService,collectorReportService);

        List<ErrorMessage> errors = collectorHelperService.loadCollectorApiData(getUnusedInputStream(),collectorFlatFileType);

        EasyMock.verify(parameterService,dateTimeService,universityDateService,collectorReportService);
        Assert.assertEquals(1,errors.size());
        Assert.assertEquals("parse.error",errors.get(0).getErrorKey());
    }

    public List<SufficientFundsItem> getSufficientFundsItems() {
        List<SufficientFundsItem> items = new ArrayList<>();
        SufficientFundsItem item = new SufficientFundsItem();
        item.setAmount(new KualiDecimal("50.00"));
        items.add(item);
        Account a = new Account();
        a.setChartOfAccountsCode("BL");
        a.setAccountNumber("0211908");
        item.setAccount(a);
        ObjectCode oc = new ObjectCode();
        oc.setFinancialObjectCode("2400");
        item.setFinancialObject(oc);
        item.setDocumentTypeCode("SB");
        BalanceType bt = new BalanceType();
        bt.setFinancialBalanceTypeCode("AC");
        item.setBalanceTyp(bt);
        ObjectType ot = new ObjectType();
        ot.setCode("EX");
        item.setFinancialObjectType(ot);
        return items;
    }

    public CollectorBatch getBatch(boolean isGood) {
        CollectorBatch batch = new CollectorBatch();
        batch.setHeaderlessBatch(false);
        batch.setUniversityFiscalYear("2017");
        batch.setChartOfAccountsCode("BL");
        batch.setOrganizationCode("CHEM");
        batch.setCampusCode("BL");
        batch.setPhoneNumber("5173533121");
        batch.setMailingAddress("1234 Main Street");
        batch.setDepartmentName("Chemistry");

        OriginEntryTotals totals = new OriginEntryTotals();
        totals.setNumDebitEntries(1);
        totals.addToTotals(getEntry());
        batch.setOriginEntryTotals(totals);
        batch.setTotalAmount(totals.getCreditAmount().add(totals.getDebitAmount()).add(totals.getOtherAmount()));
        if ( isGood ) {
            batch.setTotalRecords(1);
        } else {
            batch.setTotalRecords(0);
        }
        batch.getOriginEntries().add(getEntry());

        return batch;
    }

    public OriginEntryFull getEntry() {
        OriginEntryFull o = new OriginEntryFull();
        o.setUniversityFiscalYear(2017);
        o.setChartOfAccountsCode("BL");
        o.setAccountNumber("0211908");
        o.setSubAccountNumber("-----");
        o.setFinancialObjectCode("2400");
        o.setFinancialSubObjectCode("---");
        o.setFinancialBalanceTypeCode("AC");
        o.setFinancialObjectTypeCode("EX");
        o.setUniversityFiscalPeriodCode("02");
        o.setFinancialDocumentTypeCode("SB");
        o.setProjectCode("----------");
        o.setTransactionLedgerEntryAmount(new KualiDecimal("50.00"));
        o.setTransactionDebitCreditCode("D");
        return o;
    }

    public InputStream getUnusedInputStream() {
        return new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
    }
}