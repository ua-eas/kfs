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

<c:set var="objectCodeAttribute"
	value="${DataDictionary.BudgetConstructionObjectPick.attributes}" />

<c:set var="tabTitle">
	<bean:message key="${BCConstants.Report.SELECTED_OBJECT_CODES_MESSAGE_KEY}" />
</c:set>

<kul:tab tabTitle="${tabTitle}" defaultOpen="true">
	<div class="tab-container">
		<table class="standard">
			<tr class="header">
				<th class="grid">
					<kul:htmlAttributeLabel
						attributeEntry="${objectCodeAttribute.financialObjectCode}"
						useShortLabel="false" />
				</th>
				<th class="grid">
					<kul:htmlAttributeLabel
						attributeEntry="${objectCodeAttribute.objectCodeDescription}"
						useShortLabel="false" />
				</th>
			</tr>

			<logic:iterate name="KualiForm" id="objectCodePick"	property="objectCodePickList" indexId="ctr">
				<html-el:hidden name="KualiForm" property="objectCodePickList[${ctr}].principalId" />
				<html-el:hidden name="KualiForm" property="objectCodePickList[${ctr}].versionNumber" />
				<html-el:hidden name="KualiForm" property="objectCodePickList[${ctr}].selectFlag" />

				<c:if test="${objectCodePick.selectFlag == 1}">
					<tr class="${ctr % 2 == 0 ? 'highlight' : ''}">
						<td class="grid" valign="center">
							<kul:htmlControlAttribute
								property="objectCodePickList[${ctr}].financialObjectCode"
								attributeEntry="${objectCodeAttribute.financialObjectCode}"
								readOnly="true" />
						</td>
						<td class="grid" valign="center">
							<kul:htmlControlAttribute
								property="objectCodePickList[${ctr}].objectCodeDescription"
								attributeEntry="${objectCodeAttribute.objectCodeDescription}"
								readOnly="true" />
						</td>
					</tr>
				</c:if>
			</logic:iterate>

		</table>
		<br />
		<table class="standard">
			<tr>
				<th class="grid" colspan="6" align="left">
					<br>
					${KualiForm.operatingModeTitle}
					<br><br>
				</th>
			</tr>

			<c:if test="${empty KualiForm.reasonCodePickList}">
				<tr>
					<th class="grid" colspan="6" align="left">
						<bean:message key="${BCConstants.Report.REASON_CODE_LIST_EMPTY_MESSAGE_KEY}" />
					</th>
				</tr>
				</table>

				<div id="globalbuttons" class="globalbuttons">
					<html:submit
							styleClass="btn btn-default"
							property="methodToCall.start"
							title="back"
							alt="back"
							value="Back"/>
					<html:submit
							styleClass="btn btn-default"
							property="methodToCall.returnToCaller"
							title="close"
							alt="close"
							value="Close"/>
				</div>
			</c:if>

			<c:if test="${!empty KualiForm.reasonCodePickList}">
				<c:set var="reasonCodeAttribute" value="${DataDictionary.BudgetConstructionReasonCodePick.attributes}" />

				<tr>
					<th class="grid">
						Select
					</th>
					<th class="grid">
						<kul:htmlAttributeLabel
							attributeEntry="${reasonCodeAttribute.appointmentFundingReasonCode}"
							useShortLabel="false" />
					</th>
					<th class="grid">
						<kul:htmlAttributeLabel
							attributeEntry="${reasonCodeAttribute['appointmentFundingReason.appointmentFundingReasonDescription']}"
							useShortLabel="false" />
					</th>
				</tr>

				<logic:iterate name="KualiForm" id="reasonCodePick"	property="reasonCodePickList" indexId="ctr">
					<html-el:hidden name="KualiForm" property="reasonCodePickList[${ctr}].principalId" />
					<html-el:hidden name="KualiForm" property="reasonCodePickList[${ctr}].versionNumber" />

					<tr align="center">
						<td class="grid" valign="center">
							<html:checkbox property="reasonCodePickList[${ctr}].selectFlag"
								value="1" />
						</td>
						<td class="grid" valign="center">
							<kul:htmlControlAttribute
								property="reasonCodePickList[${ctr}].appointmentFundingReasonCode"
								attributeEntry="${reasonCodeAttribute.appointmentFundingReasonCode}"
								readOnly="true" />
						</td>
						<td class="grid" valign="center">
							<kul:htmlControlAttribute
								property="reasonCodePickList[${ctr}].appointmentFundingReason.appointmentFundingReasonDescription"
								attributeEntry="${reasonCodeAttribute['appointmentFundingReason.appointmentFundingReasonDescription']}"
								readOnly="true" />
						</td>
					</tr>
				</logic:iterate>

				</table>

				<div id="globalbuttons" class="globalbuttons">
					<html:submit
							styleClass="btn btn-default"
							property="methodToCall.start"
							title="back"
							alt="back"
							value="Back"/>
					<html:submit
							property="methodToCall.selectAllReasonCodes"
							title="Select"
							alt="Select All Codes"
							styleClass="btn btn-default"
							value="Select All"/>
					<html:submit
							property="methodToCall.unselectAllReasonCodes"
							title="Unselect"
							alt="Unselect All Codes"
							styleClass="btn btn-default"
							value="Unselect All"/>
					<html:submit
							styleClass="btn btn-default"
							property="methodToCall.performReport"
							title="Perform Report"
							alt="submit"
							onclick="excludeSubmitRestriction=true"
							value="Submit"/>
					<html:submit
							styleClass="btn btn-default"
							property="methodToCall.returnToCaller"
							title="close"
							alt="close"
							value="Close"/>
				</div>

			</c:if>
	</div>
</kul:tab>

