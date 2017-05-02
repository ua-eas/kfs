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
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<%@ attribute name="transactionalDocument" required="true" description="Boolean value of whether this is a transactional document the buttons are being displayed on or not." %>
<%@ attribute name="saveButtonOverride" required="false" description="Overrides the methodToCall for the save button." %>
<%@ attribute name="suppressRoutingControls" required="false" description="Boolean value of whether any buttons which result in routing - Submit, Approve, etc - should be displayed." %>
<%@ attribute name="suppressCancelButton" required="false" description="Boolean value of whether the cancel button should be displayed." %>
<%@ attribute name="extraButtonSource" required="false" description="The image src of a single extra button." %>
<%@ attribute name="extraButtonProperty" required="false" description="The methodToCall property of a single extra button." %>
<%@ attribute name="extraButtonAlt" required="false" description="The alt description of a single extra button." %>
<%@ attribute name="extraButtons" required="false" type="java.util.List" description="A List of org.kuali.kfs.kns.web.ui.ExtraButton objects to render before the standard button." %>
<%@ attribute name="viewOnly" required="false" description="Boolean value of whether this document is view only, which means in effect the save button would be suppressed." %>
<%@ attribute name="tabindex" required="false" %>

<c:if test="${empty tabindex}">
    <c:set var="tabindex" value="0" />
</c:if>

<c:set var="documentTypeName" value="${KualiForm.docTypeName}" />
<c:set var="documentEntry" value="${DataDictionary[documentTypeName]}" />
<c:set var="saveButtonValue" value="save" />
<c:if test="${not empty saveButtonOverride}"><c:set var="saveButtonValue" value="${saveButtonOverride}" /></c:if>

<c:if test="${not KualiForm.suppressAllButtons}">
    <div id="globalbuttons" class="globalbuttons">
        <c:if test="${!empty extraButtonSource}">
            <html:submit styleClass="globalbuttons btn btn-default" property="${extraButtonProperty}" alt="${extraButtonAlt}" tabindex="${tabindex}" value="${extraButtonAlt}"/>
        </c:if>
        <c:if test="${!empty extraButtons}">
            <c:forEach items="${extraButtons}" var="extraButton">
                <html:submit styleClass="globalbuttons btn btn-default" property="${extraButton.extraButtonProperty}" title="${extraButton.extraButtonAltText}" alt="${extraButton.extraButtonAltText}"  onclick="${extraButton.extraButtonOnclick}" tabindex="${tabindex}" value="${extraButton.extraButtonAltText}" />
            </c:forEach>
        </c:if>
        <c:if test="${!empty KualiForm.documentActions[Constants.KUALI_ACTION_PERFORM_ROUTE_REPORT] and not suppressRoutingControls}">
            <html:submit styleClass="globalbuttons btn btn-default" property="methodToCall.performRouteReport" title="Perform Route Report" alt="Perform Route Report" tabindex="${tabindex}" value="Perform Route Report" />
        </c:if>
        <c:if test="${!empty KualiForm.documentActions[Constants.KUALI_ACTION_CAN_COMPLETE] and not suppressRoutingControls}">
            <html:submit styleClass="globalbuttons btn btn-default" property="methodToCall.complete" title="complete" alt="complete" onclick="excludeSubmitRestriction=true" value="Complete" />
        </c:if>
        <c:if test="${!empty KualiForm.documentActions[Constants.KUALI_ACTION_CAN_SEND_ADHOC_REQUESTS] and not suppressRoutingControls}">
            <html:submit styleClass="globalbuttons btn btn-default" property="methodToCall.sendAdHocRequests" title="Send AdHoc Requests" alt="Send AdHoc Requests" tabindex="${tabindex}" value="Send AdHoc Requests"/>
        </c:if>
        <c:if test="${!empty KualiForm.documentActions[Constants.KUALI_ACTION_CAN_ROUTE] and not suppressRoutingControls}">
            <html:submit styleClass="globalbuttons btn btn-default" property="methodToCall.route" title="submit" alt="submit" onclick="resetScrollPosition();" tabindex="${tabindex}" value="Submit" />
        </c:if>
        <c:if test="${!empty KualiForm.documentActions[Constants.KUALI_ACTION_CAN_SAVE] and not viewOnly}">
            <html:submit styleClass="globalbuttons btn btn-default" property="methodToCall.${saveButtonValue}" title="save" alt="save" onclick="resetScrollPosition();" tabindex="${tabindex}" value="Save" />
        </c:if>
        <c:if test="${!empty KualiForm.documentActions[Constants.KUALI_ACTION_CAN_RELOAD]}">
            <html:submit styleClass="globalbuttons btn btn-default" property="methodToCall.reload" title="reload" alt="reload" onclick="excludeSubmitRestriction=true;resetScrollPosition();" tabindex="${tabindex}" value="Reload" />
        </c:if>
        <c:if test="${!empty KualiForm.documentActions[Constants.KUALI_ACTION_CAN_BLANKET_APPROVE] and not suppressRoutingControls}">
            <html:submit styleClass="globalbuttons btn btn-default" property="methodToCall.blanketApprove" title="blanket approve" alt="blanket approve" onclick="resetScrollPosition();" tabindex="${tabindex}" value="Blanket Approve"/>
        </c:if>
        <c:if test="${!empty KualiForm.documentActions[Constants.KUALI_ACTION_CAN_APPROVE] and not suppressRoutingControls}">
            <html:submit styleClass="globalbuttons btn btn-default" property="methodToCall.approve" title="approve" alt="approve" onclick="resetScrollPosition();" tabindex="${tabindex}" value="Approve" />
        </c:if>
        <c:if test="${!empty KualiForm.documentActions[Constants.KUALI_ACTION_CAN_DISAPPROVE] and not suppressRoutingControls}">
            <html:submit styleClass="globalbuttons btn btn-default" property="methodToCall.disapprove" title="disapprove" alt="disapprove" onclick="resetScrollPosition();" tabindex="${tabindex}" value="Disapprove" />
        </c:if>
        <c:if test="${!empty KualiForm.documentActions[Constants.KUALI_ACTION_CAN_FYI] and not suppressRoutingControls}">
            <html:submit styleClass="globalbuttons btn btn-default" property="methodToCall.fyi" title="fyi" alt="fyi" onclick="resetScrollPosition();" tabindex="${tabindex}" value="FYI" />
        </c:if>
        <c:if test="${!empty KualiForm.documentActions[Constants.KUALI_ACTION_CAN_ACKNOWLEDGE] and not suppressRoutingControls}">
            <html:submit styleClass="globalbuttons btn btn-default" property="methodToCall.acknowledge" title="acknowledge" alt="acknowledge" onclick="resetScrollPosition();" tabindex="${tabindex}" value="Acknowledge" />
        </c:if>
        <c:if test="${!empty KualiForm.documentActions[Constants.KUALI_ACTION_CAN_CLOSE]}">
            <html:submit styleClass="globalbuttons btn btn-default" property="methodToCall.close" title="close" alt="close" tabindex="${tabindex}" value="Close" />
        </c:if>
        <c:if test="${!empty KualiForm.documentActions[Constants.KUALI_ACTION_CAN_CANCEL] and not suppressCancelButton}">
            <html:submit styleClass="globalbuttons btn btn-default" property="methodToCall.cancel" title="cancel" alt="cancel" tabindex="${tabindex}" value="Cancel" />
        </c:if>
      <c:if test="${!empty KualiForm.documentActions[Constants.KUALI_ACTION_CAN_RECALL]}">
        <html:submit styleClass="globalbuttons btn btn-default" property="methodToCall.recall" title="Recall current document" alt="Recall current document" onclick="resetScrollPosition();" tabindex="${tabindex}" value="Recall" />
      </c:if>
        <c:if test="${!empty KualiForm.documentActions[Constants.KUALI_ACTION_CAN_COPY]}">
           <html:submit styleClass="globalbuttons btn btn-default" property="methodToCall.copy" title="Copy current document" alt="Copy current document" onclick="resetScrollPosition();" tabindex="${tabindex}" value="Copy" />
        </c:if>
    </div>

    <kul:stickyGlobalButtons bodySelector="main.doc #page-content"/>
</c:if>

