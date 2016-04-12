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

import org.kuali.rice.core.api.util.KeyValue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract base implementation of {@link KeyValuesFinder}
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class KeyValuesBase implements KeyValuesFinder, Serializable {

    public Collection<String> getOptionLabels() {
    	Collection<String> optionLabels = new ArrayList<String>();

    	Collection<KeyValue> keyLabels = getKeyValues();
        for (KeyValue keyLabel : keyLabels) {
        	optionLabels.add(keyLabel.getValue());
        }
        return optionLabels;
    }

    public Collection<String> getOptionValues() {
    	Collection<String> optionValues = new ArrayList<String>();

    	Collection<KeyValue> keyLabels = getKeyValues();
        for (KeyValue keyLabel : keyLabels) {
        	optionValues.add(keyLabel.getKey());
        }
        return optionValues;
    }

    @Override
	public Map<String, String> getKeyLabelMap() {
        Map<String, String> keyLabelMap = new HashMap<String, String>();

        List<KeyValue> keyLabels = getKeyValues();
        for (KeyValue keyLabel : keyLabels) {
        	keyLabelMap.put(keyLabel.getKey(), keyLabel.getValue());
        }

        return keyLabelMap;
    }

    @Override
	public String getKeyLabel(String key) {
        Map<String, String> keyLabelMap = getKeyLabelMap();

        if (keyLabelMap.containsKey(key)) {
            return keyLabelMap.get(key);
        }
        return null;
    }
    
    @Override
	public List<KeyValue> getKeyValues(boolean includeActiveOnly){
    	return Collections.emptyList();
    }
    
	@Override
	public void clearInternalCache() {
		// do nothing
	}

}
