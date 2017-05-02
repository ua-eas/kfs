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
<%@ attribute name="documentAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="groupList" required="true" %>
<%@ attribute name="limitByPoId" required="true" %>

<c:set var="documentType" value="${KualiForm.document.documentHeader.workflowDocument.documentTypeName}" />
<c:set var="isATypeOfPODoc" value="${KualiForm.document.isATypeOfPODoc}" />

<c:if test="${KualiForm.document.needWarningRelatedPOs}">
	<div style="margin-left: 20px; font-style:italic;">
		<bean:message key="${PurapConstants.WARNING_PURCHASEORDER_NUMBER_DONT_DISCLOSE}" />
	</div>
</c:if>

<logic:notEmpty name="KualiForm" property="${groupList}">
	<logic:iterate id="group" name="KualiForm" property="${groupList}" indexId="groupCtr">
		<c:forEach items="${group.views}" var="view" varStatus="viewCtr">
			<c:if test="${(empty limitByPoId) or (limitByPoId eq view.purapDocumentIdentifier)}">

			    <%--Setting tab vars for show/hide button. This is done only once - for the top PO in the view list, since there is only one button for the whole group. --%>
				<c:if test="${viewCtr.count eq 1}">
					<c:set var="documentTitle" value="${view.documentLabel}${view.purapDocumentIdentifier}"/>
        	        <c:set var="tabKey" value="${kfunc:generateTabKey(documentTitle)}" />
					<c:set var="currentTab" value="${kfunc:getTabState(KualiForm, tabKey)}" />
					<%-- default to close --%>
					<c:choose>
						<c:when test="${empty currentTab}">
							<c:set var="isOpen" value="false" />
						</c:when>
						<c:when test="${!empty currentTab}">
							<c:set var="isOpen" value="${currentTab == 'OPEN'}" />
						</c:when>
					</c:choose>
				</c:if>

                    <html:hidden property="tabStates(${tabKey})" value="${(isOpen ? 'OPEN' : 'CLOSE')}" />

				<h3>
					<c:choose>
						<%--This is done only once when this PO doc itself is not the current PO, thus there's one and only one related current PO, and all other related POs are noncurrent --%>
						<c:when test="${view.purchaseOrderCurrentIndicator}">
							${view.documentLabel} -
							<a href="<c:out value="${view.url}" />" target="_BLANK"><c:out value="${view.poNumberMasked}" /></a>
							<c:if test="${view.needWarning}" >
								&nbsp;UNAPPROVED
							</c:if>
							<c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
								<html:button
										property="methodToCall.toggleTab.tab${tabKey}"
										alt="hide"
										title="toggle"
										styleClass="btn btn-default small"
										styleId="tab-${tabKey}-imageToggle"
										onclick="javascript: return toggleTab(document, 'kualiFormModal', '${tabKey}');"
										value="Hide"/>
							</c:if>
							<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
								<html:button
										property="methodToCall.toggleTab.tab${tabKey}"
										alt="show"
										title="toggle"
										styleClass="btn btn-default small"
										styleId="tab-${tabKey}-imageToggle"
										onclick="javascript: return toggleTab(document, 'kualiFormModal', '${tabKey}');"
										value="Show"/>
							</c:if>
							<c:if test="${not empty view.notes}">
								<c:set var="notes" value="${view.notes}"/>
							</c:if>
						</c:when>

						<c:otherwise>
							<c:if test="${viewCtr.count eq 1}">
								<c:out value="${view.documentLabel}"/>
								<c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
									<html:button
											property="methodToCall.toggleTab.tab${tabKey}"
											alt="hide"
											title="toggle"
											styleClass="btn btn-default small"
											styleId="tab-${tabKey}-imageToggle"
											onclick="javascript: return toggleTab(document, 'kualiFormModal', '${tabKey}');"
											value="Hide"/>
								</c:if>
								<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
									<html:button
											property="methodToCall.toggleTab.tab${tabKey}"
											alt="show"
											title="toggle"
											styleClass="btn btn-default small"
											styleId="tab-${tabKey}-imageToggle"
											onclick="javascript: return toggleTab(document, 'kualiFormModal', '${tabKey}');"
											value="Show"/>
								</c:if>
							</c:if>

							${view.documentLabel} -
							<c:out value="Doc #"/>
							<a href="<c:out value="${view.url}" />"  target="_BLANK"><c:out value="${view.documentNumber}" /></a>
						</c:otherwise>
					</c:choose>
				</h3>

		    	<c:set var="viewShown" value="true"/>
	        </c:if>
		</c:forEach>

		<c:if test="${viewShown and (not isATypeOfPODoc)}">
			<c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
				<div style="display: block;" id="tab-${tabKey}-div">
			</c:if>
			<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}" >
				<div style="display: none;" id="tab-${tabKey}-div">
			</c:if>
            <table class="datatable standard side-margins" summary="Notes">
              <c:choose>
    	        <c:when test="${not empty notes}">
			        <tr class="header">
						<kul:htmlAttributeHeaderCell scope="col" width="15%">Date</kul:htmlAttributeHeaderCell>
	        			<kul:htmlAttributeHeaderCell scope="col" width="15%">User</kul:htmlAttributeHeaderCell>
			        	<kul:htmlAttributeHeaderCell scope="col" width="70%">Note</kul:htmlAttributeHeaderCell>
		        	</tr>
       				<c:forEach items="${notes}" var="note" >
	        	    	<tr>
		            		<td class="datacell">
		        	   			<c:out value="${note.notePostedTimestamp}" />
			                </td>
       				   		<td class="datacell">
	        	    			<c:out value="${note.authorUniversal.name}" />
			           		</td>
			           		<td class="datacell">
		        		       	<c:out value="${note.noteText}" />
       				    	</td>
	        		    </tr>
			        </c:forEach>
			        <c:set var="notes" value=""/>
				</c:when>
				<c:otherwise>
					<tr>
			    		<th>No Notes</th>
			    	</tr>
				</c:otherwise>
			  </c:choose>
			</table>
			</div>
		</c:if>
	</logic:iterate>

    <c:if test="${viewShown}">
		<c:if test="${isATypeOfPODoc}">
			<c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
				<div style="display: block;" id="tab-${tabKey}-div">
			</c:if>
			<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}" >
				<div style="display: none;" id="tab-${tabKey}-div">
			</c:if>
				<h3><c:out value="Please refer to the Notes and Attachments Tab for the Purchase Order Notes"/></h3>
			</div>
		</c:if>
	</c:if>
</logic:notEmpty>
