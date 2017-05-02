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
package org.kuali.kfs.krad.datadictionary.validation.constraint;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.datadictionary.validation.ValidationPattern;
import org.kuali.kfs.krad.uif.UifConstants;

/**
 * Pattern for matching any printable character
 */
public class AnyCharacterPatternConstraint extends ValidCharactersPatternConstraint {
    protected boolean allowWhitespace = false;
    protected boolean omitNewline = false;

    /**
     * @return allowWhitespace
     */
    public boolean getAllowWhitespace() {
        return allowWhitespace;
    }

    /**
     * @param allowWhitespace
     */
    public void setAllowWhitespace(boolean allowWhitespace) {
        this.allowWhitespace = allowWhitespace;
    }

    /**
     * @see ValidationPattern#getRegexString()
     */
    protected String getRegexString() {
        StringBuffer regexString = new StringBuffer("[");

        regexString.append("\\x21-\\x7E");
        if (allowWhitespace) {
            regexString.append("\\t\\v\\040");
            if (!omitNewline) {
                regexString.append("\\f\\r\\n");
            }
        }

        regexString.append("]");

        return regexString.toString();
    }

    /**
     * @see BaseConstraint#getLabelKey()
     */
    @Override
    public String getLabelKey() {
        String labelKey = super.getLabelKey();
        if (StringUtils.isNotEmpty(labelKey)) {
            return labelKey;
        }
        if (!allowWhitespace) {
            return UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "noWhitespace";
        } else {
            return UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "anyCharacterPattern";
        }
    }

    public boolean isOmitNewline() {
        return omitNewline;
    }

    /**
     * When set to true, omit new line characters from the set of valid characters.  This flag
     * will only have an effect if the allowWhitespace flag is true, otherwise all whitespace
     * including new lines characters are omitted.
     *
     * @param omitNewline
     */
    public void setOmitNewline(boolean omitNewline) {
        this.omitNewline = omitNewline;
    }
}
