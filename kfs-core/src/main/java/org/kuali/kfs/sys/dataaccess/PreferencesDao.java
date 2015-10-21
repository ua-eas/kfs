package org.kuali.kfs.sys.dataaccess;

import java.util.Map;

public interface PreferencesDao {

    Map<String, Object> findInstitutionPreferences();
    void saveInstitutionPreferences(String institutionId, Map<String, Object> preferences);
    Map<String, Object> getUserPreferences(String principalName);
    void saveUserPreferences(String principalName,String preferences);
}
