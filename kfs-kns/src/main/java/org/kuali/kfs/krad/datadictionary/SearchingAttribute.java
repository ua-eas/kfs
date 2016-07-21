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
package org.kuali.kfs.krad.datadictionary;


/**
 * This is a description of what this class does - mpham don't forget to fill this in. 
 * 
 * 
 *
 */
public class SearchingAttribute extends WorkflowAttributeMetadata {
	private static final long serialVersionUID = -612461988789474893L;
	
	private String businessObjectClassName; 
	private String attributeName;
	private boolean showAttributeInSearchCriteria = true;
	private boolean showAttributeInResultSet = false;
	
	/**
	 * @return the businessObjectClassName
	 */
	public String getBusinessObjectClassName() {
		return this.businessObjectClassName;
	}
	/**
	 * @return the attributeName
	 */
	public String getAttributeName() {
		return this.attributeName;
	}
	/**
	 * @param businessObjectClassName the businessObjectClassName to set
	 */
	public void setBusinessObjectClassName(String businessObjectClassName) {
		this.businessObjectClassName = businessObjectClassName;
	}
	/**
	 * @param attributeName the attributeName to set
	 */
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	/**
	 * Returns whether this attribute should appear in the search criteria
	 * @return the showAttributeInSearchCriteria
	 */
	public boolean isShowAttributeInSearchCriteria() {
		return this.showAttributeInSearchCriteria;
	}
	/**
	 * Sets whether this attribute should appear in the search criteria
	 * @param showAttributeInSearchCriteria the showAttributeInSearchCriteria to set
	 */
	public void setShowAttributeInSearchCriteria(
			boolean showAttributeInSearchCriteria) {
		this.showAttributeInSearchCriteria = showAttributeInSearchCriteria;
	}
	/**
	 * Returns whether this attribute should appear in the result set
	 * @return the showAttributeInResultSet
	 */
	public boolean isShowAttributeInResultSet() {
		return this.showAttributeInResultSet;
	}
	/**
	 * Sets whether this attribute should appear in the result set
	 * @param showAttributeInResultSet the showAttributeInResultSet to set
	 */
	public void setShowAttributeInResultSet(boolean showAttributeInResultSet) {
		this.showAttributeInResultSet = showAttributeInResultSet;
	}
}
