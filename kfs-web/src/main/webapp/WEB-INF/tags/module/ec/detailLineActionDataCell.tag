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

<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="action" required="true" description="the name of action that could be rendered" %>
<%@ attribute name="buttonValue" required="true" description="the button text of the given action" %>
<%@ attribute name="buttonClass" required="false" description="the class to be added to the action button" %>

<html:submit
        property="methodToCall.${action}"
        title="${action}"
        alt="${action}"
        styleClass="btn btn-default ${buttonClass}"
        value="${buttonValue}"/>
