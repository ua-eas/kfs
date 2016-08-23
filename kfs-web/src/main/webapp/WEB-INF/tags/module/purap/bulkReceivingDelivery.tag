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
<%@ attribute name="deliveryReadOnly" required="false"
              description="Boolean to indicate if delivery tab fields are read only" %>

<c:set var="fullEntryMode" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
<c:set var="notOtherDeliveryBuilding" value="${not KualiForm.document.deliveryBuildingOtherIndicator}" />
<c:set var="tabindexOverrideBase" value="20" />

<kul:tab tabTitle="Delivery" defaultOpen="true" tabErrorKey="${PurapConstants.BULK_RECEIVING_DELIVERY_TAB_ERRORS}">
    <div class="tab-container">
        <table class="standard" summary="Delivery Section">
        <%-- If PO available, display the delivery information from the PO --%>
        	<c:if test="${isPOAvailable}">
	        	<tr>
	        		<th class="right top">
	                   <kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryBuildingName}" />
	                </th>
	                <td>
	                	<kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryToName}" property="document.deliveryToName" readOnly="true" /><br>
	                	<kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryBuildingName}" property="document.deliveryBuildingName" readOnly="true" /><br>
	                   	<kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryBuildingLine1Address}" property="document.deliveryBuildingLine1Address" readOnly="true" />&nbsp;
	                   	<c:if test="${! empty KualiForm.document.deliveryBuildingLine2Address}">
	                   		<kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryBuildingLine2Address}" property="document.deliveryBuildingLine2Address" readOnly="true" />,&nbsp;
	                   	</c:if>
		            	<c:out value="Room "/><kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryBuildingRoomNumber}" property="document.deliveryBuildingRoomNumber" readOnly="true" /><br>
	                   	<c:if test="${! empty KualiForm.document.deliveryCityName}">
	    	           		<kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryCityName}" property="document.deliveryCityName" readOnly="true" />,&nbsp;
	                   	</c:if>
	                   	<c:if test="${! empty KualiForm.document.deliveryStateCode}">
		                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryStateCode}" property="document.deliveryStateCode" readOnly="true" />&nbsp;
	                  	</c:if>
	                   	<c:if test="${! empty KualiForm.document.deliveryPostalCode}">
		                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryPostalCode}" property="document.deliveryPostalCode" readOnly="true" />
	                   	</c:if>
	                   	<c:if test="${! empty KualiForm.document.deliveryCountryCode}">
		                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryCountryCode}" property="document.deliveryCountryCode" readOnly="true" />
	                   	</c:if>
	            	</td>
	                <th class="right">
	                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryInstructionText}"/>
	                </th>
	                <td>
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryInstructionText}"
	                    	property="document.deliveryInstructionText" readOnly="${true}"/>
	                </td>
	            </tr>
	            <tr>
	            	<th class="right">
	                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.preparerPersonName}" />
	                </th>
	                <td>
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.preparerPersonName}"
	                    	property="document.preparerPersonName" readOnly="${true}"/>
	                </td>
	                <th class="right top" rowspan="4">
	                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryAdditionalInstructionText}"/>
	                </th>
	                <td>
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryAdditionalInstructionText}"
	                    	property="document.deliveryAdditionalInstructionText" readOnly="${not (fullEntryMode)}" tabindexOverride="${tabindexOverrideBase + 5}"/>
	                </td>
	            </tr>
				<tr>
		            <th class="right">
	                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.requestorPersonName}"/>
	                </th>
	                <td>
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.requestorPersonName}"
	                    	property="document.requestorPersonName" readOnly="${true}"/>
	                </td>
	            </tr>
        		<tr>
        			<th class="right">
	                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.requestorPersonPhoneNumber}"/>
	                </th>
	                <td>
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.requestorPersonPhoneNumber}"
	                    	property="document.requestorPersonPhoneNumber" readOnly="${true}"/>
	                </td>
	            </tr>
	            <tr>
        			<th class="right">
	                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.institutionContactName}"/>
	                </th>
	                <td>
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.institutionContactName}"
	                    	property="document.institutionContactName" readOnly="${true}"/>
	                </td>
	            </tr>
	            <tr>
        			<th class="right">
	                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.institutionContactPhoneNumber}"/>
	                </th>
	                <td>
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.institutionContactPhoneNumber}"
	                    	property="document.institutionContactPhoneNumber" readOnly="${true}"/>
	                </td>
	                <th class="right">
	                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryCampusName}" />
	                </th>
	                <td>
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryCampusName}" property="document.deliveryCampus.campus.name" readOnly="true" />
               	 	</td>
	            </tr>
	            <tr>
        			<th class="right">
	                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.institutionContactEmailAddress}"/>
	                </th>
	                <td>
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.institutionContactEmailAddress}"
	                    	property="document.institutionContactEmailAddress" readOnly="${true}"/>
	                </td>
	                <th class="right">
	                </th>
	                <td>
	                </td>
	            </tr>

        	</c:if>


        	<%-- If PO not available --%>

        	<c:if test="${!isPOAvailable}">

	            <tr>
                    <th class="right">
                        <kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryCampusCode}" />
                    </th>
                    <td>
                        <kul:htmlControlAttribute
                            attributeEntry="${documentAttributes.deliveryCampusCode}"
                            property="document.deliveryCampusCode"
                            readOnly="true"/>
	                    <c:if test="${fullEntryMode}">
	                        <kul:lookup boClassName="org.kuali.kfs.vnd.businessobject.CampusParameter"
	                            lookupParameters="document.deliveryCampusCode:campusCode"
	                            fieldConversions="campusCode:document.deliveryCampusCode"/>
	                    </c:if>
                    </td>
	                <th class="right">
	                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryToName}"/>
	                </th>
	                <td>
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryToName}"
	                    	property="document.deliveryToName" readOnly="${not (fullEntryMode)}" tabindexOverride="${tabindexOverrideBase + 5}"/>
	                    <c:if test="${fullEntryMode}">
	                        <kul:lookup boClassName="org.kuali.rice.kim.api.identity.Person"
	                        	fieldConversions="name:document.deliveryToName,emailAddress:document.deliveryToEmailAddress,phoneNumber:document.deliveryToPhoneNumber"/>
	                    </c:if>
	                </td>
	            </tr>
	            <tr>
                    <th class="right">
                        <kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryBuildingName}"/>
                    </th>
                    <td>
                        <kul:htmlControlAttribute
                            attributeEntry="${documentAttributes.deliveryBuildingName}"
                            property="document.deliveryBuildingName"
                            readOnly="true"/>&nbsp;
                        <c:if test="${fullEntryMode}">
                            <kul:lookup boClassName="org.kuali.kfs.sys.businessobject.Building"
                                lookupParameters="document.deliveryCampus:campusCode"
                                fieldConversions="buildingCode:document.deliveryBuildingCode,buildingName:document.deliveryBuildingName,campusCode:document.deliveryCampusCode,buildingStreetAddress:document.deliveryBuildingLine1Address,buildingAddressCityName:document.deliveryCityName,buildingAddressStateCode:document.deliveryStateCode,buildingAddressZipCode:document.deliveryPostalCode,buildingAddressCountryCode:document.deliveryCountryCode"/>&nbsp;&nbsp;
                            <html:submit
									property="methodToCall.useOtherDeliveryBuilding"
									alt="building not found"
									styleClass="btn btn-default small"
									value="Building Not Found"/>
                        </c:if>
                    </td>
	                <th class="right">
	                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryToPhoneNumber}"/>
	                </th>
	                <td>
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryToPhoneNumber}"
	                    	property="document.deliveryToPhoneNumber" readOnly="${not (fullEntryMode)}" tabindexOverride="${tabindexOverrideBase + 5}"/>
	                </td>
	            </tr>

				<tr>
					<th class="right">
	                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryBuildingLine1Address}"/>
	                </th>
	                <td>
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryBuildingLine1Address}"
	                    	property="document.deliveryBuildingLine1Address"  readOnly="${notOtherDeliveryBuilding or not fullEntryMode}" tabindexOverride="${tabindexOverrideBase + 0}"/>
	                </td>
	                <th class="right">
	                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryToEmailAddress}"/>
	                </th>
	                <td>
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryToEmailAddress}"
	                    	property="document.deliveryToEmailAddress" readOnly="${not (fullEntryMode)}" tabindexOverride="${tabindexOverrideBase + 5}"/>
	                </td>
				</tr>

				<tr>
					<th class="right">
	                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryBuildingLine2Address}"/>
	                </th>
	                <td>
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryBuildingLine2Address}"
	                    	property="document.deliveryBuildingLine2Address" readOnly="${not (fullEntryMode)}" tabindexOverride="${tabindexOverrideBase + 0}"/>
	                </td>
	                <th class="right" rowspan="3">
	                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryInstructionText}"/>
	                </th>
	                <td  rowspan="3">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryInstructionText}"
	                    	property="document.deliveryInstructionText" readOnly="${not (fullEntryMode)}" tabindexOverride="${tabindexOverrideBase + 5}"/>
	                </td>
				</tr>
				<tr>
					<th class="right">
	                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryBuildingRoomNumber}"/>
	                </th>
	                <td>
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryBuildingRoomNumber}"
	                    	property="document.deliveryBuildingRoomNumber" readOnly="${not (fullEntryMode)}" tabindexOverride="${tabindexOverrideBase + 0}"/>
	                </td>
				</tr>

				<tr>
					<th class="right">
	                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryCityName}"/>
	                </th>
	                <td>
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryCityName}"
	                    	property="document.deliveryCityName" readOnly="${notOtherDeliveryBuilding or not fullEntryMode}" tabindexOverride="${tabindexOverrideBase + 0}"/>
	                </td>
	            </tr>
	            <tr>
					<th class="right">
	                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryStateCode}"/>
	                </th>
	                <td>
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryStateCode}"
	                    	property="document.deliveryStateCode" readOnly="${notOtherDeliveryBuilding or not fullEntryMode}" tabindexOverride="${tabindexOverrideBase + 0}"/>
	                </td>
	                <th class="right">&nbsp;</th>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<th class="right">
	                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryPostalCode}"/>
	                </th>
	                <td>
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryPostalCode}"
	                    	property="document.deliveryPostalCode" readOnly="${notOtherDeliveryBuilding or not fullEntryMode}" tabindexOverride="${tabindexOverrideBase + 0}"/>
	                </td>
                    <th class="right">&nbsp;</th>
                    <td>&nbsp;</td>
				</tr>
                <tr>
                    <th class="right">
                        <kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryCountryCode}"/>
                    </th>
                    <td>
                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryCountryCode}"
                            property="document.deliveryCountryCode"
                            extraReadOnlyProperty="document.deliveryCountryName"
                            readOnly="${notOtherDeliveryBuilding or not fullEntryMode}" tabindexOverride="${tabindexOverrideBase + 0}"/>
                    </td>
                    <th class="right">&nbsp;</th>
                    <td>&nbsp;</td>
                </tr>

				<tr>
    	            <td colspan="4" class="subhead">Additional</td>
	            </tr>

				<tr>
					<th class="right">
                    	<kul:htmlAttributeLabel attributeEntry="${documentAttributes.institutionContactName}" />
                 	</th>
                	<td>
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.institutionContactName}" property="document.institutionContactName" readOnly="${not (fullEntryMode)}" tabindexOverride="${tabindexOverrideBase + 0}"/>
	                    <c:if test="${(fullEntryMode)}" >
	                        <kul:lookup boClassName="org.kuali.rice.kim.api.identity.Person" fieldConversions="name:document.institutionContactName,phoneNumber:document.institutionContactPhoneNumber,emailAddress:document.institutionContactEmailAddress" />
	                    </c:if>
                	</td>
                	<th class="right" rowspan="4">
	                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryAdditionalInstructionText}"/>
	                </th>
	                <td  rowspan="4">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryAdditionalInstructionText}"
	                    	property="document.deliveryAdditionalInstructionText" readOnly="${not (fullEntryMode)}" tabindexOverride="${tabindexOverrideBase + 5}"/>
	                </td>
				</tr>
				<tr>
					<th class="right">
	                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.institutionContactPhoneNumber}" />
	                </th>
	                <td>
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.institutionContactPhoneNumber}" property="document.institutionContactPhoneNumber" readOnly="${not (fullEntryMode)}" tabindexOverride="${tabindexOverrideBase + 0}"/>
	                </td>
				</tr>
				<tr>
					<th class="right">
	                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.institutionContactEmailAddress}" />
	                </th>
	                <td>
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.institutionContactEmailAddress}" property="document.institutionContactEmailAddress" readOnly="${not (fullEntryMode)}" tabindexOverride="${tabindexOverrideBase + 0}"/>
	                </td>
				</tr>
			</c:if>
        </table>
	</div>
</kul:tab>

