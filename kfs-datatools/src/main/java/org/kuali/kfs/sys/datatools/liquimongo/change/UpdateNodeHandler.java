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

public class UpdateNodeHandler extends AbstractNodeChangeHandler implements DocumentStoreChangeHandler {
    
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UpdateNodeHandler.class);
    public static final String UPDATE_NODE = "updateNode";
    public static final String UPDATE_NODE_CHANGE_KEY = "updateNodeChangeKey";

    @Override
    public boolean handlesChange(JsonNode change) {
        return isKeyValueCorrect(change, CHANGE_TYPE, UPDATE_NODE);
    }

    @Override
    public void makeChange(JsonNode change) {
        LOG.debug("makeChange() started");

        verifyKeyExistence(change,COLLECTION_NAME);
        verifyKeyExistence(change,QUERY);
        verifyKeyExistence(change,PATH);
        verifyKeyExistence(change,REVERT_PATH);
        verifyKeyExistence(change,VALUE);
        
        String collectionName = change.get(COLLECTION_NAME).asText();
        DBObject newValue = (DBObject) JSON.parse(change.get(VALUE).toString());
        String path = change.get(PATH).asText();
        String revertPath = change.get(REVERT_PATH).asText();
        JsonNode query = change.get(QUERY); 
        Query q = JsonUtils.getQueryFromJson(query);
        
        String documentJson = mongoTemplate.findOne(q, DBObject.class, collectionName).toString();
        String newJson = JsonPath.parse(documentJson).set(path, newValue).jsonString();
        
        // Verify reversion
        DocumentContext dc = JsonPath.parse(documentJson);    
        DBObject nodeToChange = (DBObject) JSON.parse(JSON.serialize(findNode(dc, path)));       
        String revertedJson = JsonPath.parse(newJson).set(revertPath, nodeToChange).jsonString();
        try {
            ObjectMapper mapper = new ObjectMapper();
            if (!mapper.readTree(documentJson).equals(mapper.readTree(revertedJson))) {
                throw new RuntimeException("Reversion information does not match change: " + change.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException("Invalid Json created: " + revertedJson, e);
        } 
        
        // Backup old node
        nodeToChange.put(UPDATE_NODE_CHANGE_KEY, JsonUtils.calculateHash(change));
        nodeToChange.put(CHANGE_DATESTAMP_KEY, (new Date()).getTime());
        String backupCollectionName = BACKUP_PREFIX + collectionName; 
        mongoTemplate.save(nodeToChange, backupCollectionName);        
               
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
        String backupCollectionName = BACKUP_PREFIX + collectionName;
        String revertPath = change.get(REVERT_PATH).asText();
        JsonNode query = change.get(QUERY);
        Query q = JsonUtils.getQueryFromJson(query);       
        String documentJson = mongoTemplate.findOne(q, DBObject.class, collectionName).toString();              
         
        // Restore old version
        Query backupQuery = new Query(Criteria.where(UPDATE_NODE_CHANGE_KEY).is(JsonUtils.calculateHash(change)))
                .with(new Sort(new Order(Direction.DESC, CHANGE_DATESTAMP_KEY)));
        DBObject oldNode = mongoTemplate.findOne(backupQuery, DBObject.class, backupCollectionName);
        if (oldNode != null) {
            oldNode.removeField(UPDATE_NODE_CHANGE_KEY);
            oldNode.removeField(CHANGE_DATESTAMP_KEY);
            String revertedJson = JsonPath.parse(documentJson).set(revertPath, oldNode).jsonString();
            DBObject result = (DBObject) JSON.parse(revertedJson);
            mongoTemplate.remove(q, collectionName);
            mongoTemplate.save(result, collectionName);
        }
    }

}
