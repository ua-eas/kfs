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
import java.util.Date;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class DeleteNodeHandler extends AbstractNodeChangeHandler implements DocumentStoreChangeHandler {
    
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DeleteNodeHandler.class);
    public static final String DELETE_NODE = "deleteNode";
    public static final String DELETE_NODE_CHANGE_KEY = "deleteNodeChangeKey";
    public static final String REVERT_BEFORE_NODE = "revertBeforeNode";

    @Override
    public boolean handlesChange(JsonNode change) {
        return isKeyValueCorrect(change, CHANGE_TYPE, DELETE_NODE);
    }

    @Override
    public void makeChange(JsonNode change) {
        LOG.debug("makeChange() started");

        verifyKeyExistence(change,COLLECTION_NAME);
        verifyKeyExistence(change,QUERY);
        verifyKeyExistence(change,PATH);
        
        String collectionName = change.get(COLLECTION_NAME).asText();
        String path = change.get(PATH).asText();
        JsonNode query = change.get(QUERY); 
        Query q = JsonUtils.getQueryFromJson(query);
        
        String documentJson = mongoTemplate.findOne(q, DBObject.class, collectionName).toString();    
        String newJson = JsonPath.parse(documentJson).delete(path).jsonString();
        
        // Verify reversion
        DocumentContext dc = JsonPath.parse(documentJson);               
        DBObject nodeToDelete = (DBObject) JSON.parse(JSON.serialize(findNode(dc, path)));       
        String revertedJson = addNode(change, newJson, nodeToDelete, REVERT_PATH, REVERT_BEFORE_NODE).toString();
        try {
            ObjectMapper mapper = new ObjectMapper();
            if (!mapper.readTree(documentJson).equals(mapper.readTree(revertedJson))) {
                throw new RuntimeException("Reversion information does not match change: " + change.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException("Invalid Json created: " + revertedJson, e);
        } 
        
        // Backup old node
        nodeToDelete.put(DELETE_NODE_CHANGE_KEY, JsonUtils.calculateHash(change));
        nodeToDelete.put(CHANGE_DATESTAMP_KEY, (new Date()).getTime());
        String backupCollectionName = BACKUP_PREFIX + collectionName; 
        mongoTemplate.save(nodeToDelete, backupCollectionName);        
        
        
        DBObject result = (DBObject) JSON.parse(newJson);
        mongoTemplate.remove(q, collectionName);
        mongoTemplate.save(result, collectionName);
    }

    @Override
    public void revertChange(JsonNode change) {
        LOG.debug("revertChange() started");

        verifyKeyExistence(change,COLLECTION_NAME);
        verifyKeyExistence(change,QUERY);       
        
        String collectionName = change.get(COLLECTION_NAME).asText();
        String backupCollectionName = BACKUP_PREFIX + collectionName;
        JsonNode query = change.get(QUERY);
        Query q = JsonUtils.getQueryFromJson(query);       
        String documentJson = mongoTemplate.findOne(q, DBObject.class, collectionName).toString();              
         
        // Restore old version
        Query backupQuery = new Query(Criteria.where(DELETE_NODE_CHANGE_KEY).is(JsonUtils.calculateHash(change)))
                .with(new Sort(new Order(Direction.DESC, CHANGE_DATESTAMP_KEY)));
        DBObject oldNode = mongoTemplate.findOne(backupQuery, DBObject.class, backupCollectionName);
        if (oldNode != null) {
            oldNode.removeField(DELETE_NODE_CHANGE_KEY);
            oldNode.removeField(CHANGE_DATESTAMP_KEY);
            DBObject result = addNode(change, documentJson, oldNode, REVERT_PATH, REVERT_BEFORE_NODE);
            mongoTemplate.remove(q, collectionName);
            mongoTemplate.save(result, collectionName);
        }
    }

}
