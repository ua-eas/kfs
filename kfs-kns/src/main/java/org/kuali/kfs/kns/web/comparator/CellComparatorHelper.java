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

import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.lang.StringUtils;
import org.displaytag.model.Cell;
import org.kuali.rice.core.api.util.type.TypeUtils;
import org.kuali.kfs.krad.comparator.NumericValueComparator;
import org.kuali.kfs.krad.comparator.StringValueComparator;
import org.kuali.kfs.krad.comparator.TemporalValueComparator;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class...
 */
public class CellComparatorHelper {

    static private Pattern HREF_ENCLOSURE = Pattern.compile("<a [^>]+>([^<]*)</a>.*", Pattern.MULTILINE);

    /**
     * This method is intended to be used in conjunction with displaytag.
     * 
     * @see #getSanitizedStaticValue(String)
     * 
     * @param cell
     * @return a sanitized version of cell.getStaticValue().toString().
     */
    static public String getSanitizedStaticValue(Cell cell) {
        if (null == cell) {
            return null;
        }

        return null == cell.getStaticValue() ? null : getSanitizedStaticValue(cell.getStaticValue().toString());
    }

    /**
     * Remove all end-of-line and tab characters (\r, \n, \t). If the value is enclosed in an html anchor tag, strip the html anchor
     * tag. If the value ends in one or many "&nbsp;"s, strip them off. Return the modified String.
     * 
     * @param staticValue
     * @return a sanitized version of staticValue
     */
    static public String getSanitizedStaticValue(String staticValue) {

        if (null == staticValue) {
            return null;
        }

        staticValue = StringUtils.replace(staticValue, "\r", "");
        staticValue = StringUtils.replace(staticValue, "\n", "");
        staticValue = StringUtils.replace(staticValue, "\t", "");

        String sanitizedValue = staticValue;

        // Extract the value if it's wrapped in an href.
        Matcher matcher = HREF_ENCLOSURE.matcher(staticValue);
        if (matcher.matches()) {

            sanitizedValue = matcher.group(1).trim();

        }

        // Strip off any "&nbsp;"s if they come at the end of the value.
        while (sanitizedValue.endsWith("&nbsp;")) {

            sanitizedValue = sanitizedValue.substring(0, sanitizedValue.length() - 6).trim();

        }

        return sanitizedValue;

    }

    /**
     * This method returns a comparator to be used for comparing the contents of cells, that is
     * the compareTo method will be invoked w/ displaytag Cell objects
     * @param propClass
     * @return
     */
    public static Comparator getAppropriateComparatorForPropertyClass(Class propClass) {
        // TODO, do we really need to create so many comparators (1 per each cell)?
        if (propClass == null) {
            return new NullCellComparator();
        }
        else if (TypeUtils.isDecimalClass(propClass) || TypeUtils.isIntegralClass(propClass)) {
            return new NumericCellComparator();
        }
        else if (TypeUtils.isTemporalClass(propClass)) {
            return new TemporalCellComparator();
        }
        else if (String.class.equals(propClass)) {
            // StringCellComparator is smarter about nulls than String.CASE_INSENSITIVE_ORDER
            return new StringCellComparator();
        }
        else {
            return ComparableComparator.getInstance();
        }
    }
    
    /**
     * This method returns a comparator to be used for comparing propertyValues (in String form)
     * @param propClass
     * @return
     */
    public static Comparator getAppropriateValueComparatorForPropertyClass(Class propClass) {
        if (propClass == null) {
            return NullValueComparator.getInstance();
        }
        else if (TypeUtils.isDecimalClass(propClass) || TypeUtils.isIntegralClass(propClass)) {
            return NumericValueComparator.getInstance();
        }
        else if (TypeUtils.isTemporalClass(propClass)) {
            return TemporalValueComparator.getInstance();
        }
        else if (String.class.equals(propClass)) {
            // StringCellComparator is smarter about nulls than String.CASE_INSENSITIVE_ORDER
            return StringValueComparator.getInstance();
        }
        else {
            return ComparableComparator.getInstance();
        }
    }
}
