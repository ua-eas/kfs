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
package org.kuali.kfs.krad.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.krad.exception.ExceptionIncident;
import org.kuali.kfs.krad.exception.KualiExceptionIncident;
import org.kuali.kfs.krad.service.KRADServiceLocator;
import org.kuali.kfs.krad.service.KualiExceptionIncidentService;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.rice.core.api.mail.MailMessage;
import org.kuali.rice.core.api.mail.Mailer;
import org.kuali.rice.kim.api.identity.Person;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Part of this refactor included moving the mailer and messageTemplate properties
 * and the emailReport and createMailMessage methods to the new parent class.
 */
public class KualiExceptionIncidentServiceImpl implements KualiExceptionIncidentService {
    private Logger LOG = Logger.getLogger(KualiExceptionIncidentServiceImpl.class);

    /**
     * This property must be defined in the base configuration file for specifying
     * the mailing list for the report to be sent.
     * <p>Example:
     * {@code <param name="KualiReporterServiceImpl.REPORT_MAIL_LIST">a@y,b@z</param>}
     */
    public static final String REPORT_MAIL_LIST = String.format("%s.REPORT_MAIL_LIST", KualiExceptionIncidentServiceImpl.class.getSimpleName());

    private Mailer mailer;
    private MailMessage messageTemplate;

    @Override
    public void emailReport(String subject, String message) throws Exception {
        LOG.debug("emailReport() started");

        if (mailer == null) {
            String errorMessage = "mailer property of KualiExceptionIncidentServiceImpl is null";
            LOG.fatal(errorMessage);
            throw new IllegalStateException(errorMessage);
        }

        MailMessage msg = createMailMessage(subject, message);
        mailer.sendEmail(msg);
    }

    /**
     * Creates an instance of MailMessage from the inputs using the given template.
     *
     * @param subject the subject line text
     * @param message the body of the email message
     * @return MailMessage
     * @throws IllegalStateException if the {@code REPORT_MAIL_LIST} is not set
     *                               or messageTemplate does not have ToAddresses already set.
     */
    @SuppressWarnings("unchecked")
    protected MailMessage createMailMessage(String subject, String message) throws Exception {
        LOG.debug("createMailMessage() started");

        MailMessage messageTemplate = this.getMessageTemplate();
        if (messageTemplate == null) {
            throw new IllegalStateException(String.format("%s.templateMessage is null or not set",
                this.getClass().getName()));
        }

        // Copy input message reference for creating an instance of mail message
        MailMessage msg = new MailMessage();
        msg.setFromAddress(this.getFromAddress());
        msg.setToAddresses(this.getToAddresses());
        msg.setBccAddresses(this.getBccAddresses());
        msg.setCcAddresses(this.getCcAddresses());
        msg.setSubject((subject == null) ? "" : subject);
        msg.setMessage((message == null) ? "" : message);

        if (LOG.isTraceEnabled()) {
            String lm = String.format("EXIT %s", (msg == null) ? "null" : msg.toString());
            LOG.trace(lm);
        }

        return msg;
    }

    protected String getFromAddress() {
        Person actualUser = GlobalVariables.getUserSession().getActualPerson();

        String fromEmail = actualUser.getEmailAddress();
        if (StringUtils.isNotBlank(fromEmail)) {
            return fromEmail;
        } else {
            return this.getMessageTemplate().getFromAddress();
        }
    }

    protected Set<String> getToAddresses() {
        // First check if message template already define mailing list
        Set<String> emails = this.getMessageTemplate().getToAddresses();
        if (emails == null || emails.isEmpty()) {
            String mailingList = KRADServiceLocator.getKualiConfigurationService().getPropertyValueAsString(this.getToAddressesPropertyName());
            if (StringUtils.isBlank(mailingList)) {
                String em = REPORT_MAIL_LIST + " is not set or messageTemplate does not have ToAddresses already set.";
                LOG.error(em);
                throw new IllegalStateException(em);
            } else {
                return new HashSet<>(Arrays.asList(StringUtils.split(mailingList,
                    KRADConstants.FIELD_CONVERSIONS_SEPARATOR)));
            }
        } else {
            return emails;
        }
    }

    protected Set<String> getCcAddresses() {
        return this.getMessageTemplate().getCcAddresses();
    }

    protected Set<String> getBccAddresses() {
        return this.getMessageTemplate().getBccAddresses();
    }

    public final void setMailer(Mailer mailer) {
        this.mailer = mailer;
    }

    public MailMessage getMessageTemplate() {
        return messageTemplate;
    }

    public void setMessageTemplate(MailMessage messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    protected String getToAddressesPropertyName() {
        return REPORT_MAIL_LIST;
    }

    @Override
    public void report(KualiExceptionIncident exceptionIncident) throws Exception {
        if (LOG.isTraceEnabled()) {
            String lm = String.format("ENTRY %s",
                (exceptionIncident == null) ? "null" : exceptionIncident.toString());
            LOG.trace(lm);
        }

        emailReport(
            exceptionIncident.getProperty(
                KualiExceptionIncident.EXCEPTION_REPORT_SUBJECT),
            exceptionIncident.getProperty(
                KualiExceptionIncident.EXCEPTION_REPORT_MESSAGE));

        if (LOG.isTraceEnabled()) {
            String lm = String.format("EXIT");
            LOG.trace(lm);
        }

    }

    /**
     * This method first separate a composite string of the format "string token string".
     * <p>Example: 1,2,a,b where ',' is the token
     *
     * @param s
     * @param token
     * @return
     */
    public List<String> split(String s, String token) {
        if (LOG.isTraceEnabled()) {
            String lm = String.format("ENTRY %s;%s", s, token);
            LOG.trace(lm);
        }

        String[] sarray = s.split(token);
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < sarray.length && sarray[i].length() > 0; i++) {
            list.add(sarray[i]);
        }

        if (LOG.isTraceEnabled()) {
            String lm = String.format("EXIT %s", list.toString());
            LOG.trace(lm);
        }

        return list;
    }

    @Override
    public KualiExceptionIncident getExceptionIncident(Exception exception,
                                                       Map<String, String> properties) {
        if (exception == null) {
            return getExceptionIncident(properties);
        }
        if (LOG.isTraceEnabled()) {
            String lm = String.format("ENTRY %s;%s", exception.getMessage(), properties.toString());
            LOG.trace(lm);
        }

        KualiExceptionIncident ei = new ExceptionIncident(exception, properties);

        if (LOG.isTraceEnabled()) {
            String lm = String.format("EXIT %s", ei.toProperties().toString());
            LOG.trace(lm);
        }

        return ei;
    }

    @Override
    public KualiExceptionIncident getExceptionIncident(Map<String, String> properties) {
        if (LOG.isTraceEnabled()) {
            String lm = String.format("ENTRY %s", properties.toString());
            LOG.trace(lm);
        }

        ExceptionIncident ei = new ExceptionIncident(properties);

        if (LOG.isTraceEnabled()) {
            String lm = String.format("EXIT %s", ei.toProperties().toString());
            LOG.trace(lm);
        }

        return ei;
    }

    /** Rice will implode if you remove this method even though it isn't used in KFS. */
    public void setIncidentMailingList(String incidentMailingList) {
        // This property must be defined (even though it isn't used) in the base configuration file for specifying
        // the mailing list for the report to be sent. Rice will complain if it's missing.
        // Example:
        // <param name="KualiReporterServiceImpl.REPORT_MAIL_LIST">a@y,b@z</param>
    }
}
