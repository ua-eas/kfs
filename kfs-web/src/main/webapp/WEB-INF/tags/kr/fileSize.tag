<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   -
   - Copyright 2005-2017 Kuali, Inc.
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
<%@ tag body-content="scriptless" %>
<%@ attribute name="byteSize" required="true" description="The size, in bytes, to display as a file size." %>

<%@ variable name-given="fileSize" scope="NESTED" %>
<%@ variable name-given="fileSizeUnits" scope="NESTED" %>

<c:if test="${byteSize lt 1024}" >
    <c:set var="fileSize" value="${byteSize}" />
    <c:set var="fileSizeUnits" value="bytes" />
</c:if>

<c:if test="${byteSize ge 1024}">
    <c:set var="kiloSize" value="${byteSize / 1024}" />

    <c:if test="${kiloSize lt 1024}" >
        <c:set var="fileSize" value="${kiloSize}" />
        <c:set var="fileSizeUnits" value="KB" />
    </c:if>

    <c:if test="${kiloSize ge 1024}">
        <c:set var="megaSize" value="${kiloSize / 1024}" />

        <c:if test="${megaSize lt 1024}" >
            <c:set var="fileSize" value="${megaSize}" />
            <c:set var="fileSizeUnits" value="MB" />
        </c:if>

        <c:if test="${megaSize ge 1024}">
            <c:set var="gigaSize" value="${megaSize / 1024}" />

            <c:set var="fileSize" value="${gigaSize}" />
            <c:set var="fileSizeUnits" value="GB" />
        </c:if>
    </c:if>
</c:if>

<c:set var="dot" value="." />
<c:if test="${fn:contains(fileSize, dot)}" >
    <c:set var="fileSize" value="${fn:substringBefore(fileSize, dot)}" />
</c:if>

<jsp:doBody/>
