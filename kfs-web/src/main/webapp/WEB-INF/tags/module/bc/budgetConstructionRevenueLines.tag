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

<c:set var="pbglRevenueAttributes" value="${DataDictionary.PendingBudgetConstructionGeneralLedger.attributes}" />
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT] || KualiForm.systemViewOnly}" />
<c:set var="pbglRevPropertyName" value="document.pendingBudgetConstructionGeneralLedgerRevenueLines"/>

<fmt:formatNumber value="${KualiForm.document.revenueAccountLineAnnualBalanceAmountTotal}" var="formattedRevReqTotal" type="number" groupingUsed="true" />

<kul:tab tabTitle="Revenue" defaultOpen="false" tabErrorKey="${BCConstants.BUDGET_CONSTRUCTION_REVENUE_TAB_ERRORS}" tabItemCount="${formattedRevReqTotal}">
    <div class="tab-container">
        <table class="datatable standard side-margins">
            <bc:subheadingWithDetailToggleRow
                    columnCount="7"
                    subheading="Revenue"
                    usePercentAdj="${KualiForm.budgetableDocument}"
                    readOnly="${readOnly}"/>
            <tr class="header">
                <th>&nbsp;</th>
                <kul:htmlAttributeHeaderCell attributeEntry="${pbglRevenueAttributes.financialObjectCode}" />
                <kul:htmlAttributeHeaderCell attributeEntry="${pbglRevenueAttributes.financialSubObjectCode}" />
                <kul:htmlAttributeHeaderCell attributeEntry="${pbglRevenueAttributes.financialBeginningBalanceLineAmount}" />
                <kul:htmlAttributeHeaderCell attributeEntry="${pbglRevenueAttributes.accountLineAnnualBalanceAmount}" />
                <kul:htmlAttributeHeaderCell attributeEntry="${pbglRevenueAttributes.percentChange}" />
                <th>Action</th>
            </tr>

            <c:if test="${!readOnly && KualiForm.budgetableDocument}">
                <c:set var="valuesMap" value="${KualiForm.newRevenueLine.valuesMap}"/>

                <tr>
                    <kul:htmlAttributeHeaderCell literalLabel="Add:" scope="row" rowspan="1">
                        <html:hidden property="newRevenueLine.documentNumber"/>
                        <html:hidden property="newRevenueLine.universityFiscalYear"/>
                        <html:hidden property="newRevenueLine.chartOfAccountsCode"/>
                        <html:hidden property="newRevenueLine.accountNumber"/>
                        <html:hidden property="newRevenueLine.subAccountNumber"/>
                        <html:hidden property="newRevenueLine.financialBalanceTypeCode"/>
                        <html:hidden property="newRevenueLine.financialObjectTypeCode"/>
                        <html:hidden property="newRevenueLine.versionNumber"/>
                        <html:hidden property="newRevenueLine.financialBeginningBalanceLineAmount"/>
                    </kul:htmlAttributeHeaderCell>

                    <bc:pbglLineDataCell
                            dataCellCssClass="infoline"
                            accountingLine="newRevenueLine"
                            field="financialObjectCode"
                            detailFunction="loadObjectCodeInfo"
                            detailField="financialObject.financialObjectCodeName"
                            attributes="${pbglRevenueAttributes}"
                            lookup="true"
                            inquiry="true"
                            boClassSimpleName="ObjectCode"
                            readOnly="false"
                            displayHidden="false"
                            lookupOrInquiryKeys="universityFiscalYear,chartOfAccountsCode"
                            lookupParameters="revenueObjectTypeCodesLookup:financialObjectTypeCode"
                            accountingLineValuesMap="${newRevenueLine.valuesMap}"
                            inquiryExtraKeyValues="universityFiscalYear=${KualiForm.document.universityFiscalYear}"
                            anchor="revenuenewLineLineAnchor" />

                    <bc:pbglLineDataCell
                            dataCellCssClass="infoline"
                            accountingLine="newRevenueLine"
                            field="financialSubObjectCode" detailFunction="loadSubObjectInfo"
                            detailFunctionExtraParam="'${KualiForm.document.universityFiscalYear}', "
                            detailField="financialSubObject.financialSubObjectCodeName"
                            attributes="${pbglRevenueAttributes}" lookup="true" inquiry="true"
                            boClassSimpleName="SubObjectCode"
                            readOnly="false"
                            displayHidden="false"
                            lookupOrInquiryKeys="universityFiscalYear,chartOfAccountsCode,financialObjectCode,accountNumber"
                            accountingLineValuesMap="${newRevenueLine.valuesMap}"
                            inquiryExtraKeyValues="universityFiscalYear=${KualiForm.document.universityFiscalYear}"
                            lookupAnchor="revenuenewLineLineAnchor" />

                    <td class="infoline">&nbsp;</td>

                    <bc:pbglLineDataCell
                            dataCellCssClass="datacell"
                            accountingLine="newRevenueLine"
                            cellProperty="newRevenueLine.accountLineAnnualBalanceAmount"
                            attributes="${pbglRevenueAttributes}"
                            field="accountLineAnnualBalanceAmount"
                            fieldAlign="left"
                            readOnly="false"
                            rowSpan="1" dataFieldCssClass="amount" />

                    <td class="infoline">&nbsp;</td>

                    <c:set var="addTabIndex" value="${KualiForm.currentTabIndex}" />
                    <c:set var="dummyIncrementVar" value="${kfunc:incrementTabIndex(KualiForm, tabKey)}" />

                    <td class="infoline">
                        <html:submit
                                property="methodToCall.insertRevenueLine.anchorrevenuenewLineLineAnchor"
                                title="Add a Revenue Line"
                                alt="Add a Revenue Line"
                                tabindex="${addTabIndex}"
                                styleClass="btn btn-green"
                                value="Add"/>
                    </td>
                </tr>
            </c:if>

            <c:forEach items="${KualiForm.document.pendingBudgetConstructionGeneralLedgerRevenueLines}" var="item" varStatus="status" >
                <c:set var="itemLineName" value="${pbglRevPropertyName}[${status.index}]"/>
                <c:choose>
                    <c:when test="${readOnly}">
                        <c:set var="lineIsEditable" value="false" />
                    </c:when>
                    <c:otherwise>
                        <c:set var="lineIsEditable" value="true" />
                    </c:otherwise>
                </c:choose>
                <c:set var="rowspan" value="${ (!KualiForm.hideAdjustmentMeasurement && (lineIsEditable && !(empty item.financialBeginningBalanceLineAmount || item.financialBeginningBalanceLineAmount == 0))) ? 2: 1}"/>

                <tr>
                    <kul:htmlAttributeHeaderCell scope="row" rowspan="${rowspan}"/>

                    <bc:pbglLineDataCell
                            dataCellCssClass="datacell"
                            accountingLine="document.pendingBudgetConstructionGeneralLedgerRevenueLines[${status.index}]"
                            field="financialObjectCode"
                            detailFunction="loadObjectCodeInfo"
                            detailField="financialObject.financialObjectCodeShortName"
                            attributes="${pbglRevenueAttributes}"
                            lookup="true"
                            inquiry="true"
                            boClassSimpleName="ObjectCode"
                            readOnly="true"
                            displayHidden="false"
                            rowSpan="${rowspan}"
                            lookupOrInquiryKeys="chartOfAccountsCode"
                            lookupUnkeyedFieldConversions="financialObjectTypeCode:document.pendingBudgetConstructionGeneralLedgerRevenueLines[${status.index}].objectTypeCode,"
                            accountingLineValuesMap="${item.valuesMap}"
                            inquiryExtraKeyValues="universityFiscalYear=${KualiForm.document.universityFiscalYear}"
                            anchor="revenueexistingLineLineAnchor${status.index}" />

                    <c:set var="doLookupOrInquiry" value="false"/>
                    <c:if test="${item.financialSubObjectCode ne KualiForm.dashFinancialSubObjectCode}">
                        <c:set var="doLookupOrInquiry" value="true"/>
                    </c:if>

                    <bc:pbglLineDataCell
                            dataCellCssClass="datacell"
                            accountingLine="document.pendingBudgetConstructionGeneralLedgerRevenueLines[${status.index}]"
                            field="financialSubObjectCode"
                            detailFunction="loadSubObjectInfo"
                            detailFunctionExtraParam="'${KualiForm.document.universityFiscalYear}', "
                            detailField="financialSubObject.financialSubObjectCdshortNm"
                            attributes="${pbglRevenueAttributes}"
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
                            accountingLine="document.pendingBudgetConstructionGeneralLedgerRevenueLines[${status.index}]"
                            cellProperty="document.pendingBudgetConstructionGeneralLedgerRevenueLines[${status.index}].financialBeginningBalanceLineAmount"
                            attributes="${pbglRevenueAttributes}"
                            field="financialBeginningBalanceLineAmount"
                            fieldAlign="left"
                            readOnly="true"
                            fieldTrailerValue="${fieldTrailerValue}"
                            rowSpan="1" dataFieldCssClass="amount" />

                    <bc:pbglLineDataCell
                            dataCellCssClass="datacell"
                            accountingLine="document.pendingBudgetConstructionGeneralLedgerRevenueLines[${status.index}]"
                            cellProperty="document.pendingBudgetConstructionGeneralLedgerRevenueLines[${status.index}].accountLineAnnualBalanceAmount"
                            attributes="${pbglRevenueAttributes}"
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
                            accountingLine="document.pendingBudgetConstructionGeneralLedgerRevenueLines[${status.index}].percentChange"
                            cellProperty="document.pendingBudgetConstructionGeneralLedgerRevenueLines[${status.index}].percentChange"
                            attributes="${pbglRevenueAttributes}"
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
                                            property="methodToCall.performMonthlyRevenueBudget.line${status.index}.anchorrevenueexistingLineLineAnchor${status.index}"
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
                                                property="methodToCall.performMonthlyRevenueBudget.line${status.index}.anchorrevenueexistingLineLineAnchor${status.index}"
                                                title="Edit Month"
                                                alt="Edit Month"
                                                value="Edit Month"/>
                                    </c:when>
                                    <c:otherwise>
                                        <html:submit
                                                styleClass="btn btn-default"
                                                property="methodToCall.performMonthlyRevenueBudget.line${status.index}.anchorrevenueexistingLineLineAnchor${status.index}"
                                                title="View Month"
                                                alt="View Month"
                                                value="View Month"/>
                                    </c:otherwise>
                                </c:choose>
                            </c:otherwise>
                        </c:choose>

                        <html:submit
                                property="methodToCall.performBalanceInquiryForRevenueLine.line${status.index}.anchorrevenueexistingLineLineAnchor${status.index}"
                                title="Balance Inquiry For Revenue Line ${status.index}"
                                alt="Balance Inquiry For Revenue Line ${status.index}"
                                styleClass="btn btn-default"
                                value="Bal Inquiry"/>

                        <c:if test="${lineIsEditable && (empty item.financialBeginningBalanceLineAmount || item.financialBeginningBalanceLineAmount == 0)}">
                            <html:submit
                                    property="methodToCall.deleteRevenueLine.line${status.index}.anchorrevenueexistingLineLineAnchor${status.index}"
                                    title="Delete Revenue Line ${status.index}"
                                    alt="Delete Revenue Line ${status.index}"
                                    styleClass="btn btn-red"
                                    value="Delete"/>
                        </c:if>
                    </td>
                </tr>

                <c:if test="${rowspan == 2}">
                    <tr>
                        <td class="datacell center nowrap" colspan="3">
                            <bc:requestAdjustment
                                    attributes="${pbglRevenueAttributes}"
                                    adjustmentAmountFieldName="${itemLineName}.adjustmentAmount"
                                    methodToCall="adjustRevenueLinePercent"
                                    lineIndex = "${status.index}"
                                    anchor="anchorrevenueeexistingLineLineAnchor${status.index}"/>
                        </td>
                    </tr>
                </c:if>
            </c:forEach>

            <tr>
                <kul:htmlAttributeHeaderCell literalLabel="Revenue Totals" colspan="3" horizontal="true" />
                <bc:columnTotalCell dataCellCssClass="infoline"
                    cellProperty="document.revenueFinancialBeginningBalanceLineAmountTotal"
                    textStyle="${textStyle}"
                    fieldAlign="right"
                    colSpan="1" disableHiddenField="true" />
                <bc:columnTotalCell dataCellCssClass="infoline"
                    cellProperty="document.revenueAccountLineAnnualBalanceAmountTotal"
                    textStyle="${textStyle}"
                    fieldAlign="right"
                    colSpan="1" disableHiddenField="true" />
                <bc:columnTotalCell dataCellCssClass="infoline"
                    cellProperty="document.revenuePercentChangeTotal"
                    textStyle="${textStyle}"
                    fieldAlign="right"
                    colSpan="1" disableHiddenField="true" />
                <td class="infoline">&nbsp;</td>
            </tr>

            <c:if test="${!readOnly}">
                <tr>
                    <td colspan="7" class="subhead">
                        <span class="subhead-left">Global Revenue Actions</span>
                    </td>
                </tr>
                <tr>
                    <c:if test="${KualiForm.budgetableDocument}">
                        <th colspan="3" nowrap>&nbsp;
                            <a name="anchorrevenueControlsAnchor"></a>
                        </th>
                        <td colspan="3" class="datacell center nowrap">
                            <bc:requestAdjustment
                                    attributes="${pbglRevenueAttributes}"
                                    adjustmentAmountFieldName="revenueAdjustmentAmount"
                                    methodToCall="adjustAllRevenueLinesPercent"
                                    anchor="anchorrevenueControlsAnchor"/>
                        </td>
                        <td colspan="1" class="datacell center nowrap">
                            <html:submit
                                    property="methodToCall.refresh.anchorrevenueControlsAnchor"
                                    title="Refresh"
                                    alt="Refresh"
                                    styleClass="btn btn-default"
                                    value="Refresh"/>
                            <html:submit
                                    property="methodToCall.performRevMonthSpread.anchorrevenueControlsAnchor"
                                    title="Monthly Spread"
                                    alt="Monthly Spread"
                                    styleClass="btn btn-default"
                                    value="Month Spread"/>
                            <html:submit
                                    property="methodToCall.performRevMonthDelete.anchorrevenueControlsAnchor"
                                    title="Monthly Delete"
                                    alt="Monthly Delete"
                                    styleClass="btn btn-red"
                                    value="Month Delete"/>
                        </td>
                    </c:if>
                    <c:if test="${!KualiForm.budgetableDocument}">
                        <td colspan="6" class="datacell">&nbsp;</td>
                        <td colspan="1" class="datacell center">
                            <html:submit
                                    property="methodToCall.performRevMonthDelete.anchorrevenueControlsAnchor"
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
