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

<c:set var="groupTravelerAttributes" value="${DataDictionary.GroupTraveler.attributes}" />
<c:set var="customerAttributes" value="${DataDictionary.Customer.attributes}" />

<kul:tab tabTitle="Group Travel" defaultOpen="false" tabErrorKey="${TemKeyConstants.TRVL_GROUP_TRVL_ERRORS}">
	<div class="tab-container" align=center>
		<table cellpadding="0" cellspacing="0" class="datatable items standard" summary="Group Traveler Section">
			<c:if test="${fullEntryMode}">
				<tr>
					<td colspan="7" class="tab-subhead import-lines" align="right" nowrap="nowrap">
						<SCRIPT type="text/javascript">
	                		<!--
	                  		function hideImport() {
	                      		document.getElementById("showLink").style.display="inline";
	                      		document.getElementById("uploadDiv").style.display="none";
	                  		}
	                  		function showImport() {
	                      		document.getElementById("showLink").style.display="none";
	                      		document.getElementById("uploadDiv").style.display="inline";
	                  		}
	                  		document.write(
	                    		'<a id="showLink" href="#" onclick="showImport();return false;">' +
	                      		'<button title="import items from file" class="btn btn-default" alt="import items from file">' +
                                        'Import Lines' +
                                '<\/button>' +
	                    		'<\/a>' +
	                    		'<div id="uploadDiv" style="display:none; float:right;" >' +
	                      		'<html:file size="30" property="groupTravelerImportFile" />' +
	                      		'<html:submit property="methodToCall.uploadGroupTravelerImportFile"
	                                    styleClass="btn btn-green" alt="add imported items" title="add imported items" value="Add"/>' +
	                      		'<html:submit property="methodToCall.cancel"
	                                    styleClass="btn btn-default" alt="cancel import" title="cancel import" onclick="hideImport();return false;" value="Cancel" />' +
	                    		'<\/div>');
	                		//-->
	            		</SCRIPT>
						<NOSCRIPT>
							Import lines
							<html:file size="30" property="groupTravelerImportFile" style="font:10px;height:16px;" />
							<html:submit property="methodToCall.uploadGroupTravelerImportFile" alt="add imported group traveler" title="add imported group traveler" styleClass="btn btn-green" value="Add" />
						</NOSCRIPT>
					</td>
				</tr>
                <tr class="header">
                    <th>&nbsp;</th>
                    <th><kul:htmlAttributeLabel attributeEntry="${groupTravelerAttributes.groupTravelerTypeCode}" noColon="${true}" /></th>
                    <th>
                        <div style="display:block;" id="personLabel"><kul:htmlAttributeLabel attributeEntry="${groupTravelerAttributes.groupTravelerEmpId}" noColon="${true}" /></div>
                        <div style="display:none;" id="customerLabel"><kul:htmlAttributeLabel attributeEntry="${customerAttributes.customerNumber}" noColon="${true}" /></div>
                    </th>
                    <th><kul:htmlAttributeLabel attributeEntry="${groupTravelerAttributes.name}" noColon="${true}" /></th>
                    <c:if test="${fullEntryMode}">
                        <th>Actions</th>
                    </c:if>
                </tr>
				<tr class="highlight">
                    <td>&nbsp;</td>
					<td class="datacell">
                        <kul:htmlControlAttribute
                                attributeEntry="${groupTravelerAttributes.groupTravelerTypeCode}"
                                property="newGroupTravelerLine.groupTravelerTypeCode"
                                readOnly="${!empty param['newGroupTravelerLine.groupTravelerEmpId']}" />
					</td>
					<td class="datacell">
						<kul:htmlControlAttribute attributeEntry="${groupTravelerAttributes.groupTravelerEmpId}" property="newGroupTravelerLine.groupTravelerEmpId" readOnly="true" />
						<div style="display:inline;" id="personLookupButton">
							<kul:lookup boClassName="org.kuali.kfs.module.tem.businessobject.GroupTravelerForLookup"
										fieldConversions="groupTravelerId:newGroupTravelerLine.groupTravelerEmpId,name:newGroupTravelerLine.name,groupTravelerTypeCode.code:newGroupTravelerLine.groupTravelerTypeCode"
										lookupParameters="newGroupTravelerLine.groupTravelerEmpId:principalId" />
						</div>
					</td>
					<td class="datacell">
						<kul:htmlControlAttribute attributeEntry="${groupTravelerAttributes.name}" property="newGroupTravelerLine.name" readOnly="${!empty param['newGroupTravelerLine.groupTravelerEmpId']}"/>
					</td>
					<td class="infoline">
                        <html:submit
                                styleClass="btn btn-green"
                                property="methodToCall.addGroupTravelerLine"
                                alt="Add Group Traveler Line"
                                title="Add Group Traveler Line"
                                value="Add"/>
					</td>
				</tr>
			</c:if>
			<c:if test="${fn:length(KualiForm.document.groupTravelers) > 0}">
                <logic:iterate indexId="ctr" name="KualiForm" property="document.groupTravelers" id="currentLine">
                    <tr class="${(ctr + 1) % 2 == 0 ? "highlight" : ""}">
                        <kul:htmlAttributeHeaderCell literalLabel="${ctr+1}" scope="row" align="right"/>
                        <td>
                            <kul:htmlControlAttribute
                                    attributeEntry="${groupTravelerAttributes.groupTravelerTypeCode}"
                                    property="document.groupTravelers[${ctr}].groupTravelerTypeCode"
                                    readOnly="true" />
                        </td>
                        <td>
                            <kul:htmlControlAttribute
                                    attributeEntry="${groupTravelerAttributes.groupTravelerEmpId}"
                                    property="document.groupTravelers[${ctr}].groupTravelerEmpId"
                                    readOnly="true" />
                        </td>
                        <td>
                            <kul:htmlControlAttribute
                                    attributeEntry="${groupTravelerAttributes.name}"
                                    property="document.groupTravelers[${ctr}].name"
                                    readOnly="true" />
                        </td>
                        <c:if test="${fullEntryMode}">
                            <td>
                                <html:submit
                                        styleClass="btn btn-red"
                                        property="methodToCall.deleteGroupTravelerLine.line${ctr}"
                                        alt="Delete Group Traveler Line"
                                        title="Delete Group Traveler Line"
                                        value="Delete"/>
                            </td>
                        </c:if>
                    </tr>
                </logic:iterate>
			</c:if>
		</table>
	</div>
</kul:tab>
