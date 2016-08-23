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

<kul:tab tabTitle="Reports And Exports" defaultOpen="true" tabErrorKey="reportSel">
	<div class="tab-container">
        <table class="standard">
            <tr>
                <td width="200" class="right">
                    <html:submit
                            property="methodToCall.performReport.(((AccountFundingDetailReport)))"
                            title="Account Funding Detail"
                            alt="Account Funding Detail"
                            styleClass="btn btn-default"
                            value="View"/>
                </td>
                <td>Account Funding Detail</td>
            </tr>
            <tr>
                <td class="right">
                    <html:submit
                            property="methodToCall.performReport.(((AccountObjectDetailReport)))"
                            title="Account Object Detail"
                            alt="Account Object Detail"
                            styleClass="btn btn-default"
                            value="View"/>
                </td>
                <td>
                    Account Object Detail &nbsp;&nbsp;&nbsp;
                    <html:checkbox property="accountObjectDetailConsolidation" title="accSumConsolidation">&nbsp;(consolidated)</html:checkbox>
                </td>
            </tr>
            <tr>
                <td class="right">
                    <html:submit
                            property="methodToCall.performReport.(((AccountSummaryReport)))"
                            title="Account Sum"
                            alt="Account Sum"
                            styleClass="btn btn-default"
                            value="View"/>
                </td>
                <td>
                    Account Summary &nbsp;&nbsp;&nbsp;
                    <html:checkbox property="accountSummaryConsolidation" title="accSumConsolidation">&nbsp;(consolidated)</html:checkbox>
                </td>
            </tr>
            <tr>
                <td class="right">
                    <html:submit
                            property="methodToCall.performReport.(((LevelSummaryReport)))"
                            title="Level Sum"
                            alt="Level Sum"
                            styleClass="btn btn-default"
                            value="View"/>
                </td>
                <td>Level Summary </td>
            </tr>
            <tr>
                <td class="right">
                    <html:submit
                            property="methodToCall.performReport.(((TwoPLGListReport)))"
                            title="List 2PLG"
                            alt="List 2PLG"
                            styleClass="btn btn-default"
                            value="View"/>
                </td>
                <td>List 2PLG </td>
            </tr>
            <tr>
                <td class="right">
                    <html:submit
                            property="methodToCall.performReport.(((MonthSummaryReport)))"
                            title="Monthy Object Sum"
                            alt="Monthly Object Sum"
                            styleClass="btn btn-default"
                            value="View"/>
                </td>
                <td>
                    Monthly Object Summary&nbsp;&nbsp;&nbsp;
                    <html:checkbox property="monthObjectSummaryConsolidation" title="accSumConsolidation">&nbsp;(consolidated)</html:checkbox>
                </td>
            </tr>
            <tr>
                <td class="right">
                    <html:submit
                            property="methodToCall.performReport.(((ObjectSummaryReport)))"
                            title="Object Sum"
                            alt="Object Sum"
                            styleClass="btn btn-default"
                            value="View"/>
                </td>
                <td>Object Summary </td>
            </tr>
            <tr>
                <td class="right">
                    <html:submit
                            property="methodToCall.performReport.(((SynchronizationProblemsReport)))"
                            title="Payroll Synchronization Problems"
                            alt="Payroll Synchronization Problems"
                            styleClass="btn btn-default"
                            onblur="formHasAlreadyBeenSubmitted = false"
                            value="View"/>
                </td>
                <td>Payroll Synchronization Problems</td>
            </tr>
            <tr>
                <td class="right">
                    <html:submit
                            property="methodToCall.performReport.(((PositionFundingDetailReport)))"
                            title="Position Funding"
                            alt="Position Funding"
                            styleClass="btn btn-default"
                            value="View"/>
                </td>
                <td>Position Funding </td>
            </tr>
            <tr>
                <td class="right">
                    <html:submit
                            property="methodToCall.performReport.(((ReasonStatisticsReport)))"
                            title="Reason Statistics"
                            alt="Reason Statistics"
                            styleClass="btn btn-default"
                            value="View"/>
                </td>
                <td>Reason Statistics</td>
            </tr>
            <tr>
                <td class="right">
                    <html:submit
                            property="methodToCall.performReport.(((ReasonSummaryReport)))"
                            title="Reason Summary"
                            alt="Reason Summary"
                            styleClass="btn btn-default"
                            value="View"/>
                </td>
                <td>Reason Summary</td>
            </tr>
            <tr>
                <td class="right">
                    <html:submit
                            property="methodToCall.performReport.(((SalaryStatisticsReport)))"
                            title="Salary Statistics"
                            alt="Salary Statistics"
                            styleClass="btn btn-default"
                            value="View"/>
                </td>
                <td>Salary Statistics</td>
            </tr>
            <tr>
                <td class="right">
                    <html:submit
                            property="methodToCall.performReport.(((SalarySummaryReport)))"
                            title="Salary Summary"
                            alt="Salary Summary"
                            styleClass="btn btn-default"
                            value="View"/>
                </td>
                <td>Salary Summary </td>
            </tr>
            <tr>
                <td class="right">
                    <html:submit
                            property="methodToCall.performReport.(((SubFundSummaryReport)))"
                            title="SubFund Sum"
                            alt="SubFund Sum"
                            styleClass="btn btn-default"
                            value="View"/>
                </td>
                <td>Sub-Fund Summary</td>
            </tr>
        </table>

        <h3>Export</h3>
        <table class="datatable standard">
            <tr>
                <td width="200" class="right">
                    <html:submit
                            property="methodToCall.performReport.(((AccountExport)))"
                            title="Account Export"
                            alt="Account Export"
                            styleClass="btn btn-default"
                            value="View"/>
                </td>
                <td>Budgeted Revenue/Expenditure Export</td>
            </tr>
            <tr>
                <td class="right">
                    <html:submit
                            property="methodToCall.performReport.(((FundingExport)))"
                            title="Funding Export"
                            alt="Funding Export"
                            styleClass="btn btn-default"
                            value="View"/>
                </td>
                <td>Budgeted Salary Lines Export</td>
            </tr>
            <tr>
                <td class="right">
                    <html:submit
                            property="methodToCall.performReport.(((MonthlyExport)))"
                            title="Monthly Export"
                            alt="Monthly Export"
                            styleClass="btn btn-default"
                            value="View"/>
                </td>
                <td>Monthly Budget Export</td>
            </tr>
       </table>
    </div>
</kul:tab>
