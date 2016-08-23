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

<c:set var="documentAttributes" value="${DataDictionary.BudgetConstructionDocument.attributes}" />
<c:set var="accountAttributes" value="${DataDictionary.Account.attributes}" />
<c:set var="subFundGroupAttributes" value="${DataDictionary.SubFundGroup.attributes}" />
<c:set var="orgAttributes" value="${DataDictionary.Organization.attributes}" />
<c:set var="orgVals" value="${KualiForm.document.account.organization}" />
<c:set var="orgPropString" value="document.account.organization" />

<c:if test="${KualiForm.accountReportsExist}">
<c:set var="accountRptsAttributes" value="${DataDictionary.BudgetConstructionAccountReports.attributes}" />
<c:set var="orgRptsAttributes" value="${DataDictionary.BudgetConstructionOrganizationReports.attributes}" />
<c:set var="orgRptsVals" value="${KualiForm.document.budgetConstructionAccountReports.budgetConstructionOrganizationReports}" />
<c:set var="orgRptsPropString" value="document.budgetConstructionAccountReports.budgetConstructionOrganizationReports" />
</c:if>

<kul:tab tabTitle="System Information" defaultOpen="true" tabErrorKey="${BCConstants.BUDGET_CONSTRUCTION_SYSTEM_INFORMATION_TAB_ERRORS}">
    <div class="tab-container" align=center>
        <h3>System Information</h3>
        <table class="datatable standard side-margins" title="view system information" summary="view system information">
            <tr>
                <kul:htmlAttributeHeaderCell
                        labelFor="document.previousUniversityFiscalYear"
                        literalLabel="Fiscal Year:"
                        horizontal="true"/>
                <td>&nbsp;</td>
                <td class="center">
                    <kul:htmlControlAttribute
                            property="document.previousUniversityFiscalYear"
                            attributeEntry="${documentAttributes.universityFiscalYear}"
                            readOnly="${true}"
                            readOnlyBody="true">

                        ${KualiForm.document.previousUniversityFiscalYear}
                    </kul:htmlControlAttribute>
                </td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <kul:htmlAttributeHeaderCell
                        labelFor="document.accountNumber"
                        literalLabel="Chart/Account:"
                        horizontal="true"/>
                <td class="center">
                    <kul:htmlControlAttribute
                            property="document.chartOfAccountsCode"
                            attributeEntry="${documentAttributes.chartOfAccountsCode}"
                            readOnly="true"
                            readOnlyBody="true">

                        <kul:inquiry
                                boClassName="org.kuali.kfs.coa.businessobject.Chart"
                                keyValues="chartOfAccountsCode=${KualiForm.document.chartOfAccountsCode}"
                                render="true">

                            ${KualiForm.document.chartOfAccountsCode}
                        </kul:inquiry>
                    </kul:htmlControlAttribute>
                </td>
                <td class="center">
                    <kul:htmlControlAttribute
                            property="document.accountNumber"
                            attributeEntry="${documentAttributes.accountNumber}"
                            readOnly="true"
                            readOnlyBody="true">

                        <kul:inquiry
                                boClassName="org.kuali.kfs.coa.businessobject.Account"
                                keyValues="chartOfAccountsCode=${KualiForm.document.chartOfAccountsCode}&amp;accountNumber=${KualiForm.document.accountNumber}"
                                render="true">

                            ${KualiForm.document.accountNumber}
                        </kul:inquiry>
                    </kul:htmlControlAttribute>
                </td>
                <td class="center">
                    <kul:htmlControlAttribute
                            property="document.account.accountName"
                            attributeEntry="${accountAttributes.accountName}"
                            readOnly="${true}"
                            readOnlyBody="true">

                        ${KualiForm.document.account.accountName}
                    </kul:htmlControlAttribute>
                </td>
            </tr>
            <tr>
                <kul:htmlAttributeHeaderCell
                        labelFor="document.subAccountNumber"
                        literalLabel="Sub-Account:"
                        horizontal="true"/>
                <td>&nbsp;</td>
                <td class="center">
                    <kul:htmlControlAttribute
                            property="document.subAccountNumber"
                            attributeEntry="${documentAttributes.subAccountNumber}"
                            readOnly="true"
                            readOnlyBody="true">

                        <kul:inquiry
                                boClassName="org.kuali.kfs.coa.businessobject.SubAccount"
                                keyValues="chartOfAccountsCode=${KualiForm.document.chartOfAccountsCode}&amp;accountNumber=${KualiForm.document.accountNumber}&amp;subAccountNumber=${KualiForm.document.subAccountNumber}"
                                render="${KualiForm.document.subAccountNumber ne KualiForm.dashSubAccountNumber}">

                            ${KualiForm.document.subAccountNumber}
                        </kul:inquiry>
                    </kul:htmlControlAttribute>
                </td>

                <c:catch var="sa">
                    <c:set var="badSubAccount" value="${empty KualiForm.document.subAccount.subAccountName}" scope="page" />
                </c:catch>
                <c:if test="${sa!=null}">
                    <c:set var="badSubAccount" value="true" scope="page" />
                </c:if>

                <td class="center">
                    <kul:htmlControlAttribute
                            property="document.subAccount.subAccountName"
                            attributeEntry="${DataDictionary.SubAccount.attributes.subAccountName}"
                            readOnly="${true}"
                            readOnlyBody="true">

                        <c:if test="${KualiForm.document.subAccountNumber ne '-----'}">
                            <c:if test="${!badSubAccount}">
                                ${KualiForm.document.subAccount.subAccountName}
                            </c:if>
                            <c:if test="${badSubAccount}">
                                Not Found
                            </c:if>
                        </c:if>
                    </kul:htmlControlAttribute>
                </td>
            </tr>
            <tr>
                <kul:htmlAttributeHeaderCell
                        labelFor="document.account.subFundGroupCode"
                        literalLabel="Sub-Fund Group:"
                        horizontal="true"/>
                <td>&nbsp;</td>
                <td class="center">
                    <kul:htmlControlAttribute
                            property="document.account.subFundGroupCode"
                            attributeEntry="${accountAttributes.subFundGroupCode}"
                            readOnly="true"
                            readOnlyBody="true">

                        <kul:inquiry
                                boClassName="org.kuali.kfs.coa.businessobject.SubFundGroup"
                                keyValues="subFundGroupCode=${KualiForm.document.account.subFundGroupCode}"
                                render="true">

                            ${KualiForm.document.account.subFundGroupCode}
                        </kul:inquiry>
                    </kul:htmlControlAttribute>
                </td>
                <td class="center">
                    <kul:htmlControlAttribute
                            property="document.account.subFundGroup.subFundGroupDescription"
                            attributeEntry="${subFundGroupAttributes['subFundGroupDescription']}"
                            readOnly="${true}"
                            readOnlyBody="true">

                        ${KualiForm.document.account.subFundGroup.subFundGroupDescription}
                    </kul:htmlControlAttribute>
                </td>
            </tr>
            <tr>
                <kul:htmlAttributeHeaderCell
                        labelFor="document.account.organizationCode"
                        literalLabel="Org:"
                        horizontal="true"/>
                <td>&nbsp;</td>
                <td class="center">
                    <kul:htmlControlAttribute
                            property="document.account.organizationCode"
                            attributeEntry="${accountAttributes.organizationCode}"
                            readOnly="true"
                            readOnlyBody="true">

                        <kul:inquiry
                                boClassName="org.kuali.kfs.coa.businessobject.Organization"
                                keyValues="chartOfAccountsCode=${KualiForm.document.account.chartOfAccountsCode}&amp;organizationCode=${KualiForm.document.account.organizationCode}"
                                render="true">

                            ${KualiForm.document.account.organizationCode}
                        </kul:inquiry>
                    </kul:htmlControlAttribute>
                </td>
                <td class="center">
                    <kul:htmlControlAttribute
                            property="document.account.organization.organizationName"
                            attributeEntry="${orgAttributes['organizationName']}"
                            readOnly="${true}"
                            readOnlyBody="true">

                        ${KualiForm.document.account.organization.organizationName}
                    </kul:htmlControlAttribute>
                </td>
            </tr>
            <tr>
                <kul:htmlAttributeHeaderCell
                        labelFor="document.account.organization.reportsToOrganizationCode"
                        literalLabel="Reports-To Chart/Org:"
                        horizontal="true"/>
                <td class="center">
                    <kul:htmlControlAttribute
                            property="document.account.organization.reportsToChartOfAccountsCode"
                            attributeEntry="${orgAttributes.reportsToChartOfAccountsCode}"
                            readOnly="true"
                            readOnlyBody="true">

                        <kul:inquiry
                                boClassName="org.kuali.kfs.coa.businessobject.Chart"
                                keyValues="chartOfAccountsCode=${KualiForm.document.account.organization.reportsToChartOfAccountsCode}"
                                render="true">

                            ${KualiForm.document.account.organization.reportsToChartOfAccountsCode}
                        </kul:inquiry>
                    </kul:htmlControlAttribute>
                </td>
                <td class="center">
                    <kul:htmlControlAttribute
                            property="${orgPropString}.reportsToOrganizationCode"
                            attributeEntry="${orgAttributes.reportsToOrganizationCode}"
                            readOnly="true"
                            readOnlyBody="true">

                        <kul:inquiry
                                boClassName="org.kuali.kfs.coa.businessobject.Organization"
                                keyValues="chartOfAccountsCode=${orgVals.reportsToChartOfAccountsCode}&amp;organizationCode=${orgVals.reportsToOrganizationCode}"
                                render="true">

                            ${orgVals.reportsToOrganizationCode}
                        </kul:inquiry>
                    </kul:htmlControlAttribute>
                </td>
                <td class="center">
                    <kul:htmlControlAttribute
                            property="${orgPropString}.reportsToOrganization.organizationName"
                            attributeEntry="${orgAttributes['organizationName']}"
                            readOnly="${true}"
                            readOnlyBody="true">

                        ${orgVals.reportsToOrganization.organizationName}
                    </kul:htmlControlAttribute>
                </td>
            </tr>
        </table>

        <h3>Next Year Information</h3>
        <table class="datatable standard side-margins" title="next year information" summary="next year information">
            <tr>
                <kul:htmlAttributeHeaderCell
                        labelFor="document.universityFiscalYear"
                        literalLabel="Fiscal Year:"
                        horizontal="true"/>
                <td>&nbsp;</td>
                <td class="center">
                    <kul:htmlControlAttribute
                            property="document.universityFiscalYear"
                            attributeEntry="${documentAttributes.universityFiscalYear}"
                            readOnly="${true}"
                            readOnlyBody="true">

                        ${KualiForm.document.universityFiscalYear}
                    </kul:htmlControlAttribute>
                </td>
                <td>&nbsp;</td>
            </tr>
            <tr>
            <c:if test="${!KualiForm.accountReportsExist}">
                <tr>
                    <kul:htmlAttributeHeaderCell
                            labelFor="document.budgetConstructionAccountReports.reportsToChartOfAccountsCode"
                            literalLabel="Chart/Org:"
                            horizontal="true"/>
                    <td  colspan="3" class="center">No Account Reports To mapping found!</td>
                </tr>
            </c:if>
            <c:if test="${KualiForm.accountReportsExist}">
                <tr>
                    <kul:htmlAttributeHeaderCell
                            labelFor="document.budgetConstructionAccountReports.reportsToChartOfAccountsCode"
                            literalLabel="Chart/Org:"
                            horizontal="true"/>
                    <td class="center">
                        <kul:htmlControlAttribute
                                property="document.budgetConstructionAccountReports.reportsToChartOfAccountsCode"
                                attributeEntry="${accountRptsAttributes.reportsToChartOfAccountsCode}"
                                readOnly="true"
                                readOnlyBody="true">

                            <kul:inquiry
                                    boClassName="org.kuali.kfs.coa.businessobject.Chart"
                                    keyValues="chartOfAccountsCode=${KualiForm.document.budgetConstructionAccountReports.reportsToChartOfAccountsCode}"
                                    render="true">

                                ${KualiForm.document.budgetConstructionAccountReports.reportsToChartOfAccountsCode}
                            </kul:inquiry>
                        </kul:htmlControlAttribute>
                    </td>
                    <td class="center">
                        <kul:htmlControlAttribute
                                property="document.budgetConstructionAccountReports.reportsToOrganizationCode"
                                attributeEntry="${accountRptsAttributes.reportsToOrganizationCode}"
                                readOnly="true"
                                readOnlyBody="true">

                            <kul:inquiry
                                    boClassName="org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrganizationReports"
                                    keyValues="chartOfAccountsCode=${KualiForm.document.budgetConstructionAccountReports.reportsToChartOfAccountsCode}&amp;organizationCode=${KualiForm.document.budgetConstructionAccountReports.reportsToOrganizationCode}"
                                    render="true">

                                ${KualiForm.document.budgetConstructionAccountReports.reportsToOrganizationCode}
                            </kul:inquiry>
                        </kul:htmlControlAttribute>
                    </td>
                    <td class="center">
                        <kul:htmlControlAttribute
                                property="document.budgetConstructionAccountReports.budgetConstructionOrganizationReports.organization.organizationName"
                                attributeEntry="${orgAttributes.organizationName}"
                                readOnly="${true}"
                                readOnlyBody="true">

                            ${KualiForm.document.budgetConstructionAccountReports.budgetConstructionOrganizationReports.organization.organizationName}
                        </kul:htmlControlAttribute>
                    </td>
                </tr>
                <tr>
                    <kul:htmlAttributeHeaderCell
                            labelFor="document.budgetConstructionAccountReports.budgetConstructionOrganizationReports.reportsToOrganizationCode"
                            literalLabel="Reports-To Chart/Org:"
                            horizontal="true"/>
                    <td class="center">
                        <kul:htmlControlAttribute
                                property="document.budgetConstructionAccountReports.budgetConstructionOrganizationReports.reportsToChartOfAccountsCode"
                                attributeEntry="${orgRptsAttributes.reportsToChartOfAccountsCode}"
                                readOnly="true"
                                readOnlyBody="true">

                            <kul:inquiry
                                    boClassName="org.kuali.kfs.coa.businessobject.Chart"
                                    keyValues="chartOfAccountsCode=${KualiForm.document.budgetConstructionAccountReports.budgetConstructionOrganizationReports.reportsToChartOfAccountsCode}"
                                    render="true">

                                ${KualiForm.document.budgetConstructionAccountReports.budgetConstructionOrganizationReports.reportsToChartOfAccountsCode}
                            </kul:inquiry>
                        </kul:htmlControlAttribute>
                    </td>
                    <td class="center">
                        <kul:htmlControlAttribute
                                property="${orgRptsPropString}.reportsToOrganizationCode"
                                attributeEntry="${orgRptsAttributes.reportsToOrganizationCode}"
                                readOnly="true"
                                readOnlyBody="true">

                            <kul:inquiry
                                    boClassName="org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrganizationReports"
                                    keyValues="chartOfAccountsCode=${orgRptsVals.reportsToChartOfAccountsCode}&amp;organizationCode=${orgRptsVals.reportsToOrganizationCode}"
                                    render="true">

                                ${orgRptsVals.reportsToOrganizationCode}
                            </kul:inquiry>
                        </kul:htmlControlAttribute>
                    </td>
                    <td class="center">
                        <kul:htmlControlAttribute
                                property="${orgRptsPropString}.reportsToOrganization.organizationName"
                                attributeEntry="${orgAttributes['organizationName']}"
                                readOnly="${true}"
                                readOnlyBody="true">

                            ${orgRptsVals.reportsToOrganization.organizationName}
                        </kul:htmlControlAttribute>
                    </td>
                </tr>
            </c:if>
        </table>

        <h3>Approval Level Data</h3>
        <table class="datatable standard side-margins" title="Approval Level Data" summary="Approval Level Data">
            <tr>
                <kul:htmlAttributeHeaderCell
                        labelFor="document.organizationLevelCode"
                        literalLabel="Current Level:"
                        horizontal="true"/>
                <td class="center">
                    <kul:htmlControlAttribute
                            property="document.organizationLevelCode"
                            attributeEntry="${documentAttributes.organizationLevelCode}"
                            readOnly="${true}"
                            readOnlyBody="true">

                        ${KualiForm.document.organizationLevelCode}
                    </kul:htmlControlAttribute>
                </td>
                <td>&nbsp;</td>
                <td class="center">
                    <c:if test="${KualiForm.document.organizationLevelCode == 0}">
                        Account Level Update Access
                    </c:if>
                </td>
            </tr>
            <tr>
                <kul:htmlAttributeHeaderCell
                        labelFor="document.organizationLevelOrganizationCode"
                        literalLabel="Level Chart/Org:"
                        horizontal="true"/>
                <td class="center">
                    <kul:htmlControlAttribute
                            property="document.organizationLevelChartOfAccountsCode"
                            attributeEntry="${documentAttributes.organizationLevelChartOfAccountsCode}"
                            readOnly="true"
                            readOnlyBody="true">

                        <kul:inquiry
                                boClassName="org.kuali.kfs.coa.businessobject.Chart"
                                keyValues="chartOfAccountsCode=${KualiForm.document.organizationLevelChartOfAccountsCode}"
                                render="true">

                            ${KualiForm.document.organizationLevelChartOfAccountsCode}
                        </kul:inquiry>
                    </kul:htmlControlAttribute>
                </td>
                <td class="center">
                    <c:if test="${KualiForm.accountReportsExist}">
                        <kul:htmlControlAttribute
                                property="document.organizationLevelOrganizationCode"
                                attributeEntry="${documentAttributes.organizationLevelOrganizationCode}"
                                readOnly="true"
                                readOnlyBody="true">

                            <kul:inquiry
                                    boClassName="org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrganizationReports"
                                    keyValues="chartOfAccountsCode=${KualiForm.document.organizationLevelChartOfAccountsCode}&amp;organizationCode=${KualiForm.document.organizationLevelOrganizationCode}"
                                    render="true">

                                ${KualiForm.document.organizationLevelOrganizationCode}
                            </kul:inquiry>
                        </kul:htmlControlAttribute>
                    </c:if>
                </td>
                <td class="center">
                    <kul:htmlControlAttribute
                            property="document.organizationLevelOrganization.organizationName"
                            attributeEntry="${orgAttributes.organizationName}"
                            readOnly="${true}"
                            readOnlyBody="true">

                        ${KualiForm.document.organizationLevelOrganization.organizationName}
                    </kul:htmlControlAttribute>
                </td>
            </tr>
        </table>

        <h3>Controls</h3>
        <table class="datatable standard side-margins" title="controls" summary="controls">
            <tr>
                <td colspan="4" class="datacell center nowrap">
                    <c:if test="${!empty KualiForm.pullupLevelKeyLabels}">
                        <html:select property="pullupKeyCode">
                            <html:optionsCollection property="pullupLevelKeyLabels" label="label" value="key" />
                        </html:select>

                        <html:submit
                                property="methodToCall.performAccountPullup.anchorsystemControlsAnchor"
                                title="Account Pull Up"
                                alt="Account Pull Up"
                                styleClass="btn btn-default"
                                value="Pull Up"/>
                    </c:if>

                    <c:if test="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT] && !empty KualiForm.pushdownLevelKeyLabels}">
                        <html:select property="pushdownKeyCode">
                            <html:optionsCollection property="pushdownLevelKeyLabels" label="label" value="key" />
                        </html:select>

                        <html:submit
                                property="methodToCall.performAccountPushdown.anchorsystemControlsAnchor"
                                title="Account Push Down"
                                alt="Account Push Down"
                                styleClass="btn btn-default"
                                value="Push Down"/>
                    </c:if>

                    <html:submit
                            property="methodToCall.performReportDump.anchorsystemControlsAnchor"
                            title="Account Report/Dump"
                            alt="Account Report/Dump"
                            styleClass="btn btn-default"
                            value="Report/Dump"/>
                </td>
            </tr>
        </table>
    </div>
</kul:tab>
