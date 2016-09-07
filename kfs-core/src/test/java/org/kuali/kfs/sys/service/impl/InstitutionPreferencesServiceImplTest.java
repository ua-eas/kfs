/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.sys.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kuali.kfs.coa.businessobject.OrganizationReversionGlobal;
import org.kuali.kfs.fp.businessobject.CreditCardType;
import org.kuali.kfs.fp.document.ServiceBillingDocument;
import org.kuali.kfs.krad.bo.ModuleConfiguration;
import org.kuali.kfs.krad.datadictionary.BusinessObjectEntry;
import org.kuali.kfs.krad.datadictionary.DocumentEntry;
import org.kuali.kfs.krad.datadictionary.MaintenanceDocumentEntry;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.document.DocumentAuthorizer;
import org.kuali.kfs.krad.document.DocumentPresentationController;
import org.kuali.kfs.krad.maintenance.Maintainable;
import org.kuali.kfs.krad.maintenance.MaintenanceDocument;
import org.kuali.kfs.krad.rules.rule.BusinessRule;
import org.kuali.kfs.krad.service.DocumentDictionaryService;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.sys.FinancialSystemModuleConfiguration;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.dataaccess.PreferencesDao;
import org.kuali.kfs.sys.document.FinancialSystemMaintenanceDocument;
import org.kuali.kfs.sys.identity.TestPerson;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.impl.identity.PersonImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class InstitutionPreferencesServiceImplTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    abstract class PreferencesDaoInstitutionPreferences implements PreferencesDao {
        public Integer cacheLength = null;

        @Override
        public Map<String, Object> findInstitutionPreferences() {
            return null;
        }

        @Override
        public void saveInstitutionPreferences(String institutionId, Map<String, Object> preferences) {
        }

        @Override
        public Map<String, Object> findInstitutionPreferencesCache(String principalName) {
            return null;
        }

        @Override
        public void setInstitutionPreferencesCacheLength(int seconds) {
            cacheLength = seconds;
        }

        @Override
        public int getInstitutionPreferencesCacheLength() {
            return cacheLength;
        }

        @Override
        public void cacheInstitutionPreferences(String principalName, Map<String, Object> institutionPreferences) {
        }

        @Override
        public Map<String, Object> getUserPreferences(String principalName) {
            return null;
        }

        @Override
        public void saveUserPreferences(String principalName, String preferences) {
        }

        @Override
        public void saveUserPreferences(String principalName, Map<String, Object> preferences) {

        }
    }

    private PreferencesDaoInstitutionPreferences createFakePreferencesDaoInstitutionPreferences() {
        return new PreferencesDaoInstitutionPreferences() {
            @Override
            public Map<String, Object> findInstitutionPreferences() {
                Map<String, Object> ip = new ConcurrentHashMap<>();
                ip.put("institutionId", "123413535");
                ip.put("logoUrl", "static/images/out-of-the-box-logo-rtna.png");
                ip.put("institutionName", "Kuali");
                return ip;
            }

            @Override
            public void saveInstitutionPreferences(String institutionId, Map<String, Object> preferences) {

            }
        };
    }

    private PreferencesDaoInstitutionPreferences createFakePreferencesDaoInstitutionPreferencesWithMenu() {
        return new PreferencesDaoInstitutionPreferences() {
            @Override
            public Map<String, Object> findInstitutionPreferences() {
                Map<String, Object> ip = new ConcurrentHashMap<>();
                ip.put("institutionId", "123413535");
                ip.put("logoUrl", "static/images/out-of-the-box-logo-rtna.png");
                ip.put("institutionName", "Kuali");

                List<Map<String, String>> menu = new ArrayList<>();
                Map<String, String> menuItem = new ConcurrentHashMap<>();
                menuItem.put("label", "Feedback");
                menuItem.put("link", "kr/kualiFeedbackReport.do");
                menu.add(menuItem);
                menuItem = new ConcurrentHashMap<>();
                menuItem.put("label", "Help");
                menuItem.put("link", "static/help/default.htm");
                menu.add(menuItem);

                ip.put("menu", menu);
                return ip;
            }

            @Override
            public void saveInstitutionPreferences(String institutionId, Map<String, Object> preferences) {

            }
        };
    }

    private PreferencesDaoInstitutionPreferences createFakePreferencesDaoInstitutionPreferencesWithLinkGroups(List<Map<String, Object>> linkGroups) {
        return new PreferencesDaoInstitutionPreferences() {
            @Override
            public Map<String, Object> findInstitutionPreferences() {
                Map<String, Object> ip = new ConcurrentHashMap<>();
                ip.put("institutionId", "123413535");
                ip.put("logoUrl", "static/images/out-of-the-box-logo-rtna.png");
                ip.put("institutionName", "Kuali");

                ip.put("linkGroups", linkGroups);

                return ip;
            }

            @Override
            public void saveInstitutionPreferences(String institutionId, Map<String, Object> preferences) {

            }
        };
    }

    private Map<String, Object> jsonToMap(String json) {
        try {
            return new ObjectMapper().readValue(json, HashMap.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Invalid json: " + json);
        }
    }

    @Test
    public void testFindInstitutionPreferencesLinks_NoLinkGroups() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new InstitutionPreferencesServiceImpl();
        institutionPreferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferences());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesNoLinks(Optional.of("2.5"));

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertNull("Link Groups should not exist", preferences.get("linkGroups"));
    }

    @Test
    public void testFindInstitutionPreferencesLinks_RiceVersionSet() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new InstitutionPreferencesServiceImpl();
        institutionPreferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferences());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesNoLinks(Optional.of("2.5"));

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertNotNull("Versions should exist", preferences.get("versions"));

        Map<String, String> versions = (Map<String, String>)preferences.get("versions");
        Assert.assertEquals("Rice version should be set","2.5",versions.get("Kuali Rice"));
    }

    @Test
    public void testFindInstitutionPreferencesLinks_RiceVersionNotSet() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new InstitutionPreferencesServiceImpl();
        institutionPreferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferences());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesNoLinks(Optional.empty());

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertNotNull("Versions should exist", preferences.get("versions"));

        Map<String, String> versions = (Map<String, String>)preferences.get("versions");
        Assert.assertEquals("Rice version should be set to unknown","Unknown",versions.get("Kuali Rice"));
    }

    @Test
    public void testFindInstitutionPreferencesLinks_HasActionList() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new InstitutionPreferencesServiceImpl();
        institutionPreferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferences());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesNoLinks(Optional.of("2.5"));

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertTrue("Preferences should always include an action list url", preferences.containsKey("actionListUrl"));
        Assert.assertEquals("We should know what action list is", "http://tst.kfs.kuali.org/kfs-tst/kew/ActionList.do", preferences.get("actionListUrl"));
    }

    @Test
    public void testFindInstitutionPreferencesLinks_HasSignoutUrl() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new InstitutionPreferencesServiceImpl();
        institutionPreferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferences());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesNoLinks(Optional.of("2.5"));

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertTrue("Preferences should always include a signoutUrl", preferences.containsKey("signoutUrl"));
        Assert.assertEquals("We should know what the signoutUrl is", "http://tst.kfs.kuali.org/kfs-tst/logout.do", preferences.get("signoutUrl"));
    }

    @Test
    public void testFindInstitutionPreferences_HasDocSearch() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new InstitutionPreferencesServiceImpl();
        institutionPreferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferences());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesNoLinks(Optional.of("2.5"));

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertTrue("Preferences should always include a doc search url", preferences.containsKey("actionListUrl"));
        Assert.assertEquals("We should know what doc search is", "http://tst.kfs.kuali.org/kfs-tst/kew/DocumentSearch.do?docFormKey=88888888&hideReturnLink=true&returnLocation=http://tst.kfs.kuali.org/kfs-tst/goto?url=http://tst.kfs.kuali.org/kfs-tst/index.jsp", preferences.get("docSearchUrl"));
    }

    @Test
    public void testFindInstitutionPreferencesLinks_HasHelpLinksNotTransformed() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new InstitutionPreferencesServiceImpl();
        institutionPreferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferencesWithMenu());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesNoLinks(Optional.of("2.5"));

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertTrue("Preferences should always include a help url", StringUtils.isNotBlank(getMenuLinkUrl(preferences, "Help")));
        Assert.assertEquals("We should know what help url is", "static/help/default.htm", getMenuLinkUrl(preferences, "Help"));
    }

    @Test
    public void testFindInstitutionPreferencesLinks_HasHelpTransformedLinks() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new InstitutionPreferencesServiceImpl();
        institutionPreferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferencesWithMenu());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesLinks(new PersonImpl(), false);

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertTrue("Preferences should always include a help url", StringUtils.isNotBlank(getMenuLinkUrl(preferences, "Help")));
        Assert.assertEquals("We should know what help url is", "http://tst.kfs.kuali.org/kfs-tst/static/help/default.htm", getMenuLinkUrl(preferences, "Help"));
    }

    private String getMenuLinkUrl(Map<String, Object> preferences, String key) {
        List<Map<String, String>> menuItems = (List<Map<String, String>>) preferences.get("menu");
        if (CollectionUtils.isNotEmpty(menuItems)) {
            for (Map<String, String> menuItem : menuItems) {
                String label = menuItem.get("label");
                if (StringUtils.equals(label, key)) {
                    return menuItem.get("link");
                }
            }
        }
        return StringUtils.EMPTY;
    }

    @Test
    public void testFindInstitutionPreferencesLinks_HasFeedback() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new InstitutionPreferencesServiceImpl();
        institutionPreferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferencesWithMenu());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesLinks(new TestPerson(), false);

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertTrue("Preferences should always include a feedback url", StringUtils.isNotBlank(getMenuLinkUrl(preferences, "Feedback")));
        Assert.assertEquals("We should know what feedback url is", "http://tst.kfs.kuali.org/kfs-tst/kr/kualiFeedbackReport.do", getMenuLinkUrl(preferences, "Feedback"));
    }

    protected List<Map<String, Object>> buildLinkGroup(Map<String, String>... links) {
        Map<String, List<Map<String, String>>> linksByType = new ConcurrentHashMap<>();
        for (Map<String, String> currLink : links) {
            List<Map<String, String>> linksForType = linksByType.getOrDefault(currLink.get("type"), new ArrayList<>());
            linksForType.add(currLink);
            linksByType.put(currLink.get("type"), linksForType);
        }

        Map<String, Object> linkGroup = new ConcurrentHashMap<>();
        linkGroup.put("label", "Test Menu");
        linkGroup.put("links", linksByType);

        List<Map<String, Object>> linkGroups = new ArrayList<>();
        linkGroups.add(linkGroup);

        return linkGroups;
    }

    @Test
    public void testFindInstitutionPreferencesLinks_HealthyLinkGroup() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new NoPermissionsInstitutionPreferencesServiceImpl();

        Map<String, String> link = new ConcurrentHashMap<>();
        link.put("documentTypeCode", "SB");
        link.put("type", "activities");
        link.put("linkType", "kfs");

        List<Map<String, Object>> linkGroups = buildLinkGroup(link);

        institutionPreferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferencesWithLinkGroups(linkGroups));
        institutionPreferencesServiceImpl.setDocumentDictionaryService(new StubDocumentDictionaryService());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());
        institutionPreferencesServiceImpl.setKualiModuleService(new StubKualiModuleService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesLinks(new TestPerson(), false);

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertNotNull("Link Groups should exist", preferences.get("linkGroups"));
        Assert.assertTrue("Link Groups should be a List", (preferences.get("linkGroups") instanceof List));
        Assert.assertTrue("Link Groups should not be empty", !CollectionUtils.isEmpty((List) preferences.get("linkGroups")));
        Assert.assertTrue("Link Groups should have a label", !StringUtils.isBlank((String) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("label")));
        Assert.assertTrue("Link groups should have links", !CollectionUtils.isEmpty(((Map<String, List<Map<String, String>>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get("activities")));
    }

    @Test
    public void testFindInstitutionPreferencesLinks_TransactionalDocumentTypeLinkIsTransformed() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new NoPermissionsInstitutionPreferencesServiceImpl();

        Map<String, String> link = new ConcurrentHashMap<>();
        link.put("documentTypeCode", "SB");
        link.put("type", "activities");
        link.put("linkType", "kfs");

        List<Map<String, Object>> linkGroups = buildLinkGroup(link);

        institutionPreferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferencesWithLinkGroups(linkGroups));
        institutionPreferencesServiceImpl.setDocumentDictionaryService(new StubDocumentDictionaryService());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());
        institutionPreferencesServiceImpl.setKualiModuleService(new StubKualiModuleService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesLinks(new TestPerson(), false);

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertNotNull("Link Groups should exist", preferences.get("linkGroups"));
        Assert.assertTrue("Link Groups should be a List", (preferences.get("linkGroups") instanceof List));
        Assert.assertTrue("Link Groups should not be empty", !CollectionUtils.isEmpty((List) preferences.get("linkGroups")));
        Assert.assertTrue("Link Groups should have a label", !StringUtils.isBlank((String) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("label")));
        Assert.assertTrue("Link groups should have links", !CollectionUtils.isEmpty(((Map<String, List<Map<String, String>>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get("activities")));

        Map<String, String> testLink = getLinkAt(preferences, 0, "activities", 0);

        Assert.assertTrue("Link should have a label", !StringUtils.isBlank(testLink.get("label")));
        Assert.assertTrue("Link should have a link type", !StringUtils.isBlank(testLink.get("linkType")));

        String groupLink = testLink.get("link");
        Assert.assertTrue("Link should have a link", !StringUtils.isBlank(groupLink));
        Assert.assertEquals("Link should be generated correctly", "http://tst.kfs.kuali.org/kfs-tst/financialServiceBilling.do?methodToCall=docHandler&command=initiate&docTypeName=SB", groupLink);
        Assert.assertTrue("Link should NOT have a document type", StringUtils.isBlank(testLink.get("documentTypeCode")));
    }

    @Test
    public void testFindInstitutionPreferencesLinks_MissingDocumentTypeReturnsNoLink() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new NoPermissionsInstitutionPreferencesServiceImpl();

        Map<String, String> link = new ConcurrentHashMap<>();
        link.put("documentTypeCode", "SB");
        link.put("type", "activities");
        link.put("linkType", "kfs");

        Map<String, String> link2 = new ConcurrentHashMap<>();
        link2.put("documentTypeCode", "ZZZZ");
        link2.put("type", "reference");
        link2.put("linkType", "kfs");

        Map<String, String> link3 = new ConcurrentHashMap<>();
        link3.put("documentTypeCode", "CCR");
        link3.put("type", "activities");
        link3.put("linkType", "kfs");

        List<Map<String, Object>> linkGroups = buildLinkGroup(link, link2, link3);

        institutionPreferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferencesWithLinkGroups(linkGroups));
        institutionPreferencesServiceImpl.setDocumentDictionaryService(new StubDocumentDictionaryService());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());
        institutionPreferencesServiceImpl.setKualiModuleService(new StubKualiModuleService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesLinks(new TestPerson(), false);

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertNotNull("Link Groups should exist", preferences.get("linkGroups"));
        Assert.assertTrue("Link Groups should be a List", (preferences.get("linkGroups") instanceof List));
        Assert.assertTrue("Link Groups should not be empty", !CollectionUtils.isEmpty((List) preferences.get("linkGroups")));
        Assert.assertTrue("Link Groups should have a label", !StringUtils.isBlank((String) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("label")));
        Assert.assertTrue("Link groups should have activity links", !CollectionUtils.isEmpty(((Map<String, List<Map<String, String>>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get("activities")));
        Assert.assertTrue("Link groups should have not reference links", CollectionUtils.isEmpty(((Map<String, List<Map<String, String>>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get("references")));
        Assert.assertTrue("Link groups should have not have administration links", CollectionUtils.isEmpty(((Map<String, List<Map<String, String>>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get("administration")));
        Assert.assertEquals("Link group should only have one link", 1, ((Map<String, List<Map<String, String>>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get("activities").size());
        Assert.assertEquals("The one link should be for Service Billing", "Service Billing", ((Map<String, List<Map<String, String>>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get("activities").get(0).get("label"));
    }

    @Test
    public void testFindInstitutionPreferencesLinks_MaintenanceDocumentTypeLinkIsTransformed() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new NoPermissionsInstitutionPreferencesServiceImpl();

        Map<String, String> link = new ConcurrentHashMap<>();
        link.put("documentTypeCode", "CCTY");
        link.put("type", "administration");
        link.put("linkType", "kfs");

        List<Map<String, Object>> linkGroups = buildLinkGroup(link);

        institutionPreferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferencesWithLinkGroups(linkGroups));
        institutionPreferencesServiceImpl.setDocumentDictionaryService(new StubDocumentDictionaryService());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());
        institutionPreferencesServiceImpl.setKualiModuleService(new StubKualiModuleService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesLinks(new TestPerson(), false);

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertNotNull("Link Groups should exist", preferences.get("linkGroups"));
        Assert.assertTrue("Link Groups should be a List", (preferences.get("linkGroups") instanceof List));
        Assert.assertTrue("Link Groups should not be empty", !CollectionUtils.isEmpty((List) preferences.get("linkGroups")));
        Assert.assertTrue("Link Groups should have a label", !StringUtils.isBlank((String) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("label")));
        Assert.assertTrue("Link groups should have links", !CollectionUtils.isEmpty(((Map<String, List<Map<String, String>>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get("administration")));

        Map<String, String> testLink = getLinkAt(preferences, 0, "administration", 0);
        Assert.assertTrue("Link should have a label", !StringUtils.isBlank(testLink.get("label")));
        Assert.assertTrue("Link should have a link type", !StringUtils.isBlank(testLink.get("linkType")));

        String groupLink = testLink.get("link");
        Assert.assertTrue("Link should have a link", !StringUtils.isBlank(groupLink));

        Assert.assertEquals("Link should be generated correctly", "http://tst.kfs.kuali.org/kfs-tst/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.fp.businessobject.CreditCardType&docFormKey=88888888&returnLocation=http://tst.kfs.kuali.org/kfs-tst/index.jsp&hideReturnLink=true", groupLink);
        Assert.assertTrue("Link should NOT have a document type", StringUtils.isBlank(testLink.get("documentTypeCode")));
    }

    protected Map<String, String> getLinkAt(Map<String, Object> preferences, int i, String type, int j) {
        return ((Map<String, List<Map<String, String>>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(i).get("links")).get(type).get(j);
    }

    @Test
    public void testFindInstitutionPreferencesLinks_GlobalMaintenanceDocumentTypeLinkIsTransformed() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new NoPermissionsInstitutionPreferencesServiceImpl();

        Map<String, String> link = new ConcurrentHashMap<>();
        link.put("documentTypeCode", "GORV");
        link.put("type", "administration");
        link.put("linkType", "kfs");

        List<Map<String, Object>> linkGroups = buildLinkGroup(link);

        institutionPreferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferencesWithLinkGroups(linkGroups));
        institutionPreferencesServiceImpl.setDocumentDictionaryService(new StubDocumentDictionaryService());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());
        institutionPreferencesServiceImpl.setKualiModuleService(new StubKualiModuleService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesLinks(new TestPerson(), false);

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertNotNull("Link Groups should exist", preferences.get("linkGroups"));
        Assert.assertTrue("Link Groups should be a List", (preferences.get("linkGroups") instanceof List));
        Assert.assertTrue("Link Groups should not be empty", !CollectionUtils.isEmpty((List) preferences.get("linkGroups")));
        Assert.assertTrue("Link Groups should have a label", !StringUtils.isBlank((String) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("label")));
        Assert.assertTrue("Link groups should have links", !CollectionUtils.isEmpty(((Map<String, List<Map<String, String>>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get("administration")));

        Map<String, String> testLink = getLinkAt(preferences, 0, "administration", 0);
        Assert.assertTrue("Link should have a label", !StringUtils.isBlank(testLink.get("label")));
        Assert.assertTrue("Link should have a link type", !StringUtils.isBlank(testLink.get("linkType")));

        String groupLink = testLink.get("link");
        Assert.assertTrue("Link should have a link", !StringUtils.isBlank(groupLink));

        Assert.assertEquals("Link should be generated correctly", "http://tst.kfs.kuali.org/kfs-tst/kr/maintenance.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.OrganizationReversionGlobal&hideReturnLink=true", groupLink);
        Assert.assertTrue("Link should NOT have a document type", StringUtils.isBlank(testLink.get("documentTypeCode")));
    }

    @Test
    public void testFindInstitutionPreferencesLinks_RelativeLinkIsTransformed() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new InstitutionPreferencesServiceImpl();

        Map<String, String> link = new ConcurrentHashMap<>();
        link.put("link", "electronicFundTransfer.do?methodToCall=start");
        link.put("label", "Electronic Payment Claim");
        link.put("type", "activities");
        link.put("linkType", "kfs");

        List<Map<String, Object>> linkGroups = buildLinkGroup(link);

        institutionPreferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferencesWithLinkGroups(linkGroups));
        institutionPreferencesServiceImpl.setDocumentDictionaryService(new StubDocumentDictionaryService());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());
        institutionPreferencesServiceImpl.setKualiModuleService(new StubKualiModuleService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesLinks(new TestPerson(), false);

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertNotNull("Link Groups should exist", preferences.get("linkGroups"));
        Assert.assertTrue("Link Groups should be a List", (preferences.get("linkGroups") instanceof List));
        Assert.assertTrue("Link Groups should not be empty", !CollectionUtils.isEmpty((List) preferences.get("linkGroups")));
        Assert.assertTrue("Link Groups should have a label", !StringUtils.isBlank((String) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("label")));
        Assert.assertTrue("Link groups should have links", !CollectionUtils.isEmpty(((Map<String, List<Map<String, String>>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get("activities")));

        Map<String, String> testLink = getLinkAt(preferences, 0, "activities", 0);

        Assert.assertTrue("Link should have a label", !StringUtils.isBlank(testLink.get("label")));
        Assert.assertTrue("Link should have a link type", !StringUtils.isBlank(testLink.get("linkType")));

        String groupLink = testLink.get("link");
        Assert.assertTrue("Link should have a link", !StringUtils.isBlank(groupLink));

        Assert.assertEquals("Link should be generated correctly", "http://tst.kfs.kuali.org/kfs-tst/electronicFundTransfer.do?methodToCall=start", groupLink);
        Assert.assertTrue("Link should NOT have a document type", StringUtils.isBlank(testLink.get("documentTypeCode")));
    }

    @Test
    public void testFindInstitutionPreferencesNoLinks_hasNoLinks() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new NoPermissionsInstitutionPreferencesServiceImpl();
        institutionPreferencesServiceImpl.setPreferencesDao(new PreferencesDaoInstitutionPreferences() {
            @Override
            public Map<String, Object> findInstitutionPreferences() {
                return jsonToMap("{ " +
                    "\"linkGroups\": [" +
                    "    { " +
                    "      \"label\": \"Test Menu\", " +
                    "      \"links\": [" +
                    "          { \"link\": \"electronicFundTransfer.do?methodToCall=start\",\"label\": \"Electronic Payment Claim\"," +
                    "            \"type\": \"activities\",\"linkType\": \"kfs\" }" +
                    "        ] " +
                    "    } " +
                    "] " +
                    "}");
            }
        });
        institutionPreferencesServiceImpl.setDocumentDictionaryService(new StubDocumentDictionaryService());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());
        institutionPreferencesServiceImpl.setKualiModuleService(new StubKualiModuleService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesNoLinks(Optional.of("2.5"));

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertNull("Link Groups should not exist", preferences.get("linkGroups"));
    }

    @Test
    public void testFindInstitutionPreferencesLinks_canViewLinkPermission() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new NoPermissionsInstitutionPreferencesServiceImpl() {
            @Override
            protected boolean canViewLink(Map<String, Object> permission, Person person) {
                return false;
            }
        };
        institutionPreferencesServiceImpl.setPreferencesDao(new PreferencesDaoInstitutionPreferences() {
            @Override
            public Map<String, Object> findInstitutionPreferences() {
                return jsonToMap("{ " +
                    "\"linkGroups\": [" +
                    "    { " +
                    "      \"label\": \"Test Menu\", " +
                    "      \"links\": {" +
                    "          \"activities\": [ { \"link\": \"electronicFundTransfer.do?methodToCall=start\",\"label\": \"Electronic Payment Claim\"," +
                    "            \"linkType\": \"kfs\",\"permission\": {" +
                    "                \"templateNamespace\": \"KR-SYS\"," +
                    "                \"templateName\": \"Initiate Document\"," +
                    "                \"details\": { \"documentTypeCode\": \"ETB\" }" +
                    "              } } ]" +
                    "        } " +
                    "    } " +
                    "] " +
                    "}");
            }
        });

        institutionPreferencesServiceImpl.setDocumentDictionaryService(new StubDocumentDictionaryService());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());
        institutionPreferencesServiceImpl.setKualiModuleService(new StubKualiModuleService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesLinks(new TestPerson(), false);

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertEquals("Link Group should be empty", 0, ((List) preferences.get("linkGroups")).size());
    }

    @Test
    public void testFindInstitutionPreferencesLinks_canInitiateDocumentPermission() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new NoPermissionsInstitutionPreferencesServiceImpl() {
            @Override
            protected boolean canInitiateDocument(String documentTypeName, Person person) {
                return false;
            }
        };
        institutionPreferencesServiceImpl.setPreferencesDao(new PreferencesDaoInstitutionPreferences() {
            @Override
            public Map<String, Object> findInstitutionPreferences() {
                return jsonToMap("{ " +
                    "\"linkGroups\": [" +
                    "    { " +
                    "      \"label\": \"Test Menu\", " +
                    "      \"links\": {" +
                    "          \"activities\": [ { \"documentTypeCode\": \"SB\",\"linkType\": \"kfs\" } ]" +
                    "        } " +
                    "    } " +
                    "] " +
                    "}");
            }
        });

        institutionPreferencesServiceImpl.setDocumentDictionaryService(new StubDocumentDictionaryService());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());
        institutionPreferencesServiceImpl.setKualiModuleService(new StubKualiModuleService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesLinks(new TestPerson(), false);

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertEquals("Link Group should be empty", 0, ((List) preferences.get("linkGroups")).size());
    }

    @Test
    public void testFindInstitutionPreferencesLinks_canViewMaintableBusinessObjectLookupPermission() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new NoPermissionsInstitutionPreferencesServiceImpl() {
            @Override
            protected boolean canViewBusinessObjectLookup(Class<?> businessObjectClass, Person person) {
                return false;
            }
        };
        institutionPreferencesServiceImpl.setPreferencesDao(new PreferencesDaoInstitutionPreferences() {
            @Override
            public Map<String, Object> findInstitutionPreferences() {
                return jsonToMap("{ " +
                    "\"linkGroups\": [" +
                    "    { " +
                    "      \"label\": \"Test Menu\", " +
                    "      \"links\": {" +
                    "          \"administration\": [ { \"documentTypeCode\": \"CCTY\",\"linkType\": \"kfs\" } ]" +
                    "        } " +
                    "    } " +
                    "] " +
                    "}");
            }
        });

        institutionPreferencesServiceImpl.setDocumentDictionaryService(new StubDocumentDictionaryService());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());
        institutionPreferencesServiceImpl.setKualiModuleService(new StubKualiModuleService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesLinks(new TestPerson(), false);

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertEquals("Link Group should be empty", 0, ((List) preferences.get("linkGroups")).size());
    }

    @Test
    public void testInstitutionPreferencesCacheSet() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new NoPermissionsInstitutionPreferencesServiceImpl();
        PreferencesDaoInstitutionPreferences dao = new PreferencesDaoInstitutionPreferences() {
        };
        institutionPreferencesServiceImpl.setPreferencesDao(dao);

        institutionPreferencesServiceImpl.setInstitutionPreferencesCacheLength(1000);
        Assert.assertEquals("Cache Length should be set", 1000, (int) dao.cacheLength);
    }

    @Test
    public void testInstitutionPreferencesCacheGet() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new NoPermissionsInstitutionPreferencesServiceImpl();
        PreferencesDaoInstitutionPreferences dao = new PreferencesDaoInstitutionPreferences() {
        };
        institutionPreferencesServiceImpl.setPreferencesDao(dao);
        dao.cacheLength = 100;

        Assert.assertEquals("Cache Length should be retrieved", 100, (int) institutionPreferencesServiceImpl.getInstitutionPreferencesCacheLength());
    }

    @Test
    public void testFindInstitutionPreferencesLinksCache() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new NoPermissionsInstitutionPreferencesServiceImpl();
        institutionPreferencesServiceImpl.setPreferencesDao(new PreferencesDaoInstitutionPreferences() {
            private Map<String, Object> getData() {
                return jsonToMap("{ " +
                    "\"linkGroups\": [" +
                    "    { " +
                    "      \"label\": \"Test Menu\", " +
                    "      \"links\": {" +
                    "          \"administration\": [ { \"documentTypeCode\": \"CCTY\",\"linkType\": \"kfs\" } ]" +
                    "        } " +
                    "    } " +
                    "] " +
                    "}");
            }

            @Override
            public Map<String, Object> findInstitutionPreferencesCache(String principalName) {
                Map<String, Object> data = getData();
                data.put("cache", "cached");
                return data;
            }

            @Override
            public Map<String, Object> findInstitutionPreferences() {
                return getData();
            }
        });

        institutionPreferencesServiceImpl.setDocumentDictionaryService(new StubDocumentDictionaryService());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());
        institutionPreferencesServiceImpl.setKualiModuleService(new StubKualiModuleService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesLinks(new TestPerson(), true);

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertEquals("Should have retrieved cached version", "cached", preferences.get("cache"));
    }

    @Test
    public void testFindInstitutionPreferences_LookupLinkHasReturnLocation() {
        InstitutionPreferencesServiceImpl preferencesServiceImpl = new NoPermissionsInstitutionPreferencesServiceImpl();

        Map<String, String> link = new ConcurrentHashMap<>();
        link.put("link", "kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.batch.BatchJobStatus&docFormKey=88888888&hideReturnLink=true&conversionFields=name:name,group:group");
        link.put("label", "Batch Schedule");
        link.put("type", "administration");
        link.put("linkType", "kfs");

        Map<String, String> link2 = new ConcurrentHashMap<>();
        link2.put("link", "kr/lookup.do");
        link2.put("label", "Batch Schedule");
        link2.put("type", "administration");
        link2.put("linkType", "kfs");

        List<Map<String, Object>> linkGroups = buildLinkGroup(link, link2);

        preferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferencesWithLinkGroups(linkGroups));
        preferencesServiceImpl.setDocumentDictionaryService(new StubDocumentDictionaryService());
        preferencesServiceImpl.setConfigurationService(new StubConfigurationService());
        preferencesServiceImpl.setKualiModuleService(new StubKualiModuleService());

        Map<String, Object> preferences = preferencesServiceImpl.findInstitutionPreferencesLinks(new TestPerson(), false);

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertNotNull("Link Groups should exist", preferences.get("linkGroups"));
        Assert.assertTrue("Link Groups should be a List", (preferences.get("linkGroups") instanceof List));
        Assert.assertTrue("Link Groups should not be empty", !CollectionUtils.isEmpty((List) preferences.get("linkGroups")));
        Assert.assertTrue("Link Groups should have a label", !StringUtils.isBlank((String) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("label")));
        Assert.assertTrue("Link groups should have links", !CollectionUtils.isEmpty(((Map<String, List<Map<String, String>>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get("administration")));

        Map<String, String> testLink = getLinkAt(preferences, 0, "administration", 0);

        Assert.assertTrue("Link should have a label", !StringUtils.isBlank(testLink.get("label")));
        Assert.assertTrue("Link should have a link type", !StringUtils.isBlank(testLink.get("linkType")));

        String groupLink = testLink.get("link");
        Assert.assertTrue("Link should have a link", !StringUtils.isBlank(groupLink));

        Assert.assertEquals("Link should be generated correctly", "http://tst.kfs.kuali.org/kfs-tst/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.batch.BatchJobStatus&docFormKey=88888888&hideReturnLink=true&conversionFields=name:name,group:group&returnLocation=http://tst.kfs.kuali.org/kfs-tst/portal.do", groupLink);
        Assert.assertTrue("Link should NOT have a document type", StringUtils.isBlank(testLink.get("documentTypeCode")));

        Map<String, String> testLink2 = getLinkAt(preferences, 0, "administration", 1);

        String groupLink2 = testLink2.get("link");
        Assert.assertTrue("Link should have a link", !StringUtils.isBlank(groupLink2));

        Assert.assertEquals("Link should be generated correctly", "http://tst.kfs.kuali.org/kfs-tst/kr/lookup.do?returnLocation=http://tst.kfs.kuali.org/kfs-tst/portal.do", groupLink2);
    }

    @Test
    public void testRemoveGeneratedLabels() {
        List<Map<String, String>> activitiesLinks = new ArrayList<>();
        Map<String, String> baLink = new HashMap<>();
        baLink.put(KFSPropertyConstants.DOCUMENT_TYPE_CODE, "BA");
        baLink.put(KFSPropertyConstants.LABEL, "Budget Adjustment");
        baLink.put(KFSPropertyConstants.LINK_TYPE, "kfs");
        activitiesLinks.add(baLink);

        Map<String, String> bcsLink = new HashMap<>();
        bcsLink.put(KFSPropertyConstants.LINK, "budgetBudgetConstructionSelection.do?methodToCall=loadExpansionScreen");
        bcsLink.put(KFSPropertyConstants.LABEL, "Budget Construction Selection");
        bcsLink.put(KFSPropertyConstants.LINK_TYPE, "kfs");
        activitiesLinks.add(bcsLink);

        List<Map<String, String>> adminstrationLinks = new ArrayList<>();
        Map<String, String> idcLink = new HashMap<>();
        idcLink.put(KFSPropertyConstants.BUSINESS_OBJECT_CLASS, "org.kuali.kfs.coa.businessobject.IndirectCostRecoveryRateDetail");
        idcLink.put(KFSPropertyConstants.LABEL, "Indirect Cost Recovery Rate Detail");
        idcLink.put(KFSPropertyConstants.LINK_TYPE, "kfs");
        adminstrationLinks.add(idcLink);

        Map<String, Object> links = new HashMap<>();
        links.put("activities", activitiesLinks);
        links.put("administration", adminstrationLinks);

        Map<String, Object> group = new HashMap<>();
        group.put(KFSPropertyConstants.LABEL, "My Test Group");
        group.put(KFSPropertyConstants.LINKS, links);

        List<Map<String, Object>> linkGroups = new ArrayList<>();
        linkGroups.add(group);

        InstitutionPreferencesServiceImpl preferencesServiceImpl = new NoPermissionsInstitutionPreferencesServiceImpl();
        List<Map<String, Object>> processedLinkGroups = preferencesServiceImpl.removeGeneratedLabels(linkGroups);

        Map<String, Object> processedGroup = processedLinkGroups.get(0);
        Map<String, Object> processedGroupLinks = (Map<String, Object>) processedGroup.get(KFSPropertyConstants.LINKS);
        List<Map<String, String>> processedActivitiesLinks = (List<Map<String, String>>) processedGroupLinks.get("activities");
        List<Map<String, String>> processedAdministrationLinks = (List<Map<String, String>>) processedGroupLinks.get("administration");

        Assert.assertTrue(!processedActivitiesLinks.get(0).containsKey(KFSPropertyConstants.LABEL));
        Assert.assertEquals("BA", processedActivitiesLinks.get(0).get(KFSPropertyConstants.DOCUMENT_TYPE_CODE));

        Assert.assertTrue(processedActivitiesLinks.get(1).containsKey(KFSPropertyConstants.LABEL));
        Assert.assertEquals("Budget Construction Selection", processedActivitiesLinks.get(1).get(KFSPropertyConstants.LABEL));

        Assert.assertTrue(!processedAdministrationLinks.get(0).containsKey(KFSPropertyConstants.LABEL));
        Assert.assertEquals("org.kuali.kfs.coa.businessobject.IndirectCostRecoveryRateDetail", processedAdministrationLinks.get(0).get(KFSPropertyConstants.BUSINESS_OBJECT_CLASS));
    }

    @Test
    public void testGetMenu() {
        InstitutionPreferencesServiceImpl preferencesServiceImpl = new NoPermissionsInstitutionPreferencesServiceImpl();
        preferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferencesWithMenu());

        List<Map<String, String>> menu = preferencesServiceImpl.getMenu();
        Assert.assertEquals("Feedback", menu.get(0).get(KFSPropertyConstants.LABEL));
        Assert.assertEquals("kr/kualiFeedbackReport.do", menu.get(0).get(KFSPropertyConstants.LINK));
        Assert.assertEquals("Help", menu.get(1).get(KFSPropertyConstants.LABEL));
        Assert.assertEquals("static/help/default.htm", menu.get(1).get(KFSPropertyConstants.LINK));
    }

    @Test
    public void testSaveMenu() {
        InstitutionPreferencesServiceImpl preferencesServiceImpl = new NoPermissionsInstitutionPreferencesServiceImpl();
        preferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferences());

        String newMenu = "[{\"label\": \"Help\", \"link\": \"myhelp.html\"}, {\"label\": \"Feedback\", \"link\": \"feedback.html\"}]";
        List<Map<String, String>> savedMenu = preferencesServiceImpl.saveMenu(newMenu);
        Assert.assertTrue(savedMenu.get(0).containsKey(KFSPropertyConstants.LABEL));
        Assert.assertEquals("Help", savedMenu.get(0).get(KFSPropertyConstants.LABEL));
        Assert.assertTrue(savedMenu.get(0).containsKey(KFSPropertyConstants.LINK));
        Assert.assertEquals("myhelp.html", savedMenu.get(0).get(KFSPropertyConstants.LINK));
    }

    @Test
    public void testSaveMenuInvalidJson() {
        InstitutionPreferencesServiceImpl preferencesServiceImpl = new NoPermissionsInstitutionPreferencesServiceImpl();
        preferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferences());

        String newMenu = "[\"not\": \"valid\"}]";

        exception.expect(RuntimeException.class);
        exception.expectMessage("Error parsing json");
        preferencesServiceImpl.saveMenu(newMenu);
    }

    @Test
    public void testGetLogo() {
        InstitutionPreferencesServiceImpl preferencesServiceImpl = new NoPermissionsInstitutionPreferencesServiceImpl();
        preferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferences());

        Map<String, String> logoUrl = preferencesServiceImpl.getLogo();
        Assert.assertEquals("static/images/out-of-the-box-logo-rtna.png", logoUrl.get(KFSPropertyConstants.LOGO_URL));
    }

    @Test
    public void testSaveLogo() {
        InstitutionPreferencesServiceImpl preferencesServiceImpl = new NoPermissionsInstitutionPreferencesServiceImpl();
        preferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferences());

        String newLogo = "{\"logoUrl\": \"mytestimage.png\"}";
        Map<String, String> savedLogo = preferencesServiceImpl.saveLogo(newLogo);
        Assert.assertTrue(savedLogo.containsKey(KFSPropertyConstants.LOGO_URL));
        Assert.assertEquals("mytestimage.png", savedLogo.get(KFSPropertyConstants.LOGO_URL));
    }

    @Test
    public void testSaveLogoMissingLogoUrlKey() {
        InstitutionPreferencesServiceImpl preferencesServiceImpl = new NoPermissionsInstitutionPreferencesServiceImpl();
        preferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferences());

        String newLogo = "{\"notLogoUrl\": \"mytestimage.png\"}";

        exception.expect(RuntimeException.class);
        exception.expectMessage("Invalid JSON. Should contain logoUrl.");
        preferencesServiceImpl.saveLogo(newLogo);
    }

    @Test
    public void testSaveLogoInvalidJSON() {
        InstitutionPreferencesServiceImpl preferencesServiceImpl = new NoPermissionsInstitutionPreferencesServiceImpl();
        preferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferences());

        String newLogo = "\"notLogoUrl\": \"mytestimage.png\"}";

        exception.expect(RuntimeException.class);
        exception.expectMessage("Error parsing json");
        preferencesServiceImpl.saveLogo(newLogo);
    }

    @Test
    public void testFindInstitutionPreferencesLinks_RiceLookupLinkHasReturnLocation() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new InstitutionPreferencesServiceImpl();

        Map<String, String> link = new ConcurrentHashMap<>();
        link.put("link", "kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.location.impl.state.StateBo&docFormKey=88888888&hideReturnLink=true");
        link.put("label", "State");
        link.put("linkType", "rice");
        link.put("type", "reference");

        List<Map<String, Object>> linkGroups = buildLinkGroup(link);

        institutionPreferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferencesWithLinkGroups(linkGroups));
        institutionPreferencesServiceImpl.setDocumentDictionaryService(new StubDocumentDictionaryService());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());
        institutionPreferencesServiceImpl.setKualiModuleService(new StubKualiModuleService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesLinks(new TestPerson(), false);

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertNotNull("Link Groups should exist", preferences.get("linkGroups"));
        Assert.assertTrue("Link Groups should be a List", (preferences.get("linkGroups") instanceof List));
        Assert.assertTrue("Link Groups should not be empty", !CollectionUtils.isEmpty((List) preferences.get("linkGroups")));
        Assert.assertTrue("Link Groups should have a label", !StringUtils.isBlank((String) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("label")));
        Assert.assertTrue("Link groups should have links", !CollectionUtils.isEmpty(((Map<String, List<Map<String, String>>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get("reference")));

        Map<String, String> testLink = getLinkAt(preferences, 0, "reference", 0);

        Assert.assertTrue("Link should have a label", !StringUtils.isBlank(testLink.get("label")));
        Assert.assertTrue("Link should have a link type", !StringUtils.isBlank(testLink.get("linkType")));

        String groupLink = testLink.get("link");
        Assert.assertTrue("Link should have a link", !StringUtils.isBlank(groupLink));

        Assert.assertEquals("Link should be generated correctly", "http://tst.rice.kuali.org/kr-tst/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.location.impl.state.StateBo&docFormKey=88888888&hideReturnLink=true&returnLocation=http://tst.kfs.kuali.org/kfs-tst/portal.do", groupLink);
    }

    @Test
    public void testFindInstitutionPreferencesLinks_RiceNonLookupLinkLacksReturnLocation() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new InstitutionPreferencesServiceImpl();

        Map<String, String> link = new ConcurrentHashMap<>();
        link.put("link", "ksb/ThreadPool.do");
        link.put("label", "Thread Pool");
        link.put("linkType", "rice");
        link.put("type", "reference");

        List<Map<String, Object>> linkGroups = buildLinkGroup(link);

        institutionPreferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferencesWithLinkGroups(linkGroups));
        institutionPreferencesServiceImpl.setDocumentDictionaryService(new StubDocumentDictionaryService());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());
        institutionPreferencesServiceImpl.setKualiModuleService(new StubKualiModuleService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesLinks(new TestPerson(), false);

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertNotNull("Link Groups should exist", preferences.get("linkGroups"));
        Assert.assertTrue("Link Groups should be a List", (preferences.get("linkGroups") instanceof List));
        Assert.assertTrue("Link Groups should not be empty", !CollectionUtils.isEmpty((List) preferences.get("linkGroups")));
        Assert.assertTrue("Link Groups should have a label", !StringUtils.isBlank((String) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("label")));
        Assert.assertTrue("Link groups should have links", !CollectionUtils.isEmpty(((Map<String, List<Map<String, String>>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get("reference")));

        Map<String, String> testLink = getLinkAt(preferences, 0, "reference", 0);

        Assert.assertTrue("Link should have a label", !StringUtils.isBlank(testLink.get("label")));
        Assert.assertTrue("Link should have a link type", !StringUtils.isBlank(testLink.get("linkType")));

        String groupLink = testLink.get("link");
        Assert.assertTrue("Link should have a link", !StringUtils.isBlank(groupLink));

        Assert.assertEquals("Link should be generated correctly", "http://tst.rice.kuali.org/kr-tst/ksb/ThreadPool.do", groupLink);
    }

    protected class StubDocumentDictionaryService implements DocumentDictionaryService {
        @Override
        public String getLabel(String documentTypeName) {
            if (StringUtils.equals(documentTypeName, "SB")) {
                return "Service Billing";
            } else if (StringUtils.equals(documentTypeName, "CCTY")) {
                return "Credit Card Type";
            } else if (StringUtils.equals(documentTypeName, "GORV")) {
                return "Organization Reversion Global";
            }
            return null;
        }

        @Override
        public String getMaintenanceDocumentTypeName(Class dataObjectClass) {
            return null;
        }

        @Override
        public String getDescription(String documentTypeName) {
            return null;
        }

        @Override
        public Collection getDefaultExistenceChecks(Class dataObjectClass) {
            return null;
        }

        @Override
        public Collection getDefaultExistenceChecks(Document document) {
            return null;
        }

        @Override
        public Collection getDefaultExistenceChecks(String docTypeName) {
            return null;
        }

        @Override
        public Class<?> getMaintenanceDataObjectClass(String docTypeName) {
            if (StringUtils.equals(docTypeName, "CCTY")) {
                return CreditCardType.class;
            } else if (StringUtils.equals(docTypeName, "GORV")) {
                return OrganizationReversionGlobal.class;
            }
            return null;
        }

        @Override
        public Class<? extends Maintainable> getMaintainableClass(String docTypeName) {
            return null;
        }

        @Override
        public Class<? extends BusinessRule> getBusinessRulesClass(Document document) {
            return null;
        }

        @Override
        public Boolean getAllowsCopy(Document document) {
            return null;
        }

        @Override
        public Boolean getAllowsNewOrCopy(String docTypeName) {
            return null;
        }

        @Override
        public DocumentEntry getDocumentEntry(String docTypeName) {
            return null;
        }

        @Override
        public DocumentEntry getDocumentEntryByClass(Class<? extends Document> documentClass) {
            return null;
        }

        @Override
        public MaintenanceDocumentEntry getMaintenanceDocumentEntry(String docTypeName) {
            return null;
        }

        @Override
        public Class<?> getDocumentClassByName(String documentTypeName) {
            if (StringUtils.equals(documentTypeName, "SB")) {
                return ServiceBillingDocument.class;
            } else if (StringUtils.equals(documentTypeName, "CCTY") || StringUtils.equals(documentTypeName, "GORV")) {
                return FinancialSystemMaintenanceDocument.class;
            }
            return null;
        }

        @Override
        public String getDocumentTypeByClass(Class<? extends Document> documentClass) {
            return null;
        }

        @Override
        public Boolean getAllowsRecordDeletion(Class dataObjectClass) {
            return null;
        }

        @Override
        public Boolean getAllowsRecordDeletion(MaintenanceDocument document) {
            return null;
        }

        @Override
        public List<String> getLockingKeys(String docTypeName) {
            return null;
        }

        @Override
        public boolean getPreserveLockingKeysOnCopy(Class dataObjectClass) {
            return false;
        }

        @Override
        public DocumentAuthorizer getDocumentAuthorizer(String documentType) {
            return null;
        }

        @Override
        public DocumentAuthorizer getDocumentAuthorizer(Document document) {
            return null;
        }

        @Override
        public DocumentPresentationController getDocumentPresentationController(String documentType) {
            return null;
        }

        @Override
        public DocumentPresentationController getDocumentPresentationController(Document document) {
            return null;
        }
    }

    protected class StubConfigurationService implements ConfigurationService {
        @Override
        public String getPropertyValueAsString(String s) {
            if (StringUtils.equals(s, KFSConstants.APPLICATION_URL_KEY)) {
                return "http://tst.kfs.kuali.org/kfs-tst";
            } else if (StringUtils.equals(s, KRADConstants.WORKFLOW_URL_KEY)) {
                return "http://tst.kfs.kuali.org/kfs-tst/kew";
            } else if (StringUtils.equals(s, KFSConstants.RICE_SERVER_URL_KEY)) {
                return "http://tst.rice.kuali.org/kr-tst";
            } else if (StringUtils.equals(s, KFSConstants.REMOTE_VIEW_URL_KEY)) {
                return "http://tst.rice.kuali.org/kr-tst/remote";
            }
            return null;
        }

        @Override
        public boolean getPropertyValueAsBoolean(String s) {
            return false;
        }

        @Override
        public boolean getPropertyValueAsBoolean(String s, boolean defValue) {
            return defValue;
        }

        @Override
        public Map<String, String> getAllProperties() {
            return null;
        }
    }

    protected class StubKualiModuleService implements KualiModuleService {
        @Override
        public List<ModuleService> getInstalledModuleServices() {
            return null;
        }

        @Override
        public ModuleService getModuleService(String moduleId) {
            return null;
        }

        @Override
        public ModuleService getModuleServiceByNamespaceCode(String namespaceCode) {
            return null;
        }

        @Override
        public boolean isModuleServiceInstalled(String namespaceCode) {
            return false;
        }

        @Override
        public ModuleService getResponsibleModuleService(Class boClass) {
            if (boClass.equals(ServiceBillingDocument.class) || boClass.equals(CreditCardType.class)) {
                return new ModuleService() {
                    @Override
                    public ModuleConfiguration getModuleConfiguration() {
                        FinancialSystemModuleConfiguration moduleConfig = new FinancialSystemModuleConfiguration();
                        moduleConfig.setNamespaceCode("KFS-FP");
                        return moduleConfig;
                    }

                    @Override
                    public boolean isResponsibleFor(Class businessObjectClass) {
                        return false;
                    }

                    @Override
                    public boolean isResponsibleForJob(String jobName) {
                        return false;
                    }

                    @Override
                    public List listPrimaryKeyFieldNames(Class businessObjectInterfaceClass) {
                        return null;
                    }

                    @Override
                    public List<List<String>> listAlternatePrimaryKeyFieldNames(Class businessObjectInterfaceClass) {
                        return null;
                    }

                    @Override
                    public BusinessObjectEntry getExternalizableBusinessObjectDictionaryEntry(Class businessObjectInterfaceClass) {
                        return null;
                    }

                    @Override
                    public <T extends ExternalizableBusinessObject> T getExternalizableBusinessObject(Class<T> businessObjectClass, Map<String, Object> fieldValues) {
                        return null;
                    }

                    @Override
                    public <T extends ExternalizableBusinessObject> List<T> getExternalizableBusinessObjectsList(Class<T> businessObjectClass, Map<String, Object> fieldValues) {
                        return null;
                    }

                    @Override
                    public <T extends ExternalizableBusinessObject> List<T> getExternalizableBusinessObjectsListForLookup(Class<T> businessObjectClass, Map<String, Object> fieldValues, boolean unbounded) {
                        return null;
                    }

                    @Override
                    public String getExternalizableDataObjectInquiryUrl(Class<?> inquiryDataObjectClass, Properties parameters) {
                        return null;
                    }

                    @Override
                    public String getExternalizableDataObjectLookupUrl(Class<?> inquiryDataObjectClass, Properties parameters) {
                        return null;
                    }

                    @Override
                    public String getExternalizableBusinessObjectInquiryUrl(Class inquiryBusinessObjectClass, Map<String, String[]> parameters) {
                        return null;
                    }

                    @Override
                    public String getExternalizableBusinessObjectLookupUrl(Class inquiryBusinessObjectClass, Map<String, String> parameters) {
                        return null;
                    }

                    @Override
                    public <T extends ExternalizableBusinessObject> T retrieveExternalizableBusinessObjectIfNecessary(BusinessObject businessObject, T currentInstanceExternalizableBO, String externalizableRelationshipName) {
                        return null;
                    }

                    @Override
                    public <T extends ExternalizableBusinessObject> List<T> retrieveExternalizableBusinessObjectsList(BusinessObject businessObject, String externalizableRelationshipName, Class<T> externalizableClazz) {
                        return null;
                    }

                    @Override
                    public boolean isExternalizable(Class boClass) {
                        return false;
                    }

                    @Override
                    public boolean isExternalizableBusinessObjectLookupable(Class boClass) {
                        return false;
                    }

                    @Override
                    public boolean isExternalizableBusinessObjectInquirable(Class boClass) {
                        return false;
                    }

                    @Override
                    public <T extends ExternalizableBusinessObject> T createNewObjectFromExternalizableClass(Class<T> boClass) {
                        return null;
                    }

                    @Override
                    public <E extends ExternalizableBusinessObject> Class<E> getExternalizableBusinessObjectImplementation(Class<E> externalizableBusinessObjectInterface) {
                        return null;
                    }

                    @Override
                    public boolean isLocked() {
                        return false;
                    }

                    @Override
                    public boolean goToCentralRiceForInquiry() {
                        return false;
                    }

                    @Override
                    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

                    }

                    @Override
                    public void afterPropertiesSet() throws Exception {

                    }

                    @Override
                    public boolean isExternal(Class boClass) {
                        return false;
                    }
                };
            }
            return null;
        }

        @Override
        public ModuleService getResponsibleModuleServiceForJob(String jobName) {
            return null;
        }

        @Override
        public void setInstalledModuleServices(List<ModuleService> moduleServices) {

        }

        @Override
        public List<String> getDataDictionaryPackages() {
            return null;
        }

        @Override
        public String getNamespaceName(String namespaceCode) {
            return null;
        }

        @Override
        public String getNamespaceCode(Class<?> documentOrStepClass) {
            return null;
        }

        @Override
        public String getComponentCode(Class<?> documentOrStepClass) {
            return null;
        }

        @Override
        public boolean isBusinessObjectExternal(String boClassName) {
            return false;
        }
    }

    class NoPermissionsInstitutionPreferencesServiceImpl extends InstitutionPreferencesServiceImpl {
        @Override
        protected boolean canViewLink(Map<String, Object> permission, Person person) {
            return true;
        }

        @Override
        protected boolean canInitiateDocument(String documentTypeName, Person person) {
            return true;
        }

        @Override
        protected boolean canViewBusinessObjectLookup(Class<?> businessObjectClass, Person person) {
            return true;
        }
    }
}
