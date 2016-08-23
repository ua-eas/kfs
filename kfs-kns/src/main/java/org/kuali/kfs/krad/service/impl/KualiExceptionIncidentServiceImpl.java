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

import org.apache.log4j.Logger;
import org.kuali.kfs.krad.exception.ExceptionIncident;
import org.kuali.kfs.krad.exception.KualiExceptionIncident;
import org.kuali.kfs.krad.service.KualiExceptionIncidentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Modified this service so that it now extends the KualiFeedbackServiceImpl.
 * This has been done to allow user feedback and exception incidents to be
 * reported in the same way, but to potentially different email lists.  Part
 * of this refactor included moving the mailer and messageTemplate properties
 * and the emailReport and createMailMessage methods to the new parent class.
 */
public class KualiExceptionIncidentServiceImpl extends KualiFeedbackServiceImpl implements KualiExceptionIncidentService {
    private Logger LOG = Logger.getLogger(KualiExceptionIncidentServiceImpl.class);

    /**
     * An list to send incident emails to.
     */
    private String incidentMailingList;

    /**
     * This property must be defined in the base configuration file for specifying
     * the mailing list for the report to be sent.
     * <p>Example:
     * <code>
     * <param name="KualiReporterServiceImpl.REPORT_MAIL_LIST">a@y,b@z</param>
     * </code>
     */
    public static final String REPORT_MAIL_LIST = String.format("%s.REPORT_MAIL_LIST", KualiExceptionIncidentServiceImpl.class.getSimpleName());

    @Override
    protected String getToAddressesPropertyName() {
        return REPORT_MAIL_LIST;
    }

    /**
     * This overridden method send email to the specified list of addresses.
     *
     * @see KualiExceptionIncidentService#report(KualiExceptionIncident)
     */
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
     * This method first separate a composite string of the format
     * "string token string".
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

    /**
     * This overridden method create an instance of the KualiExceptionIncident.
     *
     * @see KualiExceptionIncidentService#getExceptionIncident(
     *java.lang.Exception, java.util.Map)
     */
    @Override
    public KualiExceptionIncident getExceptionIncident(Exception exception,
                                                       Map<String, String> properties) {
        if (exception == null) {
            return getExceptionIncident(properties);
        }
        if (LOG.isTraceEnabled()) {
            String lm = String.format("ENTRY %s;%s", exception.getMessage(),
                properties.toString());
            LOG.trace(lm);
        }

        KualiExceptionIncident ei = new ExceptionIncident(exception, properties);

        if (LOG.isTraceEnabled()) {
            String lm = String.format("EXIT %s", ei.toProperties().toString());
            LOG.trace(lm);
        }

        return ei;
    }

    /**
     * This overridden method create an instance of ExceptionIncident from list of
     * name-value pairs as exception incident information.
     *
     * @see KualiExceptionIncidentService#getExceptionIncident(java.util.Map)
     */
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

    /**
     * Returns the incident report mailing list.
     *
     * @return the incidentMailingList
     */
    public String getIncidentMailingList() {
        return this.incidentMailingList;
    }

    /**
     * Sets the incident report mailing list.
     *
     * @param incidentMailingList the incidentMailingList to set
     */
    public void setIncidentMailingList(String incidentMailingList) {
        this.incidentMailingList = incidentMailingList;
    }

}
