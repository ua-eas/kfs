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
<%@ include file="/krad/WEB-INF/jsp/tldHeader.jsp" %>

<tiles:useAttribute name="field" classname="org.kuali.kfs.krad.uif.field.HeaderField"/>

<c:if test="${!empty field.headerStyleClasses}">
    <c:set var="styleClass" value="class=\"${field.headerStyleClasses}\""/>
</c:if>

<c:if test="${!empty field.headerStyle}">
    <c:set var="style" value="style=\"${field.headerStyle}\""/>
</c:if>

<c:if test="${!empty field.headerLevel}">
    <c:set var="headerOpenTag" value="<${field.headerLevel} ${style} ${styleClass}>"/>
    <c:set var="headerCloseTag" value="</${field.headerLevel}>"/>
</c:if>

<c:if test="${!empty field.headerDivStyleClasses}">
    <c:set var="divStyleClass" value="class=\"${field.headerDivStyleClasses}\""/>
</c:if>

<c:if test="${!empty field.headerDivStyle}">
    <c:set var="divStyle" value="style=\"${field.headerDivStyle}\""/>
</c:if>

<krad:div component="${field}">
    <c:if test="${!empty field.headerLevel && !empty field.headerText && field.headerText != '&nbsp;'}">
        ${headerOpenTag}${field.headerText}${headerCloseTag}
    </c:if>

    <%-- render header group --%>
    <c:if test="${!empty field.group}">
        <krad:template component="${field.group}"/>
    </c:if>
</krad:div>
