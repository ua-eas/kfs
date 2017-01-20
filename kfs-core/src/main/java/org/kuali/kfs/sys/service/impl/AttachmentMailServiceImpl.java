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
package org.kuali.kfs.sys.service.impl;

import org.kuali.kfs.krad.exception.InvalidAddressException;
import org.kuali.kfs.krad.service.impl.MailServiceImpl;
import org.kuali.kfs.sys.mail.AttachmentMailMessage;
import org.kuali.kfs.sys.mail.AttachmentMailer;
import org.kuali.kfs.sys.service.AttachmentMailService;

import javax.mail.MessagingException;

/**
 * This class extends the Rice MailServiceImpl class to add support for attachments.
 */
public class AttachmentMailServiceImpl extends MailServiceImpl implements AttachmentMailService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AttachmentMailServiceImpl.class);

    protected AttachmentMailer attachmentMailer;

    @Override
    public void sendMessage(AttachmentMailMessage message) throws InvalidAddressException, MessagingException {
        LOG.debug("sendMessage() started");

        AttachmentMailMessage amm = new AttachmentMailMessage(modifyForNonProduction(message, false));
        amm.setSubject(messageSubject(amm.getSubject()));

        attachmentMailer.sendEmail(amm);
    }

    public void setAttachmentMailer(AttachmentMailer attachmentMailer) {
        this.attachmentMailer = attachmentMailer;
    }
}
