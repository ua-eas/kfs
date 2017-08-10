package edu.arizona.kfs.fp.batch.dataaccess.impl;

/**
 * Non persistable POJO that holds intermediate, non-validated data from the Bank Transaction File
 *
 * Created by nataliac on 4/6/17.
 */
public class BankTransactionDigesterVO {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BankTransactionDigesterVO.class);

    private String accountNumber;
    private String baiType;
    private String amount;
    private String custRefNo;
    private String valueDate;
    private String bankReference;
    private String descriptiveTxt6;
    private String descriptiveTxt7;

    @Override
    public String toString() {
        return  "Account:" + accountNumber + " - " +
                "BaiType: " + baiType + " - " +
                "Amount:" + amount + " - " +
                "CustRefNo:" + custRefNo + " - " +
                "BankRef:" + bankReference + " - " +
                "Desc6:" + descriptiveTxt6 + " - " +
                "Desc7:" + descriptiveTxt7;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBaiType() {
        return baiType;
    }

    public void setBaiType(String baiType) {
        this.baiType = baiType;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCustRefNo() {
        return custRefNo;
    }

    public void setCustRefNo(String custRefNo) {
        this.custRefNo = custRefNo;
    }

    public String getValueDate() {
        return valueDate;
    }

    public void setValueDate(String valueDate) {
        this.valueDate = valueDate;
    }

    public String getBankReference() {
        return bankReference;
    }

    public void setBankReference(String bankReference) {
        this.bankReference = bankReference;
    }

    public String getDescriptiveTxt6() {
        return descriptiveTxt6;
    }

    public void setDescriptiveTxt6(String descriptiveTxt6) {
        this.descriptiveTxt6 = descriptiveTxt6;
    }

    public String getDescriptiveTxt7() {
        return descriptiveTxt7;
    }

    public void setDescriptiveTxt7(String descriptiveTxt7) {
        this.descriptiveTxt7 = descriptiveTxt7;
    }
}
