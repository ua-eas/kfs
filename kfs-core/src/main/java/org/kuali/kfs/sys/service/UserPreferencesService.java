package org.kuali.kfs.sys.service;

import java.util.Map;

public interface UserPreferencesService {

    Map<String, Object> getUserPreferences(String username);
    void saveUserPreferences(String principalName,String preferences);
    void saveUserPreferencesKey(String principalName,String key,String preferences);
}
