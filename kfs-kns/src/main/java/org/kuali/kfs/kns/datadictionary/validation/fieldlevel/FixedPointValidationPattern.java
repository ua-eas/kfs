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
 * Validation pattern for matching fixed point numbers, optionally matching negative numbers
 * 
 * 
 */
public class FixedPointValidationPattern extends FieldLevelValidationPattern {
    public static final String PATTERN_TYPE_PRECISION = "fixedPoint.precision";
    public static final String PATTERN_TYPE_SCALE = "fixedPoint.scale";

    protected boolean allowNegative;
    protected int precision;
    protected int scale;

    /**
     * @return Returns the precision.
     */
    public int getPrecision() {
        return precision;
    }

    /**
     * @param precision The precision to set.
     */
    public void setPrecision(int precision) {
        this.precision = precision;
    }

    /**
     * @return Returns the scale.
     */
    public int getScale() {
        return scale;
    }

    /**
     * @param scale The scale to set.
     */
    public void setScale(int scale) {
        this.scale = scale;
    }

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
     * Adds special handling to account for optional allowNegative and dynamic precision, scale
     * 
     * @see ValidationPattern#getRegexString()
     */
    @Override
	protected String getRegexString() {    	
    	final StringBuilder regex = new StringBuilder();

        if (allowNegative) {
            regex.append("-?");
        }
        // final patter will be: -?([0-9]{0,p-s}\.[0-9]{1,s}|[0-9]{1,p-s}) where p = precision, s=scale
        regex.append("(");
        regex.append("[0-9]{0," + (getPrecision() - getScale()) + "}");
        regex.append("\\.");
        regex.append("[0-9]{1," + getScale() + "}");
        regex.append("|[0-9]{1," + (getPrecision() - getScale()) + "}");
        regex.append(")");
        return regex.toString();
    }

    /**
     * @see FieldLevelValidationPattern#getPatternTypeName()
     */
    @Override
	protected String getPatternTypeName() {
        return "fixedPoint";
    }


    /**
     * @see ValidationPattern#buildExportMap(java.lang.String)
     */
    @Override
	public ExportMap buildExportMap(String exportKey) {
        ExportMap exportMap = super.buildExportMap(exportKey);

        if (allowNegative) {
            exportMap.set("allowNegative", "true");
        }
        exportMap.set("precision", Integer.toString(precision));
        exportMap.set("scale", Integer.toString(scale));

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

	/**
	 * This overridden method ...
	 * 
	 * @see ValidationPattern#getValidationErrorMessageParameters(java.lang.String)
	 */
	@Override
	public String[] getValidationErrorMessageParameters(String attributeLabel) {
		return new String[] {attributeLabel, String.valueOf(precision), String.valueOf(scale)};
	}
	
	@Override
	public void completeValidation() throws ValidationPatternException {
		super.completeValidation();
		
    	final boolean valid =
    		(getPrecision() >= 1) &&
    		(getScale() >= 0) &&
    		(getPrecision() >= getScale());
    	
    	if (!valid) {
    		throw new ValidationPatternException("The precision must be >= 1.  The scale must be >= 0.  The precision must be >= scale. Precision: " + getPrecision() + " Scale: " + getScale());
    	}
	}
}
