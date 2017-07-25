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

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kuali.kfs.sys.dataaccess.PreferencesDao;
import org.kuali.rice.core.api.cache.CacheManagerRegistry;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class PreferencesDaoJdbcTest {
    private static final String MENU_LINKS_KEY = "menu";
    private static final String LINK_GROUPS_KEY = "linkGroups";
    private static final String LOGO_URL_KEY = "logoUrl";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private PreferencesDao preferencesDao;

    @Before
    public void setUp() throws Exception {
        preferencesDao = new PreferencesDaoJdbc();
    }

    public static class TestCache implements Cache {
        Map<Object,Object> cacheMap = new HashMap<>();

        @Override
        public String getName() { return "unittestcache"; }

        @Override
        public Object getNativeCache() {return null;}

        @Override
        public ValueWrapper get(Object key) { return () -> cacheMap.get(key); }

        @Override
        public void put(Object key, Object value) { cacheMap.put(key, value); }

        @Override
        public void evict(Object key) { cacheMap.remove(key); }

        @Override
        public void clear() { cacheMap.clear(); }
    }

    @Test
    public void cacheInstitutionPreferences() {
        CacheManager cacheManager = EasyMock.createMock(CacheManager.class);
        CacheManagerRegistry cacheManagerRegistry = EasyMock.createMock(CacheManagerRegistry.class);
        TestCache cache = new TestCache();

        EasyMock.expect(cacheManager.getCache(PreferencesDaoJdbc.INSTITUTION_PREFERENCES_CACHE)).andReturn(cache).anyTimes();
        EasyMock.expect(cacheManagerRegistry.getCacheManagerByCacheName(PreferencesDaoJdbc.INSTITUTION_PREFERENCES_CACHE)).andReturn(cacheManager).anyTimes();

        EasyMock.replay(cacheManager);
        EasyMock.replay(cacheManagerRegistry);

        preferencesDao = new PreferencesDaoJdbc();
        ((PreferencesDaoJdbc)preferencesDao).setCacheManagerRegistry(cacheManagerRegistry);

        String expected = "/mylogo.png";
        Map<String,Object> prefs = new HashMap<>();
        prefs.put("logoUrl", expected);

        preferencesDao.cacheInstitutionPreferences("khuntley", prefs);

        Assert.assertEquals(expected, ((Map<String, Object>) cache.cacheMap.get("khuntley")).get("logoUrl"));
    }

    @Test
    public void findInstitutionPreferencesCache() {
        CacheManager cacheManager = EasyMock.createMock(CacheManager.class);
        CacheManagerRegistry cacheManagerRegistry = EasyMock.createMock(CacheManagerRegistry.class);
        TestCache cache = new TestCache();

        EasyMock.expect(cacheManager.getCache(PreferencesDaoJdbc.INSTITUTION_PREFERENCES_CACHE)).andReturn(cache).anyTimes();
        EasyMock.expect(cacheManagerRegistry.getCacheManagerByCacheName(PreferencesDaoJdbc.INSTITUTION_PREFERENCES_CACHE)).andReturn(cacheManager).anyTimes();

        EasyMock.replay(cacheManager);
        EasyMock.replay(cacheManagerRegistry);

        preferencesDao = new PreferencesDaoJdbc();
        ((PreferencesDaoJdbc)preferencesDao).setCacheManagerRegistry(cacheManagerRegistry);

        String expected = "/mylogo.png";
        Map<String,Object> prefs = new HashMap<>();
        prefs.put("logoUrl", expected);

        cache.cacheMap.put("khuntley", prefs);

        prefs = preferencesDao.findInstitutionPreferencesCache("khuntley");

        Assert.assertEquals(expected, prefs.get("logoUrl"));
    }

    @Test
    public void saveInstitutionPreferencesNull() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Preferences must not be null");

        preferencesDao.saveInstitutionPreferences(null, null);
    }

    @Test
    public void saveInstitutionPreferencesNullLogoUrl() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("logoUrl attribute missing");

        Map<String, Object> preferences = new LinkedHashMap<String, Object>() {{
            put(MENU_LINKS_KEY, new Object());
            put(LINK_GROUPS_KEY, new Object());
            put(LOGO_URL_KEY, null);
        }};

        preferencesDao.saveInstitutionPreferences(null, preferences);
    }

    @Test
    public void saveInstitutionPreferencesNullMenuLinks() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("menu attribute missing");

        Map<String, Object> preferences = new LinkedHashMap<String, Object>() {{
            put(MENU_LINKS_KEY, null);
            put(LINK_GROUPS_KEY, new Object());
            put(LOGO_URL_KEY, "");
        }};

        preferencesDao.saveInstitutionPreferences(null, preferences);
    }


    @Test
    public void saveInstitutionPreferencesNullLinkGroups() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("linkGroups attribute missing");

        Map<String, Object> preferences = new LinkedHashMap<String, Object>() {{
            put(MENU_LINKS_KEY, new Object());
            put(LINK_GROUPS_KEY, null);
            put(LOGO_URL_KEY, "");
        }};

        preferencesDao.saveInstitutionPreferences(null, preferences);
    }

    @Test
    public void saveInstitutionPreferencesBadLogoUrlType() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Invalid type for logoUrl property - expecting String");

        Map<String, Object> preferences = new LinkedHashMap<String, Object>() {{
            put(MENU_LINKS_KEY, new Object());
            put(LINK_GROUPS_KEY, new Object());
            put(LOGO_URL_KEY, new Object());
        }};

        preferencesDao.saveInstitutionPreferences(null, preferences);
    }
}
