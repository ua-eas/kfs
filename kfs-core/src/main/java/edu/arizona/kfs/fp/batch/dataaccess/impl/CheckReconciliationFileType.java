package edu.arizona.kfs.fp.batch.dataaccess.impl;

import edu.arizona.kfs.fp.batch.service.BankParametersAccessService;
import edu.arizona.kfs.fp.batch.service.BankTransactionsLoadService;
import edu.arizona.kfs.fp.businessobject.BankTransaction;
import edu.arizona.kfs.sys.KFSConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.batch.BatchInputFileTypeBase;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.rice.core.api.datetime.DateTimeService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Check Recon output created by the DocumentCreationJob from the Check Reconciliation Records in the Bank Transaction File
 * <p>
 * Created by nataliac on 3/10/17.
 */
public class CheckReconciliationFileType extends BatchInputFileTypeBase {
    private static final Logger LOG = Logger.getLogger(CheckReconciliationFileType.class);

    protected String fileName;

    protected DateTimeService dateTimeService;
    protected BankParametersAccessService bankParametersAccessService;


    public CheckReconciliationFileType() {
    }

    public List<String> writeRecord(BankTransaction bankTransaction){
        List<String> errorList = new ArrayList<String>();
        // create or open the output file
        File checkReconFile = getCheckReconFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(checkReconFile,true))) {
            writer.append(getCheckReconciliationRow(bankTransaction));
            writer.flush();
        } catch (IOException e) {
            LOG.error("An IOException occurred while writing transaction "+bankTransaction.toString()+" to the Check Reconciliation File:" + getAbsoulutePath(), e);
            errorList.add("An IOException occurred while writing transaction "+bankTransaction.toString()+" to the Check Reconciliation File:" + getAbsoulutePath()+e.getMessage());
        } catch (Exception e) {
            LOG.error("Exception occurred while writing transaction  "+bankTransaction.toString()+" to the Check Reconciliation File: " + getAbsoulutePath(), e);
            errorList.add("An Exception occurred while writing transaction "+bankTransaction.toString()+" to the Check Reconciliation File:" + getAbsoulutePath()+e.getMessage());
        }

        return errorList;
    }


    public String getFileName() {
        if (StringUtils.isEmpty(fileName)) {
            //KFS3 name format: dateTimeService.getCurrentCalendar().getTimeInMillis();
            fileName = "bank_recon_input" + getDateTimeService().toDateTimeStringForFilename(getDateTimeService().getCurrentDate());
        }

        return fileName;
    }

    /**
     * This method returns the Check Reconciliation record from a BankTransaction
     *
     * @return
     */
    public String getCheckReconciliationRow(BankTransaction bankTransaction) {
        String[] checkReconFields = CheckReconciliationAdapter.buildCheckReconRecord(bankTransaction, getBankParametersAccessService().getFieldPositions());
        StringBuffer checkReconRow = new StringBuffer();
        char delimiter = getBankParametersAccessService().getBankFileDelimiter();
        for (int i = 0; i < checkReconFields.length; i++) {
            checkReconRow.append(checkReconFields[i]);
            checkReconRow.append(delimiter);
        }
        checkReconRow.append( getBankParametersAccessService().getCheckReconClearedStatusCode());
        checkReconRow.append("\n");
        LOG.debug("getCheckReconciliationRow for transaction="+bankTransaction.toString()+" is="+checkReconRow.toString());
        return checkReconRow.toString();
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileName
     */
    @Override
    public String getFileName(String principalName, Object parsedFileContents, String fileUserIdentifer) {
        return getFileName();
    }

    public void resetFileNameTimestamp(){
        fileName = null;
    }


    /**
     * @see org.kuali.kfs.sys.batch.CsvBatchInputFileTypeBase#parse(byte[])
     */
    @Override
    public Object parse(byte[] fileByteContent) throws ParseException {
        return null;
    }

    /**
     * Formally overrides super class implementation to validate parsed content
     *
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#validate(Object)
     */
    @Override
    public boolean validate(Object parsedFileContents) {
        return true;
    }




    @Override
    public void process(String fileName, Object parsedFileContents) {
    }

    /**
     *  Creates a matching .done file for the check recon file, if one exists.
     *  The .done file should only be created when the check recon file is complete.
     */
    public void createCheckReconDoneFile() {
        File checkReconFile = new File(getAbsoulutePath());
        if (checkReconFile.exists()) {
            File doneFile = null;
            try {

                doneFile = new File(getDoneFilePath(checkReconFile));
                doneFile.createNewFile();

            } catch (IOException e) {
                LOG.error("Check reconciliation .DONE file " + doneFile.getAbsolutePath() + " could not be created. ABORTING.");
                throw new RuntimeException("Check reconciliation .DONE file " + doneFile.getAbsolutePath() + " could not be created. ABORTING.");
            }
        }
    }

    /**
     * Creates and opens the file on the output path. the .done corresponding to the given file, if one exists.
     */
    protected File getCheckReconFile() {
        File checkReconFile = new File(getAbsoulutePath());
        if ( !checkReconFile.exists() ) {
            try {
                checkReconFile.createNewFile();
                File doneFile = new File(getDoneFilePath(checkReconFile));
                doneFile.createNewFile();
                if (!checkReconFile.canWrite()) {
                    LOG.error("Check reconciliation file " + getAbsoulutePath() + " cannot be opened for writing. ABORTING.");
                    throw new RuntimeException("Check reconciliation file " + getAbsoulutePath() + " cannot be opened for writing. ABORTING.");
                }
            } catch (IOException e) {
                LOG.error("Check reconciliation file " + getAbsoulutePath() + " could not be created. ABORTING.");
                throw new RuntimeException("Check reconciliation file " + getAbsoulutePath() + " could not be created. ABORTING.");
            }
        }
        return checkReconFile;
    }


    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileTypeIdentifer()
     */
    @Override
    public String getFileTypeIdentifer() {
        return KFSConstants.BankTransactionConstants.CHECK_RECON_FILE_TYPE_IDENTIFIER;
    }

    /**
     * Implementation from KFS3
     *
     * @param file
     * @return
     */
    @Override
    public String getAuthorPrincipalName(File file) {
        String[] fileNameParts = StringUtils.split(file.getName(), "_");
        if (fileNameParts.length >= 2) {
            return fileNameParts[1];
        }
        return null;
    }

    public String getAbsoulutePath() {
        return getDirectoryPath() + File.separator + getFileName() + KFSConstants.DOT_CHAR + getFileExtension();
    }

    public String getDoneFilePath(File reconFile) {
        String doneFileName = FilenameUtils.getBaseName(reconFile.getName());
        return getDirectoryPath() + File.separator + doneFileName + KFSConstants.DONE_FILE_EXTENSION;
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputType#getTitleKey()
     */
    @Override
    public String getTitleKey() {
        return KFSKeyConstants.MESSAGE_BATCH_UPLOAD_TITLE_BANK_TRANSACTIONS;
    }


    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public BankParametersAccessService getBankParametersAccessService() {
        return bankParametersAccessService;
    }

    public void setBankParametersAccessService(BankParametersAccessService bankParametersAccessService) {
        this.bankParametersAccessService = bankParametersAccessService;
    }


}
