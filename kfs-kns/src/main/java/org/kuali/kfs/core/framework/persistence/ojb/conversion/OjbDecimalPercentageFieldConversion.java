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

import java.math.BigDecimal;

public class OjbDecimalPercentageFieldConversion extends OjbKualiDecimalFieldConversion implements FieldConversion {

    private static BigDecimal oneHundred = new BigDecimal(100.0000);

    /**
     * Convert percentages to decimals for proper storage.
     *
     * @see FieldConversion#javaToSql(Object)
     */
    public Object javaToSql(Object source) {

        // Convert to BigDecimal using existing conversion.
        source = super.javaToSql(source);

        // Check for null, and verify object type.
        // Do conversion if our type is correct (BigDecimal).
        if (source != null && source instanceof BigDecimal) {
            BigDecimal converted = (BigDecimal) source;
            return converted.divide(oneHundred, 4, KualiDecimal.ROUND_BEHAVIOR);
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

        // Check for null, and verify object type.
        // Do conversion if our type is correct (BigDecimal).
        if (source != null && source instanceof BigDecimal) {
            BigDecimal converted = (BigDecimal) source;

            // Once we have converted, we need to do the super conversion to KualiDecimal.
            return super.sqlToJava(converted.multiply(oneHundred));
        } else {
            return null;
        }
    }
}
