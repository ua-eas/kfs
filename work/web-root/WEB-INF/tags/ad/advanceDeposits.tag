<%--
 Copyright 2006-2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<%@ attribute name="editingMode" required="true" description="used to decide if items may be edited" type="java.util.Map"%>
<c:set var="readOnly" value="${not empty editingMode['viewOnly']}" />

<kul:tab tabTitle="Advance Deposits" defaultOpen="true" tabErrorKey="${KFSConstants.ADVANCE_DEPOSITS_LINE_ERRORS}">
<c:set var="adAttributes" value="${DataDictionary.AdvanceDepositDetail.attributes}" />
 <div class="tab-container" align=center>
	<h3>Advance Deposits</h3>
	<table cellpadding=0 class="datatable" summary="Advance Deposits">
		<tr>
            <kul:htmlAttributeHeaderCell literalLabel="&nbsp;"/>
            <kfs:bankLabel align="center"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${adAttributes.financialDocumentAdvanceDepositDate}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${adAttributes.financialDocumentAdvanceDepositReferenceNumber}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${adAttributes.financialDocumentAdvanceDepositDescription}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${adAttributes.financialDocumentAdvanceDepositAmount}"/>
            <c:if test="${not readOnly}">
                <kul:htmlAttributeHeaderCell literalLabel="Actions"/>
            </c:if>
		</tr>
        <c:if test="${not readOnly}">
            <tr>
                <kul:htmlAttributeHeaderCell literalLabel="add:" scope="row"/>
                <kfs:bankControl property="newAdvanceDeposit.financialDocumentBankCode" objectProperty="newAdvanceDeposit.bank" depositOnly="true" readOnly="${readOnly}" style="infoline"/>
                <td class="infoline">    
                    <kul:dateInput attributeEntry="${adAttributes.financialDocumentAdvanceDepositDate}" property="newAdvanceDeposit.financialDocumentAdvanceDepositDate"/>
                </td>
                <td class="infoline">
                	<kul:htmlControlAttribute attributeEntry="${adAttributes.financialDocumentAdvanceDepositReferenceNumber}" property="newAdvanceDeposit.financialDocumentAdvanceDepositReferenceNumber" />
                </td>
                <td class="infoline">
                	<kul:htmlControlAttribute attributeEntry="${adAttributes.financialDocumentAdvanceDepositDescription}" property="newAdvanceDeposit.financialDocumentAdvanceDepositDescription" />
                </td>
                <td class="infoline">
                	<kul:htmlControlAttribute attributeEntry="${adAttributes.financialDocumentAdvanceDepositAmount}" property="newAdvanceDeposit.financialDocumentAdvanceDepositAmount" styleClass="amount"/>
                </td>
                <td class="infoline">
                	<div align="center">
                		<html:image property="methodToCall.addAdvanceDeposit" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="Add an Advance Deposit" title="Add an Advance Deposit" styleClass="tinybutton"/>
                	</div>
                </td>
            </tr>
        </c:if>
        <logic:iterate id="advanceDepositDetail" name="KualiForm" property="document.advanceDeposits" indexId="ctr">
            <tr>
                <kul:htmlAttributeHeaderCell literalLabel="${ctr+1}:" scope="row">
                    <%-- Outside this th, these hidden fields would be invalid HTML. --%>
                    <html:hidden property="document.advanceDepositDetail[${ctr}].documentNumber" />
                    <html:hidden property="document.advanceDepositDetail[${ctr}].financialDocumentTypeCode" />
                    <html:hidden property="document.advanceDepositDetail[${ctr}].financialDocumentColumnTypeCode" />
                    <html:hidden property="document.advanceDepositDetail[${ctr}].financialDocumentLineNumber" />
                    <html:hidden property="document.advanceDepositDetail[${ctr}].versionNumber" />
                    <html:hidden property="document.advanceDepositDetail[${ctr}].objectId" />
                </kul:htmlAttributeHeaderCell>
                <kfs:bankControl property="document.advanceDepositDetail[${ctr}].financialDocumentBankCode" objectProperty="document.advanceDepositDetail[${ctr}].bank" depositOnly="true" readOnly="${readOnly}"/>
                <td class="datacell">
                	<c:choose>
                        <c:when test="${readOnly}">
                            <kul:htmlControlAttribute attributeEntry="${adAttributes.financialDocumentAdvanceDepositDate}" property="document.advanceDepositDetail[${ctr}].financialDocumentAdvanceDepositDate" readOnly="true" />
                        </c:when>
                        <c:otherwise>
                            <kul:dateInput attributeEntry="${adAttributes.financialDocumentAdvanceDepositDate}" property="document.advanceDepositDetail[${ctr}].financialDocumentAdvanceDepositDate" />
                        </c:otherwise>
                    </c:choose>
                </td>
                <td class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${adAttributes.financialDocumentAdvanceDepositReferenceNumber}" property="document.advanceDepositDetail[${ctr}].financialDocumentAdvanceDepositReferenceNumber" readOnly="${readOnly}"/>
                </td>
                <td class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${adAttributes.financialDocumentAdvanceDepositDescription}" property="document.advanceDepositDetail[${ctr}].financialDocumentAdvanceDepositDescription" readOnly="${readOnly}"/>
                </td>
                <td class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${adAttributes.financialDocumentAdvanceDepositAmount}" property="document.advanceDepositDetail[${ctr}].financialDocumentAdvanceDepositAmount" readOnly="${readOnly}"/>
                </td>
                <c:if test="${not readOnly}">
                    <td class="datacell">
                    	<div align="center">
                    		<html:image property="methodToCall.deleteAdvanceDeposit.line${ctr}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" alt="Delete an Advance Deposit" title="Delete an Advance Deposit" styleClass="tinybutton"/>
                    	</div>
                    </td>
                </c:if>
            </tr>
        </logic:iterate>
		<tr>
	 		<td class="total-line" colspan="6">&nbsp;</td>
	  		<td class="total-line" ><strong>Total: ${KualiForm.document.currencyFormattedTotalAdvanceDepositAmount}</strong><html:hidden write="false" property="document.totalAdvanceDepositAmount" /></td>
            <c:if test="${not readOnly}">
                <td class="total-line">&nbsp;</td>
            </c:if>
		</tr>
	</table>
  </div>
</kul:tab>
