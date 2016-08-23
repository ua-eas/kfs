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
<c:set var="budgetAttributes" value="${DataDictionary.BudgetConstructionRequestImport.attributes}" />

<kul:page
        showDocumentInfo="false"
        htmlFormAction="${KualiForm.htmlFormAction}"
        renderMultipart="true"
        docTitle="${KualiForm.title}"
        transactionalDocument="false"
        alternativeHelp="${ConfigProperties.externalizable.help.url}default.htm?turl=WordDocuments%2Frequestimporttool.htm">

    <script type="text/javascript">var excludeSubmitRestriction = true;</script>


    <kul:tab tabTitle="Select File to Import" defaultOpen="true" tabErrorKey="${EffortConstants.EFFORT_CERTIFICATION_TAB_ERRORS}">
    	<div class="tab-container">
		    <table class="standard">
                <html:hidden name="KualiForm" property="universityFiscalYear" />
                <html:hidden name="KualiForm" property="reportMode" />
                <html:hidden property="returnAnchor" />
                <html:hidden property="returnFormKey" />
                <html:hidden property="backLocation" />

                <c:if test="${KualiForm.reportMode != 'requestImport'}">
                    <html:hidden property="chartOfAccountsCode" />
                    <html:hidden property="accountNumber" />
                    <html:hidden property="subAccountNumber" />
                    <html:hidden property="backLocation" />
                    <html:hidden property="orgReport" />
                </c:if>

		    	<c:if test="${KualiForm.reportMode == 'requestImport'}">
					<tr>
			        	<th class="grid right">
                            <kul:htmlAttributeLabel attributeEntry="${budgetAttributes.fileName}" noColon="false" />
                        </th>
			            <td class="grid"><html:file property="file" /></td>
			       	</tr>
		        	<tr>
			        	<th class="grid right">
                            <kul:htmlAttributeLabel attributeEntry="${budgetAttributes.fileType}" noColon="false" />
                        </th>
			            <td class="grid">
                            <kul:htmlControlAttribute
                                    attributeEntry="${budgetAttributes.fileType}"
                                    property="fileType"
                                    readOnly="false" />
                        </td>
					</tr>
				</c:if>
				<tr>
		        	<th class="grid right" width="50%">
                        <kul:htmlAttributeLabel attributeEntry="${budgetAttributes.fieldDelimiter}" noColon="false" />
                    </th>
		            <td class="grid">
                        <kul:htmlControlAttribute
                                attributeEntry="${budgetAttributes.fieldDelimiter}"
                                property="fieldDelimiter"
                                readOnly="false" />
                        &nbsp;
                        <kul:htmlControlAttribute
                                attributeEntry="${budgetAttributes.otherFieldDelimiter}"
                                property="otherFieldDelimiter"
                                readOnly="false" />
                    </td>
				</tr>
				<tr>
		        	<th class="grid right">
                        <kul:htmlAttributeLabel attributeEntry="${budgetAttributes.textFieldDelimiter}" noColon="false" />
                    </th>
		            <td class="grid">
                        <kul:htmlControlAttribute
                                attributeEntry="${budgetAttributes.textFieldDelimiter}"
                                property="textFieldDelimiter"
                                readOnly="false" />
                        &nbsp;
                        <kul:htmlControlAttribute
                                attributeEntry="${budgetAttributes.otherTextFieldDelimiter}"
                                property="otherTextFieldDelimiter"
                                readOnly="false" />
                    </td>
				</tr>
				<tr>
					<td class="grid center infoline" colspan="2">
							<html:submit
                                    property="methodToCall.submit"
                                    title="Import File"
                                    alt="Import File"
                                    styleClass="btn btn-default"
                                    value="Submit"/>

							<html:submit
                                    property="methodToCall.close"
                                    title="Close Window"
                                    alt="Close Window"
                                    styleClass="btn btn-default"
                                    value="Close"/>
					</td>
				</tr>
			</table>
		</div>
	</kul:tab>
</kul:page>
