package org.kuali.kfs.sys.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.kuali.rice.kim.api.identity.Person;

import java.util.Map;

public interface PreferencesService {

    Map<String, Object> findInstitutionPreferences(Person person);
    Map<String, Object> getUserPreferences(String username);
    void saveUserPreferences(String principalName,String preferences);
    void saveUserPreferencesKey(String principalName,String key,String preferences);
}
