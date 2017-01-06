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
<%@ attribute name="capitalAssetInformation" required="true" type="java.lang.Object" description="The group accounting lines for each capital asset that will be shown"%>
<c:set var="groupAccountingLinesAttributes" value="${DataDictionary.CapitalAssetAccountsGroupDetails.attributes}" />
<%@ attribute name="capitalAssetPosition" required="true" description="The index of the CAB Capital Asset"%>
<%@ attribute name="showViewButton" required="true" description="To show the view/hide button for payments"%>

<c:set var="docPos" value="0" />
<c:set var="linePos" value="0" />

<c:set var="docPos" value="${docPos+1}" />

<div class="tab-container">
	<table class="standard" style="width: calc(100% - 60px); margin-left: 60px;">
		<tr>
			<td class="tab-subhead"  width="100%" colspan="15"><h3>Accounting Lines Amount Distributions</h3></td>
		</tr>
		<tr>
			<c:set var="tabKey" value="payment-${capitalAssetPosition}"/>
			<html:hidden property="tabStates(${tabKey})" value="CLOSE" />
			<td colspan="7" style="padding:0px; border-style:none;">
				<table class="standard">
				   <c:if test="${showViewButton == true}" >
						<tr>
							<td colspan="11" class="tab-subhead">
							<html:button
									property="methodToCall.toggleTab.tab${tabKey}"
									title="toggle"
									alt="show"
									styleClass="btn btn-default small"
									styleId="tab-${tabKey}-imageToggle"
									onclick="javascript: return toggleTab(document, 'kualiFormModal', '${tabKey}'); "
									value="Show"/>
								View Payments
							</td>
						</tr>
						<tbody  style="display: none;" id="tab-${tabKey}-div">
					</c:if>

					<tr class="header">
						<kul:htmlAttributeHeaderCell
							attributeEntry="${groupAccountingLinesAttributes.capitalAssetAccountLineNumber}"
							useShortLabel="true" />
						<kul:htmlAttributeHeaderCell
							attributeEntry="${groupAccountingLinesAttributes.sequenceNumber}"
							useShortLabel="true" />
						<kul:htmlAttributeHeaderCell
							attributeEntry="${groupAccountingLinesAttributes.financialDocumentLineTypeCode}"
							useShortLabel="true" />
						<kul:htmlAttributeHeaderCell
							attributeEntry="${groupAccountingLinesAttributes.chartOfAccountsCode}"
							useShortLabel="true"
							hideRequiredAsterisk="true" />
						<kul:htmlAttributeHeaderCell
							attributeEntry="${groupAccountingLinesAttributes.accountNumber}"
							useShortLabel="true" />
						<kul:htmlAttributeHeaderCell
							attributeEntry="${groupAccountingLinesAttributes.subAccountNumber}"
							useShortLabel="true" />
						<kul:htmlAttributeHeaderCell
							attributeEntry="${groupAccountingLinesAttributes.financialObjectCode}"
							useShortLabel="true" />
						<kul:htmlAttributeHeaderCell
							attributeEntry="${groupAccountingLinesAttributes.financialSubObjectCode}"
							useShortLabel="true" />
						<kul:htmlAttributeHeaderCell
							attributeEntry="${groupAccountingLinesAttributes.projectCode}"
							useShortLabel="true" />
						<kul:htmlAttributeHeaderCell
							attributeEntry="${groupAccountingLinesAttributes.organizationReferenceId}"
							useShortLabel="true" />
						<kul:htmlAttributeHeaderCell
							attributeEntry="${groupAccountingLinesAttributes.amount}"
							useShortLabel="true"
							addClass="right"/>
					</tr>
					<c:forEach items="${capitalAssetInformation.capitalAssetAccountsGroupDetails}" var="accountLine" >
						<tr>
							<td class="infoline center">${accountLine.capitalAssetAccountLineNumber}</td>
							<td class="infoline">${accountLine.sequenceNumber}</td>
							<td class="infoline">
								<c:set var="lineType" value="${accountLine.financialDocumentLineTypeCode}" />
								<c:if test="${lineType eq KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE}">
									<c:out value="${KFSConstants.SOURCE}" />
								</c:if>
								<c:if test="${lineType eq KFSConstants.TARGET_ACCT_LINE_TYPE_CODE}">
									<c:out value="${KFSConstants.TARGET}" />
								</c:if>
							</td>
							<td class="infoline">${accountLine.chartOfAccountsCode}</td>
							<td class="infoline">${accountLine.accountNumber}</td>
							<td class="infoline">${accountLine.subAccountNumber}</td>
							<td class="infoline">${accountLine.financialObjectCode}</td>
							<td class="infoline">${accountLine.financialSubObjectCode}</td>
							<td class="infoline">${accountLine.projectCode}</td>
							<td class="infoline">${accountLine.organizationReferenceId}</td>
							<td class="infoline right">${accountLine.amount}</td>
						</tr>
					</c:forEach>
					</tbody>
				</table>
			</td>
		</tr>
	</table>
</div>
