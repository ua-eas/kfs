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

import javax.mail.MessagingException;

import org.kuali.kfs.krad.exception.InvalidAddressException;
import org.kuali.rice.core.api.mail.MailMessage;

public interface MailService {
    public void sendMessage(MailMessage message) throws InvalidAddressException, MessagingException;
    
    public void sendMessage(MailMessage message, boolean htmlMessage) throws InvalidAddressException, MessagingException;

    public String getBatchMailingList();
}
