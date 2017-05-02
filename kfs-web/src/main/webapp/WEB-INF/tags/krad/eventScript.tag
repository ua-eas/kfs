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

<%@ attribute name="component" required="true"
              description="The UIF component for which the script will be generated"
              type="org.kuali.kfs.krad.uif.component.ScriptEventSupport"%>

<%-- creates the script event registration code for the events
supported and configured on the component --%>

<krad:buffer>
<jsp:attribute name="fragment">
  <c:if test="${component.supportsOnLoad && (!empty component.onLoadScript)}">
    jq('#' + '${component.id}').load(function() {
     ${component.onLoadScript}
    });
  </c:if>

  <c:if test="${component.supportsOnDocumentReady && (!empty component.onDocumentReadyScript)}">
    jq(document).ready(function() {
     ${component.onDocumentReadyScript}
    });
  </c:if>

  <c:if test="${component.supportsOnUnload && (!empty component.onUnloadScript)}">
    jq('#' + '${component.id}').unload(function() {
     ${component.onUnloadScript}
    });
  </c:if>

  <c:if test="${component.supportsOnBlur && (!empty component.onBlurScript)}">
    jq('#' + '${component.id}').blur(function() {
     ${component.onBlurScript}
    });
  </c:if>

  <c:if test="${component.supportsOnChange && (!empty component.onChangeScript)}">
    jq('#' + '${component.id}').change(function() {
     ${component.onChangeScript}
    });
  </c:if>

  <c:if test="${component.supportsOnClick && (!empty component.onClickScript)}">
    jq('#' + '${component.id}').click(function(e) {
     ${component.onClickScript}
    });
  </c:if>

  <c:if test="${component.supportsOnDblClick && (!empty component.onDblClickScript)}">
    jq('#' + '${component.id}').dblclick(function() {
     ${component.onDblClickScript}
    });
  </c:if>

  <c:if test="${component.supportsOnFocus && (!empty component.onFocusScript)}">
    jq('#' + '${component.id}').focus(function() {
     ${component.onFocusScript}
    });
  </c:if>

  <c:if test="${component.supportsOnKeyPress && (!empty component.onKeyPressScript)}">
    jq('#' + '${component.id}').keypress(function() {
     ${component.onKeyPressScript}
    });
  </c:if>

  <c:if test="${component.supportsOnKeyUp && (!empty component.onKeyUpScript)}">
    jq('#' + '${component.id}').keyup(function() {
     ${component.onKeyUpScript}
    });
  </c:if>

  <c:if test="${component.supportsOnKeyDown && (!empty component.onKeyDownScript)}">
    jq('#' + '${component.id}').keydown(function() {
     ${component.onKeyDownScript}
    });
  </c:if>

  <c:if test="${component.supportsOnKeyDown && (!empty component.onKeyDownScript)}">
    jq('#' + '${component.id}').keydown(function() {
     ${component.onKeyDownScript}
    });
  </c:if>

  <c:if test="${component.supportsOnMouseOver && (!empty component.onMouseOverScript)}">
    jq('#' + '${component.id}').mouseover(function() {
     ${component.onMouseOverScript}
    });
  </c:if>

  <c:if test="${component.supportsOnMouseOut && (!empty component.onMouseOutScript)}">
    jq('#' + '${component.id}').mouseout(function() {
     ${component.onMouseOutScript}
    });
  </c:if>

  <c:if test="${component.supportsOnMouseUp && (!empty component.onMouseUpScript)}">
    jq('#' + '${component.id}').mouseup(function() {
     ${component.onMouseUpScript}
    });
  </c:if>

  <c:if test="${component.supportsOnMouseDown && (!empty component.onMouseDownScript)}">
    jq('#' + '${component.id}').mousedown(function() {
     ${component.onMouseDownScript}
    });
  </c:if>

  <c:if test="${component.supportsOnMouseMove && (!empty component.onMouseMoveScript)}">
    jq('#' + '${component.id}').mousemove(function() {
     ${component.onMouseMoveScript}
    });
  </c:if>
</jsp:attribute>
</krad:buffer>

<c:if test="${!empty fn:trim(bufferOut)}">
    <krad:script value="${fn:trim(bufferOut)}" />
</c:if>
