<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
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

<%--NOTE: DO NOT FORMAT THIS FILE, DISPLAY:COLUMN WILL NOT WORK CORRECTLY IF IT CONTAINS LINE BREAKS --%>

<c:set var="numberOfColumns" value="${KualiForm.numColumns}" />

<kul:page lookup="true" showDocumentInfo="false"
	headerMenuBar="${KualiForm.lookupable.createNewUrl}   ${KualiForm.lookupable.htmlMenuBar}"
	headerTitle="Lookup" docTitle="" transactionalDocument="false"
	htmlFormAction="glBalanceInquiryLookup">

    <SCRIPT type="text/javascript">
        var kualiForm = document.forms['KualiForm'];
        var kualiElements = kualiForm.elements;
    </SCRIPT>

	<div class="headerarea-small" id="headerarea-small">
        <h1>
            <c:out value="${KualiForm.lookupable.title}" />
            <kul:help resourceKey="lookupHelpText" altText="lookup help" />
        </h1>
	</div>

	<kul:enterKey methodToCall="search" />

	<html-el:hidden name="KualiForm" property="backLocation" />
	<html-el:hidden name="KualiForm" property="formKey" />
	<html-el:hidden name="KualiForm" property="lookupableImplServiceName" />
	<html-el:hidden name="KualiForm" property="businessObjectClassName" />
	<html-el:hidden name="KualiForm" property="conversionFields" />
	<html-el:hidden name="KualiForm" property="hideReturnLink" />
	<html-el:hidden name="KualiForm" property="suppressActions" />
	<html-el:hidden name="KualiForm" property="extraButtonSource" />
	<html-el:hidden name="KualiForm" property="extraButtonParams" />
	<html-el:hidden name="KualiForm" property="multipleValues" />
	<html-el:hidden name="KualiForm" property="lookupAnchor" />
	<html-el:hidden name="KualiForm" property="readOnlyFields" />
	<html-el:hidden name="KualiForm" property="lookupResultsSequenceNumber" />
	<html-el:hidden name="KualiForm" property="lookedUpCollectionName" />
	<html-el:hidden name="KualiForm" property="viewedPageNumber" />
	<html-el:hidden name="KualiForm" property="resultsActualSize" />
	<html-el:hidden name="KualiForm" property="resultsLimitedSize" />

	<kul:errors errorTitle="Errors found in Search Criteria:" />
	<kul:messages/>

	<table class="fixed" width="100%">
		<tr>
			<td>
                <c:if test="${numberOfColumns > 1}">
                    <c:set var="tableClass" value="multi-column-table"/>
                </c:if>
                <div id="lookup" align="center">
                    <table class="standard fixed ${tableClass}">
                        <c:set var="FormName" value="KualiForm" scope="request" />
                        <c:set var="FieldRows" value="${KualiForm.lookupable.rows}" scope="request" />
                        <c:set var="ActionName" value="Lookup.do" scope="request" />
                        <c:set var="IsLookupDisplay" value="true" scope="request" />
                        <c:set var="cellWidth" value="50%" scope="request" />

                        <kul:rowDisplay rows="${FieldRows}" skipTheOldNewBar="true" numberOfColumns="${numberOfColumns}" />

                        <tr align=center>
                            <td height="30" colspan=2 class="infoline">
                                <html:submit
                                        property="methodToCall.search" value="Search"
                                        styleClass="tinybutton btn btn-default"
                                        alt="Search" title="Search" />
                                <html:submit
                                        property="methodToCall.clearValues" value="Clear"
                                        styleClass="tinybutton btn btn-default"
                                        alt="Clear" title="Clear" />

                                <c:set var="backLocation" value="${KualiForm.backLocation}"/>
                                <c:if test="${empty backLocation}">
                                    <c:set var="backLocation" value="portal.do"/>
                                </c:if>
                                <c:if test="${KualiForm.formKey!=''}">
                                    <a href='<c:out value="${backLocation}?methodToCall=refresh&docFormKey=${KualiForm.formKey}&anchor=${KualiForm.lookupAnchor}" />' title="Cancel" class="btn btn-default">
                                        Cancel
                                    </a>
                                </c:if>
                                <c:if test="${! empty KualiForm.extraButtonSource && extraButtonSource != ''}">
                                    <a href='<c:out value="${backLocation}?methodToCall=refresh&refreshCaller=kualiLookupable&docFormKey=${KualiForm.formKey}&anchor=${KualiForm.lookupAnchor}" /><c:out value="${KualiForm.extraButtonParams}" />' title='<c:out value="${KualiForm.extraAltText}"/>' class="btn btn-default">
                                        <c:out value="${KualiForm.extraAltText}"/>
                                    </a>
                                </c:if>
                               <html:submit
                                       tabindex="${tabindex}"
                                       property="methodToCall.prepareToReturnSelectedResults"
                                       alt="Return selected results"
                                       title="Return selected results"
                                       styleClass="btn btn-default"
                                       value="Return Selected"/>
                            </td>
                        </tr>
                    </table>
                </div>

                <gl:balanceInquiryLookupResults resultsList="${requestScope.reqSearchResults}"/>
			</td>
		</tr>
	</table>
</kul:page>
