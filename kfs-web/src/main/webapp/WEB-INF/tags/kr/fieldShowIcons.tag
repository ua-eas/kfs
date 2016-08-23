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

<%@ attribute name="isReadOnly" required="true"
              description="Is the view for this field readOnly?" %>
<%@ attribute name="field" required="true" type="org.kuali.kfs.kns.web.ui.Field"
              description="The field for which to show the lookup icon." %>
<%@ attribute name="addHighlighting" required="false"
              description="boolean indicating if this field should be highlighted (to indicate old/new change)" %>
<%--
				#######################################################
				# If the field has errors, display error icon.
				####################################################### --%>
<kul:checkErrors keyMatch="${field.propertyName}" />
<c:if test="${hasErrors}">
	 <kul:fieldShowErrorIcon />
</c:if>
<kul:fieldShowLookupIcon isReadOnly="${isReadOnly}" field="${field}" anchor="${currentTabIndex}"/>
<kul:fieldShowDirectInquiryIcon isReadOnly="${isReadOnly}" field="${field}" anchor="${currentTabIndex}"/>
<kul:fieldShowExpandedTextareaIcon isReadOnly="${isReadOnly}" field="${field}" anchor="${currentTabIndex}"/>
<c:if test="${field.fieldLevelHelpEnabled || (!field.fieldLevelHelpDisabled && KualiForm.fieldLevelHelpEnabled)}">
<kul:fieldShowHelpIcon isReadOnly="${isReadOnly}" field="${field}" />
</c:if>

<%-- don't render the field changed icon if readonly since the fieldShowReadOnly tag will render it when the field is readonly --%>
<c:if test="${addHighlighting && field.highlightField && !isReadOnly}">
  <kul:fieldShowChangedIcon />
</c:if>

