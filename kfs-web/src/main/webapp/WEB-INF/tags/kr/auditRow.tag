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

<%@ attribute name="tabTitle" required="true" description="The title of the tab the group of audit errors are being displayed on." %>
<%@ attribute name="defaultOpen" required="true" description="Whether the tab the group of audit errors is displayed on should be rendered as open as an initial state." %>
<%@ attribute name="totalErrors" required="true" description="The total number of audit errors the tab will be displaying." %>
<%@ attribute name="category" required="true" description="The category of the cluster of audit errors being displayed, here used to create a unique tab key." %>

<c:set var="tabKeyName" value="${tabTitle}${category}" />
<c:set var="tabKey" value="${kfunc:generateTabKey(tabKeyName)}"/>
<c:set var="currentTabIndex" value="${KualiForm.currentTabIndex}"/>
<c:set var="incrementerDummy" value="${kfunc:incrementTabIndex(KualiForm, currentTabIndex)}" />
<c:set var="currentTab" value="${kfunc:getTabState(KualiForm, currentTabIndex)}"/>

<c:choose>
    <c:when test="${empty currentTab}">
        <c:set var="isOpen" value="${defaultOpen}" />
    </c:when>
    <c:when test="${!empty currentTab}" >
        <c:set var="isOpen" value="${(currentTab == 'OPEN')}" />
    </c:when>
</c:choose>

<html:hidden property="tabStates(${tabKey})" value="${(isOpen ? 'OPEN' : 'CLOSE')}" />

<!-- ROW -->

<tbody>
    <tr>
	    <td class="tab-subhead">
	      	<c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
				<html:submit
						property="methodToCall.toggleTab.tab${tabKey}"
						alt="hide" title="toggle"
						styleClass="btn btn-default small"
						styleId="tab-${tabKey}-imageToggle"
						onclick="return toggleTab(document, 'kualiFormModal', '${tabKey}');"
						value="Hide"/>
	        </c:if>
	        <c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
				<html:submit
						property="methodToCall.toggleTab.tab${tabKey}"
						alt="show" title="toggle"
						styleClass="btn btn-default small"
						styleId="tab-${tabKey}-imageToggle"
						onclick="return toggleTab(document, 'kualiFormModal', '${tabKey}');"
						value="Show"/>
	        </c:if>
	    </td>
	    <td colspan="3" class="tab-subhead" width="99%"><b>${tabTitle} (${totalErrors})</b></td>
    </tr>
</tbody>

<c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
	<tbody style="display: ;" id="tab-${tabKey}-div">
</c:if>
<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
	<tbody style="display: none;" id="tab-${tabKey}-div">
</c:if>

<!-- Before the jsp:doBody of the kul:tab tag -->
<jsp:doBody/>
<!-- After the jsp:doBody of the kul:tab tag -->

</tbody>
