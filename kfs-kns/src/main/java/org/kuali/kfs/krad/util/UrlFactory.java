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
package org.kuali.kfs.krad.util;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.Properties;

/**
 * This class Provides utility methods for re/building URLs.
 */

public class UrlFactory {
    private static Logger LOG = Logger.getLogger(UrlFactory.class);
    /**
     * Creates a new URL by taking the given URL and appending the parameter names and values from the given Properties instance to
     * it. Note: parameter names must be non-blank; parameter values must be non-null.
     *
     * @param baseUrl the URL string used as the basis for reconstruction
     * @param params Properties instance containing the desired parameters and their values
     * @throws IllegalArgumentException if the given url is null or empty
     * @throws IllegalArgumentException if the given Properties instance is null
     * @throws IllegalArgumentException if a parameter name is null or empty, or a parameter value is null
     * @throws RuntimeException if there is a problem encoding a parameter name or value into UTF-8
     * @return a newly-constructed URL string which has the given parameters and their values appended to it
     */
    private static URLCodec urlCodec = new URLCodec("UTF-8");

    public static String parameterizeUrl(String baseUrl, Properties params) {
        baseUrl = StringUtils.trim(baseUrl);
        if (StringUtils.isEmpty(baseUrl)) {
            throw new IllegalArgumentException("invalid (blank) base URL");
        }
        if (params == null) {
            throw new IllegalArgumentException("invalid (null) Properties");
        }


        StringBuffer ret = new StringBuffer(baseUrl);
        // Only start with ? if it has not been added to the url
        String delimiter = (ret.indexOf("?") == -1) ? "?" : "&";
        for (Object key : params.keySet()) {
            String paramName = StringUtils.trim((String) key);
            String paramValue = params.getProperty(paramName);
            ret.append(delimiter);
            if (StringUtils.isEmpty(paramName)) {
                throw new IllegalArgumentException("invalid (blank) paramName");
            }
            if (paramValue == null) {
                ret.append(paramName);
                ret.append("=");
            } else {
                try {
                    ret.append(paramName);
                    ret.append("=");
                    ret.append(urlCodec.encode(paramValue));
                } catch (EncoderException ex) {
                    LOG.error("Unable to encode parameter name or value: " + paramName + "=" + paramValue, ex);
                    throw new RuntimeException("Unable to encode parameter name or value: " + paramName + "=" + paramValue, ex);
                }
            }
            delimiter = "&";
        }

        return ret.toString();
    }

    public static String encode(String value) {
        try {
            return urlCodec.encode(value);
        } catch (EncoderException ex) {
            throw new RuntimeException("Unable to encode value: " + value, ex);
        }
    }
}
