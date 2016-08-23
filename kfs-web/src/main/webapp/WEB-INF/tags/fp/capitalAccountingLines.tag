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
<%@ tag description="render accounting lines for capitalization tag that contains the given accounting lines for capitalization info"%>
<%@ attribute name="readOnly" required="false" description="Whether the accounting lines for capitalization information should be read only" %>
<c:set var="capitalAccountingLinesAttributes" value="${DataDictionary.CapitalAccountingLines.attributes}"/>
<c:set var="capitalAccountingLines" value="${KualiForm.document.capitalAccountingLines}" />
<c:set var="capitalAccountingLineAttributes" value="${DataDictionary.CapitalAccountingLine.attributes}"/>

<c:set var="defaultOpen" value="false"/>
<c:if test="${KualiForm.document.capitalAccountingLinesExist and KualiForm.editCreateOrModify}" >
	<c:set var="defaultOpen" value="true"/>
</c:if>

<kul:tab tabTitle="${KFSConstants.CapitalAssets.ACCOUNTING_LINES_FOR_CAPITALIZATION_TAB_TITLE}" defaultOpen="${defaultOpen}" tabErrorKey="${KFSConstants.EDIT_ACCOUNTING_LINES_FOR_CAPITALIZATION_ERRORS}" >
     <div class="tab-container" align="center">
	    <c:if test="${!KualiForm.document.capitalAccountingLinesExist}">
		    <table cellpadding="0" cellspacing="0" summary="Accounting Lines for Capitalization">
		    	<tr>
					<td class="datacell" height="50" colspan="11"><div align="center" style="padding:30px;">There are currently no Accounting lines for capitalization entries associated with this Transaction Processing document.</div></td>
				</tr>
		    </table>
	    </c:if>
  		<c:if test="${KualiForm.document.capitalAccountingLinesExist}">
         	<table cellpadding="0" cellspacing="0" class="standard" summary="Accounting Lines for Capitalization">
				<c:choose>
				   <c:when test="${empty KualiForm.document.capitalAccountingLines}">
				       <c:if test="${!readOnly}">
					    	<tr>
							   <td class="datacell" height="50" colspan="11">
							       <div align="center" style="padding:30px;">There are Accounting lines for capitalization entries associated with this Transaction Processing document. Please click the generate button when you are ready to enter capitalization information. Note that once you click the button you cannot edit or enter new capitalization accounting lines unless you delete the capitalization information.</div>
								   <div align="center"><html:submit property="methodToCall.generateAccountingLinesForCapitalization" alt="Generate Accounting Lines For Capitalization" title="Generate Accounting Lines For Capitalization" styleClass="btn btn-default" value="Generate"/></div>
						       </td>
							</tr>
    			       </c:if>
				   </c:when>
				   <c:otherwise>
						<tr class="header">
							<kul:htmlAttributeHeaderCell
								attributeEntry="${capitalAccountingLinesAttributes.sequenceNumber}"
								useShortLabel="true"
								/>
							<kul:htmlAttributeHeaderCell
								attributeEntry="${capitalAccountingLinesAttributes.lineType}"
								useShortLabel="true"
								/>
							<kul:htmlAttributeHeaderCell
								attributeEntry="${capitalAccountingLinesAttributes.chartOfAccountsCode}"
								useShortLabel="true"
								hideRequiredAsterisk="true"
								/>
							<kul:htmlAttributeHeaderCell
								attributeEntry="${capitalAccountingLinesAttributes.accountNumber}"
								useShortLabel="true"
								/>
							<kul:htmlAttributeHeaderCell
								attributeEntry="${capitalAccountingLinesAttributes.subAccountNumber}"
								useShortLabel="true"
								/>
							<kul:htmlAttributeHeaderCell
								attributeEntry="${capitalAccountingLinesAttributes.financialObjectCode}"
								useShortLabel="true"
								/>
							<kul:htmlAttributeHeaderCell
								attributeEntry="${capitalAccountingLinesAttributes.financialSubObjectCode}"
								useShortLabel="true"
								/>
							<kul:htmlAttributeHeaderCell
								attributeEntry="${capitalAccountingLinesAttributes.projectCode}"
								useShortLabel="true"
								/>
							<kul:htmlAttributeHeaderCell
								attributeEntry="${capitalAccountingLinesAttributes.organizationReferenceId}"
								useShortLabel="true"
								/>
							<kul:htmlAttributeHeaderCell
								attributeEntry="${capitalAccountingLinesAttributes.amount}"
								useShortLabel="false"
								/>
							<kul:htmlAttributeHeaderCell
								attributeEntry="${capitalAccountingLinesAttributes.accountLinePercent}"
								useShortLabel="true"
								/>
							<kul:htmlAttributeHeaderCell
								attributeEntry="${capitalAccountingLinesAttributes.financialDocumentLineDescription}"
								useShortLabel="true"
								/>
							<kul:htmlAttributeHeaderCell
								attributeEntry="${capitalAccountingLinesAttributes.selectLine}"
								useShortLabel="false"
                                addClass="center"
								/>
						</tr>

						<logic:iterate id="capitalAccountingLinesCollection" name="KualiForm" property="document.capitalAccountingLines" indexId="ctr">
							<bean:define id="amountDistributed" name="capitalAccountingLinesCollection" property="amountDistributed"/>
							<c:set var="lineReadOnly" value="false"/>
			                <c:if test="${amountDistributed == true}" >
			                	<c:set var="lineReadOnly" value="true"/>
			               	</c:if>

					    	<tr class="${status.index % 2 == 0 ? "highlight" : ""}">
					            <td class="datacell">
                                    <kul:htmlControlAttribute attributeEntry="${capitalAccountingLinesAttributes.sequenceNumber}" property="document.capitalAccountingLines[${ctr}].sequenceNumber" readOnly="true"/>
					            </td>
					            <td class="datacell"><kul:htmlControlAttribute attributeEntry="${capitalAccountingLinesAttributes.lineType}" property="document.capitalAccountingLines[${ctr}].lineType" readOnly="true"/></td>
					            <td class="datacell"><kul:htmlControlAttribute attributeEntry="${capitalAccountingLinesAttributes.chartOfAccountsCode}" property="document.capitalAccountingLines[${ctr}].chartOfAccountsCode" readOnly="true"/></td>
					            <td class="datacell"><kul:htmlControlAttribute attributeEntry="${capitalAccountingLinesAttributes.accountNumber}" property="document.capitalAccountingLines[${ctr}].accountNumber" readOnly="true"/></td>
					            <td class="datacell"><kul:htmlControlAttribute attributeEntry="${capitalAccountingLinesAttributes.subAccountNumber}" property="document.capitalAccountingLines[${ctr}].subAccountNumber" readOnly="true"/></td>
					            <td class="datacell"><kul:htmlControlAttribute attributeEntry="${capitalAccountingLinesAttributes.financialObjectCode}" property="document.capitalAccountingLines[${ctr}].financialObjectCode" readOnly="true"/></td>
					            <td class="datacell"><kul:htmlControlAttribute attributeEntry="${capitalAccountingLinesAttributes.financialSubObjectCode}" property="document.capitalAccountingLines[${ctr}].financialSubObjectCode" readOnly="true"/></td>
					            <td class="datacell"><kul:htmlControlAttribute attributeEntry="${capitalAccountingLinesAttributes.projectCode}" property="document.capitalAccountingLines[${ctr}].projectCode" readOnly="true"/></td>
					            <td class="datacell"><kul:htmlControlAttribute attributeEntry="${capitalAccountingLinesAttributes.organizationReferenceId}" property="document.capitalAccountingLines[${ctr}].organizationReferenceId" readOnly="true"/></td>
					            <td class="datacell">
                                    <kul:htmlControlAttribute attributeEntry="${capitalAccountingLinesAttributes.amount}" property="document.capitalAccountingLines[${ctr}].amount" readOnly="true"/>
					            </td>
					            <td class="datacell">
                                    <kul:htmlControlAttribute attributeEntry="${capitalAccountingLinesAttributes.accountLinePercent}" property="document.capitalAccountingLines[${ctr}].accountLinePercent" readOnly="true"/>
					            </td>
					            <td class="datacell"><kul:htmlControlAttribute attributeEntry="${capitalAccountingLinesAttributes.financialDocumentLineDescription}" property="document.capitalAccountingLines[${ctr}].financialDocumentLineDescription" readOnly="true"/></td>
			                    <td class="datacell center">
                                    <kul:htmlControlAttribute attributeEntry="${capitalAccountingLinesAttributes.selectLine}" property="document.capitalAccountingLines[${ctr}].selectLine" readOnly="${readOnly}" disabled="${lineReadOnly}"/>
			                    </td>
					        </tr>
						</logic:iterate>
						<c:if test="${!readOnly}">
							<tr height="40">
								<td colSpan="13">
								<div align="center" style="padding:20px 30px;"><b>Select Amount Distribution Method:&nbsp;</b>
										<kul:htmlControlAttribute
										attributeEntry="${capitalAccountingLineAttributes.distributionCode}"
										property="capitalAccountingLine.distributionCode"
										readOnly="${readOnly}"/>
									</div>
								</td>
							</tr>
						</c:if>
						<c:if test="${!readOnly}">
							<tr height="40">
					            <td class="datacell" colSpan="13">
					            	<div align="center">
					            		<c:if test="${KualiForm.capitalAccountingLine.canCreateAsset}">
					                		<html:submit property="methodToCall.createAsset" alt="Create Asset Details" title="Create Asset Details" styleClass="btn btn-green" value="Create Asset"/>
										</c:if>
										<html:submit property="methodToCall.modifyAsset" alt="Modify Asset Details" title="Modify Asset Details" styleClass="btn btn-default" value="Modify Asset"/>
					                 </div>
					            </td>
							</tr>
					    	<tr>
							   <td class="datacell" height="50" colspan="13">
							       <div align="center" style="padding:20px 0 10px 0;">If you wish to delete all capitalization information in order to edit or enter new capitalization accounting lines, click the following button.</div>
							       <div align="center"><html:submit property="methodToCall.deleteAccountingLinesForCapitalization" alt="Delete Accounting Lines For Capitalization" title="Delete Accounting Lines For Capitalization" styleClass="btn btn-red" value="Delete"/></div>
							   </td>
							</tr>
						</c:if>
				   </c:otherwise>
				</c:choose>
         	</table>
  		</c:if>
	 </div>
</kul:tab>
