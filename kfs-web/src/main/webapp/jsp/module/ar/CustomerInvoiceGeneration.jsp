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

<c:set var="orgAttributes" value="${DataDictionary.Organization.attributes}"/>

<kul:page lookup="true" showDocumentInfo="false"
          headerTitle="Customer Invoice Generation" docTitle="Customer Invoice Generation" renderMultipart="true"
          transactionalDocument="false" htmlFormAction="arCustomerInvoiceGeneration" errorKey="foo">

    <div class="headerarea-small" id="headerarea-small">
        <h1>Customer Invoice Generation</h1>
    </div>

    <div id="lookup">
        <table class="standard" align="center" summary="Invoice Section">
            <tr>
                <th>
                    <label for="chartCode"><kul:htmlAttributeLabel attributeEntry="${orgAttributes.chartOfAccountsCode}" readOnly="true"/></label>
                </th>
                <td>
                    <kul:htmlControlAttribute attributeEntry="${orgAttributes.chartOfAccountsCode}" property="chartCode"/>
                    <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.Chart" fieldConversions="chartOfAccountsCode:chartCode"/>
                </td>
            </tr>
            <tr>
                <th>
                    <label for="orgCode"><kul:htmlAttributeLabel attributeEntry="${orgAttributes.organizationCode}" readOnly="true"/></label>
                </th>
                <td>
                    <kul:htmlControlAttribute attributeEntry="${orgAttributes.organizationCode}" property="orgCode"/>
                    <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.Organization" fieldConversions="organizationCode:orgCode" lookupParameters="orgCode:organizationCode,chartCode:chartOfAccountsCode"/>
                </td>
            </tr>
            <tr>
                <th>
                    <label for="userId">User Id:</label>
                </th>
                <td align=left valign=middle class="grid">
                    <html-el:text property="userId"/>
                </td>
            </tr>
            <tr>
                <th>
                    <label for="runDate">Print invoices for date:</label>
                </th>
                <td>
                    <kul:dateInput attributeEntry="${orgAttributes.organizationBeginDate}" property="runDate"/>
                </td>

            </tr>
            <tr>
                <th>
                    <label for="orgType">Org Type:</label>
                </th>
                <td>
                    <input type="radio" name="orgType" value="P" id="orgTypeP"/>
                    <label for="orgTypeP">Processing</label>
                    <input type="radio" name="orgType" value="B" id="orgTypeB"/>
                    <label for="orgTypeB">Billing</label>
                </td>
            </tr>
            <tr align="center">
                <td height="30" colspan="4" class="infoline">
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

    <kul:stickyLookupButtons/>

    <div>
        <c:if test="${!empty KualiForm.message }">
            ${KualiForm.message }
        </c:if>
    </div>

</kul:page>
