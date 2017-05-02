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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PreferencesSerializerTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final String USER_NAME = "khuntley";
    private final String OBJ_ID_KEY = "OBJ_ID";
    private final String VAL_KEY = "VAL";
    private final String KEY_CODE_KEY = "KEY_CD";
    private final String LINK_KEY = "link";

    private final String GROUP_LABEL_KEY = "groupLabel";
    private final String GROUP_POSITION_KEY = "groupPosition";
    private final String LINK_TYPE_KEY = "linkType";
    private final String LINKS_KEY = "links";
    private final String NEW_TARGET_KEY = "newTarget";
    private final String BUSINESS_OBJECT_CLASS_KEY = "businessObjectClass";
    private final String DOCUMENT_TYPE_CODE_KEY = "documentTypeCode";
    private final String LABEL_KEY = "label";
    private final String TEMPLATE_NAMESPACE_KEY = "templateNamespace";
    private final String TEMPLATE_NAME_KEY = "templateName";
    private final String DETAILS_KEY = "details";
    private final String PERMISSION_KEY = "permission";

    private final String GROUP_LABEL_COL_KEY = "LNK_GRP_LBL";
    private final String GROUP_POSITION_COL_KEY = "groupPosition";
    private final String LINK_TYPE_COL_KEY = "LNK_TYP";
    private final String LINK_CATEGORY_COL_KEY = "LNK_CTGRY";
    private final String NEW_TARGET_COL_KEY = "NEW_TGT";
    private final String BUSINESS_OBJECT_CLASS_COL_KEY = "BO_CLS";
    private final String DOCUMENT_TYPE_CODE_COL_KEY = "DOC_TYP_CD";
    private final String POSITION_COL_KEY = "POSN";
    private final String LABEL_COL_KEY = "LNK_LBL";
    private final String LINK_VALUE_COL_KEY = "LNK_VAL";
    private final String TEMPLATE_NAMESPACE_COL_KEY = "TMPL_NMSPC";
    private final String TEMPLATE_NAME_COL_KEY = "TMPL_NM";
    private final String NAV_LINK_GROUP_ID_COL_KEY = "NAV_LNK_GRP_ID";
    private final String NAV_LINK_ID_COL_KEY = "NAV_LNK_ID";
    private final String NAV_LINK_PERMISSION_ID_COL_KEY = "NAV_LNK_PERM_ID";

    @Test
    public void serializeUserPreferences_nullPrincipleName_throwsIAE() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("'principleName' cannot be null.");

        PreferencesSerializer.serializeUserPreferences(null, new HashMap<>());
    }

    @Test
    public void serializeUserPreferences_nullPrefs_throwsIAE() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("'preferences' cannot be null.");

        PreferencesSerializer.serializeUserPreferences("sarah", null);
    }

    @Test
    public void serializeUserPreferences_emptyPrefs() {
        List<Map<String, Object>> result = PreferencesSerializer.serializeUserPreferences(USER_NAME, new HashMap<>());
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void serializeUserPreferences_givenValidPrefs_serializesCorrectly() {
        final String principleNameKey = "PRNCPL_NM";
        final String keyCodeKey = "KEY_CD";
        final String valueKey = "VAL";

        Map<String, Object> prefs = ImmutableMap.of(
                "sidebarOut", Boolean.TRUE,
                "checkedLinkFilters", Arrays.asList("activities", "administration"),
                "defaultFinancialsChartOfAccountsCode", "");

        List<Map<String, Object>> result = PreferencesSerializer.serializeUserPreferences(USER_NAME, prefs);

        Assert.assertTrue(result.size() == 3);
        Map<String, Object> row1 = result.get(0);
        Assert.assertTrue(row1.size() == 4);
        Assert.assertTrue(row1.containsKey(OBJ_ID_KEY));
        Assert.assertEquals(USER_NAME, row1.get(principleNameKey));
        Assert.assertEquals("sidebarOut", row1.get(keyCodeKey));
        Assert.assertEquals("true", row1.get(valueKey));

        Map<String, Object> row2 = result.get(1);
        Assert.assertTrue(row2.size() == 4);
        Assert.assertTrue(row2.containsKey(OBJ_ID_KEY));
        Assert.assertEquals(USER_NAME, row2.get(principleNameKey));
        Assert.assertEquals("checkedLinkFilters", row2.get(keyCodeKey));
        Assert.assertEquals("[\"activities\",\"administration\"]", row2.get(valueKey));

        Map<String, Object> row3 = result.get(2);
        Assert.assertTrue(row3.size() == 4);
        Assert.assertTrue(row3.containsKey(OBJ_ID_KEY));
        Assert.assertEquals(USER_NAME, row3.get(principleNameKey));
        Assert.assertEquals("defaultFinancialsChartOfAccountsCode", row3.get(keyCodeKey));
        Assert.assertEquals("\"\"", row3.get(valueKey));
    }

    @Test
    public void serializeMenuLinks_nullMenuLinks_throwsIAE() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("'menuLinks' cannot be null.");

        PreferencesSerializer.serializeMenuLinks(null);
    }

    @Test
    public void serializeMenuLinks_emptyMenuLinks() {
        List<Map<String, Object>> result = PreferencesSerializer.serializeMenuLinks(new ArrayList<>());
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void serializeMenuLinks_givenValidMap_preservesPosition() {
        final String menuLabelKey = "label";
        final String menuLinkKey = "link";
        final String menuLabelColumnKey = "LNK_LBL";
        final String menuLinkColumnKey = "LNK_VAL";
        final String menuPositionColumnKey = "POSN";

        // given a typical list of menu links
        Map<String, Object> link1 = ImmutableMap.of(
                menuLabelKey, "Help",
                menuLinkKey, "static/help/default.htm");
        Map<String, Object> link2 = ImmutableMap.of(
                menuLabelKey, "Feedback",
                menuLinkKey, "static/feedback/default.htm");
        Map<String, Object> link3 = ImmutableMap.of(
                menuLabelKey, "Taco Tuesday",
                menuLinkKey, "static/tt/default.htm");
        List<Map<String, Object>> links = Lists.newArrayList(link1, link2, link3);

        List<Map<String, Object>> result = PreferencesSerializer.serializeMenuLinks(links);

        Assert.assertTrue(result.size() == 3);
        Map<String, Object> row1 = result.get(0);
        Assert.assertTrue(row1.size() == 4);
        Assert.assertTrue(row1.containsKey(OBJ_ID_KEY));
        Assert.assertEquals("Help", row1.get(menuLabelColumnKey));
        Assert.assertEquals("static/help/default.htm", row1.get(menuLinkColumnKey));
        Assert.assertEquals("0", row1.get(menuPositionColumnKey));

        Map<String, Object> row2 = result.get(1);
        Assert.assertTrue(row2.size() == 4);
        Assert.assertTrue(row2.containsKey(OBJ_ID_KEY));
        Assert.assertEquals("Feedback", row2.get(menuLabelColumnKey));
        Assert.assertEquals("static/feedback/default.htm", row2.get(menuLinkColumnKey));
        Assert.assertEquals("1", row2.get(menuPositionColumnKey));

        Map<String, Object> row3 = result.get(2);
        Assert.assertTrue(row3.size() == 4);
        Assert.assertTrue(row3.containsKey(OBJ_ID_KEY));
        Assert.assertEquals("Taco Tuesday", row3.get(menuLabelColumnKey));
        Assert.assertEquals("static/tt/default.htm", row3.get(menuLinkColumnKey));
        Assert.assertEquals("2", row3.get(menuPositionColumnKey));
    }

    @Test
    public void serializePermissionDetails_givenMultipleValidDetailsAndPreexistingRow_permissionRowsAreAddedToRowsInExpectedFormWithoutAffectingExistingRow() {
        // valid details
        Map<String, Object> permDetails = new LinkedHashMap<String, Object>() {{
            put("namespaceCode", "KFS-VND");
            put("component", "VendorTaxDetail");
        }};

        Map<String, Object> perm = new LinkedHashMap<String, Object>() {{
            put(TEMPLATE_NAMESPACE_KEY, "KR-NS");
            put(TEMPLATE_NAME_KEY, "Use Screen");
            put(DETAILS_KEY, permDetails);
        }};

        Map<String, Object> preexistingRow = new LinkedHashMap<String, Object>() {{
            put(OBJ_ID_KEY, "7890");
            put(NAV_LINK_PERMISSION_ID_COL_KEY, "2345");
            put(KEY_CODE_KEY, "actionClass");
            put(VAL_KEY, "org.kuali.kfs.sys.web.struts.ElectronicFundTransferAction");
        }};

        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>() {{
            add(preexistingRow);
        }};

        PreferencesSerializer.serializePermissionDetails(perm, "1234", rows);

        Assert.assertTrue(rows.size() == 3);
        Map<String, Object> row1 = rows.get(0);
        Assert.assertTrue(row1.size() == 4);
        Assert.assertEquals("7890", row1.get(OBJ_ID_KEY));
        Assert.assertEquals("2345", row1.get(NAV_LINK_PERMISSION_ID_COL_KEY));
        Assert.assertEquals("actionClass", row1.get(KEY_CODE_KEY));
        Assert.assertEquals("org.kuali.kfs.sys.web.struts.ElectronicFundTransferAction", row1.get(VAL_KEY));

        Map<String, Object> row2 = rows.get(1);
        Assert.assertTrue(row2.size() == 4);
        Assert.assertTrue(row2.containsKey(OBJ_ID_KEY));
        Assert.assertEquals("1234", row2.get(NAV_LINK_PERMISSION_ID_COL_KEY));
        Assert.assertEquals("namespaceCode", row2.get(KEY_CODE_KEY));
        Assert.assertEquals("KFS-VND", row2.get(VAL_KEY));

        Map<String, Object> row3 = rows.get(2);
        Assert.assertTrue(row3.size() == 4);
        Assert.assertTrue(row3.containsKey(OBJ_ID_KEY));
        Assert.assertEquals("1234", row3.get(NAV_LINK_PERMISSION_ID_COL_KEY));
        Assert.assertEquals("component", row3.get(KEY_CODE_KEY));
        Assert.assertEquals("VendorTaxDetail", row3.get(VAL_KEY));
    }

    @Test
    public void serializePermissionDetails_givenPreexistingRowAndNullDetails_noRowsAreAddedAndExistingRowIsUnmodified() {
        // permission includes no details
        Map<String, Object> perm = new LinkedHashMap<String, Object>() {{
            put(TEMPLATE_NAMESPACE_KEY, "KR-NS");
            put(TEMPLATE_NAME_KEY, "Use Screen");
        }};

        Map<String, Object> preexistingRow = new LinkedHashMap<String, Object>() {{
            put(OBJ_ID_KEY, "6890");
            put(NAV_LINK_PERMISSION_ID_COL_KEY, "2345");
            put(KEY_CODE_KEY, "actionClass");
            put(VAL_KEY, "org.kuali.kfs.sys.web.struts.ElectronicFundTransferAction");
        }};

        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>() {{
            add(preexistingRow);
        }};

        PreferencesSerializer.serializePermissionDetails(perm, "1234", rows);

        Assert.assertTrue(rows.size() == 1);
        Map<String, Object> row1 = rows.get(0);
        Assert.assertTrue(row1.size() == 4);
        Assert.assertEquals("6890", row1.get(OBJ_ID_KEY));
        Assert.assertEquals("2345", row1.get(NAV_LINK_PERMISSION_ID_COL_KEY));
        Assert.assertEquals("actionClass", row1.get(KEY_CODE_KEY));
        Assert.assertEquals("org.kuali.kfs.sys.web.struts.ElectronicFundTransferAction", row1.get(VAL_KEY));
    }

    @Test
    public void serializePermissions_givenPreexistingLinkRowAndPermissionData_rowsIsAddedAndExistingRowIsUnmodified() {
        Map<String, Object> permission = new LinkedHashMap<String, Object>() {{
            put(TEMPLATE_NAMESPACE_KEY, "KR-NS");
            put(TEMPLATE_NAME_KEY, "Look Up Records");
        }};

        Map<String, Object> navLink = new LinkedHashMap<String, Object>() {{
            put(LINK_KEY, "arCustomerStatement.do?methodToCall=start");
            put(LINK_TYPE_KEY, "kfs");
            put(LABEL_KEY, "Billing Statement");
            put(PERMISSION_KEY, permission);
        }};

        Map<String, Object> preexistingLinkRow = new LinkedHashMap<String, Object>() {{
            put(OBJ_ID_KEY, "43211");
            put(TEMPLATE_NAMESPACE_COL_KEY, "KR-NS");
            put(TEMPLATE_NAME_COL_KEY, "Use Screen");
            put(NAV_LINK_ID_COL_KEY, "88888");
        }};

        List<Map<String, Object>> linkRows = new ArrayList<Map<String, Object>>() {{
            add(preexistingLinkRow);
        }};

        PreferencesSerializer.serializePermissions(navLink, "9876", linkRows, Lists.newArrayList());

        Assert.assertTrue(linkRows.size() == 2);
        Map<String, Object> row1 = linkRows.get(0);
        Assert.assertTrue(row1.size() == 4);
        Assert.assertEquals("43211", row1.get(OBJ_ID_KEY));
        Assert.assertEquals("KR-NS", row1.get(TEMPLATE_NAMESPACE_COL_KEY));
        Assert.assertEquals("Use Screen", row1.get(TEMPLATE_NAME_COL_KEY));
        Assert.assertEquals("88888", row1.get(NAV_LINK_ID_COL_KEY));

        Map<String, Object> row2 = linkRows.get(1);
        Assert.assertTrue(row2.size() == 4);
        Assert.assertTrue(row2.containsKey(OBJ_ID_KEY));
        Assert.assertEquals("KR-NS", row2.get(TEMPLATE_NAMESPACE_COL_KEY));
        Assert.assertEquals("Look Up Records", row2.get(TEMPLATE_NAME_COL_KEY));
        // Assert.assertTrue(row2.containsKey(NAV_LINK_ID_COL_KEY));
    }

    @Test
    public void serializePermissions_givenPreexistingLinkRowAndNullPermission_noRowsAreAddedAndExistingRowIsUnmodified() {
        Map<String, Object> navLink = new LinkedHashMap<String, Object>() {{
            put(LINK_KEY, "arCustomerStatement.do?methodToCall=start");
            put(LINK_TYPE_KEY, "kfs");
            put(LABEL_KEY, "Billing Statement");
        }};

        Map<String, Object> preexistingLinkRow = new LinkedHashMap<String, Object>() {{
            put(OBJ_ID_KEY, "4321");
            put(TEMPLATE_NAMESPACE_COL_KEY, "KR-NS");
            put(TEMPLATE_NAME_COL_KEY, "Use Screen");
            put(NAV_LINK_ID_COL_KEY, "88888");
        }};

        List<Map<String, Object>> linkRows = new ArrayList<Map<String, Object>>() {{
            add(preexistingLinkRow);
        }};

        PreferencesSerializer.serializePermissions(navLink, "9876", linkRows, Lists.newArrayList());

        Assert.assertTrue(linkRows.size() == 1);
        Map<String, Object> row1 = linkRows.get(0);
        Assert.assertTrue(row1.size() == 4);
        Assert.assertEquals("4321", row1.get(OBJ_ID_KEY));
        Assert.assertEquals("KR-NS", row1.get(TEMPLATE_NAMESPACE_COL_KEY));
        Assert.assertEquals("Use Screen", row1.get(TEMPLATE_NAME_COL_KEY));
        Assert.assertEquals("88888", row1.get(NAV_LINK_ID_COL_KEY));
    }

    @Test
    public void serializeNavLink_givenPreexistingLinkRow_linkRowIsAddedAndExistingRowIsUnmodified() {
        Map<String, Object> navLink = new LinkedHashMap<String, Object>() {{
            put(LINK_KEY, "arCustomerStatement.do?methodToCall=start");
            put(LINK_TYPE_KEY, "kfs");
            put(LABEL_KEY, "Billing Statement");
        }};

        Map<String, Object> preexistingNavLinkRow = new LinkedHashMap<String, Object>() {{
            put(OBJ_ID_KEY, "4321");
            put(LABEL_COL_KEY, "Electronic Payment Claim");
            put(NAV_LINK_GROUP_ID_COL_KEY, "2222");
            put(LINK_CATEGORY_COL_KEY, "activities");
            put(LINK_TYPE_COL_KEY, "kfs");
            put(LINK_VALUE_COL_KEY, "electronicFundTransfer.do?methodToCall=start");
            put(DOCUMENT_TYPE_CODE_COL_KEY, "CATP");
            put(BUSINESS_OBJECT_CLASS_COL_KEY, "org.kuali.kfs.module.ec.businessobject.DuplicateCertificationsReport");
            put(NEW_TARGET_COL_KEY, "Y");
            put(POSITION_COL_KEY, 0);
        }};

        List<Map<String, Object>> navLinkRows = new ArrayList<Map<String, Object>>() {{
            add(preexistingNavLinkRow);
        }};

        PreferencesSerializer.serializeNavLink("reference", "1111", navLinkRows, 1,
                navLink, Lists.newArrayList(), Lists.newArrayList());

        Assert.assertTrue(navLinkRows.size() == 2);
        Map<String, Object> row1 = navLinkRows.get(0);
        Assert.assertTrue(row1.size() == 10);
        Assert.assertEquals("4321", row1.get(OBJ_ID_KEY));
        Assert.assertEquals("Electronic Payment Claim", row1.get(LABEL_COL_KEY));
        Assert.assertEquals("2222", row1.get(NAV_LINK_GROUP_ID_COL_KEY));
        Assert.assertEquals("activities", row1.get(LINK_CATEGORY_COL_KEY));
        Assert.assertEquals("kfs", row1.get(LINK_TYPE_COL_KEY));
        Assert.assertEquals("electronicFundTransfer.do?methodToCall=start", row1.get(LINK_VALUE_COL_KEY));
        Assert.assertEquals("CATP", row1.get(DOCUMENT_TYPE_CODE_COL_KEY));
        Assert.assertEquals("org.kuali.kfs.module.ec.businessobject.DuplicateCertificationsReport",
                row1.get(BUSINESS_OBJECT_CLASS_COL_KEY));
        Assert.assertEquals("Y", row1.get(NEW_TARGET_COL_KEY));
        Assert.assertEquals(0, row1.get(POSITION_COL_KEY));

        Map<String, Object> row2 = navLinkRows.get(1);
        Assert.assertTrue(row2.size() == 7);
        Assert.assertTrue(row2.containsKey(OBJ_ID_KEY));
        Assert.assertEquals("Billing Statement", row2.get(LABEL_COL_KEY));
        Assert.assertEquals("1111", row2.get(NAV_LINK_GROUP_ID_COL_KEY));
        Assert.assertEquals("reference", row2.get(LINK_CATEGORY_COL_KEY));
        Assert.assertEquals("kfs", row2.get(LINK_TYPE_COL_KEY));
        Assert.assertEquals("arCustomerStatement.do?methodToCall=start", row2.get(LINK_VALUE_COL_KEY));
        Assert.assertEquals(1, row2.get(POSITION_COL_KEY));
    }

    @Test
    public void serializeNavLink_givenLinkRowWithDocType_linkRowIsAddedContainingDocType() {
        Map<String, Object> navLink = new LinkedHashMap<String, Object>() {{
            put(LINK_TYPE_KEY, "kfs");
            put(DOCUMENT_TYPE_CODE_KEY, "CATP");
        }};

        List<Map<String, Object>> navLinkRows = Lists.newArrayList();

        PreferencesSerializer.serializeNavLink("reference", "1111", navLinkRows, 1,
                navLink, Lists.newArrayList(), Lists.newArrayList());

        Assert.assertTrue(navLinkRows.size() == 1);
        Map<String, Object> row1 = navLinkRows.get(0);
        Assert.assertTrue(row1.size() == 6);
        Assert.assertTrue(row1.containsKey(OBJ_ID_KEY));
        Assert.assertEquals("1111", row1.get(NAV_LINK_GROUP_ID_COL_KEY));
        Assert.assertEquals("reference", row1.get(LINK_CATEGORY_COL_KEY));
        Assert.assertEquals("kfs", row1.get(LINK_TYPE_COL_KEY));
        Assert.assertEquals("CATP", row1.get(DOCUMENT_TYPE_CODE_COL_KEY));
        Assert.assertEquals(1, row1.get(POSITION_COL_KEY));
    }

    @Test
    public void serializeNavLink_givenLinkRowWithBusinessObjClass_linkRowIsAddedContainingBusinessObjClass() {
        Map<String, Object> navLink = new LinkedHashMap<String, Object>() {{
            put(LINK_TYPE_KEY, "kfs");
            put(BUSINESS_OBJECT_CLASS_KEY, "org.kuali.kfs.module.ec.businessobject.DuplicateCertificationsReport");
        }};

        List<Map<String, Object>> navLinkRows = Lists.newArrayList();

        PreferencesSerializer.serializeNavLink("reference", "1111", navLinkRows, 1,
                navLink, Lists.newArrayList(), Lists.newArrayList());

        Assert.assertTrue(navLinkRows.size() == 1);
        Map<String, Object> row1 = navLinkRows.get(0);
        Assert.assertTrue(row1.size() == 6);
        Assert.assertTrue(row1.containsKey(OBJ_ID_KEY));
        Assert.assertEquals("1111", row1.get(NAV_LINK_GROUP_ID_COL_KEY));
        Assert.assertEquals("reference", row1.get(LINK_CATEGORY_COL_KEY));
        Assert.assertEquals("kfs", row1.get(LINK_TYPE_COL_KEY));
        Assert.assertEquals("org.kuali.kfs.module.ec.businessobject.DuplicateCertificationsReport",
                row1.get(BUSINESS_OBJECT_CLASS_COL_KEY));
        Assert.assertEquals(1, row1.get(POSITION_COL_KEY));
    }

    @Test
    public void serializeNavLink_givenLinkRowWithNewTargetFalse_linkRowIsAddedContainingNewTargetFalse() {
        Map<String, Object> navLink = new LinkedHashMap<String, Object>() {{
            put(LINK_TYPE_KEY, "kfs");
            put(NEW_TARGET_KEY, false);
        }};

        List<Map<String, Object>> navLinkRows = Lists.newArrayList();

        PreferencesSerializer.serializeNavLink("reference", "1111", navLinkRows, 1,
                navLink, Lists.newArrayList(), Lists.newArrayList());

        Assert.assertTrue(navLinkRows.size() == 1);
        Map<String, Object> row1 = navLinkRows.get(0);
        Assert.assertTrue(row1.size() == 6);
        Assert.assertTrue(row1.containsKey(OBJ_ID_KEY));
        Assert.assertEquals("1111", row1.get(NAV_LINK_GROUP_ID_COL_KEY));
        Assert.assertEquals("reference", row1.get(LINK_CATEGORY_COL_KEY));
        Assert.assertEquals("kfs", row1.get(LINK_TYPE_COL_KEY));
        Assert.assertEquals("N", row1.get(NEW_TARGET_COL_KEY));
        Assert.assertEquals(1, row1.get(POSITION_COL_KEY));
    }

    @Test
    public void serializeNavLink_givenLinkRowWithNewTargetTrue_linkRowIsAddedContainingNewTargetTrue() {
        Map<String, Object> navLink = new LinkedHashMap<String, Object>() {{
            put(LINK_TYPE_KEY, "kfs");
            put(NEW_TARGET_KEY, true);
        }};

        List<Map<String, Object>> navLinkRows = Lists.newArrayList();

        PreferencesSerializer.serializeNavLink("reference", "1111", navLinkRows, 1,
                navLink, Lists.newArrayList(), Lists.newArrayList());

        Assert.assertTrue(navLinkRows.size() == 1);
        Map<String, Object> row1 = navLinkRows.get(0);
        Assert.assertTrue(row1.size() == 6);
        Assert.assertTrue(row1.containsKey(OBJ_ID_KEY));
        Assert.assertEquals("1111", row1.get(NAV_LINK_GROUP_ID_COL_KEY));
        Assert.assertEquals("reference", row1.get(LINK_CATEGORY_COL_KEY));
        Assert.assertEquals("kfs", row1.get(LINK_TYPE_COL_KEY));
        Assert.assertEquals("Y", row1.get(NEW_TARGET_COL_KEY));
        Assert.assertEquals(1, row1.get(POSITION_COL_KEY));
    }

    @Test
    public void serializeNavLink_givenLinkRowWithLabel_linkRowIsAddedContainingLabel() {
        Map<String, Object> navLink = new LinkedHashMap<String, Object>() {{
            put(LINK_TYPE_KEY, "kfs");
            put(LABEL_KEY, "Billing Statement");
        }};

        List<Map<String, Object>> navLinkRows = Lists.newArrayList();

        PreferencesSerializer.serializeNavLink("reference", "1111", navLinkRows, 1,
                navLink, Lists.newArrayList(), Lists.newArrayList());

        Assert.assertTrue(navLinkRows.size() == 1);
        Map<String, Object> row1 = navLinkRows.get(0);
        Assert.assertTrue(row1.size() == 6);
        Assert.assertTrue(row1.containsKey(OBJ_ID_KEY));
        Assert.assertEquals("1111", row1.get(NAV_LINK_GROUP_ID_COL_KEY));
        Assert.assertEquals("reference", row1.get(LINK_CATEGORY_COL_KEY));
        Assert.assertEquals("kfs", row1.get(LINK_TYPE_COL_KEY));
        Assert.assertEquals("Billing Statement", row1.get(LABEL_COL_KEY));
        Assert.assertEquals(1, row1.get(POSITION_COL_KEY));
    }

    @Test
    public void serializeNavLink_givenLinkRowWithLinkValue_linkRowIsAddedContainingLinkValue() {
        Map<String, Object> navLink = new LinkedHashMap<String, Object>() {{
            put(LINK_TYPE_KEY, "kfs");
            put(LINK_KEY, "arCustomerStatement.do?methodToCall=start");
        }};

        List<Map<String, Object>> navLinkRows = Lists.newArrayList();

        PreferencesSerializer.serializeNavLink("reference", "1111", navLinkRows, 1,
                navLink, Lists.newArrayList(), Lists.newArrayList());

        Assert.assertTrue(navLinkRows.size() == 1);
        Map<String, Object> row1 = navLinkRows.get(0);
        Assert.assertTrue(row1.size() == 6);
        Assert.assertTrue(row1.containsKey(OBJ_ID_KEY));
        Assert.assertEquals("1111", row1.get(NAV_LINK_GROUP_ID_COL_KEY));
        Assert.assertEquals("reference", row1.get(LINK_CATEGORY_COL_KEY));
        Assert.assertEquals("kfs", row1.get(LINK_TYPE_COL_KEY));
        Assert.assertEquals("arCustomerStatement.do?methodToCall=start", row1.get(LINK_VALUE_COL_KEY));
        Assert.assertEquals(1, row1.get(POSITION_COL_KEY));
    }

    @Test
    public void serializeLinkCategories_GivenUnexpectedCategory_throwsRuntimeException() {
        Map<String, Object> groupLinksWithUnexpectedCategory = new LinkedHashMap<String, Object>() {{
            put("turkeys", Maps.newLinkedHashMap());
        }};

        Map<String, Object> navLinkGroup = new LinkedHashMap<String, Object>() {{
            put(LINKS_KEY, groupLinksWithUnexpectedCategory);
            put(LABEL_KEY, "Accounts Receivable");
        }};

        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Invalid link group category: 'turkeys'");

        PreferencesSerializer.serializeLinkCategories("33333", navLinkGroup, Lists.newArrayList(), Lists.newArrayList(),
                Lists.newArrayList());
    }

    @Test
    public void serializeLinkCategories_GivenMultipleCategories_addsCateogryDataAndGeneratesExpectedRows() {
        Map<String, Object> navLink1 = new LinkedHashMap<String, Object>() {{
            put(LINK_KEY,
                    "batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=customerLoadCSVInputFileType");
            put(LINK_TYPE_KEY, "kfs");
            put(LABEL_KEY, "Customer CSV Upload");
        }};

        Map<String, Object> navLink2 = new LinkedHashMap<String, Object>() {{
            put(LINK_KEY, "arCustomerStatement.do?methodToCall=start");
            put(LINK_TYPE_KEY, "kfs");
            put(LABEL_KEY, "Billing Statement");
        }};

        Map<String, Object> groupLinksWithExpectedCategory = new LinkedHashMap<String, Object>() {{
            put("activities", ImmutableList.of(navLink1));
            put("reference", ImmutableList.of(navLink2));
        }};

        Map<String, Object> navLinkGroup = new LinkedHashMap<String, Object>() {{
            put(LINKS_KEY, groupLinksWithExpectedCategory);
            put(LABEL_KEY, "Accounts Receivable");
        }};

        List<Map<String, Object>> navLinkRows = Lists.newArrayList();

        PreferencesSerializer.serializeLinkCategories("33333", navLinkGroup, navLinkRows, Lists.newArrayList(),
                Lists.newArrayList());

        Assert.assertTrue(navLinkRows.size() == 2);
        Map<String, Object> row1 = navLinkRows.get(0);
        Assert.assertTrue(row1.size() == 7);
        Assert.assertTrue(row1.containsKey(OBJ_ID_KEY));
        Assert.assertEquals("Customer CSV Upload", row1.get(LABEL_COL_KEY));
        Assert.assertEquals("33333", row1.get(NAV_LINK_GROUP_ID_COL_KEY));
        Assert.assertEquals("activities", row1.get(LINK_CATEGORY_COL_KEY));
        Assert.assertEquals("kfs", row1.get(LINK_TYPE_COL_KEY));
        Assert.assertEquals(
                "batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=customerLoadCSVInputFileType",
                row1.get(LINK_VALUE_COL_KEY));
        Assert.assertEquals(0, row1.get(POSITION_COL_KEY));

        Map<String, Object> row2 = navLinkRows.get(1);
        Assert.assertTrue(row2.size() == 7);
        Assert.assertTrue(row2.containsKey(OBJ_ID_KEY));
        Assert.assertEquals("33333", row2.get(NAV_LINK_GROUP_ID_COL_KEY));
        Assert.assertEquals("reference", row2.get(LINK_CATEGORY_COL_KEY));
        Assert.assertEquals("kfs", row2.get(LINK_TYPE_COL_KEY));
        Assert.assertEquals("arCustomerStatement.do?methodToCall=start", row2.get(LINK_VALUE_COL_KEY));
        Assert.assertEquals("Billing Statement", row2.get(LABEL_COL_KEY));
        Assert.assertEquals(0, row2.get(POSITION_COL_KEY));
    }

    @Test
    public void serializeLinkGroups_givenMultipleLinkGroups_AddsExpectedRows() {
        Map<String, Object> navLink1 = new LinkedHashMap<String, Object>() {{
            put(LINK_KEY,
                    "batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=customerLoadCSVInputFileType");
            put(LINK_TYPE_KEY, "kfs");
            put(LABEL_KEY, "Customer CSV Upload");
        }};

        Map<String, Object> navLink2 = new LinkedHashMap<String, Object>() {{
            put(LINK_KEY, "arCustomerStatement.do?methodToCall=start");
            put(LINK_TYPE_KEY, "kfs");
            put(LABEL_KEY, "Billing Statement");
        }};

        Map<String, Object> groupLinks1 = new LinkedHashMap<String, Object>() {{
            put("activities", ImmutableList.of(navLink1));
            put("reference", ImmutableList.of(navLink2));
        }};

        Map<String, Object> navLinkGroup1 = new LinkedHashMap<String, Object>() {{
            put(LINKS_KEY, groupLinks1);
            put(LABEL_KEY, "Accounts Receivable");
        }};

        Map<String, Object> navLink3 = new LinkedHashMap<String, Object>() {{
            put(LINK_TYPE_KEY, "kfs");
            put(DOCUMENT_TYPE_CODE_KEY, "PURP");
        }};

        Map<String, Object> navLink4 = new LinkedHashMap<String, Object>() {{
            put(LINK_TYPE_KEY, "kfs");
            put(DOCUMENT_TYPE_CODE_KEY, "AACD");
        }};

        Map<String, Object> groupLinks2 = new LinkedHashMap<String, Object>() {{
            put("administration", ImmutableList.of(navLink3));
            put("reference", ImmutableList.of(navLink4));
        }};

        Map<String, Object> navLinkGroup2 = new LinkedHashMap<String, Object>() {{
            put(LINKS_KEY, groupLinks2);
            put(LABEL_KEY, "Contracts & Grants");
        }};

        List<Map<String, Object>> navLinkGroups = ImmutableList.of(navLinkGroup1, navLinkGroup2);
        List<Map<String, Object>> navLinkGroupRows = Lists.newArrayList();

        PreferencesSerializer
                .serializeLinkGroups(navLinkGroups, navLinkGroupRows, Lists.newArrayList(), Lists.newArrayList(),
                        Lists.newArrayList());

        Assert.assertTrue(navLinkGroupRows.size() == 2);
        Map<String, Object> row1 = navLinkGroupRows.get(0);
        Assert.assertTrue(row1.size() == 3);
        Assert.assertTrue(row1.containsKey(OBJ_ID_KEY));
        Assert.assertEquals(0, row1.get(POSITION_COL_KEY));
        Assert.assertEquals("Accounts Receivable", row1.get(GROUP_LABEL_COL_KEY));

        Map<String, Object> row2 = navLinkGroupRows.get(1);
        Assert.assertTrue(row2.size() == 3);
        Assert.assertTrue(row2.containsKey(OBJ_ID_KEY));
        Assert.assertEquals(1, row2.get(POSITION_COL_KEY));
        Assert.assertEquals("Contracts & Grants", row2.get(GROUP_LABEL_COL_KEY));
    }

    @Test
    public void serializeLinkGroups_givenEmptyLinkGroups_NoRowsAreAdded() {
        List<Map<String, Object>> navLinkGroups = Lists.newArrayList();
        List<Map<String, Object>> navLinkGroupRows = Lists.newArrayList();

        PreferencesSerializer
                .serializeLinkGroups(navLinkGroups, navLinkGroupRows, Lists.newArrayList(), Lists.newArrayList(),
                        Lists.newArrayList());

        Assert.assertTrue(navLinkGroupRows.size() == 0);
    }

    @Test
    public void serializeNavLinkGroups_givenNullNavLinkGroups_ThrowsIAE() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("'navLinkGroups' cannot be null.");

        PreferencesSerializer.serializeNavLinkGroups(null);
    }

    @Test
    public void serializeNavLinkGroups_givenEmptyNavLinkGroups_ReturnsMapOfEmptyLists() {
        Map<String,List<Map<String,Object>>> result = PreferencesSerializer.serializeNavLinkGroups(Lists.newArrayList());

        Assert.assertTrue(result.size() == 4);
        List<Map<String, Object>> list1 = result.get(PreferencesDaoJdbc.TABLE_NAV_LINK_GROUPS);
        Assert.assertTrue(list1.isEmpty());
        List<Map<String, Object>> list2 = result.get(PreferencesDaoJdbc.TABLE_NAV_LINKS);
        Assert.assertTrue(list2.isEmpty());
        List<Map<String, Object>> list3 = result.get(PreferencesDaoJdbc.TABLE_NAV_LINK_PERMISSIONS);
        Assert.assertTrue(list3.isEmpty());
        List<Map<String, Object>> list4 = result.get(PreferencesDaoJdbc.TABLE_NAV_LINK_PERMISSION_DETAILS);
        Assert.assertTrue(list4.isEmpty());
    }

}