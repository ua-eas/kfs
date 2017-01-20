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
<%@ include file="/jsp/sys/kfsTldHeader.jsp" %>
<script>
    <c:if test="${!empty KualiForm.documentNumber}">
    var popUpurl = '${KualiForm.docHandlerForwardLink}';
    window.open(popUpurl, "${KualiForm.documentNumber}");
    </c:if>
</script>

<kul:page showDocumentInfo="false" htmlFormAction="camsPurApLine" renderMultipart="true"
          showTabButtons="true" docTitle="Purchasing / Accounts Payable Transactions"
          transactionalDocument="false" headerDispatch="true" headerTabActive="true"
          sessionDocument="false" headerMenuBar="" feedbackKey="true" defaultMethodToCall="refresh">
    <kul:tabTop tabTitle="Purchase Order Processing" defaultOpen="true">
        <div class="tab-container">
            <c:set var="cabPurApDocumentAttributes" value="${DataDictionary.PurchasingAccountsPayableDocument.attributes}"/>
            <table class="standard side-margins">
                <tr>
                    <th class="grid right" width="25%">
                        <kul:htmlAttributeLabel attributeEntry="${cabPurApDocumentAttributes.purchaseOrderIdentifier}" readOnly="true"/>
                    </th>
                    <td class="grid" width="75%">
                        <c:choose>
                            <c:when test="${!empty KualiForm.purchaseOrderInquiryUrl }">
                                <a href="${ConfigProperties.application.url}/${KualiForm.purchaseOrderInquiryUrl }" target="_blank">
                                        ${KualiForm.purchaseOrderIdentifier}
                                </a>
                            </c:when>
                            <c:otherwise>
                                ${KualiForm.purchaseOrderIdentifier}&nbsp;
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
                <tr>
                    <th class="grid right" width="25%">
                        <kul:htmlAttributeLabel attributeEntry="${cabPurApDocumentAttributes.purApContactEmailAddress}" readOnly="true"/>
                    </th>
                    <td class="grid" width="75%">${KualiForm.purApContactEmailAddress}</td>
                </tr>
                <tr>
                    <th class="grid right" width="25%">
                        <kul:htmlAttributeLabel attributeEntry="${cabPurApDocumentAttributes.purApContactPhoneNumber}" readOnly="true"/>
                    </th>
                    <td class="grid" width="75%">${KualiForm.purApContactPhoneNumber}</td>
                </tr>
            </table>
        </div>
    </kul:tabTop>

    <c:set var="readOnly" value="true"/>
    <c:forEach items="${KualiForm.purApDocs}" var="purApDoc">
        <c:forEach items="${purApDoc.purchasingAccountsPayableItemAssets}" var="assetLine">
            <c:if test="${assetLine.active}">
                <c:set var="readOnly" value="false"/>
            </c:if>
        </c:forEach>
    </c:forEach>

    <cams:purApItemLines activeIndicator="true" title="Active Line Items" defaultOpen="true" tabErrorKey="purApDocs*,merge*" readOnly="${readOnly}"/>
    <cams:purApItemLines activeIndicator="false" title="Submitted Line Items" defaultOpen="false"/>

    <div id="globalbuttons" class="globalbuttons">
        <c:if test="${not readOnly}">
            <html:submit
                    styleClass="btn btn-default"
                    property="methodToCall.save"
                    title="save"
                    alt="save"
                    value="Save"/>
            <html:submit
                    styleClass="btn btn-default"
                    property="methodToCall.close"
                    title="close"
                    alt="close"
                    value="Close"/>
        </c:if>
        <html:submit
                styleClass="btn btn-default"
                property="methodToCall.reload"
                title="reload"
                alt="reload"
                value="Reload"/>
        <html:submit
                styleClass="btn btn-default"
                property="methodToCall.cancel"
                title="Cancel"
                alt="Cancel"
                value="Cancel"/>
    </div>

    <kul:stickyGlobalButtons bodySelector="main.content"/>
</kul:page>
