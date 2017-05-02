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

<kul:tab tabTitle="Pre-Disbursement Processor Status" defaultOpen="false">
    <div class="tab-container" align=center>
        <c:set var="dvAttributes" value="${DataDictionary.DisbursementVoucherDocument.attributes}"/>
        <table class="datatable standard side-margins" summary="DV PDP Status">
            <tr>
                <th class="right" width="50%">
                    <kul:htmlAttributeLabel attributeEntry="${dvAttributes.disbursementVoucherPdpStatus}"/>
                </th>
                <td class="datacell" width="50%">
                    <kul:htmlControlAttribute
                        attributeEntry="${dvAttributes.disbursementVoucherPdpStatus}"
                        property="document.disbursementVoucherPdpStatus" readOnly="true"/>
                </td>
            </tr>
            <tr>
                <th class="right">
                    <kul:htmlAttributeLabel attributeEntry="${dvAttributes.extractDate}"/>
                </th>
                <td class="datacell">
                    <kul:htmlControlAttribute
                        attributeEntry="${dvAttributes.extractDate}" property="document.extractDate"
                        readOnly="true"/>
                </td>
            </tr>
            <tr>
                <th class="right">
                    <kul:htmlAttributeLabel attributeEntry="${dvAttributes.paidDate}"/>
                </th>
                <td class="datacell">
                    <kul:htmlControlAttribute
                        attributeEntry="${dvAttributes.paidDate}" property="document.paidDate" readOnly="true"/>

                    <c:if test="${not empty KualiForm.document.extractDate}">
                        <fp:dvDisbursementInfo sourceDocumentNumber="${KualiForm.document.documentNumber}"
                                               sourceDocumentType="${KualiForm.document.paymentDetailDocumentType}"/>
                    </c:if>
                </td>
            </tr>
            <tr>
                <th class="right">
                    <kul:htmlAttributeLabel attributeEntry="${dvAttributes.cancelDate}"/>
                </th>
                <td class="datacell">
                    <kul:htmlControlAttribute
                        attributeEntry="${dvAttributes.cancelDate}" property="document.cancelDate"
                        readOnly="true"/>
                </td>
            </tr>
        </table>
    </div>
</kul:tab>
