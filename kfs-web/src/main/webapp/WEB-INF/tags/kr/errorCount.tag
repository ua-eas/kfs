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

<%@ attribute name="auditCount" required="false" description="The number of audit errors displayed on the page or section including this tag." %>

<c:if test="${empty auditCount}">
  <c:set var="auditCount" value="0" />
</c:if>
<c:set var="errorCount" value="${ErrorContainer.errorCount + auditCount}" />

<c:if test="${errorCount > 0}">
  <div class="error">
    ${errorCount} error(s) found on page.
  </div>
</c:if>
