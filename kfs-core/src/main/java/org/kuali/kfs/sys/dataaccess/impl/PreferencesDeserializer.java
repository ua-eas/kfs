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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

class PreferencesDeserializer {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PreferencesDeserializer.class);

    public static final List<String> LINK_GROUP_CATEGORIES = Arrays.asList("activities", "reference", "administration");

    static Map<String,Object> deserializeUserPreferences(List<Map<String,Object>> rows) {
        Map<String, Object> preferences = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        rows.forEach(rowMap -> {
            try {
                preferences.put((String)rowMap.get("KEY_CD"), mapper.readValue((String)rowMap.get("VAL"), Object.class));
            } catch (IOException e) {
                LOG.error("deserializeUserPreferences() Error parsing json", e);
                throw new RuntimeException("Error parsing json");
            }
        });

        return preferences;
    }

    static List<Map<String,Object>> deserializeMenuLinks(List<Map<String,Object>> rows) {
        List<Map<String,Object>> menuLinks = new ArrayList<>();
        rows.forEach(row -> {
            Map<String,Object> menuLink = new HashMap<>();
            menuLink.put("label", row.get("linkLabel"));
            menuLink.put("link", row.get("linkValue"));
            menuLinks.add(menuLink);
        });
        return menuLinks;
    }

    static List<Map<String,Object>> deserializeNavLinkGroups(List<Map<String,Object>> rows) {
        List<Map<String,Object>> linkGroups = new ArrayList<>();
        rows.forEach(row -> deserializeLinkGroup(linkGroups, row));
        return linkGroups;
    }

    static void deserializeLinkGroup(List<Map<String, Object>> linkGroups, Map<String, Object> row) {
        Integer groupPosition = (Integer) row.get("groupPosition");
        Map<String,Object> group;
        if (groupPosition > linkGroups.size() - 1) {
            group = new HashMap<>();
            group.put("label", row.get("groupLabel"));
            linkGroups.add(group);
        } else {
            group = linkGroups.get(groupPosition);
        }

        deserializeLinkCategory(row, group);
    }

    static void deserializeLinkCategory(Map<String, Object> row, Map<String, Object> group) {
        Map<String,List<Map<String,Object>>> linkCategories = (Map<String, List<Map<String, Object>>>) group.get("links");
        if (linkCategories == null) {
            linkCategories = new TreeMap<>(Comparator.comparingInt(LINK_GROUP_CATEGORIES::indexOf));
            group.put("links", linkCategories);
        }

        String linkCategoryName = (String) row.get("linkCategory");
        List<Map<String,Object>> linkCategory = linkCategories.get(linkCategoryName);
        if (linkCategory == null) {
            linkCategory = new ArrayList<>();
            linkCategories.put(linkCategoryName, linkCategory);
        }

        deserializeLink(row, linkCategory);
    }

    static void deserializeLink(Map<String, Object> row, List<Map<String, Object>> linkCategory) {
        Integer linkPosition = (Integer) row.get("linkPosition");
        Map<String,Object> link;
        if (linkPosition > linkCategory.size() - 1) {
            link = new HashMap<>();
            link.put("linkType", row.get("linkType"));
            String documentTypeCode = (String) row.get("documentTypeCode");
            if (documentTypeCode != null) {
                link.put("documentTypeCode", documentTypeCode);
            }
            String businessObjectClass = (String) row.get("businessObjectClass");
            if (businessObjectClass != null) {
                link.put("businessObjectClass", businessObjectClass);
            }
            String newTarget = (String) row.get("newTarget");
            if (newTarget != null) {
                link.put("newTarget", "Y".equalsIgnoreCase(newTarget));
            }
            String linkLabel = (String) row.get("linkLabel");
            if (linkLabel != null) {
                link.put("label", linkLabel);
            }
            String linkValue = (String) row.get("linkValue");
            if (linkValue != null) {
                link.put("link", linkValue);
            }
            linkCategory.add(link);
        } else {
            link = linkCategory.get(linkPosition);
        }

        deserializeLinkPermission(row, link);
    }

    static void deserializeLinkPermission(Map<String, Object> row, Map<String, Object> link) {
        String permTemplateNamespace = (String) row.get("permTemplateNamespace");
        String permTemplateName = (String) row.get("permTemplateName");
        if (permTemplateName != null || permTemplateNamespace != null) {
            Map<String, Object> perm = (Map<String, Object>) link.get("permission");
            if (perm == null) {
                perm = new HashMap<>();
                link.put("permission", perm);
            }

            perm.put("templateNamespace", permTemplateNamespace);
            perm.put("templateName", permTemplateName);

            deserializeLinkPermissionDetail(row, perm);
        }
    }

    static void deserializeLinkPermissionDetail(Map<String, Object> row, Map<String, Object> perm) {
        String permDetailKey = (String) row.get("permDetailKey");
        String permDetailValue = (String) row.get("permDetailValue");
        if (permDetailKey != null) {
            Map<String, String> permDetails = (Map<String, String>) perm.get("details");
            if (permDetails == null) {
                permDetails = new HashMap<>();
                perm.put("details", permDetails);
            }
            permDetails.put(permDetailKey, permDetailValue);
        }
    }

}
