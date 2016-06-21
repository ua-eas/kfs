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

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.sys.datatools.liquimongo.change.DeleteNodeHandler;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class DeleteNodeHandlerTest {
    private DeleteNodeHandler deleteNodeHandler;
    private MongoOperations mongoTemplate;

    @Before
    public void setup() {
        deleteNodeHandler = new DeleteNodeHandler();
        mongoTemplate = EasyMock.createMock(MongoOperations.class);
    }
    
    @Test
    public void testHandlesDeleteNodes() throws Exception {
        String testJson = "{ \"changeType\": \"deleteNode\",\"collectionName\": \"collection\",\"query\": { } }";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode testNode = mapper.readValue(testJson, JsonNode.class);

        Assert.assertEquals("Should handle deleteNode element", true, deleteNodeHandler.handlesChange(testNode));
    }

    @Test
    public void testDoesNotHandleDeleteDocument() throws Exception {
        String testJson = "{ \"changeType\": \"deleteDocument\",\"collectionName\": \"collection\",\"query\": { } }";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode testNode = mapper.readValue(testJson, JsonNode.class);

        Assert.assertEquals("Should not handle deleteDocument element", false, deleteNodeHandler.handlesChange(testNode));
    }
    
    @Test
    public void testMakeChangeDeleteNode() throws Exception {
        Query q = new Query(Criteria.where("myId").is("10"));
        
        EasyMock.expect(mongoTemplate.findOne(q, DBObject.class, "collection")).andReturn(createSampleDocumentBeforeRemoval());
        mongoTemplate.remove(q, "collection");
        EasyMock.expectLastCall();
        mongoTemplate.save(createSampleDocumentAfterRemoval(), "collection");
        EasyMock.replay(mongoTemplate);
        
        String testJson = "{ \"changeType\": \"deleteNode\",\"collectionName\": \"collection\","
                + "\"query\": { \"myId\": \"10\"},\"revertPath\": \"$..link\","
                + "\"path\": \"$..link[?(@.label=='Label5')]\" }";  
        
        deleteNodeHandler.setMongoTemplate(mongoTemplate);
        
        ObjectMapper mapper = new ObjectMapper();
        JsonNode testNode = mapper.readValue(testJson, JsonNode.class);

        deleteNodeHandler.makeChange(testNode);
        EasyMock.verify(mongoTemplate);
    }
    
    private DBObject createSampleDocumentBeforeRemoval() {
        return (DBObject) JSON.parse("{ \"link\": [{\"label\": \"Label1\"}, {\"label\": \"Label5\"}, {\"label\": \"Label2\"}] }");
    }

    private DBObject createSampleDocumentAfterRemoval() {
        return (DBObject) JSON.parse("{ \"link\": [{\"label\": \"Label1\"}, {\"label\": \"Label2\"}] }");
    }
}
