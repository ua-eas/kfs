<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
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

<%@ attribute name="editingMode" required="true" description="used to decide if items may be edited" type="java.util.Map"%>
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<kul:tab tabTitle="Items" defaultOpen="false" tabErrorKey="${KFSConstants.ITEM_LINE_ERRORS}">
<c:set var="itemAttributes" value="${DataDictionary.InternalBillingItem.attributes}" />

 <div class="tab-container" align=center>
	<table cellpadding="0" cellspacing="0" class="datatable items standard" summary="Items section">
        <tr class="header">
            <kul:htmlAttributeHeaderCell literalLabel="&nbsp;"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemServiceDate}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemStockNumber}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemStockDescription}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemQuantity}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.unitOfMeasureCode}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemUnitAmount}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.total}"/>
            <c:if test="${not readOnly}">
                <kul:htmlAttributeHeaderCell literalLabel="Actions"/>
            </c:if>
		</tr>
        <c:if test="${not readOnly}">
            <tr class="highlight">
                <td>&nbsp;</td>
                <td class="infoline"><kul:dateInput attributeEntry="${itemAttributes.itemServiceDate}" property="newItem.itemServiceDate" /></td>
                <td class="infoline"><kul:htmlControlAttribute attributeEntry="${itemAttributes.itemStockNumber}" property="newItem.itemStockNumber" /></td>
                <td class="infoline"><kul:htmlControlAttribute attributeEntry="${itemAttributes.itemStockDescription}" property="newItem.itemStockDescription" /></td>
                <td class="infoline"><kul:htmlControlAttribute attributeEntry="${itemAttributes.itemQuantity}" property="newItem.itemQuantity" /></td>
                <td class="infoline"><kul:htmlControlAttribute attributeEntry="${itemAttributes.unitOfMeasureCode}" property="newItem.unitOfMeasureCode" /></td>
                <td class="infoline"><kul:htmlControlAttribute attributeEntry="${itemAttributes.itemUnitAmount}" property="newItem.itemUnitAmount" styleClass="amount" /></td>
                <td class="infoline"><!-- no total until it's added --></td>
                <td class="infoline"><html:submit property="methodToCall.insertItem" alt="Insert an Item" title="Insert an Item" styleClass="btn btn-green" value="Add"/></td>
            </tr>
        </c:if>
        <logic:iterate id="item" name="KualiForm" property="document.items" indexId="ctr">
            <c:choose>
                <c:when test="${not readOnly}">
                    <tr class="${(ctr + 1) % 2 == 0 ? "highlight" : ""}">
                </c:when>
                <c:otherwise>
                    <tr class="${ctr % 2 == 0 ? "highlight" : ""}">
                </c:otherwise>
            </c:choose>

                <kul:htmlAttributeHeaderCell literalLabel="${ctr+1}" scope="row">
                    <%-- Outside this th, these hidden fields would be invalid HTML. --%>
                    <html:hidden property="document.item[${ctr}].itemSequenceId" />
                    <html:hidden property="document.item[${ctr}].versionNumber" />
                </kul:htmlAttributeHeaderCell>
                <td class="datacell">
                    <c:choose>
                        <c:when test="${readOnly}">
                            <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemServiceDate}" property="document.item[${ctr}].itemServiceDate" readOnly="true"/>
                        </c:when>
                        <c:otherwise>
                            <kul:dateInput attributeEntry="${itemAttributes.itemServiceDate}" property="document.item[${ctr}].itemServiceDate"/>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${itemAttributes.itemStockNumber}" property="document.item[${ctr}].itemStockNumber" readOnly="${readOnly}"/></td>
                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${itemAttributes.itemStockDescription}" property="document.item[${ctr}].itemStockDescription" readOnly="${readOnly}"/></td>
                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${itemAttributes.itemQuantity}" property="document.item[${ctr}].itemQuantity" readOnly="${readOnly}"/></td>
                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${itemAttributes.unitOfMeasureCode}" property="document.item[${ctr}].unitOfMeasureCode" readOnly="${readOnly}"/></td>
                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${itemAttributes.itemUnitAmount}" property="document.item[${ctr}].itemUnitAmount" readOnly="${readOnly}" styleClass="amount"/></td>
                <td class="datacell">$${KualiForm.document.items[ctr].total}</td> <%-- EL doesn't quash items' plural like Struts does. --%>
                <c:if test="${not readOnly}">
                    <td class="datacell"><html:submit property="methodToCall.deleteItem.line${ctr}" title="Delete Item ${ctr+1}" alt="Delete Item ${ctr+1}" styleClass="btn btn-red" value="Delete"/></td>
                </c:if>
            </tr>
        </logic:iterate>
		<tr>
	 		<td class="total-line" colspan="7">&nbsp;</td>
	  		<td class="total-line" ><strong>Total: $${KualiForm.document.itemTotal}</strong></td>
            <c:if test="${not readOnly}">
                <td class="total-line">&nbsp;</td>
            </c:if>
		</tr>
	</table>
</div>
</kul:tab>
