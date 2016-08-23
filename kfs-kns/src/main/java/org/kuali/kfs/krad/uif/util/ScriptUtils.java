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
package org.kuali.kfs.krad.uif.util;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.type.TypeUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility class for generating JavaScript
 *
 *
 */
public class ScriptUtils {

    /**
     * Translates an Object to a String for representing the given Object as
     * a JavaScript value
     *
     * <p>
     * Handles null, List, Map, and Set collections, along with non quoting for numeric and
     * boolean types. Complex types are treated as a String value using toString
     * </p>
     *
     * @param value - Object instance to translate
     * @return String JS value
     */
    public static String translateValue(Object value) {
        String jsValue = "";

        if (value == null) {
            jsValue = "null";
            return jsValue;
        }

        if (value instanceof List) {
            jsValue = "[";

            List<Object> list = (List<Object>) value;
            for (Object listItem : list) {
                jsValue += translateValue(listItem);
                jsValue += ",";
            }
            jsValue = StringUtils.removeEnd(jsValue, ",");

            jsValue += "]";
        } else if (value instanceof Set) {
            jsValue = "[";

            Set<Object> set = (Set<Object>) value;
            for (Object setItem : set) {
                jsValue += translateValue(setItem);
                jsValue += ",";
            }
            jsValue = StringUtils.removeEnd(jsValue, ",");

            jsValue += "]";
        } else if (value instanceof Map) {
            jsValue = "{";

            Map<Object, Object> map = (Map<Object, Object>) value;
            for (Map.Entry<Object, Object> mapEntry : map.entrySet()) {
                jsValue += mapEntry.getKey().toString() + ":";
                jsValue += translateValue(mapEntry.getValue());
                jsValue += ",";
            }
            jsValue = StringUtils.removeEnd(jsValue, ",");

            jsValue += "}";
        } else {
            boolean quoteValue = true;

            Class<?> valueClass = value.getClass();
            if (TypeUtils.isBooleanClass(valueClass) || TypeUtils.isDecimalClass(valueClass) || TypeUtils
                    .isIntegralClass(valueClass)) {
                quoteValue = false;
            }

            if (quoteValue) {
                jsValue = "\"";
            }

            // TODO: should this go through property editors?
            jsValue += value.toString();

            if (quoteValue) {
                jsValue += "\"";
            }
        }

        return jsValue;
    }

    /**
     * Escapes the ' character present in collection names so it can be properly used in js without causing
     * javascript errors due to an early completion of a ' string.
     * @param name
     * @return
     */
    public static String escapeName(String name) {
        name = name.replace("'", "\\'");
        return name;
    }
}
