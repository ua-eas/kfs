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
package org.kuali.kfs.kns.datadictionary.control;


import org.kuali.kfs.krad.datadictionary.control.ControlDefinition;

/**
 *  The lookupHidden control element creates a field with a magnifying
                        glass, but no value showing.  This can be used to do a lookup to
                        return a value which will appear in another field.
 */
@Deprecated
public class LookupHiddenControlDefinition extends ControlDefinitionBase {
    private static final long serialVersionUID = -2145156789968831921L;

	public LookupHiddenControlDefinition() {
    }

    /**
     * @see ControlDefinition#isLookupHidden()
     */
    public boolean isLookupHidden() {
        return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "LookupHiddenControlDefinition";
    }
}
