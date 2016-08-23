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
<%@ attribute name="boClassName" required="true" description="The class of the business object to create the inquiry for." %>
<%@ attribute name="inquiryParameters" required="false" description="The keys of the business object to display." %>
<%@ attribute name="anchor" required="false" description="The HTML named anchor of the button rendered." %>
<%@ attribute name="tabindexOverride" required="false" description="The overridden tab index of the button rendered." %>

<c:choose>
  <c:when test="${!empty tabindexOverride}">
    <c:set var="tabindex" value="${tabindexOverride}"/>
  </c:when>
  <c:otherwise>
    <c:set var="tabindex" value="${KualiForm.nextArbitrarilyHighIndex}"/>
  </c:otherwise>
</c:choose>
<c:set var="epMethodToCallAttribute" value="methodToCall.performInquiry.(!!${boClassName}!!).((`${inquiryParameters}`)).anchor${anchor}"/>
${kfunc:registerEditableProperty(KualiForm, epMethodToCallAttribute)}
<html:image tabindex="${tabindex}" property="${epMethodToCallAttribute}"
   onclick="javascript: inquiryPop('${boClassName}','${inquiryParameters}'); return false"
   src="${ConfigProperties.kr.externalizable.images.url}book_open.png" styleClass="tinybutton" title="Direct Inquiry" alt="Direct Inquiry"/>
