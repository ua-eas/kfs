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
<%@ taglib uri="/WEB-INF/tlds/temfunc.tld" prefix="temfunc"%>
<%@ attribute name="detail"           required="false" description="The expense detail to create the form for."%>
<%@ attribute name="lineNumber"       required="true"  description="Line number for the record."%>
<%@ attribute name="detailLineNumber" required="false" description="Detail line number"%>
<%@ attribute name="detailObject"     required="true"  description="The actual object" type="org.kuali.kfs.module.tem.businessobject.ActualExpense"%>
<%@ attribute name="parentObject"     required="true"  description="The parent object" type="org.kuali.kfs.module.tem.businessobject.ActualExpense"%>
<%@ attribute name="highlight" required="false" description="Whether the row should be highlighted or not." %>

<c:set var="otherExpenseAttributes" value="${DataDictionary.ActualExpense.attributes}" />
<jsp:useBean id="paramMap" class="java.util.HashMap" />
<c:set target="${paramMap}" property="tripType" value="${KualiForm.document.tripTypeCode}" />
<c:set target="${paramMap}" property="travelerType" value="${KualiForm.document.traveler.travelerTypeCode}" />
<c:set target="${paramMap}" property="documentType" value="${KualiForm.docTypeName}" />
<c:set var="calcColspan" value="6" />


<tr class="top ${highlight ? 'highlight' : ''}">
	<c:choose>
		<c:when test="${detailLineNumber == null }">
			<th scope="row" class="infoline" rowspan="2"></th>
		</c:when>
		<c:otherwise>
			<th scope="row" class="infoline center" style="vertical-align: middle;" rowspan="2">${detailLineNumber+1}</th>
		</c:otherwise>
	</c:choose>
	<td valign="top" class="infoline"><kul:htmlControlAttribute
			attributeEntry="${otherExpenseAttributes.expenseDate}"
			property="${detail}.expenseDate" readOnly="${!fullEntryMode}" />
	</td>
	<td class="infoline">
		<c:out value="${detailObject.expenseType.name}" />
		<c:set var="strKey" value="${detail}.expenseTypeCode" />
		<c:forEach items="${ErrorPropertyList}" var="key">
			<c:if test="${key == strKey}">
				<kul:fieldShowErrorIcon />
			</c:if>
		</c:forEach>
	</td>
	<c:if test="${parentObject.mileageIndicator}">
		<c:set var="calcColspan" value="${calcColspan+2 }" />
		<td class="infoline">
            <kul:htmlControlAttribute
                    attributeEntry="${otherExpenseAttributes.miles}"
				    property="${detail}.miles"
                    readOnly="${!fullEntryMode}"
                    onchange="updateMileage(this.id)" />
        </td>
		<td class="infoline">
			<c:out value="${detailObject.contextlessMileageRate.rate}" />
		</td>
	</c:if>
	<td class="infoline">
		<div id="div_${detail}.expenseAmount">
			<kul:htmlControlAttribute
                    attributeEntry="${otherExpenseAttributes.expenseAmount}"
                    property="${detail}.expenseAmount"
                    readOnly="${!fullEntryMode || parentObject.mileage}" />
		</div>
    </td>
	<td class="infoline">
		<div id="div_${detail}.convertedAmount">
			<kul:htmlControlAttribute
                    attributeEntry="${otherExpenseAttributes.convertedAmount}"
                    property="${detail}.convertedAmount"
                    readOnly="true" />
		</div>
	</td>
	<td class="infoline center">
        <kul:htmlControlAttribute
				attributeEntry="${otherExpenseAttributes.nonReimbursable}"
				property="${detail}.nonReimbursable" readOnly="${!fullEntryMode || parentObject.nonReimbursable}" />
	</td>
	<td class="infoline center">
        <kul:htmlControlAttribute
				attributeEntry="${otherExpenseAttributes.taxable}"
				property="${detail}.taxable"
				readOnly="${!expenseTaxableMode}" />
    </td>
	<c:if test="${!empty detailObject.expenseTypeObjectCode && detailObject.expenseTypeObjectCode.receiptRequired}">
		<td class="infoline center">
            <kul:htmlControlAttribute
					attributeEntry="${DataDictionary.ExpenseTypeObjectCode.attributes.receiptRequired}"
					property="${detail}.expenseTypeObjectCode.receiptRequired" readOnly="true" />
		</td>
	</c:if>
	<td class="infoline center">
        <c:if test="${!empty detailObject.expenseTypeObjectCode && detailObject.expenseTypeObjectCode.receiptRequired}">
            <kul:htmlControlAttribute
                    attributeEntry="${otherExpenseAttributes.missingReceipt}"
                    property="${detail}.missingReceipt"
                    readOnly="${!fullEntryMode}" />
        </c:if>
        <c:if test="${empty detailObject.expenseTypeObjectCode || !detailObject.expenseTypeObjectCode.receiptRequired}">
            N/A
        </c:if>
		</div>
	</td>
	<c:if test="${parentObject.airfareIndicator}">
		<c:set var="calcColspan" value="${calcColspan+2 }" />
		<td class="infoline">
			<kul:htmlControlAttribute
                    attributeEntry="${otherExpenseAttributes.airfareSourceCode}"
                    property="${detail}.airfareSourceCode"
                    readOnly="${!fullEntryMode}" />
		</td>
		<td class="infoline">
			<c:choose>
				<c:when test="${fullEntryMode}">
					<c:set target="${paramMap}" property="expenseTypeMetaCategoryCode" value="${parentObject.expenseType.expenseTypeMetaCategoryCode}" />
					<html:select property="${detail}.classOfServiceCode" disabled="${!fullEntryMode}" tabindex="${KualiForm.currentTabIndex}">
						<c:forEach items="${temfunc:getOptionList('org.kuali.kfs.module.tem.businessobject.options.ClassOfServiceValuesFinder', paramMap)}" var="option">
							<c:set var="isSelected" value="${detailObject.classOfServiceCode == option.key}" />
							<option value="${option.key}" ${isSelected?'selected=true':'' }>${option.value}</option>
						</c:forEach>
					</html:select>
				</c:when>
				<c:otherwise>
					<c:out value="${detailObject.classOfService.classOfServiceName}" />
				</c:otherwise>
			</c:choose>
		</td>
	</c:if>
	<c:if test="${parentObject.rentalCarIndicator}">
	    <c:set var="calcColspan" value="${calcColspan+2 }" />
		<td class="infoline">
			<c:choose>
				<c:when test="${fullEntryMode}">
					<c:set target="${paramMap}" property="expenseTypeMetaCategoryCode" value="${parentObject.expenseType.expenseTypeMetaCategoryCode}" />
					<html:select property="${detail}.classOfServiceCode" disabled="${!fullEntryMode}" tabindex="${KualiForm.currentTabIndex}">
						<c:forEach items="${temfunc:getOptionList('org.kuali.kfs.module.tem.businessobject.options.ClassOfServiceValuesFinder', paramMap)}" var="option">
							<c:set var="isSelected" value="${detailObject.classOfServiceCode == option.key}" />
							<option value="${option.key}" ${isSelected?'selected=true':'' }>${option.value}</option>
						</c:forEach>
					</html:select>
				</c:when>
				<c:otherwise>
					<c:out value="${detailObject.classOfService.classOfServiceName}" />
				</c:otherwise>
			</c:choose>
		</td>
		<td class="infoline">
            <kul:htmlControlAttribute
					attributeEntry="${otherExpenseAttributes.rentalCarInsurance}"
					property="${detail}.rentalCarInsurance"
                    readOnly="${!fullEntryMode}" />
		</td>
	</c:if>
	<c:set var="notesTabIndex" value="${KualiForm.currentTabIndex}" />
	<c:set var="dummyIncrementVar" value="${kfunc:incrementTabIndex(KualiForm, tabKey)}" />
	<td class="infoline" rowspan="2">
		<c:choose>
			<c:when test="${detailLineNumber != null}">
				<c:if test="${fullEntryMode}">
                    <html:submit
							styleClass="btn btn-red"
							property="methodToCall.deleteActualExpenseDetailLine.line${lineNumber}-${detailLineNumber}"
							alt="Delete Actual Expense Line"
							title="Delete Actual Expense Detail Line"
                            value="Delete"/>
				</c:if>
				<c:if test="${!fullEntryMode}">&nbsp;</c:if>
			</c:when>
			<c:otherwise>
				<c:set var="tabindex" value="${KualiForm.currentTabIndex}" />
				<c:set var="dummyIncrementVar" value="${kfunc:incrementTabIndex(KualiForm, tabKey)}" />
                <html:submit
						styleClass="btn btn-green"
                        tabindex="${tabindex}"
						property="methodToCall.addActualExpenseDetailLine.line${lineNumber}"
						alt="Add Actual Expense Detail Line"
						title="Add Actual Expense Detail Line"
                        value="Add"/>
			</c:otherwise>
		</c:choose>
	</td>
</tr>
<tr class="${highlight ? 'highlight' : ''}">
	<th>
		<div class="right">
			<kul:htmlAttributeLabel attributeEntry="${otherExpenseAttributes.description}" noColon="false" />
		</div>
	</th>
	<td class="infoline" colspan="${calcColspan}">
		<kul:htmlControlAttribute attributeEntry="${otherExpenseAttributes.description}" property="${detail}.description" readOnly="${!fullEntryMode }" tabindexOverride="${notesTabIndex}" />
	</td>
</tr>
