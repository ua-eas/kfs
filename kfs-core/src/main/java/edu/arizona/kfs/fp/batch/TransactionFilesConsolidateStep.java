package edu.arizona.kfs.fp.batch;

import edu.arizona.kfs.fp.batch.service.BankTransactionsLoadService;
import org.kuali.kfs.sys.batch.AbstractStep;

/**
 * This step will look at all the transaction files uploaded by the bank(s) and consolidate them into one
 */
public class TransactionFilesConsolidateStep extends AbstractStep {

    private BankTransactionsLoadService bankTransactionsLoadService;
    

    @Override
    public boolean execute(String jobName, java.util.Date jobRunDate) throws InterruptedException {
        return bankTransactionsLoadService.consolidateBankTransactionFiles();
    }

    public void setBankTransactionsLoadService(BankTransactionsLoadService bankTransactionsLoadService) {
        this.bankTransactionsLoadService = bankTransactionsLoadService;
    }
}
