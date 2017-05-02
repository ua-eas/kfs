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
<%@ tag body-content="empty" %>

<%@ attribute name="attributeEntry" required="false" type="java.util.Map" description="A map of attribute information from the data dictionary." %>
<%@ attribute name="attributeEntryName" required="false"
              description="The full name of the DataDictionary entry to use,
              e.g., 'DataDictionary.Budget.attributes.budgetProjectDirectorUniversalIdentifier'.
              Either attributeEntry or attributeEntryName is required." %>
<%@ attribute name="readOnly" required="false" description="Whether the label is for a read only attribute; if it is, then no 'required' icon will be displayed."  %>
<%@ attribute name="useShortLabel" required="false" description="Whether the short label for the control should be used." %>
<%@ attribute name="labelFor" required="false" description="The control name which this label is associated with; typically the property name will be sent in here." %>
<%@ attribute name="includeHelpUrl" required="false" description="If set to true, then the help link will render a help URL regardless of the skipHelpUrl parameter value." %>
<%@ attribute name="skipHelpUrl" required="false" description="If set to true and includeHelpUrl is set to false, then the help link will not be rendered for this attribute.  If both
              this attribute and includeHelpUrl are set to false, then the KualiForm.fieldLevelHelpEnabled will control whether to render the help link." %>
<%@ attribute name="noColon" required="false" description="Whether a colon should be rendered after the label or not." %>
<%@ attribute name="forceRequired" required="false" description="Whether the required icon should be forced to be rendered." %>
<%@ attribute name="labelLink" required="false" description="Allows for the label to be turned into a link for sorting purposes. Excludes the ability to include a help url" %>

<c:if test="${not empty attributeEntryName}">
    <dd:evalNameToMap mapName="${attributeEntryName}" returnVar="attributeEntry"/>
</c:if>
<c:if test="${not empty labelFor}"><label for="${labelFor}"></c:if>
<c:if test="${(attributeEntry.required == true || forceRequired) && readOnly != true}">
  ${Constants.REQUIRED_FIELD_SYMBOL}
</c:if>
<c:if test="${not empty labelLink && includeHelpUrl || (!skipHelpUrl && KualiForm.fieldLevelHelpEnabled)}">
    <a href="${ConfigProperties.application.url}/kr/help.do?methodToCall=getAttributeHelpText&amp;businessObjectClassName=${attributeEntry.fullClassName}&amp;attributeName=${attributeEntry.name}"
        tabindex="${KualiForm.nextArbitrarilyHighIndex}" target="helpWindow" title="[Help] ${attributeEntry.label}">
</c:if>
<c:if test="${not empty labelLink}">
    <a href="${labelLink}" tabindex="${KualiForm.nextArbitrarilyHighIndex}" title="${attributeEntry.label}">
</c:if>
<c:if test="${useShortLabel == true}"><c:out value="${attributeEntry.shortLabel}" /></c:if>
<c:if test="${useShortLabel != true}"><c:out value="${attributeEntry.label}" /></c:if>
<c:if test="${!noColon}">:</c:if>
<c:if test="${not empty labelLink && includeHelpUrl || (!skipHelpUrl && KualiForm.fieldLevelHelpEnabled)}"></a></c:if>
<c:if test="${not empty labelFor}"></label></c:if>
