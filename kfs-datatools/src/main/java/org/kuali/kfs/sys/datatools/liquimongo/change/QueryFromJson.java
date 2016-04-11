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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Iterator;

/**
 * Convert a json string in the form of  { "field1": "value", "field2": "value2" }
 * into a Query object
 */
public class QueryFromJson {
    public static Query get(JsonNode query) {
        Query q = new Query();

        Iterator<String> items = query.fieldNames();
        while ( items.hasNext() ) {
            String key = items.next();
            q.addCriteria(Criteria.where(key).is(query.get(key).asText()));
        }
        return q;
    }
}
