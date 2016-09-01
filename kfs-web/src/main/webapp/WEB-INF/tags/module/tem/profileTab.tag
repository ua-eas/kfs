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
<c:set var="profileAttributes" value="${DataDictionary.TemProfile.attributes}" />
<c:set var="travelerAttributes" value="${DataDictionary.TravelerDetail.attributes}" />
<kul:tab tabTitle="TEM Profile" defaultOpen="true">
	<div class="tab-container" align="center">
		<table width="100%" cellpadding="0" cellspacing="0" class="datatable">
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${profileAttributes.travelerTypeCode}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.travelerTypeCode }</td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${profileAttributes.lastUpdate}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.lastUpdate }</td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${profileAttributes.updatedBy}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.updatedBy }</td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${profileAttributes.firstName}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.firstName }</td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${profileAttributes.middleName}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.middleName }</td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${profileAttributes.lastName}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.lastName }</td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${profileAttributes.employeeId}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.employeeId }</td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${profileAttributes.homeDepartment}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.homeDepartment }</td>
			</tr>

			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${profileAttributes.dateOfBirth}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.dateOfBirth }</td>
			</tr>
			<tr>
				<td colspan="2" class="tab-subhead"><h3>Default Accounting</h3></td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${profileAttributes.defaultChartCode}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.defaultChartCode }</td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${profileAttributes.defaultAccount}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.defaultAccount }</td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${profileAttributes.defaultSubAccount}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.defaultSubAccount }</td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${profileAttributes.defaultProjectCode}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.defaultProjectCode }</td>
			</tr>
			<tr>
				<td colspan="2" class="tab-subhead"><h3>Billing Info</h3></td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${travelerAttributes.streetAddressLine1}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.temProfileAddress.streetAddressLine1 }</td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${travelerAttributes.streetAddressLine2}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.temProfileAddress.streetAddressLine2 }</td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${travelerAttributes.cityName}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.temProfileAddress.cityName }</td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${travelerAttributes.stateCode}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.temProfileAddress.stateCode }</td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${travelerAttributes.zipCode}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.temProfileAddress.zipCode }</td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${travelerAttributes.countryCode}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.temProfileAddress.countryCode }</td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${profileAttributes.phoneNumber}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.phoneNumber }</td>
			</tr>
			<tr>
				<th class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${profileAttributes.emailAddress}" />
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.temProfile.emailAddress }</td>
			</tr>
		</table>

	</div>

</kul:tab>
