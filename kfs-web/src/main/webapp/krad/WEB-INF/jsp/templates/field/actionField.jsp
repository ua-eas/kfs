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

<tiles:useAttribute name="field"
                    classname="org.kuali.kfs.krad.uif.field.ActionField"/>

<%--
    Standard HTML Input Submit - will create an input of type submit or type image if the action
    image field is configured

 --%>
<c:if test="${field.skipInTabOrder}">
    <c:set var="tabindex" value="tabindex=-1"/>
</c:if>

<c:if test="${field.actionImage != null}">
    <c:if test="${not empty field.actionImage.height}">
        <c:set var="height" value="height='${field.actionImage.height}'"/>
    </c:if>
    <c:if test="${not empty field.actionImage.width}">
        <c:set var="width" value="width='${field.actionImage.width}'"/>
    </c:if>
</c:if>

<c:if test="${field.disabled}">
    <c:set var="disabled" value="disabled=\"true\""/>
</c:if>

<c:choose>

    <c:when test="${(field.actionImage != null) && field.actionImage.render && (empty field.actionImageLocation || field.actionImageLocation eq 'IMAGE_ONLY')}">
        <krad:attributeBuilder component="${field.actionImage}"/>

        <span id="${field.id}_span">
      <krad:fieldLabel field="${field}">

        <input type="image" id="${field.id}" ${disabled}
               src="${field.actionImage.source}"
               alt="${field.actionImage.altText}" ${height} ${width} ${style} ${styleClass} ${title} ${tabindex} />

      </krad:fieldLabel>
    </span>
    </c:when>
    <c:otherwise>
        <krad:attributeBuilder component="${field}"/>

        <c:choose>
            <c:when test="${not empty field.actionImageLocation && (field.actionImage != null) && field.actionImage.render}">

                <c:choose>
                    <c:when test="${(field.actionImageLocation eq 'TOP')}">
                        <button id="${field.id}" ${style} ${styleClass} ${title} ${disabled}><span
                                class="topBottomSpan"><img ${height} ${width}
                                class="actionImage topActionImage ${field.actionImage.styleClassesAsString}"
                                style="${field.actionImage.style}"
                                src="${field.actionImage.source}"
                                alt="${field.actionImage.altText}"/></span>${field.actionLabel}
                        </button>
                    </c:when>
                    <c:when test="${(field.actionImageLocation eq 'BOTTOM')}">
                        <button id="${field.id}" ${style} ${styleClass} ${title} ${disabled}>${field.actionLabel}<span
                                class="topBottomSpan"><img ${height} ${width}
                                style="${field.actionImage.style}"
                                class="actionImage bottomActionImage ${field.actionImage.styleClassesAsString}"
                                src="${field.actionImage.source}"
                                alt="${field.actionImage.altText}"/></span></button>
                    </c:when>
                    <c:when test="${(field.actionImageLocation eq 'RIGHT')}">
                        <button id="${field.id}" ${style} ${styleClass} ${title} ${disabled}>${field.actionLabel}<img ${height} ${width}
                                style="${field.actionImage.style}"
                                class="actionImage rightActionImage ${field.actionImage.styleClassesAsString}"
                                src="${field.actionImage.source}" alt="${field.actionImage.altText}"/></button>
                    </c:when>
                    <c:when test="${(field.actionImageLocation eq 'LEFT')}">
                        <button id="${field.id}" ${style} ${styleClass} ${title} ${disabled}><img ${height} ${width}
                                style="${field.actionImage.style}"
                                class="actionImage leftActionImage ${field.actionImage.styleClassesAsString}"
                                src="${field.actionImage.source}"
                                alt="${field.actionImage.altText}"/>${field.actionLabel}
                        </button>
                    </c:when>
                    <c:otherwise>
                        <button id="${field.id}" ${style} ${styleClass} ${title} ${disabled}>${field.actionLabel}</button>
                    </c:otherwise>
                </c:choose>
            </c:when>
            <c:otherwise>
                <button id="${field.id}" ${style} ${styleClass} ${title} ${disabled}>${field.actionLabel}</button>
            </c:otherwise>
        </c:choose>

    </c:otherwise>
</c:choose>

<c:if test="${(field.lightBoxLookup != null)}">
    <krad:template component="${field.lightBoxLookup}" componentId="${field.id}"/>
</c:if>
