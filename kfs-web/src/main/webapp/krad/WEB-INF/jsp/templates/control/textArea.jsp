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
<%@ include file="/krad/WEB-INF/jsp/tldHeader.jsp" %>

<tiles:useAttribute name="control" classname="org.kuali.kfs.krad.uif.control.TextAreaControl"/>
<tiles:useAttribute name="field" classname="org.kuali.kfs.krad.uif.field.InputField"/>

<%--
    Standard HTML TextArea Input

 --%>

<form:textarea id="${control.id}" path="${field.bindingInfo.bindingPath}" disabled="${control.disabled}"
               rows="${control.rows}" cols="${control.cols}" readonly="${control.readOnly}"
               cssClass="${control.styleClassesAsString}" cssStyle="${control.style}"
               tabindex="${control.tabIndex}" maxLength="${control.maxLength}" minLength="${control.minLength}"/>

<c:if test="${(!empty control.watermarkText)}">
    <krad:script value="createWatermark('${control.id}', '${control.watermarkText}');"/>
</c:if>

<c:if test="${control.textExpand}">
    <krad:script value="setupTextPopout('${control.id}', '${field.labelField.labelText}', '${field.instructionalMessageField.messageText}', '${field.constraintMessageField.messageText}', '${ConfigProperties['krad.externalizable.images.url']}');"/>
</c:if>
