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
package org.kuali.kfs.sys.datatools.liquimongo.change;

import java.util.Date;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.DBObject;

/**
 * Delete requested documents from MongoDB
 */
public class DeleteDocumentsHandler extends AbstractDocumentStoreChangeHandler implements DocumentStoreChangeHandler {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DeleteDocumentsHandler.class);
    public static final String DELETE_DOCUMENTS = "deleteDocument";
    public static final String DELETE_CHANGE_KEY = "deleteChangeKey";

    @Override
    public boolean handlesChange(JsonNode change) {
        return isKeyValueCorrect(change, CHANGE_TYPE, DELETE_DOCUMENTS);
    }

    @Override
    public void makeChange(JsonNode change) {
        LOG.debug("makeChange() started");

        verifyKeyExistence(change,COLLECTION_NAME);
        verifyKeyExistence(change,QUERY);

        String collectionName = change.get(COLLECTION_NAME).asText();
        JsonNode query = change.get(QUERY); 
        Query q = JsonUtils.getQueryFromJson(query);
        backupDocument(q, change, collectionName, DELETE_CHANGE_KEY);
        
        mongoTemplate.remove(q, collectionName);
    }

    @Override
    public void revertChange(JsonNode change) {
        LOG.debug("revertChange() started");

        verifyKeyExistence(change,COLLECTION_NAME);
        verifyKeyExistence(change,QUERY);

        String collectionName = change.get(COLLECTION_NAME).asText();
        String backupCollectionName = BACKUP_PREFIX + collectionName;
        JsonNode query = change.get(QUERY);
        Query q = JsonUtils.getQueryFromJson(query)
                .addCriteria(Criteria.where(DELETE_CHANGE_KEY).is(JsonUtils.calculateHash(change)))
                .with(new Sort(new Order(Direction.DESC, CHANGE_DATESTAMP_KEY)));
        
        // Restore old version
        DBObject object = mongoTemplate.findOne(q, DBObject.class, backupCollectionName);
        if (object != null) {
            Object datestamp = object.get(CHANGE_DATESTAMP_KEY);
            object.removeField(DELETE_CHANGE_KEY);
            object.removeField(CHANGE_DATESTAMP_KEY);
            mongoTemplate.save(object, collectionName);
            
            // Remove from backup all matching documents that are more recent.
            q = JsonUtils.getQueryFromJson(query)
                    .addCriteria(Criteria.where(CHANGE_DATESTAMP_KEY).gte(datestamp));
            mongoTemplate.remove(q, backupCollectionName);
        }       
    }
}
