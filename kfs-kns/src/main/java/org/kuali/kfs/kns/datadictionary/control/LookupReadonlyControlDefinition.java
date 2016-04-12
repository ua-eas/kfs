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
    The lookupReadonly control element creates a field with a magnifying
    glass and a read-only value.  This forces the user to change the value
    of the field only by use of the magnifying glass.
 */
@Deprecated
public class LookupReadonlyControlDefinition extends ControlDefinitionBase {
    private static final long serialVersionUID = -5036539644716405540L;

	public LookupReadonlyControlDefinition() {
    }

    /**
     * @see ControlDefinition#isLookupReadonly()
     */
    public boolean isLookupReadonly() {
        return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "LookupReadonlyControlDefinition";
    }
}
