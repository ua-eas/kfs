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
<%@ include file="/jsp/sys/kfsTldHeader.jsp" %>

<kul:tab tabTitle="${KualiForm.operatingModeTitle}" defaultOpen="true">
    <div class="tab-container">

    <table class="standard">
        <c:if test="${empty KualiForm.objectCodePickList}">
            <tr>
                <th class="grid" colspan="6" align="left">
                    <bean:message key="${BCConstants.Report.OBJECT_CODE_LIST_EMPTY_MESSAGE_KEY}"/>
                </th>
            </tr>

            </table>

            <div id="globalbuttons" class="globalbuttons">
                <html:submit
                        styleClass="btn btn-default"
                        property="methodToCall.returnToCaller"
                        title="close"
                        alt="close"
                        value="Close"/>
            </div>
        </c:if>

        <c:if test="${!empty KualiForm.objectCodePickList}">
            <c:set var="objectCodeAttribute" value="${DataDictionary.BudgetConstructionObjectPick.attributes}"/>

            <tr class="header">
                <th class="grid">
                    Select
                </th>
                <th class="grid">
                    <kul:htmlAttributeLabel
                            attributeEntry="${objectCodeAttribute.financialObjectCode}"
                            useShortLabel="false" noColon="true"/>
                </th>
                <th class="grid">
                    <kul:htmlAttributeLabel
                            attributeEntry="${objectCodeAttribute.objectCodeDescription}"
                            useShortLabel="false" noColon="true"/>
                </th>
            </tr>

            <logic:iterate name="KualiForm" id="objectCodePick" property="objectCodePickList" indexId="ctr">
                <html-el:hidden name="KualiForm" property="objectCodePickList[${ctr}].principalId"/>
                <html-el:hidden name="KualiForm" property="objectCodePickList[${ctr}].versionNumber"/>

                <tr align="center">
                    <td class="grid" valign="center">
                        <html:checkbox property="objectCodePickList[${ctr}].selectFlag"
                                       value="1"/>
                    </td>
                    <td class="grid" valign="center">
                        <kul:htmlControlAttribute
                                property="objectCodePickList[${ctr}].financialObjectCode"
                                attributeEntry="${objectCodeAttribute.financialObjectCode}"
                                readOnly="true"/>
                    </td>
                    <td class="grid" valign="center">
                        <kul:htmlControlAttribute
                                property="objectCodePickList[${ctr}].objectCodeDescription"
                                attributeEntry="${objectCodeAttribute.objectCodeDescription}"
                                readOnly="true"/>
                    </td>
                </tr>
            </logic:iterate>

            </table>

            <c:if test="${!KualiForm.budgetConstructionReportThresholdSettings.lockThreshold}">
                <bc:thresholdSettings/>
            </c:if>

            <div id="globalbuttons" class="globalbuttons">
                <html:submit
                        property="methodToCall.selectAllObjectCodes"
                        title="Select"
                        alt="Select All Codes"
                        styleClass="btn btn-default"
                        value="Select All"/>
                <html:submit
                        property="methodToCall.unselectAllObjectCodes"
                        title="Unselect"
                        alt="Unselect All Codes"
                        styleClass="btn btn-default"
                        value="Unselect All"/>
                <html:submit
                        styleClass="btn btn-default"
                        property="methodToCall.performReport"
                        title="Perform Report"
                        alt="submit"
                        onclick="excludeSubmitRestriction=true"
                        value="Submit"/>
                <html:submit
                        styleClass="btn btn-default"
                        property="methodToCall.returnToCaller"
                        title="close"
                        alt="close"
                        value="Close"/>
            </div>

        </c:if>
    </div>
</kul:tab>




