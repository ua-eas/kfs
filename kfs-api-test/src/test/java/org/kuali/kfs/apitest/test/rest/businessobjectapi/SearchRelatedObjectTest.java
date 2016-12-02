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
import java.util.Map;

public class SearchRelatedObjectTest {
    private static String GOOD_API = "/coa/api/v1/reference/acct?organization.responsibilityCenterCode=80";
    private static String BAD1_API = "/coa/api/v1/reference/acct?organization.organizationZipCode=47405";
    private static String BAD2_API = "/coa/api/v1/reference/acct?chartOfAccountsCode.organizationZipCode=47405";
    private static String BAD3_API = "/coa/api/v1/reference/acct?.organizationZipCode=47405";
    private static String BAD4_API = "/coa/api/v1/reference/acct?organization.=47405";
    private static String BAD5_API = "/coa/api/v1/reference/acct?......=47405";

    @Test
    public void successfulSearch() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(GOOD_API, Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        Map<String,Object> results = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));

        Assert.assertEquals(31,results.get("totalCount"));
    }

    @Test
    public void unsupportedRelatedFieldSearch() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(BAD1_API, Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST,response.getStatusLine().getStatusCode());

        Map<String,Object> results = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));

        Assert.assertEquals("Invalid Search Criteria",results.get("message"));
    }


    @Test
    public void noRelationFieldSearch() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(BAD2_API, Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST,response.getStatusLine().getStatusCode());

        Map<String,Object> results = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));

        Assert.assertEquals("Invalid Search Criteria",results.get("message"));
    }

    @Test
    public void missingObjectSearch() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(BAD3_API, Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST,response.getStatusLine().getStatusCode());

        Map<String,Object> results = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));

        Assert.assertEquals("Invalid Search Criteria",results.get("message"));
    }

    @Test
    public void missingRelatedFieldSearch() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(BAD4_API, Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST,response.getStatusLine().getStatusCode());

        Map<String,Object> results = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));

        Assert.assertEquals("Invalid Search Criteria",results.get("message"));
    }

    @Test
    public void crazyFieldSearch() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(BAD5_API, Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST,response.getStatusLine().getStatusCode());

        Map<String,Object> results = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));

        Assert.assertEquals("Invalid Search Criteria",results.get("message"));
    }
}
