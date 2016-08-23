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
package org.kuali.kfs.kns.util;

import org.kuali.kfs.krad.UserSession;
import org.kuali.kfs.krad.exception.KualiExceptionIncident;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.rice.kim.api.identity.Person;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public final class IncidentReportUtils {
	/**
     * Key to define the attribute stores exception properties such as
     * user email, user name, component name, etc.
     * <p>Value is exceptionProperties
     */
    public static final String EXCEPTION_PROPERTIES = "exceptionProperties";

	private IncidentReportUtils() {
		throw new UnsupportedOperationException("do not call");
	}

	public static Map<String, String> populateRequestForIncidentReport(String documentId, String componentName, HttpServletRequest request) {
		// Create properties of form and user for additional information
		// to be displayed or passing through JSP
		Map<String, String> properties = new HashMap<>();
		properties.put(KualiExceptionIncident.DOCUMENT_ID, documentId);
		String userEmail = "";
		String userName = "";
		String uuid = "";

		// No specific forward for the caught exception, use default logic
		// Get user information
		UserSession userSession = (UserSession) request.getSession().getAttribute(KRADConstants.USER_SESSION_KEY);
		Person sessionUser = null;
		if (userSession != null) {
			sessionUser = userSession.getPerson();
		}
		if (sessionUser != null) {
			userEmail = sessionUser.getEmailAddressUnmasked();
			userName = sessionUser.getName();
			uuid = sessionUser.getPrincipalName();
		}
		properties.put(KualiExceptionIncident.USER_EMAIL, userEmail);
		properties.put(KualiExceptionIncident.USER_NAME, userName);
		properties.put(KualiExceptionIncident.UUID, uuid);
		properties.put(KualiExceptionIncident.COMPONENT_NAME, componentName);

		return properties;
	}
}
