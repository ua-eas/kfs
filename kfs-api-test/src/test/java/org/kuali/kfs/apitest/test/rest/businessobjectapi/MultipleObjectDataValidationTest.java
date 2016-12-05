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
package org.kuali.kfs.apitest.test.rest.businessobjectapi;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import org.kuali.kfs.apitest.Constants;
import org.kuali.kfs.apitest.utils.RestUtilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultipleObjectDataValidationTest {
    private static String SEARCH_API = "/ar/api/v1/reference/arsi";

    @Test
    public void searchWithLimitAndSkip() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(SEARCH_API + "?limit=2&skip=1", Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        Map<String,Object> searchResults = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));
        List<Map<String, Object>> results = (List<Map<String, Object>>)searchResults.get(Constants.Search.RESULTS);
        Map<String, Object> bo = results.get(0);

        // Validate that the returned object is correct
        Assert.assertEquals(new ArrayList(Arrays.asList("universityFiscalYear", "processingChartOfAccountCode", "processingOrganizationCode")), searchResults.get(Constants.Search.SORT));
        Assert.assertEquals(2, searchResults.get(Constants.Search.LIMIT));
        Assert.assertEquals(1, searchResults.get(Constants.Search.SKIP));
        Assert.assertEquals(60, searchResults.get(Constants.Search.TOTAL_COUNT));
        Assert.assertEquals(new HashMap(), searchResults.get(Constants.Search.QUERY));

        // Check a sample of fields returned
        Assert.assertEquals(true,(Boolean)bo.get("active"));
        Assert.assertEquals("9460",(String)bo.get("creditCardObjectCode"));
        Assert.assertEquals("66278",(String)bo.get("lockboxNumber"));
        Assert.assertNull(bo.get("newCollectionRecord"));
        Assert.assertEquals("5A691AF93B5B424DE0404F8189D80D73",(String)bo.get("objectId"));

        Map<String,Object> processingChartOfAccount = (Map<String,Object>)bo.get("processingChartOfAccount");
        String link = (String)processingChartOfAccount.get("link");
        Assert.assertTrue(link.endsWith("/coa/api/v1/reference/coat/014F3DAF748FA448E043814FD28EA448"));
    }

    @Test
    public void searchWithDescendingSort() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(SEARCH_API + "?limit=1&sort=-universityFiscalYear,processingChartOfAccountCode,processingOrganizationCode", Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        Map<String,Object> searchResults = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));
        List<Map<String, Object>> results = (List<Map<String, Object>>)searchResults.get(Constants.Search.RESULTS);
        Map<String, Object> bo = results.get(0);

        // Validate that the returned object is correct
        Assert.assertEquals(new ArrayList(Arrays.asList("-universityFiscalYear", "processingChartOfAccountCode", "processingOrganizationCode")), searchResults.get(Constants.Search.SORT));
        Assert.assertEquals(1, searchResults.get(Constants.Search.LIMIT));
        Assert.assertEquals(0, searchResults.get(Constants.Search.SKIP));
        Assert.assertEquals(60, searchResults.get(Constants.Search.TOTAL_COUNT));
        Assert.assertEquals(new HashMap(), searchResults.get(Constants.Search.QUERY));

        // Check a sample of fields returned
        Assert.assertEquals(true,(Boolean)bo.get("active"));
        Assert.assertEquals("1599",(String)bo.get("creditCardObjectCode"));
        Assert.assertEquals("12345",(String)bo.get("lockboxNumber"));
        Assert.assertNull(bo.get("newCollectionRecord"));
        Assert.assertEquals("f54d1bdf-fd0e-4a48-b9d2-42ad8086e256",(String)bo.get("objectId"));

        Map<String,Object> processingChartOfAccount = (Map<String,Object>)bo.get("processingChartOfAccount");
        String link = (String)processingChartOfAccount.get("link");
        Assert.assertTrue(link.endsWith("/coa/api/v1/reference/coat/014F3DAF748AA448E043814FD28EA448"));
    }

    @Test
    public void searchWithoutLimit() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(SEARCH_API, Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        Map<String,Object> searchResults = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));
        List<Map<String, Object>> results = (List<Map<String, Object>>)searchResults.get(Constants.Search.RESULTS);
        Map<String, Object> bo = results.get(0);

        // Validate that the returned object is correct
        Assert.assertEquals(new ArrayList(Arrays.asList("universityFiscalYear", "processingChartOfAccountCode", "processingOrganizationCode")), searchResults.get(Constants.Search.SORT));
        Assert.assertEquals(200, searchResults.get(Constants.Search.LIMIT));
        Assert.assertEquals(0, searchResults.get(Constants.Search.SKIP));
        Assert.assertEquals(60, searchResults.get(Constants.Search.TOTAL_COUNT));
        Assert.assertEquals(new HashMap(), searchResults.get(Constants.Search.QUERY));

        // Check a sample of fields returned
        Assert.assertEquals(true,(Boolean)bo.get("active"));
        Assert.assertEquals("9460",(String)bo.get("creditCardObjectCode"));
        Assert.assertEquals("66255",(String)bo.get("lockboxNumber"));
        Assert.assertNull(bo.get("newCollectionRecord"));
        Assert.assertEquals("5A691AF93B5A424DE0404F8189D80D73",(String)bo.get("objectId"));

        Map<String,Object> processingChartOfAccount = (Map<String,Object>)bo.get("processingChartOfAccount");
        String link = (String)processingChartOfAccount.get("link");
        Assert.assertTrue(link.endsWith("/coa/api/v1/reference/coat/014F3DAF748BA448E043814FD28EA448"));
    }

    @Test
    public void searchWithModifiedBefore() throws IOException {
        String modifiedBeforeParam = "2010-07-01T00:00:00Z";
        HttpResponse response = RestUtilities.makeRequest(String.format("%s?modifiedBefore=%s", SEARCH_API, modifiedBeforeParam), Constants.KHUNTLEY_TOKEN);

        String responseString = RestUtilities.inputStreamToString(response.getEntity().getContent());

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        Map<String,Object> searchResults = RestUtilities.parse(responseString);
        List<Map<String, Object>> results = (List<Map<String, Object>>)searchResults.get(Constants.Search.RESULTS);
        Map<String, Object> bo = results.get(0);

        // Validate that the returned object is correct
        Assert.assertEquals(new ArrayList(Arrays.asList("universityFiscalYear", "processingChartOfAccountCode", "processingOrganizationCode")), searchResults.get(Constants.Search.SORT));
        Assert.assertEquals(200, searchResults.get(Constants.Search.LIMIT));
        Assert.assertEquals(0, searchResults.get(Constants.Search.SKIP));
        Assert.assertEquals(modifiedBeforeParam, searchResults.get(Constants.Search.MODIFIED_BEFORE));
        Assert.assertNull(searchResults.get(Constants.Search.MODIFIED_AFTER));
        Assert.assertEquals(60, searchResults.get(Constants.Search.TOTAL_COUNT));
        Assert.assertEquals(new HashMap(), searchResults.get(Constants.Search.QUERY));

        // Check a sample of fields returned
        Assert.assertEquals(true,(Boolean)bo.get("active"));
        Assert.assertEquals("9460",(String)bo.get("creditCardObjectCode"));
        Assert.assertEquals("66255",(String)bo.get("lockboxNumber"));
        Assert.assertNull(bo.get("newCollectionRecord"));
        Assert.assertEquals("5A691AF93B5A424DE0404F8189D80D73",(String)bo.get("objectId"));

        Map<String,Object> processingChartOfAccount = (Map<String,Object>)bo.get("processingChartOfAccount");
        String link = (String)processingChartOfAccount.get("link");
        Assert.assertTrue(link.endsWith("/coa/api/v1/reference/coat/014F3DAF748BA448E043814FD28EA448"));
    }

    @Test
    public void searchWithModifiedBeforeNoResults() throws IOException {
        String modifiedBeforeParam = "2008-07-01T00:00:00Z";
        HttpResponse response = RestUtilities.makeRequest(String.format("%s?modifiedBefore=%s", SEARCH_API, modifiedBeforeParam), Constants.KHUNTLEY_TOKEN);

        String responseString = RestUtilities.inputStreamToString(response.getEntity().getContent());

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        Map<String,Object> searchResults = RestUtilities.parse(responseString);

        // Validate that the returned object is correct
        Assert.assertEquals(new ArrayList(Arrays.asList("universityFiscalYear", "processingChartOfAccountCode", "processingOrganizationCode")), searchResults.get(Constants.Search.SORT));
        Assert.assertEquals(200, searchResults.get(Constants.Search.LIMIT));
        Assert.assertEquals(0, searchResults.get(Constants.Search.SKIP));
        Assert.assertEquals(modifiedBeforeParam, searchResults.get(Constants.Search.MODIFIED_BEFORE));
        Assert.assertNull(searchResults.get(Constants.Search.MODIFIED_AFTER));
        Assert.assertEquals(0, searchResults.get(Constants.Search.TOTAL_COUNT));
        Assert.assertEquals(new HashMap(), searchResults.get(Constants.Search.QUERY));
    }

    @Test
    public void searchWithModifiedAfter() throws IOException {
        String modifiedAfterParam = "2008-07-01T00:00:00Z";
        HttpResponse response = RestUtilities.makeRequest(String.format("%s?modifiedAfter=%s", SEARCH_API, modifiedAfterParam), Constants.KHUNTLEY_TOKEN);

        String responseString = RestUtilities.inputStreamToString(response.getEntity().getContent());

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        Map<String,Object> searchResults = RestUtilities.parse(responseString);
        List<Map<String, Object>> results = (List<Map<String, Object>>)searchResults.get(Constants.Search.RESULTS);
        Map<String, Object> bo = results.get(0);

        // Validate that the returned object is correct
        Assert.assertEquals(new ArrayList(Arrays.asList("universityFiscalYear", "processingChartOfAccountCode", "processingOrganizationCode")), searchResults.get(Constants.Search.SORT));
        Assert.assertEquals(200, searchResults.get(Constants.Search.LIMIT));
        Assert.assertEquals(0, searchResults.get(Constants.Search.SKIP));
        Assert.assertEquals(modifiedAfterParam, searchResults.get(Constants.Search.MODIFIED_AFTER));
        Assert.assertNull(searchResults.get(Constants.Search.MODIFIED_BEFORE));
        Assert.assertEquals(60, searchResults.get(Constants.Search.TOTAL_COUNT));
        Assert.assertEquals(new HashMap(), searchResults.get(Constants.Search.QUERY));

        // Check a sample of fields returned
        Assert.assertEquals(true,(Boolean)bo.get("active"));
        Assert.assertEquals("9460",(String)bo.get("creditCardObjectCode"));
        Assert.assertEquals("66255",(String)bo.get("lockboxNumber"));
        Assert.assertNull(bo.get("newCollectionRecord"));
        Assert.assertEquals("5A691AF93B5A424DE0404F8189D80D73",(String)bo.get("objectId"));

        Map<String,Object> processingChartOfAccount = (Map<String,Object>)bo.get("processingChartOfAccount");
        String link = (String)processingChartOfAccount.get("link");
        Assert.assertTrue(link.endsWith("/coa/api/v1/reference/coat/014F3DAF748BA448E043814FD28EA448"));
    }

    @Test
    public void searchWithModifiedAfterNoResults() throws IOException {
        String modifiedAfterParam = "2010-07-01T00:00:00Z";
        HttpResponse response = RestUtilities.makeRequest(String.format("%s?modifiedAfter=%s", SEARCH_API, modifiedAfterParam), Constants.KHUNTLEY_TOKEN);

        String responseString = RestUtilities.inputStreamToString(response.getEntity().getContent());

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        Map<String,Object> searchResults = RestUtilities.parse(responseString);

        // Validate that the returned object is correct
        Assert.assertEquals(new ArrayList(Arrays.asList("universityFiscalYear", "processingChartOfAccountCode", "processingOrganizationCode")), searchResults.get(Constants.Search.SORT));
        Assert.assertEquals(200, searchResults.get(Constants.Search.LIMIT));
        Assert.assertEquals(0, searchResults.get(Constants.Search.SKIP));
        Assert.assertEquals(modifiedAfterParam, searchResults.get(Constants.Search.MODIFIED_AFTER));
        Assert.assertNull(searchResults.get(Constants.Search.MODIFIED_BEFORE));
        Assert.assertEquals(0, searchResults.get(Constants.Search.TOTAL_COUNT));
        Assert.assertEquals(new HashMap(), searchResults.get(Constants.Search.QUERY));
    }

    @Test
    public void searchWithModifiedBeforeAndAfter() throws IOException {
        String modifiedAfterParam = "2008-07-01T00:00:00Z";
        String modifiedBeforeParam = "2010-07-01T00:00:00Z";
        HttpResponse response = RestUtilities.makeRequest(String.format("%s?modifiedAfter=%s&modifiedBefore=%s", SEARCH_API, modifiedAfterParam, modifiedBeforeParam), Constants.KHUNTLEY_TOKEN);

        String responseString = RestUtilities.inputStreamToString(response.getEntity().getContent());

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        Map<String,Object> searchResults = RestUtilities.parse(responseString);
        List<Map<String, Object>> results = (List<Map<String, Object>>)searchResults.get(Constants.Search.RESULTS);
        Map<String, Object> bo = results.get(0);

        // Validate that the returned object is correct
        Assert.assertEquals(new ArrayList(Arrays.asList("universityFiscalYear", "processingChartOfAccountCode", "processingOrganizationCode")), searchResults.get(Constants.Search.SORT));
        Assert.assertEquals(200, searchResults.get(Constants.Search.LIMIT));
        Assert.assertEquals(0, searchResults.get(Constants.Search.SKIP));
        Assert.assertEquals(modifiedBeforeParam, searchResults.get(Constants.Search.MODIFIED_BEFORE));
        Assert.assertEquals(modifiedAfterParam, searchResults.get(Constants.Search.MODIFIED_AFTER));
        Assert.assertEquals(60, searchResults.get(Constants.Search.TOTAL_COUNT));
        Assert.assertEquals(new HashMap(), searchResults.get(Constants.Search.QUERY));

        // Check a sample of fields returned
        Assert.assertEquals(true,(Boolean)bo.get("active"));
        Assert.assertEquals("9460",(String)bo.get("creditCardObjectCode"));
        Assert.assertEquals("66255",(String)bo.get("lockboxNumber"));
        Assert.assertNull(bo.get("newCollectionRecord"));
        Assert.assertEquals("5A691AF93B5A424DE0404F8189D80D73",(String)bo.get("objectId"));

        Map<String,Object> processingChartOfAccount = (Map<String,Object>)bo.get("processingChartOfAccount");
        String link = (String)processingChartOfAccount.get("link");
        Assert.assertTrue(link.endsWith("/coa/api/v1/reference/coat/014F3DAF748BA448E043814FD28EA448"));
    }

    @Test
    public void searchWithModifiedBeforeAndAfterNoResults() throws IOException {
        String modifiedAfterParam = "2010-07-01T00:00:00Z";
        String modifiedBeforeParam = "2008-07-01T00:00:00Z";
        HttpResponse response = RestUtilities.makeRequest(String.format("%s?modifiedAfter=%s&modifiedBefore=%s", SEARCH_API, modifiedAfterParam, modifiedBeforeParam), Constants.KHUNTLEY_TOKEN);

        String responseString = RestUtilities.inputStreamToString(response.getEntity().getContent());

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        Map<String,Object> searchResults = RestUtilities.parse(responseString);

        // Validate that the returned object is correct
        Assert.assertEquals(new ArrayList(Arrays.asList("universityFiscalYear", "processingChartOfAccountCode", "processingOrganizationCode")), searchResults.get(Constants.Search.SORT));
        Assert.assertEquals(200, searchResults.get(Constants.Search.LIMIT));
        Assert.assertEquals(0, searchResults.get(Constants.Search.SKIP));
        Assert.assertEquals(modifiedAfterParam, searchResults.get(Constants.Search.MODIFIED_AFTER));
        Assert.assertEquals(modifiedBeforeParam, searchResults.get(Constants.Search.MODIFIED_BEFORE));
        Assert.assertEquals(0, searchResults.get(Constants.Search.TOTAL_COUNT));
        Assert.assertEquals(new HashMap(), searchResults.get(Constants.Search.QUERY));
    }


    @Test
    public void searchWithInvalidLimit() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(SEARCH_API + "?limit=a", Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST,response.getStatusLine().getStatusCode());

        Map<String,Object> error = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));
        Assert.assertEquals("Invalid Search Criteria", error.get(Constants.Error.MESSAGE));

        List<Map<String, Object>> errorDetails = (List<Map<String, Object>>)error.get(Constants.Error.DETAILS);
        Assert.assertEquals(1, errorDetails.size());

        Map<String, Object> errorDetail = errorDetails.get(0);
        Assert.assertEquals("limit", errorDetail.get(Constants.Error.PROPERTY));
        Assert.assertEquals("parameter is not a number", errorDetail.get(Constants.Error.MESSAGE));
    }

    @Test
    public void searchWithInvalidSkip() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(SEARCH_API + "?skip=a", Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST,response.getStatusLine().getStatusCode());

        Map<String,Object> error = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));
        Assert.assertEquals("Invalid Search Criteria", error.get(Constants.Error.MESSAGE));

        List<Map<String, Object>> errorDetails = (List<Map<String, Object>>)error.get(Constants.Error.DETAILS);
        Assert.assertEquals(1, errorDetails.size());

        Map<String, Object> errorDetail = errorDetails.get(0);
        Assert.assertEquals("skip", errorDetail.get(Constants.Error.PROPERTY));
        Assert.assertEquals("parameter is not a number", errorDetail.get(Constants.Error.MESSAGE));
    }

    @Test
    public void searchWithInvalidSort() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(SEARCH_API + "?sort=invalidFieldName", Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST,response.getStatusLine().getStatusCode());

        Map<String,Object> error = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));
        Assert.assertEquals("Invalid Search Criteria", error.get(Constants.Error.MESSAGE));

        List<Map<String, Object>> errorDetails = (List<Map<String, Object>>)error.get(Constants.Error.DETAILS);
        Assert.assertEquals(1, errorDetails.size());

        Map<String, Object> errorDetail = errorDetails.get(0);
        Assert.assertEquals("invalidFieldName", errorDetail.get(Constants.Error.PROPERTY));
        Assert.assertEquals("invalid sort field", errorDetail.get(Constants.Error.MESSAGE));
    }

    @Test
    public void searchWithInvalidSearchField() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(SEARCH_API + "?invalidFieldName=invalidFieldValue", Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST,response.getStatusLine().getStatusCode());

        Map<String,Object> error = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));
        Assert.assertEquals("Invalid Search Criteria", error.get(Constants.Error.MESSAGE));

        List<Map<String, Object>> errorDetails = (List<Map<String, Object>>)error.get(Constants.Error.DETAILS);
        Assert.assertEquals(1, errorDetails.size());

        Map<String, Object> errorDetail = errorDetails.get(0);
        Assert.assertEquals("invalidFieldName", errorDetail.get(Constants.Error.PROPERTY));
        Assert.assertEquals("invalid query parameter name", errorDetail.get(Constants.Error.MESSAGE));
    }

    @Test
    public void searchWithInvalidModifiedBefore() throws IOException {
        String modifiedBeforeParam = "00:00:00T2008-07-01Z";
        HttpResponse response = RestUtilities.makeRequest(String.format("%s?modifiedBefore=%s", SEARCH_API, modifiedBeforeParam), Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST,response.getStatusLine().getStatusCode());

        Map<String,Object> error = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));
        Assert.assertEquals("Invalid Search Criteria", error.get(Constants.Error.MESSAGE));

        List<Map<String, Object>> errorDetails = (List<Map<String, Object>>)error.get(Constants.Error.DETAILS);
        Assert.assertEquals(1, errorDetails.size());

        Map<String, Object> errorDetail = errorDetails.get(0);
        Assert.assertEquals("modifiedBefore", errorDetail.get(Constants.Error.PROPERTY));
        Assert.assertEquals("parameter is not a valid ISO8601 date", errorDetail.get(Constants.Error.MESSAGE));
    }

    @Test
    public void searchWithInvalidModifiedAfter() throws IOException {
        String modifiedAfterParam = "00:00:00T2008-07-01Z";
        HttpResponse response = RestUtilities.makeRequest(String.format("%s?modifiedAfter=%s", SEARCH_API, modifiedAfterParam), Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST,response.getStatusLine().getStatusCode());

        Map<String,Object> error = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));
        Assert.assertEquals("Invalid Search Criteria", error.get(Constants.Error.MESSAGE));

        List<Map<String, Object>> errorDetails = (List<Map<String, Object>>)error.get(Constants.Error.DETAILS);
        Assert.assertEquals(1, errorDetails.size());

        Map<String, Object> errorDetail = errorDetails.get(0);
        Assert.assertEquals("modifiedAfter", errorDetail.get(Constants.Error.PROPERTY));
        Assert.assertEquals("parameter is not a valid ISO8601 date", errorDetail.get(Constants.Error.MESSAGE));
    }
}
