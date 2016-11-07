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
package org.kuali.kfs.sys.rest.service;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.apache.commons.collections.map.HashedMap;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.kns.lookup.LookupUtils;
import org.kuali.kfs.krad.service.PersistenceStructureService;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.rest.ErrorMessage;
import org.kuali.kfs.sys.rest.exception.ApiRequestException;
import org.kuali.kfs.sys.rest.resource.BusinessObjectApiResource;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(PowerMockRunner.class)
public class SearchParameterServiceTest {

    private BusinessObjectApiResource apiResource;
    private PersistenceStructureService persistenceStructureService;

    @Before
    public void setup() {
        apiResource = new BusinessObjectApiResource("sys");
        persistenceStructureService = EasyMock.createMock(PersistenceStructureService.class);
    }

    @Test
    public void testGetLimit() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("limit", "5");

        int limit = SearchParameterService.getLimit(Bank.class, params);

        Assert.assertEquals(5, limit);
    }

    @Test
    @PrepareForTest({LookupUtils.class})
    public void testGetLimit_NegativeLimitSpecified() {
        PowerMock.mockStatic(LookupUtils.class);
        EasyMock.expect(LookupUtils.getSearchResultsLimit(Bank.class)).andReturn(200);
        PowerMock.replay(LookupUtils.class);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("limit", "-1");

        int limit = SearchParameterService.getLimit(Bank.class, params);

        Assert.assertEquals(200, limit);
        PowerMock.verify(LookupUtils.class);
    }

    @Test
    @PrepareForTest({LookupUtils.class})
    public void testGetLimit_BusinessObjectResultsLimitNegative() {
        PowerMock.mockStatic(LookupUtils.class);
        EasyMock.expect(LookupUtils.getSearchResultsLimit(Bank.class)).andReturn(-1);
        EasyMock.expect(LookupUtils.getApplicationSearchResultsLimit()).andReturn(200);
        PowerMock.replay(LookupUtils.class);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();

        int limit = SearchParameterService.getLimit(Bank.class, params);

        Assert.assertEquals(200, limit);
        PowerMock.verify(LookupUtils.class);
    }

    @Test
    public void testGetSortCriteria() {
        List<String> validFields = Arrays.asList("objectId", "accountName", "accountNumber", "chartOfAccountsCode");
        EasyMock.expect(persistenceStructureService.listFieldNames(Account.class)).andReturn(validFields);
        apiResource.setPersistenceStructureService(persistenceStructureService);
        EasyMock.replay(persistenceStructureService);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("sort", "accountName");

        String[] sort = SearchParameterService.getSortCriteria(Account.class, params, persistenceStructureService);

        Assert.assertEquals(new String[]{"accountName"}, sort);
        EasyMock.verify(persistenceStructureService);
    }

    @Test
    public void testGetSortCriteria_NotSpecified() {
        List<String> validFields = Arrays.asList("objectId", "accountName", "accountNumber", "chartOfAccountsCode");
        List<String> primaryKeys = Arrays.asList("chartOfAccountsCode", "accountNumber");
        EasyMock.expect(persistenceStructureService.listFieldNames(Account.class)).andReturn(validFields);
        EasyMock.expect(persistenceStructureService.listPrimaryKeyFieldNames(Account.class)).andReturn(primaryKeys);
        apiResource.setPersistenceStructureService(persistenceStructureService);
        EasyMock.replay(persistenceStructureService);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();

        String[] sort = SearchParameterService.getSortCriteria(Account.class, params, persistenceStructureService);

        Assert.assertEquals(new String[]{"chartOfAccountsCode","accountNumber"}, sort);
        EasyMock.verify(persistenceStructureService);
    }

    @Test
    public void testGetSortCriteria_NotSpecified_NoPrimaryKey() {
        List<String> validFields = Arrays.asList("objectId", "accountName", "accountNumber", "chartOfAccountsCode");
        List<String> primaryKeys = new ArrayList<>();
        EasyMock.expect(persistenceStructureService.listFieldNames(Account.class)).andReturn(validFields);
        EasyMock.expect(persistenceStructureService.listPrimaryKeyFieldNames(Account.class)).andReturn(primaryKeys);
        apiResource.setPersistenceStructureService(persistenceStructureService);
        EasyMock.replay(persistenceStructureService);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();

        String[] sort = SearchParameterService.getSortCriteria(Account.class, params, persistenceStructureService);

        Assert.assertEquals(new String[]{"objectId"}, sort);
        EasyMock.verify(persistenceStructureService);
    }

    @Test
    public void testGetSortCriteria_NotSpecified_NoPrimaryKey_NoObjectId_NoNothing() {
        List<String> validFields = Arrays.asList("accountName", "accountNumber", "chartOfAccountsCode");
        List<String> primaryKeys = new ArrayList<>();
        EasyMock.expect(persistenceStructureService.listFieldNames(Account.class)).andReturn(validFields);
        EasyMock.expect(persistenceStructureService.listPrimaryKeyFieldNames(Account.class)).andReturn(primaryKeys);
        apiResource.setPersistenceStructureService(persistenceStructureService);
        EasyMock.replay(persistenceStructureService);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();

        String[] sort = SearchParameterService.getSortCriteria(Account.class, params, persistenceStructureService);

        Assert.assertEquals(new String[]{"accountName"}, sort);
        EasyMock.verify(persistenceStructureService);
    }

    @Test
    public void testGetSortCriteria_Descending() {
        List<String> validFields = Arrays.asList("objectId", "accountName", "accountNumber", "chartOfAccountsCode");
        EasyMock.expect(persistenceStructureService.listFieldNames(Account.class)).andReturn(validFields);
        apiResource.setPersistenceStructureService(persistenceStructureService);
        EasyMock.replay(persistenceStructureService);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("sort", "-accountName");

        String[] sort = SearchParameterService.getSortCriteria(Account.class, params, persistenceStructureService);

        Assert.assertEquals(new String[]{"-accountName"}, sort);
        EasyMock.verify(persistenceStructureService);
    }

    @Test
    public void testGetSortCriteria_Mutli() {
        List<String> validFields = Arrays.asList("objectId", "accountName", "accountNumber", "chartOfAccountsCode");
        EasyMock.expect(persistenceStructureService.listFieldNames(Account.class)).andReturn(validFields);
        apiResource.setPersistenceStructureService(persistenceStructureService);
        EasyMock.replay(persistenceStructureService);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("sort", "accountName,accountNumber");

        String[] sort = SearchParameterService.getSortCriteria(Account.class, params, persistenceStructureService);

        Assert.assertEquals(new String[]{"accountName", "accountNumber"}, sort);
        EasyMock.verify(persistenceStructureService);
    }

    @Test
    public void testGetSortCriteria_MutliBad() {
        List<String> validFields = Arrays.asList("objectId", "accountName", "accountNumber", "chartOfAccountsCode");
        EasyMock.expect(persistenceStructureService.listFieldNames(Account.class)).andReturn(validFields);
        apiResource.setPersistenceStructureService(persistenceStructureService);
        EasyMock.replay(persistenceStructureService);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("sort", "accountname,accountnumber");

        try {
            String[] sort = SearchParameterService.getSortCriteria(Account.class, params, persistenceStructureService);
        } catch (ApiRequestException are) {
            Response response = are.getResponse();

            Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

            Map<String, Object> exceptionMap = new HashedMap();
            exceptionMap.put("message", "Invalid Search Criteria");
            List<ErrorMessage> errorMessages = new ArrayList<>();
            errorMessages.add(new ErrorMessage("invalid sort field", "class"));
            exceptionMap.put("details", errorMessages);
            Map<String, Object> error = (Map<String, Object>)response.getEntity();
            Assert.assertEquals("Invalid Search Criteria", error.get("message"));
            Assert.assertEquals(2, ((List<ErrorMessage>)error.get("details")).size());
            Assert.assertEquals("invalid sort field", ((List<ErrorMessage>)error.get("details")).get(0).getMessage());
            Assert.assertEquals("accountname", ((List<ErrorMessage>)error.get("details")).get(0).getProperty());
            Assert.assertEquals("invalid sort field", ((List<ErrorMessage>)error.get("details")).get(1).getMessage());
            Assert.assertEquals("accountnumber", ((List<ErrorMessage>)error.get("details")).get(1).getProperty());
        }

        EasyMock.verify(persistenceStructureService);
    }

    @Test
    public void testGetSortCriteria_Bad() {
        List<String> validFields = Arrays.asList("objectId", "accountName", "accountNumber", "chartOfAccountsCode");
        EasyMock.expect(persistenceStructureService.listFieldNames(Account.class)).andReturn(validFields);
        apiResource.setPersistenceStructureService(persistenceStructureService);
        EasyMock.replay(persistenceStructureService);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("sort", "class");

        try {
            String[] sort = SearchParameterService.getSortCriteria(Account.class, params, persistenceStructureService);
        } catch (ApiRequestException are) {
            Response response = are.getResponse();

            Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

            Map<String, Object> exceptionMap = new HashedMap();
            exceptionMap.put("message", "Invalid Search Criteria");
            List<ErrorMessage> errorMessages = new ArrayList<>();
            errorMessages.add(new ErrorMessage("invalid sort field", "class"));
            exceptionMap.put("details", errorMessages);
            Map<String, Object> error = (Map<String, Object>)response.getEntity();
            Assert.assertEquals("Invalid Search Criteria", error.get("message"));
            Assert.assertEquals(1, ((List<ErrorMessage>)error.get("details")).size());
            Assert.assertEquals("invalid sort field", ((List<ErrorMessage>)error.get("details")).get(0).getMessage());
            Assert.assertEquals("class", ((List<ErrorMessage>)error.get("details")).get(0).getProperty());
        }

        EasyMock.verify(persistenceStructureService);
    }

    @Test
    public void testGetSearchQueryCriteria() {
        List<String> validFields = Arrays.asList("objectId", "accountName", "accountNumber", "chartOfAccountsCode");
        EasyMock.expect(persistenceStructureService.listFieldNames(Account.class)).andReturn(validFields);
        apiResource.setPersistenceStructureService(persistenceStructureService);
        EasyMock.replay(persistenceStructureService);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("accountName", "bob");

        Map<String, String> criteria = SearchParameterService.getSearchQueryCriteria(Account.class, params, persistenceStructureService);

        Map<String, String> validCriteria = new HashMap<>();
        validCriteria.put("accountName", "bob");
        Assert.assertEquals(validCriteria, criteria);
        EasyMock.verify(persistenceStructureService);
    }

    @Test
    public void testGetSearchQueryCriteria_Bad() {
        List<String> validFields = Arrays.asList("objectId", "accountName", "accountNumber", "chartOfAccountsCode");
        EasyMock.expect(persistenceStructureService.listFieldNames(Account.class)).andReturn(validFields);
        apiResource.setPersistenceStructureService(persistenceStructureService);
        EasyMock.replay(persistenceStructureService);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("accountname", "bob");

        try {
            Map<String, String> criteria = SearchParameterService.getSearchQueryCriteria(Account.class, params, persistenceStructureService);
        } catch (ApiRequestException are) {
            Response response = are.getResponse();

            Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

            Map<String, Object> exceptionMap = new HashedMap();
            exceptionMap.put("message", "Invalid Search Criteria");
            List<ErrorMessage> errorMessages = new ArrayList<>();
            errorMessages.add(new ErrorMessage("invalid query parameter name", "accountname"));
            exceptionMap.put("details", errorMessages);
            Map<String, Object> error = (Map<String, Object>)response.getEntity();
            Assert.assertEquals("Invalid Search Criteria", error.get("message"));
            Assert.assertEquals(1, ((List<ErrorMessage>)error.get("details")).size());
            Assert.assertEquals("invalid query parameter name", ((List<ErrorMessage>)error.get("details")).get(0).getMessage());
            Assert.assertEquals("accountname", ((List<ErrorMessage>)error.get("details")).get(0).getProperty());
        }

        EasyMock.verify(persistenceStructureService);
    }

    @Test
    public void testGetSearchQueryCriteria_MultiBad() {
        List<String> validFields = Arrays.asList("objectId", "accountName", "accountNumber", "chartOfAccountsCode");
        EasyMock.expect(persistenceStructureService.listFieldNames(Account.class)).andReturn(validFields);
        apiResource.setPersistenceStructureService(persistenceStructureService);
        EasyMock.replay(persistenceStructureService);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("accountname", "bob");
        params.add("accountnumber", "bfdsa5432adfdsaf");

        try {
            Map<String, String> criteria = SearchParameterService.getSearchQueryCriteria(Account.class, params, persistenceStructureService);
        } catch (ApiRequestException are) {
            Response response = are.getResponse();

            Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

            Map<String, Object> exceptionMap = new HashedMap();
            exceptionMap.put("message", "Invalid Search Criteria");
            List<ErrorMessage> errorMessages = new ArrayList<>();
            errorMessages.add(new ErrorMessage("invalid query parameter name", "accountname"));
            exceptionMap.put("details", errorMessages);
            Map<String, Object> error = (Map<String, Object>)response.getEntity();
            Assert.assertEquals("Invalid Search Criteria", error.get("message"));
            Assert.assertEquals(2, ((List<ErrorMessage>)error.get("details")).size());
            Assert.assertEquals("invalid query parameter name", ((List<ErrorMessage>)error.get("details")).get(0).getMessage());
            Assert.assertEquals("invalid query parameter name", ((List<ErrorMessage>)error.get("details")).get(1).getMessage());
        }

        EasyMock.verify(persistenceStructureService);
    }

    @Test
    public void testGetIntQueryParameter_Bad() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("limit", "a");

        try {
            int limit = SearchParameterService.getIntQueryParameter("limit", params);
        } catch (ApiRequestException are) {
            Response response = are.getResponse();

            Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

            Map<String, Object> exceptionMap = new HashedMap();
            exceptionMap.put("message", "Invalid Search Criteria");
            List<ErrorMessage> errorMessages = new ArrayList<>();
            errorMessages.add(new ErrorMessage("parameter is not a number", "limit"));
            exceptionMap.put("details", errorMessages);
            Map<String, Object> error = (Map<String, Object>)response.getEntity();
            Assert.assertEquals("Invalid Search Criteria", error.get("message"));
            Assert.assertEquals(1, ((List<ErrorMessage>)error.get("details")).size());
            Assert.assertEquals("parameter is not a number", ((List<ErrorMessage>)error.get("details")).get(0).getMessage());
            Assert.assertEquals("limit", ((List<ErrorMessage>)error.get("details")).get(0).getProperty());
        }
    }

    @Test
    public void testGetIntQueryParameter() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("limit", "3");

        int limit = SearchParameterService.getIntQueryParameter("limit", params);
        Assert.assertEquals(3, limit);
    }
}
