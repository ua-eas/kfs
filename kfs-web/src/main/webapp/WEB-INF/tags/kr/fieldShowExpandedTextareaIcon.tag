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

<%@ attribute name="isReadOnly" required="true"
              description="Is the view for this field readOnly?" %>
<%@ attribute name="field" required="true" type="org.kuali.kfs.kns.web.ui.Field"
              description="The field for which to show the lookup icon." %>
<%@ attribute name="anchor" required="false"
              description="The anchor (i.e. tab index) of the tab in which these icons will be displayed (primarily for lookups to return to the original section)" %>

<c:if test="${field.fieldType eq field.TEXT_AREA && field.expandedTextArea eq true}">
	<kul:expandedTextArea textAreaFieldName="${field.propertyName}" action="${fn:substringBefore(fn:substring(requestScope['org.apache.struts.taglib.html.FORM'].action, 1, -1),'.do')}" textAreaLabel="${field.fieldLabel}" disabled="false" title="${field.fieldLabel}" readOnly="${isReadOnly}" maxLength="${field.maxLength}"/>
</c:if>
