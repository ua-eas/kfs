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
<%@ include file="/krad/WEB-INF/jsp/tldHeader.jsp"%>

<tiles:useAttribute name="items" classname="java.util.List"/>
<tiles:useAttribute name="manager" classname="org.kuali.kfs.krad.uif.layout.GridLayoutManager"/>

<%--
    Grid Layout Manager:

      Places each component of the managers field list into a table cell. A new row is created after the
      configured number of columns is rendered.

      The number of horizontal places a field takes up in the grid is determined by the configured colSpan.
      Likewise the number of vertical places a field takes up is determined by the configured rowSpan.

      If the width for the column is not given by the field, it will be calculated by equally dividing the
      space by the number of columns.

      The majority of logic is implemented in grid.tag
 --%>

<c:if test="${!empty manager.styleClassesAsString}">
  <c:set var="styleClass" value="class=\"${manager.styleClassesAsString}\""/>
</c:if>

<c:if test="${!empty manager.style}">
  <c:set var="style" value="style=\"${manager.style}\""/>
</c:if>

<table id="${manager.id}" ${style} ${styleClass} role="presentation">
   <krad:grid items="${items}" numberOfColumns="${manager.numberOfColumns}"
              applyAlternatingRowStyles="${manager.applyAlternatingRowStyles}"
              applyDefaultCellWidths="${manager.applyDefaultCellWidths}"
              renderAlternatingHeaderColumns="${manager.renderAlternatingHeaderColumns}"
              firstLineStyle="${manager.firstLineStyle}"/>
</table>
