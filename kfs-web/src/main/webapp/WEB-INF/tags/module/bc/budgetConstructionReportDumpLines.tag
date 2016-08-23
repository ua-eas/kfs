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

<kul:tabTop tabTitle="Report/Export" defaultOpen="true" tabErrorKey="${BCConstants.BUDGET_CONSTRUCTION_REPORTDUMP_TAB_ERRORS}">
<div class="tab-container">
    <table class="datatable standard">
        <c:forEach items="${KualiForm.budgetConstructionDocumentReportModes}" var="item" varStatus="status" >
            <tr>
                <td class="datacell nowrap right">
                    <html:submit
                            property="methodToCall.performReportDump.line${status.index}"
                            title="Run Report/Dump For Line ${status.index}"
                            onclick="excludeSubmitRestriction=true"
                            alt="Run Report/Dump Line ${status.index}"
                            styleClass="btn btn-default"
                            value="View"/>
                </td>
                <td class="datacell nowrap left">
                    ${item.reportDesc}
                </td>
            </tr>

        </c:forEach>
    </table>
</div>
</kul:tabTop>
