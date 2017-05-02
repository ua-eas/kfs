/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
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
package org.kuali.kfs.kns.datadictionary;

import org.kuali.kfs.krad.datadictionary.DataDictionaryDefinition;

/**
 * The subSectionHeader allows the section to be separated
 * into sub-sections, each with its own name.
 */
public class MaintainableSubSectionHeaderDefinition extends MaintainableItemDefinition implements SubSectionHeaderDefinitionI {
    private static final long serialVersionUID = 3752757590555028866L;

    public MaintainableSubSectionHeaderDefinition() {
    }

    /**
     * Directly validate simple fields.
     *
     * @see DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Object)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        //do nothing ?
    }

    public String toString() {
        return "MaintainableSubSectionHeaderDefinition '" + getName() + "'";
    }
}
