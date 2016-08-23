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

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>

<kul:tabTop tabTitle="Line Item Receiving Initiation" defaultOpen="true" tabErrorKey="${PurapConstants.RECEIVING_LINE_INIT_TAB_ERRORS}">
    <div class="tab-container" align=center>
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Receiving Line Init Section">
            <tr>
                <th class="right" style="width:50%;">
                   <kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderIdentifier}" />
                </th>
                <td class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.purchaseOrderIdentifier}" property="document.purchaseOrderIdentifier" readOnly="${KualiForm.fromPurchaseOrder}" />
                </td>
            </tr>
            <tr>
                <th class="right">
                   <kul:htmlAttributeLabel attributeEntry="${documentAttributes.shipmentReceivedDate}" />
                </th>
                <td class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.shipmentReceivedDate}" property="document.shipmentReceivedDate" datePicker="true" />
                </td>
            </tr>
            <tr>
                <th class="right">
                   <kul:htmlAttributeLabel attributeEntry="${documentAttributes.shipmentPackingSlipNumber}" />
                </th>
                <td class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.shipmentPackingSlipNumber}" property="document.shipmentPackingSlipNumber" />
                </td>
            </tr>
            <tr>
                <th class="right">
                   <kul:htmlAttributeLabel attributeEntry="${documentAttributes.shipmentBillOfLadingNumber}" />
                </th>
                <td class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.shipmentBillOfLadingNumber}" property="document.shipmentBillOfLadingNumber" />
                </td>
            </tr>
            <tr>
                <th class="right">
                   <kul:htmlAttributeLabel attributeEntry="${documentAttributes.carrierCode}" />
                </th>
                <td class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.carrierCode}" property="document.carrierCode" />
                </td>
            </tr>
		</table>
    </div>

</kul:tabTop>
