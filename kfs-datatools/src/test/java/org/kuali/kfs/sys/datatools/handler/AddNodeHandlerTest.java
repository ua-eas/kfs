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
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.sys.datatools.liquimongo.change.AddNodeHandler;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class AddNodeHandlerTest {

    private AddNodeHandler addNodeHandler;
    private MongoOperations mongoTemplate;

    @Before
    public void setup() {
        addNodeHandler = new AddNodeHandler();
        mongoTemplate = EasyMock.createMock(MongoOperations.class);
    }

    @Test
    public void testHandlesAddNodes() throws Exception {
        String testJson = "{ \"changeType\": \"addNode\",\"collectionName\": \"collection\",\"query\": { } }";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode testNode = mapper.readValue(testJson, JsonNode.class);

        Assert.assertEquals("Should handle addNode element", true, addNodeHandler.handlesChange(testNode));
    }

    @Test
    public void testDoesNotHandleDeleteDocument() throws Exception {
        String testJson = "{ \"changeType\": \"deleteDocument\",\"collectionName\": \"collection\",\"query\": { } }";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode testNode = mapper.readValue(testJson, JsonNode.class);

        Assert.assertEquals("Should not handle deleteDocument element", false, addNodeHandler.handlesChange(testNode));
    }

    @Test
    public void testMakeChangeAddNode() throws Exception {
        Query q = new Query(Criteria.where("myId").is("10"));

        EasyMock.expect(mongoTemplate.findOne(q, DBObject.class, "collection")).andReturn(createSampleDocumentBeforeAddition());
        mongoTemplate.remove(q, "collection");
        EasyMock.expectLastCall();
        mongoTemplate.save(createSampleDocumentAfterAddition(), "collection");
        EasyMock.replay(mongoTemplate);

        String testJson = "{ \"changeType\": \"addNode\",\"collectionName\": \"collection\","
            + "\"query\": { \"myId\": \"10\"},\"path\": \"$..link\","
            + "\"addBeforeNode\": { \"label\" : \"Label2\"},"
            + "\"value\": { \"label\" : \"Label5\"}  }";

        addNodeHandler.setMongoTemplate(mongoTemplate);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode testNode = mapper.readValue(testJson, JsonNode.class);

        addNodeHandler.makeChange(testNode);
        EasyMock.verify(mongoTemplate);
    }

    private DBObject createSampleDocumentAfterAddition() {
        return (DBObject) JSON.parse("{ \"link\": [{\"label\": \"Label1\"}, {\"label\": \"Label5\"}, {\"label\": \"Label2\"}] }");
    }

    private DBObject createSampleDocumentBeforeAddition() {
        return (DBObject) JSON.parse("{ \"link\": [{\"label\": \"Label1\"}, {\"label\": \"Label2\"}] }");
    }

}
