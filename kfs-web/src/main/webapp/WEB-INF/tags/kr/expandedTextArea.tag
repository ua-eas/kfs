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
<%@ attribute name="textAreaFieldName" required="true" description="The name of the field populated by the text area window." %>
<%@ attribute name="action" required="true" description="The name of the action method to be filled in by the text area window." %>
<%@ attribute name="textAreaLabel" required="true" description="The label to render as part of the text area." %>
<%@ attribute name="disabled" required="false" description="Whether the text area control will be rendered as disabled." %>
<%@ attribute name="formKey" required="false" description="The unique key of the form which holds the document holding the field being populated by the text area." %>
<%@ attribute name="sessionDocument" required="false" description="An unused attribute." %>
<%@ attribute name="title" required="false" description="The title of the text area window." %>
<%@ attribute name="readOnly" required="false" description="Whether this field should be rendered as read only." %>
<%@ attribute name="maxLength" required="false" description="The maximum length of the text that can be entered into the text area." %>
<%@ attribute name="addClass" required="false" description="Additional class(es) to add to the image." %>

<c:if test="${empty formKey}">
  <c:set var="formKey" value="88888888" />
</c:if>


  <c:choose>
    <c:when test="${disabled}">
      <img class="nobord"
           src="${ConfigProperties.kr.externalizable.images.url}pencil_add1.png"
           alt="Expand Text Area">
    </c:when>
    <c:otherwise>
	    <c:choose>
	    	<c:when test="${readOnly}">
	           <c:set var="srcImage" value="${ConfigProperties.kr.externalizable.images.url}openreadonly_greenarrow01.png"/>
	           <c:set var="altMsg" value="View Text"/>
	    	</c:when>
	    	<c:otherwise>
	           <c:set var="readOnly" value="false" />
	           <c:set var="srcImage" value="${ConfigProperties.kr.externalizable.images.url}pencil_add.png"/>
	           <c:set var="altMsg" value="Expand Text Area"/>
	   		</c:otherwise>
	  	</c:choose>

       <html:image property="methodToCall.updateTextArea.((`${textAreaFieldName}:${action}:${textAreaLabel}:${readOnly}:${maxLength}`))"
                   src="${srcImage}"
                   onclick="javascript: textAreaPop('${textAreaFieldName}','${action}','${textAreaLabel}','${formKey}','${readOnly}','${maxLength}');return false"
                   styleClass="editicon ${addClass}"
                   title="${title}"
                   alt="${altMsg}"/>
    </c:otherwise>
  </c:choose>
