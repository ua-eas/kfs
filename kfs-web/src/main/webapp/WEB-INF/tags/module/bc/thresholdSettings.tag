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

<c:set var="thresholdAttribute"
	value="${DataDictionary.BudgetConstructionReportThresholdSettings.attributes}" />

<table class="standard">
	<tr>
		<th class="grid" colspan="2" align="left">
			<h3><bean:message key="${BCConstants.Report.THRESHOLD_SELECTION_MESSAGE_KEY}" /></h3>
		</th>
	</tr>

	<tr>
		<th class="right" width="50%">
			<kul:htmlAttributeLabel
				attributeEntry="${thresholdAttribute.useThreshold}"
				useShortLabel="false" />
		</th>
		<td width="50%">
			<kul:htmlControlAttribute
				property="budgetConstructionReportThresholdSettings.useThreshold"
				attributeEntry="${thresholdAttribute.useThreshold}" readOnly="false" />
		</td>
	</tr>

	<tr>
		<th class="right">
			<kul:htmlAttributeLabel
				attributeEntry="${thresholdAttribute.thresholdPercent}"
				useShortLabel="false" />
		</th>
		<td>
			<kul:htmlControlAttribute
				property="budgetConstructionReportThresholdSettings.thresholdPercent"
				attributeEntry="${thresholdAttribute.thresholdPercent}"
				readOnly="false" />
		</td>
	</tr>

	<tr>
		<th class="right">
			<kul:htmlAttributeLabel
				attributeEntry="${thresholdAttribute.useGreaterThanOperator}"
				useShortLabel="false" />
		</th>
		<td >
			<kul:htmlControlAttribute
				property="budgetConstructionReportThresholdSettings.useGreaterThanOperator"
				attributeEntry="${thresholdAttribute.useGreaterThanOperator}"
				readOnly="false" />
		</td>
	</tr>
</table>
