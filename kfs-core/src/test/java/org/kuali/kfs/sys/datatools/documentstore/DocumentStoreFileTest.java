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
package org.kuali.kfs.sys.datatools.documentstore;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.kuali.kfs.sys.datatools.util.ResourceLoaderUtil;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DocumentStoreFileTest {
    public static final String FILE_PACKAGE = "/org/kuali/kfs/core/documentstore/";
    public static final String UPDATES_FILE = "updates.json";

    @Test
    public void testUpdatesFileFormat() throws IOException {
        JsonNode rootNode = getJson(FILE_PACKAGE + UPDATES_FILE);

        JsonNode arrayOfChanges = rootNode.get("updateFiles");
        Assert.assertNotNull("updates.json must contain array named updateFiles", arrayOfChanges);

        Assert.assertTrue("updateFiles must be an array", arrayOfChanges.isArray());

        Iterator<String> fields = rootNode.fieldNames();
        while (fields.hasNext()) {
            String field = fields.next();
            if (!"updateFiles".equals(field)) {
                Assert.fail("updates.json must contain only one array named updateFiles");
            }
        }

        Iterator<JsonNode> nodes = arrayOfChanges.elements();
        while (nodes.hasNext()) {
            JsonNode child = nodes.next();
            Assert.assertTrue("Children of updateFiles must be strings", child.isTextual());
        }
    }

    @Test
    public void testAllUpdatesValid() throws IOException {
        List<String> updateFiles = getUpdateFiles(FILE_PACKAGE + UPDATES_FILE);
        updateFiles.stream().forEach(uf -> {
            try {
                JsonNode ufNode = getJson(FILE_PACKAGE + uf);
                verifyUpdateFileFormat(uf, ufNode);
                Assert.assertNotNull("Unable to read update file " + uf, ufNode);
            } catch (IOException e) {
                Assert.fail("Unable to read update file " + uf);
            }
        });
    }

    private void verifyUpdateFileFormat(String fileName, JsonNode ufNode) {
        JsonNode changeLog = ufNode.get("changeLog");
        Assert.assertNotNull(fileName + " must contain object named changeLog", changeLog);

        Assert.assertTrue(fileName + " must be an array", changeLog.isArray());

        Iterator<String> fields = ufNode.fieldNames();
        while (fields.hasNext()) {
            String field = fields.next();
            if (!"changeLog".equals(field)) {
                Assert.fail("updates.json must contain only one array named updateFiles");
            }
        }

        Iterator<JsonNode> changes = changeLog.elements();
        while (changes.hasNext()) {
            JsonNode changeLogItem = changes.next();
            Assert.assertTrue("ChangeLog item must be an object", changeLogItem.isObject());

            Iterator<String> keys = changeLogItem.fieldNames();
            while (keys.hasNext()) {
                String key = keys.next();
                if ("jira".equals(key)) {
                    JsonNode data = changeLogItem.get(key);
                    Assert.assertTrue("jira value in change log item must be a string in " + fileName, data.isTextual());
                } else if ("id".equals(key)) {
                    JsonNode data = changeLogItem.get(key);
                    Assert.assertTrue("id value in change log item must be a string in " + fileName, data.isTextual());
                } else if ("changes".equals(key)) {
                    JsonNode data = changeLogItem.get(key);
                    Assert.assertTrue("changes value in change log item must be an array in " + fileName, data.isArray());
                    verifyChangesArray(fileName, data);
                } else {
                    Assert.fail("Unknown key (" + key + ") in change log item in " + fileName);
                }
            }
        }
    }

    private void verifyChangesArray(String fileName, JsonNode data) {
        Iterator<JsonNode> attributes = data.elements();
        while (attributes.hasNext()) {
            JsonNode attribute = attributes.next();
            verifyChange(fileName, attribute);
        }
    }

    private void verifyChange(String fileName, JsonNode attribute) {
        Assert.assertTrue("Change must be an object in " + fileName, attribute.isObject());
        JsonNode data = attribute.get("changeType");
        Assert.assertNotNull("changeType must exist in a change in " + fileName, data);
        Assert.assertTrue("changeType must be a string in " + fileName, data.isTextual());
    }

    private JsonNode getJson(String resourceName) throws IOException {
        Resource resource = ResourceLoaderUtil.getFileResource(resourceName);
        if (resource == null) {
            // There are no updates
            return null;
        }

        InputStream is = resource.getInputStream();
        if (is == null) {
            Assert.fail("Unable to read updates file");
        }

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(is, JsonNode.class);
    }

    private List<String> getUpdateFiles(String listFile) throws IOException {
        List<String> files = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        JsonNode rootNode = getJson(listFile);
        if (rootNode == null) {
            return files;
        }

        JsonNode arrayOfChanges = rootNode.get("updateFiles");

        Iterator<JsonNode> items = arrayOfChanges.elements();
        while (items.hasNext()) {
            JsonNode item = items.next();
            String filename = item.asText();
            files.add(filename);
        }
        return files;
    }
}
