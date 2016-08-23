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
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp" %>

<%@ attribute name="editingMode" required="true" description="used to decide editability of overview fields"
              type="java.util.Map" %>
<c:set var="isMaintenanceForm" value='false'/>
<c:if test='<%= jspContext.findAttribute("KualiForm") != null %>'>
    <c:set var="isMaintenanceForm"
           value='<%= jspContext.findAttribute("KualiForm").getClass() == org.kuali.kfs.kns.web.struts.form.KualiMaintenanceForm.class %>'/>
</c:if>
<c:set var="isMaintenance"
       value="${isMaintenanceForm || maintenanceViewMode eq Constants.PARAM_MAINTENANCE_VIEW_MODE_MAINTENANCE}"/>
<c:set var="readOnly" value="${ ! KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT_DOCUMENT_OVERVIEW]}"/>

<c:set var="docHeaderAttributes" value="${DataDictionary.DocumentHeader.attributes}"/>
<c:set var="documentTypeName" value="${KualiForm.docTypeName}"/>
<c:set var="documentEntry" value="${DataDictionary[documentTypeName]}"/>

<dd:evalNameToMap mapName="DataDictionary.${KualiForm.docTypeName}.attributes" returnVar="documentAttributes"/>
<kul:tabTop tabTitle="Document Overview" defaultOpen="true" tabErrorKey="${Constants.DOCUMENT_ERRORS}">
    <div class="tab-container" align=center>
        <html:hidden property="document.documentHeader.documentNumber"/>
        <h3>Overview</h3>
        <table class="standard side-margins" title="view/edit document overview information"
               summary="view/edit document overview information">
            <tr>
                <kul:htmlAttributeHeaderCell
                        labelFor="document.documentHeader.documentDescription"
                        attributeEntry="${docHeaderAttributes.documentDescription}"
                        horizontal="true"
                        addClass="right top"
                        width="25%"/>
                <td class="top" width="25%">
                    <kul:htmlControlAttribute property="document.documentHeader.documentDescription"
                                              attributeEntry="${docHeaderAttributes.documentDescription}"
                                              readOnly="${readOnly}"/>
                </td>
                <kul:htmlAttributeHeaderCell
                        labelFor="document.documentHeader.explanation"
                        attributeEntry="${docHeaderAttributes.explanation}"
                        horizontal="true"
                        rowspan="2"
                        addClass="right top"
                        width="25%"/>
                <td rowspan="2" class="top" width="25%">
                    <kul:htmlControlAttribute
                            property="document.documentHeader.explanation"
                            attributeEntry="${docHeaderAttributes.explanation}"
                            readOnly="${readOnly}"
                            readOnlyAlternateDisplay="${fn:replace(fn:escapeXml(KualiForm.document.documentHeader.explanation), Constants.NEWLINE, '<br/>')}"
                            />
                </td>
            </tr>
            <tr>
                <kul:htmlAttributeHeaderCell
                        labelFor="document.documentHeader.organizationDocumentNumber"
                        attributeEntry="${docHeaderAttributes.organizationDocumentNumber}"
                        horizontal="true"
                        addClass="right"
                        />
                <td>
                    <kul:htmlControlAttribute property="document.documentHeader.organizationDocumentNumber"
                                              attributeEntry="${docHeaderAttributes.organizationDocumentNumber}"
                                              readOnly="${readOnly}"/>
                </td>
            </tr>
        </table>
        <jsp:doBody/>
    </div>
</kul:tabTop>
