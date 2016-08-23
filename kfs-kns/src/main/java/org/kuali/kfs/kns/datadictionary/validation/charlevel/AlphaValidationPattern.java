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
package org.kuali.kfs.kns.datadictionary.validation.charlevel;

import org.kuali.kfs.krad.datadictionary.exporter.ExportMap;
import org.kuali.kfs.krad.datadictionary.validation.CharacterLevelValidationPattern;
import org.kuali.kfs.krad.datadictionary.validation.ValidationPattern;
import org.kuali.kfs.krad.util.KRADConstants;

/**
 * Pattern for matching alpha characters
 */
public class AlphaValidationPattern extends CharacterLevelValidationPattern {
    protected boolean allowWhitespace = false;


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
        StringBuffer regexString = new StringBuffer("[A-Za-z");

        if (allowWhitespace) {
            regexString.append("\\s");
        }
        regexString.append("]");

        return regexString.toString();
    }


    /**
     * @see CharacterLevelValidationPattern#extendExportMap(org.kuali.bo.datadictionary.exporter.ExportMap)
     */
    public void extendExportMap(ExportMap exportMap) {
        exportMap.set("type", "alpha");

        if (allowWhitespace) {
            exportMap.set("allowWhitespace", "true");
        }
    }

    @Override
    protected String getValidationErrorMessageKeyOptions() {
        if (getAllowWhitespace()) {
            return ".allowWhitespace";
        }
        return KRADConstants.EMPTY_STRING;
    }
}
