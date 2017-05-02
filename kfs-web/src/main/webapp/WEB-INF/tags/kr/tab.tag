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
<%@ attribute name="helpLabel" required="false" description="Will display next to the standard help link/image in the tab." %>
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
<%@ attribute name="simple" required="false" description="Determines whether it should look like a tab or just be a simple sub-area" %>

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

<c:if test="${! empty tabItemCount}">
    <c:set var="tabTitle" value="${tabTitle} (${tabItemCount})" />
</c:if>

<c:set var="tabTitleSpan" value="1" />
<c:if test="${! empty spanForLongTabTitle && spanForLongTabTitle eq true}">
	<c:set var="tabTitleSpan" value="${tabTitleSpan + 1}" />
</c:if>

<c:if test="${!simple}">
    <c:set var="mainClass" value="main-panel"/>
</c:if>

<div class="${mainClass}"<c:if test="${hidden}"> style="display:none;"</c:if>>
    <c:choose>
        <c:when test="${param.mode eq 'modal'}">
            <c:set var="formId" value="kualiForm"/>
        </c:when>
        <c:otherwise>
            <c:set var="formId" value="kualiFormModal"/>
        </c:otherwise>
    </c:choose>

    <c:choose>
        <c:when test="${!simple}">
            <c:if test="${isOpen == 'true' || isOpen == 'TRUE' || alwaysOpen == 'TRUE'}">
                <div class="headerarea-small clickable" property="methodToCall.toggleTab.tab${tabKey}" title="close ${tabTitle}" id="tab-${tabKey}-imageToggle" onclick="javascript: return toggleTab(document, '${formId}', '${tabKey}'); " tabindex="-1">
            </c:if>
            <c:if test="${isOpen != 'true' && isOpen != 'TRUE' && alwaysOpen != 'TRUE'}">
                <div class="headerarea-small clickable" property="methodToCall.toggleTab.tab${tabKey}" title="open ${tabTitle}" id="tab-${tabKey}-imageToggle" onclick="javascript: return toggleTab(document, '${formId}', '${tabKey}'); " tabindex="-1">
            </c:if>
        </c:when>
        <c:otherwise>
            <div>
        </c:otherwise>
    </c:choose>

      <c:if test="${not empty leftSideHtmlProperty and not empty leftSideHtmlAttribute}"><kul:htmlControlAttribute property="${leftSideHtmlProperty}" attributeEntry="${leftSideHtmlAttribute}" disabled="${leftSideHtmlDisabled}" /></c:if>
      <a name="${tabKey}" ></a>
      <c:choose>
          <c:when test="${not empty boClassName && not empty keyValues}">
              <h2><kul:inquiry keyValues="${keyValues}" boClassName="${boClassName}" render="true"><c:out value="${tabTitle}" /></kul:inquiry></h2>
          </c:when>
          <c:otherwise>
              <c:choose>
                  <c:when test="${!simple}">
                      <h2><c:out value="${tabTitle}" /></h2>
                  </c:when>
                  <c:otherwise>
                      <h3><c:out value="${tabTitle}" /></h3>
                  </c:otherwise>
              </c:choose>
          </c:otherwise>
      </c:choose>
      <c:if test="${not empty helpUrl }">
          <kul:help alternativeHelp="${helpUrl}" alternativeHelpLabel="${helpLabel}" onClick="event.stopPropagation()" />
      </c:if>

      <c:if test="${highlightTab}">
          &nbsp;<img src="${ConfigProperties.kr.externalizable.images.url}asterisk_orange.png" alt="changed"/>
      </c:if>

      <c:if test="${!simple}">
          <div class="toggle-show-tab">
              <c:choose>
                  <c:when test="${empty midTabClassReplacement}">

                      <c:if test="${isOpen == 'true' || isOpen == 'TRUE' || alwaysOpen == 'TRUE'}">
                          <span class="glyphicon glyphicon-menu-up"></span>
                      </c:if>
                      <c:if test="${isOpen != 'true' && isOpen != 'TRUE' && alwaysOpen != 'TRUE'}">
                          <span class="glyphicon glyphicon-menu-down"></span>
                      </c:if>
                  </c:when>
                  <c:otherwise>
                      ${midTabClassReplacement}
                  </c:otherwise>
              </c:choose>
          </div>
      </c:if>
    </div>


    <c:if test="${not empty tabDescription}">
        <div class="tabtable1-mid1">${tabDescription}</div>
    </c:if>

    <c:if test="${not empty rightSideHtmlProperty and not empty rightSideHtmlAttribute}">
        <div class="tabtable1-mid1"><img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" align="absmiddle" height="29" width="1" /><kul:htmlControlAttribute property="${rightSideHtmlProperty}" attributeEntry="${rightSideHtmlAttribute}" /></div>
    </c:if>

    <c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
        <div style="display: block;" id="tab-${tabKey}-div">
    </c:if>
    <c:if test="${isOpen != 'true' && isOpen != 'TRUE'}" >
        <div style="display: none;" id="tab-${tabKey}-div">
    </c:if>

        <c:if test="${not empty extraButtonSource}">
            <c:forTokens items="${extraButtonSource}" delims=";" var="token">
                <c:choose>
                    <c:when test="${fn:contains(token, 'property=')}" >
                        <c:set var="ebProperty" value="${fn:substringAfter(token, 'property=')}"/>
                    </c:when>
                    <c:when test="${fn:contains(token, 'src=')}" >
                        <c:set var="ebSrc" value="${fn:substringAfter(token, 'src=')}"/>
                    </c:when>
                    <c:when test="${fn:contains(token, 'value=')}" >
                        <c:set var="ebValue" value="${fn:substringAfter(token, 'value=')}"/>
                    </c:when>
                    <c:when test="${fn:contains(token, 'title=')}" >
                        <c:set var="ebTitle" value="${fn:substringAfter(token, 'title=')}"/>
                    </c:when>
                    <c:when test="${fn:contains(token, 'alt=')}" >
                        <c:set var="ebAlt" value="${fn:substringAfter(token, 'alt=')}"/>
                    </c:when>
                </c:choose>
            </c:forTokens>
            <div class="tabtable1-mid1">
                <c:choose>
                    <c:when test="${not empty ebSrc}">
                        <html:image property="${ebProperty}" src="${ConfigProperties.kr.externalizable.images.url}${ebSrc}" title="${ebTitle}" alt="${ebAlt}" styleClass="tinybutton" tabindex="-1" />
                    </c:when>
                    <c:otherwise>
                        <html:submit property="${ebProperty}" value="${ebValue}" title="${ebTitle}" alt="${ebAlt}" styleClass="btn btn-default" tabindex="-1" />
                    </c:otherwise>
                </c:choose>
            </div>
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
