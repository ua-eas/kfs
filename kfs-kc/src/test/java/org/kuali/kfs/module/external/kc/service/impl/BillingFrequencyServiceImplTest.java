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
package org.kuali.kfs.module.external.kc.service.impl;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.easymock.EasyMock;
import org.eclipse.persistence.jpa.jpql.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.integration.ar.AccountsReceivableBillingFrequency;
import org.kuali.kfs.integration.ar.businessobject.BillingFrequency;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.module.external.kc.dto.BillingFrequencyDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillingFrequencyServiceImplTest {

    private BillingFrequencyServiceImpl billingFrequencyService;
    private ModuleService responsibleModuleService;

    @Before
    public void setup() {
        billingFrequencyService = new BillingFrequencyServiceImpl();
        responsibleModuleService = EasyMock.createMock(ModuleService.class);
    }

    @Test
    public void testGetAll() {
        Map<String, Object> criteria = new HashMap<String, Object>();
        EasyMock.expect(responsibleModuleService.getExternalizableBusinessObjectsListForLookup(AccountsReceivableBillingFrequency.class, criteria, true))
            .andReturn(getSampleBillingFrequencyList());
        EasyMock.replay(responsibleModuleService);

        billingFrequencyService.setResponsibleModuleService(responsibleModuleService);
        List<BillingFrequencyDTO> results = billingFrequencyService.getAll();

        EasyMock.verify(responsibleModuleService);
        if (results.size() != 1) {
            Assert.fail("billingFrequencyService.getAll() should have returned 1 result; instead it returned " + results.size());
            return;
        }

        BillingFrequencyDTO result = results.get(0);

        BillingFrequency expected = createSampleBillingFrequency();
        Assert.isTrue(compareBillingFrequencies(result, expected),
            "Expected result was " + ToStringBuilder.reflectionToString(expected) +
                " but got " + ToStringBuilder.reflectionToString(result));
    }

    @Test
    public void testGetActive() {
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put("active", "Y");
        EasyMock.expect(responsibleModuleService.getExternalizableBusinessObjectsListForLookup(AccountsReceivableBillingFrequency.class, criteria, true))
            .andReturn(getSampleBillingFrequencyList());
        EasyMock.replay(responsibleModuleService);

        billingFrequencyService.setResponsibleModuleService(responsibleModuleService);
        List<BillingFrequencyDTO> results = billingFrequencyService.getActive();

        EasyMock.verify(responsibleModuleService);
        if (results.size() != 1) {
            Assert.fail("billingFrequencyService.getActive() should have returned 1 result; instead it returned " + results.size());
            return;
        }

        BillingFrequencyDTO result = results.get(0);

        BillingFrequency expected = createSampleBillingFrequency();
        Assert.isTrue(compareBillingFrequencies(result, expected),
            "Expected result was " + ToStringBuilder.reflectionToString(expected) +
                " but got " + ToStringBuilder.reflectionToString(result));
    }

    @Test
    public void testGetBillingFrequency() {
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put("frequency", "Sample");
        EasyMock.expect(responsibleModuleService.getExternalizableBusinessObject(AccountsReceivableBillingFrequency.class, criteria))
            .andReturn(createSampleBillingFrequency());
        EasyMock.replay(responsibleModuleService);

        billingFrequencyService.setResponsibleModuleService(responsibleModuleService);
        BillingFrequencyDTO result = billingFrequencyService.getBillingFrequency("Sample");

        BillingFrequency expected = createSampleBillingFrequency();
        Assert.isTrue(compareBillingFrequencies(result, expected),
            "Expected result was " + ToStringBuilder.reflectionToString(expected) +
                " but got " + ToStringBuilder.reflectionToString(result));
    }

    private List<AccountsReceivableBillingFrequency> getSampleBillingFrequencyList() {
        List<AccountsReceivableBillingFrequency> results = new ArrayList<AccountsReceivableBillingFrequency>();

        BillingFrequency billingFrequency = createSampleBillingFrequency();
        results.add(billingFrequency);

        return results;
    }

    private BillingFrequency createSampleBillingFrequency() {
        BillingFrequency billingFrequency = new BillingFrequency();
        billingFrequency.setActive(true);
        billingFrequency.setFrequency("Sample");
        billingFrequency.setFrequencyDescription("Sample Billing Frequency");
        billingFrequency.setGracePeriodDays(3);
        return billingFrequency;
    }

    private boolean compareBillingFrequencies(BillingFrequencyDTO result, BillingFrequency expected) {
        return expected.isActive() == result.isActive() &&
            expected.getFrequency().equals(result.getFrequency()) &&
            expected.getFrequencyDescription().equals(result.getFrequencyDescription()) &&
            expected.getGracePeriodDays().equals(result.getGracePeriodDays());
    }
}
