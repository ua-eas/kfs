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
package org.kuali.kfs.module.external.kc.businessobject;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kra.external.sponsor.RolodexDTO;
import org.kuali.rice.location.api.country.Country;
import org.kuali.rice.location.api.country.CountryService;

public class AgencyAddressTest {
    private AgencyAddress agencyAddress;
    private CountryService countryService;

    @Before
    public void setUp() throws Exception {
        countryService = EasyMock.createMock(CountryService.class);
    }

    @Test
    public void testNullCountryCode() {
        //There should be no call to getCountryByAlternateCode.
        EasyMock.expect(countryService.getDefaultCountry()).andReturn(getDefaultCountry());
        EasyMock.replay(countryService);

        agencyAddress = new AgencyAddress();
        agencyAddress.setCountryService(countryService);
        agencyAddress.init(getAgency(), getRolodexDto());

        EasyMock.verify(countryService);
    }

    @Test
    public void testValidCountryCode() {
        EasyMock.expect(countryService.getDefaultCountry()).andReturn(getDefaultCountry());
        EasyMock.expect(countryService.getCountryByAlternateCode("USA")).andReturn(getDefaultCountry());
        EasyMock.replay(countryService);

        agencyAddress = new AgencyAddress();
        agencyAddress.setCountryService(countryService);
        RolodexDTO rolodexDto = getRolodexDto();
        rolodexDto.setCountryCode("USA");
        agencyAddress.init(getAgency(), rolodexDto);

        Assert.assertTrue("Country code should be US.", "US".equals(agencyAddress.getAgencyCountryCode()));
        EasyMock.verify(countryService);

    }

    private Country getDefaultCountry() {
        Country.Builder cb = Country.Builder.create("US", "USA", "United States", false, true);
        return cb.build();
    }

    private RolodexDTO getRolodexDto() {
        RolodexDTO r = new RolodexDTO();
        r.setRolodexId(1);
        return r;
    }

    private Agency getAgency() {
        Agency a = new Agency();
        return a;
    }
}
