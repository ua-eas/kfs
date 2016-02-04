<%--

    Copyright 2005-2015 The Kuali Foundation

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
<%@ include file="tldHeader.jsp"%>

<kul:page
        showDocumentInfo="false"
	    headerTitle="${QuestionPromptForm.title}"
        docTitle=""
	    transactionalDocument="false"
        htmlFormAction="questionPrompt"
	    errorKey="*">

    <html:hidden property="formKey" write="false" />
	<html:hidden property="backLocation" write="false" />
	<html:hidden property="caller" write="false" />
	<html:hidden property="questionIndex" write="false" />
	<html:hidden property="formKey" write="false" />
	<html:hidden property="context" write="false" />
	<html:hidden property="questionAnchor" write="false" />
	<html:hidden property="methodToCallPath" write="false" />
	<html:hidden property="docNum" write="false" />

    <div class="main-panel">
        <div class="center" style="margin: 30px 0;">${QuestionPromptForm.questionText}</div>

        <c:if test="${QuestionPromptForm.showReasonField}">
            <table class="standard" style="margin: 0 auto;">
                <tr>
                    <td>
                        <div class="left"><font color="red">*</font>Please enter the reason below:</div>
                    </td>
                </tr>
                <tr>
                    <td class="center">
                        <html:textarea property="reason" tabindex="0" rows="4" cols="60" />
                    </td>
                </tr>
            </table>
        </c:if>

        <div class="center" style="margin: 30px 0;">
            <c:forEach items="${QuestionPromptForm.buttons}" var="button" varStatus="status">
                <html:submit
                        property="methodToCall.processAnswer.button${status.index}"
                        styleClass="btn btn-default"
                        value="${button}"/>
            </c:forEach>
        </div>
    </div>
</kul:page>
