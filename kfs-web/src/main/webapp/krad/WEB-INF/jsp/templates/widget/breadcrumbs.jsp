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

<tiles:useAttribute name="widget"
                    classname="org.kuali.kfs.krad.uif.widget.BreadCrumbs"/>

<c:set var="current" value="${KualiForm.formHistory.generatedCurrentBreadcrumb}"/>
<c:set var="crumbs" value="${KualiForm.formHistory.generatedBreadcrumbs}"/>

<%--Create the breadcrumbs using the generatedBreadcrumbs from history, note that current
is omitted by default, but the link to it is still present, it can be shown as a clickable
link again through jquery as in setPageBreadcrumb when needed --%>
<c:if test="${(fn:length(crumbs) >= 1) || (widget.displayBreadcrumbsWhenOne && fn:length(crumbs) == 0)}">
    <label id="breadcrumb_label" class="offScreen">Breadcrumbs</label>
    <span class="${widget.styleClassesAsString}">
    <ol id="breadcrumbs" role="navigation" aria-labelledby="breadcrumb_label">
      <c:forEach var="entry" items="${crumbs}">
          <li><a href="${entry.url}">${entry.title}</a><span role="presentation"> &raquo; </span></li>
      </c:forEach>
      <span class="kr-current" id="current_breadcrumb_span">${current.title}</span>
      <a style="display:none;" id="current_breadcrumb_anchor" href="${current.url}"/>${current.title}</a>
    </ol>
  </span>
</c:if>
