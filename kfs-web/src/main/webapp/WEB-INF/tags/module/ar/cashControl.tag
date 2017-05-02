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
	description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="readOnly" required="true"
	description="If document is in read only mode"%>
<%@ attribute name="showGenerateButton" required="true"
	description="If document generate button is in view mode"%>
<%@ attribute name="editPaymentMedium" required="true"
	description="If document edit medium is in edit mode"%>
<%@ attribute name="editRefDocNbr" required="true"
	description="If document reference document number is in edit mode"%>
<%@ attribute name="editBankCode" required="true"
	description="If document bank code is in edit mode"%>
<%@ attribute name="showBankCode" required="false"
	description="If document bank code is in edit mode"%>

<c:set var="arDocHeaderAttributes"
	value="${DataDictionary.AccountsReceivableDocumentHeader.attributes}" />

<kul:tab tabTitle="General Info" defaultOpen="true" tabErrorKey="${KFSConstants.CASH_CONTROL_DOCUMENT_ERRORS}">
	<div class="tab-container" align=center>
		<table cellpadding="0" cellspacing="0" summary="General Info">
			<tr>
				<kul:htmlAttributeHeaderCell
					attributeEntry="${arDocHeaderAttributes.processingChartOfAccCodeAndOrgCode}"
					horizontal="true" width="50%"
                    addClass="right" />

				<td class="datacell-nowrap">
					<kul:htmlControlAttribute
						attributeEntry="${arDocHeaderAttributes.processingChartOfAccCodeAndOrgCode}"
						property="processingChartOfAccCodeAndOrgCode"
                        readOnly="true" />
				</td>
			</tr>
			<c:if test="${showBankCode}" >
			<tr>
				<kul:htmlAttributeHeaderCell
					attributeEntry="${documentAttributes.bankCode}"
					horizontal="true"
                    forceRequired="true"
                    labelFor="document.bankCode"
                    addClass="right" />

				<c:choose>
					<c:when test="${editBankCode}">
					    <sys:bankControl
                                property="document.bankCode"
                                objectProperty="document.bank"
                                depositOnly="false"
                                disbursementOnly="false"
                                readOnly="${readOnly}" style="datacell-nowrap" />
					</c:when>
					<c:otherwise>
                        <td class="datacell-nowrap">
                            <c:out value="${KualiForm.document.bankCode}" />
                        </td>
					</c:otherwise>
				</c:choose>
			</tr>
			</c:if>

			<tr>
                <kul:htmlAttributeHeaderCell
                        attributeEntry="${documentAttributes.customerPaymentMediumCode}"
                        horizontal="true"
                        forceRequired="true"
                        labelFor="document.customerPaymentMediumCode"
                        addClass="right" />

				<td class="datacell-nowrap">
					<c:choose>
						<c:when test="${editPaymentMedium}">
                            <kul:htmlControlAttribute
                                    attributeEntry="${documentAttributes.customerPaymentMediumCode}"
                                    property="document.customerPaymentMediumCode"
                                    onchange="submitForm()"
                                    forceRequired="true" />
						</c:when>
						<c:otherwise>
							<c:out value="${KualiForm.document.customerPaymentMedium.customerPaymentMediumDescription}" />
						</c:otherwise>
					</c:choose>
				</td>
			</tr>

			<c:if test="${KualiForm.cashPaymentMediumSelected}">
				<tr>
                    <kul:htmlAttributeHeaderCell
                            attributeEntry="${documentAttributes.referenceFinancialDocumentNumber}"
                            horizontal="true"
                            addClass="right"/>

					<td class="datacell-nowrap">
                        <kul:htmlControlAttribute
                                attributeEntry="${documentAttributes.referenceFinancialDocumentNumber}"
                                property="document.referenceFinancialDocumentNumber"
                                readOnly="${not editRefDocNbr}"
                                forceRequired="true" />
					</td>
				</tr>
			</c:if>

			<c:if test="${showGenerateButton}">
				<tr>
                    <kul:htmlAttributeHeaderCell
                            literalLabel="Generate General Ledger Pending Entries:"
                            horizontal="true"
                            addClass="right"/>
					<td class="datacell-nowrap">
                        <html:submit property="methodToCall.generateGLPEs"
                                     alt="Generate General Ledger Pending Entries"
                                     title="Generate General Ledger Pending Entries"
                                     styleClass="btn btn-default"
                                     value="Generate"/>
					</td>
				</tr>
			</c:if>
		</table>
	</div>
</kul:tab>
