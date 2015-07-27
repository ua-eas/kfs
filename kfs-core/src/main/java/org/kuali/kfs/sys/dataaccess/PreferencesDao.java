package org.kuali.kfs.sys.dataaccess;

import java.util.Map;

public interface PreferencesDao {

    Map<String, Object> findInstitutionPreferences();

}
