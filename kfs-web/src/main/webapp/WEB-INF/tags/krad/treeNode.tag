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
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="node" required="true"
              description="Node instance in the tree to be rendered"
              type="org.kuali.rice.core.api.util.tree.Node"%>

<li id="${node.data.id}" class="${node.nodeType}">
   <a href="#" class="${node.nodeType}">
       <krad:template component="${node.nodeLabel}"/>
   </a>
   <krad:template component="${node.data}" />
   <c:if test="${(node.children != null) and (fn:length(node.children) > 0)}">
   <ul>
      <c:forEach items="${node.children}" var="childNode" varStatus="itemVarStatus">
         <%-- ran into recursive tag problem on Linux, see KULRICE-5161 --%>
         <%-- krad:treeNode node="${childNode}" / --%>
         <%@ taglib tagdir="/WEB-INF/tags/krad" prefix="kul2"%>
         <kul2:containerTreeNode node="${childNode}"/>
      </c:forEach>
   </ul>
   </c:if>

</li>

