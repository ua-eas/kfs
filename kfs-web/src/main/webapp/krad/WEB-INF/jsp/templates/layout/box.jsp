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

<tiles:useAttribute name="items" classname="java.util.List"/>
<tiles:useAttribute name="manager" classname="org.kuali.kfs.krad.uif.layout.BoxLayoutManager"/>
<tiles:useAttribute name="container" classname="org.kuali.kfs.krad.uif.container.ContainerBase"/>

<%--
    Box Layout Manager:

      Places each component of the given list into a horizontal or vertical row.

      The amount of padding is configured by the seperationPadding
      property of the layout manager. The padding is implemented by setting the margin of the wrapping
      span style. For vertical orientation, the span style is set to block.
 --%>

<c:if test="${!empty manager.styleClassesAsString}">
    <c:set var="styleClass" value="class=\"${manager.styleClassesAsString}\""/>
</c:if>

<c:if test="${!empty manager.style}">
    <c:set var="style" value="${manager.style}"/>
</c:if>

<c:if test="${!empty manager.itemStyle}">
    <c:set var="itemStyle" value="style=\"${manager.itemStyle}\""/>
</c:if>

<c:choose>
    <c:when test="${manager.orientation=='HORIZONTAL'}">
        <c:set var="itemStyleClass" value="boxLayoutHorizontalItem ${manager.itemStyleClassesAsString}"/>
    </c:when>
    <c:otherwise>
        <c:set var="itemStyleClass" value="boxLayoutVerticalItem ${manager.itemStyleClassesAsString} clearfix"/>
    </c:otherwise>
</c:choose>

<c:choose>
    <c:when test="${container.fieldContainer}">
        <c:set var="style" value="float:left;${style}"/>
    </c:when>
    <c:otherwise>
        <c:set var="itemStyleClass" value="fieldLine ${itemStyleClass}"/>
    </c:otherwise>
</c:choose>

<c:set var="itemStyleClass" value="class=\"${itemStyleClass}\""/>

<c:if test="${!empty style}">
    <c:set var="style" value="style=\"${style}\""/>
</c:if>

<%-- render items --%>
<div id="${manager.id}" ${style} ${styleClass}>
    <c:forEach items="${items}" var="item" varStatus="itemVarStatus">
     <span ${itemStyle} ${itemStyleClass}>
        <krad:template component="${item}"/>
     </span>
    </c:forEach>

    <%--
       Adds a special error container for horizontal case, fields will instead display their errors here
       (errorsField in attributeFields of this layout will not generate their errorsField through their jsp, as normal)
       see BoxLayoutManager.java
    --%>
    <c:if test="${manager.layoutFieldErrors}">
	   <span id="${manager.id}_errors_block" class="kr-errorsField" style="float:left;">
	   		<c:forEach items="${container.inputFields}" var="item">
                <krad:template component="${item.errorsField}"/>
            </c:forEach>
	   </span>
    </c:if>

</div>
