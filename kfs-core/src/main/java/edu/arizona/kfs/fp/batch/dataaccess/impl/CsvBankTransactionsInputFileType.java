package edu.arizona.kfs.fp.batch.dataaccess.impl;

import au.com.bytecode.opencsv.CSVReader;
import edu.arizona.kfs.fp.batch.dataaccess.TransactionPostingDao;
import edu.arizona.kfs.fp.batch.service.BankParametersAccessService;
import edu.arizona.kfs.fp.batch.service.BankTransactionsLoadService;
import edu.arizona.kfs.fp.businessobject.BankTransaction;
import edu.arizona.kfs.fp.businessobject.BankTransactionsFileInfo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import edu.arizona.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.batch.BatchInputFileTypeBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV Bank Transaction Input File that uses the Bank Parameters to determine order of columns and delimiter
 * <p>
 * Created by nataliac on 3/10/17.
 */
public class CsvBankTransactionsInputFileType extends BatchInputFileTypeBase {
    private static final Logger LOG = Logger.getLogger(CsvBankTransactionsInputFileType.class);

    protected String fileName;
    protected String reportPath;
    protected String reportPrefix;
    protected String reportExtension;
    protected String validatedPath;
    protected String errorFilePath;

    protected BankTransactionsFileInfo bankFileInfo;

    protected char QUOTE_CHAR = '"';


    protected DateTimeService dateTimeService;
    protected BankParametersAccessService bankParametersAccessService;
    protected BankTransactionsLoadService bankTransactionsLoadService;


    public CsvBankTransactionsInputFileType() {
    }



    public void setFileToProcess(String filePath){
        File file = new File(filePath);
        this.fileName = FilenameUtils.removeExtension(file.getName());
        bankFileInfo = new BankTransactionsFileInfo(this.fileName);
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Implementation from KFS3
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileName
     */
    @Override
    public String getFileName(String principalName, Object parsedFileContents, String fileUserIdentifer) {
        String fileName = "bank_" + principalName;
        if (StringUtils.isNotBlank(fileUserIdentifer)) {
            fileName += "_" + fileUserIdentifer;
        }
        fileName += "_" + dateTimeService.toDateTimeStringForFilename(dateTimeService.getCurrentDate());

        // remove spaces in filename
        fileName = StringUtils.remove(fileName, " ");

        return fileName;
    }


    /**
     * Formally overrides super class implementation to specify/convert to the expected data structure
     * Since for Bank Transaction Files, we're not converting the ENTIRE FILE at once, but rather go line by line,
     * this method will not have a concrete implementation...
     *
     * @see org.kuali.kfs.sys.batch.CsvBatchInputFileTypeBase#parse(byte[])
     */
    @Override
    public Object parse(byte[] fileByteContent) throws ParseException {
        return null;
    }

    /**
     * Formally overrides super class implementation to validate parsed content
     *
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#validate(java.lang.Object)
     */
    @Override
    public boolean validate(Object parsedFileContents) {
        return validate();
    }


    /**
     * Method that will validate the Bank Transaction file specified by fileName line by line.
     * Also will generate and validate the Bank Transaction File Info header that contains the batchTotal, batch posting date etc.
     *
     * Any errors will be logged in the [crt_timestamp]Tfile_error.report file
     *
     * @return true - if Bank Transaction file is valid
     * false - if any errors were ancountered.
     */
    public boolean validate() {
        List<String> errorList = null;
        BufferedReader br = null;
        boolean result = true;
        try {

            File inputFile = openFile();
            br = new BufferedReader(new FileReader(inputFile));
            KualiDecimal batchTotal = parseBatchTotal(br.readLine());
            getBankFileInfo().setBatchTotal(batchTotal);
            LOG.debug("File:" + getFileName() + " has batchTotal = " + batchTotal.toString());

            //Skip the first line since it SHOULD either empty or the batchTotal
            int currentLine = 0;
            String[] currentRowData;
            BankTransactionDigesterAdapter btAdapter = new BankTransactionDigesterAdapter();
            CSVReader csvReader = new CSVReader(br, getBankParametersAccessService().getBankFileDelimiter(), QUOTE_CHAR, currentLine);
            //validate each line from the file, that it can be parsed and Bank Transactions obey each rule
            while ((currentRowData = csvReader.readNext()) != null) {
                ++currentLine;
                BankTransactionDigesterVO btDigesterVO = BankTransactionCSVBuilder.buildBankTransactionVO(currentRowData, getBankParametersAccessService().getFieldPositions());

                errorList = new ArrayList<>();
                BankTransaction bankTransaction = btAdapter.convert(btDigesterVO, errorList);
                LOG.debug("Line #" + currentLine + " Converted BankTransaction:" + bankTransaction.toString());
                //validate freshly parsed BankTransaction object
                errorList.addAll(getBankTransactionsLoadService().validateBankTransaction(bankTransaction));
                //if any errors in the conversion or validation, log them to the error file
                if (!errorList.isEmpty()) {
                    LOG.error("ERRORS for Line #" + currentLine + " :" + errorList.toString());
                    errorList.add("ERRORS above found at line #" + currentLine + " in file " + getFileName());
                    result = false;
                    logErrorsToFile(errorList);
                }

                //add current's line absolute value of transaction amount to the computed transactionTotalAmount - even if there were validation errors...
                if (bankTransaction != null && bankTransaction.getAmount() != null) {
                    KualiDecimal transactionsAmountSum = getBankFileInfo().getTransactionsTotal().add(bankTransaction.getAmount().abs());
                    getBankFileInfo().setTransactionsTotal(transactionsAmountSum);
                }

                //set the batch transaction Date as the current line (will ultimately be the date of the last line in the file)
                getBankFileInfo().setPostingDate(bankTransaction.getValueDate());
            }

            //set the total counted lines in the bank file info
            getBankFileInfo().setTransactionCount(currentLine);

            LOG.info("Finished parsing file. Bank Transactions file header = " + getBankFileInfo().toString());

            //after parsing the file, finally validate resulting BankTransactionFileInfo
            errorList.addAll(getBankTransactionsLoadService().validateBankTransactionsFileInfo(getBankFileInfo()));

            //if any errors in the final validation, also log them to the error file
            if (!errorList.isEmpty()) {
                LOG.info("ERRORS for File Info " + getBankFileInfo().toString() + " :\n" + errorList.toString());
                result = false;
                logErrorsToFile(errorList);
            }

        } catch (FileNotFoundException ex) {
            LOG.error("Cannot find Bank Transaction File: " + getAbsoulutePath());
            throw new RuntimeException("Cannot find Bank Transaction File: " + getAbsoulutePath(), ex);
        } catch (IOException ex) {
            LOG.error("An IOException occurred while validating  Bank Transaction File:" + getAbsoulutePath());
            throw new RuntimeException(ex);
        } catch (Exception e) {
            logErrorsToFile(errorList);
            LOG.error("Error validating Bank Transaction file " + getAbsoulutePath());
            throw new RuntimeException("Error validating Bank Transaction file " + getAbsoulutePath(), e);
        } finally { // always try to close the files
            try {
                br.close();

                // bufferedErrorWriter.close();
            } catch (Exception e) {
                // ignore any exception at closing time
            }
        }

        return result;
    }

    /**
     * Logs all the errors in the list to the error report file and then empties the list.
     */
    protected void logErrorsToFile(List<String> errorList) {

        if (errorList != null && !errorList.isEmpty()) {
            LOG.debug("Logging errorList to error file:" + getErrorFilePath());

            BufferedWriter bufferedErrorWriter = null;
            try {
                File errorFile = getErrorFile();
                bufferedErrorWriter = new BufferedWriter(new PrintWriter(errorFile));
                for (String error : errorList ) {
                    bufferedErrorWriter.write(error);
                    bufferedErrorWriter.newLine();
                }

            } catch (IOException e) {
                LOG.error("ERROR: Exception caught while writing to error file "+getErrorFilePath());
                throw new RuntimeException("ERROR: Exception caught while writing to error file "+getErrorFilePath(), e);
            } finally {
                try {
                    bufferedErrorWriter.flush();
                    bufferedErrorWriter.close();
                } catch (Exception e) {
                    //ignore closing exceptions...
                }
            }

            errorList.clear();
        }
    }



    @Override
    public void process(String fileName, Object parsedFileContents) {
    }

    protected File openFile() {
        if (getFileName().isEmpty()) {
            LOG.error("Bank transaction file name is empty!!! Cannot open a file with no name... ABORTING.");
            throw new RuntimeException("Bank transaction file name is empty!!! Cannot open a file with no name... ABORTING.");
        }

        File inputFile = new File(getAbsoulutePath());
        if (!inputFile.exists()) {
            LOG.error("Bank transaction file " + getAbsoulutePath() + " could not be found. ABORTING.");
            throw new RuntimeException("Bank transaction file " + getAbsoulutePath() + " could not be found. ABORTING.");
        }

        if (!inputFile.canRead()) {
            LOG.error("Bank transaction file " + getAbsoulutePath() + " cannot be opened for reading. ABORTING.");
            throw new RuntimeException("Bank transaction file " + getAbsoulutePath() + " cannot be opened for reading. ABORTING.");
        }

        return inputFile;
    }

    /**
     * Deletes the .done corresponding to the given file, if one exists.
     */
    public void deleteDoneFile() {
        String doneFilePath =  getDirectoryPath() + File.separator + fileName + KFSConstants.DONE_FILE_EXTENSION;
        try {
            File doneFile = new File(doneFilePath);
            if (doneFile.exists() && !doneFile.delete()) {
                LOG.error("Error in BankTransactionsLoadService - " + doneFilePath + " could not be deleted! ");
                throw new RuntimeException("Error in BankTransactionsLoadService - " + doneFilePath + " could not be deleted! ");
            }
        } catch (Exception e) {
            LOG.error("Error in BankTransactionsLoadService.deleteDoneFile for file=" + doneFilePath, e);
            throw new RuntimeException(e);
        }
    }


    public String getErrorFilePath() {
        if ( errorFilePath == null || StringUtils.isEmpty(errorFilePath)) {
            errorFilePath = getReportPath() + File.separator +
                    getReportPrefix() + "_" +
                    dateTimeService.toDateTimeStringForFilename(dateTimeService.getCurrentDate()) + "." +
                    getReportExtension();
        }
        return errorFilePath;
    }


    protected File getErrorFile() {
        File errorFile = new File(getErrorFilePath());
        try {
            errorFile.createNewFile();
            if (!errorFile.canWrite()) {
                LOG.error("ERROR: Bank transaction error log file " + getErrorFilePath() + " cannot be opened for writing. ABORTING.");
                throw new RuntimeException("ERROR: Bank transaction error log file " + getErrorFilePath() + " cannot be opened for writing. ABORTING.");
            }
        } catch (IOException e) {
            LOG.error("ERROR: Bank transaction error log file " + getErrorFilePath() + " could not be created. ABORTING. ");
            throw new RuntimeException("ERROR: Bank transaction error log file " + getErrorFilePath() + " could not be created. ABORTING. ", e);
        }
        return errorFile;
    }


    /**
     * Parses Batch total from the first line in the batch file, returns ZERO if the first line is empty.
     *
     * @param firstLine from the Bank Transaction File
     * @return
     */
    protected KualiDecimal parseBatchTotal(String firstLine) {
        KualiDecimal batchTotal = KualiDecimal.ZERO;
        if (StringUtils.isNotBlank(firstLine)) {
            String firstRecord = firstLine.replaceAll(",", "");
            if (!StringUtils.isNumeric(firstRecord) || firstRecord.length() > 20) {
                LOG.error("First record of Bank Transaction file " + getFileName() + " isn't structured as expected [" + firstRecord + "].");
                throw new RuntimeException("First record of Bank Transaction file " + getFileName() + " isn't structured as expected [" + firstRecord + "].");
            }
            batchTotal = new KualiDecimal(firstRecord).multiply(new KualiDecimal(0.01));
        }
        return batchTotal;
    }


    /**
     * Copy validated file into the validated folder and create .done file with the same name
     */
    public void createValidatedFiles() {
        LOG.debug("Start createValidatedFiles for file=" + getFileName());

        String destFileName = getValidatedPath() + File.separator + getFileName() + "." + getFileExtension();
        String destDoneFile = getValidatedPath() + File.separator + getFileName() + KFSConstants.DONE_FILE_EXTENSION;
        try {
            File sourceFile = new File(getAbsoulutePath());
            File destFile = new File(destFileName);
            FileUtils.copyFile(sourceFile, destFile);
            new File(destDoneFile).createNewFile();
        } catch (Exception e) {
            LOG.error("Unable to create validated files for " + getAbsoulutePath(), e);
            throw new RuntimeException("Unable to create validated files for " + getAbsoulutePath(), e);
        }
    }


    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileTypeIdentifer()
     */
    @Override
    public String getFileTypeIdentifer() {
        return KFSConstants.BANK_TRANSACTIONS_FILE_TYPE_IDENTIFIER;
    }

    /**
     * Implementation from KFS3
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

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputType#getTitleKey()
     */
    @Override
    public String getTitleKey() {
        return KFSKeyConstants.MESSAGE_BATCH_UPLOAD_TITLE_BANK_TRANSACTIONS;
    }


    public BankTransactionsFileInfo getBankFileInfo() {
        if (bankFileInfo == null) {
            bankFileInfo = new BankTransactionsFileInfo(getFileName());
        }
        return bankFileInfo;
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

    public BankTransactionsLoadService getBankTransactionsLoadService() {
        if ( bankTransactionsLoadService == null ){
            bankTransactionsLoadService = SpringContext.getBean(BankTransactionsLoadService.class);
        }
        return bankTransactionsLoadService;
    }




    public void setReportPath(String reportPath) {
        this.reportPath = reportPath;
    }

    public void setReportExtension(String reportExtension) {
        this.reportExtension = reportExtension;
    }

    public void setReportPrefix(String reportPrefix) {
        this.reportPrefix = reportPrefix;
    }

    public void setValidatedPath(String validatedPath) {
        this.validatedPath = validatedPath;
    }

    public String getReportPath() {
        return reportPath;
    }

    public String getReportPrefix() {
        return reportPrefix;
    }

    public String getReportExtension() {
        return reportExtension;
    }

    public String getValidatedPath() {
        return validatedPath;
    }

    public String getAbsoulutePath() {
        return getDirectoryPath() + File.separator + fileName + "." + getFileExtension();
    }


}
