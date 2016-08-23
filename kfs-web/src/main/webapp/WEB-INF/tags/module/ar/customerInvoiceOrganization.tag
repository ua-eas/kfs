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

<c:set var="arDocHeaderAttributes" value="${DataDictionary.AccountsReceivableDocumentHeader.attributes}" />
<c:set var="processingOrgMode" value="${KualiForm.editingMode['processingOrganizationMode']}" scope="request" />

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>

<%@ attribute name="readOnly" required="true" description="If document is in read only mode"%>

<kul:tab tabTitle="Organization" defaultOpen="true" tabErrorKey="${KFSConstants.CUSTOMER_INVOICE_DOCUMENT_ORGANIZATION_ERRORS}">
    <div class="tab-container">
        <table class="datatable standard" summary="Invoice Section">
			<tr>
                <th class="right" style="width: 25%;">
                    <kul:htmlAttributeLabel attributeEntry="${arDocHeaderAttributes.processingChartOfAccountCode}" />
                </th>
                <td class="datacell" style="width: 25%;">
                    <kul:htmlControlAttribute attributeEntry="${arDocHeaderAttributes.processingChartOfAccountCode}" property="document.accountsReceivableDocumentHeader.processingChartOfAccountCode" readOnly='${!processingOrgMode}' />
                </td>
				<th class="right" style="width: 25%;">
                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.billByChartOfAccountCode}" />
                </th>
                <td class="datacell" style="width: 25%;">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.billByChartOfAccountCode}" property="document.billByChartOfAccountCode" readOnly='${!processingOrgMode || readOnly}'/>
                </td>
            </tr>
            <tr>
				<th class="right">
                    <kul:htmlAttributeLabel attributeEntry="${arDocHeaderAttributes.processingOrganizationCode}" />
                </th>
                <td class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${arDocHeaderAttributes.processingOrganizationCode}" property="document.accountsReceivableDocumentHeader.processingOrganizationCode" readOnly='${!processingOrgMode}' />
                </td>
				<th class="right">
                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.billedByOrganizationCode}" />
                </th>
                <td class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.billedByOrganizationCode}" property="document.billedByOrganizationCode" readOnly='${!processingOrgMode || readOnly}'/>
					<c:if test="${not readOnly}">
	                    &nbsp;
	                    <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.Organization" fieldConversions="organizationCode:document.billedByOrganizationCode" lookupParameters="document.billedByOrganizationCode:organizationCode,document.billByChartOfAccountCode:chartOfAccountsCode"/>
                    </c:if>
                </td>
            </tr>
            <tr>
				<th class="right">
                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.organizationInvoiceNumber}" />
                </th>
                <td class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.organizationInvoiceNumber}" property="document.organizationInvoiceNumber" readOnly="${readOnly}"/>
                </td>
				<th class="right">
                    &nbsp;
                </th>
                <td class="datacell">
                    &nbsp;
                </td>
            </tr>
        </table>
    </div>
</kul:tab>
