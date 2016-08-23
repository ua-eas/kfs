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
package org.kuali.kfs.kns.datadictionary;

import org.kuali.kfs.krad.datadictionary.DataDictionaryDefinition;
import org.kuali.kfs.krad.datadictionary.DataDictionaryDefinitionBase;
import org.kuali.kfs.krad.datadictionary.exception.DuplicateEntryException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * inquirySection defines the format and content of
 * one section of the inquiry.
 * DD:  See InquirySectionDefinition.java
 * <p>
 * numberOfColumns = the number of fields to be displayed in each row of the inquiry section.
 * For example, numberOfColumns = 2 indicates that the label and values for two fields will be
 * displayed in each row as follows:
 * field1label field1value  |   field2label field2value
 * field3label field3value  |   field4label field4value
 * etc.
 * <p>
 * Contains section-related information for inquiry sections
 * Note: the setters do copious amounts of validation, to facilitate generating errors during the parsing process.
 */
@Deprecated
public class InquirySectionDefinition extends DataDictionaryDefinitionBase {
    private static final long serialVersionUID = 1565114894539391362L;

    protected String title;
    protected List<FieldDefinition> inquiryFields = new ArrayList<FieldDefinition>();
    protected Map<String, FieldDefinition> inquiryFieldMap = new LinkedHashMap<String, FieldDefinition>();
    protected Map inquiryCollections;

    protected Integer numberOfColumns = 2;
    protected boolean defaultOpen = true;

    public InquirySectionDefinition() {
    }


    /**
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title to the given value.
     *
     * @param title
     * @throws IllegalArgumentException if the given title is blank
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return List of attributeNames of all FieldDefinitions associated with this InquirySection, in the order in
     * which they were added
     */
    public List<String> getInquiryFieldNames() {
        List<String> itemNames = new ArrayList<String>();
        itemNames.addAll(this.inquiryFieldMap.keySet());

        return itemNames;
    }

    /**
     * @return Collection of all FieldDefinitions associated with this InquirySection, in the order in which they
     * were added
     */
    public List<FieldDefinition> getInquiryFields() {
        return inquiryFields;
    }

    /**
     * Directly validate simple fields, call completeValidation on Definition fields.
     *
     * @see DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Object)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        for (FieldDefinition inquiryField : inquiryFields) {
            inquiryField.completeValidation(rootBusinessObjectClass, null);
        }
    }

    public String toString() {
        return "InquirySectionDefinition '" + getTitle() + "'";
    }

    public Map getInquiryCollections() {
        return inquiryCollections;
    }

    /**
     * The inquiryCollection defines a collection within the Business Object which contains
     * data that should be displayed with the BO when the inquiry is performed.
     * <p>
     * Each inquiryCollection defines a set of data fields, nested inquiryCollections
     * and summaryFields.  The summaryFields will be reported in the header of
     * this inquiryCollection, .
     * <p>
     * DD: See InquiryCollectionDefinition.java
     * JSTL: The inquiryCollection element is a Map with the following keys:
     * name (String)
     * dataObjectClass (String)
     * numberOfColumns (String)
     * inquiryFields (Map)
     * inquiryCollections (Map, optional)
     * summaryTitle (String)
     * summaryFields (Map, optional)
     */
    public void setInquiryCollections(Map inquiryCollections) {
        this.inquiryCollections = inquiryCollections;
    }

    public Integer getNumberOfColumns() {
        return numberOfColumns;
    }

    /**
     * numberOfColumns = the number of fields to be displayed in each row of the inquiry section.
     * For example, numberOfColumns = 2 indicates that the label and values for two fields will be
     * displayed in each row as follows:
     * field1label field1value  |   field2label field2value
     * field3label field3value  |   field4label field4value
     * etc.
     */
    public void setNumberOfColumns(Integer numberOfColumns) {
        this.numberOfColumns = numberOfColumns;
    }


    /**
     * JSTL: inquiryFields is a Map which is accessed using a
     * key of "inquiryFields".  This map contains the following types
     * of elements:
     * inquirySubSectionHeader
     * field
     * inquiryCollection
     * Each of these entries are keyed by "attributeName".
     * The associated value is the attributeName of the
     * mapped element.
     * <p>
     * The inquirySubSectionHeader allows a separator containing text to
     * separate groups of fields.  The name attribute is the displayed text.
     * <p>
     * JSTL: inquirySubSectionHeader appears in the inquiryFields map as:
     * key = "attributeName"
     * value = name of inquirySubSectionHeader
     * <p>
     * <p>
     * The field element defines the attributes of a single data field.
     * <p>
     * DD:  See FieldDefinition.java
     * JSTL: The field element is a Map which is accessed using
     * a key of the attributeName.  This map contains the following keys:
     * attributeName (String)
     * forceInquiry (boolean String)
     * noInquiry (boolean String)
     * maxLength (String)
     * <p>
     * forceInquiry = true means that the displayed field value will
     * always be made inquirable (this attribute is not used within the code).
     * <p>
     * noInquiry = true means that the displayed field will never be made inquirable.
     * <p>
     * maxLength = the maximum allowable length of the field in the lookup result fields.  In other contexts,
     * like inquiries, this field has no effect.
     */
    public void setInquiryFields(List<FieldDefinition> inquiryFields) {
        inquiryFieldMap.clear();
        for (FieldDefinition inquiryField : inquiryFields) {
            if (inquiryField == null) {
                throw new IllegalArgumentException("invalid (null) inquiryField");
            }

            String itemName = inquiryField.getAttributeName();
            if (inquiryFieldMap.containsKey(itemName)) {
                throw new DuplicateEntryException("duplicate itemName entry for item '" + itemName + "'");
            }

            inquiryFieldMap.put(itemName, inquiryField);
        }
        this.inquiryFields = inquiryFields;
    }


    /**
     * @return the defaultOpen
     */
    public boolean isDefaultOpen() {
        return this.defaultOpen;
    }


    /**
     * @param defaultOpen the defaultOpen to set
     */
    public void setDefaultOpen(boolean defaultOpen) {
        this.defaultOpen = defaultOpen;
    }

}
