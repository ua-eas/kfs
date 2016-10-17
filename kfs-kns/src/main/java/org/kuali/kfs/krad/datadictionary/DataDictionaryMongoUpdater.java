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

import java.util.Collection;

public class DataDictionaryMongoUpdater implements Runnable {
    private DataDictionaryIndex dataDictionaryIndex;
    private DataDictionaryDao dataDictionaryDao;

    protected DataDictionaryMongoUpdater(DataDictionaryIndex ddIndex, DataDictionaryDao dataDictionaryDao) {
        this.dataDictionaryIndex = ddIndex;
        this.dataDictionaryDao = dataDictionaryDao;
    }

    private void saveDocumentEntries(Collection<DocumentEntry> documentEntries) {
        DocumentEntryMongoUpdater documentEntryMongoUpdater = new DocumentEntryMongoUpdater(documentEntries, dataDictionaryDao);
        documentEntryMongoUpdater.runUpdate();
    }

    private void saveBusinessObjectEntries(Collection<BusinessObjectEntry> businessObjectEntries) {
        BusinessObjectEntryMongoUpdater boMongoUpdater = new BusinessObjectEntryMongoUpdater(businessObjectEntries, dataDictionaryDao);
        boMongoUpdater.runUpdate();
    }

    @Override
    public void run() {
        saveBusinessObjectEntries(dataDictionaryIndex.getBusinessObjectEntries().values());
        saveDocumentEntries(dataDictionaryIndex.getDocumentEntries().values());
    }
}
