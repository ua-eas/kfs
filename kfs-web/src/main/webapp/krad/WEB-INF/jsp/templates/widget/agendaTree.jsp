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
<%@ include file="/krad/WEB-INF/jsp/tldHeader.jsp"%>

<tiles:useAttribute name="widget" classname="org.kuali.kfs.krad.uif.widget.Tree"/>
<tiles:useAttribute name="componentId"/>

<%--
    TODO:
    TODO:
    TODO: This should be somewhere else.  In a KRMS web module?
    TODO:
    TODO:
--%>

<%-- KRAD doesn't support hidden input fields at present.  This is a workaround for it. --%>
<c:if test="${KualiForm.viewTypeName != 'MAINTENANCE'}">
  <input type="hidden" name="dataObject.selectedAgendaItemId" value="${KualiForm.dataObject.selectedAgendaItemId}" class="selectedAgendaItemId" />
</c:if>

<%--
    Invokes JS method to implement a tree plug-in.  see agendaTree.js
 --%>
<krad:script value="initAgendaTree('${componentId}');"/>
