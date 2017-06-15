package edu.arizona.kfs.module.prje.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

import edu.arizona.kfs.module.prje.PRJEPropertyConstants;
import edu.arizona.kfs.module.prje.businessobject.PRJETransferRecord;
import edu.arizona.kfs.module.prje.PRJEConstants.ProrateCreditType;

/**
 * This class is only used in PRJE Reporting
 */
public class PRJEAuditItem extends TransientBusinessObjectBase {
    private String docType;
    private String docNumber;
    private String description;
    private String fromAccount;
    private String fromSubAccount;
    private String fromObjectCode;
    private String fromSubObject;
    private KualiDecimal fromBaseAmount;
    private String fromRate;
    private KualiDecimal fromFromAmount;
    private String fromDebitCredit;
    private String toAccount;
    private String toSubAccount;
    private String toObjectCode;
    private String toSubObject;
    private KualiDecimal toAmount;
    private String toDebitCredit;
    
    public PRJEAuditItem() {
        
    }
    
    public PRJEAuditItem(PRJETransferRecord transferRecord) {
        OriginEntryFull debitEntry = transferRecord.getDebitEntry();
        OriginEntryFull creditEntry = transferRecord.getCreditEntry();
        PRJEBaseAccount baseAccount = transferRecord.getBaseAccount();
        PRJEAccountLine accountLine = transferRecord.getAccountLine();
        
        this.docType = debitEntry.getFinancialDocumentTypeCode();
        this.docNumber = debitEntry.getDocumentNumber();
        this.fromAccount = debitEntry.getAccountNumber();
        this.fromSubAccount = debitEntry.getSubAccountNumber();
        this.fromObjectCode = debitEntry.getFinancialObjectCode();
        this.fromSubObject = debitEntry.getFinancialSubObjectCode();
        this.fromBaseAmount = transferRecord.getBalance();
        
        if ( ProrateCreditType.NO_OVERRIDE.getKey().equals(accountLine.getOverrideProrateType())) {
            this.fromRate = baseAccount.getProratePercent().toString();
        }
        else {
            this.fromRate = baseAccount.getProratePercent() + "*";
        }
        
        this.fromFromAmount = debitEntry.getTransactionLedgerEntryAmount();
        this.fromDebitCredit = debitEntry.getTransactionDebitCreditCode() + "R";
        this.toAccount = creditEntry.getAccountNumber();
        this.toSubAccount = creditEntry.getSubAccountNumber();
        this.toObjectCode = creditEntry.getFinancialObjectCode();
        this.toSubObject = creditEntry.getFinancialSubObjectCode();
        this.toAmount = creditEntry.getTransactionLedgerEntryAmount();
        this.toDebitCredit = creditEntry.getTransactionDebitCreditCode() + "R";
    }
    
    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }

    public String getFromSubAccount() {
        return fromSubAccount;
    }

    public void setFromSubAccount(String fromSubAccount) {
        this.fromSubAccount = fromSubAccount;
    }

    public String getFromObjectCode() {
        return fromObjectCode;
    }

    public void setFromObjectCode(String fromObjectCode) {
        this.fromObjectCode = fromObjectCode;
    }

    public String getFromSubObject() {
        return fromSubObject;
    }

    public void setFromSubObject(String fromSubObject) {
        this.fromSubObject = fromSubObject;
    }

    public KualiDecimal getFromBaseAmount() {
        return fromBaseAmount;
    }

    public void setFromBaseAmount(KualiDecimal fromBaseAmount) {
        this.fromBaseAmount = fromBaseAmount;
    }

    public String getFromRate() {
        return fromRate;
    }

    public void setFromRate(String fromRate) {
        this.fromRate = fromRate;
    }

    public KualiDecimal getFromFromAmount() {
        return fromFromAmount;
    }

    public void setFromFromAmount(KualiDecimal fromFromAmount) {
        this.fromFromAmount = fromFromAmount;
    }

    public String getFromDebitCredit() {
        return fromDebitCredit;
    }

    public void setFromDebitCredit(String fromDebitCredit) {
        this.fromDebitCredit = fromDebitCredit;
    }

    public String getToAccount() {
        return toAccount;
    }

    public void setToAccount(String toAccount) {
        this.toAccount = toAccount;
    }

    public String getToSubAccount() {
        return toSubAccount;
    }

    public void setToSubAccount(String toSubAccount) {
        this.toSubAccount = toSubAccount;
    }

    public String getToObjectCode() {
        return toObjectCode;
    }

    public void setToObjectCode(String toObjectCode) {
        this.toObjectCode = toObjectCode;
    }

    public String getToSubObject() {
        return toSubObject;
    }

    public void setToSubObject(String toSubObject) {
        this.toSubObject = toSubObject;
    }

    public KualiDecimal getToAmount() {
        return toAmount;
    }

    public void setToAmount(KualiDecimal toAmount) {
        this.toAmount = toAmount;
    }

    public String getToDebitCredit() {
        return toDebitCredit;
    }

    public void setToDebitCredit(String toDebitCredit) {
        this.toDebitCredit = toDebitCredit;
    }

    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap<String, String> retVal = new LinkedHashMap<String, String>();
        retVal.put(PRJEPropertyConstants.DOC_TYPE, getDocType());
        retVal.put(PRJEPropertyConstants.DOC_NUMBER, getDocNumber());
        retVal.put(PRJEPropertyConstants.DESCRIPTIONB, getDescription());
        retVal.put(PRJEPropertyConstants.FRM_ACCOUNT_NBR, getFromAccount());
        retVal.put(PRJEPropertyConstants.FRM_SUB_ACCT, getFromSubAccount());
        retVal.put(PRJEPropertyConstants.FRM_OBJECT_CD, getFromObjectCode());
        retVal.put(PRJEPropertyConstants.FRM_SUB_OBJECT, getFromSubObject());
        retVal.put(PRJEPropertyConstants.FRM_BASE_AMOUNT, getFromBaseAmount() == null ? null : getFromBaseAmount().toString());
        retVal.put(PRJEPropertyConstants.FRM_RATE, getFromRate());
        retVal.put(PRJEPropertyConstants.FRM_FROM_AMOUNT, getFromFromAmount() == null ? null : getFromFromAmount().toString());
        retVal.put(PRJEPropertyConstants.FRM_DEBIT_CREDIT, getFromDebitCredit());
        retVal.put(PRJEPropertyConstants.TO_ACCOUNT_NBR, getToAccount());
        retVal.put(PRJEPropertyConstants.TO_SUB_ACCT, getToSubAccount());
        retVal.put(PRJEPropertyConstants.TO_OBJECT_CD, getToObjectCode());
        retVal.put(PRJEPropertyConstants.TO_SUB_OBJECT, getToSubObject());
        retVal.put(PRJEPropertyConstants.TO_AMOUNT, getToAmount() == null ? null : getToAmount().toString());
        retVal.put(PRJEPropertyConstants.TO_DEBIT_CREDIT, getToDebitCredit());
                
        return retVal;
    }    
}
