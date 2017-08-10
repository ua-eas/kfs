package edu.arizona.kfs.fp.document;

import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.fp.document.YearEndDocument;
import org.kuali.kfs.fp.document.service.YearEndPendingEntryService;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;

public class YearEndGeneralErrorCorrectionDocument extends GeneralErrorCorrectionDocument implements YearEndDocument {

    public YearEndGeneralErrorCorrectionDocument() {
        // Warning: This is needed, as super() inits some data structures, and the
        //          and spring was not calling it
        super();
    }


    @Override
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {
        super.customizeExplicitGeneralLedgerPendingEntry(postable, explicitEntry);
        SpringContext.getBean(YearEndPendingEntryService.class).customizeExplicitGeneralLedgerPendingEntry(this, (AccountingLine) postable, explicitEntry);
    }


    @Override
    public boolean customizeOffsetGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail accountingLine, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        boolean success = super.customizeOffsetGeneralLedgerPendingEntry(accountingLine, explicitEntry, offsetEntry);
        success &= SpringContext.getBean(YearEndPendingEntryService.class).customizeOffsetGeneralLedgerPendingEntry(this, accountingLine, explicitEntry, offsetEntry);
        return success;
    }


    /**
     * This method will set the YE-specific FY and period code that current YEGE docs should post
     * to. This is different to standard behavior, as it's one-year behind normal FY.
     *
     * Said another way, we need to override this method to set the posting year and
     * posting period code so that we can have period 13 in the document's
     * table in the database so that it's consistent with the GL Entry's
     * posting year and posting period.
     *
     * Note: This means accountingPeriod arg is ignored, but our method signature is confined by
     *       a foundation interface.
     *
     * @param accountingPeriod This arg is ignored, only here due to interface
     */
    @Override
    public void setAccountingPeriod(AccountingPeriod accountingPeriod) {
        YearEndPendingEntryService yearEndPendingEntryService = SpringContext.getBean(YearEndPendingEntryService.class);
        setPostingYear(yearEndPendingEntryService.getPreviousFiscalYear());
        this.setPostingPeriodCode(yearEndPendingEntryService.getFinalAccountingPeriod());
    }

}
