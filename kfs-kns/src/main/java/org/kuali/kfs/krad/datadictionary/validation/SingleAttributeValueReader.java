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
package org.kuali.kfs.krad.datadictionary.validation;

import org.kuali.kfs.krad.datadictionary.AttributeDefinition;
import org.kuali.kfs.krad.datadictionary.exception.AttributeValidationException;
import org.kuali.kfs.krad.datadictionary.validation.capability.Constrainable;
import org.kuali.kfs.krad.datadictionary.validation.constraint.DataTypeConstraint;

import java.util.List;


/**
 * This class allows a single attribute value to be exposed to the validation service, along 
 * with some guidance about how that value should be interpreted, provided by the AttributeDefinition
 * that corresponds. It's a special AttributeValueReader since it explicitly doesn't expose any
 * other attribute values, so it should only be used when the underlying business object is not available
 * and we want to limit access to (for example) validation that requires only a single attribute value. 
 * This eliminates more complicated validation like 'this field is required when another field is filled in.'
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org) 
 */
public class SingleAttributeValueReader extends BaseAttributeValueReader {

	private Object value;
	private AttributeDefinition definition;
	
	public SingleAttributeValueReader(Object value, String entryName, String attributeName, AttributeDefinition definition) {
		this.value = value;
		this.entryName = entryName;
		this.attributeName = attributeName;
		this.definition = definition;
	}
	
	@Override
	public Constrainable getDefinition(String attributeName) {
		// Only return the definition if you have it, and if it's the definition for the passed attribute name
		return definition != null && definition.getName() != null && definition.getName().equals(attributeName) ? definition : null;
	}
	
	@Override
	public List<Constrainable> getDefinitions() {
		return null;
	}
	
	/**
	 * @see AttributeValueReader#getEntry()
	 */
	@Override
	public Constrainable getEntry() {
		return null;
	}

	@Override
	public String getLabel(String attributeName) {
		if (definition != null && definition.getName() != null && definition.getName().equals(attributeName))
			return definition.getLabel();
		
		return attributeName;
	}
	
	@Override
	public Object getObject() {
		return null;
	}
	
	@Override
	public String getPath() {
		return attributeName;
	}

	@Override
	public Class<?> getType(String selectedAttributeName) {
		Constrainable attributeDefinition = getDefinition(selectedAttributeName);
		
		if (attributeDefinition != null && attributeDefinition instanceof DataTypeConstraint) {
			DataTypeConstraint dataTypeConstraint = (DataTypeConstraint)attributeDefinition;
			if (dataTypeConstraint.getDataType() != null)
				return dataTypeConstraint.getDataType().getType();
		}
		
		// Assuming we can reliably guess
		return value != null ? value.getClass() : null;
	}

    @Override
    public boolean isReadable() {
        return true;
    }

    @Override
	public <X> X getValue() throws AttributeValidationException {
		return (X) value;
	}
	
	@Override
	public <X> X getValue(String attributeName) throws AttributeValidationException {
		Constrainable attributeDefinition = getDefinition(attributeName);
		
		if (attributeDefinition != null)
			return (X) value;
		
		return null;
	}
    
    @Override
    public AttributeValueReader clone(){
        SingleAttributeValueReader clone = new SingleAttributeValueReader(this.value, this.entryName, this.attributeName, this.definition);
        clone.setAttributeName(this.attributeName);
        return clone;
    }

}
