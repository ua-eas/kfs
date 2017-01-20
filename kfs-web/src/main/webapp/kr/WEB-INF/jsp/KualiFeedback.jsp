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
<kul:page docTitle="Feedback Form" showDocumentInfo="false" headerTitle="Feedback Form" transactionalDocument="false" renderInnerDiv="true" openNav="true">
    <div class="main-panel">
        <div class="tab-container">
            <p><bean:message key="feedback.welcome"/></p>
            <p><bean:message key="feedback.instructions"/></p>
            <br/>
            <html:form styleId="kualiForm" action="/kualiFeedbackReport" method="post" onsubmit="return hasFormAlreadyBeenSubmitted();">
                <table class="standard side-margins">
                    <tr>
                        <td class="right top">
                            <label><bean:message key="feedback.documentId.label"/></label>
                        </td>
                        <td>
                            <input type="text" name="documentId" value=""/>
                        </td>
                    </tr>
                    <tr>
                        <td class="right top">
                            <label><bean:message key="feedback.componentName.label"/></label>
                        </td>
                        <td valign="top"><textarea name="componentName" rows="5" cols="70" maxlength="1000"></textarea></td>
                    </tr>
                    <tr>
                        <td class="right top">
                            <label><bean:message key="feedback.description.label"/></label>
                        </td>
                        <td valign="top"><textarea name="description" rows="5" cols="70" maxlength="1000"></textarea></td>
                    </tr>
                    <tr>
                        <td>&nbsp;</td>
                        <td align="left">
                            <html:submit property="methodToCall.submitFeedback" value="Submit" title="Submit" alt="Submit" styleClass="btn btn-default"/>
                            <input type="submit" name="cancel" value="Close" class="btn btn-default" title="close" alt="Close Without Submitting Incident">
                        </td>
                    </tr>
                </table>
            </html:form>
        </div>
    </div>
</kul:page>
