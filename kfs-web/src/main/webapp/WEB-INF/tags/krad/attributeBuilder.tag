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
<%@ include file="/krad/WEB-INF/jsp/tldHeader.jsp"%>

<%@ attribute name="component" required="true"
              description="The UIF component for which the html attribute will be built"
              type="org.kuali.kfs.krad.uif.component.Component"%>

<%@ variable name-given="styleClass" scope="AT_END" %>
<%@ variable name-given="style" scope="AT_END" %>

<%-- Can be called by templates that are building HTML tags to build the standard attributes
such as class and style. This tag checks whether the component actually
has a value for these settings before building up the attribute. This makes the outputted html
cleaner and actually prevents problems from having any empty attribute value in some cases (like
for style. The attribute strings can be referenced by the calling template through the exported
variables --%>

   <c:if test="${!empty component.styleClassesAsString}">
      <c:set var="styleClass" value="class=\"${component.styleClassesAsString}\""/>
   </c:if>

   <c:if test="${!empty component.style}">
      <c:set var="style" value="style=\"${component.style}\""/>
   </c:if>

   <c:if test="${!empty component.title}">
      <c:set var="title" value="title=\"${component.title}\""/>
   </c:if>
