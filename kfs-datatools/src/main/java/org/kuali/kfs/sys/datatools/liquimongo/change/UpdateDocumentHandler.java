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

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import java.util.Date;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * Update requested document in MongoDB
 */
public class UpdateDocumentHandler extends AbstractDocumentStoreChangeHandler implements DocumentStoreChangeHandler {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UpdateDocumentHandler.class);
    public static final String UPDATE_DOCUMENTS = "updateDocuments";
    public static final String UPDATE_CHANGE_KEY = "updateChangeKey";

    @Override
    public boolean handlesChange(JsonNode change) {
        return isKeyValueCorrect(change, CHANGE_TYPE, UPDATE_DOCUMENTS);
    }

    @Override
    public void makeChange(JsonNode change) {
        LOG.debug("makeChange() started");

        verifyKeyExistence(change, COLLECTION_NAME);
        verifyKeyExistence(change, QUERY);
        verifyKeyExistence(change, DOCUMENT);

        String collectionName = change.get(COLLECTION_NAME).asText();
        JsonNode query = change.get(QUERY); 
        Query q = JsonUtils.getQueryFromJson(query);
        backupDocument(q, change, collectionName, UPDATE_CHANGE_KEY);
        
        // Delete then add the document
        JsonNode document = change.get(DOCUMENT);
        mongoTemplate.remove(q, collectionName);
        DBObject dbObject = (DBObject) JSON.parse(document.toString());
        mongoTemplate.save(dbObject, collectionName);
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
                .addCriteria(Criteria.where(UPDATE_CHANGE_KEY).is(JsonUtils.calculateHash(change)))
                .with(new Sort(new Order(Direction.DESC, CHANGE_DATESTAMP_KEY)));
        
        // Restore old version
        DBObject object = mongoTemplate.findOne(q, DBObject.class, backupCollectionName);
        if (object != null) {
            Object datestamp = object.get(CHANGE_DATESTAMP_KEY);
            object.removeField(UPDATE_CHANGE_KEY);
            object.removeField(CHANGE_DATESTAMP_KEY);
            
            mongoTemplate.remove(JsonUtils.getQueryFromJson(query), collectionName);
            mongoTemplate.save(object, collectionName);
            
            // Remove from backup all matching documents that are more recent.
            q = JsonUtils.getQueryFromJson(query)
                    .addCriteria(Criteria.where(CHANGE_DATESTAMP_KEY).gte(datestamp));
            mongoTemplate.remove(q, backupCollectionName);
        }   
    }
}