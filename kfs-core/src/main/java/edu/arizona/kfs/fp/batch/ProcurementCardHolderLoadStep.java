package edu.arizona.kfs.fp.batch;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.context.SpringContext;

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

    private ProcurementCardHolderLoadService procurementCardHolderLoadService;
    private BatchInputFileService batchInputFileService;
    private BatchInputFileType procurementCardHolderInputFileType;

    public BatchInputFileService getBatchInputFileService() {
        if (batchInputFileService == null) {
            batchInputFileService = SpringContext.getBean(BatchInputFileService.class);
        }
        return batchInputFileService;
    }

    /**
     * Controls the procurement cardholder load process.
     */
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        procurementCardHolderLoadService.cleanTransactionsTable();

        List<String> fileNamesToLoad = getBatchInputFileService().listInputFileNamesWithDoneFile(procurementCardHolderInputFileType);

        boolean processSuccess = true;
        List<String> processedFiles = new ArrayList<String>();
        for (String inputFileName : fileNamesToLoad) {
            processSuccess = procurementCardHolderLoadService.loadProcurementCardHolderFile(inputFileName);
            if (processSuccess) {
                processedFiles.add(inputFileName);
            }
        }

        removeDoneFiles(processedFiles);

        return processSuccess;
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
     * Sets the procurementCardHolderInputFileType attribute value.
     */
    public void setProcurementCardHolderInputFileType(BatchInputFileType procurementCardHolderInputFileType) {
        this.procurementCardHolderInputFileType = procurementCardHolderInputFileType;
    }

    /**
     * Sets the procurementCardHolderLoadService attribute value.
     */
    public void setProcurementCardHolderLoadService(ProcurementCardHolderLoadService procurementCardHolderLoadService) {
        this.procurementCardHolderLoadService = procurementCardHolderLoadService;
    }
}
