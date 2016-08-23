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

<c:set var="numberOfColumns" value="${KualiForm.numColumns}"/>

<kul:page lookup="true" showDocumentInfo="false"
          htmlFormAction="glAccountBalanceByConsolidationLookup"
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

    <table width="100%">
        <tr>
            <td>
                <div id="lookup" align="center">
                    <c:if test="${numberOfColumns > 1}">
                        <c:set var="tableClass" value="multi-column-table"/>
                    </c:if>
                    <table class="${tableClass}" align="center">
                        <c:set var="FormName" value="KualiForm" scope="request"/>
                        <c:set var="FieldRows" value="${KualiForm.lookupable.rows}" scope="request"/>
                        <c:set var="ActionName" value="glModifiedInquiry.do" scope="request"/>
                        <c:set var="IsLookupDisplay" value="true" scope="request"/>

                        <kul:rowDisplay rows="${KualiForm.lookupable.rows}" numberOfColumns="${numberOfColumns}"/>

                        <tr align=center>
                            <td height="30" colspan=2 class="infoline">
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
                                    <a href='<c:out value="${backLocation}?methodToCall=refresh&docFormKey=${KualiForm.formKey}" />'
                                       title="Cancel">
                                        <span class="tinybutton btn btn-default">Cancel</span>
                                    </a>
                                </c:if>
                                <c:if test="${not empty KualiForm.lookupable.extraButtonSource}">
                                    <a href='<c:out value="${backLocation}?methodToCall=refresh&refreshCaller=org.kuali.kfs.kns.lookup.KualiLookupableImpl&docFormKey=${KualiForm.formKey}" /><c:out value="${KualiForm.lookupable.extraButtonParams}" />'
                                       title="Cancel">
                                        <span class="tinybutton btn btn-default">Cancel</span>
                                    </a>
                                </c:if>
                            </td>
                        </tr>
                    </table>
                </div>

                <c:if test="${reqSearchResults != null and empty reqSearchResults}">
                    <div class="search-message"><bean-el:message key="error.no.matching.invoice"/></div>
                </c:if>

                <c:if test="${!empty reqSearchResultsSize }">
                    <c:set var="offset" value="0"/>
                    <a id="search-results"></a>
                    <div class="main-panel search-results">
                        <display:table class="datatable-100" name="${reqSearchResults}" id="row"
                                       export="true" pagesize="100" offset="${offset}"
                                       requestURI="glAccountBalanceByConsolidationLookup.do?methodToCall=viewResults&reqSearchResultsSize=${reqSearchResultsSize}&searchResultKey=${searchResultKey}">
                            <c:forEach items="${row.columns}" var="column" varStatus="status">

                                <c:if test="${!empty column.columnAnchor.title}">
                                    <c:set var="title" value="${column.columnAnchor.title}"/>
                                </c:if>
                                <c:if test="${empty column.columnAnchor.title}">
                                    <c:set var="title" value="${column.propertyValue}"/>
                                </c:if>

                                <display:column
                                        class="${(column.formatter.implementationClass == 'org.kuali.rice.core.web.format.CurrencyFormatter') ? 'numbercell' : 'inofocell'}"
                                        title="${column.columnTitle}" comparator="${column.comparator}"
                                        sortable="${('dummyBusinessObject.linkButtonOption' ne column.propertyName) && column.sortable}">
                                    <c:choose>
                                        <c:when test="${column.propertyURL != \"\" && param['d-16544-e'] == null}">
                                            <a href="<c:out value="${column.propertyURL}"/>" title="<c:out value="${title}" />"
                                               target="blank"><c:out value="${column.propertyValue}"/></a>
                                        </c:when>
                                        <c:otherwise>
                                            <c:out value="${column.propertyValue}"/>
                                        </c:otherwise>
                                    </c:choose>
                                </display:column>
                            </c:forEach>
                        </display:table></div>

                    <c:if test="${not empty totalsTable}">
                        <table class="datatable-80" id="row" align="center">
                            <caption style="text-align: left; font-weight: bold;">Totals</caption>
                            <thead>
                            <tr>
                                <th>Type</th>
                                <th>Budget Amount</th>
                                <th>Actuals Amount</th>
                                <th>Encumbrance Amount</th>
                                <th>Variance</th>
                            </tr>
                            </thead>
                            <tfoot>
                            <tr class="odd">
                                <th colspan="4" class="infocell" style="text-align: right;">Available Balance</th>
                                <td class="numbercell">${totalsTable[6].columns[10].propertyValue}</td>
                            </tr>
                            </tfoot>
                            <tbody>
                            <tr class="odd">
                                <td class="infocell">Income</td>
                                <td class="numbercell">${totalsTable[0].columns[7].propertyValue}</td>
                                <td class="numbercell">${totalsTable[0].columns[8].propertyValue}</td>
                                <td class="numbercell">${totalsTable[0].columns[9].propertyValue}</td>
                                <td class="numbercell">${totalsTable[0].columns[10].propertyValue}</td>
                            </tr>
                            <tr class="odd">
                                <td class="infocell">Income From Transfers</td>
                                <td class="numbercell">${totalsTable[1].columns[7].propertyValue}</td>
                                <td class="numbercell">${totalsTable[1].columns[8].propertyValue}</td>
                                <td class="numbercell">${totalsTable[1].columns[9].propertyValue}</td>
                                <td class="numbercell">${totalsTable[1].columns[10].propertyValue}</td>
                            </tr>
                            <tr class="even">
                                <td class="infocell"><b>Total Income</b></td>
                                <td class="numbercell">${totalsTable[2].columns[7].propertyValue}</td>
                                <td class="numbercell">${totalsTable[2].columns[8].propertyValue}</td>
                                <td class="numbercell">${totalsTable[2].columns[9].propertyValue}</td>
                                <td class="numbercell">${totalsTable[2].columns[10].propertyValue}</td>
                            </tr>
                            <tr class="odd">
                                <td class="infocell" colspan="5">&nbsp;</td>
                            </tr>
                            <tr class="odd">
                                <td class="infocell">Expense</td>
                                <td class="numbercell">${totalsTable[3].columns[7].propertyValue}</td>
                                <td class="numbercell">${totalsTable[3].columns[8].propertyValue}</td>
                                <td class="numbercell">${totalsTable[3].columns[9].propertyValue}</td>
                                <td class="numbercell">${totalsTable[3].columns[10].propertyValue}</td>
                            </tr>
                            <tr class="odd">
                                <td class="infocell">Expense From Transfers</td>
                                <td class="numbercell">${totalsTable[4].columns[7].propertyValue}</td>
                                <td class="numbercell">${totalsTable[4].columns[8].propertyValue}</td>
                                <td class="numbercell">${totalsTable[4].columns[9].propertyValue}</td>
                                <td class="numbercell">${totalsTable[4].columns[10].propertyValue}</td>
                            </tr>
                            <tr class="even">
                                <td class="infocell"><b>Total Expense</b></td>
                                <td class="numbercell">${totalsTable[5].columns[7].propertyValue}</td>
                                <td class="numbercell">${totalsTable[5].columns[8].propertyValue}</td>
                                <td class="numbercell">${totalsTable[5].columns[9].propertyValue}</td>
                                <td class="numbercell">${totalsTable[5].columns[10].propertyValue}</td>
                            </tr>
                            <tr class="odd">
                                <td class="infocell" colspan="5">&nbsp;</td>
                            </tr>
                            </tbody>
                        </table>
                        </div>
                    </c:if>
                </c:if>
            </td>
        </tr>
    </table>
    <br/>
    <br/>
</kul:page>
