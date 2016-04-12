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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A container that holds all of the {@link WorkflowAttributeDefinition} for a document for both document searches
 * and routing that depends on the values that exist on the document.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class WorkflowAttributes extends DataDictionaryDefinitionBase {
    private static final long serialVersionUID = 6435015497886060280L;
    
	private List<SearchingTypeDefinition> searchingTypeDefinitions;
    private Map<String, RoutingTypeDefinition> routingTypeDefinitions;
    
    public WorkflowAttributes() {
    	searchingTypeDefinitions = new ArrayList<SearchingTypeDefinition>();;
    	routingTypeDefinitions = new HashMap<String, RoutingTypeDefinition>();
    }
    
	/**
	 * @return the searchingTypeDefinitions
	 */
	public List<SearchingTypeDefinition> getSearchingTypeDefinitions() {
		return this.searchingTypeDefinitions;
	}

	/**
	 * @param searchingTypeDefinitions the searchingTypeDefinitions to set
	 */
	public void setSearchingTypeDefinitions(
			List<SearchingTypeDefinition> searchingTypeDefinitions) {
		this.searchingTypeDefinitions = searchingTypeDefinitions;
	}

	public Map<String, RoutingTypeDefinition> getRoutingTypeDefinitions() {
		return this.routingTypeDefinitions;
	}

	public void setRoutingTypeDefinitions(
			Map<String, RoutingTypeDefinition> routingTypeDefinitions) {
		this.routingTypeDefinitions = routingTypeDefinitions;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Class)
	 */
	public void completeValidation(Class rootBusinessObjectClass,
			Class otherBusinessObjectClass) {
		for (SearchingTypeDefinition definition : searchingTypeDefinitions) { 
			definition.completeValidation(rootBusinessObjectClass, otherBusinessObjectClass);
		}
		for (RoutingTypeDefinition definitions : routingTypeDefinitions.values()) {
			definitions.completeValidation(rootBusinessObjectClass, otherBusinessObjectClass);
		}
	}

}
