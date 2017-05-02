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
<%@ include file="tldHeader.jsp" %>

<kul:page showDocumentInfo="false" headerTitle="Kuali Help" docTitle=""
          transactionalDocument="false" htmlFormAction="help">
    <div class="main-panel" style="padding-bottom: 15px;">
        <table class="standard">
            <c:if test="${! empty KualiForm.helpLabel }">
                <tr>
                    <td width="10%"></td>
                    <td><b><font size="+1">${KualiForm.helpLabel}</font></b></td>
                </tr>
                <tr>
                    <td colspan="2">&nbsp;</td>
                </tr>
            </c:if>
            <tr>
                <td width="10%"></td>
                <td>${KualiForm.helpDescription}</td>
            </tr>
            <c:if test="${! empty KualiForm.helpSummary }">
                <tr>
                    <td colspan="2">&nbsp;</td>
                </tr>
                <tr>
                    <td width="10%"></td>
                    <td>${KualiForm.helpSummary}</td>
                </tr>
            </c:if>
        </table>
        <br/>
        <br/>

        <div style="width: 10%; float: left;">&nbsp;</div>
        <div style="width: 90%;">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <c:if test="${! empty KualiForm.helpRequired }">
                    <tr>
                        <th style="text-align: left; width: 10em;">Required?</th>
                        <td>${KualiForm.helpRequired}</td>
                    </tr>
                </c:if>
                <c:if test="${! empty KualiForm.validationPatternName }">
                    <tr>
                        <th style="text-align: left; width: 10em;">Data Type:</th>
                        <td>${KualiForm.validationPatternName}</td>
                    </tr>
                </c:if>
                <c:if test="${! empty KualiForm.helpMaxLength }">
                    <tr>
                        <th style="text-align: left; width: 10em;">Maximum Length:</th>
                        <td>${KualiForm.helpMaxLength}</td>
                    </tr>
                </c:if>
            </table>
        </div>
        <br>
        <br>

        <div align="center">
            <a href="javascript:window.close();" title="Close This Window">
                <span class="tinybutton btn btn-default">Close</span>
            </a>
        </div>
    </div>
</kul:page>
