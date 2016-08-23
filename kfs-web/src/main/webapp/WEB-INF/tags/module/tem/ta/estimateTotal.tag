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
<c:set var="documentAttributes" value="${DataDictionary.TravelAuthorizationDocument.attributes}" />
	<kul:tab tabTitle="Trip Detail Estimate Total" defaultOpen="true" tabErrorKey="${TemKeyConstants.TRVL_AUTH_TRVL_EXPENSES_TOTAL_ERRORS}">
	    <div id="TravelExpenseTotal" class="tab-container" align=center >
			<table border="0">
				<tbody>
					<tr>
						<th width="80%" class="right"><strong>Total Estimated</strong>:</th>
						<td width="20%"><bean:write name="KualiForm" property="document.documentGrandTotal" /></td>
					</tr>
					<tr>
						<th class="right">Less Manual Per Diem Adjustment:</th>
						<td>
                            -
                            <kul:htmlControlAttribute
                                    attributeEntry="${documentAttributes.perDiemAdjustment}"
                                    property="document.perDiemAdjustment"
                                    readOnly="${!fullEntryMode}" />
                        </td>
					</tr>
					<tr>
						<th class="right">Less CTS Charges:</th>
					    <td>-<bean:write name="KualiForm" property="document.fullCTSTotal" /></td>
					</tr>
					<c:if test="${KualiForm.showCorporateCardTotal}">
						<tr>
						    <th class="right">Amount due Corporate Credit Card:</th>
						    <td>-<bean:write name="KualiForm" property="document.corporateCardTotal" /></td>
						</tr>
					</c:if>
					<tr>
			             <th class="right">Less Non-Reimbursable:</th>
			             <td>-<bean:write name="KualiForm" property="document.nonReimbursableTotal" /></td>
		           </tr>
					<tr>
						<th class="right">Travel Expense Limit:</th>
						<c:choose>
							<c:when test="${(KualiForm.document.expenseLimit) == null}">
								<td>N/A</td>
							</c:when>
							<c:otherwise>
								<td><bean:write name="KualiForm" property="document.expenseLimit" /></td>
							</c:otherwise>
						</c:choose>
					</tr>
					<tr>
						<th class="right">Actual Encumbrance:</th>
						<td><bean:write name="KualiForm" property="document.encumbranceTotal" /></td>
					</tr>
					<c:if test="${fullEntryMode}">
						<tr>
							<td colspan="2">
								<div align="center">
									<html:submit
                                            property="methodToCall.recalculate"
                                            styleClass="btn btn-default small"
                                            alt="recalculate total"
                                            title="recalculate total"
                                            value="Recalculate"/>
	                            </div>
							</td>
						</tr>
					</c:if>
				</tbody>
			</table>
		</div>
	</kul:tab>
