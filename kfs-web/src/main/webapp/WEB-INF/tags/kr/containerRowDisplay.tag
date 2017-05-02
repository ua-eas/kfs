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

<%@ attribute name="rows" required="true" type="java.util.List"
              description="The rows of fields that we'll iterate to display." %>
<%@ attribute name="numberOfColumns" required="false"
              description="The # of fields in this row." %>
<%@ attribute name="depth" required="false"
              description="the recursion depth number" %>
<%@ attribute name="rowsHidden" required="false"
              description="boolean that indicates whether the rows should be hidden or all fields are hidden" %>
<%@ attribute name="rowsReadOnly" required="false"
              description="boolean that indicates whether the rows should be rendered as read-only (note that rows will automatically be rendered as readonly if it is an inquiry or if it is a maintenance document in readOnly mode" %>

<%-- Tomcat 5.5.30 and 5.5.31 had problems with the recursive tag, this is the workaround: --%>
<c:set var="_rows" value="${rows}" scope="request" />
<c:set var="_numberOfColumns" value="${numberOfColumns}" scope="request" />
<c:set var="_depth" value="${depth}" scope="request" />
<c:set var="_rowsHidden" value="${rowsHidden}" scope="request" />
<c:set var="_rowsReadOnly" value="${rowsReadOnly}" scope="request" />
<c:import url="/WEB-INF/jsp/recurseRowDisplay.jsp" />
