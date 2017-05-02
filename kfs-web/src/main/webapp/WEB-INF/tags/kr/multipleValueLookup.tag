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

<%@ attribute name="boClassName" required="true" description="The name of the class to look up instances of in the linked lookup." %>
<%@ attribute name="lookupParameters" required="false" description="On return from lookup, these parameters describe which attributes of the business object to populate in the lookup parent." %>
<%@ attribute name="tabindexOverride" required="false" description="Overrides the tab index of this lookup icon." %>
<%@ attribute name="anchor" required="false" description="If present, on return from lookup, the page will be scrolled to the named anchor specified by this attribute." %>
<%@ attribute name="lookedUpBODisplayName" required="false" description="this value is the human readable name of the BO being looked up" %>
<%@ attribute name="lookedUpCollectionName" required="true" description="the name of the collection being looked up (perhaps on a document collection), this value will be returned to the calling document" %>
<%@ attribute name="autoSearch" required="false" description="Determines whether auto-search will be used in the lookup." %>

<c:choose>
  <c:when test="${!empty tabindexOverride}">
    <c:set var="tabindex" value="${tabindexOverride}"/>
  </c:when>
  <c:otherwise>
    <c:set var="tabindex" value="${KualiForm.nextArbitrarilyHighIndex}"/>
  </c:otherwise>
</c:choose>

<c:if test="${!empty lookedUpBODisplayName}">
  <bean-el:message key="multiple.value.lookup.icon.label" arg0="${lookedUpBODisplayName}"/>
</c:if>
<c:set var="epMethodToCallAttribute" value="methodToCall.performLookup.(!!${boClassName}!!).((`${lookupParameters}`)).(:;${lookedUpCollectionName};:).((%true%)).((~${autoSearch}~)).anchor${anchor}"/>
${kfunc:registerEditableProperty(KualiForm, epMethodToCallAttribute)}
<input type="image" tabindex="${tabindex}" name="${epMethodToCallAttribute}"
   src="${ConfigProperties.kr.externalizable.images.url}searchicon.png" border="0" class="tinybutton searchicon" valign="middle" alt="Multiple Value Search on ${lookedUpBODisplayName}" title="Multiple Value Search on ${lookedUpBODisplayName}" />
