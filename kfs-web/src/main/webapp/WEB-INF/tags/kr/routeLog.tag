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
<kul:tab tabTitle="Route Log" defaultOpen="false">
<div class="tab-container" align=center>
	<c:if test="${ConfigProperties.test.mode ne 'true'}">
	  <iframe onload="setRouteLogIframeDimensions();" name="routeLogIFrame" id="routeLogIFrame" src="${ConfigProperties.workflow.url}/RouteLog.do?documentId=${KualiForm.workflowDocument.documentId}" height="500" width="95%" hspace='0' vspace='0' frameborder='0' title='Workflow Route Log for document id: ${KualiForm.workflowDocument.documentId}'>
	  </iframe>
	</c:if>
</div>
</kul:tab>
