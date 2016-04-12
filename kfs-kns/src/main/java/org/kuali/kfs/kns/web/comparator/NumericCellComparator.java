/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2015 The Kuali Foundation
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

import org.displaytag.model.Cell;
import org.kuali.kfs.krad.comparator.NumericValueComparator;

import java.io.Serializable;
import java.util.Comparator;

public class NumericCellComparator implements Comparator, Serializable {

    static final long serialVersionUID = 3449202365486147519L;

    public int compare(Object o1, Object o2) {

        // null guard. non-null value is greater. equal if both are null
        if (null == o1 || null == o2) {
            return (null == o1 && null == o2) ? 0 : ((null == o1) ? -1 : 1);
        }

        String numericCompare1 = CellComparatorHelper.getSanitizedStaticValue((Cell) o1);
        String numericCompare2 = CellComparatorHelper.getSanitizedStaticValue((Cell) o2);

        return NumericValueComparator.getInstance().compare(numericCompare1, numericCompare2);
    }

}
