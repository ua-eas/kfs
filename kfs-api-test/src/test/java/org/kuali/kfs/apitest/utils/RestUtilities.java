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
package org.kuali.kfs.apitest.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.kuali.kfs.apitest.TestProperties;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestUtilities {
    public static HttpResponse makeRequest(String url, String token) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet getRequest = new HttpGet(TestProperties.getProperty("test_url") + url);
        getRequest.addHeader(HttpHeaders.AUTHORIZATION,"Bearer " + TestProperties.getProperty(token));

        return httpClient.execute(getRequest);
    }

    public static String inputStreamToString(InputStream stream) {
        try {
            return IOUtils.toString(stream,"UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String,Object> parse(String json) {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};

        try {
            return mapper.readValue(json, typeRef);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void assertStatusOk(HttpResponse httpResponse) {
        Assert.assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
    }

    public static SearchResult parseSearchResult(HttpResponse httpResponse) {
        try {
            final Map<String, Object> resultObject = RestUtilities.parse(RestUtilities.inputStreamToString(httpResponse.getEntity().getContent()));
            return new SearchResult((Integer)resultObject.get("totalCount"), (Integer)resultObject.get("limit"), (Integer)resultObject.get("skip"), (List<Map<String, Object>>)resultObject.get("results"));
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

    }
}
