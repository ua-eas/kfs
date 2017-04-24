package edu.arizona.kfs.fp.batch.dataaccess.impl;

import edu.arizona.kfs.sys.KFSConstants;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.batch.BatchInputFileTypeBase;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.rice.core.api.datetime.DateTimeService;

import java.io.*;
import java.util.List;

/**
 * CSV Bank Transaction Validated File that uses the Bank Parameters to determine order of columns and delimiter
 * <p>
 * Created by nataliac on 3/10/17.
 */
public class CsvBankTransactionsValidatedFileType extends BatchInputFileTypeBase {
    private static final Logger LOG = Logger.getLogger(CsvBankTransactionsValidatedFileType.class);

    protected String outputPath;
    protected String loadFileName;

    protected DateTimeService dateTimeService;


    public void consolidateFiles( List<String> filesToProcess ) {
        LOG.debug("Starting consolidating Bank Transaction Validated Files size="+filesToProcess.size());

        // create and open the output file
        File outputFile = getOutputFile(getOutputFilePath());

        try (BufferedWriter outputBufferedWriter  =
                         new BufferedWriter(new FileWriter(outputFile))) {

            // for each input file, copy contents without the first line = batchTotal, to the consolidated output file
            for (String filePath : filesToProcess) {
                LOG.debug("Consolidating Bank Transaction Validated File="+filePath);
                File file = openFile(filePath);
                // remove the done file
                deleteDoneFile(file);

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
                        LOG.error("ERROR: Consolidate load transaction file: Failed to read/write at file "+filePath, e);
                        throw new RuntimeException("ERROR: Consolidate load transaction file: Failed to read/write at file "+filePath, e);
                } catch (Exception e) {
                    LOG.error("ERROR: Failed to open the input file [" + filePath + "].", e);
                    throw new RuntimeException("Failed to open the input file [" + filePath + "].", e);
                }
            }

        }
        catch (Exception e) {
            LOG.error("ERROR: Could not open a BufferedWriter to the output bank load file: "+getOutputFilePath(),e);
            throw new RuntimeException("ERROR: Could not open a BufferedWriter to the output bank load file: "+getOutputFilePath(),e);
        }

        LOG.debug("Finishing consolidating Bank Transaction Validated Files.");
    }


    @Override
    public void process(String fileName, Object parsedFileContents) {
    }

    /**
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
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#validate(Object)
     */
    @Override
    public boolean validate(Object parsedFileContents) {
        return true;
    }

    protected File openFile(String filePath) {
        if (filePath.isEmpty()) {
            LOG.error("Bank transaction file path is empty!!! Cannot open a file with no name... ABORTING.");
            throw new RuntimeException("Bank transaction file name is empty!!! Cannot open a file with no name... ABORTING.");
        }

        File inputFile = new File(filePath);
        if (!inputFile.exists()) {
            LOG.error("Bank transaction file " + filePath + " could not be read. ABORTING.");
            throw new RuntimeException("Bank transaction file " + filePath + " could not be read. ABORTING.");
        }

        if (!inputFile.canRead()) {
            LOG.error("Bank transaction file " + filePath + " cannot be opened for reading. ABORTING.");
            throw new RuntimeException("Bank transaction file " + filePath + " cannot be opened for reading. ABORTING.");
        }

        return inputFile;
    }

    protected File getOutputFile(String filePath){
        if (filePath.isEmpty()) {
            LOG.error("Bank transaction file path is empty!!! Cannot create a file with no name... ABORTING.");
            throw new RuntimeException("Bank transaction file name is empty!!! Cannot create a file with no name... ABORTING.");
        }
        File inputFile = new File(filePath);
        try {
            inputFile.createNewFile();
            if (!inputFile.canWrite()) {
                LOG.error("Bank transaction file " + filePath + " cannot be opened for writing. ABORTING.");
                throw new RuntimeException("Bank transaction file " + filePath + " cannot be opened for writing. ABORTING.");
            }
        } catch ( IOException e){
            LOG.error("Bank transaction file " + filePath + " could not be created. ABORTING.");
            throw new RuntimeException("Bank transaction file " + filePath + " could not be created. ABORTING.");
        }
        return inputFile;
    }

    /**
     * Deletes the .done corresponding to the given file, if one exists.
     */
    public void deleteDoneFile(File file) {
        String fileName = file.getAbsolutePath();
        String doneFilePath = FilenameUtils.removeExtension(fileName) + KFSConstants.DONE_FILE_EXTENSION;

        try {
            File doneFile = new File(doneFilePath);
            if ( doneFile.exists() && !doneFile.delete()) {
                LOG.error("ERROR - " + doneFilePath + " could not be deleted! ");
                throw new RuntimeException("ERROR - " + doneFilePath + " could not be deleted! ");
            }
        } catch (Exception e) {
            LOG.error("ERROR - at deleteDoneFile " + doneFilePath, e);
            throw new RuntimeException(e);
        }
    }


    protected String getOutputFilePath(){
        return getOutputPath() + File.separator + getLoadFileName() + "." + getFileExtension();
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
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileTypeIdentifer()
     */
    @Override
    public String getFileTypeIdentifer() {
        return KFSConstants.BANK_TRANSACTIONS_VALIDATED_FILE_TYPE_IDENTIFIER;
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

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }


    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public String getLoadFileName() {
        return loadFileName;
    }

    public void setLoadFileName(String loadFileName) {
        this.loadFileName = loadFileName;
    }
}
