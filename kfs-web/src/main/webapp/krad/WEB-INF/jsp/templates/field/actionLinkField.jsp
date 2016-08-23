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
<%@ include file="/krad/WEB-INF/jsp/tldHeader.jsp" %>

<tiles:useAttribute name="field"
                    classname="org.kuali.kfs.krad.uif.field.ActionField"/>

<%--
    HTML Link to Submit Form Via JavaScript

 --%>

<krad:attributeBuilder component="${field}"/>
<c:set var="pound" value="#"/>
<c:if test="${!empty field.navigateToPageId}">
  <c:set var="name" value="name=\"${field.navigateToPageId}\""/>
  <c:set var="href" value="href=\"${pound}${field.navigateToPageId}\""/>
</c:if>

<c:set var="tabindex" value="tabindex=0"/>
<c:if test="${field.skipInTabOrder}">
  <c:set var="tabindex" value="tabindex=-1"/>
</c:if>

<c:if test="${field.actionLabel != null}">
  <c:set var="imageRole" value="role='presentation'"/>
</c:if>

<krad:span component="${field}">

  <krad:fieldLabel field="${field}">

    <c:choose>
      <c:when test="${(field.actionImage != null) && field.actionImage.render}">
        <c:if test="${not empty field.actionImage.height}">
          <c:set var="height" value="height='${field.actionImage.height}'"/>
        </c:if>
        <c:if test="${not empty field.actionImage.width}">
          <c:set var="width" value="width='${field.actionImage.width}'"/>
        </c:if>
        <c:choose>
          <c:when test="${field.actionImageLocation != null && (field.actionImageLocation eq 'RIGHT')}">
            <a id="${field.id}" ${href}
               onclick="return false;"${name} ${style} ${styleClass} ${tabindex}>${field.actionLabel}
              <img ${imageRole}
                      class="actionImage rightActionImage ${field.actionImage.styleClassesAsString}" ${height} ${width}
                      style="${field.actionImage.style}" src="${field.actionImage.source}"
                      alt="${field.actionImage.altText}"/>
            </a>
          </c:when>
          <c:otherwise>
            <a id="${field.id}" ${href}
               onclick="return false;"${name} ${style} ${styleClass} ${tabindex}><img ${imageRole}
                    class="actionImage leftActionImage ${field.actionImage.styleClassesAsString}" ${height} ${width}
                    style="${field.actionImage.style}" src="${field.actionImage.source}"
                    alt="${field.actionImage.altText}"/>${field.actionLabel}
            </a>
          </c:otherwise>
        </c:choose>
      </c:when>
      <c:otherwise>
        <a id="${field.id}" ${href}
           onclick="return false;"${name} ${style} ${styleClass} ${tabindex}>${field.actionLabel}</a>
      </c:otherwise>
    </c:choose>

  </krad:fieldLabel>

</krad:span>
