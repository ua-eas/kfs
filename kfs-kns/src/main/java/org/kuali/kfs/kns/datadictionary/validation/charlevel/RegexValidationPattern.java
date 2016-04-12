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
package org.kuali.kfs.kns.datadictionary.validation.charlevel;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.krad.datadictionary.validation.ValidationPattern;
import org.kuali.kfs.krad.datadictionary.exporter.ExportMap;
import org.kuali.kfs.krad.datadictionary.validation.CharacterLevelValidationPattern;

/**
 * This is a description of what this class does - ctdang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class RegexValidationPattern extends CharacterLevelValidationPattern {

    private static final long serialVersionUID = -5642894236634278352L;
    private static final Logger LOG=Logger.getLogger(RegexValidationPattern.class);
    /**
     * Regular expression, e.g. "[a-zA-Z0-9]"
     */
    private String pattern;

    private String validationErrorMessageKey;
    /**
     * This exports a representation of this instance by an ExportMap.
     * 
     * @see CharacterLevelValidationPattern#extendExportMap(ExportMap)
     */
    @Override
	public void extendExportMap(ExportMap exportMap) {
        if (LOG.isTraceEnabled()) {
            String message=String.format("ENTRY %s",
                    (exportMap==null)?"null":exportMap.toString());
            LOG.trace(message);
        }
        
        // Set element value
        exportMap.set("type", "regex");
        // Set attribute (of the above element) value
        exportMap.set("pattern", getPattern());

        if (LOG.isTraceEnabled()) {
            String message=String.format("EXIT %s",
                    (exportMap==null)?"null":exportMap.toString());
            LOG.trace(message);
        }
        
     }

    /**
     * This returns an instance of this class as string.
     * 
     * @see ValidationPattern#getPatternXml()
     */
    public String getPatternXml() {
        if (LOG.isTraceEnabled()) {
            String message=String.format("ENTRY");
            LOG.trace(message);
        }
        
        StringBuffer xml = new StringBuffer("<regex ");
        xml.append(pattern);
        xml.append("/>");

        if (LOG.isTraceEnabled()) {
            String message=String.format("EXIT %s", xml.toString());
            LOG.trace(message);
        }
        
        return xml.toString();
    }

    /**
     * This returns the specified regular expression defined in the data dictionary
     * entry for validating the value of an attribute.
     * 
     * @see ValidationPattern#getRegexString()
     */
    @Override
	protected String getRegexString() {
        if (LOG.isTraceEnabled()) {
            String message=String.format("ENTRY %s",
                    (pattern==null)?"null":pattern.toString());
            LOG.trace(message);
        }
        
        if (StringUtils.isEmpty(pattern)) {
            throw new IllegalStateException(this.getClass().getName()+".pattern is empty");
        }

        if (LOG.isTraceEnabled()) {
            String message=String.format("EXIT");
            LOG.trace(message);
        }
        
        return pattern;
    }

    /**
     * @return the pattern
     */
    public final String getPattern() {
        return this.pattern;
    }

    /**
     * @param pattern the pattern to set
     */
    public final void setPattern(String pattern) {
        this.pattern = pattern;
    }

	/**
	 * @return the validationErrorMessageKey
	 */
    @Override
	public String getValidationErrorMessageKey() {
		return this.validationErrorMessageKey;
	}

	/**
	 * @param validationErrorMessageKey a message key from the application's message resource bundle signifying the error message
	 * to display if some validation does not match this pattern
	 */
	public void setValidationErrorMessageKey(String validationErrorMessageKey) {
		this.validationErrorMessageKey = validationErrorMessageKey;
	}

	/**
	 * @see ValidationPattern#completeValidation()
	 */
	@Override
	public void completeValidation() {
		super.completeValidation();
		if (StringUtils.isBlank(validationErrorMessageKey)) {
			throw new ValidationPatternException("Regex Validation Patterns must have a validation error message key defined");
		}
	}
}
