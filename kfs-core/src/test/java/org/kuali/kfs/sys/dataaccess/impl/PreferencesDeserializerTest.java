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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PreferencesDeserializerTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final String OBJ_ID_KEY = "OBJ_ID";
    private final String PRINCIPLE_NAME_KEY = "PRNCPL_NM";
    private final String KEY_CODE_KEY = "KEY_CD";
    private final String VALUE_KEY = "VAL";

    private final String LABEL_KEY = "label";
    private final String LINK_KEY = "link";
    private final String MENU_POSITION_KEY = "position";
    private final String MENU_LABEL_COLUMN_KEY = "linkLabel";
    private final String MENU_LINK_COLUMN_KEY = "linkValue";
    private final String MENU_POSITION_COLUMN_KEY = "POSN";

    private final String GROUP_LABEL_KEY = "groupLabel";
    private final String GROUP_POSITION_KEY = "groupPosition";
    private final String LINK_TYPE_KEY = "linkType";
    private final String LINK_CATEGORY_KEY = "linkCategory";
    private final String LINKS_KEY = "links";
    private final String LINK_POSITION_KEY = "linkPosition";
    private final String NEW_TARGET_KEY = "newTarget";
    private final String BUSINESS_OBJECT_CLASS_KEY = "businessObjectClass";
    private final String DOCUMENT_TYPE_CODE_KEY = "documentTypeCode";
    private final String LINK_LABEL_KEY = "linkLabel";
    private final String LINK_VALUE_KEY = "linkValue";
    private final String PERM_TEMPLATE_NAME_KEY = "permTemplateName";
    private final String PERM_TEMPLATE_NAMESPACE_KEY = "permTemplateNamespace";
    private final String PERM_DETAIL_KEY_KEY = "permDetailKey";
    private final String PERM_DETAIL_VAlUE_KEY = "permDetailValue";
    private final String TEMPLATE_NAMESPACE_KEY = "templateNamespace";
    private final String TEMPLATE_NAME_KEY = "templateName";
    private final String DETAILS_KEY = "details";
    private final String PERMISSION_KEY = "permission";

    @Test
    public void deserializeUserPreferences_EmptyList() {
        Assert.assertTrue(PreferencesDeserializer.deserializeUserPreferences(new ArrayList<>()).isEmpty());
    }

    @Test
    public void deserializeUserPreferences_givenValidRows_deserializesCorrectly() {
        // given a typical map of user preferences
        Map<String, Object> row1 = ImmutableMap.of(
                OBJ_ID_KEY, "4fa0776e-89ba-4427-951b-cda3006cd3a7",
                PRINCIPLE_NAME_KEY, "khuntley",
                KEY_CODE_KEY, "checkedLinkFilters",
                VALUE_KEY, "[\"activities\",\"references\"]");
        Map<String, Object> row2 = ImmutableMap.of(
                OBJ_ID_KEY, "4fa0776e-89ba-4427-951b-cda3006cd3a8",
                PRINCIPLE_NAME_KEY, "khuntley",
                KEY_CODE_KEY, "sidebarOut",
                VALUE_KEY, "true");
        Map<String, Object> row3 = ImmutableMap.of(
                OBJ_ID_KEY, "4fa0776e-89ba-4427-951b-cda3006cd3a9",
                PRINCIPLE_NAME_KEY, "khuntley",
                KEY_CODE_KEY, "defaultFinancialsChartOfAccountsCode",
                VALUE_KEY, "\"\"");
        List<Map<String, Object>> rows = Lists.newArrayList(row1, row2, row3);

        Map<String, Object> result = PreferencesDeserializer.deserializeUserPreferences(rows);
        Assert.assertTrue(result.size() == 3);
        Assert.assertEquals(Lists.newArrayList("activities", "references"), result.get("checkedLinkFilters"));
        Assert.assertEquals(true, result.get("sidebarOut"));
        Assert.assertEquals("", result.get("defaultFinancialsChartOfAccountsCode"));
    }

    @Test
    public void deserializeUserPreferences_mapperThrowsIOE() {
        // includes invalid json -- should throw IOE
        Map<String, Object> row = ImmutableMap.of(
                OBJ_ID_KEY, "4fa0776e-89ba-4427-951b-cda3006cd3a7",
                PRINCIPLE_NAME_KEY, "khuntley",
                KEY_CODE_KEY, "defaultFinancialsChartOfAccountsCode",
                VALUE_KEY, "");
        List<Map<String, Object>> rows = Lists.newArrayList(row);

        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Error parsing json");

        PreferencesDeserializer.deserializeUserPreferences(rows);
    }

    @Test
    public void deserializeMenuLinks_emptyList() {
        Assert.assertTrue(PreferencesDeserializer.deserializeMenuLinks(new ArrayList<>()).isEmpty());
    }

    @Test
    public void deserializeMenuLinks_givenValidRows_deserializesCorrectlyAndPreservesPosition() {
        // given a typical map of menu links
        Map<String, Object> row1 = ImmutableMap.of(
                OBJ_ID_KEY, "4fa0776e-89ba-4427-951b-cda3006cd3a8",
                MENU_LABEL_COLUMN_KEY, "Help",
                MENU_LINK_COLUMN_KEY, "static/help/default.htm");
        Map<String, Object> row2 = ImmutableMap.of(
                OBJ_ID_KEY, "4fa0776e-89ba-4427-951b-cda3006cd3a9",
                MENU_LABEL_COLUMN_KEY, "Feedback",
                MENU_LINK_COLUMN_KEY, "static/feedback/default.htm");
        Map<String, Object> row3 = ImmutableMap.of(
                OBJ_ID_KEY, "4fa0776e-89ba-4427-951b-cda3006cd3a7",
                MENU_LABEL_COLUMN_KEY, "Taco Tuesday",
                MENU_LINK_COLUMN_KEY, "static/tt/default.htm");
        List<Map<String, Object>> rows = Lists.newArrayList(row1, row2, row3);

        List<Map<String, Object>> result = PreferencesDeserializer.deserializeMenuLinks(rows);

        Assert.assertTrue(result.size() == 3);
        Map<String, Object> firstLink = result.get(0);
        Assert.assertTrue(firstLink.size() == 2);
        Assert.assertEquals("Help", firstLink.get(LABEL_KEY));
        Assert.assertEquals("static/help/default.htm", firstLink.get(LINK_KEY));

        Map<String, Object> secondLink = result.get(1);
        Assert.assertTrue(secondLink.size() == 2);
        Assert.assertEquals("Feedback", secondLink.get(LABEL_KEY));
        Assert.assertEquals("static/feedback/default.htm", secondLink.get(LINK_KEY));

        Map<String, Object> thirdLink = result.get(2);
        Assert.assertTrue(thirdLink.size() == 2);
        Assert.assertEquals("Taco Tuesday", thirdLink.get(LABEL_KEY));
        Assert.assertEquals("static/tt/default.htm", thirdLink.get(LINK_KEY));
    }

    @Test
    public void deserializeLinkPermissionDetail_givenNoExistingDetailsAndNullPermDetailKey_NoDetailsMapGetsAdded() {
        // given a typical row of nav link data
        Map<String, Object> row = new LinkedHashMap<String, Object>() {{
            put(GROUP_LABEL_KEY, "Accounting");
            put(GROUP_POSITION_KEY, 0);
            put(LINK_TYPE_KEY, "kfs");
            put(LINK_CATEGORY_KEY, "administration");
            put(LINK_POSITION_KEY, 1);
            put(NEW_TARGET_KEY, null);
            put(BUSINESS_OBJECT_CLASS_KEY, null);
            put(DOCUMENT_TYPE_CODE_KEY, null);
            put(LINK_LABEL_KEY, "Collector Flat File Upload");
            put(LINK_VALUE_KEY,
                    "batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=collectorXmlInputFileType");
            put(PERM_TEMPLATE_NAME_KEY, "Upload Batch Input File(s)");
            put(PERM_TEMPLATE_NAMESPACE_KEY, "KR-NS");
            put(PERM_DETAIL_KEY_KEY, null);
            put(PERM_DETAIL_VAlUE_KEY, null);
        }};

        // and a perm without details
        Map<String, Object> perm = ImmutableMap.of(
                TEMPLATE_NAMESPACE_KEY, "KR-NS",
                TEMPLATE_NAME_KEY, "Upload Batch Input File(s)");

        PreferencesDeserializer.deserializeLinkPermissionDetail(row, perm);

        // then no detail map is added to perm
        Assert.assertTrue(perm.size() == 2);
        Assert.assertFalse(perm.containsKey("details"));
    }

    @Test
    public void deserializeLinkPermissionDetail_givenExistingDetailsAndNullPermDetailKey_NothingGetsAddedToDetailMap() {
        // given a typical row of nav link data
        Map<String, Object> row = new LinkedHashMap<String, Object>() {{
            put(GROUP_LABEL_KEY, "Accounting");
            put(GROUP_POSITION_KEY, 1);
            put(LINK_TYPE_KEY, "kfs");
            put(LINK_CATEGORY_KEY, "administration");
            put(LINK_POSITION_KEY, 1);
            put(NEW_TARGET_KEY, null);
            put(BUSINESS_OBJECT_CLASS_KEY, null);
            put(DOCUMENT_TYPE_CODE_KEY, null);
            put(LINK_LABEL_KEY, "Collector Flat File Upload");
            put(LINK_VALUE_KEY,
                    "batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=collectorXmlInputFileType");
            put(PERM_TEMPLATE_NAME_KEY, "Upload Batch Input File(s)");
            put(PERM_TEMPLATE_NAMESPACE_KEY, "KR-NS");
            put(PERM_DETAIL_KEY_KEY, null);
            put(PERM_DETAIL_VAlUE_KEY, null);
        }};

        // details map with single item
        Map<String, Object> details = ImmutableMap.of(
                "namespaceCode", "KFS-AR"
        );

        // and a perm with details
        Map<String, Object> perm = ImmutableMap.of(
                TEMPLATE_NAMESPACE_KEY, "KR-NS",
                TEMPLATE_NAME_KEY, "Upload Batch Input File(s)",
                DETAILS_KEY, details);

        PreferencesDeserializer.deserializeLinkPermissionDetail(row, perm);

        Assert.assertTrue(perm.size() == 3);
        Assert.assertTrue(perm.containsKey(DETAILS_KEY));
        // nothing added to detail map in perm
        Assert.assertEquals(details, perm.get(DETAILS_KEY));
    }

    @Test
    public void deserializeLinkPermissionDetail_givenNoExistingDetailsAndValidPermDetailKey_DetailsMapGetsAdded() {
        // given a typical row of nav link data
        Map<String, Object> row = new LinkedHashMap<String, Object>() {{
            put(GROUP_LABEL_KEY, "Accounting");
            put(GROUP_POSITION_KEY, 0);
            put(LINK_TYPE_KEY, "kfs");
            put(LINK_CATEGORY_KEY, "administration");
            put(LINK_POSITION_KEY, 1);
            put(NEW_TARGET_KEY, null);
            put(BUSINESS_OBJECT_CLASS_KEY, null);
            put(DOCUMENT_TYPE_CODE_KEY, null);
            put(LINK_LABEL_KEY, "Collector Flat File Upload");
            put(LINK_VALUE_KEY,
                    "batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=collectorXmlInputFileType");
            put(PERM_TEMPLATE_NAME_KEY, "Upload Batch Input File(s)");
            put(PERM_TEMPLATE_NAMESPACE_KEY, "KR-NS");
            put(PERM_DETAIL_KEY_KEY, "beanName");
            put(PERM_DETAIL_VAlUE_KEY, "collectorFlatFileInputFileType");
        }};

        // and a perm without details
        Map<String, Object> perm = new LinkedHashMap<String, Object>() {{
            put(TEMPLATE_NAMESPACE_KEY, "KR-NS");
            put(TEMPLATE_NAME_KEY, "Upload Batch Input File(s)");
        }};

        PreferencesDeserializer.deserializeLinkPermissionDetail(row, perm);

        Assert.assertTrue(perm.size() == 3);
        Assert.assertTrue(perm.containsKey(DETAILS_KEY));
        Map<String, String> resultingDetails = (Map<String, String>) perm.get(DETAILS_KEY);
        Assert.assertTrue(resultingDetails.size() == 1);
        Assert.assertEquals("collectorFlatFileInputFileType", resultingDetails.get("beanName"));
    }

    @Test
    public void deserializeLinkPermissionDetail_givenExistingDetailsAndValidPermDetailKey_NewDetailsGetsAddedToDetailMap() {
        // given a typical row of nav link data
        Map<String, Object> row = new LinkedHashMap<String, Object>() {{
            put(GROUP_LABEL_KEY, "Accounting");
            put(GROUP_POSITION_KEY, 1);
            put(LINK_TYPE_KEY, "kfs");
            put(LINK_CATEGORY_KEY, "administration");
            put(LINK_POSITION_KEY, 1);
            put(NEW_TARGET_KEY, null);
            put(BUSINESS_OBJECT_CLASS_KEY, null);
            put(DOCUMENT_TYPE_CODE_KEY, null);
            put(LINK_LABEL_KEY, "Collector Flat File Upload");
            put(LINK_VALUE_KEY,
                    "batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=collectorXmlInputFileType");
            put(PERM_TEMPLATE_NAME_KEY, "Upload Batch Input File(s)");
            put(PERM_TEMPLATE_NAMESPACE_KEY, "KR-NS");
            put(PERM_DETAIL_KEY_KEY, "beanName");
            put(PERM_DETAIL_VAlUE_KEY, "collectorFlatFileInputFileType");
        }};

        // details map with single item
        Map<String, Object> details = new LinkedHashMap<String, Object>() {{
            put("namespaceCode", "KFS-AR");
        }};

        // and a perm with details
        Map<String, Object> perm = ImmutableMap.of(
                TEMPLATE_NAMESPACE_KEY, "KR-NS",
                TEMPLATE_NAME_KEY, "Upload Batch Input File(s)",
                DETAILS_KEY, details);

        PreferencesDeserializer.deserializeLinkPermissionDetail(row, perm);

        Assert.assertTrue(perm.size() == 3);
        Assert.assertTrue(perm.containsKey("details"));
        Map<String, String> resultingDetails = (Map<String, String>) perm.get(DETAILS_KEY);
        Assert.assertTrue(resultingDetails.size() == 2);
        Assert.assertEquals("KFS-AR", resultingDetails.get("namespaceCode"));
        Assert.assertEquals("collectorFlatFileInputFileType", resultingDetails.get("beanName"));
    }

    @Test
    public void deserializeLinkPermission_givenNoExistingPermissionAndBothTemplateNameAndNamespaceAreNull_NoPermissionGetsAdded() {
        // given a typical row of nav link data
        Map<String, Object> row = new LinkedHashMap<String, Object>() {{
            put(GROUP_LABEL_KEY, "Accounting");
            put(GROUP_POSITION_KEY, 1);
            put(LINK_TYPE_KEY, "kfs");
            put(LINK_CATEGORY_KEY, "administration");
            put(LINK_POSITION_KEY, 1);
            put(NEW_TARGET_KEY, null);
            put(BUSINESS_OBJECT_CLASS_KEY, null);
            put(DOCUMENT_TYPE_CODE_KEY, null);
            put(LINK_LABEL_KEY, "Collector Flat File Upload");
            put(LINK_VALUE_KEY,
                    "batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=collectorXmlInputFileType");
            put(PERM_TEMPLATE_NAME_KEY, null);
            put(PERM_TEMPLATE_NAMESPACE_KEY, null);
            put(PERM_DETAIL_KEY_KEY, null);
            put(PERM_DETAIL_VAlUE_KEY, null);
        }};

        // and a link without an existing permission
        Map<String, Object> link = ImmutableMap.of(
                LINK_KEY,
                "arCustomerAgingReportLookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.CustomerAgingReportDetail",
                LINK_LABEL_KEY, "Customer Aging Report",
                LINK_TYPE_KEY, "kfs");

        PreferencesDeserializer.deserializeLinkPermission(row, link);

        // no permission map is added
        Assert.assertTrue(link.size() == 3);
        Assert.assertFalse(link.containsKey(PERMISSION_KEY));
    }

    @Test
    public void deserializeLinkPermission_givenNoExistingPermissionAndTemplateNamespaceIsNotNull_PermissionGetsAdded() {
        // given a typical row of nav link data
        Map<String, Object> row = new LinkedHashMap<String, Object>() {{
            put(GROUP_LABEL_KEY, "Accounting");
            put(GROUP_POSITION_KEY, 1);
            put(LINK_TYPE_KEY, "kfs");
            put(LINK_CATEGORY_KEY, "administration");
            put(LINK_POSITION_KEY, 1);
            put(NEW_TARGET_KEY, null);
            put(BUSINESS_OBJECT_CLASS_KEY, null);
            put(DOCUMENT_TYPE_CODE_KEY, null);
            put(LINK_LABEL_KEY, "Collector Flat File Upload");
            put(LINK_VALUE_KEY,
                    "batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=collectorXmlInputFileType");
            put(PERM_TEMPLATE_NAME_KEY, null);
            put(PERM_TEMPLATE_NAMESPACE_KEY, "KR-NS");
            put(PERM_DETAIL_KEY_KEY, null);
            put(PERM_DETAIL_VAlUE_KEY, null);
        }};

        // and a link without an existing permission
        Map<String, Object> link = new LinkedHashMap<String, Object>() {{
            put(LINK_KEY,
                    "arCustomerAgingReportLookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.CustomerAgingReportDetail");
            put(LINK_LABEL_KEY, "Customer Aging Report");
            put(LINK_TYPE_KEY, "kfs");
        }};

        PreferencesDeserializer.deserializeLinkPermission(row, link);

        // permission map is added
        Assert.assertTrue(link.size() == 4);
        Assert.assertTrue(link.containsKey(PERMISSION_KEY));
        Map<String, String> resultingPermission = (Map<String, String>) link.get(PERMISSION_KEY);
        Assert.assertTrue(resultingPermission.size() == 2);
        Assert.assertEquals("KR-NS", resultingPermission.get(TEMPLATE_NAMESPACE_KEY));
        Assert.assertNull(resultingPermission.get(TEMPLATE_NAME_KEY));
    }

    @Test
    public void deserializeLinkPermission_givenNoExistingPermissionAndTemplateNameIsNotNull_PermissionGetsAdded() {
        // given a typical row of nav link data
        Map<String, Object> row = new LinkedHashMap<String, Object>() {{
            put(GROUP_LABEL_KEY, "Accounting");
            put(GROUP_POSITION_KEY, 1);
            put(LINK_TYPE_KEY, "kfs");
            put(LINK_CATEGORY_KEY, "administration");
            put(LINK_POSITION_KEY, 1);
            put(NEW_TARGET_KEY, null);
            put(BUSINESS_OBJECT_CLASS_KEY, null);
            put(DOCUMENT_TYPE_CODE_KEY, null);
            put(LINK_LABEL_KEY, "Collector Flat File Upload");
            put(LINK_VALUE_KEY,
                    "batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=collectorXmlInputFileType");
            put(PERM_TEMPLATE_NAME_KEY, "Upload Batch Input File(s)");
            put(PERM_TEMPLATE_NAMESPACE_KEY, null);
            put(PERM_DETAIL_KEY_KEY, null);
            put(PERM_DETAIL_VAlUE_KEY, null);
        }};

        // and a link without an existing permission
        Map<String, Object> link = new LinkedHashMap<String, Object>() {{
            put(LINK_KEY,
                    "arCustomerAgingReportLookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.CustomerAgingReportDetail");
            put(LINK_LABEL_KEY, "Customer Aging Report");
            put(LINK_TYPE_KEY, "kfs");
        }};

        PreferencesDeserializer.deserializeLinkPermission(row, link);

        // permission map is added
        Assert.assertTrue(link.size() == 4);
        Assert.assertTrue(link.containsKey(PERMISSION_KEY));
        Map<String, String> resultingPermission = (Map<String, String>) link.get(PERMISSION_KEY);
        Assert.assertTrue(resultingPermission.size() == 2);
        Assert.assertNull(resultingPermission.get(TEMPLATE_NAMESPACE_KEY));
        Assert.assertEquals("Upload Batch Input File(s)", resultingPermission.get(TEMPLATE_NAME_KEY));
    }

    @Test
    public void deserializeLinkPermission_givenExistingPermissionAndBothTemplateNameAndNamespaceAreNull_PermissionNotModified() {
        // given a typical row of nav link data
        Map<String, Object> row = new LinkedHashMap<String, Object>() {{
            put(GROUP_LABEL_KEY, "Accounting");
            put(GROUP_POSITION_KEY, 0);
            put(LINK_TYPE_KEY, "kfs");
            put(LINK_CATEGORY_KEY, "administration");
            put(LINK_POSITION_KEY, 1);
            put(NEW_TARGET_KEY, null);
            put(BUSINESS_OBJECT_CLASS_KEY, null);
            put(DOCUMENT_TYPE_CODE_KEY, null);
            put(LINK_LABEL_KEY, "Collector Flat File Upload");
            put(LINK_VALUE_KEY,
                    "batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=collectorXmlInputFileType");
            put(PERM_TEMPLATE_NAME_KEY, null);
            put(PERM_TEMPLATE_NAMESPACE_KEY, null);
            put(PERM_DETAIL_KEY_KEY, null);
            put(PERM_DETAIL_VAlUE_KEY, null);
        }};

        // existing permission
        Map<String, Object> permission = new LinkedHashMap<String, Object>() {{
            put(TEMPLATE_NAME_KEY, "Initiate Document");
            put(TEMPLATE_NAMESPACE_KEY, "KR-SYS");
        }};

        // and a link with an existing permission
        Map<String, Object> link = ImmutableMap.of(
                LINK_KEY,
                "arCustomerAgingReportLookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.CustomerAgingReportDetail",
                LINK_LABEL_KEY, "Customer Aging Report",
                LINK_TYPE_KEY, "kfs",
                PERMISSION_KEY, permission);

        PreferencesDeserializer.deserializeLinkPermission(row, link);

        Assert.assertTrue(link.size() == 4);
        Assert.assertTrue(link.containsKey(PERMISSION_KEY));
        // permission map is unchanged
        Assert.assertEquals(permission, link.get(PERMISSION_KEY));
    }

    @Test
    public void deserializeLinkPermission_givenExistingPermissionAndTemplateNamespaceIsNotNull_PermissionValuesAreOverwritten() {
        // given a typical row of nav link data
        Map<String, Object> row = new LinkedHashMap<String, Object>() {{
            put(GROUP_LABEL_KEY, "Accounting");
            put(GROUP_POSITION_KEY, 0);
            put(LINK_TYPE_KEY, "kfs");
            put(LINK_CATEGORY_KEY, "administration");
            put(LINK_POSITION_KEY, 1);
            put(NEW_TARGET_KEY, null);
            put(BUSINESS_OBJECT_CLASS_KEY, null);
            put(DOCUMENT_TYPE_CODE_KEY, null);
            put(LINK_LABEL_KEY, "Collector Flat File Upload");
            put(LINK_VALUE_KEY,
                    "batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=collectorXmlInputFileType");
            put(PERM_TEMPLATE_NAME_KEY, null);
            put(PERM_TEMPLATE_NAMESPACE_KEY, "KR-NS");
            put(PERM_DETAIL_KEY_KEY, null);
            put(PERM_DETAIL_VAlUE_KEY, null);
        }};

        // existing permission
        Map<String, Object> permission = new LinkedHashMap<String, Object>() {{
            put(TEMPLATE_NAME_KEY, "Initiate Document");
            put(TEMPLATE_NAMESPACE_KEY, "KR-SYS");
        }};

        // and a link with an existing permission
        Map<String, Object> link = ImmutableMap.of(
                LINK_KEY,
                "arCustomerAgingReportLookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.CustomerAgingReportDetail",
                LINK_LABEL_KEY, "Customer Aging Report",
                LINK_TYPE_KEY, "kfs",
                PERMISSION_KEY, permission);

        PreferencesDeserializer.deserializeLinkPermission(row, link);

        Assert.assertTrue(link.size() == 4);
        Assert.assertTrue(link.containsKey(PERMISSION_KEY));
        // permission is added to map
        Map<String, String> resultingPermission = (Map<String, String>) link.get(PERMISSION_KEY);
        Assert.assertTrue(resultingPermission.size() == 2);
        Assert.assertEquals("KR-NS", resultingPermission.get(TEMPLATE_NAMESPACE_KEY));
        Assert.assertNull(resultingPermission.get(TEMPLATE_NAME_KEY));
    }

    @Test
    public void deserializeLinkPermission_givenExistingPermissionAndTemplateNameIsNotNull_PermissionValuesAreOverwritten() {
        // given a typical row of nav link data
        Map<String, Object> row = new LinkedHashMap<String, Object>() {{
            put(GROUP_LABEL_KEY, "Accounting");
            put(GROUP_POSITION_KEY, 0);
            put(LINK_TYPE_KEY, "kfs");
            put(LINK_CATEGORY_KEY, "administration");
            put(LINK_POSITION_KEY, 1);
            put(NEW_TARGET_KEY, null);
            put(BUSINESS_OBJECT_CLASS_KEY, null);
            put(DOCUMENT_TYPE_CODE_KEY, null);
            put(LINK_LABEL_KEY, "Collector Flat File Upload");
            put(LINK_VALUE_KEY,
                    "batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=collectorXmlInputFileType");
            put(PERM_TEMPLATE_NAME_KEY, "Upload Batch Input File(s)");
            put(PERM_TEMPLATE_NAMESPACE_KEY, null);
            put(PERM_DETAIL_KEY_KEY, null);
            put(PERM_DETAIL_VAlUE_KEY, null);
        }};

        // existing permission
        Map<String, Object> permission = new LinkedHashMap<String, Object>() {{
            put(TEMPLATE_NAME_KEY, "Initiate Document");
            put(TEMPLATE_NAMESPACE_KEY, "KR-SYS");
        }};

        // and a link with an existing permission
        Map<String, Object> link = ImmutableMap.of(
                LINK_KEY,
                "arCustomerAgingReportLookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.CustomerAgingReportDetail",
                LINK_LABEL_KEY, "Customer Aging Report",
                LINK_TYPE_KEY, "kfs",
                PERMISSION_KEY, permission);

        PreferencesDeserializer.deserializeLinkPermission(row, link);

        Assert.assertTrue(link.size() == 4);
        Assert.assertTrue(link.containsKey(PERMISSION_KEY));
        // permission is added to map
        Map<String, String> resultingPermission = (Map<String, String>) link.get(PERMISSION_KEY);
        Assert.assertTrue(resultingPermission.size() == 2);
        Assert.assertNull(resultingPermission.get(TEMPLATE_NAMESPACE_KEY));
        Assert.assertEquals("Upload Batch Input File(s)", resultingPermission.get(TEMPLATE_NAME_KEY));
    }

    @Test
    public void deserializeLink_givenEmptyCategoryAndAllLinkFieldsNonNull_NewTargetY_newLinkIsAddedAsExpectedWithAllFields() {
        // given a typical row of nav link data
        Map<String, Object> row = new LinkedHashMap<String, Object>() {{
            put(GROUP_LABEL_KEY, "Accounting");
            put(GROUP_POSITION_KEY, 0);
            put(LINK_TYPE_KEY, "kfs");
            put(LINK_CATEGORY_KEY, "administration");
            put(LINK_POSITION_KEY, 0);
            put(NEW_TARGET_KEY, "Y");
            put(BUSINESS_OBJECT_CLASS_KEY, "org.kuali.kfs.vnd.businessobject.VendorContract");
            put(DOCUMENT_TYPE_CODE_KEY, "BA");
            put(LINK_LABEL_KEY, "Collector Flat File Upload");
            put(LINK_VALUE_KEY,
                    "batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=collectorXmlInputFileType");
            put(PERM_TEMPLATE_NAME_KEY, null);
            put(PERM_TEMPLATE_NAMESPACE_KEY, null);
            put(PERM_DETAIL_KEY_KEY, null);
            put(PERM_DETAIL_VAlUE_KEY, null);
        }};

        // empty link category
        List<Map<String, Object>> linkCategory = new ArrayList<>();

        PreferencesDeserializer.deserializeLink(row, linkCategory);

        Assert.assertTrue(linkCategory.size() == 1);
        Map<String, Object> resultingLink = linkCategory.get(0);
        Assert.assertTrue(resultingLink.size() == 6);
        Assert.assertEquals("kfs", resultingLink.get(LINK_TYPE_KEY));
        Assert.assertEquals("BA", resultingLink.get(DOCUMENT_TYPE_CODE_KEY));
        Assert.assertEquals("org.kuali.kfs.vnd.businessobject.VendorContract",
                resultingLink.get(BUSINESS_OBJECT_CLASS_KEY));
        Assert.assertEquals(true, resultingLink.get(NEW_TARGET_KEY));
        Assert.assertEquals("Collector Flat File Upload", resultingLink.get(LABEL_KEY));
        Assert.assertEquals(
                "batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=collectorXmlInputFileType",
                resultingLink.get(LINK_KEY));
    }

    @Test
    public void deserializeLink_givenEmptyCategoryAndAllLinkFieldsNonNull_NewTargetN_newLinkIsAddedAsExpectedWithAllFields() {
        // given a typical row of nav link data
        Map<String, Object> row = new LinkedHashMap<String, Object>() {{
            put(GROUP_LABEL_KEY, "Accounting");
            put(GROUP_POSITION_KEY, 0);
            put(LINK_TYPE_KEY, "kfs");
            put(LINK_CATEGORY_KEY, "administration");
            put(LINK_POSITION_KEY, 0);
            put(NEW_TARGET_KEY, "N");
            put(BUSINESS_OBJECT_CLASS_KEY, "org.kuali.kfs.vnd.businessobject.VendorContract");
            put(DOCUMENT_TYPE_CODE_KEY, "BA");
            put(LINK_LABEL_KEY, "Collector Flat File Upload");
            put(LINK_VALUE_KEY,
                    "batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=collectorXmlInputFileType");
            put(PERM_TEMPLATE_NAME_KEY, null);
            put(PERM_TEMPLATE_NAMESPACE_KEY, null);
            put(PERM_DETAIL_KEY_KEY, null);
            put(PERM_DETAIL_VAlUE_KEY, null);
        }};

        // empty link category
        List<Map<String, Object>> linkCategory = new ArrayList<>();

        PreferencesDeserializer.deserializeLink(row, linkCategory);

        Assert.assertTrue(linkCategory.size() == 1);
        Map<String, Object> resultingLink = linkCategory.get(0);
        Assert.assertTrue(resultingLink.size() == 6);
        Assert.assertEquals("kfs", resultingLink.get(LINK_TYPE_KEY));
        Assert.assertEquals("BA", resultingLink.get(DOCUMENT_TYPE_CODE_KEY));
        Assert.assertEquals("org.kuali.kfs.vnd.businessobject.VendorContract",
                resultingLink.get(BUSINESS_OBJECT_CLASS_KEY));
        Assert.assertEquals(false, resultingLink.get(NEW_TARGET_KEY));
        Assert.assertEquals("Collector Flat File Upload", resultingLink.get(LABEL_KEY));
        Assert.assertEquals(
                "batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=collectorXmlInputFileType",
                resultingLink.get(LINK_KEY));
    }

    @Test
    public void deserializeLink_givenEmptyCategoryAndAllLinkFieldsNonNull_NewTargetBadData_newLinkIsAddedAsExpectedWithAllFields() {
        // given a typical row of nav link data
        Map<String, Object> row = new LinkedHashMap<String, Object>() {{
            put(GROUP_LABEL_KEY, "Accounting");
            put(GROUP_POSITION_KEY, 0);
            put(LINK_TYPE_KEY, "kfs");
            put(LINK_CATEGORY_KEY, "administration");
            put(LINK_POSITION_KEY, 0);
            put(NEW_TARGET_KEY, "Random value here");
            put(BUSINESS_OBJECT_CLASS_KEY, "org.kuali.kfs.vnd.businessobject.VendorContract");
            put(DOCUMENT_TYPE_CODE_KEY, "BA");
            put(LINK_LABEL_KEY, "Collector Flat File Upload");
            put(LINK_VALUE_KEY,
                    "batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=collectorXmlInputFileType");
            put(PERM_TEMPLATE_NAME_KEY, null);
            put(PERM_TEMPLATE_NAMESPACE_KEY, null);
            put(PERM_DETAIL_KEY_KEY, null);
            put(PERM_DETAIL_VAlUE_KEY, null);
        }};

        // empty link category
        List<Map<String, Object>> linkCategory = new ArrayList<>();

        PreferencesDeserializer.deserializeLink(row, linkCategory);

        Assert.assertTrue(linkCategory.size() == 1);
        Map<String, Object> resultingLink = linkCategory.get(0);
        Assert.assertTrue(resultingLink.size() == 6);
        Assert.assertEquals("kfs", resultingLink.get(LINK_TYPE_KEY));
        Assert.assertEquals("BA", resultingLink.get(DOCUMENT_TYPE_CODE_KEY));
        Assert.assertEquals("org.kuali.kfs.vnd.businessobject.VendorContract",
                resultingLink.get(BUSINESS_OBJECT_CLASS_KEY));
        Assert.assertEquals(false, resultingLink.get(NEW_TARGET_KEY));
        Assert.assertEquals("Collector Flat File Upload", resultingLink.get(LABEL_KEY));
        Assert.assertEquals(
                "batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=collectorXmlInputFileType",
                resultingLink.get(LINK_KEY));
    }

    @Test
    public void deserializeLink_givenEmptyCategoryAndDocTypeCodeNull_newLinkIsAddedAsExpectedWithNoDocTypeCode() {
        // given a typical row of nav link data
        Map<String, Object> row = new LinkedHashMap<String, Object>() {{
            put(GROUP_LABEL_KEY, "Accounting");
            put(GROUP_POSITION_KEY, 0);
            put(LINK_TYPE_KEY, "kfs");
            put(LINK_CATEGORY_KEY, "administration");
            put(LINK_POSITION_KEY, 0);
            put(NEW_TARGET_KEY, "Y");
            put(BUSINESS_OBJECT_CLASS_KEY, "org.kuali.kfs.vnd.businessobject.VendorContract");
            put(DOCUMENT_TYPE_CODE_KEY, null);
            put(LINK_LABEL_KEY, "Collector Flat File Upload");
            put(LINK_VALUE_KEY,
                    "batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=collectorXmlInputFileType");
            put(PERM_TEMPLATE_NAME_KEY, null);
            put(PERM_TEMPLATE_NAMESPACE_KEY, null);
            put(PERM_DETAIL_KEY_KEY, null);
            put(PERM_DETAIL_VAlUE_KEY, null);
        }};

        // empty link category
        List<Map<String, Object>> linkCategory = new ArrayList<>();

        PreferencesDeserializer.deserializeLink(row, linkCategory);

        Assert.assertTrue(linkCategory.size() == 1);
        Map<String, Object> resultingLink = linkCategory.get(0);
        Assert.assertTrue(resultingLink.size() == 5);
        Assert.assertEquals("kfs", resultingLink.get(LINK_TYPE_KEY));
        Assert.assertEquals("org.kuali.kfs.vnd.businessobject.VendorContract",
                resultingLink.get(BUSINESS_OBJECT_CLASS_KEY));
        Assert.assertEquals(true, resultingLink.get(NEW_TARGET_KEY));
        Assert.assertEquals("Collector Flat File Upload", resultingLink.get(LABEL_KEY));
        Assert.assertEquals(
                "batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=collectorXmlInputFileType",
                resultingLink.get(LINK_KEY));
        Assert.assertNull(resultingLink.get(DOCUMENT_TYPE_CODE_KEY));
    }

    @Test
    public void deserializeLink_givenEmptyCategoryAndNewTargetNull_newLinkIsAddedAsExpectedWithNoNewTarget() {
        // given a typical row of nav link data
        Map<String, Object> row = new LinkedHashMap<String, Object>() {{
            put(GROUP_LABEL_KEY, "Accounting");
            put(GROUP_POSITION_KEY, 0);
            put(LINK_TYPE_KEY, "kfs");
            put(LINK_CATEGORY_KEY, "administration");
            put(LINK_POSITION_KEY, 0);
            put(NEW_TARGET_KEY, null);
            put(BUSINESS_OBJECT_CLASS_KEY, "org.kuali.kfs.vnd.businessobject.VendorContract");
            put(DOCUMENT_TYPE_CODE_KEY, "BA");
            put(LINK_LABEL_KEY, "Collector Flat File Upload");
            put(LINK_VALUE_KEY,
                    "batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=collectorXmlInputFileType");
            put(PERM_TEMPLATE_NAME_KEY, null);
            put(PERM_TEMPLATE_NAMESPACE_KEY, null);
            put(PERM_DETAIL_KEY_KEY, null);
            put(PERM_DETAIL_VAlUE_KEY, null);
        }};

        // empty link category
        List<Map<String, Object>> linkCategory = new ArrayList<>();

        PreferencesDeserializer.deserializeLink(row, linkCategory);

        Assert.assertTrue(linkCategory.size() == 1);
        Map<String, Object> resultingLink = linkCategory.get(0);
        Assert.assertTrue(resultingLink.size() == 5);
        Assert.assertEquals("kfs", resultingLink.get(LINK_TYPE_KEY));
        Assert.assertEquals("BA", resultingLink.get(DOCUMENT_TYPE_CODE_KEY));
        Assert.assertEquals("org.kuali.kfs.vnd.businessobject.VendorContract",
                resultingLink.get(BUSINESS_OBJECT_CLASS_KEY));
        Assert.assertEquals("Collector Flat File Upload", resultingLink.get(LABEL_KEY));
        Assert.assertEquals(
                "batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=collectorXmlInputFileType",
                resultingLink.get(LINK_KEY));
        Assert.assertNull(resultingLink.get(NEW_TARGET_KEY));
    }

    @Test
    public void deserializeLink_givenEmptyCategoryAndBusinessObjClassNull_newLinkIsAddedAsExpectedWithBusinessObjClass() {
        // given a typical row of nav link data
        Map<String, Object> row = new LinkedHashMap<String, Object>() {{
            put(GROUP_LABEL_KEY, "Accounting");
            put(GROUP_POSITION_KEY, 0);
            put(LINK_TYPE_KEY, "kfs");
            put(LINK_CATEGORY_KEY, "administration");
            put(LINK_POSITION_KEY, 0);
            put(NEW_TARGET_KEY, "Y");
            put(BUSINESS_OBJECT_CLASS_KEY, null);
            put(DOCUMENT_TYPE_CODE_KEY, "BA");
            put(LINK_LABEL_KEY, "Collector Flat File Upload");
            put(LINK_VALUE_KEY,
                    "batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=collectorXmlInputFileType");
            put(PERM_TEMPLATE_NAME_KEY, null);
            put(PERM_TEMPLATE_NAMESPACE_KEY, null);
            put(PERM_DETAIL_KEY_KEY, null);
            put(PERM_DETAIL_VAlUE_KEY, null);
        }};

        // empty link category
        List<Map<String, Object>> linkCategory = new ArrayList<>();

        PreferencesDeserializer.deserializeLink(row, linkCategory);

        Assert.assertTrue(linkCategory.size() == 1);
        Map<String, Object> resultingLink = linkCategory.get(0);
        Assert.assertTrue(resultingLink.size() == 5);
        Assert.assertEquals("kfs", resultingLink.get(LINK_TYPE_KEY));
        Assert.assertEquals("BA", resultingLink.get(DOCUMENT_TYPE_CODE_KEY));
        Assert.assertEquals(true, resultingLink.get(NEW_TARGET_KEY));
        Assert.assertEquals("Collector Flat File Upload", resultingLink.get(LABEL_KEY));
        Assert.assertEquals(
                "batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=collectorXmlInputFileType",
                resultingLink.get(LINK_KEY));
        Assert.assertNull(resultingLink.get(BUSINESS_OBJECT_CLASS_KEY));
    }

    @Test
    public void deserializeLink_givenEmptyCategoryAndLinkLabelNull_newLinkIsAddedAsExpectedWithNoLinkLabel() {
        // given a typical row of nav link data
        Map<String, Object> row = new LinkedHashMap<String, Object>() {{
            put(GROUP_LABEL_KEY, "Accounting");
            put(GROUP_POSITION_KEY, 0);
            put(LINK_TYPE_KEY, "kfs");
            put(LINK_CATEGORY_KEY, "administration");
            put(LINK_POSITION_KEY, 0);
            put(NEW_TARGET_KEY, "Y");
            put(BUSINESS_OBJECT_CLASS_KEY, "org.kuali.kfs.vnd.businessobject.VendorContract");
            put(DOCUMENT_TYPE_CODE_KEY, "BA");
            put(LINK_LABEL_KEY, null);
            put(LINK_VALUE_KEY,
                    "batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=collectorXmlInputFileType");
            put(PERM_TEMPLATE_NAME_KEY, null);
            put(PERM_TEMPLATE_NAMESPACE_KEY, null);
            put(PERM_DETAIL_KEY_KEY, null);
            put(PERM_DETAIL_VAlUE_KEY, null);
        }};

        // empty link category
        List<Map<String, Object>> linkCategory = new ArrayList<>();

        PreferencesDeserializer.deserializeLink(row, linkCategory);

        Assert.assertTrue(linkCategory.size() == 1);
        Map<String, Object> resultingLink = linkCategory.get(0);
        Assert.assertTrue(resultingLink.size() == 5);
        Assert.assertEquals("kfs", resultingLink.get(LINK_TYPE_KEY));
        Assert.assertEquals("BA", resultingLink.get(DOCUMENT_TYPE_CODE_KEY));
        Assert.assertEquals("org.kuali.kfs.vnd.businessobject.VendorContract",
                resultingLink.get(BUSINESS_OBJECT_CLASS_KEY));
        Assert.assertEquals(true, resultingLink.get(NEW_TARGET_KEY));
        Assert.assertEquals(
                "batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=collectorXmlInputFileType",
                resultingLink.get(LINK_KEY));
        Assert.assertNull(resultingLink.get(LABEL_KEY));
    }

    @Test
    public void deserializeLink_givenEmptyCategoryAndLinkValueNull_newLinkIsAddedAsExpectedWithNoLinkValue() {
        // given a typical row of nav link data
        Map<String, Object> row = new LinkedHashMap<String, Object>() {{
            put(GROUP_LABEL_KEY, "Accounting");
            put(GROUP_POSITION_KEY, 0);
            put(LINK_TYPE_KEY, "kfs");
            put(LINK_CATEGORY_KEY, "administration");
            put(LINK_POSITION_KEY, 0);
            put(NEW_TARGET_KEY, "Y");
            put(BUSINESS_OBJECT_CLASS_KEY, "org.kuali.kfs.vnd.businessobject.VendorContract");
            put(DOCUMENT_TYPE_CODE_KEY, "BA");
            put(LINK_LABEL_KEY, "Collector Flat File Upload");
            put(LINK_VALUE_KEY, null);
            put(PERM_TEMPLATE_NAME_KEY, null);
            put(PERM_TEMPLATE_NAMESPACE_KEY, null);
            put(PERM_DETAIL_KEY_KEY, null);
            put(PERM_DETAIL_VAlUE_KEY, null);
        }};

        // empty link category
        List<Map<String, Object>> linkCategory = new ArrayList<>();

        PreferencesDeserializer.deserializeLink(row, linkCategory);

        Assert.assertTrue(linkCategory.size() == 1);
        Map<String, Object> resultingLink = linkCategory.get(0);
        Assert.assertTrue(resultingLink.size() == 5);
        Assert.assertEquals("kfs", resultingLink.get(LINK_TYPE_KEY));
        Assert.assertEquals("BA", resultingLink.get(DOCUMENT_TYPE_CODE_KEY));
        Assert.assertEquals("org.kuali.kfs.vnd.businessobject.VendorContract",
                resultingLink.get(BUSINESS_OBJECT_CLASS_KEY));
        Assert.assertEquals(true, resultingLink.get(NEW_TARGET_KEY));
        Assert.assertEquals("Collector Flat File Upload", resultingLink.get(LABEL_KEY));
        Assert.assertNull(resultingLink.get(LINK_KEY));
    }

    @Test
    public void deserializeLink_givenCategoryHasLinks_newLinkIsAddedAsExpected() {
        // given a typical row of nav link data
        Map<String, Object> row = new LinkedHashMap<String, Object>() {{
            put(GROUP_LABEL_KEY, "Accounting");
            put(GROUP_POSITION_KEY, 0);
            put(LINK_TYPE_KEY, "kfs");
            put(LINK_CATEGORY_KEY, "administration");
            put(LINK_POSITION_KEY, 2);
            put(NEW_TARGET_KEY, "Y");
            put(BUSINESS_OBJECT_CLASS_KEY, "org.kuali.kfs.vnd.businessobject.VendorContract");
            put(DOCUMENT_TYPE_CODE_KEY, "BA");
            put(LINK_LABEL_KEY, "Collector Flat File Upload");
            put(LINK_VALUE_KEY,
                    "batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=collectorXmlInputFileType");
            put(PERM_TEMPLATE_NAME_KEY, null);
            put(PERM_TEMPLATE_NAMESPACE_KEY, null);
            put(PERM_DETAIL_KEY_KEY, null);
            put(PERM_DETAIL_VAlUE_KEY, null);
        }};

        // link category has 2 links
        List<Map<String, Object>> linkCategory = new ArrayList<Map<String, Object>>() {{
            add(Maps.newHashMap());
            add(Maps.newHashMap());
        }};

        PreferencesDeserializer.deserializeLink(row, linkCategory);

        Assert.assertTrue(linkCategory.size() == 3);
        Map<String, Object> resultingLink = linkCategory.get(2);
        Assert.assertTrue(resultingLink.size() == 6);
        Assert.assertEquals("kfs", resultingLink.get(LINK_TYPE_KEY));
        Assert.assertEquals("BA", resultingLink.get(DOCUMENT_TYPE_CODE_KEY));
        Assert.assertEquals("org.kuali.kfs.vnd.businessobject.VendorContract",
                resultingLink.get(BUSINESS_OBJECT_CLASS_KEY));
        Assert.assertEquals(true, resultingLink.get(NEW_TARGET_KEY));
        Assert.assertEquals(
                "batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=collectorXmlInputFileType",
                resultingLink.get(LINK_KEY));
        Assert.assertEquals("Collector Flat File Upload", resultingLink.get(LABEL_KEY));
    }

    @Test
    public void deserializeLink_givenCategoryAlreadyHasThisLink_noNewLinkIsAdded() {
        // given a typical row of nav link data
        Map<String, Object> row = new LinkedHashMap<String, Object>() {{
            put(GROUP_LABEL_KEY, "Accounting");
            put(GROUP_POSITION_KEY, 0);
            put(LINK_TYPE_KEY, "kfs");
            put(LINK_CATEGORY_KEY, "administration");
            put(LINK_POSITION_KEY, 1);
            put(NEW_TARGET_KEY, "Y");
            put(BUSINESS_OBJECT_CLASS_KEY, "org.kuali.kfs.vnd.businessobject.VendorContract");
            put(DOCUMENT_TYPE_CODE_KEY, "BA");
            put(LINK_LABEL_KEY, "Collector Flat File Upload");
            put(LINK_VALUE_KEY,
                    "batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=collectorXmlInputFileType");
            put(PERM_TEMPLATE_NAME_KEY, "Use Screen");
            put(PERM_TEMPLATE_NAMESPACE_KEY, null);
            put(PERM_DETAIL_KEY_KEY, null);
            put(PERM_DETAIL_VAlUE_KEY, null);
        }};

        Map<String, Object> existingLink = new LinkedHashMap<String, Object>() {{
            put(LINK_TYPE_KEY, "kfs");
            put(NEW_TARGET_KEY, true);
            put(BUSINESS_OBJECT_CLASS_KEY, "org.kuali.kfs.vnd.businessobject.VendorContract");
            put(DOCUMENT_TYPE_CODE_KEY, "BA");
            put(LABEL_KEY, "Collector Flat File Upload");
            put(LINK_KEY,
                    "batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=collectorXmlInputFileType");
        }};

        // link category has 2 links -- including this one already (row would be adding permission)
        List<Map<String, Object>> linkCategory = new ArrayList<Map<String, Object>>() {{
            add(Maps.newHashMap());
            add(existingLink);
        }};

        PreferencesDeserializer.deserializeLink(row, linkCategory);

        // no new link is added & permission data is added
        Assert.assertTrue(linkCategory.size() == 2);
        Map<String, Object> resultingLink = linkCategory.get(1);
        Assert.assertTrue(resultingLink.size() == 7);
        Assert.assertEquals("kfs", resultingLink.get(LINK_TYPE_KEY));
        Assert.assertEquals("BA", resultingLink.get(DOCUMENT_TYPE_CODE_KEY));
        Assert.assertEquals("org.kuali.kfs.vnd.businessobject.VendorContract",
                resultingLink.get(BUSINESS_OBJECT_CLASS_KEY));
        Assert.assertEquals(true, resultingLink.get(NEW_TARGET_KEY));
        Assert.assertEquals(
                "batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=collectorXmlInputFileType",
                resultingLink.get(LINK_KEY));
        Assert.assertEquals("Collector Flat File Upload", resultingLink.get(LABEL_KEY));
        Assert.assertTrue(resultingLink.containsKey(PERMISSION_KEY));
    }

    @Test
    public void deserializeLinkCategory_givenNullLinks_linksAndLinkCategoriesGetsCreatedAndPopulatedWithSingleCategory() {
        // given a typical row of nav link data
        Map<String, Object> row = new LinkedHashMap<String, Object>() {{
            put(GROUP_LABEL_KEY, "Accounting");
            put(GROUP_POSITION_KEY, 1);
            put(LINK_TYPE_KEY, "kfs");
            put(LINK_CATEGORY_KEY, "administration");
            put(LINK_POSITION_KEY, 1);
            put(NEW_TARGET_KEY, "Y");
            put(BUSINESS_OBJECT_CLASS_KEY, "org.kuali.kfs.vnd.businessobject.VendorContract");
            put(DOCUMENT_TYPE_CODE_KEY, "BA");
            put(LINK_LABEL_KEY, "Collector Flat File Upload");
            put(LINK_VALUE_KEY,
                    "batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=collectorXmlInputFileType");
            put(PERM_TEMPLATE_NAME_KEY, "Use Screen");
            put(PERM_TEMPLATE_NAMESPACE_KEY, null);
            put(PERM_DETAIL_KEY_KEY, null);
            put(PERM_DETAIL_VAlUE_KEY, null);
        }};

        Map<String, Object> group = new LinkedHashMap<String, Object>() {{
            put(LABEL_KEY, "Accounting");
        }};

        PreferencesDeserializer.deserializeLinkCategory(row, group);

        Assert.assertTrue(group.size() == 2);
        Assert.assertTrue(group.containsKey(LINKS_KEY));
        Map<String, Object> resultingLinks = (Map<String, Object>) group.get(LINKS_KEY);
        Assert.assertTrue(resultingLinks.size() == 1);
        Assert.assertTrue(resultingLinks.containsKey("administration"));
    }

    @Test
    public void deserializeLinkCategory_givenLinkCategoriesExistsButThisCategoryIsNew_NewCategoryGetsAddedTolinkCategories() {
        // given a typical row of nav link data
        Map<String, Object> row = new LinkedHashMap<String, Object>() {{
            put(GROUP_LABEL_KEY, "Accounting");
            put(GROUP_POSITION_KEY, 0);
            put(LINK_TYPE_KEY, "kfs");
            put(LINK_CATEGORY_KEY, "activities");
            put(LINK_POSITION_KEY, 1);
            put(NEW_TARGET_KEY, "Y");
            put(BUSINESS_OBJECT_CLASS_KEY, "org.kuali.kfs.vnd.businessobject.VendorContract");
            put(DOCUMENT_TYPE_CODE_KEY, "BA");
            put(LINK_LABEL_KEY, "Collector Flat File Upload");
            put(LINK_VALUE_KEY,
                    "batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=collectorXmlInputFileType");
            put(PERM_TEMPLATE_NAME_KEY, "Use Screen");
            put(PERM_TEMPLATE_NAMESPACE_KEY, null);
            put(PERM_DETAIL_KEY_KEY, null);
            put(PERM_DETAIL_VAlUE_KEY, null);
        }};

        Map<String, Object> preexistingLinks = new LinkedHashMap<String, Object>() {{
            put("administration", Lists.newArrayList());
        }};

        Map<String, Object> group = new LinkedHashMap<String, Object>() {{
            put(LABEL_KEY, "Accounting");
            put(LINKS_KEY, preexistingLinks);
        }};

        PreferencesDeserializer.deserializeLinkCategory(row, group);

        Assert.assertTrue(group.size() == 2);
        Assert.assertTrue(group.containsKey(LINKS_KEY));
        Map<String, Object> resultingLinks = (Map<String, Object>) group.get(LINKS_KEY);
        Assert.assertTrue(resultingLinks.size() == 2);
        Assert.assertTrue(resultingLinks.containsKey("administration"));
        Assert.assertTrue(resultingLinks.containsKey("activities"));
    }

    @Test
    public void deserializeLinkCategory_givenThisCategoryAlreadyExists_noNewLinkCategoryIsAdded() {
        // given a typical row of nav link data
        Map<String, Object> row = new LinkedHashMap<String, Object>() {{
            put(GROUP_LABEL_KEY, "Accounting");
            put(GROUP_POSITION_KEY, 0);
            put(LINK_TYPE_KEY, "kfs");
            put(LINK_CATEGORY_KEY, "activities");
            put(LINK_POSITION_KEY, 1);
            put(NEW_TARGET_KEY, "Y");
            put(BUSINESS_OBJECT_CLASS_KEY, "org.kuali.kfs.vnd.businessobject.VendorContract");
            put(DOCUMENT_TYPE_CODE_KEY, "BA");
            put(LINK_LABEL_KEY, "Some Other Activities Link");
            put(LINK_VALUE_KEY,
                    "batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=somethingNew");
            put(PERM_TEMPLATE_NAME_KEY, null);
            put(PERM_TEMPLATE_NAMESPACE_KEY, null);
            put(PERM_DETAIL_KEY_KEY, null);
            put(PERM_DETAIL_VAlUE_KEY, null);
        }};

        // activities category already exists
        Map<String, Object> preexistingLinks = new LinkedHashMap<String, Object>() {{
            put("activities", new ArrayList<Map<String, Object>>() {{
                add(new LinkedHashMap<String, Object>() {{
                    put("documentTypeCode", "AD");
                    put("linkType", "kfs");
                }});
            }});
            put("administration", Lists.newArrayList());
        }};

        Map<String, Object> group = new LinkedHashMap<String, Object>() {{
            put(LABEL_KEY, "Accounting");
            put(LINKS_KEY, preexistingLinks);
        }};

        PreferencesDeserializer.deserializeLinkCategory(row, group);

        Assert.assertTrue(group.size() == 2);
        Assert.assertTrue(group.containsKey(LINKS_KEY));
        Map<String, Object> resultingLinks = (Map<String, Object>) group.get(LINKS_KEY);
        Assert.assertTrue(resultingLinks.size() == 2);
        Assert.assertTrue(resultingLinks.containsKey("administration"));
        Assert.assertTrue(resultingLinks.containsKey("activities"));
        List<Map<String, Object>> resultingActivitiesCategory = (List<Map<String, Object>>) resultingLinks
                .get("activities");
        // new data is added
        Assert.assertTrue(resultingActivitiesCategory.size() == 2);
        Assert.assertEquals("BA", resultingActivitiesCategory.get(1).get(DOCUMENT_TYPE_CODE_KEY));
    }

    @Test
    public void deserializeLinkCategory_newCategoriesGetsAddedAtTheCorrectPosition() {
        Map<String, Object> refRow = new LinkedHashMap<String, Object>() {{
            put(GROUP_LABEL_KEY, "Accounting");
            put(GROUP_POSITION_KEY, 0);
            put(LINK_TYPE_KEY, "kfs");
            put(LINK_CATEGORY_KEY, "reference");
            put(LINK_POSITION_KEY, 1);
        }};
        Map<String, Object> actRow = new LinkedHashMap<String, Object>() {{
            put(GROUP_LABEL_KEY, "Accounting");
            put(GROUP_POSITION_KEY, 0);
            put(LINK_TYPE_KEY, "kfs");
            put(LINK_CATEGORY_KEY, "activities");
            put(LINK_POSITION_KEY, 1);
        }};
        Map<String, Object> adminRow = new LinkedHashMap<String, Object>() {{
            put(GROUP_LABEL_KEY, "Accounting");
            put(GROUP_POSITION_KEY, 0);
            put(LINK_TYPE_KEY, "kfs");
            put(LINK_CATEGORY_KEY, "administration");
            put(LINK_POSITION_KEY, 1);
        }};

        // no categories so far -- we have to let the method create the tree map for the ordering to work
        Map<String, Object> group = new LinkedHashMap<String, Object>() {{
            put(LABEL_KEY, "Accounting");
        }};

        // should end up 2nd
        PreferencesDeserializer.deserializeLinkCategory(refRow, group);
        // should end up 1st
        PreferencesDeserializer.deserializeLinkCategory(actRow, group);
        // should end up last
        PreferencesDeserializer.deserializeLinkCategory(adminRow, group);

        Assert.assertTrue(group.size() == 2);
        Assert.assertTrue(group.containsKey(LINKS_KEY));
        TreeMap<String, Object> resultingLinks = (TreeMap<String, Object>) group.get(LINKS_KEY);
        Assert.assertTrue(resultingLinks.size() == 3);
        Object[] keysInOrder = resultingLinks.keySet().toArray();
        Assert.assertEquals("activities", keysInOrder[0]);
        Assert.assertEquals("reference", keysInOrder[1]);
        Assert.assertEquals("administration", keysInOrder[2]);
    }

    @Test
    public void deserializeLinkGroup_newLinkGroup_getsAddedAsExpected() {
        // given a typical row of nav link data
        Map<String, Object> row = new LinkedHashMap<String, Object>() {{
            put(GROUP_LABEL_KEY, "Accounting");
            put(GROUP_POSITION_KEY, 3);
            put(LINK_TYPE_KEY, "kfs");
            put(LINK_CATEGORY_KEY, "activities");
            put(LINK_POSITION_KEY, 1);
            put(NEW_TARGET_KEY, "Y");
            put(BUSINESS_OBJECT_CLASS_KEY, "org.kuali.kfs.vnd.businessobject.VendorContract");
            put(DOCUMENT_TYPE_CODE_KEY, "BA");
            put(LINK_LABEL_KEY, "Some Other Activities Link");
            put(LINK_VALUE_KEY,
                    "batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=somethingNew");
            put(PERM_TEMPLATE_NAME_KEY, "Use Screen");
            put(PERM_TEMPLATE_NAMESPACE_KEY, null);
            put(PERM_DETAIL_KEY_KEY, null);
            put(PERM_DETAIL_VAlUE_KEY, null);
        }};

        List<Map<String, Object>> existingGroups = new ArrayList<Map<String, Object>>() {{
            add(Maps.newLinkedHashMap());
            add(Maps.newLinkedHashMap());
        }};

        PreferencesDeserializer.deserializeLinkGroup(existingGroups, row);

        Assert.assertTrue(existingGroups.size() == 3);
        Assert.assertEquals("Accounting", existingGroups.get(2).get(LABEL_KEY));
    }

    @Test
    public void deserializeLinkGroup_existingLinkGroup_noNewGroupGetsAdded() {
        // given a typical row of nav link data
        Map<String, Object> row = new LinkedHashMap<String, Object>() {{
            put(GROUP_LABEL_KEY, "Accounts Receivable");
            put(GROUP_POSITION_KEY, 1);
            put(LINK_TYPE_KEY, "kfs");
            put(LINK_CATEGORY_KEY, "activities");
            put(LINK_POSITION_KEY, 1);
            put(NEW_TARGET_KEY, "Y");
            put(BUSINESS_OBJECT_CLASS_KEY, "org.kuali.kfs.vnd.businessobject.VendorContract");
            put(DOCUMENT_TYPE_CODE_KEY, "BA");
            put(LINK_LABEL_KEY, "Some Other Activities Link");
            put(LINK_VALUE_KEY,
                    "batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=somethingNew");
            put(PERM_TEMPLATE_NAME_KEY, "Use Screen");
            put(PERM_TEMPLATE_NAMESPACE_KEY, null);
            put(PERM_DETAIL_KEY_KEY, null);
            put(PERM_DETAIL_VAlUE_KEY, null);
        }};

        List<Map<String, Object>> existingGroups = new ArrayList<Map<String, Object>>() {{
            add(new LinkedHashMap<String, Object>() {{
                put(LABEL_KEY, "Accounting");
            }});
            add(new LinkedHashMap<String, Object>() {{
                put(LABEL_KEY, "Accounts Receivable");
            }});
        }};

        PreferencesDeserializer.deserializeLinkGroup(existingGroups, row);

        Assert.assertTrue(existingGroups.size() == 2);
        Assert.assertEquals("Accounting", existingGroups.get(0).get(LABEL_KEY));
        Assert.assertEquals("Accounts Receivable", existingGroups.get(1).get(LABEL_KEY));
    }

}