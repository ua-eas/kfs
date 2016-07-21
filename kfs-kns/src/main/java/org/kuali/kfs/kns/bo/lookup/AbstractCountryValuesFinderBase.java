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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.kfs.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.location.api.country.Country;
import org.kuali.rice.location.api.services.LocationApiServiceLocator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * An abstract KeyValuesBase for defining a values finder which produces a list of Countries.  Sub-classes should
 * extend this class and override {@link #retrieveCountriesForValuesFinder()} in order to produce a list of
 * countries to include.
 */
public abstract class AbstractCountryValuesFinderBase extends KeyValuesBase {
    @Override
    public List<KeyValue> getKeyValues() {
        Country defaultCountry = getDefaultCountry();
        List<Country> countries = new ArrayList<Country>(retrieveCountriesForValuesFinder());

        List<KeyValue> values = new ArrayList<KeyValue>(countries.size() + 1);
        values.add(new ConcreteKeyValue("", ""));
        if (defaultCountry != null) {
            values.add(new ConcreteKeyValue(defaultCountry.getCode(), defaultCountry.getName()));
        }

        Collections.sort(countries, new Comparator<Country>() {
            @Override
            public int compare(Country country1, Country country2) {
                // some institutions may prefix the country name with an asterisk if the country no longer exists
                // the country names will be compared without the asterisk
                String sortValue1 = StringUtils.trim(StringUtils.removeStart(country1.getName(), "*"));
                String sortValue2 = StringUtils.trim(StringUtils.removeStart(country2.getName(), "*"));
                return sortValue1.compareToIgnoreCase(sortValue2);
            }

        });

        // the default country may show up twice, but that's fine
        for (Country country : countries) {
            if (country.isActive()) {
                values.add(new ConcreteKeyValue(country.getCode(), country.getName()));
            }
        }
        return values;
    }

    /**
     * Returns a list of countries that will be added to the result of {@link #getKeyValues()}.  Note that the result
     * may be filtered by active status
     *
     * @return a List of countries to include in the values returned by this finder
     */
    protected abstract List<Country> retrieveCountriesForValuesFinder();

    /**
     * Returns the default country to use for this values finder.  If no default country is returned, none will be
     * used.  The default implementation of this method will defer to {@link org.kuali.rice.location.api.country.CountryService#getDefaultCountry()}.
     *
     * @return the default country to use for this values finder, or null if no default country should be used
     */
    protected Country getDefaultCountry() {
        return LocationApiServiceLocator.getCountryService().getDefaultCountry();
    }
}
