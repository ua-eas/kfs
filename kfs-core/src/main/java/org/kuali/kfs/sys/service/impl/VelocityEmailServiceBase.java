/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.kfs.sys.service.impl;

import org.apache.velocity.app.VelocityEngine;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.MailService;
import org.kuali.kfs.sys.service.VelocityEmailService;
import org.kuali.rice.core.api.mail.MailMessage;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.util.Collection;
import java.util.Map;

public abstract class VelocityEmailServiceBase implements VelocityEmailService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(VelocityEmailServiceBase.class);

    protected MailService mailService;
    protected ParameterService parameterService;
    protected VelocityEngine velocityEngine;
    protected BusinessObjectService businessObjectService;
    protected boolean htmlMessage;

    /**
     * @see org.kuali.kfs.sys.service.VelocityEmailService#sendEmailNotification(java.util.Map)
     */
    @Override
    public void sendEmailNotification(final Map<String, Object> templateVariables) {
        LOG.debug("sendEmailNotification() started");

        // Allow template variables can be retrieved from extending class
        try {
            final MailMessage mailMessage = constructMailMessage(templateVariables);

            mailService.sendMessage(mailMessage,htmlMessage);
        } catch (Exception ex) {
            LOG.error("Exception received when send email ", ex);
            throw new RuntimeException("Unable to send email",ex);
        }
    }

    protected void setAndSplitEmailAddress(Collection<String> emailReceiver, MailMessage message) {
        emailReceiver.stream().forEach(r -> message.addToAddress(r));
    }

    protected void setAndSplitCcEmailReceivers(Collection<String> ccEmailReceivers, MailMessage message) {
        ccEmailReceivers.stream().forEach(r -> message.addCcAddress(r));
    }

    protected void setAndSplitBccEmailReceivers(Collection<String> bccEmailReceivers, MailMessage message) {
        bccEmailReceivers.stream().forEach(r -> message.addBccAddress(r));
    }

    /**
     * Set up MailMessage
     *
     * @return
     */
    protected MailMessage constructMailMessage(final Map<String, Object> templateVariables) {
        MailMessage message = new MailMessage();
        // from...
        message.setFromAddress(mailService.getBatchMailingList());

        Collection<String> emailReceivers;
        message.setSubject(getEmailSubject());
        emailReceivers = getProdEmailReceivers();
        if (emailReceivers != null && !emailReceivers.isEmpty()) {
            setAndSplitEmailAddress(emailReceivers, message);
        }

        emailReceivers = getCcEmailReceivers();
        if (emailReceivers != null && !emailReceivers.isEmpty()) {
            setAndSplitCcEmailReceivers(emailReceivers, message);
        }

        emailReceivers = getBccEmailReceivers();
        if (emailReceivers != null && !emailReceivers.isEmpty()) {
            setAndSplitBccEmailReceivers(emailReceivers, message);
        }

        String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, getTemplateUrl(), templateVariables);
        message.setMessage(body);
        return message;
    }

    @Override
    public Collection<String> getProdEmailReceivers() {
        return null;
    }

    @Override
    public Collection<String> getCcEmailReceivers() {
        return null;
    }

    @Override
    public Collection<String> getBccEmailReceivers() {
        return null;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    public void setHtmlMessage(boolean htmlMessage) {
        this.htmlMessage = htmlMessage;
    }
}
