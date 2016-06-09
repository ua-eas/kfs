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


import org.kuali.kfs.sys.datatools.liquimongo.change.AddDocumentHandler;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.util.JSON;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AddDocumentHandlerTest {
    private AddDocumentHandler addDocumentHandler;
    private MongoOperations mongoTemplate;

    @Before
    public void setup() {
        addDocumentHandler = new AddDocumentHandler();
        mongoTemplate = EasyMock.createMock(MongoOperations.class);
    }

    @Test
    public void testHandlesAddDocument() throws Exception {
        String testJson = "{ \"changeType\": \"addDocument\",\"collectionName\": \"collection\",\"query\": { },\"document\": { } }";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode testNode = mapper.readValue(testJson, JsonNode.class);

        Assert.assertEquals("Should handle addDocument element", true, addDocumentHandler.handlesChange(testNode));
    }

    @Test
    public void testDoesNotHandleDeleteDocument() throws Exception {
        String testJson = "{ \"changeType\": \"deleteDocument\",\"collectionName\": \"collection\",\"query\": { } }";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode testNode = mapper.readValue(testJson, JsonNode.class);

        Assert.assertEquals("Should not handle deleteDocument element", false, addDocumentHandler.handlesChange(testNode));
    }

    @Test
    public void testMakeChangeAddsDocument() throws Exception {
        mongoTemplate.save(JSON.parse("{ }"), "collection");
        EasyMock.expectLastCall();
        EasyMock.replay(mongoTemplate);
        
        addDocumentHandler.setMongoTemplate(mongoTemplate);

        String testJson = "{ \"changeType\": \"addDocument\",\"collectionName\": \"collection\",\"query\": { },\"document\": { } }";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode testNode = mapper.readValue(testJson, JsonNode.class);

        addDocumentHandler.makeChange(testNode);
        EasyMock.verify(mongoTemplate);
    }

    @Test
    public void testMissingFieldGivesGoodError() throws Exception {
        // Collection is missing
        String testJson = "{ \"changeType\": \"addDocument\",\"document\": { \"myId\": \"10\"} }";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode testNode = mapper.readValue(testJson, JsonNode.class);

        try {
            addDocumentHandler.makeChange(testNode);
            Assert.fail("Method should have thrown exception");
        } catch (IllegalArgumentException e) {
            // This is expected
        }
    }
    
    @Test
    public void testRevertChange() throws Exception {
        Query q = new Query();
        q.addCriteria(Criteria.where("myId").is("10"));
        mongoTemplate.remove(q, "InstitutionPreferences");
        EasyMock.expectLastCall();
        EasyMock.replay(mongoTemplate);
        
        addDocumentHandler.setMongoTemplate(mongoTemplate);
        
        String testJson = "{ \"changeType\": \"addDocument\",\"collectionName\": \"InstitutionPreferences\",\"query\": { \"myId\": \"10\"},\"document\": { \"institutionId\": \"123\" } }";
        
        ObjectMapper mapper = new ObjectMapper();
        JsonNode testNode = mapper.readValue(testJson, JsonNode.class);

        addDocumentHandler.revertChange(testNode);
        EasyMock.verify(mongoTemplate);
    }
}
