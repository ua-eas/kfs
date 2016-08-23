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
package org.kuali.kfs.krad.uif.field;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Object that is returned for Ajax attribute queries and exposed
 * as JSON
 */
public class AttributeQueryResult implements Serializable {
    private static final long serialVersionUID = -6688384365943881516L;

    private String resultMessage;
    private String resultMessageStyleClasses;

    private Map<String, String> resultFieldData;
    private List<String> resultData;

    public AttributeQueryResult() {
        resultFieldData = new HashMap<String, String>();
        resultData = new ArrayList<String>();
    }

    /**
     * Message text that should display (if non empty) with the results.
     * Can be used to given messages such as data not found
     *
     * @return String text to display with results
     */
    public String getResultMessage() {
        return resultMessage;
    }

    /**
     * Setter for the result message text
     *
     * @param resultMessage
     */
    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    /**
     * CSS Style classes that should be applied to the result message text
     *
     * @return String of CSS style classes
     */
    public String getResultMessageStyleClasses() {
        return resultMessageStyleClasses;
    }

    /**
     * Setter for the CSS style classes to use for the return message
     *
     * @param resultMessageStyleClasses
     */
    public void setResultMessageStyleClasses(String resultMessageStyleClasses) {
        this.resultMessageStyleClasses = resultMessageStyleClasses;
    }

    /**
     * Returns data for multiple fields as a Map where key is the field
     * name and map value is the field value
     *
     * @return Map<String, String> result field data
     */
    public Map<String, String> getResultFieldData() {
        return resultFieldData;
    }

    /**
     * Setter for the map field data
     *
     * @param resultFieldData
     */
    public void setResultFieldData(Map<String, String> resultFieldData) {
        this.resultFieldData = resultFieldData;
    }

    /**
     * Result data as a List of string objects for queries that
     * return single field multiple values
     *
     * @return List<String> result data
     */
    public List<String> getResultData() {
        return resultData;
    }

    /**
     * Setter for the attribute query result data
     *
     * @param resultData
     */
    public void setResultData(List<String> resultData) {
        this.resultData = resultData;
    }
}
