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

<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ tag description="render the header of the detail line table" %>

<%@ attribute name="attributes" required="true" type="java.util.Map"
	description="The DataDictionary entry containing attributes for the line fields."%>
<%@ attribute name="detailFieldNames" required="true"
    description="The names of the fields that will be displayed" %>
<%@ attribute name="hasActions" required="true"
    description="Determine if a user can tak an action on the given line" %>
<%@ attribute name="index" required="false"
    description="The line index" %>
<%@ attribute name="sortableFieldNames" required="false"
    description="the names of the fields that can be sorted" %>

<kul:htmlAttributeHeaderCell literalLabel="${index}"/>

<!-- render the header of the detail line table -->
<c:forTokens var="fieldName" items="${detailFieldNames}" delims=",">
    <c:set var="currencyFormatter" value="org.kuali.rice.core.web.format.CurrencyFormatter"/>
    <c:set var="integerFormatter" value="org.kuali.rice.core.web.format.IntegerFormatter"/>
    <c:set var="entryFormatter" value="${attributes[fieldName].formatterClass}" />
    <c:set var="isCurrency" value="${not empty entryFormatter && fn:contains(currencyFormatter, entryFormatter)}" />
    <c:set var="isInteger" value="${not empty entryFormatter && fn:contains(integerFormatter, entryFormatter)}" />
    <c:set var="styleClass" value="${(isCurrency || isInteger) ? 'right' : '' }" />

	<kul:htmlAttributeHeaderCell attributeEntry="${attributes[fieldName]}" addClass="${styleClass}">
		<c:if test="${fn:contains(sortableFieldNames,fieldName)}">
			&nbsp;<html:image property="methodToCall.sortDetailLineByColumn.${fieldName}"
				src="${ConfigProperties.krad.externalizable.images.url}sort_both_kns.png"
				title="Sort by ${attributes[fieldName].label}"
				alt="Sort by ${attributes[fieldName].label}"/>
		</c:if>
	</kul:htmlAttributeHeaderCell>
</c:forTokens>

<c:if test="${hasActions}">
	<kul:htmlAttributeHeaderCell literalLabel="Actions"/>
</c:if>
