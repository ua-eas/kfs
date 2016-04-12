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
package org.kuali.kfs.kns.datadictionary.control;


import org.kuali.kfs.krad.datadictionary.control.ControlDefinition;

/**
    The text element defines an HTML text control element.
    The size attribute defines the size of the field.
    If the datePicker option is entered, then  the user will be able
    to select a date from a popup calendar.
 */
@Deprecated
public class TextControlDefinition extends ControlDefinitionBase {
    private static final long serialVersionUID = 6075633623507085548L;

	public TextControlDefinition() {
    }

    /**
     * @see ControlDefinition#isText()
     */
    public boolean isText() {
        return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "TextControlDefinition";
    }
}
