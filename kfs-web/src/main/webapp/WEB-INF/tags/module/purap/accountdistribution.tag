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

<%@ attribute name="accountingLineAttributes" required="true" type="java.util.Map"
	description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="itemAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="displayCommodityCodeFields" required="true" description="Boolean to indicate if commodity code relatedfields should be displayed"%>

<script language="JavaScript" type="text/javascript" src="scripts/vnd/objectInfo.js"></script>

<c:set var="fullEntryMode" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
<c:set var="amendmentEntry"	value="${(not empty KualiForm.editingMode['amendmentEntry'])}" />
<c:set var="clearAllTaxes" value="${(not empty KualiForm.editingMode['clearAllTaxes'])}" />
<c:set var="tabindexOverrideBase" value="50" />

<div class="center">
    <c:if test="${(fullEntryMode or amendmentEntry)}">
        <c:if test="${KualiForm.hideDistributeAccounts and !KualiForm.editingMode['disableSetupAccountDistribution']}">
            <html:submit
                    property="methodToCall.setupAccountDistribution"
                    alt="setup distribution" title="setup distribution"
                    styleClass="btn btn-default"
                    value="Setup Distribution"/>
        </c:if>
        <c:if test="${!KualiForm.hideDistributeAccounts and !KualiForm.editingMode['disableSetupAccountDistribution']}">
            <button disabled alt="setup account distribution" class="btn btn-default disabled">Setup Distribution</button>
        </c:if>

        <c:if test="${!KualiForm.editingMode['disableRemoveAccounts']}">
            <html:submit
                    property="methodToCall.removeAccounts"
                    alt="remove accounts from all items"
                    title="remove accounts from all items"
                    styleClass="btn btn-default"
                    value="Remove Accounts From All Items"/>
        </c:if>

        <c:if test="${displayCommodityCodeFields}">
            <html:submit
                    property="methodToCall.clearItemsCommodityCodes"
                    alt="remove commodity codes from all items"
                    title="remove commodity codes from all items"
                    styleClass="btn btn-default"
                    value="Remove Commodity Codes From All Items"/>
        </c:if>

        <html:submit
                property="methodToCall.showAllAccounts"
                alt="expand all accounts"
                title="expand all accounts"
                styleClass="btn btn-default"
                value="Expand All Accounts"/>

        <html:submit
                property="methodToCall.hideAllAccounts"
                alt="collapse all accounts"
                title="collapse all accounts"
                styleClass="btn btn-default"
                value="Collapse All Accounts"/>
    </c:if>

    <c:if test="${(fullEntryMode or amendmentEntry) and (clearAllTaxes)}">
        <html:submit
                property="methodToCall.clearAllTaxes"
                alt="Clear all tax"
                title="Clear all tax"
                styleClass="btn btn-default"
                value="Clear All Tax"/>
    </c:if>
</div>

<c:if test="${!KualiForm.hideDistributeAccounts}">
    <c:choose>
        <c:when test="${(not empty KualiForm.editingMode['amendmentEntry'])}">
            <c:set target="${KualiForm.accountingLineEditingMode}" property="fullEntry" value="true" />
            <c:set var="accountingLineEditingMode" value = "${KualiForm.accountingLineEditingMode}"/>
        </c:when>
        <c:otherwise>
            <c:set var="accountingLineEditingMode" value = "${KualiForm.editingMode}"/>
        </c:otherwise>
    </c:choose>

    <table class="datatable standard" style="margin-top: 15px;">
        <c:if test="${displayCommodityCodeFields}">
            <tr>
                <th class="right" width="50%">
                    <kul:htmlAttributeLabel attributeEntry="${itemAttributes.purchasingCommodityCode}" />
                </th>
                <td class="left nowrap" width="50%">
                    <c:set var="commodityCodeField"  value="distributePurchasingCommodityCode" />
                    <c:set var="commodityDescriptionField"  value="distributePurchasingCommodityDescription" />
                    <kul:htmlControlAttribute
                            attributeEntry="${itemAttributes.purchasingCommodityCode}"
                            property="distributePurchasingCommodityCode"
                            readOnly="${not (fullEntryMode or amendmentEntry)}"
                            tabindexOverride="${tabindexOverrideBase + 0}"
                            onblur="loadCommodityCodeDescription( '${commodityCodeField}', '${commodityDescriptionField}' );${onblur}"/>
                    <c:if test="${fullEntryMode}">
                        <kul:lookup
                                boClassName="org.kuali.kfs.vnd.businessobject.CommodityCode"
                                fieldConversions="purchasingCommodityCode:distributePurchasingCommodityCode,commodityDescription:distributePurchasingCommodityDescription"
                                lookupParameters="'Y':active"/>
                    </c:if>
                    <div id="distributePurchasingCommodityDescription.div" class="fineprint">
                        <html:hidden write="true" property="${commodityDescriptionField}"/>
                    </div>
                </td>
            </tr>
	    </c:if>
    </table>

    <sys-java:accountingLines>
        <sys-java:accountingLineGroup newLinePropertyName="accountDistributionnewSourceLine" collectionPropertyName="accountDistributionsourceAccountingLines" collectionItemPropertyName="accountDistributionsourceAccountingLine" attributeGroupName="distributeSource" />
    </sys-java:accountingLines>

	<div class="center">
		<html:submit
                property="methodToCall.doDistribution"
                alt="do account distribution"
                title="do account distribution"
                styleClass="btn btn-default"
                value="Distribute to Items" />
		<html:submit
                property="methodToCall.cancelAccountDistribution"
                alt="cancel account distribution"
                title="cancel account distribution"
                styleClass="btn btn-default"
                value="Cancel"/>
	</div>
</c:if>
