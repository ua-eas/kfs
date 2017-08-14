package edu.arizona.kfs.fp.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.document.CapitalAccountingLinesDocumentBase;
import org.kuali.kfs.fp.document.authorization.CapitalAccountingLinesAuthorizer;
import org.kuali.kfs.fp.document.validation.impl.ProcurementCardAccountAccessibilityValidation;
import org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.document.Correctable;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

public class CapitalAccountingLinesAccessibleValidation extends ProcurementCardAccountAccessibilityValidation {
    protected CapitalAssetManagementModuleService capitalAssetManagementModuleService;

    /**
     * Due to code in CapitalAccountingLinesAuthorizerBase we need alter the
     * accessible logic a bit. Otherwise the user gets stopped for reasons they
     * shouldn't be
     *
     * @see org.kuali.kfs.fp.document.authorization.CapitalAccountingLinesAuthorizerBase#determineEditPermissionOnField
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#determineEditPermissionOnField
     * @see org.kuali.kfs.sys.document.validation.impl.AccountingLineAccessibleValidation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        if (accountingDocumentForValidation instanceof CapitalAccountingLinesDocumentBase) {
            CapitalAccountingLinesDocumentBase caldb = (CapitalAccountingLinesDocumentBase) accountingDocumentForValidation;

            if (caldb.getCapitalAccountingLines().size() > 0 && capitalAssetManagementModuleService.hasCapitalAssetObjectSubType(accountingLineForValidation)) {
                // In this scenario the line is readOnly because of the logic in CapitalAccountingLinesAuthorizerBase. We only stop the user from updating
                // if the document shouldn't be editable. That means call AccountingLineAuthorizerBase#determineEditPermissionOnField and skip
                // CapitalAccountingLinesAuthorizerBase#determineEditPermissionOnField. Furthermore error correction documents should not be stopped
                if (accountingDocumentForValidation instanceof Correctable) {
                    final String errorDocumentNumber = ((FinancialSystemDocumentHeader) accountingDocumentForValidation.getDocumentHeader()).getFinancialDocumentInErrorNumber();
                    if (StringUtils.isNotBlank(errorDocumentNumber)) {
                        return true;
                    }
                }

                // we can safely cast the lookup result to CapitalAccountingLinesAuthorizer, because even if the security module is turned on so that the returned result is CapitalAccountingLinesAuthorizer, it's
                // still fine since the latter implements CapitalAccountingLinesAuthorizer
                final CapitalAccountingLinesAuthorizer capitalAccountingLineAuthorizer = (CapitalAccountingLinesAuthorizer) lookupAccountingLineAuthorizer();
                return capitalAccountingLineAuthorizer.determineEditPermissionOnFieldBypassCapitalCheck(accountingDocumentForValidation, accountingLineForValidation, getAccountingLineCollectionProperty(), KFSPropertyConstants.ACCOUNT_NUMBER, true);
            }
        }

        return super.validate(event);
    }

    /**
     * Set the capitalAssetBuilderModuleService
     *
     * @param capitalAssetBuilderModuleService
     */
    //TODO: sskinner, release 31 KFS6->KFS7 merge; CapitalAssetBuilderModuleService is gone, make sure
    // CapitalAssetManagementModuleService is an apt substitute
    public void setCapitalAssetManagementModuleService(CapitalAssetManagementModuleService capitalAssetManagementModuleService) {
        this.capitalAssetManagementModuleService = capitalAssetManagementModuleService;
    }
}
