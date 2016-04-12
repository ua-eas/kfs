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

import java.util.List;


/**
 * This is a description of what this class does - mpham don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class RoutingTypeDefinition extends DataDictionaryDefinitionBase {
	private static final long serialVersionUID = -5455042765223753531L;
	
	private List<RoutingAttribute> routingAttributes; 
	private List<DocumentValuePathGroup> documentValuePathGroups; 

	/**
	 * @return the routingAttributes
	 */
	public List<RoutingAttribute> getRoutingAttributes() {
		return this.routingAttributes;
	}
	/**
	 * @return the documentValuePathGroups
	 */
	public List<DocumentValuePathGroup> getDocumentValuePathGroups() {
		return this.documentValuePathGroups;
	}
	/**
	 * @param routingAttributes the routingAttributes to set
	 */
	public void setRoutingAttributes(List<RoutingAttribute> routingAttributes) {
		this.routingAttributes = routingAttributes;
	}
	/**
	 * @param documentValuePathGroups the documentValuePathGroups to set
	 */
	public void setDocumentValuePathGroups(
			List<DocumentValuePathGroup> documentValuePathGroups) {
		this.documentValuePathGroups = documentValuePathGroups;
	}
	/**
	 * This overridden method ...
	 * 
	 * @see DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Class)
	 */
	public void completeValidation(Class rootBusinessObjectClass,
			Class otherBusinessObjectClass) {
		// TODO wliang - THIS METHOD NEEDS JAVADOCS
		
	}

}
