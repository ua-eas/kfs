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

<%@ attribute name="field" required="true" type="org.kuali.kfs.kns.web.ui.Field" description="The field to render as read only." %>
<%@ attribute name="addHighlighting" required="false"
              description="boolean indicating if this field should be highlighted (to indicate old/new change)" %>
<%@ attribute name="isLookup" required="false"
              description="boolean indicating if this is a Lookup Screen" %>

<c:set var="result">
    <c:choose>
      <c:when test="${field.secure}">
        <c:out value="${field.displayMaskValue}"/>
      </c:when>
      <c:when test="${(field.fieldType==field.DROPDOWN or field.fieldType==field.DROPDOWN_REFRESH or field.fieldType==field.DROPDOWN_SCRIPT or field.fieldType==field.RADIO) && empty field.additionalDisplayPropertyName}">
        <c:set var="value_found" value="false" />
        <c:forEach items="${field.fieldValidValues}" var="select">
          <c:if test="${field.propertyValue==select.key}">
            <c:if test="${!value_found}">
              <c:out value="${select.value}" />
              <c:if test="${isLookup}">
      		    <input type="hidden" name="${field.propertyName}" value="<c:out value="${field.propertyValue}"/>" />
		      </c:if>
              <c:set var="value_found" value="true" />
            </c:if>
          </c:if>
        </c:forEach>
        <c:if test="${!value_found}">
			<c:forEach items="${field.fieldInactiveValidValues}" var="select">
	          <c:if test="${field.propertyValue==select.key}">
	            <c:if test="${!value_found}">
	              <c:out value="${select.value}" />
                  <c:if test="${isLookup}">
      		        <input type="hidden" name="${field.propertyName}" value="<c:out value="${field.propertyValue}"/>" />
		          </c:if>
	              <c:set var="value_found" value="true" />
	            </c:if>
	          </c:if>
	        </c:forEach>
        </c:if>
        <c:if test="${!value_found}">
          <c:out value="${KualiForm.unconvertedValues[field.propertyName]}" default="${field.propertyValue}" />
          <c:if test="${isLookup}">
      		<input type="hidden" name="${field.propertyName}" value="<c:out value="${field.propertyValue}"/>" />
		  </c:if>
        </c:if>
      </c:when>
      <c:when test="${field.fieldType==field.TEXT_AREA}">
        <c:out value="${KualiForm.unconvertedValues[field.propertyName]}" default="${field.propertyValue}" />

      	<c:if test="${isLookup}">
      		<input type="hidden" name="${field.propertyName}"
						value="<c:out value="${field.propertyValue}"/>" />
		</c:if>
      </c:when>
      <c:otherwise>
        <c:out value="${KualiForm.unconvertedValues[field.propertyName]}" default="${field.propertyValue}" />

		<c:if test="${isLookup}">
      		<input type="hidden" name="${field.propertyName}"
						value="<c:out value="${field.propertyValue}"/>" />
		</c:if>
      </c:otherwise>
    </c:choose>
</c:set>

<c:choose>
  <c:when test="${!empty field.alternateDisplayPropertyName && !field.secure}">${field.alternateDisplayPropertyValue}</c:when>
  <c:when test="${empty result}">&nbsp;</c:when>
  <c:otherwise>${result}</c:otherwise>
</c:choose>

<c:if test="${!empty field.additionalDisplayPropertyName && !field.secure}"> *-* ${field.additionalDisplayPropertyValue}</c:if>

<c:if test="${addHighlighting && field.highlightField}">
   <kul:fieldShowChangedIcon />
</c:if>
