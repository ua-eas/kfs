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

<c:set var="pointOfViewOrgAttributes" value="${DataDictionary.BudgetConstructionOrganizationReports.attributes}"/>
<c:set var="pullupOrgAttributes" value="${DataDictionary.BudgetConstructionPullup.attributes}"/>
<c:set var="organizationAttributes" value="${DataDictionary.Organization.attributes}"/>

<kul:page
        showDocumentInfo="false"
        htmlFormAction="budgetOrganizationSelectionTree"
        renderMultipart="true"
        docTitle="Organization Selection"
        transactionalDocument="false"
        showTabButtons="true">

    <script language="JavaScript" type="text/javascript" src="scripts/module/bc/organizationSelectionTree.js"></script>

    <kul:errors keyMatch="pointOfViewOrg" errorTitle="Errors found in Organization Selection:"/>
    <c:forEach items="${KualiForm.messages}" var="message">
        <div class="error" style="color:#C47965;">${message}</div>
    </c:forEach>

    <kul:tabTop tabTitle="${KualiForm.operatingModeTitle}" defaultOpen="true" tabErrorKey="orgSel,selectionSubTreeOrgs">
        <bc:budgetConstructionOrgSelection/>
    </kul:tabTop>

    <c:if test="${!empty KualiForm.selectionSubTreeOrgs}">
        <c:if test="${KualiForm.operatingMode == BCConstants.OrgSelOpMode.REPORTS}">
            <bc:budgetConstructionOrgSelectionReport/>
        </c:if>
        <c:if test="${KualiForm.operatingMode == BCConstants.OrgSelOpMode.ACCOUNT}">
            <bc:budgetConstructionOrgSelectionAccount/>
        </c:if>
        <c:if test="${KualiForm.operatingMode == BCConstants.OrgSelOpMode.SALSET}">
            <bc:budgetConstructionOrgSelectionSalset/>
        </c:if>
        <c:if test="${KualiForm.operatingMode == BCConstants.OrgSelOpMode.PULLUP or KualiForm.operatingMode == BCConstants.OrgSelOpMode.PUSHDOWN}">
            <bc:budgetConstructionOrgSelectionPushOrPull/>
        </c:if>
    </c:if>

    <div id="globalbuttons" class="globalbuttons">
        <c:if test="${!empty KualiForm.selectionSubTreeOrgs && KualiForm.operatingMode == BCConstants.OrgSelOpMode.PULLUP}">
            <html:submit
                    property="methodToCall.performPullUp"
                    title="Perform Pullup"
                    alt="Perform Pullup"
                    styleClass="btn btn-default"
                    value="Pull Up"/>
        </c:if>
        <c:if test="${!empty KualiForm.selectionSubTreeOrgs && KualiForm.operatingMode == BCConstants.OrgSelOpMode.PUSHDOWN}">
            <html:submit
                    property="methodToCall.performPushDown"
                    title="Perform Pushdown"
                    alt="Perform Pushdown"
                    styleClass="btn btn-default"
                    value="Push Down"/>
        </c:if>
        <html:submit
                styleClass="btn btn-default"
                property="methodToCall.returnToCaller"
                title="close"
                alt="close"
                value="Close"/>
    </div>
    <kul:stickyGlobalButtons bodySelector="div#page-content"/>
</kul:page>
