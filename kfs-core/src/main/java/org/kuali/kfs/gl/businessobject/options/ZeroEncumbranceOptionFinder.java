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
package org.kuali.kfs.gl.businessobject.options;

import org.kuali.kfs.gl.Constant;
import org.kuali.kfs.krad.keyvalues.KeyValuesBase;
import org.kuali.kfs.krad.valuefinder.ValueFinder;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Options finder for the options to include zero encumbrances on the open encumbrance lookup
 */
public class ZeroEncumbranceOptionFinder extends KeyValuesBase implements ValueFinder {

    /**
     * Get the values: to include or exclude the zeroed out encumbrances
     *
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyValue> getKeyValues() {
        List<KeyValue> options = new ArrayList<KeyValue>();
        options.add(new ConcreteKeyValue(Constant.ZERO_ENCUMBRANCE_INCLUDE, Constant.ZERO_ENCUMBRANCE_INCLUDE));
        options.add(new ConcreteKeyValue(Constant.ZERO_ENCUMBRANCE_EXCLUDE, Constant.ZERO_ENCUMBRANCE_EXCLUDE));
        return options;
    }

    /**
     * Return the default value - which is to Exclude the zeroed out encumbrances
     *
     * @see org.kuali.rice.kns.lookup.valueFinder.ValueFinder#getValue()
     */
    @Override
    public String getValue() {
        return Constant.ZERO_ENCUMBRANCE_EXCLUDE;
    }

}
