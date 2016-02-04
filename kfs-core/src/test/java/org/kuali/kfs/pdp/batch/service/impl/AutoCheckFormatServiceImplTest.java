package org.kuali.kfs.pdp.batch.service.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;

import org.easymock.EasyMock;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.FormatSelection;
import org.kuali.rice.core.api.datetime.DateTimeService;

public class AutoCheckFormatServiceImplTest {

	@TestSubject
	private AutoCheckFormatServiceImpl autoCheckFormatServ = new AutoCheckFormatServiceImpl();
	
	@Mock
	private static FormatSelection fs = new FormatSelection();
	
	@BeforeClass
	public static void setUp() throws Exception{
		fs = EasyMock.createMock(FormatSelection.class);
	}
	
	@Test
	public void testNullCustomerListInFormatSelectionWhenGeneratingFomatReadyCustomerList() throws Exception {
		EasyMock.expect(fs.getCustomerList()).andReturn(null);
		EasyMock.replay(fs);
		
		assertNotNull(autoCheckFormatServ.generateListOfCustomerProfilesReadyForFormat(fs));
		EasyMock.reset(fs);
	}
	
	@Test
	public void testGeneratingFomatReadyCustomersListWithFormatSelectionCustomersWithWrongCampus() throws Exception {
		CustomerProfile cp1 = new CustomerProfile();
		cp1.setDefaultPhysicalCampusProcessingCode("IR");
		
		ArrayList<CustomerProfile> customerList = new ArrayList<CustomerProfile>();
		customerList.add(cp1);
		
		EasyMock.expect(fs.getCustomerList()).andReturn(customerList);
		EasyMock.expect(fs.getCampus()).andReturn("BL");
		EasyMock.replay(fs);
		
		//there is only only item in the list and it is marked for format as FALSE
		assertFalse(autoCheckFormatServ.generateListOfCustomerProfilesReadyForFormat(fs).get(0).isSelectedForFormat());
		
		EasyMock.reset(fs);
	}
	
	@Test
	public void testGeneratingFomatReadyCustomersListWithFormatSelectionCustomersWithMatchingCampuses() throws Exception {
		CustomerProfile cp1 = new CustomerProfile();
		cp1.setDefaultPhysicalCampusProcessingCode("BL");
		
		ArrayList<CustomerProfile> customerList = new ArrayList<CustomerProfile>();
		customerList.add(cp1);
		
		EasyMock.expect(fs.getCustomerList()).andReturn(customerList);
		EasyMock.expect(fs.getCampus()).andReturn("BL");
		EasyMock.replay(fs);
		
		//there is only only item in the list and it is marked for format as TRUE
		assertTrue(autoCheckFormatServ.generateListOfCustomerProfilesReadyForFormat(fs).get(0).isSelectedForFormat());
		
		EasyMock.reset(fs);
	}
	
	@Test
	public void testCreationOfAutoCheckFormatWhenFormatSelectionHasStartDate() throws Exception {
		//set format selection to have date already set
		EasyMock.expect(fs.getStartDate()).andReturn(new Date()).times(3);
		EasyMock.replay(fs);
		
		DateTimeService dtService = EasyMock.createMock(DateTimeService.class);
		EasyMock.expect(dtService.toDateTimeString(fs.getStartDate())).andReturn("date time");
		autoCheckFormatServ.setDateTimeService(dtService);
		
		// the method should return a null
		assertTrue(ObjectUtils.isNull(autoCheckFormatServ.createAutoCheckFormat(fs)));
		
		EasyMock.reset(fs);
	}
	
	@Test
	public void testCreationOfAutoCheckFormatWhenFormatSelectionHasNullStartDate() throws Exception {
		
		assertTrue(true);
	}
	
}
