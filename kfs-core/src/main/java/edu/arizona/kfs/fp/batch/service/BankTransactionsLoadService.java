package edu.arizona.kfs.fp.batch.service;

import edu.arizona.kfs.fp.businessobject.BankTransaction;
import edu.arizona.kfs.fp.businessobject.BankTransactionsFileInfo;

import java.util.List;

/**
 * This service provides methods to load and validate Bank Transaction batch files (so-called tfiles) for the Document Creation batch job.
 *
 */
public interface BankTransactionsLoadService {

    /**
     * Validates and parses all tfiles received from the banks in the batch staging area.
     * If there are any errors, they are written to Tfile_error.report.
     *
     * @return True if there are files with no errors, false otherwise.
     */
    public boolean validateBankTransactionFiles();


    /**
     * Validate the given BankTransaction
     *
     * @return list of errors, if any found.
     */
    public List<String> validateBankTransaction(BankTransaction bankTransaction);


    /**
     * Validate the given BankTransactionsFileInfo that it's not a duplicate upload, the batch total should match the computed total
     *
     * @return list of errors, if any found.
     */
    public List<String> validateBankTransactionsFileInfo( BankTransactionsFileInfo fileInfo );


    /**
     * Consolidates all the data accross multiple bank transaction files in one file.
     *
     * @return True if the file consolidation was successful, false otherwise.
     */
    public boolean consolidateBankTransactionFiles();


    /**
     * Reads each transactions in the consolidated bank transaction file and uses the TransactionPostingService
     * to create the appropriate document for it:
     * Check recon file, DI, AD and CCR
     *
     * Files are marked as processed at the end if there were no errors.
     * If there are any errors, they are logged, batch processing is stopped and everything is rolled back and the file is not marked as uploaded.
     *
     * @return True if all transactions were posted with no errors, false otherwise.
     */
    public boolean postTransactionsFromBankFile();


}
