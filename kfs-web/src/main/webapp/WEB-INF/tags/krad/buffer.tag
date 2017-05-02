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

<%@ attribute name="fragment" required="true"
              description="The JSP fragment code that should be evaluated and returned"
              fragment="true"%>

<%@ variable name-given="bufferOut" scope="AT_END" %>

<%-- Evaluates the given fragments and places the result into a variable that can
then be read by the calling template. This gives a mechanism for templates to buffer JSP
code --%>

<jsp:invoke var="bufferOut" fragment="fragment" />
