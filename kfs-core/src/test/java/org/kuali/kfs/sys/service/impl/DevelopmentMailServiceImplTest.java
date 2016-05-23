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
		final TestAppender appender = new TestAppender();
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
	
	private class TestAppender extends AppenderSkeleton {
	    private final List<LoggingEvent> log = new ArrayList<LoggingEvent>();

		@Override
		public void close() {
		}

		@Override
		public boolean requiresLayout() {
			return false;
		}

		@Override
		protected void append(LoggingEvent event) {
			log.add(event);
			
		}
		
		public List<LoggingEvent> getLog() {
			return new ArrayList<LoggingEvent>(log);
	    }
	}
}
