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
package org.kuali.kfs.kns.web.comparator;

import java.io.Serializable;
import java.util.Comparator;

public class NullValueComparator implements Comparator, Serializable {
    private static final NullValueComparator theInstance = new NullValueComparator();

    public NullValueComparator() {
    }

    public static NullValueComparator getInstance() {
        return theInstance;
    }

    public int compare(Object o1, Object o2) {
        if (o1 == null && o2 == null) {
            return 0;
        }
        if (o1 == null) {
            return -1;
        }
        else if (o2 == null) {
            return 1;
        }
        else {
            // probably won't go into this code segment, but doing it just in case
            return CellComparatorHelper.getAppropriateValueComparatorForPropertyClass(o1.getClass()).compare(o1, o2);
        }
    }
}
