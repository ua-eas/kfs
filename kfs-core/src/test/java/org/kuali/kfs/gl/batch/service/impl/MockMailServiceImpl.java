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
package org.kuali.kfs.gl.batch.service.impl;

import org.kuali.kfs.krad.exception.InvalidAddressException;
import org.kuali.kfs.krad.service.impl.MailServiceImpl;
import org.kuali.rice.core.api.mail.MailMessage;

import javax.mail.MessagingException;

public class MockMailServiceImpl extends MailServiceImpl {
    private MailMessage mailMessage;
    private boolean htmlMessage;

    @Override
    public void sendMessage(MailMessage message) throws InvalidAddressException {
        this.mailMessage = message;
        this.htmlMessage = false;
    }

    @Override
    public void sendMessage(MailMessage message, boolean htmlMessage) throws InvalidAddressException, MessagingException {
        this.mailMessage = message;
        this.htmlMessage = htmlMessage;
    }

    public MailMessage getMailMessage() {
        return mailMessage;
    }

    public boolean isHtmlMessage() {
        return htmlMessage;
    }
}
