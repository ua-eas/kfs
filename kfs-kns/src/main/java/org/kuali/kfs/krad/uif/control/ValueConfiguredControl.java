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
 * Indicates <code>Control</code> types that can be configured with a static value to submit, as opposed to pulling
 * the value from the underlying property
 *
 * <p>
 * Examples of this are {@link CheckboxControl}, which can be configured with a value that will be submitted when the
 * checkbox is checked. For example, suppose we had a model property of type Set<String> that represents selected car
 * types. In the UI, we can present a list of available car types with a checkbox next to each. The value for the
 * each checkbox will be the model type of the associated role: 'Ford', 'GM', 'Honda'. For each checkbox selected the
 * associated value will be submitted and populated into the Set<String> on the model.
 * </p>
 *
 *
 */
public interface ValueConfiguredControl {

    /**
     * Retrieves the value that will be submitted with the control
     *
     * @return String control value
     */
    public String getValue();

    /**
     * Setter for the value that should be submitted with the control
     *
     * @param value
     */
    public void setValue(String value);
}
