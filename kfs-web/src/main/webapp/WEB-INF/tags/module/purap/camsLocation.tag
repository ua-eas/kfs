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
<%@ attribute name="camsLocationAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="ctr" required="true" description="item count"%>
<%@ attribute name="ctr2" required="true" description="item count"%>
<%@ attribute name="camsAssetLocationProperty" required="true" description="String that represents the prefix of the property name to store into the document on the form."%>
<%@ attribute name="availability" required="true" description="Determines if this is a capture once tag or for each"%>
<%@ attribute name="poItemInactive" required="false" description="True if the PO item this is a part of is inactive"%>
<%@ attribute name="fullEntryMode" required="true" description="Determines if the asset information should editable." %>

<c:if test="${empty availability}">
    <c:set var="availability" value="${PurapConstants.CapitalAssetAvailability.EACH}"/>
</c:if>

<c:set var="offCampus" value="false" />
<logic:equal name="KualiForm" property="${camsAssetLocationProperty}.offCampusIndicator" value="Yes">
    <c:set var="offCampus" value="true" />
</logic:equal>

<c:set var="deleteLocationUrl" value="methodToCall.deleteCapitalAssetLocationByItem.(((${ctr}))).((#${ctr2}#))" />
<c:set var="refreshAssetLocationBuildingUrl" value="methodToCall.useOffCampusAssetLocationBuildingByItem.(((${ctr}))).((#${ctr2}#))" />
<c:if test="${PurapConstants.CapitalAssetAvailability.ONCE eq availability}">
    <c:set var="deleteLocationUrl" value="methodToCall.deleteCapitalAssetLocationByDocument.(((${ctr}))).((#${ctr2}#))" />
    <c:set var="refreshAssetLocationBuildingUrl" value="methodToCall.useOffCampusAssetLocationBuildingByDocument.(((${ctr}))).((#${ctr2}#))" />
</c:if>
<c:set var="tabindexOverrideBase" value="60" />

<table class="datatable" summary="" border="0" cellpadding="0" cellspacing="0" style="width:100%">
<tr>
    <td colspan="4" class="subhead">
        <span class="left">Location</span>
        <c:if test="${(fullEntryMode or amendmentEntry) and !poItemInactive and !(ctr2 eq 'new')}">
	        <span class="right">
	            <html:image property="${deleteLocationUrl}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" alt="Delete a Asset Location" title="Delete a Asset Location" styleClass="tinybutton" />
	        </span>
        </c:if>
    </td>
</tr>
<tr>
    <th width="20%" align=right valign=middle class="bord-l-b">
       <div align="right"><kul:htmlAttributeLabel attributeEntry="${camsLocationAttributes.itemQuantity}" /></div>
    </th>
    <td class="datacell">
        <kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.itemQuantity}" property="${camsAssetLocationProperty}.itemQuantity" readOnly="${!(fullEntryMode or amendmentEntry) or !(ctr2 eq 'new') or poItemInactive}" tabindexOverride="${tabindexOverrideBase + 5}"/>        
    </td>
    <th>&nbsp;</th>
    <td class="datacell">&nbsp;</td>
</tr>
<tr>
    <th width="20%" align=right valign=middle class="bord-l-b">
       <div align="right"><kul:htmlAttributeLabel attributeEntry="${camsLocationAttributes.campusCode}" /></div>
    </th>
    <td class="datacell">
        <kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.campusCode}" 
            property="${camsAssetLocationProperty}.campusCode" readOnly="true"/>&nbsp;
        <c:if test="${(fullEntryMode or amendmentEntry) && !poItemInactive && (ctr2 eq 'new')}">        
            <kul:lookup boClassName="org.kuali.kfs.vnd.businessobject.CampusParameter" fieldConversions="campusCode:${camsAssetLocationProperty}.campusCode"/>
        </c:if>
    </td>
    <th width="20%" align=right valign=middle class="bord-l-b">
       <div align="right"><kul:htmlAttributeLabel attributeEntry="${camsLocationAttributes.capitalAssetCityName}" /></div>
    </th>
    <td class="datacell">
        <kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.capitalAssetCityName}" property="${camsAssetLocationProperty}.capitalAssetCityName" readOnly="${!offCampus or !(fullEntryMode or amendmentEntry) or !(ctr2 eq 'new') or poItemInactive}" tabindexOverride="${tabindexOverrideBase + 7}"/>        
    </td>
</tr>
<tr>
    <th width="20%" align=right valign=middle class="bord-l-b">
       <div align="right"><kul:htmlAttributeLabel attributeEntry="${camsLocationAttributes.buildingCode}" /></div>
    </th>
    <td class="datacell">
        <kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.buildingCode}" 
            property="${camsAssetLocationProperty}.buildingCode" readOnly="true"/>&nbsp;
        <c:if test="${(fullEntryMode or amendmentEntry) and !poItemInactive and (ctr2 eq 'new')}">        
            <kul:lookup boClassName="org.kuali.kfs.sys.businessobject.Building"
                lookupParameters="${camsAssetLocationProperty}.campusCode:campusCode" 
	        	fieldConversions="buildingCode:${camsAssetLocationProperty}.buildingCode,campusCode:${camsAssetLocationProperty}.campusCode"
                anchor="${currentTabIndex}"/>&nbsp;&nbsp;
            <html:image property="${refreshAssetLocationBuildingUrl}" src="${ConfigProperties.externalizable.images.url}tinybutton-offcampus.gif" alt="building not found" styleClass="tinybutton"/>
        </c:if>
    </td>
    <th width="20%" align=right valign=middle class="bord-l-b">
       <div align="right"><kul:htmlAttributeLabel attributeEntry="${camsLocationAttributes.capitalAssetStateCode}" /></div>
    </th>
    <td class="datacell">
        <kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.capitalAssetStateCode}" property="${camsAssetLocationProperty}.capitalAssetStateCode" readOnly="${!offCampus or !(fullEntryMode or amendmentEntry) or !(ctr2 eq 'new') or poItemInactive}" tabindexOverride="${tabindexOverrideBase + 7}"/>
    </td>
</tr>
<tr>
    <th width="20%" align=right valign=middle class="bord-l-b">
       <div align="right"><kul:htmlAttributeLabel attributeEntry="${camsLocationAttributes.capitalAssetLine1Address}" /></div>
    </th>
    <td class="datacell">
        <kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.capitalAssetLine1Address}" property="${camsAssetLocationProperty}.capitalAssetLine1Address" readOnly="${!offCampus or !(fullEntryMode or amendmentEntry) or !(ctr2 eq 'new') or poItemInactive}" tabindexOverride="${tabindexOverrideBase + 5}"/>
    </td>
    <th width="20%" align=right valign=middle class="bord-l-b">
       <div align="right"><kul:htmlAttributeLabel attributeEntry="${camsLocationAttributes.capitalAssetPostalCode}" /></div>
    </th>
    <td class="datacell">
        <kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.capitalAssetPostalCode}" property="${camsAssetLocationProperty}.capitalAssetPostalCode" readOnly="${!offCampus or !(fullEntryMode or amendmentEntry) or !(ctr2 eq 'new') or poItemInactive}" tabindexOverride="${tabindexOverrideBase + 7}"/>
    </td>
</tr>
<tr>
<logic:notEmpty name="KualiForm" property="${camsAssetLocationProperty}.buildingCode">
    <c:set var="buildingSelected" value="true" />
</logic:notEmpty>
    <th width="20%" align=right valign=middle class="bord-l-b">
       <div align="right"><kul:htmlAttributeLabel attributeEntry="${camsLocationAttributes.buildingRoomNumber}" /></div>
    </th>
    <td class="datacell">
        <kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.buildingRoomNumber}" property="${camsAssetLocationProperty}.buildingRoomNumber" readOnly="${!(fullEntryMode or amendmentEntry) or !(ctr2 eq 'new') or poItemInactive}" tabindexOverride="${tabindexOverrideBase + 5}"/>&nbsp;
        <c:if test="${(fullEntryMode or amendmentEntry) && !poItemInactive && !offCampus && buildingSelected && (ctr2 eq 'new')}">
            <kul:lookup boClassName="org.kuali.kfs.sys.businessobject.Room" 
                readOnlyFields="buildingCode,campusCode"
                lookupParameters="'Y':active,${camsAssetLocationProperty}.campusCode:campusCode,${camsAssetLocationProperty}.buildingCode:buildingCode" 
                fieldConversions="buildingRoomNumber:${camsAssetLocationProperty}.buildingRoomNumber"/>
        </c:if>
    </td>
    <th width="20%" align=right valign=middle class="bord-l-b">
       <div align="right"><kul:htmlAttributeLabel attributeEntry="${camsLocationAttributes.capitalAssetCountryCode}" /></div>
    </th>
    <td class="datacell">
        <kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.capitalAssetCountryCode}" property="${camsAssetLocationProperty}.capitalAssetCountryCode" readOnly="${!offCampus or !(fullEntryMode or amendmentEntry) or !(ctr2 eq 'new') or poItemInactive}" tabindexOverride="${tabindexOverrideBase + 7}"/>
    </td>
</tr>

</tr>
<tr>
</tr>
</table>

