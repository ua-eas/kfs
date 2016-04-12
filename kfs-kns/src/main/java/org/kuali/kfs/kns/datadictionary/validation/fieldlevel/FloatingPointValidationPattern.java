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
package org.kuali.kfs.kns.datadictionary.validation.fieldlevel;

import org.kuali.kfs.krad.datadictionary.validation.ValidationPattern;
import org.kuali.kfs.krad.datadictionary.exporter.ExportMap;
import org.kuali.kfs.krad.datadictionary.validation.FieldLevelValidationPattern;

/**
 * Validation pattern for matching floating point numbers, optionally matching negative numbers
 * 
 * 
 */
public class FloatingPointValidationPattern extends FieldLevelValidationPattern {
    protected boolean allowNegative;

    /**
     * @return allowNegative
     */
    public boolean getAllowNegative() {
        return allowNegative;
    }

    /**
     * @param allowNegative
     */
    public void setAllowNegative(boolean allowNegative) {
        this.allowNegative = allowNegative;
    }

    /**
     * Adds special handling to account for optional allowNegative
     * 
     * @see ValidationPattern#getRegexString()
     */
    protected String getRegexString() {
        StringBuffer regex = new StringBuffer();

        if (allowNegative) {
            regex.append("-?");
        }
        regex.append(super.getRegexString());

        return regex.toString();
    }

    /**
     * @see FieldLevelValidationPattern#getPatternTypeName()
     */
    protected String getPatternTypeName() {
        return "floatingPoint";
    }


    /**
     * @see ValidationPattern#buildExportMap(java.lang.String)
     */
    public ExportMap buildExportMap(String exportKey) {
        ExportMap exportMap = super.buildExportMap(exportKey);

        if (allowNegative) {
            exportMap.set("allowNegative", "true");
        }

        return exportMap;
    }
    
	/**
	 * @see FieldLevelValidationPattern#getValidationErrorMessageKey()
	 */
	@Override
	public String getValidationErrorMessageKey() {
		StringBuilder buf = new StringBuilder();
		buf.append("error.format.").append(getClass().getName());
		if (allowNegative) {
			buf.append(".allowNegative");
		}
		return buf.toString();
	}
}
