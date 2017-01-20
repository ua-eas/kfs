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

<%--NOTE: DO NOT FORMAT THIS FILE, DISPLAY:COLUMN WILL NOT WORK CORRECTLY IF IT CONTAINS LINE BREAKS --%>
<c:set var="headerMenu" value=""/>
<c:if test="${KualiForm.suppressActions!=true and KualiForm.supplementalActionsEnabled!=true}">
    <c:set var="headerMenu" value="${KualiForm.lookupable.createNewUrl}   ${KualiForm.lookupable.htmlMenuBar}"/>
</c:if>

<c:set var="numberOfColumns" value="${KualiForm.numColumns}"/>
<c:set var="headerColspan" value="${numberOfColumns * 2}"/>


<kul:page lookup="true" showDocumentInfo="false"
          headerMenuBar="${headerMenu}"
          headerTitle="Lookup" docTitle="" transactionalDocument="false"
          htmlFormAction="lookup" placeFocus="${empty reqSearchResultsActualSize}">

    <SCRIPT type="text/javascript">
        var kualiForm = document.forms['KualiForm'];
        var kualiElements = kualiForm.elements;
    </SCRIPT>

    <c:if test="${KualiForm.supplementalActionsEnabled}">
        <div class="lookupcreatenew" title="Supplemental Search Actions">
                ${KualiForm.lookupable.supplementalMenuBar} &nbsp;
            <c:set var="extraField" value="${KualiForm.lookupable.extraField}"/>
            <c:if test="${not empty extraField}">
                <%--has to be a dropdown script for now--%>
                <c:if test="${extraField.fieldType eq extraField.DROPDOWN_SCRIPT}">

                    ${kfunc:registerEditableProperty(KualiForm, extraField.propertyName)}
                    <select id='${extraField.propertyName}' name='${extraField.propertyName}'
                            onchange="${extraField.script}" style="">
                        <kul:fieldSelectValues field="${extraField}"/>
                    </select>

                    &nbsp;

                    <kul:fieldShowIcons isReadOnly="${true}" field="${extraField}" addHighlighting="${true}"/>

                </c:if>
            </c:if>
        </div>
    </c:if>

    <c:if test="${KualiForm.headerBarEnabled}">
        <div class="headerarea-small" id="headerarea-small">
            <h1><c:out value="${KualiForm.lookupable.title}"/> <c:choose>
                <c:when test="${KualiForm.fields.documentTypeName != null}">
                    <%-- this is a custom doc search --%>
                    <kul:help searchDocumentTypeName="${fn:escapeXml(KualiForm.fields.documentTypeName)}" altText="lookup help"/>
                </c:when>
                <c:otherwise>
                    <%-- KNS looup --%>
                    <kul:help lookupBusinessObjectClassName="${KualiForm.lookupable.businessObjectClass.name}" altText="lookup help"/>
                </c:otherwise>
            </c:choose></h1>
        </div>
    </c:if>

    <c:if test="${KualiForm.renderSearchButtons}">
        <kul:enterKey methodToCall="search"/>
    </c:if>

    <html-el:hidden name="KualiForm" property="backLocation"/>
    <html-el:hidden name="KualiForm" property="formKey"/>
    <html-el:hidden name="KualiForm" property="lookupableImplServiceName"/>
    <html-el:hidden name="KualiForm" property="businessObjectClassName"/>
    <html-el:hidden name="KualiForm" property="conversionFields"/>
    <html-el:hidden name="KualiForm" property="hideReturnLink"/>
    <html-el:hidden name="KualiForm" property="suppressActions"/>
    <html-el:hidden name="KualiForm" property="multipleValues"/>
    <html-el:hidden name="KualiForm" property="lookupAnchor"/>
    <html-el:hidden name="KualiForm" property="readOnlyFields"/>
    <html-el:hidden name="KualiForm" property="referencesToRefresh"/>
    <html-el:hidden name="KualiForm" property="hasReturnableRow"/>
    <html-el:hidden name="KualiForm" property="docNum"/>
    <html-el:hidden name="KualiForm" property="showMaintenanceLinks"/>
    <html-el:hidden name="KualiForm" property="headerBarEnabled"/>
    <html-el:hidden name="KualiForm" property="fieldNameToFocusOnAfterSubmit"/>


    <c:if test="${KualiForm.headerBarEnabled}">
        <c:forEach items="${KualiForm.extraButtons}" varStatus="status">
            <html-el:hidden name="KualiForm" property="extraButtons[${status.index}].extraButtonSource"/>
            <html-el:hidden name="KualiForm" property="extraButtons[${status.index}].extraButtonParams"/>
        </c:forEach>

        <div class="msg-excol">
            <div class="left-errmsg">
                <kul:errors errorTitle="Errors found in Search Criteria:"/>
                <kul:messages/>
            </div>
        </div>
    </c:if>

    <table width="100%">
        <c:if test="${KualiForm.lookupCriteriaEnabled}">
            <tr>
                <td>
                    <div id="lookup" align="center">
                        <c:if test="${numberOfColumns > 1}">
                            <c:set var="tableClass" value="multi-column-table"/>
                        </c:if>
                        <table align="center" class="${tableClass}">
                            <c:set var="FormName" value="KualiForm" scope="request"/>
                            <c:set var="FieldRows" value="${KualiForm.lookupable.rows}" scope="request"/>
                            <c:set var="ActionName" value="Lookup.do" scope="request"/>
                            <c:set var="IsLookupDisplay" value="true" scope="request"/>
                            <c:set var="cellWidth" value="50%" scope="request"/>

                            <kul:rowDisplay rows="${FieldRows}" skipTheOldNewBar="true" numberOfColumns="${numberOfColumns}"/>

                            <tr align="center">
                                <td height="30" colspan="${headerColspan}" class="infoline">
                                    <c:if test="${KualiForm.renderSearchButtons}">
                                        <html:submit
                                                property="methodToCall.search" value="Search"
                                                styleClass="tinybutton btn btn-default"
                                                alt="Search" title="Search"/>
                                        <html:submit
                                                property="methodToCall.clearValues" value="Clear"
                                                styleClass="tinybutton btn btn-default"
                                                alt="Clear" title="Clear"/>
                                    </c:if>

                                    <c:if test="${KualiForm.formKey!=''}">
                                        <input name="" type="t" value="" style="display:none"/>

                                        <c:if test="${!empty KualiForm.backLocation}">
                                            <a href='<c:out value="${KualiForm.backLocation}?methodToCall=refresh&docFormKey=${KualiForm.formKey}&anchor=${KualiForm.lookupAnchor}&docNum=${KualiForm.docNum}" />' title="Cancel">
                                                <span class="tinybutton btn btn-default">Cancel</span>
                                            </a>
                                        </c:if>
                                    </c:if>

                                    <c:forEach items="${KualiForm.extraButtons}" var="extraButton" varStatus="status">
                                        <c:if test="${!empty extraButton.extraButtonSource && !empty extraButton.extraButtonParams}">
                                            <c:if test="${not KualiForm.ddExtraButton && !empty KualiForm.backLocation}">
                                                <a href='<c:out value="${KualiForm.backLocation}?methodToCall=refresh&refreshCaller=kualiLookupable&docFormKey=${KualiForm.formKey}&anchor=${KualiForm.lookupAnchor}&docNum=${KualiForm.docNum}" /><c:out value="${extraButton.extraButtonParams}" />' title='<c:out value="${extraButton.extraButtonAltText}" />'>
                                                    <span class="tinybutton btn btn-default"><c:out value="${extraButton.extraButtonAltText}"/></span>
                                                </a>
                                            </c:if>
                                            <c:if test="${KualiForm.ddExtraButton}">
                                                <html:button value="${extraButton.extraButtonAltText}"
                                                             property="methodToCall.customLookupableMethodCall"
                                                             alt="${extraButton.extraButtonAltText}"
                                                             onclick="${extraButton.extraButtonOnclick}"
                                                             styleClass="tinybutton btn btn-default"/>
                                            </c:if>
                                        </c:if>
                                    </c:forEach>

                                    <c:if test="${KualiForm.multipleValues && !empty KualiForm.backLocation}">
                                        <a href='<c:out value="${KualiForm.backLocation}?methodToCall=refresh&docFormKey=${KualiForm.formKey}&anchor=${KualiForm.lookupAnchor}" />' title="Return with No Value">
                                            <span class="tinybutton btn btn-default">Return with No Value</span>
                                        </a>
                                        <a href='<c:out value="${KualiForm.backLocation}?methodToCall=refresh&docFormKey=${KualiForm.formKey}&refreshCaller=multipleValues&searchResultKey=${searchResultKey}&searchResultDataKey=${searchResultDataKey}&anchor=${KualiForm.lookupAnchor}"/>' title="Return These">
                                            <span class="tinybutton btn btn-default">Return These</span>
                                        </a>
                                    </c:if>
                                </td>
                            </tr>
                        </table>
                    </div>
                </td>
            </tr>
        </c:if>
    </table>

    <c:if test="${!empty reqSearchResultsActualSize and reqSearchResultsActualSize < 1 and empty reqSearchResults}">
        <div class="search-message"><bean-el:message key="error.no.matching.invoice"/></div>
    </c:if>

    <c:if test="${!empty reqSearchResultsActualSize && empty reqSearchResults && !KualiForm.hasReturnableRow && KualiForm.formKey!='' && KualiForm.hideReturnLink!=true && !KualiForm.multipleValues}">
        <div class="search-message"><bean-el:message key="lookup.no.returnable.rows"/></div>
    </c:if>

    <c:if test="${!empty reqSearchResultsActualSize && !empty reqSearchResults}">
        <c:if test="${reqSearchResultsActualSize > reqSearchResultsLimitedSize && reqSearchResultsLimitedSize>0}">
            <div class="search-message">
                <span class="heavy"><c:out value="${reqSearchResultsActualSize}"/></span> items found. Please refine your search criteria to narrow down your search.
            </div>
        </c:if>
        <c:if test="${KualiForm.searchUsingOnlyPrimaryKeyValues}">
            <div class="search-message"><bean-el:message key="lookup.using.primary.keys" arg0="${KualiForm.primaryKeyFieldLabels}"/></div>
        </c:if>

        <a id="search-results"></a>
        <div class="main-panel search-results">
            <display:table class="datatable-100"
                           requestURIcontext="false" name="${reqSearchResults}"
                           id="row" export="true" pagesize="100" varTotals="totals"
                           excludedParams="methodToCall reqSearchResultsActualSize searchResultKey searchUsingOnlyPrimaryKeyValues actionUrlsExist"
                           requestURI="lookup.do?methodToCall=viewResults&reqSearchResultsActualSize=${reqSearchResultsActualSize}&searchResultKey=${searchResultKey}&searchUsingOnlyPrimaryKeyValues=${KualiForm.searchUsingOnlyPrimaryKeyValues}&actionUrlsExist=${KualiForm.actionUrlsExist}">

                <%-- the param['d-16544-e'] parameter below is NOT null when we are in exporting mode, so this check disables rendering of return/action URLs when we are exporting to CSV, excel, xml, etc. --%>
                <c:if test="${param['d-16544-e'] == null}">
                    <logic:present name="KualiForm" property="formKey">
                        <c:if test="${KualiForm.formKey!='' && KualiForm.hideReturnLink!=true && !KualiForm.multipleValues && !empty KualiForm.backLocation}">
                            <c:choose>
                                <c:when test="${row.rowReturnable}">
                                    <display:column class="infocell" media="html" title="Return Value">${row.returnUrl}</display:column>
                                </c:when>
                                <c:otherwise>
                                    <display:column class="infocell" media="html" title="Blank">&nbsp;</display:column>
                                </c:otherwise>
                            </c:choose>
                        </c:if>
                        <c:if test="${KualiForm.actionUrlsExist==true && KualiForm.suppressActions!=true && !KualiForm.multipleValues && KualiForm.showMaintenanceLinks}">
                            <c:choose>
                                <c:when test="${row.actionUrls!=''}">
                                    <display:column class="infocell" property="actionUrls"
                                                    title="Actions" media="html"/>
                                </c:when>
                                <c:otherwise>
                                    <display:column class="infocell"
                                                    title="Actions" media="html">&nbsp;</display:column>
                                </c:otherwise>
                            </c:choose>
                        </c:if>
                    </logic:present>
                </c:if>

                <%-- needed for total line display --%>
                <c:set var="columnNums" value=""/>
                <c:set var="totalColumnNums" value=""/>

                <c:forEach items="${row.columns}" var="column" varStatus="loopStatus">
                    <c:set var="colClass" value="${ fn:startsWith(column.formatter, 'org.kuali.kfs.krad.web.format.CurrencyFormatter') ? 'numbercell' : 'infocell' }"/>

                    <c:if test="${!empty columnNums}">
                        <c:set var="columnNums" value="${columnNums},"/>
                    </c:if>
                    <c:set var="columnNums" value="${columnNums}column${loopStatus.count}"/>

                    <c:set var="staticColumnValue" value="${column.propertyValue}"/>
                    <c:if test="${column.total}">
                        <c:set var="staticColumnValue" value="${column.unformattedPropertyValue}"/>

                        <c:if test="${!empty totalColumnNums}">
                            <c:set var="totalColumnNums" value="${totalColumnNums},"/>
                        </c:if>
                        <c:set var="totalColumnNums" value="${totalColumnNums}column${loopStatus.count}"/>
                    </c:if>

                    <c:choose>
                        <%--NOTE: Check if exporting first, as this should be outputted without extra HTML formatting --%>
                        <c:when test="${param['d-16544-e'] != null}">
                            <display:column class="${colClass}" sortable="${column.sortable}"
                                            title="${column.columnTitle}" comparator="${column.comparator}" total="${column.total}" value="${staticColumnValue}"
                                            maxLength="${column.maxLength}"><c:out value="${column.propertyValue}" escapeXml="false" default=""/></display:column>
                        </c:when>
                        <c:when test="${!empty column.columnAnchor.href || column.multipleAnchors}">
                            <display:column class="${colClass}" sortable="${column.sortable}" title="${column.columnTitle}" comparator="${column.comparator}"><c:choose><c:when test="${column.multipleAnchors}"><c:set var="numberOfColumnAnchors" value="${column.numberOfColumnAnchors}"/><c:choose><c:when test="${empty columnAnchor.target}"><c:set var="anchorTarget" value="_blank"/></c:when><c:otherwise><c:set var="anchorTarget" value="${columnAnchor.target}"/></c:otherwise></c:choose><!-- Please don't change formatting of this logic:iterate block --><logic:iterate id="columnAnchor" name="column" property="columnAnchors" indexId="ctr"><a href="<c:out value="${columnAnchor.href}"/>" target='<c:out value="${columnAnchor.target}"/>' title="${columnAnchor.title}"><c:out
                                    value="${fn:substring(columnAnchor.displayText, 0, column.maxLength)}" escapeXml="${column.escapeXMLValue}"/><c:if test="${column.maxLength gt 0 && fn:length(columnAnchor.displayText) gt column.maxLength}">...</c:if></a><c:if test="${ctr lt numberOfColumnAnchors-1}">,&nbsp;</c:if></logic:iterate></c:when><c:otherwise><c:choose><c:when test="${empty column.columnAnchor.target}"><c:set var="anchorTarget" value="_blank"/></c:when><c:otherwise><c:set var="anchorTarget" value="${column.columnAnchor.target}"/></c:otherwise></c:choose>
                                <div class="link-wrapper"><c:choose><c:when test="${!column.columnAnchor.modalAllowed}"><a href="<c:out value="${column.columnAnchor.href}&mode=standalone"/>" target='<c:out value="${anchorTarget}"/>' title="${column.columnAnchor.title} (open in new tab)"><c:out value="${fn:substring(column.propertyValue, 0, column.maxLength)}" escapeXml="${column.escapeXMLValue}"/><c:if test="${column.maxLength gt 0 && fn:length(column.propertyValue) gt column.maxLength}">...</c:if></a></c:when><c:otherwise><a href="<c:out value="${column.columnAnchor.href}&mode=modal"/>" title="${column.columnAnchor.title}" data-label="${column.columnAnchor.objectLabel}" data-remodal-target="modal"><c:out value="${fn:substring(column.propertyValue, 0, column.maxLength)}"
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           escapeXml="${column.escapeXMLValue}"/><c:if
                                        test="${column.maxLength gt 0 && fn:length(column.propertyValue) gt column.maxLength}">...</c:if></a><a href="<c:out value="${column.columnAnchor.href}&mode=standalone"/>" target='<c:out value="${anchorTarget}"/>' title="Open in new tab" class="new-window"><span class="glyphicon glyphicon-new-window"></span></a></c:otherwise></c:choose></div>
                            </c:otherwise></c:choose></display:column>
                        </c:when>
                        <%--NOTE: DO NOT FORMAT THIS FILE, DISPLAY:COLUMN WILL NOT WORK CORRECTLY IF IT CONTAINS LINE BREAKS --%>
                        <c:otherwise>
                            <display:column class="${colClass}" sortable="${column.sortable}"
                                            title="${column.columnTitle}" comparator="${column.comparator}" total="${column.total}" value="${fn:escapeXml(staticColumnValue)}"
                                            maxLength="${column.maxLength}" decorator="org.kuali.kfs.kns.web.ui.FormatAwareDecorator"><c:out value="${column.propertyValue}"/>&nbsp;</display:column>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>

                <%-- block for displaying the total line --%>
                <c:if test="${!empty totalColumnNums}">
                    <display:footer>
                        <tr>
                            <c:forTokens var="colNum" items="${columnNums}" delims="," varStatus="loopStatus">
                                <c:set var="isTotalColumn" value="false"/>

                                <c:forTokens var="totalNum" items="${totalColumnNums}" delims=",">
                                    <c:if test="${totalNum eq colNum}">
                                        <c:set var="isTotalColumn" value="true"/>
                                    </c:if>
                                </c:forTokens>

                                <c:choose>
                                    <c:when test="${isTotalColumn}">
                                        <td><b><fmt:formatNumber type="currency" value="${totals[colNum]}"/></b></td>
                                    </c:when>
                                    <c:otherwise>
                                        <td>
                                            <c:if test="${loopStatus.first}">
                                                <b><bean-el:message key="lookup.total.row.label"/></b>
                                            </c:if>
                                        </td>
                                    </c:otherwise>
                                </c:choose>

                            </c:forTokens>
                        </tr>
                    </display:footer>
                </c:if>

            </display:table>

            <c:if test="${!empty reqSearchResultsActualSize }">
                <kul:scrollToSearchResults/>
            </c:if>
        </div>
    </c:if>

    <kul:stickyLookupButtons/>

</kul:page>
