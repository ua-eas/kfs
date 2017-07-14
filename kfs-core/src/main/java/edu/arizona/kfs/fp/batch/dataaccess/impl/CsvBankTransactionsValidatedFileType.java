package edu.arizona.kfs.fp.batch.dataaccess.impl;

import au.com.bytecode.opencsv.CSVReader;
import edu.arizona.kfs.fp.businessobject.BankTransaction;
import edu.arizona.kfs.sys.KFSConstants;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.FileUtil;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.io.*;
import java.util.Date;
import java.util.List;

/**
 * CSV Bank Transaction Validated File that uses the Bank Parameters to determine order of columns and delimiter
 * <p>
 * Created by nataliac on 3/10/17.
 */
public class CsvBankTransactionsValidatedFileType extends CsvBankTransactionsInputFileType {
    private static final Logger LOG = Logger.getLogger(CsvBankTransactionsValidatedFileType.class);

    protected String outputPath;
    protected String loadFileName;
    protected BufferedReader loadFileReader;
    BankTransactionDigesterAdapter btAdapter;
    CSVReader csvReader;

    protected int currentLine = 0;


    /**
     * Looks at all the validated bank transaction files that have a .done and consolidates them into the tfile_load bank
     * file, by ignoring the first line in each file (batchTotal) which should have already been validated in the previous step
     *
     * @param filesToProcess
     */
    public void consolidateFiles(List<String> filesToProcess) {
        LOG.debug("Starting consolidating Bank Transaction Validated Files size=" + filesToProcess.size());

        // create and open the output file
        File outputFile = createOutputFile(getOutputFilePath());

        try (BufferedWriter outputBufferedWriter =
                     new BufferedWriter(new FileWriter(outputFile))) {

            // for each input file, copy contents without the first line = batchTotal, to the consolidated output file
            for (String filePath : filesToProcess) {
                LOG.debug("Consolidating Bank Transaction Validated File=" + filePath);
                File file = openFile(filePath);
                // remove the done file
                deleteDoneFile(filePath);

                try (BufferedReader inBufferedReader = new BufferedReader(new FileReader(file))) {
                    boolean firstRecord = true;
                    while (inBufferedReader.ready()) {
                        // skip batch total on first line
                        if (firstRecord) {
                            String ignoredFirstRecord = inBufferedReader.readLine();
                            firstRecord = false;
                        } else {
                            outputBufferedWriter.write(inBufferedReader.readLine() + "\n");
                        }
                    }
                } catch (IOException e) {
                    LOG.error("ERROR: Consolidate load transaction file: Failed to read/write at file " + filePath, e);
                    throw new RuntimeException("ERROR: Consolidate load transaction file: Failed to read/write at file " + filePath, e);
                } catch (Exception e) {
                    LOG.error("ERROR: Failed to open the input file [" + filePath + "].", e);
                    throw new RuntimeException("Failed to open the input file [" + filePath + "].", e);
                }
            }

        } catch (Exception e) {
            LOG.error("ERROR: Could not open a BufferedWriter to the output bank load file: " + getOutputFilePath(), e);
            throw new RuntimeException("ERROR: Could not open a BufferedWriter to the output bank load file: " + getOutputFilePath(), e);
        }

        LOG.debug("Finishing consolidating Bank Transaction Validated Files.");
    }

    /**
     * Opens the file for validated bank transaction file for processing and resets the corresponding
     * current line index
     */
    public void openTransactionsFileForProcessing() {
        LOG.debug("Start openTransactionFileForProcessing");
        try {
            // reset line
            setCurrentLine(0);

            // open buffer for reading.
            loadFileReader = new BufferedReader(new FileReader(openFile(getOutputFilePath())));
            csvReader = new CSVReader(loadFileReader, getBankParametersAccessService().getBankFileDelimiter(), KFSConstants.QUOTE_CHAR, getCurrentLine());

            // create the adapter to digest from CSV line to BankTransaction
            btAdapter = new BankTransactionDigesterAdapter();
        } catch (IOException e) {
            LOG.error("Bank transaction file " + getOutputFilePath() + " cannot be opened for reading. ABORTING.");
            throw new RuntimeException("Bank transaction file " + getAbsoulutePath() + " cannot be opened for reading. ABORTING.");
        }

        LOG.debug("END Start openTransactionFileForProcessing");
    }


    /**
     * Reads the next transaction at line <currentLine> from the bank Transaction File and returns the converted Bank Transaction object
     * If there are any errors/exceptions during the process, they will be returned in the errorList parameter
     */
    public BankTransaction readNextBankTransaction(List<String> errorList) {
        setCurrentLine(++currentLine);
        LOG.debug("Loading transaction at line " + currentLine);
        BankTransaction bankTransactionResult = null;
        try {
            String[] currentRowData;
            if ((currentRowData = csvReader.readNext()) != null) {

                BankTransactionDigesterVO btDigesterVO = BankTransactionCSVBuilder.buildBankTransactionVO(currentRowData, getBankParametersAccessService().getFieldPositions());
                bankTransactionResult = btAdapter.convert(btDigesterVO, errorList);
                if (!errorList.isEmpty()) {
                    LOG.error("ERRORS for Line #" + currentLine + " :" + errorList.toString());
                    errorList.add("ERRORS above found at line #" + currentLine + " in file " + getOutputFilePath() + " Aborting documentCreationJob.");
                }
                updateBankFileInfo(bankTransactionResult);
            }
        } catch (IOException e) {
            LOG.error("IOException occurred while loading Bank Transaction at line :" + currentLine, e);
            errorList.add("IOException occurred while loading Bank Transaction at line :" + currentLine);
        } catch (Exception e) {
            LOG.error("Exception occurred while loading Bank Transaction at line :" + currentLine, e);
            errorList.add("Exception occurred while loading Bank Transaction at line :" + currentLine);
        }

        LOG.debug("Line # " + currentLine + ": Finished reading transaction: " + (bankTransactionResult != null ? bankTransactionResult.toString() : "NULL"));
        return bankTransactionResult;
    }

    /**
     * Updates the  bankFileInfo with transaction amount, posted Date and record number from given bankTransaction.
     * bankFileInfo is used to record the bank transaction file upload in the table after finishing processing.
     * @param bankTransaction
     */
    private void updateBankFileInfo(BankTransaction bankTransaction){
        getBankFileInfo().setPostingDate(bankTransaction.getValueDate());
        KualiDecimal amount = getBankFileInfo().getBatchTotal().add(bankTransaction.getAmount().abs());
        getBankFileInfo().setBatchTotal(amount);
        getBankFileInfo().setTransactionsTotal(amount);
        getBankFileInfo().setTransactionCount(getBankFileInfo().getTransactionCount()+1);
    }



    public void renameProcessedFile() {
        String uploadFilePath = getOutputFilePath();
        File file = new File(uploadFilePath);
        try {

            String changedFileName = FilenameUtils.removeExtension(uploadFilePath) + KFSConstants.DOT_CHAR + dateTimeService.toDateTimeStringForFilename(new Date());
            file.renameTo(new File(changedFileName + KFSConstants.DOT_CHAR + getFileExtension()));

        } catch (Exception e) {
            LOG.error("ERROR:renaming transaction file " + uploadFilePath + " DocumentCreationJob will be aborted.", e);
            throw new RuntimeException("ERROR:renaming transaction file " + uploadFilePath + " DocumentCreationJob will be aborted.", e);
        }

    }


    public void closeOpenedResources() {
        IOUtils.closeQuietly(loadFileReader);
    }


    /**
     * Creates and opens the file on the output path. the .done corresponding to the given file, if one exists.
     */
    protected File createOutputFile(String filePath) {
        if (filePath.isEmpty()) {
            LOG.error("Bank transaction file path is empty!!! Cannot create a file with no name... ABORTING.");
            throw new RuntimeException("Bank transaction file name is empty!!! Cannot create a file with no name... ABORTING.");
        }
        File outputFile = new File(filePath);
        try {
            outputFile.createNewFile();
            if (!outputFile.canWrite()) {
                LOG.error("Bank transaction file " + filePath + " cannot be opened for writing. ABORTING.");
                throw new RuntimeException("Bank transaction file " + filePath + " cannot be opened for writing. ABORTING.");
            }
        } catch (IOException e) {
            LOG.error("Bank transaction file " + filePath + " could not be created. ABORTING.");
            throw new RuntimeException("Bank transaction file " + filePath + " could not be created. ABORTING.");
        }
        return outputFile;
    }


    protected String getOutputFilePath() {
        return getOutputPath() + File.separator + getLoadFileName() + KFSConstants.DOT_CHAR + getFileExtension();
    }


    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileTypeIdentifer()
     */
    @Override
    public String getFileTypeIdentifer() {
        return KFSConstants.BankTransactionConstants.BANK_TRANSACTIONS_VALIDATED_FILE_TYPE_IDENTIFIER;
    }


    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
        //check directory when setting the path
        FileUtil.createDirectory(outputPath);
    }

    public String getLoadFileName() {
        return loadFileName;
    }

    public void setLoadFileName(String loadFileName) {
        this.loadFileName = loadFileName;
    }

    public int getCurrentLine() {
        return currentLine;
    }

    public void setCurrentLine(int currentLine) {
        this.currentLine = currentLine;
    }
}
