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
package org.kuali.kfs.krad.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MessageUtils {
    /**
     * Interpolates a message based on values contained in the data map, assuming message is formatted using a ${key} syntax.
     * 
     * @param message
     *            the message to be interpolated
     * @param data
     *            Map<String, String> containing the data to be used for interpolation
     * @return the interpolated message
     */
    public static String interpolate(String message, String... data) {
        if (message != null) {
            for (int i = 0; i < data.length; i++) {
                message = message.replaceAll("\\$\\{" + i + "\\}", "" + escape(data[i]));
            }
        }
        return message;
    }

    /**
     * Interpolates a message based on parameter index, assuming message is formatted using a ${0}..${n} syntax
     * 
     * @param message
     *            the message to be interpolated
     * @param data
     *            varargs to be used for interpolation
     * @return the interpolated message
     */
    public static String interpolate(String message, Map<String, Object> data) {
        if (message != null && data != null) {
            Set<String> fields = findFields(message);
            for (String s : fields) {
            	if(data.get(s) != null){
            		message = message.replaceAll("\\$\\{" + s + "\\}", "" + escape(data.get(s).toString()));
            	}
            }
        }
        return message;
    }
    
    /**
     * Interpolates a message which requires only a single property replacement.
     * 
     * @param message
     * @param parameter
     * @param value
     * @return the interpolated message
     */
    public static String interpolate(String message, String parameter, Object value){
    	message = message.replaceAll("\\$\\{" + parameter + "\\}", "" + escape(value.toString()));
    	return message;
    }
    
    /**
     * Adds an escape to all characters requiring an escape.
     * 
     * @param input
     * @return the input string with characters escaped.
     */
    private static String escape(String input) {
        char[] toEscape = {'\\', '$', '.', '*', '+', '?', '|', '(', ')', '[', ']', '{', '}'};
        for (char c : toEscape) {
            input = input.replaceAll("\\" + c, "\\\\\\" + c);
        }
        return input;
    }
    
    /**
     * Returns a Set<String> of all interpolation targets (fields) within a String.
     * 
     * @param input
     *            the String from which to extract the interpolation targets
     * @return Set<String> containing the field names of the interpolation targets
     */
    public static Set<String> findFields(String input) {
        Set<String> result = new HashSet<String>();
        int begin = input.indexOf("${");
        while (begin != -1) {
            int end = input.indexOf("}", begin);
            result.add(input.substring(begin + 2, end));
            begin = input.indexOf("${", end);
        }
        return result;
    }

}
