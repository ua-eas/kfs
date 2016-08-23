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

import org.kuali.kfs.krad.datadictionary.AttributeDefinition;
import org.kuali.kfs.krad.datadictionary.DataDictionaryEntry;
import org.kuali.kfs.krad.datadictionary.DataDictionaryEntryBase;
import org.kuali.kfs.krad.datadictionary.exception.AttributeValidationException;
import org.kuali.kfs.krad.datadictionary.validation.capability.Constrainable;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.InvalidPropertyException;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 * This class allows a dictionary object to expose information about its fields / attributes, including the values of
 * those fields, with some guidance from the DataDictionaryEntry object.
 */
public class DictionaryObjectAttributeValueReader extends BaseAttributeValueReader {

    protected Object object;
    protected DataDictionaryEntry entry;

    protected BeanWrapper beanWrapper;

    private String attributePath;

    public DictionaryObjectAttributeValueReader(Object object, String entryName, DataDictionaryEntry entry) {
        this.object = object;
        this.entry = entry;
        this.entryName = entryName;

        if (object != null) {
            beanWrapper = new BeanWrapperImpl(object);
        }
    }

    public DictionaryObjectAttributeValueReader(Object object, String entryName, DataDictionaryEntry entry, String attributePath) {
        this(object, entryName, entry);
        this.attributePath = attributePath;
    }

    @Override
    public Constrainable getDefinition(String attrName) {
        return entry != null ? entry.getAttributeDefinition(attrName) : null;
    }

    @Override
    public List<Constrainable> getDefinitions() {
        if (entry instanceof DataDictionaryEntryBase) {
            DataDictionaryEntryBase entryBase = (DataDictionaryEntryBase) entry;
            List<Constrainable> definitions = new ArrayList<Constrainable>();
            List<AttributeDefinition> attributeDefinitions = entryBase.getAttributes();
            definitions.addAll(attributeDefinitions);
            return definitions;
        }

        return null;
    }

    @Override
    public Constrainable getEntry() {
        if (entry instanceof Constrainable)
            return (Constrainable) entry;

        return null;
    }

    @Override
    public String getLabel(String attrName) {
        AttributeDefinition attributeDefinition = entry != null ? entry.getAttributeDefinition(attrName) : null;
        return attributeDefinition != null ? attributeDefinition.getLabel() : attrName;
    }

    @Override
    public Object getObject() {
        return this.object;
    }

    @Override
    public String getPath() {
        String path = ValidationUtils.buildPath(attributePath, attributeName);
        return path != null ? path : "";
    }

    @Override
    public Class<?> getType(String attrName) {
        PropertyDescriptor propertyDescriptor = beanWrapper.getPropertyDescriptor(attrName);

        return propertyDescriptor.getPropertyType();
    }

    @Override
    public boolean isReadable() {
        return beanWrapper.isReadableProperty(attributeName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <X> X getValue() throws AttributeValidationException {
        Object value = getValue(attributeName);
        return (X) value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <X> X getValue(String attrName) throws AttributeValidationException {
        X attributeValue = null;

        Exception e = null;
        try {
            attributeValue = (X) beanWrapper.getPropertyValue(attrName);
        } catch (IllegalArgumentException iae) {
            e = iae;
        } catch (InvalidPropertyException ipe) {
            //just return null
        }

        if (e != null)
            throw new AttributeValidationException("Unable to lookup attribute value by name (" + attrName + ") using introspection", e);


        //			JLR : KS has code to handle dynamic attributes -- not sure whether this is really needed anymore if we're actually relying on types
        //            // Extract dynamic attributes
        //            if(DYNAMIC_ATTRIBUTE.equals(propName)) {
        //                dataMap.putAll((Map<String, String>)value);
        //            } else {
        //				dataMap.put(propName, value);
        //            }

        return attributeValue;
    }

    /**
     * @return false if parent attribute exists and is not null, otherwise returns true.
     */
    public boolean isParentAttributeNull() {
        boolean isParentNull = true;

        if (isNestedAttribute()) {
            String[] pathTokens = attributeName.split("\\.");

            isParentNull = false;
            String parentPath = "";
            for (int i = 0; (i < pathTokens.length - 1) && !isParentNull; i++) {
                parentPath += pathTokens[i];
                isParentNull = beanWrapper.getPropertyValue(parentPath) == null;
                parentPath += ".";
            }
        }

        return isParentNull;
    }

    public boolean isNestedAttribute() {
        return (attributePath != null || attributeName.contains("."));
    }

    @Override
    public AttributeValueReader clone() {
        DictionaryObjectAttributeValueReader readerClone =
            new DictionaryObjectAttributeValueReader(this.object, this.entryName, this.entry, this.attributePath);
        readerClone.setAttributeName(this.attributeName);


        return readerClone;
    }

}
