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
<%@ attribute name="includeDocumentHeaderFields" required="false" description="Whether to include the document number as a hidden field." %>
<%@ attribute name="includeEditMode" required="false" description="Whether to include the current edit modes as hidden fields." %>

<c:set var="documentTypeName" value="${KualiForm.docTypeName}" />
<c:set var="documentEntry" value="${DataDictionary[documentTypeName]}" />

<%-- set default values --%>
<c:if test="${empty includeDocumentHeaderFields}">
    <c:set var="includeDocumentHeaderFields" value="true" />
</c:if>
<c:if test="${empty includeEditMode}">
    <c:set var="includeEditMode" value="true" />
</c:if>

<html:hidden property="docId" />
<html:hidden property="document.documentNumber" />

<c:if test="${includeDocumentHeaderFields}">
  <html:hidden property="document.documentHeader.documentNumber" />
</c:if>
<c:if test="${includeEditMode}">
    <c:forEach items="${KualiForm.editingMode}" var="mode">
      <html:hidden property="editingMode(${mode.key})"/>
    </c:forEach>
</c:if>
