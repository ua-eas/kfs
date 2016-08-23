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
 * The select element will render an HTML select control.
 * The valuesFinderClass will have a getKeyValues() method
 * that returns a list of KeyValue objects.
 * <p>
 * An optional script attribute allows java script code to be
 * entered.  This code will be run when the user selects a new
 * value.
 */
@Deprecated
public class SelectControlDefinition extends MultivalueControlDefinitionBase {
    private static final long serialVersionUID = 7176759040882704084L;

    public SelectControlDefinition() {
    }

    /**
     * @see ControlDefinition#isSelect()
     */
    @Override
    public boolean isSelect() {
        return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "SelectControlDefinition";
    }

}
