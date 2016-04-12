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
package org.kuali.kfs.krad.keyvalues;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** a simple values finder that uses a map as the key/value source. */
public class MapValuesFinder implements org.kuali.kfs.krad.keyvalues.KeyValuesFinder {

    private Map<String, String> keyValues;

    public MapValuesFinder(Map<String, String> keyValues) {
        setKeyValues(keyValues);
    }

    public void setKeyValues(Map<String, String> keyValues) {
        if (keyValues == null) {
            throw new IllegalArgumentException("keyValues was null");
        }
        this.keyValues = Collections.unmodifiableMap(new HashMap<String, String>(keyValues));
    }

    @Override
    public List<KeyValue> getKeyValues() {
        final List<KeyValue> list = new ArrayList<KeyValue>();
        for (Map.Entry<String, String> entry : keyValues.entrySet()) {
            list.add(new ConcreteKeyValue(entry));
        }
        return Collections.unmodifiableList(list);
    }

    @Override
    public List<KeyValue> getKeyValues(boolean includeActiveOnly) {
        return getKeyValues();
    }

    @Override
    public Map<String, String> getKeyLabelMap() {
        return keyValues;
    }

    @Override
    public String getKeyLabel(String key) {
        return keyValues.get(key);
    }

    @Override
    public void clearInternalCache() {
        //do nothing
    }
}
