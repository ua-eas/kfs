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
import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.math.BigDecimal;


/**
 * This class converts NUMERIC fields from the database into KualiDecimal instances, and vice-versa.
 *
 *
 */

public class OjbKualiDecimalFieldConversion implements FieldConversion {
    private static final long serialVersionUID = 2450111778124335242L;

    /**
     * @see FieldConversion#javaToSql(Object)
     */
    public Object javaToSql(Object source) {
        Object converted = source;

        if (source instanceof KualiDecimal) {
            converted = ((KualiDecimal) source).bigDecimalValue();
        }

        return converted;
    }

    /**
     * @see FieldConversion#sqlToJava(Object)
     */
    public Object sqlToJava(Object source) {
        Object converted = source;

        if (source instanceof BigDecimal) {
            converted = new KualiDecimal((BigDecimal) source);
        }

        return converted;
    }
}
