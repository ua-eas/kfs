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
package org.kuali.kfs.kns.datadictionary.exporter;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Deprecated
public abstract class DataDictionaryMapBase implements Map {

    public int size() {
        throw new UnsupportedOperationException( "This operation not supported on a " + this.getClass().getName() );
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean containsKey(Object key) {
        return get( key ) != null;
    }

    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException( "This operation not supported on a " + this.getClass().getName() );
    }

    public Object put(Object key, Object value) {
        throw new UnsupportedOperationException( "This operation not supported on a " + this.getClass().getName() );
    }

    public Object remove(Object key) {
        throw new UnsupportedOperationException( "This operation not supported on a " + this.getClass().getName() );
    }

    public void putAll(Map map) {
        throw new UnsupportedOperationException( "This operation not supported on a " + this.getClass().getName() );
    }

    public void clear() {
        throw new UnsupportedOperationException( "This operation not supported on a " + this.getClass().getName() );
    }

    public Set keySet() {
        throw new UnsupportedOperationException( "This operation not supported on a " + this.getClass().getName() );
    }

    public Collection values() {
        throw new UnsupportedOperationException( "This operation not supported on a " + this.getClass().getName() );
    }

    public Set entrySet() {
        throw new UnsupportedOperationException( "This operation not supported on a " + this.getClass().getName() );
    }
    
}
