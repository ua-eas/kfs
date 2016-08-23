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
<%@ page import="org.kuali.kfs.kns.web.struts.action.KualiAction,org.kuali.kfs.krad.util.KRADConstants" %>
<%@ include file="tldHeader.jsp" %>
<html:html>

    <%
        String textAreaFieldLabel = request.getParameter(KualiAction.TEXT_AREA_FIELD_LABEL);
        if (textAreaFieldLabel == null) {
            textAreaFieldLabel = (String) request.getAttribute(KualiAction.TEXT_AREA_FIELD_LABEL);
        }

        pageContext.setAttribute(KualiAction.TEXT_AREA_FIELD_LABEL, textAreaFieldLabel);
    %>
    <c:if test="${empty textAreaFieldName}">
        <c:set var="textAreaFieldName"
               value="<%=request.getParameter(KualiAction.TEXT_AREA_FIELD_NAME)%>"/>
    </c:if>

    <c:set var="textAreaFieldNameJS"><esapi:encodeForJavaScript>${textAreaFieldName}</esapi:encodeForJavaScript></c:set>
    <c:set var="textAreaFieldNameAttribute"><esapi:encodeForHTMLAttribute>${textAreaFieldName}</esapi:encodeForHTMLAttribute></c:set>
    <head>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
        <link href='https://fonts.googleapis.com/css?family=Lato:300,400,700,900,400italic' rel='stylesheet' type='text/css'>
        <link href='${pageContext.request.contextPath}/css/newPortal.css?${cachingTimestamp}' rel='stylesheet' type='text/css'>
        <link href='${pageContext.request.contextPath}/css/lookup.css?${cachingTimestamp}' rel='stylesheet' type='text/css'>
        <script language="javascript" src="${pageContext.request.contextPath}/kr/scripts/core.js"></script>
    </head>
    <body onload="setTextArea('${textAreaFieldNameJS}')">
        <div id="view_div">
            <div class="main-panel">


                <div class="headerarea-small" id="headerarea-small">
                    <h2><c:out value="${textAreaFieldLabel}"/></h2>
                </div>

                <c:set var="parameters" value="<%=request.getParameterMap()%>"/>

                <c:set var="textAreaAttributes" value="${DataDictionary.AttributeReferenceElements.attributes}"/>
                <c:if test="${empty textAreaFieldName}">
                    <c:set var="textAreaFieldName" value="<%=request.getAttribute(KualiAction.TEXT_AREA_FIELD_NAME)%>"/>
                </c:if>

                <c:if test="${empty htmlFormAction}">
                    <c:set var="htmlFormAction" value="<%=request.getAttribute(KualiAction.FORM_ACTION)%>"/>
                </c:if>
                <c:if test="${empty htmlFormAction}">
                    <c:set var="htmlFormAction" value="<%=request.getParameter(KualiAction.FORM_ACTION)%>"/>
                </c:if>
                <c:if test="${empty documentWebScope}">
                    <c:set var="documentWebScope" value="<%=request.getAttribute(KRADConstants.DOCUMENT_WEB_SCOPE)%>"/>
                </c:if>
                <c:if test="${empty documentWebScope}">
                    <c:set var="documentWebScope" value="<%=request.getParameter(KRADConstants.DOCUMENT_WEB_SCOPE)%>"/>
                </c:if>
                <c:if test="${empty documentWebScope}">
                    <c:set var="documentWebScope" value="request"/>
                </c:if>
                <c:if test="${empty docFormKey}">
                    <c:set var="docFormKey" value="<%=request.getAttribute(KRADConstants.DOC_FORM_KEY)%>"/>
                </c:if>
                <c:if test="${empty docFormKey}">
                    <c:set var="docFormKey" value="<%=request.getParameter(KRADConstants.DOC_FORM_KEY)%>"/>
                </c:if>
                <c:if test="${empty docFormKey}">
                    <c:set var="docFormKey" value="88888888"/>
                </c:if>
                <c:if test="${empty textAreaFieldAnchor}">
                    <c:set var="textAreaFieldAnchor" value="<%=request.getAttribute(KualiAction.TEXT_AREA_FIELD_ANCHOR)%>"/>
                </c:if>
                <c:if test="${empty textAreaFieldAnchor}">
                    <c:set var="textAreaFieldAnchor" value="<%=request.getParameter(KualiAction.TEXT_AREA_FIELD_ANCHOR)%>"/>
                </c:if>
                <c:if test="${empty textAreaReadOnly}">
                    <c:set var="textAreaReadOnly" value="<%=request.getParameter(KualiAction.TEXT_AREA_READ_ONLY)%>"/>
                </c:if>

                <c:if test="${empty textAreaMaxLength}">
                    <c:set var="textAreaMaxLength" value="<%=request.getParameter(KualiAction.TEXT_AREA_MAX_LENGTH)%>"/>
                </c:if>


                <html:form styleId="kualiForm" method="post"
                           action="/${htmlFormAction}.do" enctype=""
                           onsubmit="return hasFormAlreadyBeenSubmitted();">

                    <table style="margin: 10px auto;">
                        <tr>
                            <td>
                                <div>
                                    <c:set var="attributeEntry" value="${textAreaAttributes.extendedTextArea}"/>
                                        <%-- cannot use struts form tags here b/c some id values will not be valid properties --%>
                                    <c:choose>
                                        <c:when test="${textAreaReadOnly == 'true'}">
                                        <textarea id="${textAreaFieldNameAttribute}" name="${textAreaFieldNameAttribute}"
                                                  rows="21"
                                                  cols="60"
                                                  readonly="readonly"
                                                ><%-- if it's a valid property then get the value...this is kind of hacky --%>
                                            <c:catch>
                                                <bean:write name="KualiForm" property="${textAreaFieldName}"/>
                                            </c:catch></textarea>
                                        </c:when>
                                        <c:otherwise>
                                            ${kfunc:registerEditableProperty(KualiForm, field.propertyName)}
                                        <textarea id="${textAreaFieldNameAttribute}" name="${textAreaFieldNameAttribute}"
                                                  rows="21"
                                                  cols="60"
                                                  maxlength="${textAreaMaxLength}"
                                                  onkeyup="textLimit(this, ${textAreaMaxLength});"
                                                ><%-- if it's a valid property then get the value...this is kind of hacky --%>
                                            <c:catch>
                                                <bean:write name="KualiForm" property="${textAreaFieldName}"/>
                                            </c:catch></textarea>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </td>
                        </tr>

                        <tr>
                            <td>
                                <div class="center" style="margin-top: 10px;">
                                    <c:choose>
                                        <c:when test="${textAreaReadOnly == 'true'}">
                                            <html:button
                                                    property="methodToCall.postTextAreaToParent.anchor${textAreaFieldAnchor}"
                                                    onclick="javascript:window.close();"
                                                    styleClass="btn btn-default"
                                                    title="close"
                                                    alt="close"
                                                    value="Close"/>
                                        </c:when>
                                        <c:otherwise>
                                            <html:submit
                                                    property="methodToCall.postTextAreaToParent.anchor${textAreaFieldAnchor}"
                                                    onclick="javascript:postValueToParentWindow('${textAreaFieldNameJS}');return false"
                                                    styleClass="btn btn-default"
                                                    title="return"
                                                    alt="return"
                                                    value="Continue"/>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </td>
                        </tr>
                    </table>

                    <html:hidden property="documentWebScope" value="${documentWebScope}"/>
                    <html:hidden property="formKey" value="${formKey}"/>
                    <html:hidden property="docFormKey" value="${docFormKey}"/>
                    <html:hidden property="refreshCaller" value="TextAreaRefresh"/>
                    <kul:csrf />

                    <c:if test="${not empty parameters}">
                        <c:forEach items="${parameters}" var="mapEntry">
                            <c:if test="${not fn:contains(mapEntry.key,'methodToCall')}">
                                <html:hidden property="${mapEntry.key}" value="${mapEntry.value[0]}"/>
                            </c:if>
                        </c:forEach>
                    </c:if>

                </html:form>
                <div id="formComplete"></div>
            </div>
        </div>
    </body>
</html:html>
