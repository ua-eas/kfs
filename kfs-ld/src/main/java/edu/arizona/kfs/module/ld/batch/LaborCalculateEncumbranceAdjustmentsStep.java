package edu.arizona.kfs.module.ld.batch;

import java.io.File;
import java.util.Date;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.springframework.util.StopWatch;

import edu.arizona.kfs.module.ld.batch.service.LaborEncumbranceAdjustmentService;

/**
 * KITT-933 / FP-INT0008-01 - Batch step to create the encumbrance adjustment file. 
 * UAF-4010 MOD-FP0008-01 Accounting for Personnel Encumbrances - Code Feature
 * 
 * @author Jonathan Keller
 */
public class LaborCalculateEncumbranceAdjustmentsStep extends AbstractStep {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborCalculateEncumbranceAdjustmentsStep.class);

    protected String batchFileDirectoryName;
    protected LaborEncumbranceAdjustmentService laborEncumbranceAdjustmentService;
    protected DateTimeService dateTimeService;

    public boolean execute(String jobName, Date jobRunDate) {
        StopWatch stopWatch = null;
        if ( LOG.isDebugEnabled() ) {
            stopWatch = new StopWatch();
            stopWatch.start();
        }

        String inputFileName = batchFileDirectoryName + File.separator + LaborEncumbranceAdjustmentService.SORTED_ENCUMBRANCE_INPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        File inputFile = new File( inputFileName );
        String balanceFileName = batchFileDirectoryName + File.separator + LaborEncumbranceAdjustmentService.ENCUMBRANCE_BALANCE_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        File balanceFile = new File( balanceFileName );
        if ( inputFile.exists() && balanceFile.exists() ) {
            String outputFileName = batchFileDirectoryName + File.separator + LaborEncumbranceAdjustmentService.ENCUMBRANCE_OUTPUT_FILE + "-" + dateTimeService.toDateTimeStringForFilename(jobRunDate) + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
            File outputFile = new File( outputFileName );
            String errorFileName = batchFileDirectoryName + File.separator + LaborEncumbranceAdjustmentService.ENCUMBRANCE_ERROR_FILE + "-" + dateTimeService.toDateTimeStringForFilename(jobRunDate) + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
            File errorFile = new File( errorFileName );
            String reconFileName =  batchFileDirectoryName + File.separator + LaborEncumbranceAdjustmentService.ENCUMBRANCE_OUTPUT_FILE + "-" + dateTimeService.toDateTimeStringForFilename(jobRunDate) + edu.arizona.kfs.gl.GeneralLedgerConstants.BatchFileSystem.RECON_FILE_EXTENSION;
            File reconFile = new File( reconFileName );
            
            getLaborEncumbranceAdjustmentService().buildEncumbranceDifferenceFile(inputFile,balanceFile,outputFile,errorFile,reconFile);
            
            // delete the input files (neither were the original input files, but are temp files, so this is safe)
            inputFile.delete();
            balanceFile.delete();
        }                    
        if (LOG.isDebugEnabled()) {
            stopWatch.stop();
            LOG.debug(getClass().getName()+" step of " + jobName + " took " + (stopWatch.getTotalTimeSeconds() / 60.0) + " minutes to complete");
        }
        return true;
    }

    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }

    protected LaborEncumbranceAdjustmentService getLaborEncumbranceAdjustmentService() {
        return laborEncumbranceAdjustmentService;
    }

    public void setLaborEncumbranceAdjustmentService(LaborEncumbranceAdjustmentService laborEncumbranceAdjustmentService) {
        this.laborEncumbranceAdjustmentService = laborEncumbranceAdjustmentService;
    }
    protected DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

}
