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
import java.util.List;
import java.util.Map;

public class MaskedDataTest {
    private static String BANK_API = "/sys/api/v1/reference/bank/684207EF61A57565E0404F8189D81EBF";
    private static String VENDOR_API = "/vnd/api/v1/reference/pven/830E61147605C23CE0404F8189D82CFD";
    private static String BANK_SEARCH_API = "/sys/api/v1/reference/bank";
    private static String VENDOR_SEARCH_API = "/vnd/api/v1/reference/pven";

    @Test
    public void maskedBankData() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(BANK_API, Constants.DAY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        Map<String,Object> bo = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));

        Assert.assertEquals("*********",(String)bo.get("bankAccountNumber"));
    }

    @Test
    public void unmaskedBankData() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(BANK_API, Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        Map<String,Object> bo = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));

        Assert.assertEquals("Bank 1",(String)bo.get("bankAccountNumber"));
    }

    @Test
    public void maskedVendorData() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(VENDOR_API, Constants.BUTT_TOKEN);

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        Map<String,Object> bo = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));

        Assert.assertEquals("*********",(String)((Map<String,Object>)bo.get("vendorHeader")).get("vendorTaxNumber"));
    }

//    @Test
//    public void unmaskedVendorData() throws IOException {
//        HttpResponse response = RestUtilities.makeRequest(VENDOR_API, Constants.KHUNTLEY_TOKEN);
//
//        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());
//
//        Map<String,Object> bo = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));
//
//        Assert.assertEquals("123456789",(String)((Map<String,Object>)bo.get("vendorHeader")).get("vendorTaxNumber"));
//    }

    @Test
    public void maskedBankSearchData() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(BANK_SEARCH_API, Constants.DAY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        Map<String,Object> searchResults = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));
        List<Map<String, Object>> results = (List<Map<String, Object>>)searchResults.get(Constants.Search.RESULTS);
        Map<String, Object> bo = results.get(0);

        Assert.assertEquals("*********",(String)bo.get("bankAccountNumber"));
    }

    @Test
    public void unmaskedBankSearchData() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(BANK_SEARCH_API, Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        Map<String,Object> searchResults = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));
        List<Map<String, Object>> results = (List<Map<String, Object>>)searchResults.get("results");
        Map<String, Object> bo = results.get(0);

        Assert.assertEquals("Bank 1",(String)bo.get("bankAccountNumber"));
    }

    @Test
    public void maskedVendorSearchData() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(VENDOR_SEARCH_API, Constants.BUTT_TOKEN);

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        Map<String,Object> searchResults = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));
        List<Map<String, Object>> results = (List<Map<String, Object>>)searchResults.get(Constants.Search.RESULTS);
        Map<String, Object> bo = results.get(0);

        Assert.assertEquals("*********",(String)((Map<String,Object>)bo.get("vendorHeader")).get("vendorTaxNumber"));
    }

//    @Test
//    public void unmaskedVendorSearchData() throws IOException {
//        HttpResponse response = RestUtilities.makeRequest(VENDOR_SEARCH_API, Constants.KHUNTLEY_TOKEN);
//
//        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());
//
//        Map<String,Object> searchResults = RestUtilities.parse(RestUtilities.inputStreamToString(response.getEntity().getContent()));
//        List<Map<String, Object>> results = (List<Map<String, Object>>)searchResults.get("results");
//        Map<String, Object> bo = results.get(0);
//
//        Assert.assertEquals("123456789",(String)((Map<String,Object>)bo.get("vendorHeader")).get("vendorTaxNumber"));
//    }
}
