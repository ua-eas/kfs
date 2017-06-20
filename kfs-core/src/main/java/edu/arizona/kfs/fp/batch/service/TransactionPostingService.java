package edu.arizona.kfs.fp.batch.service;

import edu.arizona.kfs.fp.businessobject.BankTransaction;

import java.sql.Timestamp;
import java.util.List;

/**
 * This service is used to post each individual transaction from the Bank Transaction Validated input file for the Document Creation batch job.
 *
 */
public interface TransactionPostingService {


    /**
     * Posts the given bank transaction and create the appropriate document for it:
     * Check recon file, DI, AD and CCR
     *
     * If there are any errors, they are returned in the errorList
     *
     * @return empty errorList if transaction was posted with no errors
     */
    public List<String> postTransaction(BankTransaction bankTransaction);


    /**
     * Checks in the database in the BatchFileUploads if there were any other files recorded for the date timestampBatchDate
     * with the given name and batchJobName
     *
     * Returns TRUE if any files were already processed for the posting date of this file
     */
    public boolean isDuplicateBatch(String bankTransactionsFileName, Timestamp timestampBatchDate, String bankTransactionBatchName);


    /**
     * If there is an output check recon file, it creates a .done for it and resets the timestamp for the filename.
     *
     */
    public void finalizeCheckRecon();
}
