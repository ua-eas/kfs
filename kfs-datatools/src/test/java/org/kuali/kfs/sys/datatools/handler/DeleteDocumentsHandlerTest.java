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
package org.kuali.kfs.sys.datatools.handler;

import org.kuali.kfs.sys.datatools.liquimongo.change.DeleteDocumentsHandler;
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
import java.util.Map;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DeleteDocumentsHandlerTest {
    private DeleteDocumentsHandler deleteDocumentsHandler;
    private MongoOperations mongoTemplate;

    @Before
    public void setup() {
        deleteDocumentsHandler = new DeleteDocumentsHandler();
        mongoTemplate = EasyMock.createMock(MongoOperations.class);
    }

    @Test
    public void testHandlesDeleteDocuments() throws Exception {
        String testJson = "{ \"changeType\": \"deleteDocument\",\"collectionName\": \"collection\",\"query\": { } }";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode testNode = mapper.readValue(testJson, JsonNode.class);

        Assert.assertEquals("Should handle deleteDocument element", true, deleteDocumentsHandler.handlesChange(testNode));
    }

    @Test
    public void testDoesNotHandleAddDocument() throws Exception {
        String testJson = "{ \"changeType\": \"addDocument\",\"collectionName\": \"collection\",\"query\": { } }";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode testNode = mapper.readValue(testJson, JsonNode.class);

        Assert.assertEquals("Should not handle addDocument element", false, deleteDocumentsHandler.handlesChange(testNode));
    }

    @Test
    public void testMakeChangeDeletesDocuments() throws Exception {
        Query q = new Query(Criteria.where("myId").is("10"));
        Capture<DBObject> capturedObject = EasyMock.newCapture();
        
        EasyMock.expect(mongoTemplate.findOne(q, DBObject.class, "collection")).andReturn(createSampleObject());
        mongoTemplate.save(EasyMock.and(EasyMock.capture(capturedObject), EasyMock.isA(DBObject.class)), EasyMock.eq("backup_collection"));
        EasyMock.expectLastCall();
        mongoTemplate.remove(q, "collection");
        EasyMock.expectLastCall();
        EasyMock.replay(mongoTemplate);
        
        String testJson = "{ \"changeType\": \"deleteDocument\",\"collectionName\": \"collection\",\"query\": { \"myId\": \"10\"} }";    
        
        deleteDocumentsHandler.setMongoTemplate(mongoTemplate);

        
        ObjectMapper mapper = new ObjectMapper();
        JsonNode testNode = mapper.readValue(testJson, JsonNode.class);

        deleteDocumentsHandler.makeChange(testNode);
        Map object = capturedObject.getValue().toMap();
        Assert.assertTrue(DeleteDocumentsHandler.DELETE_CHANGE_KEY + " should be added to object", object.containsKey(DeleteDocumentsHandler.DELETE_CHANGE_KEY));
        Assert.assertTrue(DeleteDocumentsHandler.CHANGE_DATESTAMP_KEY + " should be added to object", object.containsKey(DeleteDocumentsHandler.CHANGE_DATESTAMP_KEY));
        EasyMock.verify(mongoTemplate);
    }
    
    @Test
    public void testRevertChange() throws Exception {
        String testJson = "{ \"changeType\": \"deleteDocument\",\"collectionName\": \"collection\",\"query\": { \"myId\": \"10\"} }";         
        ObjectMapper mapper = new ObjectMapper();
        JsonNode testNode = mapper.readValue(testJson, JsonNode.class);
        
        Query q = new Query(Criteria.where("myId").is("10"))
                .addCriteria(Criteria.where(DeleteDocumentsHandler.DELETE_CHANGE_KEY).is(JsonUtils.calculateHash(testNode)))
                .with(new Sort(new Order(Direction.DESC, DeleteDocumentsHandler.CHANGE_DATESTAMP_KEY)));
        EasyMock.expect(mongoTemplate.findOne(q, DBObject.class, "backup_collection")).andReturn(createSampleBackupObject());
        mongoTemplate.save(createSampleObject(), "collection");
        EasyMock.expectLastCall();
        q = new Query(Criteria.where("myId").is("10"))
                .addCriteria(Criteria.where(DeleteDocumentsHandler.CHANGE_DATESTAMP_KEY).gte(123l));
        mongoTemplate.remove(q, "backup_collection");
        EasyMock.expectLastCall();
        EasyMock.replay(mongoTemplate);
        
        deleteDocumentsHandler.setMongoTemplate(mongoTemplate);
        deleteDocumentsHandler.revertChange(testNode);
        EasyMock.verify(mongoTemplate);
    }

    private DBObject createSampleBackupObject() {
        DBObject result = new BasicDBObject();
        result.put(DeleteDocumentsHandler.DELETE_CHANGE_KEY, "something");
        result.put(DeleteDocumentsHandler.CHANGE_DATESTAMP_KEY, 123l);
        return result;
    }

    private DBObject createSampleObject() {
        return new BasicDBObject();
    }

    @Test
    public void testMissingFieldGivesGoodError() throws Exception {
        deleteDocumentsHandler.setMongoTemplate(mongoTemplate);

        // Collection is missing
        String testJson = "{ \"changeType\": \"deleteDocuments\",\"query\": { \"myId\": \"10\"} }";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode testNode = mapper.readValue(testJson, JsonNode.class);

        try {
            deleteDocumentsHandler.makeChange(testNode);
            Assert.fail("Method should have thrown exception");
        } catch (IllegalArgumentException e) {
            // This is expected
        }
    }
}
