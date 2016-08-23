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
package org.kuali.kfs.krad.util;

import java.io.Serializable;
import java.util.Map;

/**
 * Holds information on an action (ticket type name) and context (ticketContext) that has been performed and can be placed in the
 * UserSession objectMap. This can be checked for by subsequent session requests to determine if the action has already taken place (for
 * example a Question or document action where the action is not recorded on the document or form).
 */
public class SessionTicket implements Serializable {
	private String ticketTypeName;
	private Map<String, String> ticketContext;

	public SessionTicket(String ticketTypeName) {
		this.ticketTypeName = ticketTypeName;
	}

	public String getTicketTypeName() {
		return this.ticketTypeName;
	}

	public void setTicketTypeName(String ticketTypeName) {
		this.ticketTypeName = ticketTypeName;
	}

	public Map<String, String> getTicketContext() {
		return this.ticketContext;
	}

	public void setTicketContext(Map<String, String> ticketContext) {
		this.ticketContext = ticketContext;
	}
}
