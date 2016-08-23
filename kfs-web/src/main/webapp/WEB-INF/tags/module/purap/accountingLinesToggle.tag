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

<%@ attribute name="accountPrefix" required="false" description="an optional prefix to specify a different location for accounting lines rather than just on the document."%>
<%@ attribute name="currentTabIndex" required="true" description="current tab index"%>

<c:set var="tabTitle" value="AccountingLines-${currentTabIndex}" />
<c:set var="tabKey" value="${kfunc:generateTabKey(tabTitle)}"/>
<c:set var="dummyIncrementer" value="${kfunc:incrementTabIndex(KualiForm, tabKey)}" />
<c:set var="currentTab" value="${kfunc:getTabState(KualiForm, tabKey)}"/>

<c:choose>
    <c:when test="${empty currentTab}">
        <c:set var="isOpen" value="false" />
    </c:when>
    <c:when test="${!empty currentTab}">
        <c:set var="isOpen" value="${currentTab == 'OPEN'}" />
    </c:when>
</c:choose>

<div class="acct-lines-toggle">
    <c:if test="${!accountingLineScriptsLoaded}">
        <script language="JavaScript" type="text/javascript" src="scripts/sys/objectInfo.js"></script>
        <c:set var="accountingLineScriptsLoaded" value="true" scope="request" />
    </c:if>

    <html:hidden property="tabStates(${tabKey})" value="${(isOpen ? 'OPEN' : 'CLOSE')}" />


    <c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
        <button
                type="submit"
                name="methodToCall.toggleTab.tab${tabKey}"
                alt="hide"
                title="Hide Accounting Lines"
                class="btn clean"
                id="tab-${tabKey}-imageToggle"
                onclick="toggleTab(document, 'kualiFormModal', '${tabKey}'); "
                value="Hide">
            <img src="${ConfigProperties.kr.externalizable.images.url}acct-lines.png" width="18px"/>
        </button>
    </c:if>
    <c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
        <button
                type="submit"
                name="methodToCall.toggleTab.tab${tabKey}"
                alt="show"
                title="Show Accounting Lines"
                class="btn clean"
                id="tab-${tabKey}-imageToggle"
                onclick="toggleTab(document, 'kualiFormModal', '${tabKey}'); "
                value="Show">
            <img src="${ConfigProperties.kr.externalizable.images.url}acct-lines.png" width="18px"/>
        </button>
    </c:if>
</div>
