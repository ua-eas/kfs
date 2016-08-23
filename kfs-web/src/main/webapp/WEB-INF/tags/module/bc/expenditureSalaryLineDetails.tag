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

<%@ attribute name="readOnly" required="false" description="determine whether the contents can be read only or not"%>

<c:set var="bciiAttributes" value="${DataDictionary['BudgetConstructionIntendedIncumbent'].attributes}" />
<c:set var="bccsftAttributes" value="${DataDictionary['BudgetConstructionCalculatedSalaryFoundationTracker'].attributes}" />
<c:set var="pbcafAttributes" value="${DataDictionary['PendingBudgetConstructionAppointmentFunding'].attributes}" />
<c:set var="bcpAttributes" value="${DataDictionary['BudgetConstructionPosition'].attributes}" />
<c:set var="fundingPropertyName" value="salarySettingExpansion.pendingBudgetConstructionAppointmentFunding"/>
<c:set var="isHourlyPaid" value="${KualiForm.salarySettingExpansion.hourlyPaid}" />
<c:set var="numOfColumsRemoved" value="${isHourlyPaid ? 0 : 1 }" />

<h3>
	<span class="subhead-left">Salary Line Detail</span>
	<span class="subhead-right">
		<c:if test="${not readOnly}">

			<c:set var="hideOrShow" value="${KualiForm.hideAdjustmentMeasurement ? 'Show' : 'Hide'}" />
			<html:submit
					property="methodToCall.toggleAdjustmentMeasurement"
					alt="${hideOrShow} percent adjustment"
					title="${hideOrShow} percent adjustment"
					styleClass="btn btn-default small"
					value="${hideOrShow} % Adjust"/>
		</c:if>
	</span>
</h3>
<table class="standard side-margins" summary="Expenditure Salary Line Detail">
	<tr class="header">
		<kul:htmlAttributeHeaderCell attributeEntry="${pbcafAttributes.appointmentFundingDeleteIndicator}"/>

		<kul:htmlAttributeHeaderCell attributeEntry="${pbcafAttributes.positionNumber}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${pbcafAttributes.emplid}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${bciiAttributes.iuClassificationLevel}"/>

		<kul:htmlAttributeHeaderCell attributeEntry="${bcpAttributes.positionSalaryPlanDefault}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${bcpAttributes.positionGradeDefault}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${bcpAttributes.iuNormalWorkMonths}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${bcpAttributes.iuPayMonths}" />

		<kul:htmlAttributeHeaderCell attributeEntry="${bccsftAttributes.csfAmount}" addClass="right" />
		<kul:htmlAttributeHeaderCell attributeEntry="${bccsftAttributes.csfFullTimeEmploymentQuantity}" addClass="right" />

		<kul:htmlAttributeHeaderCell attributeEntry="${pbcafAttributes.appointmentFundingMonth}" />

		<c:if test="${isHourlyPaid}">
			<kul:htmlAttributeHeaderCell attributeEntry="${pbcafAttributes.appointmentRequestedPayRate}" addClass="right" />
		</c:if>

		<kul:htmlAttributeHeaderCell attributeEntry="${pbcafAttributes.appointmentRequestedAmount}" addClass="right" />
		<kul:htmlAttributeHeaderCell attributeEntry="${pbcafAttributes.appointmentRequestedFteQuantity}" addClass="right" />
		<kul:htmlAttributeHeaderCell attributeEntry="${pbcafAttributes.percentChange}" addClass="right" />

		<kul:htmlAttributeHeaderCell literalLabel="Actions"/>
	</tr>

	<c:if test="${empty KualiForm.salarySettingExpansion.pendingBudgetConstructionAppointmentFunding}">
		<tr>
			<td colspan="5"><em>No Salary Lines</em></td>
		</tr>
	</c:if>

	<c:forEach items="${KualiForm.salarySettingExpansion.pendingBudgetConstructionAppointmentFunding}" var="fundingLine" varStatus="status">
		<c:set var="fundingLineName" value="${fundingPropertyName}[${status.index}]"/>
		<c:set var="isVacant" value="${fundingLine.emplid eq BCConstants.VACANT_EMPLID}" />
		<c:set var="hidePercentAdjustment" value="${fundingLine.appointmentFundingDeleteIndicator || KualiForm.hideAdjustmentMeasurement || readOnly || empty fundingLine.bcnCalculatedSalaryFoundationTracker}" />
		<c:set var="notEditable" value="${readOnly || fundingLine.persistedDeleteIndicator}"/>
		<c:set var="canDelete" value="${not notEditable && not fundingLine.appointmentFundingDeleteIndicator }" />
		<c:set var="canUndelete" value="${not notEditable && not fundingLine.vacatable && fundingLine.appointmentFundingDeleteIndicator}" />
		<c:set var="rowspan" value="${ hidePercentAdjustment ? 1: 2}"/>

		<tr>
			<%-- Appointment Funding Delete Indicator --%>
			<bc:pbglLineDataCell dataCellCssClass="datacell"
				accountingLine="${fundingLineName}"	attributes="${pbcafAttributes}"
				field="appointmentFundingDeleteIndicator" rowSpan="${rowspan}" readOnly="false"
				fieldAlign="left" disabled="${true}">

			</bc:pbglLineDataCell>

			<%-- Position Number --%>
			<bc:pbglLineDataCell dataCellCssClass="datacell"
				accountingLine="${fundingLineName}" attributes="${pbcafAttributes}"
				field="positionNumber" inquiry="true"
				lookupOrInquiryKeys="positionNumber,universityFiscalYear"
				boClassSimpleName="BudgetConstructionPosition" boPackageName="org.kuali.kfs.module.bc.businessobject"
				accountingLineValuesMap="${fundingLine.valuesMap}" fieldAlign="left" readOnly="true" rowSpan="${rowspan}"
				anchor="salaryexistingLineLineAnchor${status.index}" />

			<c:choose>
				<c:when test="${not isVacant}">
					<bc:pbglLineDataCell dataCellCssClass="datacell"
						accountingLine="${fundingLineName}"
						field="emplid" detailFunction="loadEmplInfo"
						detailField="budgetConstructionIntendedIncumbent.name"
						attributes="${pbcafAttributes}" inquiry="true"
						boClassSimpleName="BudgetConstructionIntendedIncumbent"
						boPackageName="org.kuali.kfs.module.bc.businessobject"
						readOnly="true"	displayHidden="false"
						lookupOrInquiryKeys="emplid" rowSpan="${rowspan}"
						accountingLineValuesMap="${fundingLine.valuesMap}"/>

					<td class="datacell" valign="top" rowspan="${rowspan}">${fundingLine.budgetConstructionIntendedIncumbent.iuClassificationLevel}&nbsp;</td>
				</c:when>
				<c:otherwise>
					<bc:pbglLineDataCell dataCellCssClass="datacell"
						accountingLine="${fundingLineName}.budgetConstructionIntendedIncumbent" attributes="${bciiAttributes}"
						field="name"
						readOnly="true" formattedNumberValue="${BCConstants.VACANT_EMPLID}"
						displayHidden="false" rowSpan="${rowspan}" />

					<td rowspan="${rowspan}">&nbsp;</td>
				</c:otherwise>
			</c:choose>

			<%-- Salary Plan Default --%>
			<td class="datacell" valign="top" rowSpan="${rowspan}">${fundingLine.budgetConstructionPosition.positionSalaryPlanDefault}</td>

			<%-- Position Grade Default --%>
			<td class="datacell" valign="top" rowSpan="${rowspan}">${fundingLine.budgetConstructionPosition.positionGradeDefault}</td>

			<%-- IU Normal Work Months --%>
			<td class="datacell" valign="top" rowSpan="${rowspan}">${fundingLine.budgetConstructionPosition.iuNormalWorkMonths}</td>

			<%-- IU Pay Months --%>
			<td class="datacell" valign="top" rowSpan="${rowspan}">${fundingLine.budgetConstructionPosition.iuPayMonths}</td>

			<%-- csf Amount --%>
			<c:choose>
				<c:when test="${!empty fundingLine.bcnCalculatedSalaryFoundationTracker}">
					<fmt:formatNumber value="${fundingLine.bcnCalculatedSalaryFoundationTracker[0].csfAmount}"
						var="formattedCsfAmount" type="number" groupingUsed="true"/>
					<td class="datacell" valign="top" style="text-align: right;">
						<a href="${ConfigProperties.application.url}/budgetTempListLookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.bc.businessobject.CalculatedSalaryFoundationTracker&universityFiscalYear=${KualiForm.universityFiscalYear-1}&chartOfAccountsCode=${KualiForm.chartOfAccountsCode}&accountNumber=${KualiForm.accountNumber}&subAccountNumber=${KualiForm.subAccountNumber}&financialObjectCode=${KualiForm.financialObjectCode}&financialSubObjectCode=${KualiForm.financialSubObjectCode}&hideReturnLink=true&suppressActions=true&tempListLookupMode=6&showInitialResults=true&docFormKey=${KualiForm.returnFormKey}&backLocation=${KualiForm.backLocation}"  target="_blank">
							${formattedCsfAmount}
						</a>
					</td>

					<fmt:formatNumber value="${fundingLine.bcnCalculatedSalaryFoundationTracker[0].csfFullTimeEmploymentQuantity}"
						var="formattedCsfFTE" type="number" groupingUsed="true" minFractionDigits="5" />
					<td class="datacell" valign="top" style="text-align: right;">${formattedCsfFTE}</td>
				</c:when>
				<c:otherwise>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
				</c:otherwise>
			</c:choose>

			<%-- Appointment Funding Month --%>
			<bc:pbglLineDataCell dataCellCssClass="datacell"
				accountingLine="${fundingLineName}"
				attributes="${pbcafAttributes}" field="appointmentFundingMonth" fieldAlign="right" readOnly="true" />

			<!-- appointment requested hourly rate -->
			<c:if test="${isHourlyPaid}">
				<fmt:formatNumber value="${fundingLine.appointmentRequestedPayRate}"
					var="formattedNumber" type="number" groupingUsed="true" minFractionDigits="2" />

				<bc:pbglLineDataCell dataCellCssClass="datacell"
					accountingLine="${fundingLineName}"	attributes="${pbcafAttributes}"
					field="appointmentRequestedPayRate" fieldAlign="right"
					formattedNumberValue="${formattedNumber}" readOnly="${notEditable}" dataFieldCssClass="amount"/>
			</c:if>

			<%-- Appointment Requested Amount --%>
			<bc:pbglLineDataCell dataCellCssClass="datacell"
				accountingLine="${fundingLineName}" attributes="${pbcafAttributes}"
				field="appointmentRequestedAmount" fieldAlign="right" readOnly="${notEditable}"
				rowSpan="1" dataFieldCssClass="amount">
			</bc:pbglLineDataCell>

			<!-- appointment Requested Fte Quantity -->
			<fmt:formatNumber value="${fundingLine.appointmentRequestedFteQuantity}"
				var="formattedNumber" type="number" groupingUsed="true" minFractionDigits="5" />

			<bc:pbglLineDataCell dataCellCssClass="datacell"
				accountingLine="${fundingLineName}" attributes="${pbcafAttributes}"
				field="appointmentRequestedFteQuantity" fieldAlign="right"
				formattedNumberValue="${formattedNumber}" readOnly="true" dataFieldCssClass="amount" />

			<%-- Percent Change --%>
			<bc:pbglLineDataCell dataCellCssClass="datacell"
				accountingLine="${fundingLineName}"
				attributes="${pbcafAttributes}" field="percentChange" fieldAlign="right" readOnly="true" />

			<td class="datacell" rowspan="${rowspan}" style="white-space: nowrap;">
				<html:submit
						property="methodToCall.performPositionSalarySetting.line${status.index}.anchorsalaryexistingLineLineAnchor${status.index}"
						title="Position Salary Setting For Line ${status.index}"
						alt="Position Salary Setting For Line ${status.index}"
						styleClass="btn btn-default small"
						style="margin-bottom: 2px;"
						value="Posn Salset"/>

				<c:if test="${not isVacant}">
					<html:submit
							property="methodToCall.performIncumbentSalarySetting.line${status.index}.anchorsalaryexistingLineLineAnchor${status.index}"
							title="Incumbent Salary Setting For Line ${status.index}"
							alt="Incumbent Salary Setting For Line ${status.index}"
							styleClass="btn btn-default small"
							style="margin-bottom: 2px;"
							value="Incmbnt Salset"/>
				</c:if>

				<c:if test="${not readOnly}">
					<c:if test="${fundingLine.vacatable}">
						<br/>
						<html:submit
								property="methodToCall.vacateSalarySettingLine.line${status.index}.anchorsalaryexistingLineLineAnchor${status.index}"
								title="Vacate Salary Setting Line ${status.index}"
								alt="Vacate Salary Setting Line ${status.index}"
								styleClass="btn btn-default small"
								style="margin-bottom: 2px;"
								value="Vacate"/>
					</c:if>

					<c:if test="${canDelete}">
						<html:submit
								property="methodToCall.deleteSalarySettingLine.line${status.index}.anchorsalaryexistingLineLineAnchor${status.index}"
								title="Delete Salary Setting Line ${status.index}"
								alt="Delete Salary Setting Line ${status.index}"
								styleClass="btn btn-red small"
								style="margin-bottom: 2px;"
								value="Delete"/>
					</c:if>

					<c:if test="${canUndelete}">
						<br/>
						<html:submit
								property="methodToCall.revertSalarySettingLine.line${status.index}.anchorsalaryexistingLineLineAnchor${status.index}"
								title="revert Salary Setting Line ${status.index}"
								alt="revert Salary Setting Line ${status.index}"
								styleClass="btn btn-default small"
								style="margin-bottom: 2px;"
								value="Revert"/>
					</c:if>

					<c:if test="${isHourlyPaid && not notEditable}">
						<html:submit
								property="methodToCall.normalizePayRateAndAmount.line${status.index}.anchorsalaryexistingLineLineAnchor${status.index}"
								title="Normalize the hourly rate and annual amount for Salary Setting Line ${status.index}"
								alt="Normalize the hourly rate and annual amount for Salary Setting Line ${status.index}"
								styleClass="btn btn-default small"
								style="margin-bottom: 2px;"
								value="Normalize"/>
					</c:if>
				</c:if>
			</td>
		</tr>

		<c:if test="${not hidePercentAdjustment}">
		<tr>
			<td colspan ="${7 - numOfColumsRemoved}" class="infoline"><center>
				<bc:salaryAdjustment attributes="${pbcafAttributes}"
					adjustmentMeasurementFieldName="${fundingLineName}.adjustmentMeasurement"
					adjustmentAmountFieldName="${fundingLineName}.adjustmentAmount"
					methodToCall = "adjustSalarySettingLinePercent"
					lineIndex = "${status.index}" />
				</center>
			</td>
		</tr>
		</c:if>
	</c:forEach>

	<tr>
		<kul:htmlAttributeHeaderCell scope="row" colspan="8" literalLabel="Total:" horizontal="true" addClass="right" />

	    <bc:columnTotalCell dataCellCssClass="datacell" textStyle="${textStyle}" fieldAlign="right" colSpan="1"
	    	cellProperty="csfAmountTotal" disableHiddenField="true"/>

		<fmt:formatNumber var="formattedCsfFTETotal" value="${KualiForm.csfFullTimeEmploymentQuantityTotal}"
        	type="number" groupingUsed="true" minFractionDigits="5" />
		<bc:columnTotalCell dataCellCssClass="datacell" textStyle="${textStyle}" fieldAlign="right" colSpan="1"
			cellProperty="csfFullTimeEmploymentQuantityTotal" formattedNumberValue="${formattedCsfFTETotal}" disableHiddenField="true"/>

		<bc:columnTotalCell dataCellCssClass="datacell" textStyle="${textStyle}" fieldAlign="right" colSpan="${3 - numOfColumsRemoved}"
			cellProperty="appointmentRequestedAmountTotal" disableHiddenField="true"/>

		<fmt:formatNumber var="formattedRequestedFTETotal" value="${KualiForm.appointmentRequestedFteQuantityTotal}"
        	type="number" groupingUsed="true" minFractionDigits="5" />
		<bc:columnTotalCell dataCellCssClass="datacell" textStyle="${textStyle}" fieldAlign="right" colSpan="1"
			cellProperty="appointmentRequestedFteQuantityTotal" formattedNumberValue="${formattedRequestedFTETotal}" disableHiddenField="true"/>

		<bc:columnTotalCell dataCellCssClass="datacell" textStyle="${textStyle}" fieldAlign="right" colSpan="1"
			cellProperty="percentChangeTotal" disableHiddenField="true"/>

		<kul:htmlAttributeHeaderCell />
	</tr>

	<tr>
		<kul:htmlAttributeHeaderCell scope="row" colspan="8" literalLabel="Expenditure Line Base:" horizontal="true" addClass="right" />

	    <bc:columnTotalCell dataCellCssClass="datacell" textStyle="${textStyle}" fieldAlign="right" colSpan="1"
	    	cellProperty="salarySettingExpansion.financialBeginningBalanceLineAmount" />

		<kul:htmlAttributeHeaderCell nowrap="true" literalLabel="Req:" horizontal="true" addClass="right" />

		<bc:columnTotalCell dataCellCssClass="datacell" textStyle="${textStyle}" fieldAlign="right" colSpan="${3 - numOfColumsRemoved}"
			cellProperty="salarySettingExpansion.accountLineAnnualBalanceAmount" />

		<bc:columnTotalCell dataCellCssClass="datacell" textStyle="${textStyle}" fieldAlign="right" colSpan="2"
			cellProperty="salarySettingExpansion.percentChange" />

		<kul:htmlAttributeHeaderCell />
	</tr>
</table>

<c:if test="${not readOnly}">
	<table class="standard side-margins" summary="Expenditure Salary Global Actions">
		<tr>
			<td colspan="${16 - numOfColumsRemoved}" class="subhead">
				<h3>Global Actions</h3>
			</td>
		</tr>

		<tr>
			<kul:htmlAttributeHeaderCell scope="row" colspan="8" literalLabel="" horizontal="true" />

			<td colspan ="${5 - numOfColumsRemoved}"><center>
				<bc:salaryAdjustment attributes="${pbcafAttributes}"
					adjustmentMeasurementFieldName="adjustmentMeasurement"
					adjustmentAmountFieldName="adjustmentAmount"
					methodToCall="adjustAllSalarySettingLinesPercent"/>
				</center>
			</td>

			<td colspan="3" style="white-space: nowrap;">
				<c:if test="${KualiForm.payrollPositionFeedIndicator}">
					<html:checkbox property="refreshPositionBeforeSalarySetting" title="Refresh Position" alt="Refresh Position">
						&nbsp;Refresh Position?
					</html:checkbox>
				</c:if>

				<c:if test="${KualiForm.payrollIncumbentFeedIndictor}">
					&nbsp;&nbsp;&nbsp;
					<html:checkbox property="refreshIncumbentBeforeSalarySetting" title="Refresh Incumbent" alt="Refresh Incumbent">
						&nbsp;Refresh Incumbent?
					</html:checkbox>
				</c:if>
			</td>
		</tr>
	</table>
</c:if>


