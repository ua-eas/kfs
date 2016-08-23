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
import org.kuali.kfs.krad.uif.UifConstants;

/**
 * A ValidCharactersConstraint based on AlphaNumericValidationPattern.
 */
public class AlphaNumericPatternConstraint extends AllowCharacterConstraint {
    protected boolean lowerCase = false;
    protected boolean upperCase = false;

    /**
     * A label key is auto generated for this bean if none is set. This generated message can be
     * overridden through setLabelKey, but the generated message should cover most cases.
     *
     * @see BaseConstraint#getLabelKey()
     */
    @Override
    public String getLabelKey() {
        if (StringUtils.isEmpty(labelKey)) {
            StringBuilder key = new StringBuilder("");
            if (lowerCase) {
                return (UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "alphanumericPatternLowerCase");
            } else if (upperCase) {
                return (UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "alphanumericPatternUpperCase");
            } else {
                return (UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "alphanumericPattern");
            }
        }
        return labelKey;
    }

    /**
     * The labelKey should only be set if the auto generated message by this class needs to be
     * overridden
     *
     * @see BaseConstraint#setLabelKey(java.lang.String)
     */
    @Override
    public void setLabelKey(String labelKey) {
        super.setLabelKey(labelKey);
    }

    /**
     * @see ValidCharactersPatternConstraint#getRegexString()
     */
    @Override
    protected String getRegexString() {
        StringBuilder regexString = new StringBuilder("[A-Za-z0-9");
        /*
         * This check must be first because we are removing the base 'A-Z' if lowerCase == true
         */
        if (lowerCase) {
            regexString = new StringBuilder("[a-z0-9");
        } else if (upperCase) {
            regexString = new StringBuilder("[A-Z0-9");
        }

        regexString.append(this.getAllowedCharacterRegex());

        regexString.append("]");

        return regexString.toString();
    }

    /**
     * @return the lowerCase
     */
    public boolean isLowerCase() {
        return this.lowerCase;
    }

    /**
     * Only allow lowerCase characters. DO NOT use with upperCase option, no flags set for case
     * means both upper and lower case are allowed.
     *
     * @param lowerCase the lowerCase to set
     */
    public void setLowerCase(boolean lowerCase) {
        this.lowerCase = lowerCase;
    }

    public boolean isUpperCase() {
        return upperCase;
    }

    /**
     * Only allow upperCase characters.  DO NOT use with lowerCase option, no flags set for case
     * means both upper and lower case are allowed.
     *
     * @param lowerCase the lowerCase to set
     */
    public void setUpperCase(boolean upperCase) {
        this.upperCase = upperCase;
    }
}
