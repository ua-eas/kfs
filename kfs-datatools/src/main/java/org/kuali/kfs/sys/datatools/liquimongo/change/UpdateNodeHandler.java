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
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class UpdateNodeHandler extends AbstractNodeChangeHandler implements DocumentStoreChangeHandler {
    
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UpdateNodeHandler.class);
    public static final String UPDATE_NODE = "updateNode";

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
        verifyKeyExistence(change,VALUE);
        
        String collectionName = change.get(COLLECTION_NAME).asText();
        Object newValue = JSON.parse(change.get(VALUE).toString());
        String path = change.get(PATH).asText();
        JsonNode query = change.get(QUERY); 
        Query q = JsonUtils.getQueryFromJson(query);
        
        String documentJson = mongoTemplate.findOne(q, DBObject.class, collectionName).toString();
        String newJson = JsonPath.parse(documentJson).set(path, newValue).jsonString(); 
               
        DBObject result = (DBObject) JSON.parse(newJson);
        mongoTemplate.remove(q, collectionName);
        mongoTemplate.save(result, collectionName);
    }

}
