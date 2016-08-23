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
package org.kuali.kfs.krad.service;

/**
 * This new feedback service was added to refactor
 * KualiExceptionIncidentService.  Now the KualiExceptionIncidentService
 * extends this service so that exception reporting is considered to be a type
 * of feedback.  Both services share the emailReport method which formats and
 * sends an email to the appropriate list.
 */
public interface KualiFeedbackService {

	/**
     * This property must be defined in the base configuration file for specifying
     * the mailing list for the report to be sent.
     * <p>Example:
     * <code>
     * <param name="KualiFeedbackService.REPORT_MAIL_LIST">a@y,b@z</param>
     * </code>
     */
    public static final String REPORT_MAIL_LIST = String.format("%s.REPORT_MAIL_LIST", KualiFeedbackService.class.getSimpleName());

	/**
     * This method send email to the defined mailing list with a specified subject and
     * message.
     *
     * @param subject
     * @param message
     * @throws Exception
     */
    public void emailReport(String subject, String message) throws Exception;

    public void sendFeedback(String documentId, String componentName, String description) throws Exception;
}
