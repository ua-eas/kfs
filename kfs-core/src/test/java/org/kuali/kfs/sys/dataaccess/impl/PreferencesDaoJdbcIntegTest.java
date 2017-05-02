/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
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

package org.kuali.kfs.sys.dataaccess.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.kuali.kfs.sys.dataaccess.PreferencesDao;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Assert;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/org/kuali/kfs/sys/dataaccess/impl/spring-sys-test-preferences-dao-jdbc.xml" })
@Transactional
public class PreferencesDaoJdbcIntegTest {
    private static final String NEW_TARGET_KEY = "newTarget";
    private static final String LOGO_URL_KEY = "logoUrl";
    private static final String LINK_GROUPS_KEY = "linkGroups";
    private static final String MENU_LINKS_KEY = "menu";
    private static final String LINKS_KEY = "links";
    private static final String LABEL_KEY = "label";
    private static final String LINK_KEY = "link";
    private static final String LINK_TYPE_KEY = "linkType";
    private static final String BO_CLS_KEY = "businessObjectClass";
    private static final String DOC_TYP_KEY = "documentTypeCode";
    private static final String PERMISSION_KEY = "permission";
    private static final String TMPL_NMSPC_KEY = "templateNamespace";
    private static final String TMPL_NM_KEY = "templateName";
    private static final String DETAILS_KEY = "details";

    private static final String PREFS_SIDEBAR_OUT_KEY = "sidebarOut";
    private static final String PREFS_CHECKED_LINK_FILTERS_KEY = "checkedLinkFilters";
    private static final String PREFS_DEFAULT_FIN_COA_CODE_KEY = "defaultFinancialsChartOfAccountsCode";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private PreferencesDao preferencesDao;

    @Before
    public void setUp() throws Exception {
        loadDefaultUserPrefs();
    }

    protected Map<String,Object> generateDefaultUserPrefs() {
        Map<String,Object> defaultPrefs = new HashMap<>();

        defaultPrefs.put(PREFS_SIDEBAR_OUT_KEY, Boolean.TRUE);
        defaultPrefs.put(PREFS_CHECKED_LINK_FILTERS_KEY, PreferencesDeserializer.LINK_GROUP_CATEGORIES);
        defaultPrefs.put(PREFS_DEFAULT_FIN_COA_CODE_KEY,"");

        return defaultPrefs;
    }

    protected void loadDefaultUserPrefs() {
        Map<String,Object> defaultPrefs = generateDefaultUserPrefs();

        preferencesDao.saveUserPreferences("testuser1", defaultPrefs);
        preferencesDao.saveUserPreferences("testuser2", defaultPrefs);
    }

    @Test
    public void saveInstitutionPreferences() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        Map<String,Object> preferences = preferencesDao.findInstitutionPreferences();

        //Make some changes to the menu
        preferences.put(LOGO_URL_KEY, "static/images/foo.png");

        List<Map<String,Object>> menuLinks = (List<Map<String, Object>>) preferences.get(MENU_LINKS_KEY);

        //Insert menu item
        Map<String,Object> newMenuItem = new HashMap<>();
        newMenuItem.put(LABEL_KEY, "Foo");
        newMenuItem.put(LINK_KEY, "/foo");
        menuLinks.add(0, newMenuItem);

        //Append menu item
        newMenuItem = new HashMap<>();
        newMenuItem.put(LABEL_KEY, "Bar");
        newMenuItem.put(LINK_KEY, "/bar");
        menuLinks.add(newMenuItem);

        List<Map<String,Object>> linkGroups = (List<Map<String, Object>>) preferences.get(LINK_GROUPS_KEY);

        //Modify the label of the first item
        Map<String,Object> linkGroup = linkGroups.get(0);
        linkGroup.put(LABEL_KEY, linkGroup.get(LABEL_KEY) + " FOO");

        //Grab links from the first category
        List<Map<String,Object>> links = (List<Map<String, Object>>) ((Map)linkGroup.get(LINKS_KEY)).values().iterator().next();

        //Modify the first link
        Map<String,Object> link = links.get(0);
        link.put(LINK_TYPE_KEY, "rice");

        //Add a new link to the first category
        link = new HashMap<>();
        link.put(BO_CLS_KEY, "co.kuali.foo");
        link.put(DOC_TYP_KEY, "FOOBAR");
        link.put(LINK_KEY,"/foobar");
        link.put(LABEL_KEY, "Foo Bar");
        link.put(LINK_TYPE_KEY, "kfs");
        link.put(NEW_TARGET_KEY, Boolean.FALSE);

        Map<String,Object> permission = new HashMap<>();
        link.put(PERMISSION_KEY, permission);
        permission.put(TMPL_NMSPC_KEY,"FOO");
        permission.put(TMPL_NM_KEY, "FOO");

        Map<String,String> details = new HashMap<>();
        permission.put(DETAILS_KEY, details);
        details.put("FOO", "FOODETAIL");
        details.put("BAR", "BARDETAIL");

        //Insert new link in middle of list
        links.add((int) Math.floor(links.size() / 2), link);


        //Create a new link group
        linkGroup = new HashMap<>();
        linkGroup.put(LABEL_KEY, "FOOBAR");

        Map<String,List<Map<String,Object>>> categories = new HashMap<>();
        linkGroup.put(LINKS_KEY, categories);
        links = new ArrayList<>();
        categories.put("reference", links);

        link = new HashMap<>();
        link.put(DOC_TYP_KEY, "FOOBAR2");
        link.put(LINK_TYPE_KEY, "kfs");
        links.add(link);

        //Add the new link group in middle of list
        linkGroups.add((int) Math.floor(linkGroups.size() / 2), linkGroup);


        String expected = mapper.writeValueAsString(preferences);

        preferencesDao.saveInstitutionPreferences(null, preferences);
        preferences = preferencesDao.findInstitutionPreferences();

        String actual = mapper.writeValueAsString(preferences);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void saveInstitutionPreferencesInvalidTopLevelAttribute() throws JsonProcessingException {
        Map<String,Object> preferences = preferencesDao.findInstitutionPreferences();

        preferences.put("FOO", "BAR");
        preferences.put(LOGO_URL_KEY, "/foobar");

        preferencesDao.saveInstitutionPreferences(null, preferences);
        preferences = preferencesDao.findInstitutionPreferences();

        Assert.assertFalse(preferences.containsKey("FOO"));
        Assert.assertEquals("/foobar", preferences.get(LOGO_URL_KEY));
    }

    @Test
    public void saveLogoInvalidType() {
        thrown.expect(RuntimeException.class);

        Map<String,Object> preferences = preferencesDao.findInstitutionPreferences();

        preferences.put(LOGO_URL_KEY, new Object());

        preferencesDao.saveInstitutionPreferences(null, preferences);
    }

    @Test
    public void saveInstitutionPreferencesInvalidMenuLinkType() {
        thrown.expect(ClassCastException.class);
        thrown.expectMessage("java.lang.Object cannot be cast to java.util.Map");

        Map<String,Object> preferences = preferencesDao.findInstitutionPreferences();

        //Bad menu link
        Object bogus = new Object();
        ((List)preferences.get(MENU_LINKS_KEY)).add(bogus);

        preferencesDao.saveInstitutionPreferences(null, preferences);
    }

    @Test
    public void saveInstitutionPreferencesInvalidMenuLinkAttribute() {
        Map<String,Object> preferences = preferencesDao.findInstitutionPreferences();

        //Bad menu link
        Map<String,Object> menuLink = (Map<String, Object>) ((List)preferences.get(MENU_LINKS_KEY)).iterator().next();

        menuLink.put("FOO", "BAR");
        menuLink.put(LABEL_KEY, "FOOBAR");

        preferencesDao.saveInstitutionPreferences(null, preferences);
        preferences = preferencesDao.findInstitutionPreferences();

        menuLink = (Map<String, Object>) ((List)preferences.get(MENU_LINKS_KEY)).iterator().next();

        Assert.assertFalse(menuLink.containsKey("FOO"));
        Assert.assertEquals("FOOBAR", menuLink.get(LABEL_KEY));
    }

    @Test
    public void saveInstitutionPreferencesInvalidLinkGroupType() {
        thrown.expect(ClassCastException.class);
        thrown.expectMessage("java.lang.Object cannot be cast to java.util.Map");

        Map<String,Object> preferences = preferencesDao.findInstitutionPreferences();

        List linkGroups = (List) preferences.get(LINK_GROUPS_KEY);
        linkGroups.add(new Object());

        preferencesDao.saveInstitutionPreferences(null, preferences);
    }

    @Test
    public void saveInstitutionPreferencesInvalidLinkGroupAttribute() {
        Map<String,Object> preferences = preferencesDao.findInstitutionPreferences();

        List<Map<String,Object>> linkGroups = (List<Map<String, Object>>) preferences.get(LINK_GROUPS_KEY);

        Map<String,Object> linkGroup = linkGroups.get(0);
        linkGroup.put("FOO", "BAR");
        linkGroup.put(LABEL_KEY, "FOOBAR");

        preferencesDao.saveInstitutionPreferences(null, preferences);

        preferences = preferencesDao.findInstitutionPreferences();

        linkGroups = (List<Map<String, Object>>) preferences.get(LINK_GROUPS_KEY);
        linkGroup = linkGroups.get(0);

        Assert.assertFalse(linkGroup.containsKey("FOO"));
        Assert.assertEquals("FOOBAR", linkGroup.get(LABEL_KEY));
    }

    @Test
    public void saveInstitutionPreferencesInvalidCategory() {
        String invalidCategoryName = "FOO";

        thrown.expect(RuntimeException.class);
        thrown.expectMessage(String.format("Invalid link group category: '%s'", invalidCategoryName));

        Map<String,Object> preferences = preferencesDao.findInstitutionPreferences();

        List<Map<String,Object>> linkGroups = (List<Map<String, Object>>) preferences.get(LINK_GROUPS_KEY);
        Map<String,Object> linkGroup = linkGroups.get(0);
        Map<String, List<Map<String,Object>>> categories = (Map<String, List<Map<String, Object>>>) linkGroup.get(LINKS_KEY);

        //Make sure activities category exists in the links
        Assert.assertTrue(categories.containsKey(PreferencesDeserializer.LINK_GROUP_CATEGORIES.get(0)));

        //Copy activities to foo category
        categories.put(invalidCategoryName, categories.get(PreferencesDeserializer.LINK_GROUP_CATEGORIES.get(0)));

        preferencesDao.saveInstitutionPreferences(null, preferences);
    }

    @Test
    public void saveInstitutionPreferencesInvalidLinkType() {
        thrown.expect(ClassCastException.class);
        thrown.expectMessage("java.lang.Object cannot be cast to java.util.Map");

        Map<String,Object> preferences = preferencesDao.findInstitutionPreferences();

        List<Map<String,Object>> linkGroups = (List<Map<String, Object>>) preferences.get(LINK_GROUPS_KEY);
        Map<String,Object> linkGroup = linkGroups.get(0);
        Map<String, List<Map<String,Object>>> categories = (Map<String, List<Map<String, Object>>>) linkGroup.get(LINKS_KEY);
        String categoryName = categories.keySet().iterator().next();
        List links = categories.get(categoryName);
        links.add(new Object());

        preferencesDao.saveInstitutionPreferences(null, preferences);
    }

    @Test
    public void saveInstitutionPreferencesInvalidLinkAttribute() {
        Map<String,Object> preferences = preferencesDao.findInstitutionPreferences();

        List<Map<String,Object>> linkGroups = (List<Map<String, Object>>) preferences.get(LINK_GROUPS_KEY);
        Map<String,Object> linkGroup = linkGroups.get(0);
        Map<String, List<Map<String,Object>>> categories = (Map<String, List<Map<String, Object>>>) linkGroup.get(LINKS_KEY);
        String categoryName = categories.keySet().iterator().next();
        List<Map<String,Object>> links = categories.get(categoryName);
        Map<String,Object> link = links.iterator().next();
        link.put("FOO", "BAR");
        link.put(DOC_TYP_KEY,"FOOBAR");

        preferencesDao.saveInstitutionPreferences(null, preferences);

        preferences = preferencesDao.findInstitutionPreferences();

        linkGroups = (List<Map<String, Object>>) preferences.get(LINK_GROUPS_KEY);
        linkGroup = linkGroups.get(0);
        categories = (Map<String, List<Map<String, Object>>>) linkGroup.get(LINKS_KEY);
        links = categories.get(categoryName);
        link = links.iterator().next();

        Assert.assertFalse(link.containsKey("FOO"));
        Assert.assertEquals("FOOBAR", link.get(DOC_TYP_KEY));
    }

    @Test
    public void saveInstitutionPreferencesInvalidLinkPermissionAttribute() {
        Map<String,Object> preferences = preferencesDao.findInstitutionPreferences();

        List<Map<String,Object>> linkGroups = (List<Map<String, Object>>) preferences.get(LINK_GROUPS_KEY);
        Map<String,Object> linkGroup = linkGroups.get(0);
        Map<String, List<Map<String,Object>>> categories = (Map<String, List<Map<String, Object>>>) linkGroup.get(LINKS_KEY);
        String categoryName = categories.keySet().iterator().next();
        List<Map<String,Object>> links = categories.get(categoryName);
        Optional<Map<String,Object>> linkWithPermission = links.stream().filter(link -> link.get(PERMISSION_KEY) != null).findFirst();

        Assert.assertTrue(linkWithPermission.isPresent());

        Map<String,Object> permission = (Map<String, Object>) linkWithPermission.get().get(PERMISSION_KEY);
        permission.put(TMPL_NM_KEY, "FOOBAR");
        permission.put("FOO", "BAR");

        preferencesDao.saveInstitutionPreferences(null, preferences);

        preferences = preferencesDao.findInstitutionPreferences();

        linkGroups = (List<Map<String, Object>>) preferences.get(LINK_GROUPS_KEY);
        linkGroup = linkGroups.get(0);
        categories = (Map<String, List<Map<String, Object>>>) linkGroup.get(LINKS_KEY);
        links = categories.get(categoryName);
        linkWithPermission = links.stream().filter(link -> link.get(PERMISSION_KEY) != null).findFirst();

        Assert.assertTrue(linkWithPermission.isPresent());

        permission = (Map<String, Object>) linkWithPermission.get().get(PERMISSION_KEY);

        Assert.assertFalse(permission.containsKey("FOO"));
        Assert.assertEquals("FOOBAR", permission.get(TMPL_NM_KEY));
    }

    @Test
    public void saveInstitutionPreferencesBadLinkGroupsType() {
        thrown.expect(ClassCastException.class);
        thrown.expectMessage("java.lang.Object cannot be cast to java.util.List");

        Map<String,Object> preferences = preferencesDao.findInstitutionPreferences();
        preferences.put(LINK_GROUPS_KEY, new Object());

        preferencesDao.saveInstitutionPreferences(null, preferences);
    }

    @Test
    public void saveInstitutionPreferencesBadMenuLinksType() {
        thrown.expect(ClassCastException.class);
        thrown.expectMessage("java.lang.Object cannot be cast to java.util.List");

        Map<String,Object> preferences = preferencesDao.findInstitutionPreferences();
        preferences.put(MENU_LINKS_KEY, new Object());

        preferencesDao.saveInstitutionPreferences(null, preferences);
    }

    @Test
    public void findInstitutionPreferences() {
        Map<String,Object> preferences = preferencesDao.findInstitutionPreferences();

        Assert.assertTrue(preferences.containsKey(LOGO_URL_KEY));
        Assert.assertTrue(preferences.containsKey(MENU_LINKS_KEY));
        Assert.assertTrue(preferences.containsKey(LINK_GROUPS_KEY));

        Assert.assertTrue(preferences.get(LOGO_URL_KEY) instanceof String);
        Assert.assertTrue(preferences.get(MENU_LINKS_KEY) instanceof List);
        Assert.assertTrue(preferences.get(LINK_GROUPS_KEY) instanceof List);

        //Validate logoUrl
        Assert.assertFalse(preferences.get(LOGO_URL_KEY).toString().isEmpty());

        List<Map<String,Object>> menuLinks = (List<Map<String, Object>>) preferences.get(MENU_LINKS_KEY);

        //Expect at least one menu link
        Assert.assertFalse(menuLinks.isEmpty());

        //Validate all menu links
        menuLinks.forEach(menuLink -> {
            Assert.assertTrue(menuLink.containsKey(LABEL_KEY));
            Assert.assertTrue(menuLink.containsKey(LINK_KEY));

            Assert.assertTrue(menuLink.get(LABEL_KEY) instanceof String);
            Assert.assertTrue(menuLink.get(LINK_KEY) instanceof String);

            Assert.assertFalse(menuLink.get(LABEL_KEY).toString().isEmpty());
            Assert.assertFalse(menuLink.get(LINK_KEY).toString().isEmpty());
        });

        List<Map<String,Object>> linkGroups = (List<Map<String, Object>>) preferences.get(LINK_GROUPS_KEY);

        //Expect at least one nav link
        Assert.assertFalse(linkGroups.isEmpty());

        linkGroups.forEach(linkGroup -> {
            Assert.assertTrue(linkGroup.containsKey(LABEL_KEY));
            Assert.assertTrue(linkGroup.containsKey(LINKS_KEY));

            Assert.assertTrue(linkGroup.get(LABEL_KEY) instanceof String);
            Assert.assertTrue(linkGroup.get(LINKS_KEY) instanceof Map);

            Assert.assertFalse(linkGroup.get(LABEL_KEY).toString().isEmpty());

            Map<String, List<Map<String,Object>>> categories = (Map<String, List<Map<String, Object>>>) linkGroup.get(LINKS_KEY);

            categories.keySet().forEach(categoryName -> {
                //Validate against hardcoded list of link categories
                Assert.assertTrue(PreferencesDeserializer.LINK_GROUP_CATEGORIES.contains(categoryName));

                List<Map<String,Object>> category = categories.get(categoryName);

                Assert.assertFalse(category.isEmpty());

                category.forEach(link -> {
                    Assert.assertTrue(link.containsKey(LINK_TYPE_KEY));
                    Assert.assertTrue(link.get(LINK_TYPE_KEY) instanceof String);
                    Assert.assertFalse(link.get(LINK_TYPE_KEY).toString().isEmpty());

                    if (link.containsKey(DOC_TYP_KEY)) {
                        Assert.assertTrue(link.get(DOC_TYP_KEY) instanceof String);
                        Assert.assertFalse(link.get(DOC_TYP_KEY).toString().isEmpty());
                    }

                    if (link.containsKey(BO_CLS_KEY)) {
                        Assert.assertTrue(link.get(BO_CLS_KEY) instanceof String);
                        Assert.assertFalse(link.get(BO_CLS_KEY).toString().isEmpty());
                    }

                    if (link.containsKey(LABEL_KEY)) {
                        Assert.assertTrue(link.get(LABEL_KEY) instanceof String);
                        Assert.assertFalse(link.get(LABEL_KEY).toString().isEmpty());
                    }

                    if (link.containsKey(LINK_KEY)) {
                        Assert.assertTrue(link.get(LINK_KEY) instanceof String);
                        Assert.assertFalse(link.get(LINK_KEY).toString().isEmpty());
                    }

                    if (link.containsKey(NEW_TARGET_KEY)) {
                        Assert.assertTrue(link.get(NEW_TARGET_KEY) instanceof Boolean);
                    }

                    if (link.containsKey(PERMISSION_KEY)) {
                        Assert.assertTrue(link.get(PERMISSION_KEY) instanceof Map);
                        Map<String,Object> permission = (Map<String, Object>) link.get(PERMISSION_KEY);

                        Assert.assertTrue(permission.containsKey(TMPL_NMSPC_KEY));
                        Assert.assertTrue(permission.containsKey(TMPL_NM_KEY));

                        Assert.assertTrue(permission.get(TMPL_NMSPC_KEY) instanceof String);
                        Assert.assertTrue(permission.get(TMPL_NM_KEY) instanceof String);

                        Assert.assertFalse(permission.get(TMPL_NMSPC_KEY).toString().isEmpty());
                        Assert.assertFalse(permission.get(TMPL_NM_KEY).toString().isEmpty());

                        if (permission.containsKey(DETAILS_KEY)) {
                            Assert.assertTrue(permission.get(DETAILS_KEY) instanceof Map);

                            Map<String,String> details = (Map<String, String>) permission.get(DETAILS_KEY);
                            Assert.assertFalse(details.isEmpty());
                        }
                    }
                });
            });
        });
    }

    @Test
    public void getUserPrefsSupportedTypes() {
        Map<String,Object> expected = new HashMap<>();
        expected.put("list", Arrays.asList("a", "b", "c"));
        expected.put("bool", Boolean.TRUE);
        expected.put("str", "mystr");
        expected.put("int", 23);
        expected.put("float", 1.23);
        Map<String,String> mapInput = new HashMap<>();
        mapInput.put("foo", "bar");
        expected.put("map", mapInput);

        preferencesDao.saveUserPreferences("testuser", expected);

        Map<String,Object> actual = preferencesDao.getUserPreferences("testuser");

        expected.keySet().forEach(key -> Assert.assertEquals(expected.get(key), actual.get(key)));
    }

    @Test
    public void getUserPrefsNonExistentUser() {
        Assert.assertNull(preferencesDao.getUserPreferences("foobar"));
    }

    @Test
    public void saveUserPreferencesMap() {
        Map<String, Object> expected = generateDefaultUserPrefs();

        preferencesDao.saveUserPreferences("testuser", expected);

        Map<String, Object> actual = preferencesDao.getUserPreferences("testuser");

        expected.keySet().forEach(key -> Assert.assertEquals(expected.get(key), actual.get(key)));
    }

    @Test
    public void saveUserPreferencesString() throws JsonProcessingException {
        Map<String, Object> expected = generateDefaultUserPrefs();

        preferencesDao.saveUserPreferences("testuser", new ObjectMapper().writeValueAsString(expected));

        Map<String, Object> actual = preferencesDao.getUserPreferences("testuser");

        expected.keySet().forEach(key -> Assert.assertEquals(expected.get(key), actual.get(key)));
    }

    @Test
    public void saveUserPreferencesBadString() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Error parsing json");
        preferencesDao.saveUserPreferences("testuser", "kjdsflkj");
    }

    @Test
    public void saveUserPreferencesNull() {
        preferencesDao.saveUserPreferences("testuser", (String)null);
        Map<String, Object> actual = preferencesDao.getUserPreferences("testuser");
        Assert.assertNull(actual);

        preferencesDao.saveUserPreferences("testuser", (Map)null);
        actual = preferencesDao.getUserPreferences("testuser");
        Assert.assertNull(actual);
    }
}
