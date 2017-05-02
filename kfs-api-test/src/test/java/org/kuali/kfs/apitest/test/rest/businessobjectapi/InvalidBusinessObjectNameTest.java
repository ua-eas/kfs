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

public class InvalidBusinessObjectNameTest {
    private static String BAD_API = "/coa/api/v1/reference/xxx/EE235779-F59D-3BA4-52CB-F49355EB8E8B";
    private static String GOOD_API = "/coa/api/v1/reference/proj/EE235779-F59D-3BA4-52CB-F49355EB8E8B";
    private static String BAD_SEARCH_API = "/coa/api/v1/reference/xxx";
    private static String GOOD_SEARCH_API = "/coa/api/v1/reference/proj";

    @Test
    public void invalidBusinessObjectName() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(BAD_API, Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_NOT_FOUND,response.getStatusLine().getStatusCode());
    }

    @Test
    public void validBusinessObjectName() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(GOOD_API, Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());
    }

    @Test
    public void invalidSearchBusinessObjectName() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(BAD_SEARCH_API, Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_NOT_FOUND,response.getStatusLine().getStatusCode());
    }

    @Test
    public void validSearchBusinessObjectName() throws IOException {
        HttpResponse response = RestUtilities.makeRequest(GOOD_SEARCH_API, Constants.KHUNTLEY_TOKEN);

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());
    }
}
