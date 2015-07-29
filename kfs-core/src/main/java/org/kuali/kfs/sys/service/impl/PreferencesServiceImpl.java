package org.kuali.kfs.sys.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.sys.dataaccess.PreferencesDao;
import org.kuali.kfs.sys.service.PreferencesService;
import org.kuali.rice.krad.service.DocumentDictionaryService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PreferencesServiceImpl implements PreferencesService {

    private DocumentDictionaryService documentDictionaryService;
    private PreferencesDao preferencesDao;

    @Override
    public Map<String, Object> findInstitutionPreferences() {
        final Map<String, Object> institutionPreferences = preferencesDao.findInstitutionPreferences();

        
        return institutionPreferences;
    }


    public DocumentDictionaryService getDocumentDictionaryService() {
        return documentDictionaryService;
    }

    public void setDocumentDictionaryService(DocumentDictionaryService documentDictionaryService) {
        this.documentDictionaryService = documentDictionaryService;
    }

    public PreferencesDao getPreferencesDao() {
        return preferencesDao;
    }

    public void setPreferencesDao(PreferencesDao preferencesDao) {
        this.preferencesDao = preferencesDao;
    }
}
