/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2014 The Kuali Foundation
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

import org.kuali.kfs.krad.exception.InvalidAddressException;
import org.kuali.kfs.krad.service.MailService;
import org.kuali.rice.core.api.mail.MailMessage;

import javax.mail.MessagingException;

public class DevelopmentMailServiceImpl implements MailService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DevelopmentMailServiceImpl.class);

    @Override
    public void sendMessage(MailMessage message) throws InvalidAddressException, MessagingException {
        LOG.info( "*********************** EMAIL SEND *****************************");
        LOG.info( "FROM : " + message.getFromAddress() );
        LOG.info( "TO   : " + message.getToAddresses() );
        LOG.info( "CC   : " + message.getCcAddresses() );
        LOG.info( "BCC  : " + message.getBccAddresses() );
        LOG.info( "SUBJECT : " + message.getSubject() );
        LOG.info( "MESSAGE : \n" + message.getMessage() );
        LOG.info( "*********************** END EMAIL  *****************************");
    }

    @Override
    public void sendMessage(MailMessage message, boolean htmlMessage) throws InvalidAddressException, MessagingException {
        LOG.info("HTML Message: " + htmlMessage);
        sendMessage(message);
    }

    @Override
    public String getBatchMailingList() {
        return null;
    }
}
