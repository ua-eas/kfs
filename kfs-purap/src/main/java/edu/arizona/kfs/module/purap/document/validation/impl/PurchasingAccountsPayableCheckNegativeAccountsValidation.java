package edu.arizona.kfs.module.purap.document.validation.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.GlobalVariables;

public class PurchasingAccountsPayableCheckNegativeAccountsValidation extends org.kuali.kfs.module.purap.document.validation.impl.PurchasingAccountsPayableCheckNegativeAccountsValidation {

    private ParameterService parameterService;

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        PurchasingAccountsPayableDocument document = (PurchasingAccountsPayableDocument) event.getDocument();

        GlobalVariables.getMessageMap().clearErrorPath();
        // GlobalVariables.getMessageMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);

        // if this was set somewhere on the doc(for later use) in prepare for save we could avoid this call
        getPurapAccountingService().updateAccountAmounts(document);

        Set<String> excludedItemTypeCodes = getExcludeItemTypeCodesByParameter(document);
        List<SourceAccountingLine> sourceLines = getPurapAccountingService().generateSummaryExcludeItemTypes(document.getItems(), excludedItemTypeCodes);

        for (SourceAccountingLine sourceAccountingLine : sourceLines) {
            // check if the summary account is for tax withholding
            boolean isTaxAccount = getPurapAccountingService().isTaxAccount(document, sourceAccountingLine);

            // exclude tax withholding accounts from non-negative requirement
            if (!isTaxAccount && sourceAccountingLine.getAmount().isNegative()) {

                String subAccountNumber = (sourceAccountingLine.getSubAccountNumber() == null) ? "" : sourceAccountingLine.getSubAccountNumber();
                String subObjectCode = (sourceAccountingLine.getFinancialSubObjectCode() == null) ? "" : sourceAccountingLine.getFinancialSubObjectCode();
                String projCode = (sourceAccountingLine.getProjectCode() == null) ? "" : sourceAccountingLine.getProjectCode();
                String orgRefId = (sourceAccountingLine.getOrganizationReferenceId() == null) ? "" : sourceAccountingLine.getOrganizationReferenceId();

                String accountString = sourceAccountingLine.getChartOfAccountsCode() + " - " + sourceAccountingLine.getAccountNumber() + " - " + subAccountNumber + " - " + sourceAccountingLine.getFinancialObjectCode() + " - " + subObjectCode + " - " + projCode + " - " + orgRefId;
                GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ACCOUNT_AMOUNT_TOTAL, accountString, sourceAccountingLine.getAmount() + "");
                valid &= false;
            }
        }

        GlobalVariables.getMessageMap().clearErrorPath();
        return valid;
    }

    private Set<String> getExcludeItemTypeCodesByParameter(PurchasingAccountsPayableDocument document) {

        HashSet<String> excludeSet = new HashSet<String>();
        if (document instanceof PaymentRequestDocument) {
            excludeSet.addAll(parameterService.getParameterValuesAsString(KFSConstants.OptionalModuleNamespaces.PURCHASING_ACCOUNTS_PAYABLE, "Payment Request", PurapConstants.ITEM_ALLOWS_NEGATIVE));
        }
        if (document instanceof VendorCreditMemoDocument) {
            excludeSet.addAll(parameterService.getParameterValuesAsString(KFSConstants.OptionalModuleNamespaces.PURCHASING_ACCOUNTS_PAYABLE, "Vendor Credit Memo", PurapConstants.ITEM_ALLOWS_NEGATIVE));
        }

        return excludeSet;
    }

}
