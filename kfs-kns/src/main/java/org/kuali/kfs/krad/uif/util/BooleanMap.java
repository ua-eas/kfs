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
package org.kuali.kfs.krad.uif.util;

import java.util.HashMap;
import java.util.Set;

/**
 * Map implementation takes a <code>Set</code> of Strings and converts to Map
 * where the string is the map key and value is the Boolean true, convenience
 * collection for expression language
 */
public class BooleanMap extends HashMap<String, Boolean> {
    private static final long serialVersionUID = 4042557657401395547L;

    public BooleanMap(Set<String> keys) {
        super();

        for (String key : keys) {
            this.put(key, Boolean.TRUE);
        }
    }

    /**
     * Overrides the get method to return Boolean false if the key does not
     * exist in the Map
     *
     * @see java.util.HashMap#get(java.lang.Object)
     */
    @Override
    public Boolean get(Object key) {
        if (super.containsKey(key)) {
            return super.get(key);
        }

        return Boolean.FALSE;
    }

}
