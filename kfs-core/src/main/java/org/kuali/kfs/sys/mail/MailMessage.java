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
package org.kuali.kfs.sys.mail;

import java.util.Set;

public interface MailMessage {
    String getFromAddress();
    void setFromAddress(String fromAddress);

    Set<String> getToAddresses();
    void setToAddresses(Set toAddresses);
    void addToAddress(String toAddress);

    Set<String> getCcAddresses();
    void setCcAddresses(Set ccAddresses);
    void addCcAddress(String ccAddress);

    Set<String> getBccAddresses();
    void setBccAddresses(Set bccAddresses);
    void addBccAddress(String bccAddress);

    String getSubject();
    void setSubject(String subject);

    String getMessage();

    byte[] getAttachmentContent();
    void setAttachmentContent(byte[] attachmentContent);

    String getAttachmentContentType();
    void setAttachmentContentType(String attachmentContentType);

    String getAttachmentFileName();
    void setAttachmentFileName(String attachmentFileName);
}
