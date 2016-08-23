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
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<%-- Added a new tag which displays a banner when in the testing environments --%>
<c:if test="${!empty UserSession && UserSession.displayTestBanner }">
    <div class="testBanner">
        <img src="${pageContext.request.contextPath}/kr/static/images/alert.png" alt="Alert" />
        <c:choose>
            <c:when test="${fn:toUpperCase(UserSession.currentEnvironment) eq 'STG'}">
                <c:set var="envDisplay" value="Staging" />
            </c:when>
            <c:when test="${fn:toUpperCase(UserSession.currentEnvironment) eq 'DEV'}">
                <c:set var="envDisplay" value="Development" />
            </c:when>
            <c:otherwise>
                <c:set var="envDisplay" value="Test ${fn:toUpperCase(UserSession.currentEnvironment)}" />
            </c:otherwise>
        </c:choose>
        <bean:message key="test.banner.message" arg0="${envDisplay}" />
    </div>
</c:if>
