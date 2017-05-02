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
<c:set var="benefitInq"
       value="${DataDictionary.BenefitInquiry.attributes}"/>

<kul:page showDocumentInfo="false" showTabButtons="false"
          headerTitle="Fringe Benefit Inquiry" docTitle="Fringe Benefit Inquiry"
          transactionalDocument="false" htmlFormAction="fringeBenefitInquiry">
    <kul:tabTop tabTitle="Fringe Benefit Detail" defaultOpen="true">
        <div id="workarea">
            <table class="standard" summary="Fringe Benefit Detail">
                <tr class="header">
                    <th width="25%"></th>
                    <kul:htmlAttributeHeaderCell literalLabel="Object Code" scope="col"/>
                    <kul:htmlAttributeHeaderCell literalLabel="Amount" scope="col" addClass="right"/>
                    <th width="25%"></th>
                </tr>

                <logic:iterate name="KualiForm" id="benefitInquiry" property="benefitInquiry" indexId="ctr">
                    <tr class="${ctr % 2 == 0 ? 'highlight' : ''}">
                        <td width="25%"></td>
                        <td>
                            <bean:write name="benefitInquiry" property="fringeBenefitObjectCode"/>
                        </td>
                        <td class="right">
                            <bean:write name="benefitInquiry" property="benefitAmount"/>
                        </td>
                        <td width="25%"></td>
                    </tr>
                </logic:iterate>
            </table>
        </div>
        <div id="globalbuttons" class="globalbuttons">
            <html:submit
                    styleClass="btn btn-default"
                    onclick="window.close();return false;"
                    title="close the window"
                    alt="close the window"
                    value="Close"/>
        </div>
    </kul:tabTop>

</kul:page>
