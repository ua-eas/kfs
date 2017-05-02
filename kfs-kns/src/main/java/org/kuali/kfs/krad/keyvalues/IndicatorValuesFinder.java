/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
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
package org.kuali.kfs.krad.keyvalues;

import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class returns list of boolean key value pairs.
 */
public class IndicatorValuesFinder extends org.kuali.kfs.krad.keyvalues.KeyValuesBase {

    public static final IndicatorValuesFinder INSTANCE = new IndicatorValuesFinder();

    protected static final List<KeyValue> ACTIVE_LABELS;

    static {
        final List<KeyValue> activeLabels = new ArrayList<KeyValue>(3);
        activeLabels.add(new ConcreteKeyValue(KRADConstants.YES_INDICATOR_VALUE, "Yes"));
        activeLabels.add(new ConcreteKeyValue(KRADConstants.NO_INDICATOR_VALUE, "No"));
        activeLabels.add(new ConcreteKeyValue("", "Both"));

        ACTIVE_LABELS = Collections.unmodifiableList(activeLabels);
    }

    @Override
    public List<KeyValue> getKeyValues() {
        return ACTIVE_LABELS;
    }
}
