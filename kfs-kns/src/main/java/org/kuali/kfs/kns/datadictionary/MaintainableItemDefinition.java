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
package org.kuali.kfs.kns.datadictionary;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.datadictionary.DataDictionaryDefinitionBase;

/**
 * Abstract superclass for all maintainable fields and collections.  Never used directly.
 */
@Deprecated
public abstract class MaintainableItemDefinition extends DataDictionaryDefinitionBase {
    private static final long serialVersionUID = 4564613758722159747L;

	private String name;

    public MaintainableItemDefinition() {
    }


    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name to the given value.
     *
     * @param name
     * @throws IllegalArgumentException if the given name is blank
     */
    public void setName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("invalid (blank) name");
        }
        this.name = name;
    }


    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "MaintainableItemDefinition for item " + getName();
    }
}
