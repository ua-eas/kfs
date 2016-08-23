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
          htmlFormAction="glBalanceInquiry"
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
                <c:if test="${param.inquiryFlag != 'true'}">
                    <c:if test="${numberOfColumns > 1}">
                        <c:set var="tableClass" value="multi-column-table"/>
                    </c:if>
                    <div id="lookup" align="center">
                        <table class="${tableClass}" align="center">
                            <c:set var="FormName" value="KualiForm" scope="request"/>
                            <c:set var="FieldRows" value="${KualiForm.lookupable.rows}" scope="request"/>
                            <c:set var="ActionName" value="glBalanceInquiry.do" scope="request"/>
                            <c:set var="IsLookupDisplay" value="true" scope="request"/>

                            <kul:rowDisplay rows="${FieldRows}" numberOfColumns="${numberOfColumns}"/>

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
                                        <a href='<c:out value="${backLocation}?methodToCall=refresh&docFormKey=${KualiForm.formKey}" />' title="Cancel">
                                            <span class="tinybutton btn btn-default">Cancel</span>
                                        </a>
                                    </c:if>
                                    <c:if test="${not empty KualiForm.lookupable.extraButtonSource}">
                                        <a href='<c:out value="${backLocation}?methodToCall=refresh&refreshCaller=org.kuali.kfs.kns.lookup.KualiLookupableImpl&docFormKey=${KualiForm.formKey}" /><c:out value="${KualiForm.lookupable.extraButtonParams}" />' title='<c:out value="{KualiForm.lookupable.extraAltText}"/>'>
                                            <span class="tinybutton btn btn-default"><c:out value="${KualiForm.lookupable.extraAltText}"/></span>
                                        </a>
                                    </c:if>
                                </td>
                            </tr>
                        </table>
                    </div>
                </c:if>
                <c:if test="${param.inquiryFlag == 'true'}">
                    <c:set var="url" value="glBalanceInquiry.do" scope="request"/>

                    <c:url value="${url}" var="amountViewSwitch">
                        <c:forEach items="${param}" var="params">
                            <c:if
                                    test="${params.key == 'dummyBusinessObject.amountViewOption'}">
                                <c:if test="${params.value == 'Accumulate' }">
                                    <c:param name="${params.key}" value="Monthly"/>
                                    <c:set var="amountViewLabel" value="View Monthly Amount"/>
                                </c:if>
                                <c:if test="${params.value != 'Accumulate' }">
                                    <c:param name="${params.key}" value="Accumulate"/>
                                    <c:set var="amountViewLabel" value="View Accumulate Amount"/>
                                </c:if>
                            </c:if>

                            <c:if
                                    test="${params.key != 'dummyBusinessObject.amountViewOption'}">
                                <c:param name="${params.key}" value="${params.value}"/>
                            </c:if>
                        </c:forEach>
                    </c:url>

                    <a href="<c:out value='${amountViewSwitch}'/>">
                        <c:out value='${amountViewLabel}'/>
                    </a>
                </c:if>

                <c:if test="${reqSearchResults != null and empty reqSearchResults}">
                    <div class="search-message"><bean-el:message key="error.no.matching.invoice"/></div>
                </c:if>

                <c:if test="${!empty reqSearchResultsSize }">

                    <a id="search-results"></a>
                    <div class="main-panel search-results">
                        <display:table class="datatable-100" name="${reqSearchResults}" id="row"
                                       export="true" pagesize="100" defaultsort="1" decorator="org.kuali.kfs.gl.businessobject.inquiry.BalanceInquiryTableDecorator"
                                       requestURI="glBalanceInquiry.do?methodToCall=viewResults&reqSearchResultsSize=${reqSearchResultsSize}&searchResultKey=${searchResultKey}">

                            <c:set var="columnLength" value="${fn:length(row.columns)-13}"/>
                            <c:forEach items="${row.columns}" var="column" varStatus="status">

                                <c:if test="${!empty column.columnAnchor.title}">
                                    <c:set var="title" value="${column.columnAnchor.title}"/>
                                </c:if>
                                <c:if test="${empty column.columnAnchor.title}">
                                    <c:set var="title" value="${column.propertyValue}"/>
                                </c:if>

                                <c:choose>
                                    <c:when test="${column.formatter.implementationClass == 'org.kuali.rice.core.web.format.CurrencyFormatter'}">

                                        <display:column class="numbercell" sortable="true" media="${(status.index < columnLength) ? 'all' : 'csv excel xml'}"
                                                        decorator="org.kuali.kfs.kns.web.ui.FormatAwareDecorator"
                                                        title="${column.columnTitle}" comparator="${column.comparator}"><c:choose><c:when test="${column.propertyURL != \"\"}"><a href="<c:out value="${column.propertyURL}"/>" title="<c:out value="${column.columnAnchor.title}" />" target="blank"><c:out value="${column.propertyValue}"/></a></c:when><c:otherwise><c:out value="${column.propertyValue}"/></c:otherwise></c:choose></display:column>

                                    </c:when>

                                    <c:otherwise>

                                        <c:choose>

                                            <c:when test="${column.propertyURL != \"\"}">

                                                <display:column class="infocell" sortable="${column.sortable}"
                                                                decorator="org.kuali.kfs.kns.web.ui.FormatAwareDecorator"
                                                                title="${column.columnTitle}" media="${(status.index < columnLength) ? 'all' : 'csv excel xml'}"
                                                                comparator="${column.comparator}"><a href="<c:out value="${column.propertyURL}"/>" title="<c:out value="${column.columnAnchor.title}" />" target="blank"><c:out value="${column.propertyValue}"/></a></display:column>

                                            </c:when>

                                            <c:otherwise>

                                                <display:column class="infocell" sortable="${column.sortable}"
                                                                decorator="org.kuali.kfs.kns.web.ui.FormatAwareDecorator"
                                                                title="${column.columnTitle}" media="${(status.index < columnLength) ? 'all' : 'csv excel xml'}"
                                                                comparator="${column.comparator}"><c:if test="${column.columnTitle == 'Project Code'}">
                                                    <div style="white-space: nowrap"><c:out value="${column.propertyValue}"/></div>
                                                </c:if><c:if test="${column.columnTitle != 'Project Code'}"><c:out value="${column.propertyValue}"/></c:if></display:column>

                                            </c:otherwise>

                                        </c:choose>

                                    </c:otherwise>

                                </c:choose>
                            </c:forEach>
                        </display:table></div>
                </c:if>
            </td>
        </tr>
    </table>
</kul:page>
