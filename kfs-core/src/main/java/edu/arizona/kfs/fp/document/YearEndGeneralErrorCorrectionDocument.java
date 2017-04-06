package edu.arizona.kfs.fp.document;

import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.fp.document.YearEndDocument;
import org.kuali.kfs.fp.document.service.YearEndPendingEntryService;
import org.kuali.kfs.sys.context.SpringContext;

public class YearEndGeneralErrorCorrectionDocument extends GeneralErrorCorrectionDocument implements YearEndDocument {

	public YearEndGeneralErrorCorrectionDocument() {
		super();
	}

    @Override
    public void setAccountingPeriod(AccountingPeriod accountingPeriod) {
        //Need to override this method to set the posting year and
        //posting period code so that we can have period 13 in the document's
        //table in the database so that it's consistent with the GL Entry's
        //posting year and posting period.
        YearEndPendingEntryService yearEndPendingEntryService = SpringContext.getBean(YearEndPendingEntryService.class);
        setPostingYear(yearEndPendingEntryService.getPreviousFiscalYear());
        this.setPostingPeriodCode(yearEndPendingEntryService.getFinalAccountingPeriod());
    }

}
