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
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.kfs.krad.datadictionary.exception.AttributeValidationException;

/**
 *                     The reference element specifies the name of a reference
                    object that is required to exist in order for the primary
                    business object to be created or modified on a BO.

                    DD: See ReferenceDefinition.java

                    JSTL: references are Maps with the following keys:
                    * attributeName (String)
                    * activeIndicatorAttributeName (String)
                    * activeIndicatorReversed (boolean String)
                    * attributeToHighlightOnFail (String)
                    * displayFieldName (String)

 *
 */
public class ReferenceDefinition extends DataDictionaryDefinitionBase {
    private static final long serialVersionUID = 1737968024207302931L;

	protected String attributeName;
    protected String attributeToHighlightOnFail;
    protected String displayFieldName;
    protected String collection;
    protected Class<? extends BusinessObject> collectionBusinessObjectClass;
    protected Class<? extends BusinessObject> businessObjectClass;

    public ReferenceDefinition() {}

    /**
     * @return attributeName
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * attributeName is the name of a reference object that
                        must exist and not be null.  In the case of a collection,
                        then this is the name of a reference object within the
                        collection element.
     *
     * @throws IllegalArgumentException if the given attributeName is blank
     */
    public void setAttributeName(String attributeName) {
        if (StringUtils.isBlank(attributeName)) {
            throw new IllegalArgumentException("invalid (blank) attributeName");
        }
        this.attributeName = attributeName;
    }

    /**
     * Gets the attributeToHighlightOnFail attribute.
     *
     * @return Returns the attributeToHighlightOnFail.
     */
    public String getAttributeToHighlightOnFail() {
        return attributeToHighlightOnFail;
    }

    /**
            attributeToHighlightOnFail is the name of the busines
                        object attribute which will be highlighted when
                        the default existence check fails.
     */
    public void setAttributeToHighlightOnFail(String attributeToHighlightOnFail) {
        if (StringUtils.isBlank(attributeToHighlightOnFail)) {
            throw new IllegalArgumentException("invalid (blank) attributeToHighlightOnFail");
        }
        this.attributeToHighlightOnFail = attributeToHighlightOnFail;
    }

    /**
     * Gets the displayFieldName attribute.
     *
     * @return Returns the displayFieldName.
     */
    public String getDisplayFieldName() {
        return displayFieldName;
    }

    /**
        displayFieldName is the name of the field to pull the label as it will
                        appear in an error message.  e.g. "chartOfAccountsCode".
     */
    public void setDisplayFieldName(String displayFieldName) {
        this.displayFieldName = displayFieldName;
    }

    /**
     * This method returns true if the displayFieldName is set, otherwise it returns false. Whether the displayFieldName is set is
     * defined by whether it has any non-whitespace content in it.
     *
     * @return
     */
    public boolean isDisplayFieldNameSet() {
        return StringUtils.isNotBlank(displayFieldName);
    }

    public String getCollection() {
        return collection;
    }

    /**
        collection is the name of a collection that must exist
     */
    public void setCollection(String collection) {
        this.collection = collection;
    }

    public boolean isCollectionReference() {
        return StringUtils.isNotBlank(getCollection());
    }

    public Class<? extends BusinessObject> getCollectionBusinessObjectClass() {
        if( collectionBusinessObjectClass == null && isCollectionReference() ){
            collectionBusinessObjectClass=DataDictionary.getCollectionElementClass(businessObjectClass, collection);
        }

        return collectionBusinessObjectClass;
    }

    /** Class that the specified collection represents.  Does not need to be set.  The DD
     * Will set this attribute through introspection.
     */
    public void setCollectionBusinessObjectClass(Class<? extends BusinessObject> collectionBusinessObjectClass) {
        this.collectionBusinessObjectClass = collectionBusinessObjectClass;
    }

    /**
     * Directly validate simple fields.
     *
     * @see DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Object)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {


        // make sure the attributeName is actually a property of the BO
        String tmpAttributeName = isCollectionReference() ? collection : attributeName;
        if (!DataDictionary.isPropertyOf(rootBusinessObjectClass, tmpAttributeName)) {
            throw new AttributeValidationException("unable to find attribute '" + tmpAttributeName + "' in rootBusinessObjectClass '" + rootBusinessObjectClass.getName() + "' (" + "" + ")");
        }
        // make sure the attributeToHighlightOnFail is actually a property of the BO
        if (isCollectionReference()) {
            getCollectionBusinessObjectClass(); // forces loading of the class
            if ( collectionBusinessObjectClass == null ) {
                throw new AttributeValidationException("Unable to determine collectionBusinessObjectClass for collection '" + businessObjectClass.getName() + "." + collection + "'");
            }

            if (!DataDictionary.isPropertyOf(collectionBusinessObjectClass, attributeToHighlightOnFail)) {
                throw new AttributeValidationException("unable to find attribute '" + attributeToHighlightOnFail + "' in collectionBusinessObjectClass '" + collectionBusinessObjectClass.getName() + "' (" + "" + ")");
            }
        }
        else {
            if (!DataDictionary.isPropertyOf(rootBusinessObjectClass, attributeToHighlightOnFail)) {
                throw new AttributeValidationException("unable to find attribute '" + attributeToHighlightOnFail + "' in rootBusinessObjectClass '" + rootBusinessObjectClass.getName() + "' (" + "" + ")");
            }
        }

    }


    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "ReferenceDefinition for attribute " + getAttributeName();
    }

    public Class<? extends BusinessObject> getBusinessObjectClass() {
        return businessObjectClass;
    }

    public void setBusinessObjectClass(Class<? extends BusinessObject> businessObjectClass) {
        this.businessObjectClass = businessObjectClass;
    }
}
