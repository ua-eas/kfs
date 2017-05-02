package edu.arizona.kfs.module.ld.batch;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.springframework.util.StopWatch;

import edu.arizona.kfs.module.ld.batch.service.LaborEncumbranceAdjustmentService;

/**
 * KITT-933 / FP-INT0008-01 - Builds a balance file from the current encumbrance balances in the labor ledger.  
 * 
 * @author Jonathan Keller
 */
public class LaborBuildEncumbranceBalanceFileStep extends AbstractStep {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborBuildEncumbranceBalanceFileStep.class);

    protected String batchFileDirectoryName;
    protected LaborEncumbranceAdjustmentService laborEncumbranceAdjustmentService;
    
    
    public boolean execute(String jobName, Date jobRunDate) {
        StopWatch stopWatch = null;
        if ( LOG.isDebugEnabled() ) {
            stopWatch = new StopWatch();
            stopWatch.start();
        }

        File inputFile = new File( batchFileDirectoryName + File.separator + LaborEncumbranceAdjustmentService.SORTED_ENCUMBRANCE_INPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION );
        if ( inputFile.exists() ) {
            File outputFile = new File( batchFileDirectoryName + File.separator + LaborEncumbranceAdjustmentService.ENCUMBRANCE_BALANCE_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION );
            Integer fiscalYear = null;
            
            // get fiscal year from data file
            // this assumes that there is only one fiscal year per file
            LineIterator inputEntries = null;
            try {
                inputEntries = FileUtils.lineIterator(inputFile);
                if ( inputEntries.hasNext() ) {
                    String firstLine = inputEntries.nextLine();
                    LaborOriginEntry oe = new LaborOriginEntry(firstLine); 
                    fiscalYear = oe.getUniversityFiscalYear();
                }
            } catch (IOException ex) {
                LOG.error("Error encountered trying to read first line of labor encumbrance file", ex);
                throw new RuntimeException("Error encountered trying to read first line of labor encumbrance file", ex);
            } finally {
                LineIterator.closeQuietly(inputEntries);
            }    
            // only build the file if the FY was non-null - otherwise there was no file
            // or a problem with the file - later steps will catch the problem
            if ( fiscalYear != null ) {
                getLaborEncumbranceAdjustmentService().buildBalanceFile(fiscalYear, outputFile);
            }
        }                    
        if (LOG.isDebugEnabled()) {
            stopWatch.stop();
            LOG.debug(getClass().getName() + " took " + (stopWatch.getTotalTimeSeconds() / 60.0) + " minutes to complete");
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

    public String getBatchFileDirectoryName() {
        return batchFileDirectoryName;
    }
}
