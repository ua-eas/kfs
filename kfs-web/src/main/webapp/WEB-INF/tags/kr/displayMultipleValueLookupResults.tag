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

<%@ attribute name="resultsList" required="true" type="java.util.List" description="The rows of fields that we'll iterate to display." %>
<c:if test="${empty resultsList && KualiForm.methodToCall != 'start' && KualiForm.methodToCall != 'refresh'}">
	There were no results found.
</c:if>
<c:if test="${!empty resultsList}">
    <c:if test="${KualiForm.searchUsingOnlyPrimaryKeyValues}">
    	<bean-el:message key="lookup.using.primary.keys" arg0="${KualiForm.primaryKeyFieldLabels}"/>
    	<br/><br/>
    </c:if>
    <c:if test="${! KualiForm.hasReturnableRow}">
    	<bean-el:message key="multiple.value.lookup.no.returnable.rows" />
    	<br/><br/>
    </c:if>
	<c:choose>
		<c:when test="${param['d-16544-e'] == null}">
			<kul:tableRenderPagingBanner pageNumber="${KualiForm.viewedPageNumber}" totalPages="${KualiForm.totalNumberOfPages}"
				firstDisplayedRow="${KualiForm.firstRowIndex}" lastDisplayedRow="${KualiForm.lastRowIndex}" resultsActualSize="${KualiForm.resultsActualSize}"
				resultsLimitedSize="${KualiForm.resultsLimitedSize}"
				buttonExtraParams=".${Constants.METHOD_TO_CALL_PARM12_LEFT_DEL}${KualiForm.searchUsingOnlyPrimaryKeyValues}${Constants.METHOD_TO_CALL_PARM12_RIGHT_DEL}"/>
			<input type="hidden" name="${Constants.MULTIPLE_VALUE_LOOKUP_PREVIOUSLY_SELECTED_OBJ_IDS_PARAM}" value="${KualiForm.compositeSelectedObjectIds}"/>
			<input type="hidden" name="${Constants.TableRenderConstants.PREVIOUSLY_SORTED_COLUMN_INDEX_PARAM}" value="${KualiForm.columnToSortIndex}"/>
			<c:if test="${KualiForm.hasReturnableRow}" >
				<p>
					<html:submit
							alt="Select all rows from all pages"
							title="Select all rows from all pages"
							styleClass="btn btn-default"
							property="methodToCall.selectAll.${Constants.METHOD_TO_CALL_PARM12_LEFT_DEL}${KualiForm.searchUsingOnlyPrimaryKeyValues}${Constants.METHOD_TO_CALL_PARM12_RIGHT_DEL}.x"
							value="Select All From All Pages"/>
					<html:submit
							alt="Deselect all rows from all pages"
							title="Unselect all rows from all pages"
							styleClass="btn btn-default"
							property="methodToCall.unselectAll.${Constants.METHOD_TO_CALL_PARM12_LEFT_DEL}${KualiForm.searchUsingOnlyPrimaryKeyValues}${Constants.METHOD_TO_CALL_PARM12_RIGHT_DEL}.x"
							value="Deselect All From All Pages"/>
					<button
							class="btn btn-default"
							onclick="setAllMultipleValueLookuResults(true); return false;"
							alt="Select all rows from this page"
							title="Select all rows from this page">
						Select All From This Page
					</button>
					<button
							class="btn btn-default"
							onclick="setAllMultipleValueLookuResults(false); return false;"
							alt="Deselect all rows from this page"
							title="Deselect all rows from this page">
						Deselect All From This Page
					</button>
					<html:submit
							alt="Return selected results"
							title="Return selected results"
							styleClass="btn btn-default"
							property="methodToCall.prepareToReturnSelectedResults.${Constants.METHOD_TO_CALL_PARM12_LEFT_DEL}${KualiForm.searchUsingOnlyPrimaryKeyValues}${Constants.METHOD_TO_CALL_PARM12_RIGHT_DEL}"
							value="Return Selected"/>
				</p>
			</c:if>
			<table class="datatable-100" id="row">
				<thead>
					<tr>
											<th>
							Select?
						</th>
						<c:forEach items="${resultsList[0].columns}" var="column" varStatus="columnLoopStatus">
							<th class="sortable nowrap">
								${column.columnTitle}
								&nbsp;
								<input name="methodToCall.sort.<c:out value="${columnLoopStatus.index}"/>.${Constants.METHOD_TO_CALL_PARM12_LEFT_DEL}${KualiForm.searchUsingOnlyPrimaryKeyValues}${Constants.METHOD_TO_CALL_PARM12_RIGHT_DEL}" type="image" src="${ConfigProperties.krad.externalizable.images.url}sort_both_kns.png" alt="Sort column ${column.columnTitle}" valign="bottom" title="Sort column ${column.columnTitle}">
							</th>
						</c:forEach>
					</tr>

				</thead>
				<c:forEach items="${resultsList}" var="row" varStatus="rowLoopStatus" begin="${KualiForm.firstRowIndex}" end="${KualiForm.lastRowIndex}">
					<c:set var="rowclass" value="odd"/>
					<c:if test="${rowLoopStatus.count % 2 == 0}">
						<c:set var="rowclass" value="even"/>
					</c:if>
					<tr class="${rowclass}">
					<td class="infocell">
							<c:if test="${ row.rowReturnable }" >
								<c:set var="returnValue" value="${row.rowId}" />
								${row.returnUrl}
								${kfunc:registerEditableProperty(KualiForm, returnValue)}
								<input type="hidden" name="${Constants.MULTIPLE_VALUE_LOOKUP_DISPLAYED_OBJ_ID_PARAM_PREFIX}${row.objectId}" value="onscreen"/>
							</c:if>
						</td>
												<c:forEach items="${row.columns}" var="column">
							<td class="infocell" title="${column.propertyValue}">
								<c:if test="${!empty column.columnAnchor.href}">
									<a href="<c:out value="${column.columnAnchor.href}"/>" title="${column.columnAnchor.title}" target="_blank">
								</c:if>
								<c:out value="${fn:substring(column.propertyValue, 0, column.maxLength)}"/><c:if test="${column.maxLength gt 0 && fn:length(column.propertyValue) gt column.maxLength}">...</c:if><c:if test="${!empty column.columnAnchor.href}"></a></c:if>
								&nbsp;
							</td>
						</c:forEach>
											</tr>
				</c:forEach>
			</table>
			<c:if test="${ KualiForm.hasReturnableRow }" >
				<p>
					<html:submit
							alt="Select all rows from all pages"
							title="Select all rows from all pages"
							styleClass="btn btn-default"
							property="methodToCall.selectAll.${Constants.METHOD_TO_CALL_PARM12_LEFT_DEL}${KualiForm.searchUsingOnlyPrimaryKeyValues}${Constants.METHOD_TO_CALL_PARM12_RIGHT_DEL}.x"
							value="Select All From All Pages"/>
					<html:submit
							alt="Deselect all rows from all pages"
							title="Unselect all rows from all pages"
							styleClass="btn btn-default"
							property="methodToCall.unselectAll.${Constants.METHOD_TO_CALL_PARM12_LEFT_DEL}${KualiForm.searchUsingOnlyPrimaryKeyValues}${Constants.METHOD_TO_CALL_PARM12_RIGHT_DEL}.x"
							value="Deselect All From All Pages"/>
					<button
							class="btn btn-default"
							onclick="setAllMultipleValueLookuResults(true); return false;"
							alt="Select all rows from this page"
							title="Select all rows from this page">
						Select All From This Page
					</button>
					<button
							class="btn btn-default"
							onclick="setAllMultipleValueLookuResults(false); return false;"
							alt="Deselect all rows from this page"
							title="Deselect all rows from this page">
						Deselect All From This Page
					</button>
					<html:submit
							alt="Return selected results"
							title="Return selected results"
							styleClass="btn btn-default"
							property="methodToCall.prepareToReturnSelectedResults.${Constants.METHOD_TO_CALL_PARM12_LEFT_DEL}${KualiForm.searchUsingOnlyPrimaryKeyValues}${Constants.METHOD_TO_CALL_PARM12_RIGHT_DEL}"
							value="Return Selected"/>
				</p>
			</c:if>
			<kul:multipleValueLookupExportBanner/>
		</c:when>
		<c:otherwise>
			<display:table class="datatable-100"
				requestURIcontext="false" name="${reqSearchResults}"
				id="row" export="true" pagesize="100">
				<c:forEach items="${row.columns}" var="column" varStatus="loopStatus">
					<display:column class="${colClass}" sortable="${column.sortable}"
								title="${column.columnTitle}" comparator="${column.comparator}"
								maxLength="${column.maxLength}"><c:out value="${column.propertyValue}" escapeXml="false" default="" /></display:column>
				</c:forEach>
			</display:table>
		</c:otherwise>
	</c:choose>
</c:if>
