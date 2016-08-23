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
<c:set var="showTabButtons" value="" />
<c:set var="defaultMethodToCall" value="" />
<c:set var="additionalScriptFiles" value="" />
<c:set var="lookup" value="true" />
<c:set var="headerMenuBar" value="${kualiInquirable.htmlMenuBar}" />
<c:set var="headerTitle" value="Inquiry" />

<kul:page showDocumentInfo="${showDocumentInfo}" docTitle="${docTitle}"
	htmlFormAction="${htmlFormAction}" transactionalDocument="false"
	renderMultipart="${renderMultipart}" showTabButtons="${showTabButtons}"
	defaultMethodToCall="${defaultMethodToCall}" additionalScriptFiles="${additionalScriptFiles}"
	lookup="${lookup}" headerMenuBar="${headerMenuBar}" headerTitle="${headerTitle}">

<%-- Put the header on the page. --%>

	<div id="workarea">
		<%-- settting FieldSections to KualiForm.sections --%>
		<c:set var="FieldSections" value="${KualiForm.sections}" />
		<div class="headerarea-small" id="headerarea-small">
			<h1>${kualiInquirable.title}</h1>
		</div>
	</div>

		<kul:tableWrapper>
		<%-- Show the information about the business object. --%>
		<c:set var="firstTab" value="${true}" /><%-- make the background transparent in kul:tab for the first pass --%>
		<br />
		<c:forEach items="${FieldSections}" var="section">

		  <%-- call helper tag to look ahead through fields for old to new changes, and highlight tab if so --%>
          <kul:checkTabHighlight rows="${section.rows}" addHighlighting="false" />

		  <kul:tab tabTitle="${section.sectionTitle}" defaultOpen="true" tabErrorKey="${section.errorKey}" highlightTab="${tabHighlight}" transparentBackground="${firstTab}">
		    <div class="tab-container" align="center">
		      <table width="100%" cellpadding=0 cellspacing=0 class="datatable">
			     <kul:rowDisplay rows="${section.rows}" numberOfColumns="${section.numberOfColumns}" />
			  </table>
	        </div>
		  </kul:tab>

		  <c:set var="firstTab" value="${false}" /><%-- make the background opaque after first pass --%>
		</c:forEach>
		</kul:tableWrapper>


        <kul:directInquiryClose />


</kul:page>
