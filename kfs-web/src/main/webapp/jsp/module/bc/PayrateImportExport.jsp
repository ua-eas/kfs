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

<c:set var="payrateImportExportAttributes"	value="${DataDictionary.PayrateImportExport.attributes}" />

<kul:page
        showDocumentInfo="false"
	    htmlFormAction="budgetPayrateImportExport"
        renderMultipart="true"
	    docTitle="Payrate Import/Export"
        transactionalDocument="false"
        showTabButtons="true">

	<script type="text/javascript">var excludeSubmitRestriction = true;</script>

    <html:hidden property="returnAnchor" />
    <html:hidden property="returnFormKey" />
    <html:hidden property="backLocation" />
    <html:hidden name="KualiForm" property="universityFiscalYear" />

    <kul:tabTop tabTitle="Payrate Import/Export" defaultOpen="true">
		<div class="tab-container" align=center>
            <h3>Payrate Export</h3>
            <table class="standard side-margins">
				<tr>
					<td class="infoline">
						<kul:htmlAttributeLabel attributeEntry="${payrateImportExportAttributes.positionUnionCode}" />
                        <kul:htmlControlAttribute
                                property="positionUnionCode"
                                readOnly="false"
                                attributeEntry="${payrateImportExportAttributes.positionUnionCode}"/>
						<kul:lookup
                                boClassName="org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition"
								fieldConversions="positionUnionCode:positionUnionCode"
								lookupParameters="positionUnionCode:positionUnionCode" />
					</td>
					<td class="infoline"  >
						<kul:htmlAttributeLabel attributeEntry="${payrateImportExportAttributes.csfFreezeDate}" />
						<kul:htmlControlAttribute property="csfFreezeDate" readOnly="false" attributeEntry="${payrateImportExportAttributes.csfFreezeDate}" datePicker="true" />
					</td>
					<td class="infoline right">
                        <html:submit
                                property="methodToCall.performExport"
                                title="Export"
                                alt="Export"
                                styleClass="btn btn-default"
                                value="Export"/>
					</td>
				</tr>
			</table>

            <h3>Payrate Import</h3>
            <table class="standard side-margins">
				<tr>
					<td class="infoline">
						<kul:htmlAttributeLabel attributeEntry="${payrateImportExportAttributes.fileName}" />
						<html:file property="file" style="display:inline;" />
					</td>
					<td class="infoline right" colspan="2">
                        <html:submit
                                property="methodToCall.performImport"
                                title="Import"
                                alt="Import"
                                styleClass="btn btn-default"
                                value="Import"/>
					</td>
				</tr>
			</table>

            <div class="center" style="margin:20px 0;">
                <html:submit
                        property="methodToCall.close"
                        title="Import"
                        alt="Import"
                        styleClass="btn btn-default"
                        value="Close"/>
            </div>
		</div>
	</kul:tabTop>
</kul:page>
