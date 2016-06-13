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

import java.io.IOException;

import org.springframework.data.mongodb.core.query.Query;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class AddNodeHandler extends AbstractNodeChangeHandler implements DocumentStoreChangeHandler {
    
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AddNodeHandler.class);
    public static final String ADD_BEFORE_NODE = "addBeforeNode";
    public static final String ADD_NODE = "addNode";

    @Override
    public boolean handlesChange(JsonNode change) {
        return isKeyValueCorrect(change, CHANGE_TYPE, ADD_NODE);
    }

    @Override
    public void makeChange(JsonNode change) {
        LOG.debug("makeChange() started");

        verifyKeyExistence(change,COLLECTION_NAME);
        verifyKeyExistence(change,QUERY);
        verifyKeyExistence(change,VALUE);
        
        String collectionName = change.get(COLLECTION_NAME).asText();
        DBObject nodeToAdd = (DBObject) JSON.parse(change.get(VALUE).toString());
        JsonNode query = change.get(QUERY); 
        Query q = JsonUtils.getQueryFromJson(query);
        
        String documentJson = mongoTemplate.findOne(q, DBObject.class, collectionName).toString();
        String newJson = addNode(change, documentJson, nodeToAdd, PATH, ADD_BEFORE_NODE).toString();
        
        // Verify reversion    
        verifyKeyExistence(change,REVERT_PATH);
        String revertPath = change.get(REVERT_PATH).asText();
        String revertedJson = JsonPath.parse(newJson).delete(revertPath).jsonString();       
        try {
            ObjectMapper mapper = new ObjectMapper();
            if (!mapper.readTree(documentJson).equals(mapper.readTree(revertedJson))) {
                throw new RuntimeException("Reversion information does not match change: " + change.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException("Invalid Json created: " + revertedJson, e);
        }       
        
        DBObject result = (DBObject) JSON.parse(newJson);
        mongoTemplate.remove(q, collectionName);
        mongoTemplate.save(result, collectionName);
    }

    @Override
    public void revertChange(JsonNode change) {
        LOG.debug("revertChange() started");

        verifyKeyExistence(change,COLLECTION_NAME);
        verifyKeyExistence(change,QUERY);  
        verifyKeyExistence(change,REVERT_PATH);
        
        String collectionName = change.get(COLLECTION_NAME).asText();
        String revertPath = change.get(REVERT_PATH).asText();
        JsonNode query = change.get(QUERY);
        Query q = JsonUtils.getQueryFromJson(query);       
        String documentJson = mongoTemplate.findOne(q, DBObject.class, collectionName).toString();
        String revertedJson = JsonPath.parse(documentJson).delete(revertPath).jsonString();
        
        DBObject result = (DBObject) JSON.parse(revertedJson);
        mongoTemplate.remove(q, collectionName);
        mongoTemplate.save(result, collectionName);
    }

}
