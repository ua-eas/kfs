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
package org.kuali.kfs.kns.datadictionary.exporter;

import org.kuali.kfs.kns.datadictionary.TransactionalDocumentEntry;
import org.kuali.kfs.krad.datadictionary.exporter.ExportMap;

/**
 * TransactionalDocumentEntryMapper
 */
@Deprecated
public class TransactionalDocumentEntryMapper extends DocumentEntryMapper {

    /**
     * @param entry
     * @return Map containing a String- and Map-based representation of the given entry
     */
    public ExportMap mapEntry(TransactionalDocumentEntry entry) {
        ExportMap entryMap = super.mapEntry(entry);

        // simple properties
        entryMap.set("transactionalDocument", "true");
        entryMap.set("documentClass", entry.getDocumentClass().getName());
        entryMap.set("allowsCopy", Boolean.toString(entry.getAllowsCopy()));

        return entryMap;
    }

}
