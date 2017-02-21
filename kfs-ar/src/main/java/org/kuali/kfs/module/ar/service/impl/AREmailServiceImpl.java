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
package org.kuali.kfs.module.ar.service.impl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.kns.service.DataDictionaryService;
import org.kuali.kfs.krad.bo.Attachment;
import org.kuali.kfs.krad.bo.Note;
import org.kuali.kfs.krad.exception.InvalidAddressException;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.DocumentService;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.NoteService;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.batch.UpcomingMilestoneNotificationStep;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.businessobject.InvoiceAddressDetail;
import org.kuali.kfs.module.ar.businessobject.Milestone;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.service.AREmailService;
import org.kuali.kfs.module.ar.service.ContractsGrantsBillingUtilityService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.mail.BodyMailMessage;
import org.kuali.kfs.sys.service.EmailService;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;

import javax.mail.MessagingException;
import java.io.IOException;
import java.sql.Date;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class AREmailServiceImpl implements AREmailService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AREmailServiceImpl.class);

    protected ParameterService parameterService;
    protected DataDictionaryService dataDictionaryService;
    protected ConfigurationService kualiConfigurationService;
    protected BusinessObjectService businessObjectService;
    protected DocumentService documentService;
    protected NoteService noteService;
    protected KualiModuleService kualiModuleService;
    protected ContractsGrantsBillingUtilityService contractsGrantsBillingUtilityService;
    protected EmailService emailService;

    /**
     * This method is used to send emails to the agency
     *
     * @param invoices
     */
    @Override
    public boolean sendInvoicesViaEmail(Collection<ContractsGrantsInvoiceDocument> invoices) throws InvalidAddressException, MessagingException {
        LOG.debug("sendInvoicesViaEmail() starting.");

        boolean success = true;

        for (ContractsGrantsInvoiceDocument invoice : invoices) {
            List<InvoiceAddressDetail> invoiceAddressDetails = invoice.getInvoiceAddressDetails();
            for (InvoiceAddressDetail invoiceAddressDetail : invoiceAddressDetails) {
                if (ArConstants.InvoiceTransmissionMethod.EMAIL.equals(invoiceAddressDetail.getInvoiceTransmissionMethodCode())) {
                    Note note = noteService.getNoteByNoteId(invoiceAddressDetail.getNoteId());

                    if (ObjectUtils.isNotNull(note)) {
                        BodyMailMessage message = new BodyMailMessage();

                        String sender = parameterService.getParameterValueAsString(KFSConstants.OptionalModuleNamespaces.ACCOUNTS_RECEIVABLE, ArConstants.CONTRACTS_GRANTS_INVOICE_COMPONENT, ArConstants.FROM_EMAIL_ADDRESS);
                        message.setFromAddress(sender);

                        CustomerAddress customerAddress = invoiceAddressDetail.getCustomerAddress();
                        String recipients = invoiceAddressDetail.getCustomerEmailAddress();
                        if (StringUtils.isNotEmpty(recipients)) {
                            message.getToAddresses().add(recipients);
                        } else {
                            LOG.warn("sendInvoicesViaEmail() No recipients indicated.");
                        }

                        String subject = getSubject(invoice);
                        message.setSubject(subject);
                        if (StringUtils.isEmpty(subject)) {
                            LOG.warn("sendInvoicesViaEmail() Empty subject being sent.");
                        }

                        String bodyText = getMessageBody(invoice, customerAddress);
                        message.setMessage(bodyText);
                        if (StringUtils.isEmpty(bodyText)) {
                            LOG.warn("sendInvoicesViaEmail() Empty bodyText being sent.");
                        }

                        Attachment attachment = note.getAttachment();
                        if (ObjectUtils.isNotNull(attachment)) {
                            try {
                                message.setAttachmentContent(IOUtils.toByteArray(attachment.getAttachmentContents()));
                            } catch (IOException ex) {
                                LOG.error("Error setting attachment contents", ex);
                                throw new RuntimeException(ex);
                            }
                            message.setAttachmentFileName(attachment.getAttachmentFileName());
                            message.setAttachmentContentType(attachment.getAttachmentMimeTypeCode());
                        }

                        emailService.sendMessage(message,false);

                        invoiceAddressDetail.setInitialTransmissionDate(new Date(new java.util.Date().getTime()));
                        documentService.updateDocument(invoice);
                    } else {
                        success = false;
                    }
                }
            }
        }
        return success;
    }

    protected String getSubject(ContractsGrantsInvoiceDocument invoice) {
        String subject = kualiConfigurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_EMAIL_SUBJECT);

        return MessageFormat.format(subject, invoice.getInvoiceGeneralDetail().getAward().getProposal().getGrantNumber(),
            invoice.getInvoiceGeneralDetail().getProposalNumber(),
            invoice.getDocumentNumber());
    }

    protected String getMessageBody(ContractsGrantsInvoiceDocument invoice, CustomerAddress customerAddress) {
        String message = kualiConfigurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_EMAIL_BODY);

        String department = "";
        String[] orgCode = invoice.getInvoiceGeneralDetail().getAward().getAwardPrimaryFundManager().getFundManager().getPrimaryDepartmentCode().split("-");
        Map<String, Object> key = new HashMap<String, Object>();
        key.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, orgCode[0].trim());
        key.put(KFSPropertyConstants.ORGANIZATION_CODE, orgCode[1].trim());
        Organization org = businessObjectService.findByPrimaryKey(Organization.class, key);
        if (ObjectUtils.isNotNull(org)) {
            department = org.getOrganizationName();
        }

        return MessageFormat.format(message, customerAddress.getCustomer().getCustomerName(),
            customerAddress.getCustomerAddressName(),
            invoice.getInvoiceGeneralDetail().getAward().getAwardPrimaryFundManager().getFundManager().getName(),
            invoice.getInvoiceGeneralDetail().getAward().getAwardPrimaryFundManager().getProjectTitle(),
            department,
            invoice.getInvoiceGeneralDetail().getAward().getAwardPrimaryFundManager().getFundManager().getPhoneNumber(),
            invoice.getInvoiceGeneralDetail().getAward().getAwardPrimaryFundManager().getFundManager().getEmailAddress());
    }

    /**
     * This method sends out emails for upcoming milestones.
     */
    @Override
    public void sendEmailNotificationsForMilestones(List<Milestone> milestones, ContractsAndGrantsBillingAward award) {
        LOG.debug("sendEmailNotificationsForMilestones() started");

        BodyMailMessage message = new BodyMailMessage();

        message.setFromAddress(emailService.getFromAddress());
        message.setSubject(getEmailSubject(ArConstants.REMINDER_EMAIL_SUBJECT));
        message.getToAddresses().add(award.getAwardPrimaryFundManager().getFundManager().getEmailAddress());
        StringBuffer body = new StringBuffer();

        String messageKey = kualiConfigurationService.getPropertyValueAsString(ArKeyConstants.MESSAGE_CG_UPCOMING_MILESTONES_EMAIL_LINE_1);
        body.append(messageKey + "\n\n");

        for (Milestone milestone : milestones) {
            String proposalNumber = dataDictionaryService.getAttributeLabel(Milestone.class, KFSPropertyConstants.PROPOSAL_NUMBER);
            String milestoneNumber = dataDictionaryService.getAttributeLabel(Milestone.class, ArPropertyConstants.MilestoneFields.MILESTONE_NUMBER);
            String milestoneDescription = dataDictionaryService.getAttributeLabel(Milestone.class, ArPropertyConstants.MilestoneFields.MILESTONE_DESCRIPTION);
            String milestoneAmount = dataDictionaryService.getAttributeLabel(Milestone.class, ArPropertyConstants.MilestoneFields.MILESTONE_AMOUNT);
            String milestoneExpectedCompletionDate = dataDictionaryService.getAttributeLabel(Milestone.class, ArPropertyConstants.MilestoneFields.MILESTONE_EXPECTED_COMPLETION_DATE);

            body.append(proposalNumber + ": " + milestone.getProposalNumber() + " \n");
            body.append(milestoneNumber + ": " + milestone.getMilestoneNumber() + " \n");
            body.append(milestoneDescription + ": " + milestone.getMilestoneDescription() + " \n");
            body.append(milestoneAmount + ": " + milestone.getMilestoneAmount() + " \n");
            body.append(milestoneExpectedCompletionDate + ": " + milestone.getMilestoneExpectedCompletionDate() + " \n");

            body.append("\n\n");
        }
        body.append("\n\n");

        messageKey = kualiConfigurationService.getPropertyValueAsString(ArKeyConstants.MESSAGE_CG_UPCOMING_MILESTONES_EMAIL_LINE_2);
        body.append(MessageFormat.format(messageKey, new Object[]{null}) + "\n\n");

        message.setMessage(body.toString());

        emailService.sendMessage(message,false);
    }

    /**
     * Retrieves the email subject text from system parameter then checks environment code and prepends to message if not
     * production.
     *
     * @param subjectParameterName name of parameter giving the subject text
     * @return subject text
     */
    protected String getEmailSubject(String subjectParameterName) {
        return parameterService.getParameterValueAsString(UpcomingMilestoneNotificationStep.class, subjectParameterName);
    }

    protected Properties getConfigProperties() {
        return ConfigContext.getCurrentContextConfig().getProperties();
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

    public void setKualiConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setContractsGrantsBillingUtilityService(ContractsGrantsBillingUtilityService contractsGrantsBillingUtilityService) {
        this.contractsGrantsBillingUtilityService = contractsGrantsBillingUtilityService;
    }

    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }
}
