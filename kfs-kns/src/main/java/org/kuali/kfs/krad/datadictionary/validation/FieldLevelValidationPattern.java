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
package org.kuali.kfs.krad.datadictionary.validation;

import org.kuali.kfs.krad.datadictionary.exporter.ExportMap;
import org.kuali.kfs.krad.service.KRADServiceLocator;

import java.util.regex.Pattern;

/**
 * Abstraction of the regular expressions used to validate attribute values.
 */
@Deprecated
abstract public class FieldLevelValidationPattern extends ValidationPattern {
    protected Pattern regexPattern;

    /**
     * Uses the key returned by getConfigurationRegexKey to fetch the validationPattern's regex string from the
     * ConfigurationService
     *
     * @see ValidationPattern#getRegexString()
     */
    protected String getRegexString() {
        return (String) KRADServiceLocator.getKualiConfigurationService().getPropertyValueAsString(
            "validationPatternRegex." + getPatternTypeName());
    }

    /**
     * @return the key used to retrieve the validationPattern's type name, which is used as the suffix of the regex property key, as
     * the type entry in the exportMap, etc.
     */
    abstract protected String getPatternTypeName();


    /**
     * @return regular expression Pattern generated using the individual ValidationPattern subclass
     */
    public final Pattern getRegexPattern() {
        if (regexPattern == null) {
            StringBuffer completeRegex = new StringBuffer("^");
            completeRegex.append(getRegexString());
            completeRegex.append("$");
            regexPattern = Pattern.compile(completeRegex.toString());
        }
        return regexPattern;
    }


    /**
     * @see ValidationPattern#buildExportMap(java.lang.String)
     */
    public ExportMap buildExportMap(String exportKey) {
        ExportMap exportMap = new ExportMap(exportKey);

        exportMap.set("type", getPatternTypeName());

        return exportMap;
    }

    /**
     * This overridden method ...
     *
     * @see ValidationPattern#getValidationErrorMessageKey()
     */
    @Override
    public String getValidationErrorMessageKey() {
        StringBuilder buf = new StringBuilder();
        buf.append("error.format.").append(getClass().getName());
        return buf.toString();
    }
}
