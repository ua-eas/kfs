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
package org.kuali.kfs.module.cam.batch.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.gl.service.impl.StringHelper;
import org.kuali.kfs.kns.service.DataDictionaryService;
import org.kuali.kfs.krad.exception.InvalidAddressException;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.MailService;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.krad.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.batch.AssetDepreciationStep;
import org.kuali.kfs.module.cam.batch.AssetPaymentInfo;
import org.kuali.kfs.module.cam.batch.service.AssetDepreciationService;
import org.kuali.kfs.module.cam.batch.service.ReportService;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetDepreciationTransaction;
import org.kuali.kfs.module.cam.businessobject.AssetObjectCode;
import org.kuali.kfs.module.cam.document.dataaccess.DepreciableAssetsDao;
import org.kuali.kfs.module.cam.document.dataaccess.DepreciationBatchDao;
import org.kuali.kfs.module.cam.document.service.AssetDateService;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.batch.service.SchedulerService;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.mail.MailMessage;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.kuali.kfs.sys.KFSConstants.BALANCE_TYPE_ACTUAL;

/**
 * This class is a service that calculates the depreciation amount for each asset that has a eligible asset payment.
 * <p>
 * When an error occurs running this process, a pdf file will be created with the error message. However, this doesn't mean that
 * this process automatically leaves all the records as they were right before running the program. If the process fails, is
 * suggested to do the following before trying to run the process again: a.)Delete gl pending entry depreciation entries: DELETE
 * FROM GL_PENDING_ENTRY_T WHERE FDOC_TYP_CD = 'DEPR' b.)Substract from the accumulated depreciation amount the depreciation
 * calculated for the fiscal month that was ran, and then reset the depreciation amount field for the fiscal month that was ran. ex:
 * Assuming that the fiscal month = 8 then: UPDATE CM_AST_PAYMENT_T SET AST_ACUM_DEPR1_AMT = AST_ACUM_DEPR1_AMT -
 * AST_PRD8_DEPR1_AMT, AST_PRD8_DEPR1_AMT=0
 */
@Transactional
public class AssetDepreciationServiceImpl implements AssetDepreciationService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetDepreciationServiceImpl.class);

    protected ParameterService parameterService;
    protected AssetService assetService;
    protected ReportService reportService;
    protected DateTimeService dateTimeService;
    protected DepreciableAssetsDao depreciableAssetsDao;
    protected ConfigurationService kualiConfigurationService;
    protected GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    protected BusinessObjectService businessObjectService;
    protected UniversityDateService universityDateService;
    protected OptionsService optionsService;
    protected DataDictionaryService dataDictionaryService;
    protected DepreciationBatchDao depreciationBatchDao;
    protected String cronExpression;
    protected MailService mailService;
    protected ObjectCodeService objectCodeService;
    protected WorkflowDocumentService workflowDocumentService;
    private AssetDateService assetDateService;
    private SchedulerService schedulerService;

    /**
     * @see org.kuali.kfs.module.cam.batch.service.AssetDepreciationService#runDepreciation()
     */
    @Override
    public void runDepreciation() {
        LOG.debug("runDepreciation() started");

        Integer fiscalYear = -1;
        Integer fiscalMonth = -1;
        String errorMsg = "";
        List<String> documentNos = new ArrayList<>();
        List<String[]> reportLog = new ArrayList<>();
        Collection<AssetObjectCode> assetObjectCodes = new ArrayList<>();
        boolean hasErrors = false;
        Calendar depreciationDate = dateTimeService.getCurrentCalendar();
        String depreciationDateParameter = null;
        DateFormat dateFormat = new SimpleDateFormat(CamsConstants.DateFormats.YEAR_MONTH_DAY);
        boolean executeJob = false;
        String errorMessage = kualiConfigurationService.getPropertyValueAsString(CamsKeyConstants.Depreciation.DEPRECIATION_ALREADY_RAN_MSG);

        try {
            executeJob = runAssetDepreciation();
            if(executeJob)
            {
                LOG.info("*******" + CamsConstants.Depreciation.DEPRECIATION_BATCH + " HAS BEGUN *******");

                /*
                 * Getting the system parameter "DEPRECIATION_DATE" When this parameter is used to determine which fiscal month and year
                 * is going to be depreciated If blank then the system will take the system date to determine the fiscal period
                 */
                if (parameterService.parameterExists(AssetDepreciationStep.class, CamsConstants.Parameters.DEPRECIATION_DATE_PARAMETER)) {
                    depreciationDateParameter = parameterService.getParameterValueAsString(AssetDepreciationStep.class, CamsConstants.Parameters.DEPRECIATION_DATE_PARAMETER);
                } else {
                    throw new IllegalStateException(kualiConfigurationService.getPropertyValueAsString(CamsKeyConstants.Depreciation.DEPRECIATION_DATE_PARAMETER_NOT_FOUND));
                }

                if(StringUtils.isBlank(depreciationDateParameter)) {
                    depreciationDateParameter = dateFormat.format(dateTimeService.getCurrentDate());
                }
                // This validates the system parameter depreciation_date has a valid format of YYYY-MM-DD.
                if ( !StringUtils.isBlank(depreciationDateParameter) ) {
                    try {
                        depreciationDate.setTime(dateFormat.parse(depreciationDateParameter.trim()));
                    } catch (ParseException e) {
                        throw new IllegalArgumentException(kualiConfigurationService.getPropertyValueAsString(CamsKeyConstants.Depreciation.INVALID_DEPRECIATION_DATE_FORMAT));
                    }
                }
                LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Depreciation run date: " + depreciationDateParameter);

                UniversityDate universityDate = businessObjectService.findBySinglePrimaryKey(UniversityDate.class, new java.sql.Date(depreciationDate.getTimeInMillis()));
                if (universityDate == null) {
                    throw new IllegalStateException(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_UNIV_DATE_NOT_FOUND));
                }

                fiscalYear = universityDate.getUniversityFiscalYear();
                fiscalMonth = new Integer(universityDate.getUniversityFiscalAccountingPeriod());
                  assetObjectCodes = getAssetObjectCodes(fiscalYear);
                // If the depreciation date is not = to the system date then, the depreciation process cannot run.
                LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Fiscal Year = " + fiscalYear + " & Fiscal Period=" + fiscalMonth);

                int fiscalStartMonth = Integer.parseInt(optionsService.getCurrentYearOptions().getUniversityFiscalYearStartMo());
                reportLog.addAll(depreciableAssetsDao.generateStatistics(true, null, fiscalYear, fiscalMonth, depreciationDate,dateTimeService.toDateString(depreciationDate.getTime()), assetObjectCodes,fiscalStartMonth, errorMessage));
                // update if fiscal period is 12
                // depreciationBatchDao.updateAssetsCreatedInLastFiscalPeriod(fiscalMonth, fiscalYear);
                updateAssetsDatesForLastFiscalPeriod(fiscalMonth, fiscalYear);
                // Retrieving eligible asset payment details
                LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Getting list of asset payments eligible for depreciation.");
                Collection<AssetPaymentInfo> depreciableAssetsCollection = depreciationBatchDao.getListOfDepreciableAssetPaymentInfo(fiscalYear, fiscalMonth, depreciationDate);
                // if we have assets eligible for depreciation then, calculate depreciation and create glpe's transactions
                if (depreciableAssetsCollection != null && !depreciableAssetsCollection.isEmpty()) {
                    SortedMap<String, AssetDepreciationTransaction> depreciationTransactions = this.calculateDepreciation(fiscalYear, fiscalMonth, depreciableAssetsCollection, depreciationDate, assetObjectCodes);
                        processGeneralLedgerPendingEntry(fiscalYear, fiscalMonth, documentNos, depreciationTransactions);
                } else {
                    throw new IllegalStateException(kualiConfigurationService.getPropertyValueAsString(CamsKeyConstants.Depreciation.NO_ELIGIBLE_FOR_DEPRECIATION_ASSETS_FOUND));
                }
            }
        } catch (Exception e) {
            LOG.error("Error occurred");
            LOG.error(CamsConstants.Depreciation.DEPRECIATION_BATCH + "**************************************************************************");
            LOG.error(CamsConstants.Depreciation.DEPRECIATION_BATCH + "AN ERROR HAS OCCURRED! - ERROR: " + e.getMessage(),e);
            LOG.error(CamsConstants.Depreciation.DEPRECIATION_BATCH + "**************************************************************************");
            hasErrors = true;
            errorMsg = "Depreciation process ran unsucessfuly.\nReason:" + e.getMessage();
        } finally {
            if (!hasErrors && executeJob) {
                int fiscalStartMonth = Integer.parseInt(optionsService.getCurrentYearOptions().getUniversityFiscalYearStartMo());
                reportLog.addAll(depreciableAssetsDao.generateStatistics(false, documentNos, fiscalYear, fiscalMonth, depreciationDate,dateTimeService.toDateString(depreciationDate.getTime()), assetObjectCodes, fiscalStartMonth, errorMessage));
            }
            // the report will be generated only when there is an error or when the log has something.
            if (!reportLog.isEmpty() || !errorMsg.trim().equals("")) {
                reportService.generateDepreciationReport(reportLog, errorMsg, depreciationDateParameter);
            }

            LOG.debug("*******" + CamsConstants.Depreciation.DEPRECIATION_BATCH + " HAS ENDED *******");
        }
    }

    @Override
    public Collection<AssetObjectCode> getAssetObjectCodes(Integer fiscalYear) {
        LOG.debug("DepreciableAssetsDAoOjb.getAssetObjectCodes() -  started");
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Getting asset object codes.");

        Collection<AssetObjectCode> assetObjectCodesCollection;
        HashMap<String, Object> fields = new HashMap<String, Object>();
        fields.put(CamsPropertyConstants.AssetObject.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        fields.put(CamsPropertyConstants.AssetObject.ACTIVE, Boolean.TRUE);
        assetObjectCodesCollection = businessObjectService.findMatching(AssetObjectCode.class, fields);

        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Finished getting asset object codes - which are:" + assetObjectCodesCollection.toString());
        LOG.debug("DepreciableAssetsDAoOjb.getAssetObjectCodes() -  ended");
        return assetObjectCodesCollection;
    }

    @Override
    public boolean resetPeriodValuesWhenFirstFiscalPeriod() {
        LOG.debug("resetPeriodValuesWhenFirstFiscalPeriod() started");

        try {
            depreciationBatchDao.resetPeriodValuesWhenFirstFiscalPeriod(1);
            return true;
        } catch (Exception e) {
            LOG.error("resetPeriodValuesWhenFirstFiscalPeriod() Exception",e);
            return false;
        }
    }

    protected boolean runAssetDepreciation() throws ParseException {
        boolean executeJob = false;
        List<String> errorMessages = new ArrayList<String>();
        Date currentDate = DateUtils.truncate(dateTimeService.getCurrentDate(), Calendar.DATE);
        Date beginDate = getBlankOutBeginDate();
        Date endDate = getBlankOutEndDate();

        if (hasBlankOutPeriodStarted(beginDate, endDate)) {
                String blankOutPeriodrunDate = parameterService.getParameterValueAsString(AssetDepreciationStep.class, CamsConstants.Parameters.BLANK_OUT_PERIOD_RUN_DATE);
                if(!StringHelper.isNullOrEmpty(blankOutPeriodrunDate)){
                    Date runDate = convertToDate(blankOutPeriodrunDate);

                    if(runDate.compareTo(beginDate)>=0 && runDate.compareTo(endDate)<=0) {
                        if(currentDate.equals(runDate)) {
                            executeJob = true;
                        }
                        else {
                            LOG.info("Today is not BLANK_OUT_PERIOD_RUN_DATE. executeJob not set to true");
                        }

                    }
                    else {
                        String blankOutBegin =  parameterService.getParameterValueAsString(AssetDepreciationStep.class, CamsConstants.Parameters.BLANK_OUT_PERIOD_BEGIN);
                        String blankOutEnd =  parameterService.getParameterValueAsString(AssetDepreciationStep.class, CamsConstants.Parameters.BLANK_OUT_PERIOD_END);
                        String message =  "BLANK_OUT_PERIOD_RUN_DATE: " + blankOutPeriodrunDate + " is not in the blank out period range." + "Blank out period range is [ " +
                        blankOutBegin + "-" + blankOutEnd + " ] ." ;
                        errorMessages.add(message);
                        LOG.info(message);
                    }
                }
                else {
                    String message = "Parameter BLANK_OUT_PERIOD_RUN_DATE (component: Asset Depreciation Step) is not set" +
                    " Please set the date correctly to run the job.";
                    errorMessages.add(message);
                    LOG.info(message);
                }
        }
        else {
            if (schedulerService.cronConditionMet(this.cronExpression)) {
                executeJob = true;
            } else {
                LOG.info("Cron condition not met. executeJob not set to true");
            }
        }

        if(!executeJob && !errorMessages.isEmpty()) {
            sendWarningMail(errorMessages);
        }

        return executeJob;
    }

    protected boolean hasBlankOutPeriodStarted(Date beginDate, Date endDate) {
        Date currentDate = DateUtils.truncate(dateTimeService.getCurrentDate(), Calendar.DATE);
        if(ObjectUtils.isNotNull(beginDate) && ObjectUtils.isNotNull(endDate)) {
            if(currentDate.compareTo(beginDate)>=0 && currentDate.compareTo(endDate)<=0 ) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * This method calculate blank out period end date.
     * @return blank out period end date in MM/dd/yyyy format.
     * @throws ParseException
     */
    private Date getBlankOutEndDate() throws ParseException {
        String endDate = parameterService.getParameterValueAsString(AssetDepreciationStep.class, CamsConstants.Parameters.BLANK_OUT_PERIOD_END);
        if(!StringHelper.isNullOrEmpty(endDate)) {
            int endDay = new Integer(StringUtils.substringAfterLast(endDate, "/")).intValue();
            int endMonth = new Integer(StringUtils.substringBeforeLast(endDate, "/")).intValue()-1  ;
            Calendar blankOutEndcalendar = Calendar.getInstance();
            blankOutEndcalendar.set(blankOutEndcalendar.get(Calendar.YEAR), endMonth , endDay);
            return  convertToDate(dateTimeService.toString(blankOutEndcalendar.getTime(), CamsConstants.DateFormats.MONTH_DAY_YEAR));
        } else {
            return null;
        }
    }

    /**
     *
     * This method calculate blank out period begin date.
     * @return blank out period begin date in MM/dd/yyyy format.
     * @throws ParseException
     */
    private Date getBlankOutBeginDate() throws ParseException {
        String beginDate =  parameterService.getParameterValueAsString(AssetDepreciationStep.class, CamsConstants.Parameters.BLANK_OUT_PERIOD_BEGIN);

        if(!StringHelper.isNullOrEmpty(beginDate)) {
            int beginDay = new Integer(StringUtils.substringAfterLast(beginDate, "/")).intValue();
            int beginMonth = new Integer(StringUtils.substringBeforeLast(beginDate, "/")).intValue()-1;
            Calendar blankOutBegincalendar = Calendar.getInstance();
            blankOutBegincalendar.set(blankOutBegincalendar.get(Calendar.YEAR),beginMonth , beginDay);
            return convertToDate(dateTimeService.toString(blankOutBegincalendar.getTime(), CamsConstants.DateFormats.MONTH_DAY_YEAR));
        } else {
            return null;
        }
    }

    private Date convertToDate(String date) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(CamsConstants.DateFormats.MONTH_DAY_YEAR);
        dateFormat.setLenient(false);
        return dateFormat.parse(date);
    }

    /**
     * This method calculates the depreciation of each asset payment, creates the depreciation transactions that will be stored in
     * the general ledger pending entry table
     *
     * @param depreciableAssetsCollection asset payments eligible for depreciation
     * @return SortedMap with a list of depreciation transactions
     */
    protected SortedMap<String, AssetDepreciationTransaction> calculateDepreciation(Integer fiscalYear, Integer fiscalMonth, Collection<AssetPaymentInfo> depreciableAssetsCollection, Calendar depreciationDate, Collection<AssetObjectCode> assetObjectCodes) {
        LOG.debug("calculateDepreciation() - start");

        Collection<String> organizationPlantFundObjectSubType = new ArrayList<String>();
        Collection<String> campusPlantFundObjectSubType = new ArrayList<String>();
        int depreciationPeriod = 1;
        SortedMap<String, AssetDepreciationTransaction> depreciationTransactionSummary = new TreeMap<String, AssetDepreciationTransaction>();
        double ageAtPeriodStart = 0d;
        double ageAtPeriodEnd = 0d;
        double assetLifeInMonths = 0d;
        Calendar assetDepreciationDate = Calendar.getInstance();

        try {
            LOG.debug(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Getting the parameters for the plant fund object sub types.");
            // Getting system parameters needed.
            if (parameterService.parameterExists(AssetDepreciationStep.class, CamsConstants.Parameters.DEPRECIATION_ORGANIZATION_PLANT_FUND_OBJECT_SUB_TYPE)) {
                organizationPlantFundObjectSubType = new ArrayList<>( parameterService.getParameterValuesAsString(AssetDepreciationStep.class, CamsConstants.Parameters.DEPRECIATION_ORGANIZATION_PLANT_FUND_OBJECT_SUB_TYPE) );
            }
            if (parameterService.parameterExists(AssetDepreciationStep.class, CamsConstants.Parameters.DEPRECIATION_CAMPUS_PLANT_FUND_OBJECT_SUB_TYPES)) {
                campusPlantFundObjectSubType = new ArrayList<>( parameterService.getParameterValuesAsString(AssetDepreciationStep.class, CamsConstants.Parameters.DEPRECIATION_CAMPUS_PLANT_FUND_OBJECT_SUB_TYPES) );
            }
            if (parameterService.parameterExists(AssetDepreciationStep.class, CamsConstants.Parameters.DEPRECIATION_PERIOD)) {
                String depreciationPeriodString = parameterService.getParameterValueAsString(AssetDepreciationStep.class, CamsConstants.Parameters.DEPRECIATION_PERIOD);
                if (!StringUtils.isBlank(depreciationPeriodString)) {
                    depreciationPeriod = Integer.parseInt(depreciationPeriodString);
                }
            }

            if (fiscalMonth % depreciationPeriod != 0) {
                throw new IllegalStateException(kualiConfigurationService.getPropertyValueAsString(CamsKeyConstants.Depreciation.FISCAL_MONTH_NOT_VALID));
            }

            LOG.debug("getBaseAmountOfAssets(Collection<AssetPayment> depreciableAssetsCollection) - Started.");
            // Invoking method that will calculate the base amount for each asset payment transactions, which could be more than 1
            // per asset.
            LOG.debug(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Calculating the base amount for each asset.");
            Map<Long, KualiDecimal> salvageValueAssetDeprAmounts = depreciationBatchDao.getPrimaryDepreciationBaseAmountForSV();
            // Get assets with no depreciation
            Set<Long> assetsWithNoDepreciation = depreciationBatchDao.getAssetsWithNoDepreciation();
            // Retrieving the object asset codes.
            Map<String, AssetObjectCode> assetObjectCodeMap = buildChartObjectToCapitalizationObjectMap(assetObjectCodes);
            Map<String, ObjectCode> capitalizationObjectCodes = new HashMap<String, ObjectCode>();
            // Assuming that depreciationDate represents the end of the depreciation period, we need to find the beginning of the period to
            // calculate the assets' age as of that time.
            int depreciationStartMonth = depreciationDate.get(Calendar.MONTH) - depreciationPeriod + 1;
            // For first-time depreciations, we catch up based on the age at the end of the depreciation period.
            int depreciationEndMonth = depreciationStartMonth + depreciationPeriod;

            // Reading asset payments
            LOG.debug(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Reading collection with eligible asset payment details.");
            int counter = 0;
            List<AssetPaymentInfo> saveList = new ArrayList<AssetPaymentInfo>();
            for (AssetPaymentInfo assetPaymentInfo : depreciableAssetsCollection) {
                AssetObjectCode assetObjectCode = assetObjectCodeMap.get(assetPaymentInfo.getChartOfAccountsCode() + "-" + assetPaymentInfo.getFinancialObjectCode());
                if (assetObjectCode == null) {
                    LOG.error(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Asset object code not found for " + fiscalYear + "-" + assetPaymentInfo.getChartOfAccountsCode() + "-" + assetPaymentInfo.getFinancialObjectCode());
                    LOG.error(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Asset payment is not included in depreciation " + assetPaymentInfo.getCapitalAssetNumber() + " - " + assetPaymentInfo.getPaymentSequenceNumber());
                    continue;
                }
                ObjectCode accumulatedDepreciationFinancialObject = getDepreciationObjectCode(fiscalYear, capitalizationObjectCodes, assetPaymentInfo, assetObjectCode.getAccumulatedDepreciationFinancialObjectCode());
                ObjectCode depreciationExpenseFinancialObject = getDepreciationObjectCode(fiscalYear, capitalizationObjectCodes, assetPaymentInfo, assetObjectCode.getDepreciationExpenseFinancialObjectCode());

                if (ObjectUtils.isNull(accumulatedDepreciationFinancialObject)) {
                    LOG.error(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Accumulated Depreciation Financial Object Code not found for " + fiscalYear + "-" + assetPaymentInfo.getChartOfAccountsCode() + "-" + assetObjectCode.getAccumulatedDepreciationFinancialObjectCode());
                    LOG.error(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Asset payment is not included in depreciation " + assetPaymentInfo.getCapitalAssetNumber() + " - " + assetPaymentInfo.getPaymentSequenceNumber());
                    continue;
                }

                if (ObjectUtils.isNull(depreciationExpenseFinancialObject)) {
                    LOG.error(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Depreciation Expense Financial Object Code not found for " + fiscalYear + "-" + assetPaymentInfo.getChartOfAccountsCode() + "-" + assetObjectCode.getDepreciationExpenseFinancialObjectCode());
                    LOG.error(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Asset payment is not included in depreciation " + assetPaymentInfo.getCapitalAssetNumber() + " - " + assetPaymentInfo.getPaymentSequenceNumber());
                    continue;
                }
                Long assetNumber = assetPaymentInfo.getCapitalAssetNumber();
                assetDepreciationDate.setTime(assetPaymentInfo.getDepreciationDate());
                KualiDecimal transactionAmount = KualiDecimal.ZERO;
                KualiDecimal deprAmountSum = salvageValueAssetDeprAmounts.get(assetNumber);
                boolean needToCatchUp = assetsWithNoDepreciation.contains(assetNumber);

                // Calculating the life of the asset in months.
                assetLifeInMonths = assetPaymentInfo.getDepreciableLifeLimit() * 12;
                // Calculating the asset age in months using the depreciation date and the asset service date.
                ageAtPeriodStart = depreciationStartMonth - assetDepreciationDate.get(Calendar.MONTH) + (depreciationDate.get(Calendar.YEAR) - assetDepreciationDate.get(Calendar.YEAR)) * 12;
                ageAtPeriodEnd = depreciationEndMonth - assetDepreciationDate.get(Calendar.MONTH) + (depreciationDate.get(Calendar.YEAR) - assetDepreciationDate.get(Calendar.YEAR)) * 12;
                // If the asset was purchased during the depreciation period, then we may have overcorrected.
                if (ageAtPeriodStart < 0) {
                    ageAtPeriodStart = 0;
                }

                // **************************************************************************************************************
                // CALCULATING ACCUMULATED DEPRECIATION BASED ON FORMULA FOR SINGLE LINE AND SALVAGE VALUE DEPRECIATION METHODS.
                // **************************************************************************************************************
                KualiDecimal primaryDepreciationBaseAmount = assetPaymentInfo.getPrimaryDepreciationBaseAmount();
                if (primaryDepreciationBaseAmount == null) {
                    primaryDepreciationBaseAmount = KualiDecimal.ZERO;
                    assetPaymentInfo.setPrimaryDepreciationBaseAmount(KualiDecimal.ZERO);
                }

                KualiDecimal priorAccumulatedAmount = assetPaymentInfo.getAccumulatedPrimaryDepreciationAmount();
                if (priorAccumulatedAmount == null) {
                    priorAccumulatedAmount = KualiDecimal.ZERO;
                    assetPaymentInfo.setAccumulatedPrimaryDepreciationAmount(KualiDecimal.ZERO);
                }

                KualiDecimal remainingAmount = primaryDepreciationBaseAmount.subtract(priorAccumulatedAmount);
                if (CamsConstants.Asset.DEPRECIATION_METHOD_SALVAGE_VALUE_CODE.equals(assetPaymentInfo.getPrimaryDepreciationMethodCode()) && deprAmountSum != null && deprAmountSum.isNonZero()) {
                    remainingAmount = remainingAmount.subtract((primaryDepreciationBaseAmount.divide(deprAmountSum)).multiply(assetPaymentInfo.getSalvageAmount()));
                }

                BigDecimal remainingAmountPrecise = remainingAmount.bigDecimalValue().setScale(KualiDecimal.SCALE+3);
                BigDecimal transactionAmountPrecise = BigDecimal.ZERO.setScale(KualiDecimal.SCALE+3);

                // If this is the last depreciation run in the asset's life then depreciate the remaining amount:
                if (depreciationPeriod >= assetLifeInMonths - ageAtPeriodStart) {
                    transactionAmount = remainingAmount;
                    transactionAmountPrecise = transactionAmount.bigDecimalValue();
                }
                // For first-time calculation, perform catch up as necssary.
                else if (needToCatchUp) {
                    transactionAmount = remainingAmount.multiply(new KualiDecimal(ageAtPeriodEnd)).divide(new KualiDecimal(assetLifeInMonths));
                    transactionAmountPrecise = remainingAmountPrecise.multiply(new BigDecimal(ageAtPeriodEnd)).divide(new BigDecimal(assetLifeInMonths), RoundingMode.HALF_UP);
                }
                // Otherwise prorate the depreciation over the remaining life:
                else {
                    transactionAmount = remainingAmount.multiply(new KualiDecimal(depreciationPeriod)).divide(new KualiDecimal((assetLifeInMonths - ageAtPeriodStart)));
                    transactionAmountPrecise = remainingAmountPrecise.multiply(new BigDecimal(depreciationPeriod)).divide(new BigDecimal((assetLifeInMonths - ageAtPeriodStart)), RoundingMode.HALF_UP);
                }
                BigDecimal currentRoundingError = transactionAmount.bigDecimalValue().subtract(transactionAmountPrecise);
                Integer currentRoundingErrorMillicents = currentRoundingError.multiply(new BigDecimal(Math.pow(10, transactionAmountPrecise.scale()))).intValue();

                Integer accumulatedRoundingErrorMillicents = currentRoundingErrorMillicents + assetPaymentInfo.getAccumulatedRoundingErrorInMillicents();
                if (accumulatedRoundingErrorMillicents >= 500) {
                    transactionAmount = transactionAmount.subtract(new KualiDecimal(0.01));
                    accumulatedRoundingErrorMillicents = accumulatedRoundingErrorMillicents - 1000;
                } else if (accumulatedRoundingErrorMillicents <= -500) {
                    transactionAmount = transactionAmount.add(new KualiDecimal(0.01));
                    accumulatedRoundingErrorMillicents = accumulatedRoundingErrorMillicents + 1000;
                }

                // Calculating new accumulated depreciation amount
                KualiDecimal accumulatedDepreciationAmount = priorAccumulatedAmount.add(transactionAmount);

                String transactionType = KFSConstants.GL_DEBIT_CODE;
                if (transactionAmount.isNegative()) {
                    transactionType = KFSConstants.GL_CREDIT_CODE;
                }
                String plantAccount = "";
                String plantCOA = "";

                // getting the right Plant Fund Chart code & Plant Fund Account
                if (organizationPlantFundObjectSubType.contains(assetPaymentInfo.getFinancialObjectSubTypeCode())) {
                    plantAccount = assetPaymentInfo.getOrganizationPlantAccountNumber();
                    plantCOA = assetPaymentInfo.getOrganizationPlantChartCode();
                }
                else if (campusPlantFundObjectSubType.contains(assetPaymentInfo.getFinancialObjectSubTypeCode())) {
                    plantAccount = assetPaymentInfo.getCampusPlantAccountNumber();
                    plantCOA = assetPaymentInfo.getCampusPlantChartCode();
                }
                if (StringUtils.isBlank(plantCOA) || StringUtils.isBlank(plantAccount)) {
                    // skip the payment
                    LOG.error(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Plant COA is " + plantCOA + " and plant account is " + plantAccount + " for Financial Object SubType Code = " + assetPaymentInfo.getFinancialObjectSubTypeCode() + " so Asset payment is not included in depreciation " + assetPaymentInfo.getCapitalAssetNumber() + " - " + assetPaymentInfo.getPaymentSequenceNumber());
                    continue;
                }
                LOG.debug("Asset#: " + assetNumber + " - Payment sequence#:" + assetPaymentInfo.getPaymentSequenceNumber() + " - Asset Depreciation date:" + assetDepreciationDate + " - Life:" + assetLifeInMonths + " - Depreciation base amt:" + primaryDepreciationBaseAmount + " - Accumulated depreciation:" + priorAccumulatedAmount + " - Month Elapsed:" + ageAtPeriodStart + " - Calculated accum depreciation:" + accumulatedDepreciationAmount + " - Depreciation amount:" + transactionAmount.toString() + " - Depreciation Method:" + assetPaymentInfo.getPrimaryDepreciationMethodCode());
                assetPaymentInfo.setAccumulatedPrimaryDepreciationAmount(accumulatedDepreciationAmount);
                assetPaymentInfo.setTransactionAmount(transactionAmount);
                assetPaymentInfo.setAccumulatedRoundingErrorInMillicents(accumulatedRoundingErrorMillicents);
                counter++;
                saveList.add(assetPaymentInfo);
                // Saving depreciation amount in the asset payment table
                if (counter % 1000 == 0) {
                    depreciationBatchDao.updateAssetPayments(saveList, fiscalMonth);
                    saveList.clear();
                }
                // if the asset has a depreciation amount <> 0 then, create its debit and credit entries.
                if (transactionAmount.isNonZero()) {
                    this.populateDepreciationTransaction(assetPaymentInfo, transactionType, plantCOA, plantAccount, depreciationExpenseFinancialObject, depreciationTransactionSummary);
                    transactionType = (transactionType.equals(KFSConstants.GL_DEBIT_CODE) ? KFSConstants.GL_CREDIT_CODE : KFSConstants.GL_DEBIT_CODE);
                    this.populateDepreciationTransaction(assetPaymentInfo, transactionType, plantCOA, plantAccount, accumulatedDepreciationFinancialObject, depreciationTransactionSummary);
                }
            }
            depreciationBatchDao.updateAssetPayments(saveList, fiscalMonth);
            saveList.clear();
            return depreciationTransactionSummary;
        }
        catch (Exception e) {
            LOG.error("Error occurred", e);
            throw new IllegalStateException(kualiConfigurationService.getPropertyValueAsString(CamsKeyConstants.Depreciation.ERROR_WHEN_CALCULATING_DEPRECIATION) + " :" + e.getMessage());
        }
    }


    /**
     * This method stores in a collection of business objects the depreciation transaction that later on will be passed to the
     * processGeneralLedgerPendingEntry method in order to store the records in gl pending entry table
     *
     * @param assetPayment asset payment
     * @param transactionType which can be [C]redit or [D]ebit
     * @param plantCOA plant fund char of account code
     * @param plantAccount plant fund char of account code
     * @param financialObject char of account object code linked to the payment
     * @param depreciationTransactionSummary
     * @return none
     */
    protected void populateDepreciationTransaction(AssetPaymentInfo assetPayment, String transactionType, String plantCOA, String plantAccount, ObjectCode deprObjectCode, SortedMap<String, AssetDepreciationTransaction> depreciationTransactionSummary) {
        LOG.debug("populateDepreciationTransaction(AssetDepreciationTransaction depreciationTransaction, AssetPayment assetPayment, String transactionType, KualiDecimal transactionAmount, String plantCOA, String plantAccount, String accumulatedDepreciationFinancialObjectCode, String depreciationExpenseFinancialObjectCode, ObjectCode financialObject, SortedMap<String, AssetDepreciationTransaction> depreciationTransactionSummary) -  started");
        LOG.debug(CamsConstants.Depreciation.DEPRECIATION_BATCH + "populateDepreciationTransaction(): populating AssetDepreciationTransaction pojo - Asset#:" + assetPayment.getCapitalAssetNumber());
        AssetDepreciationTransaction depreciationTransaction = new AssetDepreciationTransaction();
        depreciationTransaction.setCapitalAssetNumber(assetPayment.getCapitalAssetNumber());
        depreciationTransaction.setChartOfAccountsCode(plantCOA);
        depreciationTransaction.setAccountNumber(plantAccount);
        depreciationTransaction.setSubAccountNumber(assetPayment.getSubAccountNumber());
        depreciationTransaction.setFinancialObjectCode(deprObjectCode.getFinancialObjectCode());
        depreciationTransaction.setFinancialSubObjectCode(assetPayment.getFinancialSubObjectCode());
        depreciationTransaction.setFinancialObjectTypeCode(deprObjectCode.getFinancialObjectTypeCode());
        depreciationTransaction.setTransactionType(transactionType);
        depreciationTransaction.setProjectCode(assetPayment.getProjectCode());
        depreciationTransaction.setTransactionAmount(assetPayment.getTransactionAmount());
        depreciationTransaction.setTransactionLedgerEntryDescription(CamsConstants.Depreciation.TRANSACTION_DESCRIPTION + assetPayment.getCapitalAssetNumber());

        String sKey = depreciationTransaction.getKey();

        // Grouping the asset transactions by asset#, accounts, sub account, object, transaction type (C/D), etc. in order to
        // only have one credit and one credit by group.
        if (depreciationTransactionSummary.containsKey(sKey)) {
            depreciationTransaction = depreciationTransactionSummary.get(sKey);
            depreciationTransaction.setTransactionAmount(depreciationTransaction.getTransactionAmount().add(assetPayment.getTransactionAmount()));
        }
        else {
            depreciationTransactionSummary.put(sKey, depreciationTransaction);
        }
        LOG.debug("populateDepreciationTransaction(AssetDepreciationTransaction depreciationTransaction, AssetPayment assetPayment, String transactionType, KualiDecimal transactionAmount, String plantCOA, String plantAccount, String accumulatedDepreciationFinancialObjectCode, String depreciationExpenseFinancialObjectCode, ObjectCode financialObject, SortedMap<String, AssetDepreciationTransaction> depreciationTransactionSummary) -  ended");
    }

    /**
     * This method stores the depreciation transactions in the general pending entry table and creates a new documentHeader entry.
     * <p>
     *
     * @param trans SortedMap with the transactions
     * @return none
     */
    protected void processGeneralLedgerPendingEntry(Integer fiscalYear, Integer fiscalMonth, List<String> documentNos, SortedMap<String, AssetDepreciationTransaction> trans) {
        LOG.debug("populateExplicitGeneralLedgerPendingEntry(AccountingDocument, AccountingLine, GeneralLedgerPendingEntrySequenceHelper, GeneralLedgerPendingEntry) - start");

        String financialSystemDocumentTypeCodeCode;
        try {

            String documentNumber = createNewDepreciationDocument(documentNos);
            financialSystemDocumentTypeCodeCode = CamsConstants.DocumentTypeName.ASSET_DEPRECIATION;
            LOG.debug(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Depreciation Document Type Code: " + financialSystemDocumentTypeCodeCode);

            Timestamp transactionTimestamp = new Timestamp(dateTimeService.getCurrentDate().getTime());

            GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper();
            List<GeneralLedgerPendingEntry> saveList = new ArrayList<GeneralLedgerPendingEntry>();
            int counter = 0;

            for (AssetDepreciationTransaction t : trans.values()) {
                if (t.getTransactionAmount().isNonZero()) {
                    counter++;
                    LOG.debug(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Creating GLPE entries for asset:" + t.getCapitalAssetNumber());
                    GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();
                    explicitEntry.setFinancialSystemOriginationCode(KFSConstants.ORIGIN_CODE_KUALI);
                    explicitEntry.setDocumentNumber(documentNumber);
                    explicitEntry.setTransactionLedgerEntrySequenceNumber(new Integer(sequenceHelper.getSequenceCounter()));
                    sequenceHelper.increment();
                    explicitEntry.setChartOfAccountsCode(t.getChartOfAccountsCode());
                    explicitEntry.setAccountNumber(t.getAccountNumber());
                    explicitEntry.setSubAccountNumber(null);
                    explicitEntry.setFinancialObjectCode(t.getFinancialObjectCode());
                    explicitEntry.setFinancialSubObjectCode(null);
                    explicitEntry.setFinancialBalanceTypeCode(BALANCE_TYPE_ACTUAL);
                    explicitEntry.setFinancialObjectTypeCode(t.getFinancialObjectTypeCode());
                    explicitEntry.setUniversityFiscalYear(fiscalYear);
                    explicitEntry.setUniversityFiscalPeriodCode(StringUtils.leftPad(fiscalMonth.toString().trim(), 2, "0"));
                    explicitEntry.setTransactionLedgerEntryDescription(t.getTransactionLedgerEntryDescription());
                    explicitEntry.setTransactionLedgerEntryAmount(t.getTransactionAmount().abs());
                    explicitEntry.setTransactionDebitCreditCode(t.getTransactionType());
                    explicitEntry.setTransactionDate(new java.sql.Date(transactionTimestamp.getTime()));
                    explicitEntry.setFinancialDocumentTypeCode(financialSystemDocumentTypeCodeCode);
                    explicitEntry.setFinancialDocumentApprovedCode(KFSConstants.DocumentStatusCodes.APPROVED);
                    explicitEntry.setVersionNumber(new Long(1));
                    explicitEntry.setTransactionEntryProcessedTs(new java.sql.Timestamp(transactionTimestamp.getTime()));
                    // this.generalLedgerPendingEntryService.save(explicitEntry);
                    saveList.add(explicitEntry);
                    if (counter % 1000 == 0) {
                        // save here
                        depreciationBatchDao.savePendingGLEntries(saveList);
                        saveList.clear();
                    }
                    if (sequenceHelper.getSequenceCounter() == 99999) {
                        // create new document and sequence is reset
                        documentNumber = createNewDepreciationDocument(documentNos);
                        sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper();
                    }
                }
            }
            // save last list
            depreciationBatchDao.savePendingGLEntries(saveList);
            saveList.clear();

        }
        catch (Exception e) {
            LOG.error("Error occurred", e);
            throw new IllegalStateException(kualiConfigurationService.getPropertyValueAsString(CamsKeyConstants.Depreciation.ERROR_WHEN_UPDATING_GL_PENDING_ENTRY_TABLE) + " :" + e.getMessage());
        }
        LOG.debug("populateExplicitGeneralLedgerPendingEntry(AccountingDocument, AccountingLine, GeneralLedgerPendingEntrySequenceHelper, GeneralLedgerPendingEntry) - end");
    }


    protected String createNewDepreciationDocument(List<String> documentNos) throws WorkflowException {
        WorkflowDocument workflowDocument = workflowDocumentService.createWorkflowDocument(CamsConstants.DocumentTypeName.ASSET_DEPRECIATION, GlobalVariables.getUserSession().getPerson());
        // **************************************************************************************************
        // Create a new document header object
        // **************************************************************************************************
        LOG.debug(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Creating document header entry.");

        FinancialSystemDocumentHeader documentHeader = new FinancialSystemDocumentHeader();
        documentHeader.setWorkflowDocument(workflowDocument);
        documentHeader.setDocumentNumber(workflowDocument.getDocumentId());
        documentHeader.setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.APPROVED);
        documentHeader.setExplanation(CamsConstants.Depreciation.DOCUMENT_DESCRIPTION);
        documentHeader.setDocumentDescription(CamsConstants.Depreciation.DOCUMENT_DESCRIPTION);
        documentHeader.setFinancialDocumentTotalAmount(KualiDecimal.ZERO);

        LOG.debug(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Saving document header entry.");
        this.businessObjectService.save(documentHeader);
        LOG.debug(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Document Header entry was saved successfully.");
        // **************************************************************************************************

        String documentNumber = documentHeader.getDocumentNumber();
        documentNos.add(documentNumber);
        LOG.debug(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Document Number Created: " + documentNumber);
        return documentNumber;
    }


    /**
     * Depreciation object code is returned from cache or from DB
     *
     * @param capitalizationObjectCodes collection cache
     * @param assetPaymentInfo
     * @param capitalizationFinancialObjectCode
     * @return
     */
    protected ObjectCode getDepreciationObjectCode(Integer fiscalYear, Map<String, ObjectCode> capObjectCodesCache, AssetPaymentInfo assetPaymentInfo, String capitalizationFinancialObjectCode) {
        ObjectCode deprObjCode = null;
        String key = assetPaymentInfo.getChartOfAccountsCode() + "-" + capitalizationFinancialObjectCode;
        if ((deprObjCode = capObjectCodesCache.get(key)) == null) {
            deprObjCode = objectCodeService.getByPrimaryId(fiscalYear, assetPaymentInfo.getChartOfAccountsCode(), capitalizationFinancialObjectCode);
            if (ObjectUtils.isNotNull(deprObjCode)) {
                capObjectCodesCache.put(key, deprObjCode);
            }
        }
        return deprObjCode;
    }

    /**
     * Builds map between object code to corresponding asset object code
     *
     * @return Map
     */
    protected Map<String, AssetObjectCode> buildChartObjectToCapitalizationObjectMap(Collection<AssetObjectCode> assetObjectCodes) {
        Map<String, AssetObjectCode> assetObjectCodeMap = new HashMap<String, AssetObjectCode>();

        for (AssetObjectCode assetObjectCode : assetObjectCodes) {
            List<ObjectCode> objectCodes = assetObjectCode.getObjectCode();
            for (ObjectCode objectCode : objectCodes) {
                String key = objectCode.getChartOfAccountsCode() + "-" + objectCode.getFinancialObjectCode();
                if (!assetObjectCodeMap.containsKey(key)) {
                    assetObjectCodeMap.put(key, assetObjectCode);
                }
            }
        }
        return assetObjectCodeMap;
    }

    private void sendWarningMail(List<String> errorMessages) {

        LOG.debug("sendEmail() starting");
        MailMessage message = new MailMessage();

        message.setFromAddress(mailService.getBatchMailingList());
        String subject = "Asset Depreciation Job status";
        message.setSubject(subject);
        Collection<String> toAddresses =  parameterService.getParameterValuesAsString(AssetDepreciationStep.class, CamsConstants.Parameters.RUN_DATE_NOTIFICATION_EMAIL_ADDRESSES);
        message.getToAddresses().add(toAddresses);


        StringBuffer sb = new StringBuffer();
        sb.append("Unable to run Depreciation process.Reason:\n");
        for (String msg : errorMessages) {
            sb.append(msg + "\n");
        }

        sb.append("Please set the dates correctly to run the job.");

        message.setMessage(sb.toString());

        try {
            mailService.sendMessage(message);
        }
        catch (MessagingException e) {
            LOG.error("sendErrorEmail() Invalid email address. Message not sent", e);
        }
        catch (InvalidAddressException e) {
            LOG.error("sendErrorEmail() Invalid email address. Message not sent", e);
        }
    }

    /**
     * Depreciation (end of year) Period 13 assets incorrect depreciation start date Update asset created in period 13 with in
     * service date and depreciate date if batch runs in the last fiscal period
     *
     * @param fiscalMonth2
     * @param fiscalYear2
     */
    protected void updateAssetsDatesForLastFiscalPeriod(Integer fiscalMonth, Integer fiscalYear) {
        if (fiscalMonth == 12) {
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Starting updateAssetsCreatedInLastFiscalPeriod()");
            // Getting last date of fiscal year
            Date lastDateOfFiscalYear = universityDateService.getLastDateOfFiscalYear(fiscalYear);
            if (lastDateOfFiscalYear == null) {
                throw new IllegalStateException(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_UNIV_DATE_NOT_FOUND));
            }
            final java.sql.Date lastFiscalYearDate = new java.sql.Date(lastDateOfFiscalYear.getTime());

            List<String> movableEquipmentObjectSubTypes = new ArrayList<String>();
            if (parameterService.parameterExists(Asset.class, CamsConstants.Parameters.MOVABLE_EQUIPMENT_OBJECT_SUB_TYPES)) {
                movableEquipmentObjectSubTypes.addAll(parameterService.getParameterValuesAsString(Asset.class, CamsConstants.Parameters.MOVABLE_EQUIPMENT_OBJECT_SUB_TYPES));
            }

            // Only update assets with a object sub type code equals to any MOVABLE_EQUIPMENT_OBJECT_SUB_TYPES.
            if (!movableEquipmentObjectSubTypes.isEmpty()) {
                updateAssetDatesPerConvention(lastFiscalYearDate, movableEquipmentObjectSubTypes, CamsConstants.DepreciationConvention.CREATE_DATE);
                updateAssetDatesPerConvention(lastFiscalYearDate, movableEquipmentObjectSubTypes, CamsConstants.DepreciationConvention.FULL_YEAR);
                updateAssetDatesPerConvention(lastFiscalYearDate, movableEquipmentObjectSubTypes, CamsConstants.DepreciationConvention.HALF_YEAR);
            }
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Finished updateAssetsCreatedInLastFiscalPeriod()");
        }
    }

    /**
     * Depreciation (end of year) Period 13 assets incorrect depreciation start date
     * <P>
     * Update assets created in period 13 with its in-service date and depreciation date per depreciation convention.
     *
     * @param lastFiscalYearDate
     * @param movableEquipmentObjectSubTypes
     * @param depreciationConventionCd
     */
    protected void updateAssetDatesPerConvention(final java.sql.Date lastFiscalYearDate, List<String> movableEquipmentObjectSubTypes, String depreciationConventionCd) {
        List<Map<String, Object>> selectedAssets = depreciationBatchDao.getAssetsByDepreciationConvention(lastFiscalYearDate, movableEquipmentObjectSubTypes, depreciationConventionCd);

        if (selectedAssets != null && !selectedAssets.isEmpty()) {
            List<String> assetNumbers = new ArrayList<String>();
            for (Map<String, Object> assetMap : selectedAssets) {
                assetNumbers.add(((BigDecimal) assetMap.get("CPTLAST_NBR")).toString());
            }
            // calculate asset deprecation date per depreciation convention
            java.sql.Date depreciationDate = assetDateService.computeDepreciationDateForPeriod13(depreciationConventionCd, lastFiscalYearDate);
            depreciationBatchDao.updateAssetInServiceAndDepreciationDate(assetNumbers, lastFiscalYearDate, depreciationDate);
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Finished updateAssetInServiceAndDepreciationDate() for Depreciation convention " + depreciationConventionCd + " for " + assetNumbers.size() + " assets : " + assetNumbers.toString());
        }
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setDepreciableAssetsDao(DepreciableAssetsDao depreciableAssetsDao) {
        this.depreciableAssetsDao = depreciableAssetsDao;
    }

    public void setCamsReportService(ReportService reportService) {
        this.reportService = reportService;
    }

    public void setConfigurationService(ConfigurationService kcs) {
        kualiConfigurationService = kcs;
    }

    public void setGeneralLedgerPendingEntryService(GeneralLedgerPendingEntryService generalLedgerPendingEntryService) {
        this.generalLedgerPendingEntryService = generalLedgerPendingEntryService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    @Override
    public void setDepreciationBatchDao(DepreciationBatchDao depreciationBatchDao) {
        this.depreciationBatchDao = depreciationBatchDao;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }

    public void setAssetDateService(AssetDateService assetDateService) {
        this.assetDateService = assetDateService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public void setSchedulerService(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    public void setAssetService(AssetService assetService) {
        this.assetService = assetService;
    }

    public void setObjectCodeService(ObjectCodeService objectCodeService) {
        this.objectCodeService = objectCodeService;
    }

    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }
}
