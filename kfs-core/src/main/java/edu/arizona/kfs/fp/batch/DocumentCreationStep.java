package edu.arizona.kfs.fp.batch;

import edu.arizona.kfs.fp.batch.service.TransactionPostingService;
import org.kuali.kfs.sys.batch.AbstractStep;

/**
 * This step will call a service method to load the procurement cardholder xml file into the transaction table.
 */
public class DocumentCreationStep extends AbstractStep {

    private TransactionPostingService transactionPostingService;
    
    /**
     * Controls the procurement cardholder load process.
     */
    @Override
    public boolean execute(String jobName, java.util.Date jobRunDate) throws InterruptedException {
        return transactionPostingService.postTransactionsFromBankFile();
    }

    public void setTransactionPostingService(TransactionPostingService transactionPostingService) {
        this.transactionPostingService = transactionPostingService;
    }
}
