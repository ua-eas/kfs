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
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<c:set var="docTitle">
    <bean:message key="${KualiForm.titleKey}"/>
</c:set>

<kul:page
        showDocumentInfo="false"
	    headerTitle="Batch File Upload"
        docTitle="${docTitle}"
        renderMultipart="true"
	    transactionalDocument="false"
        htmlFormAction="batchUpload"
        errorKey="foo"
        alternativeHelp="${ConfigProperties.externalizable.help.url}${KualiForm.url}">

    <html:hidden property="batchUpload.batchInputTypeName" />
    <c:set var="batchUploadAttributes" value="${DataDictionary.BatchUpload.attributes}" />

    <kul:tabTop tabTitle="Manage Batch Files" defaultOpen="true" tabErrorKey="">
        <div class="tab-container">
            <kul:errors keyMatch="*" errorTitle="Errors Found In File:" warningTitle="Warnings Found In File:" displayInDiv="true"/>

            <table class="standard" summary="" cellpadding="0" cellspacing="0">
                <tr class="header">
                    <th>&nbsp;</th>
                    <th><label for="uploadFile">${KFSConstants.REQUIRED_FIELD_SYMBOL}Browse File</label></th>
                    <th><label for="batchUpload.fileUserIdentifer">${KFSConstants.REQUIRED_FIELD_SYMBOL}File Identifier</label></th>
                    <th>Actions</th>
                </tr>
                <tr>
                    <th scope="row"></th>
                    <td class="infoline">
                        <html:file styleId="uploadFile" property="uploadFile"/>
                        <span class="fineprint"></span>
                    </td>
                    <td class="infoline">
                        <kul:htmlControlAttribute attributeEntry="${batchUploadAttributes.fileUserIdentifer}" property="batchUpload.fileUserIdentifer"/>
                        <span class="fineprint"></span>
                    </td>
                    <td class="infoline">
                        <html:submit
                                styleClass="btn btn-green"
                                property="methodToCall.save"
                                title="Upload Batch File"
                                alt="Upload Batch File"
                                value="Add"/>
                    </td>
                </tr>
            </table>
        </div>
	</kul:tabTop>
</kul:page>
