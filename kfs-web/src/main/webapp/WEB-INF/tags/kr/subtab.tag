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

<%@ attribute name="width" required="true"
              description="The width of the table containing the show/hide button, e.g., '80%'." %>
<%@ attribute name="subTabTitle" required="false"
              description="The title to display next to the show/hide button." %>
<%@ attribute name="buttonAlt" required="false"
              description="The show/hide button alt text and title (must not contain HTML tags or quotes)." %>
<%@ attribute name="noShowHideButton" required="false"
              description="Boolean to hide the show/hide button (but the row is displayed anyway)." %>
<%@ attribute name="highlightTab" required="false" description="Whether the tab should be highlighted with the orange asterisk icon." %>
<%@ attribute name="boClassName" required="false" description="If present, renders a multiple value lookup icon at the top of the sub-tab collection." %>
<%@ attribute name="lookedUpBODisplayName" required="false" description="this value is the human readable name of the BO being looked up" %>
<%@ attribute name="lookedUpCollectionName" required="false" description="the name of the collection being looked up (perhaps on a document collection), this value will be returned to the calling document" %>
<%@ attribute name="useCurrentTabIndexAsKey" required="false" description="Whether to use the current tab index as the current tab key, or (if this is false) generate a new one." %>
<%@ attribute name="open" required="false" description="An override which forces this subtab render as open, if passed a true (or nothing), or closed, if passed a false" %>

<c:if test="${empty open}">
   <c:set var="open" value="true" />
</c:if>

<table class="datatable standard" style="width: ${width}; text-align: left; margin-left: auto; margin-right: auto;">
    <tbody>
        <tr>
            <td class="tab-subhead">
            	<c:if test="${!noShowHideButton}">
                	<a name="${KualiForm.currentTabIndex}"></a>
                </c:if>
                <h3>
                    ${subTabTitle}

                    <c:if test="${highlightTab}">
                        &nbsp;<img src="${ConfigProperties.kr.externalizable.images.url}asterisk_orange.png" alt="changed"/>
                    </c:if>

                    <c:if test="${!noShowHideButton}">
                        <c:set var="currentTabIndex" value="${KualiForm.currentTabIndex}" scope="request"/>

                        <c:choose>
                            <c:when test="${(useCurrentTabIndexAsKey)}">
                                <c:set var="tabKey" value="${currentTabIndex}"/>
                            </c:when>
                            <c:otherwise>
                                <c:set var="tabKey" value="${kfunc:generateTabKey(subTabTitle)}"/>
                            </c:otherwise>
                        </c:choose>

                        <c:set var="doINeedThis" value="${kfunc:incrementTabIndex(KualiForm, tabKey)}" />
                        <c:set var="currentTab" value="${kfunc:getTabState(KualiForm, tabKey)}"/>
                        <c:set var="isOpen" value="${(empty currentTab ? true : (currentTab == 'OPEN')) && open}"/>

                        <html:hidden property="tabStates(${tabKey})" value="${(isOpen ? 'OPEN' : 'CLOSE')}" />
                        <html:button
                                property="methodToCall.toggleTab.tab${tabKey}"
                                title="${isOpen ? 'close' : 'open'} ${buttonAlt}"
                                alt="${isOpen ? 'close' : 'open'} ${buttonAlt}"
                                styleClass="btn btn-default"
                                styleId="tab-${tabKey}-imageToggle"
                                onclick="javascript: return toggleTab(document, 'kualiFormModal', '${tabKey}'); "
                                tabindex="-1"
                                value="${isOpen ? 'Hide' : 'Show'}"/>
                    </c:if>
                </h3>
                <c:if test="${!empty boClassName}">
	                <span class="right">
    	            	<kul:multipleValueLookup
                                boClassName="${boClassName}"
                                lookedUpBODisplayName="${lookedUpBODisplayName}"
        	        			lookedUpCollectionName="${lookedUpCollectionName}"
                                anchor="${tabKey}" />
            	    </span>
            	</c:if>
            </td>
        </tr>
    </tbody>
</table>

<c:if test="${!noShowHideButton}">
    <div style="display: ${isOpen ? 'block' : 'none'};" id="tab-${tabKey}-div">
</c:if>

<jsp:doBody/>

<c:if test="${!noShowHideButton}">
    </div>
</c:if>
