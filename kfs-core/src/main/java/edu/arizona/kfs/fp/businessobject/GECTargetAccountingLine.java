package edu.arizona.kfs.fp.businessobject;


import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class GECTargetAccountingLine extends org.kuali.kfs.fp.businessobject.GECTargetAccountingLine {


    public void setObjectTypeCode(@SuppressWarnings("unused") String objectTypeCode) {
        // no-op, since super's getter is a sub-field of a composite object; this
        // satisfies PojoPropertyUtilsBean from complaining
    }


    /*
     * Overridden to not use line sequence numbers as the primary way to detect change
     * events (namely Update and Review).
     */
    @Override
    public boolean isLike(AccountingLine other) {

        if (other == null || !getObjectId().equals(other.getObjectId())) {
            // Not the same, this is a UUID
            return false;
        }

        // The following are all the fields that can be changed in the UI:
        String coa = getChartOfAccountsCode();
        String accountNumber = getAccountNumber();
        String subAccountNumber = getSubAccountNumber();
        String objectCode = getFinancialObjectCode();
        String subObjectCode = getFinancialSubObjectCode();
        String projectCode = getProjectCode();
        String orgRefId = getOrganizationReferenceId();
        KualiDecimal amount = getAmount();

        // Carefully arranged to shortcircuit when this' value is not null, and it doesn't match
        // the input; a false triggers an update event, and true triggers a review event
        if ((coa != null && !coa.equals(other.getChartOfAccountsCode()))
                || (accountNumber != null && !accountNumber.equals(other.getAccountNumber()))
                || (subAccountNumber != null && !subAccountNumber.equals(other.getSubAccountNumber()))
                || (objectCode != null && !objectCode.equals(other.getFinancialObjectCode()))
                || (subObjectCode != null && !subObjectCode.equals(other.getFinancialSubObjectCode()))
                || (projectCode != null && !projectCode.equals(other.getProjectCode()))
                || (orgRefId != null && !orgRefId.equals(other.getOrganizationReferenceId()))
                || (amount != null && !amount.equals(other.getAmount()))) {
            return false;
        }

        // If we made it here, we are the same
        return true;
    }

}
