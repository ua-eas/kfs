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

<div id="globalbuttons" class="globalbuttons">
    <c:if test="${KualiForm.canExport}">
        <html:submit value="Export" styleClass="btn btn-default" property="methodToCall.export" title="Perform Export" alt="Perform Export" />
    </c:if>
    <button class="globalbuttons btn btn-default" onclick="window.close()" title="close this window" alt="close this window">Close</button>

    <kul:stickyGlobalButtons/>
</div>
