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
        agencyAddress.init(getAgency(),getRolodexDto());
        
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
        Country.Builder cb = Country.Builder.create("US","USA","United States",false,true);
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