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
<%@ include file="/jsp/sys/kfsTldHeader.jsp" %>


<kul:documentPage
        showDocumentInfo="true"
        documentTypeName="${KualiForm.docTitle}"
        htmlFormAction="${KualiForm.htmlFormAction}"
        renderMultipart="true"
        showTabButtons="true">

    <c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}"/>

    <sys:documentOverview editingMode="${KualiForm.editingMode}"/>


    <c:if test="${debug == true}">
        <kul:tab tabTitle="Debug" defaultOpen="true" tabErrorKey="debug">
            <div class="tab-container">
                <table class="standard" summary="">
                    <tr>
                        <td class="subhead"><h3>Debug</h3></td>
                    </tr>
                </table>
                <table class="standard">
                    <tr>
                        <td width="10%">editableFlag</td>
                        <td>${KualiForm.editableFlag}</td>
                    </tr>
                    <tr>
                        <td>manualEditFlag</td>
                        <td>${KualiForm.manualEditFlag}</td>
                    </tr>
                    <tr>
                        <td>processInBatch</td>
                        <td>${KualiForm.processInBatch}</td>
                    </tr>
                    <tr>
                        <td>chooseSystem</td>
                        <td>${KualiForm.chooseSystem}</td>
                    </tr>
                    <tr>
                        <td>editMethod</td>
                        <td>${KualiForm.editMethod}</td>
                    </tr>
                    <tr>
                        <td>inputGroupId</td>
                        <td>${KualiForm.document.correctionInputFileName}</td>
                    </tr>
                    <tr>
                        <td>outputGroupId</td>
                        <td>${KualiForm.document.correctionOutputFileName}</td>
                    </tr>
                    <tr>
                        <td>inputFileName</td>
                        <td>${KualiForm.inputFileName}</td>
                    </tr>
                    <tr>
                        <td>dataLoadedFlag</td>
                        <td>${KualiForm.dataLoadedFlag}</td>
                    </tr>
                    <tr>
                        <td>matchCriteriaOnly</td>
                        <td>${KualiForm.matchCriteriaOnly}</td>
                    </tr>
                    <tr>
                        <td>editableFlag</td>
                        <td>${KualiForm.editableFlag}</td>
                    </tr>
                    <tr>
                        <td>deleteFileFlag</td>
                        <td>${KualiForm.deleteFileFlag}</td>
                    </tr>
                    <tr>
                        <td>allEntries.size</td>
                        <td>${KualiForm.allEntriesSize}</td>
                    </tr>
                    <tr>
                        <td>readOnly</td>
                        <td>${readOnly}</td>
                    </tr>
                </table>
            </div>
        </kul:tab>
    </c:if>

    <kul:tab tabTitle="Summary" defaultOpen="true" tabErrorKey="summary">
        <c:if test="${KualiForm.document.correctionTypeCode ne 'R' and (not (KualiForm.persistedOriginEntriesMissing && KualiForm.inputGroupIdFromLastDocumentLoad eq KualiForm.inputGroupId)) && ((KualiForm.dataLoadedFlag and !KualiForm.restrictedFunctionalityMode) or KualiForm.document.correctionOutputFileName != null or !KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT])}">

            <div class="tab-container" style="margin: 0 36px;">
                <table class="standard" summary="">
                    <tr>
                        <c:if test="${KualiForm.showOutputFlag == true or KualiForm.showSummaryOutputFlag == true}">
                            <td class="subhead"><h3>Summary of Output Group</h3></td>
                        </c:if>
                        <c:if test="${KualiForm.showOutputFlag == false}">
                            <td class="subhead"><h3>Summary of Input Group</h3></td>
                        </c:if>
                    </tr>
                </table>
                <table class="standard">
                    <tr>
                        <th width="20%"> Total Debits:</th>
                        <td width="80%">
                            <fmt:formatNumber value="${KualiForm.document.correctionDebitTotalAmount}"
                                              groupingUsed="true" minFractionDigits="2"/>
                        </td>
                    </tr>
                    <tr>
                        <th> Total Credits:</th>
                        <td>
                            <fmt:formatNumber
                                    value="${KualiForm.document.correctionCreditTotalAmount}" groupingUsed="true"
                                    minFractionDigits="2"/>
                        </td>
                    </tr>
                    <tr>
                        <th> Total No DB/CR:</th>
                        <td>
                            <fmt:formatNumber
                                    value="${KualiForm.document.correctionBudgetTotalAmount}" groupingUsed="true"
                                    minFractionDigits="2"/>
                        </td>
                    </tr>
                    <tr>
                        <th> Rows output:</th>
                        <td>
                            <fmt:formatNumber
                                    value="${KualiForm.document.correctionRowCount}" groupingUsed="true"/>
                        </td>
                    </tr>
                </table>
            </div>
        </c:if>
        <c:if test="${KualiForm.persistedOriginEntriesMissing && KualiForm.inputGroupIdFromLastDocumentLoad eq KualiForm.document.correctionInputFileName}">
            <div class="tab-container" style="margin: 0 36px;">
                <table class="standard" summary="">
                    <tr>
                        <c:if test="${KualiForm.showOutputFlag == true or KualiForm.showSummaryOutputFlag == true}">
                            <td class="subhead"><h3>Summary of Output Group</h3></td>
                        </c:if>
                        <c:if test="${KualiForm.showOutputFlag == false or KualiForm.showSummaryOutputFlag == true}">
                            <td class="subhead"><h3>Summary of Input Group</h3></td>
                        </c:if>
                    </tr>
                </table>
                <table class="standard">
                    <tr>
                        <td>The summary is unavailable because the origin entries are unavailable.</td>
                    </tr>
                </table>
            </div>
        </c:if>
        <c:if test="${KualiForm.restrictedFunctionalityMode && not KualiForm.persistedOriginEntriesMissing && KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}">
            <div class="tab-container" style="margin: 0 36px;">
                <table class="standard" summary="">
                    <tr>
                        <c:if test="${KualiForm.showOutputFlag == true or KualiForm.showSummaryOutputFlag == true}">
                            <td class="subhead"><h3>Summary of Output Group</h3></td>
                        </c:if>
                        <c:if test="${KualiForm.showOutputFlag == false or KualiForm.showSummaryOutputFlag == true}">
                            <td class="subhead"><h3>Summary of Input Group</h3></td>
                        </c:if>
                    </tr>
                </table>
                <table class="standard">
                    <tr>
                        <td>The summary is unavailable because the selected origin entry group is too large.</td>
                    </tr>
                </table>
            </div>
        </c:if>

        <%-- ------------------------------------------------------------ This is read only mode --------------------------------------------------- --%>
        <c:if test="${readOnly == true}">
            <div style="margin: 0 15px;">
                <c:forEach var="group" items="${KualiForm.document.correctionChangeGroup}">
                    <c:forEach items="${group.correctionCriteria}" var="criteria" varStatus="cc">

                    </c:forEach>
                    <c:forEach items="${group.correctionChange}" var="change">

                    </c:forEach>
                </c:forEach>

                <kul:tab tabTitle="System and Edit Method" defaultOpen="true">
                    <div class="tab-container">
                        <table class="standard side-margins">
                            <tr>
                                <th width="20%">System:</th>
                                <td width="80%"><c:out value="${KualiForm.document.system}"/></td>
                            </tr>
                            <tr>
                                <th>Edit Method:</th>
                                <td><c:out value="${KualiForm.document.method}"/></td>
                            </tr>
                        </table>
                    </div>
                </kul:tab>

                <kul:tab tabTitle="Input and Output File" defaultOpen="true">
                    <div class="tab-container">
                        <table class="standard side-margins">
                            <c:if test="${KualiForm.document.correctionInputFileName != null}">
                                <tr>
                                    <th width="20%">Input File Name:</th>
                                    <td width="80%"><c:out value="${KualiForm.document.correctionInputFileName}"/></td>
                                </tr>
                            </c:if>
                            <tr>
                                <th>Output File Name:
                                </td>
                                <c:if test="${KualiForm.document.correctionOutputFileName != null}">
                                    <td><c:out value="${KualiForm.document.correctionOutputFileName}"/></td>
                                </c:if>
                                <c:if test="${KualiForm.document.correctionOutputFileName == null}">
                                    <c:if test="${KualiForm.document.correctionTypeCode eq 'R'}">
                                        <td><c:out value="${Constants.NOT_AVAILABLE_STRING}"/></td>
                                    </c:if>
                                    <c:if test="${KualiForm.document.correctionTypeCode ne 'R'}">
                                        <td> The output file name is unavailable until the document has a status of PROCESSED or FINAL.</td>
                                    </c:if>
                                </c:if>
                            </tr>
                        </table>
                    </div>
                </kul:tab>

                <c:set var="searchResultsTitle" value="Search Results"/>
                <c:if test="${!KualiForm.restrictedFunctionalityMode}">
                    <c:set var="searchResultsTitle" value="Search Results - Output Group"/>
                </c:if>

                <kul:tab tabTitle="${searchResultsTitle}" defaultOpen="true">
                    <div class="tab-container">
                        <c:if test="${KualiForm.restrictedFunctionalityMode}">
                            <div class="tab-container">
                                <table class="standard" summary="Search Results">
                                    <tr>
                                        <td><bean:message key="gl.correction.restricted.functionality.search.results.label"/></td>
                                    </tr>
                                </table>
                            </div>
                        </c:if>
                        <c:if test="${!KualiForm.restrictedFunctionalityMode}">
                            <table class="standard" summary="Search Results">
                                <tr>
                                    <c:choose>
                                        <c:when test="${KualiForm.documentType == 'LLCP'}">
                                            <td>
                                                <ld:displayLaborOriginEntrySearchResults
                                                        laborOriginEntries="${KualiForm.displayEntries}"/>
                                            </td>
                                        </c:when>
                                        <c:otherwise>
                                            <td>
                                                <glcp:displayOriginEntrySearchResults originEntries="${KualiForm.displayEntries}"/>
                                            </td>
                                        </c:otherwise>
                                    </c:choose>
                                </tr>
                            </table>
                        </c:if>
                    </div>
                </kul:tab>

                <kul:tab tabTitle="Edit Options and Action" defaultOpen="true">
                    <div class="tab-container">
                        <table class="standard side-margins" summary="Edit Options and Action">
                            <tr>
                                <td class="center">
                                    <html:checkbox styleId="processInBatch" property="processInBatch" disabled="true"/> <STRONG>
                                    <label for="processInBatch">Process In Batch</label> </STRONG> &nbsp; &nbsp; &nbsp; &nbsp;
                                    <c:if test="${KualiForm.document.correctionTypeCode == 'C'}">
                                        <html:checkbox styleId="matchCriteriaOnly" property="matchCriteriaOnly"
                                                       disabled="true"/> <STRONG> <label for="matchCriteriaOnly">Output only
                                        records which match criteria?</label> </STRONG>
                                    </c:if>
                                </td>
                            </tr>
                        </table>
                    </div>
                </kul:tab>

                <c:if test="${KualiForm.document.correctionTypeCode == 'C'}">
                    <kul:tab tabTitle="Search and Modification Criteria" defaultOpen="true">
                        <div class="tab-container">
                            <table class="standard side-margins" summary="Search Criteria">
                                <tr class="header">
                                    <th class="subhead" width="50%">Search Criteria</th>
                                    <th class="subhead" width="50%">Modification Criteria</th>
                                </tr>
                                <c:forEach var="group" items="${KualiForm.document.correctionChangeGroup}">
                                    <tr>
                                        <td colspan="2" style="vertical-align: top;">Group:</td>
                                    </tr>
                                    <tr style="border-bottom: 1px solid #b6b6b6;">
                                        <td class="bord-l-b" style="vertical-align: top;">
                                            <c:forEach items="${group.correctionCriteria}" var="criteria" varStatus="cc">
                                                <label for="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldName">Field</label>:
                                                <html:select disabled="true"
                                                             property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldName"
                                                             styleId="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldName"
                                                             title="Field">
                                                    <c:choose>
                                                        <c:when test="${KualiForm.documentType == 'LLCP'}">
                                                            <html:optionsCollection
                                                                    property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|module|ld|businessobject|options|LaborOriginEntryFieldFinder"
                                                                    label="value" value="key"/>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <html:optionsCollection
                                                                    property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|gl|businessobject|options|OriginEntryFieldFinder"
                                                                    label="value" value="key"/>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </html:select>
                                                <label for="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionOperatorCode">Operator</label>:
                                                <html:select disabled="true"
                                                             property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionOperatorCode"
                                                             styleId="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionOperatorCode"
                                                             title="Operator">
                                                    <html:optionsCollection
                                                            property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|gl|businessobject|options|SearchOperatorsFinder"
                                                            label="value" value="key"/>
                                                </html:select>
                                                <label for="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldValue">Value</label>:
                                                <html:text disabled="true"
                                                           property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldValue"
                                                           styleId="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldValue"
                                                           title="Value"/>
                                                <br>
                                            </c:forEach>
                                        </td>
                                        <td class="bord-l-b" style="vertical-align: top;">
                                            <c:forEach items="${group.correctionChange}" var="change">
                                                <label for="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeItem[${change.correctionChangeLineNumber}].correctionFieldName">Field</label>:
                                                <html:select disabled="true"
                                                             property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeItem[${change.correctionChangeLineNumber}].correctionFieldName"
                                                             styleId="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeItem[${change.correctionChangeLineNumber}].correctionFieldName"
                                                             title="Field">
                                                    <c:choose>
                                                        <c:when test="${KualiForm.documentType == 'LLCP'}">
                                                            <html:optionsCollection
                                                                    property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|module|ld|businessobject|options|LaborOriginEntryFieldFinder"
                                                                    label="value" value="key"/>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <html:optionsCollection
                                                                    property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|gl|businessobject|options|OriginEntryFieldFinder"
                                                                    label="value" value="key"/>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </html:select>
                                                <label for="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeItem[${change.correctionChangeLineNumber}].correctionFieldValue">Replacement
                                                    Value</label>:
                                                <html:text disabled="true"
                                                           property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeItem[${change.correctionChangeLineNumber}].correctionFieldValue"
                                                           styleId="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeItem[${change.correctionChangeLineNumber}].correctionFieldValue"
                                                           title="Replacement Value"/>
                                                <br>
                                            </c:forEach>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </table>
                        </div>
                    </kul:tab>
                </c:if>
            </div>
        </c:if>
    </kul:tab>

    <c:if test="${readOnly == false}">
        <kul:tab tabTitle="Correction Process" defaultOpen="true" tabErrorKey="systemAndEditMethod">
            <div class="tab-container">
                <table class="standard" summary="Correction Process">
                    <tr>
                        <th class="right"><label for="chooseSystem">Select System</label> and <label for="editMethod">Edit
                            Method</label>:
                        </th>
                        <td>
                            <html:select property="chooseSystem" styleId="chooseSystem" title="Select System">
                                <html:optionsCollection
                                        property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|gl|businessobject|options|CorrectionChooseSystemValuesFinder"
                                        label="value" value="key"/>
                            </html:select>

                            <html:select property="editMethod" styleId="editMethod" title="Edit Method">
                                <html:optionsCollection
                                        property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|gl|businessobject|options|CorrectionEditMethodValuesFinder"
                                        label="value" value="key"/>
                            </html:select>

                            <html:submit
                                    property="methodToCall.selectSystemEditMethod.anchor${currentTabIndex}"
                                    styleClass="btn btn-default"
                                    alt="Select System and Edit Method"
                                    title="Select System and Edit Method"
                                    value="Select"/>
                        </td>
                    </tr>
                </table>
            </div>
        </kul:tab>
        <kul:tab tabTitle="Documents in System" defaultOpen="true" tabErrorKey="documentsInSystem">
            <c:if test="${KualiForm.chooseSystem == 'D'}">
                <div class="tab-container">
                    <table class="standard" summary="Documents in System">
                        <tr>
                            <td colspan="2" class="center">
                                <label for="inputGroupId"><strong>Origin Entry Group</strong></label><br/><br/>
                                <html:select property="document.correctionInputFileName" size="10"
                                             styleId="inputGroupId" title="Origin Entry Group">
                                    <c:if test="${KualiForm.inputGroupIdFromLastDocumentLoadIsMissing and KualiForm.inputGroupId eq KualiForm.inputGroupIdFromLastDocumentLoad}">
                                        <option value="<c:out value="${KualiForm.inputGroupIdFromLastDocumentLoad}"/>"
                                                selected="selected"><c:out
                                                value="${KualiForm.inputGroupIdFromLastDocumentLoad}"/> Document was
                                            last saved with this origin entry group selected. Group is no longer in
                                            system.
                                        </option>
                                    </c:if>

                                    <c:choose>
                                        <c:when test="${KualiForm.documentType == 'LLCP'}">
                                            <c:choose>
                                                <c:when test="${KualiForm.editMethod eq 'R'}">
                                                    <html:optionsCollection
                                                            property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|module|ld|businessobject|options|ProcessingCorrectionLaborGroupEntriesFinder"
                                                            label="value" value="key"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <html:optionsCollection
                                                            property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|module|ld|businessobject|options|CorrectionLaborGroupEntriesFinder"
                                                            label="value" value="key"/>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:when>
                                        <c:otherwise>
                                            <c:choose>
                                                <c:when test="${KualiForm.editMethod eq 'R'}">
                                                    <html:optionsCollection
                                                            property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|gl|businessobject|options|ProcessingCorrectionGroupEntriesFinder"
                                                            label="value" value="key"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <html:optionsCollection
                                                            property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|gl|businessobject|options|CorrectionGroupEntriesFinder"
                                                            label="value" value="key"/>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:otherwise>
                                    </c:choose>
                                </html:select>

                                <br/><br/>
                                <c:if test="${KualiForm.editMethod eq 'R'}">
                                    <html:submit
                                            property="methodToCall.confirmDeleteDocument.anchor${currentTabIndex}"
                                            styleClass="btn btn-default"
                                            alt="Remove Group From Processing"
                                            title="Remove Group From Processing"
                                            value="Remove Group From Processing"/>
                                </c:if>
                                <c:if test="${KualiForm.editMethod eq 'M' or KualiForm.editMethod eq 'C'}">
                                    <html:submit
                                            property="methodToCall.loadGroup.anchor${currentTabIndex}"
                                            styleClass="btn btn-default"
                                            alt="Show All Entries"
                                            title="Show All Entries"
                                            value="Load Group"/>
                                </c:if>
                                <html:submit
                                        property="methodToCall.saveToDesktop.anchor${currentTabIndex}"
                                        styleClass="btn btn-default"
                                        alt="Save To Desktop"
                                        title="Save To Desktop"
                                        onclick="excludeSubmitRestriction=true"
                                        value="Copy Group To Desktop"/>
                            </td>
                        </tr>
                    </table>
                </div>
            </c:if>
        </kul:tab>
        <kul:tab tabTitle="Correction File Upload" defaultOpen="true" tabErrorKey="fileUpload">
            <c:if test="${KualiForm.chooseSystem == 'U'}">
                <div class="tab-container">
                    <table class="standard" summary="Correction File Upload">
                        <tr>
                            <th class="right" width="50%">Corrections <label for="sourceFile">File Upload</label>:</th>
                            <td>
                                <html:file size="30" property="sourceFile" styleId="sourceFile" title="File Upload"/>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2" class="center">
                                <html:submit
                                        property="methodToCall.uploadFile.anchor${currentTabIndex}"
                                        styleClass="btn btn-default"
                                        alt="upload file"
                                        title="upload file"
                                        value="Load Document"/>
                            </td>
                        </tr>
                    </table>
                </div>
            </c:if>
        </kul:tab>
        <kul:tab tabTitle="Search Results" defaultOpen="true" tabErrorKey="searchResults">
            <c:if test="${KualiForm.restrictedFunctionalityMode && !KualiForm.persistedOriginEntriesMissing}">
                <div class="tab-container">
                    <table class="standard" summary="">
                        <tr>
                            <td class="subhead"><h3>Search Results</h3></td>
                        </tr>
                        <tr>
                            <td><bean:message key="gl.correction.restricted.functionality.search.results.label"/></td>
                        </tr>
                    </table>
                </div>
            </c:if>
            <c:if test="${KualiForm.restrictedFunctionalityMode && KualiForm.persistedOriginEntriesMissing && KualiForm.inputGroupIdFromLastDocumentLoad eq KualiForm.document.correctionInputFileName}">
                <div class="tab-container">
                    <table class="standard" summary="">
                        <tr>
                            <td class="subhead"><h3>Search Results</h3></td>
                        </tr>
                        <tr>
                            <td><bean:message key="gl.correction.persisted.origin.entries.missing"/></td>
                        </tr>
                    </table>
                </div>
            </c:if>
            <c:if test="${KualiForm.chooseSystem != null and KualiForm.editMethod != null and KualiForm.dataLoadedFlag == true and !KualiForm.restrictedFunctionalityMode and !(KualiForm.persistedOriginEntriesMissing && KualiForm.inputGroupIdFromLastDocumentLoad eq KualiForm.document.correctionInputFileName)}">
                <div class="tab-container" align="left" style="overflow: scroll; width: 100% ;">
                    <table class="standard" summary="">
                        <tr>
                            <c:choose>
                                <c:when test="${KualiForm.showOutputFlag == true and KualiForm.editMethod == 'C'}">
                                    <td class="subhead"><h3>Search Results - Output Group</h3></td>
                                </c:when>
                                <c:when test="${KualiForm.showOutputFlag == false and KualiForm.editMethod == 'C'}">
                                    <td class="subhead"><h3>Search Results - Input Group</h3></td>
                                </c:when>
                                <c:when test="${KualiForm.showOutputFlag == true and KualiForm.editMethod == 'M'}">
                                    <td class="subhead"><h3>Search Results - Matching Entries Only</h3></td>
                                </c:when>
                                <c:when test="${KualiForm.showOutputFlag == false and KualiForm.editMethod == 'M'}">
                                    <td class="subhead"><h3>Search Results - All Entries</h3></td>
                                </c:when>
                            </c:choose>
                        </tr>
                        <tr>
                            <c:choose>
                                <c:when test="${KualiForm.documentType == 'LLCP'}">
                                    <td>
                                        <ld:displayLaborOriginEntrySearchResults
                                                laborOriginEntries="${KualiForm.displayEntries}"/>
                                    </td>
                                </c:when>
                                <c:otherwise>
                                    <td>
                                        <glcp:displayOriginEntrySearchResults
                                                originEntries="${KualiForm.displayEntries}"/>
                                    </td>
                                </c:otherwise>
                            </c:choose>
                        </tr>
                        <c:if test="${KualiForm.editMethod == 'M' and KualiForm.editableFlag == true}">
                            <tr>
                                <td class="subhead"><h3>Manual Editing</h3></td>
                            </tr>
                            <tr>
                                <td>
                                    <table id="eachEntryForManualEdit" class="standard">
                                        <thead>
                                        <tr class="header">
                                            <th>Manual Edit</th>

                                            <c:forEach items="${KualiForm.tableRenderColumnMetadata}" var="column">

                                                <th class="sortable">
                                                    <label for="<c:out value="${column.propertyName}"/>">
                                                        <c:out value="${column.columnTitle}"/><c:if
                                                            test="${empty column.columnTitle}">$nbsp;</c:if>
                                                    </label>
                                                </th>
                                            </c:forEach>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <tr class="odd">
                                            <c:choose>
                                                <c:when test="${KualiForm.documentType == 'LLCP'}">
                                                    <c:choose>
                                                        <c:when test="${KualiForm.laborEntryForManualEdit.entryId == 0}">
                                                            <td>
                                                                <html:submit
                                                                        property="methodToCall.addManualEntry.anchor${currentTabIndex}"
                                                                        styleClass="btn btn-green small"
                                                                        alt="edit"
                                                                        title="edit"
                                                                        value="Add"/>
                                                            </td>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <td>
                                                                <html:submit
                                                                        property="methodToCall.saveManualEntry.anchor${currentTabIndex}"
                                                                        styleClass="btn btn-default small"
                                                                        alt="edit"
                                                                        title="edit"
                                                                        value="Edit"/>
                                                            </td>
                                                        </c:otherwise>
                                                    </c:choose>
                                                    <td><html:text property="laborEntryUniversityFiscalYear" size="5"
                                                                   styleId="laborEntryUniversityFiscalYear"/></td>
                                                    <td><html:text
                                                            property="laborEntryForManualEdit.chartOfAccountsCode"
                                                            size="5"
                                                            styleId="laborEntryForManualEdit.chartOfAccountsCode"/></td>
                                                    <td><html:text property="laborEntryForManualEdit.accountNumber"
                                                                   size="7"
                                                                   styleId="laborEntryForManualEdit.accountNumber"/></td>
                                                    <td><html:text property="laborEntryForManualEdit.subAccountNumber"
                                                                   size="7"
                                                                   styleId="laborEntryForManualEdit.subAccountNumber"/></td>
                                                    <td><html:text
                                                            property="laborEntryForManualEdit.financialObjectCode"
                                                            size="5"
                                                            styleId="laborEntryForManualEdit.financialObjectCode"/></td>
                                                    <td><html:text
                                                            property="laborEntryForManualEdit.financialSubObjectCode"
                                                            size="6"
                                                            styleId="laborEntryForManualEdit.financialSubObjectCode"/></td>
                                                    <td><html:text
                                                            property="laborEntryForManualEdit.financialBalanceTypeCode"
                                                            size="8"
                                                            styleId="laborEntryForManualEdit.financialBalanceTypeCode"/></td>
                                                    <td><html:text
                                                            property="laborEntryForManualEdit.financialObjectTypeCode"
                                                            size="6"
                                                            styleId="laborEntryForManualEdit.financialObjectTypeCode"/></td>
                                                    <td><html:text
                                                            property="laborEntryForManualEdit.universityFiscalPeriodCode"
                                                            size="6"
                                                            styleId="laborEntryForManualEdit.universityFiscalPeriodCode"/></td>
                                                    <td><html:text
                                                            property="laborEntryForManualEdit.financialDocumentTypeCode"
                                                            size="10"
                                                            styleId="laborEntryForManualEdit.financialDocumentTypeCode"/></td>
                                                    <td><html:text
                                                            property="laborEntryForManualEdit.financialSystemOriginationCode"
                                                            size="6"
                                                            styleId="laborEntryForManualEdit.financialSystemOriginationCode"/></td>
                                                    <td><html:text property="laborEntryForManualEdit.documentNumber"
                                                                   size="14"
                                                                   styleId="laborEntryForManualEdit.documentNumber"/></td>
                                                    <td><html:text
                                                            property="laborEntryTransactionLedgerEntrySequenceNumber"
                                                            size="9"
                                                            styleId="laborEntryTransactionLedgerEntrySequenceNumber"/></td>
                                                    <td><html:text property="laborEntryForManualEdit.positionNumber"
                                                                   size="14"
                                                                   styleId="laborEntryForManualEdit.positionNumber"/></td>
                                                    <td><html:text property="laborEntryForManualEdit.projectCode"
                                                                   size="7"
                                                                   styleId="laborEntryForManualEdit.projectCode"/></td>
                                                    <td><html:text
                                                            property="laborEntryForManualEdit.transactionLedgerEntryDescription"
                                                            size="11"
                                                            styleId="laborEntryForManualEdit.transactionLedgerEntryDescription"/></td>
                                                    <td><html:text property="laborEntryTransactionLedgerEntryAmount"
                                                                   size="7"
                                                                   styleId="laborEntryTransactionLedgerEntryAmount"/></td>
                                                    <td><html:text
                                                            property="laborEntryForManualEdit.transactionDebitCreditCode"
                                                            size="9"
                                                            styleId="laborEntryForManualEdit.transactionDebitCreditCode"/></td>
                                                    <td><html:text property="laborEntryTransactionDate" size="12"
                                                                   styleId="laborEntryTransactionDate"/></td>
                                                    <td><html:text
                                                            property="laborEntryForManualEdit.organizationDocumentNumber"
                                                            size="12"
                                                            styleId="laborEntryForManualEdit.organizationDocumentNumber"/></td>
                                                    <td><html:text
                                                            property="laborEntryForManualEdit.organizationReferenceId"
                                                            size="13"
                                                            styleId="laborEntryForManualEdit.organizationReferenceId"/></td>
                                                    <td><html:text
                                                            property="laborEntryForManualEdit.referenceFinancialDocumentTypeCode"
                                                            size="10"
                                                            styleId="laborEntryForManualEdit.referenceFinancialDocumentTypeCode"/></td>
                                                    <td><html:text
                                                            property="laborEntryForManualEdit.referenceFinancialSystemOriginationCode"
                                                            size="10"
                                                            styleId="laborEntryForManualEdit.referenceFinancialSystemOriginationCode"/></td>
                                                    <td><html:text
                                                            property="laborEntryForManualEdit.referenceFinancialDocumentNumber"
                                                            size="9"
                                                            styleId="laborEntryForManualEdit.referenceFinancialDocumentNumber"/></td>
                                                    <td><html:text property="laborEntryFinancialDocumentReversalDate"
                                                                   size="8"
                                                                   styleId="laborEntryFinancialDocumentReversalDate"/></td>
                                                    <td><html:text
                                                            property="laborEntryForManualEdit.transactionEncumbranceUpdateCode"
                                                            size="13"
                                                            styleId="laborEntryForManualEdit.transactionEncumbranceUpdateCode"/></td>
                                                    <td><html:text property="laborEntryTransactionPostingDate" size="14"
                                                                   styleId="laborEntryTransactionPostingDate"/></td>
                                                    <td><html:text property="laborEntryPayPeriodEndDate" size="14"
                                                                   styleId="laborEntryPayPeriodEndDate"/></td>
                                                    <td><html:text property="laborEntryTransactionTotalHours" size="14"
                                                                   styleId="laborEntryTransactionTotalHours"/></td>
                                                    <td><html:text property="laborEntryPayrollEndDateFiscalYear"
                                                                   size="14"
                                                                   styleId="laborEntryPayrollEndDateFiscalYear"/></td>
                                                    <td><html:text
                                                            property="laborEntryForManualEdit.payrollEndDateFiscalPeriodCode"
                                                            size="14"
                                                            styleId="laborEntryForManualEdit.payrollEndDateFiscalPeriodCode"/></td>
                                                    <td><html:text property="laborEntryForManualEdit.emplid" size="14"
                                                                   styleId="laborEntryForManualEdit.emplid"/></td>
                                                    <td><html:text property="laborEntryEmployeeRecord" size="14"
                                                                   styleId="laborEntryEmployeeRecord"/></td>
                                                    <td><html:text property="laborEntryForManualEdit.earnCode" size="14"
                                                                   styleId="laborEntryForManualEdit.earnCode"/></td>
                                                    <td><html:text property="laborEntryForManualEdit.payGroup" size="14"
                                                                   styleId="laborEntryForManualEdit.payGroup"/></td>
                                                    <td><html:text
                                                            property="laborEntryForManualEdit.salaryAdministrationPlan"
                                                            size="14"
                                                            styleId="laborEntryForManualEdit.salaryAdministrationPlan"/></td>
                                                    <td><html:text property="laborEntryForManualEdit.grade" size="14"
                                                                   styleId="laborEntryForManualEdit.grade"/></td>
                                                    <td><html:text property="laborEntryForManualEdit.runIdentifier"
                                                                   size="14"
                                                                   styleId="laborEntryForManualEdit.runIdentifier"/></td>
                                                    <td><html:text
                                                            property="laborEntryForManualEdit.laborLedgerOriginalChartOfAccountsCode"
                                                            size="14"
                                                            styleId="laborEntryForManualEdit.laborLedgerOriginalChartOfAccountsCode"/></td>
                                                    <td><html:text
                                                            property="laborEntryForManualEdit.laborLedgerOriginalAccountNumber"
                                                            size="14"
                                                            styleId="laborEntryForManualEdit.laborLedgerOriginalAccountNumber"/></td>
                                                    <td><html:text
                                                            property="laborEntryForManualEdit.laborLedgerOriginalSubAccountNumber"
                                                            size="14"
                                                            styleId="laborEntryForManualEdit.laborLedgerOriginalSubAccountNumber"/></td>
                                                    <td><html:text
                                                            property="laborEntryForManualEdit.laborLedgerOriginalFinancialObjectCode"
                                                            size="14"
                                                            styleId="laborEntryForManualEdit.laborLedgerOriginalFinancialObjectCode"/></td>
                                                    <td><html:text
                                                            property="laborEntryForManualEdit.laborLedgerOriginalFinancialSubObjectCode"
                                                            size="14"
                                                            styleId="laborEntryForManualEdit.laborLedgerOriginalFinancialSubObjectCode"/></td>
                                                    <td><html:text property="laborEntryForManualEdit.hrmsCompany"
                                                                   size="14"
                                                                   styleId="laborEntryForManualEdit.hrmsCompany"/></td>
                                                    <td><html:text property="laborEntryForManualEdit.setid" size="14"
                                                                   styleId="laborEntryForManualEdit.setid"/></td>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:choose>
                                                        <c:when test="${KualiForm.entryForManualEdit.entryId == 0}">
                                                            <td>
                                                                <html:submit
                                                                        property="methodToCall.addManualEntry.anchor${currentTabIndex}"
                                                                        styleClass="btn btn-green small"
                                                                        alt="edit"
                                                                        title="edit"
                                                                        value="Add"/>
                                                            </td>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <td>

                                                                <html:submit
                                                                        property="methodToCall.saveManualEntry.anchor${currentTabIndex}"
                                                                        styleClass="btn btn-default small"
                                                                        alt="edit"
                                                                        title="edit"
                                                                        value="Edit"/>
                                                            </td>
                                                        </c:otherwise>
                                                    </c:choose>

                                                    <td><html:text property="entryUniversityFiscalYear" size="5"
                                                                   styleId="entryUniversityFiscalYear"/></td>
                                                    <td><html:text property="entryForManualEdit.chartOfAccountsCode"
                                                                   size="5"
                                                                   styleId="entryForManualEdit.chartOfAccountsCode"/></td>
                                                    <td><html:text property="entryForManualEdit.accountNumber" size="7"
                                                                   styleId="entryForManualEdit.accountNumber."/></td>
                                                    <td><html:text property="entryForManualEdit.subAccountNumber"
                                                                   size="7"
                                                                   styleId="entryForManualEdit.subAccountNumber"/></td>
                                                    <td><html:text property="entryForManualEdit.financialObjectCode"
                                                                   size="5"
                                                                   styleId="entryForManualEdit.financialObjectCode"/></td>
                                                    <td><html:text property="entryForManualEdit.financialSubObjectCode"
                                                                   size="6"
                                                                   styleId="entryForManualEdit.financialSubObjectCode"/></td>
                                                    <td><html:text
                                                            property="entryForManualEdit.financialBalanceTypeCode"
                                                            size="8"
                                                            styleId="entryForManualEdit.financialBalanceTypeCode"/></td>
                                                    <td><html:text property="entryForManualEdit.financialObjectTypeCode"
                                                                   size="6"
                                                                   styleId="entryForManualEdit.financialObjectTypeCode"/></td>
                                                    <td><html:text
                                                            property="entryForManualEdit.universityFiscalPeriodCode"
                                                            size="6"
                                                            styleId="entryForManualEdit.universityFiscalPeriodCode"/></td>
                                                    <td><html:text
                                                            property="entryForManualEdit.financialDocumentTypeCode"
                                                            size="10"
                                                            styleId="entryForManualEdit.financialDocumentTypeCode"/></td>
                                                    <td><html:text
                                                            property="entryForManualEdit.financialSystemOriginationCode"
                                                            size="6"
                                                            styleId="entryForManualEdit.financialSystemOriginationCode"/></td>
                                                    <td><html:text property="entryForManualEdit.documentNumber"
                                                                   size="14"
                                                                   styleId="entryForManualEdit.documentNumber"/></td>
                                                    <td><html:text property="entryTransactionLedgerEntrySequenceNumber"
                                                                   size="9"
                                                                   styleId="entryTransactionLedgerEntrySequenceNumber"/></td>
                                                    <td><html:text
                                                            property="entryForManualEdit.transactionLedgerEntryDescription"
                                                            size="11"
                                                            styleId="entryForManualEdit.transactionLedgerEntryDescription"/></td>
                                                    <td><html:text property="entryTransactionLedgerEntryAmount" size="7"
                                                                   styleId="entryTransactionLedgerEntryAmount"/></td>
                                                    <td><html:text
                                                            property="entryForManualEdit.transactionDebitCreditCode"
                                                            size="9"
                                                            styleId="entryForManualEdit.transactionDebitCreditCode"/></td>
                                                    <td><html:text property="entryTransactionDate" size="12"
                                                                   styleId="entryTransactionDate"/></td>
                                                    <td><html:text
                                                            property="entryForManualEdit.organizationDocumentNumber"
                                                            size="12"
                                                            styleId="entryForManualEdit.organizationDocumentNumber"/></td>
                                                    <td><html:text property="entryForManualEdit.projectCode" size="7"
                                                                   styleId="entryForManualEdit.projectCode"/></td>
                                                    <td><html:text property="entryForManualEdit.organizationReferenceId"
                                                                   size="13"
                                                                   styleId="entryForManualEdit.organizationReferenceId"/></td>
                                                    <td><html:text
                                                            property="entryForManualEdit.referenceFinancialDocumentTypeCode"
                                                            size="10"
                                                            styleId="entryForManualEdit.referenceFinancialDocumentTypeCode"/></td>
                                                    <td><html:text
                                                            property="entryForManualEdit.referenceFinancialSystemOriginationCode"
                                                            size="10"
                                                            styleId="entryForManualEdit.referenceFinancialSystemOriginationCode"/></td>
                                                    <td><html:text
                                                            property="entryForManualEdit.referenceFinancialDocumentNumber"
                                                            size="9"
                                                            styleId="entryForManualEdit.referenceFinancialDocumentNumber"/></td>
                                                    <td><html:text property="entryFinancialDocumentReversalDate"
                                                                   size="8"
                                                                   styleId="entryFinancialDocumentReversalDate"/></td>
                                                    <td><html:text
                                                            property="entryForManualEdit.transactionEncumbranceUpdateCode"
                                                            size="13"
                                                            styleId="entryForManualEdit.transactionEncumbranceUpdateCode"/></td>
                                                </c:otherwise>
                                            </c:choose>

                                        </tr>
                                        </tbody>
                                    </table>
                                </td>
                            </tr>
                        </c:if>
                        <c:if test="${KualiForm.manualEditFlag == true}">
                            <td>
                                <STRONG>Do you want to edit this document?</STRONG>
                                <html:submit
                                        property="methodToCall.manualEdit.anchor${currentTabIndex}"
                                        styleClass="btn btn-default"
                                        alt="show edit"
                                        title="show edit"
                                        value="Edit"/>
                            </td>
                        </c:if>
                    </table>
                </div>
            </c:if>
        </kul:tab>
        <kul:tab tabTitle="Edit Options and Action" defaultOpen="true" tabErrorKey="Edit Options and Action">
            <c:if test="${KualiForm.deleteFileFlag == false and (KualiForm.dataLoadedFlag == true || KualiForm.restrictedFunctionalityMode) and ((KualiForm.editMethod == 'C') or (KualiForm.editMethod == 'M' and KualiForm.editableFlag == true))}">
                <div class="tab-container">
                    <table class="standard" summary="">
                        <c:if test="${KualiForm.editMethod == 'C'}">
                            <tr>
                                <td class="center">
                                    <html:checkbox styleId="processInBatch" property="processInBatch"/> <STRONG> <label
                                        for="processInBatch">Process In Batch</label> </STRONG> &nbsp; &nbsp; &nbsp;
                                    &nbsp;
                                    <input type="hidden" name="processInBatch{CheckboxPresentOnFormAnnotation}"
                                           value="true"/>
                                    <html:checkbox styleId="matchCriteriaOnly" property="matchCriteriaOnly"/> <STRONG>
                                    <label for="matchCriteriaOnly">Output only records which match criteria?</label>
                                </STRONG>
                                    <input type="hidden" name="matchCriteriaOnly{CheckboxPresentOnFormAnnotation}"
                                           value="true"/>
                                </td>
                            </tr>
                            <c:if test="${KualiForm.restrictedFunctionalityMode == false}">
                                <tr>
                                    <td class="center">
                                        <c:if test="${KualiForm.showOutputFlag == true}">
                                            <strong>Show Input Group</strong>
                                            <html:submit
                                                    property="methodToCall.showOutputGroup.anchor${currentTabIndex - 1}"
                                                    styleClass="btn btn-default small"
                                                    alt="show Input Group"
                                                    title="show Input Group"
                                                    value="Show"/>
                                        </c:if>
                                        <c:if test="${KualiForm.showOutputFlag == false}">
                                            <strong>Show Output Group</strong>
                                            <html:submit
                                                    property="methodToCall.showOutputGroup.anchor${currentTabIndex - 1}"
                                                    styleClass="btn btn-default small"
                                                    alt="show Output Group"
                                                    title="show Output Group"
                                                    value="Show"/>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:if>
                        </c:if>
                        <c:if test="${KualiForm.editMethod == 'M' and KualiForm.editableFlag == true}">
                            <tr>
                                <td class="center">
                                    <html:checkbox styleId="processInBatch" property="processInBatch"/>
                                    <STRONG><label for="processInBatch">Process In Batch</label></STRONG>
                                    <input type="hidden" name="processInBatch{CheckboxPresentOnFormAnnotation}" value="true"/>
                                </td>
                            </tr>
                        </c:if>
                    </table>
                </div>
            </c:if>
        </kul:tab>
        <kul:tab tabTitle="Edit Criteria" defaultOpen="true" tabErrorKey="editCriteria">
            <c:if test="${KualiForm.deleteFileFlag == false and KualiForm.editMethod == 'C' and (KualiForm.dataLoadedFlag == true || KualiForm.restrictedFunctionalityMode == true)}">
                <div class="tab-container">
                    <table class="standard" summary="">
                        <tr class="header">
                            <th class="subhead">Search Criteria</th>
                            <th class="subhead">Modification Criteria</th>
                        </tr>
                        <c:forEach var="group" items="${KualiForm.document.correctionChangeGroup}">
                            <tr>
                                <td colspan="2" align="left" class="bord-l-b"
                                    style="vertical-align: top;">
                                    <strong>Group:</strong>
                                    <html:submit
                                            property="methodToCall.removeCorrectionGroup.group${group.correctionChangeGroupLineNumber}.anchor${currentTabIndex}"
                                            styleClass="btn btn-red small"
                                            alt="delete correction group"
                                            title="delete correction group"
                                            value="Delete"/>
                                </td>
                            </tr>
                            <tr style="border-bottom: 1px solid #b6b6b6;">
                                <td class="bord-l-b" style="vertical-align: top;">
                                    <label for="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionFieldName">Field</label>:
                                    <html:select
                                            property="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionFieldName"
                                            styleId="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionFieldName"
                                            title="Field">
                                        <option value=""></option>
                                        <c:choose>
                                            <c:when test="${KualiForm.documentType == 'LLCP'}">
                                                <html:optionsCollection
                                                        property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|module|ld|businessobject|options|LaborOriginEntryFieldFinder"
                                                        label="value" value="key"/>
                                            </c:when>
                                            <c:otherwise>
                                                <html:optionsCollection
                                                        property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|gl|businessobject|options|OriginEntryFieldFinder"
                                                        label="value" value="key"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </html:select>
                                    <label for="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionOperatorCode">Operator</label>:
                                    <html:select
                                            property="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionOperatorCode"
                                            styleId="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionOperatorCode"
                                            title="Operator">
                                        <html:optionsCollection
                                                property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|gl|businessobject|options|SearchOperatorsFinder"
                                                label="value" value="key"/>
                                    </html:select>
                                    <label for="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionFieldValue">Value</label>:
                                    <html:text
                                            property="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionFieldValue"
                                            styleId="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionFieldValue"
                                            title="Value"/>
                                    <html:submit
                                            property="methodToCall.addCorrectionCriteria.criteria${group.correctionChangeGroupLineNumber}.anchor${currentTabIndex}"
                                            styleClass="btn btn-green small"
                                            alt="Add Search Criteria"
                                            title="Add Search Criteria"
                                            value="Add"/>
                                    <br/>
                                    <c:forEach items="${group.correctionCriteria}" var="criteria" varStatus="cc">
                                        <label for="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldName">Field</label>:
                                        <html:select
                                                property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldName"
                                                styleId="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldName"
                                                title="field">

                                            <c:choose>
                                                <c:when test="${KualiForm.documentType == 'LLCP'}">
                                                    <html:optionsCollection
                                                            property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|module|ld|businessobject|options|LaborOriginEntryFieldFinder"
                                                            label="value" value="key"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <html:optionsCollection
                                                            property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|gl|businessobject|options|OriginEntryFieldFinder"
                                                            label="value" value="key"/>
                                                </c:otherwise>
                                            </c:choose>

                                        </html:select>
                                        <label for="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionOperatorCode">Operator</label>:
                                        <html:select
                                                property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionOperatorCode"
                                                styleId="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionOperatorCode"
                                                title="Operator">
                                            <html:optionsCollection
                                                    property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|gl|businessobject|options|SearchOperatorsFinder"
                                                    label="value" value="key"/>
                                        </html:select>
                                        <label for="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldValue">Value</label>:
                                        <html:text
                                                property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldValue"
                                                styleId="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldValue"
                                                title="Value"/>
                                        <html:submit
                                                property="methodToCall.removeCorrectionCriteria.criteria${group.correctionChangeGroupLineNumber}-${criteria.correctionCriteriaLineNumber}.anchor${currentTabIndex}"
                                                styleClass="btn btn-red small"
                                                alt="delete search criterion"
                                                title="delete search criterion"
                                                value="Delete"/>
                                        <br>
                                    </c:forEach>
                                </td>
                                <td class="bord-l-b" style="vertical-align: top;">
                                    <label for="groupsItem[${group.correctionChangeGroupLineNumber}].correctionChange.correctionFieldName">Field</label>:
                                    <html:select
                                            property="groupsItem[${group.correctionChangeGroupLineNumber}].correctionChange.correctionFieldName"
                                            styleId="groupsItem[${group.correctionChangeGroupLineNumber}].correctionChange.correctionFieldName"
                                            title="Field">
                                        <option value=""></option>
                                        <c:choose>
                                            <c:when test="${KualiForm.documentType == 'LLCP'}">
                                                <html:optionsCollection
                                                        property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|module|ld|businessobject|options|LaborOriginEntryFieldFinder"
                                                        label="value" value="key"/>
                                            </c:when>
                                            <c:otherwise>
                                                <html:optionsCollection
                                                        property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|gl|businessobject|options|OriginEntryFieldFinder"
                                                        label="value" value="key"/>
                                            </c:otherwise>
                                        </c:choose>

                                    </html:select>
                                    <label for="groupsItem[${group.correctionChangeGroupLineNumber}].correctionChange.correctionFieldValue">Replacement
                                        Value</label>:
                                    <html:text
                                            property="groupsItem[${group.correctionChangeGroupLineNumber}].correctionChange.correctionFieldValue"
                                            styleId="groupsItem[${group.correctionChangeGroupLineNumber}].correctionChange.correctionFieldValue"
                                            title="Replacement Value"/>
                                    <html:submit
                                            property="methodToCall.addCorrectionChange.change${group.correctionChangeGroupLineNumber}.anchor${currentTabIndex}"
                                            alt="add replacement specification"
                                            title="add replacement specification"
                                            styleClass="btn btn-green small"
                                            value="Add"/>
                                    <br/>
                                    <c:forEach items="${group.correctionChange}" var="change">
                                        <label for="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeItem[${change.correctionChangeLineNumber}].correctionFieldName">Field</label>:
                                        <html:select
                                                property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeItem[${change.correctionChangeLineNumber}].correctionFieldName"
                                                styleId="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeItem[${change.correctionChangeLineNumber}].correctionFieldName"
                                                title="Field">
                                            <c:choose>
                                                <c:when test="${KualiForm.documentType == 'LLCP'}">
                                                    <html:optionsCollection
                                                            property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|module|ld|businessobject|options|LaborOriginEntryFieldFinder"
                                                            label="value" value="key"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <html:optionsCollection
                                                            property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|gl|businessobject|options|OriginEntryFieldFinder"
                                                            label="value" value="key"/>
                                                </c:otherwise>
                                            </c:choose>
                                        </html:select>
                                        <label for="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeItem[${change.correctionChangeLineNumber}].correctionFieldValue">Replacement
                                            Value</label>:
                                        <html:text
                                                property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeItem[${change.correctionChangeLineNumber}].correctionFieldValue"
                                                styleId="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionChangeItem[${change.correctionChangeLineNumber}].correctionFieldValue"
                                                title="Replacement Value"/>
                                        <html:submit
                                                property="methodToCall.removeCorrectionChange.change${group.correctionChangeGroupLineNumber}-${change.correctionChangeLineNumber}.anchor${currentTabIndex}"
                                                alt="delete search specification"
                                                title="delete search specification"
                                                styleClass="btn btn-red small"
                                                value="Delete"/>
                                        <br>
                                    </c:forEach>
                                </td>
                            </tr>
                        </c:forEach>
                        <tr>
                            <td colspan="2" align="left" class="bord-l-b" style="vertical-align: top;">
                                <center>
                                    <STRONG>Add Groups </STRONG>
                                    <html:submit
                                            property="methodToCall.addCorrectionGroup.anchor${currentTabIndex}"
                                            alt="add correction group"
                                            title="add correction group"
                                            styleClass="btn btn-green small"
                                            value="Add"/>
                                </center>
                            </td>
                        </tr>
                    </table>
                </div>
            </c:if>
        </kul:tab>
        <!-- Search for Manual Edit -->
        <kul:tab tabTitle="Search Criteria for Manual Edit" defaultOpen="true" tabErrorKey="manualEditCriteria">
            <c:if test="${KualiForm.editMethod == 'M' and KualiForm.editableFlag == true and KualiForm.dataLoadedFlag == true}">
                <div class="tab-container">
                    <table class="standard" summary="">
                        <tr style="border-bottom: 1px solid #b6b6b6;">
                            <td class="bord-l-b" style="vertical-align: top;">
                                <c:forEach var="group" items="${KualiForm.document.correctionChangeGroup}">
                                    <c:forEach items="${group.correctionCriteria}" var="criteria" varStatus="cc">
                                        <label for="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldName">Field</label>:
                                        <html:select
                                                property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldName"
                                                styleId="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldName"
                                                title="Field">
                                            <c:choose>
                                                <c:when test="${KualiForm.documentType == 'LLCP'}">
                                                    <html:optionsCollection
                                                            property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|module|ld|businessobject|options|LaborOriginEntryFieldFinder"
                                                            label="value" value="key"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <html:optionsCollection
                                                            property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|gl|businessobject|options|OriginEntryFieldFinder"
                                                            label="value" value="key"/>
                                                </c:otherwise>
                                            </c:choose>
                                        </html:select>
                                        <label for="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionOperatorCode">Operator</label>:
                                        <html:select
                                                property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionOperatorCode"
                                                styleId="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionOperatorCode"
                                                title="Operator">
                                            <html:optionsCollection
                                                    property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|gl|businessobject|options|SearchOperatorsFinder"
                                                    label="value" value="key"/>
                                        </html:select>
                                        <label for="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldValue">Value</label>:
                                        <html:text
                                                property="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldValue"
                                                styleId="correctionDocument.correctionChangeGroupItem[${group.correctionChangeGroupLineNumber}].correctionCriteriaItem[${criteria.correctionCriteriaLineNumber}].correctionFieldValue"
                                                title="Value"/>
                                        <html:submit
                                                property="methodToCall.removeCorrectionCriteria.criteria${group.correctionChangeGroupLineNumber}-${criteria.correctionCriteriaLineNumber}.anchor${currentTabIndex}"
                                                styleClass="btn btn-red small"
                                                alt="Remove Search Criteria"
                                                title="Remove Search Criteria"
                                                value="Delete"/>
                                        <br/>
                                    </c:forEach>
                                    <label for="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionFieldName">Field</label>:
                                    <html:select
                                            property="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionFieldName"
                                            styleId="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionFieldName"
                                            title="Field">
                                        <option value=""></option>
                                        <c:choose>
                                            <c:when test="${KualiForm.documentType == 'LLCP'}">
                                                <html:optionsCollection
                                                        property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|module|ld|businessobject|options|LaborOriginEntryFieldFinder"
                                                        label="value" value="key"/>
                                            </c:when>
                                            <c:otherwise>
                                                <html:optionsCollection
                                                        property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|gl|businessobject|options|OriginEntryFieldFinder"
                                                        label="value" value="key"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </html:select>
                                    <label for="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionOperatorCode">Operator</label>:
                                    <html:select
                                            property="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionOperatorCode"
                                            styleId="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionOperatorCode"
                                            title="Operator">
                                        <html:optionsCollection
                                                property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|gl|businessobject|options|SearchOperatorsFinder"
                                                label="value" value="key"/>
                                    </html:select>
                                    <label for="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionFieldValue">Value</label>:
                                    <html:text
                                            property="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionFieldValue"
                                            styleId="groupsItem[${group.correctionChangeGroupLineNumber}].correctionCriteria.correctionFieldValue"
                                            title="Value"/>
                                    <html:submit
                                            property="methodToCall.addCorrectionCriteria.criteria${group.correctionChangeGroupLineNumber}.anchor${currentTabIndex}"
                                            styleClass="btn btn-green small"
                                            alt="Add Search Criteria"
                                            title="Add Search Criteria"
                                            value="Add"/>
                                    <br/>
                                </c:forEach>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <center>
                                    <c:if test="${KualiForm.showOutputFlag == true}">
                                        <strong>Show All Entries</strong>
                                        <html:submit
                                                property="methodToCall.searchCancelForManualEdit.anchor${currentTabIndex - 3}"
                                                styleClass="btn btn-default small"
                                                alt="Show Matching Entries"
                                                title="Show Matching Entries"
                                                value="Show"/>
                                    </c:if>
                                    <c:if test="${KualiForm.showOutputFlag == false}">
                                        <strong>Show Matching Entries</strong>
                                        <html:submit
                                                property="methodToCall.searchForManualEdit.anchor${currentTabIndex - 3}"
                                                styleClass="btn btn-default small"
                                                alt="Show All Entries"
                                                title="Show All Entries"
                                                value="Show"/>
                                    </c:if>
                                </center>
                            </td>
                        </tr>
                    </table>
                </div>
            </c:if>
        </kul:tab>
    </c:if>
    <kul:notes/>
    <kul:adHocRecipients/>
    <kul:routeLog/>
    <kul:superUserActions/>
    <sys:documentControls transactionalDocument="false"/>
</kul:documentPage>
