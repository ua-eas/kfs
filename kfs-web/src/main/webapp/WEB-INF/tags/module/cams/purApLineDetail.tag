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
<%@ attribute name="chkcount" required="true" description="The total check number"%>
<%@ attribute name="docPos" required="true" description="The index of the CAB PurAp Document"%>
<%@ attribute name="linePos" required="true" description="The index of CAB PurAp item asset"%>
<%@ attribute name="itemLine" required="true" type="org.kuali.kfs.module.cam.businessobject.PurchasingAccountsPayableItemAsset" %>
<%@ attribute name="purApDocLine" required="true" type="org.kuali.kfs.module.cam.businessobject.PurchasingAccountsPayableDocument" %>
<%@ attribute name="rowClass" required="false" description="Class to add to the row" %>

<script language="JavaScript" type="text/javascript" src="scripts/module/cams/selectCheckBox.js"></script>

<c:set var="purApDocumentAttributes" value="${DataDictionary.PurchasingAccountsPayableDocument.attributes}" />
<c:set var="purApItemAssetAttributes" value="${DataDictionary.PurchasingAccountsPayableItemAsset.attributes}" />
<c:set var="purApLineAssetAccountsAttributes" value="${DataDictionary.PurchasingAccountsPayableLineAssetAccount.attributes}" />
<c:set var="generalLedgerAttributes" value="${DataDictionary.GeneralLedgerEntry.attributes}" />
<c:set var="financialSystemDocumentHeaderAttributes" value="${DataDictionary.FinancialSystemDocumentHeader.attributes}" />
<c:set var="genericAttributes" value="${DataDictionary.GenericAttributes.attributes}" />
<c:set var="CapitalAssetInformationAttributes"	value="${DataDictionary.CapitalAssetInformation.attributes}" />
<c:set var="dateFormatPattern" value="MM/dd/yyyy"/>

<c:choose>
	<c:when test="${itemLine.tradeInAllowance}">
		<c:set var="color" value="red" />
	</c:when>
	<c:otherwise>
		<c:choose>
		<c:when test="${itemLine.additionalChargeNonTradeInIndicator}">
			<c:set var="color" value="blue" />
		</c:when>
		<c:otherwise>
			<c:set var="color" value="black" />
		</c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose>
<c:set var="assetItemStr" value="purApDoc[${docPos-1}].purchasingAccountsPayableItemAsset[${linePos-1}]" />
<tr class="${rowClass}" style="color:${color}">
	<c:choose>
	<c:when test="${itemLine.active && !itemLine.additionalChargeNonTradeInIndicator && !itemLine.tradeInAllowance}">
		<td rowspan="2"><html:checkbox styleId="systemCheckbox" property="${assetItemStr}.selectedValue" /></td>
	</c:when>
	<c:otherwise>
		<td rowspan="2">&nbsp;</td>
	</c:otherwise>
	</c:choose>

	<c:if test="${!itemLine.active}">
	    <td class="infoline center">
	    	<html:link target="_blank" href="cabPurApLine.do?methodToCall=viewDoc&documentNumber=${itemLine.capitalAssetManagementDocumentNumber}">
				${itemLine.capitalAssetManagementDocumentNumber }
			</html:link>
		</td>
		<td class="infoline center">
			<c:forEach items="${itemLine.approvedAssetNumbers }" var="assetNumber" >
				<kul:inquiry boClassName="org.kuali.kfs.integration.cam.CapitalAssetManagementAsset" keyValues="capitalAssetNumber=${assetNumber}" render="true">
					${assetNumber }
				</kul:inquiry>
			</c:forEach>
		</td>
	</c:if>

	<td class="infoline">
		${purApDocLine.purapDocumentIdentifier}
		<c:if test="${!empty itemLine.paymentRequestIdentifier}">
			-${itemLine.paymentRequestIdentifier}
		</c:if>
	</td>
	<td class="infoline">${purApDocLine.documentTypeCode}</td>
	<td class="infoline">${purApDocLine.statusDescription}</td>
	<c:choose>
		<c:when test="${!empty itemLine.itemLineNumber}">
			<td class="infoline">
				<c:set var="preTagUrl" value="${itemLine.preTagInquiryUrl}" />
				<c:choose>
					<c:when test="${!empty preTagUrl}" >
						<a href="${ConfigProperties.application.url}/${preTagUrl }" target="_blank">${itemLine.itemLineNumber}</a>
					</c:when>
					<c:otherwise>
						${itemLine.itemLineNumber}
					</c:otherwise>
				</c:choose>
			</td>
		</c:when>
		<c:otherwise>
			<td class="infoline">${itemLine.itemTypeCode}</td>
		</c:otherwise>
	</c:choose>
	<td class="infoline">${itemLine.accountsPayableItemQuantity }</td>
	<td class="infoline">
		<c:if test="${itemLine.active }">
			<kul:htmlControlAttribute property="${assetItemStr}.splitQty" attributeEntry="${purApItemAssetAttributes.accountsPayableItemQuantity}"/>
		</c:if>
	</td>
	<td class="infoline">${itemLine.unitCost}</td>
	<td class="infoline">${itemLine.firstFincialObjectCode }</td>
	<td class="infoline">
		<c:choose>
			<c:when test="${itemLine.active }">
				<kul:htmlControlAttribute property="${assetItemStr}.accountsPayableLineItemDescription" attributeEntry="${purApItemAssetAttributes.accountsPayableLineItemDescription}"/>
			</c:when>
			<c:otherwise>
				${itemLine.accountsPayableLineItemDescription }
			</c:otherwise>
		</c:choose>
	</td>
	<td class="infoline">
     		<kul:htmlControlAttribute property="${assetItemStr}.capitalAssetTransactionTypeCode" attributeEntry="${purApItemAssetAttributes.capitalAssetTransactionTypeCode}" readOnly="true" readOnlyBody="true">
				<kul:inquiry boClassName="org.kuali.kfs.module.cam.businessobject.AssetTransactionType" keyValues="capitalAssetTransactionTypeCode=${itemLine.capitalAssetTransactionTypeCode}" render="true">
					<html:hidden write="true" property="${assetItemStr}.capitalAssetTransactionTypeCode" />
           		</kul:inquiry>
       		</kul:htmlControlAttribute>
		<br>
		<c:forEach items="${itemLine.purApItemAssets}" var="purApItemAsset">
			<c:set var="i" value="${i+1}" />
			${purApItemAsset.capitalAssetNumber}&nbsp;
		</c:forEach>
	</td>
	<c:choose>
		<c:when test="${itemLine.itemAssignedToTradeInIndicator}">
			<td class="infoline">Y</td>
		</c:when>
		<c:otherwise>
			<td class="infoline">N</td>
		</c:otherwise>
	</c:choose>
	<c:choose>
		<c:when test="${itemLine.active }">
			<td class="infoline">
				<c:if test="${!itemLine.tradeInAllowance}">
					<html:submit
							styleClass="btn btn-default small"
							style="margin-bottom: 2px;"
							property="methodToCall.split.doc${docPos-1}.line${linePos-1}"
							title="Split"
							alt="Split"
							value="Split"/>
					<br>
					<c:if test="${itemLine.accountsPayableItemQuantity < 1 }">
						<html:submit
								styleClass="btn btn-default small"
								style="margin-bottom: 2px;"
								property="methodToCall.percentPayment.doc${docPos-1}.line${linePos-1}"
								title="Percent Payment"
								alt="Percent Payment"
								value="Percent Payment"/>
						<br>
					</c:if>
				</c:if>
				<html:submit
						styleClass="btn btn-default small"
						style="margin-bottom: 2px;"
						property="methodToCall.allocate.doc${docPos-1}.line${linePos-1}"
						title="Allocate"
						alt="allocate"
						value="Allocate"/>
				<br>
				<c:if test="${itemLine.createAssetIndicator}">
					<html:submit
							styleClass="btn btn-default small"
							style="margin-bottom: 2px;"
							property="methodToCall.createAsset.doc${docPos-1}.line${linePos-1}"
							title="Create Asset"
							alt="createAsset"
							value="Create Asset"/>
					<br>
				</c:if>
				<c:if test="${itemLine.applyPaymentIndicator}">
					<html:submit
							styleClass="btn btn-default small"
							property="methodToCall.applyPayment.doc${docPos-1}.line${linePos-1}"
							title="applyPayment"
							alt="applyPayment"
							value="Apply Payment"/>
				</c:if>
			</td>
		</c:when>
	</c:choose>
</tr>
<tr class="${rowClass}">
	<c:set var="tabKey" value="payment-${docPos}-${linePos}"/>
	<html:hidden property="tabStates(${tabKey})" value="CLOSE" />
	<td colspan="13" style="padding:0px; border-style:none;">
		<table class="standard">
			<tr>
				<td colspan="14" class="tab-subhead" style="border-right: medium none;">
				<html:button
						property="methodToCall.toggleTab.tab${tabKey}"
						title="toggle"
						alt="show"
						styleClass="btn btn-default small"
						styleId="tab-${tabKey}-imageToggle"
						value="Show"
						onclick="javascript: return toggleTab(document, 'kualiFormModal', '${tabKey}'); "/>
					View Payments
				</td>
			</tr>
			<tbody  style="display: none;" id="tab-${tabKey}-div">
				<tr class="header">
					<kul:htmlAttributeHeaderCell literalLabel="&nbsp;"/>
					<kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerAttributes.chartOfAccountsCode}" hideRequiredAsterisk="true"/>
					<kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerAttributes.accountNumber}" hideRequiredAsterisk="true"/>
					<kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerAttributes.subAccountNumber}" hideRequiredAsterisk="true"/>
					<kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerAttributes.financialObjectCode}" hideRequiredAsterisk="true"/>
					<kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerAttributes.financialSubObjectCode}" hideRequiredAsterisk="true"/>
					<kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerAttributes.projectCode}" hideRequiredAsterisk="true"/>
					<kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerAttributes.referenceFinancialDocumentNumber}" hideRequiredAsterisk="true"/>
					<kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerAttributes.documentNumber}" hideRequiredAsterisk="true"/>
					<kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerAttributes.financialDocumentTypeCode}" hideRequiredAsterisk="true"/>
					<kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerAttributes.transactionDate}" hideRequiredAsterisk="true"/>
					<kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerAttributes.universityFiscalYear}" hideRequiredAsterisk="true"/>
					<kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerAttributes.universityFiscalPeriodCode}" hideRequiredAsterisk="true"/>
					<kul:htmlAttributeHeaderCell attributeEntry="${purApLineAssetAccountsAttributes.itemAccountTotalAmount}" hideRequiredAsterisk="true" addClass="right"/>
				</tr>
				<c:set var="acctId" value="0" />
				<c:forEach items="${itemLine.purchasingAccountsPayableLineAssetAccounts}" var="payment" >
					<tr>
						<c:set var="acctId" value="${acctId+1}"/>
						<td class="infoline">&nbsp;</td>
						<td class="infoline">${payment.generalLedgerEntry.chartOfAccountsCode}</td>
						<td class="infoline">${payment.generalLedgerEntry.accountNumber}</td>
						<td class="infoline">${payment.generalLedgerEntry.subAccountNumber}</td>
						<td class="infoline">${payment.generalLedgerEntry.financialObjectCode}</td>
						<td class="infoline">${payment.generalLedgerEntry.financialSubObjectCode}</td>
						<td class="infoline">${payment.generalLedgerEntry.projectCode}</td>
						<td class="infoline">
							<c:choose>
							<c:when test="${!empty KualiForm.purchaseOrderInquiryUrl }">
								<a href="${ConfigProperties.application.url}/${KualiForm.purchaseOrderInquiryUrl }" target="_blank">${KualiForm.purchaseOrderIdentifier}</a>
							</c:when>
							<c:otherwise>
								${KualiForm.purchaseOrderIdentifier}
							</c:otherwise>
							</c:choose>
						</td>
						<td class="infoline">
							<html:link target="_blank" href="cabPurApLine.do?methodToCall=viewDoc&documentNumber=${payment.generalLedgerEntry.documentNumber}">
								${payment.generalLedgerEntry.documentNumber}
							</html:link>
						</td>
						<td class="infoline">${payment.generalLedgerEntry.financialDocumentTypeCode}</td>
						<td class="infoline" align="left"><fmt:formatDate value="${payment.generalLedgerEntry.transactionDate}" pattern="${dateFormatPattern}"/></td>
						<td class="infoline">${payment.generalLedgerEntry.universityFiscalYear}</td>
						<td class="infoline">${payment.generalLedgerEntry.universityFiscalPeriodCode}</td>
						<td class="infoline right">${payment.itemAccountTotalAmount}</td>
					</tr>
				</c:forEach>
				<th colspan="13" style="text-align: right;">Total:</th>
				<th class="right">${itemLine.totalCost}</th>
			</tbody>
		</table>
	</td>
</tr>
