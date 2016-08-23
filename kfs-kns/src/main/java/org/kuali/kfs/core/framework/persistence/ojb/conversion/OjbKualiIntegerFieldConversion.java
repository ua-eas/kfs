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
import org.kuali.rice.core.api.util.type.KualiInteger;

import java.math.BigInteger;

public class OjbKualiIntegerFieldConversion implements FieldConversion {

    /**
     * @see FieldConversion#javaToSql(Object)
     */
    public Object javaToSql(Object source) {
        Object converted = source;

        if (source instanceof KualiInteger) {
            converted = new Long(((KualiInteger) source).longValue());
        }

        return converted;
    }

    /**
     * @see FieldConversion#sqlToJava(Object)
     */
    public Object sqlToJava(Object source) {
        Object converted = source;

        if (source instanceof Long) {
            converted = new KualiInteger(((Long) source).longValue());
        } else if (source instanceof BigInteger) {
            converted = new KualiInteger((BigInteger) source);
        }

        return converted;
    }
}
