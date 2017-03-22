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

<kul:page lookup="true"
          showDocumentInfo="false"
          headerTitle="Sidebar Menu Cache" docTitle="Sidebar Menu Cache Configuration" renderMultipart="true"
          transactionalDocument="false" htmlFormAction="sidebarMenuCache" errorKey="foo">

    <div class="headerarea-small" id="headerarea-small">
        <h1>Sidebar Menu Cache</h1>
    </div>

    <div class="msg-excol">
        <div class="left-errmsg">
            <kul:errors errorTitle="Errors found:"/>
        </div>
    </div>
    <table width="100%">
        <tr>
            <td width="1%"><img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" width="20"
                                height="20"></td>
            <td>
                <div id="lookup" align="center">
                    <table align="center" cellpadding="0" class="multi-column-table">
                        <tr align="center">
                            <td height="30" colspan="4" class="infoline">
                                <input type="submit" name="methodToCall.clear" value="Clear Sidebar Menu Cache" class="tinybutton btn btn-default" title="Clear Cache" alt="Clear Cache">
                                <!-- KULRICE-8092: Enter key won't return values in Parameter Component in IE-->
                                <input name="" type="t" value="" style="display:none"/>
                                <!-- Optional extra buttons -->
                            </td>
                        </tr>
                    </table>
                </div>
            </td>
        </tr>
    </table>

</kul:page>
<script type="application/javascript">
    $(function () {
        $(".left-errmsg:last").hide();
    });
</script>
