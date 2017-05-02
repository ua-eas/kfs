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
package org.kuali.kfs.core.framework.persistence.ojb.conversion;

import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;

/**
 * This class originates from a similar class in the Kuali Financial System and has been adapted from
 * that original state which was originally authored by the Kuali Nervous System team.
 */
public class OjbCharBooleanConversion2 implements FieldConversion {
    /**
     * This handles checking the boolean value coming in and converts it to
     * the appropriate Y or N value.
     * @see FieldConversion#javaToSql(Object)
     */
    public Object javaToSql(Object source) {
        if (source instanceof Boolean) {
            if (source != null) {
                Boolean b = (Boolean) source;
                return b.booleanValue() ? "Y" : "N";
            } else {
                return null;
            }
        } else if (source instanceof String) {
            if ("true".equals(source)) {
                return "Y";
            } else if ("false".equals(source)) {
                return "N";
            }
        }
        return source;
    }

    /**
     * This handles checking the sql coming back from the database and converting
     * it to the appropriate boolean true or false value.
     * @see FieldConversion#sqlToJava(Object)
     */
    public Object sqlToJava(Object source) {
        try {
            if (source instanceof String) {
                if (source != null) {
                    String s = (String) source;
                    return Boolean.valueOf("YT1".contains(s));
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
