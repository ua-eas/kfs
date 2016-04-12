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
package org.kuali.kfs.krad.datadictionary.exporter;

import org.kuali.kfs.krad.exception.DuplicateKeyException;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Adds a litle strong type-checking and validation on top of the generic LinkedHashMap
 * 
 * 
 */
@Deprecated
public class StringMap extends LinkedHashMap<String, Object> {
    private static final long serialVersionUID = 7364206011639131063L;

    /**
     * Associates the given String with the given Map value.
     * 
     * @param key
     * @param value
     */
    public void set(String key, Map<String, Object> value) {
        setUnique(key, value);
    }

    /**
     * Associates the given String with the given String value.
     * 
     * @param key
     * @param value
     */
    public void set(String key, String value) {
        setUnique(key, value);
    }


    /**
     * Verifies that the key isn't blank, and that the value isn't null, and prevents duplicate keys from being used.
     * 
     * @param key
     * @param value
     */
    private void setUnique(String key, Object value) {
        if (value == null) {
            throw new IllegalArgumentException("invalid (null) value");
        }

        if (containsKey(key)) {
            throw new DuplicateKeyException("duplicate key '" + key + "'");
        }

        super.put(key, value);
    }

    @Override
    public Object put(String key, Object value) {
        throw new UnsupportedOperationException("direct calls to put not supported");
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
        throw new UnsupportedOperationException("direct calls to put not supported");
    }
}
