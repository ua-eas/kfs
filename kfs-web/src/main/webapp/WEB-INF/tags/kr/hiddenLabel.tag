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
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>
<%@ tag body-content="empty" %>

<%@ attribute name="property" required="true" description="A control which renders a property as both a hidden field and as a property." %>

<%-- This tag is from textAttribute.tag -r 1.3's isLabel attribute.
When I refactored testAttribute.tag and textareaAttribute.tag into htmlControlAttribute.tag
for KULNRVSYS-872, this part of the tag didn't fit because it wasn't using the DataDictionary's
attribute entry, so I refactored this part into this separate tag.
I'm not sure what it's doing or what the following comment means. --%>

<%-- would be better to use the bean:write but had problems to get the KualiForm into property with JSTL
${KualiForm.document.budget.fringeRates[ctr].appointmentType.fringeRateAmount} --%>
<html:hidden property="${property}"/><bean:write name="KualiForm" property="${property}"/>

