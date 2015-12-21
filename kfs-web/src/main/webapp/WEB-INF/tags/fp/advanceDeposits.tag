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

<%@ attribute name="editingMode" required="true" description="used to decide if items may be edited" type="java.util.Map"%>
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT] || KualiForm.document.errorCorrected}" />

<kul:tab tabTitle="Advance Deposits" defaultOpen="true" tabErrorKey="${KFSConstants.ADVANCE_DEPOSITS_LINE_ERRORS}">
<c:set var="adAttributes" value="${DataDictionary.AdvanceDepositDetail.attributes}" />
 <div class="tab-container" align=center>
	<table cellpadding=0 class="datatable standard" summary="Advance Deposits">
		<tr class="header">
            <kul:htmlAttributeHeaderCell literalLabel="&nbsp;"/>
            <sys:bankLabel align="left" addClass="left" horizontal="${false}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${adAttributes.financialDocumentAdvanceDepositDate}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${adAttributes.financialDocumentAdvanceDepositReferenceNumber}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${adAttributes.financialDocumentAdvanceDepositDescription}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${adAttributes.financialDocumentAdvanceDepositAmount}"/>
            <c:if test="${not readOnly}">
                <kul:htmlAttributeHeaderCell literalLabel="Actions"/>
            </c:if>
		</tr>
        <c:if test="${not readOnly}">
            <tr class="highlight">
                <kul:htmlAttributeHeaderCell literalLabel="&nbsp;" scope="row"/>
                <sys:bankControl property="newAdvanceDeposit.financialDocumentBankCode" objectProperty="newAdvanceDeposit.bank" depositOnly="true" readOnly="${readOnly}" style="infoline left"/>
                <td class="infoline">    
                    <kul:dateInput attributeEntry="${adAttributes.financialDocumentAdvanceDepositDate}" property="newAdvanceDeposit.financialDocumentAdvanceDepositDate"/>
                </td>
                <td class="infoline">
                	<kul:htmlControlAttribute attributeEntry="${adAttributes.financialDocumentAdvanceDepositReferenceNumber}" property="newAdvanceDeposit.financialDocumentAdvanceDepositReferenceNumber" />
                </td>
                <td class="infoline">
                	<kul:htmlControlAttribute attributeEntry="${adAttributes.financialDocumentAdvanceDepositDescription}" property="newAdvanceDeposit.financialDocumentAdvanceDepositDescription" />
                </td>
                <td class="infoline">
                	<kul:htmlControlAttribute attributeEntry="${adAttributes.financialDocumentAdvanceDepositAmount}" property="newAdvanceDeposit.financialDocumentAdvanceDepositAmount" styleClass="amount"/>
                </td>
                <td class="infoline">
                    <html:submit
                            property="methodToCall.addAdvanceDeposit"
                            alt="Add an Advance Deposit"
                            title="Add an Advance Deposit"
                            styleClass="btn btn-green"
                            value="Add"/>
                </td>
            </tr>
        </c:if>
        <logic:iterate id="advanceDepositDetail" name="KualiForm" property="document.advanceDeposits" indexId="ctr">
            <c:set var="rowClass" value=""/>
            <c:choose>
                <c:when test="${not readOnly}">
                    <c:if test="${(ctr + 1) % 2 == 0}">
                        <c:set var="rowClass" value="highlight"/>
                    </c:if>
                </c:when>
                <c:otherwise>
                    <c:if test="${ctr % 2 == 0}">
                        <c:set var="rowClass" value="highlight"/>
                    </c:if>
                </c:otherwise>
            </c:choose>

            <tr class="${rowClass}">
                <kul:htmlAttributeHeaderCell literalLabel="${ctr+1}" scope="row"/>
                <sys:bankControl property="document.advanceDepositDetail[${ctr}].financialDocumentBankCode" objectProperty="document.advanceDepositDetail[${ctr}].bank" depositOnly="true" readOnly="${readOnly}"/>
                <td class="datacell">
                	<c:choose>
                        <c:when test="${readOnly}">
                            <kul:htmlControlAttribute attributeEntry="${adAttributes.financialDocumentAdvanceDepositDate}" property="document.advanceDepositDetail[${ctr}].financialDocumentAdvanceDepositDate" readOnly="true" />
                        </c:when>
                        <c:otherwise>
                            <kul:dateInput attributeEntry="${adAttributes.financialDocumentAdvanceDepositDate}" property="document.advanceDepositDetail[${ctr}].financialDocumentAdvanceDepositDate" />
                        </c:otherwise>
                    </c:choose>
                </td>
                <td class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${adAttributes.financialDocumentAdvanceDepositReferenceNumber}" property="document.advanceDepositDetail[${ctr}].financialDocumentAdvanceDepositReferenceNumber" readOnly="${readOnly}"/>
                </td>
                <td class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${adAttributes.financialDocumentAdvanceDepositDescription}" property="document.advanceDepositDetail[${ctr}].financialDocumentAdvanceDepositDescription" readOnly="${readOnly}"/>
                </td>
                <td class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${adAttributes.financialDocumentAdvanceDepositAmount}" property="document.advanceDepositDetail[${ctr}].financialDocumentAdvanceDepositAmount" readOnly="${readOnly}"/>
                </td>
                <c:if test="${not readOnly}">
                    <td class="datacell">
                        <html:submit
                                property="methodToCall.deleteAdvanceDeposit.line${ctr}"
                                alt="Delete an Advance Deposit"
                                title="Delete an Advance Deposit"
                                styleClass="btn btn-red"
                                value="Delete"/>
                    </td>
                </c:if>
            </tr>
        </logic:iterate>
		<tr>
			<c:set var="leadingColSpan" value="${KualiForm.editingMode[Constants.BANK_ENTRY_VIEWABLE_EDITING_MODE] ? 6 : 5}" />
	 		<td class="total-line" colspan="${leadingColSpan}">&nbsp;</td>
	  		<td class="total-line" ><strong>Total: ${KualiForm.document.currencyFormattedTotalAdvanceDepositAmount}</strong></td>
            <c:if test="${not readOnly}">
                <td class="total-line">&nbsp;</td>
            </c:if>
		</tr>
	</table>
  </div>
</kul:tab>
