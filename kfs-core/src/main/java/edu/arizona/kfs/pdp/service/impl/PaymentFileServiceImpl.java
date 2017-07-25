package edu.arizona.kfs.pdp.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.kuali.kfs.pdp.businessobject.Batch;
import org.kuali.kfs.pdp.businessobject.LoadPaymentStatus;
import org.kuali.kfs.pdp.businessobject.PaymentFileLoad;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.service.PaymentFileValidationService;
import org.kuali.kfs.pdp.service.PdpEmailService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.util.ErrorMessage;
import org.kuali.kfs.krad.util.MessageMap;
import org.springframework.transaction.annotation.Transactional;

import edu.arizona.kfs.pdp.service.PaymentFileService;

/**
 * @see edu.arizona.kfs.pdp.service.PaymentFileService
 */
@Transactional
public class PaymentFileServiceImpl extends org.kuali.kfs.pdp.service.impl.PaymentFileServiceImpl implements PaymentFileService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentFileServiceImpl.class);

    // These properties are also declared in the base class as private but no get methods 
    // are available so we declare them here so we can access them without modifying the foundation source.
    private BatchInputFileService batchInputFileService;
    private PaymentFileValidationService paymentFileValidationService;
    private BusinessObjectService businessObjectService;
    private PdpEmailService paymentFileEmailService;
    private ConfigurationService kualiConfigurationService;
    private String outgoingDirectoryName;
   
    /**
     * @see edu.arizona.kfs.pdp.service.PaymentFileService#processPaymentFilesBool(BatchInputFileType)
     */
    @Override
    public boolean processPaymentFilesBool(BatchInputFileType paymentInputFileType) {
        List<String> fileNamesToLoad = batchInputFileService.listInputFileNamesWithDoneFile(paymentInputFileType);

        boolean succeeded = true;
        for (String incomingFileName : fileNamesToLoad) {
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("processPaymentFiles() Processing " + incomingFileName);
                }
                
                // remove done file before any error occurs (including parsing errors)
                super.removeDoneFile(incomingFileName);

                // collect various information for status of load
                LoadPaymentStatus status = new LoadPaymentStatus();
                status.setMessageMap(new MessageMap());

                // process payment file
                PaymentFileLoad paymentFile = super.processPaymentFile(paymentInputFileType, incomingFileName, status.getMessageMap());
                if (paymentFile != null && paymentFile.isPassedValidation()) {
                    // load payment data
                    loadPayments(paymentFile, status, incomingFileName);
                    createOutputFile(status, incomingFileName);
                }
                else {
                	succeeded = false;                    
                }
            }
            catch (RuntimeException e) {
                LOG.error("Caught exception trying to load payment file: " + incomingFileName, e);
                // swallow exception so we can continue processing files, the errors have been reported by email
                succeeded = false;
            }
        }
        return succeeded;
    }
        
    @Override
    public void loadPayments(PaymentFileLoad paymentFile, LoadPaymentStatus status, String incomingFileName) {
        status.setChart(paymentFile.getChart());
        status.setUnit(paymentFile.getUnit());
        status.setSubUnit(paymentFile.getSubUnit());
        status.setCreationDate(paymentFile.getCreationDate());
        status.setDetailCount(paymentFile.getActualPaymentCount());
        status.setDetailTotal(paymentFile.getCalculatedPaymentTotalAmount());

        // create batch record for payment load
        Batch batch = super.createNewBatch(paymentFile, super.getBaseFileName(incomingFileName));
        businessObjectService.save(batch);

        paymentFile.setBatchId(batch.getId());
        status.setBatchId(batch.getId());

        // do warnings and set defaults
        List<String> warnings = paymentFileValidationService.doSoftEdits(paymentFile);
        status.setWarnings(warnings);
        
        // Since the Validation Service can now remove payments from the batch, 
        // we need to re-acquire the number and total of payments remaining in the "file"
        batch.setPaymentCount(new KualiInteger(paymentFile.getPaymentCount()));
        batch.setPaymentTotalAmount(paymentFile.getPaymentTotalAmount());
        businessObjectService.save(batch);

        // store groups
        for (PaymentGroup paymentGroup : paymentFile.getPaymentGroups()) {
            businessObjectService.save(paymentGroup);
        }

        // send list of warnings
        paymentFileEmailService.sendLoadEmail(paymentFile, warnings);
        if (paymentFile.isTaxEmailRequired()) {
            paymentFileEmailService.sendTaxEmail(paymentFile);
        }

        LOG.debug("loadPayments() was successful");
        status.setLoadStatus(LoadPaymentStatus.LoadStatus.SUCCESS);
    }

    @Override
    protected PaymentFileLoad parsePaymentFile(BatchInputFileType paymentInputFileType, String incomingFileName, MessageMap errorMap) {
    	PaymentFileLoad paymentFile = null;
    	FileInputStream fileContents;
        try {
            fileContents = new FileInputStream(incomingFileName);
        }
        catch (FileNotFoundException e1) {        	
            LOG.error("file to load not found " + incomingFileName, e1);
            paymentFileEmailService.sendErrorEmail(paymentFile, errorMap);
            throw new RuntimeException("Cannot find the file requested to be loaded " + incomingFileName, e1);
        }

        // do the parse
        try {
            byte[] fileByteContent = IOUtils.toByteArray(fileContents);
            paymentFile = (PaymentFileLoad) batchInputFileService.parse(paymentInputFileType, fileByteContent);
        }
        catch (IOException e) {        	
            LOG.error("error while getting file bytes:  " + e.getMessage(), e);
            paymentFileEmailService.sendErrorEmail(paymentFile, errorMap);
            throw new RuntimeException("Error encountered while attempting to get file bytes: " + e.getMessage(), e);
        }
        catch (ParseException e1) {
            LOG.error("Error parsing xml " + e1.getMessage());
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_PARSING_XML, new String[] { e1.getMessage() });
            paymentFileEmailService.sendErrorEmail(paymentFile, errorMap);
            throw new RuntimeException("Error parsing xml " + e1.getMessage(), e1);
        }

        return paymentFile;
    }
   
    @Override
    public boolean createOutputFile(LoadPaymentStatus status, String inputFileName) {

        //add a step to check for directory paths
        super.prepareDirectories(super.getRequiredDirectoryNames());

        // construct the outgoing file name
        String filename = outgoingDirectoryName + "/" + super.getBaseFileName(inputFileName);

        // set code-message indicating overall load status
        String code;
        String message;
        if (LoadPaymentStatus.LoadStatus.SUCCESS.equals(status.getLoadStatus())) {
            code = "SUCCESS";
            message = "Successful Load";
        }
        else {
            code = "FAIL";
            message = "Load Failed: ";
            List<ErrorMessage> errorMessages = status.getMessageMap().getMessages(KFSConstants.GLOBAL_ERRORS);
            for (ErrorMessage errorMessage : errorMessages) {
                String resourceMessage = kualiConfigurationService.getPropertyValueAsString(errorMessage.getErrorKey());
                resourceMessage = MessageFormat.format(resourceMessage, (Object[]) errorMessage.getMessageParameters());
                message += resourceMessage + ", ";
            }
        }

        try {
            FileOutputStream out = new FileOutputStream(filename);
            PrintStream p = new PrintStream(out);

            p.println("<pdp_load_status>");
            p.println("  <input_file_name>" + inputFileName + "</input_file_name>");
            p.println("  <code>" + code + "</code>");
            p.println("  <count>" + status.getDetailCount() + "</count>");
            if (status.getDetailTotal() != null) {
                p.println("  <total>" + status.getDetailTotal() + "</total>");
            }
            else {
                p.println("  <total>0</total>");
            }

            p.println("  <description>" + message + "</description>");
            p.println("  <messages>");
            if (status.getWarnings() != null) {
	            for (String warning : status.getWarnings()) {
	                p.println("    <message>" + warning + "</message>");
	            }
            }
            p.println("  </messages>");
            p.println("</pdp_load_status>");

            p.close();
            out.close();
            // creating .done file
            File doneFile = new File(filename.substring(0, filename.lastIndexOf(".")) + ".done");
            doneFile.createNewFile();
        }
        catch (FileNotFoundException e) {
            LOG.error("createOutputFile() Cannot create output file", e);
            return false;
        }
        catch (IOException e) {
            LOG.error("createOutputFile() Cannot write to output file", e);
            return false;
        }

        return true;
    }
         
    public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
        this.batchInputFileService = batchInputFileService;
        super.setBatchInputFileService(batchInputFileService);
    }
  
    public void setPaymentFileValidationService(PaymentFileValidationService paymentFileValidationService) {
        this.paymentFileValidationService = paymentFileValidationService;
        super.setPaymentFileValidationService(paymentFileValidationService);
    }
    
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
        super.setBusinessObjectService(businessObjectService);
    }
      
    public void setPaymentFileEmailService(PdpEmailService paymentFileEmailService) {
        this.paymentFileEmailService = paymentFileEmailService;
        super.setPaymentFileEmailService(paymentFileEmailService);
    }
   
    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
        super.setConfigurationService(kualiConfigurationService);
    }
    
    public void setOutgoingDirectoryName(String outgoingDirectoryName) {
        this.outgoingDirectoryName = outgoingDirectoryName;
        super.setOutgoingDirectoryName(outgoingDirectoryName);
    }
      
}

