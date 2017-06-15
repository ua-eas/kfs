package edu.arizona.kfs.module.prje.businessobject;

import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import edu.arizona.kfs.module.prje.businessobject.PRJEAccountLine;
import edu.arizona.kfs.module.prje.businessobject.PRJEBaseAccount;

/**
 * Encapsulates Withdrawal and Deposit data for PRJE Transactions.  Used as
 * a common representation of transfer data between the PRJE Service and
 * Reporting system.
 */
public class PRJETransferRecord {
    // Variables assigned in the constructor
    private PRJEBaseAccount baseAccount;
    private PRJEAccountLine accountLine;
    
    // Variables assigned over the life of the record 
    private KualiDecimal balance = new KualiDecimal(0.0);
    private KualiDecimal amount = new KualiDecimal(0.0);
    private OriginEntryFull debitEntry;
    private OriginEntryFull creditEntry;
    
    public PRJETransferRecord(PRJEBaseAccount baseAccount, PRJEAccountLine accountLine) {
        this.baseAccount = baseAccount;
        this.accountLine = accountLine;
    }
    
    public PRJEBaseAccount getBaseAccount() {
        return baseAccount;
    }

    public void setBaseAccount(PRJEBaseAccount baseAccount) {
        this.baseAccount = baseAccount;
    }

    public PRJEAccountLine getAccountLine() {
        return accountLine;
    }

    public void setAccountLine(PRJEAccountLine accountLine) {
        this.accountLine = accountLine;
    }

    public KualiDecimal getBalance() {
        return balance;
    }

    public void setBalance(KualiDecimal balance) {
        this.balance = balance;
    }
    
    public KualiDecimal getAmount() {
        return amount;
    }

    public void setAmount(KualiDecimal amount) {
        this.amount = amount;
    }

    public OriginEntryFull getDebitEntry() {
        return debitEntry;
    }

    public void setDebitEntry(OriginEntryFull debitEntry) {
        this.debitEntry = debitEntry;
    }

    public OriginEntryFull getCreditEntry() {
        return creditEntry;
    }

    public void setCreditEntry(OriginEntryFull creditEntry) {
        this.creditEntry = creditEntry;
    }
}
