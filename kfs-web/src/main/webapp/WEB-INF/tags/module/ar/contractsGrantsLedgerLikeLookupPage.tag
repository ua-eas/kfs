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

<%@ attribute name="formActionName" required="true" description="The controller method that the lookup form should post against." %>

<c:set var="numberOfColumns" value="${KualiForm.numColumns}" />

<kul:page lookup="true" showDocumentInfo="false"
          htmlFormAction="${formActionName}"
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
                        <c:set var="FieldRows" value="${KualiForm.lookupable.rows}"
                               scope="request"/>
                        <c:set var="ActionName" value="glModifiedInquiry.do" scope="request"/>
                        <c:set var="IsLookupDisplay" value="true" scope="request"/>

                        <kul:rowDisplay rows="${KualiForm.lookupable.rows}" numberOfColumns="${numberOfColumns}" />

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
                                <html:submit
                                        property="methodToCall.cancel"
                                        value="Cancel"
                                        styleClass="tinybutton btn btn-default"
                                        alt="Cancel" title="Cancel"/>

                                <!-- Optional extra button -->
                               <c:set var="extraButtons" value="${KualiForm.extraButtons}" />
									<div id="globalbuttons" class="globalbuttons">
										<c:if test="${!empty extraButtons}">
											<c:forEach items="${extraButtons}" var="extraButton">
												<html:submit value="${extraButton.extraButtonAltText}"
													styleClass="globalbuttons btn btn-default"
													property="${extraButton.extraButtonProperty}"
													title="${extraButton.extraButtonAltText}"
													alt="${extraButton.extraButtonAltText}" />
											</c:forEach>
										</c:if>
									</div>
								</td>
                        </tr>
                    </table>
                </div>
				<jsp:doBody/>
			</td>
        </tr>
    </table>
    <br/>
    <br/>
</kul:page>
