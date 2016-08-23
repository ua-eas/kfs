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
<%@ attribute name="documentAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="viewList" required="true" %>
<%@ attribute name="limitByPoId" required="false" %>

<c:set var="documentType" value="${KualiForm.document.documentHeader.workflowDocument.documentTypeName}" />
<c:set var="isRequisition" value="${KualiForm.document.isReqsDoc}" />

<logic:notEmpty name="KualiForm" property="${viewList}">
	<logic:iterate id="view" name="KualiForm" property="${viewList}" indexId="viewCtr">
		<c:if test="${(empty limitByPoId) or (limitByPoId eq view.purchaseOrderIdentifier)}">

			<c:set var="documentTitle" value="${view.documentLabel}${view.documentIdentifierString}"/>
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

            <html:hidden property="tabStates(${tabKey})" value="${(isOpen ? 'OPEN' : 'CLOSE')}" />

			<h3>
				<c:choose>
					<c:when test="${isRequisition}">
						${view.documentLabel} -
						<a href="<c:out value="${view.url}" />" target="_BLANK"><c:out value="${view.documentIdentifierString}" /></a>
						&nbsp;(Purchase Order - ${view.purchaseOrderIdentifier})
					</c:when>
					<c:otherwise>
						${view.documentLabel} -
						<a href="<c:out value="${view.url}" />" target="_BLANK"><c:out value="${view.documentIdentifierString}" /></a>
					</c:otherwise>
				</c:choose>
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
			</h3>

			<c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
				<div style="display: block;" id="tab-${tabKey}-div">
			</c:if>
			<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}" >
				<div style="display: none;" id="tab-${tabKey}-div">
			</c:if>

				<table class="datatable standard side-margins" summary="Notes">
					<c:if test="${!empty view.notes}">
						<tr class="header">
							<kul:htmlAttributeHeaderCell scope="col" width="15%">Date</kul:htmlAttributeHeaderCell>
							<kul:htmlAttributeHeaderCell scope="col" width="15%">User</kul:htmlAttributeHeaderCell>
							<kul:htmlAttributeHeaderCell scope="col" width="70%">Note</kul:htmlAttributeHeaderCell>
						</tr>
						<c:forEach items="${view.notes}" var="note" >
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
					</c:if>
					<c:if test="${empty view.notes}">
						<tr>
							<th>No Notes</th>
						</tr>
					</c:if>
				</table>
			</div>

	    	<c:set var="viewShown" value="true"/>
	    </c:if>
	</logic:iterate>
</logic:notEmpty>


