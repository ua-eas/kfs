package org.kuali.kfs.sys.dataaccess;

import java.util.Map;

public interface PreferencesDao {

    Map<String, Object> findInstitutionPreferences();
    Map<String, Object> findInstitutionPreferencesCache(String principalName);
    void cacheInstitutionPreferences(String principalName, Map<String, Object> institutionPreferences);

    Map<String, Object> getUserPreferences(String principalName);
    void saveUserPreferences(String principalName,String preferences);
}
