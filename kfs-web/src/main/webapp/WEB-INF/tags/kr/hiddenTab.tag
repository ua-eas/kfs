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

<%@ attribute name="forceOpen" required="true" description="Whether the hidden tab should be considered open or not." %>


<%-- maintain tabstate --%>
<%--   getTabStateJstl call is *required*, since it changes the currentTabIndex as a side-effect --%>
<%--   (which also means that I must retrieve currentTabIndex before retrieving tabStateJstl) --%>
<c:set var="currentTabIndex" value="${KualiForm.currentTabIndex}"/>
<!--  Ideally the tabKey should be the tabTitle, but since this is a hidden tab, I don't know what its title should be -->
<c:set var="tabKey" value="hiddenTabTitle"/>
<!--  hit form method to increment tab index -->
<c:set var="dummyIncrementer" value="${kfunc:incrementTabIndex(KualiForm, tabKey)}" />

<c:set var="currentTab" value="${kfunc:getTabState(KualiForm, tabKey)}"/>

<html:hidden property="tabStates(${tabKey}).open" value="${forceOpen}" />

<%-- display tab contents --%>
<jsp:doBody/>
