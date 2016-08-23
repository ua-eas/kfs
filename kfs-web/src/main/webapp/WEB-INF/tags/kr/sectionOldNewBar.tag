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
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<%@ attribute name="action" required="true"
		description="What's the action requested? Maintenance copy and edit actions get Old and New." %>
<%@ attribute name="colspan" required="true"
              description="What's the colspan for each header column?" %>
<%@ attribute name="depth" required="true"
		description="What level of recursion are we on?  Avoids putting 'New' on container contents." %>

<c:choose>

    <c:when test="${Constants.MAINTENANCE_COPY_ACTION eq action || Constants.MAINTENANCE_EDIT_ACTION eq action}">
        <c:set var="isCopyActionNotEditAction" value="${Constants.MAINTENANCE_COPY_ACTION eq action}"/>
            <th>&nbsp;</th>
            <th colspan="${colspan}" class="old" width="25%">Previous</th>
            <th>&nbsp;</th>
            <th colspan="${colspan}" class="new" width="25%">Proposed</th>
        </tr><tr>
	</c:when>

	<c:when test="${depth eq 0}">
		<%-- Show just one section header that goes all the way across. --%>
        <th>&nbsp;</th>
		<th colspan="${colspan}" class="new">New</th>
        </tr><tr>
    </c:when>

    <c:otherwise>
        <%-- Show nothing. --%>
    </c:otherwise>

</c:choose>
