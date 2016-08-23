<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   -
   - Copyright 2005-2016 The Kuali Foundation
   -
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   -
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   -
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<c:if test="${!accountingLineScriptsLoaded}">
    <script type='text/javascript' src="dwr/interface/ObjectCodeService.js"></script>
    <script type='text/javascript' src="dwr/interface/SubObjectCodeService.js"></script>
	<script language="JavaScript" type="text/javascript" src="scripts/sys/objectInfo.js"></script>
	<c:set var="accountingLineScriptsLoaded" value="true" scope="request" />
</c:if>

<c:set var="pbglExpenditureAttributes" value="${DataDictionary.PendingBudgetConstructionGeneralLedger.attributes}" />
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT] || KualiForm.systemViewOnly}" />
<c:set var="salsetDisabled" value="${KualiForm.salarySettingDisabled}" />
<c:set var="benecalcDisabled" value="${KualiForm.benefitsCalculationDisabled}" />
<c:set var="salarySettingOnly" value="${KualiForm.document.salarySettingOnly}" />
<c:set var="pbglExpPropertyName" value="document.pendingBudgetConstructionGeneralLedgerExpenditureLines"/>


<fmt:formatNumber value="${KualiForm.document.expenditureAccountLineAnnualBalanceAmountTotal}" var="formattedExpReqTotal" type="number" groupingUsed="true" />

<kul:tab tabTitle="Expenditure" defaultOpen="false" tabErrorKey="${BCConstants.BUDGET_CONSTRUCTION_EXPENDITURE_TAB_ERRORS}" tabItemCount="${formattedExpReqTotal}">
    <div class="tab-container">
        <table class="datatable standard side-margins">
            <bc:subheadingWithDetailToggleRow
                    columnCount="7"
                    subheading="Expenditure"
                    usePercentAdj="${KualiForm.budgetableDocument}"
                    readOnly="${readOnly}"/>
            <tr class="header">
                <th>&nbsp;</th>
                <kul:htmlAttributeHeaderCell attributeEntry="${pbglExpenditureAttributes.financialObjectCode}" />
                <kul:htmlAttributeHeaderCell attributeEntry="${pbglExpenditureAttributes.financialSubObjectCode}" />
                <kul:htmlAttributeHeaderCell attributeEntry="${pbglExpenditureAttributes.financialBeginningBalanceLineAmount}" />
                <kul:htmlAttributeHeaderCell attributeEntry="${pbglExpenditureAttributes.accountLineAnnualBalanceAmount}" />
                <kul:htmlAttributeHeaderCell attributeEntry="${pbglExpenditureAttributes.percentChange}" />
                <th>Action</th>
            </tr>

            <c:if test="${!readOnly && KualiForm.budgetableDocument}">
                <c:set var="valuesMap" value="${KualiForm.newExpenditureLine.valuesMap}"/>

                <tr>
                    <td>
                        <html:hidden property="newExpenditureLine.documentNumber"/>
                        <html:hidden property="newExpenditureLine.universityFiscalYear"/>
                        <html:hidden property="newExpenditureLine.chartOfAccountsCode"/>
                        <html:hidden property="newExpenditureLine.accountNumber"/>
                        <html:hidden property="newExpenditureLine.subAccountNumber"/>
                        <html:hidden property="newExpenditureLine.financialBalanceTypeCode"/>
                        <html:hidden property="newExpenditureLine.financialObjectTypeCode"/>
                        <html:hidden property="newExpenditureLine.versionNumber"/>
                        <html:hidden property="newExpenditureLine.financialBeginningBalanceLineAmount"/>
                    </td>

                    <bc:pbglLineDataCell
                            dataCellCssClass="infoline"
                            accountingLine="newExpenditureLine"
                            field="financialObjectCode"
                            detailFunction="loadObjectCodeInfo"
                            detailField="financialObject.financialObjectCodeName"
                            attributes="${pbglExpenditureAttributes}"
                            lookup="true"
                            inquiry="true"
                            boClassSimpleName="ObjectCode"
                            readOnly="false"
                            displayHidden="false"
                            lookupOrInquiryKeys="universityFiscalYear,chartOfAccountsCode"
                            lookupParameters="expenditureObjectTypeCodesLookup:financialObjectTypeCode"
                            accountingLineValuesMap="${newExpenditureLine.valuesMap}"
                            inquiryExtraKeyValues="universityFiscalYear=${KualiForm.document.universityFiscalYear}"
                            anchor="expenditurenewLineLineAnchor" />

                    <bc:pbglLineDataCell
                            dataCellCssClass="infoline"
                            accountingLine="newExpenditureLine"
                            field="financialSubObjectCode"
                            detailFunction="loadSubObjectInfo"
                            detailFunctionExtraParam="'${KualiForm.document.universityFiscalYear}', "
                            detailField="financialSubObject.financialSubObjectCodeName"
                            attributes="${pbglExpenditureAttributes}"
                            lookup="true"
                            inquiry="true"
                            boClassSimpleName="SubObjectCode"
                            readOnly="false"
                            displayHidden="false"
                            lookupOrInquiryKeys="universityFiscalYear,chartOfAccountsCode,financialObjectCode,accountNumber"
                            accountingLineValuesMap="${newExpenditureLine.valuesMap}"
                            inquiryExtraKeyValues="universityFiscalYear=${KualiForm.document.universityFiscalYear}"
                            lookupAnchor="expenditurenewLineLineAnchor" />

                    <td class="infoline">&nbsp;</td>

                    <bc:pbglLineDataCell
                            dataCellCssClass="datacell"
                            accountingLine="newExpenditureLine"
                            cellProperty="newExpenditureLine.accountLineAnnualBalanceAmount"
                            attributes="${pbglExpenditureAttributes}"
                            field="accountLineAnnualBalanceAmount"
                            fieldAlign="left"
                            readOnly="false"
                            rowSpan="1"
                            dataFieldCssClass="amount" />

                    <td class="infoline">&nbsp;</td>

                    <c:set var="addTabIndex" value="${KualiForm.currentTabIndex}" />
                    <c:set var="dummyIncrementVar" value="${kfunc:incrementTabIndex(KualiForm, tabKey)}" />
                    <td class="infoline">
                        <html:submit
                                property="methodToCall.insertExpenditureLine.anchorexpenditurenewLineLineAnchor"
                                title="Add an Expenditure Line"
                                alt="Add an Expenditure Line"
                                tabindex="${addTabIndex}"
                                styleClass="btn btn-green"
                                value="Add"/>
                    </td>
                </tr>
            </c:if>


            <c:forEach items="${KualiForm.document.pendingBudgetConstructionGeneralLedgerExpenditureLines}" var="item" varStatus="status" >
                <c:set var="itemLineName" value="${pbglExpPropertyName}[${status.index}]"/>
                <c:set var="lineIsEditable" value="${!(readOnly || (item.financialObjectCode == KFSConstants.BudgetConstructionConstants.OBJECT_CODE_2PLG) || (!benecalcDisabled && !empty item.laborObject && item.laborObject.financialObjectFringeOrSalaryCode == BCConstants.LABOR_OBJECT_FRINGE_CODE))}" />
                <c:set var="line2PLGIsDeletable" value="${!readOnly && (item.financialObjectCode == KFSConstants.BudgetConstructionConstants.OBJECT_CODE_2PLG)}" />
                <c:set var="detailSalarylineIsDeleteable" value="${lineIsEditable && (salsetDisabled || empty item.laborObject || !(item.laborObject.detailPositionRequiredIndicator && item.pendingBudgetConstructionAppointmentFundingExists))}" />
                <c:set var="rowspan" value="${ (!KualiForm.hideAdjustmentMeasurement && (lineIsEditable && !(empty item.financialBeginningBalanceLineAmount || item.financialBeginningBalanceLineAmount == 0))) ? 2: 1}"/>

                <tr>
                    <kul:htmlAttributeHeaderCell scope="row" rowspan="${rowspan}">
                        <bc:pbglLineDataCellDetail/>
                    </kul:htmlAttributeHeaderCell>

                    <bc:pbglLineDataCell
                            dataCellCssClass="datacell"
                            accountingLine="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}]"
                            field="financialObjectCode"
                            detailFunction="loadObjectCodeInfo"
                            detailField="financialObject.financialObjectCodeShortName"
                            attributes="${pbglExpenditureAttributes}"
                            lookup="true"
                            inquiry="true"
                            boClassSimpleName="ObjectCode"
                            readOnly="true"
                            displayHidden="false"
                            rowSpan="${rowspan}"
                            lookupOrInquiryKeys="chartOfAccountsCode"
                            lookupUnkeyedFieldConversions="financialObjectTypeCode:document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].objectTypeCode,"
                            accountingLineValuesMap="${item.valuesMap}"
                            inquiryExtraKeyValues="universityFiscalYear=${KualiForm.document.universityFiscalYear}"
                            anchor="expenditureexistingLineLineAnchor${status.index}" />

                    <c:set var="doLookupOrInquiry" value="false"/>
                    <c:if test="${item.financialSubObjectCode ne KualiForm.dashFinancialSubObjectCode}">
                        <c:set var="doLookupOrInquiry" value="true"/>
                    </c:if>

                    <bc:pbglLineDataCell
                            dataCellCssClass="datacell"
                            accountingLine="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}]"
                            field="financialSubObjectCode"
                            detailFunction="loadSubObjectInfo"
                            detailFunctionExtraParam="'${KualiForm.document.universityFiscalYear}', "
                            detailField="financialSubObject.financialSubObjectCdshortNm"
                            attributes="${pbglExpenditureAttributes}"
                            lookup="${doLookupOrInquiry}"
                            inquiry="${doLookupOrInquiry}"
                            boClassSimpleName="SubObjectCode"
                            readOnly="true"
                            displayHidden="false"
                            rowSpan="${rowspan}"
                            lookupOrInquiryKeys="chartOfAccountsCode,financialObjectCode,accountNumber"
                            accountingLineValuesMap="${item.valuesMap}"
                            inquiryExtraKeyValues="universityFiscalYear=${KualiForm.document.universityFiscalYear}" />

                    <c:set var="fieldTrailerValue" value="" />
                    <c:if test="${empty item.financialBeginningBalanceLineAmount}">
                        <c:set var="fieldTrailerValue" value="&nbsp;" />
                    </c:if>

                    <bc:pbglLineDataCell
                            dataCellCssClass="datacell"
                            accountingLine="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}]"
                            cellProperty="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].financialBeginningBalanceLineAmount"
                            attributes="${pbglExpenditureAttributes}"
                            field="financialBeginningBalanceLineAmount"
                            fieldAlign="left"
                            readOnly="true"
                            fieldTrailerValue="${fieldTrailerValue}"
                            rowSpan="1" dataFieldCssClass="amount" />

                    <bc:pbglLineDataCell
                            dataCellCssClass="datacell"
                            accountingLine="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}]"
                            cellProperty="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].accountLineAnnualBalanceAmount"
                            attributes="${pbglExpenditureAttributes}"
                            field="accountLineAnnualBalanceAmount"
                            fieldAlign="left"
                            readOnly="${!lineIsEditable}"
                            rowSpan="1" dataFieldCssClass="amount" />

                    <c:set var="fieldTrailerValue" value="" />
                    <c:if test="${empty item.percentChange}">
                        <c:set var="fieldTrailerValue" value="&nbsp;" />
                    </c:if>

                    <fmt:formatNumber value="${item.percentChange}" var="formattedNumber" type="number" groupingUsed="true" minFractionDigits="2" />

                    <bc:pbglLineDataCell
                            dataCellCssClass="datacell"
                            accountingLine="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].percentChange"
                            cellProperty="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].percentChange"
                            attributes="${pbglExpenditureAttributes}"
                            field="percentChange"
                            formattedNumberValue="${formattedNumber}"
                            fieldTrailerValue="${fieldTrailerValue}"
                            fieldAlign="left"
                            readOnly="true"
                            rowSpan="1" dataFieldCssClass="amount" />

                    <td class="datacell nowrap" rowspan="${rowspan}">
                        <c:choose>
                            <c:when test="${empty item.budgetConstructionMonthly[0]}" >
                                <c:if test="${lineIsEditable && KualiForm.budgetableDocument}">
                                    <html:submit
                                            styleClass="btn btn-default"
                                            property="methodToCall.performMonthlyExpenditureBudget.line${status.index}.anchorexpenditureexistingLineLineAnchor${status.index}"
                                            title="Create Month"
                                            alt="Create Month"
                                            value="Create Month"/>
                                </c:if>
                            </c:when>
                            <c:otherwise>
                                <c:choose>
                                    <c:when test="${lineIsEditable}">
                                        <html:submit
                                                styleClass="btn btn-default"
                                                property="methodToCall.performMonthlyExpenditureBudget.line${status.index}.anchorexpenditureexistingLineLineAnchor${status.index}"
                                                title="Edit Month"
                                                alt="Edit Month"
                                                value="Edit Month"/>
                                    </c:when>
                                    <c:otherwise>
                                        <html:submit
                                                styleClass="btn btn-default"
                                                property="methodToCall.performMonthlyExpenditureBudget.line${status.index}.anchorexpenditureexistingLineLineAnchor${status.index}"
                                                title="View Month"
                                                alt="View Month"
                                                value="View Month"/>
                                    </c:otherwise>
                                </c:choose>
                            </c:otherwise>
                        </c:choose>

                        <html:submit
                                property="methodToCall.performBalanceInquiryForExpenditureLine.line${status.index}.anchorexpenditureexistingLineLineAnchor${status.index}"
                                title="Balance Inquiry For Expenditure Line ${status.index}"
                                alt="Balance Inquiry For Expenditure Line ${status.index}"
                                styleClass="btn btn-default"
                                value="Bal Inquiry"/>

                        <c:if test="${!empty item.positionObjectBenefit[0] && !benecalcDisabled && !salarySettingOnly}">
                            <html:submit
                                    property="methodToCall.performShowBenefits.line${status.index}.anchorexpenditureexistingLineLineAnchor${status.index}"
                                    title="Show Benefits For ${status.index}"
                                    alt="Show Benefits For Line ${status.index}"
                                    styleClass="btn btn-default"
                                    value="Show Benefits"/>
                        </c:if>

                        <c:if test="${!empty item.laborObject && item.laborObject.detailPositionRequiredIndicator && !salsetDisabled}">
                            <html:submit
                                    property="methodToCall.performSalarySetting.line${status.index}.anchorexpenditureexistingLineLineAnchor${status.index}"
                                    title="Perform Salary Setting For ${status.index}"
                                    alt="Perform Salary Setting For Line ${status.index}"
                                    styleClass="btn btn-default"
                                    value="Salary Setting"/>
                        </c:if>

                        <c:if test="${(lineIsEditable && (empty item.financialBeginningBalanceLineAmount || item.financialBeginningBalanceLineAmount == 0) && detailSalarylineIsDeleteable) || line2PLGIsDeletable}">
                            <html:submit
                                    property="methodToCall.deleteExpenditureLine.line${status.index}.anchorexpenditureexistingLineLineAnchor${status.index}"
                                    title="Delete Expenditure Line ${status.index}"
                                    alt="Delete Expenditure Line ${status.index}"
                                    styleClass="btn btn-red"
                                    value="Delete"/>
                        </c:if>
                    </td>
                </tr>

                <c:if test="${rowspan == 2}">
                    <tr>
                        <td class="datacell center nowrap" colspan = "3">
                            <bc:requestAdjustment
                                    attributes="${pbglExpenditureAttributes}"
                                    adjustmentAmountFieldName="${itemLineName}.adjustmentAmount"
                                    methodToCall="adjustExpenditureLinePercent"
                                    lineIndex = "${status.index}"
                                    anchor="anchorexpenditureexistingLineLineAnchor${status.index}"/>
                        </td>
                    </tr>
                </c:if>
            </c:forEach>

            <tr>
                <kul:htmlAttributeHeaderCell literalLabel="Expenditure Totals" colspan="3" horizontal="true" />
                <bc:columnTotalCell
                        dataCellCssClass="infoline"
                        cellProperty="document.expenditureFinancialBeginningBalanceLineAmountTotal"
                        textStyle="${textStyle}"
                        fieldAlign="left"
                        colSpan="1" />
                <bc:columnTotalCell
                        dataCellCssClass="infoline"
                        cellProperty="document.expenditureAccountLineAnnualBalanceAmountTotal"
                        textStyle="${textStyle}"
                        fieldAlign="left"
                        colSpan="1" />
                <bc:columnTotalCell
                        dataCellCssClass="infoline"
                        cellProperty="document.expenditurePercentChangeTotal"
                        textStyle="${textStyle}"
                        fieldAlign="left"
                        colSpan="1" />
                <td class="infoline">&nbsp;</td>
            </tr>

            <c:if test="${!readOnly}">
                <tr>
                    <td colspan="7" class="subhead">
                    <span class="subhead-left">Global Expenditure Actions</span>
                </td>
                </tr>

                <tr>
                    <c:if test="${KualiForm.budgetableDocument}">
                        <th colspan="3" nowrap>&nbsp;
                            <a name="anchorexpenditureControlsAnchor"></a>
                        </th>
                        <td colspan="3" class="datacell center nowrap">
                            <bc:requestAdjustment
                                    attributes="${pbglExpenditureAttributes}"
                                    adjustmentAmountFieldName="expenditureAdjustmentAmount"
                                    methodToCall="adjustAllExpenditureLinesPercent"
                                    anchor="anchorexpenditureControlsAnchor"/>
                        </td>
                        <td colspan="1" class="datacell nowrap center">
                            <html:submit
                                    property="methodToCall.refresh.anchorexpenditureControlsAnchor"
                                    title="Refresh"
                                    alt="Refresh"
                                    styleClass="btn btn-default"
                                    value="Refresh"/>
                            <html:submit
                                    property="methodToCall.performExpMonthSpread.anchorexpenditureControlsAnchor"
                                    title="Monthly Spread"
                                    alt="Monthly Spread"
                                    styleClass="btn btn-default"
                                    value="Month Spread"/>
                            <html:submit
                                    property="methodToCall.performExpMonthDelete.anchorexpenditureControlsAnchor"
                                    title="Monthly Delete"
                                    alt="Monthly Delete"
                                    styleClass="btn btn-red"
                                    value="Month Delete"/>

                            <c:if test="${!benecalcDisabled && !salarySettingOnly}">
                                <html:submit
                                        property="methodToCall.performCalculateBenefits.anchorexpenditureControlsAnchor"
                                        title="Calculate Benefits"
                                        alt="Calculate Benefits"
                                        styleClass="btn btn-default"
                                        value="Calculate Benefits"/>
                            </c:if>
                        </td>
                    </c:if>
                    <c:if test="${!KualiForm.budgetableDocument}">
                        <td colspan="6" class="datacell nowrap">&nbsp;</td>
                        <td colspan="1" class="datacell center nowrap">
                            <html:submit
                                    property="methodToCall.performExpMonthDelete.anchorexpenditureControlsAnchor"
                                    title="Monthly Delete"
                                    alt="Monthly Delete"
                                    styleClass="btn btn-red"
                                    value="Month Delete"/>
                        </td>
                    </c:if>
                </tr>
            </c:if>
        </table>
    </div>
</kul:tab>
