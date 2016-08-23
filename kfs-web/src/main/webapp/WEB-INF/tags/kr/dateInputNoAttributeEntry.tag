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

<%@ attribute name="property" required="true" description="The property associated that should have a date input rendered for it." %>
<%@ attribute name="size" required="true" description="The size of the HTML text field rendered for this date input." %>
<%@ attribute name="maxLength" required="true" description="The maximum length of the input the HTML text field rendered for this date input will hold." %>

<%@ attribute name="accessibilityHint" required="false"
        description="Use this to attach further information to the title attribute of a field
        if present"%>

<kul:checkErrors keyMatch="${property}"/>

<c:choose>
	<c:when test="${!empty accessibilityHint}">
		<html:text property="${property}" styleId="${property}" size="${size}" maxlength="${maxLength}" style="${textStyle}" alt="${accessibilityHint}" title="${accessibilityHint}" />
	</c:when>
	<c:otherwise>
		<html:text property="${property}" styleId="${property}" size="${size}" maxlength="${maxLength}" style="${textStyle}"/>
	</c:otherwise>
</c:choose>
<c:if test="${hasErrors==true}">
  <kul:fieldShowErrorIcon />
</c:if>
<img src="${ConfigProperties.kr.externalizable.images.url}cal.png" width="24" id="${property}_datepicker" style="cursor: pointer;" alt="Date selector" title="Date selector" />
<script type="text/javascript">
	Calendar.setup(
	{
    	inputField : "${property}", // ID of the input field
    	ifFormat : "%m/%d/%Y", // the date format
    	button : "${property}_datepicker" // ID of the button
    }
    );
</script>

