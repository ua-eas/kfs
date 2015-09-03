package org.kuali.kfs.sys.dataaccess;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

public interface PreferencesDao {

    Map<String, Object> findInstitutionPreferences();
    Map<String, Object> getUserPreferences(String principalName);
    void saveUserPreferences(String principalName,String preferences);
}
