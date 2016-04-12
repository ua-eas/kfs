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
package org.kuali.kfs.krad.uif.control;


/**
 * Represents a HTML Select control. Provides preset options for the User to
 * choose from by a drop down
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class SelectControl extends MultiValueControlBase implements SizedControl {
    private static final long serialVersionUID = 6443247954759096815L;

    private int size;
    private boolean multiple;

    public SelectControl() {
        size = 1;
        multiple = false;
    }

    /**
     * Vertical size of the control. This determines how many options can be
     * seen without using the control scoll bar. Defaults to 1
     *
     * @return int size
     */
    public int getSize() {
        return this.size;
    }

    /**
     * @see SizedControl#setSize(int)
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Indicates whether multiple values can be selected. Defaults to false
     * <p>
     * If multiple is set to true, the underlying property must be of Array type
     * </p>
     *
     * @return boolean true if multiple values can be selected, false if only
     *         one value can be selected
     */
    public boolean isMultiple() {
        return this.multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

}
