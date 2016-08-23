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

<tiles:useAttribute name="control" classname="org.kuali.kfs.krad.uif.control.SpinnerControl "/>
<tiles:useAttribute name="field" classname="org.kuali.kfs.krad.uif.field.InputField"/>

<%--
    Create Standard HTML Text Input then decorates with Spinner plugin

 --%>

<tiles:insertTemplate template="/krad/WEB-INF/jsp/templates/control/text.jsp">
  <tiles:putAttribute name="control" value="${field.control}"/>
  <tiles:putAttribute name="field" value="${field}"/>
</tiles:insertTemplate>

<krad:script value="createSpinner('${control.id}', ${control.spinner.componentOptionsJSString});" />


