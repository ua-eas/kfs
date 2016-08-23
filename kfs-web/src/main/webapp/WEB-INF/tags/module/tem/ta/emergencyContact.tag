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

<kul:tab tabTitle="${TemConstants.TabTitles.EMERGENCY_CONTACT_INFORMATION_TAB_TITLE}" defaultOpen="${KualiForm.document.emergencyContactDefaultOpen}" tabErrorKey="${TemKeyConstants.TRVL_AUTH_EMERGENCY_CONTACT_ERRORS}">
	<div class="tab-container" align="center">
		<c:set var="documentAttributes" value="${DataDictionary.TravelAuthorizationDocument.attributes}" />
		<c:set var="travelerAttributes" value="${DataDictionary.TravelerDetail.attributes}" />
		<c:set var="emergencyContactAttributes" value="${DataDictionary.TravelerDetailEmergencyContact.attributes}" />
		<c:set var="contactRelationTypeAttributes" value="${DataDictionary.ContactRelationType.attributes}" />

        <table cellpadding="0" cellspacing="0" class="datatable" summary="Emergency Contact Section">
            <tr>
                <th class="right">
                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.cellPhoneNumber}" />
                </th>
                <td class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.cellPhoneNumber}" property="document.cellPhoneNumber" readOnly="${!fullEntryMode}" />
                </td>
            </tr>
            <tr>
                <th class="right">
                    <kul:htmlAttributeLabel attributeEntry="${travelerAttributes.citizenship}" useShortLabel="true" />
                </th>
                <td class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${travelerAttributes.citizenship}" property="document.traveler.citizenship" readOnly="true" />
                </td>
            </tr>
        </table>

        <h3>Emergency Contact(s) for Travelers</h3>
        <table cellpadding="0" class="datatable standard items" summary="Emergency Contacts">
            <tbody>
                <tr class="header">
                    <th width="10">&nbsp;</th>
                    <th>
                        <kul:htmlAttributeLabel attributeEntry="${emergencyContactAttributes.contactRelationTypeCode}" useShortLabel="true" noColon="${true}"/>
                    </th>
                    <th>
                        <kul:htmlAttributeLabel attributeEntry="${emergencyContactAttributes.contactName}" noColon="${true}" />
                    </th>
                    <th>
                        <kul:htmlAttributeLabel attributeEntry="${emergencyContactAttributes.phoneNumber}" noColon="${true}" />
                    </th>
                    <th>
                        <kul:htmlAttributeLabel attributeEntry="${emergencyContactAttributes.emailAddress}" noColon="${true}" />
                    </th>
                    <th>
                        <kul:htmlAttributeLabel attributeEntry="${emergencyContactAttributes.primary}" noColon="${true}" />
                    </th>
                    <c:if test="${fullEntryMode}">
                        <th>Actions</th>
                    </c:if>
                </tr>
                <c:if test="${fullEntryMode}">
                    <tr class="highlight">
                        <th scope="row">&nbsp;</th>
                        <td>
                            <kul:htmlControlAttribute
                                    attributeEntry="${emergencyContactAttributes.contactRelationTypeCode}"
                                    property="newEmergencyContactLine.contactRelationTypeCode"
                                    readOnly="false" />
                        </td>
                        <td class="infoline">
                            <kul:htmlControlAttribute
                                    attributeEntry="${emergencyContactAttributes.contactName}"
                                    property="newEmergencyContactLine.contactName"
                                    readOnly="false" />
                        </td>
                        <td class="infoline">
                            <kul:htmlControlAttribute
                                    attributeEntry="${emergencyContactAttributes.phoneNumber}"
                                    property="newEmergencyContactLine.phoneNumber"
                                    readOnly="false" />
                        </td>
                        <td class="infoline">
                            <kul:htmlControlAttribute
                                    attributeEntry="${emergencyContactAttributes.emailAddress}"
                                    property="newEmergencyContactLine.emailAddress"
                                    readOnly="false" />
                        </td>
                        <td class="infoline">
                            <kul:htmlControlAttribute
                                    attributeEntry="${emergencyContactAttributes.primary}"
                                    property="newEmergencyContactLine.primary"
                                    readOnly="false" />
                        </td>
                        <td class="infoline">
                                <html:submit
                                        styleClass="btn btn-green"
                                        property="methodToCall.addEmergencyContactLine"
                                        alt="Add Emergency Contact Line"
                                        title="Add Emergency Contact Line"
                                        value="Add"/>
                        </td>
                    </tr>
                </c:if>

                <c:if test="${KualiForm.document.traveler.emergencyContacts != null && !empty KualiForm.document.traveler.emergencyContacts}">
                    <logic:iterate indexId="ctr" name="KualiForm" property="document.traveler.emergencyContacts" id="currentLine">
                        <tr class="${(ctr + 1) % 2 == 0 ? "highlight" : ""}">
                            <th>
                                <kul:htmlControlAttribute
                                        attributeEntry="${emergencyContactAttributes.financialDocumentLineNumber}"
                                        property="document.traveler.emergencyContacts[${ctr}].financialDocumentLineNumber"
                                        readOnly="true" />
                            </th>
                            <td>
                                <kul:htmlControlAttribute
                                        attributeEntry="${emergencyContactAttributes.contactRelationTypeCode}"
                                        property="document.traveler.emergencyContacts[${ctr}].contactRelationTypeCode"
                                        readOnly="${!fullEntryMode}" />
                            </td>
                            <td>
                                <kul:htmlControlAttribute
                                        attributeEntry="${emergencyContactAttributes.contactName}"
                                        property="document.traveler.emergencyContacts[${ctr}].contactName"
                                        readOnly="${!fullEntryMode}" />
                            </td>
                            <td>
                                <kul:htmlControlAttribute
                                        attributeEntry="${emergencyContactAttributes.phoneNumber}"
                                        property="document.traveler.emergencyContacts[${ctr}].phoneNumber"
                                        readOnly="${!fullEntryMode}" />
                            </td>
                            <td>
                                <kul:htmlControlAttribute
                                        attributeEntry="${emergencyContactAttributes.emailAddress}"
                                        property="document.traveler.emergencyContacts[${ctr}].emailAddress"
                                        readOnly="${!fullEntryMode}" />
                            </td>
                            <td>
                                <kul:htmlControlAttribute
                                        attributeEntry="${emergencyContactAttributes.primary}"
                                        property="document.traveler.emergencyContacts[${ctr}].primary"
                                        readOnly="${!fullEntryMode}" />
                            </td>
                            <c:if test="${fullEntryMode}">
                                <td>
                                    <html:submit
                                            styleClass="btn btn-red"
                                            property="methodToCall.deleteEmergencyContactLine.line${ctr}"
                                            alt="Delete Emergency Contact Line"
                                            title="Delete Emergency Contact Line"
                                            value="Delete"/>
                                </td>
                            </c:if>
                        </tr>
                    </logic:iterate>
                </c:if>
            </tbody>
        </table>

        <h3>Modes of Transportation while out-of-country</h3>
        <table cellpadding="0" class="datatable standard" summary="Modes of Transportation" style="width:90%;">
            <tbody>
                <logic:iterate indexId="ctr2" name="KualiForm" property="modesOfTransportation" id="currentMode">
                    <c:if test="${ctr2 == 0 || ctr2%5 == 0}">
                        <tr>
                    </c:if>
                    <td>
                        <tem-html:multibox property="selectedTransportationModes" disabled="${!fullEntryMode}">
                            <bean:write name="currentMode" property="key" />
                        </tem-html:multibox> <bean:write name="currentMode" property="value" />
                    </td>

                    <c:if test="${ctr2 == 4 || ctr2 == 9}">
                        </tr>
                    </c:if>
                </logic:iterate>
            </tbody>
        </table>
        <table>
            <tr>
                <th class="right top">
                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.regionFamiliarity}" />
                </th>
                <td class="datacell">
                    <kul:htmlControlAttribute
                            attributeEntry="${documentAttributes.regionFamiliarity}"
                            property="document.regionFamiliarity"
                            readOnly="${!fullEntryMode}" />
                </td>
            </tr>
        </table>
	</div>
</kul:tab>
