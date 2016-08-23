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
<%@ include file="/jsp/sys/kfsTldHeader.jsp" %>

<kul:tab tabTitle="Non-Employee Travel Expense" defaultOpen="false"
         tabErrorKey="${KFSConstants.DV_NON_EMPL_TRAVEL_TAB_ERRORS}">
    <c:set var="nonEmplTravelAttributes" value="${DataDictionary.DisbursementVoucherNonEmployeeTravel.attributes}"/>
    <c:set var="travelExpenseAttributes" value="${DataDictionary.DisbursementVoucherNonEmployeeExpense.attributes}"/>

    <div class="tab-container">
        <table class="datatable standard" summary="Non-Employee Travel Section">
            <tr>
                <td colspan="2" class="tab-subhead"><h3>Traveler Information</h3></td>
            </tr>
            <tr>
                <th width="35%">
                    <div align="right"><kul:htmlAttributeLabel
                            attributeEntry="${nonEmplTravelAttributes.disbVchrNonEmpTravelerName}"/></div>
                </th>
                <td width="65%"><kul:htmlControlAttribute
                        attributeEntry="${nonEmplTravelAttributes.disbVchrNonEmpTravelerName}"
                        property="document.dvNonEmployeeTravel.disbVchrNonEmpTravelerName"
                        readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
            </tr>
            <tr>
                <th>
                    <div align="right"><kul:htmlAttributeLabel
                            attributeEntry="${nonEmplTravelAttributes.disbVchrServicePerformedDesc}"/></div>
                </th>
                <td valign="top"><kul:htmlControlAttribute
                        attributeEntry="${nonEmplTravelAttributes.disbVchrServicePerformedDesc}"
                        property="document.dvNonEmployeeTravel.disbVchrServicePerformedDesc"
                        readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
            </tr>
            <tr>
                <th>
                    <div align="right"><kul:htmlAttributeLabel
                            attributeEntry="${nonEmplTravelAttributes.dvServicePerformedLocName}"/></div>
                </th>
                <td><kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.dvServicePerformedLocName}"
                                              property="document.dvNonEmployeeTravel.dvServicePerformedLocName"
                                              readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
            </tr>
            <tr>
                <th>
                    <div align="right"><kul:htmlAttributeLabel
                            attributeEntry="${nonEmplTravelAttributes.dvServiceRegularEmprName}"/></div>
                </th>
                <td><kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.dvServiceRegularEmprName}"
                                              property="document.dvNonEmployeeTravel.dvServiceRegularEmprName"
                                              readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
            </tr>
        </table>

        <table cellpadding=0 class="datatable" summary="Destination">
            <tr>
                <td colspan="6" class="tab-subhead"><h3>Destination</h3></td>
            </tr>
            <tr>
                <th>&nbsp;</th>
                <th>
                    <div align=left><kul:htmlAttributeLabel
                            attributeEntry="${nonEmplTravelAttributes.disbVchrTravelFromCityName}"/></div>
                </th>
                <th>
                    <div align=left><kul:htmlAttributeLabel
                            attributeEntry="${nonEmplTravelAttributes.disbVchrTravelFromStateCode}"/>*US only
                    </div>
                </th>
                <th>
                    <div align=left><kul:htmlAttributeLabel
                            attributeEntry="${nonEmplTravelAttributes.dvTravelFromCountryCode}"/></div>
                </th>
                <th>
                    <div align=left><kul:htmlAttributeLabel
                            attributeEntry="${nonEmplTravelAttributes.perDiemEndDateTime}"/></div>
                </th>
            </tr>

            <tr>
                <th scope="row">
                    <div align="right">From:</div>
                </th>
                <td valign=top><kul:htmlControlAttribute
                        attributeEntry="${nonEmplTravelAttributes.disbVchrTravelFromCityName}"
                        property="document.dvNonEmployeeTravel.disbVchrTravelFromCityName"
                        readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
                <td valign=top><kul:htmlControlAttribute
                        attributeEntry="${nonEmplTravelAttributes.disbVchrTravelFromStateCode}"
                        property="document.dvNonEmployeeTravel.disbVchrTravelFromStateCode"
                        readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
                <td valign=top><kul:htmlControlAttribute
                        attributeEntry="${nonEmplTravelAttributes.dvTravelFromCountryCode}"
                        property="document.dvNonEmployeeTravel.dvTravelFromCountryCode"
                        readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
                <td valign=top><kul:htmlControlAttribute
                        attributeEntry="${nonEmplTravelAttributes.perDiemStartDateTime}"
                        property="document.dvNonEmployeeTravel.perDiemStartDateTime"
                        readOnly="${!fullEntryMode&&!travelEntryMode}"/>
                    <c:if test="${fullEntryMode||travelEntryMode}">
                        <img src="${ConfigProperties.kr.externalizable.images.url}cal.png" width="24"
                             id="document.dvNonEmployeeTravel.perDiemStartDateTime_datepicker" style="cursor: pointer;"
                             title="Date selector" alt="Date selector"/>
                        <script type="text/javascript">
                            Calendar.setup(
                                    {
                                        inputField: "document.dvNonEmployeeTravel.perDiemStartDateTime", // ID of the input field
                                        ifFormat: "%m/%d/%Y %I:%M %p", // the date format
                                        button: "document.dvNonEmployeeTravel.perDiemStartDateTime_datepicker", // ID of the button
                                        showsTime: true,
                                        timeFormat: "12"
                                    }
                            );
                        </script>
                    </c:if>
                </td>
                </td>
            </tr>

            <tr>
                <th scope="row">
                    <div align="right">To:</div>
                </th>
                <td valign=top><kul:htmlControlAttribute
                        attributeEntry="${nonEmplTravelAttributes.disbVchrTravelToCityName}"
                        property="document.dvNonEmployeeTravel.disbVchrTravelToCityName"
                        readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
                <td valign=top><kul:htmlControlAttribute
                        attributeEntry="${nonEmplTravelAttributes.disbVchrTravelToStateCode}"
                        property="document.dvNonEmployeeTravel.disbVchrTravelToStateCode"
                        readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
                <td valign=top><kul:htmlControlAttribute
                        attributeEntry="${nonEmplTravelAttributes.disbVchrTravelToCountryCode}"
                        property="document.dvNonEmployeeTravel.disbVchrTravelToCountryCode"
                        readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
                <td valign=top><kul:htmlControlAttribute
                        attributeEntry="${nonEmplTravelAttributes.perDiemStartDateTime}"
                        property="document.dvNonEmployeeTravel.perDiemEndDateTime"
                        readOnly="${!fullEntryMode&&!travelEntryMode}"/>
                    <c:if test="${fullEntryMode||travelEntryMode}">
                        <img src="${ConfigProperties.kr.externalizable.images.url}cal.png" width="24"
                             id="document.dvNonEmployeeTravel.perDiemEndDateTime_datepicker" style="cursor: pointer;"
                             title="Date selector" alt="Date selector"/>
                        <script type="text/javascript">
                            Calendar.setup(
                                    {
                                        inputField: "document.dvNonEmployeeTravel.perDiemEndDateTime", // ID of the input field
                                        ifFormat: "%m/%d/%Y %I:%M %p", // the date format
                                        button: "document.dvNonEmployeeTravel.perDiemEndDateTime_datepicker", // ID of the button
                                        showsTime: true,
                                        timeFormat: "12"
                                    }
                            );
                        </script>
                    </c:if>
                </td>
            </tr>
        </table>

        <table cellpadding=0 class="datatable" summary="Per Diem, Personal Vehicle ">
            <tr>
                <td colspan="2" class="tab-subhead"><h3>Per Diem</h3></td>
                <td colspan="2" class="tab-subhead"><h3>Personal Vehicle</h3></td>
            </tr>
            <tr>
                <td colspan="2" class="tab-subhead">* All fields required if section is used.</td>
                <td colspan="2" class="tab-subhead">* All fields required if section is used.</td>
            </tr>
            <tr>
                <th>
                    <div align="right"><kul:htmlAttributeLabel
                            attributeEntry="${nonEmplTravelAttributes.disbVchrPerdiemCategoryName}"/></div>
                </th>
                <td><kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.disbVchrPerdiemCategoryName}"
                                              property="document.dvNonEmployeeTravel.disbVchrPerdiemCategoryName"
                                              readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
                <th scope="row">
                    <div align="right"><kul:htmlAttributeLabel
                            attributeEntry="${nonEmplTravelAttributes.disbVchrAutoFromCityName}"/></div>
                </th>
                <td><kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.disbVchrAutoFromCityName}"
                                              property="document.dvNonEmployeeTravel.disbVchrAutoFromCityName"
                                              readOnly="${!fullEntryMode&&!travelEntryMode}"/>
                    <kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.disbVchrAutoFromStateCode}"
                                              property="document.dvNonEmployeeTravel.disbVchrAutoFromStateCode"
                                              readOnly="${!fullEntryMode&&!travelEntryMode}"/>
                </td>
            </tr>

            <tr>
                <th>
                    <div align="right"><kul:htmlAttributeLabel
                            attributeEntry="${nonEmplTravelAttributes.disbVchrPerdiemRate}"/></div>
                </th>
                <td><kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.disbVchrPerdiemRate}"
                                              property="document.dvNonEmployeeTravel.disbVchrPerdiemRate"
                                              readOnly="${!fullEntryMode&&!travelEntryMode}"/>
                    &nbsp;&nbsp;<a href="dvPerDiem.do?methodToCall=showTravelPerDiemLinks"
                                   tabindex="${KualiForm.nextArbitrarilyHighIndex}" target="_blank">Per Diem Links</a>
                </td>
                <th scope="row">
                    <div align="right"><kul:htmlAttributeLabel
                            attributeEntry="${nonEmplTravelAttributes.disbVchrAutoToCityName}"/></div>
                </th>
                <td><kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.disbVchrAutoToCityName}"
                                              property="document.dvNonEmployeeTravel.disbVchrAutoToCityName"
                                              readOnly="${!fullEntryMode&&!travelEntryMode}"/>
                    <kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.disbVchrAutoToStateCode}"
                                              property="document.dvNonEmployeeTravel.disbVchrAutoToStateCode"
                                              readOnly="${!fullEntryMode&&!travelEntryMode}"/>
                </td>
            </tr>

            <tr>
                <th>
                    <div align="right"><kul:htmlAttributeLabel
                            attributeEntry="${nonEmplTravelAttributes.disbVchrPerdiemCalculatedAmt}"/></div>
                </th>
                <td>
                    <div class="left"><kul:htmlControlAttribute
                            attributeEntry="${nonEmplTravelAttributes.disbVchrPerdiemCalculatedAmt}"
                            property="document.dvNonEmployeeTravel.disbVchrPerdiemCalculatedAmt" readOnly="true"/></div>
                    <c:if test="${fullEntryMode||travelEntryMode}">
                        <div class="right">
                            <html:submit
                                    styleClass="btn btn-default small"
                                    property="methodToCall.clearTravelPerDiem"
                                    alt="Clear Per Diem"
                                    title="Clear Per Diem"
                                    value="Clear"/>
                        </div>
                        <div class="right">
                            <html:submit
                                    styleClass="btn btn-default small"
                                    property="methodToCall.calculateTravelPerDiem"
                                    alt="Calculate Per Diem"
                                    title="Calculate Per Diem"
                                    value="Calculate"/>
                        </div>
                    </c:if>
                </td>
                <th scope="row">
                    <div align="right"><kul:htmlAttributeLabel
                            attributeEntry="${nonEmplTravelAttributes.disbVchrAutoRoundTripCode}"/></div>
                </th>
                <td><kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.disbVchrAutoRoundTripCode}"
                                              property="document.dvNonEmployeeTravel.disbVchrAutoRoundTripCode"
                                              readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
            </tr>

            <tr>
                <th>
                    <div align="right"><kul:htmlAttributeLabel
                            attributeEntry="${nonEmplTravelAttributes.disbVchrPerdiemActualAmount}"/></div>
                </th>
                <td><kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.disbVchrPerdiemActualAmount}"
                                              property="document.dvNonEmployeeTravel.disbVchrPerdiemActualAmount"
                                              readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
                <th scope="row">
                    <div align="right"><kul:htmlAttributeLabel
                            attributeEntry="${nonEmplTravelAttributes.dvPersonalCarMileageAmount}"/></div>
                </th>
                <td><kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.dvPersonalCarMileageAmount}"
                                              property="document.dvNonEmployeeTravel.dvPersonalCarMileageAmount"
                                              readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
            </tr>

            <tr>
                <th rowspan="2">
                    <div align="right"><kul:htmlAttributeLabel
                            attributeEntry="${nonEmplTravelAttributes.dvPerdiemChangeReasonText}"/></div>
                </th>
                <td rowspan="2"><kul:htmlControlAttribute
                        attributeEntry="${nonEmplTravelAttributes.dvPerdiemChangeReasonText}"
                        property="document.dvNonEmployeeTravel.dvPerdiemChangeReasonText"
                        readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
                <th>
                    <div align="right"><kul:htmlAttributeLabel
                            attributeEntry="${nonEmplTravelAttributes.disbVchrMileageCalculatedAmt}"/></div>
                </th>
                <td>
                    <div class="left"><kul:htmlControlAttribute
                            attributeEntry="${nonEmplTravelAttributes.disbVchrMileageCalculatedAmt}"
                            property="document.dvNonEmployeeTravel.disbVchrMileageCalculatedAmt" readOnly="true"/></div>
                    <c:if test="${fullEntryMode||travelEntryMode}">
                        <div class="right">
                            <html:submit
                                    styleClass="btn btn-default small"
                                    property="methodToCall.clearTravelMileageAmount"
                                    alt="Clear Total Mileage"
                                    title="Clear Total Mileage"
                                    value="Clear"/>
                        </div>
                        <div class="right">
                            <html:submit
                                    styleClass="btn btn-default small"
                                    property="methodToCall.calculateTravelMileageAmount"
                                    alt="Calculate Total Mileage"
                                    title="Calculate Total Mileage"
                                    value="Calculate"/>
                        </div>
                    </c:if>
                </td>
            </tr>

            <tr>
                <th>
                    <div align="right"><kul:htmlAttributeLabel
                            attributeEntry="${nonEmplTravelAttributes.disbVchrPersonalCarAmount}"/></div>
                </th>
                <td><kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.disbVchrPersonalCarAmount}"
                                              property="document.dvNonEmployeeTravel.disbVchrPersonalCarAmount"
                                              readOnly="${!fullEntryMode&&!travelEntryMode}"/>
            </tr>
        </table>

        <table cellpadding="0" class="datatable standard items" summary="Travel Reimbursements">
            <tbody>
            <tr>
                <td colspan="5" class="tab-subhead"><h3>Traveler Expenses</h3></td>
            </tr>
            <tr>
                <td colspan="5" class="tab-subhead">* All fields required if section is used</td>
            </tr>
            <tr class="header">
                <th width="10">&nbsp;</th>
                <th>
                    <div align=left><kul:htmlAttributeLabel
                            attributeEntry="${travelExpenseAttributes.disbVchrExpenseCode}"/></div>
                </th>
                <th>
                    <div align=left><kul:htmlAttributeLabel
                            attributeEntry="${travelExpenseAttributes.disbVchrExpenseCompanyName}"/></div>
                </th>
                <th>
                    <div align=left><kul:htmlAttributeLabel
                            attributeEntry="${travelExpenseAttributes.disbVchrExpenseAmount}"/></div>
                </th>
                <c:if test="${fullEntryMode||travelEntryMode}">
                    <th>Actions</th>
                </c:if>
            </tr>

            <c:if test="${fullEntryMode||travelEntryMode}">
                <tr>
                    <th scope="row">
                        <div align="right">&nbsp;</div>
                    </th>
                    <td valign=top class="infoline"><kul:htmlControlAttribute
                            attributeEntry="${travelExpenseAttributes.disbVchrExpenseCode}"
                            property="newNonEmployeeExpenseLine.disbVchrExpenseCode"
                            readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
                    <td valign=top class="infoline"><kul:htmlControlAttribute
                            attributeEntry="${travelExpenseAttributes.disbVchrExpenseCompanyName}"
                            property="newNonEmployeeExpenseLine.disbVchrExpenseCompanyName"
                            readOnly="${!fullEntryMode&&!travelEntryMode}"/>
                        <kul:lookup boClassName="org.kuali.kfs.fp.businessobject.TravelCompanyCode"
                                    fieldConversions="name:newNonEmployeeExpenseLine.disbVchrExpenseCompanyName,code:newNonEmployeeExpenseLine.disbVchrExpenseCode"
                                    fieldLabel="${travelExpenseAttributes.disbVchrExpenseCompanyName.label}"
                                    lookupParameters="'N':travelExpenseTypeCode.prepaidExpense"
                                    readOnlyFields="travelExpenseTypeCode.prepaidExpense"/>
                    </td>
                    <td valign=top nowrap class="infoline">
                        <kul:htmlControlAttribute attributeEntry="${travelExpenseAttributes.disbVchrExpenseAmount}"
                                                  property="newNonEmployeeExpenseLine.disbVchrExpenseAmount"
                                                  readOnly="${!fullEntryMode&&!travelEntryMode}"/>
                    </td>
                    <td class="infoline">
                        <html:submit
                                styleClass="btn btn-green"
                                property="methodToCall.addNonEmployeeExpenseLine"
                                alt="Add Expense Line"
                                title="Add Expense Line"
                                value="Add"/>
                    </td>
                </tr>
            </c:if>

            <logic:iterate indexId="ctr" name="KualiForm" property="document.dvNonEmployeeTravel.dvNonEmployeeExpenses"
                           id="currentLine">
                <tr class="${ctr % 2 == 0 ? 'highlight' : ''}">
                    <th scope="row">
                        <div align="right"><kul:htmlControlAttribute
                                attributeEntry="${travelExpenseAttributes.financialDocumentLineNumber}"
                                property="document.dvNonEmployeeTravel.dvNonEmployeeExpenses[${ctr}].financialDocumentLineNumber"
                                readOnly="true"/></div>
                    </th>
                    <td valign=top><kul:htmlControlAttribute
                            attributeEntry="${travelExpenseAttributes.disbVchrExpenseCode}"
                            property="document.dvNonEmployeeTravel.dvNonEmployeeExpenses[${ctr}].disbVchrExpenseCode"
                            readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
                    <td valign=top><kul:htmlControlAttribute
                            attributeEntry="${travelExpenseAttributes.disbVchrExpenseCompanyName}"
                            property="document.dvNonEmployeeTravel.dvNonEmployeeExpenses[${ctr}].disbVchrExpenseCompanyName"
                            readOnly="${!fullEntryMode&&!travelEntryMode}"/>
                        <c:if test="${fullEntryMode||travelEntryMode}">
                            <kul:lookup boClassName="org.kuali.kfs.fp.businessobject.TravelCompanyCode"
                                        fieldConversions="name:document.dvNonEmployeeTravel.dvNonEmployeeExpenses[${ctr}].disbVchrExpenseCompanyName,code:document.dvNonEmployeeTravel.dvNonEmployeeExpenses[${ctr}].disbVchrExpenseCode"
                                        fieldLabel="${travelExpenseAttributes.disbVchrExpenseCompanyName.label}"
                                        lookupParameters="'N':travelExpenseTypeCode.prepaidExpense"
                                        readOnlyFields="travelExpenseTypeCode.prepaidExpense"/>
                        </c:if>
                    </td>
                    <td valign=top nowrap>
                        <kul:htmlControlAttribute attributeEntry="${travelExpenseAttributes.disbVchrExpenseAmount}"
                                                  property="document.dvNonEmployeeTravel.dvNonEmployeeExpenses[${ctr}].disbVchrExpenseAmount"
                                                  readOnly="${!fullEntryMode&&!travelEntryMode}"/>
                    </td>
                    <c:if test="${fullEntryMode||travelEntryMode}">
                        <td>
                            <html:submit
                                    styleClass="btn btn-red"
                                    property="methodToCall.deleteNonEmployeeExpenseLine.line${ctr}"
                                    alt="Delete Expense Line"
                                    title="Delete Expense Line"
                                    value="Delete"/>
                        </td>
                    </c:if>
                </tr>
            </logic:iterate>


            <tr>
                <th colspan="3" class="infoline" scope="row">
                    <div align="right"><kul:htmlAttributeLabel
                            attributeEntry="${nonEmplTravelAttributes.totalExpenseAmount}"/></div>
                </th>
                <td valign="top" nowrap="nowrap" class="infoline">
                    <div align="center"><strong>$ ${KualiForm.document.dvNonEmployeeTravel.totalExpenseAmount}</strong>
                    </div>
                </td>
                <c:if test="${fullEntryMode||travelEntryMode}">
                    <td class="infoline">&nbsp;</td>
                </c:if>
            </tr>
            <tr>
                <td colspan="5" class="tab-subhead"><h3>Travel Expenses Total</h3></td>
            </tr>
            <tr>
                <th colspan="3" class="infoline" scope="row">
                    <div align="right"><kul:htmlAttributeLabel
                            attributeEntry="${nonEmplTravelAttributes.totalTravelAmount}"/></div>
                </th>
                <td valign="top" nowrap="nowrap" class="infoline">
                    <div align="center">
                        <kul:checkErrors keyMatch="${KFSConstants.DV_CHECK_TRAVEL_TOTAL_ERROR}"/>
                        $<kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.totalTravelAmount}"
                                                   property="document.dvNonEmployeeTravel.totalTravelAmount"
                                                   readOnly="true"/>
                    </div>
                </td>
                <c:if test="${fullEntryMode||travelEntryMode}">
                    <td class="infoline">&nbsp;</td>
                </c:if>
            </tr>
            <tr>
                <td colspan="5" class="tab-subhead"><h3>Pre Paid Expenses</h3></td>
            </tr>
            <tr>
                <td colspan="5" class="tab-subhead">* All fields required if section is used</td>
            </tr>
            <tr class="header">
                <th>&nbsp;</th>
                <th>
                    <div align=left><kul:htmlAttributeLabel
                            attributeEntry="${travelExpenseAttributes.disbVchrPrePaidExpenseCode}"/></div>
                </th>
                <th>
                    <div align=left><kul:htmlAttributeLabel
                            attributeEntry="${travelExpenseAttributes.disbVchrPrePaidExpenseCompanyName}"/></div>
                </th>
                <th>
                    <div align=left><kul:htmlAttributeLabel
                            attributeEntry="${travelExpenseAttributes.disbVchrExpenseAmount}"/></div>
                </th>
                <c:if test="${fullEntryMode||travelEntryMode}">
                    <th>Actions</th>
                </c:if>
            </tr>

            <c:if test="${fullEntryMode||travelEntryMode}">
                <tr>
                    <th scope="row">
                        <div align="right">&nbsp;</div>
                    </th>
                    <td valign=top class="infoline"><kul:htmlControlAttribute
                            attributeEntry="${travelExpenseAttributes.disbVchrPrePaidExpenseCode}"
                            property="newPrePaidNonEmployeeExpenseLine.disbVchrPrePaidExpenseCode"
                            readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
                    <td valign=top class="infoline"><kul:htmlControlAttribute
                            attributeEntry="${travelExpenseAttributes.disbVchrPrePaidExpenseCompanyName}"
                            property="newPrePaidNonEmployeeExpenseLine.disbVchrPrePaidExpenseCompanyName"
                            readOnly="${!fullEntryMode&&!travelEntryMode}"/>
                        <kul:lookup boClassName="org.kuali.kfs.fp.businessobject.TravelCompanyCode"
                                    fieldConversions="name:newPrePaidNonEmployeeExpenseLine.disbVchrPrePaidExpenseCompanyName,code:newPrePaidNonEmployeeExpenseLine.disbVchrPrePaidExpenseCode"
                                    fieldLabel="${travelExpenseAttributes.disbVchrPrePaidExpenseCompanyName.label}"
                                    lookupParameters="'Y':travelExpenseTypeCode.prepaidExpense"
                                    readOnlyFields="travelExpenseTypeCode.prepaidExpense"/>
                    </td>
                    <td valign=top nowrap class="infoline">
                        <kul:htmlControlAttribute attributeEntry="${travelExpenseAttributes.disbVchrExpenseAmount}"
                                                  property="newPrePaidNonEmployeeExpenseLine.disbVchrExpenseAmount"
                                                  readOnly="${!fullEntryMode&&!travelEntryMode}"/>
                    </td>
                    <td class="infoline">
                        <html:submit
                                styleClass="btn btn-green"
                                property="methodToCall.addPrePaidNonEmployeeExpenseLine"
                                alt="Add Expense Line"
                                title="Add Expense Line"
                                value="Add"/>
                    </td>
                </tr>
            </c:if>

            <logic:iterate indexId="ctr2" name="KualiForm"
                           property="document.dvNonEmployeeTravel.dvPrePaidEmployeeExpenses" id="currentLine">
                <tr class="${ctr2 % 2 == 0 ? 'highlight' : ''}">
                    <th scope="row">
                        <div align="right"><kul:htmlControlAttribute
                                attributeEntry="${travelExpenseAttributes.financialDocumentLineNumber}"
                                property="document.dvNonEmployeeTravel.dvPrePaidEmployeeExpenses[${ctr2}].financialDocumentLineNumber"
                                readOnly="true"/></div>
                    </th>
                    <td valign=top><kul:htmlControlAttribute
                            attributeEntry="${travelExpenseAttributes.disbVchrPrePaidExpenseCode}"
                            property="document.dvNonEmployeeTravel.dvPrePaidEmployeeExpenses[${ctr2}].disbVchrExpenseCode"
                            readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
                    <td valign=top><kul:htmlControlAttribute
                            attributeEntry="${travelExpenseAttributes.disbVchrPrePaidExpenseCompanyName}"
                            property="document.dvNonEmployeeTravel.dvPrePaidEmployeeExpenses[${ctr2}].disbVchrExpenseCompanyName"
                            readOnly="${!fullEntryMode&&!travelEntryMode}"/>
                        <c:if test="${fullEntryMode||travelEntryMode}">
                            <kul:lookup boClassName="org.kuali.kfs.fp.businessobject.TravelCompanyCode"
                                        fieldConversions="name:document.dvNonEmployeeTravel.dvPrePaidEmployeeExpenses[${ctr2}].disbVchrExpenseCompanyName,code:document.dvNonEmployeeTravel.dvPrePaidEmployeeExpenses[${ctr2}].disbVchrExpenseCode"
                                        lookupParameters="'Y':travelExpenseTypeCode.prepaidExpense"
                                        readOnlyFields="travelExpenseTypeCode.prepaidExpense"/>
                        </c:if>
                    </td>
                    <td valign=top nowrap>
                        <kul:htmlControlAttribute attributeEntry="${travelExpenseAttributes.disbVchrExpenseAmount}"
                                                  property="document.dvNonEmployeeTravel.dvPrePaidEmployeeExpenses[${ctr2}].disbVchrExpenseAmount"
                                                  readOnly="${!fullEntryMode&&!travelEntryMode}"/>
                    </td>
                    <c:if test="${fullEntryMode||travelEntryMode}">
                        <td>
                            <html:submit
                                    styleClass="btn btn-red"
                                    property="methodToCall.deletePrePaidEmployeeExpenseLine.line${ctr2}"
                                    alt="Delete Expense Line"
                                    title="Delete Expense Line"
                                    value="Delete"/>
                        </td>
                    </c:if>
                </tr>
            </logic:iterate>
            <tr>
                <th colspan="3" class="infoline" scope="row">
                    <div align="right"><kul:htmlAttributeLabel
                            attributeEntry="${nonEmplTravelAttributes.totalPrePaidAmount}"/></div>
                </th>
                <td valign="top" nowrap="nowrap" class="infoline">
                    <div align="center"><strong>$ ${KualiForm.document.dvNonEmployeeTravel.totalPrePaidAmount}</strong>
                    </div>
                </td>
                <c:if test="${fullEntryMode||travelEntryMode}">
                    <td class="infoline">&nbsp;</td>
                </c:if>
            </tr>

            </tbody>
        </table>

    </div>
</kul:tab>
