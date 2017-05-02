/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kra.external.award;

import org.kuali.kra.external.awardpayment.AwardMethodOfPaymentDTO;
import org.kuali.kra.external.sponsor.SponsorDTO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "awardDTO", propOrder = {
    "awardId",
    "awardNumber",
    "accountNumber",
    "chartOfAccountsCode",
    "proposal",
    "awardStartDate",
    "awardEndDate",
    "awardTotalAmount",
    "awardDocumentNumber",
    "awardLastUpdateDate",
    "awardDirectCostAmount",
    "awardIndirectCostAmount",
    "awardCreateTimestamp",
    "proposalAwardTypeCode",
    "awardStatusCode",
    "active",
    "sponsorCode",
    "title",
    "awardCommentText",
    "sponsor",
    "principalInvestigatorId",
    "unitNumber",
    "fundManagerId",
    "additionalFormsRequired",
    "autoApproveInvoice",
    "stopWork",
    "additionalFormsDescription",
    "invoicingOption",
    "invoicingOptionDescription",
    "dunningCampaignId",
    "stopWorkReason",
    "minInvoiceAmount",
    "methodOfPayment",
    "invoiceBillingFrequency",
    "excludedFromInvoicing",
    "excludedFromInvoicingReason",
    "sequenceNumber",
    "sequenceStatus",
    "obligationStartDate",
    "obligationEndDate",
    "awardAccounts",
    "locFund",
    "locFundCode",
    "locFundStartDate",
    "locFundExpirationDate",
    "locFundAmount",
    "locFundGroup",
    "locFundGroupCode"
})
public class AwardDTO implements Serializable {

    private static final long serialVersionUID = -7830094624716529740L;

    private Long awardId;
    private String awardNumber;
    private String accountNumber;
    private String chartOfAccountsCode;
    private ProposalDTO proposal;
    private Date awardStartDate;
    private Date awardEndDate;
    private BigDecimal awardTotalAmount;
    private String awardDocumentNumber;
    private Date awardLastUpdateDate;
    private BigDecimal awardDirectCostAmount;
    private BigDecimal awardIndirectCostAmount;
    private Date awardCreateTimestamp;
    private String proposalAwardTypeCode;
    private String awardStatusCode;
    private boolean active;
    private String sponsorCode;
    private String title;
    private String awardCommentText;
    private SponsorDTO sponsor;
    private String principalInvestigatorId;
    private String unitNumber;
    private String fundManagerId;
    private boolean additionalFormsRequired;
    private boolean autoApproveInvoice;
    private boolean stopWork;
    private String additionalFormsDescription;
    private String invoicingOption;
    private String invoicingOptionDescription;
    private String dunningCampaignId;
    private String stopWorkReason;
    private boolean excludedFromInvoicing;
    private String excludedFromInvoicingReason;
    private BigDecimal minInvoiceAmount;
    private AwardMethodOfPaymentDTO methodOfPayment;
    private String invoiceBillingFrequency;
    private Integer sequenceNumber;
    private String sequenceStatus;
    private Date obligationStartDate;
    private Date obligationEndDate;
    private List<AwardAccountDTO> awardAccounts;

    private String locFund;
    private String locFundCode;
    private Date locFundStartDate;
    private Date locFundExpirationDate;
    private BigDecimal locFundAmount;
    private String locFundGroup;
    private String locFundGroupCode;


    public Long getAwardId() {
        return awardId;
    }

    public void setAwardId(Long awardId) {
        this.awardId = awardId;
    }

    public ProposalDTO getProposal() {
        return proposal;
    }

    public void setProposal(ProposalDTO proposal) {
        this.proposal = proposal;
    }

    public Date getAwardStartDate() {
        return awardStartDate;
    }

    public void setAwardStartDate(Date awardStartDate) {
        this.awardStartDate = awardStartDate;
    }

    public Date getAwardEndDate() {
        return awardEndDate;
    }

    public void setAwardEndDate(Date awardEndDate) {
        this.awardEndDate = awardEndDate;
    }

    public BigDecimal getAwardTotalAmount() {
        return awardTotalAmount;
    }

    public void setAwardTotalAmount(BigDecimal awardTotalAmount) {
        this.awardTotalAmount = awardTotalAmount;
    }

    public String getAwardDocumentNumber() {
        return awardDocumentNumber;
    }

    public void setAwardDocumentNumber(String awardDocumentNumber) {
        this.awardDocumentNumber = awardDocumentNumber;
    }

    public Date getAwardLastUpdateDate() {
        return awardLastUpdateDate;
    }

    public void setAwardLastUpdateDate(Date awardLastUpdateDate) {
        this.awardLastUpdateDate = awardLastUpdateDate;
    }

    public BigDecimal getAwardDirectCostAmount() {
        return awardDirectCostAmount;
    }

    public void setAwardDirectCostAmount(BigDecimal awardDirectCostAmount) {
        this.awardDirectCostAmount = awardDirectCostAmount;
    }

    public BigDecimal getAwardIndirectCostAmount() {
        return awardIndirectCostAmount;
    }

    public void setAwardIndirectCostAmount(BigDecimal awardIndirectCostAmount) {
        this.awardIndirectCostAmount = awardIndirectCostAmount;
    }

    public Date getAwardCreateTimestamp() {
        return awardCreateTimestamp;
    }

    public void setAwardCreateTimestamp(Date awardCreateTimestamp) {
        this.awardCreateTimestamp = awardCreateTimestamp;
    }

    public String getProposalAwardTypeCode() {
        return proposalAwardTypeCode;
    }

    public void setProposalAwardTypeCode(String proposalAwardTypeCode) {
        this.proposalAwardTypeCode = proposalAwardTypeCode;
    }

    public String getAwardStatusCode() {
        return awardStatusCode;
    }

    public void setAwardStatusCode(String awardStatusCode) {
        this.awardStatusCode = awardStatusCode;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public String getSponsorCode() {
        return sponsorCode;
    }

    public void setSponsorCode(String sponsorCode) {
        this.sponsorCode = sponsorCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAwardCommentText() {
        return awardCommentText;
    }

    public void setAwardCommentText(String awardCommentText) {
        this.awardCommentText = awardCommentText;
    }

    public SponsorDTO getSponsor() {
        return sponsor;
    }

    public void setSponsor(SponsorDTO sponsor) {
        this.sponsor = sponsor;
    }

    public String getPrincipalInvestigatorId() {
        return principalInvestigatorId;
    }

    public void setPrincipalInvestigatorId(String principalInvestigatorId) {
        this.principalInvestigatorId = principalInvestigatorId;
    }

    public String getFundManagerId() {
        return fundManagerId;
    }

    public void setFundManagerId(String fundManagerId) {
        this.fundManagerId = fundManagerId;
    }

    public boolean isAdditionalFormsRequired() {
        return additionalFormsRequired;
    }

    public void setAdditionalFormsRequired(boolean additionalFormsRequired) {
        this.additionalFormsRequired = additionalFormsRequired;
    }

    public boolean isStopWork() {
        return stopWork;
    }

    public void setStopWork(boolean stopWork) {
        this.stopWork = stopWork;
    }

    public String getAdditionalFormsDescription() {
        return additionalFormsDescription;
    }

    public void setAdditionalFormsDescription(String additionalFormsDescription) {
        this.additionalFormsDescription = additionalFormsDescription;
    }

    public String getInvoicingOption() {
        return invoicingOption;
    }

    public void setInvoicingOption(String invoicingOption) {
        this.invoicingOption = invoicingOption;
    }

    public String getDunningCampaignId() {
        return dunningCampaignId;
    }

    public void setDunningCampaignId(String dunningCampaignId) {
        this.dunningCampaignId = dunningCampaignId;
    }

    public String getStopWorkReason() {
        return stopWorkReason;
    }

    public void setStopWorkReason(String stopWorkReason) {
        this.stopWorkReason = stopWorkReason;
    }

    public boolean isAutoApproveInvoice() {
        return autoApproveInvoice;
    }

    public void setAutoApproveInvoice(boolean autoApproveInvoice) {
        this.autoApproveInvoice = autoApproveInvoice;
    }

    public BigDecimal getMinInvoiceAmount() {
        return minInvoiceAmount;
    }

    public void setMinInvoiceAmount(BigDecimal minInvoiceAmount) {
        this.minInvoiceAmount = minInvoiceAmount;
    }

    public String getAwardNumber() {
        return awardNumber;
    }

    public void setAwardNumber(String awardNumber) {
        this.awardNumber = awardNumber;
    }

    public AwardMethodOfPaymentDTO getMethodOfPayment() {
        return methodOfPayment;
    }

    public void setMethodOfPayment(AwardMethodOfPaymentDTO methodOfPayment) {
        this.methodOfPayment = methodOfPayment;
    }

    public String getInvoiceBillingFrequency() {
        return invoiceBillingFrequency;
    }

    public void setInvoiceBillingFrequency(String invoiceBillingFrequency) {
        this.invoiceBillingFrequency = invoiceBillingFrequency;
    }

    public String getInvoicingOptionDescription() {
        return invoicingOptionDescription;
    }

    public void setInvoicingOptionDescription(String invoicingOptionDescription) {
        this.invoicingOptionDescription = invoicingOptionDescription;
    }

    public boolean isExcludedFromInvoicing() {
        return excludedFromInvoicing;
    }

    public void setExcludedFromInvoicing(boolean excludedFromInvoicing) {
        this.excludedFromInvoicing = excludedFromInvoicing;
    }

    public String getExcludedFromInvoicingReason() {
        return excludedFromInvoicingReason;
    }

    public void setExcludedFromInvoicingReason(String excludedFromInvoicingReason) {
        this.excludedFromInvoicingReason = excludedFromInvoicingReason;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getSequenceStatus() {
        return sequenceStatus;
    }

    public void setSequenceStatus(String sequenceStatus) {
        this.sequenceStatus = sequenceStatus;
    }

    public Date getObligationStartDate() {
        return obligationStartDate;
    }

    public void setObligationStartDate(Date obligationStartDate) {
        this.obligationStartDate = obligationStartDate;
    }

    public Date getObligationEndDate() {
        return obligationEndDate;
    }

    public void setObligationEndDate(Date obligationEndDate) {
        this.obligationEndDate = obligationEndDate;
    }

    public List<AwardAccountDTO> getAwardAccounts() {
        return awardAccounts;
    }

    public void setAwardAccounts(List<AwardAccountDTO> awardAccounts) {
        this.awardAccounts = awardAccounts;
    }

    public String getLocFund() {
        return locFund;
    }

    public void setLocFund(String locFund) {
        this.locFund = locFund;
    }

    public String getLocFundCode() {
        return locFundCode;
    }

    public void setLocFundCode(String locFundCode) {
        this.locFundCode = locFundCode;
    }

    public Date getLocFundStartDate() {
        return locFundStartDate;
    }

    public void setLocFundStartDate(Date locFundStartDate) {
        this.locFundStartDate = locFundStartDate;
    }

    public Date getLocFundExpirationDate() {
        return locFundExpirationDate;
    }

    public void setLocFundExpirationDate(Date locFundExpirationDate) {
        this.locFundExpirationDate = locFundExpirationDate;
    }

    public BigDecimal getLocFundAmount() {
        return locFundAmount;
    }

    public void setLocFundAmount(BigDecimal locFundAmount) {
        this.locFundAmount = locFundAmount;
    }

    public String getLocFundGroup() {
        return locFundGroup;
    }

    public void setLocFundGroup(String locFundGroup) {
        this.locFundGroup = locFundGroup;
    }

    public String getLocFundGroupCode() {
        return locFundGroupCode;
    }

    public void setLocFundGroupCode(String locFundGroupCode) {
        this.locFundGroupCode = locFundGroupCode;
    }
}
