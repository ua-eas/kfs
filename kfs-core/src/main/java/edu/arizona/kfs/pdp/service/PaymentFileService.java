package edu.arizona.kfs.pdp.service;

import org.kuali.kfs.sys.batch.BatchInputFileType;

/**
 * Handles processing (validation, loading, and reporting) of incoming payment files.
 */
public interface PaymentFileService extends org.kuali.kfs.pdp.service.PaymentFileService {

    /**
     * Process all incoming payment files
     * This is the returns boolean version of delivered {@link PaymentFileService#processPaymentFiles(BatchInputFileType)}
     * We have implemented logic so that one failing file does not prevent other files from loading, but
     * we still need to be able to indicate success or failure to Control-M.  
     * 
     * @param paymentInputFileType <code>BatchInputFileType</code> for payment files
     * @return true if process is successful, false otherwise
     */
	public boolean processPaymentFilesBool(BatchInputFileType paymentInputFileType);
    
}

