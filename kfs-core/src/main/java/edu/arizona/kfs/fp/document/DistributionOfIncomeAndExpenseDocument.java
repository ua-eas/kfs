package edu.arizona.kfs.fp.document;

import edu.arizona.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.rice.krad.util.ObjectUtils;

import java.sql.Date;

/**
 * Created by nataliac on 5/11/17.
 */
public class DistributionOfIncomeAndExpenseDocument extends org.kuali.kfs.fp.document.DistributionOfIncomeAndExpenseDocument{
    private Date postingDate;
    /**
     * Default constructor that calls super.
     */
    public DistributionOfIncomeAndExpenseDocument() {
        super();
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

    public Date getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(Date postingDate) {
        this.postingDate = postingDate;
    }
}
