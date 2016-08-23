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
package org.kuali.kfs.kns.bo.lookup;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.kfs.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.location.api.country.Country;
import org.kuali.rice.location.api.services.LocationApiServiceLocator;
import org.kuali.rice.location.api.state.State;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StateValuesFinder  extends KeyValuesBase {
    private String countryCode = "";

    @Override
    public List<KeyValue> getKeyValues() {
        List<KeyValue> labels = new ArrayList<KeyValue>();
        if (StringUtils.isEmpty(this.countryCode)) {
            Country defaultCountry = LocationApiServiceLocator.getCountryService().getDefaultCountry();
            if (defaultCountry != null) {
                countryCode = defaultCountry.getCode();
            }
        }
        List<State> baseCodes = LocationApiServiceLocator.getStateService().findAllStatesInCountry(countryCode);
        List<State> codes = new ArrayList<State>( baseCodes );
        Collections.sort(codes, new Comparator<State>() {
            @Override
            public int compare(State o1, State o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        List<KeyValue> newLabels = new ArrayList<KeyValue>();
        newLabels.add(new ConcreteKeyValue("", ""));
        for (State state : codes) {
            if(state.isActive()) {
                newLabels.add(new ConcreteKeyValue(state.getCode(), state.getName()));
            }
        }
        labels = newLabels;

        return labels;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }


    @Override
    public void clearInternalCache() {
    }
}
