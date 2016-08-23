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

<kul:tab tabTitle="Pre-Paid Travel Expenses" defaultOpen="false" tabErrorKey="${KFSConstants.DV_PREPAID_TAB_ERRORS}">
	<c:set var="prePaidConfAttributes" value="${DataDictionary.DisbursementVoucherPreConferenceDetail.attributes}" />
	<c:set var="prePaidRegistrantAttributes" value="${DataDictionary.DisbursementVoucherPreConferenceRegistrant.attributes}" />

    <div class="tab-container" align=center >
    	<table cellpadding=0 class="datatable standard" summary="Pre-Paid Travel Section">
            <tr>
              <td colspan="2" class="tab-subhead"><h3>Overview</h3></td>
            </tr>

            <tr>
              <th width="35%" ><div align="right"><kul:htmlAttributeLabel attributeEntry="${prePaidConfAttributes.dvConferenceDestinationName}"/></div></th>
              <td width="65%" ><kul:htmlControlAttribute attributeEntry="${prePaidConfAttributes.dvConferenceDestinationName}" property="document.dvPreConferenceDetail.dvConferenceDestinationName" readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
            </tr>

            <tr>
              <th><div align="right"><kul:htmlAttributeLabel attributeEntry="${prePaidConfAttributes.disbVchrExpenseCode}"/></div></th>
              <td><kul:htmlControlAttribute attributeEntry="${prePaidConfAttributes.disbVchrExpenseCode}" property="document.dvPreConferenceDetail.disbVchrExpenseCode" readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
            </tr>

            <tr>
              <th><div align="right"><kul:htmlAttributeLabel attributeEntry="${prePaidConfAttributes.disbVchrConferenceStartDate}"/></div></th>
              <td><kul:htmlControlAttribute attributeEntry="${prePaidConfAttributes.disbVchrConferenceStartDate}" datePicker="true" property="document.dvPreConferenceDetail.disbVchrConferenceStartDate" readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
            </tr>

            <tr>
              <th><div align="right"><kul:htmlAttributeLabel attributeEntry="${prePaidConfAttributes.disbVchrConferenceEndDate}"/></div></th>
              <td><kul:htmlControlAttribute attributeEntry="${prePaidConfAttributes.disbVchrConferenceEndDate}" datePicker="true" property="document.dvPreConferenceDetail.disbVchrConferenceEndDate" readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
            </tr>
        </table>

        <table cellpadding=0 class="datatable standard items" summary="Expenses">
            <tr>
              <td colspan="6" class="tab-subhead"><h3>Expenses</h3></td>
            </tr>

            <tr class="header">
              <th>&nbsp;</th>
              <th><kul:htmlAttributeLabel attributeEntry="${prePaidRegistrantAttributes.dvConferenceRegistrantName}"/></th>
              <th><kul:htmlAttributeLabel attributeEntry="${prePaidRegistrantAttributes.disbVchrPreConfDepartmentCd}"/></th>
              <th><kul:htmlAttributeLabel attributeEntry="${prePaidRegistrantAttributes.dvPreConferenceRequestNumber}"/></th>
              <th><kul:htmlAttributeLabel attributeEntry="${prePaidRegistrantAttributes.disbVchrExpenseAmount}"/></th>
              <c:if test="${fullEntryMode||travelEntryMode}">
	              <th>Actions</th>
	          </c:if>
            </tr>

            <tr>
              <th scope="row">
                  &nbsp;
              </th>
              <td valign=top nowrap class="infoline">
                  <kul:htmlControlAttribute attributeEntry="${prePaidRegistrantAttributes.dvConferenceRegistrantName}" property="newPreConferenceRegistrantLine.dvConferenceRegistrantName" readOnly="${!fullEntryMode&&!travelEntryMode}"/>
              </td>
              <td valign=top nowrap class="infoline">
                  <kul:htmlControlAttribute attributeEntry="${prePaidRegistrantAttributes.disbVchrPreConfDepartmentCd}" property="newPreConferenceRegistrantLine.disbVchrPreConfDepartmentCd" readOnly="${!fullEntryMode&&!travelEntryMode}"/>
              </td>
              <td valign=top nowrap class="infoline">
                  <kul:htmlControlAttribute attributeEntry="${prePaidRegistrantAttributes.dvPreConferenceRequestNumber}" property="newPreConferenceRegistrantLine.dvPreConferenceRequestNumber" readOnly="${!fullEntryMode&&!travelEntryMode}"/>
              </td>
              <td valign=top nowrap class="infoline">
                  <kul:htmlControlAttribute attributeEntry="${prePaidRegistrantAttributes.disbVchrExpenseAmount}" property="newPreConferenceRegistrantLine.disbVchrExpenseAmount" readOnly="${!fullEntryMode&&!travelEntryMode}"/>
              </td>
              <c:if test="${fullEntryMode||travelEntryMode}">
	              <td class="infoline">
	                   <html:submit
                               styleClass="btn btn-green"
                               property="methodToCall.addPreConfRegistrantLine"
                               title="Add Pre-Conference Registrant Line"
                               alt="Add Pre-Conference Registrant Line"
                               value="Add"/>
	              </td>
              </c:if>
            </tr>

            <logic:iterate indexId="ctr" name="KualiForm" property="document.dvPreConferenceDetail.dvPreConferenceRegistrants" id="currentLine">
                <tr class="${ctr % 2 == 0 ? 'highlight' : ''}">
                    <th scope="row">
                        <kul:htmlControlAttribute attributeEntry="${prePaidRegistrantAttributes.financialDocumentLineNumber}" property="document.dvPreConferenceDetail.dvPreConferenceRegistrants[${ctr}].financialDocumentLineNumber" readOnly="true"/>
                    </th>
                    <td valign=top nowrap>
                        <kul:htmlControlAttribute attributeEntry="${prePaidRegistrantAttributes.dvConferenceRegistrantName}" property="document.dvPreConferenceDetail.dvPreConferenceRegistrants[${ctr}].dvConferenceRegistrantName" readOnly="${!fullEntryMode&&!travelEntryMode}"/>
                    </td>
                    <td valign=top nowrap>
                        <kul:htmlControlAttribute attributeEntry="${prePaidRegistrantAttributes.disbVchrPreConfDepartmentCd}" property="document.dvPreConferenceDetail.dvPreConferenceRegistrants[${ctr}].disbVchrPreConfDepartmentCd" readOnly="${!fullEntryMode&&!travelEntryMode}"/>
                    </td>
                    <td valign=top nowrap>
                        <kul:htmlControlAttribute attributeEntry="${prePaidRegistrantAttributes.dvPreConferenceRequestNumber}" property="document.dvPreConferenceDetail.dvPreConferenceRegistrants[${ctr}].dvPreConferenceRequestNumber" readOnly="${!fullEntryMode&&!travelEntryMode}"/>
                    </td>
                    <td valign=top nowrap>
                        <kul:htmlControlAttribute attributeEntry="${prePaidRegistrantAttributes.disbVchrExpenseAmount}" property="document.dvPreConferenceDetail.dvPreConferenceRegistrants[${ctr}].disbVchrExpenseAmount" readOnly="${!fullEntryMode&&!travelEntryMode}"/>
                    </td>
                    <c:if test="${fullEntryMode||travelEntryMode}">
                        <td>
                            <html:submit
                                    styleClass="btn btn-red"
                                    property="methodToCall.deletePreConfRegistrantLine.line${ctr}"
                                    title="Delete Pre-Conference Registrant Line"
                                    alt="Delete Pre-Conference Registrant Line"
                                    value="Delete"/>
                        </td>
                    </c:if>
                </tr>
            </logic:iterate>

            <tr>
                <td colspan="4" class="infoline" scope="row"><div align="right"><strong><kul:htmlAttributeLabel attributeEntry="${prePaidConfAttributes.disbVchrConferenceTotalAmt}"/></strong></div></td>
                <td class="infoline" nowrap="nowrap" valign="top"><div align="center"><strong>$<kul:htmlControlAttribute attributeEntry="${prePaidConfAttributes.disbVchrConferenceTotalAmt}" property="document.dvPreConferenceDetail.disbVchrConferenceTotalAmt" readOnly="true"/></strong></div></td>
            	<c:if test="${fullEntryMode||travelEntryMode}">
               		<td class="infoline">&nbsp;</td>
              	</c:if>
            </tr>
          </table>
        </div>
</kul:tab>
