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
package org.kuali.kfs.apitest.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.kuali.kfs.apitest.TestProperties;

import java.io.IOException;

public class CollectorUtilities {
    public static HttpResponse collectorRequest(String token, String contentType, String body) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost postRequest = new HttpPost(TestProperties.getProperty("test_url") + "/gl/api/v1/collector");
        postRequest.addHeader(HttpHeaders.AUTHORIZATION,"Bearer " + TestProperties.getProperty(token));
        if ( contentType != null ) {
            postRequest.addHeader(HttpHeaders.CONTENT_TYPE, contentType);
        }
        if ( body != null ) {
            HttpEntity entity = new ByteArrayEntity(body.getBytes("UTF-8"));
            postRequest.setEntity(entity);
        }

        return httpClient.execute(postRequest);
    }
}
