/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ar.batch.service.impl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.batch.LockboxLoadStep;
import org.kuali.kfs.module.ar.batch.service.LockboxLoadService;
import org.kuali.kfs.module.ar.businessobject.Lockbox;
import org.kuali.kfs.module.ar.businessobject.LockboxDetail;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.FlatFileInformation;
import org.kuali.kfs.sys.batch.FlatFileTransactionInformation;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.kfs.sys.mail.BodyMailMessage;
import org.kuali.kfs.sys.service.EmailService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Transactional
public class LockboxLoadServiceImpl implements LockboxLoadService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LockboxLoadServiceImpl.class);

    private BatchInputFileType batchInputFileType;
    private String reportsDirectory;
    private BatchInputFileService batchInputFileService;
    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;
    private EmailService emailService;
    private ParameterService parameterService;

   @Override
    public boolean loadFile() {
        LOG.debug("loadFile() started");

        boolean result = true;

        List<FlatFileInformation> flatFileInformationList = new ArrayList<>();
        FlatFileInformation flatFileInformation = null;

        List<String> fileNamesToLoad = getListOfFilesToProcess();
        LOG.info("loadFile() Found " + fileNamesToLoad.size() + " file(s) to process.");

        List<String> processedFiles = new ArrayList<String>();

        for (String inputFileName : fileNamesToLoad) {
            LOG.info("loadFile() Beginning processing of filename: " + inputFileName + ".");
            flatFileInformation = new FlatFileInformation(inputFileName);
            flatFileInformationList.add(flatFileInformation);

            if (loadFile(inputFileName, flatFileInformation)) {
                processedFiles.add(inputFileName);
                flatFileInformation.addFileInfoMessage("File successfully completed processing.");
            } else {
                flatFileInformation.addFileErrorMessage("File failed to process successfully.");

            }

        }

        removeDoneFiles(processedFiles);
        sendLoadSummaryEmail(flatFileInformationList);

        return result;
    }

    protected boolean loadFile(String fileName, FlatFileInformation flatFileInformation) {
        boolean valid = true;
        //  load up the file into a byte array
        byte[] fileByteContent = safelyLoadFileBytes(fileName);

        //  parse the file against the configuration define in the spring file and load it into an object
        LOG.info("loadFile() Attempting to parse the file");
        Object parsedObject = null;

        try {
            parsedObject = batchInputFileService.parse(batchInputFileType, fileByteContent);
        } catch (ParseException e) {
            LOG.error("loadFile() Error parsing batch file: " + e.getMessage());
            flatFileInformation.addFileErrorMessage("Error parsing batch file: " + e.getMessage());
            valid = false;
        }

        // validate the parsed data
        if (parsedObject != null) {
            valid = validate(parsedObject);
            copyAllMessage(parsedObject, flatFileInformation);
            if (valid) {
                loadLockbox(parsedObject);
            }
        }
        return valid;
    }


    @Override
    public boolean validate(Object parsedFileContents) {
       LOG.debug("validate() started");

        // compare header with detail record
        boolean valid = true;
        List<Lockbox> lockboxList = (List<Lockbox>) parsedFileContents;
        for (Lockbox lockbox : lockboxList) {
            if (!compareDetailsWithHeader(lockbox)) {
                valid = false;
                break;
            }
        }

        return valid;
    }


    /**
     * No processing
     */
    @Override
    public void process(String fileName, Object parsedFileContents) {
    }

    protected boolean compareDetailsWithHeader(Lockbox lockbox) {
        boolean isHeaderMatchedDetails = true;
        KualiDecimal headerTransBatchTotal = lockbox.getHeaderTransactionBatchTotal();
        Integer headerTransBatchCount = lockbox.getHeaderTransactionBatchCount();
        KualiDecimal detailInvPaidTotal = new KualiDecimal(0);
        Integer totalDetailRecords = 0;
        for (LockboxDetail detail : lockbox.getLockboxDetails()) {
            detailInvPaidTotal = detailInvPaidTotal.add(detail.getInvoicePaidOrAppliedAmount());
            totalDetailRecords++;
        }

        if (headerTransBatchTotal.compareTo(detailInvPaidTotal) == 0 && headerTransBatchCount.compareTo(totalDetailRecords) == 0) {

            String message = "Good Transfer for lockbox number " + lockbox.getLockboxNumber() + "."
                + " Transaction count : " + lockbox.getHeaderTransactionBatchCount()
                + " Transaction total amount : $ " + lockbox.getHeaderTransactionBatchTotal();
            lockbox.getFlatFileTransactionInformation().addInfoMessage(message);
            GlobalVariables.getMessageMap().putInfo(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_CUSTOM, message);
        }


        if (headerTransBatchTotal.compareTo(detailInvPaidTotal) != 0) {
            String message = "Bad Transmmission for lock box number " + lockbox.getLockboxNumber() + "."
                + " Detail does not match header control values "
                + " Header total : $ " + lockbox.getHeaderTransactionBatchTotal()
                + " Detail total : $ " + detailInvPaidTotal;
            lockbox.getFlatFileTransactionInformation().addErrorMessage(message);
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_CUSTOM, message);
            isHeaderMatchedDetails = false;
        }

        if (headerTransBatchCount.compareTo(totalDetailRecords) != 0) {
            String message = "Bad Transmmission for lock box number " + lockbox.getLockboxNumber() + "."
                + " Detail does not match header control values "
                + " Header Count : " + lockbox.getHeaderTransactionBatchCount()
                + " Detail total : " + totalDetailRecords;
            lockbox.getFlatFileTransactionInformation().addErrorMessage(message);
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_CUSTOM, message);
            isHeaderMatchedDetails = false;
        }

        return isHeaderMatchedDetails;
    }


    /**
     * Send load lockbox file validation
     *
     * @param flatFileInformationList
     */
    public void sendLoadSummaryEmail(List<FlatFileInformation> flatFileInformationList) {
        for (FlatFileInformation information : flatFileInformationList) {
            sendEmail(information);
        }
    }

    public void sendEmail(FlatFileInformation flatFileInformation) {
        LOG.debug("sendEmail() starting");

        BodyMailMessage message = new BodyMailMessage();

        message.setFromAddress(emailService.getDefaultFromAddress());
        String subject = parameterService.getParameterValueAsString(LockboxLoadStep.class, ArConstants.Lockbox.SUMMARY_AND_ERROR_NOTIFICATION_EMAIL_SUBJECT);
        String fileName = StringUtils.substringAfterLast(flatFileInformation.getFileName(), "\\");
        message.setSubject(subject + "[ " + fileName + " ]");
        List<String> toAddressList = new ArrayList<String>(parameterService.getParameterValuesAsString(LockboxLoadStep.class, ArConstants.Lockbox.SUMMARY_AND_ERROR_NOTIFICATION_TO_EMAIL_ADDRESSES));
        message.getToAddresses().addAll(toAddressList);
        String body = composeLockboxLoadBody(flatFileInformation);
        message.setMessage(body);

        emailService.sendMessage(message,false);
    }

    protected String composeLockboxLoadBody(FlatFileInformation flatFileInformation) {
        String contactText = parameterService.getParameterValueAsString(LockboxLoadStep.class, ArConstants.Lockbox.CONTACTS_TEXT);
        StringBuffer body = new StringBuffer();
        body.append(contactText);
        body.append("\n");

        for (Object object : flatFileInformation.getFlatFileIdentifierToTransactionInfomationMap().values()) {
            for (String[] message : ((FlatFileTransactionInformation) object).getMessages()) {
                body.append(message[1]);
                body.append("\n");
            }
        }

        for (String[] resultMessage : flatFileInformation.getMessages()) {
            body.append(resultMessage[1]);
            body.append("\n");
        }

        return body.toString();
    }


    protected List<String> getListOfFilesToProcess() {
        //  create a list of the files to process
        List<String> fileNamesToLoad = batchInputFileService.listInputFileNamesWithDoneFile(batchInputFileType);

        if (fileNamesToLoad == null) {
            LOG.error("getListOfFilesToProcess() BatchInputFileService.listInputFileNamesWithDoneFile(" +
                batchInputFileType.getFileTypeIdentifer() + ") returned NULL which should never happen.");
            throw new RuntimeException("BatchInputFileService.listInputFileNamesWithDoneFile(" +
                batchInputFileType.getFileTypeIdentifer() + ") returned NULL which should never happen.");
        }

        //  filenames returned should never be blank/empty/null
        for (String inputFileName : fileNamesToLoad) {
            if (StringUtils.isBlank(inputFileName)) {
                LOG.error("getListOfFilesToProcess() One of the file names returned as ready to process [" + inputFileName +
                    "] was blank.  This should not happen, so throwing an error to investigate.");
                throw new RuntimeException("One of the file names returned as ready to process [" + inputFileName +
                    "] was blank.  This should not happen, so throwing an error to investigate.");
            }
        }

        return fileNamesToLoad;
    }

    /**
     * Accepts a file name and returns a byte-array of the file name contents, if possible.
     * <p>
     * Throws RuntimeExceptions if FileNotFound or IOExceptions occur.
     *
     * @param fileName String containing valid path & filename (relative or absolute) of file to load.
     * @return A Byte Array of the contents of the file.
     */
    protected byte[] safelyLoadFileBytes(String fileName) {
        byte[] fileByteContent;
        try (InputStream fileContents = new FileInputStream(fileName)) {
            fileByteContent = IOUtils.toByteArray(fileContents);
        } catch (FileNotFoundException e1) {
            LOG.error("safelyLoadFileBytes() Batch file not found [" + fileName + "]. " + e1.getMessage());
            throw new RuntimeException("Batch File not found [" + fileName + "]. " + e1.getMessage());
        } catch (IOException e1) {
            LOG.error("safelyLoadFileBytes() IO Exception loading: [" + fileName + "]. " + e1.getMessage());
            throw new RuntimeException("IO Exception loading: [" + fileName + "]. " + e1.getMessage());
        }
        return fileByteContent;
    }

    protected void loadLockbox(Object parsedObject) {
        // create the lockbox object to load data
        List loadLockboxList = new ArrayList<Lockbox>();
        List<Lockbox> lockboxList = (List<Lockbox>) parsedObject;
        int batchSequenceNumber = 1;
        for (Lockbox lockbox : lockboxList) {
            setLockboxToLoad(loadLockboxList, lockbox, batchSequenceNumber);
            batchSequenceNumber++;
        }

        // save lockbox data in AR_LOCKBOX_T
        businessObjectService.save(loadLockboxList);
    }

    /**
     * Clears out associated .done files for the processed data files.
     */
    protected void removeDoneFiles(List<String> dataFileNames) {
        for (String dataFileName : dataFileNames) {
            File doneFile = new File(StringUtils.substringBeforeLast(dataFileName, ".") + ".done");
            if (doneFile.exists()) {
                doneFile.delete();
            }
        }
    }

    protected void setLockboxToLoad(List loadLockboxList, Lockbox lockbox, int batchSequenceNumber) {
        for (LockboxDetail detail : lockbox.getLockboxDetails()) {
            Lockbox lockboxToLoad = new Lockbox();
            lockboxToLoad.setLockboxNumber(lockbox.getLockboxNumber());
            lockboxToLoad.setScannedInvoiceDate(lockbox.getScannedInvoiceDate());
            lockboxToLoad.setCustomerNumber(detail.getCustomerNumber());
            lockboxToLoad.setBatchSequenceNumber(batchSequenceNumber);
            lockboxToLoad.setProcessedInvoiceDate(dateTimeService.getCurrentSqlDate());
            lockboxToLoad.setFinancialDocumentReferenceInvoiceNumber(detail.getFinancialDocumentReferenceInvoiceNumber());
            lockboxToLoad.setBillingDate(detail.getBillingDate());
            lockboxToLoad.setInvoiceTotalAmount(detail.getInvoiceTotalAmount());
            lockboxToLoad.setInvoicePaidOrAppliedAmount(detail.getInvoicePaidOrAppliedAmount());
            lockboxToLoad.setCustomerPaymentMediumCode(detail.getCustomerPaymentMediumCode());
            loadLockboxList.add(lockboxToLoad);
        }
    }

    protected void copyAllMessage(Object parsedObject, FlatFileInformation flatFileInformation) {
        List<Lockbox> lockboxList = (List<Lockbox>) parsedObject;
        for (Lockbox lockbox : lockboxList) {
            FlatFileTransactionInformation information = lockbox.getFlatFileTransactionInformation();
            flatFileInformation.getOrAddFlatFileData(lockbox.getLockboxNumber(), information);
        }
    }

    @Override
    public String getFileName(String principalName, Object parsedFileContents, String fileUserIdentifier) {
        return null;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setBatchInputFileType(BatchInputFileType batchInputFileType) {
        this.batchInputFileType = batchInputFileType;
    }

    public void setReportsDirectory(String reportsDirectory) {
        this.reportsDirectory = reportsDirectory;
    }

    public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
        this.batchInputFileService = batchInputFileService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }
}
