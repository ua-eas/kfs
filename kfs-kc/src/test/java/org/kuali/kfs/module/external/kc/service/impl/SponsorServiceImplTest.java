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

import java.util.HashMap;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kra.external.sponsor.SponsorCriteriaDto;
import org.kuali.kra.external.sponsor.SponsorWebService;

public class SponsorServiceImplTest {

    private SponsorServiceImpl sponsorService;
    private SponsorWebService sponsorWebService;

    @Before
    public void setup() {
        sponsorService = new SponsorServiceImpl();
        sponsorWebService = EasyMock.createMock(SponsorWebService.class);
    }

    @Test
    public void testCriteriaMapping() {
        SponsorCriteriaDto criteria = new SponsorCriteriaDto();
        criteria.setSponsorCode("123");
        criteria.setCustomerNumber("234");
        criteria.setSponsorName("Sponsor");
        criteria.setDunsPlusFourNumber("0000");
        criteria.setActive("Y");
        EasyMock.expect(sponsorWebService.getMatchingSponsors(eqCriteria(criteria))).andReturn(null);
        EasyMock.replay(sponsorWebService);

        HashMap<String, Object> fieldValues = new HashMap<String,Object>();
        fieldValues.put("agencyNumber", "123");
        fieldValues.put("customerNumber", "234");
        fieldValues.put("reportingName", "Sponsor");
        fieldValues.put("dunsPlusFourNumber", "0000");
        fieldValues.put("active", "Y");
        sponsorService.setWebService(sponsorWebService);
        sponsorService.findMatching(fieldValues);

        EasyMock.verify(sponsorWebService);
    }

    public static SponsorCriteriaDto eqCriteria(SponsorCriteriaDto in) {
        EasyMock.reportMatcher(new CriteriaEquals(in));
        return null;
    }

    private static class CriteriaEquals implements IArgumentMatcher {

        private SponsorCriteriaDto expected;

        public CriteriaEquals(SponsorCriteriaDto expected) {
            this.expected = expected;
        }

        @Override
        public void appendTo(StringBuffer buffer) {
            buffer.append(ToStringBuilder.reflectionToString(expected));
        }

        @Override
        public boolean matches(Object actual) {
            return EqualsBuilder.reflectionEquals(expected, actual);
        }

    }
}
