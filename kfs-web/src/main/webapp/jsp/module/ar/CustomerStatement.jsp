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

	<c:set var="orgAttributes" value="${DataDictionary.Organization.attributes}" />
	<c:set var="arDocHeaderAttributes" value="${DataDictionary.AccountsReceivableDocumentHeader.attributes}" />
	<c:set var="invoiceAttributes" value="${DataDictionary.CustomerInvoiceDocument.attributes}" />
	<c:set var="accountAttributes" value="${DataDictionary.Account.attributes}" />
	<c:set var="customerBillingStatementAttributes" value="${DataDictionary.CustomerBillingStatement.attributes}" />

<kul:page lookup="true" showDocumentInfo="false"
	headerTitle="Billing Statement Generation" docTitle="Billing Statement Generation" renderMultipart="true"
	transactionalDocument="false" htmlFormAction="arCustomerStatement" errorKey="foo">

    <div class="headerarea-small" id="headerarea-small">
        <h1>Billing Statement Generation</h1>
    </div>

    <div id="lookup">
        <table class="standard" summary="Billing Statement" style="margin: 20px auto 0 auto;">
            <tr>
                <th width="50%">
                    <label for="chartCode"><kul:htmlAttributeLabel attributeEntry="${orgAttributes.chartOfAccountsCode}" readOnly="true"/></label>
                </th>
                <td width="50%">
                    <kul:htmlControlAttribute attributeEntry="${orgAttributes.chartOfAccountsCode}" property="chartCode"/>
                    <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.Chart"  fieldConversions="chartOfAccountsCode:chartCode"/>
                </td>
            </tr>
            <tr>
                <th>
                    <label for="orgCode"><kul:htmlAttributeLabel attributeEntry="${orgAttributes.organizationCode}" readOnly="true"/></label>
                </th>
                <td>
                    <kul:htmlControlAttribute attributeEntry="${orgAttributes.organizationCode}" property="orgCode"/>
                    <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.Organization"  fieldConversions="organizationCode:orgCode" lookupParameters="orgCode:organizationCode,chartCode:chartOfAccountsCode"/>
                </td>
            </tr>
             <tr>
                <th>
                    <label for="customerNumber"><kul:htmlAttributeLabel attributeEntry="${arDocHeaderAttributes.customerNumber}" readOnly="true"/></label>
                </th>
                <td>
                    <kul:htmlControlAttribute attributeEntry="${arDocHeaderAttributes.customerNumber}" property="customerNumber"/>
                    <kul:lookup boClassName="org.kuali.kfs.module.ar.businessobject.Customer" fieldConversions="customerNumber:customerNumber" lookupParameters="customerNumber:customerNumber"/>
                </td>
            </tr>
            <tr>
                <th>
                    <label for="accountNumber"><kul:htmlAttributeLabel attributeEntry="${accountAttributes.accountNumber}" readOnly="true"/></label>
                </th>
                <td>
                    <kul:htmlControlAttribute attributeEntry="${accountAttributes.accountNumber}" property="accountNumber"/>
                    <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.Account" fieldConversions="accountNbr:accountNumber" lookupParameters="accountNumber:accountNbr"/>
                </td>
            </tr>
            <tr>
                <th>
                    <label><kul:htmlAttributeLabel attributeEntry="${customerBillingStatementAttributes.statementFormat}" readOnly="true" /></label>
                </th>
                <td>
                    <input type="radio" name="statementFormat" id="statementFormatSummary" value="Summary" checked />
                    <label for="statementFormatSummary">Summary</label>
                    <input type="radio" name="statementFormat" id="statementFormatDetail" value="Detail" />
                    <label for="statementFormatDetail">Detail</label>
                </td>
            </tr>
            <tr>
                <th>
                    <label><kul:htmlAttributeLabel attributeEntry="${customerBillingStatementAttributes.includeZeroBalanceCustomers}" readOnly="true" /></label>
                </th>
                <td>
                    <input type="radio" name="includeZeroBalanceCustomers" id="includeZeroBalanceCustomersYes" value="Yes"/>
                    <label for="includeZeroBalanceCustomersYes">Yes</label>
                    <input type="radio" name="includeZeroBalanceCustomers" id="includeZeroBalanceCustomersNo" value="No" checked />
                    <label for="includeZeroBalanceCustomersNo">No</label>
                </td>
            </tr>
            <tr align="center">
                <td height="30" colspan="4"  class="infoline">
                    <c:set var="extraButtons" value="${KualiForm.extraButtons}"/>
                    <c:if test="${!empty extraButtons}">
                        <c:forEach items="${extraButtons}" var="extraButton">
                            <html:submit styleClass="tinybutton btn btn-default" property="${extraButton.extraButtonProperty}" title="${extraButton.extraButtonAltText}" alt="${extraButton.extraButtonAltText}" value="${extraButton.extraButtonAltText}"/>
                        </c:forEach>
                    </c:if>
                </td>
            </tr>
        </table>
    </div>

	<div>
	    <c:if test="${!empty KualiForm.message }">
	        ${KualiForm.message }
        </c:if>
   </div>
</kul:page>
