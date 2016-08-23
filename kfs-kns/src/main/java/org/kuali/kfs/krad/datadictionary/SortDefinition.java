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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The defaultSort element specifies the sequence in which the
 * lookup search results should be displayed.  It contains an
 * ascending/descending indicator and a list of attribute names.
 * <p>
 * DD: See SortDefinition.java
 * <p>
 * JSTL: defaultSort is a Map with the following keys:
 * sortAscending (boolean String)
 * sortAttributes (Map)
 * <p>
 * By the time JSTL export occurs, the optional attributeName from the defaultSort
 * tag will have been converted into the first contained sortAttribute
 */
public class SortDefinition extends DataDictionaryDefinitionBase {
    private static final long serialVersionUID = -1092811342186612461L;

    protected boolean sortAscending = true;
    protected List<String> attributeNames = new ArrayList<String>();

    public SortDefinition() {
    }


    /**
     * The sortAttribute element defines one part of the sort key.
     * The full sort key is comprised of the sortAttribute's in the
     * order in which they have been defined.
     * <p>
     * DD: See SortAttributesDefinition.java.
     * <p>
     * JSTL: sortAttribute is a Map which is accessed using a
     * key of the attributeName of the sortAttribute.
     * It contains a single entry with the following key:
     * "attributeName"
     * <p>
     * The associated value is the attributeName of the sortAttribute.
     * See LookupMapBuilder.java
     *
     * @throws IllegalArgumentException if the given attributeName is blank
     */
    public void setAttributeName(String attributeName) {
        if (StringUtils.isBlank(attributeName)) {
            throw new IllegalArgumentException("invalid (blank) attributeName");
        }
        if (attributeNames.size() != 0) {
            throw new IllegalStateException("unable to set sort attributeName when sortAttributes have already been added");
        }

        attributeNames.add(attributeName);
    }

    /**
     * @return the List of associated attribute names as Strings
     */
    public List<String> getAttributeNames() {
        return this.attributeNames;
    }


    /**
     * @return true if items should sort in ascending order
     */
    public boolean getSortAscending() {
        return sortAscending;
    }

    public void setSortAscending(boolean sortAscending) {
        this.sortAscending = sortAscending;
    }


    /**
     * Directly validate simple fields.
     *
     * @see DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Object)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        for (String attributeName : attributeNames) {
            if (!DataDictionary.isPropertyOf(rootBusinessObjectClass, attributeName)) {
                throw new AttributeValidationException("unable to find sort attribute '" + attributeName + "' in rootBusinessObjectClass '" + rootBusinessObjectClass.getName() + "' (" + "" + ")");
            }
        }
    }


    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer attrList = new StringBuffer("[");
        for (Iterator<String> i = attributeNames.iterator(); i.hasNext(); ) {
            attrList.append(i.next());
            if (i.hasNext()) {
                attrList.append(",");
            }
        }
        attrList.append("]");

        return "SortDefinition :  " + attrList.toString();
    }


    /**
     * The sortAttributes element allows a multiple-part sort key
     * to be defined
     * <p>
     * JSTL: sortAttributes is a Map which is accessed using a
     * key of "sortAttributes". This map contains an entry for
     * sort attribute.  The key is:
     * attributeName of a sort field.
     * The associated value is a sortAttribute ExportMap.
     */
    public void setAttributeNames(List<String> attributeNames) {
        this.attributeNames = attributeNames;
    }

}
