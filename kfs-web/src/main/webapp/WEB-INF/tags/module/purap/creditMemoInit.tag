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
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>
<c:set var="tabindexOverrideBase" value="20" />

<kul:tabTop tabTitle="Credit Memo Initiation" defaultOpen="true" tabErrorKey="*">
    <div class="tab-container">
        <table class="standard" summary="Credit Memo Init Section" >
            <tr>
                <th class="right" width="25%">
                   <kul:htmlAttributeLabel attributeEntry="${documentAttributes.creditMemoNumber}" />
                </th>
                <td class="datacell" width="25%">
                   <kul:htmlControlAttribute
                   		attributeEntry="${documentAttributes.creditMemoNumber}" property="document.creditMemoNumber"
                   		tabindexOverride="${tabindexOverrideBase + 0}"/>
                </td>
                <th class="right" width="25%">
                   **<kul:htmlAttributeLabel attributeEntry="${documentAttributes.paymentRequestIdentifier}" />
                </th>
                <td class="datacell" width="25%">
                   <kul:htmlControlAttribute
                   		attributeEntry="${documentAttributes.paymentRequestIdentifier}" property="document.paymentRequestIdentifier"
                   		tabindexOverride="${tabindexOverrideBase + 5}"/>
                </td>
            </tr>

            <tr>
                <th class="right">
                   <kul:htmlAttributeLabel attributeEntry="${documentAttributes.creditMemoDate}" />
                </th>
                <td class="datacell">
                   <kul:htmlControlAttribute
                   		attributeEntry="${documentAttributes.creditMemoDate}" property="document.creditMemoDate" datePicker="true"
                   		tabindexOverride="${tabindexOverrideBase + 0}"/>
                </td>
                <th class="right">
                   **<kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderIdentifier}" />
                </th>
                <td class="datacell">
                   <kul:htmlControlAttribute
                   		attributeEntry="${documentAttributes.purchaseOrderIdentifier}" property="document.purchaseOrderIdentifier"
                   		tabindexOverride="${tabindexOverrideBase + 5}"/>
                </td>
            </tr>

            <tr>
                <th class="right">
                   <kul:htmlAttributeLabel  attributeEntry="${documentAttributes.creditMemoAmount}" />
                </th>
                <td class="datacell">
                   <kul:htmlControlAttribute
                   		attributeEntry="${documentAttributes.creditMemoAmount}" property="document.creditMemoAmount"
                   		tabindexOverride="${tabindexOverrideBase + 0}"/>
                </td>
                <th class="right">
                   **<kul:htmlAttributeLabel  attributeEntry="${documentAttributes.vendorNumber}" />
                </th>
                <td class="datacell">
                   <kul:htmlControlAttribute
                   		attributeEntry="${documentAttributes.vendorNumber}" property="document.vendorNumber"
                   		tabindexOverride="${tabindexOverrideBase + 5}"/>
                </td>
            </tr>
		</table>
    </div>
</kul:tabTop>
