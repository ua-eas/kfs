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
<%@ include file="tldHeader.jsp" %>
<kul:page docTitle="Listing of inactivation blockers"
          htmlFormAction="inactivationBlockers" transactionalDocument="false"
          headerTitle="${headerTitle}">

    <c:forEach items="${KualiForm.blockingValues}" var="blockingBO">
        <h2><c:out value="${blockingBO.key}"/></h2>
        <c:forEach items="${blockingBO.value}" var="blockingRecord">
            <c:out value="${blockingRecord}"/><br/>
        </c:forEach>
    </c:forEach>
</kul:page>
