/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2015 The Kuali Foundation
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

import org.kuali.kfs.krad.exception.KualiExceptionIncident;

import java.util.Map;

/**
 * This is used for sending report of an incident
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface KualiExceptionIncidentService extends KualiFeedbackService {

    /**
     * This method send email to the defined mailing list using the exception incident
     * instance.
     * 
     * @param exceptionIncident
     * @throws Exception
     */
    public void report(KualiExceptionIncident exceptionIncident) throws Exception;
    
    /**
     * This method create an instance of the KualiExceptionIncident from its factory.
     * 
     * @param exception
     * @param properties Additional information when the exception is thrown
     * <p>example:
     * <ul>
     * <li>Document id</li>
     * <li>User email</li>
     * <li>User name</li>
     * <li>Component name</li>
     * </ul>
     * @return
     */
    public KualiExceptionIncident getExceptionIncident(
            Exception exception, Map<String, String> properties);

    /**
     * This method create an instance of the KualiExceptionIncident from its factory.
     * This method is used when the thrown exception is not available. It's an implicit
     * initialization.
     * 
     * @param properties The list of name-value pairs containing the thrown exception
     * information
     * @return
     */
    public KualiExceptionIncident getExceptionIncident(Map<String, String> properties);
}
