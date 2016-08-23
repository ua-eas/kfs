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
package org.kuali.kfs.sys.datatools.businessobject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.Assert;
import org.junit.Test;
import org.kuali.kfs.sys.datatools.liquimongo.businessobject.DocumentStoreChange;

import java.io.IOException;

public class DocumentStoreChangeTest {
    @Test
    public void propertiesArePopulated() throws IOException {
        String changeJson = "{\n" +
            "              \"jira\": \"KFSTP-2186\",\n" +
            "              \"id\": \"add InstitutionPreferences data\",\n" +
            "              \"changes\": [\n" +
            "                  {\n" +
            "                  }\n" +
            "                ]\n" +
            "            }";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode changeNode = mapper.readValue(changeJson, JsonNode.class);

        DocumentStoreChange dsc = new DocumentStoreChange("filename", changeNode);
        Assert.assertEquals("ChangeID is populated", "add InstitutionPreferences data", dsc.getChangeId());
        Assert.assertEquals("Filename is populated", "filename", dsc.getFileName());
        Assert.assertTrue("Hash is populated", dsc.getHash() != null);
    }
}
