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
package org.kuali.kfs.kns.web.ui;

import org.apache.commons.lang.StringUtils;
import org.displaytag.decorator.DisplaytagColumnDecorator;
import org.displaytag.exception.DecoratorException;
import org.displaytag.properties.MediaTypeEnum;
import org.kuali.kfs.kns.web.comparator.CellComparatorHelper;
import org.kuali.kfs.krad.util.KRADConstants;

import javax.servlet.jsp.PageContext;

/** @see #decorate(Object, PageContext, MediaTypeEnum) */
@Deprecated
public class FormatAwareDecorator implements DisplaytagColumnDecorator {

    /**
     * Empty values don't show up properly in HTML. So, the String "&nbsp;" is substituted for an empty or null value of cellValue
     * if mediaType is MediaTypeEnum.HTML. If mediaType is not {@link MediaTypeEnum.HTML} and cellValue is not null, then
     * <code>CellComparatorHelper.getSanitizedValue(cellValue.toString())</code> is returned.
     *
     * @param cellValue
     * @param pageContext
     * @param mediaType
     */
    public Object decorate(Object cellValue, PageContext pageContext, MediaTypeEnum mediaType) throws DecoratorException {

        if (null == cellValue) {
            return getEmptyStringFor(mediaType);
        }

        final String decoratedOutput;

        if (isCollection(cellValue)) {
        	decoratedOutput = createCollectionString(cellValue);
        } else {
        	decoratedOutput = MediaTypeEnum.HTML.equals(mediaType) ? cellValue.toString() : CellComparatorHelper
                    .getSanitizedStaticValue(cellValue.toString());
        }

        return StringUtils.isBlank(decoratedOutput) ? getEmptyStringFor(mediaType) : StringUtils.trim(decoratedOutput);
    }

    /**
     * Takes a cellValue which is a collection and creates a String representations.
     *
     * <p>
     * If a column resulting from lookup contains collection values, each of the collection entry
     * should be printed on one line (i.e. separated by a <br/>). If there is no entry in the
     * collection, then we'll just print an &nbsp for the column.
     * </p>
     *
     * @param cellValue the cell value to convert
     * @return the string representation of the cell value
     */
    private static String createCollectionString(Object cellValue) {
    	String decoratedOutput = "";

    	String cellContentToBeParsed = cellValue.toString().substring(1, cellValue.toString().indexOf("]"));
        if (StringUtils.isNotBlank(cellContentToBeParsed)) {
            String[] parsed = cellContentToBeParsed.split(",");
            for (String elem : parsed) {
                decoratedOutput = decoratedOutput + elem + "<br/>";
            }
        }
        return decoratedOutput;
    }

    /**
     * Checks if a cell value is a Collection
     *
     * @param cellValue to check
     * @return true if a Collection
     */
    private static boolean isCollection(Object cellValue) {
        return cellValue != null && (cellValue.toString().indexOf("[") == 0 && cellValue.toString().indexOf("]") > 0 && ((cellValue.toString().length() -1) == cellValue.toString().indexOf("]")));
    }

    /**
     * Gets an empty string type based on the media type.
     *
     * @param mediaType the media type
     * @return the empty string
     */
    private static String getEmptyStringFor(MediaTypeEnum mediaType) {
    	return MediaTypeEnum.HTML.equals(mediaType) ? "&nbsp" : KRADConstants.EMPTY_STRING;
    }

}
