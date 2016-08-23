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

<%@ attribute name="columnCount" required="true" description="Total number of columns in the accounting lines table, to be spanned by this row." %>
<%@ attribute name="subheading" required="true" description="Tab subheading to display, typically redundant with the tab heading." %>
<%@ attribute name="usePercentAdj" required="false" description="Set this to 'true' to display the percent adjust toggle button in the subheading" %>
<%@ attribute name="readOnly" required="false" description="Used in conjunction with the usePercentAdj attribute" %>

<c:if test="{empty usePercentAdj}">
    <c:set var="usePercentAdj" value='false'/>
</c:if>

<c:if test="{usePercentRevExp && empty readOnly}">
    <c:set var="readOnly" value='true'/>
</c:if>

<tr>
    <td colspan="${columnCount}" class="subhead">
        <span class="subhead-left">${subheading}</span>
        <span class="subhead-right">
            <c:if test="${usePercentAdj && !readOnly}">
                <html:hidden property="hideAdjustmentMeasurement"/>
                <c:set var="hideOrShow" value="${KualiForm.hideAdjustmentMeasurement ? 'Show' : 'Hide'}" />

                <html:submit
                        property="methodToCall.toggleAdjustmentMeasurement"
                        alt="${hideOrShow} percent adjustment" title="${hideOrShow} percent adjustment"
                        styleClass="btn btn-default"
                        value="${hideOrShow} % Adjust"/>
            </c:if>

            <html:hidden name="KualiForm" property="hideDetails"/>
            <c:if test="${!empty KualiForm.hideDetails}">
                <c:set var="toggle" value="${KualiForm.hideDetails ? 'Show' : 'Hide'}"/>

                <html:submit
                        property="methodToCall.${toggle}Details"
                        alt="${toggle} transaction details"
                        title="${toggle} transaction details"
                        styleClass="btn btn-default"
                        value="${toggle} Detail"/>
            </c:if>
        </span>
    </td>
</tr>
