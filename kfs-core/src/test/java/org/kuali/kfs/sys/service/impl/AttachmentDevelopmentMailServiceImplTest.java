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

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.krad.exception.InvalidAddressException;
import org.kuali.kfs.sys.mail.AttachmentMailMessage;
import org.kuali.rice.core.api.mail.MailMessage;

import javax.mail.MessagingException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

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
