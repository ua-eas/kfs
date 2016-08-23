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
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<c:set var="bodyClass" value="body"/>
<c:choose>
    <c:when test="${param.mode eq 'standalone'}">
        <c:set var="bodyClass" value="fullwidth body"/>
    </c:when>
    <c:when test="${param.mode eq 'modal'}">
        <c:set var="bodyClass" value="fullwidth inquirymodal body"/>
    </c:when>
</c:choose>


<!DOCTYPE html>
<html:html>
    <head>
        <meta http-equiv="content-type" content="text/html; charset=UTF-8" />
        <title><bean:message key="app.title" /> :: Institution Configuration</title>

        <c:if test="${not empty SESSION_TIMEOUT_WARNING_MILLISECONDS}">
            <script type="text/javascript">
                <!--
                setTimeout("alert('Your session will expire in ${SESSION_TIMEOUT_WARNING_MINUTES} minutes.')",'${SESSION_TIMEOUT_WARNING_MILLISECONDS}');
                // -->
            </script>
        </c:if>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
        <link href='https://fonts.googleapis.com/css?family=Lato:300,400,700,900,400italic' rel='stylesheet' type='text/css'>
        <link href='${pageContext.request.contextPath}/css/newPortal.css?${cachingTimestamp}' rel='stylesheet' type='text/css'>
        <link href='${pageContext.request.contextPath}/css/lookup.css?${cachingTimestamp}' rel='stylesheet' type='text/css'>
        <link href='${pageContext.request.contextPath}/css/institution-config.css?${cachingTimestamp}' rel='stylesheet' type='text/css'/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/remodal.min.css">
    </head>
    <body>
        <header id="header" class="navbar navbar-default navbar-fixed-top"></header>
        <div class="${bodyClass}">
            <main class="content">
                <div id="content-overlay"></div>
                <div id="view_div">
                    <div class="main-panel">
                        ${headerMenuBar}
                        <div id="page-content">
                            <jsp:doBody/>
                        </div>
                    </div>
                </div>
            </main>

            <div id="sidebar"></div>
        </div>

        <footer id="footer"></footer>

        <script src="${pageContext.request.contextPath}/scripts/jquery.min.js"></script>
        <script src="${pageContext.request.contextPath}/scripts/bootstrap.min.js"></script>
        <script src="${pageContext.request.contextPath}/scripts/jquery-ui.min.js"></script>

        <script src="${pageContext.request.contextPath}/scripts/polyfill.min.js"></script>
        <script src="${pageContext.request.contextPath}/scripts/notify.min.js"></script>
    </body>
</html:html>
