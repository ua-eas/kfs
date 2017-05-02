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
<%@ include file="tldHeader.jsp" %>

<kul:page
        showDocumentInfo="false"
        headerTitle="${QuestionPromptForm.title}"
        docTitle=""
        transactionalDocument="false"
        htmlFormAction="questionPrompt"
        errorKey="*">

    <html:hidden property="formKey" write="false"/>
    <html:hidden property="backLocation" write="false"/>
    <html:hidden property="caller" write="false"/>
    <html:hidden property="questionIndex" write="false"/>
    <html:hidden property="formKey" write="false"/>
    <html:hidden property="context" write="false"/>
    <html:hidden property="questionAnchor" write="false"/>
    <html:hidden property="methodToCallPath" write="false"/>
    <html:hidden property="docNum" write="false"/>

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
                        <html:textarea property="reason" tabindex="0" rows="4" cols="60"/>
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
