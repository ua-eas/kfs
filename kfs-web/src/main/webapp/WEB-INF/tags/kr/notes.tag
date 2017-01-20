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

<%@ attribute name="displayTopicFieldInNotes" required="false" description="Whether to display the note topic column in the table of notes." %>
<%@ attribute name="attachmentTypesValuesFinderClass" required="false" description="A finder class to give options for the types of attachments allowed as as note attachments on this document." %>
<%@ attribute name="transparentBackground" required="false" description="Whether the tab should render as having the background transparent around the corners of the tab." %>
<%@ attribute name="defaultOpen" required="false" description="Whether the tab for the notes is rendered as open." %>
<%@ attribute name="preserveWhitespace" required="false" description="Whether to preserve the whitespace contained inside the text of the notes" %>

<c:set var="noteColSpan" value="6" />

<c:set var="noteType" value="${KualiForm.document.noteType}"/>
<c:set var="documentNotes" value="${KualiForm.document.notes}" />
<c:set var="propPrefix" value="document." />

<c:set var="documentTypeName" value="${KualiForm.docTypeName}" />
<c:set var="documentEntry" value="${DataDictionary[documentTypeName]}" />
<c:set var="allowsNoteAttachments" value="${documentEntry.allowsNoteAttachments}" />
<c:set var="allowsNoteFYI" value="${documentEntry.allowsNoteFYI}" />
<c:set var="tabTitle" value="Notes and Attachments" />
<c:if test="${allowsNoteAttachments eq false}">
  <c:set var="tabTitle" value="Notes" />
</c:if>

<c:if test="${not empty attachmentTypesValuesFinderClass}">
  <c:set var="noteColSpan" value="${noteColSpan + 1}" />
</c:if>

<c:if test="${empty displayTopicFieldInNotes}">
  <c:set var="displayTopicFieldInNotes" value="${documentEntry.displayTopicFieldInNotes}" />
</c:if>

<c:if test="${displayTopicFieldInNotes eq true}">
  <c:set var="noteColSpan" value="${noteColSpan + 1}" />
</c:if>

<kul:tab tabTitle="${tabTitle}" defaultOpen="${!empty documentNotes or (not empty defaultOpen and defaultOpen)}" tabErrorKey="${Constants.DOCUMENT_NOTES_ERRORS},attachmentFile" tabItemCount="${fn:length(documentNotes)}" transparentBackground="${transparentBackground}" >
    <c:set var="notesAttributes" value="${DataDictionary.Note.attributes}" />
    <div class="tab-container" id="G4">
        <jsp:doBody/>
        <table class="datatable items standard" summary="view/add notes">
            <tbody>
                <c:if test="${ ((not empty attachmentTypesValuesFinderClass) and (allowsNoteAttachments eq true)) || kfunc:canAddNoteAttachment(KualiForm.document)}" >
                    <tr class="new-note">
                        <td class="infoline">&nbsp;</td>
                        <td class="infoline">&nbsp;</td>
                        <td class="infoline">&nbsp;</td>
                        <c:if test="${displayTopicFieldInNotes eq true}">
                            <td class="infoline">
                                <kul:htmlAttributeLabel attributeEntry="${notesAttributes.noteTopicText}" forceRequired="true" />
                                <br/>
                                <kul:htmlControlAttribute attributeEntry="${notesAttributes.noteTopicText}" property="newNote.noteTopicText" forceRequired="true" />
                            </td>
                        </c:if>
                        <td class="infoline">
                            <kul:htmlAttributeLabel attributeEntry="${notesAttributes.noteText}" forceRequired="${true}"/>
                            <br/>
                            <kul:htmlControlAttribute attributeEntry="${notesAttributes.noteText}" property="newNote.noteText" forceRequired="${notesAttributes.noteText.required}" />
                        </td>
                        <c:if test="${allowsNoteAttachments eq true}">
                            <td class="infoline">
                                <kul:htmlAttributeLabel attributeEntry="${notesAttributes.attachment}" />
                                <br/>
                                <html:file property="attachmentFile" size="30" styleId="attachmentFile" value="" />
                                <br/>
                                <html:submit property="methodToCall.cancelBOAttachment" title="Cancel Attachment" alt="Remove Attachment" styleClass="tinybutton btn btn-default small" value="Remove Attachment"/>
                            </td>
                        </c:if>
                        <c:if test="${(not empty attachmentTypesValuesFinderClass) and (allowsNoteAttachments eq true)}">
                            <c:set var="finderClass" value="${fn:replace(attachmentTypesValuesFinderClass,'.','|')}"/>
                            <td class="infoline">
                                <html:select property="newNote.attachment.attachmentTypeCode">
                                    <html:optionsCollection property="actionFormUtilMap.getOptionsMap${Constants.ACTION_FORM_UTIL_MAP_METHOD_PARM_DELIMITER}${finderClass}" label="value" value="key"/>
                                </html:select>
                            </td>
                        </c:if>
                        <td class="infoline">
                            <html:submit property="methodToCall.insertBONote" alt="Add a Note" title="Add a Note" styleClass="tinybutton btn btn-green" value="Add"/>
                        </td>
                        <c:if test="${allowsNoteFYI}" >
                            <td>&nbsp;</td>
                        </c:if>
                    </tr>
                </c:if>

                <c:if test="${not empty documentNotes}">
                    <tr class="header">
                        <kul:htmlAttributeHeaderCell literalLabel="&nbsp;" scope="col" align="left"/>
                        <kul:htmlAttributeHeaderCell attributeEntry="${notesAttributes.notePostedTimestamp}" hideRequiredAsterisk="true" scope="col" align="left"/>
                        <kul:htmlAttributeHeaderCell attributeEntry="${notesAttributes.authorUniversalIdentifier}" hideRequiredAsterisk="true" scope="col" align="left"/>

                        <c:if test="${displayTopicFieldInNotes eq true}">
                            <kul:htmlAttributeHeaderCell attributeEntry="${notesAttributes.noteTopicText}" labelFor="newNote.noteTopicText" hideRequiredAsterisk="${true}" scope="col" align="left" />
                        </c:if>

                        <kul:htmlAttributeHeaderCell attributeEntry="${notesAttributes.noteText}" labelFor="newNote.noteText" hideRequiredAsterisk="${true}" scope="col" align="left"/>

                        <c:if test="${allowsNoteAttachments eq true}">
                          <kul:htmlAttributeHeaderCell attributeEntry="${notesAttributes.attachment}" labelFor="attachmentFile" scope="col" align="left"/>
                        </c:if>

                        <c:if test="${(not empty attachmentTypesValuesFinderClass) and (allowsNoteAttachments eq true)}">
                          <kul:htmlAttributeHeaderCell literalLabel="Attachment Type" scope="col" align="left"/>
                        </c:if>

                        <c:if test="${allowsNoteFYI}" >
                          <kul:htmlAttributeHeaderCell literalLabel="Notification Recipient" scope="col"/>
                        </c:if>

                        <kul:htmlAttributeHeaderCell literalLabel="Actions" scope="col"/>
                    </tr>
                </c:if>

				<html:hidden property="newNote.noteTypeCode" value="${noteType.code}"/>

                <c:forEach var="note" items="${documentNotes}" varStatus="status">
                    <c:set var="authorUniversalIdentifier" value = "${note.authorUniversalIdentifier}" />

                    <c:if test="${kfunc:canViewNoteAttachment(KualiForm.document, null)}" >
                        <tr class="${status.index % 2 == 0 ? "highlight" : ""}">
                            <td>${status.index + 1}</td>
                            <td class="datacell">
                                <bean:write name="KualiForm" property="${propPrefix}note[${status.index}].notePostedTimestamp"/>
                                &nbsp;
                            </td>

                            <td class="datacell">
                                <bean:write name="KualiForm" property="${propPrefix}note[${status.index}].authorUniversal.name"/>
                            </td>

                            <c:if test="${displayTopicFieldInNotes eq true}">
                                <td class="datacell">
                                    <bean:write name="KualiForm" property="${propPrefix}note[${status.index}].noteTopicText"/>
                                </td>
                            </c:if>

                            <td class="datacell note-text">
                                <c:if test="${empty preserveWhitespace or preserveWhitespace}">
                                    <kul:preserveWhitespace><bean:write name="KualiForm" property="${propPrefix}note[${status.index}].noteText" /></kul:preserveWhitespace>
                                </c:if>
                                <c:if test="${not empty preserveWhitespace and not preserveWhitespace}">
                                    <bean:write name="KualiForm" property="${propPrefix}note[${status.index}].noteText" />
                                </c:if>
                            </td>

                            <c:choose>
                                <c:when test="${(!empty note.attachment) and (note.attachment.complete)}">
                                    <td class="datacell">
                                        <c:if test="${allowsNoteAttachments eq true}">
                                            <c:if test="${(!empty note.attachment)}">
                                                <c:set var="attachmentTypeCode" value ="${note.attachment.attachmentTypeCode}" />
                                                <c:set var="mimeTypeCode" value="${note.attachment.attachmentMimeTypeCode}" />
                                            </c:if>
                                            <span>
                                                <c:if test="${kfunc:canViewNoteAttachment(KualiForm.document, attachmentTypeCode)}" >
                                                    <html:image property="methodToCall.downloadBOAttachment.attachment[${status.index}]" src="${ConfigProperties.kr.externalizable.images.url}${kfunc:getAttachmentImageForUrl(mimeTypeCode)}" title="download attachment" alt="download attachment" style="padding:5px;margin-bottom:-10px;" onclick="excludeSubmitRestriction=true"/>
                                                </c:if>
                                                <bean:write name="KualiForm" property="${propPrefix}note[${status.index}].attachment.attachmentFileName"/>
                                                &nbsp;
                                                <kul:fileSize byteSize="${note.attachment.attachmentFileSize}">
                                                    (<c:out value="${fileSize} ${fileSizeUnits}" />,  <bean:write name="KualiForm" property="${propPrefix}note[${status.index}].attachment.attachmentMimeTypeCode"/>)
                                                </kul:fileSize>
                                            </span>
                                        </c:if>
                                    </td>

                                    <c:if test="${(not empty attachmentTypesValuesFinderClass) and (allowsNoteAttachments eq true)}">
                                        <td class="datacell">
                                            &nbsp;
									        <c:set var="mapKey" value = "getOptionsMap${Constants.ACTION_FORM_UTIL_MAP_METHOD_PARM_DELIMITER}${finderClass}" />
									        <c:set var="attachmentTypeFinderMap" value="${KualiForm.actionFormUtilMap[mapKey]}"  />
                                            <c:forEach items="${attachmentTypeFinderMap}" var="type">
                                                <c:if test="${type.key eq note.attachment.attachmentTypeCode}">${type.value}</c:if>
                                            </c:forEach>
                                        </td>
                                    </c:if>
                                </c:when>
                                <c:otherwise>
                                    <td class="datacell">&nbsp;</td>
                                    <c:if test="${(not empty attachmentTypesValuesFinderClass) and (allowsNoteAttachments eq true)}">
                                        <td class="datacell">&nbsp;</td>
                                    </c:if>
                                </c:otherwise>
                            </c:choose>

                            <c:if test="${allowsNoteFYI}" >
                                <td class="infoline">
                                    <c:if test="${!empty KualiForm.documentActions[Constants.KUALI_ACTION_CAN_SEND_NOTE_FYI]}">
                                         <kul:user userIdFieldName="${propPrefix}note[${status.index}].adHocRouteRecipient.id"
                                          userId="${note.adHocRouteRecipient.id}"
                                          universalIdFieldName=""
                                          universalId=""
                                          userNameFieldName="${propPrefix}note[${status.index}].adHocRouteRecipient.name"
                                          userName="${note.adHocRouteRecipient.name}"
                                          readOnly="false"
                                          fieldConversions="principalName:${propPrefix}note[${status.index}].adHocRouteRecipient.id,name:${propPrefix}note[${status.index}].adHocRouteRecipient.name"
                                          lookupParameters="${propPrefix}note[${status.index}].adHocRouteRecipient.id:principalName" />
                                    </c:if>
                                    <c:if test="${empty KualiForm.documentActions[Constants.KUALI_ACTION_CAN_SEND_NOTE_FYI]}">
                                        &nbsp;
                                    </c:if>
                                </td>
                            </c:if>

                            <td class="datacell">
                                <c:if test="${kfunc:canDeleteNoteAttachment(KualiForm.document, attachmentTypeCode, authorUniversalIdentifier)}">
                                    <html:submit property="methodToCall.deleteBONote.line${status.index}" title="Delete a Note" alt="Delete a Note" styleClass="tinybutton btn btn-red" value="Delete"/>
                                </c:if> &nbsp;
                                <c:if test="${allowsNoteFYI && !empty KualiForm.documentActions[Constants.KUALI_ACTION_CAN_SEND_NOTE_FYI]}" >
                                    <html:submit property="methodToCall.sendNoteWorkflowNotification.line${status.index}" title="Send FYI" alt="Send FYI" styleClass="tinybutton btn btn-default" value="Send"/>
                                </c:if>
                            </td>
                        </tr>
	                </c:if>
                </c:forEach>
            </tbody>
        </table>
    </div>
</kul:tab>
