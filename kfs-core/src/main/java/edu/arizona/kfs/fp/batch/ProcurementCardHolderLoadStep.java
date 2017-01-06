package edu.arizona.kfs.fp.batch;

import java.io.File;
import java.sql.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;

import edu.arizona.kfs.fp.batch.service.ProcurementCardHolderLoadService;

/**
 * This step will call a service method to load the procurement cardholder xml file into the transaction table.
 * Validates the data before the load. Functions performed by this step:
 * 1) Lookup path and filename from APC for the procurement cardholder input file
 * 2) Load the procurement cardholder xml file
 * 3) Parse each holder and validate against the data dictionary
 * 4) Clean FP_PRCRMNT_CARD_HLDR_LD_T from the previous run
 * 5) Load new transactions into FP_PRCRMNT_CARD_HLDR_LD_T
 * 6) Rename input file using the current date (backup) RESTART: All functions performed within a single
 * transaction. Step can be restarted as needed.
 */
public class ProcurementCardHolderLoadStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardHolderLoadStep.class);

    private ProcurementCardHolderLoadService procurementCardHolderLoadService;
    private BatchInputFileService batchInputFileService;
    private BatchInputFileType procurementCardHolderInputFileType;
    
    /**
     * Controls the procurement cardholder load process.
     */
    public boolean execute(String jobName, java.util.Date jobRunDate) throws InterruptedException {
        procurementCardHolderLoadService.cleanTransactionsTable();

        List<String> fileNamesToLoad = batchInputFileService.listInputFileNamesWithDoneFile(procurementCardHolderInputFileType);

        //process only most recent file set to prevent duplicates
        Map<Date, String> fileNameMap = buildMapForFileNameList(fileNamesToLoad);
        Date holdDate = null;
        for (Iterator<Date> iter2 = fileNameMap.keySet().iterator(); iter2.hasNext();) {
            Date fileDate = (Date) iter2.next();
            if (holdDate == null || holdDate.compareTo(fileDate) < 0) {
                holdDate = fileDate;
            }
        }
        String holdDateString = (String) fileNameMap.get(holdDate);
        
        boolean processSuccess = true;

        for (String inputFileName : fileNamesToLoad) {
            if (inputFileName.contains(holdDateString)) {
                // Removing related .done file right away
                removeDoneFile(inputFileName);
                LOG.info("Loading Procurement Cardholder file " + new File(inputFileName).getName());
                processSuccess = procurementCardHolderLoadService.loadProcurementCardHolderFile(inputFileName);
            }
        }
        //remove any leftover done files
        removeDoneFiles(fileNamesToLoad);
        return processSuccess;
    }
    
    public Map<Date, String> buildMapForFileNameList(List<String> fileNamesToLoad) {
        Map<Date, String> fileNameMap = new HashMap<Date, String>();
        File inputFile = null;
        String shortFileName = null;
        
        for (String inputFileName : fileNamesToLoad) {
            inputFile = new File(inputFileName);
            shortFileName = inputFile.getName();
            // place file date values in map
            fileNameMap.put(Date.valueOf(shortFileName.substring(10, 20)), shortFileName.substring(10, 20));
        }
        return fileNameMap;
    }
    
    /**
     * Clears out associated .done files for the processed data files.
     */
    private void removeDoneFiles(List<String> dataFileNames) {
        for (String dataFileName : dataFileNames) {
            File doneFile = new File(StringUtils.substringBeforeLast(dataFileName, ".") + ".done");
            if (doneFile.exists()) {
                doneFile.delete();
            }
        }
    }
    
    /**
     * Clears out the associated .done file for the data file about to be processed.
     * 
     * @param dataFileName the name of date file with done file to remove
     */
    protected void removeDoneFile(String dataFileName) {
        File doneFile = new File(StringUtils.substringBeforeLast(dataFileName, ".") + ".done");
        if (doneFile.exists()) {
            doneFile.delete();
        }
    }
    
    public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
        this.batchInputFileService = batchInputFileService;
    }

    public void setProcurementCardHolderInputFileType(BatchInputFileType procurementCardHolderInputFileType) {
        this.procurementCardHolderInputFileType = procurementCardHolderInputFileType;
    }

    public void setProcurementCardHolderLoadService(ProcurementCardHolderLoadService procurementCardHolderLoadService) {
        this.procurementCardHolderLoadService = procurementCardHolderLoadService;
    }
}
