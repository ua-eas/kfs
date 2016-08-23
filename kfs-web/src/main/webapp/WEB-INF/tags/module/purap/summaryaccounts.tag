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

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this tag's fields." %>
<%@ attribute name="itemAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>

<c:set var="summaryItemAttributes" value="${DataDictionary.PurApSummaryItem.attributes}"/>

<kul:tab tabTitle="Account Summary" defaultOpen="false" tabErrorKey="${PurapConstants.ACCOUNT_SUMMARY_TAB_ERRORS}">

	<div class="tab-container" align="center" valign="middle">
        <c:if test="${KualiForm.document.inquiryRendered}">
	        <div>
	            Object Code and Sub-Object Code inquiries and descriptions have been removed because this is a prior year document.
            </div>
            <br>
        </c:if>

		<html:submit
				property="methodToCall.refreshAccountSummary"
				alt="refresh account summary"
				styleClass="btn btn-default"
				value="Refresh Account Summary"/>

		<table class="datatable standard side-margins" summary="view/edit pending entries">
			<logic:empty name="KualiForm" property="summaryAccounts">
				<h4>No Accounts</h4>
			</logic:empty>
			<logic:notEmpty name="KualiForm" property="summaryAccounts">
				<logic:iterate id="summaryAccount" name="KualiForm" property="summaryAccounts" indexId="ctr">
				    <tr>
					    <td colspan="9" class="tab-subhead" style="border-right: none;">
					        <h3>Account Summary ${ctr+1}</h3>
					    </td>
				    </tr>

				    <tr class="header">
                		<kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.chartOfAccountsCode}" hideRequiredAsterisk="true" scope="col"/>
                		<kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.accountNumber}" hideRequiredAsterisk="true" scope="col"/>
                		<kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.subAccountNumber}" hideRequiredAsterisk="true" scope="col"/>
                		<kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.financialObjectCode}" hideRequiredAsterisk="true" scope="col"/>
            	    	<kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.financialSubObjectCode}" hideRequiredAsterisk="true" scope="col"/>
            		    <kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.projectCode}" hideRequiredAsterisk="true" scope="col"/>
                		<kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.organizationReferenceId}" hideRequiredAsterisk="true" scope="col"/>
 	    				<kul:htmlAttributeHeaderCell attributeEntry="${DataDictionary.DocumentHeader.attributes.organizationDocumentNumber}" hideRequiredAsterisk="true" scope="col"/>
                		<kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.amount}" hideRequiredAsterisk="true" scope="col" addClass="right"/>
			    	</tr>

					<tr>
						<td class="datacell">
							<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.Chart" keyValues="chartOfAccountsCode=${summaryAccount.account.chartOfAccountsCode}" render="true">
								<bean:write name="KualiForm" property="summaryAccounts[${ctr}].account.chartOfAccountsCode"/>
							</kul:inquiry>
							&nbsp;
						</td>
						<td class="datacell">
							<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.Account" keyValues="chartOfAccountsCode=${summaryAccount.account.chartOfAccountsCode}&accountNumber=${summaryAccount.account.accountNumber}" render="true">
								<bean:write name="KualiForm" property="summaryAccounts[${ctr}].account.accountNumber"/>
							</kul:inquiry>
							&nbsp;
						</td>
						<td class="datacell">
							<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.SubAccount" keyValues="chartOfAccountsCode=${summaryAccount.account.chartOfAccountsCode}&accountNumber=${summaryAccount.account.accountNumber}&subAccountNumber=${summaryAccount.account.subAccountNumber}" render="true">
								<bean:write name="KualiForm" property="summaryAccounts[${ctr}].account.subAccountNumber"/>
							</kul:inquiry>
							&nbsp;
						</td>
						<td class="datacell">
						    <c:choose>
  						        <c:when test="${KualiForm.document.inquiryRendered}">
							        <kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.ObjectCode" keyValues="financialObjectCode=${summaryAccount.account.financialObjectCode}&chartOfAccountsCode=${summaryAccount.account.chartOfAccountsCode}&universityFiscalYear=${KualiForm.document.postingYear}" render="true">
								        <bean:write name="KualiForm" property="summaryAccounts[${ctr}].account.financialObjectCode"/>
							        </kul:inquiry>
							        &nbsp;
						        </c:when>
						        <c:otherwise>
						            <bean:write name="KualiForm" property="summaryAccounts[${ctr}].account.financialObjectCode"/>&nbsp;
						        </c:otherwise>
						    </c:choose>
						</td>
						<td class="datacell">
						    <c:choose>
  						        <c:when test="${KualiForm.document.inquiryRendered}">
							        <kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.SubObjectCode" keyValues="accountNumber=${summaryAccount.account.accountNumber}&financialSubObjectCode=${summaryAccount.account.financialSubObjectCode}&financialObjectCode=${summaryAccount.account.financialObjectCode}&chartOfAccountsCode=${summaryAccount.account.chartOfAccountsCode}&universityFiscalYear=${KualiForm.document.postingYear}" render="true">
								        <bean:write name="KualiForm" property="summaryAccounts[${ctr}].account.financialSubObjectCode"/>
							        </kul:inquiry>
							        &nbsp;
							    </c:when>
							    <c:otherwise>
							        <bean:write name="KualiForm" property="summaryAccounts[${ctr}].account.financialSubObjectCode"/>&nbsp;
							    </c:otherwise>
							</c:choose>
						</td>
						<td class="datacell">
							<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.ProjectCode" keyValues="projectCode=${summaryAccount.account.projectCode}" render="true">
								<bean:write name="KualiForm" property="summaryAccounts[${ctr}].account.projectCode"/>
							</kul:inquiry>
							&nbsp;
						</td>
					    <td class="datacell">
							<bean:write name="KualiForm" property="summaryAccounts[${ctr}].account.organizationReferenceId"/>&nbsp;
						</td>
					    <td class="datacell">
							<bean:write name="KualiForm" property="document.documentHeader.organizationDocumentNumber"/>&nbsp;
						</td>
						<td class="datacell"><div align="right">
							<bean:write name="KualiForm" property="summaryAccounts[${ctr}].account.amount"/>&nbsp;</div>
						</td>
					</tr>

                    <tr>
                        <td colspan="9" height=30 style="padding: 20px;">
                            <div align="center">
								<table width="75%" border="0" cellpadding="0" cellspacing="0" class="datatable">
									<tr>
										<th colspan="3" style="padding: 0px; border-right: none; border-top: 1px solid #999999;">
											<div align="left">Items of Account Summary ${ctr+1} </div>
										</th>
									</tr>
									<tr class="header">
										<kul:htmlAttributeHeaderCell width="25%" >Item</kul:htmlAttributeHeaderCell>
										<kul:htmlAttributeHeaderCell width="50%" attributeEntry="${summaryItemAttributes.itemDescription}" />
										<kul:htmlAttributeHeaderCell width="25%" attributeEntry="${summaryItemAttributes.estimatedEncumberanceAmount}" addClass="right" />
									</tr>
									<logic:iterate id="itemValue" name="KualiForm" property="summaryAccounts[${ctr}].items" indexId="ctrItem">
										<tr class="${ctr % 2 == 0 ? 'highlight' : ''}">
											<td width="25%" class="datacell">
												<kul:htmlControlAttribute attributeEntry="${summaryItemAttributes.itemLineNumber}" property="summaryAccounts[${ctr}].items[${ctrItem}].itemIdentifierString" readOnly="true" />&nbsp;
											</td>
											<td width="50%" class="datacell">
												<kul:htmlControlAttribute attributeEntry="${summaryItemAttributes.itemDescription}" property="summaryAccounts[${ctr}].items[${ctrItem}].itemDescription" readOnly="true" />&nbsp;
											</td>
											<td width="25%" class="datacell right">
												<kul:htmlControlAttribute attributeEntry="${summaryItemAttributes.estimatedEncumberanceAmount}" property="summaryAccounts[${ctr}].items[${ctrItem}].estimatedEncumberanceAmount" readOnly="true" />&nbsp;
											</td>
										</tr>
									</logic:iterate>
								</table>
                            </div>
                        </td>
                    </tr>

				</logic:iterate>
			</logic:notEmpty>
		</table>
	</div>
</kul:tab>
