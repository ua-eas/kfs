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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Hex;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;

/**
 * Helpful methods for manipulating Json in the context of the Mongo database.
 */
public class JsonUtils {

    /**
     * Convert a json string in the form of  { "field1": "value", "field2": "value2" }
     * into a Query object
     *
     * @param query
     * @return
     */
    public static Query getQueryFromJson(JsonNode query) {
        Query q = new Query();

        Iterator<String> items = query.fieldNames();
        while (items.hasNext()) {
            String key = items.next();
            q.addCriteria(Criteria.where(key).is(query.get(key).asText()));
        }
        return q;
    }

    /**
     * Calculates a hash value for a JSON node.
     *
     * @param node
     * @return
     */
    public static String calculateHash(JsonNode node) {
        try {
            byte[] bytesOfMessage = node.toString().getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] md5hash = md.digest(bytesOfMessage);
            return Hex.encodeHexString(md5hash);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new UnsupportedOperationException("Unable to hash change", e);
        }
    }

    /**
     * Convert a json string in the form of  { "field1": "value", "field2": "value2" }
     * into a Map
     *
     * @param query
     * @return
     */
    public static Map<String, Object> getMapFromJson(JsonNode node) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(node, Map.class);
    }
}
