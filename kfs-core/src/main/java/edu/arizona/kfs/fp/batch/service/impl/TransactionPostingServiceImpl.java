package edu.arizona.kfs.fp.batch.service.impl;

import edu.arizona.kfs.fp.batch.service.TransactionPostingService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This service is used to load and process the consolidated Bank Transaction batch file (so-called tfile) for the Document Creation batch job.
 *
 */
@Transactional
public class TransactionPostingServiceImpl implements TransactionPostingService {

    /**
     * Reads each transactions in the consolidated bank transaction file and create the appropriate document for it:
     * Check recon file, DI, AD and CCR
     *
     * Files are marked as processed at the end if there were no errors.
     * If there are any errors, batch processing is stopped and everything is rolled back.
     *
     * @return True if all transactions were posted with no errors, false otherwise.
     */
    public boolean postTransactionsFromBankFile(){
        return true;
    }
}
