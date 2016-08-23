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
<%@ include file="/krad/WEB-INF/jsp/tldHeader.jsp" %>

<tiles:useAttribute name="view" classname="org.kuali.kfs.krad.uif.view.View"/>

<krad:html view="${view}">
    <!-- begin of view render -->
    <!----------------------------------- #APPLICATION HEADER --------------------------------------->
    <krad:template component="${view.applicationHeader}"/>

    <c:if test="${!view.breadcrumbsInApplicationHeader}">
        <krad:template component="${view.breadcrumbs}"/>
    </c:if>

    <!----------------------------------- #VIEW HEADER --------------------------------------->
    <div id="viewheader_div">
        <krad:template component="${view.header}"/>
    </div>

    <!-- changing any ids here will break navigation slide out functionality -->
    <div id="viewlayout_div">
        <!----------------------------------- #VIEW NAVIGATION --------------------------------------->
        <div id="viewnavigation_div">
            <krad:template component="${view.navigation}"
                           currentPageId="${view.currentPageId}"/>
        </div>

        <krad:template component="${view.errorsField}"/>

            <%-- write out view, page id as hidden so the view can be reconstructed if necessary --%>
        <c:if test="${view.renderForm}">
            <form:hidden path="viewId"/>

            <%-- all forms will be stored in session, this is the conversation key --%>
            <form:hidden path="formKey"/>
            <%-- Based on its value, form elements will be checked for dirtyness --%>
            <form:hidden path="validateDirty"/>
        </c:if>

        <!----------------------------------- #VIEW PAGE --------------------------------------->
        <div id="viewpage_div">
            <krad:template component="${view.currentPage}"/>

            <c:if test="${view.renderForm}">
                <form:hidden path="pageId"/>
                <c:if test="${!empty view.currentPage}">
                    <form:hidden id="currentPageTitle" path="view.currentPage.title"/>
                </c:if>
                <form:hidden path="jumpToId"/>
                <form:hidden path="jumpToName"/>
                <form:hidden path="focusId"/>
                <form:hidden path="formHistory.historyParameterString"/>
            </c:if>

            <krad:script value="performJumpTo();"/>
            <c:if test="${view.currentPage.autoFocus}">
                <krad:script value="performFocus();"/>
            </c:if>
        </div>
    </div>

    <!----------------------------------- #VIEW FOOTER --------------------------------------->
    <div id="viewfooter_div">
        <krad:template component="${view.footer}"/>
    </div>

    <!----------------------------------- #APPLICATION FOOTER --------------------------------------->
    <krad:template component="${view.applicationFooter}"/>

</krad:html>
<!-- end of view render -->
