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
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

        mongoTemplate.remove(q, "collection");
        EasyMock.expectLastCall();
        EasyMock.replay(mongoTemplate);

        String testJson = "{ \"changeType\": \"deleteDocument\",\"collectionName\": \"collection\",\"query\": { \"myId\": \"10\"} }";

        deleteDocumentsHandler.setMongoTemplate(mongoTemplate);


        ObjectMapper mapper = new ObjectMapper();
        JsonNode testNode = mapper.readValue(testJson, JsonNode.class);

        deleteDocumentsHandler.makeChange(testNode);
        EasyMock.verify(mongoTemplate);
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
