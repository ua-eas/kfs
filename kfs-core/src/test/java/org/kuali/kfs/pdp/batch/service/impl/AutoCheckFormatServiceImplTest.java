package org.kuali.kfs.pdp.batch.service.impl;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.rice.kim.api.identity.Person;

public class AutoCheckFormatServiceImplTest {

	@TestSubject
	private AutoCheckFormatServiceImpl autoCheckFormatServ = new AutoCheckFormatServiceImpl();
	
	@Mock
	private static Person kualiUser;
	
	@Mock
	private static GlobalVariables gv;
	
	@BeforeClass
	public static void setUp() throws Exception{
		EasyMock.createNiceMock(Person.class);
		EasyMock.createNiceMock(GlobalVariables.class);
	}
	
	@Test
	public void testStepWhenSystemUserIsNull() throws Exception {
		
		EasyMock.expect(gv.getUserSession()).andReturn(null);
		EasyMock.replay(gv);
		
		assertFalse(autoCheckFormatServ.processChecks());
	}
	
	@Test
	public void testProcessChecks() {
		fail("Not yet implemented");
	}

	@Test
	public void testFormatChecks() {
		fail("Not yet implemented");
	}

	@Test
	public void testMarkPaymentsForFormat() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateAutoCheckFormat() {
		fail("Not yet implemented");
	}

}
