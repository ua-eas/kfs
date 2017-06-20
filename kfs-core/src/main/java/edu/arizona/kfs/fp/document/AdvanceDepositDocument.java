package edu.arizona.kfs.fp.document;

import org.kuali.kfs.fp.businessobject.AdvanceDepositDetail;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.AccountingDocumentRuleHelperService;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.rice.krad.util.ObjectUtils;
import edu.arizona.kfs.sys.KFSPropertyConstants;

import java.sql.Date;

/**
 * Created by nataliac on 5/9/17.
 */
public class AdvanceDepositDocument extends org.kuali.kfs.fp.document.AdvanceDepositDocument {
    private Date postingDate;

    /**
     * Default constructor that calls super.
     */
    public AdvanceDepositDocument() {
        super();
    }

    /**
     * @see org.kuali.kfs.fp.document.CreditCardReceiptDocument#generateDocumentGeneralLedgerPendingEntries(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        boolean success = true;

        if (SpringContext.getBean(BankService.class).isBankSpecificationEnabled()) {
            int displayedDepositNumber = 1;
            for (AdvanceDepositDetail detail : getAdvanceDeposits()) {
                detail.refreshReferenceObject(KFSPropertyConstants.BANK);

                GeneralLedgerPendingEntryService glpeService = SpringContext.getBean(GeneralLedgerPendingEntryService.class);
                GeneralLedgerPendingEntry bankOffsetEntry = new GeneralLedgerPendingEntry();
                if (!glpeService.populateBankOffsetGeneralLedgerPendingEntry(detail.getBank(), detail.getFinancialDocumentAdvanceDepositAmount(), this, getPostingYear(), sequenceHelper, bankOffsetEntry, KFSConstants.ADVANCE_DEPOSITS_LINE_ERRORS)) {
                    success = false;
                    LOG.warn("Error in populating bankOffsetGeneralLedgerPendingEntry in AdvanceDepositDocument... Skipping "+detail.toString());
                    continue; // An unsuccessfully populated bank offset entry may contain invalid relations, so don't add it
                }

                AccountingDocumentRuleHelperService accountingDocumentRuleUtil = SpringContext.getBean(AccountingDocumentRuleHelperService.class);
                bankOffsetEntry.setTransactionLedgerEntryDescription(accountingDocumentRuleUtil.formatProperty(KFSKeyConstants.AdvanceDeposit.DESCRIPTION_GLPE_BANK_OFFSET, displayedDepositNumber++));
                if ( ObjectUtils.isNotNull(getPostingDate()) ) {
                    // Set fiscal period and reference origin code for Bank offset entry
                    for (GeneralLedgerPendingEntry glpe : getGeneralLedgerPendingEntries()) {
                        if (ObjectUtils.isNotNull(glpe) && KFSPropertyConstants.BENEFIT_EXPENSE_DOC_TYPE.equalsIgnoreCase(glpe.getReferenceFinancialSystemOriginationCode())){
                            populateOffsetEntry(bankOffsetEntry, glpe);
                        }
                    }
                    addPendingEntry(bankOffsetEntry);
                    sequenceHelper.increment();

                    GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(bankOffsetEntry);
                    success &= glpeService.populateOffsetGeneralLedgerPendingEntry(getPostingYear(), bankOffsetEntry, sequenceHelper, offsetEntry);

                    // Set fiscal period and reference origin code for offset entry
                    for (GeneralLedgerPendingEntry glpe : getGeneralLedgerPendingEntries()) {
                        if (ObjectUtils.isNotNull(glpe) && KFSPropertyConstants.BENEFIT_EXPENSE_DOC_TYPE.equalsIgnoreCase(glpe.getReferenceFinancialSystemOriginationCode())){
                            populateOffsetEntry(offsetEntry, glpe);
                        }
                    }
                    addPendingEntry(offsetEntry);
                    sequenceHelper.increment();
                } else {
                    LOG.info("postingDate is EMPTY! Skipping "+detail.toString());
                }

            }
        } else {
            LOG.info("BankSpecification IS NOT Enabled. Exiting AdvanceDepositDocument.generateDocumentGeneralLedgerPendingEntries(). ");
        }

        return success;
    }

    private void populateOffsetEntry(GeneralLedgerPendingEntry offsetEntry, GeneralLedgerPendingEntry glpe){
        offsetEntry.setUniversityFiscalPeriodCode( getAccountingPeriodService().getByDate(getPostingDate()).getUniversityFiscalPeriodCode());
        offsetEntry.setUniversityFiscalYear( getAccountingPeriodService().getByDate(getPostingDate()).getUniversityFiscalYear());
        offsetEntry.setReferenceFinancialSystemOriginationCode(glpe.getReferenceFinancialSystemOriginationCode());
        offsetEntry.setReferenceFinancialDocumentTypeCode(glpe.getReferenceFinancialDocumentTypeCode());
        offsetEntry.setReferenceFinancialDocumentNumber(glpe.getReferenceFinancialDocumentNumber());
    }


    /**
     * @see org.kuali.kfs.fp.document.CashReceiptFamilyBase#customizeExplicitGeneralLedgerPendingEntry(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail,
     *      org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry)
     */
    @Override
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {
        super.customizeExplicitGeneralLedgerPendingEntry(postable, explicitEntry);
        if (KFSPropertyConstants.BENEFIT_EXPENSE_DOC_TYPE.equalsIgnoreCase(postable.getReferenceOriginCode()) && ObjectUtils.isNotNull(getPostingDate())) {
            explicitEntry.setUniversityFiscalPeriodCode(getAccountingPeriodService().getByDate(getPostingDate()).getUniversityFiscalPeriodCode());
            explicitEntry.setUniversityFiscalYear(getAccountingPeriodService().getByDate(getPostingDate()).getUniversityFiscalYear());
        }
    }

    /**
     * Gets the postingDate attribute.
     * @return Returns the postingDate.
     */
    public Date getPostingDate() {
        return postingDate;
    }

    /**
     * Sets the postingDate attribute value.
     * @param postingDate The postingDate to set.
     */
    public void setPostingDate(Date postingDate) {
        this.postingDate = postingDate;
    }
}
