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
<%@ include file="/krad/WEB-INF/jsp/tldHeader.jsp" %>

<tiles:useAttribute name="field" classname="org.kuali.kfs.krad.uif.field.ImageField"/>

<%--
    Standard HTML Image element

 --%>

<c:if test="${!empty field.height}">
    <c:set var="height" value="height=\"${field.height}\""/>
</c:if>

<krad:attributeBuilder component="${field}"/>

<krad:span component="${field}">

    <krad:fieldLabel field="${field}">
        <%-- render caption header above --%>
        <c:if test="${!empty field.captionHeader.headerText && field.captionHeaderAboveImage}">
            <krad:template component="${field.captionHeader}"/>
        </c:if>

        <img id="${field.id}" src="${field.source}" alt="${field.altText}"
            ${height} ${style} ${styleClass} ${title} />

        <%-- render caption header below --%>
        <c:if test="${!empty field.captionHeader.headerText && !field.captionHeaderAboveImage}">
            <krad:template component="${field.captionHeader}"/>
        </c:if>

        <%-- render cutline text --%>
        <c:if test="${!empty field.cutline.messageText}">
            <krad:template component="${field.cutline}"/>
        </c:if>

    </krad:fieldLabel>

</krad:span>
