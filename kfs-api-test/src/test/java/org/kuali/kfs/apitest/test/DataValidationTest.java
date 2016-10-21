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
package org.kuali.kfs.apitest.test;

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

public class DataValidationTest {
    private static String API = "/api/v1/business-object/ar/system-informations/5A691AF93B5A424DE0404F8189D80D73";
    private static String SEARCH_API = "/api/v1/business-object/ar/system-informations";

    @Test
    public void validModuleName() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(API, Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        Map<String,Object> bo = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));

        // Check a sample of fields returned
        Assert.assertEquals(true,(Boolean)bo.get("active"));
        Assert.assertEquals("9460",(String)bo.get("creditCardObjectCode"));
        Assert.assertEquals("66255",(String)bo.get("lockboxNumber"));
        Assert.assertEquals(false,(Boolean)bo.get("newCollectionRecord"));
        Assert.assertEquals("5A691AF93B5A424DE0404F8189D80D73",(String)bo.get("objectId"));

        Map<String,Object> processingChartOfAccount = (Map<String,Object>)bo.get("processingChartOfAccount");
        String link = (String)processingChartOfAccount.get("link");
        Assert.assertTrue(link.endsWith("/api/v1/business-object/coa/charts/014F3DAF748BA448E043814FD28EA448"));

        int versionNumber = (Integer)bo.get("versionNumber");
        Assert.assertEquals(1,versionNumber);
    }

    @Test
    public void validSearchModuleName() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(SEARCH_API + "?objectId=5A691AF93B5A424DE0404F8189D80D73", Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        Map<String,Object> searchResults = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));
        List<Map<String, Object>> results = (List<Map<String, Object>>)searchResults.get(Constants.Search.RESULTS);
        Map<String, Object> bo = results.get(0);

        // Validate that the returned object is correct
        Assert.assertEquals(new ArrayList(Arrays.asList("objectId")), searchResults.get(Constants.Search.SORT));
        Assert.assertEquals(200, searchResults.get(Constants.Search.LIMIT));
        Assert.assertEquals(0, searchResults.get(Constants.Search.SKIP));
        Assert.assertEquals(1, searchResults.get(Constants.Search.TOTAL_COUNT));
        Map<String, String> query = new HashMap<String, String>();
        query.put("objectId", "5A691AF93B5A424DE0404F8189D80D73");
        Assert.assertEquals(query, searchResults.get(Constants.Search.QUERY));

        // Check a sample of fields returned
        Assert.assertEquals(true,(Boolean)bo.get("active"));
        Assert.assertEquals("9460",(String)bo.get("creditCardObjectCode"));
        Assert.assertEquals("66255",(String)bo.get("lockboxNumber"));
        Assert.assertEquals(false,(Boolean)bo.get("newCollectionRecord"));
        Assert.assertEquals("5A691AF93B5A424DE0404F8189D80D73",(String)bo.get("objectId"));

        Map<String,Object> processingChartOfAccount = (Map<String,Object>)bo.get("processingChartOfAccount");
        String link = (String)processingChartOfAccount.get("link");
        Assert.assertTrue(link.endsWith("/api/v1/business-object/coa/charts/014F3DAF748BA448E043814FD28EA448"));

        int versionNumber = (Integer)bo.get("versionNumber");
        Assert.assertEquals(1,versionNumber);
    }

    @Test
    public void validSearchWithLimitAndSkip() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(SEARCH_API + "?limit=2&skip=1", Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        Map<String,Object> searchResults = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));
        List<Map<String, Object>> results = (List<Map<String, Object>>)searchResults.get(Constants.Search.RESULTS);
        Map<String, Object> bo = results.get(0);

        // Validate that the returned object is correct
        Assert.assertEquals(new ArrayList(Arrays.asList("objectId")), searchResults.get(Constants.Search.SORT));
        Assert.assertEquals(2, searchResults.get(Constants.Search.LIMIT));
        Assert.assertEquals(1, searchResults.get(Constants.Search.SKIP));
        Assert.assertEquals(60, searchResults.get(Constants.Search.TOTAL_COUNT));
        Assert.assertEquals(new HashMap(), searchResults.get(Constants.Search.QUERY));

        // Check a sample of fields returned
        Assert.assertEquals(true,(Boolean)bo.get("active"));
        Assert.assertEquals("9460",(String)bo.get("creditCardObjectCode"));
        Assert.assertEquals("66255",(String)bo.get("lockboxNumber"));
        Assert.assertEquals(false,(Boolean)bo.get("newCollectionRecord"));
        Assert.assertEquals("03550bdf-2b67-48a3-bafe-dd431d017059",(String)bo.get("objectId"));

        Map<String,Object> processingChartOfAccount = (Map<String,Object>)bo.get("processingChartOfAccount");
        String link = (String)processingChartOfAccount.get("link");
        Assert.assertTrue(link.endsWith("/api/v1/business-object/coa/charts/014F3DAF748BA448E043814FD28EA448"));

        int versionNumber = (Integer)bo.get("versionNumber");
        Assert.assertEquals(2,versionNumber);
    }

    @Test
    public void validSearchWithSort() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(SEARCH_API + "?limit=1&sort=-objectId", Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        Map<String,Object> searchResults = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));
        List<Map<String, Object>> results = (List<Map<String, Object>>)searchResults.get(Constants.Search.RESULTS);
        Map<String, Object> bo = results.get(0);

        // Validate that the returned object is correct
        Assert.assertEquals(new ArrayList(Arrays.asList("-objectId")), searchResults.get(Constants.Search.SORT));
        Assert.assertEquals(1, searchResults.get(Constants.Search.LIMIT));
        Assert.assertEquals(0, searchResults.get(Constants.Search.SKIP));
        Assert.assertEquals(60, searchResults.get(Constants.Search.TOTAL_COUNT));
        Assert.assertEquals(new HashMap(), searchResults.get(Constants.Search.QUERY));

        // Check a sample of fields returned
        Assert.assertEquals(true,(Boolean)bo.get("active"));
        Assert.assertEquals("1599",(String)bo.get("creditCardObjectCode"));
        Assert.assertEquals("66248",(String)bo.get("lockboxNumber"));
        Assert.assertEquals(false,(Boolean)bo.get("newCollectionRecord"));
        Assert.assertEquals("fde14bd6-73b1-48c3-8939-20909f0f3459",(String)bo.get("objectId"));

        Map<String,Object> processingChartOfAccount = (Map<String,Object>)bo.get("processingChartOfAccount");
        String link = (String)processingChartOfAccount.get("link");
        Assert.assertTrue(link.endsWith("/api/v1/business-object/coa/charts/014F3DAF7495A448E043814FD28EA448"));

        int versionNumber = (Integer)bo.get("versionNumber");
        Assert.assertEquals(2,versionNumber);
    }
}
