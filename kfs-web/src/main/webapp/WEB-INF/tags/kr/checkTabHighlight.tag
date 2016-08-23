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


<%@ attribute name="rows" required="true" type="java.util.List"
        description="rows containing fields to iterate through and check for a highlighted field" %>
<%@ attribute name="addHighlighting" required="false"
              description="boolean to add HTML tags if this field should be highlighted (to indicate old/new change)" %>

<%-- iterates through the list of rows, and each row's fields, if the field is marked as highlighted, meaning has changed
on maintenance document, then sets a var to indicate the tab should be highlighted as well --%>
<c:set var="tabHighlight" value="false" scope="request"/>

<c:if test="${addHighlighting}">
  <c:forEach items="${rows}" var="row">
    <c:forEach items="${row.fields}" var="field">
        <c:if test="${(field.fieldType eq field.CONTAINER) && !tabHighlight}" >
           <%-- cannot refer to recursive tag (checkTabHighlight) using kul alias or Jetty 7 will have jsp compilation errors on Linux --%>
           <c:set var="_rows" value="${field.containerRows}" scope="request" />
           <c:set var="_addHighlighting" value="${addHighlighting}" scope="request" />
           <c:import url="/WEB-INF/jsp/recurseCheckTabHighlight.jsp" />
        </c:if>

  	    <c:if test="${field.highlightField}">
          <c:set var="tabHighlight" value="true" scope="request"/>
  	    </c:if>
  	</c:forEach>
  </c:forEach>
</c:if>
