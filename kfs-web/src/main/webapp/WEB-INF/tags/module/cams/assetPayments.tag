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
<c:set var="assetAttributes" value="${DataDictionary.Asset.attributes}" />
<c:set var="requiredCapitalAssetNumber" value="* Asset Number" />
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
<c:set var="tabKey" value="${kfunc:generateTabKey(subTabTitle)}"/>

<kul:tab tabTitle="Assets" defaultOpen="true" tabErrorKey="capitalAssetNumber*,commonErrorSection">
	<div class="tab-container" id="assets">

		<c:if test="${!readOnly}">
			<table class="standard side-margins">
			    <tr>
				    <th class="right" width="50%">${requiredCapitalAssetNumber}:</th>
					<td class="infoline" valign="top" width="50%">
						<kul:htmlControlAttribute attributeEntry="${assetAttributes.capitalAssetNumber}" property="capitalAssetNumber"/>
						<kul:multipleValueLookup boClassName="org.kuali.kfs.module.cam.businessobject.Asset" lookedUpCollectionName="assetPaymentAssetDetail"/>
						&nbsp;&nbsp;&nbsp;&nbsp;
					    <html:submit
								property="methodToCall.insertAssetPaymentAssetDetail"
								styleClass="btn btn-green"
								alt="Add an asset"
								title="Add an asset"
								value="Add"/>
					</td>
			    </tr>
			</table>
		</c:if>

		<cams:assetPaymentsAssetInformation/>
    </div>
</kul:tab>
