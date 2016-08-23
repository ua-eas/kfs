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
package org.kuali.kfs.krad.uif.control;

/**
 * Represents a group of HTML checkbox controls. Provides preset options for the
 * user to choose by a series of checkbox controls. Only or more options can be
 * select
 *
 *
 */
public class CheckboxGroupControl extends MultiValueControlBase {
    private static final long serialVersionUID = 8800478332086081970L;

    private String delimiter;

    public CheckboxGroupControl() {
        super();
    }

    /**
     * Delimiter string to be rendered between the checkbox group options,
     * defaults to none
     *
     * @return String delimiter string
     */
    public String getDelimiter() {
        return this.delimiter;
    }

    /**
     * Setter for the string delimiter for each checkbox option
     *
     * @param delimiter
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

}
