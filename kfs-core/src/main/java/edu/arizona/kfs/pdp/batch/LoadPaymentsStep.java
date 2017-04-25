package edu.arizona.kfs.pdp.batch;

import java.net.MalformedURLException;
import java.util.Date;

import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.XmlBatchInputFileTypeBase;
import org.springframework.core.io.UrlResource;

import edu.arizona.kfs.pdp.service.PaymentFileService;

/**
 * This step will call the <code>PaymentService</code> to pick up incoming PDP payment files and process.
 */
public class LoadPaymentsStep extends org.kuali.kfs.pdp.batch.LoadPaymentsStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LoadPaymentsStep.class);
    
    // these services are also declared in the base class as private but no get methods 
    // are available so we declare them here so we can access them without modifying the foundation source 
    private PaymentFileService paymentFileService;
    private BatchInputFileType paymentInputFileType;
   
    /**
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String, java.util.Date)
     */
    @Override
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        LOG.debug("execute() started");
        //check if payment.xsd exists. If not terminate at this point.
        if(paymentInputFileType instanceof XmlBatchInputFileTypeBase) {
            try {
                UrlResource schemaResource = new UrlResource(((XmlBatchInputFileTypeBase)paymentInputFileType).getSchemaLocation());
                if(!schemaResource.exists()) {
                    LOG.error(schemaResource.getFilename() + " file does not exist");
                    throw new RuntimeException("error getting schema stream from url: " + schemaResource.getFilename() + " file does not exist ");
                }
            }
            catch (MalformedURLException ex) {
                LOG.error("error getting schema url: " + ex.getMessage());
                throw new RuntimeException("error getting schema url:  " + ex.getMessage(), ex);
            }
        }

        return paymentFileService.processPaymentFilesBool(paymentInputFileType);
       
    }
  
    public void setPaymentFileService(PaymentFileService paymentFileService) {
        this.paymentFileService = paymentFileService;
        super.setPaymentFileService(paymentFileService);
    }
  
    public void setPaymentInputFileType(BatchInputFileType paymentInputFileType) {
        this.paymentInputFileType = paymentInputFileType;
        super.setPaymentInputFileType(paymentInputFileType);
    }

}
