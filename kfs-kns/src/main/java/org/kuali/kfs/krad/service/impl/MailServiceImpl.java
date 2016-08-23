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
package org.kuali.kfs.krad.service.impl;

import org.kuali.kfs.krad.exception.InvalidAddressException;
import org.kuali.kfs.krad.service.MailService;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.mail.EmailBcList;
import org.kuali.rice.core.api.mail.EmailBody;
import org.kuali.rice.core.api.mail.EmailCcList;
import org.kuali.rice.core.api.mail.EmailFrom;
import org.kuali.rice.core.api.mail.EmailSubject;
import org.kuali.rice.core.api.mail.EmailToList;
import org.kuali.rice.core.api.mail.MailMessage;
import org.kuali.rice.core.api.mail.Mailer;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;

public class MailServiceImpl implements MailService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MailServiceImpl.class);

    protected Mailer mailer;
    protected String batchMailingList;
    protected String nonProductionNotificationMailingList;
    protected boolean realNotificationsEnabled = true;
    protected ConfigurationService configurationService;

    @Override
    public void sendMessage(MailMessage message) throws InvalidAddressException, MessagingException {
        LOG.debug("sendMessage() started");

        sendMessage(message, false);
    }

    @Override
    public void sendMessage(MailMessage message, boolean htmlMessage) throws InvalidAddressException, MessagingException {
        LOG.debug("sendMessage() started");

        MailMessage mm = modifyForNonProduction(message, htmlMessage);
        mm.setSubject(messageSubject(message.getSubject()));

        List bccAddresses = new ArrayList<String>();
        bccAddresses.addAll(mm.getBccAddresses());

        List ccAddresses = new ArrayList<String>();
        ccAddresses.addAll(mm.getCcAddresses());

        List toAddresses = new ArrayList<String>();
        toAddresses.addAll(mm.getToAddresses());

        mailer.sendEmail(new EmailFrom(mm.getFromAddress()), new EmailToList(toAddresses), new EmailSubject(mm.getSubject()), new EmailBody(mm.getMessage()), new EmailCcList(ccAddresses), new EmailBcList(bccAddresses), htmlMessage);
    }

    @Override
    public String getBatchMailingList() {
        LOG.debug("getBatchMailingList() started");

        if (realNotificationsEnabled) {
            return batchMailingList;
        } else {
            return nonProductionNotificationMailingList;
        }
    }

    protected String messageSubject(String subject) {
        String app = configurationService.getPropertyValueAsString(CoreConstants.Config.APPLICATION_ID);
        String env = configurationService.getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY);
        return app + " " + env + ": " + subject;
    }

    protected MailMessage modifyForNonProduction(MailMessage originalMessage, boolean htmlMessage) {

        if (realNotificationsEnabled) {
            return originalMessage;
        }

        MailMessage modifiedMessage = new MailMessage();
        modifiedMessage.setFromAddress(originalMessage.getFromAddress());
        modifiedMessage.setSubject(originalMessage.getSubject());

        StringBuilder buf = new StringBuilder();
        String newLine = htmlMessage ? "<br/>\n" : "\n";
        buf.append("Email To: ").append(originalMessage.getToAddresses()).append(newLine);
        buf.append("Email CC: ").append(originalMessage.getCcAddresses()).append(newLine);
        buf.append("Email BCC: ").append(originalMessage.getBccAddresses()).append(newLine + newLine);
        buf.append(originalMessage.getMessage());
        modifiedMessage.setMessage(buf.toString());

        modifiedMessage.addToAddress(nonProductionNotificationMailingList);
        return modifiedMessage;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public void setMailer(Mailer mailer) {
        this.mailer = mailer;
    }

    public void setRealNotificationsEnabled(boolean realNotificationsEnabled) {
        this.realNotificationsEnabled = realNotificationsEnabled;
    }

    public void setNonProductionNotificationMailingList(String nonProductionNotificationMailingList) {
        this.nonProductionNotificationMailingList = nonProductionNotificationMailingList;
    }

    public void setBatchMailingList(String batchMailingList) {
        this.batchMailingList = batchMailingList;
    }
}
