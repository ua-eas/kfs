package edu.arizona.kfs.fp.batch;

import edu.arizona.kfs.fp.batch.service.BankTransactionsLoadService;

import org.kuali.kfs.sys.batch.AbstractStep;

/**
 * This step will call a service method to load the procurement cardholder xml file into the transaction table.
 * Validates the data before the load.
 */
public class TransactionFilesValidationStep extends AbstractStep {

    private BankTransactionsLoadService bankTransactionsLoadService;
    
    /**
     * Validate the bank transaction files.
     */
    @Override
    public boolean execute(String jobName, java.util.Date jobRunDate) throws InterruptedException {
        return bankTransactionsLoadService.validateBankTransactionFiles();
    }

    public void setBankTransactionsLoadService(BankTransactionsLoadService bankTransactionsLoadService) {
        this.bankTransactionsLoadService = bankTransactionsLoadService;
    }

}
