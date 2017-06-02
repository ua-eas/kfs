package edu.arizona.kfs.pdp.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.pdp.PdpParameterConstants;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.batch.LoadPaymentsStep;
import org.kuali.kfs.pdp.batch.SendAchAdviceNotificationsStep;
import org.kuali.kfs.pdp.businessobject.ACHBank;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentFileLoad;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.businessobject.PaymentNoteText;
import org.kuali.kfs.pdp.service.AchBankService;
import org.kuali.kfs.pdp.service.CustomerProfileService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.mail.MailMessage;
import org.kuali.rice.core.web.format.CurrencyFormatter;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.service.MailService;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.MessageMap;

import edu.arizona.kfs.pdp.service.PdpEmailService;

public class PdpEmailServiceImpl extends org.kuali.kfs.pdp.service.impl.PdpEmailServiceImpl implements PdpEmailService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PdpEmailServiceImpl.class);
   
    protected CustomerProfileService customerProfileService;
    protected ConfigurationService kualiConfigurationService;
    protected MailService mailService;
    protected ParameterService parameterService;
    protected DataDictionaryService dataDictionaryService;
    protected AchBankService achBankService;
    
    public void sendPrepaidChecksLoadEmail(PaymentFileLoad prepaidChecksFile, List<String> warnings, String fileName) {    	
        LOG.debug("sendPrepaidChecksLoadEmail() starting");

        // check email configuration
        if (!super.isPaymentEmailEnabled()) {
            return;
        }

        MailMessage message = new MailMessage();

        String returnAddress = parameterService.getParameterValueAsString(KfsParameterConstants.PRE_DISBURSEMENT_BATCH.class, KFSConstants.FROM_EMAIL_ADDRESS_PARM_NM);
        if(StringUtils.isEmpty(returnAddress)) {
            returnAddress = mailService.getBatchMailingList();
        }
        message.setFromAddress(returnAddress);
        message.setSubject("Prepaid Checks File Load Success Notification - " + fileName);

        List<String> ccAddresses = new ArrayList<String>( parameterService.getParameterValuesAsString(LoadPaymentsStep.class, PdpParameterConstants.HARD_EDIT_CC) );
        message.getCcAddresses().addAll(ccAddresses);
        message.getBccAddresses().addAll(ccAddresses);
        
        CustomerProfile customer = customerProfileService.get(prepaidChecksFile.getChart(), prepaidChecksFile.getUnit(), prepaidChecksFile.getSubUnit());
        String toAddresses = StringUtils.deleteWhitespace(customer.getProcessingEmailAddr());
        List<String> toAddressList = Arrays.asList(toAddresses.split(","));

        message.getToAddresses().addAll(toAddressList);
       
        StringBuilder body = new StringBuilder();
        body.append(getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_FILE_LOADED) + "\n\n");
        super.addPaymentFieldsToBody(body, prepaidChecksFile.getBatchId().intValue(), prepaidChecksFile.getChart(), prepaidChecksFile.getUnit(), prepaidChecksFile.getSubUnit(), prepaidChecksFile.getCreationDate(), prepaidChecksFile.getPaymentCount(), prepaidChecksFile.getPaymentTotalAmount());

        body.append("\n" + getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_WARNING_MESSAGES) + "\n");
        for (String warning : warnings) {
            body.append(warning + "\n\n");
        }
        
        message.setMessage(body.toString());

        // KFSMI-6475 - if not a production instance, replace the recipients with the testers list
        super.alterMessageWhenNonProductionInstance(message, null);

        try {
            mailService.sendMessage(message);
        }
        catch (Exception e) {
        	 LOG.error("sendPrepaidChecksLoadEmail() Invalid email address. Message not sent", e);
             throw new RuntimeException("sendPrepaidChecksLoadEmail() Invalid email address. Message not sent " + e.getMessage(), e);             
        }        

    }

    public void sendPrepaidChecksErrorEmail(PaymentFileLoad prepaidChecksFile, MessageMap errors, String fileName) {    
        LOG.debug("sendPrepaidChecksErrorEmail() starting");

        // check email configuration
        if (!super.isPaymentEmailEnabled()) {
            return;
        }

        MailMessage message = new MailMessage();

        String returnAddress = parameterService.getParameterValueAsString(KfsParameterConstants.PRE_DISBURSEMENT_BATCH.class, KFSConstants.FROM_EMAIL_ADDRESS_PARM_NM);
        if(StringUtils.isEmpty(returnAddress)) {
            returnAddress = mailService.getBatchMailingList();
        }
        message.setFromAddress(returnAddress);
        message.setSubject("Prepaid Checks File Load ERROR Notification - " + fileName);

        StringBuilder body = new StringBuilder();
        List<String> ccAddresses = new ArrayList<String>( parameterService.getParameterValuesAsString(LoadPaymentsStep.class, PdpParameterConstants.HARD_EDIT_CC) );

        if (prepaidChecksFile == null) {
            if (ccAddresses.isEmpty()) {
                LOG.warn("sendPrepaidChecksErrorEmail() No HARD_EDIT_CC addresses.  No email sent");
                return;
            }

            message.getToAddresses().addAll(ccAddresses);

            body.append(getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_BAD_FILE_PARSE) + "\n\n");
        }
        else {       	
            CustomerProfile customer = customerProfileService.get(prepaidChecksFile.getChart(), prepaidChecksFile.getUnit(), prepaidChecksFile.getSubUnit());
            if (customer == null) {
                LOG.error("sendPrepaidChecksErrorEmail() Invalid Customer.  Sending email to CC addresses");

                if (ccAddresses.isEmpty()) {
                    LOG.error("sendPrepaidChecksErrorEmail() No HARD_EDIT_CC addresses.  No email sent");
                    return;
                }

                message.getToAddresses().addAll(ccAddresses);

                body.append(getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_INVALID_CUSTOMER) + "\n\n");
            }
            else {           	
                String toAddresses = StringUtils.deleteWhitespace(customer.getProcessingEmailAddr());
                List<String> toAddressList = Arrays.asList(toAddresses.split(","));

                message.getToAddresses().addAll(toAddressList);
                message.getCcAddresses().addAll(ccAddresses);
                //TODO: for some reason the mail service does not work unless the bcc list has addresss. This is a temporary workaround
                message.getBccAddresses().addAll(ccAddresses);
            }
        }
              
        if (prepaidChecksFile != null) {
        	body.append(getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_FILE_NOT_LOADED) + "\n\n");
            addPaymentFieldsToBody(body, null, prepaidChecksFile.getChart(), prepaidChecksFile.getUnit(), prepaidChecksFile.getSubUnit(), prepaidChecksFile.getCreationDate(), prepaidChecksFile.getPaymentCount(), prepaidChecksFile.getPaymentTotalAmount());
        }

        body.append("\n" + getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_ERROR_MESSAGES) + "\n");
        List<ErrorMessage> errorMessages = errors.getMessages(KFSConstants.GLOBAL_ERRORS);
        if (errorMessages != null) {
            for (ErrorMessage errorMessage : errorMessages) {
                body.append(getMessage(errorMessage.getErrorKey(), (Object[]) errorMessage.getMessageParameters()) + "\n\n");
            }

            message.setMessage(body.toString());

            // KFSMI-6475 - if not a production instance, replace the recipients with the testers list
            super.alterMessageWhenNonProductionInstance(message, null);

            try {
                mailService.sendMessage(message);
            }
            catch (Exception e) {
            	LOG.error("sendPrepaidChecksErrorEmail() Invalid email address.  Message not sent", e);
                throw new RuntimeException("sendPrepaidChecksLoadEmail() Invalid email address. Message not sent " + e.getMessage(), e);                
            }
        }
               
    }
         
    public void setCustomerProfileService(CustomerProfileService customerProfileService) {
    	  this.customerProfileService = customerProfileService;
          super.setCustomerProfileService(customerProfileService);
    }
  
    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
        super.setConfigurationService(kualiConfigurationService);
    }
    
    public void setMailService(MailService mailService) {
        this.mailService = mailService;
        super.setMailService(mailService);
    }
    
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
        super.setParameterService(parameterService);        
    }
    
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
        super.setDataDictionaryService(dataDictionaryService);
    }
   
    public void setAchBankService(AchBankService achBankService) {
        this.achBankService = achBankService;
        super.setAchBankService(achBankService);
    }
    
    @Override
    public void sendAchAdviceEmail(PaymentGroup paymentGroup, PaymentDetail paymentDetail, CustomerProfile customer) {
        LOG.debug("sendAchAdviceEmail() starting");

        MailMessage message = new MailMessage();
        String fromAddresses = customer.getAdviceReturnEmailAddr();
        String toAddresses = paymentGroup.getAdviceEmailAddress();
        Collection<String> ccAddresses = parameterService.getParameterValuesAsString(SendAchAdviceNotificationsStep.class, PdpParameterConstants.ACH_SUMMARY_CC_EMAIL_ADDRESSES_PARMAETER_NAME);
        Collection<String> bccAddresses = parameterService.getParameterValuesAsString(SendAchAdviceNotificationsStep.class, PdpParameterConstants.ACH_SUMMARY_BCC_EMAIL_ADDRESSES_PARMAETER_NAME);
        String batchAddresses = mailService.getBatchMailingList();
        String subject = customer.getAdviceSubjectLine();

        message.addToAddress(toAddresses);
        if(!ccAddresses.isEmpty()){
            message.getCcAddresses().addAll(ccAddresses);
        }
        if(!bccAddresses.isEmpty()){
            message.getBccAddresses().addAll(bccAddresses);
        }
        message.setFromAddress(fromAddresses);
        message.setSubject(subject);

        if (LOG.isDebugEnabled()) {
            LOG.debug("sending email to " + toAddresses + " for disb # " + paymentGroup.getDisbursementNbr());
        }

        StringBuilder body = new StringBuilder();
        body.append(getMessage(PdpKeyConstants.MESSAGE_PDP_ACH_ADVICE_EMAIL_TOFROM, paymentGroup.getPayeeName(), customer.getAchPaymentDescription()));

        // formatter for payment amounts
        Formatter formatter = new CurrencyFormatter();

        // get bank name to which the payment is being transferred
        String bankName = "";

        ACHBank achBank = achBankService.getByPrimaryId(paymentGroup.getAchBankRoutingNbr());
        if (achBank == null) {
            LOG.error("Bank cound not be found for routing number " + paymentGroup.getAchBankRoutingNbr());
        }
        else {
            bankName = achBank.getBankName();
        }

        body.append(getMessage(PdpKeyConstants.MESSAGE_PDP_ACH_ADVICE_EMAIL_BANKAMOUNT, bankName, formatter.formatForPresentation(paymentDetail.getNetPaymentAmount())));

        // print detail amounts
        int labelPad = 25;

        String newPaymentAmountLabel = dataDictionaryService.getAttributeLabel(PaymentDetail.class, PdpPropertyConstants.PaymentDetail.PAYMENT_NET_AMOUNT);
        body.append(StringUtils.rightPad(newPaymentAmountLabel, labelPad) + formatter.formatForPresentation(paymentDetail.getNetPaymentAmount()) + "\n");

        if (paymentDetail.getOrigInvoiceAmount().isNonZero()) {
            String origInvoiceAmountLabel = dataDictionaryService.getAttributeLabel(PaymentDetail.class, PdpPropertyConstants.PaymentDetail.PAYMENT_ORIGINAL_INVOICE_AMOUNT);
            body.append(StringUtils.rightPad(origInvoiceAmountLabel, labelPad) + formatter.formatForPresentation(paymentDetail.getOrigInvoiceAmount()) + "\n");
        }

        if (paymentDetail.getInvTotDiscountAmount().isNonZero()) {
            String invTotDiscountAmountLabel = dataDictionaryService.getAttributeLabel(PaymentDetail.class, PdpPropertyConstants.PaymentDetail.PAYMENT_INVOICE_TOTAL_DISCOUNT_AMOUNT);
            body.append(StringUtils.rightPad(invTotDiscountAmountLabel, labelPad) + formatter.formatForPresentation(paymentDetail.getInvTotDiscountAmount()) + "\n");
        }

        if (paymentDetail.getInvTotShipAmount().isNonZero()) {
            String invTotShippingAmountLabel = dataDictionaryService.getAttributeLabel(PaymentDetail.class, PdpPropertyConstants.PaymentDetail.PAYMENT_INVOICE_TOTAL_SHIPPING_AMOUNT);
            body.append(StringUtils.rightPad(invTotShippingAmountLabel, labelPad) + formatter.formatForPresentation(paymentDetail.getInvTotShipAmount()) + "\n");
        }

        if (paymentDetail.getInvTotOtherDebitAmount().isNonZero()) {
            String invTotOtherDebitAmountLabel = dataDictionaryService.getAttributeLabel(PaymentDetail.class, PdpPropertyConstants.PaymentDetail.PAYMENT_INVOICE_TOTAL_OTHER_DEBIT_AMOUNT);
            body.append(StringUtils.rightPad(invTotOtherDebitAmountLabel, labelPad) + formatter.formatForPresentation(paymentDetail.getInvTotOtherDebitAmount()) + "\n");
        }

        if (paymentDetail.getInvTotOtherCreditAmount().isNonZero()) {
            String invTotOtherCreditAmountLabel = dataDictionaryService.getAttributeLabel(PaymentDetail.class, PdpPropertyConstants.PaymentDetail.PAYMENT_INVOICE_TOTAL_OTHER_CREDIT_AMOUNT);
            body.append(StringUtils.rightPad(invTotOtherCreditAmountLabel, labelPad) + formatter.formatForPresentation(paymentDetail.getInvTotOtherCreditAmount()) + "\n");
        }

        body.append("\n" + customer.getAdviceHeaderText() + "\n");

        if (StringUtils.isNotBlank(paymentDetail.getPurchaseOrderNbr())) {
            String purchaseOrderNbrLabel = dataDictionaryService.getAttributeLabel(PaymentDetail.class, PdpPropertyConstants.PaymentDetail.PAYMENT_PURCHASE_ORDER_NUMBER);
            body.append(StringUtils.rightPad(purchaseOrderNbrLabel, labelPad) + paymentDetail.getPurchaseOrderNbr() + "\n");
        }

        if (StringUtils.isNotBlank(paymentDetail.getInvoiceNbr())) {
            String invoiceNbrLabel = dataDictionaryService.getAttributeLabel(PaymentDetail.class, PdpPropertyConstants.PaymentDetail.PAYMENT_INVOICE_NUMBER);
            body.append(StringUtils.rightPad(invoiceNbrLabel, labelPad) + paymentDetail.getInvoiceNbr() + "\n");
        }

        if (StringUtils.isNotBlank(paymentDetail.getCustPaymentDocNbr())) {
            String custPaymentDocNbrLabel = dataDictionaryService.getAttributeLabel(PaymentDetail.class, PdpPropertyConstants.PaymentDetail.PAYMENT_CUSTOMER_DOC_NUMBER);
            body.append(StringUtils.rightPad(custPaymentDocNbrLabel, labelPad) + paymentDetail.getCustPaymentDocNbr() + "\n");
        }

        if (StringUtils.isNotBlank(paymentDetail.getCustomerInstitutionNumber())) {
            String customerInstituitionNbrLabel = dataDictionaryService.getAttributeLabel(PaymentGroup.class, PdpPropertyConstants.CUSTOMER_INSTITUTION_NUMBER);
            body.append(StringUtils.rightPad(customerInstituitionNbrLabel, labelPad) + paymentDetail.getCustomerInstitutionNumber() + "\n");
        }

        body.append("\n");

        // print payment notes
        for (PaymentNoteText paymentNoteText : paymentDetail.getNotes()) {
            body.append(paymentNoteText.getCustomerNoteText() + "\n");
        }

        if (paymentDetail.getNotes().isEmpty()) {
            body.append(getMessage(PdpKeyConstants.MESSAGE_PDP_ACH_ADVICE_EMAIL_NONOTES));
        }

        message.setMessage(body.toString());

        // KFSMI-6475 - if not a production instance, replace the recipients with the testers list
        alterMessageWhenNonProductionInstance(message, null);

        try {
            mailService.sendMessage(message);
        }
        catch (Exception e) {
            LOG.error("sendAchAdviceEmail() Invalid email address. Sending message to " + customer.getAdviceReturnEmailAddr(), e);

            // send notification to advice return address with payment details- need a new MailMessage here of course
            MailMessage msg = new MailMessage();
            msg.addToAddress(customer.getAdviceReturnEmailAddr());

            String returnAddress = parameterService.getParameterValueAsString(KFSConstants.ParameterNamespaces.PDP, "Batch", KFSConstants.FROM_EMAIL_ADDRESS_PARM_NM);
            if(StringUtils.isEmpty(returnAddress)) {
                returnAddress = mailService.getBatchMailingList();
            }
            msg.setFromAddress(returnAddress);
            msg.setSubject(getMessage(PdpKeyConstants.MESSAGE_PDP_ACH_ADVICE_INVALID_EMAIL_ADDRESS));

            LOG.warn("bouncing email to " + customer.getAdviceReturnEmailAddr() + " for disb # " + paymentGroup.getDisbursementNbr());
            // KFSMI-6475 - if not a production instance, replace the recipients with the testers list
            alterMessageWhenNonProductionInstance(msg, null);

            try {
                mailService.sendMessage(msg);
            }
            catch (Exception e1) {
                LOG.error("Could not send email to advice return email address on customer profile: " + customer.getAdviceReturnEmailAddr(), e1);
                throw new RuntimeException("Could not send email to advice return email address on customer profile: " + customer.getAdviceReturnEmailAddr());
            }
        }
    }
}
