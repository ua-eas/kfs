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
package org.kuali.kfs.krad.datadictionary.validation.constraint;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.datadictionary.validation.ValidationPattern;
import org.kuali.kfs.krad.uif.UifConstants;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Pattern for matching numeric characters, difference between NumericPatternConstraint and IntegerPatternConstraint
 * is that a numeric pattern constraint is for matching numeric characters and can be mixed with other characters
 * by setting allow flags on, while integer is for only positive/negative numbers
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class NumericPatternConstraint extends AllowCharacterConstraint {
    
    /**
     * @see ValidationPattern#getRegexString()
     */
    protected String getRegexString() {
        StringBuilder regexString = new StringBuilder("[0-9");
        regexString.append(this.getAllowedCharacterRegex());
        regexString.append("]");

        return regexString.toString();
    }

    /**
     * This overridden method ...
     * 
     * @see BaseConstraint#getLabelKey()
     */
    @Override
    public String getLabelKey() {
        String labelKey = super.getLabelKey();
        if (StringUtils.isNotEmpty(labelKey)) {
            return labelKey;
        }
        return UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "numericPattern";
    }

}
