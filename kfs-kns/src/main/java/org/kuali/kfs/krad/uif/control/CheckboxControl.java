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
 * Represents a HTML Checkbox control. Typically used for boolean attributes (where the
 * value is either on/off, true/false)
 */
public class CheckboxControl extends ControlBase implements ValueConfiguredControl {
    private static final long serialVersionUID = -1397028958569144230L;

    private String value;

    public CheckboxControl() {
        super();
    }

    /**
     * The value that will be submitted when the checkbox control is checked
     * <p>
     * <p>
     * Value can be left blank, in which case the checkbox will submit a boolean value that
     * will populate a boolean property. In cases where the checkbox needs to submit another value (for
     * instance possibly in the checkbox group) the value can be set which will override the default.
     * </p>
     *
     * @return String value for checkbox
     */
    public String getValue() {
        return value;
    }

    /**
     * Setter for the value that should be submitted when the checkbox is checked
     *
     * @param value
     */
    public void setValue(String value) {
        this.value = value;
    }
}
