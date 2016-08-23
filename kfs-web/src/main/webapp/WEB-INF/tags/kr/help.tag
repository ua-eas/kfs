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
<%@ attribute name="resourceKey" required="false" description="The resource key to use as help text." %>
<%@ attribute name="businessObjectClassName" required="false" description="The class of the business object the help text is assoicated with." %>
<%@ attribute name="attributeName" required="false" description="The name of the attribute which will have help text rendered for it; requires to be used in conjunction with the businessObjectClassName attribute." %>
<%@ attribute name="documentTypeName" required="false" description="The name of the document type to show help text for." %>
<%@ attribute name="pageName" required="false" description="The page of a specific document to show a help icon for." %>
<%@ attribute name="altText" required="false" description="The alternate text for the help icon." %>
<%@ attribute name="parameterNamespace" required="false" description="The namespace of the parameter to be used with the parameterName, parameterDetailType attributes to find the URL for the help text." %>
<%@ attribute name="parameterDetailType" required="false" description="The name of the parameter Detail which will be used with the parameterNamespace, ParameterName attributesto find the URL for the help text." %>
<%@ attribute name="parameterName" required="false" description="The name of the parameter which will be used with the parameterNamespace, parameterDetailType attributes to find the URL for the help text." %>
<%@ attribute name="searchDocumentTypeName" required="false" description="The document type name of a document being looked up to display help for." %>
<%@ attribute name="lookupBusinessObjectClassName" required="false" description="The business object looked up on the lookup page, which needs to have specific help displayed for it." %>
<%@ attribute name="alternativeHelp" required="false"%>
<%@ attribute name="alternativeHelpLabel" required="false" description="Text that will appear next to the ?" %>
<%@ attribute name="onClick" required="false" description="an optional onclick attribute to add" %>

<%--
  this tag is formatted to prevent any spaces occuring between the <a>/</a> tags and the <img> tag representing the help icon.  Internet
  explorer sometimes renders an underline (i.e. a hyperlinked space character) if there are extra spaces, and the formatting of this tag prevents those
  spaces from occuring.
--%>
<c:choose><
  c:when test="${(!empty alternativeHelp)}"
    ><a href="${alternativeHelp}"  target="_blank" title="[Help]${altText}" class="help-link" onclick="${onClick}"></c:when
><
  c:when test="${! empty resourceKey }"
    ><a href="${ConfigProperties.application.url}/kr/help.do?methodToCall=getResourceHelpText&amp;resourceKey=${resourceKey}" tabindex="-1" target="helpWindow" title="[Help]${altText}" class="help-link"></c:when
  ><
  c:when test="${(! empty businessObjectClassName) && (! empty attributeName) }"
    ><a href="${ConfigProperties.application.url}/kr/help.do?methodToCall=getAttributeHelpText&amp;businessObjectClassName=${businessObjectClassName}&amp;attributeName=${attributeName}" tabindex="-1" target="helpWindow"  title="[Help]${altText}" class="help-link"></c:when
  ><
  c:when test="${(! empty businessObjectClassName) && ( empty attributeName) }"
    ><a href="${ConfigProperties.application.url}/kr/help.do?methodToCall=getBusinessObjectHelpText&amp;businessObjectClassName=${businessObjectClassName}" tabindex="-1" target="helpWindow" title="[Help]${altText}" class="help-link"></c:when
  ><
  c:when test="${(! empty documentTypeName) && (! empty pageName) }"
    ><a href="${ConfigProperties.application.url}/kr/help.do?methodToCall=getPageHelpText&amp;documentTypeName=${documentTypeName}&amp;pageName=${pageName}" tabindex="-1" target="helpWindow"  title="[Help]${altText}" class="help-link"></c:when
  ><
  c:when test="${! empty documentTypeName }"
    ><a href="${ConfigProperties.application.url}/kr/help.do?methodToCall=getDocumentHelpText&amp;documentTypeName=${documentTypeName}" tabindex="-1" target="helpWindow"  title="[Help]${altText}" class="help-link"></c:when
  ><
  c:when test="${(! empty parameterNamespace) && (! empty parameterDetailType) && (! empty parameterName)}"
    ><a href="${ConfigProperties.application.url}/kr/help.do?methodToCall=getHelpUrlByNamespace&amp;helpParameterNamespace=${parameterNamespace}&amp;helpParameterDetailType=${parameterDetailType}&amp;helpParameterName=${parameterName}" tabindex="-1" target="helpWindow" class="help-link"></c:when
><
  c:when test="${(!empty searchDocumentTypeName)}"
    ><a href="${ConfigProperties.application.url}/kr/help.do?methodToCall=getLookupHelpText&amp;searchDocumentTypeName=${searchDocumentTypeName}" tabindex="-1" target="helpWindow" title="[Help]${altText}" class="help-link"></c:when
><
  c:when test="${(!empty lookupBusinessObjectClassName)}"
    ><a href="${ConfigProperties.application.url}/kr/help.do?methodToCall=getLookupHelpText&amp;lookupBusinessObjectClassName=${lookupBusinessObjectClassName}" tabindex="-1" target="helpWindow" title="[Help]${altText}" class="help-link"></c:when
>
</c:choose
><img src="${ConfigProperties.kr.externalizable.images.url}my_cp_inf.png" alt="[Help]${altText}" hspace=5 border=0  align="middle" class="help"/>
<c:if test="${not empty alternativeHelpLabel}">
    <span class="help-label">
        ${alternativeHelpLabel}
    </span>
</c:if>
</a>
