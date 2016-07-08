package org.kuali.kfs.sys.service.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.krad.exception.InvalidAddressException;
import org.kuali.kfs.sys.mail.AttachmentMailMessage;
import org.kuali.rice.core.api.mail.MailMessage;

public class AttachmentDevelopmentMailServiceImplTest {

    private AttachmentDevelopmentMailServiceImpl attachementDevelopmentMailServiceImpl;

    private static final String TO_ADDRESS = "toAddress";
    private static final String FROM_ADDRESS = "fromAddress";
    private TestLogAppender appender;
    private Logger logger;

    @Before
    public void setUp() {
        attachementDevelopmentMailServiceImpl = new AttachmentDevelopmentMailServiceImpl();
        appender = new TestLogAppender();
        logger = Logger.getRootLogger();
    }

    @After
    public void tearDown() {
        attachementDevelopmentMailServiceImpl = null;
        appender = null;
        logger = null;
    }

    @Test
    public void testSendMessageMailMessage() throws InvalidAddressException, MessagingException {
        logger.addAppender(appender);
        try {
            MailMessage testMessage = generateBaseMailMessage();
            attachementDevelopmentMailServiceImpl.sendMessage(testMessage);
        } finally {
            logger.removeAppender(appender);
        }

        final List<LoggingEvent> log = appender.getLog();
        assertStringToLogMessageElement("TO   : [" + TO_ADDRESS + "]", log, 2);
        assertStringToLogMessageElement("FROM : " + FROM_ADDRESS, log, 1);
    }

    @Test
    public void testSendMessageNoAttachmentMailMessage() throws InvalidAddressException, MessagingException {
        logger.addAppender(appender);
        try {
            AttachmentMailMessage testMessage = generateBaseMailMessage();
            attachementDevelopmentMailServiceImpl.sendMessage(testMessage);
        } finally {
            logger.removeAppender(appender);
        }

        final List<LoggingEvent> log = appender.getLog();

        assertStringToLogMessageElement("\t FILE NAME : null", log, 8);
        assertStringToLogMessageElement("\t SIZE      : No content found", log, 9);
        assertStringToLogMessageElement("\t MIME TYPE : null", log, 10);
    }

    @Test
    public void testSendMessageAttachmentMailMessage() throws InvalidAddressException, MessagingException {
        logger.addAppender(appender);
        try {
            AttachmentMailMessage testMessage = generateBaseMailMessage();
            testMessage.setFileName("testFile.txt");
            testMessage.setContent(hexStringToByteArray("testing content is fun"));
            testMessage.setType("txt");
            attachementDevelopmentMailServiceImpl.sendMessage(testMessage);
        } finally {
            logger.removeAppender(appender);
        }

        final List<LoggingEvent> log = appender.getLog();

        assertStringToLogMessageElement("\t FILE NAME : testFile.txt", log, 8);
        assertStringToLogMessageElement("\t SIZE      : 11", log, 9);
        assertStringToLogMessageElement("\t MIME TYPE : txt", log, 10);
    }

    private AttachmentMailMessage generateBaseMailMessage() {
        AttachmentMailMessage testMessage = new AttachmentMailMessage();
        testMessage.setFromAddress(FROM_ADDRESS);
        Set<String> toAddresses = new HashSet<String>();
        toAddresses.add(TO_ADDRESS);
        testMessage.setToAddresses(toAddresses);
        testMessage.setMessage("testing message");
        return testMessage;
    }

    private void assertStringToLogMessageElement(String stringToCompare, List<LoggingEvent> log, int logElement) {
        assertEquals(stringToCompare, log.get(logElement).getMessage());
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

}
