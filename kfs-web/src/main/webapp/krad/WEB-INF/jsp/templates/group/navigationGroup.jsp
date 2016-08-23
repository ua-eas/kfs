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

<tiles:useAttribute name="group" classname="org.kuali.kfs.krad.uif.container.NavigationGroup"/>
<tiles:useAttribute name="currentPageId"/>

<%-- renders standard unordered list and calls doNavigation function --%>

<!----------------------------------- #NAVIGATION --------------------------------------->
<krad:div component="${group}">
  <%-- render items in list --%>
  <ul id="${group.id}" role="navigation">
    <c:forEach items="${group.items}" var="item" varStatus="itemVarStatus">
      <li>
         <krad:template component="${item}"/>
      </li>
    </c:forEach>
  </ul>
</krad:div>

<krad:script value="
  var options = ${group.componentOptionsJSString};
  options.currentPage = '${currentPageId}';
  createNavigation('${group.id}', '${group.navigationType}', options);
"/>
