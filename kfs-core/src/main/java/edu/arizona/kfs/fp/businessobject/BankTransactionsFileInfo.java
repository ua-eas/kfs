package edu.arizona.kfs.fp.businessobject;


import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.sql.Date;


/**
 * Non persistable POJO that encapsulates the meta data about a Bank Transactions file,
 * Used by BankTransactionLoad Service and TransactionPosting Service for Document Creation Job
 *
 * Created by nataliac on 4/17/17.
 */
public class BankTransactionsFileInfo {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BankTransactionsFileInfo.class);

    private String fileName;
    private int transactionCount;
    private KualiDecimal batchTotal = KualiDecimal.ZERO;
    private KualiDecimal transactionsTotal = KualiDecimal.ZERO;
    private Date postingDate;

    public BankTransactionsFileInfo(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return  "FileName:" + fileName +  " - " +
                "TransactionCount:" + transactionCount + " - " +
                "BatchTotal: " + batchTotal.toString() + " - " +
                "TransactionsTotal:" + transactionsTotal.toString() + " - " +
                "PostingDate:" + postingDate + " . " ;
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(int transactionCount) {
        this.transactionCount = transactionCount;
    }

    public KualiDecimal getBatchTotal() {
        return batchTotal;
    }

    public void setBatchTotal(KualiDecimal batchTotal) {
        this.batchTotal = batchTotal;
    }

    public KualiDecimal getTransactionsTotal() {
        return transactionsTotal;
    }

    public void setTransactionsTotal(KualiDecimal transactionsTotal) {
        this.transactionsTotal = transactionsTotal;
    }

    public Date getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(Date postingDate) {
        this.postingDate = postingDate;
    }
}
