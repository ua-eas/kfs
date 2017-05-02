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
package org.kuali.kfs.kns.datadictionary.validation.charlevel;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.datadictionary.exporter.ExportMap;
import org.kuali.kfs.krad.datadictionary.validation.CharacterLevelValidationPattern;
import org.kuali.kfs.krad.datadictionary.validation.ValidationPattern;

import java.util.regex.Pattern;

/**
 * Pattern for matching any character in the given list (String)
 */
public class CharsetValidationPattern extends CharacterLevelValidationPattern {
    protected String validChars;

    /**
     * @return String containing all valid chars for this charset
     */
    public String getValidChars() {
        return validChars;
    }

    /**
     * @param validChars for this charset
     */
    public void setValidChars(String validChars) {
        if (StringUtils.isEmpty(validChars)) {
            throw new IllegalArgumentException("invalid (empty) validChars");
        }

        this.validChars = validChars;
    }


    /**
     * Escapes every special character I could think of, to limit potential misuse of this pattern.
     *
     * @see ValidationPattern#getRegexString()
     */
    protected String getRegexString() {
        if (StringUtils.isEmpty(validChars)) {
            throw new IllegalStateException("validChars is empty");
        }

        // filter out and escape chars which would confuse the pattern-matcher
        Pattern filteringChars = Pattern.compile("([\\-\\[\\]\\{\\}\\$\\.\\^\\(\\)\\*\\&\\|])");
        String filteredChars = filteringChars.matcher(validChars).replaceAll("\\\\$1");

        StringBuffer regexString = new StringBuffer("[");
        regexString.append(filteredChars);
        if (filteredChars.endsWith("\\")) {
            regexString.append("\\");
        }
        regexString.append("]");

        return regexString.toString();
    }


    /**
     * @see CharacterLevelValidationPattern#extendExportMap(org.kuali.bo.datadictionary.exporter.ExportMap)
     */
    public void extendExportMap(ExportMap exportMap) {
        exportMap.set("type", "charset");

        exportMap.set("validChars", getValidChars());
    }

    /**
     * This overridden method ...
     *
     * @see CharacterLevelValidationPattern#getValidationErrorMessageParameters(java.lang.String, java.lang.String)
     */
    @Override
    public String[] getValidationErrorMessageParameters(String attributeLabel) {
        // build character list
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < validChars.length(); i++) {
            buf.append(validChars.charAt(i));
            if (i != validChars.length() - 1) {
                buf.append(", ");
            }
        }
        String characterList = buf.toString();

        if (getMaxLength() != -1) {
            return new String[]{attributeLabel, String.valueOf(getMaxLength()), characterList};
        }
        if (getExactLength() != -1) {
            return new String[]{attributeLabel, String.valueOf(getExactLength()), characterList};
        }
        return new String[]{attributeLabel, "0", characterList};
    }
}
