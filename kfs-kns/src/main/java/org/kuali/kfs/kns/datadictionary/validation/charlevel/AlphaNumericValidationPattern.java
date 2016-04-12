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

import org.kuali.kfs.krad.datadictionary.validation.ValidationPattern;
import org.kuali.kfs.krad.datadictionary.exporter.ExportMap;
import org.kuali.kfs.krad.datadictionary.validation.CharacterLevelValidationPattern;

/**
 * Pattern for matching alphanumeric characters
 * 
 * Also, allows conditionally whitespace, underscore, period, parens, dollar signs, and forward slash.
 */
public class AlphaNumericValidationPattern extends CharacterLevelValidationPattern {
    protected boolean allowWhitespace = false;
    protected boolean allowUnderscore = false;
    protected boolean allowPeriod = false;

    protected boolean allowParenthesis = false;
    protected boolean allowDollar = false;
    protected boolean allowForwardSlash = false;
    protected boolean lowerCase = false;
    protected boolean allowDash = false;
    
    /**
     * @return allowPeriod
     */
    public boolean getAllowPeriod() {
        return allowPeriod;
    }

    /**
     * @param allowPeriod
     */
    public void setAllowPeriod(boolean allowPeriod) {
        this.allowPeriod = allowPeriod;
    }    
    
    /**
	 * @return the allowPeriod
	 */
	public boolean isAllowPeriod() {
		return allowPeriod;
	}
    
    /**
	 * @return the allowParenthesis
	 */
	public boolean isAllowParenthesis() {
		return allowParenthesis;
	}

	/**
	 * @param allowParenthesis the allowParenthesis to set
	 */
	public void setAllowParenthesis(boolean allowParenthesis) {
		this.allowParenthesis = allowParenthesis;
	}
	
	/**
	 * @return the allowDollar
	 */
	public boolean isAllowDollar() {
		return allowDollar;
	}

	/**
	 * @param allowDollar the allowDollar to set
	 */
	public void setAllowDollar(boolean allowDollar) {
		this.allowDollar = allowDollar;
	}

	/**
	 * @return the allowforwardSlash
	 */
	public boolean isAllowForwardSlash() {
		return allowForwardSlash;
	}

	/**
	 * @param allowForwardSlash the allowforwardSlash to set
	 */
	public void setAllowForwardSlash(boolean allowForwardSlash) {
		this.allowForwardSlash = allowForwardSlash;
	}
    
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
     * @return allowUnderscore
     */
    public boolean getAllowUnderscore() {
        return allowUnderscore;
    }

    /**
     * @param allowUnderscore
     */
    public void setAllowUnderscore(boolean allowUnderscore) {
        this.allowUnderscore = allowUnderscore;
    }
   
    /**
	 * @return the lowerCase
	 */
	public boolean isLowerCase() {
		return this.lowerCase;
	}

	/**
	 * @param lowerCase the lowerCase to set
	 */
	public void setLowerCase(boolean lowerCase) {
		this.lowerCase = lowerCase;
	}

    /**
     * @return allowDash
     */
    public boolean getAllowDash() {
        return allowDash;
    }

    /**
     * @param allowDash
     */
    public void setAllowDash(boolean allowDash) {
        this.allowDash = allowDash;
    }

    /**
     * @see ValidationPattern#getRegexString()
     */
    protected String getRegexString() {
    	StringBuilder regexString = new StringBuilder("[A-Za-z0-9");
    	
    	/*
    	 * This check must be first because we are removing the base 'A-Z' if lowerCase == true
    	 */
    	if(lowerCase){
    		regexString = new StringBuilder("[a-z0-9");
    	}

        if (allowWhitespace) {
            regexString.append("\\s");
        }
        if (allowUnderscore) {
            regexString.append("_");
        }
        if (allowPeriod) {
            regexString.append(".");
        }
        if(allowParenthesis) {
        	regexString.append("(");
        	regexString.append(")");
        }
        if(allowDollar) {
        	regexString.append("$");
        }
        if(allowForwardSlash) {
        	regexString.append("/");
        }
        if (allowDash) {
            regexString.append("-");
        }
        regexString.append("]");

        return regexString.toString();
    }


    /**
     * @see CharacterLevelValidationPattern#extendExportMap(org.kuali.bo.datadictionary.exporter.ExportMap)
     */
    public void extendExportMap(ExportMap exportMap) {
        exportMap.set("type", "alphaNumeric");

        if (lowerCase) {
            exportMap.set("allowUpperCase", "true");
        }
        if (allowWhitespace) {
            exportMap.set("allowWhitespace", "true");
        }
        if (allowUnderscore) {
            exportMap.set("allowUnderscore", "true");
        }
        if (allowPeriod) {
        	exportMap.set("allowPeriod", "true");
        }
        if(allowParenthesis) {
            exportMap.set("allowParenthesis", "true");

        }
        if(allowDollar) {
            exportMap.set("allowDollar", "true");

        }
        if(allowForwardSlash) {
            exportMap.set("allowForwardSlash", "true");

        }
        if (allowDash) {
            exportMap.set("allowDash", "true");
        }
    }

	@Override
	protected String getValidationErrorMessageKeyOptions() {
		final StringBuilder opts = new StringBuilder();

		if (lowerCase) {
			opts.append(".lowerCase");
		}
		if (allowWhitespace) {
			opts.append(".allowWhitespace");
		}
		if (allowUnderscore) {
			opts.append(".allowUnderscore");
		}
		if (allowPeriod) {
			opts.append(".allowPeriod");
		}
		if(allowParenthesis) {
			opts.append(".allowParenthesis");
		}
		if(allowDollar) {
			opts.append(".allowDollar");
		}
		if(allowForwardSlash) {
			opts.append(".allowForwardSlash");
		}
        if (allowDash) {
            opts.append(".allowDash");
		}

		return opts.toString();
	}
}
