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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.krad.exception.InvalidAddressException;
import org.kuali.rice.core.api.mail.MailMessage;

public class DevelopmentMailServiceImplTest {

	private DevelopmentMailServiceImpl developmentMailServiceImpl;

	private static final String TO_ADDRESS = "toAddress";
	private static final String FROM_ADDRESS = "fromAddress";

	@Before
	public void setUp() {
		developmentMailServiceImpl = new DevelopmentMailServiceImpl();
	}

	@After
	public void tearDown() {
		developmentMailServiceImpl = null;
	}

	@Test
	public void test() throws InvalidAddressException, MessagingException {
        final TestLogAppender appender = new TestLogAppender();
        final Logger logger = Logger.getRootLogger();
        logger.addAppender(appender);
        try {
        	MailMessage testMessage = new MailMessage();
        	testMessage.setFromAddress(FROM_ADDRESS);
        	Set<String> toAddresses = new HashSet<String>();
        	toAddresses.add(TO_ADDRESS);
        	testMessage.setToAddresses(toAddresses);
        	testMessage.setMessage("testing message");
        	developmentMailServiceImpl.sendMessage(testMessage);
        }
        finally {
          logger.removeAppender(appender);
        }

        final List<LoggingEvent> log = appender.getLog();
        String toAddressOutput = "TO   : [" + TO_ADDRESS + "]";
        assertEquals(toAddressOutput, log.get(2).getMessage());
        String fromAddressOutput = "FROM : " + FROM_ADDRESS;
        assertEquals(fromAddressOutput, log.get(1).getMessage());

	}
}
