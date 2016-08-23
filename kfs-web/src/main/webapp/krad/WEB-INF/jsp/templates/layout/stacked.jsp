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

<tiles:useAttribute name="items" classname="java.util.List"/>
<tiles:useAttribute name="manager" classname="org.kuali.kfs.krad.uif.layout.StackedLayoutManager"/>
<tiles:useAttribute name="container" classname="org.kuali.kfs.krad.uif.container.ContainerBase"/>

<%--
    Stacked Layout Manager:

 --%>

<c:if test="${!empty manager.styleClassesAsString}">
    <c:set var="styleClass" value="class=\"${manager.styleClassesAsString}\""/>
</c:if>

<c:if test="${!empty manager.style}">
    <c:set var="style" value="style=\"${manager.style}\""/>
</c:if>

<c:set var="itemSpanClasses" value="class=\"fieldLine boxLayoutVerticalItem clearfix\""/>

<c:if test="${container.fieldContainer}">
    <c:set var="fieldItemsStyle" value="style=\"float:left;\""/>
    <c:set var="itemSpanClasses" value="class=\"fieldContainerVerticalItem clearfix\""/>
</c:if>

<div id="${manager.id}" ${style} ${styleClass}>
  <span ${fieldItemsStyle}>
    <c:choose>
        <c:when test="${manager.wrapperGroup != null}">
            <%-- render Group --%>
            <krad:template component="${manager.wrapperGroup}"/>
        </c:when>
        <c:otherwise>
            <%-- render items --%>
            <c:forEach items="${manager.stackedGroups}" var="item" varStatus="itemVarStatus">
          <span ${itemSpanClasses}>
            <krad:template component="${item}"/>
          </span>
            </c:forEach>
        </c:otherwise>
    </c:choose>
  </span>
</div>
