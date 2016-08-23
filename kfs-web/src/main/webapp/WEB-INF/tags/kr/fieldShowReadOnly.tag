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

<%@ attribute name="field" required="true" type="org.kuali.kfs.kns.web.ui.Field" description="The field to render as read only." %>
<%@ attribute name="addHighlighting" required="false" description="boolean indicating if this field should be highlighted (to indicate old/new change)" %>
<%@ attribute name="isLookup" required="false" description="boolean indicating if this is a Lookup Screen" %>

<span id="${field.propertyName}.div">
    <c:choose>
        <c:when test="${not (empty field.inquiryURL.href || empty field.propertyValue)}">
        	<c:choose>
        		<c:when test="${fn:startsWith(field.inquiryURL.href, 'http')}">
        			<c:set var="inquiryPrefix" value="" />
        		</c:when>
        		<c:otherwise>
        			<c:set var="inquiryPrefix" value="${ConfigProperties.application.url}/kr/" />
        		</c:otherwise>
        	</c:choose>
        	<c:choose>
	        	<c:when test="${field.inquiryURL.modalAllowed}">
		            <a href="<c:out value="${inquiryPrefix}${field.inquiryURL.href}&mode=modal"/>" title="<c:out value="${field.inquiryURL.title}"/>" data-label="<c:out value="${field.inquiryURL.objectLabel}"/>" data-remodal-target="modal">
		                <kul:readonlyfield addHighlighting="${addHighlighting}" field="${field}" isLookup="${isLookup}" />
		            </a>
		            <a href="<c:out value="${inquiryPrefix}${field.inquiryURL.href}&mode=standalone"/>" target='_blank' title="Open in new tab" class="new-window">
		                <span class="glyphicon glyphicon-new-window"></span>
		            </a>
	            </c:when>
	            <c:otherwise>
	            	<a href="<c:out value="${inquiryPrefix}${field.inquiryURL.href}&mode=standalone"/>" target='_blank' title="<c:out value="${field.inquiryURL.title}"/> (open in new tab)" data-label="<c:out value="${field.inquiryURL.objectLabel}"/>">
		                <kul:readonlyfield addHighlighting="${addHighlighting}" field="${field}" isLookup="${isLookup}" />
		            </a>
	            </c:otherwise>
            </c:choose>
        </c:when>
        <c:otherwise>
            <kul:readonlyfield addHighlighting="${addHighlighting}" field="${field}" isLookup="${isLookup}" />
        </c:otherwise>
    </c:choose>
</span>
