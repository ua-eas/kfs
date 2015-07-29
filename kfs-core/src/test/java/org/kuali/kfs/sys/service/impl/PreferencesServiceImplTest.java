package org.kuali.kfs.sys.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.kuali.kfs.sys.dataaccess.PreferencesDao;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PreferencesServiceImplTest {
    @Test
    public void testFindInstitutionPreferences_NoLinkGroups() {
        PreferencesServiceImpl preferencesServiceImpl = new PreferencesServiceImpl();
        preferencesServiceImpl.setPreferencesDao(new PreferencesDao() {
            @Override
            public Map<String, Object> findInstitutionPreferences() {
                Map<String, Object> ip = new ConcurrentHashMap<>();
                ip.put("institutionId","123413535");
                ip.put("logoUrl", "https://s3.amazonaws.com/images.kfs.kuali.org/monsters-u-logo.jpg");
                ip.put("institutionName", "Monsters");
                return ip;
            }
        });
        Map<String, Object> preferences = preferencesServiceImpl.findInstitutionPreferences();
        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertNull("Link Groups should not exist", preferences.get("linkGroups"));
    }

    @Test
    public void testFindInstitutionPreferences_HealthyLinkGroup() {
        PreferencesServiceImpl preferencesServiceImpl = new PreferencesServiceImpl();
        preferencesServiceImpl.setPreferencesDao(new PreferencesDao() {
            @Override
            public Map<String, Object> findInstitutionPreferences() {
                Map<String, Object> ip = new ConcurrentHashMap<>();
                ip.put("institutionId", "123413535");
                ip.put("logoUrl", "https://s3.amazonaws.com/images.kfs.kuali.org/monsters-u-logo.jpg");
                ip.put("institutionName", "Monsters");

                Map<String, String> link = new ConcurrentHashMap<>();
                link.put("documentTypeCode", "REQS");

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
        Map<String, Object> preferences = preferencesServiceImpl.findInstitutionPreferences();
        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertNotNull("Link Groups should exist", preferences.get("linkGroups"));
        Assert.assertTrue("Link Groups should be a List", (preferences.get("linkGroups") instanceof List));
        Assert.assertTrue("Link Groups should not be empty", !CollectionUtils.isEmpty((List) preferences.get("linkGroups")));
        Assert.assertTrue("Link Groups should have a label", !StringUtils.isBlank((String) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("label")));
        Assert.assertTrue("Link groups should have links", !CollectionUtils.isEmpty((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")));
    }

    @Test
    public void testFindInstitutionPreferences_DocumentTypeLinkIsTransformed() {
        PreferencesServiceImpl preferencesServiceImpl = new PreferencesServiceImpl();
        preferencesServiceImpl.setPreferencesDao(new PreferencesDao() {
            @Override
            public Map<String, Object> findInstitutionPreferences() {
                Map<String, Object> ip = new ConcurrentHashMap<>();
                ip.put("institutionId", "123413535");
                ip.put("logoUrl", "https://s3.amazonaws.com/images.kfs.kuali.org/monsters-u-logo.jpg");
                ip.put("institutionName", "Monsters");

                Map<String, String> link = new ConcurrentHashMap<>();
                link.put("documentTypeCode", "REQS");

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
        Map<String, Object> preferences = preferencesServiceImpl.findInstitutionPreferences();
        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertNotNull("Link Groups should exist", preferences.get("linkGroups"));
        Assert.assertTrue("Link Groups should be a List", (preferences.get("linkGroups") instanceof List));
        Assert.assertTrue("Link Groups should not be empty", !CollectionUtils.isEmpty((List) preferences.get("linkGroups")));
        Assert.assertTrue("Link Groups should have a label", !StringUtils.isBlank((String) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("label")));
        Assert.assertTrue("Link groups should have links", !CollectionUtils.isEmpty((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")));

        Assert.assertTrue("Link should have a label", !StringUtils.isBlank(((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get(0).get("label")));
        Assert.assertTrue("Link should have a link", !StringUtils.isBlank(((List<Map<String, String>>)((List<Map<String, Object>>)preferences.get("linkGroups")).get(0).get("links")).get(0).get("link")));
        Assert.assertTrue("Link should NOT have a document type", StringUtils.isBlank(((List<Map<String, String>>)((List<Map<String, Object>>)preferences.get("linkGroups")).get(0).get("links")).get(0).get("documentTypeCode")));
    }
}
