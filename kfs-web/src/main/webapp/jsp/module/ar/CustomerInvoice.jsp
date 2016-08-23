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
<%@ page import="org.kuali.kfs.sys.context.SpringContext" %>
<%@ page import="org.kuali.kfs.coa.service.AccountService" %>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<c:set var="accountsCanCrossCharts" value="<%=SpringContext.getBean(AccountService.class).accountsCanCrossCharts()%>" />
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<kul:documentPage showDocumentInfo="true"
	documentTypeName="CustomerInvoiceDocument"
	htmlFormAction="arCustomerInvoice" renderMultipart="true"
	showTabButtons="true">

    <sys:hiddenDocumentFields />

	<sys:documentOverview editingMode="${KualiForm.editingMode}" />

    <ar:customerInvoiceOrganization documentAttributes="${DataDictionary.CustomerInvoiceDocument.attributes}"  readOnly="${readOnly}"/>
    <ar:customerInvoiceRecurrenceDetails documentAttributes="${DataDictionary.CustomerInvoiceDocument.attributes}" readOnly="${readOnly}" />
    <ar:customerInvoiceGeneral documentAttributes="${DataDictionary.CustomerInvoiceDocument.attributes}" readOnly="${readOnly}" />
    <ar:customerInvoiceAddresses documentAttributes="${DataDictionary.CustomerInvoiceDocument.attributes}" readOnly="${readOnly}" />

	<kul:tab tabTitle="Accounting Lines" defaultOpen="true" tabErrorKey="${KFSConstants.SOURCE_ACCOUNTING_LINE_ERROR_PATTERN}"
             helpUrl="${KualiForm.accountingLineImportInstructionsUrl}" helpLabel="Import Templates">
		<sys-java:accountingLines>
			<sys-java:accountingLineGroup newLinePropertyName="newSourceLine" collectionPropertyName="document.sourceAccountingLines" collectionItemPropertyName="document.sourceAccountingLine" attributeGroupName="source" />
		</sys-java:accountingLines>
	</kul:tab>

	<gl:generalLedgerPendingEntries />
	<kul:notes />
	<kul:adHocRecipients />
	<kul:routeLog />
	<kul:superUserActions />

	<c:set var="extraButtons" value="${KualiForm.extraButtons}" scope="request"/>
	<sys:documentControls transactionalDocument="true" extraButtons="${extraButtons}"/>
</kul:documentPage>
