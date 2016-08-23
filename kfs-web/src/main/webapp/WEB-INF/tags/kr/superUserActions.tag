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
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>
<%@ attribute name="showTab" required="false" description="used to decide if the tab should be open by default"%>
<c:if test="${KualiForm.superUserAuthorized}">
    <c:set var="tabTitle"><bean:message key="superuser.tab.label" /></c:set>
    <c:set var="actionLabel"><bean:message key="superuser.action.column.label" /></c:set>
    <c:set var="requestedLabel"><bean:message key="superuser.requested.column.label" /></c:set>
    <c:set var="timeLabel"><bean:message key="superuser.time.column.label" /></c:set>
    <c:set var="annotationLabel"><bean:message key="superuser.annotation.column.label" /></c:set>
    <c:set var="tabOpenBydefault" value="true" />
    <c:if test="${not empty showTab}"><c:set var="tabOpenBydefault" value="${showTab}" /></c:if>
        <kul:tab tabTitle="${tabTitle}"
                 defaultOpen="${tabOpenBydefault}"
                 tabErrorKey="superuser.errors"
                 transparentBackground="${transparentBackground}">
            <div class="tab-container" align=center id="G4">
            <c:if test="${(KualiForm.superUserApproveSingleActionRequestAuthorized && KualiForm.stateAllowsApproveSingleActionRequest && not empty KualiForm.actionRequestsRequiringApproval) && KualiForm.superUserActionAvaliable}">
                <table cellpadding="0" cellspacing="0" class="datatable standard" summary="view/add notes">
                    <tbody>
                        <tr class="header">
                            <th style="width: 5%; text-align: center;"><input type="checkbox" onclick="jQuery('input.superUserAction').prop('checked', jQuery(this).prop('checked'))" /></th>
                            <th style="width: 15%;">${actionLabel}</th>
                            <th style="width: 15%;">${requestedLabel}</th>
                            <th style="width: 15%;">${timeLabel}</th>
                            <th style="width: 50%;">${annotationLabel}</th>
                        </tr>
                        <c:forEach var="actionRequest" items="${KualiForm.actionRequests}" varStatus="status">
                            <tr class="${status.index % 2 == 0 ? "highlight" : ""}">
                                <td class="datacell" style="text-align: center;"><html:multibox property="selectedActionRequests" value="${actionRequest.id}" styleClass="superUserAction" /></td>
                                <td class="datacell">${actionRequest.actionRequested}</td>
                                <td class="datacell">
                                    <c:choose>
                                        <c:when test="${actionRequest.userRequest}">
                                            <c:out value="${kfunc:getPrincipalDisplayName(actionRequest.principalId)}" />
                                        </c:when>
                                        <c:when test="${actionRequest.groupRequest}">
                                            <c:out value="${kfunc:getKimGroupDisplayName(actionRequest.groupId)}" />
                                        </c:when>
                                        <c:otherwise>
                                            <c:out value="${kfunc:getRoleDisplayName(actionRequest)}" />
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="datacell"><joda:format value="${actionRequest.dateCreated}" pattern="MM/dd/yyyy hh:mm a"/>&nbsp;</td>
                                <td class="datacell">${actionRequest.annotation}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>
            <br>
            <c:if test="${KualiForm.superUserActionAvaliable}">
                <div>
                    <label for="superUserAnnotation" style="width:715px;text-transform:uppercase;font-size:12px;padding-bottom:2px;text-align:left;color:#767676;">
                        Annotation*:
                    </label>
                    <br/>
                    <html:textarea property="superUserAnnotation" rows="5" cols="100" styleId="superUserAnnotation" />
                </div>
                <div style="padding:20px 0 40px 0;">
                    <c:if test="${KualiForm.superUserApproveSingleActionRequestAuthorized && KualiForm.stateAllowsApproveSingleActionRequest && not empty KualiForm.actionRequestsRequiringApproval}">
                        <html-el:submit property="methodToCall.takeSuperUserActions" style="border-style:none;" styleClass="btn btn-default" value="Take Selected Actions" />
                    </c:if>
                    <c:if test="${KualiForm.superUserApproveDocumentAuthorized && KualiForm.stateAllowsApproveOrDisapprove}">
                        <html-el:submit property="methodToCall.superUserApprove" style="border-style:none;" styleClass="btn btn-default" value="Approve Document"/>
                    </c:if>
                    <c:if test="${KualiForm.superUserDisapproveDocumentAuthorized && KualiForm.stateAllowsApproveOrDisapprove}">
                        <html-el:submit property="methodToCall.superUserDisapprove" style="border-style:none;" styleClass="btn btn-default" value="Disapprove Document" />
                    </c:if>
                </div>
            </c:if>
        </div>
    </kul:tab>
</c:if>
