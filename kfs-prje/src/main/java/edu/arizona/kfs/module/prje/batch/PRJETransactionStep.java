package edu.arizona.kfs.module.prje.batch;

import java.util.Date;

import org.kuali.kfs.sys.batch.AbstractStep;

import edu.arizona.kfs.module.prje.service.PRJEService;

public class PRJETransactionStep extends AbstractStep {
    private PRJEService prjeService;
    
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        return prjeService.process();
    }

    public void setPrjeService(PRJEService prjeService) {
        this.prjeService = prjeService;
    }
}
