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
<%@ include file="/jsp/sys/kfsTldHeader.jsp" %>

<kul:page lookup="true" showDocumentInfo="false"
          htmlFormAction="glModifiedInquiry"
          headerMenuBar="${KualiForm.lookupable.htmlMenuBar}"
          headerTitle="Lookup" docTitle="" transactionalDocument="false">
    <div class="headerarea-small" id="headerarea-small">
        <h1><c:out value="${KualiForm.lookupable.title}"/> <kul:help
                resourceKey="lookupHelpText" altText="lookup help"/></h1>
    </div>
    <kul:enterKey methodToCall="search"/>
    <html-el:hidden name="KualiForm" property="backLocation"/>
    <html-el:hidden name="KualiForm" property="formKey"/>
    <html-el:hidden name="KualiForm" property="lookupableImplServiceName"/>
    <html-el:hidden name="KualiForm" property="businessObjectClassName"/>
    <html-el:hidden name="KualiForm" property="conversionFields"/>
    <html-el:hidden name="KualiForm" property="hideReturnLink"/>
    <kul:errors errorTitle="Errors found in Search Criteria:"/>

    <c:set var="numberOfColumns" value="${KualiForm.numColumns}"/>
    <c:if test="${numberOfColumns > 1}">
        <c:set var="tableClass" value="multi-column-table"/>
    </c:if>

    <table align="center">
        <tr>
            <td>
                <c:if test="${param.inquiryFlag != 'true'}">
                    <div id="lookup" align="center">
                        <table class="${tableClass}" align="center">
                            <c:set var="FormName" value="KualiForm" scope="request"/>
                            <c:set var="FieldRows" value="${KualiForm.lookupable.rows}" scope="request"/>
                            <c:set var="ActionName" value="glModifiedInquiry.do" scope="request"/>
                            <c:set var="IsLookupDisplay" value="true" scope="request"/>

                            <kul:rowDisplay rows="${KualiForm.lookupable.rows}"
                                            numberOfColumns="${numberOfColumns}"/>

                            <tr align=center>
                                <td height="30" colspan="${numberOfColumns*2}" class="infoline">
                                    <html:submit
                                            property="methodToCall.search" value="Search"
                                            styleClass="tinybutton btn btn-default"
                                            alt="Search" title="Search"/>
                                    <html:submit
                                            property="methodToCall.clearValues" value="Clear"
                                            styleClass="tinybutton btn btn-default"
                                            alt="Clear" title="Clear"/>

                                    <c:set var="backLocation" value="${KualiForm.backLocation}"/>
                                    <c:if test="${empty backLocation}">
                                        <c:set var="backLocation" value="portal.do"/>
                                    </c:if>
                                    <c:if test="${KualiForm.formKey!=''}">
                                        <a href='<c:out value="${backLocation}?methodToCall=refresh&docFormKey=${KualiForm.formKey}" />' title="Cancel">
                                            <span class="tinybutton btn btn-default">Cancel</span>
                                        </a>
                                    </c:if>
                                    <c:if test="${not empty KualiForm.lookupable.extraButtonSource}">
                                        <a href='<c:out value="${backLocation}?methodToCall=refresh&refreshCaller=org.kuali.kfs.kns.lookup.KualiLookupableImpl&docFormKey=${KualiForm.formKey}" /><c:out value="${KualiForm.lookupable.extraButtonParams}" />' title='<c:out value="${KualiForm.lookupable.extraAltText}" />'>
                                            <span class="tinybutton btn btn-default"><c:out value="${KualiForm.lookupable.extraAltText}"/></span>
                                        </a>
                                    </c:if>
                                </td>
                            </tr>
                        </table>
                    </div>
                </c:if>
            </td>
        </tr>
    </table>

    <c:if test="${reqSearchResults != null and empty reqSearchResults}">
        <div class="search-message"><bean-el:message key="error.no.matching.invoice"/></div>
    </c:if>

    <table width="100%">
        <tr>
            <td>
                <c:if test="${!empty reqSearchResultsSize}">
                    <%-- If results size isn't the same as the actual size, it
                         means that the result set was truncated (see
                         LookupDaoOjb.executeSearch), so display the total amount
                         of matching records. --%>
                <c:if test="${reqSearchResultsSize != fn:length(reqSearchResults)}">
                    <c:out value="${reqSearchResultsSize}"/> items found.
                    Please refine your search criteria to narrow down your search.
                </c:if>

                <c:set var="exporting" value="${!empty param['d-16544-e']}"
                       scope="request"/>
                <a id="search-results"></a>
                <div class="main-panel search-results">
                    <display:table class="datatable-100" name="${reqSearchResults}" id="row" export="true" pagesize="100" defaultsort="1"
                                   requestURI="glModifiedInquiry.do?methodToCall=viewResults&reqSearchResultsSize=${reqSearchResultsSize}&searchResultKey=${searchResultKey}">

                        <c:forEach items="${row.columns}" var="column">

                            <c:if test="${!empty column.columnAnchor.title}">
                                <c:set var="title" value="${column.columnAnchor.title}"/>
                            </c:if>
                            <c:if test="${empty column.columnAnchor.title}">
                                <c:set var="title" value="${column.propertyValue}"/>
                            </c:if>
                            <c:choose>

                                <c:when
                                        test="${column.formatter.implementationClass == 'org.kuali.rice.core.web.format.CurrencyFormatter'}">

                                    <display:column class="numbercell" sortable="true"
                                                    decorator="org.kuali.kfs.kns.web.ui.FormatAwareDecorator"
                                                    title="${column.columnTitle}" comparator="${column.comparator}">

                                        <c:choose>

                                            <c:when test="${column.propertyURL != \"\"}">
                                                <a href="<c:out value="${column.propertyURL}"/>"
                                                   title="<c:out value="${title}" />" target="blank"><c:out
                                                        value="${column.propertyValue}"/></a>
                                            </c:when>

                                            <c:otherwise>
                                                <c:out value="${column.propertyValue}"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </display:column>

                                </c:when>

                                <c:otherwise>

                                    <c:choose>

                                        <c:when test="${column.propertyURL != \"\"}">

                                            <display:column class="infocell" sortable="${column.sortable}"
                                                            decorator="org.kuali.kfs.kns.web.ui.FormatAwareDecorator"
                                                            title="${column.columnTitle}"
                                                            comparator="${column.comparator}">

                                                <a href="<c:out value="${column.propertyURL}"/>"
                                                   title="<c:out value="${title}" />" target="blank"><c:out
                                                        value="${column.propertyValue}"/></a>

                                            </display:column>

                                        </c:when>

                                        <c:otherwise>

                                            <display:column class="infocell" sortable="${column.sortable}"
                                                            decorator="org.kuali.kfs.kns.web.ui.FormatAwareDecorator"
                                                            title="${column.columnTitle}"
                                                            comparator="${column.comparator}">

                                                <c:if
                                                        test="${!exporting && column.columnTitle == 'Project Code'}">
                                                    <div style="white-space: nowrap"><c:out
                                                            value="${column.propertyValue}"/></div>
                                                </c:if>

                                                <c:if
                                                        test="${exporting || column.columnTitle != 'Project Code'}">
                                                    <c:out value="${column.propertyValue}"/>
                                                </c:if>

                                            </display:column>

                                        </c:otherwise>

                                    </c:choose>

                                </c:otherwise>

                            </c:choose>

                        </c:forEach>
                        <c:if test="${param['d-16544-e'] == null}">
                            <logic:present name="KualiForm" property="formKey">
                                <c:if
                                        test="${KualiForm.formKey!='' && KualiForm.hideReturnLink!=true && !KualiForm.multipleValues && param.inquiryFlag != 'true'}">
                                    <display:column class="infocell" property="returnUrl"
                                                    media="html"/>
                                </c:if>
                                <c:if
                                        test="${row.actionUrls!='' && KualiForm.suppressActions!=true && !KualiForm.multipleValues && KualiForm.showMaintenanceLinks}">
                                    <display:column class="infocell" property="actionUrls"
                                                    title="Actions" media="html"/>
                                </c:if>
                            </logic:present>
                        </c:if>
                    </display:table></div>
            </td>
            </c:if>
        </tr>
    </table>

    <kul:stickyLookupButtons/>

</kul:page>
