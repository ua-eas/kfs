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
package org.kuali.kfs.core.framework.persistence.ojb.conversion;

import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;

/**
 * Performs conversion of java boolean fields to and from the database
 */
public class OjbCharBooleanConversion implements FieldConversion {
    public static final String DATABASE_BOOLEAN_TRUE_STRING_REPRESENTATION = "Y";
    public static final String DATABASE_BOOLEAN_FALSE_STRING_REPRESENTATION = "N";

    /**
     * @see FieldConversion#javaToSql(Object)
     */
    public Object javaToSql(Object source) {
        if (source instanceof Boolean) {
            if (source != null) {
                Boolean b = (Boolean) source;
                return b.booleanValue() ? DATABASE_BOOLEAN_TRUE_STRING_REPRESENTATION : DATABASE_BOOLEAN_FALSE_STRING_REPRESENTATION;
            } else {
                return null;
            }
        } else if (source instanceof String) {
            if ("true".equalsIgnoreCase((String) source) || "yes".equalsIgnoreCase((String) source) || "y".equalsIgnoreCase((String) source)) {
                return DATABASE_BOOLEAN_TRUE_STRING_REPRESENTATION;
            } else if ("false".equalsIgnoreCase((String) source) || "no".equalsIgnoreCase((String) source) || "n".equalsIgnoreCase((String) source)) {
                return DATABASE_BOOLEAN_FALSE_STRING_REPRESENTATION;
            }
        }
        return source;
    }

    /**
     * @see FieldConversion#sqlToJava(Object)
     */
    public Object sqlToJava(Object source) {
        try {
            if (source instanceof String) {
                if (source != null) {
                    String s = (String) source;
                    String trueValues = DATABASE_BOOLEAN_TRUE_STRING_REPRESENTATION + "T1";
                    return trueValues.contains(s);
                } else {
                    return null;
                }
            }
            return source;
        } catch (Throwable t) {
            t.printStackTrace();
            throw new RuntimeException("I have exploded converting types", t);
        }
    }

}
