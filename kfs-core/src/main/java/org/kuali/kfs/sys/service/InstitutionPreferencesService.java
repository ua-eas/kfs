package org.kuali.kfs.sys.service;

import org.kuali.rice.kim.api.identity.Person;

import java.util.List;
import java.util.Map;

public interface InstitutionPreferencesService {
    Map<String, Object> findInstitutionPreferencesLinks(Person person, boolean useCache);
    Map<String, Object> findInstitutionPreferencesNoLinks();
    void saveInstitutionPreferences(String institutionId, String linkGroups);
    Map<String,Object> getAllLinkGroups();
    List<Map<String, String>> getMenu();
    List<Map<String, String>> saveMenu(String menu);
    void setInstitutionPreferencesCacheLength(int seconds);
    int getInstitutionPreferencesCacheLength();
    boolean hasConfigurationPermission(String principalName);
}
