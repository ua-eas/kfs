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
<c:set var="pullupOrgAttributes" value="${DataDictionary.BudgetConstructionPullup.attributes}" />
<c:set var="organizationAttributes" value="${DataDictionary.Organization.attributes}" />

<h3>Organization Sub-Tree</h3>
<table class="datatable standard" summary="Organization Sub-Tree">
    <tr class="header">
        <th class="center">Selected</th>
        <th>&nbsp;</th>
        <th>Organization Sub-Tree</th>
        <th class="center">Action</th>
    </tr>

    <c:forEach items="${KualiForm.selectionSubTreeOrgs}" var="item" varStatus="status">
        <tr class="${status.index % 2 == 0 ? 'highlight' : ''}">
            <td class="center">
                <c:choose>
                    <c:when test="${KualiForm.operatingMode == BCConstants.OrgSelOpMode.PULLUP or KualiForm.operatingMode == BCConstants.OrgSelOpMode.PUSHDOWN}">
                        <html:select property="selectionSubTreeOrgs[${status.index}].pullFlag">
                            <html:optionsCollection property="pullFlagKeyLabels" label="value" value="key" />
                        </html:select>
                    </c:when>
                    <c:otherwise>
                        <html:checkbox property="selectionSubTreeOrgs[${status.index}].pullFlag" value="1" />
                    </c:otherwise>
                </c:choose>
            </td>
            <td>
                <kul:htmlControlAttribute
                        property="selectionSubTreeOrgs[${status.index}].chartOfAccountsCode"
                        attributeEntry="${pullupOrgAttributes.chartOfAccountsCode}"
                        readOnly="true"
                        readOnlyBody="true">

                    <kul:inquiry
                            boClassName="org.kuali.kfs.coa.businessobject.Chart"
                            keyValues="chartOfAccountsCode=${item.chartOfAccountsCode}"
                            render="${!empty KualiForm.selectionSubTreeOrgs[status.index].chartOfAccountsCode}">

                        ${KualiForm.selectionSubTreeOrgs[status.index].chartOfAccountsCode}
                    </kul:inquiry>
                </kul:htmlControlAttribute>
                -
                <kul:htmlControlAttribute
                        property="selectionSubTreeOrgs[${status.index}].organizationCode"
                        attributeEntry="${pullupOrgAttributes.organizationCode}"
                        readOnly="true"
                        readOnlyBody="true">

                    <kul:inquiry
                            boClassName="org.kuali.kfs.coa.businessobject.Organization"
                            keyValues="chartOfAccountsCode=${item.chartOfAccountsCode}&amp;organizationCode=${item.organizationCode}"
                            render="${!empty KualiForm.selectionSubTreeOrgs[status.index].organizationCode}">

                        ${KualiForm.selectionSubTreeOrgs[status.index].organizationCode}
                    </kul:inquiry>
                </kul:htmlControlAttribute>
            </td>
            <td>
                <kul:htmlControlAttribute
                        property="selectionSubTreeOrgs[${status.index}].organization.organizationName"
                        attributeEntry="${organizationAttributes.organizationName}"
                        readOnly="true"
                        readOnlyBody="true">

                    ${KualiForm.selectionSubTreeOrgs[status.index].organization.organizationName}
                </kul:htmlControlAttribute>
            </td>
            <td class="center">
                <c:if test="${!item.leaf}">
                    <html:image
                            property="methodToCall.navigateDown.line${status.index}.anchorselectionSubTreeOrgsAnchor${status.index}"
                            src="${ConfigProperties.externalizable.images.url}purap-down.gif"
                            title="Drill Down"
                            alt="Drill Down"
                            styleClass="tinybutton" />
                </c:if>
            </td>
        </tr>
    </c:forEach>

    <tr>
        <c:choose>
            <c:when test="${KualiForm.operatingMode == BCConstants.OrgSelOpMode.SALSET || KualiForm.operatingMode == BCConstants.OrgSelOpMode.REPORTS || KualiForm.operatingMode == BCConstants.OrgSelOpMode.ACCOUNT}">
                <td colspan="4" class="infoline center">
                    <html:submit
                            property="methodToCall.selectAll"
                            title="Select All"
                            alt="Select All"
                            styleClass="btn btn-default"
                            value="Select All"/>
                    <html:submit
                            property="methodToCall.clearAll"
                            title="Clear All"
                            alt="Clear All"
                            styleClass="btn btn-default"
                            value="Clear All"/>
                </td>
            </c:when>
            <c:when test="${KualiForm.operatingMode == BCConstants.OrgSelOpMode.PULLUP}">
                <td colspan="4" class="infoline center">
                    <html:submit
                            property="methodToCall.selectPullOrgAll"
                            title="Select Org All"
                            alt="Select Org All"
                            styleClass="btn btn-default"
                            value="Set Org"/>
                    <html:submit
                            property="methodToCall.selectPullSubOrgAll"
                            title="Select SubOrg All"
                            alt="Select Sub Org All"
                            styleClass="btn btn-default"
                            value="Set Sub Org"/>
                    <html:submit
                            property="methodToCall.selectPullBothAll"
                            title="Select Both All"
                            alt="Select Both All"
                            styleClass="btn btn-default"
                            value="Set Org & Sub Org"/>
                    <html:submit
                            property="methodToCall.clearAll"
                            title="Clear All"
                            alt="Clear All"
                            styleClass="btn btn-default"
                            value="Clear All"/>
                </td>
            </c:when>
            <c:when test="${KualiForm.operatingMode == BCConstants.OrgSelOpMode.PUSHDOWN}">
                <td colspan="4" class="infoline center">
                    <html:submit
                            property="methodToCall.selectPushOrgLevAll"
                            title="Select Org Lev All"
                            alt="Select Org Lev All"
                            styleClass="btn btn-default"
                            value="Set Org"/>
                    <html:submit
                            property="methodToCall.selectPushMgrLevAll"
                            title="Select Mgr Lev All"
                            alt="Select Mgr Lev All"
                            styleClass="btn btn-default"
                            value="Set Fiscal Officer Level"/>
                    <html:submit
                            property="methodToCall.selectPushOrgMgrLevAll"
                            title="Select Org and Mgr Lev All"
                            alt="Select Org and Mgr Lev All"
                            styleClass="btn btn-default"
                            value="Set Org & Fiscal Officer Level"/>
                    <html:submit
                            property="methodToCall.selectPushLevOneAll"
                            title="Select Lev One All"
                            alt="Select Lev One All"
                            styleClass="btn btn-default"
                            value="Set Level One"/>
                    <html:submit
                            property="methodToCall.selectPushLevZeroAll"
                            title="Select Lev Zero All"
                            alt="Select Lev Zero All"
                            styleClass="btn btn-default"
                            value="Set Level Zero"/>
                    <html:submit
                            property="methodToCall.clearAll"
                            title="Clear All"
                            alt="Clear All"
                            styleClass="btn btn-default"
                            value="Clear All"/>
                </td>
            </c:when>
            <c:otherwise>
                <td>&nbsp;</td>
            </c:otherwise>
        </c:choose>
    </tr>
</table>

