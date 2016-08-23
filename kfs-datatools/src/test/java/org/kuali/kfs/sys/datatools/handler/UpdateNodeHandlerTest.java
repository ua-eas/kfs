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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.sys.datatools.liquimongo.change.UpdateNodeHandler;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

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

        EasyMock.expect(mongoTemplate.findOne(q, DBObject.class, "collection")).andReturn(createSampleDocumentBeforeUpdate());
        mongoTemplate.remove(q, "collection");
        EasyMock.expectLastCall();
        mongoTemplate.save(createSampleDocumentAfterUpdate(), "collection");
        EasyMock.replay(mongoTemplate);

        String testJson = "{ \"changeType\": \"updateNode\",\"collectionName\": \"collection\","
            + "\"query\": { \"myId\": \"10\"},"
            + "\"path\": \"$..link[?(@.label=='Label5')]\","
            + "\"value\": { \"label\" : \"Label6\"}  }";

        updateNodeHandler.setMongoTemplate(mongoTemplate);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode testNode = mapper.readValue(testJson, JsonNode.class);

        updateNodeHandler.makeChange(testNode);
        EasyMock.verify(mongoTemplate);
    }

    @Test
    public void testMakeChangeUpdateLabel() throws Exception {
        Query q = new Query(Criteria.where("myId").is("10"));

        EasyMock.expect(mongoTemplate.findOne(q, DBObject.class, "collection")).andReturn(createSampleDocumentBeforeUpdate());
        mongoTemplate.remove(q, "collection");
        EasyMock.expectLastCall();
        mongoTemplate.save(createSampleDocumentAfterUpdate(), "collection");
        EasyMock.replay(mongoTemplate);

        String testJson = "{ \"changeType\": \"updateNode\",\"collectionName\": \"collection\","
            + "\"query\": { \"myId\": \"10\"},"
            + "\"path\": \"$..link[?(@.label=='Label5')].label\","
            + "\"value\": \"Label6\" }";

        updateNodeHandler.setMongoTemplate(mongoTemplate);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode testNode = mapper.readValue(testJson, JsonNode.class);

        updateNodeHandler.makeChange(testNode);
        EasyMock.verify(mongoTemplate);
    }

    private DBObject createSampleDocumentBeforeUpdate() {
        return (DBObject) JSON.parse("{ \"link\": [{\"label\": \"Label1\"}, {\"label\": \"Label5\"}, {\"label\": \"Label2\"}] }");
    }

    private DBObject createSampleDocumentAfterUpdate() {
        return (DBObject) JSON.parse("{ \"link\": [{\"label\": \"Label1\"}, {\"label\": \"Label6\"}, {\"label\": \"Label2\"}] }");
    }

}
