<%--
 Copyright 2007 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
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
