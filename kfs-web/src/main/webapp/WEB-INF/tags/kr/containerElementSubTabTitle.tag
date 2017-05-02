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


<%@ attribute name="containerField" required="true" type="org.kuali.kfs.kns.web.ui.Field"
              description="A Field.CONTAINER (element) type field." %>
<%@ attribute name="isFieldAddingToACollection" required="true"
              description="Whether this is an add element (versus an existing element)." %>

<c:choose>
<c:when test="${isFieldAddingToACollection}" >
    New <c:out value="${containerField.containerElementName}" default="Addition"/>
</c:when>
<c:otherwise >
    <c:out value="${containerField.containerElementName}"/>
    <c:forEach items="${containerField.containerDisplayFields}" var="summaryField" varStatus="status">
        ${status.first ? "(" : ""}
        <kul:readonlyfield addHighlighting="false" field="${summaryField}"/>
        ${status.last ? ")" : "-"}
    </c:forEach>
</c:otherwise>
</c:choose>
