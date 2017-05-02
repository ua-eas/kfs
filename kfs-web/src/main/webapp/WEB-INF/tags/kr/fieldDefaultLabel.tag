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

<%@ attribute name="isLookup" required="true" description="Whether the default label is for a field on a lookup or not." %>
<%@ attribute name="isRequired" required="true" description="Is the field a required field?" %>
<%@ attribute name="isReadOnly" required="true" description="Is the field read only?" %>
<%@ attribute name="cellWidth" required="true" description="How wide should the label cell be?" %>
<%@ attribute name="fieldName" required="true" description="What the Field Name is?" %>
<%@ attribute name="fieldType" required="true" description="What type of field is being displayed?" %>
<%@ attribute name="fieldLabel" required="true" description="What's the label to show for the field?" %>
<%@ attribute name="cellClass" required="false" description="Additional class for the cell" %>

<c:if test="${isLookup || (!(empty fieldType) && not isLookup)}">

	<th class="grid right ${cellClass}" style="width:${cellWidth};">
	<c:if test="${!isReadOnly}">
<label id="${fieldName}.label" for="${fieldName}">
</c:if>

		<%--<c:out value="fieldType is ${fieldType}, isReadOnly is ${isReadOnly}, cellWidth is ${cellWidth}<br/>" escapeXml="false" />--%>

		<c:choose>

			<c:when test="${!(empty fieldLabel)}">

				<c:if test="${isRequired && !isReadOnly}">

					${Constants.REQUIRED_FIELD_SYMBOL}&nbsp;

				</c:if>

				<c:out value="${fieldLabel}" />:

			</c:when>

			<c:otherwise>

				&nbsp;

			</c:otherwise>

		</c:choose>

<c:if test="${!isReadOnly}">
    	</label>
    	</c:if>

	</th>
    </c:if>
