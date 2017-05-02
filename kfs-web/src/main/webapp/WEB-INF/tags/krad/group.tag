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
<%@ include file="/krad/WEB-INF/jsp/tldHeader.jsp"%>

<%@ attribute name="group" required="true"
              description="The group instance that is being rendered"
              type="org.kuali.kfs.krad.uif.container.Group"%>
<%@ attribute name="groupBodyIdSuffix" required="false"
              description="String to append to the div id for the group body" %>

<c:if test="${empty groupBodyIdSuffix}">
    <c:set var="groupBodyIdSuffix" value="_group"/>
</c:if>

<%-- Standard wrapper for group templates --%>
<krad:div component="${group}">

  <!----------------------------------- #GROUP '${group.id}' HEADER --------------------------------------->
  <c:if test="${!empty group.header}">
    <krad:template component="${group.header}"/>
  </c:if>

  <div id="${group.id}${groupBodyIdSuffix}">
    <krad:template component="${group.instructionalMessageField}"/>
    <krad:template component="${group.errorsField}"/>

    <jsp:doBody/>

    <!----------------------------------- #GROUP '${group.id}' FOOTER --------------------------------------->
    <c:if test="${!empty group.footer}">
      <krad:template component="${group.footer}"/>
    </c:if>
  </div>

</krad:div>

<%-- render group disclosure --%>
<krad:template component="${group.disclosure}" parent="${group}"/>
