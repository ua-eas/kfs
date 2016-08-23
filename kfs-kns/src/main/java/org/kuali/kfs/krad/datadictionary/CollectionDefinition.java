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
package org.kuali.kfs.krad.datadictionary;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.datadictionary.exception.AttributeValidationException;
import org.kuali.kfs.krad.datadictionary.validation.capability.CollectionSizeConstrainable;
import org.kuali.kfs.krad.datadictionary.validation.constraint.CollectionSizeConstraint;

/**
 * A single Collection attribute definition in the DataDictionary, which contains information relating to the display, validation,
 * and general maintenance of a specific Collection attribute of an entry.
 */
public class CollectionDefinition extends DataDictionaryDefinitionBase implements CollectionSizeConstrainable {
    private static final long serialVersionUID = -2644072136271281041L;

    protected String dataObjectClass;
    protected String name;
    protected String label;
    protected String shortLabel;
    protected String elementLabel;

    protected String summary;

    protected String description;

    protected Integer minOccurs;
    protected Integer maxOccurs;

    public CollectionDefinition() {
        //empty
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("invalid (blank) name");
        }
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        if (StringUtils.isBlank(label)) {
            throw new IllegalArgumentException("invalid (blank) label");
        }
        this.label = label;
    }

    /**
     * @return the shortLabel, or the label if no shortLabel has been set
     */
    public String getShortLabel() {
        return (shortLabel != null) ? shortLabel : label;
    }

    public void setShortLabel(String shortLabel) {
        if (StringUtils.isBlank(shortLabel)) {
            throw new IllegalArgumentException("invalid (blank) shortLabel");
        }
        this.shortLabel = shortLabel;
    }

    /**
     * Gets the elementLabel attribute.
     *
     * @return Returns the elementLabel.
     */
    public String getElementLabel() {
        return elementLabel;
    }

    /**
     * The elementLabel defines the name to be used for a single object
     * within the collection.  For example: "Address" may be the name
     * of one object within the "Addresses" collection.
     */
    public void setElementLabel(String elementLabel) {
        this.elementLabel = elementLabel;
    }

    public String getSummary() {
        return summary;
    }

    /**
     * The summary element is used to provide a short description of the
     * attribute or collection.  This is designed to be used for help purposes.
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    /**
     * The description element is used to provide a long description of the
     * attribute or collection.  This is designed to be used for help purposes.
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * @return the dataObjectClass
     */
    public String getDataObjectClass() {
        return this.dataObjectClass;
    }

    /**
     * @param objectClass the dataObjectClass to set
     */
    public void setDataObjectClass(String dataObjectClass) {
        this.dataObjectClass = dataObjectClass;
    }

    /**
     * Directly validate simple fields, call completeValidation on Definition fields.
     *
     * @see DataDictionaryEntry#completeValidation()
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        if (!DataDictionary.isCollectionPropertyOf(rootBusinessObjectClass, name)) {
            throw new AttributeValidationException("property '" + name + "' is not a collection property of class '" + rootBusinessObjectClass + "' (" + "" + ")");
        }
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CollectionDefinition for collection " + getName();
    }

    /**
     * @see CollectionSizeConstraint#getMaximumNumberOfElements()
     */
    @Override
    public Integer getMaximumNumberOfElements() {
        return this.maxOccurs;
    }

    /**
     * @see CollectionSizeConstraint#getMinimumNumberOfElements()
     */
    @Override
    public Integer getMinimumNumberOfElements() {
        return this.minOccurs;
    }

    /**
     * @return the minOccurs
     */
    public Integer getMinOccurs() {
        return this.minOccurs;
    }

    /**
     * @param minOccurs the minOccurs to set
     */
    public void setMinOccurs(Integer minOccurs) {
        this.minOccurs = minOccurs;
    }

    /**
     * @return the maxOccurs
     */
    public Integer getMaxOccurs() {
        return this.maxOccurs;
    }

    /**
     * @param maxOccurs the maxOccurs to set
     */
    public void setMaxOccurs(Integer maxOccurs) {
        this.maxOccurs = maxOccurs;
    }

}
