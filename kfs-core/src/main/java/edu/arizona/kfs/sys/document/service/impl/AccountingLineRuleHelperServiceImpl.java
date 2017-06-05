package edu.arizona.kfs.sys.document.service.impl;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.AccountingLineOverride;
import org.kuali.rice.krad.util.GlobalVariables;

import edu.arizona.kfs.fp.businessobject.GECSourceAccountingLine;

public class AccountingLineRuleHelperServiceImpl extends org.kuali.kfs.sys.document.service.impl.AccountingLineRuleHelperServiceImpl {

    /*
     * Overridden to add custom error message for GEC
     */
    @SuppressWarnings("deprecation") // some constants, even though it's the same as super
    public boolean hasAccountRequiredOverrides(AccountingLine line, String overrideCode) {
        boolean retVal = true;
        AccountingLineOverride override = AccountingLineOverride.valueOf(overrideCode);
        Account account = line.getAccount();
        if (AccountingLineOverride.needsExpiredAccountOverride(account) && !override.hasComponent(AccountingLineOverride.COMPONENT.EXPIRED_ACCOUNT)) {
            Account continuation = accountService.getUnexpiredContinuationAccountOrNull(account);
            if (line instanceof GECSourceAccountingLine) {
                String propertyName = KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME;
                String messageKey = edu.arizona.kfs.sys.KFSKeyConstants.GEC_ERROR_DOCUMENT_ACCOUNT_EXPIRED;
                String[] messageArgs = new String[]{account.getAccountNumber()};
                GlobalVariables.getMessageMap().putError(propertyName, messageKey, messageArgs);
            } else if (continuation == null) {
                GlobalVariables.getMessageMap().putError(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME, KFSKeyConstants.ERROR_DOCUMENT_ACCOUNT_EXPIRED_NO_CONTINUATION, new String[]{account.getAccountNumber()});
            } else {
                GlobalVariables.getMessageMap().putError(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME, KFSKeyConstants.ERROR_DOCUMENT_ACCOUNT_EXPIRED, new String[]{account.getAccountNumber(), continuation.getChartOfAccountsCode(), continuation.getAccountNumber()});
            }
            retVal = false;
        }

        return retVal;
    }

}
