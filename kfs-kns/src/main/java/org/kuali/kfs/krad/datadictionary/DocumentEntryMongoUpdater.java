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

import org.kuali.kfs.krad.dao.DataDictionaryDao;
import org.kuali.kfs.krad.document.TransactionalDocument;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DocumentEntryMongoUpdater {
    private Collection<DocumentEntry> documentEntries;
    private DataDictionaryDao dataDictionaryDao;
    private ApiNamesGenerator apiNamesGenerator;

    public DocumentEntryMongoUpdater(Collection<DocumentEntry> documentEntries, DataDictionaryDao dataDictionaryDao) {
        this.documentEntries = documentEntries;
        this.dataDictionaryDao = dataDictionaryDao;
        this.apiNamesGenerator = new ApiNamesGenerator();
    }

    public void runUpdate() {
        for (DocumentEntry documentEntry : documentEntries) {
            updateDocumentEntry(documentEntry);
        }
    }

    private void updateDocumentEntry(DocumentEntry documentEntry) {
        Map<String, Object> mongoDocumentEntry = dataDictionaryDao.retrieveDocumentEntryByType(documentEntry.getDocumentTypeName());
        if (mongoDocumentEntry == null) {
            mongoDocumentEntry = createMongoDocumentEntry(documentEntry);
        } else {
            updateMongoDocumentEntry(documentEntry, mongoDocumentEntry);
        }
        dataDictionaryDao.saveDocumentEntry(mongoDocumentEntry);
    }

    private Map<String, Object> createMongoDocumentEntry(DocumentEntry documentEntry) {
        Map<String, Object> mongoDocumentEntry = new ConcurrentHashMap<>();
        mongoDocumentEntry.put("institutionId","");
        mongoDocumentEntry.put("module",apiNamesGenerator.convertDocumentEntryToModuleName(documentEntry));
        mongoDocumentEntry.put("documentReferenceName",apiNamesGenerator.convertDocumentEntryToUrlDocumentName(documentEntry));
        mongoDocumentEntry.put("documentClassName",documentEntry.getDocumentClass().getName());
        mongoDocumentEntry.put("workflowTypeName",documentEntry.getDocumentTypeName());
        updateMongoDocumentEntry(documentEntry, mongoDocumentEntry);
        return mongoDocumentEntry;
    }

    private String determineFamily(DocumentEntry documentEntry) {
        return (TransactionalDocument.class.isAssignableFrom(documentEntry.getDocumentClass())) ? "transactional" : "maintenance";
    }

    private void updateMongoDocumentEntry(DocumentEntry documentEntry, Map<String, Object> mongoDocumentEntry) {
        mongoDocumentEntry.put("documentFamily", determineFamily(documentEntry));
        if (documentEntry instanceof MaintenanceDocumentEntry) {
            final MaintenanceDocumentEntry maintenanceDocumentEntry = (MaintenanceDocumentEntry)documentEntry;
            mongoDocumentEntry.put("maintenanceBusinessObjectClass", maintenanceDocumentEntry.getDataObjectClass().getName());
        }
    }
}
