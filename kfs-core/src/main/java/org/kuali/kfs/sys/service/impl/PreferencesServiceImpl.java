package org.kuali.kfs.sys.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.dataaccess.PreferencesDao;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.kfs.sys.service.PreferencesService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.kfs.krad.bo.GlobalBusinessObject;
import org.kuali.kfs.krad.bo.ModuleConfiguration;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.document.TransactionalDocument;
import org.kuali.kfs.krad.maintenance.MaintenanceDocument;
import org.kuali.kfs.krad.service.DocumentDictionaryService;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@NonTransactional
public class PreferencesServiceImpl implements PreferencesService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PreferencesServiceImpl.class);

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
        LOG.debug("findInstitutionPreferences() started");

        final Map<String, Object> institutionPreferences = preferencesDao.findInstitutionPreferences();

        appendMenuProperties(institutionPreferences);
        transformLinks(institutionPreferences);

        return institutionPreferences;
    }

    @Override
    public Map<String, Object> getUserPreferences(String principalName) {
        LOG.debug("getUserPreferences() started");

        return preferencesDao.getUserPreferences(principalName);
    }

    @Override
    public void saveUserPreferences(String principalName, String preferences) {
        LOG.debug("saveUserPreferences() started");

        preferencesDao.saveUserPreferences(principalName, preferences);
    }

    @Override
    public void saveUserPreferencesKey(String principalName, String key, String preferences) {
        LOG.debug("saveUserPreferencesKey() started");

        Map<String, Object> userPrefs = getUserPreferences(principalName);
        if ( userPrefs == null ) {
            userPrefs = new ConcurrentHashMap<>();
        }
        userPrefs.put(key, preferences);

        ObjectMapper mapper = new ObjectMapper();
        try {
            saveUserPreferences(principalName, mapper.writeValueAsString(userPrefs));
        } catch (JsonProcessingException e) {
            LOG.error("saveUserPreferencesKey() Error processing json",e);
            throw new RuntimeException("Error processing json");
        }
    }

    protected void appendMenuProperties(Map<String, Object> institutionPreferences) {
        appendActionListUrl(institutionPreferences);
        appendDocSearchUrl(institutionPreferences);
        appendSignoutUrl(institutionPreferences);
    }

    protected void appendActionListUrl(Map<String, Object> institutionPreferences) {
        final String actionListUrl = configurationService.getPropertyValueAsString(KRADConstants.WORKFLOW_URL_KEY)+"/ActionList.do";
        institutionPreferences.put("actionListUrl", actionListUrl);
    }

    protected void appendSignoutUrl(Map<String, Object> institutionPreferences) {
        final String signoutUrl = configurationService.getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY)+"/logout.do";
        institutionPreferences.put("signoutUrl", signoutUrl);
    }

    protected void appendDocSearchUrl(Map<String, Object> institutionPreferences) {
        final String docSearchUrl = configurationService.getPropertyValueAsString(KRADConstants.WORKFLOW_URL_KEY)+"/DocumentSearch.do?docFormKey=88888888&hideReturnLink=true&returnLocation=" + configurationService.getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY) + "/index.jsp";
        institutionPreferences.put("docSearchUrl", docSearchUrl);
    }

    protected void transformLinks(Map<String, Object> institutionPreferences) {
        for(Map<String, Object> linkGroup : getLinkGroups(institutionPreferences)) {
            transformLinksInLinkGroup(linkGroup);
        }

        for (Map<String, String> menuItem: getMenuItems(institutionPreferences)) {
            if (StringUtils.isNotBlank(menuItem.get("link"))) {
                menuItem.put("link", fixRelativeLink(menuItem.get("link")));
            }
        }
    }

    protected List<Map<String, Object>> getLinkGroups(Map<String, Object> institutionPreferences) {
        final List<Map<String, Object>> linkGroups = (List<Map<String, Object>>)institutionPreferences.get("linkGroups");
        if (!ObjectUtils.isNull(linkGroups)) {
            return linkGroups;
        }
        return new ArrayList<>();
    }

    protected List<Map<String, String>> getMenuItems(Map<String, Object> institutionPreferences) {
        List<Map<String, String>> menuItems = (List<Map<String, String>>)institutionPreferences.get("menu");
        if (!ObjectUtils.isNull(menuItems)) {
            return menuItems;
        }
        return new ArrayList<>();
    }

    protected void transformLinksInLinkGroup(Map<String, Object> linkGroup) {
        List<Map<String,String>> updatedLinks = getLinks(linkGroup).stream().map((Map<String, String> link) -> {
            return transformLink(link);
        }).filter((Map<String, String> link) -> {
            return link.containsKey("label") && !StringUtils.isBlank(link.get("label")) && link.containsKey("link") && !StringUtils.isBlank(link.get("link"));
        }).collect(Collectors.toList());
        linkGroup.put("links", updatedLinks);
    }

    protected List<Map<String, String>> getLinks(Map<String, Object> linkGroup) {
        return (List<Map<String, String>>)linkGroup.get("links");
    }

    protected Map<String, String> transformLink(Map<String, String> link) {
        if (link.containsKey("documentTypeCode")) {
            final String documentTypeName = link.remove("documentTypeCode");
            final Map<String, String> linkInfo = determineLinkInfo(documentTypeName);
            return linkInfo;
        } else if (StringUtils.isNotBlank(link.get("link"))) {
            Map<String, String> newLink = new ConcurrentHashMap<>();
            newLink.put("link", fixRelativeLink(link.get("link")));
            newLink.put("label", link.get("label"));
            return newLink;
        }
        return new ConcurrentHashMap<>();
    }

    protected String fixRelativeLink(String link) {
        if (!link.startsWith("http")) {
            final String applicationUrl = configurationService.getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY);
            link = applicationUrl + "/" + link;
        }
        return link;
    }

    protected Map<String, String> determineLinkInfo(String documentTypeName) {
        final String label = documentDictionaryService.getLabel(documentTypeName);
        final Class<? extends Document> documentClass = (Class<? extends Document>)documentDictionaryService.getDocumentClassByName(documentTypeName);
        String link = StringUtils.EMPTY;
        if (!ObjectUtils.isNull(documentClass)) {
            if (TransactionalDocument.class.isAssignableFrom(documentClass)) {
                link = constructTransactionalDocumentLinkFromClass(documentClass, documentTypeName);
            } else if (MaintenanceDocument.class.isAssignableFrom(documentClass)) {
                link = constructMaintenanceDocumentLinkFromClass(documentTypeName);
            }
        }
        return constructLinkInfo(label, link);
    }

    protected String constructTransactionalDocumentLinkFromClass(Class<? extends Document> documentClass, String documentTypeName) {
        final String applicationUrl = configurationService.getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY);
        return applicationUrl + "/" + determineUrlNameForClass(documentClass) + transformClassName(documentClass) + ".do?methodToCall=docHandler&command=initiate&docTypeName="+documentTypeName;
    }

    protected String constructMaintenanceDocumentLinkFromClass(String documentTypeName) {
        final String applicationUrl = configurationService.getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY);
        final Class<?> businessObjectClass = documentDictionaryService.getMaintenanceDataObjectClass(documentTypeName);
        if (GlobalBusinessObject.class.isAssignableFrom(businessObjectClass)) {
            return applicationUrl + "/kr/maintenance.do?methodToCall=start&businessObjectClassName=" + businessObjectClass.getName() + "&hideReturnLink=true";
        }
        return applicationUrl + "/kr/lookup.do?methodToCall=start&businessObjectClassName=" + businessObjectClass.getName() + "&docFormKey=88888888&returnLocation=" + applicationUrl + "/index.jsp&hideReturnLink=true";
    }

    protected String transformClassName(Class<? extends Document> documentClass) {
        return documentClass.getSimpleName().replace("Document", "");
    }

    protected String determineUrlNameForClass(Class<? extends Document> documentClass) {
        final ModuleService module = kualiModuleService.getResponsibleModuleService(documentClass);
        if (!ObjectUtils.isNull(module)) {
            final ModuleConfiguration moduleConfiguration = module.getModuleConfiguration();
            return lookupUrlNameFromNamespace(moduleConfiguration.getNamespaceCode());
        }
        return "";
    }

    protected String lookupUrlNameFromNamespace(String namespaceCode) {
        return namespaceCodeToUrlName.get(namespaceCode);
    }

    protected Map<String, String> constructLinkInfo(String label, String link) {
        Map<String, String> linkInfo = new ConcurrentHashMap<>();
        if (!StringUtils.isBlank(label)) {
            linkInfo.put("label", label);
        }
        if (!StringUtils.isBlank(link)) {
            linkInfo.put("link", link);
        }
        return linkInfo;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public void setDocumentDictionaryService(DocumentDictionaryService documentDictionaryService) {
        this.documentDictionaryService = documentDictionaryService;
    }

    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }

    public void setPreferencesDao(PreferencesDao preferencesDao) {
        this.preferencesDao = preferencesDao;
    }

    public void setNamespaceCodeToUrlName(Map<String, String> namespaceCodeToUrlName) {
        this.namespaceCodeToUrlName = namespaceCodeToUrlName;
    }
}
