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
package org.kuali.kfs.kns.web.ui;

import java.io.Serializable;

/**
 * 
 */
@Deprecated
public class HeaderField implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public static final HeaderField EMPTY_FIELD = new HeaderField();
    
    private String id;  // to be used as a unique identifier if needed
    private String ddAttributeEntryName;
    private String displayValue;
    private String nonLookupValue;
    private boolean lookupAware;
    
    public HeaderField() {
    }
    
    public HeaderField(String id, String ddAttributeEntryName, String displayValue, String nonLookupValue) {
    	this(ddAttributeEntryName, displayValue, nonLookupValue);
    	this.id = id;
    }
    
    public HeaderField(String ddAttributeEntryName, String displayValue, String nonLookupValue) {
        this.ddAttributeEntryName = ddAttributeEntryName;
        this.displayValue = displayValue;
        this.nonLookupValue = nonLookupValue;
        this.lookupAware = true;
    }
    
    public HeaderField(String ddAttributeEntryName, String displayValue) {
        this.ddAttributeEntryName = ddAttributeEntryName;
        this.displayValue = displayValue;
        this.lookupAware = false;
    }
    
    public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDdAttributeEntryName() {
        return this.ddAttributeEntryName;
    }
    
    public void setDdAttributeEntryName(String ddAttributeEntryName) {
        this.ddAttributeEntryName = ddAttributeEntryName;
    }
    
    public String getDisplayValue() {
        return this.displayValue;
    }
    
    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }
    
    public String getNonLookupValue() {
        return this.nonLookupValue;
    }
    
    public void setNonLookupValue(String nonLookupValue) {
        this.nonLookupValue = nonLookupValue;
    }
    
    public boolean isLookupAware() {
        return this.lookupAware;
    }
    
    public void setLookupAware(boolean lookupAware) {
        this.lookupAware = lookupAware;
    }
}
