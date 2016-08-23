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
package org.kuali.kfs.module.cam.batch.service;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.krad.UserSession;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.batch.AssetDepreciationStep;
import org.kuali.kfs.module.cam.batch.AssetPaymentInfo;
import org.kuali.kfs.module.cam.batch.service.impl.AssetDepreciationServiceImpl;
import org.kuali.kfs.module.cam.businessobject.AssetDepreciationConvention;
import org.kuali.kfs.module.cam.businessobject.AssetObjectCode;
import org.kuali.kfs.module.cam.document.dataaccess.DepreciableAssetsDao;
import org.kuali.kfs.module.cam.document.dataaccess.DepreciationBatchDao;
import org.kuali.kfs.module.cam.document.dataaccess.impl.DepreciationBatchDaoJdbc;
import org.kuali.kfs.module.cam.document.service.AssetDateService;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.module.cam.fixture.AssetDepreciationServiceFixture;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.batch.service.SchedulerService;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.identity.TestPerson;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kim.api.identity.Person;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@RunWith(PowerMockRunner.class)
public class AssetDepreciationServiceTest {
    private String ERROR_RECORD_NUMBER_DOESNT_MATCH = "Depreciated assets collection doesn't have the same number of elements of the results we need to compare against";
    private String ERROR_INVALID_AMOUNTS = "Depreciation figures don't match those in the properties file";
    private ConfigurationService kualiConfigurationService;
    private AssetDepreciationServiceImpl camsAssetDepreciationService;
    private BusinessObjectService businessObjectService;
    private ParameterService parameterService;
    private DateTimeService dateTimeService;
    private SchedulerService schedulerService;
    private OptionsService optionsService;
    private DepreciableAssetsDao depreciableAssetsDao;
    private DepreciationBatchDao depreciationBatchDao;
    private ObjectCodeService objectCodeService;
    private WorkflowDocumentService workflowDocumentService;
    private AssetService assetService;
    private AssetDateService assetDateService;
    private UniversityDateService universityDateService;
    private UserSession userSession;
    private WorkflowDocument workflowDocument;
    private ReportService reportService;
    private List<AssetPaymentInfo> savedPaymentInfo;

    @Before
    public void setUp() throws Exception {
        camsAssetDepreciationService = new AssetDepreciationServiceImpl();
        businessObjectService = EasyMock.createMock(BusinessObjectService.class);
        kualiConfigurationService = EasyMock.createMock(ConfigurationService.class);
        parameterService = EasyMock.createMock(ParameterService.class);
        dateTimeService = EasyMock.createMock(DateTimeService.class);
        schedulerService = EasyMock.createMock(SchedulerService.class);
        optionsService = EasyMock.createMock(OptionsService.class);
        depreciableAssetsDao = EasyMock.createMock(DepreciableAssetsDao.class);
        depreciationBatchDao = EasyMock.createMock(DepreciationBatchDao.class);
        objectCodeService = EasyMock.createMock(ObjectCodeService.class);
        workflowDocumentService = EasyMock.createMock(WorkflowDocumentService.class);
        assetService = EasyMock.createMock(AssetService.class);
        userSession = EasyMock.createMock(UserSession.class);
        workflowDocument = EasyMock.createMock(WorkflowDocument.class);
        assetDateService = EasyMock.createMock(AssetDateService.class);
        universityDateService = EasyMock.createMock(UniversityDateService.class);
        reportService = EasyMock.createMock(ReportService.class);
        PowerMock.mockStatic(GlobalVariables.class);

        savedPaymentInfo = new ArrayList<>();

        camsAssetDepreciationService.setDateTimeService(dateTimeService);
        camsAssetDepreciationService.setConfigurationService(kualiConfigurationService);
        camsAssetDepreciationService.setParameterService(parameterService);
        camsAssetDepreciationService.setSchedulerService(schedulerService);
        camsAssetDepreciationService.setOptionsService(optionsService);
        camsAssetDepreciationService.setBusinessObjectService(businessObjectService);
        camsAssetDepreciationService.setDepreciableAssetsDao(depreciableAssetsDao);
        camsAssetDepreciationService.setDepreciationBatchDao(depreciationBatchDao);
        camsAssetDepreciationService.setObjectCodeService(objectCodeService);
        camsAssetDepreciationService.setWorkflowDocumentService(workflowDocumentService);
        camsAssetDepreciationService.setAssetService(assetService);
        camsAssetDepreciationService.setAssetDateService(assetDateService);
        camsAssetDepreciationService.setUniversityDateService(universityDateService);
        camsAssetDepreciationService.setCamsReportService(reportService);
    }

    @Test
    @PrepareForTest(GlobalVariables.class)
    public void testRunDepreciation() throws Exception {
        recordMocks();
        replayMocks();
        camsAssetDepreciationService.runDepreciation();
        verifyMocks();

        Collection<AssetPaymentInfo> resultsMustGet = AssetDepreciationServiceFixture.DATA.getResultsFromPropertiesFile();
        Assert.isTrue(resultsMustGet.size() == savedPaymentInfo.size(), ERROR_RECORD_NUMBER_DOESNT_MATCH);
        Assert.isTrue(this.isDepreciationOk(savedPaymentInfo, resultsMustGet), ERROR_INVALID_AMOUNTS);
    }

    @Test
    @PrepareForTest(GlobalVariables.class)
    public void testInvalidFiscalMonth() throws Exception {
        recordInvalidFiscalMonthMocks();
        replayMocks();
        camsAssetDepreciationService.runDepreciation();
    }

    @Test
    @PrepareForTest(GlobalVariables.class)
    public void testMissingDepreciationDateParameter() throws Exception {
        recordMissingDepreciationDateParameterMocks();
        replayMocks();
        camsAssetDepreciationService.runDepreciation();
        verifyMocks();
    }

    private void recordMocks() throws Exception {
        Date date = new Date();
        Date depreciationDate = AssetDepreciationServiceFixture.DATA.getDepreciationDate();
        Calendar depreciationCalendar = Calendar.getInstance();
        depreciationCalendar.setTime(depreciationDate);
        List<AssetObjectCode> assetObjectCodes = AssetDepreciationServiceFixture.DATA.getAssetObjectCodes();
        EasyMock.expect(dateTimeService.getCurrentCalendar()).andReturn(Calendar.getInstance()).anyTimes();
        EasyMock.expect(kualiConfigurationService.getPropertyValueAsString(CamsKeyConstants.Depreciation.DEPRECIATION_ALREADY_RAN_MSG))
            .andReturn("Already ran");
        EasyMock.expect(dateTimeService.getCurrentDate()).andReturn(date).anyTimes();
        EasyMock.expect(parameterService.getParameterValueAsString(AssetDepreciationStep.class, CamsConstants.Parameters.BLANK_OUT_PERIOD_BEGIN))
            .andReturn(null);
        EasyMock.expect(parameterService.getParameterValueAsString(AssetDepreciationStep.class, CamsConstants.Parameters.BLANK_OUT_PERIOD_END))
            .andReturn(null);
        EasyMock.expect(schedulerService.cronConditionMet(null)).andReturn(true);
        EasyMock.expect(parameterService.parameterExists(AssetDepreciationStep.class, CamsConstants.Parameters.DEPRECIATION_DATE_PARAMETER))
            .andReturn(true);
        EasyMock.expect(parameterService.getParameterValueAsString(AssetDepreciationStep.class, CamsConstants.Parameters.DEPRECIATION_DATE_PARAMETER))
            .andReturn(AssetDepreciationServiceFixture.DATA.getDepreciationDateString());
        EasyMock.expect(businessObjectService.findBySinglePrimaryKey(UniversityDate.class, new java.sql.Date(depreciationDate.getTime())))
            .andReturn(AssetDepreciationServiceFixture.DATA.getUniversityDate());
        HashMap<String, Object> fields = new HashMap<String, Object>();
        fields.put(CamsPropertyConstants.AssetObject.UNIVERSITY_FISCAL_YEAR, 2010);
        fields.put(CamsPropertyConstants.AssetObject.ACTIVE, Boolean.TRUE);
        EasyMock.expect(businessObjectService.findMatching(AssetObjectCode.class, fields)).andReturn(assetObjectCodes);
        EasyMock.expect(depreciableAssetsDao.generateStatistics(true, null, 2010, 1, depreciationCalendar, "",
            assetObjectCodes, 1, "Already ran")).andReturn(new ArrayList<String[]>());
        EasyMock.expect(depreciationBatchDao.getListOfDepreciableAssetPaymentInfo(2010, 1, depreciationCalendar))
            .andReturn(AssetDepreciationServiceFixture.DATA.getAssetPaymentInfo());
        EasyMock.expect(parameterService.parameterExists(AssetDepreciationStep.class, CamsConstants.Parameters.DEPRECIATION_ORGANIZATION_PLANT_FUND_OBJECT_SUB_TYPE))
            .andReturn(true);
        EasyMock.expect(parameterService.getParameterValuesAsString(AssetDepreciationStep.class, CamsConstants.Parameters.DEPRECIATION_ORGANIZATION_PLANT_FUND_OBJECT_SUB_TYPE))
            .andReturn(Arrays.asList(new String[]{"C1", "C2", "CF", "CM", "ES", "NA", "UC", "UF"}));
        EasyMock.expect(parameterService.parameterExists(AssetDepreciationStep.class, CamsConstants.Parameters.DEPRECIATION_CAMPUS_PLANT_FUND_OBJECT_SUB_TYPES))
            .andReturn(true);
        EasyMock.expect(parameterService.getParameterValuesAsString(AssetDepreciationStep.class, CamsConstants.Parameters.DEPRECIATION_CAMPUS_PLANT_FUND_OBJECT_SUB_TYPES))
            .andReturn(Arrays.asList(new String[]{"BD", "BF", "BI", "BR", "BX", "IF", "LE", "LF", "LI", "LR"}));
        EasyMock.expect(parameterService.parameterExists(AssetDepreciationStep.class, CamsConstants.Parameters.DEPRECIATION_PERIOD))
            .andReturn(true);
        EasyMock.expect(parameterService.getParameterValueAsString(AssetDepreciationStep.class, CamsConstants.Parameters.DEPRECIATION_PERIOD))
            .andReturn("1");
        EasyMock.expect(depreciationBatchDao.getPrimaryDepreciationBaseAmountForSV()).andReturn(AssetDepreciationServiceFixture.DATA.getPrimaryDepreciationBaseAmountForSV());
        EasyMock.expect(depreciationBatchDao.getAssetsWithNoDepreciation()).andReturn(AssetDepreciationServiceFixture.DATA.getAssetsWithNoDepreciation());
        EasyMock.expect(objectCodeService.getByPrimaryId(2010, "BL", "8910"))
            .andReturn(getObjectCode(2010, "BL", "8910")).anyTimes();
        EasyMock.expect(objectCodeService.getByPrimaryId(2010, "BL", "5115"))
            .andReturn(getObjectCode(2010, "BL", "5115")).anyTimes();

        depreciationBatchDao.updateAssetPayments(EasyMock.isA(List.class), EasyMock.eq(1));
        EasyMock.expectLastCall().andDelegateTo(new DepreciationBatchDaoJdbc() {
            @Override
            public void updateAssetPayments(List<AssetPaymentInfo> assetPayments, Integer fiscalPeriod) {
                savedPaymentInfo.addAll(assetPayments);
            }
        });

        // createNewDepreciationDocument
        EasyMock.expect(GlobalVariables.getUserSession()).andReturn(userSession);
        EasyMock.expect(userSession.getPerson()).andReturn(getPerson());
        EasyMock.expect(workflowDocumentService.createWorkflowDocument("DEPR", null)).andReturn(workflowDocument);
        EasyMock.expect(workflowDocument.getDocumentId()).andReturn("12345");
        EasyMock.expect(businessObjectService.save(EasyMock.isA(FinancialSystemDocumentHeader.class))).andReturn(null);

        EasyMock.expect(optionsService.getCurrentYearOptions()).andReturn(AssetDepreciationServiceFixture.DATA.getSystemOptions()).times(2);
        EasyMock.expect(dateTimeService.toDateString(depreciationDate)).andReturn("").anyTimes();
        EasyMock.expect(depreciableAssetsDao.generateStatistics(false, Arrays.asList(new String[]{"12345"}), 2010, 1, depreciationCalendar, "",
            assetObjectCodes, 1, "Already ran")).andReturn(new ArrayList<String[]>());
        // TODO: Capture generated GLPEs and verify they are as expected.
        depreciationBatchDao.savePendingGLEntries(EasyMock.isA(List.class));
        EasyMock.expectLastCall();
    }

    private void recordInvalidFiscalMonthMocks() throws Exception {
        Date date = new Date();
        Date depreciationDate = AssetDepreciationServiceFixture.DATA.getDepreciationDate();
        Calendar depreciationCalendar = Calendar.getInstance();
        depreciationCalendar.setTime(depreciationDate);
        List<AssetObjectCode> assetObjectCodes = AssetDepreciationServiceFixture.DATA.getAssetObjectCodes();
        EasyMock.expect(dateTimeService.getCurrentCalendar()).andReturn(Calendar.getInstance()).anyTimes();
        EasyMock.expect(kualiConfigurationService.getPropertyValueAsString(CamsKeyConstants.Depreciation.DEPRECIATION_ALREADY_RAN_MSG))
            .andReturn("Already ran");
        EasyMock.expect(dateTimeService.getCurrentDate()).andReturn(date).anyTimes();
        EasyMock.expect(parameterService.getParameterValueAsString(AssetDepreciationStep.class, CamsConstants.Parameters.BLANK_OUT_PERIOD_BEGIN))
            .andReturn(null).times(2);
        EasyMock.expect(parameterService.getParameterValueAsString(AssetDepreciationStep.class, CamsConstants.Parameters.BLANK_OUT_PERIOD_END))
            .andReturn(null).times(2);
        EasyMock.expect(schedulerService.cronConditionMet(null)).andReturn(true);
        EasyMock.expect(parameterService.parameterExists(AssetDepreciationStep.class, CamsConstants.Parameters.DEPRECIATION_DATE_PARAMETER))
            .andReturn(true);
        EasyMock.expect(parameterService.getParameterValueAsString(AssetDepreciationStep.class, CamsConstants.Parameters.DEPRECIATION_DATE_PARAMETER))
            .andReturn(AssetDepreciationServiceFixture.DATA.getDepreciationDateString());
        EasyMock.expect(businessObjectService.findBySinglePrimaryKey(UniversityDate.class, new java.sql.Date(depreciationDate.getTime())))
            .andReturn(AssetDepreciationServiceFixture.DATA.getUniversityDate());
        HashMap<String, Object> fields = new HashMap<String, Object>();
        fields.put(CamsPropertyConstants.AssetObject.UNIVERSITY_FISCAL_YEAR, 2010);
        fields.put(CamsPropertyConstants.AssetObject.ACTIVE, Boolean.TRUE);
        EasyMock.expect(businessObjectService.findMatching(AssetObjectCode.class, fields)).andReturn(assetObjectCodes);
        EasyMock.expect(depreciableAssetsDao.generateStatistics(true, null, 2010, 1, depreciationCalendar, "",
            assetObjectCodes, 1, "Already ran")).andReturn(new ArrayList<String[]>());
        EasyMock.expect(depreciationBatchDao.getListOfDepreciableAssetPaymentInfo(2010, 1, depreciationCalendar))
            .andReturn(AssetDepreciationServiceFixture.DATA.getAssetPaymentInfo());
        EasyMock.expect(parameterService.parameterExists(AssetDepreciationStep.class, CamsConstants.Parameters.DEPRECIATION_ORGANIZATION_PLANT_FUND_OBJECT_SUB_TYPE))
            .andReturn(true);
        EasyMock.expect(parameterService.getParameterValuesAsString(AssetDepreciationStep.class, CamsConstants.Parameters.DEPRECIATION_ORGANIZATION_PLANT_FUND_OBJECT_SUB_TYPE))
            .andReturn(Arrays.asList(new String[]{"C1", "C2", "CF", "CM", "ES", "NA", "UC", "UF"}));
        EasyMock.expect(parameterService.parameterExists(AssetDepreciationStep.class, CamsConstants.Parameters.DEPRECIATION_CAMPUS_PLANT_FUND_OBJECT_SUB_TYPES))
            .andReturn(true);
        EasyMock.expect(parameterService.getParameterValuesAsString(AssetDepreciationStep.class, CamsConstants.Parameters.DEPRECIATION_CAMPUS_PLANT_FUND_OBJECT_SUB_TYPES))
            .andReturn(Arrays.asList(new String[]{"BD", "BF", "BI", "BR", "BX", "IF", "LE", "LF", "LI", "LR"}));
        EasyMock.expect(parameterService.parameterExists(AssetDepreciationStep.class, CamsConstants.Parameters.DEPRECIATION_PERIOD))
            .andReturn(true);

        // This is what makes fiscal period 1 invalid.
        EasyMock.expect(parameterService.getParameterValueAsString(AssetDepreciationStep.class, CamsConstants.Parameters.DEPRECIATION_PERIOD))
            .andReturn("3");
        EasyMock.expect(kualiConfigurationService.getPropertyValueAsString(CamsKeyConstants.Depreciation.FISCAL_MONTH_NOT_VALID))
            .andReturn("Fiscal month of depreciation date must be a multiple of the depreciation period.");
        EasyMock.expect(kualiConfigurationService.getPropertyValueAsString(CamsKeyConstants.Depreciation.ERROR_WHEN_CALCULATING_DEPRECIATION))
            .andReturn("An error occurred when calculating assets depreciation.");

        EasyMock.expect(optionsService.getCurrentYearOptions()).andReturn(AssetDepreciationServiceFixture.DATA.getSystemOptions()).times(2);
        EasyMock.expect(dateTimeService.toDateString(depreciationDate)).andReturn("").anyTimes();
        EasyMock.expect(depreciableAssetsDao.generateStatistics(false, new ArrayList<>(), 2010, 1, depreciationCalendar, "",
            assetObjectCodes, 1, "Already ran")).andReturn(new ArrayList<String[]>());
        reportService.generateDepreciationReport(EasyMock.eq(new ArrayList<>()), EasyMock.isA(String.class), EasyMock.isA(String.class));
        EasyMock.expectLastCall();
    }

    private void recordMissingDepreciationDateParameterMocks() {
        Calendar depreciationCalendar = Calendar.getInstance();
        depreciationCalendar.set(Calendar.HOUR_OF_DAY, 0);
        depreciationCalendar.set(Calendar.MINUTE, 0);
        depreciationCalendar.set(Calendar.SECOND, 0);
        depreciationCalendar.set(Calendar.MILLISECOND, 0);
        Date date = depreciationCalendar.getTime();
        EasyMock.expect(dateTimeService.getCurrentCalendar()).andReturn(Calendar.getInstance()).anyTimes();
        EasyMock.expect(kualiConfigurationService.getPropertyValueAsString(CamsKeyConstants.Depreciation.DEPRECIATION_ALREADY_RAN_MSG))
            .andReturn("Already ran");
        EasyMock.expect(dateTimeService.getCurrentDate()).andReturn(date).anyTimes();
        EasyMock.expect(parameterService.getParameterValueAsString(AssetDepreciationStep.class, CamsConstants.Parameters.BLANK_OUT_PERIOD_BEGIN))
            .andReturn(null);
        EasyMock.expect(parameterService.getParameterValueAsString(AssetDepreciationStep.class, CamsConstants.Parameters.BLANK_OUT_PERIOD_END))
            .andReturn(null);
        EasyMock.expect(schedulerService.cronConditionMet(null)).andReturn(true);
        EasyMock.expect(parameterService.parameterExists(AssetDepreciationStep.class, CamsConstants.Parameters.DEPRECIATION_DATE_PARAMETER))
            .andReturn(false);

        // Expect to see today's date used, since the parameter is absent.  Return null because we've tested what we want
        // to test, and don't want to mock all of depreciation.
        EasyMock.expect(businessObjectService.findBySinglePrimaryKey(UniversityDate.class, new java.sql.Date(date.getTime())))
            .andReturn(null);

        EasyMock.expect(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_UNIV_DATE_NOT_FOUND)).andReturn("");
        reportService.generateDepreciationReport(EasyMock.eq(new ArrayList<>()), EasyMock.isA(String.class), EasyMock.isA(String.class));
        EasyMock.expectLastCall();
    }

    private void replayMocks() {
        EasyMock.replay(dateTimeService, kualiConfigurationService, parameterService, schedulerService, optionsService, businessObjectService);
        EasyMock.replay(depreciableAssetsDao, depreciationBatchDao, objectCodeService, workflowDocumentService, workflowDocument, reportService);
        PowerMock.replay(GlobalVariables.class);
    }

    private void verifyMocks() {
        EasyMock.verify(dateTimeService, kualiConfigurationService, parameterService, schedulerService, optionsService, businessObjectService);
        EasyMock.verify(depreciableAssetsDao, depreciationBatchDao, workflowDocumentService, reportService);
        EasyMock.reset(dateTimeService, kualiConfigurationService, parameterService, schedulerService, optionsService, businessObjectService);
        EasyMock.reset(depreciableAssetsDao, depreciationBatchDao, objectCodeService, workflowDocumentService, workflowDocument, reportService);
    }

    private ObjectCode getObjectCode(int year, String chart, String code) {
        ObjectCode result = new ObjectCode();
        result.setActive(true);
        result.setChartOfAccountsCode(chart);
        result.setUniversityFiscalYear(year);
        result.setFinancialObjectCode(code);
        return result;
    }

    private AssetDepreciationConvention getDepreciationConvention(String code) {
        AssetDepreciationConvention result = new AssetDepreciationConvention();
        result.setDepreciationConventionCode(code);
        return result;
    }

    private Person getPerson() {
        return new TestPerson("testPrincipalId", "testPrincipalName");
    }

    /**
     * Determines whether or not a calculated depreciation amount has the same value as the on the fixture file
     *
     * @param savedPaymentInfo
     * @param resultsMustGet
     * @return
     */
    public boolean isDepreciationOk(Collection<AssetPaymentInfo> savedPaymentInfo, Collection<AssetPaymentInfo> resultsMustGet) {
        Iterator<AssetPaymentInfo> resultsMustGetIterator = resultsMustGet.iterator();
        Iterator<AssetPaymentInfo> depreciatedAssetsIterator = savedPaymentInfo.iterator();

        while (depreciatedAssetsIterator.hasNext()) {
            AssetPaymentInfo depreciatedAsset = depreciatedAssetsIterator.next();
            AssetPaymentInfo resultMustGet = resultsMustGetIterator.next();

            if (!resultMustGet.getAccumulatedPrimaryDepreciationAmount().equals(depreciatedAsset.getAccumulatedPrimaryDepreciationAmount())) {
                return false;
            }

            if (!resultMustGet.getTransactionAmount().equals(depreciatedAsset.getTransactionAmount())) {
                return false;
            }

            if (!resultMustGet.getAccumulatedRoundingErrorInMillicents().equals(depreciatedAsset.getAccumulatedRoundingErrorInMillicents())) {
                return false;
            }
        }
        return true;
    }
}
