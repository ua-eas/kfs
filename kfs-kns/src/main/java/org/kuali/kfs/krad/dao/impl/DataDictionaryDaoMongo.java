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

package org.kuali.kfs.krad.dao.impl;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.krad.dao.DataDictionaryDao;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Map;

public class DataDictionaryDaoMongo implements DataDictionaryDao {
    private static final String BUSINESS_OBJECT_ENTRIES_COLLECTION = "businessObjectMetadata";
    private static final String BUSINESS_OBJECT_ENTRY_CLASS_NAME_KEY = "businessObjectClassName";
    private static final String DOCUMENT_ENTRIES_COLLECTION = "documentMetadata";
    private static final String DOCUMENT_ENTRY_CLASS_NAME_KEY = "documentClassName";
    private static final String DOCUMENT_ENTRY_WORKFLOW_NAME_KEY = "workflowTypeName";

    private MongoTemplate mongoTemplate;

    @Override
    public Map<String, Object> retrieveBusinessObjectEntry(String className) {
        List<Map> businessObjectEntries = mongoTemplate.find(buildQueryForBusinessObjectEntry(className), Map.class, BUSINESS_OBJECT_ENTRIES_COLLECTION);
        if (!CollectionUtils.isEmpty(businessObjectEntries)) {
            return businessObjectEntries.get(0);
        }
        return null;
    }

    protected Query buildQueryForBusinessObjectEntry(String className) {
        return new BasicQuery("{ " + BUSINESS_OBJECT_ENTRY_CLASS_NAME_KEY + " : \"" + className + "\"}");
    }

    @Override
    public Map<String, Object> retrieveDocumentEntryByClassname(String className) {
        List<Map> documentEntries = mongoTemplate.find(buildQueryForDocumentEntry(DOCUMENT_ENTRY_CLASS_NAME_KEY, className), Map.class, DOCUMENT_ENTRIES_COLLECTION);
        if (!CollectionUtils.isEmpty(documentEntries)) {
            return documentEntries.get(0);
        }
        return null;
    }

    @Override
    public Map<String, Object> retrieveDocumentEntryByType(String type) {
        List<Map> documentEntries = mongoTemplate.find(buildQueryForDocumentEntry(DOCUMENT_ENTRY_WORKFLOW_NAME_KEY, type), Map.class, DOCUMENT_ENTRIES_COLLECTION);
        if (!CollectionUtils.isEmpty(documentEntries)) {
            return documentEntries.get(0);
        }
        return null;
    }

    protected Query buildQueryForDocumentEntry(String collection, String className) {
        return new BasicQuery("{ " + collection + " : \"" + className + "\"}");
    }

    @Override
    public void saveBusinessObjectEntry(Map<String, Object> businessObjectEntry) {
        mongoTemplate.save(businessObjectEntry, BUSINESS_OBJECT_ENTRIES_COLLECTION);
    }

    @Override
    public void saveDocumentEntry(Map<String, Object> documentEntry) {
        mongoTemplate.save(documentEntry, DOCUMENT_ENTRIES_COLLECTION);
    }

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
}
