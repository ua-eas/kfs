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
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="editingMode" required="true" type="java.util.Map"%>

<c:set var="allowAdditionalDeposits" value="${editingMode['allowAdditionalDeposits']}" />

<kul:tab tabTitle="Deposits" defaultOpen="true" tabErrorKey="${KFSConstants.CASH_MANAGEMENT_DEPOSIT_ERRORS}">
    <div class="tab-container" align=center>
        <c:if test="${allowAdditionalDeposits}">
            <div align=left style="padding-left: 10px">
                <html:submit value="Add Interim Deposit" styleClass="btn btn-default" property="methodToCall.addInterimDeposit" title="create interim" alt="create interim deposit"/>
                <html:submit value="Add Final Deposit" styleClass="btn btn-default" property="methodToCall.addFinalDeposit" title="create final deposit" alt="create final deposit"/>
            </div>
            <br>
        </c:if>

        <logic:iterate indexId="ctr" name="KualiForm" property="document.deposits" id="currentDeposit">
            <c:if test="${ctr == 0}">
                <hr>
            </c:if>

            <fp:deposit editingMode="${editingMode}" depositIndex="${ctr}" deposit="${currentDeposit}" />

            <hr>
        </logic:iterate>

        <c:if test="${KualiForm.lastInterimDepositFinalizable}">
          <div align="left" style="padding-left: 10px">
            <html:submit value="Finalize Deposits" styleClass="btn btn-default" property="methodToCall.finalizeLastInterimDeposit" title="make last interim deposit final" alt="make last interim deposit final" />
          </div>
        </c:if>
    </div>
</kul:tab>
