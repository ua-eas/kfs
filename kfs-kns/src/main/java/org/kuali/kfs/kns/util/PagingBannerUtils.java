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
package org.kuali.kfs.kns.util;

import org.apache.commons.lang.NumberUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.collect.CollectionUtils;

import java.util.Enumeration;

/**
 * Utility for that is used along with the tableRenderPagingBanner.tag.
 *
 *
 *
 */
public final class PagingBannerUtils {

	/** do not call. */
	private PagingBannerUtils() {
		throw new UnsupportedOperationException("do not call");
	}

    /**
     * find the number string in a method to call parameter with the following format parameterPrefix.1 or
     * parameterPrefix.1.bleh
     *
     * @param paramPrefix the
     * @param parameterNames the parameter names.
     * @return the numerical value or -1
     */
    public static int getNumbericalValueAfterPrefix(String paramPrefix, Enumeration<String> parameterNames) {

    	for (String parameterName : CollectionUtils.toIterable(parameterNames)) {
    		if (parameterName.startsWith(paramPrefix)) {
            	parameterName = WebUtils.endsWithCoordinates(parameterName) ? parameterName : parameterName + ".x";
            	String numberStr = StringUtils.substringBetween(parameterName, paramPrefix, ".");
            	if (NumberUtils.isDigits(numberStr)) {
            		return Integer.parseInt(numberStr);
            	}
            }
        }

    	return -1;
    }

    /**
     * same as method above except for use when it is not feasible to use ordinals to identify columns -- for example,
     * if dynamic attributes may be used
     */
    public static String getStringValueAfterPrefix(String paramPrefix, Enumeration<String> parameterNames) {
        for (String parameterName : CollectionUtils.toIterable(parameterNames)) {
            if (parameterName.startsWith(paramPrefix)) {
                parameterName = WebUtils.endsWithCoordinates(parameterName) ? parameterName : parameterName + ".x";
                return StringUtils.substringBetween(parameterName, paramPrefix, ".");
            }
        }

        return "";
    }
}
