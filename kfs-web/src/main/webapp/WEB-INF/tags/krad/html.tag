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
<%@ include file="/krad/WEB-INF/jsp/tldHeader.jsp"%>

<%@ attribute name="view" required="true"
              description="The view instance the html page is being rendered for."
              type="org.kuali.kfs.krad.uif.view.View"%>

<!DOCTYPE HTML>
<html lang="en">

  <!----------------------------------- #BEGIN HEAD --------------------------------------->
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8" />
    <c:if test="${not empty SESSION_TIMEOUT_WARNING_MILLISECONDS}">
      <script type="text/javascript">
        <!--
        setTimeout("alert('Your session will expire in ${SESSION_TIMEOUT_WARNING_MINUTES} minutes.')",'${SESSION_TIMEOUT_WARNING_MILLISECONDS}');
      // -->
      </script>
    </c:if>

    <krad:scriptingVariables/>

    <title>
      <s:message code="app.title"/>
      :: ${view.title}
    </title>

    <c:forEach items="${view.theme.stylesheets}" var="cssFile" >
      <c:if test="${fn:startsWith(cssFile, '/')}">
        <c:set var="cssFile" value="${pageContext.request.contextPath}/${fn:substringAfter(cssFile,'/')}"/>
      </c:if>
      <link href="${cssFile}" rel="stylesheet" type="text/css" />
    </c:forEach>

    <c:forEach items="${view.additionalCssFiles}" var="cssFile" >
      <c:if test="${fn:startsWith(cssFile, '/')}">
        <c:set var="cssFile" value="${pageContext.request.contextPath}/${fn:substringAfter(cssFile,'/')}"/>
      </c:if>
      <link href="${cssFile}" rel="stylesheet" type="text/css" />
    </c:forEach>

    <c:forEach items="${view.theme.jsFiles}"	var="javascriptFile">
      <c:if test="${fn:length(fn:trim(javascriptFile)) > 0}">
        <script language="JavaScript" type="text/javascript" src="${pageContext.request.contextPath}/${javascriptFile}"></script>
      </c:if>
    </c:forEach>

    <c:forEach items="${view.additionalScriptFiles}" var="scriptFile" >
      <c:if test="${fn:startsWith(scriptFile, '/')}">
        <c:set var="scriptFile" value="${pageContext.request.contextPath}/${fn:substringAfter(scriptFile,'/')}"/>
      </c:if>
      <script language="JavaScript" type="text/javascript" src="${scriptFile}"></script>
    </c:forEach>

    <!-- preload script (server variables) -->
    <script type="text/javascript">
        ${view.preLoadScript}
    </script>

    <!-- custom script for the view -->
    <script type="text/javascript">
      jq(document).ready(function() {
        ${view.onLoadScript}
      })
    </script>
  </head>

  <!----------------------------------- #BEGIN BODY --------------------------------------->

  <body>
    <%--View is hidden here but shown by the initial ready jq script after page content scripts
     have completed--%>
    <div id="view_div" style="display:none;">
     <krad:div component="${view}">

      <krad:backdoor/>

      <!----------------------------------- #BEGIN FORM --------------------------------------->
      <c:if test="${view.renderForm}">
        <c:set var="postUrl" value="${view.formPostUrl}"/>
        <c:if test="${empty postUrl}">
          <c:set var="postUrl" value="${KualiForm.formPostUrl}"/>
        </c:if>

        <form:form
           id="kualiForm"
           action="${postUrl}"
           method="post"
           enctype="multipart/form-data"
           modelAttribute="KualiForm"
           onsubmit="${view.onSubmitScript}"
           cssStyle="form_format topLabel page">

           <a name="topOfForm"></a>

           <jsp:doBody/>

           <span id="formComplete"></span>
        </form:form>
        <!----------------------------------- End Form --------------------------------------->
      </c:if>

      <c:if test="${!view.renderForm}">
         <jsp:doBody/>
      </c:if>

     </krad:div>
    </div>
  </body>
</html>
