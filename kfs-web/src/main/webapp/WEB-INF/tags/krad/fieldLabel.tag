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
<%@ include file="/krad/WEB-INF/jsp/tldHeader.jsp"%>

<%@ attribute name="field" required="true"
              description="The field instance that is being rendered"
              type="org.kuali.kfs.krad.uif.field.Field"%>

<%-- Used to wrap field templates and handle the label rendering --%>

<%-- check to see if label exists and if it has been rendered in another field (grid layout)--%>
<c:set var="renderLabel" value="${!empty field.labelField && !field.labelFieldRendered}"/>

<%-- render field label left --%>
<c:if test="${renderLabel && ((field.labelPlacement eq 'LEFT') || (field.labelPlacement eq 'TOP'))}">
  <krad:template component="${field.labelField}"/>
</c:if>

<jsp:doBody/>

<%-- render field label right --%>
<c:if test="${renderLabel && (field.labelPlacement eq 'RIGHT')}">
  <krad:template component="${field.labelField}"/>
</c:if>
