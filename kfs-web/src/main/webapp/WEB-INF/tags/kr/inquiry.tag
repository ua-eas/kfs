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
<%@ attribute name="boClassName" required="true" description="The class name of the business object this inquiry is being rendered for." %>
<%@ attribute name="keyValues" required="true" description="The set of keys and values which are the primary key of the business object." %>
<%@ attribute name="render" required="true" description="boolean indicating whether the inquiry link should be rendered. The body is rendered unconditionally." %>

<jsp:doBody var="bodyValue"/>
<c:set var="trimmedBodyValue" value="${fn:trim(bodyValue)}"/>
<c:set var="modalAllowed" value="${!kualiModuleService.isBusinessObjectExternal(boClassName)}"/>

<c:if test="${render && !empty trimmedBodyValue}">
	<c:choose>
		<c:when test="${modalAllowed}">
    		<a href="${ConfigProperties.application.url}/kr/inquiry.do?methodToCall=start&businessObjectClassName=${boClassName}&${keyValues}&mode=modal"  data-remodal-target="modal" title="Open in modal">
    	</c:when>
    	<c:otherwise>
    		<a href="${ConfigProperties.application.url}/kr/inquiry.do?methodToCall=start&businessObjectClassName=${boClassName}&${keyValues}&mode=standalone"  target="_blank" title="Open in new tab" onclick="event.stopPropagation();">
    	</c:otherwise>
    </c:choose>
</c:if>

    ${bodyValue}

<c:if test="${render && !empty trimmedBodyValue}">
    </a>
    <c:if test="${modalAllowed}">
	    <a href="${ConfigProperties.application.url}/kr/inquiry.do?methodToCall=start&businessObjectClassName=${boClassName}&${keyValues}&mode=standalone" target="_blank" title="Open in new tab" class="new-window" onclick="event.stopPropagation();">
	        <span class="glyphicon glyphicon-new-window"></span>
	    </a>
    </c:if>
</c:if>
