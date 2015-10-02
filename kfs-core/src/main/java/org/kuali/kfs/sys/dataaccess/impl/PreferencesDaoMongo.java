package org.kuali.kfs.sys.dataaccess.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.sys.dataaccess.PreferencesDao;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PreferencesDaoMongo implements PreferencesDao {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PreferencesDaoMongo.class);

    public static final String INSTITUTION_PREFERENCES = "InstitutionPreferences";
    public static final String INSTITUTION_ID_KEY = "institutionId";

    public static final String USER_PREFERENCES = "UserPreferences";
    public static final String PRINCIPAL_NAME_KEY = "principalName";
    public static final String PREFERENCES_KEY = "preferences";

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

    @Override
    public Map<String, Object> getUserPreferences(String principalName) {
        LOG.debug("getUserPreferences() started");

        List<Map> documents = mongoTemplate.find(getPrincipalNameQuery(principalName), Map.class, USER_PREFERENCES);

        if (CollectionUtils.isNotEmpty(documents)) {
            Map<String, Object> doc = documents.get(0);

            // Pull out the preferences object from this and return it
            return (Map<String, Object>)doc.get(PREFERENCES_KEY);
        }

        return null;
    }

    @Override
    public void saveUserPreferences(String principalName, String preferences) {
        LOG.debug("saveUserPreferences() started");

        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> doc = new ConcurrentHashMap<>();
        doc.put(PRINCIPAL_NAME_KEY,principalName);
        try {
            doc.put(PREFERENCES_KEY, mapper.readValue(preferences,Map.class));
        } catch (IOException e) {
            LOG.error("saveUserPreferences() Error parsing json",e);
            throw new RuntimeException("Error parsing json");
        }

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
}
