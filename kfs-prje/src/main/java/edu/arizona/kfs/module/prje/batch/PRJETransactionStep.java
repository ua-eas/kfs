package edu.arizona.kfs.module.prje.batch;

import java.util.Date;

import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.batch.Step;

import edu.arizona.kfs.module.prje.service.PRJEService;

public class PRJETransactionStep extends AbstractStep implements Step {
    private PRJEService prjeService;
    
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        return getPrjeService().process();
    }

    public PRJEService getPrjeService() {
        return prjeService;
    }

    public void setPrjeService(PRJEService prjeService) {
        this.prjeService = prjeService;
    }
}
