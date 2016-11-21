/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.kfs.sys.rest.resource;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.kfs.krad.UserSession;
import org.kuali.kfs.krad.datadictionary.DataDictionary;
import org.kuali.kfs.krad.datadictionary.MaintenanceDocumentEntry;
import org.kuali.kfs.krad.service.DataDictionaryService;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.util.KRADUtils;
import org.kuali.kfs.sys.businessobject.UnitOfMeasure;
import org.kuali.kfs.sys.identity.TestPerson;
import org.kuali.kfs.sys.rest.BusinessObjectApiResourceTestHelper;
import org.kuali.kfs.sys.rest.MockDataDictionaryService;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.permission.PermissionService;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;

@RunWith(PowerMockRunner.class)
public class BusinessObjectApiResourceTest {

    private UserSession userSession;
    private Person testPerson = new TestPerson("testPrincipalId", "testPrincipalName");
    private BusinessObjectApiResource apiResource;
    private DataDictionaryService dataDictionaryService;
    private DataDictionary dataDictionary;
    private KualiModuleService kualiModuleService;
    private PermissionService permissionService;

    @Before
    public void setup() {
        apiResource = new BusinessObjectApiResource("sys");
        userSession = EasyMock.createMock(UserSession.class);
        kualiModuleService = EasyMock.createMock(KualiModuleService.class);
        dataDictionaryService = EasyMock.partialMockBuilder(MockDataDictionaryService.class).addMockedMethods("containsDictionaryObject",
            "getDictionaryObject", "getDataDictionary").createMock();
        dataDictionary = EasyMock.createMock(DataDictionary.class);
        permissionService = EasyMock.createMock(PermissionService.class);
        PowerMock.mockStatic(KRADUtils.class);

    }

    @Test
    @PrepareForTest({KRADUtils.class})
    public void testIsAuthorized_NoPerm() throws Exception {
        Class clazz = UnitOfMeasure.class;
        String documentTypeName = "PMUM";
        String namespaceCode = "KFS-SYS";

        EasyMock.expect(userSession.getPerson()).andReturn(testPerson).times(2);
        EasyMock.expect(KRADUtils.getUserSessionFromRequest(null)).andReturn(userSession).anyTimes();
        EasyMock.expect(KRADUtils.getNamespaceAndComponentSimpleName(clazz)).andReturn(BusinessObjectApiResourceTestHelper.makeMap(namespaceCode, documentTypeName)).times(2);
        EasyMock.expect(permissionService.isAuthorizedByTemplate("testPrincipalId", "KR-NS", KimConstants.PermissionTemplateNames.INQUIRE_INTO_RECORDS,
            BusinessObjectApiResourceTestHelper.makeMap(namespaceCode, documentTypeName), Collections.emptyMap())).andReturn(false);
        EasyMock.expect(permissionService.isAuthorizedByTemplate("testPrincipalId", "KR-NS", KimConstants.PermissionTemplateNames.LOOK_UP_RECORDS,
            BusinessObjectApiResourceTestHelper.makeMap(namespaceCode, documentTypeName), Collections.emptyMap())).andReturn(false);

        EasyMock.replay(permissionService, userSession);
        PowerMock.replay(KRADUtils.class);
        BusinessObjectApiResource.setKualiModuleService(kualiModuleService);
        BusinessObjectApiResource.setPermissionService(permissionService);
        BusinessObjectApiResource.setDataDictionaryService(dataDictionaryService);

        Assert.assertFalse(apiResource.isAuthorized(clazz));
        EasyMock.verify(permissionService, userSession);
    }

    @Test
    @PrepareForTest({KRADUtils.class})
    public void testIsAuthorized_NoInquiryIntoRecordsPerm() throws Exception {
        Class clazz = UnitOfMeasure.class;
        String documentTypeName = "PMUM";
        String namespaceCode = "KFS-SYS";

        EasyMock.expect(userSession.getPerson()).andReturn(testPerson).times(2);
        EasyMock.expect(KRADUtils.getUserSessionFromRequest(null)).andReturn(userSession).anyTimes();
        EasyMock.expect(KRADUtils.getNamespaceAndComponentSimpleName(clazz)).andReturn(BusinessObjectApiResourceTestHelper.makeMap(namespaceCode, documentTypeName)).times(2);
        EasyMock.expect(permissionService.isAuthorizedByTemplate("testPrincipalId", "KR-NS", KimConstants.PermissionTemplateNames.INQUIRE_INTO_RECORDS,
            BusinessObjectApiResourceTestHelper.makeMap(namespaceCode, documentTypeName), Collections.emptyMap())).andReturn(false);
        EasyMock.expect(permissionService.isAuthorizedByTemplate("testPrincipalId", "KR-NS", KimConstants.PermissionTemplateNames.LOOK_UP_RECORDS,
            BusinessObjectApiResourceTestHelper.makeMap(namespaceCode, documentTypeName), Collections.emptyMap())).andReturn(true);

        EasyMock.replay(permissionService, userSession);
        PowerMock.replay(KRADUtils.class);
        BusinessObjectApiResource.setKualiModuleService(kualiModuleService);
        BusinessObjectApiResource.setPermissionService(permissionService);
        BusinessObjectApiResource.setDataDictionaryService(dataDictionaryService);

        Assert.assertFalse(apiResource.isAuthorized(clazz));
        EasyMock.verify(permissionService, userSession);
    }

    @Test
    @PrepareForTest({KRADUtils.class})
    public void testIsAuthorized_NoLookupRecordsPerm() throws Exception {
        Class clazz = UnitOfMeasure.class;
        String documentTypeName = "PMUM";
        String namespaceCode = "KFS-SYS";

        EasyMock.expect(userSession.getPerson()).andReturn(testPerson).times(2);
        EasyMock.expect(KRADUtils.getUserSessionFromRequest(null)).andReturn(userSession).anyTimes();
        EasyMock.expect(KRADUtils.getNamespaceAndComponentSimpleName(clazz)).andReturn(BusinessObjectApiResourceTestHelper.makeMap(namespaceCode, documentTypeName)).times(2);
        EasyMock.expect(permissionService.isAuthorizedByTemplate("testPrincipalId", "KR-NS", KimConstants.PermissionTemplateNames.INQUIRE_INTO_RECORDS,
            BusinessObjectApiResourceTestHelper.makeMap(namespaceCode, documentTypeName), Collections.emptyMap())).andReturn(true);
        EasyMock.expect(permissionService.isAuthorizedByTemplate("testPrincipalId", "KR-NS", KimConstants.PermissionTemplateNames.LOOK_UP_RECORDS,
            BusinessObjectApiResourceTestHelper.makeMap(namespaceCode, documentTypeName), Collections.emptyMap())).andReturn(false);

        EasyMock.replay(permissionService, userSession);
        PowerMock.replay(KRADUtils.class);
        BusinessObjectApiResource.setKualiModuleService(kualiModuleService);
        BusinessObjectApiResource.setPermissionService(permissionService);
        BusinessObjectApiResource.setDataDictionaryService(dataDictionaryService);

        Assert.assertFalse(apiResource.isAuthorized(clazz));
        EasyMock.verify(permissionService, userSession);
    }

    @Test
    @PrepareForTest({KRADUtils.class})
    public void testIsAuthorized() throws Exception {
        Class clazz = UnitOfMeasure.class;
        String documentTypeName = "PMUM";
        String namespaceCode = "KFS-SYS";

        EasyMock.expect(userSession.getPerson()).andReturn(testPerson).times(2);
        EasyMock.expect(KRADUtils.getUserSessionFromRequest(null)).andReturn(userSession).anyTimes();
        EasyMock.expect(KRADUtils.getNamespaceAndComponentSimpleName(clazz)).andReturn(BusinessObjectApiResourceTestHelper.makeMap(namespaceCode, documentTypeName)).times(2);
        EasyMock.expect(permissionService.isAuthorizedByTemplate("testPrincipalId", "KR-NS", KimConstants.PermissionTemplateNames.INQUIRE_INTO_RECORDS,
            BusinessObjectApiResourceTestHelper.makeMap(namespaceCode, documentTypeName), Collections.emptyMap())).andReturn(true);
        EasyMock.expect(permissionService.isAuthorizedByTemplate("testPrincipalId", "KR-NS", KimConstants.PermissionTemplateNames.LOOK_UP_RECORDS,
            BusinessObjectApiResourceTestHelper.makeMap(namespaceCode, documentTypeName), Collections.emptyMap())).andReturn(true);

        EasyMock.replay(permissionService, userSession);
        PowerMock.replay(KRADUtils.class);
        BusinessObjectApiResource.setKualiModuleService(kualiModuleService);
        BusinessObjectApiResource.setPermissionService(permissionService);
        BusinessObjectApiResource.setDataDictionaryService(dataDictionaryService);

        Assert.assertTrue(apiResource.isAuthorized(clazz));
        EasyMock.verify(permissionService, userSession);
    }

    @Test
    @PrepareForTest({KRADUtils.class})
    public void testGetMaintenanceDocumentEntry() {
        String documentTypeName = "acct";
        EasyMock.expect(dataDictionaryService.getDataDictionary()).andReturn(dataDictionary);
        EasyMock.expect(dataDictionary.getDocumentEntry(documentTypeName.toUpperCase())).andReturn(null);
        EasyMock.replay(dataDictionaryService, dataDictionary);
        apiResource.setDataDictionaryService(dataDictionaryService);
        apiResource.getMaintenanceDocumentEntry(documentTypeName);
        EasyMock.verify(dataDictionaryService, dataDictionary);
    }

    @Test
    @PrepareForTest({KRADUtils.class})
    public void testGetMaintenanceDocumentEntry_LongerDocTypeName() {
        String documentTypeName = "SecurityDefinitionMaintenanceDocument";
        EasyMock.expect(dataDictionaryService.getDataDictionary()).andReturn(dataDictionary);
        EasyMock.expect(dataDictionary.getDocumentEntry(documentTypeName)).andReturn(null);
        EasyMock.replay(dataDictionaryService, dataDictionary);
        apiResource.setDataDictionaryService(dataDictionaryService);
        apiResource.getMaintenanceDocumentEntry(documentTypeName);
        EasyMock.verify(dataDictionaryService, dataDictionary);
    }

    @Test
    @PrepareForTest({KRADUtils.class})
    public void testGetMaintenanceDocumentEntry_NullDocTypeName() {
        String documentTypeName = " ";
        EasyMock.replay(dataDictionaryService, dataDictionary);
        apiResource.setDataDictionaryService(dataDictionaryService);
        MaintenanceDocumentEntry documentEntry = apiResource.getMaintenanceDocumentEntry(documentTypeName);
        Assert.assertNull(documentEntry);
        EasyMock.verify(dataDictionaryService, dataDictionary);
    }
}
