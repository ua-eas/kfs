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
<%@ tag body-content="scriptless" %>

<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<%@ attribute name="map" required="true" type="java.util.Map" description="The Map to iterate they keys over." %>
<%@ attribute name="valueVar" required="true" rtexprvalue="false" description="The name of the variable to put the values in the map attribute into, as the Map is iterated over." %>
<%@ variable name-from-attribute="valueVar" alias="valueHolder" scope="NESTED" %>

<c:forEach items='${map}' var='mapEntry' >
    <c:set var='valueHolder' value="${mapEntry.value}" />

    <jsp:doBody/>
</c:forEach>
