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
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.prefs.Preferences;
import java.util.stream.IntStream;

class PreferencesSerializer {
    private static final Logger LOG = Logger.getLogger(PreferencesSerializer.class);

    static List<Map<String,Object>> serializeUserPreferences(String principalName, Map<String,Object> preferences) {
        Validate.notNull(principalName, "'principleName' cannot be null.");
        Validate.notNull(preferences, "'preferences' cannot be null.");

        List<Map<String,Object>> rows = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        for (String key : preferences.keySet()) {
            Map<String,Object> row = new HashMap<>();
            row.put("PRNCPL_NM", principalName);
            row.put("OBJ_ID", generateObjectId());
            row.put("KEY_CD", key);

            try {
                row.put("VAL", mapper.writeValueAsString(preferences.get(key)));
            } catch (JsonProcessingException e) {
                LOG.error("serializeUserPreferences() Error serializing to json", e);
                throw new RuntimeException("Unable to serialize user preferences value to json");
            }

            rows.add(row);
        }

        return rows;
    }

    static List<Map<String,Object>> serializeMenuLinks(List<Map<String,Object>> menuLinks) {
        Validate.notNull(menuLinks, "'menuLinks' cannot be null.");

        List<Map<String,Object>> rows = new ArrayList<>();
        IntStream.range(0, menuLinks.size()).forEach(position -> {
            Map<String,Object> menuLink = menuLinks.get(position);
            Map<String,Object> row = new HashMap<>();
            row.put("OBJ_ID", generateObjectId());
            row.put("LNK_LBL", menuLink.get("label"));
            row.put("LNK_VAL", menuLink.get("link"));
            row.put("POSN", String.valueOf(position));
            rows.add(row);
        });
        return rows;
    }

    /**
     * Key is the table name (nav link group, nav link, etc)
     * Values are the data to insert in each table
     *
     * @param navLinkGroups
     * @return
     */
    static Map<String,List<Map<String,Object>>> serializeNavLinkGroups(List<Map<String,Object>> navLinkGroups) {
        Validate.notNull(navLinkGroups, "'navLinkGroups' cannot be null.");

        List<Map<String,Object>> navLinkGroupRows = new ArrayList<>();
        List<Map<String,Object>> navLinkRows = new ArrayList<>();
        List<Map<String,Object>> navLinkPermissionRows = new ArrayList<>();
        List<Map<String,Object>> navLinkPermissionDetailRows = new ArrayList<>();

        Map<String,List<Map<String,Object>>> result = new HashMap<>();
        result.put(PreferencesDaoJdbc.TABLE_NAV_LINK_GROUPS, navLinkGroupRows);
        result.put(PreferencesDaoJdbc.TABLE_NAV_LINKS, navLinkRows);
        result.put(PreferencesDaoJdbc.TABLE_NAV_LINK_PERMISSIONS, navLinkPermissionRows);
        result.put(PreferencesDaoJdbc.TABLE_NAV_LINK_PERMISSION_DETAILS, navLinkPermissionDetailRows);

        serializeLinkGroups(navLinkGroups, navLinkGroupRows, navLinkRows, navLinkPermissionRows, navLinkPermissionDetailRows);

        return result;
    }

    static void serializeLinkGroups(List<Map<String, Object>> navLinkGroups,
            List<Map<String, Object>> navLinkGroupRows, List<Map<String, Object>> navLinkRows,
            List<Map<String, Object>> navLinkPermissionRows, List<Map<String, Object>> navLinkPermissionDetailRows) {
        IntStream.range(0, navLinkGroups.size()).forEach(navLinkGroupPos -> {
            Map<String,Object> navLinkGroup = navLinkGroups.get(navLinkGroupPos);
            Map<String,Object> navLinkGroupRow = new HashMap<>();
            String navLinkGroupId = generateObjectId();
            navLinkGroupRow.put("OBJ_ID", navLinkGroupId);
            navLinkGroupRow.put("POSN", navLinkGroupPos);
            navLinkGroupRow.put("LNK_GRP_LBL", navLinkGroup.get("label"));
            navLinkGroupRows.add(navLinkGroupRow);

            serializeLinkCategories(navLinkGroupId, navLinkGroup, navLinkRows, navLinkPermissionRows, navLinkPermissionDetailRows);
        });
    }

    static void serializeLinkCategories(String navLinkGroupId, Map<String, Object> navLinkGroup,
            List<Map<String, Object>> navLinkRows, List<Map<String, Object>> navLinkPermissionRows,
            List<Map<String, Object>> navLinkPermissionDetailRows) {
        Map<String, Object> linkCategories = (Map<String, Object>) navLinkGroup.get("links");
        linkCategories.keySet().forEach(linkCategory -> {
            if (!PreferencesDeserializer.LINK_GROUP_CATEGORIES.contains(linkCategory)) {
                throw new RuntimeException("Invalid link group category: '" + linkCategory + "'");
            }
            serializeNavLinks(linkCategory, navLinkGroupId, (List<Map<String, Object>>) linkCategories.get(linkCategory),
                    navLinkRows, navLinkPermissionRows, navLinkPermissionDetailRows);
        });
    }

    static void serializeNavLinks(String linkCategory, String navLinkGroupId, List<Map<String, Object>> navLinks,
            List<Map<String, Object>> navLinkRows, List<Map<String, Object>> navLinkPermissionRows,
            List<Map<String, Object>> navLinkPermissionDetailRows) {
        IntStream.range(0, navLinks.size()).forEach(navLinkPos -> {
            serializeNavLink(linkCategory, navLinkGroupId, navLinkRows, navLinkPos,
                    navLinks.get(navLinkPos), navLinkPermissionRows, navLinkPermissionDetailRows);
        });
    }

    static void serializeNavLink(String linkCategory, String navLinkGroupId,
            List<Map<String, Object>> navLinkRows, int navLinkPos, Map<String, Object> navLink,
            List<Map<String, Object>> navLinkPermissionRows, List<Map<String, Object>> navLinkPermissionDetailRows) {
        Map<String,Object> navLinkRow = new HashMap<>();
        String navLinkId = generateObjectId();

        navLinkRow.put("OBJ_ID", navLinkId);
        navLinkRow.put("NAV_LNK_GRP_ID", navLinkGroupId);
        navLinkRow.put("POSN", navLinkPos);
        navLinkRow.put("LNK_TYP", navLink.get("linkType"));
        navLinkRow.put("LNK_CTGRY", linkCategory);

        String documentTypeCode = (String) navLink.get("documentTypeCode");
        if (documentTypeCode != null) {
            navLinkRow.put("DOC_TYP_CD", documentTypeCode);
        }
        String businessObjectClass = (String) navLink.get("businessObjectClass");
        if (businessObjectClass != null) {
            navLinkRow.put("BO_CLS", businessObjectClass);
        }
        Boolean newTarget = (Boolean) navLink.get("newTarget");
        if (newTarget != null) {
            navLinkRow.put("NEW_TGT", newTarget ? "Y" : "N");
        }
        String linkLabel = (String) navLink.get("label");
        if (linkLabel != null) {
            navLinkRow.put("LNK_LBL", linkLabel);
        }
        String linkValue = (String) navLink.get("link");
        if (linkValue != null) {
            navLinkRow.put("LNK_VAL", linkValue);
        }

        navLinkRows.add(navLinkRow);

        serializePermissions(navLink, navLinkId, navLinkPermissionRows, navLinkPermissionDetailRows);
    }

    static void serializePermissions(Map<String, Object> navLink, String navLinkId,
            List<Map<String, Object>> navLinkPermissionRows, List<Map<String, Object>> navLinkPermissionDetailRows) {
        Map<String, Object> permission = (Map<String, Object>) navLink.get("permission");
        if (permission != null) {
            Map<String,Object> navLinkPermissionRow = new HashMap<>();
            String navLinkPermissionId = generateObjectId();
            navLinkPermissionRow.put("OBJ_ID", navLinkPermissionId);
            navLinkPermissionRow.put("NAV_LNK_ID", navLinkId);
            navLinkPermissionRow.put("TMPL_NMSPC", permission.get("templateNamespace"));
            navLinkPermissionRow.put("TMPL_NM", permission.get("templateName"));
            navLinkPermissionRows.add(navLinkPermissionRow);

            serializePermissionDetails(permission, navLinkPermissionId, navLinkPermissionDetailRows);
        }
    }

    static void serializePermissionDetails(Map<String, Object> permission, String navLinkPermissionId,
            List<Map<String, Object>> navLinkPermissionDetailRows) {
        Map<String,String> permissionDetails = (Map<String, String>) permission.get("details");
        if (permissionDetails != null) {
            permissionDetails.keySet().forEach(permissionKey -> {
                Map<String,Object> navLinkPermissionDetailRow = new HashMap<>();
                navLinkPermissionDetailRow.put("OBJ_ID", generateObjectId());
                navLinkPermissionDetailRow.put("NAV_LNK_PERM_ID", navLinkPermissionId);
                navLinkPermissionDetailRow.put("KEY_CD", permissionKey);
                navLinkPermissionDetailRow.put("VAL", permissionDetails.get(permissionKey));
                navLinkPermissionDetailRows.add(navLinkPermissionDetailRow);
            });
        }
    }

    private static String generateObjectId() {
        return UUID.randomUUID().toString();
    }
}
