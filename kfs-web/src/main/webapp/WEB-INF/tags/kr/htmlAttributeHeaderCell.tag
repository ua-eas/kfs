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

<%@ attribute name="attributeEntry" required="false" type="java.util.Map"
              description="The entry of an attribute in the DataDictionary to use to generate the label." %>
<%@ attribute name="attributeEntryName" required="false"
              description="The full name of the DataDictionary entry to use as the attributeEntry.
              E.g., 'DataDictionary.Budget.attributes.budgetProjectDirectorUniversalIdentifier'." %>
<%@ attribute name="literalLabel" required="false"
              description="A String to use for the label instead of the DataDictionary." %>

<%@ attribute name="horizontal" required="false" description="boolean to orient this header horizontally" %>
<%@ attribute name="align" required="false" description="overrides horizontal alignment" %>
<%@ attribute name="rowspan" required="false" description="defaults to 1" %>
<%@ attribute name="colspan" required="false" description="defaults to 1" %>
<%@ attribute name="scope" required="false" description="defaults to no scope attribute" %>
<%@ attribute name="width" required="false" description="defaults to no width attribute" %>
<%@ attribute name="labelFor" required="false" description="relates the label to an input control" %>
<%@ attribute name="forceRequired" required="false" description="indicate the field is required despite the DataDictionary" %>
<%@ attribute name="hideRequiredAsterisk" required="false" description="if set to true, this will hide the required asterisk symbol under all situations" %>
<%@ attribute name="anchor" required="false" description="adds a named anchor inside the header cell" %>
<%@ attribute name="nowrap" required="false" description="add the 'nowrap' clause to the TH" %>
<%@ attribute name="useShortLabel" required="false" description="indicate to use shortLabel or not and default to true" %>
<%@ attribute name="headerLink" required="false" description="allows you to change the header lable to a link " %>
<%@ attribute name="addClass" required="false" description="additional classes to add" %>

<c:set var="scopeAttribute" value='scope="${scope}"'/>  <%-- this works for HTML output (but not for JSP execution) --%>
<c:set var="alignAttribute" value='align="${align}"'/>
<c:set var="widthAttribute" value='width="${width}"'/>
<c:set var="isUseShortLabel" ><c:out value="${useShortLabel}" default="true"/></c:set>

<th
    rowspan="${empty rowspan ? 1 : rowspan}"
    colspan="${empty colspan ? 1 : colspan}"
    ${empty scope ? '' : scopeAttribute}
    ${empty align ? (!horizontal ? '' : 'align="right"') : alignAttribute}
    ${empty width ? '' : widthAttribute}
    ${empty nowrap ? '' : 'nowrap'}
    ${empty addClass ? "" : "class='".concat(addClass).concat("'")}
    >
    <c:if test="${not empty anchor}">
    	<a name="${anchor}"></a>
    </c:if>
    <c:choose>
        <c:when test="${empty attributeEntry && empty attributeEntryName}">
            <c:if test="${not empty labelFor}"><label for="${labelFor}"></c:if>${literalLabel}
            <c:if test="${not empty labelFor}"></label></c:if>
        </c:when>
        <c:otherwise>
            <kul:htmlAttributeLabel
                attributeEntry="${attributeEntry}"
                attributeEntryName="${attributeEntryName}"
                useShortLabel="${isUseShortLabel}"
                noColon="${!horizontal}"
                labelFor="${labelFor}"
                forceRequired="${forceRequired}"
                readOnly="${hideRequiredAsterisk}"
                labelLink="${headerLink}"
                />
        </c:otherwise>
    </c:choose>
    <jsp:doBody/>
</th>
