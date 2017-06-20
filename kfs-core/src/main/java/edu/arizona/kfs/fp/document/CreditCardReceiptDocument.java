package edu.arizona.kfs.fp.document;

import edu.arizona.kfs.sys.KFSPropertyConstants;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.AccountingDocumentRuleHelperService;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

import java.sql.Date;

/**
 * Created by nataliac on 5/10/17.
 */
public class CreditCardReceiptDocument extends org.kuali.kfs.fp.document.CreditCardReceiptDocument {
    private Date postingDate;

    /**
     * Default constructor that calls super.
     */
    public CreditCardReceiptDocument() {
        super();
    }


    /**
     * @see org.kuali.kfs.fp.document.CreditCardReceiptDocument#generateDocumentGeneralLedgerPendingEntries(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        boolean success = true;

        if (SpringContext.getBean(BankService.class).isBankSpecificationEnabled()) {

            Bank offsetBank = StringUtils.isBlank(getCreditCardReceiptBankCode()) ? getOffsetBank() : SpringContext.getBean(BankService.class).getByPrimaryId(getCreditCardReceiptBankCode());
            if (ObjectUtils.isNull(offsetBank)) {
                LOG.error("OffsetBank could not be found !!! BankSpecification IS NOT Enabled. Exiting CreditCardReceiptDocument.generateDocumentGeneralLedgerPendingEntries(). ");
                GlobalVariables.getMessageMap().putError(KFSKeyConstants.CreditCardReceipt.FIELD_DOCUMENT_CREDIT_CARD_TYPE_CODE, KFSKeyConstants.CreditCardReceipt.ERROR_DOCUMENT_CREDIT_CARD_BANK_MUST_EXIST_WHEN_BANK_ENHANCEMENT_ENABLED, new String[]{KFSParameterKeyConstants.ENABLE_BANK_SPECIFICATION_IND, KFSParameterKeyConstants.DEFAULT_BANK_BY_DOCUMENT_TYPE});
                return false;
            }

            GeneralLedgerPendingEntryService glpeService = SpringContext.getBean(GeneralLedgerPendingEntryService.class);
            KualiDecimal bankOffsetAmount = glpeService.getOffsetToCashAmount(this).negated();
            GeneralLedgerPendingEntry bankOffsetEntry = new GeneralLedgerPendingEntry();
            success &= glpeService.populateBankOffsetGeneralLedgerPendingEntry(offsetBank, bankOffsetAmount, this, getPostingYear(), sequenceHelper, bankOffsetEntry, KFSConstants.CREDIT_CARD_RECEIPTS_LINE_ERRORS);

            // An unsuccessfully populated bank offset entry may contain invalid relations, so don't add it
            if (!success) {
                LOG.info("Could not populate BankOffsetGeneralLedgerPendingEntry!!! Exiting...");
                return false;
            }

            AccountingDocumentRuleHelperService accountingDocumentRuleUtil = SpringContext.getBean(AccountingDocumentRuleHelperService.class);
            bankOffsetEntry.setTransactionLedgerEntryDescription(accountingDocumentRuleUtil.formatProperty(KFSKeyConstants.Bank.DESCRIPTION_GLPE_BANK_OFFSET));

            if (ObjectUtils.isNotNull(getPostingDate())) {
                // Set fiscal period and reference origin code for Bank offset entry
                for (GeneralLedgerPendingEntry glpe : getGeneralLedgerPendingEntries()) {
                    if (ObjectUtils.isNotNull(glpe) && KFSPropertyConstants.BENEFIT_EXPENSE_DOC_TYPE.equalsIgnoreCase(glpe.getReferenceFinancialSystemOriginationCode())) {
                        populateOffsetEntry(bankOffsetEntry, glpe);
                    }
                }
                addPendingEntry(bankOffsetEntry);
                sequenceHelper.increment();

                GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(bankOffsetEntry);
                success &= glpeService.populateOffsetGeneralLedgerPendingEntry(getPostingYear(), bankOffsetEntry, sequenceHelper, offsetEntry);
                // Set fiscal period and reference origin code for offset entry
                for (GeneralLedgerPendingEntry glpe : getGeneralLedgerPendingEntries()) {
                    if (ObjectUtils.isNotNull(glpe) && KFSPropertyConstants.BENEFIT_EXPENSE_DOC_TYPE.equalsIgnoreCase(glpe.getReferenceFinancialSystemOriginationCode())) {
                        populateOffsetEntry(offsetEntry, glpe);
                    }
                }

                addPendingEntry(offsetEntry);
                sequenceHelper.increment();
            } else {
                LOG.info("postingDate is EMPTY! Can't add offsetEntries.... ");
            }
        } else {
            LOG.info("BankSpecification IS NOT Enabled. Exiting CreditCardReceiptDocument.generateDocumentGeneralLedgerPendingEntries(). ");
        }
        return success;
    }


    /**
     * @see org.kuali.kfs.fp.document.CashReceiptFamilyBase#customizeExplicitGeneralLedgerPendingEntry(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail,
     * org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry)
     */
    @Override
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {
        super.customizeExplicitGeneralLedgerPendingEntry(postable, explicitEntry);
        if (KFSPropertyConstants.BENEFIT_EXPENSE_DOC_TYPE.equalsIgnoreCase(postable.getReferenceOriginCode()) && ObjectUtils.isNotNull(getPostingDate())) {
            explicitEntry.setUniversityFiscalPeriodCode(getAccountingPeriodService().getByDate(getPostingDate()).getUniversityFiscalPeriodCode());
            explicitEntry.setUniversityFiscalYear(getAccountingPeriodService().getByDate(getPostingDate()).getUniversityFiscalYear());
        }

    }

    private void populateOffsetEntry(GeneralLedgerPendingEntry offsetEntry, GeneralLedgerPendingEntry glpe) {
        offsetEntry.setUniversityFiscalPeriodCode(getAccountingPeriodService().getByDate(getPostingDate()).getUniversityFiscalPeriodCode());
        offsetEntry.setUniversityFiscalYear(getAccountingPeriodService().getByDate(getPostingDate()).getUniversityFiscalYear());
        offsetEntry.setReferenceFinancialSystemOriginationCode(glpe.getReferenceFinancialSystemOriginationCode());
        offsetEntry.setReferenceFinancialDocumentTypeCode(glpe.getReferenceFinancialDocumentTypeCode());
        offsetEntry.setReferenceFinancialDocumentNumber(glpe.getReferenceFinancialDocumentNumber());
    }

    public Date getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(Date postingDate) {
        this.postingDate = postingDate;
    }
}
