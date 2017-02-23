package org.kuali.kfs.sys.service.impl;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.sys.mail.BodyMailMessage;
import org.kuali.kfs.sys.mail.MailMessage;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;

public class EmailServiceImplTest {
    private EmailServiceImpl emailService;
    private ParameterService parameterService;
    private ConfigurationService configurationService;
    private MockJavaMailSender mailSender;

    @Before
    public void setUp() throws Exception {
        parameterService = EasyMock.createMock(ParameterService.class);
        configurationService = EasyMock.createMock(ConfigurationService.class);
        mailSender = new MockJavaMailSender();

        emailService = new EmailServiceImpl();
        emailService.setParameterService(parameterService);
        emailService.setConfigurationService(configurationService);
        emailService.setJavaMailSender(mailSender);
    }

    @Test
    public void testGetFromAddress() {
        EasyMock.expect(parameterService.getParameterValueAsString("KFS-SYS", "All", "DEFAULT_FROM_EMAIL_ADDRESS")).andReturn("me@me.com");
        EasyMock.replay(parameterService, configurationService);

        String fromAddress = emailService.getDefaultFromAddress();

        EasyMock.verify(parameterService, configurationService);
        Assert.assertEquals("me@me.com", fromAddress);
    }

    @Test
    public void testGetDefaultToAddress() {
        EasyMock.expect(parameterService.getParameterValueAsString("KFS-SYS", "All", "DEFAULT_TO_EMAIL_ADDRESS")).andReturn("tome@me.com");
        EasyMock.replay(parameterService, configurationService);

        String toAddress = emailService.getDefaultToAddress();

        EasyMock.verify(parameterService, configurationService);
        Assert.assertEquals("tome@me.com", toAddress);
    }

    @Test
    public void testSendMessageLogTst() {
        EasyMock.expect(configurationService.getPropertyValueAsString("production.environment.code")).andReturn("prd");
        EasyMock.expect(configurationService.getPropertyValueAsString("environment")).andReturn("tst");
        EasyMock.expect(parameterService.getParameterValueAsString("KFS-SYS","All","NON_PRODUCTION_EMAIL_MODE")).andReturn("L");
        EasyMock.replay(parameterService, configurationService);

        BodyMailMessage message = getTestMessage();

        emailService.sendMessage(message,false);

        EasyMock.verify(parameterService, configurationService);
        Assert.assertEquals(0,mailSender.sendMailCalls);
    }

    @Test
    public void testSendAttachmentMessageLogTst() {
        EasyMock.expect(configurationService.getPropertyValueAsString("production.environment.code")).andReturn("prd");
        EasyMock.expect(configurationService.getPropertyValueAsString("environment")).andReturn("tst");
        EasyMock.expect(parameterService.getParameterValueAsString("KFS-SYS","All","NON_PRODUCTION_EMAIL_MODE")).andReturn("L");
        EasyMock.replay(parameterService, configurationService);

        MailMessage message = getTestAttachmentMessage();
        emailService.sendMessage(message,false);

        EasyMock.verify(parameterService, configurationService);
        Assert.assertEquals(0,mailSender.sendMailCalls);
    }

    @Test
    public void testSendAttachmentMessageLogTstInvalidMode() {
        EasyMock.expect(configurationService.getPropertyValueAsString("production.environment.code")).andReturn("prd");
        EasyMock.expect(configurationService.getPropertyValueAsString("environment")).andReturn("tst");
        EasyMock.expect(parameterService.getParameterValueAsString("KFS-SYS","All","NON_PRODUCTION_EMAIL_MODE")).andReturn("X");
        EasyMock.replay(parameterService, configurationService);

        MailMessage message = getTestAttachmentMessage();
        emailService.sendMessage(message,false);

        EasyMock.verify(parameterService, configurationService);
        Assert.assertEquals(0,mailSender.sendMailCalls);
    }

    @Test
    public void testSendMessagePrd() throws MessagingException {
        EasyMock.expect(configurationService.getPropertyValueAsString("production.environment.code")).andReturn("prd");
        EasyMock.expect(configurationService.getPropertyValueAsString("environment")).andReturn("prd");
        EasyMock.expect(configurationService.getPropertyValueAsString("application.id")).andReturn("fin");
        EasyMock.expect(configurationService.getPropertyValueAsString("application.url")).andReturn("/fin");
        EasyMock.replay(parameterService, configurationService);

        BodyMailMessage message = getTestMessage();
        emailService.sendMessage(message,false);

        EasyMock.verify(parameterService, configurationService);
        Assert.assertEquals(1,mailSender.sendMailCalls);
        verifyMimeMessage(mailSender.mimeMessage);
    }

    @Test()
    public void testSendMessagePrdNoTo() {
        EasyMock.replay(parameterService, configurationService);

        BodyMailMessage message = new BodyMailMessage();
        message.setFromAddress("me@me.com");
        message.setSubject("Subject");
        message.setMessage("Hi there");

        emailService.sendMessage(message,false);

        EasyMock.verify(parameterService, configurationService);
        Assert.assertEquals(0,mailSender.sendMailCalls);
    }

    @Test()
    public void testSendMessagePrdNoFrom() {
        EasyMock.replay(parameterService, configurationService);

        BodyMailMessage message = new BodyMailMessage();
        message.addToAddress("me@me.com");
        message.setSubject("Subject");
        message.setMessage("Hi there");

        emailService.sendMessage(message,false);

        EasyMock.verify(parameterService, configurationService);
        Assert.assertEquals(0,mailSender.sendMailCalls);
    }

    @Test(expected = RuntimeException.class)
    public void testSendMessagePrdEmailSendException() {
        EasyMock.expect(configurationService.getPropertyValueAsString("production.environment.code")).andReturn("prd");
        EasyMock.expect(configurationService.getPropertyValueAsString("environment")).andReturn("prd");
        EasyMock.expect(configurationService.getPropertyValueAsString("application.id")).andReturn("fin");
        EasyMock.expect(configurationService.getPropertyValueAsString("application.url")).andReturn("/fin");
        EasyMock.replay(parameterService, configurationService);
        mailSender.throwExceptionOnSend = true;

        BodyMailMessage message = new BodyMailMessage();
        message.setFromAddress("you@you.com");
        message.addToAddress("me@me.com");
        message.setSubject("Subject");
        message.setMessage("Hi there");

        emailService.sendMessage(message,false);
    }

    @Test
    public void testSendAttachmentMessagePrd() throws MessagingException {
        EasyMock.expect(configurationService.getPropertyValueAsString("production.environment.code")).andReturn("prd");
        EasyMock.expect(configurationService.getPropertyValueAsString("environment")).andReturn("prd");
        EasyMock.expect(configurationService.getPropertyValueAsString("application.id")).andReturn("fin");
        EasyMock.expect(configurationService.getPropertyValueAsString("application.url")).andReturn("/fin");
        EasyMock.replay(parameterService, configurationService);

        MailMessage message = getTestAttachmentMessage();
        emailService.sendMessage(message,false);

        EasyMock.verify(parameterService, configurationService);
        Assert.assertEquals(1,mailSender.sendMailCalls);
        MimeMessage mm = mailSender.mimeMessage;
        verifyAttachmentMimeMessage(mm);
    }

    @Test
    public void testSendMessageTstProdMode() throws MessagingException {
        EasyMock.expect(configurationService.getPropertyValueAsString("production.environment.code")).andReturn("prd");
        EasyMock.expect(configurationService.getPropertyValueAsString("environment")).andReturn("tst");
        EasyMock.expect(parameterService.getParameterValueAsString("KFS-SYS","All","NON_PRODUCTION_EMAIL_MODE")).andReturn("P");
        EasyMock.expect(configurationService.getPropertyValueAsString("application.id")).andReturn("fin");
        EasyMock.expect(configurationService.getPropertyValueAsString("application.url")).andReturn("/fin");
        EasyMock.replay(parameterService, configurationService);

        BodyMailMessage message = getTestMessage();
        emailService.sendMessage(message,false);

        EasyMock.verify(parameterService, configurationService);
        Assert.assertEquals(1,mailSender.sendMailCalls);
        verifyMimeMessage(mailSender.mimeMessage);
    }

    @Test
    public void testSendMessageTstTestMode() throws MessagingException {
        EasyMock.expect(configurationService.getPropertyValueAsString("production.environment.code")).andReturn("prd");
        EasyMock.expect(configurationService.getPropertyValueAsString("environment")).andReturn("tst");
        EasyMock.expect(parameterService.getParameterValueAsString("KFS-SYS","All","NON_PRODUCTION_EMAIL_MODE")).andReturn("T");
        EasyMock.expect(parameterService.getParameterValueAsString("KFS-SYS","All","NON_PRODUCTION_TO_EMAIL_ADDRESS")).andReturn("nonprod@me.com");
        EasyMock.expect(configurationService.getPropertyValueAsString("application.id")).andReturn("fin");
        EasyMock.expect(configurationService.getPropertyValueAsString("application.url")).andReturn("/fin");
        EasyMock.replay(parameterService, configurationService);

        BodyMailMessage message = getTestMessage();
        emailService.sendMessage(message,false);

        EasyMock.verify(parameterService, configurationService);
        Assert.assertEquals(1,mailSender.sendMailCalls);

        MimeMessage mm = mailSender.mimeMessage;
        Assert.assertEquals("me@me.com",mm.getFrom()[0].toString());
        Assert.assertEquals("fin /fin: Subject",mm.getSubject());

        Address[] ccAddress = mm.getRecipients(MimeMessage.RecipientType.CC);
        Address[] bccAddress = mm.getRecipients(MimeMessage.RecipientType.BCC);
        Address[] toAddress = mm.getRecipients(MimeMessage.RecipientType.TO);

        Assert.assertEquals(1,toAddress.length);
        Assert.assertEquals("nonprod@me.com",toAddress[0].toString());
        Assert.assertNull(ccAddress);
        Assert.assertNull(bccAddress);
        Assert.assertEquals("me@me.com",mm.getFrom()[0].toString());
    }

    private void verifyMimeMessage(MimeMessage mm) throws MessagingException {
        // Do this for now until we can validate the body and attachment shit
        verifyAttachmentMimeMessage(mm);
    }

    private void verifyAttachmentMimeMessage(MimeMessage mm) throws MessagingException {
        Assert.assertEquals("me@me.com",mm.getFrom()[0].toString());
        Assert.assertEquals("fin /fin: Subject",mm.getSubject());

        Address[] ccAddress = mm.getRecipients(MimeMessage.RecipientType.CC);
        Address[] bccAddress = mm.getRecipients(MimeMessage.RecipientType.BCC);
        Address[] toAddress = mm.getRecipients(MimeMessage.RecipientType.TO);

        Assert.assertEquals(1,toAddress.length);
        Assert.assertEquals("you@you.com",toAddress[0].toString());
        Assert.assertEquals(2,ccAddress.length);
        Assert.assertEquals(1,bccAddress.length);
        Assert.assertEquals("bc1@bcc.com",bccAddress[0].toString());
    }

    private MailMessage getTestAttachmentMessage() {
        BodyMailMessage message = new BodyMailMessage();
        message.addToAddress("you@you.com");
        message.addCcAddress("cc1@cc.com");
        message.addCcAddress("cc2@cc.com");
        message.addBccAddress("bc1@bcc.com");
        message.setFromAddress("me@me.com");
        message.setSubject("Subject");
        message.setMessage("Hi there");
        message.setAttachmentContent("Attachment Content".getBytes());
        message.setAttachmentFileName("filename.txt");
        message.setAttachmentContentType("text/plain");
        return message;
    }

    private BodyMailMessage getTestMessage() {
        BodyMailMessage message = new BodyMailMessage();
        message.addToAddress("you@you.com");
        message.addCcAddress("cc1@cc.com");
        message.addCcAddress("cc2@cc.com");
        message.addBccAddress("bc1@bcc.com");
        message.setFromAddress("me@me.com");
        message.setSubject("Subject");
        message.setMessage("Hi there");
        return message;
    }

    class MockJavaMailSender implements JavaMailSender {
        boolean throwExceptionOnSend = false;
        int sendMailCalls = 0;
        MimeMessage mimeMessage;

        @Override
        public MimeMessage createMimeMessage() {
            return new MimeMessage((Session)null);
        }

        @Override
        public MimeMessage createMimeMessage(InputStream inputStream) throws MailException {
            return new MimeMessage((Session)null);
        }

        @Override
        public void send(MimeMessage mimeMessage) throws MailException {
            if ( throwExceptionOnSend ) {
                throw new MailParseException("Bad");
            }
            sendMailCalls++;
            this.mimeMessage = mimeMessage;
        }

        @Override
        public void send(MimeMessage[] mimeMessages) throws MailException {
            throw new RuntimeException("Not implemented for test");
        }

        @Override
        public void send(MimeMessagePreparator mimeMessagePreparator) throws MailException {
            throw new RuntimeException("Not implemented for test");
        }

        @Override
        public void send(MimeMessagePreparator[] mimeMessagePreparators) throws MailException {
            throw new RuntimeException("Not implemented for test");
        }

        @Override
        public void send(SimpleMailMessage simpleMailMessage) throws MailException {
            throw new RuntimeException("Not implemented for test");
        }

        @Override
        public void send(SimpleMailMessage[] simpleMailMessages) throws MailException {
            throw new RuntimeException("Not implemented for test");
        }
    }
}