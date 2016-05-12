<%--
 Copyright 2005-2008 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
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
