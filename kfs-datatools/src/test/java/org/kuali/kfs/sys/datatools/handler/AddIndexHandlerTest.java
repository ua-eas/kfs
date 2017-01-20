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
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.sys.datatools.liquimongo.change.AddIndexHandler;
import org.springframework.data.mongodb.core.IndexOperations;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.index.IndexInfo;

import java.util.List;

public class AddIndexHandlerTest {
    private AddIndexHandler addIndexHandler;
    private MongoOperations mongoTemplate;

    @Before
    public void setup() {
        addIndexHandler = new AddIndexHandler();
        mongoTemplate = EasyMock.createMock(MongoOperations.class);
    }

    @Test
    public void testHandlesAddIndex() throws Exception {
        String testJson = "{ \"changeType\": \"addIndex\",\"collectionName\": \"collection\",\"query\": { },\"document\": { } }";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode testNode = mapper.readValue(testJson, JsonNode.class);

        Assert.assertEquals("Should handle addIndex element", true, addIndexHandler.handlesChange(testNode));
    }

    @Test
    public void testDoesNotHandleDeleteDocument() throws Exception {
        String testJson = "{ \"changeType\": \"deleteDocument\",\"collectionName\": \"collection\",\"query\": { } }";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode testNode = mapper.readValue(testJson, JsonNode.class);

        Assert.assertEquals("Should not handle deleteDocument element", false, addIndexHandler.handlesChange(testNode));
    }

    @Test
    public void testMakeChangeAddsIndex() throws Exception {
        EasyMock.expect(mongoTemplate.indexOps("collection")).andReturn(new IndexOperations() {
            @Override
            public void ensureIndex(IndexDefinition indexDefinition) {
            }

            @Override
            public void dropIndex(String s) {
            }

            @Override
            public void dropAllIndexes() {
            }

            @Override
            public void resetIndexCache() {
            }

            @Override
            public List<IndexInfo> getIndexInfo() {
                return null;
            }
        });
        EasyMock.expectLastCall();
        EasyMock.replay(mongoTemplate);

        addIndexHandler.setMongoTemplate(mongoTemplate);

        String testJson = "{ \"changeType\": \"addIndex\",\"collectionName\": \"collection\",\"key\": { \"createdAt\": 1 },\"options\": { \"name\": \"expireIndex\" } }";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode testNode = mapper.readValue(testJson, JsonNode.class);

        addIndexHandler.makeChange(testNode);
        EasyMock.verify(mongoTemplate);
    }

    @Test
    public void testMissingFieldGivesGoodError() throws Exception {
        // Collection is missing
        String testJson = "{ \"changeType\": \"addIndex\",\"key\": { \"myId\": \"1\"} }";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode testNode = mapper.readValue(testJson, JsonNode.class);

        try {
            addIndexHandler.makeChange(testNode);
            Assert.fail("Method should have thrown exception");
        } catch (IllegalArgumentException e) {
            // This is expected
        }
    }
}
