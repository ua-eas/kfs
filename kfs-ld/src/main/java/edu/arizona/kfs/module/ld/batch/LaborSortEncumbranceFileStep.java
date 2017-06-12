package edu.arizona.kfs.module.ld.batch;

import java.io.File;
import java.util.Date;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.BatchSortUtil;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.springframework.util.StopWatch;

import edu.arizona.kfs.module.ld.batch.service.LaborEncumbranceAdjustmentService;

/**
 * Sorts the incoming encumbrance balance file from HCM.
 * 
 * @author jonathan
 */
public class LaborSortEncumbranceFileStep extends AbstractStep {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborSortEncumbranceFileStep.class);
    protected String batchFileDirectoryName;
    protected String processedBatchFileDirectoryName;
    protected DateTimeService dateTimeService;

    /**
     * Sorts the incoming encumbrance balance file from HCM.
     * 
     * @param jobName the name of the job this step is being run as part of
     * @param jobRunDate the time/date the job was started
     * @return true if the job completed successfully, false if otherwise
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String)
     */
    public boolean execute(String jobName, Date jobRunDate) {
        StopWatch stopWatch = null;
        boolean succeeded = true;
        if (LOG.isDebugEnabled()) {
            stopWatch = new StopWatch();
            stopWatch.start(jobName);
        }
        File processedDirectory = new File( processedBatchFileDirectoryName );
        if ( !processedDirectory.exists() ) {
            processedDirectory.mkdir();
        }
        String inputFileName = batchFileDirectoryName + File.separator + LaborEncumbranceAdjustmentService.ENCUMBRANCE_INPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        File inputFile = new File( inputFileName );
        // get the done file
        String doneFileName = batchFileDirectoryName + File.separator + LaborEncumbranceAdjustmentService.ENCUMBRANCE_INPUT_FILE + GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION;
        File doneFile = new File( doneFileName );
        String outputFileName = batchFileDirectoryName + File.separator + LaborEncumbranceAdjustmentService.SORTED_ENCUMBRANCE_INPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        if ( inputFile.exists() && doneFile.exists() ) {
            if ( LOG.isDebugEnabled() ) {
                LOG.debug( "Sorting: \n   " + inputFileName + "\nto\n   " + outputFileName);
            }
            BatchSortUtil.sortTextFileWithFields(inputFileName, outputFileName, new LaborEncumbranceBalanceOriginEntrySortComparator());
            inputFile.setLastModified(jobRunDate.getTime());
            inputFile.renameTo(new File( processedDirectory, LaborEncumbranceAdjustmentService.ENCUMBRANCE_INPUT_FILE + "-" + dateTimeService.toDateTimeStringForFilename(jobRunDate) + GeneralLedgerConstants.BatchFileSystem.EXTENSION ) );
            doneFile.delete();
        } else {
            if ( inputFile.exists() && !doneFile.exists() ) {
                LOG.warn( "Done file for " + inputFileName + " not present - assuming file is not ready." );
            } else {
                LOG.warn( "Unable to find " + inputFileName + ", sorting skipped.  No output file created." );
            }
            succeeded = false;
        }
        if (LOG.isDebugEnabled()) {
            stopWatch.stop();
            LOG.debug(this.getClass().getName() + " of " + jobName + " took " + (stopWatch.getTotalTimeSeconds() / 60.0) + " minutes to complete");
        }
        return succeeded;
    }
   
    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }

    public String getBatchFileDirectoryName() {
        return batchFileDirectoryName;
    }

    public String getProcessedBatchFileDirectoryName() {
        return processedBatchFileDirectoryName;
    }

    public void setProcessedBatchFileDirectoryName(String processedBatchFileDirectoryName) {
        this.processedBatchFileDirectoryName = processedBatchFileDirectoryName;
    }
    protected DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

}
