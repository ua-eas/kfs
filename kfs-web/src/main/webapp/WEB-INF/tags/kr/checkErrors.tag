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

<%@ attribute name="keyMatch" required="true" description="A concatenated String with properties to match error keys with, each seperated by a comma." %>
<%@ attribute name="auditMatch" required="false" description="Audit information which errors should be matched against if no errors were matched by keyMatch." %>

<c:set var="hasErrors" value="false" scope="request" />

<c:choose>
	<c:when test="${! (empty keyMatch)}">
		<c:forEach items="${fn:split(keyMatch,',')}" var="prefix">
			<c:forEach items="${ErrorPropertyList}" var="key">
				<c:if test="${(fn:endsWith(prefix,'*') && fn:startsWith(key,fn:replace(prefix,'*',''))) || (key eq prefix)}">
					<c:set var="hasErrors" value="true" scope="request"/>
				</c:if>
			</c:forEach>
		</c:forEach>
	</c:when>

	<c:otherwise>
		<logic:messagesPresent>
			<c:set var="hasErrors" value="true" scope="request" />
		</logic:messagesPresent>
	</c:otherwise>
</c:choose>

<c:if test="${!(empty auditMatch) && !hasErrors}">
	<c:forEach items="${fn:split(auditMatch,',')}" var="prefix">
		<c:forEach items="${AuditErrors}" var="cluster">
			<c:forEach items="${cluster.value.auditErrorList}" var="audit">
				<c:if test="${(fn:endsWith(prefix,'*') && fn:startsWith(audit.errorKey,fn:replace(prefix,'*',''))) || (audit.errorKey eq prefix)}">
					<c:set var="hasErrors" value="true" scope="request"/>
				</c:if>
			</c:forEach>
		</c:forEach>
	</c:forEach>
</c:if>


