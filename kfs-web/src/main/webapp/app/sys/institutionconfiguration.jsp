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

<style>
   #sortable { list-style-type: none; margin: 0; padding: 0; width: 60%; }
   #sortable li { margin: 0 3px 3px 3px; padding: 0.4em; padding-left: 1.5em; font-size: 1.4em; height: 18px; }
   #sortable li span { position: absolute; margin-left: -1.3em; }
</style>

<kul:page docTitle="Navigation Configuration" showDocumentInfo="false"
          headerTitle="Navigation Configuration" transactionalDocument="false"
          renderInnerDiv="true">
    <script src="${pageContext.request.contextPath}/scripts/jquery-ui.min.js"></script>
    <script src="${pageContext.request.contextPath}/build/institutionconfig.bundle.js"></script>
</kul:page>