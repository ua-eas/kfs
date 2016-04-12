/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2015 The Kuali Foundation
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
package org.kuali.kfs.kns.bo.lookup;

import org.kuali.rice.location.api.country.Country;
import org.kuali.rice.location.api.country.CountryService;
import org.kuali.rice.location.api.services.LocationApiServiceLocator;

import java.util.List;

/**
 * This class returns list of country value pairs.
 */
public class CountryValuesFinder extends AbstractCountryValuesFinderBase {
    /**
     * Returns all countries, regardless of active status or restricted status
     */
    @Override
    protected List<Country> retrieveCountriesForValuesFinder() {
        CountryService countryService = LocationApiServiceLocator.getCountryService();
        return countryService.findAllCountries();
    }
}
