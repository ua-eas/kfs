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
package org.kuali.kfs.pdp.service.impl;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.easymock.TestSubject;
import org.junit.Test;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.ProcessSummary;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;

public class FormatServiceImplTest {


    @TestSubject
    private FormatServiceImpl formatServ = new FormatServiceImpl();

	@Test
	public void testAddSummaryToCustomerProfileMapWhenProcessSummaryIsNull() {
		HashMap<CustomerProfile, List<KualiDecimal>> custProMap = new HashMap<CustomerProfile, List<KualiDecimal>>();
		formatServ.addSummaryToCustomerProfileMap(custProMap, null);
		assertTrue(custProMap.isEmpty());
	}

	@Test
	public void testAddSummaryToCustomerProfileMapWhenMapIsNull() {
		HashMap<CustomerProfile, List<KualiDecimal>> custProMap = new HashMap<CustomerProfile, List<KualiDecimal>>();
		formatServ.addSummaryToCustomerProfileMap(null, null);
		//by sending in a null map should get back an instantiated HashMap
		assertTrue(custProMap.isEmpty());
	}

	@Test
	public void testAddSummaryToCustomerProfileMapWhenProcessSummaryHasNullCustomerProfile() throws Exception {
		ProcessSummary processSummary = new ProcessSummary();
		processSummary.setCustomer(null);

		HashMap<CustomerProfile, List<KualiDecimal>> custProMap = new HashMap<CustomerProfile, List<KualiDecimal>>();

		formatServ.addSummaryToCustomerProfileMap(custProMap, processSummary);

		assertTrue(custProMap.isEmpty());
	}

	@Test
	public void testAddSummaryToCustomerProfileMapWhenProcessSummaryHasValidCustomerProfile() throws Exception {
		ProcessSummary procSum = createBasicProcessSummary();
		CustomerProfile custProfile = procSum.getCustomer();
		HashMap<CustomerProfile, List<KualiDecimal>> custProMap = new HashMap<CustomerProfile, List<KualiDecimal>>();

		formatServ.addSummaryToCustomerProfileMap(custProMap, procSum);

		// check to make sure map was updated
		assertTrue(custProMap.size()==1);

		// check to see if correct key-value pair was added to map
		List<KualiDecimal> totals = custProMap.get(custProfile);
		assertTrue(totals.get(0).intValue() == 3);
		assertTrue(totals.get(1).intValue() == 350);
	}

	@Test
	public void testAddSummaryToCustomerProfileMapWhenSameCustomerProfileKeyExists() throws Exception {
		HashMap<CustomerProfile, List<KualiDecimal>> custProMap = new HashMap<CustomerProfile, List<KualiDecimal>>();
		createBasicSummaryMap(custProMap);

		ProcessSummary procSum = createBasicProcessSummary();
		procSum.setProcessTotalCount(new KualiInteger(2));
		procSum.setProcessTotalAmount(new KualiDecimal(150));
		CustomerProfile custProfile = procSum.getCustomer();

		formatServ.addSummaryToCustomerProfileMap(custProMap, procSum);

		assertTrue(custProMap.size()==1);
		List<KualiDecimal> totals = custProMap.get(custProfile);
		assertTrue(totals.get(0).intValue() == 5);
		assertTrue(totals.get(1).intValue() == 500);
	}

	@Test
	public void testAddSummaryToCustomerProfileMapForTwoDifferentCustomerProfiles() throws Exception {
		HashMap<CustomerProfile, List<KualiDecimal>> custProMap = new HashMap<CustomerProfile, List<KualiDecimal>>();
		createBasicSummaryMap(custProMap);

		// create a new process summary for a new customer profile
		ProcessSummary procSum = createBasicProcessSummary();
		procSum.setProcessTotalCount(new KualiInteger(2));
		procSum.setProcessTotalAmount(new KualiDecimal(150));
		CustomerProfile custProfile = procSum.getCustomer();
		custProfile.setChartCode("IR");
		procSum.setCustomer(custProfile);

		formatServ.addSummaryToCustomerProfileMap(custProMap, procSum);

		// there should be two key-value pairs in the map
		assertTrue(custProMap.size()==2);

		// the key-vlaue pair for the new cust profile that was added
		List<KualiDecimal> totals = custProMap.get(custProfile);
		assertTrue(totals.get(0).intValue() == 2);
		assertTrue(totals.get(1).intValue() == 150);
	}

	/**
	 * @param custProMap
	 */
	protected void createBasicSummaryMap(HashMap<CustomerProfile, List<KualiDecimal>> custProMap) {
		CustomerProfile custPro = new CustomerProfile();
		custPro.setChartCode("BL");
		custPro.setUnitCode("KUAL");
		custPro.setSubUnitCode("DV");
		List<KualiDecimal> totals = new ArrayList<KualiDecimal>();
		totals.add(0, new KualiDecimal(3));
		totals.add(1, new KualiDecimal(350));

		custProMap.put(custPro, totals);
	}

	/**
	 * @return
	 */
	protected ProcessSummary createBasicProcessSummary(){

		CustomerProfile custPro = new CustomerProfile();
		custPro.setChartCode("BL");
		custPro.setUnitCode("KUAL");
		custPro.setSubUnitCode("DV");

		ProcessSummary procSum = new ProcessSummary();
		procSum.setCustomer(custPro);
		procSum.setProcessTotalCount(new KualiInteger(3));
		procSum.setProcessTotalAmount(new KualiDecimal(350));

		return procSum;
	}

}


