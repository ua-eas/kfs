package edu.arizona.kfs.fp.document.authorization;

import org.kuali.kfs.fp.document.DistributionOfIncomeAndExpenseDocument;
import org.kuali.kfs.fp.document.authorization.FinancialProcessingAccountingLineAuthorizer;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.ElectronicPaymentClaim;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Authorizer which deals with financial processing document issues, specifically sales tax lines on documents This class utilizes
 * the new accountingLine model.
 */
public class DistributionOfIncomeAndExpenseAccountingLineAuthorizer extends FinancialProcessingAccountingLineAuthorizer {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DistributionOfIncomeAndExpenseAccountingLineAuthorizer.class);

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#determineEditPermissionOnField(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine, String, String, boolean)
     */
    @Override
    public boolean determineEditPermissionOnField(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty, String fieldName, boolean editablePage) {
        final boolean canModify = super.determineEditPermissionOnField(accountingDocument, accountingLine, accountingLineCollectionProperty, fieldName, editablePage);

        if (canModify && accountingLine.isSourceAccountingLine() && !accountingLineCollectionProperty.contains("newSource")) {
            return !this.isAutoGeneratedSourceLine(accountingDocument, accountingLine);
        }

        return canModify;
    }

    /**
     * @see org.kuali.kfs.fp.document.authorization.DistributionOfIncomeAndExpenseAccountingLineAuthorizer#renderNewLine(org.kuali.kfs.sys.document.AccountingDocument,
     *      String)
     */
    @Override
    public boolean renderNewLine(AccountingDocument accountingDocument, String accountingGroupProperty) {
        final boolean shouldRender = super.renderNewLine(accountingDocument, accountingGroupProperty);

        return shouldRender;
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#determineEditPermissionOnLine(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine, String, boolean, boolean)
     */
    @Override
    public boolean determineEditPermissionOnLine(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty, boolean currentUserIsDocumentInitiator, boolean pageIsEditable) {
        final boolean hasEditPermOnLine = super.determineEditPermissionOnLine(accountingDocument, accountingLine, accountingLineCollectionProperty, currentUserIsDocumentInitiator, pageIsEditable);
        
        if (hasEditPermOnLine && accountingLine.isSourceAccountingLine() && !accountingLineCollectionProperty.contains("newSource")) {
            return !this.isAutoGeneratedSourceLine(accountingDocument, accountingLine);
        }

        return hasEditPermOnLine;
    }

    /**
     * Determines if the DI document has electronic payment claims associated with it
     * 
     * @param accountingDocument a DI document
     * @return true if there are electronic payment claims, false otherwise
     */
    protected boolean hasElectronicPaymentClaims(AccountingDocument accountingDocument) {
        DistributionOfIncomeAndExpenseDocument diDoc = (DistributionOfIncomeAndExpenseDocument) accountingDocument;

        List<ElectronicPaymentClaim> electronicPaymentClaims = diDoc.getElectronicPaymentClaims();

        if (electronicPaymentClaims == null) {
            diDoc.refreshReferenceObject(KFSPropertyConstants.ELECTRONIC_PAYMENT_CLAIMS);
            electronicPaymentClaims = diDoc.getElectronicPaymentClaims();
        }

        return (!ObjectUtils.isNull(electronicPaymentClaims) && electronicPaymentClaims.size() > 0);
    }

    /**
     * determine whether the given source accounting line is auto generated by ElectronicPaymentClaims
     * 
     * @param accountingDocument the given document
     * @param accountingLine the given accounting line
     * @return true if the given accounting line is auto generated by ElectronicPaymentClaims
     */
    protected boolean isAutoGeneratedSourceLine(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        if (this.hasElectronicPaymentClaims(accountingDocument) && ObjectUtils.isNotNull(accountingLine)) {
            DistributionOfIncomeAndExpenseDocument diDoc = (DistributionOfIncomeAndExpenseDocument) accountingDocument;

            List<ElectronicPaymentClaim> electronicPaymentClaims = diDoc.getElectronicPaymentClaims();
            Integer sequenceNumber = accountingLine.getSequenceNumber();
            if(sequenceNumber != null && sequenceNumber > electronicPaymentClaims.size()){
                return false;
            }
            
            for (ElectronicPaymentClaim claim : electronicPaymentClaims) {
                boolean isAutoGeneratedSourceLine = this.isAutoGeneratedSourceLine(accountingLine, claim);

                if (isAutoGeneratedSourceLine) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * determine whether the given accounting line is generated from the given electronic payment claim
     * 
     * @param accountingLine the given accounting line
     * @param electronicPaymentClaim the given electronic payment claim
     * @return true if the given accounting line is generated from the given electronic payment claim; otherwise, false
     */
    protected boolean isAutoGeneratedSourceLine(AccountingLine accountingLine, ElectronicPaymentClaim electronicPaymentClaim) {
        AccountingLine generatedAccountingLine = this.getAccountingLineOfElectronicPaymentClaim(electronicPaymentClaim);

        boolean isAutoGeneratedSourceLine = ObjectUtil.equals(accountingLine, generatedAccountingLine, this.getKeyListForComparison());
 
        return isAutoGeneratedSourceLine;
    }

    /**
     * get the accounting line of the given electronic payment claim
     * 
     * @param electronicPaymentClaim the given electronic payment claim
     * @return the accounting line of the given electronic payment claim
     */
    protected AccountingLine getAccountingLineOfElectronicPaymentClaim(ElectronicPaymentClaim electronicPaymentClaim) {
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);

        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, electronicPaymentClaim.getDocumentNumber());
        fieldValues.put(KFSPropertyConstants.SEQUENCE_NUMBER, electronicPaymentClaim.getFinancialDocumentLineNumber());
        fieldValues.put("financialDocumentLineTypeCode", KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE);

        return (AccountingLine) businessObjectService.findByPrimaryKey(SourceAccountingLine.class, fieldValues);
    }

    /**
     * the key list being used to compare two accounting line
     */
    protected List<String> keyListForComparison = null;

    protected List<String> getKeyListForComparison() {
        if (ObjectUtils.isNotNull(keyListForComparison) && !keyListForComparison.isEmpty()) {
            return keyListForComparison;
        }

        keyListForComparison = new ArrayList<String>();
        keyListForComparison.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        keyListForComparison.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        keyListForComparison.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        keyListForComparison.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        keyListForComparison.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        keyListForComparison.add(KFSPropertyConstants.PROJECT_CODE);
        keyListForComparison.add(KFSPropertyConstants.ORGANIZATION_REFERENCE_ID);
        keyListForComparison.add(KFSPropertyConstants.AMOUNT);

        return keyListForComparison;
    }
}
