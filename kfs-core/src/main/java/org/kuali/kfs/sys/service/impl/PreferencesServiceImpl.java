package org.kuali.kfs.sys.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.dataaccess.PreferencesDao;
import org.kuali.kfs.sys.service.PreferencesService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.bo.ModuleConfiguration;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentDictionaryService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PreferencesServiceImpl implements PreferencesService {

    private ConfigurationService configurationService;
    private DocumentDictionaryService documentDictionaryService;
    private KualiModuleService kualiModuleService;
    private PreferencesDao preferencesDao;

    private Map<String, String> namespaceCodeToUrlName;

    public PreferencesServiceImpl() {
        // initiate defaults for namespaceCodeToUrlName map
        namespaceCodeToUrlName = new ConcurrentHashMap<>();
        namespaceCodeToUrlName.put("KFS-AR", "ar");
        namespaceCodeToUrlName.put("KFS-CAM", "cams");
        namespaceCodeToUrlName.put("KFS-CAB", "cab");
        namespaceCodeToUrlName.put("KFS-FP", "financial");
        namespaceCodeToUrlName.put("KFS-GL", "generalLedger");
        namespaceCodeToUrlName.put("KFS-LD", "labor");
        namespaceCodeToUrlName.put("KFS-BC", "budget");
        namespaceCodeToUrlName.put("KFS-PURAP", "purap");
        namespaceCodeToUrlName.put("KFS-CG", "cg");
        namespaceCodeToUrlName.put("KFS-EC", "effort");
        namespaceCodeToUrlName.put("KFS-TEM", "tem");
    }

    @Override
    public Map<String, Object> findInstitutionPreferences() {
        final Map<String, Object> institutionPreferences = getPreferencesDao().findInstitutionPreferences();

        transformLinks(institutionPreferences);
        
        return institutionPreferences;
    }

    protected void transformLinks(Map<String, Object> institutionPreferences) {
        for(Map<String, Object> linkGroup : getLinkGroups(institutionPreferences)) {
            transformLinksInLinkGroup(linkGroup);
        }
    }

    protected List<Map<String, Object>> getLinkGroups(Map<String, Object> institutionPreferences) {
        final List<Map<String, Object>> linkGroups = (List<Map<String, Object>>)institutionPreferences.get("linkGroups");
        if (!ObjectUtils.isNull(linkGroups)) {
            return linkGroups;
        }
        return new ArrayList<>();
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
        final Class<? extends Document> documentClass = (Class<? extends Document>)getDocumentDictionaryService().getDocumentClassByName(documentTypeName);
        final String link = constructTransactionalDocumentLinkFromClass(documentClass, documentTypeName);

        return constructLinkInfo(label, link);
    }

    protected String constructTransactionalDocumentLinkFromClass(Class<? extends Document> documentClass, String documentTypeName) {
        final String applicationUrl = getConfigurationService().getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY);
        return applicationUrl + determineUrlNameForClass(documentClass) + transformClassName(documentClass) + ".do?methodToCall=docHandler&command=initiate&docTypeName="+documentTypeName;
    }

    protected String transformClassName(Class<? extends Document> documentClass) {
        return documentClass.getSimpleName().replace("Document", "");
    }

    protected String determineUrlNameForClass(Class<? extends Document> documentClass) {
        final ModuleService module = getKualiModuleService().getResponsibleModuleService(documentClass);
        if (!ObjectUtils.isNull(module)) {
            final ModuleConfiguration moduleConfiguration = module.getModuleConfiguration();
            return lookupUrlNameFromNamespace(moduleConfiguration.getNamespaceCode());
        }
        return "";
    }

    protected String lookupUrlNameFromNamespace(String namespaceCode) {
        return getNamespaceCodeToUrlName().get(namespaceCode);
    }

    protected Map<String, String> constructLinkInfo(String label, String link) {
        Map<String, String> linkInfo = new ConcurrentHashMap<>();
        linkInfo.put("label", label);
        linkInfo.put("link", link);
        return linkInfo;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public DocumentDictionaryService getDocumentDictionaryService() {
        return documentDictionaryService;
    }

    public void setDocumentDictionaryService(DocumentDictionaryService documentDictionaryService) {
        this.documentDictionaryService = documentDictionaryService;
    }

    public KualiModuleService getKualiModuleService() {
        return kualiModuleService;
    }

    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }

    public PreferencesDao getPreferencesDao() {
        return preferencesDao;
    }

    public void setPreferencesDao(PreferencesDao preferencesDao) {
        this.preferencesDao = preferencesDao;
    }

    public Map<String, String> getNamespaceCodeToUrlName() {
        return namespaceCodeToUrlName;
    }

    public void setNamespaceCodeToUrlName(Map<String, String> namespaceCodeToUrlName) {
        this.namespaceCodeToUrlName = namespaceCodeToUrlName;
    }
}
