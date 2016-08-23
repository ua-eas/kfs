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
<%@ attribute name="tabTitle" required="true" description="The label to render for the tab." %>
<%@ attribute name="defaultOpen" required="true" description="Whether the tab should default to rendering as open." %>
<%@ attribute name="tabErrorKey" required="false" description="The property key this tab should display errors associated with." %>
<%@ attribute name="boClassName" required="false" description="If present, makes the tab title an inquiry link using the business object class declared here.  Used with the keyValues attribute." %>
<%@ attribute name="keyValues" required="false" description="If present, makes the tab title an inquiry link using the primary key values declared here.  Used with the boClassName attribute." %>
<%@ attribute name="auditCluster" required="false" description="The error audit cluster associated with this page." %>
<%@ attribute name="tabAuditKey" required="false" description="The property key this tab should display audit errors associated with." %>

<c:set var="currentTabIndex" value="${KualiForm.currentTabIndex}"/>
<c:set var="tabKey" value="${kfunc:generateTabKey(tabTitle)}"/>
<c:set var="doINeedThis" value="${kfunc:incrementTabIndex(KualiForm, tabKey)}" />
<c:set var="currentTab" value="${kfunc:getTabState(KualiForm, tabKey)}"/>
<c:choose>
    <c:when test="${empty currentTab}">
        <c:set var="isOpen" value="${defaultOpen}" />
    </c:when>
    <c:when test="${!empty currentTab}" >
        <c:set var="isOpen" value="${(currentTab == 'OPEN')}" />
    </c:when>
</c:choose>
<c:if test="${!empty tabErrorKey or !empty tabAuditKey}">
    <kul:checkErrors keyMatch="${tabErrorKey}" auditMatch="${tabAuditKey}"/>
    <c:set var="isOpen" value="${hasErrors ? true : isOpen}"/>
</c:if>
<html:hidden property="tabStates(${tabKey})" value="${(isOpen ? 'OPEN' : 'CLOSE')}" />

<div id="workarea">
    <div class="main-panel">
        <div class="headerarea-small clickable" property="methodToCall.toggleTab.tab${tabKey}" title="Hide/Show ${tabTitle}" id="tab-${tabKey}-imageToggle" onclick="javascript: return toggleTab(document, 'kualiFormModal', '${tabKey}'); " tabindex="-1">
            <a name="${tabKey}"></a>
            <c:choose>
                <c:when test="${not empty boClassName && not empty keyValues}">
                    <h2><kul:inquiry keyValues="${keyValues}" boClassName="${boClassName}" render="true"><c:out value="${tabTitle}" /></kul:inquiry></h2>
                </c:when>
                <c:otherwise>
                    <h2><c:out value="${tabTitle}" /></h2>
                </c:otherwise>
            </c:choose>
            <div class="toggle-show-tab">
                <span class="glyphicon glyphicon-menu-up"></span>
            </div>
        </div>

        <c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
            <c:set var="tabDisplay" value="block"/>
        </c:if>
        <c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
            <c:set var="tabDisplay" value="none"/>
        </c:if>
        <div style="display: ${tabDisplay};" id="tab-${tabKey}-div">
            <c:if test="${! (empty tabAuditKey) or ! (empty tabErrorKey)}">
                <c:if test="${! (empty tabErrorKey)}">
                    <kul:errors keyMatch="${tabErrorKey}" errorTitle="Errors found in this Section:" displayInDiv="true"/>
                </c:if>
                <c:if test="${! (empty tabAuditKey)}">
                    <div class="tab-container-error">
                        <div class="left-errmsg-tab">
                            <c:forEach items="${fn:split(auditCluster,',')}" var="cluster">
                                <kul:auditErrors cluster="${cluster}" keyMatch="${tabAuditKey}" isLink="false" includesTitle="true"/>
                            </c:forEach>
                        </div>
                    </div>
                </c:if>
            </c:if>
            <div class="tab-container">
                <jsp:doBody/>
            </div>
        </div>
    </div>
