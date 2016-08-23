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
<%@ include file="tldHeader.jsp"%>
<html>
<head>

</head>
<body>
<%-- Below form used for non java script enabled browsers --%>
<form name="disabledJavaScriptReportForm" id="disabledJavaScriptReportForm" method="post" action="${workflowRouteReportUrl}">
  <c:forEach var="keyLabelPair" items="${noJavaScriptFormVariables}">
    <input type="hidden" name='${keyLabelPair.key}' value='<c:out value="${keyLabelPair.label}" escapeXml="true"/>'>
  </c:forEach>
  <noscript>
    Click this button to see the Routing Report:&nbsp;&nbsp;&nbsp;<input type="submit" value="View Report">
  </noscript>
</form>
<%-- Below forms used for java script enabled browsers --%>
<form name="backForm" id="backForm" method="post" action="${backUrlBase}">
  <c:forEach var="keyLabelPair" items="${backFormHiddenVariables}">
    <input type="hidden" name="${keyLabelPair.key}" value="${keyLabelPair.label}">
  </c:forEach>
</form>
<form name="routeReportForm" id="routeReportForm" method="post" action="${workflowRouteReportUrl}">
  <c:forEach var="keyLabelPair" items="${javaScriptFormVariables}">
    <input type="hidden" name='${keyLabelPair.key}' value='<c:out value="${keyLabelPair.label}" escapeXml="true"/>'>
  </c:forEach>
<script language ="javascript">
window.onload = dothis();
function dothis() {
  _win = window.open('', 'routereport');
  document.routeReportForm.target=_win.name;
  document.routeReportForm.submit();
  document.backForm.submit();
}
</script>
</form>
</body>
</html>
