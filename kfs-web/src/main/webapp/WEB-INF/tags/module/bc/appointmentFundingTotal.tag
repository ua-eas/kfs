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

<%@ attribute name="pcafAware" required="true" type="java.lang.Object"
			  description="The object containing the appointment funding lines"%>

<table class="standard">
	<tr class="header">
		<th style="width: 15%;">&nbsp;</th>
		<th style="width: 25%;">&nbsp;</th>
		<th class="right" style="width: 10%;">Amount</th>
		<th style="width: 5%;">&nbsp;</th>
		<th style="width: 5%;">&nbsp;</th>
		<th class="right" style="width: 10%;">Standard Hours</th>
		<th class="right" style="width: 10%;">FTE</th>
		<th style="width: 10%;">&nbsp;</th>
		<th style="width: 10%;">&nbsp;</th>
	</tr>

	<tr class="highlight">
		<td>&nbsp;</td>
		<th class="right">CSF:</th>
		<td class="right">
         	<fmt:formatNumber value="${KualiForm.csfAmountTotal}" type="number" groupingUsed="true"/>
		</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td class="right">
			<fmt:formatNumber value="${KualiForm.csfStandardHoursTotal}" type="number" groupingUsed="true" minFractionDigits="2" />
		</td>
		<td class="right">
         	<fmt:formatNumber value="${KualiForm.csfFullTimeEmploymentQuantityTotal}" type="number" groupingUsed="true" minFractionDigits="5" />
		</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<th  class="right">Request:</th>
		<td  class="right">
			<fmt:formatNumber value="${KualiForm.appointmentRequestedAmountTotal}" type="number" groupingUsed="true"/>
		</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td  class="right">
         	<fmt:formatNumber value="${KualiForm.appointmentRequestedStandardHoursTotal}" type="number" groupingUsed="true" minFractionDigits="2" />
		</td>
		<td  class="right">
         	<fmt:formatNumber value="${KualiForm.appointmentRequestedFteQuantityTotal}" type="number" groupingUsed="true" minFractionDigits="5" />
		</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	<tr class="highlight">
		<td>&nbsp;</td>
		<th  class="right">Leaves Request CSF:</th>
        <td  class="right">
			<fmt:formatNumber value="${KualiForm.appointmentRequestedCsfAmountTotal}" type="number" groupingUsed="true"/>
		</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td  class="right">
         	<fmt:formatNumber value="${KualiForm.appointmentRequestedCsfStandardHoursTotal}" type="number" groupingUsed="true" minFractionDigits="2" />
		</td>
		<td  class="right">
         	<fmt:formatNumber value="${KualiForm.appointmentRequestedCsfFteQuantityTotal}" type="number" groupingUsed="true" minFractionDigits="5" />
		</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
</table>
