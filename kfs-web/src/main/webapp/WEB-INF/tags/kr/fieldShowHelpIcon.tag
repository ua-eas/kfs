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

<c:if test="${isReadOnly ne true && field.fieldType ne field.KUALIUSER && field.fieldType ne field.HIDDEN}">

	<c:if test="${field.fieldType ne field.IMAGE_SUBMIT && field.fieldType ne field.CONTAINER}">

        <kul:help
            businessObjectClassName="${field.businessObjectClassName}"
            attributeName="${field.fieldHelpName}"
            altText="${field.fieldLabel}"
            alternativeHelp="${field.fieldLevelHelpUrl}" />

	</c:if>

</c:if>
