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
package org.kuali.kfs.krad.comparator;

import org.kuali.rice.core.web.format.DateFormatter;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

public class TemporalValueComparator implements Comparator, Serializable {
    private static final TemporalValueComparator theInstance = new TemporalValueComparator();

    public TemporalValueComparator() {
    }

    public static TemporalValueComparator getInstance() {
        return theInstance;
    }

    public int compare(Object o1, Object o2) {

        // null guard. non-null value is greater. equal if both are null
        if (null == o1 || null == o2) {
            return null == o1 && null == o2 ? 0 : null == o1 ? -1 : 1;
        }

        String s1 = (String) o1;
        String s2 = (String) o2;

        DateFormatter f1 = new DateFormatter();

        Date d1 = (Date) f1.convertFromPresentationFormat(s1);
        Date d2 = (Date) f1.convertFromPresentationFormat(s2);

        if (null == d1 || null == d2) {
            return null == d1 && null == d2 ? 0 : null == d1 ? -1 : 1;
        }

        return d1.equals(d2) ? 0 : d1.before(d2) ? -1 : 1;
    }
}
