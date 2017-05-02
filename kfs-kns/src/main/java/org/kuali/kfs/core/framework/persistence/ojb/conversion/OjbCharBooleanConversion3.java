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
 *
 * For records in the KEW tables, "0" and "1" are used to represent "false" and "true"
 * respectively which is the standard way to represent these values in OJB.
 * This differs from other pieces of the KNS where "N" and "Y" are used.
 */
public class OjbCharBooleanConversion3 implements FieldConversion {

    /**
     * This handles checking any incoming String value and converts them
     * to the appropriate Boolean value.
     * @see FieldConversion#javaToSql(Object)
     */
    public Object javaToSql(Object source) {
        if (source instanceof String) {
            if ("Y".equals(source)) {
                return Boolean.TRUE;
            } else if ("N".equals(source)) {
                return Boolean.FALSE;
            }
        }
        return source;
    }

    public Object sqlToJava(Object source) {
        return source;
    }
}
