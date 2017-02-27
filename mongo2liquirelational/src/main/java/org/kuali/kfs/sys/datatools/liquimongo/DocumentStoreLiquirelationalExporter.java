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

package org.kuali.kfs.sys.datatools.liquimongo;

import com.mongodb.util.JSON;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.dataaccess.PreferencesDao;
import org.kuali.kfs.sys.dataaccess.impl.PreferencesDaoMongo;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by kelly on 2/24/17.
 */
public class DocumentStoreLiquirelationalExporter {
    private ClassPathXmlApplicationContext applicationContext;
    private PreferencesDao preferencesDao;
    private MongoTemplate mongoTemplate;

    //DEFAULTS
    private String author = "KFS701";
    private String idPrefix = "FINP-1125_";
    private String context = "bootstrap";

    public final static String TABLE_INSTITUTIONAL_PREFS = "INST_PREF_T";
    public final static String TABLE_USER_PREFS = "USR_PREFS_T";
    public final static String TABLE_MENU_LINK = "MENU_LNK_T";
    public final static String TABLE_NAV_LINK = "NAV_LNK_T";
    public final static String TABLE_NAV_LINK_GROUP = "NAV_LNK_GRP_T";
    public final static String TABLE_NAV_LINK_PERMISSION = "NAV_LNK_PERM_T";
    public final static String TABLE_NAV_LINK_PERMISSION_DETAIL = "NAV_LNK_PERM_DETAIL_T";

    public static void main(String[] args) {
        try {
            DocumentStoreLiquirelationalExporter dsle = new DocumentStoreLiquirelationalExporter();
            dsle.initialize();
            dsle.emitPreferences();
            dsle.emitUserPreferences();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    protected void initialize() {
        applicationContext = new ClassPathXmlApplicationContext("org/kuali/kfs/sys/datatools/liquimongo/spring-mongo2liquirelational.xml");
        applicationContext.start();
        preferencesDao = applicationContext.getBean(PreferencesDaoMongo.class);
        mongoTemplate = applicationContext.getBean(MongoTemplate.class);

        if (StringUtils.isNotBlank(System.getProperty("author"))) {
            this.author = System.getProperty("author");
        }

        if (StringUtils.isNotBlank(System.getProperty("id"))) {
            this.idPrefix = System.getProperty("id") + "_";
        }

        if (StringUtils.isNotBlank(System.getProperty("context"))) {
            this.context = System.getProperty("context");
        }
    }

    protected void emitUserPreferences() {
        for (Map<String,Object> userPrefs : mongoTemplate.findAll(Map.class, PreferencesDaoMongo.USER_PREFERENCES)) {
            String principalName = (String) userPrefs.get("principalName");
            emitChangesetStart(String.format("%s_%s", TABLE_USER_PREFS, principalName));
            Map<String,Object> prefs = (Map<String, Object>) userPrefs.get("preferences");
            for (String key : prefs.keySet()) {
                emitInsertStart(TABLE_USER_PREFS);
                emitColumn("OBJ_ID", generateObjectId());
                emitColumn("PRNCPL_NM", principalName);
                emitColumn("KEY_CD", key);
                emitColumn("VAL", JSON.serialize(prefs.get(key)));
                emitInsertEnd();
            }
            emitChangesetEnd();
        }
    }

    protected void emitPreferences() {
        Map<String,Object> prefs = preferencesDao.findInstitutionPreferences();

        emitInstPrefs(prefs);
        emitMenuLinks(prefs);
        emitNavLinkGroups(prefs);
    }

    protected void emitInstPrefs(Map<String,Object> instPrefs) {
        emitChangesetStart(TABLE_INSTITUTIONAL_PREFS);
        emitInsertStart(TABLE_INSTITUTIONAL_PREFS);
        emitColumn("LOGO_DATA", (String)instPrefs.get("logoUrl"));
        emitInsertEnd();
        emitChangesetEnd();
    }

    protected void emitMenuLinks(Map<String,Object> instPrefs) {
        emitChangesetStart(TABLE_MENU_LINK);
        int order = 0;
        for (Map<String,String> menuLink : (List<Map<String, String>>) instPrefs.get("menu")) {
            emitInsertStart(TABLE_MENU_LINK);
            emitColumn("OBJ_ID", generateObjectId());
            emitColumn("LNK_LBL", menuLink.get("label"));
            emitColumn("LNK_VAL", menuLink.get("link"));
            emitColumn("ORDER", String.valueOf(order++));
            emitInsertEnd();
        }
        emitChangesetEnd();
    }

    protected void emitNavLinkGroups(Map<String,Object> instPrefs) {
        List<Map<String,Object>> linkGroups = (List<Map<String, Object>>) instPrefs.get("linkGroups");

        for (int i = 0; i < linkGroups.size(); i++) {
            Map<String,Object> linkGroup = linkGroups.get(i);

            String linkGroupLabel = (String) linkGroup.get("label");
            String linkGroupId = generateObjectId();
            emitChangesetStart(String.format("%s_%s", TABLE_NAV_LINK_GROUP, linkGroupLabel));

            //Emit the link group first
            emitInsertStart(TABLE_NAV_LINK_GROUP);
            emitColumn("OBJ_ID", linkGroupId);
            emitColumn("LNK_GRP_LBL", linkGroupLabel);
            emitColumn("ORDER", String.valueOf(i));
            emitInsertEnd();

            Map<String,Object> linkCategories = (Map<String, Object>) linkGroup.get("links");

            for (String linkCategory : linkCategories.keySet()) {
                List<Map<String,Object>> links = (List<Map<String, Object>>) linkCategories.get(linkCategory);
                for (int j = 0; j < links.size(); j++) {
                    Map<String,Object> link = links.get(j);

                    String linkId = generateObjectId();

                    emitInsertStart(TABLE_NAV_LINK);
                    emitColumn("OBJ_ID", linkId);
                    emitColumn("NAV_LNK_GRP", linkGroupId);
                    emitColumn("LNK_CTGRY", linkCategory);
                    emitColumn("LNK_TYP", (String) link.get("linkType"));
                    emitColumn("ORDER", String.valueOf(j));
                    if (link.get("newTarget") != null) {
                        emitColumn("NEW_TGT", (Boolean)link.get("newTarget") ? "Y" : "N");
                    }
                    if (link.get("documentTypeCode") != null) {
                        emitColumn("DOC_TYP_CD", (String) link.get("documentTypeCode"));
                    }
                    if (link.get("businessObjectClass") != null) {
                        emitColumn("BO_CLS", (String) link.get("businessObjectClass"));
                    }
                    if (link.get("label") != null) {
                        emitColumn("LNK_LBL", (String) link.get("label"));
                    }
                    if (link.get("link") != null) {
                        emitColumn("LNK_VAL", (String) link.get("link"));
                    }
                    emitInsertEnd();

                    Map<String,Object> permission = (Map<String, Object>) link.get("permission");
                    if (permission != null) {
                        String permissionId = generateObjectId();

                        emitInsertStart(TABLE_NAV_LINK_PERMISSION);
                        emitColumn("OBJ_ID", permissionId);
                        emitColumn("NAV_LNK", linkId);
                        emitColumn("TMPL_NMSPC", (String) permission.get("templateNamespace"));
                        emitColumn("TMPL_NM", (String) permission.get("templateName"));
                        emitInsertEnd();

                        Map<String,String> details = (Map<String, String>) permission.get("details");
                        if (details != null) {
                            for (String key : details.keySet()) {
                                String permissionDetailId = generateObjectId();
                                emitInsertStart(TABLE_NAV_LINK_PERMISSION_DETAIL);
                                emitColumn("OBJ_ID", permissionDetailId);
                                emitColumn("NAV_LNK_PERM", permissionId);
                                emitColumn("KEY_CD", key);
                                emitColumn("VAL", details.get(key));
                                emitInsertEnd();
                            }
                        }
                    }
                }
            }

            emitChangesetEnd();
        }
    }

    protected String generateObjectId() {
        return UUID.randomUUID().toString();
    }

    protected void emitChangesetStart(String idSuffix) {
        System.out.println(String.format("    <changeSet author=\"%s\" id=\"%s%s\" context=\"%s\">", author, idPrefix, idSuffix.replaceAll("[^A-Za-z0-9_]", ""), context));
    }

    protected void emitChangesetEnd() {
        System.out.println("    </changeSet>");
    }

    protected void emitInsertStart(String table) {
        System.out.println(String.format("        <insert tableName=\"%s\">", table));
    }

    protected void emitInsertEnd() {
        System.out.println("        </insert>");
    }

    protected void emitColumn(String name, String value) {
        System.out.println(String.format("            <column name=\"%s\"><![CDATA[%s]]></column>", name, value));
    }
}
