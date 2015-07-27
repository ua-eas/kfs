package org.kuali.kfs.sys.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.sys.dataaccess.PreferencesDao;
import org.kuali.kfs.sys.service.PreferencesService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PreferencesServiceImpl implements PreferencesService {

    private PreferencesDao preferencesDao;

    @Override
    public Map<String, Object> findInstitutionPreferences() {
        return preferencesDao.findInstitutionPreferences();
    }

    public PreferencesDao getPreferencesDao() {
        return preferencesDao;
    }

    public void setPreferencesDao(PreferencesDao preferencesDao) {
        this.preferencesDao = preferencesDao;
    }
}
