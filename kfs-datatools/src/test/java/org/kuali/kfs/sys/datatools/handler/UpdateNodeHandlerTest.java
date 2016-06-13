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
 */package org.kuali.kfs.sys.datatools.handler;

import java.util.Map;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.sys.datatools.liquimongo.change.UpdateNodeHandler;
import org.kuali.kfs.sys.datatools.liquimongo.change.JsonUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class UpdateNodeHandlerTest {

    private UpdateNodeHandler updateNodeHandler;
    private MongoOperations mongoTemplate;

    @Before
    public void setup() {
        updateNodeHandler = new UpdateNodeHandler();
        mongoTemplate = EasyMock.createMock(MongoOperations.class);
    }
    
    @Test
    public void testHandlesUpdateNodes() throws Exception {
        String testJson = "{ \"changeType\": \"updateNode\",\"collectionName\": \"collection\",\"query\": { } }";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode testNode = mapper.readValue(testJson, JsonNode.class);

        Assert.assertEquals("Should handle updateNode element", true, updateNodeHandler.handlesChange(testNode));
    }

    @Test
    public void testDoesNotHandleDeleteDocument() throws Exception {
        String testJson = "{ \"changeType\": \"deleteDocument\",\"collectionName\": \"collection\",\"query\": { } }";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode testNode = mapper.readValue(testJson, JsonNode.class);

        Assert.assertEquals("Should not handle deleteDocument element", false, updateNodeHandler.handlesChange(testNode));
    }
    
    @Test
    public void testMakeChangeUpdateNode() throws Exception {
        Query q = new Query(Criteria.where("myId").is("10"));
        Capture<DBObject> capturedObject = EasyMock.newCapture();
        
        EasyMock.expect(mongoTemplate.findOne(q, DBObject.class, "collection")).andReturn(createSampleDocumentBeforeUpdate());
        mongoTemplate.save(EasyMock.and(EasyMock.capture(capturedObject), EasyMock.isA(DBObject.class)), EasyMock.eq("backup_collection"));
        EasyMock.expectLastCall();
        mongoTemplate.remove(q, "collection");
        EasyMock.expectLastCall();
        mongoTemplate.save(createSampleDocumentAfterUpdate(), "collection");
        EasyMock.replay(mongoTemplate);
        
        String testJson = "{ \"changeType\": \"updateNode\",\"collectionName\": \"collection\","
                + "\"query\": { \"myId\": \"10\"},"
                + "\"path\": \"$..link[?(@.label=='Label5')]\","
                + "\"revertPath\": \"$..link[?(@.label=='Label6')]\","
                + "\"value\": { \"label\" : \"Label6\"}  }"; 
        
        updateNodeHandler.setMongoTemplate(mongoTemplate);
        
        ObjectMapper mapper = new ObjectMapper();
        JsonNode testNode = mapper.readValue(testJson, JsonNode.class);

        updateNodeHandler.makeChange(testNode);
        Map object = capturedObject.getValue().toMap();
        Assert.assertTrue(UpdateNodeHandler.UPDATE_NODE_CHANGE_KEY + " should be added to object", object.containsKey(UpdateNodeHandler.UPDATE_NODE_CHANGE_KEY));
        Assert.assertTrue(UpdateNodeHandler.CHANGE_DATESTAMP_KEY + " should be added to object", object.containsKey(UpdateNodeHandler.CHANGE_DATESTAMP_KEY));
        EasyMock.verify(mongoTemplate);
    }
    
    @Test
    public void testFailsIfReversionIncorrect() throws Exception {
        Query q = new Query(Criteria.where("myId").is("10"));
        
        EasyMock.expect(mongoTemplate.findOne(q, DBObject.class, "collection")).andReturn(createSampleDocumentBeforeUpdate());
        EasyMock.replay(mongoTemplate);
        
        String testJson = "{ \"changeType\": \"updateNode\",\"collectionName\": \"collection\","
                + "\"query\": { \"myId\": \"10\"},"
                + "\"path\": \"$..link[?(@.label=='Label5')]\","
                + "\"revertPath\": \"$..link[?(@.label=='Label5')]\","
                + "\"value\": { \"label\" : \"Label6\"}  }"; 
        
        updateNodeHandler.setMongoTemplate(mongoTemplate);
        
        ObjectMapper mapper = new ObjectMapper();
        JsonNode testNode = mapper.readValue(testJson, JsonNode.class);

        try {
            updateNodeHandler.makeChange(testNode);;
            Assert.fail("Method should have thrown exception");
        } catch (RuntimeException e) {
            // This is expected
        }
        
        EasyMock.verify(mongoTemplate);
    }
    
    @Test
    public void testRevertChange() throws Exception {
        String testJson = "{ \"changeType\": \"updateNode\",\"collectionName\": \"collection\","
                + "\"query\": { \"myId\": \"10\"},"
                + "\"path\": \"$..link[?(@.label=='Label5')]\","
                + "\"revertPath\": \"$..link[?(@.label=='Label6')]\","
                + "\"value\": { \"label\" : \"Label6\"}  }";         
        ObjectMapper mapper = new ObjectMapper();
        JsonNode testNode = mapper.readValue(testJson, JsonNode.class);
        
        Query q1 = new Query(Criteria.where("myId").is("10"));
        EasyMock.expect(mongoTemplate.findOne(q1, DBObject.class, "collection")).andReturn(createSampleDocumentAfterUpdate());
        Query q2 = new Query(Criteria.where(UpdateNodeHandler.UPDATE_NODE_CHANGE_KEY).is(JsonUtils.calculateHash(testNode)))
                .with(new Sort(new Order(Direction.DESC, UpdateNodeHandler.CHANGE_DATESTAMP_KEY)));
        EasyMock.expect(mongoTemplate.findOne(q2, DBObject.class, "backup_collection")).andReturn(createSampleBackupObject());
        mongoTemplate.remove(q1, "collection");
        EasyMock.expectLastCall();
        mongoTemplate.save(createSampleDocumentBeforeUpdate(), "collection");
        EasyMock.expectLastCall();
        EasyMock.replay(mongoTemplate);
        
        updateNodeHandler.setMongoTemplate(mongoTemplate);
        updateNodeHandler.revertChange(testNode);
        EasyMock.verify(mongoTemplate);
    }
    
    private DBObject createSampleBackupObject() {
        DBObject result = new BasicDBObject();
        result.put("label", "Label5");
        result.put(UpdateNodeHandler.UPDATE_NODE_CHANGE_KEY, "something");
        result.put(UpdateNodeHandler.CHANGE_DATESTAMP_KEY, 123l);
        return result;
    }
    
    private DBObject createSampleDocumentBeforeUpdate() {
        return (DBObject) JSON.parse("{ \"link\": [{\"label\": \"Label1\"}, {\"label\": \"Label5\"}, {\"label\": \"Label2\"}] }");
    }

    private DBObject createSampleDocumentAfterUpdate() {
        return (DBObject) JSON.parse("{ \"link\": [{\"label\": \"Label1\"}, {\"label\": \"Label6\"}, {\"label\": \"Label2\"}] }");
    }
    
}
