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

<c:set var="isMaintenance" value="false" />
<c:set var="showDocumentInfo" value="false" />
<c:set var="docTitle" value="${kualiInquirable.title}" />
<c:set var="htmlFormAction" value="inquiry" />
<c:set var="renderMultipart" value="" />
<c:set var="showTabButtons" value="true" />
<c:set var="defaultMethodToCall" value="" />
<c:set var="additionalScriptFiles" value="" />
<c:set var="lookup" value="true" />
<c:set var="headerMenuBar" value="${kualiInquirable.htmlMenuBar}" />
<c:set var="headerTitle" value="Inquiry" />

<c:choose>
	<c:when test="${param.mode eq 'modal'}">
		<kul:pageBody showDocumentInfo="${showDocumentInfo}" docTitle="${docTitle}"
					  htmlFormAction="${htmlFormAction}" transactionalDocument="false"
					  renderMultipart="${renderMultipart}" showTabButtons="${showTabButtons}"
					  defaultMethodToCall="${defaultMethodToCall}" lookup="${lookup}"
					  headerMenuBar="${headerMenuBar}" alternativeHelp="${alternativeHelp}">

			<kul:inquiryPageBody />
		</kul:pageBody>
	</c:when>
	<c:otherwise>
		<kul:page showDocumentInfo="${showDocumentInfo}" docTitle="${docTitle}"
				  htmlFormAction="${htmlFormAction}" transactionalDocument="false"
				  renderMultipart="${renderMultipart}" showTabButtons="${showTabButtons}"
				  defaultMethodToCall="${defaultMethodToCall}" additionalScriptFiles="${additionalScriptFiles}"
				  lookup="${lookup}" headerMenuBar="${headerMenuBar}" headerTitle="${headerTitle}">

			<kul:inquiryPageBody />
		</kul:page>
	</c:otherwise>
</c:choose>
