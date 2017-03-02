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
import org.kuali.kfs.gl.batch.CollectorStep;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.gl.service.SufficientFundsService;
import org.kuali.kfs.krad.util.ErrorMessage;
import org.kuali.kfs.krad.util.MessageMap;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.SufficientFundsItem;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.util.ArrayList;
import java.util.List;

public class CollectorHelperServiceImplTest {
    private CollectorHelperServiceImpl collectorHelperService;
    private ParameterService parameterService;
    private SufficientFundsService sufficientFundsService;

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
        };

        parameterService = EasyMock.createMock(ParameterService.class);
        sufficientFundsService = EasyMock.createMock(SufficientFundsService.class);

        collectorHelperService.setParameterService(parameterService);
        collectorHelperService.setSufficientFundsService(sufficientFundsService);
    }

    @Test
    public void testPerformValidationNoCheckSufficientFunds() {
        CollectorBatch batch = getBatch();
        MessageMap messageMap = new MessageMap();

        EasyMock.expect(parameterService.getParameterValueAsBoolean(CollectorStep.class,"PERFORM_DUPLICATE_HEADER_CHECK_IND")).andReturn(false);
        EasyMock.expect(parameterService.getParameterValueAsBoolean("KFS-GL","All","COLLECTOR_CHECK_SUFFICIENT_FUNDS_IND")).andReturn(false);
        EasyMock.replay(parameterService,sufficientFundsService);

        boolean isValid = collectorHelperService.performValidation(batch,messageMap);

        EasyMock.verify(parameterService,sufficientFundsService);
        Assert.assertEquals(true,isValid);
        Assert.assertEquals(0,messageMap.getErrorCount());
    }

    @Test
    public void testPerformValidationCheckSufficientFunds_withSufficientFunds() {
        CollectorBatch batch = getBatch();
        MessageMap messageMap = new MessageMap();

        EasyMock.expect(parameterService.getParameterValueAsBoolean(CollectorStep.class,"PERFORM_DUPLICATE_HEADER_CHECK_IND")).andReturn(false);
        EasyMock.expect(parameterService.getParameterValueAsBoolean("KFS-GL","All","COLLECTOR_CHECK_SUFFICIENT_FUNDS_IND")).andReturn(true);
        EasyMock.expect(sufficientFundsService.checkSufficientFunds((List<Transaction>)EasyMock.anyObject())).andReturn(new ArrayList<>());
        EasyMock.replay(parameterService,sufficientFundsService);

        boolean isValid = collectorHelperService.performValidation(batch,messageMap);

        EasyMock.verify(parameterService,sufficientFundsService);
        Assert.assertEquals(true,isValid);
        Assert.assertEquals(0,messageMap.getErrorCount());
    }

    @Test
    public void testPerformValidationCheckSufficientFunds_noSufficientFunds() {
        CollectorBatch batch = getBatch();
        MessageMap messageMap = new MessageMap();

        EasyMock.expect(parameterService.getParameterValueAsBoolean(CollectorStep.class,"PERFORM_DUPLICATE_HEADER_CHECK_IND")).andReturn(false);
        EasyMock.expect(parameterService.getParameterValueAsBoolean("KFS-GL","All","COLLECTOR_CHECK_SUFFICIENT_FUNDS_IND")).andReturn(true);
        EasyMock.expect(sufficientFundsService.checkSufficientFunds((List<Transaction>)EasyMock.anyObject())).andReturn(getSufficientFundsItems());
        EasyMock.replay(parameterService,sufficientFundsService);

        boolean isValid = collectorHelperService.performValidation(batch,messageMap);

        EasyMock.verify(parameterService,sufficientFundsService);
        Assert.assertEquals(false,isValid);
        Assert.assertEquals(1,messageMap.getErrorCount());
        List<ErrorMessage> messages = messageMap.getErrorMessagesForProperty(KFSConstants.GLOBAL_ERRORS);
        Assert.assertEquals(1,messages.size());
        Assert.assertEquals("error.collector.insufficientfunds",messages.get(0).getErrorKey());
        Assert.assertEquals("BL-0211908-2400-SB-AC-EX-50.00",messages.get(0).getMessageParameters()[0]);
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

    public CollectorBatch getBatch() {
        CollectorBatch batch = new CollectorBatch();
        batch.setHeaderlessBatch(false);
        batch.setUniversityFiscalYear("2017");
        batch.setChartOfAccountsCode("BL");
        batch.setOrganizationCode("CHEM");
        batch.setCampusCode("BL");
        batch.setPhoneNumber("5173533121");
        batch.setMailingAddress("1234 Main Street");
        batch.setDepartmentName("Chemistry");

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
        o.setProjectCode("----------");
        o.setTransactionLedgerEntryAmount(new KualiDecimal("50.00"));
        return o;
    }
}