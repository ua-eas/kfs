<!--
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
-->
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<%@ attribute name="docTitle" required="true" description="The title to display for the page." %>
<%@ attribute name="docTitleClass" required="false" description="The class added to the title of the page." %>
<%@ attribute name="transactionalDocument" required="true" description="The name of the document type this document page is rendering." %>
<%@ attribute name="showDocumentInfo" required="false" description="Boolean value of whether to display the Document Type name and document type help on the page." %>
<%@ attribute name="headerMenuBar" required="false" description="HTML text for menu bar to display at the top of the page." %>
<%@ attribute name="htmlFormAction" required="false" description="The URL that the HTML form rendered on this page will be posted to." %>
<%@ attribute name="renderMultipart" required="false" description="Boolean value of whether the HTML form rendred on this page will be encoded to accept multipart - ie, uploaded attachment - input." %>
<%@ attribute name="showTabButtons" required="false" description="Whether to show the show/hide all tabs buttons." %>
<%@ attribute name="extraTopButtons" required="false" type="java.util.List" description="A List of org.kuali.rice.kns.web.ui.ExtraButton objects to display at the top of the page." %>
<%@ attribute name="headerDispatch" required="false" description="Overrides the header navigation tab buttons to go directly to the action given here." %>
<%@ attribute name="lookup" required="false" description="indicates whether the lookup page specific page should be shown"%>

<%-- for non-lookup pages --%>
<%@ attribute name="headerTabActive" required="false" description="The name of the active header tab, if header navigation is used." %>
<%@ attribute name="feedbackKey" required="false" description="application resources key that contains feedback contact address only used when lookup attribute is false"%>
<%@ attribute name="defaultMethodToCall" required="false" description="The name of default methodToCall on the action for this page." %>
<%@ attribute name="errorKey" required="false" description="If present, this is the key which will be used to match errors that need to be rendered at the top of the page." %>
<%@ attribute name="auditCount" required="false" description="The number of audit errors displayed on this page." %>
<%@ attribute name="documentWebScope" required="false" description="The scope this page - which is hard coded to session, making this attribute somewhat useless." %>
<%@ attribute name="maintenanceDocument" required="false" description="Boolean value of whether this page is rendering a maintenance document." %>
<%@ attribute name="renderRequiredFieldsLabel" required = "false" description="Boolean value of whether to include a helpful note that the asterisk represents a required field - good for accessibility." %>
<%@ attribute name="alternativeHelp" required="false"%>

<c:choose>
    <c:when test="${param.mode eq 'standalone'}">
        <c:set var="wrapperClass" value="fullwidth"/>
    </c:when>
    <c:when test="${param.mode eq 'modal'}">
        <c:set var="wrapperClass" value="fullwidth inquirymodal"/>
        <c:set var="sidebarBGWrapperClass" value="inquirymodal"/>
    </c:when>
</c:choose>


<%-- Is the screen an inquiry? --%>
<c:set var="_isInquiry" value="${requestScope[Constants.PARAM_MAINTENANCE_VIEW_MODE] eq Constants.PARAM_MAINTENANCE_VIEW_MODE_INQUIRY}" />

<c:if test="${param.mode ne 'standalone' and param.mode ne 'modal'}">
    <div id="header" class="navbar navbar-default navbar-fixed-top"></div>
</c:if>

<div id="sidebar-bg-wrapper" class="${sidebarBGWrapperClass}">
    <div id="wrapper" class="${wrapperClass}">
        <div id="sidebar-wrapper"></div>
        <div id="main-wrapper">
            <div id="main">
                <c:if test="${param.mode eq 'modal'}">
                    <div class="modal-header">
                        <div id="breadcrumbs"></div>
                        <button type="button" data-remodal-action="close" class="close remodal-close"><span aria-hidden="true">&times;</span></button>
                    </div>
                </c:if>

                <c:choose>
                    <c:when test="${empty htmlFormAction}">
                        <div id="dashboard"></div>
                    </c:when>
                    <c:otherwise>
                        <c:choose>
                            <c:when test="${lookup}" >

                                <div id="view_div">
                                <kul:backdoor />

                                <c:choose>
                                    <c:when test="${!_isInquiry}">
                                        <div class="main-panel">
                                    </c:when>
                                    <c:otherwise>
                                        <div class="inquiry">
                                    </c:otherwise>
                                </c:choose>

                                <c:if test="${! empty headerMenuBar and !_isInquiry and KualiForm.showMaintenanceLinks}">
                                    <div class="lookupcreatenew">
                                        ${headerMenuBar}
                                    </div>
                                </c:if>
                                <c:choose>
                                    <c:when test="${!empty alternativeHelp}">
                                        <h1>${docTitle}<kul:help documentTypeName="${KualiForm.docTypeName}" alternativeHelp="${alternativeHelp}" altText="document help"/></h1>
                                    </c:when>
                                    <c:otherwise>
                                        <c:if test="${showDocumentInfo}">
                                            <h1>${docTitle}<kul:help documentTypeName="${KualiForm.docTypeName}" altText="document help"/></h1>
                                        </c:if>
                                    </c:otherwise>
                                </c:choose>
                            </c:when>
                            <c:otherwise>
                                <div id="view_div">
                                    <kul:backdoor />
                                    <div class="main-panel">
                                        ${headerMenuBar}
                            </c:otherwise>
                        </c:choose>
                    </c:otherwise>
                </c:choose>

                <c:set var="encoding" value=""/>
                <c:if test="${not empty renderMultipart and renderMultipart eq true}">
                    <c:set var="encoding" value="multipart/form-data"/>
                </c:if>

                <c:if test="${not empty htmlFormAction}">
                    <c:choose>
                        <c:when test="${param.mode eq 'modal'}">
                            <c:set var="formId" value="kualiForm"/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="formId" value="kualiFormModal"/>
                        </c:otherwise>
                    </c:choose>
                    <html:form styleId="${formId}" action="/${htmlFormAction}.do"
                               method="post" enctype="${encoding}"
                               onsubmit="return hasFormAlreadyBeenSubmitted();">
                        <c:if test="${not lookup}" >
                            <a name="topOfForm"></a>
                            <div class="headerarea" id="headerarea">
                            <h1 class="${docTitleClass}">
                                ${docTitle}&nbsp;
                                <c:choose>
                                    <c:when test="${!empty alternativeHelp}">
                                        <kul:help documentTypeName="${KualiForm.docTypeName}" alternativeHelp="${alternativeHelp}" altText="document help" />
                                    </c:when>
                                    <c:otherwise>
                                        <c:if test="${showDocumentInfo}">
                                            <kul:help documentTypeName="${KualiForm.docTypeName}" altText="document help"/>
                                        </c:if>
                                    </c:otherwise>
                                </c:choose>
                            </h1>
                            <c:if test="${!empty defaultMethodToCall}">
                                <kul:enterKey methodToCall="${defaultMethodToCall}" />
                            </c:if>
                        </c:if>

                        <!-- DOCUMENT INFO HEADER BOX -->
                        <c:set var="docHeaderAttributes" value="${DataDictionary.DocumentHeader.attributes}" />
                        <c:if test="${showDocumentInfo}">
                            <c:set var="KualiForm" value="${KualiForm}" />
                            <jsp:useBean id="KualiForm" type="org.kuali.rice.kns.web.struts.form.KualiForm" />

                            <c:set var="numberOfHeaderRows" value="<%=new Integer((int) java.lang.Math.ceil((double) KualiForm.getDocInfo().size()/KualiForm.getNumColumns()))%>" />
                            <c:set var="headerFieldCount" value="<%=new Integer(KualiForm.getDocInfo().size())%>" />
                            <c:set var="headerFields" value="${KualiForm.docInfo}" />
                            <c:set var="fieldCounter" value="0" />

                            <div class="headerbox">
                                <c:choose>
                                    <c:when test="${lookup}" >
                                        <table summary="document header: general information" cellpadding="0" cellspacing="0">
                                    </c:when>
                                    <c:otherwise>
                                        <table class="headerinfo" summary="document header: general information" cellpadding="0" cellspacing="0">
                                    </c:otherwise>
                                </c:choose>

                                    <c:forEach var="i" begin="1" end="${numberOfHeaderRows}" varStatus="status">
                                        <tr>
                                            <c:forEach var="j" begin="1" end="<%=KualiForm.getNumColumns()%>" varStatus="innerStatus">
                                                <c:choose>
                                                    <c:when test="${headerFieldCount > fieldCounter}">
                                                        <c:set var="headerField" value="${headerFields[fieldCounter]}" />
                                                        <c:choose>
                                                            <c:when test="${(empty headerField) or (empty headerField.ddAttributeEntryName)}">
                                                                <kul:htmlAttributeHeaderCell />
                                                                <td>&nbsp;</td>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <kul:htmlAttributeHeaderCell attributeEntryName="${headerField.ddAttributeEntryName}" horizontal="true" scope="row" />
                                                                <td>
                                                                    <c:if test="${empty headerField.nonLookupValue and empty headerField.displayValue}">
                                                                        &nbsp;
                                                                    </c:if>
                                                                    <c:choose>
                                                                        <c:when test="${headerField.lookupAware and (not lookup)}" >
                                                                            ${headerField.nonLookupValue}
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            ${headerField.displayValue}
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </td>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <kul:htmlAttributeHeaderCell />
                                                        <td>&nbsp;</td>
                                                    </c:otherwise>
                                                </c:choose>
                                                <c:if test="${headerFieldCount > fieldCounter}">
                                                </c:if>
                                                <c:set var="fieldCounter" value="${fieldCounter+1}" />
                                            </c:forEach>
                                        </tr>
                                    </c:forEach>
                                </table>
                            </div>
                        </c:if>

                        <c:choose>
                            <c:when test="${lookup}" >
                                <%-- Display the expand/collapse buttons for the lookup/inquiry, if specified. --%>
                                <c:if test="${showTabButtons != '' && showTabButtons == true}">
                                    <div class="right">
                                        <div class="excol">
                                            <div class="lookupcreatenew">
                                                <html:button styleId="expandAll" property="methodToCall.showAllTabs" title="show all panel content" alt="show all panel content" styleClass="btn btn-primary" onclick="return expandAllTab('${formId}');" tabindex="-1" value="expand all" />
                                                <html:button styleId="collapseAll" property="methodToCall.hideAllTabs" title="hide all panel content" alt="hide all panel content" styleClass="btn btn-primary" onclick="return collapseAllTab('${formId}');" tabindex="-1" value="collapse all" />
                                            </div>
                                        </div>
                                    </div>
                                </c:if>
                            </c:when>
                            <c:otherwise>
                                </div>
                                <c:if test="${not empty KualiForm.headerNavigationTabs}">
                                    <div class="horz-links-bkgrnd" id="horz-links">
                                        <div id="tabs">
                                            <dl class="tabul">
                                                <c:choose>
                                                    <c:when test="${empty headerDispatch}">
                                                        <c:forEach var="headerTab" items="${KualiForm.headerNavigationTabs}" varStatus="status">
                                                            <c:set var="currentTab" value="${headerTabActive eq headerTab.headerTabNavigateTo}" /> <!-- ${headerTab.headerTabNavigateTo}; ${headerTabActive}; ${currentTab} -->
                                                            <c:choose>
                                                                <c:when test="${currentTab}"><dt class="licurrent"></c:when>
                                                                <c:otherwise><dt></c:otherwise>
                                                            </c:choose>
                                                <span class="tabright ${currentTab ? 'tabcurrent' : ''}">
                                                    <html:submit value="${headerTab.headerTabDisplayName}" property="methodToCall.headerTab.headerDispatch.${headerDispatch}.navigateTo.${headerTab.headerTabNavigateTo}"  alt="${headerTab.headerTabDisplayName}" disabled="true" />
                                                </span></dt>
                                                        </c:forEach>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:forEach var="headerTab" items="${KualiForm.headerNavigationTabs}" varStatus="status">
                                                            <c:set var="currentTab" value="${headerTabActive eq headerTab.headerTabNavigateTo}" /> <!-- ${headerTab.headerTabNavigateTo}; ${headerTabActive}; ${currentTab} -->
                                                            <c:choose><c:when test="${currentTab}"><dt class="licurrent"></c:when><c:otherwise><dt></c:otherwise></c:choose>
                                                <span class="tabright ${currentTab ? 'tabcurrent' : ''}">
                                                    <html:submit value="${headerTab.headerTabDisplayName}" property="methodToCall.headerTab.headerDispatch.${headerDispatch}.navigateTo.${headerTab.headerTabNavigateTo}"  alt="${headerTab.headerTabDisplayName}" disabled="${headerTab.disabled}"  />
                                                </span></dt>
                                                        </c:forEach>
                                                    </c:otherwise>
                                                </c:choose>
                                            </dl>
                                        </div>
                                    </div>
                                </c:if>
                                <div class="msg-excol">
                                    <div class="left-errmsg">
                                        <kul:errorCount auditCount="${auditCount}"/>
                                        <c:if test="${!empty errorKey}">
                                            <kul:errors keyMatch="${errorKey}" errorTitle=" "/>
                                        </c:if>
                                        <c:if test="${empty errorKey}">
                                            <kul:errors keyMatch="${Constants.GLOBAL_ERRORS}"
                                                        errorTitle=" " />
                                        </c:if>
                                        <kul:messages/>
                                        <kul:lockMessages/>
                                    </div>
                                    <div class="right">
                                        <div class="excol">
                                            <c:if test="${!empty extraTopButtons}">
                                                <c:forEach items="${extraTopButtons}" var="extraButton">
                                                    <html:button styleClass="btn btn-default" property="${extraButton.extraButtonProperty}" alt="${extraButton.extraButtonAltText}" onclick="${extraButton.extraButtonOnclick}"/> &nbsp;&nbsp;
                                                </c:forEach>
                                            </c:if>
                                            <c:if test="${showTabButtons != '' && showTabButtons == true}">
                                                <html:button styleId="expandAll" property="methodToCall.showAllTabs" title="show all panel content" alt="show all panel content" styleClass="btn btn-primary" onclick="return expandAllTab('${formId}');" tabindex="-1" value="expand all" />
                                                <html:button styleId="collapseAll" property="methodToCall.hideAllTabs" title="hide all panel content" alt="hide all panel content" styleClass="btn btn-primary" onclick="return collapseAllTab('${formId}');" tabindex="-1" value="collapse all" />
                                            </c:if>
                                            <c:if test="${renderRequiredFieldsLabel}" >
                                                <br>* Required Field
                                            </c:if>
                                        </div>
                                    </div>
                                </div>
                                <table class="page-main" width="100%" cellpadding="0" cellspacing="0">
                                    <tr>
                                        <td width="1%">
                                            <img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" width="20" height="20" />
                                        </td>
                                    </tr>
                                </table>
                            </c:otherwise>
                        </c:choose>

                        <jsp:doBody/>

                        <c:choose>
                            <c:when test="${lookup}" >
                                <kul:footer lookup="true"/>
                                <!-- So that JS expandAllTab / collapseAllTab know the tabStates size. Subtract 1 because currentTabIndex = size + 1. -->
                                <html:hidden property="tabStatesSize" value="${KualiForm.currentTabIndex - 1}" />
                            </c:when>
                            <c:otherwise>
                                <div class="left-errmsg">
                                    <kul:errors displayRemaining="true"
                                                errorTitle="Other errors:"
                                                warningTitle="Other warnings:"
                                                infoTitle="Other informational messages:"/>
                                </div>
                                <kul:footer feedbackKey="${feedbackKey}" />

                                <!-- So that JS expandAllTab / collapseAllTab know the tabStates size. Subtract 1 because currentTabIndex = size + 1. -->
                                <html:hidden property="tabStatesSize"
                                             value="${KualiForm.currentTabIndex - 1}" />


                                <!-- state maintenance for returning the user to the action list if they started there -->
                                <logic:present name="KualiForm"
                                               property="returnToActionList">
                                    <html:hidden name="KualiForm"
                                                 property="returnToActionList" />
                                </logic:present>
                            </c:otherwise>
                        </c:choose>
                        <c:if test="${transactionalDocument || maintenanceDocument}">
                            <html:hidden property="documentWebScope" value="session"/>
                            <html:hidden property="formKey" value="${KualiForm.formKey}" />
                            <html:hidden property="docFormKey" value="${KualiForm.formKey}" />
                            <html:hidden property="docNum" value="${KualiForm.document.documentNumber}" />
                        </c:if>
                        <kul:editablePropertiesGuid />

                    </html:form>
                </c:if>

                <div id="formComplete"></div>
            </div>
        </div>
    </div>
</div>
</div>
</div>

<c:if test="${param.mode ne 'standalone' and param.mode ne 'modal'}">
    <div id="footer"></div>

    <script src="${pageContext.request.contextPath}/build/bundle.js"></script>
</c:if>
