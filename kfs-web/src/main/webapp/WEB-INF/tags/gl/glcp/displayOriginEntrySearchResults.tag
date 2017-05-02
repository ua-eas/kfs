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
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="originEntries" required="true" type="java.util.List" description="The list of OriginEntries that we'll iterate to display." %>

<c:if test="${empty originEntries}">
    No Origin Entries found.
</c:if>
<c:if test="${!empty originEntries}">
    <kul:tableRenderPagingBanner
            pageNumber="${KualiForm.originEntrySearchResultTableMetadata.viewedPageNumber}"
            totalPages="${KualiForm.originEntrySearchResultTableMetadata.totalNumberOfPages}"
            firstDisplayedRow="${KualiForm.originEntrySearchResultTableMetadata.firstRowIndex}"
            lastDisplayedRow="${KualiForm.originEntrySearchResultTableMetadata.lastRowIndex}"
            resultsActualSize="${KualiForm.originEntrySearchResultTableMetadata.resultsActualSize}"
            resultsLimitedSize="${KualiForm.originEntrySearchResultTableMetadata.resultsLimitedSize}"
            buttonExtraParams=".anchor${currentTabIndex}"/>

    <input type="hidden" name="originEntrySearchResultTableMetadata.${Constants.TableRenderConstants.PREVIOUSLY_SORTED_COLUMN_INDEX_PARAM}" value="${KualiForm.originEntrySearchResultTableMetadata.columnToSortIndex}"/>
    <input type="hidden" name="originEntrySearchResultTableMetadata.sortDescending" value="${KualiForm.originEntrySearchResultTableMetadata.sortDescending}"/>
    <input type="hidden" name="originEntrySearchResultTableMetadata.viewedPageNumber" value="${KualiForm.originEntrySearchResultTableMetadata.viewedPageNumber}"/>
    <table class="standard" id="originEntry">
        <thead>
            <tr class="header">
                <c:if test="${KualiForm.editableFlag == true}">
                    <th>Manual Edit</th>
                </c:if>
                <c:forEach items="${KualiForm.tableRenderColumnMetadata}" var="column" varStatus="columnLoopStatus">
                    <th class="sortable nowrap">
                        <c:out value="${column.columnTitle}"/>
                        <c:if test="${column.sortable}">
                            <html:image
                                    property="methodToCall.sort.${columnLoopStatus.index}.anchor${currentTabIndex}"
                                    src="${ConfigProperties.krad.externalizable.images.url}sort_both_kns.png"
                                    styleClass="tinybutton"
                                    alt="Sort column"
                                    title="Sort column ${column.columnTitle}"
                                    style="margin-bottom:-5px;"/>
                        </c:if>
                    </th>
                </c:forEach>
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${originEntries}" var="originEntry" varStatus="loopStatus"
                    begin="${KualiForm.originEntrySearchResultTableMetadata.firstRowIndex}" end="${KualiForm.originEntrySearchResultTableMetadata.lastRowIndex}">

                <c:set var="rowclass" value=""/>
                <c:if test="${loopStatus.count % 2 != 0}">
                    <c:set var="rowclass" value="highlight"/>
                </c:if>
                <tr class="${rowclass}">
                    <c:if test="${KualiForm.editableFlag == true and KualiForm.editMethod == 'M'}">
                        <td>
                            <html:submit
                                    property="methodToCall.editManualEntry.entryId${originEntry.entryId}.anchor${currentTabIndex}"
                                    styleClass="btn btn-default small"
                                    alt="edit"
                                    title="edit"
                                    value="Edit"/>
                            <html:submit
                                    property="methodToCall.deleteManualEntry.entryId${originEntry.entryId}.anchor${currentTabIndex}"
                                    styleClass="btn btn-red small"
                                    alt="delete"
                                    title="delete"
                                    value="Delete"/>
                        </td>
                    </c:if>
                    <td class="infocell"><c:out value="${originEntry.universityFiscalYear}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.chartOfAccountsCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.accountNumber}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.subAccountNumber}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.financialObjectCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.financialSubObjectCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.financialBalanceTypeCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.financialObjectTypeCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.universityFiscalPeriodCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.financialDocumentTypeCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.financialSystemOriginationCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.documentNumber}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.transactionLedgerEntrySequenceNumber}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.transactionLedgerEntryDescription}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.transactionLedgerEntryAmount}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.transactionDebitCreditCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.transactionDate}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.organizationDocumentNumber}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.projectCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.organizationReferenceId}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.referenceFinancialDocumentTypeCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.referenceFinancialSystemOriginationCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.referenceFinancialDocumentNumber}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.financialDocumentReversalDate}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.transactionEncumbranceUpdateCode}" />&nbsp;</td>
                </tr>
            </c:forEach>
        <tbody>
    </table>
</c:if>
