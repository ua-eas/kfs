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
import org.kuali.kfs.krad.util.KRADConstants;

import java.util.regex.Pattern;

/**
 * Abstraction of the regular expressions used to validate attribute values.
 *
 *
 */
@Deprecated
abstract public class CharacterLevelValidationPattern extends ValidationPattern {
    protected Pattern regexPattern;

    protected int maxLength = -1;
    protected int exactLength = -1;

    /**
     * Sets maxLength parameter for the associated regex.
     *
     * @param maxLength
     */
    public void setMaxLength(int maxLength) {
        if (this.exactLength != -1) {
            throw new IllegalStateException("illegal attempt to set maxLength after mutually-exclusive exactLength has been set");
        }

        this.maxLength = maxLength;
    }

    /**
     * @return current maxLength, or -1 if none has been set
     */
    public int getMaxLength() {
        return maxLength;
    }


    /**
     * Sets exactLength parameter for the associated regex.
     *
     * @param exactLength
     */
    public void setExactLength(int exactLength) {
        if (this.maxLength != -1) {
            throw new IllegalStateException("illegal attempt to set exactLength after mutually-exclusive maxLength has been set");
        }

        this.exactLength = exactLength;
    }

    /**
     * @return current exactLength, or -1 if none has been set
     */
    public int getExactLength() {
        return exactLength;
    }


    /**
     * @return regular expression Pattern generated using the individual ValidationPattern subclass
     */
    final public Pattern getRegexPattern() {
        if ( regexPattern == null ) {
            String regexString = getRegexString();

            StringBuffer completeRegex = new StringBuffer("^");
            completeRegex.append(getRegexString());

            if (maxLength != -1) {
                completeRegex.append("{0," + maxLength + "}");
            }
            else if (exactLength != -1) {
                completeRegex.append("{" + exactLength + "}");
            }
            else {
                completeRegex.append("*");
            }

            completeRegex.append("$");

            regexPattern = Pattern.compile(completeRegex.toString());
        }
        return regexPattern;
    }


    /**
     * @see ValidationPattern#buildExportMap(java.lang.String)
     */
    final public ExportMap buildExportMap(String exportKey) {
        ExportMap exportMap = new ExportMap(exportKey);

        if (getMaxLength() != -1) {
            exportMap.set("maxLength", Integer.toString(getMaxLength()));
        }
        else if (getExactLength() != -1) {
            exportMap.set("exactLength", Integer.toString(getExactLength()));
        }

        extendExportMap(exportMap);

        return exportMap;
    }

    /**
     * Extends the given (parent class) exportMap as needed to represent subclass instances
     *
     * @param exportMap
     */
    abstract public void extendExportMap(ExportMap exportMap);

	@Override
	public String[] getValidationErrorMessageParameters(String attributeLabel) {
		if (getMaxLength() != -1) {
			return new String[] {attributeLabel, String.valueOf(getMaxLength())};
		}
		if (getExactLength() != -1) {
			return new String[] {attributeLabel, String.valueOf(getExactLength())};
		}
		return new String[] {attributeLabel};
	}

	/**
	 * This overridden method ...
	 *
	 * @see ValidationPattern#getValidationErrorMessageKey()
	 */
	@Override
	public String getValidationErrorMessageKey() {
		StringBuilder buf = new StringBuilder();
		buf.append("error.format.").append(getClass().getName()).append(getValidationErrorMessageKeyOptions());
		if (getMaxLength() != -1) {
			buf.append(".maxLength");
		}
		if (getExactLength() != -1) {
			buf.append(".exactLength");
		}
		return buf.toString();
	}

	protected String getValidationErrorMessageKeyOptions() {
		return KRADConstants.EMPTY_STRING;
	}
}
