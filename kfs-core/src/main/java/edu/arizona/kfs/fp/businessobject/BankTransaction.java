package edu.arizona.kfs.fp.businessobject;

import org.kuali.kfs.fp.businessobject.CreditCardVendor;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.sql.Date;

/**
 * Non persistable POJO that encapsulates the data from each transaction in the Bank Transactions file,
 * Used by BankTransactionLoad Service and TransactionPosting Service for Document Creation Job
 * <p>
 * Tech note: KFS3 Former name: TfileObj
 * <p>
 *
 * Created by nataliac on 4/3/17.
 */
public class BankTransaction {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BankTransaction.class);

    private Long accountNumber;
    private Long baiType;
    private KualiDecimal amount = KualiDecimal.ZERO;
    private String custRefNo;
    private Date valueDate;
    private String bankReference;
    private String descriptiveTxt6;
    private String descriptiveTxt7;


    @Override
    public String toString() {
        return  "Account:" + accountNumber.toString() + " - " +
                "BaiType: " + baiType.toString() + " - " +
                "Amount:" + amount.toString() + " - " +
                "CustRefNo:" + custRefNo + " - " +
                "BankRef:" + bankReference + " - " +
                "Desc6:" + descriptiveTxt6 + " - " +
                "Desc7:" + descriptiveTxt7;
    }


    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accountNumber == null) ? 0 : accountNumber.hashCode());
        result = prime * result + ((amount == null) ? 0 : amount.hashCode());
        result = prime * result + ((baiType == null) ? 0 : baiType.hashCode());
        result = prime * result + ((bankReference == null) ? 0 : bankReference.hashCode());
        result = prime * result + ((custRefNo == null) ? 0 : custRefNo.hashCode());
        result = prime * result + ((descriptiveTxt6 == null) ? 0 : descriptiveTxt6.hashCode());
        result = prime * result + ((descriptiveTxt7 == null) ? 0 : descriptiveTxt7.hashCode());
        result = prime * result + ((valueDate == null) ? 0 : valueDate.hashCode());
        return result;
    }


    /**
     * TODO: Do we really need to override Object.equals???
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BankTransaction other = (BankTransaction) obj;
        if (accountNumber == null) {
            if (other.accountNumber != null)
                return false;
        }
        else if (!accountNumber.equals(other.accountNumber))
            return false;
        if (amount == null) {
            if (other.amount != null)
                return false;
        }
        else if (!amount.equals(other.amount))
            return false;
        if (baiType == null) {
            if (other.baiType != null)
                return false;
        }
        else if (!baiType.equals(other.baiType))
            return false;
        if (bankReference == null) {
            if (other.bankReference != null)
                return false;
        }
        else if (!bankReference.equals(other.bankReference))
            return false;
        if (custRefNo == null) {
            if (other.custRefNo != null)
                return false;
        }
        else if (!custRefNo.equals(other.custRefNo))
            return false;
        if (descriptiveTxt6 == null) {
            if (other.descriptiveTxt6 != null)
                return false;
        }
        else if (!descriptiveTxt6.equals(other.descriptiveTxt6))
            return false;
        if (descriptiveTxt7 == null) {
            if (other.descriptiveTxt7 != null)
                return false;
        }
        else if (!descriptiveTxt7.equals(other.descriptiveTxt7))
            return false;
        if (valueDate == null) {
            if (other.valueDate != null)
                return false;
        }
        else if (!valueDate.equals(other.valueDate))
            return false;
        return true;
    }


    public Long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Long getBaiType() {
        return baiType;
    }

    public void setBaiType(Long baiType) {
        this.baiType = baiType;
    }

    public KualiDecimal getAmount() {
        return amount;
    }

    public void setAmount(KualiDecimal amount) {
        this.amount = amount;
    }

    public String getCustRefNo() {
        return custRefNo;
    }

    public void setCustRefNo(String custRefNo) {
        this.custRefNo = custRefNo;
    }

    public Date getValueDate() {
        return valueDate;
    }

    public void setValueDate(Date valueDate) {
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

    public void setDescriptiveTxt6(String descriptiveTxt21) {
        this.descriptiveTxt6 = descriptiveTxt21;
    }

    public String getDescriptiveTxt7() {
        return descriptiveTxt7;
    }

    public void setDescriptiveTxt7(String descriptiveTxt) {
        this.descriptiveTxt7 = descriptiveTxt;
    }

    public String getDescription(){
        return descriptiveTxt6 + descriptiveTxt7;
    }


}
