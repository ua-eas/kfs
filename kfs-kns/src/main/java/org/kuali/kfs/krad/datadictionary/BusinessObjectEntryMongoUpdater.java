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
import java.util.HashMap;
import java.util.Map;

public class BusinessObjectEntryMongoUpdater {
    private Collection<BusinessObjectEntry> businessObjectEntries;
    private DataDictionaryDao dataDictionaryDao;
    private ApiNamesGenerator apiNamesGenerator;

    public BusinessObjectEntryMongoUpdater(Collection<BusinessObjectEntry> businessObjectEntries, DataDictionaryDao dataDictionaryDao) {
        this.businessObjectEntries = businessObjectEntries;
        this.dataDictionaryDao = dataDictionaryDao;
        this.apiNamesGenerator = new ApiNamesGenerator();
    }

    public void runUpdate() {
        for (BusinessObjectEntry businessObjectEntry : businessObjectEntries) {
            updateBusinessObjectEntry(businessObjectEntry);
        }
    }

    private void updateBusinessObjectEntry(BusinessObjectEntry businessObjectEntry) {
        Map<String, Object> mongoBusinessObjectEntry = dataDictionaryDao.retrieveBusinessObjectEntry(businessObjectEntry.getBusinessObjectClass().getName());
        if (mongoBusinessObjectEntry == null) {
            mongoBusinessObjectEntry = createNewMongoEntry(businessObjectEntry);
        } else {
            updateValues(businessObjectEntry, mongoBusinessObjectEntry);
        }
        dataDictionaryDao.saveBusinessObjectEntry(mongoBusinessObjectEntry);
    }

    private void updateValues(BusinessObjectEntry businessObjectEntry, Map<String, Object> mongoBusinessObjectEntry) {
        mongoBusinessObjectEntry.put("objectLabel", businessObjectEntry.getObjectLabel());
    }

    private Map<String, Object> createNewMongoEntry(BusinessObjectEntry businessObjectEntry) {
        Map<String, Object> newEntry = new HashMap<>();
        newEntry.put("institutionId", "");
        newEntry.put("module", apiNamesGenerator.convertBusinessObjectEntryToModuleName(businessObjectEntry));
        newEntry.put("businessObjectReferenceName", apiNamesGenerator.convertBusinessObjectEntryToUrlBoName(businessObjectEntry));
        newEntry.put("businessObjectClassName", businessObjectEntry.getBusinessObjectClass().getName());
        newEntry.put("objectLabel", businessObjectEntry.getObjectLabel());
        return newEntry;
    }
}
