package org.kuali.kfs.sys.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.kns.datadictionary.BusinessObjectEntry;
import org.kuali.kfs.kns.service.DataDictionaryService;
import org.kuali.kfs.krad.bo.GlobalBusinessObject;
import org.kuali.kfs.krad.bo.ModuleConfiguration;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.document.DocumentAuthorizer;
import org.kuali.kfs.krad.document.TransactionalDocument;
import org.kuali.kfs.krad.maintenance.MaintenanceDocument;
import org.kuali.kfs.krad.service.DocumentDictionaryService;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.KRADUtils;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.dataaccess.PreferencesDao;
import org.kuali.kfs.sys.service.InstitutionPreferencesService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.permission.PermissionService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Transactional
public class InstitutionPreferencesServiceImpl implements InstitutionPreferencesService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(InstitutionPreferencesServiceImpl.class);

    private ConfigurationService configurationService;
    private DocumentDictionaryService documentDictionaryService;
    private KualiModuleService kualiModuleService;
    private PreferencesDao preferencesDao;
    private PermissionService permissionService;
    private IdentityService identityService;
    private DataDictionaryService dataDictionaryService;
    private DocumentTypeService documentTypeService;

    private Map<String, String> namespaceCodeToUrlName;

    private static final int LOGO_HEIGHT = 70;
    private static final int MAX_LOGO_SIZE_KB = 100;

    public InstitutionPreferencesServiceImpl() {
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
    public Map<String, Object> findInstitutionPreferencesNoLinks() {
        LOG.debug("findInstitutionPreferencesNoLinks() started");

        final Map<String, Object> institutionPreferences = preferencesDao.findInstitutionPreferences();
        appendMenuProperties(institutionPreferences);
        institutionPreferences.remove(KFSPropertyConstants.LINK_GROUPS);

        return institutionPreferences;
    }

    @Override
    public void saveInstitutionPreferences(String institutionId, String linkGroupsString) {
        LOG.debug("saveInstitutionPreferences started");

        ObjectMapper mapper = new ObjectMapper();

        List<Map<String, Object>> linkGroups;
        try {
            linkGroups = mapper.readValue(linkGroupsString, List.class);
        } catch (IOException e) {
            LOG.error("saveInstitutionPreferences Error parsing json", e);
            throw new RuntimeException("Error parsing json");
        }

        linkGroups = removeGeneratedLabels(linkGroups);

        Map<String, Object> preferences = preferencesDao.findInstitutionPreferences();
        preferences.put(KFSPropertyConstants.LINK_GROUPS, linkGroups);

        preferencesDao.saveInstitutionPreferences(institutionId, preferences);
    }

    @Override
    public Map<String, Object> findInstitutionPreferencesLinks(Person person, boolean useCache) {
        LOG.debug("findInstitutionPreferencesLinks() started");

        if ( useCache ) {
            Map<String, Object> preferences = preferencesDao.findInstitutionPreferencesCache(person.getPrincipalName());
            if ( preferences != null ) {
                return preferences;
            }
        }

        Map<String, Object> preferences = preferencesDao.findInstitutionPreferences();
        linkPermissionCheck(preferences, person);
        transformLinks(preferences, person);

        preferencesDao.cacheInstitutionPreferences(person.getPrincipalName(), preferences);
        return preferences;
    }

    @Override
    public void setInstitutionPreferencesCacheLength(int seconds) {
        LOG.debug("setInstitutionPreferencesCacheLength() started");

        preferencesDao.setInstitutionPreferencesCacheLength(seconds);
    }

    @Override
    public int getInstitutionPreferencesCacheLength() {
        LOG.debug("getInstitutionPreferencesCacheLength() started");

        int i = preferencesDao.getInstitutionPreferencesCacheLength();
        LOG.error("getInstitutionPreferencesCacheLength() " + i);
        return i;
    }

    /**
     * This removes links from inititutionPreferences that have a permission
     * specified and the user does not have the permission
     * @param institutionPreferences institution preferences
     * @param person person
     */
    private void linkPermissionCheck(Map<String, Object> institutionPreferences, Person person) {
        getLinkGroups(institutionPreferences).forEach(linkGroup -> {
            Map<String, List<Map<String, Object>>> links = (Map<String, List<Map<String, Object>>>) linkGroup.get(KFSPropertyConstants.LINKS);
            links.replaceAll((String key, List<Map<String, Object>> value) -> linkPermissionCheckByType(value, person));
        });
    }

    protected List<Map<String, Object>> linkPermissionCheckByType(List<Map<String, Object>> links, Person person) {
        return links.stream()
                .filter(link -> (link.get(KFSPropertyConstants.PERMISSION) == null) || canViewLink((Map<String, Object>) link.get(KFSPropertyConstants.PERMISSION), person))
                .collect(Collectors.toList());
    }

    protected void appendMenuProperties(Map<String, Object> institutionPreferences) {
        appendActionListUrl(institutionPreferences);
        appendDocSearchUrl(institutionPreferences);
        appendSignoutUrl(institutionPreferences);
    }

    protected void appendActionListUrl(Map<String, Object> institutionPreferences) {
        final String actionListUrl = configurationService.getPropertyValueAsString(KRADConstants.WORKFLOW_URL_KEY)+"/ActionList.do";
        institutionPreferences.put(KFSPropertyConstants.ACTION_LIST_URL, actionListUrl);
    }

    protected void appendSignoutUrl(Map<String, Object> institutionPreferences) {
        final String signoutUrl = configurationService.getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY)+"/logout.do";
        institutionPreferences.put(KFSPropertyConstants.SIGNOUT_URL, signoutUrl);
    }

    protected void appendDocSearchUrl(Map<String, Object> institutionPreferences) {
        final String docSearchUrl = configurationService.getPropertyValueAsString(KRADConstants.WORKFLOW_URL_KEY)+"/DocumentSearch.do?docFormKey=88888888&hideReturnLink=true&returnLocation=" + configurationService.getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY) + "/index.jsp";
        institutionPreferences.put(KFSPropertyConstants.DOC_SEARCH_URL, docSearchUrl);
    }

    protected void transformLinks(Map<String, Object> institutionPreferences,Person person) {
        Iterator<Map<String,Object>> i = getLinkGroups(institutionPreferences).iterator();
        while ( i.hasNext() ) {
            Map<String,Object> linkGroup = i.next();
            if ( ! transformLinksInLinkGroup(linkGroup,person) ) {
                i.remove();
            }
        }

        for (Map<String, String> menuItem: getMenuItems(institutionPreferences)) {
            if (StringUtils.isNotBlank(menuItem.get(KFSPropertyConstants.LINK))) {
                menuItem.put(KFSPropertyConstants.LINK, fixRelativeLink(menuItem.get(KFSPropertyConstants.LINK)));
            }
        }
    }

    protected List<Map<String, Object>> getLinkGroups(Map<String, Object> institutionPreferences) {
        final List<Map<String, Object>> linkGroups = (List<Map<String, Object>>)institutionPreferences.get(KFSPropertyConstants.LINK_GROUPS);
        if (!ObjectUtils.isNull(linkGroups)) {
            return linkGroups;
        }
        return new ArrayList<>();
    }

    protected List<Map<String, String>> getMenuItems(Map<String, Object> institutionPreferences) {
        List<Map<String, String>> menuItems = (List<Map<String, String>>)institutionPreferences.get(KFSPropertyConstants.MENU);
        if (!ObjectUtils.isNull(menuItems)) {
            return menuItems;
        }
        return new ArrayList<>();
    }

    /**
     * Filter out links that the user does not have permission to view, build links for document types
     * @param linkGroup link group
     * @param person person
     * @return true if the group contains links, false if it is empty
     */
    protected boolean transformLinksInLinkGroup(Map<String, Object> linkGroup,Person person) {
        Map<String, List<Map<String,Object>>> links = getLinks(linkGroup);
        links.replaceAll((String linkType, List<Map<String, Object>> linksByType) -> transformLinksByLinkType(linksByType, person));
        Iterator<Map.Entry<String, List<Map<String, Object>>>> linksIter = links.entrySet().iterator();
        while (linksIter.hasNext()) {
            Map.Entry<String, List<Map<String, Object>>> nextLinks = linksIter.next();
            if (CollectionUtils.isEmpty(nextLinks.getValue())) {
                linksIter.remove();
            }
        }
        linkGroup.put(KFSPropertyConstants.LINKS, links);
        return links.size() > 0;
    }

    protected List<Map<String, Object>> transformLinksByLinkType(List<Map<String, Object>> links, Person person) {
        return links.stream()
                .map((Map<String, Object> link) -> transformLink(link, person))
                .filter((Map<String, Object> link) -> link.containsKey(KFSPropertyConstants.LABEL) && !StringUtils.isBlank((String) link.get(KFSPropertyConstants.LABEL)) && link.containsKey(KFSPropertyConstants.LINK) && !StringUtils.isBlank((String) link.get(KFSPropertyConstants.LINK)))
                .collect(Collectors.toList());
    }

    protected Map<String, List<Map<String, Object>>> getLinks(Map<String, Object> linkGroup) {
        return (Map<String, List<Map<String, Object>>>)linkGroup.get(KFSPropertyConstants.LINKS);
    }

    protected Map<String, Object> transformLink(Map<String, Object> link, Person person) {
        Map<String, Object> linkInfo = new ConcurrentHashMap<>();

        if (StringUtils.isNotBlank((String)link.get(KFSPropertyConstants.DOCUMENT_TYPE_CODE))) {
            final String documentTypeName = (String)link.remove(KFSPropertyConstants.DOCUMENT_TYPE_CODE);
            linkInfo = determineDocumentLinkInfo(documentTypeName, person);
        } else if (StringUtils.isNotBlank((String)link.get(KFSPropertyConstants.BUSINESS_OBJECT_CLASS))) {
            final String businessObjectClassName = (String)link.remove(KFSPropertyConstants.BUSINESS_OBJECT_CLASS);
            linkInfo = determineLookupLinkInfo(businessObjectClassName, person);
        } else if (StringUtils.isNotBlank((String)link.get(KFSPropertyConstants.LINK))) {
            String finalLink;
            if (link.get(KFSPropertyConstants.LINK_TYPE) != null && StringUtils.equals((String)link.get(KFSPropertyConstants.LINK_TYPE), KFSConstants.NavigationLinkTypes.RICE)) {
                finalLink = determineRiceLink((String)link.get(KFSPropertyConstants.LINK));
            } else {
                finalLink = fixRelativeLink((String)link.get(KFSPropertyConstants.LINK));
            }
            linkInfo.put(KFSPropertyConstants.LINK, finalLink);
            linkInfo.put(KFSPropertyConstants.LABEL, link.get(KFSPropertyConstants.LABEL));
        }

        if (linkInfo.containsKey(KFSPropertyConstants.LABEL)) {
            linkInfo.put(KFSPropertyConstants.LINK_TYPE, link.get(KFSPropertyConstants.LINK_TYPE));
        }

        if (link.containsKey(KFSPropertyConstants.NEW_TARGET)) {
            linkInfo.put(KFSPropertyConstants.NEW_TARGET, link.get(KFSPropertyConstants.NEW_TARGET));
        } else {
            linkInfo.put(KFSPropertyConstants.NEW_TARGET, false);
        }

        return linkInfo;
    }

    protected String determineRiceLink(String link) {
        String riceHost = configurationService.getPropertyValueAsString(KFSConstants.RICE_SERVER_URL_KEY);
        if (!link.startsWith("/")) {
            link = "/" + addReturnLocationToLookupLink(link);
        }
        return riceHost + link;
    }

    protected String fixRelativeLink(String link) {
        String fixedLink = link;
        if (!link.startsWith("http")) {
            String updatedLink = addReturnLocationToLookupLink(link);
            fixedLink = configurationService.getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY) + "/" + updatedLink;
        }
        return fixedLink;
    }

    protected String addReturnLocationToLookupLink(String link) {
        String updatedLink = link;
        if (link.startsWith(KFSConstants.LOOKUP_ACTION)) {
            String connector = link.contains("?") ? "&" : "?";
            updatedLink = link + connector + KFSConstants.RETURN_LOCATION_PARAMETER + "=" + configurationService.getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY) + "/portal.do";
        }
        return updatedLink;
    }

    protected Map<String, Object> determineDocumentLinkInfo(String documentTypeName, Person person) {
        final String label = documentDictionaryService.getLabel(documentTypeName);
        final Class<? extends Document> documentClass = (Class<? extends Document>)documentDictionaryService.getDocumentClassByName(documentTypeName);
        String link = StringUtils.EMPTY;
        if (!ObjectUtils.isNull(documentClass)) {
            if (TransactionalDocument.class.isAssignableFrom(documentClass)) {
                if ( canInitiateDocument(documentTypeName,person) ) {
                    link = constructTransactionalDocumentLinkFromClass(documentClass, documentTypeName);
                }
            } else if (MaintenanceDocument.class.isAssignableFrom(documentClass)) {
                final Class<?> businessObjectClass = documentDictionaryService.getMaintenanceDataObjectClass(documentTypeName);
                if (GlobalBusinessObject.class.isAssignableFrom(businessObjectClass)) {
                    if ( canInitiateDocument(documentTypeName,person) ) {
                        link = constructGlobalMaintenanceDocumentLinkFromClass(businessObjectClass);
                    }
                } else {
                    if ( canViewBusinessObjectLookup(businessObjectClass, person) ) {
                        link = constructBusinessObjectLookupLinkFromClass(businessObjectClass);
                    }
                }
            }
        }
        return constructLinkInfo(label, link);
    }

    protected boolean canViewLink(Map<String,Object> permission, Person person) {
        String templateNamespace = (String)permission.get(KFSPropertyConstants.TEMPLATE_NAMESPACE);
        String templateName = (String)permission.get(KFSPropertyConstants.TEMPLATE_NAME);
        Map<String,String> details = (Map<String,String>)permission.get(KFSPropertyConstants.DETAILS);

        if ( templateNamespace == null ) {
            LOG.error("canViewLink() Permission on link is missing templateNamespace");
            return false;
        }
        if ( templateName == null ) {
            LOG.error("canViewLink() Permission on link is missing templateName");
            return false;
        }
        if ( details == null ) {
            LOG.error("canViewLink() Permission on link is missing details object");
            return false;
        }

        return KimApiServiceLocator.getPermissionService().isAuthorizedByTemplate(person.getPrincipalId(),
                templateNamespace, templateName, details, Collections.<String, String>emptyMap());
    }

    protected boolean canInitiateDocument(String documentTypeName,Person person) {
        DocumentAuthorizer documentAuthorizer = documentDictionaryService.getDocumentAuthorizer(documentTypeName);
        DocumentType documentType = documentTypeService.getDocumentTypeByName(documentTypeName);
        return documentAuthorizer.canInitiate(documentTypeName, person) && documentType != null && documentType.isActive();
    }

    protected boolean canViewBusinessObjectLookup(Class<?> businessObjectClass, Person person) {
        return KimApiServiceLocator.getPermissionService().isAuthorizedByTemplate(
                person.getPrincipalId(), KRADConstants.KNS_NAMESPACE,
                KimConstants.PermissionTemplateNames.LOOK_UP_RECORDS,
                KRADUtils.getNamespaceAndComponentSimpleName(businessObjectClass),
                Collections.<String, String>emptyMap());
    }

    protected String constructTransactionalDocumentLinkFromClass(Class<? extends Document> documentClass, String documentTypeName) {
        final String applicationUrl = configurationService.getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY);
        return applicationUrl + "/" + determineUrlNameForClass(documentClass) + transformClassName(documentClass) + ".do?methodToCall=docHandler&command=initiate&docTypeName="+documentTypeName;
    }

    protected String constructBusinessObjectLookupLinkFromClass(Class<?> businessObjectClass) {
        final String applicationUrl = configurationService.getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY);
        return applicationUrl + "/kr/lookup.do?methodToCall=start&businessObjectClassName=" + businessObjectClass.getName() + "&docFormKey=88888888&returnLocation=" + applicationUrl + "/index.jsp&hideReturnLink=true";
    }

    protected String constructGlobalMaintenanceDocumentLinkFromClass(Class<?> businessObjectClass) {
        final String applicationUrl = configurationService.getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY);
        return applicationUrl + "/kr/maintenance.do?methodToCall=start&businessObjectClassName=" + businessObjectClass.getName() + "&hideReturnLink=true";
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

    protected Map<String, Object> constructLinkInfo(String label, String link) {
        Map<String, Object> linkInfo = new ConcurrentHashMap<>();
        if (!StringUtils.isBlank(label)) {
            linkInfo.put(KFSPropertyConstants.LABEL, label);
        }
        if (!StringUtils.isBlank(link)) {
            linkInfo.put(KFSPropertyConstants.LINK, link);
        }
        return linkInfo;
    }

    protected List<Map<String, Object>> removeGeneratedLabels(List<Map<String, Object>> linkGroups) {
        for (Map<String, Object> linkGroup : linkGroups) {
            Map<String, List<Map<String,Object>>> updatedLinks = getLinks(linkGroup);
            updatedLinks.replaceAll((String linkType, List<Map<String, Object>> links) ->
                    links.stream().map((Map<String, Object> link) -> {
                        if (link.containsKey(KFSPropertyConstants.DOCUMENT_TYPE_CODE) || link.containsKey(KFSPropertyConstants.BUSINESS_OBJECT_CLASS)) {
                            link.remove(KFSPropertyConstants.LABEL);
                        }
                        return link;
                    }).collect(Collectors.toList()));
            linkGroup.put(KFSPropertyConstants.LINKS, updatedLinks);
        }
        return linkGroups;
    }

    protected Map<String, Object> determineLookupLinkInfo(String businessObjectClassName, Person person) {
        String link = null;
        String label = null;

        try {
            final Class<?> businessObjectClass = Class.forName(businessObjectClassName);
            if ( canViewBusinessObjectLookup(businessObjectClass, person) ) {
                final BusinessObjectEntry entry = (BusinessObjectEntry)getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(businessObjectClassName);
                if (entry != null && !StringUtils.isBlank(entry.getObjectLabel()) && !ObjectUtils.isNull(entry.getLookupDefinition())) {
                    label = entry.getObjectLabel();
                }
                link = constructBusinessObjectLookupLinkFromClass(businessObjectClass);
            }
        } catch (ClassNotFoundException cnfe) {
            throw new RuntimeException("Misconfigured class in navigation links: "+businessObjectClassName, cnfe);
        }

        return constructLinkInfo(label, link);
    }

    @Override
    public Map<String, Object> getAllLinkGroups() {
        final Map<String, Object> institutionPreferences = preferencesDao.findInstitutionPreferences();
        List<Map<String, Object>> linkGroups = getLinkGroups(institutionPreferences);

        for (Map<String, Object> linkGroup: linkGroups) {
            Map<String, List<Map<String,Object>>> updatedLinks = getLinks(linkGroup);
            updatedLinks.replaceAll((String linkType, List<Map<String, Object>> links) -> links.stream().map((Map<String, Object> link) -> {
                if (link.containsKey(KFSPropertyConstants.DOCUMENT_TYPE_CODE)) {
                    link.put(KFSPropertyConstants.LABEL, documentDictionaryService.getLabel((String)link.get(KFSPropertyConstants.DOCUMENT_TYPE_CODE)));
                } else if (link.containsKey(KFSPropertyConstants.BUSINESS_OBJECT_CLASS)) {
                    link.put(KFSPropertyConstants.LABEL, getDataDictionaryService().getDataDictionary().getBusinessObjectEntry((String) link.get(KFSPropertyConstants.BUSINESS_OBJECT_CLASS)).getObjectLabel());
                }
                return link;
            }).collect(Collectors.toList()));
            linkGroup.put(KFSPropertyConstants.LINKS, updatedLinks);
        }

        Map<String, Object> groupsWithInstitutionId = new ConcurrentHashMap<>();
        groupsWithInstitutionId.put(KFSPropertyConstants.INSTITUTION_ID, institutionPreferences.get(KFSPropertyConstants.INSTITUTION_ID));
        groupsWithInstitutionId.put(KFSPropertyConstants.LINK_GROUPS, linkGroups);

        return groupsWithInstitutionId;
    }

    @Override
    public boolean hasConfigurationPermission(String principalName) {
        final String principalId = getIdentityService().getPrincipalByPrincipalName(principalName).getPrincipalId();

        Map<String,String> permissionDetails = new HashMap<>();
        permissionDetails.put(KimConstants.AttributeConstants.NAMESPACE_CODE, KFSConstants.CoreModuleNamespaces.KFS);
        permissionDetails.put(KimConstants.AttributeConstants.ACTION_CLASS, KFSConstants.ReactComponents.INSTITUTION_CONFIG);

        return getPermissionService().hasPermissionByTemplate(principalId, KFSConstants.CoreModuleNamespaces.KNS, KimConstants.PermissionTemplateNames.USE_SCREEN, permissionDetails);
    }

    @Override
    public List<Map<String, String>> getMenu() {
        final Map<String, Object> institutionPreferences = preferencesDao.findInstitutionPreferences();
        return (List<Map<String, String>>)institutionPreferences.get(KFSPropertyConstants.MENU);
    }

    @Override
    public List<Map<String, String>> saveMenu(String menuString) {
        ObjectMapper mapper = new ObjectMapper();

        List<Map<String, String>> menu;
        try {
            menu = mapper.readValue(menuString, List.class);
        } catch (IOException e) {
            LOG.error("saveMenu Error parsing json", e);
            throw new RuntimeException("Error parsing json");
        }

        final Map<String, Object> institutionPreferences = preferencesDao.findInstitutionPreferences();
        institutionPreferences.put(KFSPropertyConstants.MENU, menu);
        preferencesDao.saveInstitutionPreferences((String)institutionPreferences.get(KFSPropertyConstants.INSTITUTION_ID), institutionPreferences);
        return menu;
    }

    @Override
    public Map<String, String> getLogo() {
        final Map<String, Object> institutionPreferences = preferencesDao.findInstitutionPreferences();
        String logoUrl = (String)institutionPreferences.get(KFSPropertyConstants.LOGO_URL);
        Map<String, String> logo = new ConcurrentHashMap<>();
        logo.put(KFSPropertyConstants.LOGO_URL, logoUrl);
        return logo;
    }

    @Override
    public Map<String, String> uploadLogo(InputStream uploadedInputStream, String filename) {
        Map<String, String> filePath = new ConcurrentHashMap<>();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] bytes;
        try {
            IOUtils.copy(uploadedInputStream, outputStream);
            bytes = outputStream.toByteArray();

            if (outputStream.size() / 1000 > MAX_LOGO_SIZE_KB) {
                throw new RuntimeException("Image size must be less than " + MAX_LOGO_SIZE_KB + " KB.");
            }

            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            if (bufferedImage.getHeight() < LOGO_HEIGHT) {
                throw new RuntimeException("Image must have a height of at least " + LOGO_HEIGHT + " pixels.");
            }

            int xPos = 0;
            boolean hasTransparency = false;
            while (xPos < bufferedImage.getWidth()) {
                if (bufferedImage.getRGB(xPos,0) == 0) {
                    hasTransparency = true;
                    break;
                }
                xPos++;
            }
            if (!hasTransparency) {
                throw new RuntimeException("Image background must be transparent.");
            }

            String[] fileParts = filename.split("\\.");
            String extension = fileParts[fileParts.length - 1];

            String imageBase64 = new String(Base64.encodeBase64(bytes));
            String image = "data:image/" + extension + ";base64," + imageBase64;
            filePath.put(KFSPropertyConstants.LOGO_URL, image);
        } catch (IOException ioe) {
            LOG.error("Failed to upload logo", ioe);
            throw new RuntimeException("Error uploading logo");
        } finally {
            IOUtils.closeQuietly(outputStream);
            IOUtils.closeQuietly(uploadedInputStream);
        }

        return filePath;
    }

    @Override
    public Map<String, String> saveLogo(String logoString) {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, String> logo;
        try {
            logo = mapper.readValue(logoString, Map.class);
        } catch (IOException e) {
            LOG.error("saveLogo Error parsing json", e);
            throw new RuntimeException("Error parsing json");
        }

        if (!logo.containsKey(KFSPropertyConstants.LOGO_URL)) {
            throw new RuntimeException("Invalid JSON. Should contain logoUrl.");
        }

        final Map<String, Object> institutionPreferences = preferencesDao.findInstitutionPreferences();
        institutionPreferences.put(KFSPropertyConstants.LOGO_URL, logo.get(KFSPropertyConstants.LOGO_URL));
        preferencesDao.saveInstitutionPreferences((String)institutionPreferences.get(KFSPropertyConstants.INSTITUTION_ID), institutionPreferences);
        return logo;
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

    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    public PermissionService getPermissionService() {
        return permissionService;
    }

    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    public IdentityService getIdentityService() {
        return identityService;
    }

    public void setIdentityService(IdentityService identityService) {
        this.identityService = identityService;
    }
    
    public void setDocumentTypeService(DocumentTypeService documentTypeService) {
		this.documentTypeService = documentTypeService;
	}
}
