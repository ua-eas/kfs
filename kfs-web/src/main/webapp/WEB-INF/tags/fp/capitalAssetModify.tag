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

<%@ tag description="render the given field in the capital asset info object"%>

<%@ attribute name="readOnly" required="false" description="Whether the capital asset information should be read only" %>

<script language="JavaScript" type="text/javascript" src="dwr/interface/VendorService.js"></script>
<script language="JavaScript" type="text/javascript" src="scripts/vnd/objectInfo.js"></script>

<c:set var="attributes" value="${DataDictionary.CapitalAssetInformation.attributes}" />
<c:set var="dataCellCssClass" value="datacell" />
<c:set var="capitalAssetInfoName" value="document.capitalAssetInformation" />
<c:set var="amountReadOnly" value="${readOnly or KualiForm.distributeEqualAmount}" />

<c:set var="totalColumnSpan" value="7"/>

<table class="datatable standard side-margins" cellpadding="0" cellspacing="0" summary="Capital Asset Information">
	<tr>
		<td colspan="2" class="tab-subhead">
	   		System Control Amount: <c:out value="${KualiForm.systemControlAmount}" />
	   	</td>
	   	<c:set var="totalColumnSpan" value="${totalColumnSpan-2}"/>
	   	<c:if test="${KualiForm.createdAssetsControlAmount != 0.00}" >
	   		<c:set var="totalColumnSpan" value="2"/>
	   	</c:if>
	   	<c:if test="${KualiForm.createdAssetsControlAmount == 0.00}" >
	   		<c:set var="totalColumnSpan" value="3"/>
	   	</c:if>

	   	<td colspan="${totalColumnSpan}" class="tab-subhead">
	   		System Control Remainder Amount: <c:out value="${KualiForm.createdAssetsControlAmount}" />
	   	</td>
	   	<c:if test="${KualiForm.createdAssetsControlAmount != 0.00}" >
	   		<td colspan="1" class="tab-subhead center">
                <html:submit
                        property="methodToCall.redistributeModifyCapitalAssetAmount"
                        title="Redistribute Total Amount for modify capital assets"
                        alt="Redistribute Total Amount for modify capital assets"
                        styleClass="btn btn-default"
                        value="Redistribute Total Amount"/>
			</td>
	   		<td colspan="1" class="tab-subhead">
	   			<div class="right">
	   				Lookup/Add Multiple Capital Asset Lines <kul:multipleValueLookup boClassName="org.kuali.kfs.integration.cam.CapitalAssetManagementAsset" lookedUpCollectionName="capitalAssetManagementAssets" />
				</div>
	   		</td>
	   	</c:if>
	</tr>
</table>
<table class="datatable standard" cellpadding="0" cellspacing="0" summary="Capital Asset Information">
	<c:forEach items="${KualiForm.document.capitalAssetInformation}" var="detailLine" varStatus="status">
		<c:set var="distributionAmountCode" value="${detailLine.distributionAmountCode}" />
		<c:if test="${distributionAmountCode eq KFSConstants.CapitalAssets.DISTRIBUTE_COST_EQUALLY_CODE}">
			<c:set var="distributionAmountDescription" value="${KFSConstants.CapitalAssets.DISTRIBUTE_COST_EQUALLY_DESCRIPTION}" />
			<c:set var="amountReadOnly" value="true" />
		</c:if>
		<c:if test="${distributionAmountCode eq KFSConstants.CapitalAssets.DISTRIBUTE_COST_BY_INDIVIDUAL_ASSET_AMOUNT_CODE}">
			<c:set var="distributionAmountDescription" value="${KFSConstants.CapitalAssets.DISTRIBUTE_COST_BY_INDIVIDUAL_ASSET_AMOUNT_DESCRIPTION}" />
			<c:set var="amountReadOnly" value="${readOnly}" />
		</c:if>

		<c:if test="${detailLine.capitalAssetActionIndicator == KFSConstants.CapitalAssets.CAPITAL_ASSET_MODIFY_ACTION_INDICATOR}">
			<tr>
                <td colspan="8">
	     		    <h3>Capital Asset for Accounting Line</h3>
                    <c:if test="${not empty detailLine.capitalAssetAccountsGroupDetails}" >
                        <fp:capitalAssetAccountsGroupDetails capitalAssetAccountsGroupDetails="${detailLine.capitalAssetAccountsGroupDetails}"
                            capitalAssetAccountsGroupDetailsName="${capitalAssetInfoName}[${status.index}].capitalAssetAccountsGroupDetails" readOnly="${readOnly}"
                            capitalAssetAccountsGroupDetailsIndex="${status.index}"/>
                    </c:if>
                </td>
		   </tr>
			<tr>
				<td colspan="8">
		     		<div align="center" valign="middle">
			     		<table class="datatable" style="border-top: 1px solid #c3c3c3; width: 60%;" cellpadding="0" cellspacing="0" summary="Asset for Accounting Lines">
						   <tr>
								<kul:htmlAttributeHeaderCell literalLabel=""/>
						   	    <kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetNumber}" labelFor="${capitalAssetInfoName}.capitalAssetNumber"/>
								<kul:htmlAttributeHeaderCell attributeEntry="${attributes.distributionAmountCode}" labelFor="${capitalAssetInfoName}.distributionAmountCode"/>
								<kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetLineAmount}" labelFor="${capitalAssetInfoName}.capitalAssetLineAmount"/>
								<c:if test="${!readOnly}">
									<kul:htmlAttributeHeaderCell literalLabel="Action"/>
								</c:if>
						   </tr>
						   <tr>
						   		<kul:htmlAttributeHeaderCell literalLabel="${detailLine.capitalAssetLineNumber}"/></td>

						   		<fp:dataCell dataCellCssClass="${dataCellCssClass}"
									businessObjectFormName="${capitalAssetInfoName}[${status.index}]" attributes="${attributes}" readOnly="${readOnly}"
									field="capitalAssetNumber" lookup="true" inquiry="true"
									boClassSimpleName="CapitalAssetManagementAsset" boPackageName="org.kuali.kfs.integration.cam"
									lookupUnkeyedFieldConversions="capitalAssetNumber:${capitalAssetInfoName}.capitalAssetTagNumber,"
									lookupOrInquiryKeys="capitalAssetNumber"
									businessObjectValuesMap="${capitalAssetInfo.valuesMap}"/></td>

								<td>
									<div><c:out value="${distributionAmountDescription}"/></div>
								</td>

								<fp:dataCell dataCellCssClass="${dataCellCssClass}" dataFieldCssClass="amount"
									businessObjectFormName="${capitalAssetInfoName}[${status.index}]" attributes="${attributes}" readOnly="${amountReadOnly}"
									field="capitalAssetLineAmount" lookup="false" inquiry="false" /></td>

								<c:if test="${!readOnly}">
									<td class="infoline">
                                        <html:submit
                                                property="methodToCall.refreshCapitalAssetModify.line${status.index}.Anchor"
                                                title="Refresh Modify capital Asset Information"
                                                alt="Refresh Modify capital Asset Information"
                                                styleClass="btn btn-default"
                                                value="Refresh"/>
                                        <html:submit
                                                property="methodToCall.deleteCapitalAssetModify.line${status.index}.Anchor"
                                                title="Delete the capital Asset Information"
                                                alt="Delete the capital Asset Information"
                                                styleClass="btn btn-red"
                                                value="Delete"/>
                                        <html:submit
                                                property="methodToCall.clearCapitalAssetModify.line${status.index}.Anchor"
                                                title="Clear the capital Asset Information"
                                                alt="Clear the capital Asset Information"
                                                styleClass="btn btn-default"
                                                value="Clear"/>
									</td>
								</c:if>
						   </tr>
						</table>
					</div>
		   		</td>
			</tr>
		</c:if>
	</c:forEach>
</table>
