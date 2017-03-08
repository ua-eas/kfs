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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.dataaccess.PreferencesDao;
import org.kuali.rice.core.api.cache.CacheManagerRegistry;
import org.kuali.rice.core.impl.services.CoreImplServiceLocator;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreferencesDaoJdbc implements PreferencesDao {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PreferencesDaoJdbc.class);

    public static final String INSTITUTION_PREFERENCES_CACHE = KFSConstants.APPLICATION_NAMESPACE_CODE + "/InstitutionPreferences";

    private ColumnMapRowMapperQuery userPreferencesQuery;
    private ColumnMapRowMapperQuery navLinksQuery;
    private ColumnMapRowMapperQuery menuLinksQuery;
    private LogoQuery logoQuery;

    private SimpleJdbcInsert userPreferencesInsert;
    private SimpleJdbcInsert menuLinksInsert;
    private SimpleJdbcInsert navLinkGroupsInsert;
    private SimpleJdbcInsert navLinksInsert;
    private SimpleJdbcInsert navLinkPermissionsInsert;
    private SimpleJdbcInsert navLinkPermissionDetailsInsert;

    private JdbcTemplate jdbcTemplate;
    private CacheManagerRegistry cacheManagerRegistry;

    private static final String TABLE_INST_PREFS = "inst_pref_t";
    private static final String TABLE_USER_PREFS = "usr_prefs_t";
    private static final String TABLE_MENU_LINKS = "menu_lnk_t";
    static final String TABLE_NAV_LINK_GROUPS = "nav_lnk_grp_t";
    static final String TABLE_NAV_LINKS = "nav_lnk_t";
    static final String TABLE_NAV_LINK_PERMISSIONS = "nav_lnk_perm_t";
    static final String TABLE_NAV_LINK_PERMISSION_DETAILS = "nav_lnk_perm_dtl_t";

    private static final String UPDATE_LOGO = String.format("update %s set logo_data = ?", TABLE_INST_PREFS);

    private static final String DELETE_MENU_LINKS = String.format("delete from %s", TABLE_MENU_LINKS);
    private static final String DELETE_NAV_LINK_GROUPS = String.format("delete from %s", TABLE_NAV_LINK_GROUPS);
    private static final String DELETE_NAV_LINKS = String.format("delete from %s", TABLE_NAV_LINKS);
    private static final String DELETE_NAV_LINK_PERMISSIONS = String.format("delete from %s", TABLE_NAV_LINK_PERMISSIONS);
    private static final String DELETE_NAV_LINK_PERMISSION_DETAILS = String.format("delete from %s", TABLE_NAV_LINK_PERMISSION_DETAILS);
    private static final String DELETE_USER_PREFERENCES = String.format("delete from %s where prncpl_nm = ?", TABLE_USER_PREFS);

    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);

        userPreferencesQuery = new ColumnMapRowMapperQuery(dataSource,
                "select obj_id, prncpl_nm, key_cd, val from usr_prefs_t where prncpl_nm = ?",
                new SqlParameter("principalName", Types.VARCHAR));
        navLinksQuery = new ColumnMapRowMapperQuery(dataSource,
                "select g.lnk_grp_lbl as groupLabel, g.posn as groupPosition, l.lnk_typ as linkType, l.lnk_ctgry as linkCategory, " +
                        "l.posn as linkPosition, l.new_tgt as newTarget, l.bo_cls as businessObjectClass, l.doc_typ_cd as documentTypeCode, l.lnk_lbl as linkLabel, " +
                        "l.lnk_val as linkValue, lp.tmpl_nmspc as permTemplateNamespace, lp.tmpl_nm as permTemplateName, " +
                        "lpd.key_cd as permDetailKey, lpd.val as permDetailValue from nav_lnk_grp_t as g " +
                        "left outer join nav_lnk_t as l on g.obj_id = l.nav_lnk_grp_id " +
                        "left outer join nav_lnk_perm_t as lp on l.obj_id = lp.nav_lnk_id " +
                        "left outer join nav_lnk_perm_dtl_t as lpd on lp.obj_id = lpd.nav_lnk_perm_id " +
                        "order by groupPosition, linkCategory, linkPosition");

        menuLinksQuery = new ColumnMapRowMapperQuery(dataSource,
                "select lnk_lbl as linkLabel, lnk_val as linkValue, posn from menu_lnk_t order by posn");
        logoQuery = new LogoQuery(dataSource);

        userPreferencesInsert = new SimpleJdbcInsert(dataSource).withTableName(TABLE_USER_PREFS);
        menuLinksInsert = new SimpleJdbcInsert(dataSource).withTableName(TABLE_MENU_LINKS);
        navLinkGroupsInsert = new SimpleJdbcInsert(dataSource).withTableName(TABLE_NAV_LINK_GROUPS);
        navLinksInsert = new SimpleJdbcInsert(dataSource).withTableName(TABLE_NAV_LINKS);
        navLinkPermissionsInsert = new SimpleJdbcInsert(dataSource).withTableName(TABLE_NAV_LINK_PERMISSIONS);
        navLinkPermissionDetailsInsert = new SimpleJdbcInsert(dataSource).withTableName(TABLE_NAV_LINK_PERMISSION_DETAILS);
    }

    @Override
    public Map<String, Object> findInstitutionPreferences() {
        LOG.debug("findInstitutionPreferences() started");

        List<Map<String,Object>> linkGroups = PreferencesDeserializer.deserializeNavLinkGroups(navLinksQuery.execute());
        Map<String,Object> prefs = new HashMap<>();
        prefs.put("linkGroups", linkGroups);
        prefs.put("menu", PreferencesDeserializer.deserializeMenuLinks(menuLinksQuery.execute()));
        prefs.put("logoUrl", logoQuery.execute().get(0).get("logo_data"));
        prefs.put(KFSPropertyConstants.INSTITUTION_ID, "1232413535");

        return prefs;
    }

    @Override
    public void saveInstitutionPreferences(String institutionId, Map<String, Object> preferences) {
        LOG.debug("saveInstitutionPreferences started");

        if (preferences == null) {
            throw new RuntimeException("Preferences must not be null");
        }

        if (preferences.get("linkGroups") == null) {
            throw new RuntimeException("linkGroups attribute missing");
        }

        if (preferences.get("menu") == null) {
            throw new RuntimeException("menu attribute missing");
        }

        if (preferences.get("logoUrl") == null) {
            throw new RuntimeException("logoUrl attribute missing");
        }

        jdbcTemplate.update(DELETE_NAV_LINK_PERMISSION_DETAILS);
        jdbcTemplate.update(DELETE_NAV_LINK_PERMISSIONS);
        jdbcTemplate.update(DELETE_NAV_LINKS);
        jdbcTemplate.update(DELETE_NAV_LINK_GROUPS);

        Map<String,List<Map<String,Object>>> rowsMap =
                PreferencesSerializer.serializeNavLinkGroups((List<Map<String,Object>>) preferences.get("linkGroups"));
        rowsMap.get(TABLE_NAV_LINK_GROUPS).forEach(row -> navLinkGroupsInsert.execute(row));
        rowsMap.get(TABLE_NAV_LINKS).forEach(row -> navLinksInsert.execute(row));
        rowsMap.get(TABLE_NAV_LINK_PERMISSIONS).forEach(row -> navLinkPermissionsInsert.execute(row));
        rowsMap.get(TABLE_NAV_LINK_PERMISSION_DETAILS).forEach(row -> navLinkPermissionDetailsInsert.execute(row));

        saveMenuLinks((List<Map<String,Object>>) preferences.get("menu"));
        jdbcTemplate.update(UPDATE_LOGO, preferences.get("logoUrl"));
    }

    @Override
    public Map<String, Object> findInstitutionPreferencesCache(String principalName) {
        LOG.debug("findInstitutionPreferencesCache() started");

        Cache.ValueWrapper cachedValue = getInstitutionPreferencesCache().get(principalName);
        if (cachedValue != null) {
            return (Map<String, Object>) cachedValue.get();
        } else {
            return null;
        }
    }

    @Override
    public void cacheInstitutionPreferences(String principalName, Map<String, Object> institutionPreferences) {
        LOG.debug("cacheInstitutionPreferences() started");

        getInstitutionPreferencesCache().put(principalName, institutionPreferences);
    }

    @Override
    public Map<String, Object> getUserPreferences(String principalName) {
        LOG.debug("getUserPreferences() started");

        Map<String, Object> result = PreferencesDeserializer.deserializeUserPreferences(userPreferencesQuery.execute(principalName));
        return result.keySet().isEmpty() ? null : result;
    }

    @Override
    public void saveUserPreferences(String principalName, String preferences) {
        LOG.debug("saveUserPreferences(String, String) started");

        if (preferences != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                saveUserPreferences(principalName, (Map<String, Object>) mapper.readValue(preferences, Map.class));
            } catch (IOException e) {
                LOG.error("saveUserPreferences() Error parsing json", e);
                throw new RuntimeException("Error parsing json");
            }
        } else {
            saveUserPreferences(principalName, (Map)null);
        }
    }

    @Override
    public void saveUserPreferences(String principalName, Map<String, Object> preferences) {
        LOG.debug("saveUserPreferences(String, Map) started");

        jdbcTemplate.update(DELETE_USER_PREFERENCES, principalName);

        if (preferences != null) {
            PreferencesSerializer.serializeUserPreferences(principalName, preferences).forEach(rowMap -> userPreferencesInsert.execute(rowMap));
        }
    }

    @Override
    public void setInstitutionPreferencesCacheLength(int seconds) {}

    @Override
    public int getInstitutionPreferencesCacheLength() {
        return 0;
    }

    public CacheManagerRegistry getCacheManagerRegistry() {
        return cacheManagerRegistry;
    }

    public void setCacheManagerRegistry(CacheManagerRegistry cacheManagerRegistry) {
        this.cacheManagerRegistry = cacheManagerRegistry;
    }

    protected Cache getInstitutionPreferencesCache() {
        CacheManager cm = cacheManagerRegistry.getCacheManagerByCacheName(INSTITUTION_PREFERENCES_CACHE);
        return cm.getCache(INSTITUTION_PREFERENCES_CACHE);
    }

    protected void saveMenuLinks(List<Map<String,Object>> menuLinks) {
        LOG.debug("saveMenuLinks(...) started");

        jdbcTemplate.update(DELETE_MENU_LINKS);

        if (menuLinks != null) {
            PreferencesSerializer.serializeMenuLinks(menuLinks).forEach(row -> menuLinksInsert.execute(row));
        }
    }

    public static class ColumnMapRowMapperQuery extends MappingSqlQuery<Map<String,Object>> {
        private ColumnMapRowMapper rowMapper;

        ColumnMapRowMapperQuery(DataSource dataSource, String sql, SqlParameter... parameters) {
            super(dataSource, sql);
            for (SqlParameter parameter : parameters) {
                super.declareParameter(parameter);
            }
            super.compile();
            this.rowMapper = new ColumnMapRowMapper();
        }

        @Override
        protected Map<String, Object> mapRow(ResultSet resultSet, int i) throws SQLException {
            return this.rowMapper.mapRow(resultSet, i);
        }
    }

    public static class LogoQuery extends MappingSqlQuery<Map<String,Object>> {
        private LobHandler lobHandler = new DefaultLobHandler();

        LogoQuery(DataSource dataSource) {
            super(dataSource, "select logo_data from inst_pref_t");
            compile();
        }

        @Override
        protected Map<String, Object> mapRow(ResultSet resultSet, int i) throws SQLException {
            Map<String, Object> result = new HashMap<>();
            result.put("logo_data", lobHandler.getClobAsString(resultSet, i + 1));
            return result;
        }
    }
}
