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
package org.kuali.kfs.module.ld.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.integration.ld.LaborLedgerBalance;
import org.kuali.kfs.integration.ld.LaborLedgerExpenseTransferAccountingLine;
import org.kuali.kfs.integration.ld.LaborLedgerObject;
import org.kuali.kfs.integration.ld.LaborLedgerPositionObjectBenefit;
import org.kuali.kfs.integration.ld.LaborLedgerPositionObjectGroup;
import org.kuali.kfs.integration.ld.LaborModuleService;
import org.kuali.kfs.kns.lookup.HtmlData;
import org.kuali.kfs.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.kfs.krad.bo.AdHocRoutePerson;
import org.kuali.kfs.krad.bo.AdHocRouteRecipient;
import org.kuali.kfs.krad.bo.DocumentHeader;
import org.kuali.kfs.krad.rules.rule.event.SaveDocumentEvent;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.DocumentService;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.SessionDocumentService;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.krad.util.UrlFactory;
import org.kuali.kfs.krad.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.module.ld.LaborPropertyConstants;
import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.module.ld.businessobject.LedgerEntry;
import org.kuali.kfs.module.ld.businessobject.LedgerEntryGLSummary;
import org.kuali.kfs.module.ld.document.SalaryExpenseTransferDocument;
import org.kuali.kfs.module.ld.service.LaborBenefitsCalculationService;
import org.kuali.kfs.module.ld.service.LaborLedgerBalanceService;
import org.kuali.kfs.module.ld.service.LaborLedgerEntryService;
import org.kuali.kfs.module.ld.service.LaborLedgerPendingEntryService;
import org.kuali.kfs.module.ld.service.LaborOriginEntryService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.AccountingLineOverride;
import org.kuali.kfs.sys.businessobject.AccountingLineOverride.COMPONENT;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * This implements the service methods that may be used by outside of labor module
 */
@Transactional
public class LaborModuleServiceImpl implements LaborModuleService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborModuleServiceImpl.class);

    private final static String LINK_DOCUMENT_NUMBER_TO_LABOR_ORIGIN_CODES_PARAM_NAME = "LINK_DOCUMENT_NUMBER_TO_LABOR_ORIGIN_CODES";
    private final static String GL_LABOR_ENTRY_SUMMARIZATION_INQUIRY_BASE_URL = "laborGLLaborEntrySummarizationInquiry.do";
    private final static String GL_LABOR_ENTRY_SUMMARIZATION_INQUIRY_METHOD = "viewResults";

    @Override
    public KualiDecimal calculateFringeBenefitFromLaborObject(LaborLedgerObject laborLedgerObject, KualiDecimal salaryAmount, String accountNumber, String subAccountNumber) {
        return getLaborBenefitsCalculationService().calculateFringeBenefit(laborLedgerObject, salaryAmount, accountNumber, subAccountNumber);
    }

    @Override
    public KualiDecimal calculateFringeBenefit(Integer fiscalYear, String chartCode, String objectCode, KualiDecimal salaryAmount, String accountNumber, String subAccountNumber) {
        return getLaborBenefitsCalculationService().calculateFringeBenefit(fiscalYear, chartCode, objectCode, salaryAmount, accountNumber, subAccountNumber);
    }

    @Override
    public void createAndBlankApproveSalaryExpenseTransferDocument(String documentDescription, String explanation, String annotation, List<String> adHocRecipients, List<LaborLedgerExpenseTransferAccountingLine> sourceAccountingLines, List<LaborLedgerExpenseTransferAccountingLine> targetAccountingLines) throws WorkflowException {
        LOG.debug("createSalaryExpenseTransferDocument() start");

        if (sourceAccountingLines == null || sourceAccountingLines.isEmpty()) {
            LOG.info("Cannot create a salary expense document when the given source accounting line is empty.");
            return;
        }

        if (targetAccountingLines == null || targetAccountingLines.isEmpty()) {
            LOG.info("Cannot create a salary expense document when the given target accounting line is empty.");
            return;
        }

        SalaryExpenseTransferDocument document = (SalaryExpenseTransferDocument) getDocumentService().getNewDocument(SalaryExpenseTransferDocument.class);

        document.setEmplid(sourceAccountingLines.get(0).getEmplid());
        document.setSourceAccountingLines(sourceAccountingLines);
        document.setTargetAccountingLines(targetAccountingLines);

        DocumentHeader documentHeader = document.getDocumentHeader();
        documentHeader.setDocumentDescription(documentDescription);
        documentHeader.setExplanation(explanation);

        document.prepareForSave(new SaveDocumentEvent(document));
        document.populateDocumentForRouting();

        String documentTitle = document.getDocumentTitle();
        if (StringUtils.isNotBlank(documentTitle)) {
            document.getDocumentHeader().getWorkflowDocument().setTitle(documentTitle);
        }

        String organizationDocumentNumber = document.getDocumentHeader().getOrganizationDocumentNumber();
        if (StringUtils.isNotBlank(organizationDocumentNumber)) {
            document.getDocumentHeader().getWorkflowDocument().setApplicationDocumentId(organizationDocumentNumber);
        }

        this.getBusinessObjectService().save(document);

        List<AdHocRouteRecipient> adHocRecipientList = new ArrayList<AdHocRouteRecipient>();

        for (String adHocRouteRecipient : adHocRecipients) {
            adHocRecipientList.add(this.buildApprovePersonRecipient(adHocRouteRecipient));
        }

        // blanket approve salary expense transfer doc bypassing all rules
        SpringContext.getBean(WorkflowDocumentService.class).blanketApprove(document.getDocumentHeader().getWorkflowDocument(), annotation, adHocRecipientList);
        SpringContext.getBean(SessionDocumentService.class).addDocumentToUserSession(GlobalVariables.getUserSession(), document.getDocumentHeader().getWorkflowDocument());

    }

    protected AdHocRouteRecipient buildApprovePersonRecipient(String userId) {
        AdHocRouteRecipient adHocRouteRecipient = new AdHocRoutePerson();
        adHocRouteRecipient.setActionRequested(KewApiConstants.ACTION_REQUEST_APPROVE_REQ);
        adHocRouteRecipient.setId(userId);
        return adHocRouteRecipient;
    }

    @Override
    public int countPendingSalaryExpenseTransfer(String emplid) {
        Map<String, Object> positiveFieldValues = new HashMap<String, Object>();
        positiveFieldValues.put(KFSPropertyConstants.EMPLID, emplid);
        positiveFieldValues.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, KFSConstants.FinancialDocumentTypeCodes.SALARY_EXPENSE_TRANSFER);

        List<String> approvedCodes = Arrays.asList(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.APPROVED, KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.PROCESSED);
        Map<String, Object> negativeFieldValues = new HashMap<String, Object>();
        negativeFieldValues.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_APPROVED_CODE, approvedCodes);

        return getBusinessObjectService().countMatching(LaborLedgerPendingEntry.class, positiveFieldValues, negativeFieldValues);
    }

    @Override
    public List<String> findEmployeesWithPayType(Map<Integer, Set<String>> payPeriods, List<String> balanceTypes, Map<String, Set<String>> earnCodePayGroupMap) {
        return getLaborLedgerEntryService().findEmployeesWithPayType(payPeriods, balanceTypes, earnCodePayGroupMap);
    }

    @Override
    public boolean isEmployeeWithPayType(String emplid, Map<Integer, Set<String>> payPeriods, List<String> balanceTypes, Map<String, Set<String>> earnCodePayGroupMap) {
        return getLaborLedgerEntryService().isEmployeeWithPayType(emplid, payPeriods, balanceTypes, earnCodePayGroupMap);
    }

    @Override
    public Collection<LaborLedgerBalance> findLedgerBalances(Map<String, Collection<String>> fieldValues, Map<String, Collection<String>> excludedFieldValues, Set<Integer> fiscalYears, List<String> balanceTypes, List<String> positionObjectGroupCodes) {
        Collection<LaborLedgerBalance> LaborLedgerBalances = new ArrayList<LaborLedgerBalance>();

        Map<String, List<String>> excludedFieldValueList = new HashMap<String, List<String>>();
        for (Map.Entry<String, Collection<String>> e : excludedFieldValues.entrySet()) {
            // convert collection to list
            List<String> list = new ArrayList<String>(e.getValue());
            Collections.sort(list);
            excludedFieldValueList.put(e.getKey(), list);
        }
        Map<String, List<String>> fieldValueList = new HashMap<String, List<String>>();
        for (Map.Entry<String, Collection<String>> e : fieldValues.entrySet()) {
            // convert collection to list
            List<String> list = new ArrayList<String>(e.getValue());
            Collections.sort(list);
            fieldValueList.put(e.getKey(), list);
        }
        Collection<LedgerBalance> ledgerBalances = getLaborLedgerBalanceService().findLedgerBalances(fieldValueList, excludedFieldValueList, fiscalYears, balanceTypes, positionObjectGroupCodes);
        for (LedgerBalance balance : ledgerBalances) {
            LaborLedgerBalances.add(balance);
        }
        return LaborLedgerBalances;
    }

    public LaborLedgerPositionObjectGroup getLaborLedgerPositionObjectGroup(String positionObjectGroupCode) {
        Map<String, Object> primaryKeys = new HashMap<String, Object>();
        primaryKeys.put(LaborPropertyConstants.POSITION_OBJECT_GROUP_CODE, positionObjectGroupCode);

        return getKualiModuleService().getResponsibleModuleService(LaborLedgerPositionObjectGroup.class).getExternalizableBusinessObject(LaborLedgerPositionObjectGroup.class, primaryKeys);
    }

    @Override
    public boolean doesLaborLedgerPositionObjectGroupExist(String positionObjectGroupCode) {
        return this.getLaborLedgerPositionObjectGroup(positionObjectGroupCode) != null;
    }

    @Override
    public LaborLedgerObject retrieveLaborLedgerObject(Integer fiscalYear, String chartOfAccountsCode, String objectCode) {
        Map<String, Object> searchCriteria = new HashMap<String, Object>();
        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectCode);

        return getKualiModuleService().getResponsibleModuleService(LaborLedgerObject.class).getExternalizableBusinessObject(LaborLedgerObject.class, searchCriteria);
    }

    @Override
    public LaborLedgerObject retrieveLaborLedgerObject(ObjectCode financialObject) {
        if (financialObject == null) {
            throw new IllegalArgumentException("The given financial object cannot be null.");
        }

        Integer fiscalYear = financialObject.getUniversityFiscalYear();
        String chartOfAccountsCode = financialObject.getChartOfAccountsCode();
        String financialObjectCode = financialObject.getFinancialObjectCode();

        return this.retrieveLaborLedgerObject(fiscalYear, chartOfAccountsCode, financialObjectCode);
    }

    @Override
    public boolean hasPendingLaborLedgerEntry(String chartOfAccountsCode, String accountNumber) {
        return getLaborLedgerPendingEntryService().hasPendingLaborLedgerEntry(chartOfAccountsCode, accountNumber);
    }

    @Override
    public List<LaborLedgerPositionObjectBenefit> retrieveLaborPositionObjectBenefits(Integer fiscalYear, String chartOfAccountsCode, String objectCode) {
        Map<String, Object> searchCriteria = new HashMap<String, Object>();

        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectCode);
        return getKualiModuleService().getResponsibleModuleService(LaborLedgerPositionObjectBenefit.class).getExternalizableBusinessObjectsList(LaborLedgerPositionObjectBenefit.class, searchCriteria);
    }

    @Override
    public List<LaborLedgerPositionObjectBenefit> retrieveActiveLaborPositionObjectBenefits(Integer fiscalYear, String chartOfAccountsCode, String objectCode) {
        Map<String, Object> searchCriteria = new HashMap<String, Object>();

        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectCode);
        searchCriteria.put(KFSPropertyConstants.ACTIVE, KFSConstants.ACTIVE_INDICATOR);

        return getKualiModuleService().getResponsibleModuleService(LaborLedgerPositionObjectBenefit.class).getExternalizableBusinessObjectsList(LaborLedgerPositionObjectBenefit.class, searchCriteria);
    }

    @Override
    public boolean hasFringeBenefitProducingObjectCodes(Integer fiscalYear, String chartOfAccountsCode, String financialObjectCode) {
        List<LaborLedgerPositionObjectBenefit> objectBenefits = this.retrieveActiveLaborPositionObjectBenefits(fiscalYear, chartOfAccountsCode, financialObjectCode);
        return (objectBenefits != null && !objectBenefits.isEmpty());
    }

    @Override
    public Collection<String> getLaborLedgerGLOriginCodes() {
        return getParameterService().getParameterValuesAsString(LedgerEntry.class, LINK_DOCUMENT_NUMBER_TO_LABOR_ORIGIN_CODES_PARAM_NAME);
    }

    /**
     * Builds the url for the given GL entry to go to inquiry screen for related LD entries
     *
     * @see org.kuali.kfs.integration.ld.LaborModuleService#getInquiryUrlForGeneralLedgerEntryDocumentNumber(org.kuali.kfs.gl.businessobject.Entry)
     */
    @Override
    public HtmlData getInquiryUrlForGeneralLedgerEntryDocumentNumber(Entry entry) {
        Properties props = new Properties();
        props.setProperty(KFSConstants.DISPATCH_REQUEST_PARAMETER, GL_LABOR_ENTRY_SUMMARIZATION_INQUIRY_METHOD);
        props.setProperty(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, entry.getUniversityFiscalYear().toString());
        props.setProperty(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE, entry.getUniversityFiscalPeriodCode());
        props.setProperty(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, entry.getChartOfAccountsCode());
        props.setProperty(KFSPropertyConstants.ACCOUNT_NUMBER, entry.getAccountNumber());
        props.setProperty(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, entry.getSubAccountNumber());
        props.setProperty(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, entry.getFinancialObjectCode());
        props.setProperty(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, entry.getFinancialSubObjectCode());
        props.setProperty(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, entry.getFinancialBalanceTypeCode());
        props.setProperty(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, entry.getFinancialObjectTypeCode());
        props.setProperty(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, entry.getFinancialDocumentTypeCode());
        props.setProperty(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE, entry.getFinancialSystemOriginationCode());
        props.setProperty(KFSPropertyConstants.DOCUMENT_NUMBER, entry.getDocumentNumber());
        props.setProperty(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, LedgerEntryGLSummary.class.getName());
        HtmlData htmlData = new AnchorHtmlData(UrlFactory.parameterizeUrl(GL_LABOR_ENTRY_SUMMARIZATION_INQUIRY_BASE_URL, props), entry.getDocumentNumber());
        return htmlData;
    }

    @Override
    public String getBenefitRateCategoryCode(String chartOfAccountsCode, String accountNumber, String subAccountNumber) {
        return getLaborBenefitsCalculationService().getBenefitRateCategoryCode(chartOfAccountsCode, accountNumber, subAccountNumber);
    }

    @Override
    public String getCostSharingSourceAccountNumber() {
        return getLaborBenefitsCalculationService().getCostSharingSourceAccountNumber();
    }

    @Override
    public String getCostSharingSourceSubAccountNumber() {
        return getLaborBenefitsCalculationService().getCostSharingSourceSubAccountNumber();
    }

    @Override
    public String getCostSharingSourceChartOfAccountsCode() {
        return getLaborBenefitsCalculationService().getCostSharingSourceAccountChartOfAccountsCode();
    }

    @Override
    public AccountingLineOverride determineNeededOverrides(AccountingDocument document, AccountingLine line) {
        boolean isDocumentFinalOrProcessed = false;
        if (ObjectUtils.isNotNull(document)) {
            AccountingDocument accountingDocument = document;
            isDocumentFinalOrProcessed = accountingDocument.isDocumentFinalOrProcessed();
        }
        Set<Integer> neededOverrideComponents = new HashSet<Integer>();
        if (AccountingLineOverride.needsExpiredAccountOverride(line, isDocumentFinalOrProcessed)) {
            neededOverrideComponents.add(COMPONENT.EXPIRED_ACCOUNT);
        }
        if (AccountingLineOverride.needsObjectBudgetOverride(line.getAccount(), line.getObjectCode())) {
            neededOverrideComponents.add(COMPONENT.NON_BUDGETED_OBJECT);
        }
        if (AccountingLineOverride.needsNonFringAccountOverride(line.getAccount())) {
            neededOverrideComponents.add(COMPONENT.NON_FRINGE_ACCOUNT_USED);
        }
        Integer[] inputComponentArray = neededOverrideComponents.toArray(new Integer[neededOverrideComponents.size()]);

        return AccountingLineOverride.valueOf(inputComponentArray);
    }

    @Override
    @Deprecated
    public AccountingLineOverride determineNeededOverrides(AccountingLine line) {
        Set<Integer> neededOverrideComponents = new HashSet<Integer>();
        if (AccountingLineOverride.needsExpiredAccountOverride(line.getAccount())) {
            neededOverrideComponents.add(COMPONENT.EXPIRED_ACCOUNT);
        }
        if (AccountingLineOverride.needsObjectBudgetOverride(line.getAccount(), line.getObjectCode())) {
            neededOverrideComponents.add(COMPONENT.NON_BUDGETED_OBJECT);
        }
        if (AccountingLineOverride.needsNonFringAccountOverride(line.getAccount())) {
            neededOverrideComponents.add(COMPONENT.NON_FRINGE_ACCOUNT_USED);
        }
        Integer[] inputComponentArray = neededOverrideComponents.toArray(new Integer[neededOverrideComponents.size()]);

        return AccountingLineOverride.valueOf(inputComponentArray);
    }

    public LaborBenefitsCalculationService getLaborBenefitsCalculationService() {
        return SpringContext.getBean(LaborBenefitsCalculationService.class);
    }

    public LaborLedgerEntryService getLaborLedgerEntryService() {
        return SpringContext.getBean(LaborLedgerEntryService.class);
    }

    public LaborLedgerBalanceService getLaborLedgerBalanceService() {
        return SpringContext.getBean(LaborLedgerBalanceService.class);
    }

    public DocumentService getDocumentService() {
        return SpringContext.getBean(DocumentService.class);
    }

    public BusinessObjectService getBusinessObjectService() {
        return SpringContext.getBean(BusinessObjectService.class);
    }

    public LaborLedgerPendingEntryService getLaborLedgerPendingEntryService() {
        return SpringContext.getBean(LaborLedgerPendingEntryService.class);
    }

    public LaborOriginEntryService getLaborOriginEntryService() {
        return SpringContext.getBean(LaborOriginEntryService.class);
    }

    public ParameterService getParameterService() {
        return SpringContext.getBean(ParameterService.class);
    }

    public KualiModuleService getKualiModuleService() {
        return SpringContext.getBean(KualiModuleService.class);
    }
}
