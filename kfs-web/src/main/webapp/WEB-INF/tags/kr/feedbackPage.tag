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
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp" %>

<%@ attribute name="docTitle" required="true" description="The title to display for the page." %>
<%@ attribute name="transactionalDocument" required="true"
              description="The name of the document type this document page is rendering." %>
<%@ attribute name="showDocumentInfo" required="false"
              description="Boolean value of whether to display the Document Type name and document type help on the page." %>
<%@ attribute name="headerTitle" required="false"
              description="The title of this page which will be displayed in the browser's header bar.  If left blank, docTitle will be used instead." %>
<%@ attribute name="htmlFormAction" required="false"
              description="The URL that the HTML form rendered on this page will be posted to." %>
<%@ attribute name="defaultMethodToCall" required="false"
              description="The name of default methodToCall on the action for this page." %>
<%@ attribute name="errorKey" required="false"
              description="If present, this is the key which will be used to match errors that need to be rendered at the top of the page." %>

<!DOCTYPE html>
<html:html>
    <head>
        <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
        <c:if test="${not empty SESSION_TIMEOUT_WARNING_MILLISECONDS}">
            <script type="text/javascript">
                <!--
                setTimeout("alert('Your session will expire in ${SESSION_TIMEOUT_WARNING_MINUTES} minutes.')", '${SESSION_TIMEOUT_WARNING_MILLISECONDS}');
                // -->
            </script>
        </c:if>

        <script type="text/javascript">var jsContextPath = "${pageContext.request.contextPath}";</script>
        <title><bean:message key="app.title"/> :: ${headerTitle}</title>
        <c:forEach items="${fn:split(ConfigProperties.kns.css.files, ',')}"
                   var="cssFile">
            <c:if test="${fn:length(fn:trim(cssFile)) > 0}">
                <link href="${pageContext.request.contextPath}/${cssFile}"
                      rel="stylesheet" type="text/css"/>
            </c:if>
        </c:forEach>
        <c:forEach items="${fn:split(ConfigProperties.kns.javascript.files, ',')}"
                   var="javascriptFile">
            <c:if test="${fn:length(fn:trim(javascriptFile)) > 0}">
                <script language="JavaScript" type="text/javascript"
                        src="${pageContext.request.contextPath}/${javascriptFile}"></script>
            </c:if>
        </c:forEach>

        <script type="text/javascript">
            var jq = jQuery.noConflict();
        </script>
    </head>
    <c:if test="${not empty KualiForm.anchor}">
        <c:if test="${ConfigProperties.test.mode ne 'true'}">
            <c:set var="anchorValue"><esapi:encodeForJavaScript>${KualiForm.anchor}</esapi:encodeForJavaScript></c:set>
            <c:set var="anchorScript"
                   value="jumpToAnchor('${anchorValue}');"/>
        </c:if>
    </c:if>
    <c:if test="${empty anchorScript}">
        <c:set var="anchorScript" value="placeFocus();"/>
    </c:if>
    <body id="feedback-body" onload="if ( !restoreScrollPosition() ) { ${anchorScript} }"
          onKeyPress="return isReturnKeyAllowed('${Constants.DISPATCH_REQUEST_PARAMETER}.' , event);">
        <div id="view_div">
            <kul:backdoor/>
            <c:if test="${not empty htmlFormAction}">
                <html:form styleId="kualiForm" action="/${htmlFormAction}.do"
                           method="post" onsubmit="return hasFormAlreadyBeenSubmitted();">
                    <a name="topOfForm"></a>
                    <div class="headerarea-small" id="headerarea-small">
                        <h1>
                                ${docTitle}&nbsp;
                        </h1>
                        <kul:enterKey methodToCall="${defaultMethodToCall}"/>
                    </div>
                    <table class="page-main" width="100%" cellpadding="0" cellspacing="0">
                        <tr>
                            <td width="1%">
                                <img
                                        src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif"
                                        alt="" width="20" height="20"/>
                            </td>
                            <td>
                                <jsp:doBody/>
                                <p>&nbsp;</p>
                            </td>
                            <!-- KULRICE-8093: Horizontal scrolling for maintenance documents with errors listed  -->
                            <td width="21">
                                <img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" width="20" height="20"/>
                            </td>
                        </tr>
                    </table>
                    <kul:editablePropertiesGuid/>
                </html:form>
            </c:if>
            <div id="formComplete"></div>
        </div>
    </body>
</html:html>
