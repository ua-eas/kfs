<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
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
<c:set var="documentAttributes" value="${DataDictionary.TravelAuthorizationDocument.attributes}" />
<c:set var="travelerAttributes" value="${DataDictionary.TravelerDetail.attributes}" />
<c:set var="docType" value="${KualiForm.document.dataDictionaryEntry.documentTypeName}"/>
<c:set var="isTR" value="${KualiForm.docTypeName == TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT}" />

<c:set var="tabindexOverrideBase" value="8" />
	<c:if test="${isTR && delinquent != null}"><div style="color:red;">**Delinquent Travel Reimbursement**</div></c:if>	
	<h3>Traveler Section</h3>
	<table cellpadding="0" cellspacing="0" class="datatable" summary="Traveler Section">
     	<c:if test="${fullEntryMode && travelArranger}">
        <tr>
            <th class="right">Traveler Lookup:</th>
            <td class="datacell" colspan="3">
                <kul:lookup boClassName="org.kuali.kfs.module.tem.businessobject.TravelerProfileForLookup" lookupParameters="document.traveler.travelerTypeCode:travelerTypeCode" />
            </td>
        </tr>
        </c:if>
		<tr>
            <th class="right">
                <kul:htmlAttributeLabel attributeEntry="${travelerAttributes.travelerTypeCode}" />
            </th>
            <td class="datacell" colspan="3">
                <kul:htmlControlAttribute attributeEntry="${DataDictionary.TravelerType.attributes.name}" property="document.traveler.travelerType.name" readOnly="true"/>
            </td>
       </tr>
       <c:if test="${KualiForm.document.traveler.travelerTypeCode == 'EMP'}">
       <tr>
			<th class="right">
                <kul:htmlAttributeLabel attributeEntry="${travelerAttributes.principalId}" />
			</th>
			<td class="datacell">
				<kul:htmlControlAttribute attributeEntry="${travelerAttributes.principalId}" property="document.traveler.principalId" readOnly="true"/>
			</td>
            <th class="right">
                <kul:htmlAttributeLabel attributeEntry="${travelerAttributes.principalName}" readOnly="true" />
            </th>
            <td class="datacell">
            	<kul:htmlControlAttribute attributeEntry="${travelerAttributes.principalName}" property="document.traveler.principalName" readOnly="true"/>
            </td>
		</tr>
        </c:if>
		<tr>
            <th class="right">
                <kul:htmlAttributeLabel attributeEntry="${travelerAttributes.firstName}" />
            </th>
            <td class="datacell">
                <kul:htmlControlAttribute attributeEntry="${travelerAttributes.firstName}" property="document.traveler.firstName" readOnly="true"/>           
            </td>
            <th class="right">
                <kul:htmlAttributeLabel attributeEntry="${travelerAttributes.lastName}" />
            </th>
            <td class="datacell">
                <kul:htmlControlAttribute attributeEntry="${travelerAttributes.lastName}" property="document.traveler.lastName" readOnly="true"/>          
            </td>
        </tr> 
        <c:if test="${fullEntryMode}">
        <tr>
            <th class="right">Address Lookup:</th>
            <td class="datacell" colspan="3">
                <kul:lookup boClassName="org.kuali.kfs.module.tem.businessobject.TemProfileAddress" lookupParameters="document.traveler.principalId:principalId,document.traveler.customerNumber:customerNumber"
			    	fieldConversions="streetAddressLine1:document.traveler.streetAddressLine1,streetAddressLine2:document.traveler.streetAddressLine2,zipCode:document.traveler.zipCode,countryCode:document.traveler.countryCode,stateCode:document.traveler.stateCode,cityName:document.traveler.cityName" />
            </td>
        </tr>
        </c:if>       
        <tr>
            <th class="right">
                <kul:htmlAttributeLabel attributeEntry="${travelerAttributes.streetAddressLine1}" />
            </th>
            <td class="datacell">
                <kul:htmlControlAttribute attributeEntry="${travelerAttributes.streetAddressLine1}" property="document.traveler.streetAddressLine1" readOnly="true"/>           
            </td>
            <th class="right">
                <kul:htmlAttributeLabel attributeEntry="${travelerAttributes.streetAddressLine2}" />
            </th>
            <td class="datacell">
                <kul:htmlControlAttribute attributeEntry="${travelerAttributes.streetAddressLine2}" property="document.traveler.streetAddressLine2" readOnly="true"/>            
            </td>
        </tr>
        <tr>
            <th class="right">
                <kul:htmlAttributeLabel attributeEntry="${travelerAttributes.cityName}" />
            </th>
            <td class="datacell">
                <kul:htmlControlAttribute attributeEntry="${travelerAttributes.cityName}" property="document.traveler.cityName" readOnly="true"/>           
            </td>
            <th class="right">
                <kul:htmlAttributeLabel attributeEntry="${travelerAttributes.stateCode}" />
            </th>
            <td class="datacell">
                <kul:htmlControlAttribute attributeEntry="${travelerAttributes.stateCode}" property="document.traveler.stateCode" readOnly="true" tabindexOverride="${tabindexOverrideBase + 5}"/>
            </td>
        </tr>
        <tr>
            <th class="right">
                <kul:htmlAttributeLabel attributeEntry="${travelerAttributes.countryCode}" />
            </th>
            <td class="datacell">
                <kul:htmlControlAttribute attributeEntry="${travelerAttributes.countryCode}" property="document.traveler.countryCode" readOnly="true" onchange="javascript: getAllStates();" tabindexOverride="${tabindexOverrideBase + 4}"/>           
            </td>
            <th class="right">
                <kul:htmlAttributeLabel attributeEntry="${travelerAttributes.zipCode}" />
            </th>
            <td class="datacell">
                <kul:htmlControlAttribute attributeEntry="${travelerAttributes.zipCode}" property="document.traveler.zipCode" readOnly="true"/>
				<c:if test="${!readOnly  && fullEntryMode}">
              		<kul:lookup boClassName="org.kuali.rice.location.framework.postalcode.PostalCodeEbo" fieldConversions="code:document.traveler.zipCode,countryCode:document.traveler.countryCode,stateCode:document.traveler.stateCode,cityName:document.traveler.cityName" 
              		lookupParameters="document.traveler.countryCode:countryCode,document.traveler.zipCode:code,document.traveler.stateCode:stateCode,document.traveler.cityName:cityName" />
              	</c:if>
            </td>
        </tr>
        <tr>
            <th class="right">
                <kul:htmlAttributeLabel attributeEntry="${travelerAttributes.emailAddress}" />
            </th>
            <td class="datacell">
                <kul:htmlControlAttribute attributeEntry="${travelerAttributes.emailAddress}" property="document.traveler.emailAddress" readOnly="true"/>            
            </td>
            <th class="right">
            	<kul:htmlAttributeLabel attributeEntry="${travelerAttributes.phoneNumber}" />
            </th>
            <td class="datacell">
                <kul:htmlControlAttribute attributeEntry="${travelerAttributes.phoneNumber}" property="document.traveler.phoneNumber" readOnly="true"/>           
            </td>
        </tr>
        <tr>
            <th class="right">
                <kul:htmlAttributeLabel attributeEntry="${travelerAttributes.liabilityInsurance}" />
            </th>
            <td class="datacell" colspan="3">
                <kul:htmlControlAttribute attributeEntry="${travelerAttributes.liabilityInsurance}" property="document.traveler.liabilityInsurance" readOnly="${!fullEntryMode}"/>
            </td>
        </tr>                      
	</table>
