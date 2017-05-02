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
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;

import java.math.BigDecimal;

public class OjbKualiIntegerPercentageFieldConversion extends OjbDecimalPercentageFieldConversion {

    /**
     * Convert percentages to decimals for proper storage.
     *
     * @see FieldConversion#javaToSql(Object)
     */
    public Object javaToSql(Object source) {
        if (source != null && source instanceof KualiInteger) {
            return super.javaToSql(((KualiInteger) source).bigDecimalValue());
        } else {
            return null;
        }
    }

    /**
     * Convert database decimals to 'visual' percentages for use in our business objects.
     *
     * @see FieldConversion#sqlToJava(Object)
     */
    public Object sqlToJava(Object source) {
        if (source != null && source instanceof BigDecimal) {
            return new KualiInteger(((KualiDecimal) (super.sqlToJava(source))).bigDecimalValue());
        } else {
            return null;
        }
    }

}
