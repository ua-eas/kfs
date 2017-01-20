/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.util.JSON;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.sys.datatools.liquimongo.change.UpdateDocumentHandler;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class UpdateDocumentHandlerTest {
    private UpdateDocumentHandler updateDocumentHandler;
    private MongoOperations mongoTemplate;

    @Before
    public void setup() {
        updateDocumentHandler = new UpdateDocumentHandler();
        mongoTemplate = EasyMock.createMock(MongoOperations.class);
    }

    @Test
    public void testHandlesUpdateDocuments() throws Exception {
        String testJson = "{ \"changeType\": \"updateDocuments\",\"collectionName\": \"collection\",\"query\": {},\"document\": { } }";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode testNode = mapper.readValue(testJson, JsonNode.class);

        Assert.assertEquals("Should handle updateDocument element", true, updateDocumentHandler.handlesChange(testNode));
    }

    @Test
    public void testDoesNotHandleAddDocument() throws Exception {
        String testJson = "{ \"changeType\": \"addDocument\",\"collectionName\": \"collection\",\"query\": {},\"document\": { } }";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode testNode = mapper.readValue(testJson, JsonNode.class);

        Assert.assertEquals("Should not handle addDocument element", false, updateDocumentHandler.handlesChange(testNode));
    }

    @Test
    public void testMakeChangeUpdatesDocument() throws Exception {
        Query q = new Query(Criteria.where("myId").is("10"));

        mongoTemplate.remove(q, "collection");
        EasyMock.expectLastCall();
        mongoTemplate.save(JSON.parse("{ }"), "collection");
        EasyMock.expectLastCall();
        EasyMock.replay(mongoTemplate);
        updateDocumentHandler.setMongoTemplate(mongoTemplate);

        String testJson = "{ \"changeType\": \"updateDocuments\",\"collectionName\": \"collection\",\"query\": { \"myId\": \"10\" },\"document\": {} }";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode testNode = mapper.readValue(testJson, JsonNode.class);

        updateDocumentHandler.makeChange(testNode);
        EasyMock.verify(mongoTemplate);
    }

    @Test
    public void testMissingFieldGivesGoodError() throws Exception {
        // Collection is missing
        String testJson = "{ \"changeType\": \"addDocument\",\"query\": {},\"document\": { \"myId\": \"10\"} }";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode testNode = mapper.readValue(testJson, JsonNode.class);

        try {
            updateDocumentHandler.makeChange(testNode);
            Assert.fail("Method should have thrown exception");
        } catch (IllegalArgumentException e) {
            // This is expected
        }
    }
}
