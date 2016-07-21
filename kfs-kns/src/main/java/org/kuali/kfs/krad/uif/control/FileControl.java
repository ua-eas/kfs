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
 * Represents a HTML File control, generally rendered as an input control with
 * type 'file'. Allows user to upload a file to the application
 *
 * 
 */
public class FileControl extends ControlBase implements SizedControl {
    private static final long serialVersionUID = -5919326390841646189L;

    private int size;

    public FileControl() {
        super();
    }

    /**
     * @see SizedControl#getSize()
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

}
