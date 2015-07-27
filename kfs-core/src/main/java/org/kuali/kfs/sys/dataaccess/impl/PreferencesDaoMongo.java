package org.kuali.kfs.sys.dataaccess.impl;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.sys.dataaccess.PreferencesDao;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PreferencesDaoMongo implements PreferencesDao {

    private MongoTemplate mongoTemplate;

    @Override
    public Map<String, Object> findInstitutionPreferences() {
        Map<String, Object> institutionalPreference = new ConcurrentHashMap<>();
        List<Map> institutionPreferences = mongoTemplate.findAll(Map.class, "InstitutionPreferences");

        if (CollectionUtils.isNotEmpty(institutionPreferences)) {
            institutionalPreference = institutionPreferences.get(0);
        }
        return institutionalPreference;
    }

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

}
