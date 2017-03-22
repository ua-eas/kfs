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
package org.kuali.kfs.sys.dataaccess.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.sys.dataaccess.PreferencesDao;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.data.mongodb.core.query.BasicQuery;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PreferencesDaoMongo implements PreferencesDao {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PreferencesDaoMongo.class);

    public static final String INSTITUTION_PREFERENCES = "InstitutionPreferences";
    public static final String INSTITUTION_ID_KEY = "institutionId";
    public static final String INSTITUTION_PREFERENCES_CACHE = "InstitutionPreferencesCache";
    public static final String INSTITUTION_PREFERENCES_CACHE_LENGTH = "InstitutionPreferencesCacheLength";
    public static final String EXPIRE_INDEX_NAME = "expireIndex";

    public static final String USER_PREFERENCES = "UserPreferences";
    public static final String PRINCIPAL_NAME_KEY = "principalName";
    public static final String PREFERENCES_KEY = "preferences";
    public static final String DATE_KEY = "createdAt";
    public static final String CACHED_KEY = "cached";

    private MongoTemplate mongoTemplate;

    @Override
    public Map<String, Object> findInstitutionPreferences() {
        LOG.debug("findInstitutionPreferences() started");

        Map<String, Object> institutionalPreference = new ConcurrentHashMap<>();
        List<Map> institutionPreferences = mongoTemplate.findAll(Map.class, INSTITUTION_PREFERENCES);

        if (CollectionUtils.isNotEmpty(institutionPreferences)) {
            institutionalPreference = institutionPreferences.get(0);
        }
        return institutionalPreference;
    }

    @Override
    public void saveInstitutionPreferences(String institutionId, Map<String, Object> preferences) {
        LOG.debug("saveInstitutionPreferences started");

        // Delete existing document if any
        mongoTemplate.remove(getInstitutionIdQuery(institutionId), INSTITUTION_PREFERENCES);

        mongoTemplate.save(preferences, INSTITUTION_PREFERENCES);
    }

    public Map<String, Object> findInstitutionPreferencesCache(String principalName) {
        LOG.debug("findInstitutionPreferencesCache() started");

        List<Map> institutionPreferences = mongoTemplate.find(getPrincipalNameQuery(principalName), Map.class, INSTITUTION_PREFERENCES_CACHE);

        if (CollectionUtils.isNotEmpty(institutionPreferences)) {
            return institutionPreferences.get(0);
        }
        return null;
    }

    @Override
    public void cacheInstitutionPreferences(String principalName, Map<String, Object> institutionPreferences) {
        LOG.debug("cacheInstitutionPreferences() started");

        institutionPreferences.put(PRINCIPAL_NAME_KEY, principalName);
        institutionPreferences.put(DATE_KEY, new Date());
        institutionPreferences.put(CACHED_KEY, true);
        institutionPreferences.remove("_id");

        mongoTemplate.remove(getPrincipalNameQuery(principalName), INSTITUTION_PREFERENCES_CACHE);
        mongoTemplate.save(institutionPreferences, INSTITUTION_PREFERENCES_CACHE);
    }

    @Override
    public void clearInstitutionPreferencesCache() {}

    private void dropCacheLengthObject() {
        mongoTemplate.remove(getCacheLengthQuery(), INSTITUTION_PREFERENCES_CACHE_LENGTH);
    }

    private BasicQuery getCacheLengthQuery() {
        return new BasicQuery("{ \"id\" :\"cacheLength\" }");
    }

    private void dropIndexIfExists(String indexName) {
        List<IndexInfo> indexes = mongoTemplate.indexOps(INSTITUTION_PREFERENCES_CACHE).getIndexInfo();
        if (indexes.stream().anyMatch(i -> i.getName().equals(indexName))) {
            mongoTemplate.indexOps(INSTITUTION_PREFERENCES_CACHE).dropIndex(indexName);
        }
        dropCacheLengthObject();
    }

    private void createExpireIndex(String indexName, int seconds) {
        IndexDefinition expireIndex = new IndexDefinition() {
            @Override
            public DBObject getIndexKeys() {
                return new BasicDBObject("createdAt", 1);
            }

            @Override
            public DBObject getIndexOptions() {
                BasicDBObject b = new BasicDBObject("expireAfterSeconds", seconds);
                b.put("name", indexName);
                return b;
            }
        };

        mongoTemplate.indexOps(INSTITUTION_PREFERENCES_CACHE).ensureIndex(expireIndex);

        // Spring doesn't retrieve all index options so we need to save/get the expiration time value of
        // it using another way rather than looking at the index itself.
        CacheLength cl = new CacheLength();
        cl.expireSeconds = seconds;
        mongoTemplate.save(cl, INSTITUTION_PREFERENCES_CACHE_LENGTH);
    }

    @Override
    public Map<String, Object> getUserPreferences(String principalName) {
        LOG.debug("getUserPreferences() started");

        List<Map> documents = mongoTemplate.find(getPrincipalNameQuery(principalName), Map.class, USER_PREFERENCES);

        if (CollectionUtils.isNotEmpty(documents)) {
            Map<String, Object> doc = documents.get(0);

            // Pull out the preferences object from this and return it
            return (Map<String, Object>) doc.get(PREFERENCES_KEY);
        }

        return null;
    }

    @Override
    public void saveUserPreferences(String principalName, String preferences) {
        LOG.debug("saveUserPreferences(String, String) started");

        try {
            ObjectMapper mapper = new ObjectMapper();
            saveUserPreferences(principalName, (Map<String, Object>) mapper.readValue(preferences, Map.class));
        } catch (IOException e) {
            LOG.error("saveUserPreferences() Error parsing json", e);
            throw new RuntimeException("Error parsing json");
        }

    }

    @Override
    public void saveUserPreferences(String principalName, Map<String, Object> preferences) {
        LOG.debug("saveUserPreferences(String, Map) started");

        Map<String, Object> doc = new ConcurrentHashMap<>();
        doc.put(PRINCIPAL_NAME_KEY, principalName);
        doc.put(PREFERENCES_KEY, preferences);

        // Delete existing document if any
        mongoTemplate.remove(getPrincipalNameQuery(principalName), USER_PREFERENCES);

        mongoTemplate.save(doc, USER_PREFERENCES);
    }

    private BasicQuery getPrincipalNameQuery(String principalName) {
        return new BasicQuery("{ " + PRINCIPAL_NAME_KEY + " : \"" + principalName + "\"}");
    }

    private BasicQuery getInstitutionIdQuery(String institutionId) {
        return new BasicQuery("{ " + INSTITUTION_ID_KEY + " : \"" + institutionId + "\"}");
    }

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    class CacheLength {
        public String id = "cacheLength";
        public int expireSeconds;
    }
}

