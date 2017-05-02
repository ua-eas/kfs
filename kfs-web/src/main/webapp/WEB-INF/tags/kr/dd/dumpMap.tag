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

<c:if test="${!empty prefix}">
    <c:set var="prefix" value="${prefix}." />
</c:if>

<c:if test="${empty noTable}">
    <table border=1 cellpadding=4>
        <c:if test="${!empty title}">
            <tr>
                <th colspan=2 align=left>${title}</th>
            </tr>
        </c:if>

        <tr>
            <th align=left>key</th>
            <th align=left>value</th>
        </tr>
</c:if>

        <c:forEach items='${map}' var='mapEntry' >
            <c:if test="${fn:startsWith(mapEntry.value,'{')}">
                <dd:dumpMap noTable="true" prefix="${prefix}${mapEntry.key}" map="${mapEntry.value}" />
            </c:if>

            <c:if test="${!fn:startsWith(mapEntry.value,'{')}">
                <tr>
                    <td valign=top>${prefix}${mapEntry.key}</td>

                    <td>
                        <table cellspacing=0 cellpadding=0>
                            <c:set var="subItems" value="${fn:split(mapEntry.value, ',')}" />

                            <c:forEach items="${subItems}" var="subItem" >
                                <tr><td>${subItem}</td></tr>
                            </c:forEach>
                        </table>
                    </td>
                </tr>
            </c:if>
        </c:forEach>
<c:if test="${empty noTable}">
    </table>
</c:if>
