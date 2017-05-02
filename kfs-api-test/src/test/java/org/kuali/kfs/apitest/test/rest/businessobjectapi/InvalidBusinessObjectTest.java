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
package org.kuali.kfs.apitest.test.rest.businessobjectapi;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import org.kuali.kfs.apitest.Constants;
import org.kuali.kfs.apitest.utils.RestUtilities;

import java.io.IOException;

public class InvalidBusinessObjectTest {
    private static String BAD_API = "/vnd/api/v1/reference/pven/xxx";
    private static String GOOD_API = "/vnd/api/v1/reference/pven/8FFE7896-8093-7D85-0203-486C10E20BD2";
    private static String BAD_SEARCH_API = "/vnd/api/v1/reference/pven?objectId=xxx";
    private static String GOOD_SEARCH_API = "/vnd/api/v1/reference/pven?vendorHeaderGeneratedIdentifier=4005&vendorDetailAssignedIdentifier=0";

    @Test
    public void invalidBusinessObject() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(BAD_API, Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_NOT_FOUND,response.getStatusLine().getStatusCode());
    }

    @Test
    public void validBusinessObject() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(GOOD_API, Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());
    }

    @Test
    public void invalidSearchBusinessObject() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(BAD_SEARCH_API, Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST,response.getStatusLine().getStatusCode());
    }

    @Test
    public void validSearchBusinessObject() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(GOOD_SEARCH_API, Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());
    }
}
