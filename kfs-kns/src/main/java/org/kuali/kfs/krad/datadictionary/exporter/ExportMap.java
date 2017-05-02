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
package org.kuali.kfs.krad.datadictionary.exporter;

import java.util.Collections;
import java.util.Map;

/*
 * An ExportMap represents an entry or definition from the dataDictionary as a Map of the contents of that entry or definintion, and
 * the key by which that entry or definition will be stored in the parent Map.
 *
 *
 */
@Deprecated
public class ExportMap {
    private final String exportKey;
    private final StringMap exportData;

    public ExportMap(String exportKey) {
        this.exportKey = exportKey;
        this.exportData = new StringMap();
    }


    /**
     * @return exportKey associated with this instance
     */
    public String getExportKey() {
        return this.exportKey;
    }

    /**
     * @return unmodifiable copy of the exportData associated with this Map
     */
    public Map<String, Object> getExportData() {
        return Collections.unmodifiableMap(this.exportData);
    }


    /**
     * Adds the ExportMap's exportKey and exportData as a key,value pair to this Map
     */
    public void set(ExportMap map) {
        if (map == null) {
            throw new IllegalArgumentException("invalid (null) map");
        }

        exportData.set(map.getExportKey(), map.getExportData());
    }

    /**
     * If the given map is not null, adds the ExportMap's exportKey and exportData as a key,value pair to this Map.
     */
    public void setOptional(ExportMap map) {
        if (map != null) {
            set(map);
        }
    }

    /**
     * Adds the given key,value pair to this Map
     *
     * @param key
     * @param value
     */
    public void set(String key, String value) {
        if (key == null) {
            throw new IllegalArgumentException("invalid (null) key");
        }
        if (value == null) {
            throw new IllegalArgumentException("invalid (null) value - key=" + key);
        }

        exportData.set(key, value);
    }


    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return this.exportKey + "(" + this.exportData.size() + " children)";
    }
}
