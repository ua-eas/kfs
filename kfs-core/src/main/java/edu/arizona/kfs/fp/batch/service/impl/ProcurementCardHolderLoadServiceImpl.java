package edu.arizona.kfs.fp.batch.service.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.rice.krad.service.BusinessObjectService;

import edu.arizona.kfs.fp.batch.service.ProcurementCardHolderLoadService;
import edu.arizona.kfs.fp.businessobject.ProcurementCardHolderLoad;

/**
 * This is the default implementation of the ProcurementCardHolderLoadService interface.
 * Handles loading, parsing, and storing of incoming procurement cardholder batch files.
 */
public class ProcurementCardHolderLoadServiceImpl implements ProcurementCardHolderLoadService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardHolderLoadServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private BatchInputFileService batchInputFileService;
    private BatchInputFileType procurementCardHolderInputFileType;

    public BusinessObjectService getBusinessObjectService() {
        if (businessObjectService == null) {
            businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        }
        return businessObjectService;
    }

    public BatchInputFileService getBatchInputFileService() {
        if (batchInputFileService == null) {
            batchInputFileService = SpringContext.getBean(BatchInputFileService.class);
        }
        return batchInputFileService;
    }

    /**
     * Calls businessObjectService to remove all the procurement cardholder rows from the load table.
     */
    public void cleanTransactionsTable() {
        getBusinessObjectService().deleteMatching(ProcurementCardHolderLoad.class, new HashMap());
    }

    /**
     * Validates and parses the given file, then stores procurement cardholders in a temp table.
     * 
     * @param fileName
     *            The name of the file to be parsed.
     * @return This method always returns true. An exception is thrown if a problem occurs while loading the file.
     */
    public boolean loadProcurementCardHolderFile(String fileName) {
        FileInputStream fileContents;
        try {
            fileContents = new FileInputStream(fileName);
        } catch (FileNotFoundException e1) {
            LOG.error("file to parse not found " + fileName, e1);
            throw new RuntimeException("Cannot find the file requested to be parsed " + fileName + " " + e1.getMessage(), e1);
        }

        Collection pcardHolders = null;
        try {
            byte[] fileByteContent = IOUtils.toByteArray(fileContents);
            pcardHolders = (Collection) getBatchInputFileService().parse(procurementCardHolderInputFileType, fileByteContent);
        } catch (IOException e) {
            LOG.error("error while getting file bytes:  " + e.getMessage(), e);
            throw new RuntimeException("Error encountered while attempting to get file bytes: " + e.getMessage(), e);
        } catch (ParseException e) {
            LOG.error("Error parsing xml " + e.getMessage());
            throw new RuntimeException("Error parsing xml " + e.getMessage(), e);
        }

        if (pcardHolders == null || pcardHolders.isEmpty()) {
            LOG.warn("No PCard Holders in input file " + fileName);
        }

        loadTransactions((List) pcardHolders);

        LOG.info("Total holders loaded: " + Integer.toString(pcardHolders.size()));
        return true;
    }

    /**
     * Loads all the parsed XML holders into the temp holder table.
     * 
     * @param holders
     *            List of Procurement Cardholders to load.
     */
    protected void loadTransactions(List holders) {
        getBusinessObjectService().save(holders);
    }

    /**
     * Sets the procurementCardHolderInputFileType attribute value.
     * 
     * @param procurementCardHolderInputFileType
     *            The procurementCardHolderInputFileType to set.
     */
    public void setProcurementCardHolderInputFileType(BatchInputFileType procurementCardHolderInputFileType) {
        this.procurementCardHolderInputFileType = procurementCardHolderInputFileType;
    }
}
