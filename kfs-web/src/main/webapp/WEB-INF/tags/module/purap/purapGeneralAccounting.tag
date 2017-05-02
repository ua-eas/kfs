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

<%@ attribute name="accountPrefix" required="false" description="an optional prefix to specify a different location for acocunting lines rather than just on the document."%>
<%@ attribute name="itemColSpan" required="true" description="item columns to span"%>
<%@ attribute name="rowStyle" required="false" description="custom styling for the row"%>
<%@ attribute name="rowClass" required="false" description="custom class for the row"%>

<%@ attribute name="currentTabIndex" required="false" description="current tab index"%>
<%@ attribute name="showToggle" required="false" description="whether or not to show the hide/show toggle button"%>

<c:if test="${empty currentTabIndex}">
    <c:set var="currentTabIndex" value="${KualiForm.currentTabIndex}"/>
</c:if>

<c:if test="${empty showToggle}">
    <c:set var="showToggle" value="true"/>
</c:if>

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

<tr style="${rowStyle}" class="${rowClass}">
    <td colspan="${itemColSpan}">
        <c:if test="${!accountingLineScriptsLoaded}">
            <script language="JavaScript" type="text/javascript" src="scripts/sys/objectInfo.js"></script>
            <c:set var="accountingLineScriptsLoaded" value="true" scope="request" />
        </c:if>

        <html:hidden property="tabStates(${tabKey})" value="${(isOpen ? 'OPEN' : 'CLOSE')}" />

        <table class="standard">
            <c:if test="${showToggle}">
                <tr>
                    <th class="left">
                        Accounting Lines
                        <c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
                             <html:submit
                                     property="methodToCall.toggleTab.tab${tabKey}"
                                     alt="hide"
                                     title="toggle"
                                     styleClass="btn btn-default small"
                                     styleId="tab-${tabKey}-imageToggle"
                                     onclick="javascript: return toggleTab(document, '${tabKey}'); "
                                     value="Hide"/>
                         </c:if>
                         <c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
                             <html:submit
                                     property="methodToCall.toggleTab.tab${tabKey}"
                                     alt="show"
                                     title="toggle"
                                     styleClass="btn btn-default small"
                                     styleId="tab-${tabKey}-imageToggle"
                                     onclick="javascript: return toggleTab(document, '${tabKey}'); "
                                     value="Show"/>
                         </c:if>
                    </th>
                </tr>
            </c:if>

            <c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
                <tr style="display: none;"  id="tab-${tabKey}-div">
            </c:if>

            <th style="padding:0;">
                <sys-java:accountingLines>
                    <sys-java:accountingLineGroup newLinePropertyName="${accountPrefix}newSourceLine" collectionPropertyName="${accountPrefix}sourceAccountingLines" collectionItemPropertyName="${accountPrefix}sourceAccountingLine" attributeGroupName="source" />
                </sys-java:accountingLines>
            </th>

            <c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
                </tr>
            </c:if>
        </table>
    </td>
</tr>



