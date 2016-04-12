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

import org.kuali.kfs.krad.datadictionary.exception.AttributeValidationException;

/**
 *  A complex attribute definition in the DataDictictionary. This can be be used to define 
 *  an attribute for a DataObjectEntry's attribute list which is represented by another
 *  object entry definition. It will 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ComplexAttributeDefinition extends AttributeDefinitionBase{
	
	protected DataDictionaryEntry dataObjectEntry;
	

	/**
	 * @return the dataObjectEntry
	 */
	public DataDictionaryEntry getDataObjectEntry() {
		return this.dataObjectEntry;
	}

	/**
	 * @param dataObjectEntry the dataObjectEntry to set
	 */
	public void setDataObjectEntry(DataDictionaryEntry dataObjectEntry) {
		this.dataObjectEntry = dataObjectEntry;
	}


	/**
	 * @see DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Class)
	 */
	@Override
	public void completeValidation(Class<?> rootObjectClass, Class<?> otherObjectClass) {
		if (getDataObjectEntry() == null){
			throw new AttributeValidationException("complex property '" + getName() + "' in class '"
					+ rootObjectClass.getName() + " does not have a dataObjectClass defined");
			
		}
	}

}
