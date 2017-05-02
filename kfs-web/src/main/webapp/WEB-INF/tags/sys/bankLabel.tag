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
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp" %>

<%@ attribute name="align" required="false" description="label alignment in cell" %>
<%@ attribute name="addClass" required="false" description="class to be added to the cell" %>
<%@ attribute name="horizontal" required="false" description="horizontal header" %>
<c:if test="${KualiForm.editingMode[KFSConstants.BANK_ENTRY_VIEWABLE_EDITING_MODE]}">

    <c:if test="${empty align}">
        <c:set var="align" value="left"/>
    </c:if>

    <c:if test="${empty addClass}">
        <c:set var="addClass" value="right"/>
    </c:if>

    <c:if test="${empty horizontal}">
        <c:set var="horizontal" value="${true}"/>
    </c:if>

    <kul:htmlAttributeHeaderCell
            attributeEntry="${DataDictionary.Bank.attributes.bankCode}"
            align="${align}"
            addClass="${addClass}"
            horizontal="${horizontal}"/>

</c:if>
