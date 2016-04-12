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

import org.kuali.kfs.krad.datadictionary.DataDictionaryEntryBase;
import org.kuali.kfs.krad.datadictionary.PrimitiveAttributeDefinition;
import org.kuali.kfs.krad.datadictionary.RelationshipDefinition;
import org.kuali.kfs.krad.datadictionary.exporter.ExportMap;

/**
 * RelationshipsMapBuilder
 * 
 * 
 */
@Deprecated
public class RelationshipsMapBuilder {

    /**
     * Default constructor
     */
    public RelationshipsMapBuilder() {
    }


    /**
     * @param entry
     * @return ExportMap containing the standard entries for the entry's RelationshipDefinitions
     */
    public ExportMap buildRelationshipsMap(DataDictionaryEntryBase entry) {
        ExportMap relationshipsMap = new ExportMap("relationships");

        for ( RelationshipDefinition relationship : entry.getRelationships() ) {
            relationshipsMap.set(buildRelationshipMap(relationship));
        }

        return relationshipsMap;
    }

    private ExportMap buildRelationshipMap(RelationshipDefinition relationship) {
        ExportMap relationshipMap = new ExportMap(relationship.getObjectAttributeName());

        ExportMap attributesMap = new ExportMap("primitiveAttributes");

        int count = 0;
        for (PrimitiveAttributeDefinition primitiveAttributeDefinition : relationship.getPrimitiveAttributes()) {
            ExportMap attributeMap = new ExportMap(Integer.toString(count++));
            attributeMap.set("sourceName", primitiveAttributeDefinition.getSourceName());
            attributeMap.set("targetName", primitiveAttributeDefinition.getTargetName());

            attributesMap.set(attributeMap);
        }

        relationshipMap.set(attributesMap);

        return relationshipMap;
    }

}
