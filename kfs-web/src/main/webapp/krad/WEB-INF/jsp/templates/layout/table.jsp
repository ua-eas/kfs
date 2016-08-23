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
<tiles:useAttribute name="manager" classname="org.kuali.kfs.krad.uif.layout.TableLayoutManager"/>

<%--
    Table Layout Manager:

      Works on a collection group to lay out the items as a table.
 --%>

<c:if test="${!empty manager.styleClassesAsString}">
  <c:set var="styleClass" value="class=\"${manager.styleClassesAsString}\""/>
</c:if>

<c:if test="${!empty manager.style}">
  <c:set var="style" value="style=\"${manager.style}\""/>
</c:if>

<c:if test="${manager.separateAddLine}">
  <krad:template component="${manager.addLineGroup}"/>
</c:if>

<c:if test="${!empty manager.headerFields}">
	<table id="${manager.id}" ${style} ${styleClass}>

		  <thead>
		     <krad:grid items="${manager.headerFields}" numberOfColumns="${manager.numberOfColumns}"
		                renderHeaderRow="true" renderAlternatingHeaderColumns="false"
		                applyDefaultCellWidths="${manager.applyDefaultCellWidths}"/>
		  </thead>

		  <tbody>
		     <krad:grid items="${manager.dataFields}" numberOfColumns="${manager.numberOfColumns}"
		                applyAlternatingRowStyles="${manager.applyAlternatingRowStyles}"
		                applyDefaultCellWidths="${manager.applyDefaultCellWidths}"
                    firstLineStyle="${manager.firstLineStyle}"
		                renderAlternatingHeaderColumns="false"/>
		  </tbody>

	</table>
</c:if>

<%-- invoke table tools widget --%>
<krad:template component="${manager.richTable}" componentId="${manager.id}"/>
