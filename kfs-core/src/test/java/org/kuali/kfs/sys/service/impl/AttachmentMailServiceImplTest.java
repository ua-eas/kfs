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
package org.kuali.kfs.sys.service.impl;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.sys.mail.AttachmentMailMessage;
import org.kuali.kfs.sys.mail.AttachmentMailer;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.mail.MailMessage;

import javax.mail.MessagingException;
import java.util.HashSet;
import java.util.Set;

public class AttachmentMailServiceImplTest {
    public final static String FROM_ADDRESS = "from@email.com";
    public final static String BATCH_ADDRESS = "batch@email.com";
    public final static String NON_PROD_BATCH_ADDRESS = "nonprod@email.com";
    public final static String EMAIL_BODY = "BODY";
    public final static String EMAIL_SUBJECT = "SUBJECT";

    @Test
    public void testSendMessageProduction() throws Exception {
        AttachmentMailServiceImpl mailService = new AttachmentMailServiceImpl();
        mailService.setBatchMailingList(BATCH_ADDRESS);
        mailService.setNonProductionNotificationMailingList(NON_PROD_BATCH_ADDRESS);
        mailService.setRealNotificationsEnabled(true);

        ConfigurationService configurationService = EasyMock.createMock(ConfigurationService.class);
        EasyMock.expect(configurationService.getPropertyValueAsString(CoreConstants.Config.APPLICATION_ID)).andReturn("ID").once();
        EasyMock.expect(configurationService.getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY)).andReturn("URL").once();
        EasyMock.replay(configurationService);
        mailService.setConfigurationService(configurationService);

        MockAttachmentMailer mockMailer = new MockAttachmentMailer();
        mailService.setAttachmentMailer(mockMailer);

        mailService.sendMessage(getTestMailMessage());

        MailMessage mm = mockMailer.mailMessage;
        Assert.assertEquals(mm.getFromAddress(), FROM_ADDRESS);
        Assert.assertEquals(mm.getSubject(), "ID URL: " + EMAIL_SUBJECT);
        Assert.assertEquals(mm.getMessage(), EMAIL_BODY);
        Assert.assertEquals(mm.getToAddresses().size(), 2);
        Assert.assertTrue(mm.getToAddresses().contains("to1@email.com"));
        Assert.assertTrue(mm.getToAddresses().contains("to2@email.com"));
        Assert.assertEquals(mm.getBccAddresses().size(), 2);
        Assert.assertEquals(mm.getCcAddresses().size(), 2);
    }

    @Test
    public void testSendMessageNonProduction() throws Exception {
        AttachmentMailServiceImpl mailService = new AttachmentMailServiceImpl();
        mailService.setBatchMailingList(BATCH_ADDRESS);
        mailService.setNonProductionNotificationMailingList(NON_PROD_BATCH_ADDRESS);
        mailService.setRealNotificationsEnabled(false);

        ConfigurationService configurationService = EasyMock.createMock(ConfigurationService.class);
        EasyMock.expect(configurationService.getPropertyValueAsString(CoreConstants.Config.APPLICATION_ID)).andReturn("ID").once();
        EasyMock.expect(configurationService.getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY)).andReturn("URL").once();
        EasyMock.replay(configurationService);
        mailService.setConfigurationService(configurationService);

        MockAttachmentMailer mockMailer = new MockAttachmentMailer();
        mailService.setAttachmentMailer(mockMailer);

        mailService.sendMessage(getTestMailMessage());

        MailMessage mm = mockMailer.mailMessage;
        Assert.assertEquals(mm.getFromAddress(), FROM_ADDRESS);
        Assert.assertEquals(mm.getSubject(), "ID URL: " + EMAIL_SUBJECT);
        Assert.assertEquals(mm.getMessage(), "Email To: [to2@email.com, to1@email.com]\nEmail CC: [cc1@email.com, cc2@email.com]\nEmail BCC: [bcc2@email.com, bcc1@email.com]\n\nBODY");
        Assert.assertEquals(mm.getToAddresses().size(), 1);
        Assert.assertTrue(mm.getToAddresses().contains(NON_PROD_BATCH_ADDRESS));
        Assert.assertEquals(mm.getBccAddresses().size(), 0);
        Assert.assertEquals(mm.getCcAddresses().size(), 0);
    }

    public AttachmentMailMessage getTestMailMessage() {
        AttachmentMailMessage mm = new AttachmentMailMessage();
        mm.setSubject(EMAIL_SUBJECT);
        mm.setMessage(EMAIL_BODY);
        mm.setFromAddress(FROM_ADDRESS);

        Set<String> toSet = new HashSet<>();
        toSet.add("to1@email.com");
        toSet.add("to2@email.com");
        mm.setToAddresses(toSet);

        Set<String> ccSet = new HashSet<>();
        ccSet.add("cc1@email.com");
        ccSet.add("cc2@email.com");
        mm.setCcAddresses(ccSet);

        Set<String> bccSet = new HashSet<>();
        bccSet.add("bcc1@email.com");
        bccSet.add("bcc2@email.com");
        mm.setBccAddresses(bccSet);

        return mm;
    }

    class MockAttachmentMailer implements AttachmentMailer {
        public AttachmentMailMessage mailMessage;

        @Override
        public void sendEmail(AttachmentMailMessage message) throws MessagingException {
            this.mailMessage = message;
        }
    }
}
