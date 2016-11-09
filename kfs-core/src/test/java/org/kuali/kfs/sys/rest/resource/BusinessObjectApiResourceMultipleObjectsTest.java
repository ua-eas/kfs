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

import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.kns.datadictionary.MaintenanceDocumentEntry;
import org.kuali.kfs.kns.lookup.LookupUtils;
import org.kuali.kfs.kns.service.BusinessObjectAuthorizationService;
import org.kuali.kfs.krad.UserSession;
import org.kuali.kfs.krad.datadictionary.DataDictionary;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.DataDictionaryService;
import org.kuali.kfs.krad.service.KRADServiceLocator;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.krad.service.PersistenceStructureService;
import org.kuali.kfs.krad.util.KRADUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.identity.TestPerson;
import org.kuali.kfs.sys.rest.BusinessObjectApiResourceTestHelper;
import org.kuali.kfs.sys.rest.ErrorMessage;
import org.kuali.kfs.sys.rest.MockDataDictionaryService;
import org.kuali.kfs.sys.rest.exception.ApiRequestException;
import org.kuali.kfs.sys.rest.service.SearchParameterService;
import org.kuali.kfs.sys.rest.service.SerializationService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kim.api.identity.Person;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(PowerMockRunner.class)
public class BusinessObjectApiResourceMultipleObjectsTest {
    private BusinessObjectApiResource apiResource;
    private PersistenceStructureService persistenceStructureService;
    private BusinessObjectAuthorizationService businessObjectAuthorizationService;
    private BusinessObjectService businessObjectService;
    private DataDictionaryService dataDictionaryService;
    private MaintenanceDocumentEntry maintenanceDocumentEntry;
    private DataDictionary dataDictionary;
    private UserSession userSession;
    private KualiModuleService kualiModuleService;
    private ConfigurationService configurationService;
    private ModuleService moduleService;
    private SearchParameterService searchParameterService;
    private SerializationService serializationService;
    private Person testPerson = new TestPerson("testPrincipalId", "testPrincipalName");

    @Before
    public void setup() {
        apiResource = new BusinessObjectApiResource("sys");
        persistenceStructureService = EasyMock.createMock(PersistenceStructureService.class);
        businessObjectAuthorizationService = EasyMock.createMock(BusinessObjectAuthorizationService.class);
        businessObjectService = EasyMock.createMock(BusinessObjectService.class);
        dataDictionaryService = EasyMock.partialMockBuilder(MockDataDictionaryService.class).addMockedMethods("containsDictionaryObject", "getDictionaryObject", "getDataDictionary").createMock();
        maintenanceDocumentEntry = EasyMock.createMock(MaintenanceDocumentEntry.class);
        dataDictionary = EasyMock.createMock(DataDictionary.class);
        userSession = EasyMock.createMock(UserSession.class);
        kualiModuleService = EasyMock.createMock(KualiModuleService.class);
        configurationService = EasyMock.createMock(ConfigurationService.class);
        moduleService = EasyMock.createMock(ModuleService.class);
        searchParameterService = EasyMock.createMock(SearchParameterService.class);
        serializationService = EasyMock.createMock(SerializationService.class);
        PowerMock.mockStatic(KRADUtils.class);
        PowerMock.mockStatic(KRADServiceLocator.class);
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class, KRADUtils.class})
    public void testSearchBusinessObjects() {
        Map<String, String> queryCriteria = new HashMap<>();
        queryCriteria.put("bankCode", "FW");

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("bankCode", "FW");
        params.add("limit", "1");
        params.add("skip", "1");

        commonMultipleBankBusinessObjectTestPrep(BusinessObjectApiResourceTestHelper.getBank(), queryCriteria, 1, 1, new String[] { "bankCode" }, params);

        UriInfo uriInfo = EasyMock.createMock(UriInfo.class);
        EasyMock.expect(uriInfo.getQueryParameters()).andReturn(params);

        EasyMock.replay(uriInfo, businessObjectService, persistenceStructureService, dataDictionaryService, businessObjectAuthorizationService,
                dataDictionary, userSession, maintenanceDocumentEntry, searchParameterService, serializationService);
        PowerMock.replay(KRADServiceLocator.class,org.kuali.kfs.krad.util.ObjectUtils.class,KRADUtils.class);

        BusinessObjectApiResource.setDataDictionaryService(dataDictionaryService);
        BusinessObjectApiResource.setBusinessObjectService(businessObjectService);
        BusinessObjectApiResource.setBusinessObjectAuthorizationService(businessObjectAuthorizationService);
        BusinessObjectApiResource.setPersistenceStructureService(persistenceStructureService);
        BusinessObjectApiResource.setDataDictionaryService(dataDictionaryService);
        BusinessObjectApiResource.setKualiModuleService(kualiModuleService);
        BusinessObjectApiResource.setConfigurationService(configurationService);
        BusinessObjectApiResource.setSearchParameterService(searchParameterService);
        BusinessObjectApiResource.setSerializationService(serializationService);

        Map<String, Object> results = apiResource.searchBusinessObjects(Bank.class, uriInfo, maintenanceDocumentEntry);

        Assert.assertEquals(6, results.size());
        Assert.assertTrue("results should specify limit", results.containsKey("limit"));
        Assert.assertEquals(1, results.get("limit"));
        Assert.assertTrue("results should specify skip", results.containsKey("skip"));
        Assert.assertEquals(1, results.get("skip"));
        Assert.assertTrue("results should specify totalCount", results.containsKey("totalCount"));
        Assert.assertEquals(1, results.get("totalCount"));
        Assert.assertTrue("results should specify query", results.containsKey("query"));
        Map<String, String> query = new HashMap<>();
        query.put("bankCode", "FW");
        Assert.assertEquals(query, results.get("query"));
        Assert.assertTrue("results should specify sort", results.containsKey("sort"));
        Assert.assertEquals("bankCode", ((String[])results.get("sort"))[0]);
        Assert.assertTrue("results should specify results", results.containsKey("results"));
        Assert.assertEquals(1, ((List<Object>)results.get("results")).size());
        EasyMock.verify(uriInfo, businessObjectService, persistenceStructureService, dataDictionaryService, businessObjectAuthorizationService,
                dataDictionary, userSession, maintenanceDocumentEntry, searchParameterService, serializationService);
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class, KRADUtils.class})
    public void testSearchBusinessObjects_NoResults() {
        Map<String, String> queryCriteria = new HashMap<>();
        queryCriteria.put("bankCode", "FW");

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("bankCode", "FW");
        params.add("limit", "3");
        params.add("skip", "2");

        commonMultipleBankBusinessObjectTestPrep(null, queryCriteria, 2, 3, new String[] { "bankCode" }, params);

        UriInfo uriInfo = EasyMock.createMock(UriInfo.class);
        EasyMock.expect(uriInfo.getQueryParameters()).andReturn(params);

        EasyMock.replay(uriInfo, businessObjectService, persistenceStructureService, dataDictionaryService, businessObjectAuthorizationService,
            dataDictionary, userSession, maintenanceDocumentEntry, searchParameterService, serializationService);
        PowerMock.replay(KRADServiceLocator.class);
        PowerMock.replay(org.kuali.kfs.krad.util.ObjectUtils.class);
        PowerMock.replay(KRADUtils.class);

        BusinessObjectApiResource.setDataDictionaryService(dataDictionaryService);
        BusinessObjectApiResource.setBusinessObjectService(businessObjectService);
        BusinessObjectApiResource.setBusinessObjectAuthorizationService(businessObjectAuthorizationService);
        BusinessObjectApiResource.setPersistenceStructureService(persistenceStructureService);
        BusinessObjectApiResource.setDataDictionaryService(dataDictionaryService);
        BusinessObjectApiResource.setSearchParameterService(searchParameterService);
        BusinessObjectApiResource.setSerializationService(serializationService);

        Map<String, Object> results = apiResource.searchBusinessObjects(Bank.class, uriInfo, maintenanceDocumentEntry);

        Assert.assertEquals(6, results.size());
        Assert.assertTrue("results should specify limit", results.containsKey("limit"));
        Assert.assertEquals(3, results.get("limit"));
        Assert.assertTrue("results should specify skip", results.containsKey("skip"));
        Assert.assertEquals(2, results.get("skip"));
        Assert.assertTrue("results should specify totalCount", results.containsKey("totalCount"));
        Assert.assertEquals(0, results.get("totalCount"));
        Assert.assertTrue("results should specify query", results.containsKey("query"));
        Map<String, String> query = new HashMap<>();
        query.put("bankCode", "FW");
        Assert.assertEquals(query, results.get("query"));
        Assert.assertTrue("results should specify sort", results.containsKey("sort"));
        Assert.assertEquals("bankCode", ((String[])results.get("sort"))[0]);
        Assert.assertTrue("results should specify results", results.containsKey("results"));
        Assert.assertEquals(0, ((List<Object>)results.get("results")).size());
        EasyMock.verify(uriInfo, businessObjectService, persistenceStructureService, dataDictionaryService, businessObjectAuthorizationService,
                dataDictionary, userSession, maintenanceDocumentEntry, searchParameterService, serializationService);
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class, KRADUtils.class, LookupUtils.class})
    public void testSearchBusinessObjects_NoLimitSpecified() {
        Map<String, String> queryCriteria = new HashMap<>();
        queryCriteria.put("bankCode", "FW");

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("bankCode", "FW");

        commonMultipleBankBusinessObjectTestPrep(null, queryCriteria, 0, 200, new String[] { "bankCode" }, params);

        UriInfo uriInfo = EasyMock.createMock(UriInfo.class);
        EasyMock.expect(uriInfo.getQueryParameters()).andReturn(params);

        PowerMock.mockStatic(LookupUtils.class);

        EasyMock.replay(uriInfo, businessObjectService, persistenceStructureService, dataDictionaryService, businessObjectAuthorizationService,
            dataDictionary, userSession, maintenanceDocumentEntry, searchParameterService, serializationService);
        PowerMock.replay(KRADServiceLocator.class);
        PowerMock.replay(org.kuali.kfs.krad.util.ObjectUtils.class);
        PowerMock.replay(KRADUtils.class);
        PowerMock.replay(LookupUtils.class);

        BusinessObjectApiResource.setDataDictionaryService(dataDictionaryService);
        BusinessObjectApiResource.setBusinessObjectService(businessObjectService);
        BusinessObjectApiResource.setBusinessObjectAuthorizationService(businessObjectAuthorizationService);
        BusinessObjectApiResource.setPersistenceStructureService(persistenceStructureService);
        BusinessObjectApiResource.setDataDictionaryService(dataDictionaryService);
        BusinessObjectApiResource.setSearchParameterService(searchParameterService);
        BusinessObjectApiResource.setSerializationService(serializationService);

        Map<String, Object> results = apiResource.searchBusinessObjects(Bank.class, uriInfo, maintenanceDocumentEntry);

        Assert.assertEquals(6, results.size());
        Assert.assertTrue("results should specify limit", results.containsKey("limit"));
        Assert.assertEquals(200, results.get("limit"));
        Assert.assertTrue("results should specify skip", results.containsKey("skip"));
        Assert.assertEquals(0, results.get("skip"));
        Assert.assertTrue("results should specify totalCount", results.containsKey("totalCount"));
        Assert.assertEquals(0, results.get("totalCount"));
        Assert.assertTrue("results should specify query", results.containsKey("query"));
        Map<String, String> query = new HashMap<>();
        query.put("bankCode", "FW");
        Assert.assertEquals(query, results.get("query"));
        Assert.assertTrue("results should specify sort", results.containsKey("sort"));
        Assert.assertEquals("bankCode", ((String[])results.get("sort"))[0]);
        Assert.assertTrue("results should specify results", results.containsKey("results"));
        Assert.assertEquals(0, ((List<Object>)results.get("results")).size());
        EasyMock.verify(uriInfo, businessObjectService, persistenceStructureService, dataDictionaryService, businessObjectAuthorizationService,
                dataDictionary, userSession, maintenanceDocumentEntry, searchParameterService, serializationService);
        PowerMock.verify(LookupUtils.class);
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class, KRADUtils.class})
    public void testSearchBusinessObjects_checkChildLinks() {
        Map<String, String> queryCriteria = new HashMap<>();
        queryCriteria.put("bankCode", "FW");

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("bankCode", "FW");
        params.add("limit", "1");
        params.add("skip", "1");

        commonMultipleBankBusinessObjectTestPrep(BusinessObjectApiResourceTestHelper.getBank(), queryCriteria, 1, 1, new String[]{"bankCode"}, params);

        UriInfo uriInfo = EasyMock.createMock(UriInfo.class);
        EasyMock.expect(uriInfo.getQueryParameters()).andReturn(params);

        EasyMock.replay(uriInfo, businessObjectService, persistenceStructureService, dataDictionaryService, businessObjectAuthorizationService, dataDictionary, userSession,
            maintenanceDocumentEntry, kualiModuleService, moduleService, configurationService, searchParameterService, serializationService);
        PowerMock.replay(KRADServiceLocator.class);
        PowerMock.replay(org.kuali.kfs.krad.util.ObjectUtils.class);
        PowerMock.replay(KRADUtils.class);

        BusinessObjectApiResource.setDataDictionaryService(dataDictionaryService);
        BusinessObjectApiResource.setBusinessObjectService(businessObjectService);
        BusinessObjectApiResource.setBusinessObjectAuthorizationService(businessObjectAuthorizationService);
        BusinessObjectApiResource.setPersistenceStructureService(persistenceStructureService);
        BusinessObjectApiResource.setDataDictionaryService(dataDictionaryService);
        BusinessObjectApiResource.setKualiModuleService(kualiModuleService);
        BusinessObjectApiResource.setConfigurationService(configurationService);
        BusinessObjectApiResource.setSearchParameterService(searchParameterService);
        BusinessObjectApiResource.setSerializationService(serializationService);

        Map<String, Object> results = apiResource.searchBusinessObjects(Bank.class, uriInfo, maintenanceDocumentEntry);

        Assert.assertEquals(6, results.size());
        Assert.assertTrue("results should specify limit", results.containsKey("limit"));
        Assert.assertEquals(1, results.get("limit"));
        Assert.assertTrue("results should specify skip", results.containsKey("skip"));
        Assert.assertEquals(1, results.get("skip"));
        Assert.assertTrue("results should specify totalCount", results.containsKey("totalCount"));
        Assert.assertEquals(1, results.get("totalCount"));
        Assert.assertTrue("results should specify query", results.containsKey("query"));
        Map<String, String> query = new HashMap<>();
        query.put("bankCode", "FW");
        Assert.assertEquals(query, results.get("query"));
        Assert.assertTrue("results should specify sort", results.containsKey("sort"));
        Assert.assertEquals("bankCode", ((String[]) results.get("sort"))[0]);
        Assert.assertTrue("results should specify results", results.containsKey("results"));
        Assert.assertEquals(1, ((List<Object>) results.get("results")).size());
        final Map<String, Object> serializedBank = ((List<Map<String, Object>>) results.get("results")).get(0);
        Assert.assertTrue("Serialized bank should have a key for cashOffsetFinancialChartOfAccount", serializedBank.containsKey(KFSPropertyConstants.CASH_OFFSET_FINANCIAL_CHART_OF_ACCOUNT));
        Assert.assertTrue("Cash Offset Financial Chart should have a link key", ((Map<String, Object>) serializedBank.get(KFSPropertyConstants.CASH_OFFSET_FINANCIAL_CHART_OF_ACCOUNT)).containsKey(KFSPropertyConstants.LINK));
        Assert.assertEquals("Cash Offset Financial Chart should have a proper link", "https://kuali.co/fin/coa/api/v1/reference/coat/BNKCHART5554455", ((Map<String, Object>) serializedBank.get(KFSPropertyConstants.CASH_OFFSET_FINANCIAL_CHART_OF_ACCOUNT)).get(KFSPropertyConstants.LINK));
        EasyMock.verify(uriInfo, businessObjectService, persistenceStructureService, dataDictionaryService, businessObjectAuthorizationService, dataDictionary, userSession,
                maintenanceDocumentEntry, kualiModuleService, moduleService, configurationService, searchParameterService, serializationService);
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class, KRADUtils.class})
    public void testSearchBusinessObjects_InvalidSort_Ojb() {
        Map<String, String> queryCriteria = new HashMap<>();
        queryCriteria.put("accountName", "MyAccount");

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("accountName", "MyAccount");
        params.add("limit", "1");
        params.add("skip", "1");
        params.add("sort", "closed");

        List<String> ojbFields = Arrays.asList("chartOfAccountsCode","accountNumber","accountName");
        EasyMock.expect(persistenceStructureService.listFieldNames(Account.class)).andReturn(ojbFields).anyTimes();
        EasyMock.expect(KRADServiceLocator.getPersistenceStructureService()).andReturn(persistenceStructureService).anyTimes();

        UriInfo uriInfo = EasyMock.createMock(UriInfo.class);
        EasyMock.expect(uriInfo.getQueryParameters()).andReturn(params);

        EasyMock.expect(searchParameterService.getSearchQueryCriteria(params, ojbFields)).andReturn(queryCriteria);
        EasyMock.expect(searchParameterService.getIntQueryParameter(KFSConstants.Search.SKIP, params)).andReturn(1);
        EasyMock.expect(searchParameterService.getLimit(Account.class, params)).andReturn(1);
        List<ErrorMessage> errorMessages = new ArrayList<>();
        errorMessages.add(new ErrorMessage("invalid sort field", "closed"));
        EasyMock.expect(searchParameterService.getSortCriteria(Account.class, params, ojbFields)).andThrow(new ApiRequestException("Invalid Search Criteria", errorMessages));

        Map<String, Object> ojbFieldsMap = new HashMap<>();
        ojbFieldsMap.put(SerializationService.FIELDS_KEY, ojbFields);
        EasyMock.expect(serializationService.findBusinessObjectFields(maintenanceDocumentEntry)).andReturn(ojbFieldsMap);

        EasyMock.replay(uriInfo, businessObjectService, persistenceStructureService, dataDictionaryService, businessObjectAuthorizationService, dataDictionary,
                userSession, maintenanceDocumentEntry, searchParameterService, serializationService);
        PowerMock.replay(KRADServiceLocator.class,org.kuali.kfs.krad.util.ObjectUtils.class,KRADUtils.class);

        BusinessObjectApiResource.setDataDictionaryService(dataDictionaryService);
        BusinessObjectApiResource.setBusinessObjectService(businessObjectService);
        BusinessObjectApiResource.setBusinessObjectAuthorizationService(businessObjectAuthorizationService);
        BusinessObjectApiResource.setPersistenceStructureService(persistenceStructureService);
        BusinessObjectApiResource.setDataDictionaryService(dataDictionaryService);
        BusinessObjectApiResource.setSearchParameterService(searchParameterService);
        BusinessObjectApiResource.setSerializationService(serializationService);

        try {
            apiResource.searchBusinessObjects(Account.class, uriInfo, maintenanceDocumentEntry);
        } catch (ApiRequestException e) {
            Response response = e.getResponse();
            Assert.assertEquals("Response should be a 400", 400, response.getStatus());
            Map<String, Object> entity = (Map<String,Object>)response.getEntity();
            Assert.assertEquals("Response entity size should be 2", 2, entity.size());
            Assert.assertEquals("Response message should be Invalid Search Criteria", "Invalid Search Criteria", entity.get("message"));
            List<ErrorMessage> details = (List<ErrorMessage>)entity.get("details");
            Assert.assertEquals("Details should have 1 error message", 1, details.size());
            Assert.assertEquals("Details error message is incorrect", "invalid sort field", details.get(0).getMessage());
            Assert.assertEquals("Details property is incorrect", "closed", details.get(0).getProperty());
        }
        EasyMock.verify(uriInfo, businessObjectService, persistenceStructureService, dataDictionaryService, businessObjectAuthorizationService, dataDictionary,
                userSession, maintenanceDocumentEntry, searchParameterService, serializationService);
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class, KRADUtils.class})
    public void testSearchBusinessObjects_InvalidQueryParam_Ojb() {
        Map<String, String> queryCriteria = new HashMap<>();
        queryCriteria.put("closed", "true");

        List<String> ojbFields = Arrays.asList("chartOfAccountsCode","accountNumber","accountName");
        EasyMock.expect(persistenceStructureService.listFieldNames(Account.class)).andReturn(ojbFields).anyTimes();
        EasyMock.expect(KRADServiceLocator.getPersistenceStructureService()).andReturn(persistenceStructureService).anyTimes();

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("closed", "true");
        params.add("limit", "1");
        params.add("skip", "1");

        UriInfo uriInfo = EasyMock.createMock(UriInfo.class);
        EasyMock.expect(uriInfo.getQueryParameters()).andReturn(params);

        List<ErrorMessage> errorMessages = new ArrayList<>();
        errorMessages.add(new ErrorMessage("invalid query parameter name", "closed"));
        EasyMock.expect(searchParameterService.getSearchQueryCriteria(params, ojbFields)).andThrow(new ApiRequestException("Invalid Search Criteria", errorMessages));

        Map<String, Object> ojbFieldsMap = new HashMap<>();
        ojbFieldsMap.put(SerializationService.FIELDS_KEY, ojbFields);
        EasyMock.expect(serializationService.findBusinessObjectFields(maintenanceDocumentEntry)).andReturn(ojbFieldsMap);

        EasyMock.replay(uriInfo, businessObjectService, persistenceStructureService, dataDictionaryService, businessObjectAuthorizationService,
                dataDictionary, userSession, maintenanceDocumentEntry, searchParameterService, serializationService);
        PowerMock.replay(KRADServiceLocator.class,org.kuali.kfs.krad.util.ObjectUtils.class,KRADUtils.class);

        BusinessObjectApiResource.setDataDictionaryService(dataDictionaryService);
        BusinessObjectApiResource.setBusinessObjectService(businessObjectService);
        BusinessObjectApiResource.setBusinessObjectAuthorizationService(businessObjectAuthorizationService);
        BusinessObjectApiResource.setPersistenceStructureService(persistenceStructureService);
        BusinessObjectApiResource.setDataDictionaryService(dataDictionaryService);
        BusinessObjectApiResource.setSearchParameterService(searchParameterService);
        BusinessObjectApiResource.setSerializationService(serializationService);

        try {
            apiResource.searchBusinessObjects(Account.class, uriInfo, maintenanceDocumentEntry);
        } catch (ApiRequestException e) {
            Response response = e.getResponse();
            Assert.assertEquals("Response should be a 400", 400, response.getStatus());
            Map<String, Object> entity = (Map<String,Object>)response.getEntity();
            Assert.assertEquals("Response entity size should be 2", 2, entity.size());
            Assert.assertEquals("Response message should be Invalid Search Criteria", "Invalid Search Criteria", entity.get("message"));
            List<ErrorMessage> details = (List<ErrorMessage>)entity.get("details");
            Assert.assertEquals("Details should have 1 error message", 1, details.size());
            Assert.assertEquals("Details error message is incorrect", "invalid query parameter name", details.get(0).getMessage());
            Assert.assertEquals("Details property is incorrect", "closed", details.get(0).getProperty());
        }
        EasyMock.verify(uriInfo, businessObjectService, persistenceStructureService, dataDictionaryService, businessObjectAuthorizationService, dataDictionary,
                userSession, maintenanceDocumentEntry, searchParameterService, serializationService);
    }

    private void commonMultipleBankBusinessObjectTestPrep(Bank result, Map<String, String> queryCriteria, int skip, int limit, String[] sort, MultivaluedMap<String, String> params) {
        String className = Bank.class.getSimpleName();

        List<Bank> collection = new ArrayList<>();
        if (result != null) {
            collection.add(result);
        }

        EasyMock.expect(businessObjectService.countMatching(Bank.class, queryCriteria)).andReturn(collection.size());
        EasyMock.expect(businessObjectService.findMatching(EasyMock.eq(Bank.class), EasyMock.eq(queryCriteria), EasyMock.eq(skip), EasyMock.eq(limit), EasyMock.aryEq(sort))).andReturn(collection);

        EasyMock.expect(businessObjectAuthorizationService.isNonProductionEnvAndUnmaskingTurnedOff()).andReturn(false).anyTimes();
        EasyMock.expect(dataDictionaryService.getDataDictionary()).andReturn(dataDictionary).anyTimes();
        EasyMock.expect(dataDictionary.getBusinessObjectEntry(className)).andReturn(BusinessObjectApiResourceTestHelper.getDDEntry(Bank.class)).anyTimes();
        EasyMock.expect(businessObjectAuthorizationService.canFullyUnmaskField(testPerson, Bank.class, "bankAccountNumber", null)).andReturn(false).anyTimes();
        EasyMock.expect(businessObjectAuthorizationService.canPartiallyUnmaskField(testPerson, Bank.class, "bankRoutingNumber", null)).andReturn(false).anyTimes();
        EasyMock.expect(KRADUtils.getUserSessionFromRequest(null)).andReturn(userSession).anyTimes();
        EasyMock.expect(userSession.getPerson()).andReturn(testPerson).anyTimes();

        List<String> validFields = Arrays.asList("objectId", "bankCode", "bankName", "bankRountingNumber", "bankAccountNumber");
        EasyMock.expect(persistenceStructureService.listFieldNames(Bank.class)).andReturn(validFields).anyTimes();
        Map<String, Object> validFieldsMap = new HashMap<>();
        validFieldsMap.put(SerializationService.FIELDS_KEY, validFields);
        EasyMock.expect(serializationService.findBusinessObjectFields(maintenanceDocumentEntry)).andReturn(validFieldsMap);
        EasyMock.expect(serializationService.businessObjectToJson(Bank.class, result, validFieldsMap, testPerson)).andReturn(BusinessObjectApiResourceTestHelper.getSerializedBank()).anyTimes();

        EasyMock.expect(searchParameterService.getSearchQueryCriteria(params, validFields)).andReturn(queryCriteria);
        EasyMock.expect(searchParameterService.getIntQueryParameter(KFSConstants.Search.SKIP, params)).andReturn(skip);
        EasyMock.expect(searchParameterService.getLimit(Bank.class, params)).andReturn(limit);
        EasyMock.expect(searchParameterService.getSortCriteria(Bank.class, params, validFields)).andReturn(sort);

        EasyMock.expect(KRADServiceLocator.getPersistenceStructureService()).andReturn(persistenceStructureService).anyTimes();
    }
}
