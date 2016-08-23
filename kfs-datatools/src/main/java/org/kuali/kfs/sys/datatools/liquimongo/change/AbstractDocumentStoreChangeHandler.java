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
package org.kuali.kfs.sys.datatools.liquimongo.change;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.mongodb.core.MongoOperations;

public abstract class AbstractDocumentStoreChangeHandler implements DocumentStoreChangeHandler {
    protected MongoOperations mongoTemplate;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AbstractDocumentStoreChangeHandler.class);

    public static final String CHANGE_TYPE = "changeType";

    protected boolean isKeyValueCorrect(JsonNode node, String key, String value) {
        return node.get(key).asText().equals(value);
    }

    protected void verifyKeyExistence(JsonNode node, String key) {
        if (node.get(key) == null) {
            LOG.error("verifyKeyExistence() " + key + " is required in node: " + node);
            throw new IllegalArgumentException(key + " is missing from change json");
        }
    }

    public void setMongoTemplate(MongoOperations mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
}
