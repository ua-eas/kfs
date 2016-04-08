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
package org.kuali.kfs.sys.datatools.liquimongo.businessobject;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.codec.binary.Hex;
import org.springframework.data.annotation.Transient;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class DocumentStoreChange {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentStoreChange.class);

    private String fileName;
    private Date changeDate;
    private String changeId;
    private String hash;

    @Transient
    private JsonNode changeNode;

    public DocumentStoreChange() {
        changeDate = new Date();
    }

    public DocumentStoreChange(String fileName, JsonNode node) {
        this();
        this.fileName = fileName;
        this.changeNode = node;
        this.changeId = node.get("id").asText();
        this.hash = calculateHash();
    }

    private String calculateHash() {
        try {
            byte[] bytesOfMessage = changeNode.toString().getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] md5hash = md.digest(bytesOfMessage);
            return Hex.encodeHexString(md5hash);
        } catch (NoSuchAlgorithmException|UnsupportedEncodingException e) {
            LOG.error("calculateHash() Unable to md5 hash",e);
            throw new UnsupportedOperationException("Unable to hash change");
        }
    }

    public List<JsonNode> getAllChanges() {
        List<JsonNode> changeList = new ArrayList<>();

        JsonNode changes = changeNode.get("changes");
        Iterator<JsonNode> items = changes.elements();
        while (items.hasNext()) {
            changeList.add(items.next());
        }
        return changeList;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public String getChangeId() {
        return changeId;
    }

    public void setChangeId(String changeId) {
        this.changeId = changeId;
    }

    public JsonNode getChangeNode() {
        return changeNode;
    }

    public void setChangeNode(JsonNode changeNode) {
        this.changeNode = changeNode;
    }

    @Override
    public String toString() {
        return "DocumentStoreChange{" +
                "changeId='" + changeId + '\'' +
                ", fileName='" + fileName + '\'' +
                ", changeDate=" + changeDate +
                ", hash=" + hash +
                '}';
    }
}
