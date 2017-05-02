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
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<%-- settting FieldSections to KualiForm.sections --%>
<c:set var="FieldSections" value="${KualiForm.sections}" />
<c:if test="${!empty kualiInquirable.title}">
    <div>
        <div class="headerarea-small" id="headerarea-small">
            <h1>${kualiInquirable.title}</h1>
        </div>
    </div>
</c:if>

<html:hidden property="businessObjectClassName"/>

<c:forEach items="${KualiForm.inquiryPrimaryKeys}" var="primaryKey">
    <input type="hidden" name="previousPkValue_${primaryKey.key}" value="<c:out value="${primaryKey.value}"/>"/>
</c:forEach>

<c:forEach items="${KualiForm.inactiveRecordDisplay}" var="entry">
    <input type="hidden" name="${Constants.INACTIVE_RECORD_DISPLAY_PARAM_PREFIX}${entry.key}" value="${entry.value}"/>
</c:forEach>

<%-- Show the information about the business object. --%>
<c:forEach items="${FieldSections}" var="section">
    <%-- call helper tag to look ahead through fields for old to new changes, and highlight tab if so --%>
    <kul:checkTabHighlight rows="${section.rows}" addHighlighting="false" />

    <kul:tab tabTitle="${section.sectionTitle}" defaultOpen="${section.defaultOpen}" tabErrorKey="${section.errorKey}" highlightTab="${tabHighlight}" transparentBackground="${firstTab}" extraButtonSource="${section.extraButtonSource}">
        <div class="tab-container" align="center">
            <table class="standard">
                <kul:inquiryRowDisplay rows="${section.rows}" numberOfColumns="${section.numberOfColumns}" />
            </table>
        </div>
    </kul:tab>
</c:forEach>

<c:if test="${param.mode ne 'modal'}">
    <kul:inquiryControls />
</c:if>
