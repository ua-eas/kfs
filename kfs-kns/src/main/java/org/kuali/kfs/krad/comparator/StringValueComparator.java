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

import java.io.Serializable;
import java.util.Comparator;

public final class StringValueComparator implements Serializable, Comparator<String> {
    private static final StringValueComparator INSTANCE = new StringValueComparator();

    private StringValueComparator() {
        //private
    }

    public static StringValueComparator getInstance() {
        return INSTANCE;
    }

    @Override
    public int compare(String o1, String o2) {
        // null guard. non-null value is greater. equal if both are null
        if (null == o1 || null == o2) {
            return (null == o1 && null == o2) ? 0 : ((null == o1) ? -1 : 1);
        }

        return String.CASE_INSENSITIVE_ORDER.compare(o1, o2);
    }
}
