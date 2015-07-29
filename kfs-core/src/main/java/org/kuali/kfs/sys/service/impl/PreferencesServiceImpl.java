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

    protected void transformLinks(Map<String, Object> institutionPreferences) {
        for(Map<String, Object> linkGroup : getLinkGroups(institutionPreferences)) {
            transformLinksInLinkGroup(linkGroup);
        }
    }

    protected List<Map<String, Object>> getLinkGroups(Map<String, Object> institutionPreferences) {
        return (List<Map<String, Object>>)institutionPreferences.get("linkGroups");
    }

    protected void transformLinksInLinkGroup(Map<String, Object> linkGroup) {
        for (Map<String, String> link : getLinks(linkGroup)) {
            transformLink(link);
        }
    }

    protected List<Map<String, String>> getLinks(Map<String, Object> linkGroup) {
        return (List<Map<String, String>>)linkGroup.get("links");
    }

    private void transformLink(Map<String, String> link) {
        if (link.containsKey("documentTypeCode")) {
            final String documentTypeName = link.remove("documentTypeCode");
            final Map<String, String> linkInfo = determineLinkInfo(documentTypeName);
            link.put("label", linkInfo.get("label"));
            link.put("link", linkInfo.get("link"));
        }
    }

    protected Map<String, String> determineLinkInfo(String documentTypeName) {
        final String label = getDocumentDictionaryService().getLabel(documentTypeName);
        // "InternalBillingDocument" -> "http://tst.kfs.kuali.org/kfs-tst/financialInternalBilling.do?methodToCall=docHandler&command=initiate&docTypeName=IB"

        Map<String, String> linkInfo = new ConcurrentHashMap<>();
        linkInfo.put("label", label);
        return linkInfo;
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
