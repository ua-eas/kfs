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

<%@ attribute name="editingMode" required="true" description="used to decide if items may be edited" type="java.util.Map"%>
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<kul:tab tabTitle="Final Billed Indicator Entries" defaultOpen="true" tabErrorKey="document.invoiceEntries">
	<c:set var="attributes" value="${DataDictionary.FinalBilledIndicatorEntry.attributes}" />
	<div class="tab-container" align=center>
		<table cellpadding=0 class="datatable items standard" summary="Final Billed Indicator Entries">
			<tr class="header">
				<kul:htmlAttributeHeaderCell literalLabel="&nbsp;" />
				<kul:htmlAttributeHeaderCell attributeEntry="${attributes.invoiceDocumentNumber}" />
				<c:if test="${not readOnly}">
					<kul:htmlAttributeHeaderCell literalLabel="Actions" />
				</c:if>
			</tr>
			<c:if test="${not readOnly}">
				<tr class="highlight">
					<kul:htmlAttributeHeaderCell literalLabel="&nbsp;" scope="row" />
					<td class="infoline">
                        <kul:htmlControlAttribute attributeEntry="${attributes.invoiceDocumentNumber}" property="invoiceEntry.invoiceDocumentNumber" readOnly="${readOnly}" />
                    </td>
					<td class="infoline">
                        <html:submit property="methodToCall.addInvoiceEntry" alt="Add an Invoice Entry" title="Add an Invoice Entry" styleClass="btn btn-green" value="Add" />
					</td>
				</tr>
			</c:if>

			<logic:iterate id="FinalBilledIndicatorEntry" name="KualiForm" property="document.invoiceEntries" indexId="ctr">
                <c:choose>
                    <c:when test="${not readOnly}">
                        <tr class="${(ctr + 1) % 2 == 0 ? "highlight" : ""}">
                    </c:when>
                    <c:otherwise>
                        <tr class="${ctr % 2 == 0 ? "highlight" : ""}">
                    </c:otherwise>
                </c:choose>

					<kul:htmlAttributeHeaderCell literalLabel="${ctr+1}" scope="row" />

					<td class="datacell">
                        <kul:htmlControlAttribute
                                attributeEntry="${attributes.invoiceDocumentNumber}"
                                property="document.invoiceEntries[${ctr}].invoiceDocumentNumber"
                                readOnly="true"
                                />
                    </td>
					<c:if test="${not readOnly}">
						<td class="datacell">
                            <html:submit property="methodToCall.deleteInvoiceEntry.line${ctr}" alt="Delete an Invoice Entry" title="Delete an Invoice Entry" styleClass="btn btn-red" value="Delete" />
						</td>
					</c:if>
				</tr>
			</logic:iterate>
		</table>
	</div>
</kul:tab>
