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

import org.kuali.kfs.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kim.api.KimConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A values finder for returning KEW rule delegation type codes.
 */
public class DelegationTypeValuesFinder extends KeyValuesBase {
    private static final List<KeyValue> C_DELEGATION_TYPES;

    static {
        final List<KeyValue> delegationTypes = new ArrayList<KeyValue>();
        delegationTypes.add(new ConcreteKeyValue("", ""));
        for (String delegationType : KimConstants.KimUIConstants.DELEGATION_TYPES.keySet()) {
            delegationTypes.add(new ConcreteKeyValue(delegationType, KimConstants.KimUIConstants.DELEGATION_TYPES.get(delegationType)));
        }
        C_DELEGATION_TYPES = Collections.unmodifiableList(delegationTypes);
    }

    @Override
    public List<KeyValue> getKeyValues() {
        return C_DELEGATION_TYPES;
    }
}
