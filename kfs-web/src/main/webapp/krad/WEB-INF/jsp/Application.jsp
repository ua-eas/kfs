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

<c:choose>
    <c:when test="${KualiForm.renderFullView}">

        <%-- render full view --%>
        <krad:template component="${KualiForm.view}"/>

    </c:when>
    <c:otherwise>

        <%-- render page only --%>
        <html>
            <%-- rerun view pre-load script to get new state variables for page --%>
        <krad:script value="${view.preLoadScript}"/>

        <s:nestedPath path="KualiForm">
            <krad:template component="${KualiForm.view.breadcrumbs}"/>

            <div id="viewpage_div">
                <krad:template component="${KualiForm.view.currentPage}"/>

                <c:if test="${KualiForm.view.renderForm}">
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

                <c:if test="${KualiForm.view.currentPage.autoFocus}">
                    <krad:script value="performFocus();"/>
                </c:if>
            </div>
        </s:nestedPath>
        </html>

    </c:otherwise>
</c:choose>
