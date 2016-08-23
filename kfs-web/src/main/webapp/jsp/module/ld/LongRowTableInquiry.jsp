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
          htmlFormAction="laborLongRowTableInquiry"
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

            <td><c:if test="${param.inquiryFlag != 'true'}">
                <div id="lookup" align="center"><br/>
                    <br/>
                    <table align="center">
                        <c:set var="FormName" value="KualiForm" scope="request"/>
                        <c:set var="FieldRows" value="${KualiForm.lookupable.rows}"
                               scope="request"/>
                        <c:set var="ActionName" value="glBalanceInquiry.do" scope="request"/>
                        <c:set var="IsLookupDisplay" value="true" scope="request"/>

                        <kul:rowDisplay rows="${FieldRows}"/>

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
                                <html:submit
                                        property="methodToCall.cancel" value="Cancel"
                                        styleClass="tinybutton btn btn-default"
                                        alt="Cancel" title="Cancel"/>

                                <c:if test="${not empty KualiForm.lookupable.extraButtonSource}">
                                    <a href='<c:out value="${KualiForm.backLocation}?methodToCall=refresh&refreshCaller=org.kuali.kfs.kns.lookup.KualiLookupableImpl&docFormKey=${KualiForm.formKey}" /><c:out value="${KualiForm.lookupable.extraButtonParams}" />' title='<c:out value="${KualiForm.lookupable.extraButtonAltText}" />'>
                                        <span class="tinybutton btn btn-default"><c:out value="${KualiForm.lookupable.extraButtonAltText}"/></span>
                                    </a>
                                </c:if>
                            </td>
                        </tr>
                    </table>
                </div>

                <br/>
                <br/>
            </c:if>

                <br/>
                <br/>

                <c:if test="${reqSearchResultsActualSize>0}">
                    <c:out value="${reqSearchResultsActualSize}"/> items found.
                </c:if>

                <display:table class="datatable-100" cellspacing="0"
                               cellpadding="0" name="${reqSearchResults}" id="row"
                               export="true" pagesize="100" defaultsort="1" decorator="org.kuali.kfs.module.ld.businessobject.lookup.LongRowTableDecorator"
                               requestURI="laborLongRowTableInquiry.do?methodToCall=viewResults&reqSearchResultsActualSize=${reqSearchResultsActualSize}&searchResultKey=${searchResultKey}">

                    <c:set var="columnLength" value="14"/>
                    <c:forEach items="${row.columns}" var="column" varStatus="status">

                        <c:choose>
                            <c:when test="${column.formatter.implementationClass == 'org.kuali.rice.core.web.format.CurrencyFormatter'}">
                                <display:column class="numbercell" media="${(status.index < columnLength) ? 'all' : 'csv excel xml'}"
                                                decorator="org.kuali.kfs.kns.web.ui.FormatAwareDecorator"
                                                comparator="${column.comparator}" title="${column.columnTitle}" sortable="true">
                                    <c:if test="${column.propertyURL != ''}">
                                        <a href="<c:out value="${column.propertyURL}"/>" title="${column.propertyValue}"
                                           target="blank"><c:out value="${column.propertyValue}"/></a>
                                    </c:if>

                                    <c:if test="${column.propertyURL == ''}"><c:out value="${column.propertyValue}"/></c:if>
                                </display:column>
                            </c:when>

                            <c:otherwise>
                                <c:if test="${column.propertyURL != ''}">
                                    <display:column class="infocell"
                                                    decorator="org.kuali.kfs.kns.web.ui.FormatAwareDecorator"
                                                    media="${(status.index < columnLength) ? 'all' : 'csv excel xml'}"
                                                    comparator="${column.comparator}" title="${column.columnTitle}" sortable="true">

                                        <a href="<c:out value="${column.propertyURL}"/>" title="${column.propertyValue}"
                                           target="blank"><c:out value="${column.propertyValue}"/></a>

                                    </display:column>
                                </c:if>

                                <c:if test="${column.propertyURL == ''}">
                                    <display:column class="infocell"
                                                    decorator="org.kuali.kfs.kns.web.ui.FormatAwareDecorator"
                                                    media="${(status.index < columnLength) ? 'all' : 'csv excel xml'}"
                                                    comparator="${column.comparator}" title="${column.columnTitle}" sortable="true">

                                        <c:if test="${column.columnTitle == 'Project Code'}">
                                            <div style="white-space: nowrap"><c:out
                                                    value="${column.propertyValue}"/></div>
                                        </c:if>

                                        <c:if test="${column.columnTitle != 'Project Code'}">
                                            <c:out value="${column.propertyValue}"/>
                                        </c:if>

                                    </display:column>
                                </c:if>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </display:table>
            </td>
        </tr>
    </table>

</kul:page>
