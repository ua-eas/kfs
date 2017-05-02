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
<%@ include file="/krad/WEB-INF/jsp/tldHeader.jsp"%>

<%@ tag dynamic-attributes="templateParameters"%>

<%@ attribute name="component" required="true"
	description="The UIF component for which the template will be generated"
	type="org.kuali.kfs.krad.uif.component.Component"%>
<%@ attribute name="body" required="false"
	description="If the template takes a body (wraps content) that content should be passed with this parameter" %>

<c:if test="${empty body}">
  <c:set var="body" value=""/>
</c:if>

<%-- verify the component is not null and should be rendered --%>

<%-- check to see if the component should render, if this has progressiveDisclosure and not getting disclosed via ajax
still render, but render in a hidden container --%>
<c:if test="${!empty component && (component.render || (!component.render && !component.progressiveRenderViaAJAX && !empty component.progressiveRender))}">

	<c:choose>
		<c:when	test="${!component.render && !component.progressiveRenderViaAJAX && !empty component.progressiveRender}">
			<div style="display: none;" id="${component.id}_refreshWrapper" class="refreshWrapper">
		</c:when>
    <c:when test="${!empty component.progressiveRender || !empty component.conditionalRefresh || !empty component.refreshWhenChanged || component.refreshedByAction}">
      <div id="${component.id}_refreshWrapper" class="refreshWrapper">
    </c:when>
    <c:when test="${component.hidden}">
      <div style="display: none;">
    </c:when>
	</c:choose>

	<c:choose>
		<%-- for self rendered components, write out render output --%>
		<c:when test="${component.selfRendered}">
	        ${component.renderOutput}
	  </c:when>

		<%-- render component through template --%>
		<c:otherwise>
			<tiles:insertTemplate template="${component.template}">
				<tiles:putAttribute name="${component.componentTypeName}" value="${component}" />
        <tiles:putAttribute name="body"	value="${body}" />
				<c:forEach items="${templateParameters}" var="parameter">
					<tiles:putAttribute name="${parameter.key}"	value="${parameter.value}" />
				</c:forEach>
			</tiles:insertTemplate>
		</c:otherwise>
	</c:choose>

	<%-- generate event code for component --%>
	<krad:eventScript component="${component}" />

	<c:if test="${!empty component.progressiveRender || !empty component.conditionalRefresh || !empty component.refreshWhenChanged
	              || component.refreshedByAction || component.hidden}">
		</div>
	</c:if>
</c:if>

<c:if test="${(!empty component) && (!empty component.progressiveRender)}">
	<%-- For progressive rendering requiring an ajax call, put in place holder div --%>
	<c:if test="${!component.render && (component.progressiveRenderViaAJAX || component.progressiveRenderAndRefresh)}">
		<div id="${component.id}_refreshWrapper" class="unrendered refreshWrapper"	style="display: none;"></div>
	</c:if>

	<%-- setup progressive handlers for each control which may satisfy a disclosure condition --%>
	<c:forEach items="${component.progressiveDisclosureControlNames}" var="cName">
		<krad:script
			value="var condition = function(){return (${component.progressiveDisclosureConditionJs});};
			setupProgressiveCheck(&quot;${cName}&quot;, '${component.id}', '${component.factoryId}', condition, ${component.progressiveRenderAndRefresh}, '${component.refreshDiscloseMethodToCall}');" />
	</c:forEach>
	<krad:script value="hiddenInputValidationToggle('${component.id}_refreshWrapper');" />
</c:if>

<%-- Conditional Refresh setup --%>
<c:if test="${!empty component.conditionalRefresh}">
	<c:forEach items="${component.conditionalRefreshControlNames}" var="cName">
		<krad:script
			value="var condition = function(){return (${component.conditionalRefreshConditionJs});};
		setupRefreshCheck(&quot;${cName}&quot;, '${component.id}', '${component.factoryId}', condition, '${component.refreshDiscloseMethodToCall}');" />
	</c:forEach>
</c:if>

<%-- Refresh when changed setup --%>
<c:if test="${!empty component.refreshWhenChanged}">
	<c:forEach items="${component.refreshWhenChangedControlNames}" var="cName">
		<krad:script value="setupOnChangeRefresh(&quot;${cName}&quot;, '${component.id}', '${component.factoryId}', '${component.refreshDiscloseMethodToCall}');" />
	</c:forEach>
</c:if>

