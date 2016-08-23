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

<%@ attribute name="tabTitle" required="false" description="The label to render for the tab." %>
<%@ attribute name="spanForLongTabTitle" required="false" description="If true, sets the CSS class for the title such that it will display over multiple columns" %>
<%@ attribute name="tabDescription" required="false" description="An explanatory description which will be rendered on the tab." %>
<%@ attribute name="defaultOpen" required="true" description="Whether the tab should default to rendering as open." %>
<%@ attribute name="tabErrorKey" required="false" description="The property key this tab should display errors associated with." %>
<%@ attribute name="innerTabErrorKey" required="false" description="The error path for errors whose message should not be displayed in this tab.  Errors will cause the tab to be opened. Path can be wildcarded with and asterisk.  Multiple paths must be separated with a comma and no white spaces." %>
<%@ attribute name="auditCluster" required="false" description="The error audit cluster associated with this page." %>
<%@ attribute name="tabAuditKey" required="false" description="The property key this tab should display audit errors associated with." %>
<%@ attribute name="tabItemCount" required="false" description="Expands the title to display this count alongside." %>
<%@ attribute name="helpUrl" required="false" description="Will display as a standard help link/image in the tab." %>
<%@ attribute name="leftSideHtmlProperty" required="false" description="The property name of an attribute to display at the left side of the tab. Used with leftSideHtmlAttribute." %>
<%@ attribute name="leftSideHtmlAttribute" required="false" type="java.util.Map" description="The data dictionary entry for an attribute to display at the left side of the tab.  Used with leftSideHtmlProperty." %>
<%@ attribute name="leftSideHtmlDisabled" required="false" description="If leftSideHtmlProperty and leftSideHtmlAttribute have been utilized, whether to display the left hand attribute as disabled." %>
<%@ attribute name="rightSideHtmlProperty" required="false" description="The property name of an attribute to display at the right side of the tab. Used with rightSideHtmlAttribute." %>
<%@ attribute name="rightSideHtmlAttribute" required="false" type="java.util.Map" description="The data dictionary entry for an attribute to display at the right side of the tab.  Used with rightSideHtmlProperty." %>
<%@ attribute name="transparentBackground" required="false" description="Whether the tab should render as having the background transparent around the corners of the tab." %>
<%@ attribute name="highlightTab" required="false" description="Whether the tab should be highlighted with the orange asterisk icon." %>
<%@ attribute name="extraButtonSource" required="false" description="The image source for an extra button to display on the tab." %>
<%@ attribute name="useCurrentTabIndexAsKey" required="false" description="Whether to use the current tab index as the current tab key, or (if this is false) generate a new one." %>
<%@ attribute name="hidden" required="false" description="Renders the tab as closed." %>
<%@ attribute name="useRiceAuditMode" required="false" description="If present and tabAuditKey is not present, renders all the audit errors in the audit cluster." %>
<%@ attribute name="midTabClassReplacement" required="false" description="Text to use as a replacement for the show/hide buttons rendering." %>
<%@ attribute name="boClassName" required="false" description="If present, makes the tab title an inquiry link using the business object class declared here.  Used with the keyValues attribute." %>
<%@ attribute name="keyValues" required="false" description="If present, makes the tab title an inquiry link using the primary key values declared here.  Used with the boClassName attribute." %>
<%@ attribute name="alwaysOpen" required="false" description="allows a tab to always stay open" %>

<%@ variable name-given="tabKey" scope="NESTED" description="forces the tabKey variable to nested scope" %>

<c:set var="currentTabIndex" value="${KualiForm.currentTabIndex}" scope="request"/>
<c:set var="topLevelTabIndex" value="${KualiForm.currentTabIndex}" scope="request"/>

<c:choose>
    <c:when test="${(useCurrentTabIndexAsKey)}">
        <c:set var="tabKey" value="${currentTabIndex}"/>
    </c:when>
    <c:otherwise>
        <c:set var="tabKey" value="${kfunc:generateTabKey(tabTitle)}"/>
    </c:otherwise>
</c:choose>

<!--  hit form method to increment tab index -->
<c:set var="doINeedThis" value="${kfunc:incrementTabIndex(KualiForm, tabKey)}" />

<c:set var="currentTab" value="${kfunc:getTabState(KualiForm, tabKey)}"/>
<c:choose>
    <c:when test="${empty currentTab}">
        <c:set var="isOpen" value="${defaultOpen}" />
    </c:when>
    <c:when test="${!empty currentTab}" >
        <c:set var="isOpen" value="${currentTab == 'OPEN'}" />
    </c:when>
</c:choose>

<!-- if the section has errors, override and set isOpen to true -->
<c:if test="${!empty tabErrorKey or not empty tabAuditKey}">
  <kul:checkErrors keyMatch="${tabErrorKey}" auditMatch="${tabAuditKey}"/>
  <c:set var="isOpen" value="${hasErrors ? true : isOpen}"/>
</c:if>

<c:if test="${isOpen != 'true' and !empty innerTabErrorKey}">
    <kul:checkErrors keyMatch="${innerTabErrorKey}" />
    <c:set var="isOpen" value="${hasErrors ? true : isOpen}" />
</c:if>

<c:if test="${hidden}">
    <c:set var="isOpen" value="false"/>
</c:if>

<html:hidden property="tabStates(${tabKey})" value="${(isOpen ? 'OPEN' : 'CLOSE')}" />
<c:if test="${empty currentTab}">
    <c:set var="currentTab" value="${(isOpen ? 'OPEN' : 'CLOSE')}"/>
</c:if>
<!-- TAB -->

<c:if test="${! empty tabItemCount}">
  <c:set var="tabTitle" value="${tabTitle} (${tabItemCount})" />
</c:if>

<c:set var="tabTitleSpan" value="1" />
<c:if test="${! empty spanForLongTabTitle && spanForLongTabTitle eq true}">
    <c:set var="tabTitleSpan" value="${tabTitleSpan + 1}" />
</c:if>



<c:set var="leftTabImage" value="${ConfigProperties.kr.externalizable.images.url}tab-topleft1.gif" />
<c:set var="rightTabImage" value="${ConfigProperties.kr.externalizable.images.url}tab-topright1.gif" />
<c:set var="rightTabClass" value="tabtable2-right" />
<c:set var="midTabClass" value="tabtable2-mid" />
<c:if test="${transparentBackground}">
  <c:set var="leftTabImage" value="${ConfigProperties.kr.externalizable.images.url}tab-topleft.gif" />
  <c:set var="rightTabImage" value="${ConfigProperties.kr.externalizable.images.url}tab-topright.gif" />
  <c:set var="rightTabClass" value="tabtable1-right" />
  <c:set var="midTabClass" value="tabtable1-mid" />
</c:if>


<div class="main-panel">
    <c:if test="${isOpen == 'true' || isOpen == 'TRUE' || alwaysOpen == 'TRUE'}">
        <c:set var="tabAction" value="close"/>
            <html:image property="methodToCall.toggleTab.tab${tabKey}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-hide.gif" title="close ${tabTitle}" alt="close ${tabTitle}" styleClass="tinybutton"  styleId="tab-${tabKey}-imageToggle" style="display: none;" tabindex="-1" />
    </c:if>
    <c:if test="${isOpen != 'true' && isOpen != 'TRUE' && alwaysOpen != 'TRUE'}">
        <c:set var="tabAction" value="open"/>
            <html:image  property="methodToCall.toggleTab.tab${tabKey}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-show.gif" title="open ${tabTitle}" alt="open ${tabTitle}" styleClass="tinybutton" styleId="tab-${tabKey}-imageToggle" style="display: none;" tabindex="-1"/>
    </c:if>

    <div class="headerarea-small clickable"
         property="methodToCall.toggleTab.tab${tabKey}"
         title="${tabAction} ${tabTitle}"
         alt="${tabAction} ${tabTitle}"
         styleClass="tinybutton"
         id="tab-${tabKey}-imageToggle"
         onclick="$('#tab-${tabKey}-imageToggle').click();"
         tabindex="-1">

        <c:if test="${not empty leftSideHtmlProperty and not empty leftSideHtmlAttribute}">
            <kul:htmlControlAttribute property="${leftSideHtmlProperty}" attributeEntry="${leftSideHtmlAttribute}" disabled="${leftSideHtmlDisabled}" />
        </c:if>
        <a name="${tabKey}" ></a>
        <c:choose>
            <c:when test="${not empty boClassName && not empty keyValues}">
                <h2><kul:inquiry keyValues="${keyValues}" boClassName="${boClassName}" render="true"><c:out value="${tabTitle}" /></kul:inquiry></h2>
            </c:when>
            <c:otherwise>
                <h2><c:out value="${tabTitle}" /></h2>
            </c:otherwise>
        </c:choose>
        <c:if test="${not empty helpUrl }">
            <kul:help alternativeHelp="${helpUrl}" />
        </c:if>

        <div class="toggle-show-tab">
            <c:if test="${isOpen == 'true' || isOpen == 'TRUE' || alwaysOpen == 'TRUE'}">
                <span class="glyphicon glyphicon-menu-up"></span>
            </c:if>
            <c:if test="${isOpen != 'true' && isOpen != 'TRUE' && alwaysOpen != 'TRUE'}">
                <span class="glyphicon glyphicon-menu-down"></span>
            </c:if>
        </div>
    </div>

    <c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
        <div style="display: block; margin: 20px 0;" id="tab-${tabKey}-div">
    </c:if>
    <c:if test="${isOpen != 'true' && isOpen != 'TRUE'}" >
        <div style="display: none;" id="tab-${tabKey}-div">
    </c:if>

        <c:if test="${! (empty tabErrorKey)}">
            <kul:errors keyMatch="${tabErrorKey}" displayInDiv="true"/>
        </c:if>

        <c:if test="${! (empty tabAuditKey) && (useRiceAuditMode == 'true')}">
            <div class="tab-container-error"><div class="left-errmsg-tab">
                <c:forEach items="${fn:split(auditCluster,',')}" var="cluster">
                    <kul:auditErrors cluster="${cluster}" keyMatch="${tabAuditKey}" isLink="false" includesTitle="true"/>
                </c:forEach>
            </div></div>
        </c:if>

        <jsp:doBody/>
    </div>
</div>
