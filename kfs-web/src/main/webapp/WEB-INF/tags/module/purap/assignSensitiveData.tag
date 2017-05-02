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

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>
<%@ attribute name="itemAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for item's fields." %>
<%@ attribute name="poSensitiveDataAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for purchase order sensitive data's fields." %>
<%@ attribute name="sensitiveDataAssignAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for sensitive data assignment's fields." %>

<c:set var="lastSensitiveDataAssignment" value="${KualiForm.lastSensitiveDataAssignment}" />

<kul:tabTop tabTitle="Assign Sensitive Data to Purchase Order" defaultOpen="true" tabErrorKey="${PurapConstants.ASSIGN_SENSITIVE_DATA_TAB_ERRORS}">

    <div class="tab-container">
    	<h3>General Information</h3>
        <table class="standard" style="table-layout: fixed;" summary="Sensitive Data Assignment General Information">
        	<tr>
        		<th class="right" width="25%">
            		Reason for Assignment:
            	</th>
            	<td class="datacell" width="25%">
                	<kul:htmlControlAttribute attributeEntry="${sensitiveDataAssignAttributes.sensitiveDataAssignmentReasonText}" property="sensitiveDataAssignmentReason" />
            	</td>
        		<th class="right" width="25%">
            		Last Updated by Person:
            	</th>
            	<td class="datacell" width="25%">
					&nbsp;
	            	<c:if test="${lastSensitiveDataAssignment != null}">
                		<kul:htmlControlAttribute attributeEntry="${sensitiveDataAssignAttributes.sensitiveDataAssignmentPersonIdentifier}" property="lastSensitiveDataAssignment.sensitiveDataAssignmentPersonIdentifier" readOnly="true" />
            		</c:if>
            	</td>
            </tr>
            <tr>
        		<th class="right" width="25%">
            		Reason for Last Update:
            	</th>
            	<td class="datacell" width="25%">
					&nbsp;
	            	<c:if test="${lastSensitiveDataAssignment != null}">
                		<kul:htmlControlAttribute attributeEntry="${sensitiveDataAssignAttributes.sensitiveDataAssignmentReasonText}" property="lastSensitiveDataAssignment.sensitiveDataAssignmentReasonText" readOnly="true" />
            		</c:if>
            	</td>
        		<th class="right" width="25%">
            		Last Updated on Date:
            	</th>
            	<td class="datacell" width="25%">
					&nbsp;
	            	<c:if test="${lastSensitiveDataAssignment != null}">
                		<kul:htmlControlAttribute attributeEntry="${sensitiveDataAssignAttributes.sensitiveDataAssignmentChangeDate}" property="lastSensitiveDataAssignment.sensitiveDataAssignmentChangeDate" readOnly="true" />
            		</c:if>
            	</td>
            <tr>
        </table>

    	<h3>Vendor Information</h3>
        <table class="standard" summary="Purchase Order Vendor Information">
        	<tr>
        		<th class="right" width="50%">
            		<kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorName}" />
            	</th>
            	<td class="datacell" width="50%">
                	<kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorName}" property="document.vendorName" readOnly="true" />
            	</td>
            </tr>
        </table>

        <h3>Items Information</h3>
        <table class="standard side-margins acct-lines" summary="Purchase Order Items Information">
            <tr class="header top">
				<th></th>
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemTypeCode}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemQuantity}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemUnitOfMeasureCode}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemCatalogNumber}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.purchasingCommodityCode}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemDescription}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemUnitPrice}" addClass="right" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.extendedPrice}" addClass="right" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemAssignedToTradeInIndicator}" addClass="center" />
            </tr>

			<c:set var="itemCount" value="0"/>
			<logic:iterate indexId="ctr" name="KualiForm" property="document.items" id="itemLine">
				<c:if test="${itemLine.itemType.lineItemIndicator == true}">
					<c:set var="itemCount" value="${itemCount + 1}"/>
				</c:if>
			</logic:iterate>
            <logic:iterate indexId="ctr" name="KualiForm" property="document.items" id="itemLine">
	        	<c:if test="${itemLine.itemType.lineItemIndicator == true}">
                	<tr class="top line" style="${ctr == itemCount - 1 ? 'border-bottom:1px solid #BBBBBB;' : ''}">
                    	<th class="datacell"><bean:write name="KualiForm" property="document.item[${ctr}].itemLineNumber"/></th>
                        <td class="datacell"><kul:htmlControlAttribute readOnly="true" attributeEntry="${itemAttributes.itemTypeCode}" property="document.item[${ctr}].itemTypeCode"/></td>
                        <td class="datacell"><kul:htmlControlAttribute readOnly="true" attributeEntry="${itemAttributes.itemQuantity}" property="document.item[${ctr}].itemQuantity" /></td>
 				        <td class="datacell"><kul:htmlControlAttribute readOnly="true" attributeEntry="${itemAttributes.itemUnitOfMeasureCode}" property="document.item[${ctr}].itemUnitOfMeasureCode" /></td>
 				        <td class="datacell"><kul:htmlControlAttribute readOnly="true" attributeEntry="${itemAttributes.itemCatalogNumber}" property="document.item[${ctr}].itemCatalogNumber" /></td>
 				        <td class="datacell"><kul:htmlControlAttribute readOnly="true" attributeEntry="${itemAttributes.purchasingCommodityCode}" property="document.item[${ctr}].purchasingCommodityCode" /></td>
 				        <td class="datacell"><kul:htmlControlAttribute readOnly="true" attributeEntry="${itemAttributes.itemDescription}" property="document.item[${ctr}].itemDescription" /></td>
 				        <td class="datacell right"><kul:htmlControlAttribute readOnly="true" attributeEntry="${itemAttributes.itemUnitPrice}" property="document.item[${ctr}].itemUnitPrice" /></td>
 				        <td class="datacell right"><kul:htmlControlAttribute readOnly="true" attributeEntry="${itemAttributes.extendedPrice}" property="document.item[${ctr}].extendedPrice" /></td>
  				        <td class="datacell center"><kul:htmlControlAttribute readOnly="true" attributeEntry="${itemAttributes.itemAssignedToTradeInIndicator}" property="document.item[${ctr}].itemAssignedToTradeInIndicator" /></td>
                    </tr>
                </c:if>
            </logic:iterate>
        </table>

        <h3>New Sensitive Data Entry To Be Assigned</h3>
        <table class="standard side-margins" summary="Assign New Sensitive Data Entry">
            <tr class="header">
				<kul:htmlAttributeHeaderCell attributeEntry="${poSensitiveDataAttributes.sensitiveDataCode}" />
				<kul:htmlAttributeHeaderCell literalLabel="Actions"/>
        	</tr>
        	<tr>
        		<td class="datacell">
        			<kul:htmlControlAttribute
							attributeEntry="${poSensitiveDataAttributes.sensitiveDataCode}"
        					property="newSensitiveDataLine.sensitiveDataCode" />
        		</td>
        		<td class="datacell">
        			<html:submit
							property="methodToCall.addSensitiveData"
        					alt="Add New Sensitive Data Entry"
							title="Add New Sensitive Data Entry"
							styleClass="btn btn-green"
							value="Add"/>
        		</td>
        	</tr>
        </table>

        <h3>Current Sensitive Data Entries Assigned</h3>
        <table class="standard side-margins" summary="Delete/Update Current Sensitive Data Entries">
            <tr class="header">
				<kul:htmlAttributeHeaderCell attributeEntry="${poSensitiveDataAttributes.sensitiveDataCode}" />
				<kul:htmlAttributeHeaderCell literalLabel="Actions"/>
        	</tr>
            <logic:iterate indexId="ctr" name="KualiForm" property="sensitiveDatasAssigned" id="sd">
				<tr class="${ctr % 2 == 0 ? 'highlight' : ''}">
					<td class="datacell">
						<kul:htmlControlAttribute
								attributeEntry="${poSensitiveDataAttributes.sensitiveDataCode}"
								property="sensitiveDatasAssigned[${ctr}].sensitiveDataCode" />
					</td>
					<td class="datacell">
						<html:submit
								property="methodToCall.deleteSensitiveData.line${ctr}"
								alt="Delete Sensitive Data Entry ${ctr+1}"
								title="Delete Sensitive Data Entry ${ctr+1}"
								styleClass="btn btn-red"
								value="Delete"/>
					</td>
				</tr>
        	</logic:iterate>
        </table>
    </div>
</kul:tabTop>



