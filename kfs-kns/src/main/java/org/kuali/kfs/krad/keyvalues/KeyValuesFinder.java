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

import java.util.List;
import java.util.Map;

/**
 * Defines basic methods value finders
 *
 * 
 */
public interface KeyValuesFinder {

    /**
     * Builds a list of key values representations for valid value selections.
     *
     * @return List of KeyValue objects
     */
    public List<KeyValue> getKeyValues();

    /**
     * Builds a list of key values representations for valid value selections.
     *
     * @param includeActiveOnly whether to only include active values in the list
     *  
     * @return List of KeyValue objects.
     */
    public List<KeyValue> getKeyValues(boolean includeActiveOnly);

    /**
     * Returns a map with the key as the key of the map and the label as the value. Used to render the label instead of the code in
     * the jsp when the field is readonly.
     *
     * @return
     */
    public Map<String, String> getKeyLabelMap();

    /**
     * Returns the label for the associated key.
     *
     * @param key
     * @return
     */
    public String getKeyLabel(String key);
    
   
    /**
     * Clears any internal cache that is being maintained by the value finder
     */
    public void clearInternalCache();

}
