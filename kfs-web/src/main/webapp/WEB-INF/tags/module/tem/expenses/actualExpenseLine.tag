<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
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
<%@ attribute name="lineNumber"   required="false" description="Line number for the record." %>
<%@ attribute name="expense"      required="false" description="The expense to create the form for." %>
<%@ attribute name="detailObject" required="true"  description="The actual object" type="org.kuali.kfs.module.tem.businessobject.ActualExpense"%>
<%@ attribute name="highlight" required="false" description="Whether the row should be highlighted or not." %>

<c:set var="otherExpenseAttributes" value="${DataDictionary.ActualExpense.attributes}" />
<jsp:useBean id="paramMap" class="java.util.HashMap" />
<c:set target="${paramMap}" property="tripType" value="${KualiForm.document.tripTypeCode}" />
<c:set target="${paramMap}" property="travelerType" value="${KualiForm.document.traveler.travelerTypeCode}" />
<c:set target="${paramMap}" property="documentType" value="${KualiForm.docTypeName}" />

		<tr class="top ${highlight ? 'highlight' : ''}">
            <c:choose>
                <c:when test="${lineNumber != null}">
                    <th rowspan="3" style="vertical-align: middle;">${lineNumber}</th>
                </c:when>
                <c:otherwise>
                    <th rowspan="2"></th>
                </c:otherwise>
            </c:choose>
			<td class="infoline nowrap">
				<kul:htmlControlAttribute
					attributeEntry="${otherExpenseAttributes.expenseDate}"
					property="${expense}.expenseDate"
					readOnly="${lineNumber != null || !fullEntryMode}" />
			</td>
			<td class="infoline">
				<c:choose>
					<c:when test="${empty lineNumber }">
						<c:set target="${paramMap}" property="groupTravelCount" value="${fn:length(KualiForm.document.groupTravelers)}" />
						<html:select
                                property="${expense}.expenseTypeCode"
							    styleId="${expense}.expenseTypeCode"
							    onchange="checkDirectBilled('${expense}');loadExpenseTypeObjectCode(this, '${KualiForm.docTypeName}', '${KualiForm.document.traveler.travelerTypeCode}', '${KualiForm.document.tripTypeCode}');disableExpenseAmount(this)"
							    tabindex="${KualiForm.currentTabIndex}">

                            <c:forEach items="${temfunc:getOptionList('org.kuali.kfs.module.tem.businessobject.options.TravelExpenseTypeValuesFinder', paramMap)}" var="option">
								<c:set var="isSelected" value="${detailObject.expenseTypeCode == option.key}" />
								<option value="${option.key}" ${isSelected ? 'selected=true' : '' }>${option.value}</option>
							</c:forEach>
						</html:select>
						<c:forEach items="${ErrorPropertyList}" var="key">
							<c:if test="${key == 'newActualExpenseLine.expenseTypeCode'}">
								<kul:fieldShowErrorIcon />
							</c:if>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<c:out value="${detailObject.expenseType.name}" />
					</c:otherwise>
				</c:choose>	
			</td>
			<td class="infoline nowrap">
				<c:choose>
					<c:when test="${lineNumber == null }">
						<kul:htmlControlAttribute attributeEntry="${otherExpenseAttributes.travelCompanyCodeName}" property="${expense}.travelCompanyCodeName"/>
                		<kul:lookup boClassName="org.kuali.kfs.fp.businessobject.TravelCompanyCode" 
                			fieldConversions="name:${expense}.travelCompanyCodeName,code:${expense}.expenseTypeCode" 
                			fieldLabel="${otherExpenseAttributes.travelCompanyCodeName.label}" 
                			lookupParameters="${expense}.expenseTypeCode:code,${expense}.travelCompanyCodeName:name" 
                			readOnlyFields="expenseType.prepaidExpense"/>
					</c:when>
					<c:otherwise>
						<c:out value="${detailObject.travelCompanyCodeName}" />&nbsp;
					</c:otherwise>
				</c:choose>
			</td>
			<td class="infoline">
				<div id="div_${expense}.expenseAmount">
					<kul:htmlControlAttribute
						    attributeEntry="${otherExpenseAttributes.expenseAmount}"
						    property="${expense}.expenseAmount"
                            readOnly="${!fullEntryMode || detailObject.mileage}" />
				</div>
			</td>
			<td class="infoline">
				<div id="div_${expense}.currencyRate">
					<c:set var="currencyRateReadOnly" value="${!conversionRateEntryMode}" />
					<c:if test="${!conversionRateEntryMode}">
						<c:set var="currencyRateReadOnly" value="${lineNumber != null || !fullEntryMode || !empty detailObject.expenseDetails || detailObject.expenseTypeObjectCode.expenseType.expenseDetailRequired}" />
					</c:if>
					<kul:htmlControlAttribute
                            attributeEntry="${otherExpenseAttributes.currencyRate}"
                            property="${expense}.currencyRate"
                            readOnly="${currencyRateReadOnly}" />
	            </div>
            </td>
            <td class="infoline center">
                <kul:htmlControlAttribute
						attributeEntry="${otherExpenseAttributes.nonReimbursable}"
						property="${expense}.nonReimbursable"
						readOnly="${!fullEntryMode || !empty detailObject.expenseDetails || detailObject.expenseTypeObjectCode.expenseType.expenseDetailRequired}" />
            </td>
            <td class="infoline center">
                <kul:htmlControlAttribute
						attributeEntry="${otherExpenseAttributes.taxable}"
						property="${expense}.taxable"
						readOnly="${!expenseTaxableMode || !empty detailObject.expenseDetails || detailObject.expenseTypeObjectCode.expenseType.expenseDetailRequired }" />
            </td>
            <td class="infoline center">
                <c:if test="${! empty detailObject.expenseTypeObjectCode}" >
                    <kul:htmlControlAttribute
                            attributeEntry="${DataDictionary.ExpenseTypeObjectCode.attributes.receiptRequired}"
                            property="${expense}.expenseTypeObjectCode.receiptRequired"
                            readOnly="true" />
                </c:if>
            </td>
			
			<td class="infoline center">
                <c:if test="${detailObject.expenseTypeObjectCode.receiptRequired}">
                    <kul:htmlControlAttribute
							attributeEntry="${otherExpenseAttributes.missingReceipt}"
							property="${expense}.missingReceipt"
							readOnly="${!fullEntryMode}" />
                </c:if>
                <c:if test="${!detailObject.expenseTypeObjectCode.receiptRequired}">
						N/A
                </c:if>
            </td>
			<td class="infoline">
				<div id="div_${expense}.convertedAmount">
					<kul:htmlControlAttribute
                            attributeEntry="${otherExpenseAttributes.convertedAmount}"
                            property="${expense}.convertedAmount"
                            readOnly="true" />
				</div>
			</td>
			<td class="infoline" rowspan="2">
				<c:set var="notesTabIndex" value="${KualiForm.currentTabIndex}" />
				<c:set var="dummyIncrementVar" value="${kfunc:incrementTabIndex(KualiForm, tabKey)}" />
				<c:set var="tabindex" value="${KualiForm.currentTabIndex}"/>
				<c:set var="dummyIncrementVar" value="${kfunc:incrementTabIndex(KualiForm, tabKey)}" />
                <c:choose>
                    <c:when test="${fullEntryMode}">
                        <c:choose>
                            <c:when test="${lineNumber == null }">
                                <html:submit
                                        styleClass="btn btn-green"
                                        tabindex="${ tabindex}"
                                        property="methodToCall.addActualExpenseLine.line${lineNumber-1}"
                                        alt="Add Actual Expense Line"
                                        title="Add Actual Expense Line"
                                        value="Add"/>
                            </c:when>
                            <c:otherwise>
                                <html:submit
                                        styleClass="btn btn-red"
                                        property="methodToCall.deleteActualExpenseLine.line${lineNumber-1}"
                                        alt="Delete Actual Expense Line"
                                        title="Delete Actual Expense Line"
                                        value="Delete"/>
                            </c:otherwise>
                        </c:choose>
                    </c:when>
                    <c:otherwise>
                        &nbsp;
                    </c:otherwise>
                </c:choose>
			</td>
		</tr>
		<tr class="${highlight ? 'highlight' : ''}">
			<th class="right">
                <kul:htmlAttributeLabel attributeEntry="${otherExpenseAttributes.description}" />
			</th>
			<td class="infoline" colspan="3">
				<kul:htmlControlAttribute
                        attributeEntry="${otherExpenseAttributes.description}"
                        property="${expense}.description"
                        readOnly="${!fullEntryMode}"
                        tabindexOverride="${notesTabIndex}" />
			</td>
            <td colspan="6">
                <c:if test="${lineNumber == null}" >
                    <a href="${KualiForm.foreignCurrencyUrl}" target="currency_conversion_window" tabindex="${KualiForm.currentTabIndex}">Rate Conversion Site</a>
                </c:if>
            </td>
		</tr>
