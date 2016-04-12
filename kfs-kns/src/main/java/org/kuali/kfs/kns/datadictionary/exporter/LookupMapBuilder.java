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
package org.kuali.kfs.kns.datadictionary.exporter;

import java.util.Iterator;

import org.kuali.kfs.kns.datadictionary.FieldDefinition;
import org.kuali.kfs.kns.datadictionary.LookupDefinition;
import org.kuali.kfs.kns.datadictionary.BusinessObjectEntry;
import org.kuali.kfs.krad.datadictionary.SortDefinition;
import org.kuali.kfs.krad.datadictionary.exporter.ExportMap;

/**
 * LookupMapBuilder
 *
 *
 */
@Deprecated
public class LookupMapBuilder {

    /**
     * Default constructor
     */
    public LookupMapBuilder() {
    }


    /**
     * @param entry
     * @return ExportMap containing the standard entries for the given entry's LookupDefinition, or null if the given entry has no
     *         lookupDefinition
     */
    public ExportMap buildLookupMap(BusinessObjectEntry entry) {
        ExportMap lookupMap = null;

        if (entry.hasLookupDefinition()) {
            LookupDefinition lookupDefinition = entry.getLookupDefinition();
            lookupMap = new ExportMap("lookup");

            // simple properties
            if (lookupDefinition.getLookupableID() != null) {
                lookupMap.set("lookupableID", lookupDefinition.getLookupableID());
            }

            lookupMap.set("title", lookupDefinition.getTitle());

            if (lookupDefinition.hasMenubar()) {
                lookupMap.set("menubar", lookupDefinition.getMenubar());
            }

            if (lookupDefinition.hasResultSetLimit()) {
                lookupMap.set("resultSetLimit", lookupDefinition.getResultSetLimit().toString());
            }
            // complex properties
            lookupMap.setOptional(buildDefaultSortMap(lookupDefinition));
            lookupMap.set(buildLookupFieldsMap(lookupDefinition));
            lookupMap.set(buildResultFieldsMap(lookupDefinition));
        }

        return lookupMap;
    }

    private ExportMap buildDefaultSortMap(LookupDefinition lookupDefinition) {
        ExportMap defaultSortMap = null;

        if (lookupDefinition.hasDefaultSort()) {
            SortDefinition defaultSortDefinition = lookupDefinition.getDefaultSort();
            defaultSortMap = new ExportMap("defaultSort");

            defaultSortMap.set("sortAscending", Boolean.toString(defaultSortDefinition.getSortAscending()));
            defaultSortMap.set(buildSortAttributesMap(defaultSortDefinition));
        }

        return defaultSortMap;
    }

    private ExportMap buildSortAttributesMap(SortDefinition sortDefinition) {
        ExportMap sortAttributesMap = new ExportMap("sortAttributes");

        for (Iterator i = sortDefinition.getAttributeNames().iterator(); i.hasNext();) {
            String attributeName = (String) i.next();

            ExportMap attributeMap = new ExportMap(attributeName);
            attributeMap.set("attributeName", attributeName);

            sortAttributesMap.set(attributeMap);
        }

        return sortAttributesMap;
    }

    private ExportMap buildLookupFieldsMap(LookupDefinition lookupDefinition) {
        ExportMap lookupFieldsMap = new ExportMap("lookupFields");

        for (Iterator i = lookupDefinition.getLookupFields().iterator(); i.hasNext();) {
            FieldDefinition lookupField = (FieldDefinition) i.next();
            lookupFieldsMap.set(buildLookupFieldMap(lookupField));
        }

        return lookupFieldsMap;
    }

    private ExportMap buildLookupFieldMap(FieldDefinition lookupField) {
        ExportMap lookupFieldMap = new ExportMap(lookupField.getAttributeName());

        lookupFieldMap.set("attributeName", lookupField.getAttributeName());
        lookupFieldMap.set("required", Boolean.toString(lookupField.isRequired()));

        return lookupFieldMap;
    }

    private ExportMap buildResultFieldsMap(LookupDefinition lookupDefinition) {
        ExportMap resultFieldsMap = new ExportMap("resultFields");

        for (Iterator i = lookupDefinition.getResultFields().iterator(); i.hasNext();) {
            FieldDefinition resultField = (FieldDefinition) i.next();
            resultFieldsMap.set(MapperUtils.buildFieldMap(resultField));
        }

        return resultFieldsMap;
    }

}
