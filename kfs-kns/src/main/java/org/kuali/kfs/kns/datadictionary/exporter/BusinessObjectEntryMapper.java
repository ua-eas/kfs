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

import org.kuali.kfs.kns.datadictionary.BusinessObjectEntry;
import org.kuali.kfs.krad.datadictionary.exporter.ExportMap;

/**
 * BusinessObjectEntryMapper
 */
@Deprecated
public class BusinessObjectEntryMapper {

    /**
     * Default constructor
     */
    public BusinessObjectEntryMapper() {
    }


    /**
     * @param entry
     * @return Map containing a String- and Map-based representation of the given entry
     */
    public ExportMap mapEntry(BusinessObjectEntry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("invalid (null) entry");
        }

        ExportMap entryMap = new ExportMap(entry.getJstlKey());
        
        // simple properties
        entryMap.set("dataObjectClass", entry.getBusinessObjectClass().getName());
        if (entry.getExporterClass() != null) {
        	entryMap.set("exporterClass", entry.getExporterClass().getName());
        }
        final String objectLabel = entry.getObjectLabel();
        if (objectLabel != null) {
            entryMap.set("objectLabel", objectLabel);
        }
        final String objectDescription = entry.getObjectDescription();
        if (objectDescription != null) {
            entryMap.set("objectDescription", objectDescription);
        }

        // complex properties
        entryMap.setOptional(new InquiryMapBuilder().buildInquiryMap(entry));
        entryMap.setOptional(new LookupMapBuilder().buildLookupMap(entry));
        entryMap.set(new AttributesMapBuilder().buildAttributesMap(entry));
        entryMap.set(new CollectionsMapBuilder().buildCollectionsMap(entry));
        entryMap.set(new RelationshipsMapBuilder().buildRelationshipsMap(entry));

        return entryMap;
    }
}
