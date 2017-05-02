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
<%@ tag
    description="This tag evaluates a String as an EL name, returning the Map named by the String.
    It would not be needed if there were some way to evaluate the contents of an EL as an EL." %>

<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<%@ attribute name="mapName" required="true"
    description="The name of the map to return, e.g., 'DataDictionary.Budget.attributes.budgetAgency'.
    This tag cannot handle map keys containing . (dot), because dot is used to delimit properties and keys." %>
<%@ attribute name="returnVar" required="true" rtexprvalue="false"
    description="The name of the variable in the caller to set with the result." %>
<%@ variable name-from-attribute="returnVar" alias="valueHolder" scope="AT_END" variable-class="java.util.Map" %>

<c:forTokens items='${mapName}' var='namePart' delims='.'>
    <c:choose>
        <c:when test="${empty walker}">
            <c:set var='walker' value="${pageScope[namePart]}" />
            <c:if test="${empty walker}">
                <c:set var='walker' value="${requestScope[namePart]}" />
            </c:if>
            <c:if test="${empty walker}">
                <c:set var='walker' value="${sessionScope[namePart]}" />
            </c:if>
            <c:if test="${empty walker}">
                <c:set var='walker' value="${applicationScope[namePart]}" />
            </c:if>
        </c:when>
        <c:otherwise>
            <c:set var='walker' value="${walker[namePart]}" />
        </c:otherwise>
    </c:choose>
</c:forTokens>
<c:set var='valueHolder' value="${walker}" />
