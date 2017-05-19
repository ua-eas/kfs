package edu.arizona.kfs.fp.batch;

import edu.arizona.kfs.fp.batch.service.BankTransactionsLoadService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This step will call a service method to load the procurement cardholder xml file into the transaction table.
 */
public class DocumentCreationStep extends AbstractStep {

    private BankTransactionsLoadService bankTransactionsLoadService;
    
    /**
     * Controls the procurement cardholder load process.
     */
    @Override
    public boolean execute(String jobName, java.util.Date jobRunDate) throws InterruptedException {
        //TODO make sure it's necessary to switch to SYSTEM_USER??
        GlobalVariables.clear();
        UserSession systemUser = new UserSession(KFSConstants.SYSTEM_USER);
        GlobalVariables.setUserSession(systemUser);
        systemUser.setBackdoorUser(KFSConstants.SYSTEM_USER);

        return bankTransactionsLoadService.postTransactionsFromBankFile();
    }

    public void setBankTransactionsLoadService(BankTransactionsLoadService bankTransactionsLoadService) {
        this.bankTransactionsLoadService = bankTransactionsLoadService;
    }
}
