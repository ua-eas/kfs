package org.kuali.kfs.sys.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.kuali.kfs.coa.businessobject.OrganizationReversionGlobal;
import org.kuali.kfs.fp.businessobject.CreditCardType;
import org.kuali.kfs.fp.document.ServiceBillingDocument;
import org.kuali.kfs.sys.FinancialSystemModuleConfiguration;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.dataaccess.PreferencesDao;
import org.junit.Assert;
import org.kuali.kfs.sys.document.FinancialSystemMaintenanceDocument;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.kuali.rice.krad.bo.ModuleConfiguration;
import org.kuali.rice.krad.datadictionary.BusinessObjectEntry;
import org.kuali.rice.krad.datadictionary.DocumentEntry;
import org.kuali.rice.krad.datadictionary.MaintenanceDocumentEntry;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.document.DocumentAuthorizer;
import org.kuali.rice.krad.document.DocumentPresentationController;
import org.kuali.rice.krad.maintenance.Maintainable;
import org.kuali.rice.krad.maintenance.MaintenanceDocument;
import org.kuali.rice.krad.rules.rule.BusinessRule;
import org.kuali.rice.krad.service.DocumentDictionaryService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.util.KRADConstants;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PreferencesServiceImplTest {
    abstract class PreferencesDaoInstitionPreferences implements PreferencesDao {
        @Override
        public Map<String, Object> getUserPreferences(String principalName) {
            return null;
        }

        @Override
        public void saveUserPreferences(String principalName, String preferences) {

        }
    }

    @Test
    public void testFindInstitutionPreferences_NoLinkGroups() {
        PreferencesServiceImpl preferencesServiceImpl = new PreferencesServiceImpl();
        preferencesServiceImpl.setPreferencesDao(new PreferencesDaoInstitionPreferences() {
            @Override
            public Map<String, Object> findInstitutionPreferences() {
                Map<String, Object> ip = new ConcurrentHashMap<>();
                ip.put("institutionId","123413535");
                ip.put("logoUrl", "https://s3.amazonaws.com/images.kfs.kuali.org/monsters-u-logo.jpg");
                ip.put("institutionName", "Monsters");
                return ip;
            }
        });
        preferencesServiceImpl.setConfigurationService(new StubConfigurationService());

        Map<String, Object> preferences = preferencesServiceImpl.findInstitutionPreferences();

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertNull("Link Groups should not exist", preferences.get("linkGroups"));
    }

    @Test
    public void testFindInstitutionPreferences_HasActionList() {
        PreferencesServiceImpl preferencesServiceImpl = new PreferencesServiceImpl();
        preferencesServiceImpl.setPreferencesDao(new PreferencesDaoInstitionPreferences() {
            @Override
            public Map<String, Object> findInstitutionPreferences() {
                Map<String, Object> ip = new ConcurrentHashMap<>();
                ip.put("institutionId", "123413535");
                ip.put("logoUrl", "https://s3.amazonaws.com/images.kfs.kuali.org/monsters-u-logo.jpg");
                ip.put("institutionName", "Monsters");
                return ip;
            }
        });
        preferencesServiceImpl.setConfigurationService(new StubConfigurationService());

        Map<String, Object> preferences = preferencesServiceImpl.findInstitutionPreferences();

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertTrue("Preferences should always include an action list url", preferences.containsKey("actionListUrl"));
        Assert.assertEquals("We should know what action list is", "http://tst.kfs.kuali.org/kfs-tst/kew/ActionList.do", preferences.get("actionListUrl"));
    }

    @Test
    public void testFindInstitutionPreferences_HasSignoutUrl() {
        PreferencesServiceImpl preferencesServiceImpl = new PreferencesServiceImpl();
        preferencesServiceImpl.setPreferencesDao(new PreferencesDaoInstitionPreferences() {
            @Override
            public Map<String, Object> findInstitutionPreferences() {
                Map<String, Object> ip = new ConcurrentHashMap<>();
                ip.put("institutionId", "123413535");
                ip.put("logoUrl", "https://s3.amazonaws.com/images.kfs.kuali.org/monsters-u-logo.jpg");
                ip.put("institutionName", "Monsters");
                return ip;
            }
        });
        preferencesServiceImpl.setConfigurationService(new StubConfigurationService());

        Map<String, Object> preferences = preferencesServiceImpl.findInstitutionPreferences();

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertTrue("Preferences should always include a signoutUrl", preferences.containsKey("signoutUrl"));
        Assert.assertEquals("We should know what the signoutUrl is", "http://tst.kfs.kuali.org/kfs-tst/logout.do", preferences.get("signoutUrl"));
    }

    @Test
    public void testFindInstitutionPreferences_HasDocSearch() {
        PreferencesServiceImpl preferencesServiceImpl = new PreferencesServiceImpl();
        preferencesServiceImpl.setPreferencesDao(new PreferencesDaoInstitionPreferences() {
            @Override
            public Map<String, Object> findInstitutionPreferences() {
                Map<String, Object> ip = new ConcurrentHashMap<>();
                ip.put("institutionId", "123413535");
                ip.put("logoUrl", "https://s3.amazonaws.com/images.kfs.kuali.org/monsters-u-logo.jpg");
                ip.put("institutionName", "Monsters");
                return ip;
            }
        });
        preferencesServiceImpl.setConfigurationService(new StubConfigurationService());

        Map<String, Object> preferences = preferencesServiceImpl.findInstitutionPreferences();

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertTrue("Preferences should always include a doc search url", preferences.containsKey("actionListUrl"));
        Assert.assertEquals("We should know what doc search is", "http://tst.kfs.kuali.org/kfs-tst/kew/DocumentSearch.do?docFormKey=88888888&hideReturnLink=true&returnLocation=http://tst.kfs.kuali.org/kfs-tst/index.jsp", preferences.get("docSearchUrl"));
    }

    @Test
    public void testFindInstitutionPreferences_HasHelp() {
        PreferencesServiceImpl preferencesServiceImpl = new PreferencesServiceImpl();
        preferencesServiceImpl.setPreferencesDao(new PreferencesDaoInstitionPreferences() {
            @Override
            public Map<String, Object> findInstitutionPreferences() {
                Map<String, Object> ip = new ConcurrentHashMap<>();
                ip.put("institutionId", "123413535");
                ip.put("logoUrl", "https://s3.amazonaws.com/images.kfs.kuali.org/monsters-u-logo.jpg");
                ip.put("institutionName", "Monsters");

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
        });
        preferencesServiceImpl.setConfigurationService(new StubConfigurationService());

        Map<String, Object> preferences = preferencesServiceImpl.findInstitutionPreferences();

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertTrue("Preferences should always include a help url", StringUtils.isNotBlank(getMenuLinkUrl(preferences, "Help")));
        Assert.assertEquals("We should know what help url is", "http://tst.kfs.kuali.org/kfs-tst/static/help/default.htm", getMenuLinkUrl(preferences, "Help"));
    }

    private String getMenuLinkUrl(Map<String, Object> preferences, String key) {
        List<Map<String, String>> menuItems = (List<Map<String, String>>)preferences.get("menu");
        if (CollectionUtils.isNotEmpty(menuItems)) {
            for (Map<String, String> menuItem: menuItems) {
                String label = menuItem.get("label");
                if (StringUtils.equals(label, key)) {
                    return menuItem.get("link");
                }
            }
        }
        return StringUtils.EMPTY;
    }

    @Test
    public void testFindInstitutionPreferences_HasFeedback() {
        PreferencesServiceImpl preferencesServiceImpl = new PreferencesServiceImpl();
        preferencesServiceImpl.setPreferencesDao(new PreferencesDaoInstitionPreferences() {
            @Override
            public Map<String, Object> findInstitutionPreferences() {
                Map<String, Object> ip = new ConcurrentHashMap<>();
                ip.put("institutionId", "123413535");
                ip.put("logoUrl", "https://s3.amazonaws.com/images.kfs.kuali.org/monsters-u-logo.jpg");
                ip.put("institutionName", "Monsters");

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
        });
        preferencesServiceImpl.setConfigurationService(new StubConfigurationService());

        Map<String, Object> preferences = preferencesServiceImpl.findInstitutionPreferences();

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertTrue("Preferences should always include a feedback url", StringUtils.isNotBlank(getMenuLinkUrl(preferences, "Feedback")));
        Assert.assertEquals("We should know what feedback url is", "http://tst.kfs.kuali.org/kfs-tst/kr/kualiFeedbackReport.do", getMenuLinkUrl(preferences, "Feedback"));
    }

    @Test
    public void testFindInstitutionPreferences_HealthyLinkGroup() {
        PreferencesServiceImpl preferencesServiceImpl = new PreferencesServiceImpl();
        preferencesServiceImpl.setPreferencesDao(new PreferencesDaoInstitionPreferences() {
            @Override
            public Map<String, Object> findInstitutionPreferences() {
                Map<String, Object> ip = new ConcurrentHashMap<>();
                ip.put("institutionId", "123413535");
                ip.put("logoUrl", "https://s3.amazonaws.com/images.kfs.kuali.org/monsters-u-logo.jpg");
                ip.put("institutionName", "Monsters");

                Map<String, String> link = new ConcurrentHashMap<>();
                link.put("documentTypeCode", "SB");

                List<Map<String, String>> links = new ArrayList<>();
                links.add(link);

                Map<String, Object> linkGroup = new ConcurrentHashMap<>();
                linkGroup.put("label", "Test Menu");
                linkGroup.put("links", links);

                List<Map<String, Object>> linkGroups = new ArrayList<>();
                linkGroups.add(linkGroup);

                ip.put("linkGroups", linkGroups);

                return ip;
            }
        });
        preferencesServiceImpl.setDocumentDictionaryService(new StubDocumentDictionaryService());
        preferencesServiceImpl.setConfigurationService(new StubConfigurationService());
        preferencesServiceImpl.setKualiModuleService(new StubKualiModuleService());

        Map<String, Object> preferences = preferencesServiceImpl.findInstitutionPreferences();

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertNotNull("Link Groups should exist", preferences.get("linkGroups"));
        Assert.assertTrue("Link Groups should be a List", (preferences.get("linkGroups") instanceof List));
        Assert.assertTrue("Link Groups should not be empty", !CollectionUtils.isEmpty((List) preferences.get("linkGroups")));
        Assert.assertTrue("Link Groups should have a label", !StringUtils.isBlank((String) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("label")));
        Assert.assertTrue("Link groups should have links", !CollectionUtils.isEmpty((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")));
    }

    @Test
    public void testFindInstitutionPreferences_TransactionalDocumentTypeLinkIsTransformed() {
        PreferencesServiceImpl preferencesServiceImpl = new PreferencesServiceImpl();
        preferencesServiceImpl.setPreferencesDao(new PreferencesDaoInstitionPreferences() {
            @Override
            public Map<String, Object> findInstitutionPreferences() {
                Map<String, Object> ip = new ConcurrentHashMap<>();
                ip.put("institutionId", "123413535");
                ip.put("logoUrl", "https://s3.amazonaws.com/images.kfs.kuali.org/monsters-u-logo.jpg");
                ip.put("institutionName", "Monsters");

                Map<String, String> link = new ConcurrentHashMap<>();
                link.put("documentTypeCode", "SB");

                List<Map<String, String>> links = new ArrayList<>();
                links.add(link);

                Map<String, Object> linkGroup = new ConcurrentHashMap<>();
                linkGroup.put("label", "Test Menu");
                linkGroup.put("links", links);

                List<Map<String, Object>> linkGroups = new ArrayList<>();
                linkGroups.add(linkGroup);

                ip.put("linkGroups", linkGroups);

                return ip;
            }
        });
        preferencesServiceImpl.setDocumentDictionaryService(new StubDocumentDictionaryService());
        preferencesServiceImpl.setConfigurationService(new StubConfigurationService());
        preferencesServiceImpl.setKualiModuleService(new StubKualiModuleService());

        Map<String, Object> preferences = preferencesServiceImpl.findInstitutionPreferences();

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertNotNull("Link Groups should exist", preferences.get("linkGroups"));
        Assert.assertTrue("Link Groups should be a List", (preferences.get("linkGroups") instanceof List));
        Assert.assertTrue("Link Groups should not be empty", !CollectionUtils.isEmpty((List) preferences.get("linkGroups")));
        Assert.assertTrue("Link Groups should have a label", !StringUtils.isBlank((String) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("label")));
        Assert.assertTrue("Link groups should have links", !CollectionUtils.isEmpty((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")));

        Assert.assertTrue("Link should have a label", !StringUtils.isBlank(((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get(0).get("label")));

        String link = ((List<Map<String, String>>)((List<Map<String, Object>>)preferences.get("linkGroups")).get(0).get("links")).get(0).get("link");
        Assert.assertTrue("Link should have a link", !StringUtils.isBlank(link));
        Assert.assertEquals("Link should be generated correctly", "http://tst.kfs.kuali.org/kfs-tst/financialServiceBilling.do?methodToCall=docHandler&command=initiate&docTypeName=SB", link);
        Assert.assertTrue("Link should NOT have a document type", StringUtils.isBlank(((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get(0).get("documentTypeCode")));
    }

    @Test
    public void testFindInstitutionPreferences_MissingDocumentTypeReturnsNoLink() {
        PreferencesServiceImpl preferencesServiceImpl = new PreferencesServiceImpl();
        preferencesServiceImpl.setPreferencesDao(new PreferencesDaoInstitionPreferences() {
            @Override
            public Map<String, Object> findInstitutionPreferences() {
                Map<String, Object> ip = new ConcurrentHashMap<>();
                ip.put("institutionId", "123413535");
                ip.put("logoUrl", "https://s3.amazonaws.com/images.kfs.kuali.org/monsters-u-logo.jpg");
                ip.put("institutionName", "Monsters");

                Map<String, String> link = new ConcurrentHashMap<>();
                link.put("documentTypeCode", "SB");

                Map<String, String> link2 = new ConcurrentHashMap<>();
                link2.put("documentTypeCode", "ZZZZ");

                Map<String, String> link3 = new ConcurrentHashMap<>();
                link3.put("documentTypeCode", "CCR");

                List<Map<String, String>> links = new ArrayList<>();
                links.add(link);
                links.add(link2);
                links.add(link3);

                Map<String, Object> linkGroup = new ConcurrentHashMap<>();
                linkGroup.put("label", "Test Menu");
                linkGroup.put("links", links);

                List<Map<String, Object>> linkGroups = new ArrayList<>();
                linkGroups.add(linkGroup);

                ip.put("linkGroups", linkGroups);

                return ip;
            }
        });
        preferencesServiceImpl.setDocumentDictionaryService(new StubDocumentDictionaryService());
        preferencesServiceImpl.setConfigurationService(new StubConfigurationService());
        preferencesServiceImpl.setKualiModuleService(new StubKualiModuleService());

        Map<String, Object> preferences = preferencesServiceImpl.findInstitutionPreferences();

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertNotNull("Link Groups should exist", preferences.get("linkGroups"));
        Assert.assertTrue("Link Groups should be a List", (preferences.get("linkGroups") instanceof List));
        Assert.assertTrue("Link Groups should not be empty", !CollectionUtils.isEmpty((List) preferences.get("linkGroups")));
        Assert.assertTrue("Link Groups should have a label", !StringUtils.isBlank((String) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("label")));
        Assert.assertTrue("Link groups should have links", !CollectionUtils.isEmpty((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")));
        Assert.assertEquals("Link group should only have one link", 1, ((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).size());
        Assert.assertEquals("The one link should be for Service Billing", "Service Billing", ((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get(0).get("label"));
    }

    @Test
    public void testFindInstitutionPreferences_MaintenanceDocumentTypeLinkIsTransformed() {
        PreferencesServiceImpl preferencesServiceImpl = new PreferencesServiceImpl();
        preferencesServiceImpl.setPreferencesDao(new PreferencesDaoInstitionPreferences() {
            @Override
            public Map<String, Object> findInstitutionPreferences() {
                Map<String, Object> ip = new ConcurrentHashMap<>();
                ip.put("institutionId", "123413535");
                ip.put("logoUrl", "https://s3.amazonaws.com/images.kfs.kuali.org/monsters-u-logo.jpg");
                ip.put("institutionName", "Monsters");

                Map<String, String> link = new ConcurrentHashMap<>();
                link.put("documentTypeCode", "CCTY");

                List<Map<String, String>> links = new ArrayList<>();
                links.add(link);

                Map<String, Object> linkGroup = new ConcurrentHashMap<>();
                linkGroup.put("label", "Test Menu");
                linkGroup.put("links", links);

                List<Map<String, Object>> linkGroups = new ArrayList<>();
                linkGroups.add(linkGroup);

                ip.put("linkGroups", linkGroups);

                return ip;
            }
        });
        preferencesServiceImpl.setDocumentDictionaryService(new StubDocumentDictionaryService());
        preferencesServiceImpl.setConfigurationService(new StubConfigurationService());
        preferencesServiceImpl.setKualiModuleService(new StubKualiModuleService());

        Map<String, Object> preferences = preferencesServiceImpl.findInstitutionPreferences();

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertNotNull("Link Groups should exist", preferences.get("linkGroups"));
        Assert.assertTrue("Link Groups should be a List", (preferences.get("linkGroups") instanceof List));
        Assert.assertTrue("Link Groups should not be empty", !CollectionUtils.isEmpty((List) preferences.get("linkGroups")));
        Assert.assertTrue("Link Groups should have a label", !StringUtils.isBlank((String) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("label")));
        Assert.assertTrue("Link groups should have links", !CollectionUtils.isEmpty((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")));

        Assert.assertTrue("Link should have a label", !StringUtils.isBlank(((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get(0).get("label")));

        String link = ((List<Map<String, String>>)((List<Map<String, Object>>)preferences.get("linkGroups")).get(0).get("links")).get(0).get("link");
        Assert.assertTrue("Link should have a link", !StringUtils.isBlank(link));

        Assert.assertEquals("Link should be generated correctly", "http://tst.kfs.kuali.org/kfs-tst/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.fp.businessobject.CreditCardType&docFormKey=88888888&returnLocation=http://tst.kfs.kuali.org/kfs-tst/index.jsp&hideReturnLink=true", link);
        Assert.assertTrue("Link should NOT have a document type", StringUtils.isBlank(((List<Map<String, String>>)((List<Map<String, Object>>)preferences.get("linkGroups")).get(0).get("links")).get(0).get("documentTypeCode")));
    }

    @Test
    public void testFindInstitutionPreferences_GlobalMaintenanceDocumentTypeLinkIsTransformed() {
        PreferencesServiceImpl preferencesServiceImpl = new PreferencesServiceImpl();
        preferencesServiceImpl.setPreferencesDao(new PreferencesDaoInstitionPreferences() {
            @Override
            public Map<String, Object> findInstitutionPreferences() {
                Map<String, Object> ip = new ConcurrentHashMap<>();
                ip.put("institutionId", "123413535");
                ip.put("logoUrl", "https://s3.amazonaws.com/images.kfs.kuali.org/monsters-u-logo.jpg");
                ip.put("institutionName", "Monsters");

                Map<String, String> link = new ConcurrentHashMap<>();
                link.put("documentTypeCode", "GORV");

                List<Map<String, String>> links = new ArrayList<>();
                links.add(link);

                Map<String, Object> linkGroup = new ConcurrentHashMap<>();
                linkGroup.put("label", "Test Menu");
                linkGroup.put("links", links);

                List<Map<String, Object>> linkGroups = new ArrayList<>();
                linkGroups.add(linkGroup);

                ip.put("linkGroups", linkGroups);

                return ip;
            }
        });
        preferencesServiceImpl.setDocumentDictionaryService(new StubDocumentDictionaryService());
        preferencesServiceImpl.setConfigurationService(new StubConfigurationService());
        preferencesServiceImpl.setKualiModuleService(new StubKualiModuleService());

        Map<String, Object> preferences = preferencesServiceImpl.findInstitutionPreferences();

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertNotNull("Link Groups should exist", preferences.get("linkGroups"));
        Assert.assertTrue("Link Groups should be a List", (preferences.get("linkGroups") instanceof List));
        Assert.assertTrue("Link Groups should not be empty", !CollectionUtils.isEmpty((List) preferences.get("linkGroups")));
        Assert.assertTrue("Link Groups should have a label", !StringUtils.isBlank((String) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("label")));
        Assert.assertTrue("Link groups should have links", !CollectionUtils.isEmpty((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")));

        Assert.assertTrue("Link should have a label", !StringUtils.isBlank(((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get(0).get("label")));

        String link = ((List<Map<String, String>>)((List<Map<String, Object>>)preferences.get("linkGroups")).get(0).get("links")).get(0).get("link");
        Assert.assertTrue("Link should have a link", !StringUtils.isBlank(link));

        Assert.assertEquals("Link should be generated correctly", "http://tst.kfs.kuali.org/kfs-tst/kr/maintenance.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.OrganizationReversionGlobal&hideReturnLink=true", link);
        Assert.assertTrue("Link should NOT have a document type", StringUtils.isBlank(((List<Map<String, String>>)((List<Map<String, Object>>)preferences.get("linkGroups")).get(0).get("links")).get(0).get("documentTypeCode")));
    }

    @Test
    public void testFindInstitutionPreferences_RelativeLinkIsTransformed() {
        PreferencesServiceImpl preferencesServiceImpl = new PreferencesServiceImpl();
        preferencesServiceImpl.setPreferencesDao(new PreferencesDaoInstitionPreferences() {
            @Override
            public Map<String, Object> findInstitutionPreferences() {
                Map<String, Object> ip = new ConcurrentHashMap<>();
                ip.put("institutionId", "123413535");
                ip.put("logoUrl", "https://s3.amazonaws.com/images.kfs.kuali.org/monsters-u-logo.jpg");
                ip.put("institutionName", "Monsters");

                Map<String, String> link = new ConcurrentHashMap<>();
                link.put("link", "electronicFundTransfer.do?methodToCall=start");
                link.put("label", "Electronic Payment Claim");

                List<Map<String, String>> links = new ArrayList<>();
                links.add(link);

                Map<String, Object> linkGroup = new ConcurrentHashMap<>();
                linkGroup.put("label", "Test Menu");
                linkGroup.put("links", links);

                List<Map<String, Object>> linkGroups = new ArrayList<>();
                linkGroups.add(linkGroup);

                ip.put("linkGroups", linkGroups);

                return ip;
            }
        });
        preferencesServiceImpl.setDocumentDictionaryService(new StubDocumentDictionaryService());
        preferencesServiceImpl.setConfigurationService(new StubConfigurationService());
        preferencesServiceImpl.setKualiModuleService(new StubKualiModuleService());

        Map<String, Object> preferences = preferencesServiceImpl.findInstitutionPreferences();

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertNotNull("Link Groups should exist", preferences.get("linkGroups"));
        Assert.assertTrue("Link Groups should be a List", (preferences.get("linkGroups") instanceof List));
        Assert.assertTrue("Link Groups should not be empty", !CollectionUtils.isEmpty((List) preferences.get("linkGroups")));
        Assert.assertTrue("Link Groups should have a label", !StringUtils.isBlank((String) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("label")));
        Assert.assertTrue("Link groups should have links", !CollectionUtils.isEmpty((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")));

        Assert.assertTrue("Link should have a label", !StringUtils.isBlank(((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get(0).get("label")));

        String link = ((List<Map<String, String>>)((List<Map<String, Object>>)preferences.get("linkGroups")).get(0).get("links")).get(0).get("link");
        Assert.assertTrue("Link should have a link", !StringUtils.isBlank(link));

        Assert.assertEquals("Link should be generated correctly", "http://tst.kfs.kuali.org/kfs-tst/electronicFundTransfer.do?methodToCall=start", link);
        Assert.assertTrue("Link should NOT have a document type", StringUtils.isBlank(((List<Map<String, String>>)((List<Map<String, Object>>)preferences.get("linkGroups")).get(0).get("links")).get(0).get("documentTypeCode")));
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
            }
            return null;
        }

        @Override
        public boolean getPropertyValueAsBoolean(String s) {
            return false;
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
                ModuleService fpModule = new ModuleService() {
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
                };
                return fpModule;

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
    }
}
