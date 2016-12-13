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
    public void searchWithLastUpdatedTimestampSort() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(SEARCH_API + "?limit=1&sort=lastUpdatedTimestamp", Constants.KHUNTLEY_TOKEN);

        String responseString = RestUtilities.inputStreamToString(response.getEntity().getContent());

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        Map<String,Object> searchResults = RestUtilities.parse(responseString);
        List<Map<String, Object>> results = (List<Map<String, Object>>)searchResults.get(Constants.Search.RESULTS);
        Map<String, Object> bo = results.get(0);

        // Validate that the returned object is correct
        Assert.assertEquals(new ArrayList(Arrays.asList("lastUpdatedTimestamp")), searchResults.get(Constants.Search.SORT));
        Assert.assertEquals(1, searchResults.get(Constants.Search.LIMIT));
        Assert.assertEquals(0, searchResults.get(Constants.Search.SKIP));
        Assert.assertEquals(60, searchResults.get(Constants.Search.TOTAL_COUNT));
        Assert.assertEquals(new HashMap(), searchResults.get(Constants.Search.QUERY));

        // Check a sample of fields returned
        Assert.assertEquals(true,(Boolean)bo.get("active"));
        Assert.assertEquals("1599",(String)bo.get("creditCardObjectCode"));
        Assert.assertEquals("12345",(String)bo.get("lockboxNumber"));
        Assert.assertNull(bo.get("newCollectionRecord"));
    }

    @Test
    public void searchWithUpdatedBefore() throws IOException {
        String updatedBeforeParam = "1277942400000"; // 07/01/2010
        HttpResponse response = RestUtilities.makeRequest(String.format("%s?updatedBefore=%s", SEARCH_API, updatedBeforeParam), Constants.KHUNTLEY_TOKEN);

        String responseString = RestUtilities.inputStreamToString(response.getEntity().getContent());

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        Map<String,Object> searchResults = RestUtilities.parse(responseString);
        List<Map<String, Object>> results = (List<Map<String, Object>>)searchResults.get(Constants.Search.RESULTS);
        Map<String, Object> bo = results.get(0);

        // Validate that the returned object is correct
        Assert.assertEquals(new ArrayList(Arrays.asList("universityFiscalYear", "processingChartOfAccountCode", "processingOrganizationCode")), searchResults.get(Constants.Search.SORT));
        Assert.assertEquals(200, searchResults.get(Constants.Search.LIMIT));
        Assert.assertEquals(0, searchResults.get(Constants.Search.SKIP));
        Assert.assertEquals(updatedBeforeParam, searchResults.get(Constants.Search.UPDATED_BEFORE));
        Assert.assertNull(searchResults.get(Constants.Search.UPDATED_AFTER));
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
    public void searchWithUpdatedBeforeNoResults() throws IOException {
        String updatedBeforeParam = "1214870400000"; // 07/01/2008
        HttpResponse response = RestUtilities.makeRequest(String.format("%s?updatedBefore=%s", SEARCH_API, updatedBeforeParam), Constants.KHUNTLEY_TOKEN);

        String responseString = RestUtilities.inputStreamToString(response.getEntity().getContent());

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        Map<String,Object> searchResults = RestUtilities.parse(responseString);

        // Validate that the returned object is correct
        Assert.assertEquals(new ArrayList(Arrays.asList("universityFiscalYear", "processingChartOfAccountCode", "processingOrganizationCode")), searchResults.get(Constants.Search.SORT));
        Assert.assertEquals(200, searchResults.get(Constants.Search.LIMIT));
        Assert.assertEquals(0, searchResults.get(Constants.Search.SKIP));
        Assert.assertEquals(updatedBeforeParam, searchResults.get(Constants.Search.UPDATED_BEFORE));
        Assert.assertNull(searchResults.get(Constants.Search.UPDATED_AFTER));
        Assert.assertEquals(0, searchResults.get(Constants.Search.TOTAL_COUNT));
        Assert.assertEquals(new HashMap(), searchResults.get(Constants.Search.QUERY));
    }

    @Test
    public void searchWithUpdatedAfter() throws IOException {
        String updatedAfterParam = "1214870400000"; // 07/01/2008
        HttpResponse response = RestUtilities.makeRequest(String.format("%s?updatedAfter=%s", SEARCH_API, updatedAfterParam), Constants.KHUNTLEY_TOKEN);

        String responseString = RestUtilities.inputStreamToString(response.getEntity().getContent());

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        Map<String,Object> searchResults = RestUtilities.parse(responseString);
        List<Map<String, Object>> results = (List<Map<String, Object>>)searchResults.get(Constants.Search.RESULTS);
        Map<String, Object> bo = results.get(0);

        // Validate that the returned object is correct
        Assert.assertEquals(new ArrayList(Arrays.asList("universityFiscalYear", "processingChartOfAccountCode", "processingOrganizationCode")), searchResults.get(Constants.Search.SORT));
        Assert.assertEquals(200, searchResults.get(Constants.Search.LIMIT));
        Assert.assertEquals(0, searchResults.get(Constants.Search.SKIP));
        Assert.assertEquals(updatedAfterParam, searchResults.get(Constants.Search.UPDATED_AFTER));
        Assert.assertNull(searchResults.get(Constants.Search.UPDATED_BEFORE));
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
    public void searchWithUpdatedAfterNoResults() throws IOException {
        String updatedAfterParam = "1277942400000"; // 07/01/2010
        HttpResponse response = RestUtilities.makeRequest(String.format("%s?updatedAfter=%s", SEARCH_API, updatedAfterParam), Constants.KHUNTLEY_TOKEN);

        String responseString = RestUtilities.inputStreamToString(response.getEntity().getContent());

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        Map<String,Object> searchResults = RestUtilities.parse(responseString);

        // Validate that the returned object is correct
        Assert.assertEquals(new ArrayList(Arrays.asList("universityFiscalYear", "processingChartOfAccountCode", "processingOrganizationCode")), searchResults.get(Constants.Search.SORT));
        Assert.assertEquals(200, searchResults.get(Constants.Search.LIMIT));
        Assert.assertEquals(0, searchResults.get(Constants.Search.SKIP));
        Assert.assertEquals(updatedAfterParam, searchResults.get(Constants.Search.UPDATED_AFTER));
        Assert.assertNull(searchResults.get(Constants.Search.UPDATED_BEFORE));
        Assert.assertEquals(0, searchResults.get(Constants.Search.TOTAL_COUNT));
        Assert.assertEquals(new HashMap(), searchResults.get(Constants.Search.QUERY));
    }

    @Test
    public void searchWithupdatedBeforeAndAfter() throws IOException {
        String updatedAfterParam = "1214870400000"; // 07/01/2008
        String updatedBeforeParam = "1277942400000"; // 07/01/2010
        HttpResponse response = RestUtilities.makeRequest(String.format("%s?updatedAfter=%s&updatedBefore=%s", SEARCH_API, updatedAfterParam, updatedBeforeParam), Constants.KHUNTLEY_TOKEN);

        String responseString = RestUtilities.inputStreamToString(response.getEntity().getContent());

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        Map<String,Object> searchResults = RestUtilities.parse(responseString);
        List<Map<String, Object>> results = (List<Map<String, Object>>)searchResults.get(Constants.Search.RESULTS);
        Map<String, Object> bo = results.get(0);

        // Validate that the returned object is correct
        Assert.assertEquals(new ArrayList(Arrays.asList("universityFiscalYear", "processingChartOfAccountCode", "processingOrganizationCode")), searchResults.get(Constants.Search.SORT));
        Assert.assertEquals(200, searchResults.get(Constants.Search.LIMIT));
        Assert.assertEquals(0, searchResults.get(Constants.Search.SKIP));
        Assert.assertEquals(updatedBeforeParam, searchResults.get(Constants.Search.UPDATED_BEFORE));
        Assert.assertEquals(updatedAfterParam, searchResults.get(Constants.Search.UPDATED_AFTER));
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
    public void searchWithupdatedBeforeAndAfterNoResults() throws IOException {
        String updatedAfterParam = "1277942400000"; // 07/01/2010
        String updatedBeforeParam = "1214870400000"; // 07/01/2008
        HttpResponse response = RestUtilities.makeRequest(String.format("%s?updatedAfter=%s&updatedBefore=%s", SEARCH_API, updatedAfterParam, updatedBeforeParam), Constants.KHUNTLEY_TOKEN);

        String responseString = RestUtilities.inputStreamToString(response.getEntity().getContent());

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        Map<String,Object> searchResults = RestUtilities.parse(responseString);

        // Validate that the returned object is correct
        Assert.assertEquals(new ArrayList(Arrays.asList("universityFiscalYear", "processingChartOfAccountCode", "processingOrganizationCode")), searchResults.get(Constants.Search.SORT));
        Assert.assertEquals(200, searchResults.get(Constants.Search.LIMIT));
        Assert.assertEquals(0, searchResults.get(Constants.Search.SKIP));
        Assert.assertEquals(updatedAfterParam, searchResults.get(Constants.Search.UPDATED_AFTER));
        Assert.assertEquals(updatedBeforeParam, searchResults.get(Constants.Search.UPDATED_BEFORE));
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
    public void searchWithInvalidUpdatedBefore() throws IOException {
        String updatedBeforeParam = "00:00:00T2008-07-01Z";
        HttpResponse response = RestUtilities.makeRequest(String.format("%s?updatedBefore=%s", SEARCH_API, updatedBeforeParam), Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST,response.getStatusLine().getStatusCode());

        Map<String,Object> error = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));
        Assert.assertEquals("Invalid Search Criteria", error.get(Constants.Error.MESSAGE));

        List<Map<String, Object>> errorDetails = (List<Map<String, Object>>)error.get(Constants.Error.DETAILS);
        Assert.assertEquals(1, errorDetails.size());

        Map<String, Object> errorDetail = errorDetails.get(0);
        Assert.assertEquals("updatedBefore", errorDetail.get(Constants.Error.PROPERTY));
        Assert.assertEquals("parameter is not a valid Unix time value", errorDetail.get(Constants.Error.MESSAGE));
    }

    @Test
    public void searchWithInvalidUpdatedAfter() throws IOException {
        String updatedAfterParam = "00:00:00T2008-07-01Z";
        HttpResponse response = RestUtilities.makeRequest(String.format("%s?updatedAfter=%s", SEARCH_API, updatedAfterParam), Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST,response.getStatusLine().getStatusCode());

        Map<String,Object> error = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));
        Assert.assertEquals("Invalid Search Criteria", error.get(Constants.Error.MESSAGE));

        List<Map<String, Object>> errorDetails = (List<Map<String, Object>>)error.get(Constants.Error.DETAILS);
        Assert.assertEquals(1, errorDetails.size());

        Map<String, Object> errorDetail = errorDetails.get(0);
        Assert.assertEquals("updatedAfter", errorDetail.get(Constants.Error.PROPERTY));
        Assert.assertEquals("parameter is not a valid Unix time value", errorDetail.get(Constants.Error.MESSAGE));
    }
}
