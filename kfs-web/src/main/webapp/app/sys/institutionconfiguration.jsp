<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   -
   - Copyright 2005-2014 The Kuali Foundation
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

<link href='${pageContext.request.contextPath}/css/institutionconfig.css' rel='stylesheet' type='text/css'>

<kul:page docTitle="Navigation Configuration" showDocumentInfo="false"
          headerTitle="Navigation Configuration" transactionalDocument="false"
          renderInnerDiv="true">

    <div class="main-panel">
        <div class="headerarea-small" id="headerarea-small">
            <h1><span class="glyphicon glyphicon-cog"></span>Navigation Configuration</h1>
        </div>

        <div id="institutionconfig"></div>
    </div>

    <script src="${pageContext.request.contextPath}/scripts/jquery-ui.min.js"></script>
    <script src="${pageContext.request.contextPath}/build/institutionconfig.bundle.js"></script>
</kul:page>