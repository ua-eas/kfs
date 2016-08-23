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
package org.kuali.kfs.kns.datadictionary.exporter;

import org.kuali.kfs.krad.datadictionary.CollectionDefinition;
import org.kuali.kfs.krad.datadictionary.DataDictionaryEntryBase;
import org.kuali.kfs.krad.datadictionary.exporter.ExportMap;

/**
 * CollectionsMapBuilder
 *
 *
 */
@Deprecated
public class CollectionsMapBuilder {

    /**
     * Default constructor
     */
    public CollectionsMapBuilder() {
    }


    /**
     * @param entry
     * @return ExportMap containing the standard entries for the entry's CollectionsDefinition
     */
    public ExportMap buildCollectionsMap(DataDictionaryEntryBase entry) {
        ExportMap collectionsMap = new ExportMap("collections");

        for ( CollectionDefinition collection : entry.getCollections() ) {
            collectionsMap.set(buildCollectionMap(collection));
        }

        return collectionsMap;
    }

    private ExportMap buildCollectionMap(CollectionDefinition collection) {
        ExportMap collectionMap = new ExportMap(collection.getName());

        collectionMap.set("name", collection.getName());
        collectionMap.set("label", collection.getLabel());
        collectionMap.set("shortLabel", collection.getShortLabel());
        if (collection.getSummary() != null) {
            collectionMap.set("summary", collection.getSummary());
        }
        if (collection.getDescription() != null) {
            collectionMap.set("description", collection.getDescription());
        }

        return collectionMap;
    }

}
