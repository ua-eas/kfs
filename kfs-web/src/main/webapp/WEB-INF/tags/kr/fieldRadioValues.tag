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

<%@ attribute name="field" required="true" type="org.kuali.kfs.kns.web.ui.Field" description="The field to render radio button options for." %>
<%@ attribute name="onblur" required="false" description="Javascript code which will be executed with the input field's onblur event is triggered." %>
<%@ attribute name="onchange" required="false" description="Javascript code which will be executed with the input field's onchange event is triggered." %>
<%@ attribute name="tabIndex" required="false" description="Tab index to use for next field" %>

${kfunc:registerEditableProperty(KualiForm, field.propertyName)}
<c:forEach items="${field.fieldValidValues}" var="radio">
  <c:if test="${!empty radio.value}">
    <input type="radio"
        ${field.propertyValue eq radio.key ? 'checked="checked"' : ''}
        name='${field.propertyName}'
        id='${field.propertyName}${radio.value}'
        value='<c:out value="${radio.key}" />'
		title='${field.fieldLabel} - ${radio.value}'
        onblur="${onblur}" onchange="${onchange}" tabIndex="${tabIndex}"/>
    <label for='${field.propertyName}${radio.value}'><c:out value="${radio.value}"/></label>
  </c:if>

  <c:set var="tabIndex" value="${KualiForm.currentTabIndex}"/>
  <c:set var="dummyIncrementVar" value="${kfunc:incrementTabIndex(KualiForm, tabIndex)}" />
</c:forEach>
