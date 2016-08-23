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
<%@ page import="org.kuali.kfs.krad.exception.KualiExceptionIncident"%>
<%@ include file="tldHeader.jsp"%>

<c:set var="textAreaAttributes"
	value="${DataDictionary.AttributeReferenceElements.attributes}" />

<c:set var="parameters" value="<%=request.getAttribute(\"KualiExceptionHandlerAction\")%>" />

<c:if test="${not empty parameters}">
	<c:set var="documentId"       value="${parameters.documentId}" />
	<c:set var="userEmail"        value="${parameters.userEmail}" />
	<c:set var="userName"         value="${parameters.userName}" />
	<c:set var="principalName"         value="${parameters.principalName}" />
	<c:set var="componentName"    value="${parameters.componentName}" />
	<c:set var="exceptionReportSubject" value="${parameters.exceptionReportSubject}" />
	<c:set var="exceptionMessage" value="${parameters.exceptionMessage}" />
	<c:set var="displayMessage"   value="${parameters.displayMessage}" />
	<c:set var="stackTrace"       value="${parameters.stackTrace}" />
	<c:set var="exceptionHideIncidentReport" value="${parameters.exceptionHideIncidentReport}" />

</c:if>
<c:if test="${empty documentId}">
	<c:set var="documentId"
       value="<%=request.getParameter(KualiExceptionIncident.DOCUMENT_ID)%>" />
</c:if>
<c:if test="${empty userEmail}">
	<c:set var="userEmail"
       value="<%=request.getParameter(KualiExceptionIncident.USER_EMAIL)%>" />
</c:if>
<c:if test="${empty userName}">
	<c:set var="userName"
       value="<%=request.getParameter(KualiExceptionIncident.USER_NAME)%>" />
</c:if>
<c:if test="${empty principalName}">
	<c:set var="principalName"
       value="<%=request.getParameter(KualiExceptionIncident.UUID)%>" />
</c:if>
<c:if test="${empty componentName}">
	<c:set var="componentName"
       value="<%=request.getParameter(KualiExceptionIncident.COMPONENT_NAME)%>" />
</c:if>
<c:if test="${empty exceptionReportSubject}">
	<c:set var="exceptionReportSubject"
       value="<%=request.getParameter(KualiExceptionIncident.EXCEPTION_REPORT_SUBJECT)%>" />
</c:if>
<c:if test="${empty exceptionMessage}">
	<c:set var="exceptionMessage"
       value="<%=request.getParameter(KualiExceptionIncident.EXCEPTION_MESSAGE)%>" />
</c:if>
<c:if test="${empty displayMessage}">
	<c:set var="displayMessage"
       value="<%=request.getParameter(KualiExceptionIncident.DISPLAY_MESSAGE)%>" />
</c:if>
<c:if test="${empty stackTrace}">
	<c:set var="stackTrace"
       value="<%=request.getParameter(KualiExceptionIncident.STACK_TRACE)%>" />
</c:if>

<c:set var="docTitle" value="Incident Report" />

<c:if test="${exceptionHideIncidentReport eq true}">
	<c:set var="docTitle" value="Error" />
</c:if>

<kul:page showDocumentInfo="false"
	headerTitle="Incident Report"
	docTitle="${docTitle}"
	transactionalDocument="false"
	htmlFormAction="kualiExceptionIncidentReport"
	defaultMethodToCall="notify"
	errorKey="*">

	<html:hidden property="documentId"       write="false" value="${documentId}" />
	<html:hidden property="userEmail"        write="false" value="${userEmail}" />
	<html:hidden property="userName"         write="false" value="${userName}" />
	<html:hidden property="principalName"         write="false" value="${principalName}" />
	<html:hidden property="componentName"    write="false" value="${componentName}" />
	<html:hidden property="exceptionReportSubject" write="false" value="${exceptionReportSubject}" />
	<html:hidden property="exceptionMessage" write="false" value="${exceptionMessage}" />
	<html:hidden property="displayMessage"   write="false" value="${displayMessage}" />
	<html:hidden property="stackTrace"       write="false" value="${stackTrace}" />
	<html:hidden property="exceptionHideIncidentReport" write="false" value="${exceptionHideIncidentReport}" />

	<c:set var="tabTitle" value="${exceptionHideIncidentReport eq false ? 'Please use the Incident Report form below to report the problems' : ''}"/>

	<kul:tab tabTitle="${tabTitle}" defaultOpen="true">
		<div class="tab-container">
			<table class="standard" style="width: calc(100% - 200px); margin:0 100px;">
				<c:if test="${exceptionHideIncidentReport eq false}">
					<tr>
						<td colspan="2" class="center">
							<h4>This information will be forwarded to
								our support team. Please describe what action you were taking
								when the problem occurred.</h4>
						</td>
					</tr>
					<tr>
						<th style="width: 150px;" class="right top">Document Id
						</th>
						<td><c:out value="${documentId}"/></td>
					</tr>
				</c:if>
				<tr>
					<th class="right top">Error Message</th>
					<td style="color: #C47965;"><c:out value="${displayMessage}"/></td>
				</tr>
				<c:if test="${exceptionHideIncidentReport eq false}">
					<tr>
						<th class="right top">User Input</th>
						<td><textarea name='description' rows='5' cols='100' maxlength='1000'></textarea></td>
					</tr>
				</c:if>
				<tr>
					<th>&nbsp;</th>
					<td>
						<div>
							<c:if test="${exceptionHideIncidentReport eq false}">
								<input type="submit" name="submit" class="btn btn-default" title="submit"
									   alt="Submit Incident" value="Submit">
							</c:if>
							<button name="cancel" value="true" class="btn btn-default" title="close"
									alt="Close Without Submitting Incident">
								Close
							</button>
						</div>
					</td>
				</tr>
			</table>

			<br />
			<c:if test="${exceptionHideIncidentReport eq false && !kfunc:isProductionEnvironment()}">
				<table class="standard side-margins">
					<tr>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td colspan="2">
							<strong>******************Stack Trace-Only shown when not in production*****************</strong>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<pre><c:out value="${stackTrace}"/></pre>
						</td>
					</tr>
				</table>
			</c:if>
		</div>
	</kul:tab>
</kul:page>

