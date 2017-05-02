/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
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


@Deprecated
public class BusinessObjectEntry extends org.kuali.kfs.krad.datadictionary.BusinessObjectEntry {

    protected InquiryDefinition inquiryDefinition;
    protected LookupDefinition lookupDefinition;

    @Override
    public void completeValidation() {

        super.completeValidation();

        if (hasInquiryDefinition()) {
            inquiryDefinition.completeValidation(getDataObjectClass(), null);
        }

        if (hasLookupDefinition()) {
            lookupDefinition.completeValidation(getDataObjectClass(), null);
        }
    }

    /**
     * @return true if this instance has an inquiryDefinition
     */
    public boolean hasInquiryDefinition() {
        return (inquiryDefinition != null);
    }

    /**
     * @return current inquiryDefinition for this BusinessObjectEntry, or null if there is none
     */
    public InquiryDefinition getInquiryDefinition() {
        return inquiryDefinition;
    }

    /**
     * The inquiry element is used to specify the fields that will be displayed on the
     * inquiry screen for this business object and the order in which they will appear.
     * <p>
     * DD: See InquiryDefinition.java
     * <p>
     * JSTL: The inquiry element is a Map which is accessed using
     * a key of "inquiry".  This map contains the following keys:
     * title (String)
     * inquiryFields (Map)
     * <p>
     * See InquiryMapBuilder.java
     */
    public void setInquiryDefinition(InquiryDefinition inquiryDefinition) {
        this.inquiryDefinition = inquiryDefinition;
    }

    /**
     * @return true if this instance has a lookupDefinition
     */
    public boolean hasLookupDefinition() {
        return (lookupDefinition != null);
    }

    /**
     * @return current lookupDefinition for this BusinessObjectEntry, or null if there is none
     */
    public LookupDefinition getLookupDefinition() {
        return lookupDefinition;
    }

    /**
     * The lookup element is used to specify the rules for "looking up"
     * a business object.  These specifications define the following:
     * How to specify the search criteria used to locate a set of business objects
     * How to display the search results
     * <p>
     * DD: See LookupDefinition.java
     * <p>
     * JSTL: The lookup element is a Map which is accessed using
     * a key of "lookup".  This map contains the following keys:
     * lookupableID (String, optional)
     * title (String)
     * menubar (String, optional)
     * defaultSort (Map, optional)
     * lookupFields (Map)
     * resultFields (Map)
     * resultSetLimit (String, optional)
     * <p>
     * See LookupMapBuilder.java
     */
    public void setLookupDefinition(LookupDefinition lookupDefinition) {
        this.lookupDefinition = lookupDefinition;
    }
}
