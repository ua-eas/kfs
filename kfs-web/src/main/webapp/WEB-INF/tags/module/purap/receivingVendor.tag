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

<c:set var="fullEntryMode" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
<c:set var="vendorReadOnly" value="${(not empty KualiForm.editingMode['lockVendorEntry'])}" />
<c:set var="tabindexOverrideBase" value="10" />

<kul:tab tabTitle="Vendor" defaultOpen="true" tabErrorKey="${PurapConstants.VENDOR_ERRORS}">
    <div class="tab-container">
        <table class="standard" summary="Vendor Section">
            <tr>
                <th class="right" width="25%">
                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorName}" />
                </th>
                <td class="datacell" width="25%">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorName}" property="document.vendorName" readOnly="${true}" />
                </td>
                <th class="right" width="25%">
                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorCityName}" />
                </th>
                <td class="datacell" width="25%">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorCityName}" property="document.vendorCityName" readOnly="${true}" />
                </td>
            </tr>

            <tr>
                <th class="right">
                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorNumber}" />
                </th>
                <td class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorNumber}" property="document.vendorNumber" readOnly="true" />
                </td>
                <th class="right">
                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorStateCode}" />
                    <br> *required for US
                </th>
                <td class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorStateCode}" property="document.vendorStateCode" readOnly="${true}" />
                </td>
            </tr>

            <tr>
                <th class="right">
                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorLine1Address}" />
                </th>
                <td class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorLine1Address}" property="document.vendorLine1Address" readOnly="${true}" />
                </td>
                <th class="right">
					<kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorPostalCode}" />
                    <br> *required for US
                </th>
                <td class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorPostalCode}" property="document.vendorPostalCode" readOnly="${true}" />
                </td>
            </tr>

            <tr>
                <th class="right">
                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorLine2Address}" />
                </th>
                <td class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorLine2Address}" property="document.vendorLine2Address" readOnly="${true}" />
                </td>
                <th class="right">
                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorCountryCode}" />
                </th>
				<td class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorCountryCode}" property="document.vendorCountryCode"
                    extraReadOnlyProperty="document.vendorCountry.name"
                    readOnly="${true}" />
				</td>
            </tr>

            <tr>
            	<th class="right">
    	        	<kul:htmlAttributeLabel attributeEntry="${documentAttributes.shipmentReceivedDate}" />
            	</th>
            	<td class="datacell">
	            	<kul:htmlControlAttribute attributeEntry="${documentAttributes.shipmentReceivedDate}" property="document.shipmentReceivedDate" readOnly="${vendorReadOnly || not(fullEntryMode)}" tabindexOverride="${tabindexOverrideBase + 0}"/>
            	</td>
            	<th class="right">&nbsp;</th>
            	<td class="datacell">&nbsp;</td>
            </tr>

            <tr>
            	<th class="right">
	            	<kul:htmlAttributeLabel attributeEntry="${documentAttributes.shipmentPackingSlipNumber}" />
            	</th>
            	<td class="datacell">
            		<kul:htmlControlAttribute attributeEntry="${documentAttributes.shipmentPackingSlipNumber}" property="document.shipmentPackingSlipNumber" readOnly="${vendorReadOnly || not(fullEntryMode)}" tabindexOverride="${tabindexOverrideBase + 0}"/>
            	</td>
            	<th class="right">
            		<kul:htmlAttributeLabel attributeEntry="${documentAttributes.shipmentReferenceNumber}" />
            	</th>
            	<td class="datacell">
            		<kul:htmlControlAttribute attributeEntry="${documentAttributes.shipmentReferenceNumber}" property="document.shipmentReferenceNumber" readOnly="${vendorReadOnly || not(fullEntryMode)}" tabindexOverride="${tabindexOverrideBase + 5}"/>
            	</td>
            </tr>

            <tr>
            	<th class="right">
	            	<kul:htmlAttributeLabel attributeEntry="${documentAttributes.shipmentBillOfLadingNumber}" />
            	</th>
            	<td class="datacell">
            		<kul:htmlControlAttribute attributeEntry="${documentAttributes.shipmentBillOfLadingNumber}" property="document.shipmentBillOfLadingNumber" readOnly="${vendorReadOnly || not(fullEntryMode)}" tabindexOverride="${tabindexOverrideBase + 0}"/>
            	</td>
            	<th class="right">
            		<kul:htmlAttributeLabel attributeEntry="${documentAttributes.carrierCode}" />
            	</th>
            	<td class="datacell">
            		<kul:htmlControlAttribute attributeEntry="${documentAttributes.carrierCode}" property="document.carrierCode" readOnly="${vendorReadOnly || not(fullEntryMode)}" tabindexOverride="${tabindexOverrideBase + 5}"/>
            	</td>
            </tr>
	    </table>
    </div>
</kul:tab>
