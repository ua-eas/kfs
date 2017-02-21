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
package org.kuali.kfs.sys.service.impl;

import org.apache.commons.lang.Validate;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.sys.mail.MailMessage;
import org.kuali.kfs.sys.service.EmailService;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;

public class EmailServiceImpl implements EmailService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EmailServiceImpl.class);

    protected static final String PARAM_NON_PRODUCTION_EMAIL_MODE = "NON_PRODUCTION_EMAIL_MODE";
    protected static final String PARAM_FROM_EMAIL_ADDRESS = "FROM_EMAIL_ADDRESS";
    protected static final String PARAM_DEFAULT_TO_EMAIL_ADDRESS = "DEFAULT_TO_EMAIL_ADDRESS";
    protected static final String PARAM_NON_PRODUCTION_TO_EMAIL_ADDRESS = "NON_PRODUCTION_TO_EMAIL_ADDRESS";

    protected static final String MODE_LOG = "L";
    protected static final String MODE_TEST = "T";
    protected static final String MODE_PROD = "P";

    protected JavaMailSender javaMailSender;
    protected ConfigurationService configurationService;
    protected ParameterService parameterService;

    // L - Log email
    // T - email to test account
    // P - production mode

    // If production, send email
    // If not, check parameter
    //   Log email or
    //   Email to test account or
    //   send email normally

    @Override
    public String getFromAddress() {
        return parameterService.getParameterValueAsString("KFS-SYS","All",PARAM_FROM_EMAIL_ADDRESS);
    }

    @Override
    public String getDefaultToAddress() {
        return parameterService.getParameterValueAsString("KFS-SYS","All",PARAM_DEFAULT_TO_EMAIL_ADDRESS);
    }

    @Override
    public void sendMessage(MailMessage message, boolean htmlMessage) {
        LOG.debug("sendMessage() started");

        // Note this is using org.apache.commons.lang instead of org.apache.commons.lang3
        // The two versions throw different exceptions.
        Validate.notEmpty(message.getToAddresses(),"No To Addresses");
        Validate.notEmpty(message.getFromAddress(), "No From Address");

        String emailMode = getMode();

        if (MODE_LOG.equals(emailMode)) {
            logMessage(message);
            return;
        }

        if (MODE_TEST.equals(emailMode)) {
            modifyMessageForTestMode(message);
        }
        message.setSubject(modifyMessageSubject(message.getSubject()));

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, message.getAttachmentContent() != null);
            helper.setTo(message.getToAddresses().toArray(new String[message.getToAddresses().size()]));
            helper.setBcc(message.getBccAddresses().toArray(new String[message.getBccAddresses().size()]));
            helper.setCc(message.getCcAddresses().toArray(new String[message.getCcAddresses().size()]));
            helper.setSubject(message.getSubject());
            helper.setFrom(message.getFromAddress());
            helper.setText(message.getMessage(), htmlMessage);

            if ( message.getAttachmentContent() != null ) {
                helper.addAttachment(message.getAttachmentFileName(),new ByteArrayResource(message.getAttachmentContent()),message.getAttachmentContentType());
            }

            javaMailSender.send(mimeMessage);
        } catch (MailException|MessagingException e) {
            LOG.error("sendMessage() Unable to send email",e);
            throw new RuntimeException("Unable to send email",e);
        }
    }

    protected String modifyMessageSubject(String subject) {
        String app = configurationService.getPropertyValueAsString(CoreConstants.Config.APPLICATION_ID);
        String env = configurationService.getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY);
        return app + " " + env + ": " + subject;
    }

    protected void modifyMessageForTestMode(MailMessage message) {
        LOG.debug("modifyMessageForTestMode() started");

        message.setBccAddresses(new HashSet<>());
        message.setCcAddresses(new HashSet<>());
        message.setToAddresses(Collections.singleton(getNonProductionToEmailAddress()));
    }

    protected void logMessage(MailMessage message) {
        LOG.info("logMessage() Send email to: " + message.getToAddresses().stream().collect(Collectors.joining(",")));
        if ( ! message.getCcAddresses().isEmpty() ) {
            LOG.info("logMessage() CC: " + message.getCcAddresses().stream().collect(Collectors.joining(",")));
        }
        if ( ! message.getBccAddresses().isEmpty() ) {
            LOG.info("logMessage() BCC: " + message.getBccAddresses().stream().collect(Collectors.joining(",")));
        }
        LOG.info("logMessage() Subject: " + message.getSubject());
    }

    protected String getNonProductionToEmailAddress() {
        return parameterService.getParameterValueAsString("KFS-SYS","All",PARAM_NON_PRODUCTION_TO_EMAIL_ADDRESS);
    }

    protected String getMode() {
        if ( isProduction() ) {
            return MODE_PROD;
        }

        String nonProductionMode = parameterService.getParameterValueAsString("KFS-SYS","All",PARAM_NON_PRODUCTION_EMAIL_MODE);
        if ( ! (MODE_PROD.equals(nonProductionMode) || MODE_TEST.equals(nonProductionMode) || MODE_LOG.equals(nonProductionMode)) ) {
            LOG.error("getMode() Invalid parameter value for " + PARAM_NON_PRODUCTION_EMAIL_MODE + ": " + nonProductionMode);
            return MODE_LOG;
        }

        return nonProductionMode;
    }

    protected boolean isProduction() {
        String productionEnvironmentCode = configurationService.getPropertyValueAsString("production.environment.code");
        String currentEnvironment = configurationService.getPropertyValueAsString("environment");

        return productionEnvironmentCode.toLowerCase().equals(currentEnvironment.toLowerCase());
    }

    public void setJavaMailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
