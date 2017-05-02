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

<c:set var="arDocHeaderAttributes" value="${DataDictionary.AccountsReceivableDocumentHeader.attributes}" />

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
	description="The DataDictionary entry containing attributes for this row's fields."%>

<%@ attribute name="readOnly" required="true" description="If document is in read only mode"%>

<c:if test="${!empty KualiForm.document.invoiceGeneralDetail.proposalNumber}">
	<kul:tab tabTitle="Organization" defaultOpen="true" tabErrorKey="${KFSConstants.CUSTOMER_INVOICE_DOCUMENT_ORGANIZATION_ERRORS}">
		<div class="tab-container" align=center>
			<table cellpadding="0" cellspacing="0" class="datatable standard" summary="Invoice Section">
				<tr>
					<th class="right" style="width: 25%;">
                        <kul:htmlAttributeLabel attributeEntry="${arDocHeaderAttributes.processingChartOfAccountCode}" />
					</th>
					<td class="datacell" style="width: 25%;">
                        <kul:htmlControlAttribute
                                attributeEntry="${arDocHeaderAttributes.processingChartOfAccountCode}"
                                property="document.accountsReceivableDocumentHeader.processingChartOfAccountCode"
                                readOnly="true" />
                    </td>
					<th class="right" style="width: 25%;">
                        <kul:htmlAttributeLabel attributeEntry="${documentAttributes.billByChartOfAccountCode}" />
					</th>
					<td class="datacell" style="width: 25%;">
                        <kul:htmlControlAttribute
                                attributeEntry="${documentAttributes.billByChartOfAccountCode}"
                                property="document.billByChartOfAccountCode"
                                readOnly="true" />
                    </td>
				</tr>
				<tr>
					<th class="right">
                        <kul:htmlAttributeLabel attributeEntry="${arDocHeaderAttributes.processingOrganizationCode}" />
					</th>
					<td class="datacell">
                        <kul:htmlControlAttribute
                                attributeEntry="${arDocHeaderAttributes.processingOrganizationCode}"
							    property="document.accountsReceivableDocumentHeader.processingOrganizationCode"
                                readOnly="true" />
                    </td>
					<th class="right">
                        <kul:htmlAttributeLabel attributeEntry="${documentAttributes.billedByOrganizationCode}" />
					</th>
					<td class="datacell">
                        <kul:htmlControlAttribute
                                attributeEntry="${documentAttributes.billedByOrganizationCode}"
							    property="document.billedByOrganizationCode"
                                readOnly="true" />
                    </td>
				</tr>
			</table>
		</div>
	</kul:tab>
</c:if>
