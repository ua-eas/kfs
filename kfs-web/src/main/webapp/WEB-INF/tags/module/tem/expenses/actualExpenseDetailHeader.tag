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
<%@ taglib uri="/WEB-INF/tlds/temfunc.tld" prefix="temfunc"%>

<%@ attribute name="detailObject" required="true"
	description="The actual object"
	type="org.kuali.kfs.module.tem.businessobject.ActualExpense"%>

<c:set var="otherExpenseAttributes" value="${DataDictionary.ActualExpense.attributes}" />
<c:set var="temExtension" value="${DataDictionary.ExpenseTypeObjectCode.attributes}" />
<c:set var="isTA" value="${KualiForm.isTravelAuthorizationDoc}" />

<jsp:useBean id="paramMap" class="java.util.HashMap" />
<c:set target="${paramMap}" property="tripType"
	value="${KualiForm.document.tripTypeCode}" />
<c:set target="${paramMap}" property="travelerType"
	value="${KualiForm.document.traveler.travelerTypeCode}" />
<c:set target="${paramMap}" property="documentType"
	value="${KualiForm.docTypeName}" />

<tr class="header">
	<th scope="row">&nbsp;</th>
	<th>
        <kul:htmlAttributeLabel
                attributeEntry="${otherExpenseAttributes.expenseDate}"
                noColon="true" />
	</th>
	<th>
        <kul:htmlAttributeLabel
				attributeEntry="${otherExpenseAttributes.expenseTypeCode}"
				noColon="true" />
	</th>
	<c:if test="${detailObject.mileageIndicator}">
		<th>
            <kul:htmlAttributeLabel
					attributeEntry="${otherExpenseAttributes.miles}"
					noColon="true" />
        </th>
		<th>
            <kul:htmlAttributeLabel
					attributeEntry="${otherExpenseAttributes['mileageRate.rate']}"
					noColon="true" />
		</th>
	</c:if>
	<th>
        <kul:htmlAttributeLabel
				attributeEntry="${otherExpenseAttributes.expenseAmount}"
				noColon="true" />
	</th>
	<th>$US</th>
	<th class="center">
        <kul:htmlAttributeLabel
				attributeEntry="${otherExpenseAttributes.nonReimbursable}"
				noColon="true" />
	</th>
	<th class="center">
        <kul:htmlAttributeLabel
				attributeEntry="${otherExpenseAttributes.taxable}"
				noColon="true" />
	</th>
	<th class="center">
        <kul:htmlAttributeLabel
				attributeEntry="${otherExpenseAttributes.missingReceipt}"
				noColon="true" />
	</th>
	<c:if test="${detailObject.airfareIndicator}">
		<th>
            <kul:htmlAttributeLabel
					attributeEntry="${otherExpenseAttributes.airfareSourceCode}"
					noColon="true" />
        </th>
		<th>
            <kul:htmlAttributeLabel
					attributeEntry="${otherExpenseAttributes.classOfServiceCode}"
					noColon="true" />
		</th>
	</c:if>
	<c:if test="${detailObject.rentalCarIndicator}">
		<th>
            <kul:htmlAttributeLabel
					attributeEntry="${otherExpenseAttributes.classOfServiceCode}"
					noColon="true" />
        </th>
		<th>
            <kul:htmlAttributeLabel
					attributeEntry="${otherExpenseAttributes.rentalCarInsurance}"
					noColon="true" />
		</th>
	</c:if>
	<th>
		<div>Actions</div>
	</th>
</tr>
