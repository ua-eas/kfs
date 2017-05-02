<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   -
   - Copyright 2005-2017 Kuali, Inc.
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

<%@ attribute name="documentAttributes" required="true"
	type="java.util.Map"
	description="The DataDictionary entry containing attributes for cash control document fields."%>
<%@ attribute name="cashControlDetailAttributes" required="true"
	type="java.util.Map"
	description="The DataDictionary entry containing attributes for cash control detail fields."%>
<%@ attribute name="readOnly" required="true"
	description="If document is in read only mode"%>
<%@ attribute name="editDetails" required="true"
	description="If document details are in edit mode"%>
<%@ attribute name="editPaymentAppDoc" required="true"
	description="If payment application document number should be a link"%>

<kul:tab tabTitle="Cash Control Details" defaultOpen="true" tabErrorKey="${KFSConstants.CASH_CONTROL_DETAILS_ERRORS}">
	<div id="cashControlDetails" class="tab-container">
		<table cellpadding="0" cellspacing="0" class="datatable standard" summary="Cash control Details">
			<tr class="header">
				<kul:htmlAttributeHeaderCell literalLabel="&nbsp;" />
				<kul:htmlAttributeHeaderCell attributeEntry="${cashControlDetailAttributes.documentNumber}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${cashControlDetailAttributes.status}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${cashControlDetailAttributes.customerNumber}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${cashControlDetailAttributes.customerPaymentMediumIdentifier}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${cashControlDetailAttributes.customerPaymentDate}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${cashControlDetailAttributes.financialDocumentLineAmount}" />
				<c:if test="${editDetails}">
					<kul:htmlAttributeHeaderCell literalLabel="Actions" />
				</c:if>
			</tr>
			<c:if test="${editDetails}">
				<ar:cashControlDetail propertyName="newCashControlDetail"
					cashControlDetailAttributes="${cashControlDetailAttributes}"
					addLine="true" readOnly="${readOnly}" rowHeading="&nbsp;"
					editPaymentAppDoc="${editPaymentAppDoc}"
					rowClass="highlight"
                    cssClass="infoline"
                    actionMethod="addCashControlDetail"
					actionAlt="Add Cash Control Detail"
					actionText="Add"
                    actionButtonClass="btn-green"/>
			</c:if>
			<logic:iterate id="cashControlDetail" name="KualiForm" property="document.cashControlDetails" indexId="ctr">
				<c:set var="rowClass" value=""/>
                <c:choose>
                    <c:when test="${editDetails}">
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

                <ar:cashControlDetail
					propertyName="document.cashControlDetail[${ctr}]"
					cashControlDetailAttributes="${cashControlDetailAttributes}"
					addLine="false" readOnly="${!editDetails}" rowHeading="${ctr+1}"
					editPaymentAppDoc="${editPaymentAppDoc}"
                    rowClass="${rowClass}"
					cssClass="datacell"
					actionMethod="deleteCashControlDetail.line${ctr}"
					actionAlt="Delete Cash Control Detail"
					actionText="Delete"
                    actionButtonClass="btn-red" />
			</logic:iterate>
			<tr>
				<td class="total-line" colspan="6">
					&nbsp;
				</td>
				<td class="total-line">
					<strong>Total:
						${KualiForm.document.currencyFormattedTotalCashControlAmount}</strong>
				</td>
				<c:if test="${!readOnly}">
					<td class="total-line">
						&nbsp;
					</td>
				</c:if>
			</tr>

		</table>
	</div>
</kul:tab>
