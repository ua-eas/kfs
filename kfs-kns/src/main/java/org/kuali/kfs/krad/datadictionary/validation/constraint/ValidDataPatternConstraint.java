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
package org.kuali.kfs.krad.datadictionary.validation.constraint;

import org.apache.commons.lang.StringUtils;

/**
 * Class used to
 *
 *
 */
public abstract class ValidDataPatternConstraint extends ValidCharactersConstraint {

    /**
     * Warning: This value should NOT be set on this class as the value is built dynamically from the
     * flags set on the constraint - if this value IS set it will override any automatic generation and only
     * use that which was set through this method for server side validation
     *
     * @see ValidCharactersConstraint#setValue(java.lang.String)
     */
    @Override
    public void setValue(String value) {
        super.setValue(value);
    }

    /**
     * @see ValidCharactersConstraint#getValue()
     */
    @Override
    public String getValue() {
        if(StringUtils.isEmpty(value)){
            return "^" + getRegexString() + "$";
        }
        return value;

    }

    /**
     * This method returns a string representing a regex with characters to match, this string should not
     * include the start(^) and end($) symbols
     */
    abstract protected String getRegexString();

}
