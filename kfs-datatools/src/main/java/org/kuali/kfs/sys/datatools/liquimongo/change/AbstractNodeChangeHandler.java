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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import net.minidev.json.JSONArray;

public abstract class AbstractNodeChangeHandler extends AbstractDocumentStoreChangeHandler {

    protected static final String PATH = "path";
    protected static final String VALUE = "value";

    /**
     * Adds a Json node to a given Json array within a document.
     *
     * @param change The Json that holds the change definition.
     * @param documentJson The document to be changed.
     * @param nodeToAdd The Mongo Json node to add.
     * @param pathKey The key within the change Json that will yield a JsonPath expression pointing to the array.
     * @param beforeNodeKey The key within the change Json that will (optionally) yield key-value criteria,
     *        indicating which element in the arry to insert the new node before.
     * @return The Mongo Json representation of the altered document.
     */
    protected DBObject addNode(JsonNode change, String documentJson, DBObject nodeToAdd, String pathKey, String beforeNodeKey) {
        verifyKeyExistence(change,pathKey);
        String revertPath = change.get(pathKey).asText();

        DocumentContext dc = JsonPath.parse(documentJson);
        Object nodeToRestore = findNode(dc, revertPath);
        if (nodeToRestore == null || !(nodeToRestore instanceof JSONArray)) {
            throw new RuntimeException("Revert path must point to an array: " + revertPath);
        }

        JSONArray arrayToRestore = (JSONArray) nodeToRestore;

        boolean changeApplied = false;
        if (change.has(beforeNodeKey)) {
            JsonNode revertBeforeNode = change.get(beforeNodeKey);
            Map<String, Object> conditions = JsonUtils.getMapFromJson(revertBeforeNode);
            for (int i = 0; i < arrayToRestore.size(); i++) {
                Object objectToCheck = arrayToRestore.get(i);
                if (objectToCheck instanceof Map) {
                    Map mapToCheck = (Map) objectToCheck;
                    if (checkConditions(mapToCheck, conditions)) {
                        arrayToRestore.add(i, nodeToAdd);
                        changeApplied = true;
                        break;
                    }
                }
            }
        }
        if (!changeApplied) {
         // Revert before node is optional.  Since it wasn't specified or failed for some other reason
         // (perhaps because of an intervening manual change), add the node at the end of the array.
            arrayToRestore.add(nodeToAdd);
        }

        String newJson = dc.jsonString();
        DBObject result = (DBObject) JSON.parse(newJson);
        return result;
    }

    private boolean checkConditions(Map mapToCheck, Map<String, Object> conditions) {
        for (Entry<String, Object> entry : conditions.entrySet() ) {
            if (!entry.getValue().equals(mapToCheck.get(entry.getKey()))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Finds a node within o document, using a JsonPath expression.
     *
     * @param dc
     * @param path
     * @return
     */
    protected Object findNode(DocumentContext dc, String path) {
        Set<Option> dcOptions = dc.configuration().getOptions();
        dc.configuration().addOptions(Option.ALWAYS_RETURN_LIST, Option.SUPPRESS_EXCEPTIONS);
        List<Object> nodes = dc.read(path);
        dc.configuration().setOptions(dcOptions.toArray(new Option[dcOptions.size()]));
        if (nodes.size() != 1) {
            throw new RuntimeException("Cannot find unique node: " + path);
        }
        return nodes.get(0);
    }
}
