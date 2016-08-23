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
<%@ include file="/krad/WEB-INF/jsp/tldHeader.jsp"%>

<tiles:useAttribute name="field"
	classname="org.kuali.kfs.krad.uif.field.LinkField" />
<tiles:useAttribute name="body"/>

<%--
    Standard HTML Link
 --%>

<krad:attributeBuilder component="${field}" />

<krad:span component="${field}">

  <krad:fieldLabel field="${field}">

    <c:if test="${(field.lightBox != null)}">
      <krad:template component="${field.lightBox}" componentId="${field.id}"/>
    </c:if>

    <c:if test="${field.skipInTabOrder}">
      <c:set var="tabindex" value="tabindex=-1"/>
    </c:if>

    <c:if test="${empty fn:trim(body)}">
      <c:set var="body" value="${field.linkLabel}"/>
    </c:if>

    <a id="${field.id}" href="${field.hrefText}" target="${field.target}" title="${field.title}"
      ${style} ${styleClass} ${tabindex} >${body}</a>

  </krad:fieldLabel>

</krad:span>
