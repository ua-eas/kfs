package org.kuali.kfs.krad.service.impl;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.mail.EmailBcList;
import org.kuali.rice.core.api.mail.EmailBody;
import org.kuali.rice.core.api.mail.EmailCcList;
import org.kuali.rice.core.api.mail.EmailFrom;
import org.kuali.rice.core.api.mail.EmailSubject;
import org.kuali.rice.core.api.mail.EmailTo;
import org.kuali.rice.core.api.mail.EmailToList;
import org.kuali.rice.core.api.mail.MailMessage;
import org.kuali.rice.core.api.mail.Mailer;

import javax.mail.MessagingException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MailServiceImplTest {
    public final static String FROM_ADDRESS = "from@email.com";
    public final static String BATCH_ADDRESS = "batch@email.com";
    public final static String NON_PROD_BATCH_ADDRESS = "nonprod@email.com";
    public final static String EMAIL_BODY = "BODY";
    public final static String EMAIL_SUBJECT = "SUBJECT";

    @Test
    public void testSendMessageProduction() throws Exception {
        MailServiceImpl mailService = new MailServiceImpl();
        mailService.setBatchMailingList(BATCH_ADDRESS);
        mailService.setNonProductionNotificationMailingList(NON_PROD_BATCH_ADDRESS);
        mailService.setRealNotificationsEnabled(true);

        ConfigurationService configurationService = EasyMock.createMock(ConfigurationService.class);
        EasyMock.expect(configurationService.getPropertyValueAsString(CoreConstants.Config.APPLICATION_ID)).andReturn("ID").once();
        EasyMock.expect(configurationService.getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY)).andReturn("URL").once();
        EasyMock.replay(configurationService);
        mailService.setConfigurationService(configurationService);

        MockMailer mockMailer = new MockMailer();
        mailService.setMailer(mockMailer);

        mailService.sendMessage(getTestMailMessage());

        MailMessage mm = mockMailer.mailMessage;
        Assert.assertEquals(mm.getFromAddress(),FROM_ADDRESS);
        Assert.assertEquals(mm.getSubject(),"ID URL: " + EMAIL_SUBJECT);
        Assert.assertEquals(mm.getMessage(),EMAIL_BODY);
        Assert.assertEquals(mm.getToAddresses().size(),2);
        Assert.assertTrue(mm.getToAddresses().contains("to1@email.com"));
        Assert.assertTrue(mm.getToAddresses().contains("to2@email.com"));
        Assert.assertEquals(mm.getBccAddresses().size(),2);
        Assert.assertEquals(mm.getCcAddresses().size(),2);
    }

    @Test
    public void testSendMessageNonProduction() throws Exception {
        MailServiceImpl mailService = new MailServiceImpl();
        mailService.setBatchMailingList(BATCH_ADDRESS);
        mailService.setNonProductionNotificationMailingList(NON_PROD_BATCH_ADDRESS);
        mailService.setRealNotificationsEnabled(false);

        ConfigurationService configurationService = EasyMock.createMock(ConfigurationService.class);
        EasyMock.expect(configurationService.getPropertyValueAsString(CoreConstants.Config.APPLICATION_ID)).andReturn("ID").once();
        EasyMock.expect(configurationService.getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY)).andReturn("URL").once();
        EasyMock.replay(configurationService);
        mailService.setConfigurationService(configurationService);

        MockMailer mockMailer = new MockMailer();
        mailService.setMailer(mockMailer);

        mailService.sendMessage(getTestMailMessage());

        MailMessage mm = mockMailer.mailMessage;
        Assert.assertEquals(mm.getFromAddress(),FROM_ADDRESS);
        Assert.assertEquals(mm.getSubject(),"ID URL: " + EMAIL_SUBJECT);
        Assert.assertEquals(mm.getMessage(),"Email To: [to2@email.com, to1@email.com]\nEmail CC: [cc1@email.com, cc2@email.com]\nEmail BCC: [bcc2@email.com, bcc1@email.com]\n\nBODY");
        Assert.assertEquals(mm.getToAddresses().size(),1);
        Assert.assertTrue(mm.getToAddresses().contains(NON_PROD_BATCH_ADDRESS));
        Assert.assertEquals(mm.getBccAddresses().size(),0);
        Assert.assertEquals(mm.getCcAddresses().size(),0);
    }

    @Test
    public void testSendMessageProductionHtml() throws Exception {
        MailServiceImpl mailService = new MailServiceImpl();
        mailService.setBatchMailingList(BATCH_ADDRESS);
        mailService.setNonProductionNotificationMailingList(NON_PROD_BATCH_ADDRESS);
        mailService.setRealNotificationsEnabled(true);

        ConfigurationService configurationService = EasyMock.createMock(ConfigurationService.class);
        EasyMock.expect(configurationService.getPropertyValueAsString(CoreConstants.Config.APPLICATION_ID)).andReturn("ID").once();
        EasyMock.expect(configurationService.getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY)).andReturn("URL").once();
        EasyMock.replay(configurationService);
        mailService.setConfigurationService(configurationService);

        MockMailer mockMailer = new MockMailer();
        mailService.setMailer(mockMailer);

        mailService.sendMessage(getTestMailMessage(),true);

        MailMessage mm = mockMailer.mailMessage;
        Assert.assertEquals(mm.getFromAddress(),FROM_ADDRESS);
        Assert.assertEquals(mm.getSubject(),"ID URL: " + EMAIL_SUBJECT);
        Assert.assertEquals(mm.getMessage(),EMAIL_BODY);
        Assert.assertEquals(mm.getToAddresses().size(),2);
        Assert.assertTrue(mm.getToAddresses().contains("to1@email.com"));
        Assert.assertTrue(mm.getToAddresses().contains("to2@email.com"));
        Assert.assertEquals(mm.getBccAddresses().size(),2);
        Assert.assertEquals(mm.getCcAddresses().size(),2);
    }

    @Test
    public void testSendMessageNonProductionHtml() throws Exception {
        MailServiceImpl mailService = new MailServiceImpl();
        mailService.setBatchMailingList(BATCH_ADDRESS);
        mailService.setNonProductionNotificationMailingList(NON_PROD_BATCH_ADDRESS);
        mailService.setRealNotificationsEnabled(false);

        ConfigurationService configurationService = EasyMock.createMock(ConfigurationService.class);
        EasyMock.expect(configurationService.getPropertyValueAsString(CoreConstants.Config.APPLICATION_ID)).andReturn("ID").once();
        EasyMock.expect(configurationService.getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY)).andReturn("URL").once();
        EasyMock.replay(configurationService);
        mailService.setConfigurationService(configurationService);

        MockMailer mockMailer = new MockMailer();
        mailService.setMailer(mockMailer);

        mailService.sendMessage(getTestMailMessage(),true);

        MailMessage mm = mockMailer.mailMessage;
        Assert.assertEquals(mm.getFromAddress(),FROM_ADDRESS);
        Assert.assertEquals(mm.getSubject(),"ID URL: " + EMAIL_SUBJECT);
        Assert.assertEquals(mm.getMessage(),"Email To: [to2@email.com, to1@email.com]<br/>\nEmail CC: [cc1@email.com, cc2@email.com]<br/>\nEmail BCC: [bcc2@email.com, bcc1@email.com]<br/>\n<br/>\nBODY");
        Assert.assertEquals(mm.getToAddresses().size(),1);
        Assert.assertTrue(mm.getToAddresses().contains(NON_PROD_BATCH_ADDRESS));
        Assert.assertEquals(mm.getBccAddresses().size(),0);
        Assert.assertEquals(mm.getCcAddresses().size(),0);
    }

    public MailMessage getTestMailMessage() {
        MailMessage mm = new MailMessage();
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

    class MockMailer implements Mailer {
        public MailMessage mailMessage;
        public boolean htmlMessage;

        @Override
        public void sendEmail(MailMessage mailMessage) throws MessagingException {
            this.mailMessage = mailMessage;
        }

        @Override
        public void sendEmail(EmailFrom emailFrom, EmailTo emailTo, EmailSubject emailSubject, EmailBody emailBody, boolean htmlMessage) {
            mailMessage = new MailMessage();
            mailMessage.setFromAddress(emailFrom.getFromAddress());
            Set<String> toAddr = new HashSet<>();
            toAddr.add(emailTo.getToAddress());
            mailMessage.setToAddresses(toAddr);
            mailMessage.setSubject(emailSubject.getSubject());
            mailMessage.setMessage(emailBody.getBody());
            this.htmlMessage = htmlMessage;
        }

        @Override
        public void sendEmail(EmailFrom emailFrom, EmailToList emailToList, EmailSubject emailSubject, EmailBody emailBody, EmailCcList emailCcList, EmailBcList emailBcList, boolean htmlMessage) {
            mailMessage = new MailMessage();
            mailMessage.setFromAddress(emailFrom.getFromAddress());
            mailMessage.setToAddresses(toSet(emailToList.getToAddresses()));
            mailMessage.setSubject(emailSubject.getSubject());
            mailMessage.setMessage(emailBody.getBody());
            mailMessage.setCcAddresses(toSet(emailCcList.getCcAddresses()));
            mailMessage.setBccAddresses(toSet(emailBcList.getBcAddresses()));
            this.htmlMessage = htmlMessage;
        }

        private Set<String> toSet(List<String> values) {
            Set<String> out = new HashSet<>();
            out.addAll(values);
            return out;
        }
    }
}