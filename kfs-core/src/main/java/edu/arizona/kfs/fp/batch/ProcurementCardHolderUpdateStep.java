package edu.arizona.kfs.fp.batch;

import java.util.Date;

import org.kuali.kfs.sys.batch.AbstractStep;

import edu.arizona.kfs.fp.batch.service.ProcurementCardHolderUpdateService;

/**
 * This step will call a service method to insert/update the procurement card holder records from the loaded procurement card holder table.
 */
public class ProcurementCardHolderUpdateStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardHolderUpdateStep.class);
    private ProcurementCardHolderUpdateService procurementCardHolderUpdateService;

    /**
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String, java.util.Date)
     */
    public boolean execute(String jobName, Date jobRunDate) {
        return procurementCardHolderUpdateService.updateProcurementCardHolderRecords();
    }

    /**
     * @param procurementCardHolderUpdateService
     *            The procurementCardHolderUpdateService to set.
     */
    public void setProcurementCardHolderUpdateService(ProcurementCardHolderUpdateService procurementCardHolderUpdateService) {
        this.procurementCardHolderUpdateService = procurementCardHolderUpdateService;
    }
}
