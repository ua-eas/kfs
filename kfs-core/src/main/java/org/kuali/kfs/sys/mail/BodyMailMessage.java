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

import java.util.HashSet;
import java.util.Set;

public class BodyMailMessage implements MailMessage {
    private String fromAddress;
    private Set<String> toAddresses = new HashSet<>();
    private Set<String> ccAddresses = new HashSet<>();
    private Set<String> bccAddresses = new HashSet<>();
    private String subject = "";
    private String message = "";
    private byte[] attachmentContent = null;
    private String attachmentContentType = null;
    private String attachmentFileName = null;

    @Override
    public void addToAddress(String toAddress) {
        toAddresses.add(toAddress);
    }

    @Override
    public void addCcAddress(String ccAddress) {
        ccAddresses.add(ccAddress);
    }

    @Override
    public void addBccAddress(String bccAddress) {
        bccAddresses.add(bccAddress);
    }

    @Override
    public String getFromAddress() {
        return fromAddress;
    }

    @Override
    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    @Override
    public Set<String> getToAddresses() {
        return toAddresses;
    }

    @Override
    public void setToAddresses(Set toAddresses) {
        this.toAddresses = toAddresses;
    }

    @Override
    public Set<String> getCcAddresses() {
        return ccAddresses;
    }

    @Override
    public void setCcAddresses(Set ccAddresses) {
        this.ccAddresses = ccAddresses;
    }

    @Override
    public Set<String> getBccAddresses() {
        return bccAddresses;
    }

    @Override
    public void setBccAddresses(Set bccAddresses) {
        this.bccAddresses = bccAddresses;
    }

    @Override
    public String getSubject() {
        return subject;
    }

    @Override
    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public byte[] getAttachmentContent() {
        return attachmentContent;
    }

    @Override
    public void setAttachmentContent(byte[] attachmentContent) {
        this.attachmentContent = attachmentContent;
    }

    @Override
    public String getAttachmentContentType() {
        return attachmentContentType;
    }

    @Override
    public void setAttachmentContentType(String attachmentContentType) {
        this.attachmentContentType = attachmentContentType;
    }

    @Override
    public String getAttachmentFileName() {
        return attachmentFileName;
    }

    @Override
    public void setAttachmentFileName(String attachmentFileName) {
        this.attachmentFileName = attachmentFileName;
    }
}
