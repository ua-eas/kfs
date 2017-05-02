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

<%@ attribute name="displayRequisitionFields" required="false"
              description="Boolean to indicate if REQ specific fields should be displayed" %>

<%@ attribute name="displayPurchaseOrderFields" required="false"
              description="Boolean to indicate if PO specific fields should be displayed" %>

<c:set var="fullEntryMode" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT] && (empty KualiForm.editingMode['restrictFiscalEntry'])}" />
<c:set var="systemSelected" value="${KualiForm.document.capitalAssetSystemTypeCode}"/>

<kul:tab tabTitle="Capital Asset" defaultOpen="false" tabErrorKey="${PurapConstants.CAPITAL_ASSET_TAB_ERRORS}">
    <div class="tab-container" align=center>
        <h3>System Type Choice</h3>
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Choose System Type Section">
         	<tr>
        		<th colspan="2" class="left">
        			<div class="annotate">
        				<c:out value="${PurapConstants.CapitalAssetTabStrings.SYSTEM_DEFINITION}"/>
        			</div>
        		</td>
			</tr>
			<tr>
	        	<c:choose>
	        		<c:when test="${systemSelected eq PurapConstants.CapitalAssetTabStrings.INDIVIDUAL_ASSETS}">
	        			<th class="right" width="50%">
	        				<c:out value="${PurapConstants.CapitalAssetTabStrings.INDIVIDUAL_ASSETS_DESC}"/>
	        			</th>
	        			<td class="datacell" width="50%">
	        				(selected)
	        			</td>
	        		</c:when>
	        		<c:otherwise>
			    		<th style="font-weight:normal;" class="right" width="50%">
	        					<c:out value="${PurapConstants.CapitalAssetTabStrings.INDIVIDUAL_ASSETS_DESC}"/>
	        			</th>
	        			<td class="datacell" width="50%">
	        				<html:submit property="methodToCall.selectSystemType.${PurapConstants.CapitalAssetTabStrings.INDIVIDUAL_ASSETS}" alt="Select Individual Assets" title="Select Individual Assets" styleClass="btn btn-default small" value="Select" />
	        			</td>
	        		</c:otherwise>
	        	</c:choose>
			</tr>
			<tr>
				<c:choose>
					<c:when test="${systemSelected eq PurapConstants.CapitalAssetTabStrings.ONE_SYSTEM}">
						<th class="right" width="50%">
			        		<c:out value="${PurapConstants.CapitalAssetTabStrings.ONE_SYSTEM_DESC}"/>
			    		</th>
			    		<td class="datacell" width="50%">
			    			(selected)
			    		</td>
			    	</c:when>
			    	<c:otherwise>
			    		<th style="font-weight:normal" class="right" width="50%">
	        					<c:out value="${PurapConstants.CapitalAssetTabStrings.ONE_SYSTEM_DESC}"/>
	        			</th>
	        			<td class="datacell" width="50%">
	        				<html:submit property="methodToCall.selectSystemType.${PurapConstants.CapitalAssetTabStrings.ONE_SYSTEM}" alt="Select One System" title="Select One System" styleClass="btn btn-default small" value="Select"/>
	        			</td>
			    	</c:otherwise>
			    </c:choose>

			</tr>
			<tr>
				<c:choose>
					<c:when test="${systemSelected eq PurapConstants.CapitalAssetTabStrings.MULTIPLE_SYSTEMS}">
						<th class="right" width="50%">
			        		<c:out value="${PurapConstants.CapitalAssetTabStrings.MULTIPLE_SYSTEMS_DESC}"/>
			    		</th>
			    		<td class="datacell" width="50%">
			    			(selected)
			    		</td>
			    	</c:when>
			    	<c:otherwise>
			    		<th style="font-weight:normal" class="right" width="50%">
	        				<c:out value="${PurapConstants.CapitalAssetTabStrings.MULTIPLE_SYSTEMS_DESC}"/>
	        			</th>
	        			<td class="datacell" width="50%">
	        				<html:submit property="methodToCall.selectSystemType.${PurapConstants.CapitalAssetTabStrings.MULTIPLE_SYSTEMS}" alt="Select Multiple Systems" title="Select Multiple Systems" styleClass="btn btn-default small" value="Select"/>
	        			</td>
			    	</c:otherwise>
			    </c:choose>
			</tr>
        </table>

        <h3>Asset Information</h3>
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Choose System Type Section">
            <tr>
            	<c:choose>
            		<c:when test="${systemSelected eq PurapConstants.CapitalAssetTabStrings.INDIVIDUAL_ASSETS}">
            			<tr>
            				<th colspan="2" class="right">
            					<c:out value="${PurapConstants.CapitalAssetTabStrings.ASSET_DATA}"/>
            				</th>
            			</tr>
            		</c:when>
            		<c:otherwise>
            			<tr>
			            	<th class="right">
			            		<kul:htmlAttributeLabel attributeEntry="${documentAttributes.systemDescription}" />
			            	</th>
			            	<td class="datacell">
			            		<kul:htmlControlAttribute attributeEntry="${documentAttributes.systemDescription}" property="document.systemDescription" readOnly="${not (fullEntryMode)}" />
			            	</td>
            				<c:if test="${systemSelected eq PurapConstants.CapitalAssetTabStrings.ONE_SYSTEM}">
								<td>
	            					<!-- Here is where the hook to the Blue Box goes. -->
	            				</td>
	            			</c:if>
	            		</tr>
            		</c:otherwise>
            	</c:choose>
            </tr>
      	</table>
	</div>
</kul:tab>
