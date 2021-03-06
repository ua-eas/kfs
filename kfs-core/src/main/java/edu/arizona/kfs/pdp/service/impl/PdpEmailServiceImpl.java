package edu.arizona.kfs.pdp.service.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.kuali.kfs.sys.report.BusinessObjectReportHelper;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.mail.MailMessage;
import org.kuali.rice.core.web.format.CurrencyFormatter;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.exception.InvalidAddressException;
import org.kuali.rice.krad.service.MailService;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;

import edu.arizona.kfs.pdp.service.PdpEmailService;

public class PdpEmailServiceImpl extends org.kuali.kfs.pdp.service.impl.PdpEmailServiceImpl implements PdpEmailService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PdpEmailServiceImpl.class);
   
    protected CustomerProfileService customerProfileService;
    protected ConfigurationService kualiConfigurationService;
    protected MailService mailService;
    protected ParameterService parameterService;
    protected DataDictionaryService dataDictionaryService;
    protected AchBankService achBankService;
    protected BusinessObjectReportHelper paymentDetailReportHelper;
    protected Set<String> knownEmailDomains;
    
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
    
    @Override
    public void sendAchAdviceEmail(PaymentGroup paymentGroup, PaymentDetail paymentDetail, CustomerProfile customer) {
        LOG.debug("sendAchAdviceEmail() starting");
        
        if(ObjectUtils.isNotNull(paymentDetail)){
            super.sendAchAdviceEmail(paymentGroup, paymentDetail, customer);
            return;
        }
        
        MailMessage message = new MailMessage();
        String fromAddresses = customer.getAdviceReturnEmailAddr();
        String toAddresses = paymentGroup.getAdviceEmailAddress();
        Collection<String> ccAddresses = parameterService.getParameterValuesAsString(SendAchAdviceNotificationsStep.class, PdpParameterConstants.ACH_SUMMARY_CC_EMAIL_ADDRESSES_PARMAETER_NAME);
        Collection<String> bccAddresses = parameterService.getParameterValuesAsString(SendAchAdviceNotificationsStep.class, PdpParameterConstants.ACH_SUMMARY_BCC_EMAIL_ADDRESSES_PARMAETER_NAME);
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

        StringBuilder body = buildMessageBody(paymentGroup, customer);
        message.setMessage(body.toString());
                
        // KFSMI-6475 - if not a production instance, replace the recipients with the testers list
        super.alterMessageWhenNonProductionInstance(message, null);
              
        try {
        	checkEmailAddressDomain(paymentGroup.getAdviceEmailAddress());
            mailService.sendMessage(message);
        }
        catch (Exception e) {
            toAddresses = customer.getAdviceReturnEmailAddr();
            if(StringUtils.isEmpty(toAddresses)) {
            	toAddresses = mailService.getBatchMailingList();
            }
            message.setToAddresses(new HashSet());
            message.addToAddress(toAddresses);
            LOG.error("sendAchAdviceEmail() Invalid email address. Sending message to " + toAddresses, e);

            String returnAddress = parameterService.getParameterValueAsString(KfsParameterConstants.PRE_DISBURSEMENT_BATCH.class, KFSConstants.FROM_EMAIL_ADDRESS_PARM_NM);
            if(StringUtils.isEmpty(returnAddress)) {
                returnAddress = mailService.getBatchMailingList();
            }
            message.setFromAddress(returnAddress);
            message.setSubject(getMessage(PdpKeyConstants.MESSAGE_PDP_ACH_ADVICE_INVALID_EMAIL_ADDRESS));

            LOG.debug("bouncing email to " + customer.getAdviceReturnEmailAddr() + " for disb # " + paymentGroup.getDisbursementNbr());
            // KFSMI-6475 - if not a production instance, replace the recipients with the testers list
            super.alterMessageWhenNonProductionInstance(message, null);

            try {
                mailService.sendMessage(message);
            }
            catch (Exception e1) {
                LOG.error("Could not send email to advice return email address on customer profile: " + customer.getAdviceReturnEmailAddr(), e1);
                throw new RuntimeException("Could not send email to advice return email address on customer profile: " + customer.getAdviceReturnEmailAddr());
            }
        }
    }
    
    /**
     * build message body with the given payment group and customer profile
     * @param paymentGroup the given payment group
     * @param customer the given customer profile
     * @return message body built from the given payment group and customer profile
     */
    protected StringBuilder buildMessageBody(PaymentGroup paymentGroup, CustomerProfile customer) {
        Map<String, String> tableDefinition = paymentDetailReportHelper.getTableDefinition();
        Formatter formatter = new CurrencyFormatter();
                
        StringBuilder body = new StringBuilder();
        body.append(getMessage(PdpKeyConstants.MESSAGE_PDP_ACH_ADVICE_EMAIL_TOFROM, paymentGroup.getPayeeName(), customer.getAchPaymentDescription()));

        ACHBank achBank = achBankService.getByPrimaryId(paymentGroup.getAchBankRoutingNbr());
        if (ObjectUtils.isNull(achBank)) {
            LOG.error("Bank cound not be found for routing number " + paymentGroup.getAchBankRoutingNbr());
        }

        String bankName = ObjectUtils.isNull(achBank) ? StringUtils.EMPTY : achBank.getBankName();
        Object netPayment = formatter.formatForPresentation(paymentGroup.getNetPaymentAmount());
        String bankNetPaymentMessage = getMessage(PdpKeyConstants.MESSAGE_PDP_ACH_ADVICE_EMAIL_BANKAMOUNT, bankName, netPayment);
        body.append(bankNetPaymentMessage).append(BusinessObjectReportHelper.LINE_BREAK);
        
        String AdviceHeaderText = customer.getAdviceHeaderText();
        AdviceHeaderText = StringUtils.isBlank(AdviceHeaderText) ? StringUtils.EMPTY : AdviceHeaderText;
        body.append(AdviceHeaderText).append(BusinessObjectReportHelper.LINE_BREAK);
        
        String tableHeaderFormat = tableDefinition.get(KFSConstants.ReportConstants.TABLE_HEADER_LINE_KEY);
        String separatorLine = tableDefinition.get(KFSConstants.ReportConstants.SEPARATOR_LINE_KEY);
        String tableCellFormat = tableDefinition.get(KFSConstants.ReportConstants.TABLE_CELL_FORMAT_KEY);
        body.append(tableHeaderFormat);
        
        for (PaymentDetail payment : paymentGroup.getPaymentDetails()) {
            List<String> paymentPropertyList = paymentDetailReportHelper.getTableCellValues(payment, false);
            String paymentDetailLine = String.format(tableCellFormat, paymentPropertyList.toArray());
            body.append(paymentDetailLine);
        }
        
        body.append(separatorLine);
        return body;       
    }
    
    private void checkEmailAddressDomain(String emailAddress) throws InvalidAddressException {
        if (StringUtils.isNotBlank(emailAddress)) {
            int pos = emailAddress.indexOf('@');
            try {
                if (pos > -1) {
                    String domain = emailAddress.substring(pos+1).trim();
                    // check the domain name - no check needed for known domains
                    if (!knownEmailDomains.contains(domain.toLowerCase())) {
                        // see if we can find the email domain by name
                        InetAddress.getByName(domain);
                    }
                } 
                else {
                    throw new InvalidAddressException("email address " + emailAddress + " contains no '@' character");
                }
            }            
            catch (UnknownHostException ex) {
                throw new InvalidAddressException("invalid email domain name:  " + emailAddress.substring(pos+1));
            }
        } 
        else {
            throw new InvalidAddressException("email address is blank or null");
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
    
    public void setPaymentDetailReportHelper(BusinessObjectReportHelper paymentDetailReportHelper) {
        this.paymentDetailReportHelper = paymentDetailReportHelper;
    }
    
    public void setKnownEmailDomains(Set<String> knownEmailDomains) {
        this.knownEmailDomains = knownEmailDomains;
        
        if (LOG.isDebugEnabled() && (knownEmailDomains != null)) {
            for (String s : knownEmailDomains) {
                LOG.debug("known email domain: " + s);
            }
        }
    }
    
}
