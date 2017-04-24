package edu.arizona.kfs.fp.batch.dataaccess;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.fp.businessobject.CreditCardVendor;
import org.kuali.kfs.sys.businessobject.Bank;

import java.sql.Timestamp;


public interface TransactionPostingDao {
    public CreditCardVendor getCreditCardVendorObject(String vendorNumber);


    public Bank getBankObjectByAccountNumber(String accountNumber);


    public boolean isExisting(String accountNumber);


    public Bank getBankObjectByBankCode(String bankCode);


    public ObjectCode getObjectTpeCodeByObjectCode(String objectCodeCarrier);


    public boolean isDuplicateBatch(String tfileName, Timestamp timestampBatchDate, String bankTransactionBatchName);
}
