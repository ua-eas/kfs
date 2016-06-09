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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * Add requested document to MongoDB
 */
public class AddDocumentHandler extends AbstractDocumentStoreChangeHandler implements DocumentStoreChangeHandler {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AddDocumentHandler.class);
    public static final String ADD_DOCUMENT = "addDocument";

    @Override
    public boolean handlesChange(JsonNode change) {
        return isKeyValueCorrect(change, CHANGE_TYPE, ADD_DOCUMENT);
    }

    @Override
    public void makeChange(JsonNode change) {
        LOG.debug("makeChange() started");

        verifyKeyExistence(change, COLLECTION_NAME);
        verifyKeyExistence(change, DOCUMENT);
        verifyKeyExistence(change,QUERY); // Needed for reversion

        String collectionName = change.get(COLLECTION_NAME).asText();
        JsonNode document = change.get(DOCUMENT);

        DBObject dbObject = (DBObject)JSON.parse(document.toString());
        mongoTemplate.save(dbObject, collectionName);
    }

    @Override
    public void revertChange(JsonNode change) {
        LOG.debug("revertChange() started");

        verifyKeyExistence(change, COLLECTION_NAME);
        verifyKeyExistence(change,QUERY);

        String collectionName = change.get(COLLECTION_NAME).asText();
        JsonNode query = change.get(QUERY); 
        Query q = JsonUtils.getQueryFromJson(query);   
        
        mongoTemplate.remove(q, collectionName);
    }
}
